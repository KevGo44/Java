package de.kkuester.pentagonquest3d.core;

import de.kkuester.pentagonquest3d.ai.MonsterAI;
import de.kkuester.pentagonquest3d.assets.ModelLibrary;
import de.kkuester.pentagonquest3d.audio.SoundManager;
import de.kkuester.pentagonquest3d.combat.CombatSystem;
import de.kkuester.pentagonquest3d.entities.Item;
import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.Player;
import de.kkuester.pentagonquest3d.render.HudOverlay;
import de.kkuester.pentagonquest3d.render.MonsterModelMapping;
import de.kkuester.pentagonquest3d.render.ParticleSystem3D;
import de.kkuester.pentagonquest3d.world.Collision;
import de.kkuester.pentagonquest3d.world.DungeonBuilder;
import de.kkuester.pentagonquest3d.world.DungeonLayout;
import de.kkuester.pentagonquest3d.world.DungeonOccupancy;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Zentraler Game-Loop: Bewegung/Kollision, Kamera, Kampf, Gegner-KI und die
 * Synchronisation der 3D-Szene mit dem Spielzustand.
 */
public class GameLoop extends AnimationTimer {

    public interface Listener {
        void onGameOver(boolean victory, int score, int level);
    }

    private static final double EYE_HEIGHT = 0.55;
    private static final double PLAYER_SPEED = 1.6;
    private static final double SPRINT_MULTIPLIER = 1.5;
    private static final double MOUSE_SENSITIVITY = 0.15;
    private static final double PITCH_LIMIT = 80;

    public GameState state = GameState.MENU;

    private final InputManager input;
    private final Settings settings;
    private final SoundManager sound;
    private final HudOverlay hud;
    private final ParticleSystem3D particles = new ParticleSystem3D();
    private final CombatSystem combat = new CombatSystem();
    private final ModelLibrary models;
    private final Listener listener;

    private final Group worldRoot = new Group();
    private final Group monstersRoot = new Group();
    private final Group cameraYaw = new Group();
    private final Group cameraPitch = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);

    private Player player;
    private DungeonLayout layout;
    private Collision collision;
    private final List<Monster> monsters = new ArrayList<>();
    private final Map<Monster, MeshView> monsterViews = new HashMap<>();
    private final Random random = new Random();

    private int score;
    private boolean spaceHeldLastFrame;
    private long lastNanos = -1;

    public GameLoop(InputManager input, Settings settings, SoundManager sound, HudOverlay hud,
                     ModelLibrary models, Listener listener) {
        this.input = input;
        this.settings = settings;
        this.sound = sound;
        this.hud = hud;
        this.models = models;
        this.listener = listener;

        camera.setNearClip(0.02);
        camera.setFarClip(200);
        camera.setFieldOfView(75);

        // Kein Fackel-Prop im Kenney-„Mini Dungeon"-Set enthalten; stattdessen ein dezentes,
        // mit der Kamera mitlaufendes Licht ("Stirnlampe"), damit die Umgebung sichtbar bleibt,
        // ohne viele einzelne PointLights im Dungeon zu benötigen (Performance).
        PointLight headlamp = new PointLight(Color.rgb(255, 235, 205));
        headlamp.setTranslateY(-0.1);
        headlamp.getScope().add(worldRoot);

        cameraPitch.getChildren().addAll(camera, headlamp);
        cameraPitch.getTransforms().add(new Rotate(0, Rotate.X_AXIS));
        cameraYaw.getChildren().add(cameraPitch);
        cameraYaw.getTransforms().add(new Rotate(0, Rotate.Y_AXIS));

        worldRoot.getChildren().add(monstersRoot);
        worldRoot.getChildren().add(particles.getRoot());
    }

    public Group getWorldRoot() {
        return worldRoot;
    }

    public Group getCameraGroup() {
        return cameraYaw;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void startNewGame(String playerName) {
        player = new Player(playerName.isBlank() ? "Held" : playerName);
        layout = DungeonLayout.generate(random);
        collision = new Collision(DungeonOccupancy.build(layout));
        monsters.clear();
        monsters.addAll(layout.spawnAllMonsters(random));
        score = 0;

        worldRoot.getChildren().removeIf(n -> n != monstersRoot && n != particles.getRoot());
        monstersRoot.getChildren().clear();
        monsterViews.clear();

        DungeonBuilder builder = new DungeonBuilder(models);
        Group dungeonGeometry = builder.build(layout, random);
        worldRoot.getChildren().add(dungeonGeometry);

        for (Monster m : monsters) {
            MeshView view = MonsterModelMapping.createView(models, m.type);
            monstersRoot.getChildren().add(view);
            monsterViews.put(m, view);
        }

        player.x = DungeonLayout.cellCenterX(0);
        player.z = DungeonLayout.cellCenterZ(0);
        player.yawDeg = 0;
        player.pitchDeg = 0;

        state = GameState.PLAYING;
        input.setMouseLookActive(true);
        hud.showBanner("Das Ödland ruft, " + player.name + "...");
        sound.playMusicLoop();
        sound.setMusicVolume(settings.getMusicVolume());
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void handle(long now) {
        if (lastNanos < 0) {
            lastNanos = now;
            return;
        }
        double dt = Math.min(0.05, (now - lastNanos) / 1.0e9);
        lastNanos = now;

        if (state == GameState.PLAYING) {
            update(dt);
        }
        particles.update(dt);
        hud.update(dt);
        if (state == GameState.PLAYING || state == GameState.PAUSED || state == GameState.INVENTORY
                || state == GameState.HELP) {
            hud.render(player, monsters, layout);
        }
    }

    private void update(double dt) {
        handleMouseLook();
        handleMovement(dt);
        handleCombatInput();
        combat.update(player, dt);
        updateMonsters(dt);
        syncCamera();

        if (player.isDead()) {
            triggerEnd(false);
        } else if (isBossDefeated()) {
            triggerEnd(true);
        }
    }

    private void handleMouseLook() {
        double[] delta = input.consumeMouseDelta();
        player.yawDeg += delta[0] * MOUSE_SENSITIVITY;
        player.pitchDeg = clamp(player.pitchDeg + delta[1] * MOUSE_SENSITIVITY, -PITCH_LIMIT, PITCH_LIMIT);
    }

    private void handleMovement(double dt) {
        double forward = 0, strafe = 0;
        if (input.isDown(KeyCode.W)) forward += 1;
        if (input.isDown(KeyCode.S)) forward -= 1;
        if (input.isDown(KeyCode.D)) strafe += 1;
        if (input.isDown(KeyCode.A)) strafe -= 1;

        boolean spaceDown = input.isDown(KeyCode.SPACE);
        if (spaceDown && !spaceHeldLastFrame) {
            if (combat.tryDodge(player)) {
                sound.play(SoundManager.Sfx.DODGE);
            }
        }
        spaceHeldLastFrame = spaceDown;

        double speed = PLAYER_SPEED * (input.isDown(KeyCode.SHIFT) ? SPRINT_MULTIPLIER : 1.0);
        if (player.invulnerableTimer > 0) {
            speed *= 1.8; // Ausweichrolle: kurzer Tempo-Schub
        }

        double len = Math.hypot(forward, strafe);
        if (len > 0.0001) {
            forward /= len;
            strafe /= len;
            double yawRad = Math.toRadians(player.yawDeg);
            double fx = Math.sin(yawRad), fz = Math.cos(yawRad);
            double rx = Math.sin(yawRad + Math.PI / 2), rz = Math.cos(yawRad + Math.PI / 2);
            double dx = (fx * forward + rx * strafe) * speed * dt;
            double dz = (fz * forward + rz * strafe) * speed * dt;
            Collision.Position moved = collision.moveWithSliding(player.x, player.z, dx, dz, player.radius);
            player.x = moved.x();
            player.z = moved.z();
        }
    }

    private void handleCombatInput() {
        player.blocking = input.isBlockHeld();
        if (input.consumeAttackClick()) {
            CombatSystem.AttackResult result = combat.tryAttack(player, monsters);
            if (result.hit()) {
                sound.play(SoundManager.Sfx.SWING);
                sound.play(SoundManager.Sfx.HIT);
                hud.addDamagePopup("-" + result.damage(), Color.rgb(255, 200, 90));
                particles.spawnHitSpark(result.target().x, EYE_HEIGHT, result.target().z, Color.rgb(255, 210, 120));
                if (result.killed()) {
                    onMonsterKilled(result.target());
                }
            } else {
                sound.play(SoundManager.Sfx.SWING);
            }
        }
    }

    private void updateMonsters(double dt) {
        for (Monster m : monsters) {
            if (m.isDead()) continue;
            boolean landedHit = MonsterAI.update(m, player, collision, dt);
            if (landedHit) {
                int dmg = combat.resolveMonsterAttack(player, m);
                if (dmg > 0) {
                    sound.play(SoundManager.Sfx.HURT);
                    hud.addDamagePopup("-" + dmg, Color.rgb(255, 90, 90));
                }
            }
            MeshView view = monsterViews.get(m);
            if (view != null) {
                view.setTranslateX(m.x);
                view.setTranslateZ(m.z);
                view.getTransforms().setAll(new Rotate(m.facingYawDeg, Rotate.Y_AXIS));
                MonsterModelMapping.applyFlash(view, m.flashTimer > 0);
            }
        }
    }

    private void onMonsterKilled(Monster m) {
        MeshView view = monsterViews.remove(m);
        if (view != null) {
            monstersRoot.getChildren().remove(view);
        }
        particles.spawnDeathBurst(m.x, EYE_HEIGHT, m.z, Color.rgb(255, 140, 90));
        sound.play(SoundManager.Sfx.HIT);

        List<Item> drops = m.type.rollDrops(random);
        for (Item item : drops) {
            if (player.inventory.size() < player.maxInventory) {
                player.inventory.add(item);
                hud.addDamagePopup(item.name, Color.rgb(150, 220, 255));
            }
        }
        List<String> levelUps = player.gainXp(m.type.xp);
        score += m.type.xp * 10;
        if (!levelUps.isEmpty()) {
            sound.play(SoundManager.Sfx.LEVEL_UP);
            hud.showBanner("Level " + player.level + "!");
        }
        if (m.type == de.kkuester.pentagonquest3d.entities.MonsterType.ORK_KOENIG) {
            hud.showBanner("Der Ork-König ist gefallen!");
        }
    }

    private boolean isBossDefeated() {
        return monsters.stream()
                .filter(m -> m.type == de.kkuester.pentagonquest3d.entities.MonsterType.ORK_KOENIG)
                .allMatch(Monster::isDead);
    }

    private void syncCamera() {
        ((Rotate) cameraYaw.getTransforms().get(0)).setAngle(player.yawDeg);
        ((Rotate) cameraPitch.getTransforms().get(0)).setAngle(-player.pitchDeg);
        cameraYaw.setTranslateX(player.x);
        cameraYaw.setTranslateY(EYE_HEIGHT + Math.sin(System.nanoTime() / 3.0e8) * 0.01);
        cameraYaw.setTranslateZ(player.z);
    }

    private void triggerEnd(boolean victory) {
        state = victory ? GameState.WIN : GameState.GAME_OVER;
        input.setMouseLookActive(false);
        sound.play(victory ? SoundManager.Sfx.VICTORY : SoundManager.Sfx.DEATH);
        if (listener != null) {
            listener.onGameOver(victory, score, player.level);
        }
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
