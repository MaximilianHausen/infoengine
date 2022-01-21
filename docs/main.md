#### Hinweis: Der Code und die Kommentare sind auf Englisch, weil viele Sachen in Deutsch länger sind

## Allgemeines
- Ich verwende IntelliJ Idea mit Java 17 und Gradle 7.2 (Kotlin Buildscript). Wenn es woanders nicht funktioniert liegt es wahrscheinlich am Kotlin Buildscript
- Die Documentation ist so aufgebaut, dass sie den Packages möglichst ähnlich ist
- Einfache Nutzungsbeispiele für fast alles gibt es in den Unit-Tests

### Highlights
In jeder Unterkategorie gibt es eine Liste mit meiner Meinung nach besonders schönen Codeabschnitten

### Namensherkunft
Alle Packages haben so einen langen Namen und es würde bei den imports komisch aussehen, wenn die anderen ewig lang sind und meine nicht.\
org.totogames: Weil mein Name für sowas noch viel länger wär verwende ich immer totogames als Name, benannt nach dem Hauptcharakter in meinem ersten Programmierprojekt\
infoengine: Info weil es für den Informatikunterricht ist und Engine weil es eine Game-Engine ist (wenn auch ohne Leveleditor und andere Tools)

### [JetBrains.Annotations](https://javadoc.io/doc/org.jetbrains/annotations)
Anmerkungen, um mit Nullability und sowas zu helfen. Nur auf Dinge, die von außerhalb verfügbar sind, angewandt. Sonstige Bemerkungen:
- Nullability
  - @NotNull: Kann nicht null sein
  - Nichts: Sollte nicht null sein (Null wenn Error)
  - @Nullable: Null kommt im normalen Gebrauch vor
- @MustBeInvokedByOverriders: Nur verwendet, wenn die Methode auch zum Überschreiben gedacht ist

### Inhaltsverzeichnis
- [loading](loading.md)
- [rendering](rendering.md)
- [util](util.md)