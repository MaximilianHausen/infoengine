# org.totogames.infoengine.rendering

### Beschreibung
Rendering! Juhuu!

### Window
Stellt ein Fenster, erstellt mit glfw, dar und bietet Verwaltungsmethoden dafür. Das Objekt darf nicht zerstört werden.

### opengl
Abstraktionen von OpenGL-Objekten, damit sie in Java nicht so umständlich sind.
Jedes Java-Objekt stellt ein OpenGL-Objekt dar und tut sein bestes, damit synchron zu bleiben.
Das funktioniert aber nur, wenn die OpenGL-Objekte ausschließlich über die Abstraktionen verwaltet werden.

### opengl.enums
Enums für verschiedene OpenGL Argumente, verwendet anstatt der unpraktischen statischen Konstanten wie GL_VERTEX_SHADER

### Highlights
- protected abstract in Shader