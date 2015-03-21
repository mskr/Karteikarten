README

/WEB-INF
    web.xml
    /lib
        myjar.jar
    /classes
        myclass.class
/META-INF
    context.xml

Die Ordnerstruktur mit META-INF und WEB-INF ist Tomcat-spezifisch und wird von Eclipse erkannt.
Das hilft später beim Build.
Man exportiert das Dynamic Web Project als WAR File und kann es dann auf jedem Tomcat Server deployen.

Wir sollten auch die Bibliotheken nach /WEB-INF/lib kopieren.
Dann muss sie (wahrscheinlich) nicht mehr jeder nach "<Tomcat>/lib" kopieren (noch ungetestet).

Um die web.xml muss man sich nicht kuemmern. Wird von Eclipse beim Export automatisch in WEB-INF abgelegt.

Beim Export als WAR File werden von Eclipse auch automatisch der kompilierte Java Code nach WEB-INF/classes gepackt.
Weil wir jetzt aber auch die web Files  im Source Folder haben werden die auch dazu gepackt.
Sollte man vielleicht vor dem Build doch ausserhalb des Source Folders haben.

Von Marius, 20.3.