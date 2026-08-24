package de.kkuester.ufospiel.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DifficultyTest {

    @Test
    void next_cyclesThroughAllValuesAndWrapsAround() {
        assertEquals(Difficulty.NORMAL, Difficulty.EASY.next());
        assertEquals(Difficulty.HARD, Difficulty.NORMAL.next());
        assertEquals(Difficulty.EASY, Difficulty.HARD.next());
    }

    @Test
    void harderDifficulty_hasHigherSpeedFactor() {
        assertTrue(Difficulty.HARD.speedFactor > Difficulty.NORMAL.speedFactor);
        assertTrue(Difficulty.NORMAL.speedFactor > Difficulty.EASY.speedFactor);
    }
}
