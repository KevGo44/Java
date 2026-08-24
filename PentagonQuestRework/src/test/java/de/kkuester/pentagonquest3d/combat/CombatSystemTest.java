package de.kkuester.pentagonquest3d.combat;

import de.kkuester.pentagonquest3d.entities.Monster;
import de.kkuester.pentagonquest3d.entities.MonsterType;
import de.kkuester.pentagonquest3d.entities.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatSystemTest {

    private static Player playerFacingNorth() {
        Player p = new Player("Hero");
        p.x = 0; p.z = 0; p.yawDeg = 0; // per Konvention: forward = (sin(yaw), 0, cos(yaw)) = (0,0,1)
        return p;
    }

    @Test
    void tryAttack_hitsMonsterDirectlyInFrontWithinRange() {
        CombatSystem combat = new CombatSystem();
        Player player = playerFacingNorth();
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = 0; m.z = 0.5; // direkt vor dem Spieler, innerhalb ATTACK_RANGE

        CombatSystem.AttackResult result = combat.tryAttack(player, List.of(m));

        assertTrue(result.hit());
        assertEquals(m, result.target());
        assertEquals(player.attackDamage, result.damage());
    }

    @Test
    void tryAttack_missesMonsterOutOfRange() {
        CombatSystem combat = new CombatSystem();
        Player player = playerFacingNorth();
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = 0; m.z = 10;

        CombatSystem.AttackResult result = combat.tryAttack(player, List.of(m));

        assertFalse(result.hit());
    }

    @Test
    void tryAttack_missesMonsterBehindPlayer() {
        CombatSystem combat = new CombatSystem();
        Player player = playerFacingNorth();
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = 0; m.z = -0.5; // hinter dem Spieler, außerhalb des Blickkegels

        CombatSystem.AttackResult result = combat.tryAttack(player, List.of(m));

        assertFalse(result.hit());
    }

    @Test
    void tryAttack_ignoresAlreadyDeadMonsters() {
        CombatSystem combat = new CombatSystem();
        Player player = playerFacingNorth();
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = 0; m.z = 0.5;
        m.applyDamage(m.maxHp);

        CombatSystem.AttackResult result = combat.tryAttack(player, List.of(m));

        assertFalse(result.hit());
    }

    @Test
    void tryAttack_respectsCooldown() {
        CombatSystem combat = new CombatSystem();
        Player player = playerFacingNorth();
        Monster m1 = new Monster(MonsterType.GOBLIN, 0, 0);
        m1.x = 0; m1.z = 0.5;
        Monster m2 = new Monster(MonsterType.GOBLIN, 0, 0);
        m2.x = 0; m2.z = 0.5;

        assertTrue(combat.tryAttack(player, List.of(m1)).hit());
        assertFalse(combat.tryAttack(player, List.of(m2)).hit(), "sollte im Cooldown sein");
    }

    @Test
    void tryAttack_reportsKilledWhenHpReachesZero() {
        CombatSystem combat = new CombatSystem();
        Player player = playerFacingNorth();
        player.attackDamage = 9999;
        Monster m = new Monster(MonsterType.GOBLIN, 0, 0);
        m.x = 0; m.z = 0.5;

        CombatSystem.AttackResult result = combat.tryAttack(player, List.of(m));

        assertTrue(result.killed());
        assertTrue(m.isDead());
    }

    @Test
    void tryDodge_consumesStaminaAndRespectsCooldown() {
        CombatSystem combat = new CombatSystem();
        Player player = new Player("Hero");
        double staminaBefore = player.stamina;

        assertTrue(combat.tryDodge(player));
        assertEquals(staminaBefore - CombatSystem.DODGE_COST, player.stamina, 0.0001);
        assertFalse(combat.tryDodge(player), "sollte direkt danach im Cooldown sein");
    }

    @Test
    void tryDodge_failsWithoutEnoughStamina() {
        CombatSystem combat = new CombatSystem();
        Player player = new Player("Hero");
        player.stamina = 5;

        assertFalse(combat.tryDodge(player));
    }

    @Test
    void resolveMonsterAttack_isBlockedDuringInvulnerability() {
        CombatSystem combat = new CombatSystem();
        Player player = new Player("Hero");
        player.invulnerableTimer = 1.0;
        Monster attacker = new Monster(MonsterType.ORK, 0, 0);

        int dmg = combat.resolveMonsterAttack(player, attacker);

        assertEquals(0, dmg);
        assertEquals(player.maxHp, player.hp);
    }

    @Test
    void resolveMonsterAttack_dealsDamageWhenVulnerable() {
        CombatSystem combat = new CombatSystem();
        Player player = new Player("Hero");
        Monster attacker = new Monster(MonsterType.ORK, 0, 0);

        int dmg = combat.resolveMonsterAttack(player, attacker);

        assertTrue(dmg > 0);
        assertTrue(player.hp < player.maxHp);
    }
}
