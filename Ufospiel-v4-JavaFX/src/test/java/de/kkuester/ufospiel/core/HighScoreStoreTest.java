package de.kkuester.ufospiel.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HighScoreStore persists via java.util.prefs, which is real user-level OS storage
 * shared with the running game. Each test backs up and restores that one key so the
 * suite never leaves stray data behind for the actual game to pick up.
 */
class HighScoreStoreTest {

    private final Preferences prefs = Preferences.userNodeForPackage(HighScoreStore.class);
    private String backup;

    @BeforeEach
    void backupExistingScores() {
        backup = prefs.get("highscores", null);
        prefs.remove("highscores");
    }

    @AfterEach
    void restoreExistingScores() {
        if (backup == null) {
            prefs.remove("highscores");
        } else {
            prefs.put("highscores", backup);
        }
    }

    @Test
    void addScore_ordersEntriesHighestFirst() {
        HighScoreStore store = new HighScoreStore();
        store.addScore("Anna", 500);
        store.addScore("Ben", 1500);
        store.addScore("Cara", 1000);

        List<HighScoreStore.Entry> entries = store.getEntries();
        assertEquals(3, entries.size());
        assertEquals("Ben", entries.get(0).name());
        assertEquals("Cara", entries.get(1).name());
        assertEquals("Anna", entries.get(2).name());
    }

    @Test
    void addScore_capsListAtTenEntries() {
        HighScoreStore store = new HighScoreStore();
        for (int i = 0; i < 15; i++) {
            store.addScore("Pilot" + i, i * 100);
        }
        assertEquals(10, store.getEntries().size());
        assertEquals(1400, store.getEntries().get(0).score(), "the highest score should survive the cap");
    }

    @Test
    void qualifies_isTrueWhenListNotYetFull() {
        HighScoreStore store = new HighScoreStore();
        assertTrue(store.qualifies(1));
    }

    @Test
    void qualifies_isFalseForLowScoreOnceListIsFull() {
        HighScoreStore store = new HighScoreStore();
        for (int i = 0; i < 10; i++) {
            store.addScore("Pilot" + i, 1000 + i);
        }
        assertFalse(store.qualifies(0));
        assertTrue(store.qualifies(999999));
    }

    @Test
    void reloadingFromPreferences_restoresPersistedEntries() {
        HighScoreStore store = new HighScoreStore();
        store.addScore("Dora", 777);

        HighScoreStore reloaded = new HighScoreStore();
        assertEquals(1, reloaded.getEntries().size());
        assertEquals("Dora", reloaded.getEntries().get(0).name());
        assertEquals(777, reloaded.getEntries().get(0).score());
    }
}
