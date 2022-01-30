# org.totogames.infoengine.util

### Beschreibung
Hier kommt alles rein, was sonst nirgends hinpasst

### [Action](../src/main/java/org/totogames/infoengine/util/Action.java)/[Func](../src/main/java/org/totogames/infoengine/util/Func.java)
Functional Interfaces, um standartisierte Lamda-Typen zu haben, weil die von Java extrem unübersichtlich sind (Runnable, Consumer, BiConsumer, Function, BiFunction).
Die Namen geben die Anzahl der Parameter an, weil es in Java nicht mehrere gleichnamige Klassen geben kann, die sich nur durch die Anzahl der Generics unterscheiden.
Fast direkt von C# übertragen ([Action](https://docs.microsoft.com/en-us/dotnet/api/system.action), [Func](https://docs.microsoft.com/en-us/dotnet/api/system.func-1))

### [Event](../src/main/java/org/totogames/infoengine/util/Event.java)
Mehrere Actions können zum Event subscriben und unsubscriben.
Mit Event::run() werden dann alle subscriber aufgerufen.
Es gibt eine Variante ohne und eine mit Parametern.
Auch das ahmt Events aus C# nach, ist aber trotzdem wegen der Sprachunterschiede recht verschieden.\
Wichtig: Lambdas können ohne Weiteres nicht unsubscriben, weil, selbst wenn die lambda gleich aussieht, jedes Mal eine neue anonyme Klasse erzeugt wird:

```java
// Das ist alles (fast) das Gleiche
event.subscribe(this::doSomething);
event.subscribe(() -> doSomething());
event.subscribe(new Action0 {
    @Override
    public void run() {
        doSomething();
    }
});
```

### [Collector](../src/main/java/org/totogames/infoengine/util/Collector.java)
Gleich wie ein Event, aber mit return type

### [Logger](../src/main/java/org/totogames/infoengine/util/logging/Logger.java)
Ein Logger eben, nix besonderes.
Man kann für jede Nachricht eine Wichtigkeitsstufe festlegen.
Der Logger kann damit unwichtige Logs je nach Einstellung aussortieren.
Wohin die Logs gehen lässt sich mit einer Action1<String> festlegen.

### [IO](../src/main/java/org/totogames/infoengine/util/IO.java)
Kann verschiedene Sachen aus Dateien lesen.

### Highlights
- Functional Interfaces, Lambdas und ihre Verwendung in Events