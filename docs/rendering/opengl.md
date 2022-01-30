# org.totogames.infoengine.rendering.opengl

### Beschreibung
Wrapper für OpenGL-Objekte, damit die in Java nicht so umständlich sind.
Dabei verwenden sie so wenig state duplication wie möglich, doch manchmal geht es eben nicht anders.
Deshalb sollten die OpenGL-Objekte ausschließlich über die Abstraktionen verwaltet werden, damit sie sich nicht desynchronisieren.\
Jeder Wrapper implementiert [IDisposable](../../src/main/java/org/totogames/infoengine/IDisposable.java) und wirft bei allen Methodenaufrufen nach *.dispose() eine *DisposedException.

### Enums
Enums für verschiedene OpenGL Integers, verwendet anstatt der unpraktischen statischen Konstanten (ShaderType.VERTEX_SHADER statt GL46C.GL_VERTEX_SHADER)
- opengl.enums.custom: Enums, die kein direktes OpenGL-Äquivalent haben
- opengl.enums.texparams: Sind sehr viele verschiedene enums, also in eigenem package

### [@RequiresBind](../../src/main/java/org/totogames/infoengine/rendering/opengl/wrappers/RequiresBind.java)
Das Objekt muss für diese Methode an ein aktives OpenGL-Target gebunden sein (mit *.bind()).
Bei Texturen muss außerdem die Texture-Unit, an die die Textur gebunden ist, aktiv sein.
Wird aus Performancegründen nicht überprüft. (Und weil echt witzige Bugs entstehen, wenn es nicht beachtet wird)

### [Buffer](../../src/main/java/org/totogames/infoengine/rendering/opengl/wrappers/Buffer.java)
Der Status der verschiedenen Targets wird in bindStatus gespeichert, damit beim Lesen/Schreiben das passende Target automatisch erkannt werden kann.

### [Texture](../../src/main/java/org/totogames/infoengine/rendering/opengl/wrappers/Texture.java)
Die verschiedenen [Texturtypen](../../src/main/java/org/totogames/infoengine/rendering/opengl/enums/TextureType.java) haben zu große Unterschiede, um sie sinnvoll in einer Klasse zu kombinieren.
Deshalb hat die abstrakte Klasse alle allgemeinen Aktionen (z.B. bind, dispose), die Implementierungen die speziellen Sachen für den Typ (z.B. Passende Methoden zum Schreiben).
Der Typ der Textur wird, um Reflection zu vermeiden, aus dem Konstruktor gelesen, den alle Implementationen aufrufen müssen. (Verlässt sich darauf, dass die Implementierung den richtigen Typ angibt)