package de.kkuester.pentagonquest3d.combat;

import de.kkuester.pentagonquest3d.ai.MonsterAI;
import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.Player;

import java.util.List;

/**
 * Spieler-Kampfressourcen (Angriff/Block/Ausweichrolle, Ausdauer) sowie die
 * Trefferauflösung für den Spielerangriff (Distanz + Blickkegel vor der Kamera).
 */
public class CombatSystem {

    public static final double ATTACK_RANGE = 1.1;
    public static final double ATTACK_CONE_DEG = 80;
    public static final double ATTACK_COOLDOWN = 0.42;
    public static final double DODGE_COST = 30;
    public static final double DODGE_COOLDOWN = 0.6;
    public static final double DODGE_INVULN_DURATION = 0.28;
    public static final double DODGE_BURST_SPEED = 6.5;
    public static final double BLOCK_STAMINA_DRAIN_PER_SEC = 22;
    public static final double STAMINA_REGEN_PER_SEC = 16;

    public record AttackResult(Monster target, int damage, boolean killed) {
        public static final AttackResult MISS = new AttackResult(null, 0, false);

        public boolean hit() {
            return target != null;
        }
    }

    /** Ausdauer-Regeneration/-Verbrauch sowie Cooldown-Ticks; einmal pro Frame aufrufen. */
    public void update(Player player, double dt) {
        if (player.attackCooldown > 0) {
            player.attackCooldown = Math.max(0, player.attackCooldown - dt);
        }
        if (player.dodgeCooldown > 0) {
            player.dodgeCooldown = Math.max(0, player.dodgeCooldown - dt);
        }
        if (player.invulnerableTimer > 0) {
            player.invulnerableTimer = Math.max(0, player.invulnerableTimer - dt);
        }
        if (player.blocking) {
            player.stamina = Math.max(0, player.stamina - BLOCK_STAMINA_DRAIN_PER_SEC * dt);
        } else if (player.stamina < player.maxStamina) {
            player.stamina = Math.min(player.maxStamina, player.stamina + STAMINA_REGEN_PER_SEC * dt);
        }
    }

    public boolean canAttack(Player player) {
        return player.attackCooldown <= 0;
    }

    /** Löst einen Nahkampf-Angriff aus: nächster lebender Gegner in Reichweite + Blickkegel. */
    public AttackResult tryAttack(Player player, List<Monster> monsters) {
        if (!canAttack(player)) {
            return AttackResult.MISS;
        }
        player.attackCooldown = ATTACK_COOLDOWN;

        Monster best = null;
        double bestDist = Double.MAX_VALUE;
        for (Monster m : monsters) {
            if (m.isDead()) {
                continue;
            }
            double dx = m.x - player.x;
            double dz = m.z - player.z;
            double dist = Math.hypot(dx, dz);
            if (dist > ATTACK_RANGE + m.radius) {
                continue;
            }
            double angleTo = Math.toDegrees(Math.atan2(dx, dz));
            if (Math.abs(normalizeAngle(angleTo - player.yawDeg)) > ATTACK_CONE_DEG / 2.0) {
                continue;
            }
            if (dist < bestDist) {
                bestDist = dist;
                best = m;
            }
        }
        if (best == null) {
            return AttackResult.MISS;
        }
        int damage = player.attackDamage;
        best.applyDamage(damage);
        MonsterAI.onHit(best);
        return new AttackResult(best, damage, best.isDead());
    }

    /** Wendet einen eingehenden Monster-Treffer auf den Spieler an (respektiert Ausweich-I-Frames). */
    public int resolveMonsterAttack(Player player, Monster attacker) {
        if (player.invulnerableTimer > 0) {
            return 0;
        }
        return player.receiveHit(attacker.attackDamage);
    }

    public boolean tryDodge(Player player) {
        if (player.dodgeCooldown > 0 || player.stamina < DODGE_COST) {
            return false;
        }
        player.stamina -= DODGE_COST;
        player.dodgeCooldown = DODGE_COOLDOWN;
        player.invulnerableTimer = Math.max(player.invulnerableTimer, DODGE_INVULN_DURATION);
        return true;
    }

    private static double normalizeAngle(double deg) {
        double a = deg % 360;
        if (a > 180) a -= 360;
        if (a < -180) a += 360;
        return a;
    }
}
