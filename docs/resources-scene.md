# Sceneloading

Scenes können aus Json-Dateien geladen und darin gespeichert werden. Jeder Component serialisiert seine Daten beim Speichern und deserialisiert sie beim Laden.

## Format

```json
{
    "formatVersion": "1.0",
    "name": "ExampleSceneName",

    "entityCount": 3,

    "systems": [
        "net.totodev.example.ExampleSystem",
        "net.totodev.example.AnotherSystem"
    ],

    "components": [
        {
            "type": "net.totodev.example.ExampleComponent",
            "data": [
                {
                    "entity": 0,
                    "value": "datadatadatadatadata"
                },
                {
                    "entity": 2,
                    "value": "datadatadatadatadata"
                }
            ]
        }
    ],

    "globalComponents": [
        {
            "type": "net.totodev.example.ExampleGlobalComponent",
            "data": "datadatadatadatadata"
        }
    ]
}
```

- `formatVersion:` Die Version des Formats, in dem die Scene gespeichert wurde.
- `name:` Der Name der Szene. Momentan nicht verwendet.
- `entityCount:` Die Anzahl der Entities in der Scene. Beispiel: Ein entityCount von 3 heißt, dass die Entities 0, 1, und 2 erstellt werden. "Lücken" in der Nummerierung werden nicht unterstützt.
- `systems:` Eine Liste der Klassennamen aller Systems in der Scene. Um geladen zu werden, muss die genannte Klasse einen parameterlosen Constructor haben.
- `components:` Eine Liste aller Components in der Scene. Ein Component wird mit Klassenname und Daten gespeichert. Die Daten sind der State des Components und werden einzeln für jede Entity gespeichert, auf der der Component vorhanden ist.
- `globalComponents:` Wie normale Components, aber ohne Daten pro Entity.

## Code

Um Scenes zu laden, gibt es die SceneLoader Klasse.

```java
public static void foo() {
    Scene scene = SceneLoader.loadSceneFromFile(Path.of("./testScene.json"))
}
```
