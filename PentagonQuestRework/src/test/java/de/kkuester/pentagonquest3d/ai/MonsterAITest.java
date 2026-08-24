package de.kkuester.pentagonquest3d.ai;

import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.MonsterType;
import de.kkuester.pentagonquest3d.entities.Player;
import de.kkuester.pentagonquest3d.world.Collision;
import de.kkuester.pentagonquest3d.world.DungeonLayout;
import de.kkuester.pentagonquest3d.world.DungeonOccupancy;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MonsterAITest {

    private static Collision openCollision() {
        DungeonLayout layout = DungeonLayout.generate(new Random(5));
        return new Collision(DungeonOccupancy.build(layout));
    }

    /** Sicher im begehbaren Innenbereich der Startzelle liegender Punkt (kein Wandkontakt). */
    private static double roomCenterX() {
        return DungeonLayout.cellCenterX(0);
    }

    private static double roomCenterZ() {
        return DungeonLayout.cellCenterZ(0);
    }

    @Test
    void idleMonster_startsChasingOncePlayerIsNear() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = roomCenterX(); m.z = roomCenterZ();
        Player player = new Player("Hero");
        player.x = roomCenterX() + 1.5; player.z = roomCenterZ(); // innerhalb DETECT_RADIUS

        MonsterAI.update(m, player, openCollision(), 0.1);

        assertEquals(Monster.AiState.CHASE, m.aiState);
    }

    @Test
    void idleMonster_ignoresDistantPlayer() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = roomCenterX(); m.z = roomCenterZ();
        Player player = new Player("Hero");
        player.x = roomCenterX() + 50; player.z = roomCenterZ() + 50;

        MonsterAI.update(m, player, openCollision(), 0.1);

        assertEquals(Monster.AiState.IDLE, m.aiState);
    }

    @Test
    void chasingMonster_movesTowardPlayer() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.aiState = Monster.AiState.CHASE;
        m.x = roomCenterX() - 0.5; m.z = roomCenterZ();
        Player player = new Player("Hero");
        player.x = roomCenterX() + 0.5; player.z = roomCenterZ();

        double startX = m.x;
        MonsterAI.update(m, player, openCollision(), 0.1);

        assertTrue(m.x > startX, "Monster soll sich auf den Spieler zu bewegen");
    }

    @Test
    void chasingMonster_switchesToAttackWhenInMeleeRange() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.aiState = Monster.AiState.CHASE;
        m.x = roomCenterX(); m.z = roomCenterZ();
        Player player = new Player("Hero");
        player.x = roomCenterX() + MonsterType.GOBLIN.meleeRange * 0.5;
        player.z = roomCenterZ();

        MonsterAI.update(m, player, openCollision(), 0.1);

        assertEquals(Monster.AiState.ATTACK, m.aiState);
    }

    @Test
    void attackingMonster_landsHitOnlyAfterCooldownElapses() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.aiState = Monster.AiState.ATTACK;
        m.attackCooldown = 0; // sofort bereit
        m.x = roomCenterX(); m.z = roomCenterZ();
        Player player = new Player("Hero");
        player.x = roomCenterX() + MonsterType.GOBLIN.meleeRange * 0.5;
        player.z = roomCenterZ();

        boolean firstHit = MonsterAI.update(m, player, openCollision(), 0.1);
        assertTrue(firstHit, "Angriff sollte sofort auslösen, da Cooldown 0 war");

        boolean secondHit = MonsterAI.update(m, player, openCollision(), 0.1);
        assertFalse(secondHit, "direkt danach sollte der Cooldown noch laufen");
    }

    @Test
    void deadMonster_alwaysReportsDeadState() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.applyDamage(m.maxHp);
        Player player = new Player("Hero");

        MonsterAI.update(m, player, openCollision(), 0.1);

        assertEquals(Monster.AiState.DEAD, m.aiState);
    }

    @Test
    void onHit_putsMonsterIntoHurtStateWithFlash() {
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.aiState = Monster.AiState.CHASE;

        MonsterAI.onHit(m);

        assertEquals(Monster.AiState.HURT, m.aiState);
        assertTrue(m.flashTimer > 0);
    }
}
