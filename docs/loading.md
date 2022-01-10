# org.totogames.infoframework.loading

### Beschreibung
Alles was zum Laden von Szenen und Entities gebraucht wird

### [EntityBuilder](../src/main/java/org/totogames/infoframework/loading/EntityBuilder.java)
Ein einheitlicher Weg, Entities zu erstellen und zu initialisieren. Nix besonderes, einfach ein normaler Builder

### [SceneModel](../src/main/java/org/totogames/infoframework/loading/SceneModel.java)/[EntityModel](../src/main/java/org/totogames/infoframework/loading/EntityModel.java)
Json-Modelle für Szenen und Entities

### [SceneLoader](../src/main/java/org/totogames/infoframework/loading/SceneLoader.java)
Lädt Szenen aus Json-Text (Format unten)
```json
{
  "editorVersion": "1.0.0",
  "formatVersion": "1.0.0",

  "name": "ExampleSceneName",
  "type": "org.totogames.infoframework.ecs.Scene",

  "entities": [
    {
      "name": "ExampleEntityName",
      "type": "org.totogames.exampleproject.ExampleEntity",
      "x": 0,
      "y": 0,
      "z": 0,
      "data": {
        "fieldName": "fieldValue"
      }
    },
    {
      "name": "ExampleParentName",
      "type": "org.totogames.exampleproject.ExampleEntity",
      "x": 0,
      "y": 0,
      "z": 0,
      "children": [
        {
          "name": "ExampleChildName",
          "type": "org.totogames.exampleproject.ExampleEntity",
          "x": 0,
          "y": 0,
          "z": 0
        }
      ]
    }
  ]
}
```