package de.kkuester.pentagonquest3d.entities;

public class Monster extends Character {

    public enum AiState { IDLE, CHASE, ATTACK, HURT, DEAD }

    public final MonsterType type;
    public AiState aiState = AiState.IDLE;
    public double stateTimer;
    public double attackCooldown;
    public double flashTimer;
    public double facingYawDeg;
    public boolean lootDropped;

    /** Zellen-Koordinaten im Dungeon-Raster, in dem dieses Monster gespawnt wurde (für Streifzug-Grenzen). */
    public final int homeCol, homeRow;

    public Monster(MonsterType type, int homeCol, int homeRow) {
        super(type.hp, type.attackDamage, type.displayName);
        this.type = type;
        this.homeCol = homeCol;
        this.homeRow = homeRow;
        this.radius = 0.35 * type.meshScale;
    }
}
