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

Am Anfang von jedem Programm muss die Engine initialisiert werden. Dabei wird schon ein unsichtbares Fenster erstellt, das aber erst später beim Starten der Engine sichtbar wird. Der Hauptthread wird von Engine::start blockiert, bis das Hauptfenster geschlossen wird. Es kann aber auch von einem anderen Thread aus manuell beendet werden.

```java
public static void main(String[] args) {
    Engine.initialize("TutorialApp", new SemVer(1, 0, 0), 800, 600);
    Engine.start();
}
```

## Scenes

Eine Scene ist ein Level. Man kann eine Scene auf zwei verschiedenen Wegen erstellen: manuell mit Code oder automatisch aus einer json-Datei. Hier werden wir die Scene manuell erstellen, weil es einfacher ist und mehr über die Funktionsweise zeigt. Mehr Infos zum Laden aus einer Datei gibt es im Artikel über [Sceneloading](sceneloading.md).

```java
public static void main(String[] args) {
    Engine.initialize("TutorialApp", new SemVer(1, 0, 0), 800, 600);

    Scene scene = new Scene();
    int entity = scene.createEntity();

    scene.start();
    Engine.start();
    scene.stop();
}
```

Eine neue Scene wird mit dem leeren Konstruktor erstellt und agiert dann als Container für entities (Global) Components und Systems
