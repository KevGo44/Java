package de.kkuester.pentagonquest3d.ai;

import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.Player;
import de.kkuester.pentagonquest3d.world.Collision;

/**
 * Einfache Zustandsautomat-KI: IDLE -> CHASE -> ATTACK -> (HURT) -> DEAD.
 * Verfolgung per direktem Steering zum Spieler, an die Kollisionsauflösung
 * gekoppelt - kein volles Pathfinding nötig, der Dungeon ist klein genug und
 * die Korridore ausreichend direkt.
 */
public final class MonsterAI {

    private static final double DETECT_RADIUS = 4.0;
    private static final double LOSE_INTEREST_RADIUS = 6.5;
    private static final double CHASE_SPEED = 1.15;
    private static final double ATTACK_COOLDOWN = 1.3;
    private static final double HURT_DURATION = 0.25;

    private MonsterAI() {
    }

    /** Aktualisiert das Monster einen Frame lang; gibt true zurück, wenn genau jetzt ein Treffer landet. */
    public static boolean update(Monster m, Player player, Collision collision, double dt) {
        if (m.isDead()) {
            m.aiState = Monster.AiState.DEAD;
            return false;
        }
        if (m.flashTimer > 0) {
            m.flashTimer = Math.max(0, m.flashTimer - dt);
        }
        if (m.attackCooldown > 0) {
            m.attackCooldown -= dt;
        }

        double dx = player.x - m.x;
        double dz = player.z - m.z;
        double dist = Math.hypot(dx, dz);

        switch (m.aiState) {
            case IDLE -> {
                if (dist < DETECT_RADIUS) {
                    m.aiState = Monster.AiState.CHASE;
                }
            }
            case CHASE -> {
                if (dist > LOSE_INTEREST_RADIUS) {
                    m.aiState = Monster.AiState.IDLE;
                    break;
                }
                if (dist <= m.type.meleeRange + player.radius) {
                    m.aiState = Monster.AiState.ATTACK;
                    break;
                }
                double invLen = dist > 0.0001 ? 1.0 / dist : 0;
                double moveX = dx * invLen * CHASE_SPEED * dt;
                double moveZ = dz * invLen * CHASE_SPEED * dt;
                Collision.Position moved = collision.moveWithSliding(m.x, m.z, moveX, moveZ, m.radius);
                m.x = moved.x();
                m.z = moved.z();
                m.facingYawDeg = Math.toDegrees(Math.atan2(dx, dz));
            }
            case ATTACK -> {
                m.facingYawDeg = Math.toDegrees(Math.atan2(dx, dz));
                if (dist > m.type.meleeRange + player.radius + 0.3) {
                    m.aiState = Monster.AiState.CHASE;
                    break;
                }
                if (m.attackCooldown <= 0) {
                    m.attackCooldown = ATTACK_COOLDOWN;
                    return true;
                }
            }
            case HURT -> {
                m.stateTimer -= dt;
                if (m.stateTimer <= 0) {
                    m.aiState = Monster.AiState.CHASE;
                }
            }
            case DEAD -> {
                // nichts zu tun
            }
        }
        return false;
    }

    public static void onHit(Monster m) {
        m.flashTimer = 0.14;
        if (!m.isDead()) {
            m.aiState = Monster.AiState.HURT;
            m.stateTimer = HURT_DURATION;
        }
    }
}
