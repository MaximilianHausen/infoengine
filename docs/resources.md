# Resources

## Format

Resourcen werden aus Resourcepacks geladen (ähnlich zu Minecraft). Die grundlegende Struktur ist festgelegt, jede Kategorie kann aber beliebig weiter organisiert werden.

```
<root>                  Resource Key
├──scenes
│  │  scene1.json       scenes/scene1.png
│  │  scene2.json       scenes/scene2.png
│  │  ...
│  │  
│  └──dir1
│     │  scene3.json    scenes/dir1/scene3.png
│     │  ...
│
├──textures
│  │  tex1.png          textures/tex1.png
│  │  tex2.jpg          textures/tex2.jpg
│  │  ...
│
└──shaders
   │  sh1.vsh           shaders/sh1.vsh
   │  sh2.fsh           shaders/sh2.fsh
   │  ...
```

## Verwendung

Verwaltet werden Resourcepacks vom statischen ResourceManager. Von dort können sie dann mit ihrem Resource Key wieder abgerufen werden. Ein Resource Key ist der relative Pfad von der Pack-Wurzel. Beim Laden muss der Pfad zum root-Ordner des Texturepacks angegeben werden.

```java
public void foo() {
    ResourceManager.loadResourcePack(new File("./resourcepacks/pack1/"));
    ImageResource image = ResourceManager.getImage("textures/tex1.png");
}
```

Es können auch mehrere Resourcepacks geladen werden, wobei das neuere dann die Ressourcen aus dem älteren überschreibt.

```java
public void foo() {
    ResourceManager.loadResourcePack(new File("./resourcepacks/pack1/")); // Inhalt: [ textures/tex.png, textures/otherTex.png ]
    ResourceManager.loadResourcePack(new File("./resourcepacks/pack2/")); // Inhalt: [ textures/tex.png ]

    ImageResource image = ResourceManager.getImage("textures/tex.png"); // Kommt aus pack2
    ImageResource image = ResourceManager.getImage("textures/otherTex.png"); // Kommt aus pack1
}
```

## Resource-Typen

### Scenes

Noch nicht in das Resourcensystem implementiert. Für eine temporäre Lösung siehe [Sceneloading](resources-scene.md).

### Bilder

Bilder werden zu einer ImageResource geladen und dort zu einem VkImage, VkImageView und VkSampler weiterverarbeitet. ImageResources verwenden lazy-loading und laden das tatsächliche Bild erst dann, wenn es das erste Mal aufgerufen wird. Während das echte Bild im Hintergrund auf einem Worker-Thread lädt, wird ein durchsichtiges 1*1 Pixel Bild zurückgegeben. Man kann das Bild auch manuell mit ImageResource::load laden, wenn es beim ersten Aufruf sofort gebraucht wird.

!> Das ursprüngliche Bild wird NICHT aufgehoben, um Arbeitsspeicher zu sparen
