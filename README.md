# Karteikarten
eLearning system based on online content creation with note cards. Made for Sopra 14/15 at Uni Ulm.

## Dependencies
If you want to run this project you need to get the following software somewhere
+ apache-tomcat-7.0.63
+ mysql-5.6
+ neo4j-community-2.3.0-M01

and place the folders as named above in the project root. Each folder must contain a "bin" folder as a direct subdirectory with the right executables inside. Then you should be able to do

```
javac Launcher.java
java Launcher`
```

## Description (german :D)

### Client-Seitig
#### JavaScript
Die eLearning-Plattform ist als SPA aufgebaut. Deshalb enthält
die Clientseite sehr viel Logik. Der Server dient jetzt nur noch als eine Art
"verlängerter Arm" der Datenbank. Er antwortet nur auf Daten-Anfragen des
Clients und schickt die Daten dann zurück. Dies wird durch clientseitige AJAXCalls
aus dem JavaScript-Code realisiert. Leider müssen anfallende Sicherheitsund
Zugangsprüfungen jetzt doppelt anfallen. Zum einen muss der JavaScriptCode
dafür sorgen, dass der Benutzer keine ungültigen Aktionen tätigt bzw.
dass er entsprechende Buttons (Erstellen, Löschen) erst gar nicht angezeigt bekommt.
Zum anderen muss der Server ebenfalls alle Prüfungen durchführen um
Hackern den Zugang zu verwehren. Im Großen und Ganzen besteht der Server
also nur aus diversen Zugangsprüfungen und der Weiterleitung von Daten.
Der Client, genauergesagt der JavaScript-Code stellt die Logik der eLearningPlattform
dar. Er ist für die korrekte Darstellung, sowie für die Interaktion mit
dem Benutzer verantwortlich.
#### HTML
Das HTML-Dokument enthält nur eine Art Grundgerüst der Website mit nicht
veränderlichen Komponenten und es enthält unsichtbare HTML-Templates, die
vom JavaScript-Code "geklont" werden um sie wo anders weiterzuverwenden.
### Server-Seitig
#### Servlets
Die Servlets des Servers antworten eingehenden Anfragen ausschließlich mit
JSON-Objekten. Es wird also serverseitig kein HTML-Code erzeugt. JSONObjekte
sind im Grunde genommen eine Liste von Key-Value-Paaren wobei
Values entweder Strings oder Arrays (Liste von String-Values) sind. Das Ganze
Objekt wird dann als reiner String übertragen und im JavaScript wieder als
Objekt interpretiert. So lässt sich die Client-Server-Kommunikation problemlos
realisieren.
#### Datenbank
Der nächste Punkt ist die persistente Datenspeicherung. Hier wird zum einen
die SQL-Datenbank "MySQL" verwendet. Diese ist kostenlos und somit fiel
die Wahl nicht schwer. Diese Datenbank verwaltet den Großteil aller Daten im
System. Ein Datenbank-Manager stellt hierzu eine Schnittstelle bereit, die es
den Servlets einen einfachen Zugriff auf die Daten erlaubt.
Der zweite Teil unseres Datenbank-Systems bildet die Graphdatenbank "Neo4J".
Diese ist besonders gut geeignet für die Darstellung von Beziehungen in Graphen.
Deshalb wurde sie von uns verwendet, um die Beziehungen zwischen Karteikarten
zu speichern. Sie bietet einfache Funktionen als eine SQL-Datenbank
um durch den Graph zu wandern, um Kindknoten zu suchen oder um Breiten
und Tiefensuche zu betreiben. Dies sollte vor allem bei einer großen Anzahl an
Karteikarten einen Performance-Vorteil bringen.
