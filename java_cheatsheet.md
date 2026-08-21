# Java CheatSheet

> Nachschlagewerk mit kommentierten Beispielen — Schwerpunkt Objektorientierung.
> Zwei Verzeichnisse: **Übersicht** (nur Kapitel) zum groben Orientieren,
> **Detail** (alle Unterpunkte) zum direkten Anspringen.

---

## Übersicht

- [1. Grundlagen & Programmaufbau](#1-grundlagen--programmaufbau)
- [2. Kontrollstrukturen](#2-kontrollstrukturen)
- [3. Datenstrukturen](#3-datenstrukturen)
- [4. Exception-Handling](#4-exception-handling)
- [5. Klassen, Attribute & Methoden](#5-klassen-attribute--methoden)
- [6. Vererbung & Polymorphie](#6-vererbung--polymorphie)
- [7. Typecasting](#7-typecasting)
- [8. Generics](#8-generics)
- [9. Patterns](#9-patterns)
- [10. Schnellreferenz](#10-schnellreferenz)

---

## Inhaltsverzeichnis (Detail)

- [1. Grundlagen & Programmaufbau](#1-grundlagen--programmaufbau)
    - [1.1 Grundgerüst & Ausführung](#11-grundgerüst--ausführung)
    - [1.2 Variablen & Ausgabe](#12-variablen--ausgabe)
- [2. Kontrollstrukturen](#2-kontrollstrukturen)
    - [2.1 Verzweigungen](#21-verzweigungen)
    - [2.2 switch](#22-switch)
    - [2.3 Schleifen](#23-schleifen)
- [3. Datenstrukturen](#3-datenstrukturen)
    - [3.1 Arrays](#31-arrays)
    - [3.2 Listen (`List`) — Beispiel `ArrayList`](#32-listen-list--beispiel-arraylist)
    - [3.3 Queues & Stacks](#33-queues--stacks)
    - [3.4 Sets (`Set`) — Beispiel `HashSet`](#34-sets-set--beispiel-hashset)
    - [3.5 Maps (`Map`) — Beispiel `HashMap`](#35-maps-map--beispiel-hashmap)
    - [3.6 Auswahlhilfe](#36-auswahlhilfe)
- [4. Exception-Handling](#4-exception-handling)
    - [4.1 Häufige Exceptions](#41-häufige-exceptions)
    - [4.2 try-catch-finally](#42-try-catch-finally)
    - [4.3 Eigene Exceptions & `throw`](#43-eigene-exceptions--throw)
- [5. Klassen, Attribute & Methoden](#5-klassen-attribute--methoden)
    - [5.1 Klassen und Objekte](#51-klassen-und-objekte)
    - [5.2 Attribute & Sichtbarkeit](#52-attribute--sichtbarkeit)
    - [5.3 Datentypen: primitiv vs. Referenz](#53-datentypen-primitiv-vs-referenz)
    - [5.4 Wrapper-Klassen](#54-wrapper-klassen)
    - [5.5 Methoden & Konstruktoren](#55-methoden--konstruktoren)
    - [5.6 Punktnotation & `this`](#56-punktnotation--this)
    - [5.7 Abstraktion & Kapselung](#57-abstraktion--kapselung)
    - [5.8 Referenzen, `==` vs. `equals()`](#58-referenzen--vs-equals)
- [6. Vererbung & Polymorphie](#6-vererbung--polymorphie)
    - [6.1 Vererbung & `super`](#61-vererbung--super)
    - [6.2 Abstrakte Klassen & Polymorphie](#62-abstrakte-klassen--polymorphie)
    - [6.3 Interfaces](#63-interfaces)
- [7. Typecasting](#7-typecasting)
    - [7.1 Upcasting](#71-upcasting)
    - [7.2 Downcasting & `instanceof`](#72-downcasting--instanceof)
- [8. Generics](#8-generics)
- [9. Patterns](#9-patterns)
    - [9.1 Factory Method](#91-factory-method)
    - [9.2 Composite Pattern](#92-composite-pattern)
    - [9.3 Visitor Pattern](#93-visitor-pattern)
    - [9.4 Iterator Pattern](#94-iterator-pattern)
    - [9.5 State Pattern](#95-state-pattern)
- [10. Schnellreferenz](#10-schnellreferenz)
    - [10.1 Häufige Imports](#101-häufige-imports)
    - [10.2 Schlüsselwörter auf einen Blick](#102-schlüsselwörter-auf-einen-blick)
    - [10.3 Java vs. Python](#103-java-vs-python)

---

## 1. Grundlagen & Programmaufbau

### 1.1 Grundgerüst & Ausführung

```java
// Jede öffentliche Klasse braucht eine Datei mit GENAU demselben Namen:
// HelloWorld.java  ->  public class HelloWorld
public class HelloWorld {

    // main = Einstiegspunkt; die JVM sucht beim Start exakt diese Signatur
    public static void main(String[] args) {   // args = Kommandozeilenargumente
        System.out.println("Hallo Welt");      // println: Ausgabe MIT Zeilenumbruch
        System.out.print("ohne Umbruch");      // print:   Ausgabe OHNE Zeilenumbruch
    }
}
// static = gehört zur Klasse, nicht zum Objekt -> die JVM kann main aufrufen,
//          ohne vorher ein Objekt zu erzeugen (siehe 5.2)
```

```bash
javac HelloWorld.java   # kompiliert zu HelloWorld.class (Bytecode)
java HelloWorld         # startet main() — OHNE die Endung .class
```

### 1.2 Variablen & Ausgabe

```java
int x = 12;                      // Typ steht IMMER vor dem Namen (statisch typisiert)
final double PI = 3.14159;       // final = Wert kann nicht mehr geändert werden (Konstante)
var name = "Ada";                // ab Java 10: Typ wird aus dem Wert abgeleitet (bleibt String!)

// Jede Anweisung endet mit einem Semikolon, Blöcke stehen in { }
System.out.println("Name: " + name + ", x = " + x);   // '+' verkettet Strings
System.out.printf("%s ist %d Jahre alt%n", name, 36); // formatiert: %s String, %d Ganzzahl, %n Umbruch
```

---

## 2. Kontrollstrukturen

Kontrollstrukturen steuern, in welcher Reihenfolge Anweisungen ausgeführt werden.

### 2.1 Verzweigungen

```java
int x = 12;
// Die Bedingung muss IMMER einen boolean liefern (anders als in C/Python:
// eine Zahl allein ist keine gültige Bedingung).
if (x > 10) {
    System.out.println("größer als 10");
} else if (x == 10) {            // beliebig viele else-if-Zweige möglich
    System.out.println("gleich 10");
} else {                         // optionaler Auffangzweig
    System.out.println("kleiner als 10");
}
// Ausgabe: größer als 10

// Kurzform (ternärer Operator): Bedingung ? Wert-wenn-wahr : Wert-wenn-falsch
String text = (x > 0) ? "positiv" : "nicht positiv";
```

### 2.2 switch

```java
int wochentag = 2;
// switch prüft EINEN Wert gegen mehrere Möglichkeiten -> kompakter als if-else-Ketten
switch (wochentag) {
    case 1 -> System.out.println("Montag");
    case 2 -> System.out.println("Dienstag");
    case 3, 4 -> System.out.println("Mittwoch oder Donnerstag");   // mehrere Werte pro case
    default -> System.out.println("Wochenende oder unbekannt");    // greift, wenn nichts passt
}
// Ausgabe: Dienstag

// Pfeil-Syntax (ab Java 14): kein 'break' nötig, es läuft NUR der passende Zweig.
// Alte Doppelpunkt-Syntax: ohne 'break' läuft der nächste case mit (Fall-Through!)
switch (wochentag) {
    case 1:
        System.out.println("Montag");
        break;                    // ohne break geht es in case 2 weiter
    default:
        System.out.println("anderer Tag");
}

// switch als AUSDRUCK: liefert einen Wert zurück
String tag = switch (wochentag) {
    case 1 -> "Montag";
    case 2 -> "Dienstag";
    default -> "unbekannt";
};
```

### 2.3 Schleifen

```java
// klassische for-Schleife: Startwert; Bedingung; Schrittweite
for (int i = 0; i < 3; i++) {     // i++ = i um 1 erhöhen
    System.out.print(i + " ");
}                                  // -> 0 1 2

// foreach (enhanced for): läuft über alle Elemente, ohne Index
int[] arr = {10, 20, 30};
for (int v : arr) {               // lies: "für jedes v aus arr"
    System.out.print(v + " ");
}                                  // -> 10 20 30
// Achtung: kein Index verfügbar und kein Ändern der Sammlung während des Laufs

// while: prüft VOR dem Durchlauf -> läuft evtl. gar nicht
int n = 3;
while (n > 0) {
    System.out.print(n + " ");
    n--;                          // Abbruchbedingung nicht vergessen -> sonst Endlosschleife
}                                  // -> 3 2 1

// do-while: prüft NACH dem Durchlauf -> läuft mindestens einmal
int m = 0;
do {
    System.out.print("läuft mindestens einmal ");
} while (m > 0);

// Steuerung innerhalb jeder Schleife:
//   break    -> Schleife sofort komplett verlassen
//   continue -> Rest des Durchlaufs überspringen, nächste Iteration starten
```

---

## 3. Datenstrukturen

Eine Datenstruktur bestimmt, wie Daten angeordnet und verwaltet werden. Java kennt
zwei Kategorien: **Arrays** (fest, sehr schnell) und das **Collections-Framework**
(dynamisch, flexibel). `Collection` ist dabei selbst ein Interface, das die
Grundoperationen für die Verwaltung von Objekten vorgibt.

### 3.1 Arrays

```java
// Ein Array ist eine Sammlung von Elementen GLEICHEN Typs mit FESTER Länge.
// Die Länge wird beim Anlegen festgelegt und ist danach unveränderlich.
int[] nums = new int[3];          // [] hinter dem Typ; alle Felder starten mit 0
nums[0] = 5; nums[1] = 9; nums[2] = 2;

System.out.println(nums.length);  // 3  -> length ist ein FELD, keine Methode (kein length()!)
System.out.println(nums[1]);      // 9  -> Indexzugriff in O(1), Zählung ab 0

int[] direkt = {10, 20, 30};      // Kurzform mit sofortiger Belegung
// nums[3] -> ArrayIndexOutOfBoundsException: gültig sind nur 0 .. length-1
```

Eigenschaften: feste Länge · Indexzugriff in O(1) · homogen (nur ein Datentyp).
Für dynamische Sammlungen stattdessen Collections verwenden.

### 3.2 Listen (`List`) — Beispiel `ArrayList`

```java
import java.util.ArrayList;
import java.util.List;

// Links das INTERFACE (List), rechts die konkrete Implementierung (ArrayList).
// Vorteil: die Implementierung lässt sich später austauschen, ohne den Rest zu ändern.
List<String> namen = new ArrayList<>();   // <> = Diamond Operator, Typ wird übernommen
namen.add("Ada");
namen.add("Linus");
namen.add("Ada");                          // Duplikate sind erlaubt

System.out.println(namen.get(1));          // Linus -> Zugriff über Index
System.out.println(namen.size());          // 3     -> size() statt length!
// Intern arbeitet ArrayList mit einem Array, das bei Bedarf automatisch vergrößert wird.
```

| Methode | Wirkung |
|---|---|
| `add(E e)` | fügt ein Element hinzu |
| `get(int index)` | liest das Element an der Position |
| `remove(int index)` / `remove(Object o)` | entfernt per Index bzw. per Wert |
| `size()` | Anzahl der Elemente |
| `contains(Object o)` | prüft, ob ein Wert enthalten ist |

### 3.3 Queues & Stacks

Eine **Queue** ist eine Warteschlange (FIFO — First In, First Out), ein **Stack**
ein Stapel (LIFO — Last In, First Out). `Queue` ist ein Interface, `ArrayDeque`
eine passende Implementierung für beides. Intern lässt sich eine Queue als einfach
verkettete Liste denken: jedes Element kennt seinen Nachfolger, ein Zeiger wandert
über die Kette. Die Datenstruktur schränkt die Zugriffe bewusst ein, damit die
FIFO- bzw. LIFO-Eigenschaft garantiert bleibt.

```java
import java.util.*;

// --- Queue (FIFO): hinten rein, vorne raus ---
Queue<String> q = new ArrayDeque<>();
q.offer("A"); q.offer("B");        // offer = am Ende einreihen
System.out.println(q.poll());      // A -> ältestes Element entfernen und liefern
System.out.println(q.peek());      // B -> nur ansehen, NICHT entfernen

// --- Stack (LIFO): oben rein, oben raus ---
Deque<Integer> st = new ArrayDeque<>();
st.push(1); st.push(2); st.push(3);   // push = oben auflegen
System.out.println(st.pop());         // 3 -> zuletzt Aufgelegtes zuerst
System.out.println(st.peek());        // 2 -> oberstes Element ansehen
```

| Queue | Wirkung |
|---|---|
| `offer(E e)` | Element am Ende einfügen (`true` bei Erfolg) |
| `poll()` | erstes Element entfernen und zurückgeben (`null`, wenn leer) |
| `peek()` | erstes Element ansehen, ohne zu entfernen (`null`, wenn leer) |
| `isEmpty()` / `size()` | leer? / Anzahl der Elemente |

| Stack | Wirkung |
|---|---|
| `push(E e)` | Element oben auflegen |
| `pop()` | oberstes Element entfernen und zurückgeben (Exception, wenn leer) |
| `peek()` | oberstes Element ansehen, ohne zu entfernen |
| `isEmpty()` / `size()` | leer? / Anzahl der Elemente |

> Die alte Klasse `java.util.Stack` existiert zwar noch, gilt aber als veraltet —
> `Deque` / `ArrayDeque` ist die heute übliche Wahl.

### 3.4 Sets (`Set`) — Beispiel `HashSet`

```java
import java.util.HashSet;
import java.util.Set;

// Ein Set speichert nur EINDEUTIGE Werte — Duplikate werden still verworfen.
// HashSet stellt die Eindeutigkeit über eine Hashfunktion sicher.
Set<Integer> zahlen = new HashSet<>();
zahlen.add(3); zahlen.add(3); zahlen.add(1);   // die zweite 3 wird ignoriert

System.out.println(zahlen.size());         // 2
System.out.println(zahlen.contains(1));    // true -> Suche in O(1) statt O(n)
// Reihenfolge ist NICHT garantiert; wer sie braucht: LinkedHashSet oder TreeSet
```

| Methode | Wirkung |
|---|---|
| `add(E e)` | fügt hinzu (`false`, wenn schon vorhanden) |
| `remove(Object o)` | entfernt das Element |
| `contains(Object o)` | prüft Existenz |
| `size()` | Anzahl der Elemente |

> Damit **eigene Klassen** korrekt in einem Set funktionieren, müssen `equals()`
> und `hashCode()` überschrieben werden — sonst gilt jedes Objekt als verschieden.

### 3.5 Maps (`Map`) — Beispiel `HashMap`

```java
import java.util.*;

// Map speichert Schlüssel-Wert-Paare. Map ist ein Interface, HashMap die
// gängige Implementierung: der Key wird über hashCode() in einen Index
// (Bucket) übersetzt. Landen zwei Keys im selben Bucket (Kollision), wird dort
// eine Liste geführt und mit equals() der richtige Eintrag gesucht.
Map<String, Integer> punkte = new HashMap<>();   // <Key-Typ, Wert-Typ>
punkte.put("Alice", 10);
punkte.put("Bob", 15);
punkte.put("Alice", 12);                  // gleicher Key -> alter Wert wird ÜBERSCHRIEBEN

System.out.println(punkte.get("Alice"));         // 12
System.out.println(punkte.containsKey("Bob"));   // true
System.out.println(punkte.getOrDefault("Eve", 0));  // 0 statt null bei fehlendem Key

// Durchlaufen aller Paare
for (Map.Entry<String, Integer> e : punkte.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}
```

| Methode | Wirkung |
|---|---|
| `put(K key, V value)` | Paar einfügen oder überschreiben |
| `get(Object key)` | Wert zum Schlüssel abrufen (`null`, wenn nicht vorhanden) |
| `remove(Object key)` | Paar entfernen |
| `containsKey(Object key)` | prüft, ob der Schlüssel existiert |
| `keySet()` / `values()` / `entrySet()` | alle Schlüssel / alle Werte / alle Paare |

### 3.6 Auswahlhilfe

| Ich brauche … | Struktur | Kernaussage |
|---|---|---|
| feste Anzahl gleicher Werte | `int[]` | schnellster Zugriff, Länge unveränderlich |
| Reihenfolge, Duplikate erlaubt | `ArrayList` | Standardfall, Zugriff per Index |
| Warteschlange (FIFO) | `ArrayDeque` als `Queue` | `offer` / `poll` |
| Stapel (LIFO) | `ArrayDeque` als `Deque` | `push` / `pop` |
| jeder Wert nur einmal | `HashSet` | Eindeutigkeit über `hashCode()` |
| Zuordnung Schlüssel → Wert | `HashMap` | `put` / `get` |

---

## 4. Exception-Handling

Ein Laufzeitfehler würde das Programm abstürzen lassen. Über Exceptions lässt sich
ein solcher Fehler abfangen und im Programm behandeln, statt den Ablauf zu beenden.

### 4.1 Häufige Exceptions

| Exception | Ursache | Beispiel |
|---|---|---|
| `NullPointerException` | Zugriff auf ein Objekt, das `null` ist | `String s = null; s.length();` |
| `ArrayIndexOutOfBoundsException` | Index außerhalb der Array-Grenzen | `int[] a = new int[3]; a[3];` |
| `StringIndexOutOfBoundsException` | ungültiger Index bei String-Operation | `"Hallo".charAt(10);` |
| `IllegalArgumentException` | ungültiges Argument in einer Methode | `Thread.sleep(-5);` |
| `ArithmeticException` | mathematisch ungültige Operation | `int x = 1 / 0;` |
| `NumberFormatException` | String lässt sich nicht in eine Zahl wandeln | `Integer.parseInt("abc");` |

> **Unchecked** (erben von `RuntimeException`, u. a. alle oben genannten): das
> Behandeln ist möglich, aber nicht verpflichtend. **Checked** Exceptions
> (z. B. `IOException`) muss der Compiler-Vertrag erzwingen — sie müssen gefangen
> oder per `throws` weitergereicht werden.

### 4.2 try-catch-finally

```java
try {
    int z = Integer.parseInt("42");        // hier kann es krachen
    System.out.println(z);
} catch (NumberFormatException ex) {       // beliebig viele catch-Blöcke möglich;
    System.out.println("Ungültige Zahl");  // spezielle Typen ZUERST, allgemeine danach
} catch (Exception ex) {                   // Auffangnetz für alles Übrige
    System.out.println("Sonstiger Fehler: " + ex.getMessage());
} finally {
    System.out.println("Aufräumen abgeschlossen.");  // läuft IMMER — auch nach return
}
// Ausgabe:
// 42
// Aufräumen abgeschlossen.

// try-with-resources: schließt die Ressource automatisch (Gegenstück zu Pythons 'with')
try (Scanner sc = new Scanner(System.in)) {
    String zeile = sc.nextLine();
}   // sc.close() passiert hier automatisch, auch bei einer Exception
```

### 4.3 Eigene Exceptions & `throw`

```java
// Eigene Exception = eigene Klasse, die von RuntimeException (unchecked)
// oder von Exception (checked) erbt.
class NegativeValueException extends RuntimeException {
    public NegativeValueException(String msg) {
        super(msg);                        // Meldung an die Oberklasse durchreichen
    }
}

static int sqrtInt(int x) {
    if (x < 0) {
        throw new NegativeValueException("negativer Wert: " + x);   // Fehler auslösen
    }
    return (int) Math.sqrt(x);
}
// throw  = Exception JETZT auslösen
// throws = im Methodenkopf ankündigen, dass die Methode sie weiterreichen kann
```

---

## 5. Klassen, Attribute & Methoden

### 5.1 Klassen und Objekte

Eine **Klasse** ist ein Bauplan: sie legt Struktur und Verwendungszweck fest und
erzeugt beliebig viele Objekte mit gleichen Eigenschaften. Ein **Objekt** ist eine
konkrete Instanz dieses Bauplans. Zur Laufzeit läuft die gesamte Wechselwirkung
eines Programms ausschließlich zwischen Objekten ab.

```
┌──────────────────────────────────────────────┐
│ Piece                            ← Klassenname
├──────────────────────────────────────────────┤
│ kind: Kind                       ← Attribute
│ color: Color                       (Zustand)
│ board: Board
│ row: int
│ col: int
├──────────────────────────────────────────────┤
│ Piece(kind, color, board, row, col)  ← Konstruktor
│ charRep(): char                  ← Methoden
│ toString(): String                 (Verhalten)
│ canCapture(other: Piece): boolean
└──────────────────────────────────────────────┘
```

```java
public class Piece {                 // Bauplan
    // ... Attribute, Konstruktor, Methoden
}

Piece p = new Piece(...);            // Objekt (Instanz) erzeugen
// new  -> legt das Objekt im Heap an und ruft den Konstruktor auf
// p    -> Referenzvariable, die auf dieses Objekt zeigt (siehe 5.8)
```

### 5.2 Attribute & Sichtbarkeit

Attribute sind Variablen einer Klasse, die im Vorfeld deklariert werden und
Eigenschaften repräsentieren. Sie sind innerhalb der gesamten Klasse sichtbar.

| Modifikator | Sichtbarkeit |
|---|---|
| `public` | von überall |
| `private` | nur innerhalb der Klasse selbst |
| `protected` | zusätzlich in Unterklassen und im selben Paket |
| *(ohne)* | nur im selben Paket (Package-Private) |

```java
public class Konto {
    private double kontostand;       // Instanzattribut: pro Objekt eigener Wert
    static int anzahlKonten = 0;     // Klassenattribut: EINMAL für die ganze Klasse

    public Konto(double start) {
        this.kontostand = start;     // erstmalige Wertbelegung = Initialisierung
        anzahlKonten++;              // gilt objektübergreifend
    }
}

Konto.anzahlKonten;   // static -> Zugriff über die KLASSE, nicht über ein Objekt
// Dasselbe gilt für statische Methoden (z. B. Math.sqrt oder main).
```

### 5.3 Datentypen: primitiv vs. Referenz

Ein Datentyp beschreibt, wie Daten im Speicher interpretiert werden: das Bitmuster
`01000001` ergibt als Ganzzahl **65**, nach ASCII gelesen den Buchstaben **A**.

| Typ | Bedeutung |
|---|---|
| `int` | Ganzzahlen |
| `double` | Gleitkommazahlen, 64 Bit |
| `float` | Gleitkommazahlen, 32 Bit |
| `char` | einzelnes Zeichen |
| `String` | Zeichenkette (Referenztyp!) |
| `boolean` | Wahrheitswert `true` / `false` |

```java
// PRIMITIV: speichert den Wert direkt, keine Methoden verfügbar
int a = 5;
double d = 2.5;
boolean flag = true;

// REFERENZ: speichert eine ADRESSE im Heap, dort liegt der eigentliche Datensatz;
// dazu gehören Objekte einer Klasse inklusive passender Methoden
String s = "Hallo";
int[] arr = new int[3];
Konto k = new Konto(100);
```

### 5.4 Wrapper-Klassen

Zu jedem primitiven Typ gibt es eine Wrapper-Klasse, die den Wert als Objekt
„verpackt" und nützliche Funktionen mitbringt (nötig z. B. für `List<Integer>`,
da Collections keine primitiven Typen aufnehmen).

```java
Integer boxed = 42;              // Autoboxing: int -> Integer (automatisch)
int prim = boxed;                // Unboxing:  Integer -> int
```

| `Integer` | Beschreibung |
|---|---|
| `Integer.parseInt(String)` | wandelt String in `int` (primitiv) |
| `Integer.valueOf(String)` | wie `parseInt`, liefert aber ein `Integer`-Objekt |
| `intValue()` | gibt den `int`-Wert eines `Integer`-Objekts zurück |
| `toString()` | wandelt `Integer` in `String` |
| `compareTo(Integer)` / `compare(int x, int y)` | Vergleich zweier Werte (−1, 0, 1) |
| `max(x, y)` / `min(x, y)` | Maximum / Minimum |
| `Integer.toBinaryString(n)` / `toHexString(n)` | Binär- bzw. Hexdarstellung als String |

| `Double` | Beschreibung |
|---|---|
| `Double.parseDouble(String)` | wandelt String in `double` (primitiv) |
| `Double.valueOf(String)` | liefert ein `Double`-Objekt |
| `doubleValue()` / `toString()` | Wert als `double` / als String |
| `isNaN()` / `isInfinite()` | prüft auf Sonderfälle |
| `compare(double a, double b)` | Vergleich zweier Werte |

| `Character` | Beschreibung |
|---|---|
| `Character.isLetter(c)` / `isDigit(c)` | Buchstabe? / Ziffer? |
| `Character.isWhitespace(c)` | Leerzeichen oder Whitespace? |
| `Character.toUpperCase(c)` / `toLowerCase(c)` | Groß- / Kleinbuchstabe |
| `charValue()` / `toString()` | Wert als `char` / als String |

| `Boolean` | Beschreibung |
|---|---|
| `Boolean.parseBoolean(String)` | liefert primitiven `boolean` |
| `Boolean.valueOf(String)` | liefert `Boolean`-Objekt (nur `"true"` ergibt `true`) |
| `booleanValue()` / `toString()` | primitiver Wert / `"true"` bzw. `"false"` |
| `compare(boolean x, boolean y)` | Vergleich (`false` < `true`) |
| `Boolean.TRUE` / `Boolean.FALSE` | konstante Werte |

### 5.5 Methoden & Konstruktoren

```java
public class Beispiel {

    // KONSTRUKTOR: heißt wie die Klasse, hat KEINEN Rückgabetyp.
    // Hier werden meist alle charakterisierenden Attributwerte übergeben.
    public Beispiel(String name) { /* ... */ }

    // METHODE: Rückgabetyp -> Name -> (Parameter)
    public int addiere(int a, int b) {   // a, b = Parameter
        return a + b;                    // Rückgabewert muss zum Typ passen
    }

    public void zeigeAn() {              // void = gibt bewusst NICHTS zurück
        System.out.println("nichts zurück");
    }

    // ÜBERLADEN: gleicher Name, unterschiedliche Parameterlisten.
    // Der Compiler wählt anhand der übergebenen Argumente die passende Variante.
    public void printA(String input) { System.out.println(input); }
    public void printA()             { System.out.println("no input"); }
}

printA("");        // gibt einen leeren String aus
printA();          // gibt "no input" aus
printA("test");    // gibt "test" aus
```

### 5.6 Punktnotation & `this`

```java
objekt.methode();          // Methodenaufruf über das Objekt
objekt.attribut;           // Attributzugriff — nur, wenn die Sichtbarkeit es erlaubt
Klasse.statischeMethode(); // static -> über die Klasse ansprechen

public class Punkt {
    private int x;
    public Punkt(int x) {
        this.x = x;        // this = dieses Objekt selbst;
    }                      // trennt hier das Attribut x vom Parameter x
}
```

### 5.7 Abstraktion & Kapselung

**Abstraktion** reduziert ein System auf die wesentlichen Merkmale: eine Klasse
bekommt nur die Attribute und Methoden, die sie für ihren Zweck wirklich braucht.
**Kapselung** ergänzt das, indem der Zustand über Modifikatoren nach außen
abgeschirmt wird — Zugriff nur über kontrollierte Methoden (Getter/Setter), in
denen sich zusätzlich Bedingungen wie zulässige Wertebereiche prüfen lassen.
Zusammen ergibt das die **Single Responsibility**: jede Klasse erfüllt genau eine
Aufgabe und gibt nur so viel preis wie nötig.

```java
public class GeheimSafe {
    private String passwort;                  // von außen unsichtbar

    public GeheimSafe(String passwort) {
        this.passwort = passwort;
    }

    // einzige nach außen erlaubte Operation — das Passwort selbst bleibt verborgen
    public boolean pruefePasswort(String eingabe) {
        return passwort.equals(eingabe);
    }
}
```

### 5.8 Referenzen, `==` vs. `equals()`

Eine Referenz ist ein Zeiger auf eine Speicherposition im Heap. Beim Erzeugen eines
Objekts landen dessen Daten dort, die Adresse wird in der Referenzvariable abgelegt.
Verweisen mehrere Klassen so aufeinander, spricht man von **Assoziation** — dem
„wer kennt wen?"-Prinzip (z. B. ein Schachbrett-Objekt, das seine Figuren-Objekte
verwaltet).

```java
Farmer farmer1 = new Farmer(10, 10);
Farmer farmer2 = new Farmer(20, 20);

farmer2 = farmer1;      // KEINE Kopie! Nur die ADRESSE in farmer2 wird überschrieben
                        // -> beide Variablen zeigen jetzt auf dasselbe Objekt
```

```
Farmer1: carpool.Farmer@27716f4  | Sheeps: 10, Cows: 10
Farmer2: carpool.Farmer@452b3a41 | Sheeps: 20, Cows: 20
Farmer2: carpool.Farmer@27716f4  | Sheeps: 10, Cows: 10   <- selbe Adresse wie Farmer1
```

Das Objekt `@452b3a41` wird von keinem Zeiger mehr verwaltet, ist damit nicht mehr
erreichbar und wird irgendwann vom **Garbage Collector** aus dem Speicher entfernt.
Sollen die Werte übernommen werden statt die Referenz, muss eine Kopie erzeugt oder
jedes Attribut einzeln gesetzt werden.

```java
farmer1 == farmer2        // REFERENZgleichheit: nur true, wenn dasselbe Objekt
farmer1.equals(farmer2)   // INHALTSgleichheit: für eigene Klassen zu überschreiben
a == b                    // bei primitiven Typen der normale Wertvergleich
```

> Klassiker: `"abc" == eingabe` funktioniert bei Strings scheinbar manchmal (String-Pool),
> ist aber falsch — für Strings immer `equals()` verwenden.

---

## 6. Vererbung & Polymorphie

### 6.1 Vererbung & `super`

Eine Unterklasse erbt alle Attribute und Methoden der Oberklasse und kann sie
verfeinern oder ergänzen. So werden Gemeinsamkeiten an einer Stelle gebündelt und
Redundanz vermieden. In Java erbt jede Klasse automatisch von `Object`.

```java
public class Fahrzeug {
    protected int raeder;                    // protected -> in Unterklassen sichtbar
    public Fahrzeug(int raeder) { this.raeder = raeder; }
}

public class Auto extends Fahrzeug {         // extends = erbt von
    private String marke;

    public Auto(String marke) {
        super(4);                            // PFLICHT und ZUERST: Konstruktor der Oberklasse
        this.marke = marke;
    }
}
```

### 6.2 Abstrakte Klassen & Polymorphie

**Polymorphie**: verschiedene Klassen implementieren dieselbe Methode unterschiedlich
(`@Override`). Welche Variante läuft, entscheidet zur Laufzeit der konkrete Typ des
Objekts — nicht der Typ der Variablen. Im Beispiel erben `InnerNode`, `SingleNode`
und `Leaf` von der abstrakten Klasse `Node` und traversieren damit einen Baum.

```java
// abstract = von dieser Klasse können KEINE Objekte erzeugt werden;
// sie dient nur als gemeinsame Basis.
public abstract class Node {
    private Node left;
    private Node right;
    protected int value;

    public Node(Node left, Node right, int value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public int getValue() { return value; }
    public Node getLeft()  { return left; }
    public Node getRight() { return right; }

    // abstrakte Methode: nur die Signatur, kein Rumpf
    // -> JEDE Unterklasse MUSS sie implementieren
    public abstract int doSomething();
}

public class InnerNode extends Node {        // Knoten mit zwei Kindern
    public InnerNode(Node left, Node right, int value) {
        super(left, right, value);
    }
    @Override
    public int doSomething() {               // rekursiv: beide Teilbäume + eigener Wert
        return this.getLeft().doSomething() + this.getRight().doSomething() + this.value;
    }
}

public class SingleNode extends Node {       // Knoten mit nur einem Kind
    public SingleNode(Node left, int value) {
        super(left, null, value);            // rechtes Kind bewusst null
    }
    @Override
    public int doSomething() {
        return this.getLeft().doSomething() + this.value;
    }
}

public class Leaf extends Node {             // Blatt: Rekursionsanker
    public Leaf(int value) {
        super(null, null, value);            // keine Kinder
    }
    @Override
    public int doSomething() {
        return this.value;                   // gibt den Wert direkt zurück
    }
}
```

```java
package Graph;

public class Graph {
    public Node root, n1, n2, n3, n4, n5;

    public Graph() {
        this.n4 = new Leaf(4);
        this.n5 = new Leaf(1);
        this.n3 = new Leaf(6);
        this.n1 = new InnerNode(n3, n4, 8);     // 6 + 4 + 8 = 18
        this.n2 = new SingleNode(n5, 7);        // 1 + 7     =  8
        this.root = new InnerNode(n1, n2, 3);   // 18 + 8 + 3 = 29
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        System.out.println(g.root.doSomething());   // 29
        System.out.println(g.n1.doSomething());     // 18
        System.out.println(g.n4.doSomething());     //  4
    }
}
// Alle drei Aufrufe sehen gleich aus — es läuft aber jeweils die Implementierung
// der konkreten Klasse (InnerNode bzw. Leaf). Das ist Polymorphie.
```

### 6.3 Interfaces

Ein Interface legt fest, **welche** Funktionen eine Klasse bieten muss, ohne
festzulegen, **wie** sie umgesetzt werden. Eine Klasse kann mehrere Interfaces
implementieren (Mehrfachvererbung von Fähigkeiten) — von Klassen dagegen nur von
einer einzigen erben.

```java
public interface Fahren     {}
public interface Schwimmen  {}
public interface Fliegen    {}

public class Auto           implements Fahren {}
public class Wasserflugzeug implements Schwimmen, Fliegen {}   // mehrere möglich
public class Boot           implements Schwimmen {}

// Mit Methoden: alle Methoden sind implizit public abstract
public interface Zeichenbar {
    void zeichne();                    // MUSS von jeder implementierenden Klasse erfüllt werden
    int MAX = 100;                      // Felder sind implizit public static final (Konstanten)
}
```

| | abstrakte Klasse | Interface |
|---|---|---|
| Vererbung | nur **eine** möglich | **mehrere** möglich |
| Attribute | normale Attribute erlaubt | nur Konstanten |
| Zweck | „ist ein" — gemeinsame Basis mit Code | „kann etwas" — reine Fähigkeit |

---

## 7. Typecasting

Beim Typecasting wird die Interpretation eines Datensatzes verändert — ein Objekt
wird in einen anderen Typ „umgewandelt". Innerhalb einer Vererbungshierarchie gibt
es zwei Richtungen.

### 7.1 Upcasting

```java
// Unterklasse -> Oberklasse: passiert automatisch und ist IMMER sicher,
// da jede Instanz der Unterklasse auch eine Instanz der Oberklasse ist.
Node n = new Leaf(5);
n.doSomething();     // erlaubt: in Node deklariert
// n.printLeafInfo(); // FEHLER: über eine Node-Referenz nicht sichtbar
```

### 7.2 Downcasting & `instanceof`

```java
// Oberklasse -> Unterklasse: muss EXPLIZIT erfolgen und kann schiefgehen.
// Passt der tatsächliche Typ nicht, gibt es eine ClassCastException.
public static void main(String[] args) {
    Graph g = new Graph();
    Node someNode = g.n4;                    // Upcasting: Leaf -> Node (automatisch)

    System.out.println(someNode.doSomething());   // allgemeiner Zugriff möglich

    // instanceof prüft zur Laufzeit, ob der Cast gefahrlos möglich ist
    if (someNode instanceof Leaf) {
        Leaf leafNode = (Leaf) someNode;     // explizites Downcasting
        leafNode.printLeafInfo();            // jetzt ist die Leaf-Methode erreichbar
    }
}

// Kurzform ab Java 16 (Pattern Matching): prüfen und casten in einem Schritt
if (someNode instanceof Leaf leafNode) {
    leafNode.printLeafInfo();
}
```

Downcasting wird immer dann nötig, wenn über eine Oberklassenreferenz auf Methoden
zugegriffen werden soll, die nur in einer speziellen Unterklasse existieren.

---

## 8. Generics

Generics erlauben, Klassen, Interfaces und Methoden mit **Typparametern** zu
definieren. Der Typ wird erst bei der Verwendung festgelegt. Das bringt
**Typsicherheit** (Prüfung bereits zur Kompilierzeit) und macht Casts überflüssig.
Klassisches Beispiel ist `ArrayList<T>`: der Platzhalter `<T>` sorgt dafür, dass nur
Objekte dieses Typs in die Liste dürfen.

```java
// Generische Klasse: T ist ein Platzhalter für einen beliebigen Typ
public class Box<T> {
    private T content;
    public void setContent(T content) { this.content = content; }
    public T getContent() { return content; }
}

// Generische Methode: <T> steht VOR dem Rückgabetyp
public class GenericMethodDemo {
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
}

public class GenericTest {
    public static void main(String[] args) {
        Box<String> stringBox = new Box<>();      // T ist hier String
        stringBox.setContent("Hallo Generics");
        System.out.println(stringBox.getContent());   // kein Cast nötig

        Box<Integer> intBox = new Box<>();        // dieselbe Klasse, anderer Typ
        intBox.setContent(42);
        System.out.println(intBox.getContent());

        Integer[] zahlen = {1, 2, 3, 4};
        String[]  worte  = {"A", "B", "C"};
        GenericMethodDemo.printArray(zahlen);     // 1 2 3 4
        GenericMethodDemo.printArray(worte);      // A B C
    }
}
```

```java
// Bounds: den erlaubten Typbereich einschränken
public class Rechner<T extends Number> { }   // nur Number und deren Unterklassen
// Übliche Buchstaben: T = Type, E = Element, K = Key, V = Value
```

---

## 9. Patterns

Ein Pattern ist eine Blaupause, die ein wiederkehrendes Entwurfsproblem löst — nicht
als starrer Standard, sondern als Konstrukt, das sich beliebig oft anwenden lässt,
ohne zweimal identisch umgesetzt zu werden.

### 9.1 Factory Method

**Erzeugungsmuster.** Statt `new` direkt im Code zu streuen, kapselt eine
Fabrikmethode die Objekterzeugung. Erzeugt eine Klasse ihre Instanzen selbst mit
`new`, ist sie fest an eine konkrete Implementierung gebunden; die Factory trennt
Erzeugung und Nutzung sauber voneinander.

```java
public interface Node { int doSomething(); }

public class NodeFactory {
    // Die Fabrikmethode entscheidet, WELCHE konkrete Klasse entsteht.
    // Der Aufrufer kennt nur noch den Typ 'Node'.
    public static Node create(String typ, int value) {
        return switch (typ) {
            case "leaf"      -> new Leaf(value);
            case "composite" -> new CompositeNode(value);
            default -> throw new IllegalArgumentException("unbekannt: " + typ);
        };
    }
}

Node n = NodeFactory.create("leaf", 5);   // kein 'new Leaf' im aufrufenden Code
```

### 9.2 Composite Pattern

**Strukturmuster.** Ermöglicht die einheitliche Behandlung von Einzelobjekten und
Objektgruppen — ideal für hierarchische Strukturen wie Bäume. Blätter und Knoten
werden über ein gemeinsames Interface angesprochen, die Traversierung läuft
vollständig rekursiv, ohne zwischen beiden zu unterscheiden.

```java
public abstract class Node {
    protected int value;
    public Node(int value) { this.value = value; }
    public int getValue() { return value; }

    // zentrale Operation, die beide Typen beherrschen müssen
    public abstract int doSomething();
}

public class Leaf extends Node {              // Blatt: führt die Operation DIREKT aus
    public Leaf(int value) { super(value); }
    @Override
    public int doSomething() { return this.value; }
}

public class CompositeNode extends Node {     // Knoten: reicht sie an die Kinder WEITER
    private List<Node> children = new ArrayList<>();

    public CompositeNode(int value) { super(value); }
    public void add(Node child) { children.add(child); }   // Kind einhängen

    @Override
    public int doSomething() {
        int sum = this.value;
        for (Node child : children) {          // Typ egal: Leaf oder CompositeNode
            sum += child.doSomething();        // rekursiver Abstieg
        }
        return sum;
    }
}
```

```java
public class Graph {
    public static void main(String[] args) {
        Leaf l1 = new Leaf(4);
        Leaf l2 = new Leaf(1);
        Leaf l3 = new Leaf(6);

        CompositeNode c1 = new CompositeNode(8);
        c1.add(l3); c1.add(l1);                 // 8 + 6 + 4 = 18

        CompositeNode c2 = new CompositeNode(7);
        c2.add(l2);                             // 7 + 1     =  8

        CompositeNode root = new CompositeNode(3);
        root.add(c1); root.add(c2);             // 3 + 18 + 8 = 29

        System.out.println(root.doSomething());   // 29
        System.out.println(c1.doSomething());     // 18
        System.out.println(l1.doSomething());     //  4
    }
}
```

### 9.3 Visitor Pattern

**Verhaltensmuster.** Erlaubt neue Operationen auf einer Objektstruktur, ohne deren
Klassen zu ändern. Statt die Logik in die Knoten einzubauen, wird ein Besucherobjekt
durch die Struktur geschickt. Die Struktur stellt nur eine `accept`-Methode bereit
und bestimmt die Traversierung; die eigentliche Operation steckt im Visitor. Nützlich,
wenn viele verschiedene Auswertungen auf derselben Datenstruktur nötig sind.

```java
public abstract class Node {
    protected int value;
    public Node(int value) { this.value = value; }

    // universelle Aufnahme: nimmt JEDEN Visitor entgegen
    public abstract void accept(Visitor v);
}

public class Leaf extends Node {
    public Leaf(int value) { super(value); }
    @Override
    public void accept(Visitor v) {
        v.visit(this);      // 'this' ist hier vom Typ Leaf -> passende visit-Variante
    }
}

public class CompositeNode extends Node {
    private List<Node> children = new ArrayList<>();
    public CompositeNode(int value) { super(value); }
    public void add(Node child) { children.add(child); }

    @Override
    public void accept(Visitor v) {
        v.visit(this);                      // erst sich selbst besuchen lassen ...
        for (Node child : children) {
            child.accept(v);                // ... dann denselben Visitor weiterreichen
        }
    }
}

// Das Interface legt fest, welche Knotentypen ein Visitor behandeln können MUSS
public interface Visitor {
    void visit(Leaf leaf);
    void visit(CompositeNode node);         // überladen -> Auswahl über den Argumenttyp
}

// Konkreter Visitor: trägt seinen eigenen Zustand (die Summe) mit sich
public class SumVisitor implements Visitor {
    private int sum = 0;
    @Override public void visit(Leaf leaf)          { sum += leaf.value; }
    @Override public void visit(CompositeNode node) { sum += node.value; }
    public int getSum() { return sum; }
}
```

```java
public class Graph {
    public static void main(String[] args) {
        Leaf l1 = new Leaf(4);
        Leaf l2 = new Leaf(1);
        CompositeNode c1 = new CompositeNode(3);
        c1.add(l1); c1.add(l2);

        SumVisitor visitor = new SumVisitor();   // ein Visitor-Objekt = ein Durchlauf
        c1.accept(visitor);
        System.out.println("Summe: " + visitor.getSum());   // 8
    }
}
```

### 9.4 Iterator Pattern

**Verhaltensmuster.** Erlaubt das Durchlaufen einer Sammlung, ohne deren interne
Struktur offenzulegen. Die Traversierung wird von der Datenstruktur entkoppelt und
standardisiert — bei Bäumen lässt sich so die Reihenfolge (Pre-, In-, Post-Order)
flexibel austauschen. `hasNext()` meldet, ob es noch ein Folgeelement gibt,
`next()` liefert es und enthält die eigentliche Iterationslogik.

```java
// Variante 1: eigenes Interface
public interface MyIterator<T> {      // T = Typparameter (im PDF stand hier 'Node')
    boolean hasNext();
    T next();
}

public class NodeIterator implements MyIterator<Node> {
    private Stack<Node> stack = new Stack<>();    // verwaltet die noch offenen Knoten

    public NodeIterator(Node root) {
        if (root != null) stack.push(root);       // Startpunkt auflegen
    }

    @Override
    public boolean hasNext() { return !stack.isEmpty(); }

    @Override
    public Node next() {
        Node current = stack.pop();               // obersten Knoten entnehmen
        if (current instanceof CompositeNode) {   // nur Knoten haben Kinder
            CompositeNode c = (CompositeNode) current;
            List<Node> children = new ArrayList<>(c.getChildren());
            Collections.reverse(children);        // Stack dreht die Reihenfolge um
            for (Node child : children) {         // -> vorher umkehren = Pre-Order
                stack.push(child);
            }
        }
        return current;
    }
}
```

```java
NodeIterator it = new NodeIterator(root);
while (it.hasNext()) {                       // klassische Iterator-Schleife
    Node n = it.next();
    System.out.println("Node-Wert: " + n.value);
}
// mit Collections.reverse:  3, 4, 1   (Einfügereihenfolge)
// ohne Collections.reverse: 3, 1, 4   (Stack kehrt sie um)
```

```java
// Variante 2: die Java-Interfaces nutzen
public class NodeIterator implements Iterator<Node> { /* wie oben */ }

// Iterable macht die Struktur for-each-fähig
public abstract class Node implements Iterable<Node> {
    protected int value;
    public Node(int value) { this.value = value; }

    @Override
    public Iterator<Node> iterator() {       // von Iterable gefordert
        return new NodeIterator(this);
    }
}

for (Node n : root) {                        // funktioniert dank Iterable direkt
    System.out.println("Node-Wert: " + n.value);
}
```

### 9.5 State Pattern

**Verhaltensmuster.** Ändert das Verhalten eines Objekts abhängig von seinem
internen Zustand. Statt großer `if`- oder `switch`-Konstrukte wird jeder Zustand als
eigene Klasse modelliert; das Kontextobjekt delegiert die Aufrufe an das aktuell
gesetzte Zustandsobjekt. Neue Zustände kommen als neue Klasse dazu, ohne bestehende
Logik anzufassen — klassisch für Automaten mit Zustandsübergangsdiagramm.

```java
// Das Interface legt fest, was jeder Zustand können muss
public interface State {
    void handle();
}

public class IdleState implements State {
    @Override public void handle() { System.out.println("Automat wartet auf Produktauswahl."); }
}
public class InputState implements State {
    @Override public void handle() { System.out.println("Produkt erkannt. Bitte Münze einwerfen."); }
}
public class DispensingState implements State {
    @Override public void handle() { System.out.println("Produkt wird ausgegeben..."); }
}

// Kontextklasse: kennt nur 'State', nicht die konkreten Zustände
public class VendingMachine {
    private State state;

    public VendingMachine() { this.state = new IdleState(); }   // Startzustand

    public void setState(State state) { this.state = state; }   // Zustandswechsel
    public void request() { state.handle(); }                   // delegiert nach außen unsichtbar
}
```

```java
VendingMachine machine = new VendingMachine();
machine.request();                            // Automat wartet auf Produktauswahl.
machine.setState(new InputState());
machine.request();                            // Produkt erkannt. Bitte Münze einwerfen.
machine.setState(new DispensingState());
machine.request();                            // Produkt wird ausgegeben...
```

```java
// Variante mit automatischem Wechsel: die Zustände liegen als innere anonyme
// Klassen im Kontext und setzen den Nachfolger selbst.
public class VendingMachine {
    private abstract class State {           // innere Klasse: sieht das Feld 'state'
        abstract void handle();
    }

    private final State idleState = new State() {
        @Override void handle() { System.out.println("Idle");    state = inputState; }
    };
    private final State inputState = new State() {
        @Override void handle() { System.out.println("Coin");    state = dispensingState; }
    };
    private final State dispensingState = new State() {
        @Override void handle() { System.out.println("Product"); state = idleState; }
    };

    State state = idleState;                  // verwaltet den aktuellen Zustand
    public void request() { state.handle(); } // ein Aufruf = ein Zustandsübergang

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.request();   // Idle
        vm.request();   // Coin
        vm.request();   // Product
    }
}
```

---

## 10. Schnellreferenz

### 10.1 Häufige Imports

| Import | Zweck |
|---|---|
| `java.util.*` | Sammelimport für alle Collections |
| `java.util.ArrayList` / `List` | dynamische Liste |
| `java.util.HashMap` / `Map` | Schlüssel-Wert-Paare |
| `java.util.HashSet` / `Set` | Menge ohne Duplikate |
| `java.util.ArrayDeque` / `Queue` / `Deque` | Queue und Stack |
| `java.util.Collections` | Hilfsmethoden: `sort()`, `reverse()`, `shuffle()` |
| `java.util.Arrays` | Hilfsmethoden für Arrays: `sort()`, `toString()`, `asList()` |
| `java.util.Iterator` / `Iterable` | Iterator Pattern |
| `java.util.Scanner` | Eingaben von der Konsole lesen |

### 10.2 Schlüsselwörter auf einen Blick

| Wort | Bedeutung |
|---|---|
| `static` | gehört zur Klasse, nicht zum Objekt |
| `final` | nicht mehr änderbar (Variable) bzw. nicht überschreibbar (Methode) |
| `abstract` | ohne Rumpf; Klasse nicht instanziierbar, Methode muss überschrieben werden |
| `extends` / `implements` | erbt von einer Klasse / erfüllt ein Interface |
| `super` / `this` | Oberklasse / dieses Objekt |
| `@Override` | markiert das bewusste Überschreiben (Compiler prüft die Signatur) |
| `void` | Methode gibt nichts zurück |
| `instanceof` | Typprüfung zur Laufzeit |

### 10.3 Java vs. Python

| Thema | Java | Python |
|---|---|---|
| Typisierung | statisch, Typ steht im Code | dynamisch, Typ folgt aus dem Wert |
| Blöcke | geschweifte Klammern `{ }` | Einrückung |
| Liste | `List<String>` / `ArrayList` | `list` |
| Map / Dict | `HashMap<K,V>` | `dict` |
| Länge | `arr.length`, `liste.size()`, `s.length()` | überall `len()` |
| Ausgabe | `System.out.println()` | `print()` |
| Konstruktor | `public Klasse(...)` | `__init__(self, ...)` |
| Selbstbezug | `this` (implizit verfügbar) | `self` (explizit als 1. Parameter) |
| Gleichheit | `equals()` Inhalt, `==` Referenz | `==` Inhalt, `is` Referenz |
| Einstiegspunkt | `public static void main(String[] args)` | `if __name__ == "__main__":` |
| Ausführung | erst `javac`, dann `java` | direkt `python datei.py` |
