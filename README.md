# Java

![Java](https://img.shields.io/badge/Java-21+-orange)
![Status](https://img.shields.io/badge/status-Dokumentation-lightgrey)

---

## Inhalt

- [Java](#java)
  - [Inhalt](#inhalt)
  - [Repository-Struktur](#repository-struktur)
  - [Java CheatSheet](#java-cheatsheet)
  - [Voraussetzungen](#voraussetzungen)

---

## Repository-Struktur

```
.
├── java_cheatsheet.md      Nachschlagewerk: Sprachgrundlagen, OOP, Patterns
└── README.md
```

---

## Java CheatSheet

[`java_cheatsheet.md`](java_cheatsheet.md) deckt in 10 Kapiteln den Weg von der
Syntax bis zu den Entwurfsmustern ab. Jeder Codeblock ist zeilenweise
kommentiert; zwei Verzeichnisse (Kapitelübersicht + Detailverzeichnis) führen
direkt zur gesuchten Stelle.

| Kapitel | Inhalt |
|---|---|
| 1–2 | Programmaufbau, `main`, Variablen, Kontrollstrukturen (`if`, `switch`, Schleifen) |
| 3 | Arrays und Collections: `List`, `Queue`/`Deque`, `Set`, `Map` inkl. Auswahlhilfe |
| 4 | Exception-Handling: häufige Exceptions, `try-catch-finally`, eigene Exceptions |
| 5 | Klassen, Attribute, Sichtbarkeit, Datentypen, Wrapper-Klassen, Kapselung, Referenzen |
| 6 | Vererbung, Polymorphie, abstrakte Klassen, Interfaces |
| 7–8 | Typecasting (Up-/Downcasting, `instanceof`), Generics |
| 9 | Patterns: Factory Method, Composite, Visitor, Iterator, State |
| 10 | Schnellreferenz: Imports, Schlüsselwörter, Vergleich Java ↔ Python |

---

## Voraussetzungen

- **JDK 21 oder neuer** (LTS). Das CheatSheet nutzt an einzelnen Stellen neuere
  Sprachmittel: `switch` mit Pfeil-Syntax (ab 14) und `instanceof` mit Pattern
  Matching (ab 16)

Installation prüfen:

```bash
java -version
javac -version
```