# Entity Component System

## Einführung

Ein Entity Component System (kurz ECS) ist eine Architektur, in der Daten und Verhalten streng getrennt werden. Daten werden in Components und Verhalten in Systems gespeichert. Combiniert werden sie in einer Scene, in der zu jeder Entity (Objekt in der Spielwelt) von Components Daten gespeichert werden und Systems dann diese Daten lesen und verändern.

> Systems sind komplett stateless. Aller State wird in Components gespeichert, auf die man von Systems aus dynamisch zugreift.

Es gibt nicht ein "richtiges" ECS. Es ist eher ein Konzept mit viel Freiheit in der Implementierung. Die Variante, die Infoengine verwendet, basiert auf den [Devlogs der Bitsquid-Engine](https://bitsquid.blogspot.com/2014/08/). Die grundlegende Idee ist, dass Components kein Array of Structures, sondern eine Structure of Arrays sind. Das heißt, dass es nur eine Instanz von einem bestimmten Component gibt, das seine Daten für jede Entity in der Scene speichert. Das macht es effizienter für Systems, eine Aktion auf vielen Entities durchzuführen, weil verwandte Daten nebeneinander im Arbeitsspeicher liegen, was besser für die Branch Prediction der CPU ist.

## Scene

Eine Scene stellt ein Level oder eine Welt dar und hat 4 wichtige Komponenten:

### Entities

Jedes Objekt in der Welt ist eine Entity. In Infoengine sind Entities nichts als eine int, haben also an sich keine Daten und kein Verhalten. Man kann sich Entites auch als ein Key vorstellen, zu dem die Values in den verschiedenen Components gespeichert sind.

### (Global) Components

In Components werden alle Daten über die Spielwelt gespeichert. Sie werden unterschieden in normale und globale Components. Normale Components speichern Daten pro Entity und haben ein Konzept von "auf einer Entity sein". Globale Components speichern Daten für die ganze Szene, ohne Bezug zu Entities.

### Systems

Systems sind Gameplay-Code-Pakete. Sie haben vollen Zugriff auf die Scene, in der sie sind, und agieren basierend auf den Components in dieser. Sie sollten aber selbst keine relevanten Daten speichern. Interaktion zwischen verschiedenen Systems ist über Events möglich.

### Events

Events sind eine Möglichkeit für Systems, miteinander zu interagieren, ohne einen direkten Link aufzubauen. Ein Event ist eine Liste an Methoden, die unter einem Namen registriert werden. Man kann das Event dann aufrufen, woraufhin alle registrierten Methoden aufgerufen werden. Dabei können auch Argumente mit übergeben werden. In Praxis ist es dann so, dass ein System ein Event aufruft und andere Systems dann darauf reagieren.
