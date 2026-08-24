package de.kkuester.ufospiel.core;

import de.kkuester.ufospiel.audio.SoundManager;
import de.kkuester.ufospiel.entities.*;
import de.kkuester.ufospiel.render.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameLoop extends AnimationTimer {

    public interface Listener {
        void onGameOver(int score, int wave);
    }

    private final double width;
    private final double height;
    private final GraphicsContext gc;
    private final InputManager input;
    private final Settings settings;
    private final SoundManager sound;
    private final Listener listener;

    public GameState state = GameState.MENU;

    private final Starfield starfield;
    private final Nebula nebula;
    private final ParticleSystem particles = new ParticleSystem();
    private final ScreenShake shake = new ScreenShake();
    private final HUDRenderer hud;

    private Player player;
    private final List<Asteroid> asteroids = new ArrayList<>();
    private final List<EnemyShip> enemies = new ArrayList<>();
    private Boss boss;
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    private int score;
    private int combo;
    private double comboTimer;
    private int wave;
    private double waveBreatherTimer;
    private boolean waveClearedPending;
    private int enemiesToSpawnThisWave;
    private double enemySpawnTimer;
    private double hitStopTimer;
    private long lastNanos = -1;
    private final Random random = new Random();

    public GameLoop(GraphicsContext gc, double width, double height, InputManager input, Settings settings,
                     SoundManager sound, Font hudFont, Font titleFont, Listener listener) {
        this.gc = gc;
        this.width = width;
        this.height = height;
        this.input = input;
        this.settings = settings;
        this.sound = sound;
        this.listener = listener;
        this.starfield = new Starfield(width, height);
        this.nebula = new Nebula(width, height);
        this.hud = new HUDRenderer(hudFont, titleFont);
    }

    public void startNewGame() {
        player = new Player(width / 2.0, height / 2.0);
        asteroids.clear();
        enemies.clear();
        projectiles.clear();
        powerUps.clear();
        boss = null;
        score = 0;
        combo = 0;
        comboTimer = 0;
        wave = 0;
        waveClearedPending = false;
        state = GameState.PLAYING;
        startWave(1);
    }

    public int getScore() {
        return score;
    }

    public int getWave() {
        return wave;
    }

    @Override
    public void handle(long now) {
        if (lastNanos < 0) {
            lastNanos = now;
            return;
        }
        double dt = Math.min(0.05, (now - lastNanos) / 1.0e9);
        lastNanos = now;

        starfield.update(dt);
        nebula.update(dt);

        if (state == GameState.PLAYING) {
            update(dt);
        }

        particles.update(dt);
        shake.update(dt);
        hud.update(dt);

        render();
    }

    private void update(double dt) {
        if (hitStopTimer > 0) {
            hitStopTimer -= dt;
            return;
        }
        handleInput(dt);
        player.update(dt, width, height);
        updateProjectiles(dt);
        for (Asteroid a : asteroids) {
            a.update(dt, width, height);
        }
        updateEnemies(dt);
        updateBoss(dt);
        updatePowerUps(dt);
        handleCollisions();
        updateWaveProgress(dt);
        if (comboTimer > 0) {
            comboTimer -= dt;
            if (comboTimer <= 0) {
                combo = 0;
            }
        }
    }

    private void handleInput(double dt) {
        double dx = 0, dy = 0;
        if (input.isActionDown(GameAction.UP)) dy -= 1;
        if (input.isActionDown(GameAction.DOWN)) dy += 1;
        if (input.isActionDown(GameAction.LEFT)) dx -= 1;
        if (input.isActionDown(GameAction.RIGHT)) dx += 1;
        if (dx != 0 || dy != 0) {
            player.thrust(dx, dy, dt);
            double angle = Math.atan2(dy, dx);
            particles.spawnThruster(player.x - Math.cos(angle) * 18, player.y - Math.sin(angle) * 18, angle,
                    Color.rgb(120, 200, 255));
        }
        if (input.isActionDown(GameAction.FIRE)) {
            tryFire();
        }
    }

    private void tryFire() {
        double up = -Math.PI / 2;
        if (player.canFire()) {
            if (player.spreadShotTimer > 0) {
                for (double off : new double[]{-0.3, 0, 0.3}) {
                    projectiles.add(new Projectile(Projectile.Kind.PLAYER_SPREAD, player.x, player.y - 20, up + off, 520));
                }
            } else {
                projectiles.add(new Projectile(Projectile.Kind.PLAYER_LASER, player.x, player.y - 20, up, 620));
            }
            player.resetFireCooldown();
            sound.play(SoundManager.Sfx.FIRE);
        }
        if (player.canFireHoming()) {
            projectiles.add(new Projectile(Projectile.Kind.PLAYER_HOMING, player.x, player.y - 20, up, 380));
            player.homingMissiles--;
            player.resetHomingCooldown();
        }
    }

    private void updateProjectiles(double dt) {
        for (Iterator<Projectile> it = projectiles.iterator(); it.hasNext(); ) {
            Projectile p = it.next();
            Entity target = p.kind == Projectile.Kind.PLAYER_HOMING ? findNearestHostile(p.x, p.y) : null;
            p.update(dt, target);
            if (p.x < -60 || p.x > width + 60 || p.y < -60 || p.y > height + 60) {
                p.dead = true;
            }
            if (p.dead) {
                it.remove();
            }
        }
    }

    private Entity findNearestHostile(double x, double y) {
        Entity nearest = null;
        double best = Double.MAX_VALUE;
        for (Asteroid a : asteroids) {
            double d = dist2(x, y, a.x, a.y);
            if (d < best) {
                best = d;
                nearest = a;
            }
        }
        for (EnemyShip e : enemies) {
            double d = dist2(x, y, e.x, e.y);
            if (d < best) {
                best = d;
                nearest = e;
            }
        }
        if (boss != null && dist2(x, y, boss.x, boss.y) < best) {
            nearest = boss;
        }
        return nearest;
    }

    private static double dist2(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2, dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    private void updateEnemies(double dt) {
        double speedFactor = settings.getDifficulty().speedFactor;
        for (EnemyShip e : enemies) {
            boolean wantsFire = e.update(dt, width, height, speedFactor);
            if (wantsFire) {
                double angle = e.angleToward(player.x, player.y);
                projectiles.add(new Projectile(Projectile.Kind.ENEMY_BOLT, e.x, e.y, angle, 260));
                e.resetFireTimer();
            }
        }
    }

    private void updateBoss(double dt) {
        if (boss != null) {
            List<Projectile> spawned = boss.update(dt, player.x, player.y, width);
            projectiles.addAll(spawned);
        }
    }

    private void updatePowerUps(double dt) {
        for (Iterator<PowerUp> it = powerUps.iterator(); it.hasNext(); ) {
            PowerUp p = it.next();
            p.update(dt);
            if (p.y > height + 40) {
                p.dead = true;
            }
            if (p.dead) {
                it.remove();
            }
        }
    }

    private void handleCollisions() {
        for (Projectile proj : projectiles) {
            if (proj.dead) continue;
            if (proj.isPlayerOwned()) {
                for (Iterator<Asteroid> ait = asteroids.iterator(); ait.hasNext(); ) {
                    Asteroid a = ait.next();
                    if (proj.collidesWith(a)) {
                        a.hitPoints -= proj.damage;
                        proj.dead = true;
                        particles.spawnSparkle(proj.x, proj.y, Color.rgb(200, 160, 255));
                        if (a.hitPoints <= 0) {
                            killAsteroid(a);
                            ait.remove();
                        }
                        break;
                    }
                }
                if (proj.dead) continue;
                for (Iterator<EnemyShip> eit = enemies.iterator(); eit.hasNext(); ) {
                    EnemyShip e = eit.next();
                    if (proj.collidesWith(e)) {
                        e.hitPoints -= proj.damage;
                        proj.dead = true;
                        particles.spawnSparkle(proj.x, proj.y, Color.rgb(255, 150, 120));
                        if (e.hitPoints <= 0) {
                            killEnemy(e);
                            eit.remove();
                        }
                        break;
                    }
                }
                if (!proj.dead && boss != null && proj.collidesWith(boss)) {
                    boss.hitPoints -= proj.damage;
                    proj.dead = true;
                    particles.spawnSparkle(proj.x, proj.y, Color.rgb(255, 120, 190));
                    shake.addTrauma(0.08);
                    if (boss.hitPoints <= 0) {
                        killBoss();
                    }
                }
            } else if (!player.isInvulnerable() && proj.collidesWith(player)) {
                proj.dead = true;
                onPlayerHit();
            }
        }
        projectiles.removeIf(p -> p.dead);

        if (!player.isInvulnerable()) {
            boolean hit = false;
            for (Asteroid a : asteroids) {
                if (player.collidesWith(a)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                for (EnemyShip e : enemies) {
                    if (player.collidesWith(e)) {
                        hit = true;
                        break;
                    }
                }
            }
            if (!hit && boss != null && player.collidesWith(boss)) {
                hit = true;
            }
            if (hit) {
                onPlayerHit();
            }
        }

        for (Iterator<PowerUp> it = powerUps.iterator(); it.hasNext(); ) {
            PowerUp p = it.next();
            if (player.collidesWith(p)) {
                applyPowerUp(p.type);
                particles.spawnSparkle(p.x, p.y, p.type.color);
                sound.play(SoundManager.Sfx.BANG_SMALL);
                it.remove();
            }
        }
    }

    private void killAsteroid(Asteroid a) {
        particles.spawnExplosion(a.x, a.y, Color.rgb(210, 180, 255), 14, 180);
        shake.addTrauma(a.size == Asteroid.Size.LARGE ? 0.18 : 0.08);
        sound.play(a.size == Asteroid.Size.LARGE ? SoundManager.Sfx.BANG_LARGE : SoundManager.Sfx.BANG_SMALL);
        List<Asteroid> children = a.split();
        asteroids.addAll(children);
        addScore(a.size.score, a.x, a.y);
        maybeDropPowerUp(a.x, a.y, children.isEmpty() ? 0.32 : 0.1);
    }

    private void killEnemy(EnemyShip e) {
        particles.spawnExplosion(e.x, e.y, Color.rgb(255, 140, 120), 18, 200);
        shake.addTrauma(0.22);
        sound.play(SoundManager.Sfx.BANG_LARGE);
        addScore(EnemyShip.SCORE_VALUE, e.x, e.y);
        maybeDropPowerUp(e.x, e.y, 0.3);
    }

    private void killBoss() {
        particles.spawnExplosion(boss.x, boss.y, Color.rgb(255, 140, 200), 70, 260);
        shake.addTrauma(1.0);
        hitStopTimer = 0.18;
        sound.play(SoundManager.Sfx.BANG_LARGE);
        addScore(Boss.SCORE_VALUE, boss.x, boss.y);
        boss = null;
        hud.showBanner("BOSS BESIEGT!");
    }

    private void onPlayerHit() {
        boolean lostLife = player.takeHit();
        shake.addTrauma(0.5);
        particles.spawnExplosion(player.x, player.y, Color.rgb(255, 120, 120), 10, 140);
        if (lostLife) {
            sound.play(SoundManager.Sfx.BANG_LARGE);
            if (player.lives <= 0) {
                triggerGameOver();
            } else {
                player.resetForNewLife(width, height);
                hud.showBanner("SCHIFF VERLOREN");
            }
        }
    }

    private void triggerGameOver() {
        state = GameState.GAME_OVER;
        if (listener != null) {
            listener.onGameOver(score, wave);
        }
    }

    private void addScore(int base, double x, double y) {
        combo++;
        comboTimer = 1.6;
        int multiplier = Math.min(combo, 8);
        int points = base * multiplier * (player.scoreMultiplierTimer > 0 ? 2 : 1);
        score += points;
        hud.addPopup(x, y, "+" + points, Color.rgb(255, 220, 140));
    }

    private void maybeDropPowerUp(double x, double y, double chance) {
        if (random.nextDouble() < chance) {
            PowerUp.Type[] types = PowerUp.Type.values();
            PowerUp.Type type = types[random.nextInt(types.length)];
            powerUps.add(new PowerUp(type, x, y));
        }
    }

    private void applyPowerUp(PowerUp.Type type) {
        switch (type) {
            case SHIELD -> player.shieldPowerTimer = 6.0;
            case EXTRA_LIFE -> player.lives++;
            case SCORE_MULTIPLIER -> player.scoreMultiplierTimer = 10.0;
            case SPREAD_SHOT -> player.spreadShotTimer = 10.0;
            case HOMING_MISSILES -> player.homingMissiles += 4;
            case RAPID_FIRE -> player.rapidFireTimer = 8.0;
        }
        hud.addPopup(player.x, player.y - 30, type.name().replace('_', ' '), type.color);
    }

    private void startWave(int n) {
        wave = n;
        boolean bossWave = bossWave(n);
        hud.showBanner(bossWave ? "BOSS - WELLE " + n : "WELLE " + n);
        double speedFactor = settings.getDifficulty().speedFactor * (1 + (n - 1) * 0.06);
        if (bossWave) {
            boss = new Boss(width / 2.0, height * 0.28, n);
            int asteroidCount = Math.min(3, 1 + n / 5);
            for (int i = 0; i < asteroidCount; i++) {
                spawnAsteroidAtEdge(Asteroid.Size.LARGE, speedFactor);
            }
        } else {
            int asteroidCount = Math.min(12, 4 + n);
            for (int i = 0; i < asteroidCount; i++) {
                spawnAsteroidAtEdge(Asteroid.Size.LARGE, speedFactor);
            }
        }
        enemiesToSpawnThisWave = n >= 3 ? Math.min(5, n - 2) : 0;
        enemySpawnTimer = 3.0;
        waveClearedPending = false;
    }

    private boolean bossWave(int n) {
        return n % 5 == 0;
    }

    private void updateWaveProgress(double dt) {
        if (enemiesToSpawnThisWave > 0) {
            enemySpawnTimer -= dt;
            if (enemySpawnTimer <= 0) {
                spawnEnemyAtEdge();
                enemiesToSpawnThisWave--;
                enemySpawnTimer = 4.0 + random.nextDouble() * 2;
            }
        }
        boolean cleared = asteroids.isEmpty() && enemies.isEmpty() && enemiesToSpawnThisWave == 0 && boss == null;
        if (cleared) {
            if (!waveClearedPending) {
                waveClearedPending = true;
                waveBreatherTimer = 2.4;
            } else {
                waveBreatherTimer -= dt;
                if (waveBreatherTimer <= 0) {
                    startWave(wave + 1);
                }
            }
        }
    }

    private void spawnAsteroidAtEdge(Asteroid.Size size, double speedFactor) {
        double[] pos = randomEdgePoint();
        double angle = Math.atan2(height / 2.0 - pos[1], width / 2.0 - pos[0]) + (random.nextDouble() - 0.5) * 1.2;
        asteroids.add(new Asteroid(size, pos[0], pos[1], angle, speedFactor));
    }

    private void spawnEnemyAtEdge() {
        double[] pos = randomEdgePoint();
        enemies.add(new EnemyShip(pos[0], pos[1]));
    }

    private double[] randomEdgePoint() {
        int edge = random.nextInt(4);
        return switch (edge) {
            case 0 -> new double[]{random.nextDouble() * width, -40};
            case 1 -> new double[]{random.nextDouble() * width, height + 40};
            case 2 -> new double[]{-40, random.nextDouble() * height};
            default -> new double[]{width + 40, random.nextDouble() * height};
        };
    }

    private void render() {
        gc.save();
        gc.setFill(Color.rgb(6, 8, 18));
        gc.fillRect(0, 0, width, height);
        gc.translate(shake.getOffsetX(), shake.getOffsetY());

        nebula.render(gc);
        starfield.render(gc);

        boolean showGameplay = state == GameState.PLAYING || state == GameState.PAUSED || state == GameState.GAME_OVER;
        if (showGameplay && player != null) {
            for (PowerUp p : powerUps) p.render(gc);
            for (Asteroid a : asteroids) a.render(gc);
            for (EnemyShip e : enemies) e.render(gc);
            if (boss != null) boss.render(gc);
            for (Projectile p : projectiles) p.render(gc);
            player.render(gc);
            particles.render(gc);
            hud.render(gc, width, score, combo, wave, player);
        } else {
            particles.render(gc);
        }
        gc.restore();
    }
}
