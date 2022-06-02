# Quickstart
## Gradle dependencies
Alle Versionen von Infoengine sind auf dem Totodev Maven-Repository verfügbar.
```groovy
repositories {
    maven {
        url "https://maven.totodev.de/releases/"
    }
}
```
Es müssen sowohl die Java-Library als auch die Natives für die benötigten Plattformen als Dependencies angegeben werden. Es können auch mehrere Plattformen angegeben werden.
```groovy
dependencies {
    implementation "net.totodev:infoengine:$VERSION"
    runtimeOnly("net.totodev:infoengine:$VERSION") {
        capabilities {
            requireCapability("net.totodev:infoengine-$NATIVES")
        }
    }
}
```
!> Verfügbare Plattformen: x64windows, x86windows, arm64windows, x64linux, arm64linux, arm32linux, x64macos, arm64macos

## Startup
Am Anfang von jedem Programm muss die Engine initialisiert werden. Dabei wird schon ein unsichtbares Fenster erstellt, das aber erst später beim Starten der Engine sichtbar wird. Weil der Hauptthread von Engine::start blockiert wird, muss das Programm von einem anderen Thread wieder beendet werden. Aber es wird auch automatisch beendet, wenn das Fenster geschlossen wird.
```java
public static void main(String[] args) {
    Engine.initialize("TutorialApp", new SemVer(1, 0, 0), 800, 600);
    
    // Exit Engine::start after 5 seconds
    new Thread(() -> {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
        Engine.terminate();
    }).start();
    
    Engine.start();
}
```

## Szenen
Eine Scene kann man auf zwei verschiedene Wege erstellen: Manuell mit Code oder Automatisch aus einer Datei. Hier werden wir die Scene mit Code erstellen, weil es einfacher ist und mehr über die Funktionsweise zeigt. Mehr Infos zum Laden aus einer Datei gibt es hier: [Sceneloading](sceneloading.md)