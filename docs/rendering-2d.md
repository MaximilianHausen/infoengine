# 2d Rendering

## Features

- Einfaches Sprite-Rendering mit translation, rotation und scale
- Kamera-Transformation mit push constants
- Instanced rendering
  - Nur ein draw call!
  - Bindless textures (max. 128)
  - Quadgröße automatisch aus Bildgröße berechnen und ohne Vertex Buffer übertragen

## Verwendung

Das Renderer2d System liest jede Frame die Daten von den Sprite2d und Camera2d Components und rendert damit ein Bild in die Swapchain.

### Camera2d

Camera2d markiert eine Entity als Kamera, aus deren Perspektive die Scene gerendert werden kann. Momentan wird immer die erste Kamera verwendet. Man kann Größe und Offset der Kamera anpassen. Die Größe gibt die Entfernung vom Mittelpunkt bis zum Bildschirmrand auf beiden Achsen an. Der Offset wird auf die Position der Entity addiert und erlaubt so Feinanpassung der Kameraposition.

### Sprite2d

In Sprite2d wird das sichtbare Bild einer Entity als ImageResource angegeben. Das Renderer2d System liest dann dieses Bild und rendert es für diese Entity.
