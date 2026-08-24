package de.kkuester.pentagonquest3d.core;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class HighScoreStore {

    public record Entry(String name, int score) {
    }

    private static final int MAX_ENTRIES = 10;
    private final Preferences prefs = Preferences.userNodeForPackage(HighScoreStore.class);
    private final List<Entry> entries = new ArrayList<>();

    public HighScoreStore() {
        load();
    }

    private void load() {
        entries.clear();
        String raw = prefs.get("highscores", "");
        if (raw.isBlank()) {
            return;
        }
        for (String part : raw.split(";")) {
            String[] kv = part.split(":", 2);
            if (kv.length == 2) {
                try {
                    entries.add(new Entry(kv[0], Integer.parseInt(kv[1])));
                } catch (NumberFormatException ignored) {
                    // fehlerhafter Eintrag wird übersprungen
                }
            }
        }
        entries.sort((a, b) -> b.score() - a.score());
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        for (Entry e : entries) {
            if (sb.length() > 0) sb.append(';');
            sb.append(e.name().replace(":", "").replace(";", "")).append(':').append(e.score());
        }
        prefs.put("highscores", sb.toString());
    }

    public List<Entry> getEntries() {
        return List.copyOf(entries);
    }

    public boolean qualifies(int score) {
        return entries.size() < MAX_ENTRIES || entries.isEmpty()
                || score > entries.get(entries.size() - 1).score();
    }

    public void addScore(String name, int score) {
        entries.add(new Entry(name.isBlank() ? "Held" : name, score));
        entries.sort((a, b) -> b.score() - a.score());
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        save();
    }
}
