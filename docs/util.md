# org.totogames.infoframework.util

### Beschreibung
Hier kommt alles rein, was sonst nirgends hinpasst

### [Action](../src/main/java/org/totogames/infoframework/util/Action0.java)/[Func](../src/main/java/org/totogames/infoframework/util/Func0.java)
Functional Interfaces, um standartisierte Lamda-Typen zu haben, weil die von Java extrem unübersichtlich sind (Runnable, Consumer, BiConsumer, Function, BiFunction).
Die Namen geben die Anzahl der Parameter an, weil es in Java nicht mehrere gleichnamige Klassen geben kann, die sich nur durch die Anzahl der Generics unterscheiden.
Fast direkt von C# übertragen ([Action](https://docs.microsoft.com/en-us/dotnet/api/system.action), [Func](https://docs.microsoft.com/en-us/dotnet/api/system.func-1))

### [Event](../src/main/java/org/totogames/infoframework/util/Event.java)
Mehrere Action0 können zum Event subscriben und unsubscriben.
Mit Event::invoke() werden dann alle subscriber aufgerufen.
Auch das ahmt Events aus C# nach, ist aber trotzdem wegen der Sprachunterschiede recht verschieden.\
Wichtig: Lambdas können ohne Weiteres nicht unsubscriben, weil, selbst wenn die lambda gleich aussieht, jedes Mal eine neue anonyme Klasse erzeugt wird:

```java
// Das ist alles das Gleiche
event.subscribe(this::doSomething);
event.subscribe(() -> doSomething());
event.subscribe(new Action0 {
    @Override
    public void run() {
        doSomething();
    }
});
```

### [Logger](../src/main/java/org/totogames/infoframework/util/Logger.java)

### [IO](../src/main/java/org/totogames/infoframework/util/IO.java)

### Highlights
- Functional Interfaces und Lambdas
- LogSeverity als Enum mit custom-Werten und deren Verwendung