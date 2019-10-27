# Karteikarten

An eLearning system based on online content creation with note cards.

![Screenshot](https://raw.githubusercontent.com/MariusUniUlm/Karteikarten/master/prog/source/client/files/images/119.png "You see the mobile version here. Thanks to CSS the layout degrades gracefully :)")

[More Screenshots](/docu/ui-design)

## Dependencies

If you want to run this project you need to get the following software somewhere.

### apache-tomcat-7.0.63

### mysql-8.0.18-winx64

Available from MySQL homepage (also as portable zip file):
https://dev.mysql.com/downloads/mysql/

I decided to deeplink to mysql-8.0.18-winx64-debug-test.zip, because Oracle redirects to uncanny login pages:
https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.18-winx64-debug-test.zip

### neo4j-community-3.5.12

https://neo4j.com/download-center/#community

Place the folders as named above in the project root, so that the automatic Launcher tool finds them (change the Launcher source to support other versions). Each folder must contain a "bin" folder as a direct subdirectory with the right executables inside.

## Install and Launch

To setup initial database contents, consider [prog/install](prog/install).

Then you should be able to do

```
javac Launcher.java
java Launcher
```

## Description

### Client Side

#### JavaScript

The eLearning platform is built as a SPA. Therefore, the client side contains a lot of logic. The server now only serves as a kind of "extended arm" of the database. It only responds to data requests from the client and then sends the data back. This is realized by client-side AJAXCalls from the JavaScript code. Unfortunately, security and access checks must now be incurred twice. On the one hand, the JavaScript code has to ensure that the user does not make any invalid actions or that he does not even see corresponding buttons (create, delete). On the other hand, the server must also perform all checks to deny hackers access. On the whole, therefore, the server consists only of various access checks and the forwarding of data. The client, more specifically the JavaScript code, represents the logic of the eLearning platform. It is responsible for the correct presentation as well as the interaction with the user.

#### HTML

The HTML document contains only a kind of skeleton of the website with non-changeable components and it contains invisible HTML templates, which are "cloned" by the JavaScript code to reuse them elsewhere.

### Server Side

#### Servlets

The servlets of the server respond to incoming requests only with JSON objects. Thus, no HTML code is generated on the server side. JSON objects are basically a list of key value pairs, where values ​​are either strings or arrays (list of string values). The whole object is then transferred as a pure string and again interpreted as an object in JavaScript. This makes it easy to implement client-server communication.

#### Datenbank

The next point is persistent data storage. On the one hand, the SQL database "MySQL" is used here. This is free and so the choice was not difficult. This database manages most of the data in the system. A database manager provides an interface that allows the servlets easy access to the data. The second part of our database system is the graph database "Neo4J". This is particularly well suited for the representation of relationships in graphs. That's why it was used by us to store the relationships between cards. It provides simple functions as a SQL database to move through the graph, to search child nodes, or to search latitudes and depths. This should bring a performance advantage especially with a large number of index cards.

## About

Made for "Sopra 14/15" at Ulm University.