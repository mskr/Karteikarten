-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 04. Jun 2015 um 16:04
-- Server Version: 5.6.20
-- PHP-Version: 5.5.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `sopra`
--

DELIMITER $$
--
-- Funktionen
--
CREATE DEFINER=`root`@`localhost` FUNCTION `levenshtein`( s1 VARCHAR(255), s2 VARCHAR(255) ) RETURNS int(11)
    DETERMINISTIC
BEGIN 
    DECLARE s1_len, s2_len, i, j, c, c_temp, cost INT; 
    DECLARE s1_char CHAR; 
    -- max strlen=255 
    DECLARE cv0, cv1 VARBINARY(256); 
    SET s1_len = CHAR_LENGTH(s1), s2_len = CHAR_LENGTH(s2), cv1 = 0x00, j = 1, i = 1, c = 0; 
    IF s1 = s2 THEN 
      RETURN 0; 
    ELSEIF s1_len = 0 THEN 
      RETURN s2_len; 
    ELSEIF s2_len = 0 THEN 
      RETURN s1_len; 
    ELSE 
      WHILE j <= s2_len DO 
        SET cv1 = CONCAT(cv1, UNHEX(HEX(j))), j = j + 1; 
      END WHILE; 
      WHILE i <= s1_len DO 
        SET s1_char = SUBSTRING(s1, i, 1), c = i, cv0 = UNHEX(HEX(i)), j = 1; 
        WHILE j <= s2_len DO 
          SET c = c + 1; 
          IF s1_char = SUBSTRING(s2, j, 1) THEN  
            SET cost = 0; ELSE SET cost = 1; 
          END IF; 
          SET c_temp = CONV(HEX(SUBSTRING(cv1, j, 1)), 16, 10) + cost; 
          IF c > c_temp THEN SET c = c_temp; END IF; 
            SET c_temp = CONV(HEX(SUBSTRING(cv1, j+1, 1)), 16, 10) + 1; 
            IF c > c_temp THEN  
              SET c = c_temp;  
            END IF; 
            SET cv0 = CONCAT(cv0, UNHEX(HEX(c))), j = j + 1; 
        END WHILE; 
        SET cv1 = cv0, i = i + 1; 
      END WHILE; 
    END IF; 
    RETURN c; 
  END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benachrichtigung`
--

CREATE TABLE IF NOT EXISTS `benachrichtigung` (
`ID` int(11) NOT NULL,
  `Inhalt` mediumtext NOT NULL,
  `Erstelldatum` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `benachrichtigung`
--

INSERT INTO `benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`) VALUES
(1, 'Ihr Profil wurde geändert!', '2015-06-04 13:17:57'),
(2, 'Sie wurden zur Veranstaltung Softwaretechnik II als Moderator hinzugefügt!', '2015-06-04 13:21:47'),
(3, 'Die Veranstaltung Softwaretechnik II wurde bearbeitet.', '2015-06-04 13:29:03');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benachrichtigung_einladung_moderator`
--

CREATE TABLE IF NOT EXISTS `benachrichtigung_einladung_moderator` (
`ID` int(11) NOT NULL,
  `Benachrichtigung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Gelesen` tinyint(1) NOT NULL DEFAULT '0',
  `Angenommen` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Daten für Tabelle `benachrichtigung_einladung_moderator`
--

INSERT INTO `benachrichtigung_einladung_moderator` (`ID`, `Benachrichtigung`, `Benutzer`, `Veranstaltung`, `Gelesen`, `Angenommen`) VALUES
(1, 2, 1, 2, 0, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benachrichtigung_karteikartenaenderung`
--

CREATE TABLE IF NOT EXISTS `benachrichtigung_karteikartenaenderung` (
`ID` int(11) NOT NULL,
  `Benachrichtigung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Karteikarte` int(11) NOT NULL,
  `Gelesen` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benachrichtigung_neuer_kommentar`
--

CREATE TABLE IF NOT EXISTS `benachrichtigung_neuer_kommentar` (
`ID` int(11) NOT NULL,
  `Benachrichtigung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Kommentar` int(11) NOT NULL,
  `Gelesen` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benachrichtigung_profil_geaendert`
--

CREATE TABLE IF NOT EXISTS `benachrichtigung_profil_geaendert` (
`ID` int(11) NOT NULL,
  `Benachrichtigung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Admin` int(11) NOT NULL,
  `Gelesen` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Daten für Tabelle `benachrichtigung_profil_geaendert`
--

INSERT INTO `benachrichtigung_profil_geaendert` (`ID`, `Benachrichtigung`, `Benutzer`, `Admin`, `Gelesen`) VALUES
(1, 1, 5, 1, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benachrichtigung_veranstaltungsaenderung`
--

CREATE TABLE IF NOT EXISTS `benachrichtigung_veranstaltungsaenderung` (
`ID` int(11) NOT NULL,
  `Benachrichtigung` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Gelesen` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Daten für Tabelle `benachrichtigung_veranstaltungsaenderung`
--

INSERT INTO `benachrichtigung_veranstaltungsaenderung` (`ID`, `Benachrichtigung`, `Veranstaltung`, `Benutzer`, `Gelesen`) VALUES
(1, 3, 2, 1, 0),
(2, 3, 2, 5, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer`
--

CREATE TABLE IF NOT EXISTS `benutzer` (
`ID` int(11) NOT NULL,
  `eMail` varchar(30) NOT NULL,
  `Vorname` varchar(30) NOT NULL,
  `Nachname` varchar(30) NOT NULL,
  `Profilbild` varchar(30) NOT NULL DEFAULT 'default.png',
  `Matrikelnummer` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL,
  `Nutzerstatus` enum('STUDENT','DOZENT','ADMIN','') NOT NULL DEFAULT 'STUDENT',
  `NotifyKommentare` enum('KEINE','VERANSTALTUNG_TEILGENOMMEN','DISKUSSION_TEILGENOMMEN','') NOT NULL DEFAULT 'KEINE',
  `NotifyVeranstAenderung` tinyint(1) NOT NULL DEFAULT '0',
  `NotifyKarteikartenAenderung` tinyint(1) NOT NULL DEFAULT '0',
  `CryptedPW` varchar(60) NOT NULL COMMENT 'hash = salt(29 Zeichen) + crypted (Rest)'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Daten für Tabelle `benutzer`
--

INSERT INTO `benutzer` (`ID`, `eMail`, `Vorname`, `Nachname`, `Profilbild`, `Matrikelnummer`, `Studiengang`, `Nutzerstatus`, `NotifyKommentare`, `NotifyVeranstAenderung`, `NotifyKarteikartenAenderung`, `CryptedPW`) VALUES
(1, 'admin@sopra.de', 'Der', 'Admin', 'default.png', 0, 'Sonstiges', 'ADMIN', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$MlwSUah1sDIHq5WztuxO8O/iW02X9/w1/J3PWkwSIhoMG0d6RieFC'),
(2, 'andreas.rottach@uni-ulm.de', 'Andreas', 'Rottach', 'default.png', 1234567, 'Informatik', 'STUDENT', 'VERANSTALTUNG_TEILGENOMMEN', 1, 1, '$2a$10$tzvmaerwHkDXK/JH1rnAHul9tNg7UuBKsTGDj3C9nCHi6z6Iyonhm'),
(3, 'matthias.englert@uni-ulm.de', 'Matthias', 'Englert', 'default.png', 815, 'Informatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$3/LOgBMsK8Lu2fYo3j0WLOIjrCC/w/MTjBit.aezhz9ED.apKE5hS'),
(4, 'mk@ulm.de', 'Marius', 'Kircher', 'default.png', 123456, 'Medieninformatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$7LlnqoZqrmtgSH4tHVdMjOFT50RwLillAq0p3Z.nz87tjTgHzxn9O'),
(5, 'helmut.partsch@uni-ulm.de', 'Helmut', 'Partsch', 'default.png', 2345678, 'Sonstiges', 'DOZENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$3s5UOGTQ6PtnW144JmofdOJKzk7WBYDPSEsbb3vrNl281tAJDrPbq');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer_veranstaltung_zuordnung`
--

CREATE TABLE IF NOT EXISTS `benutzer_veranstaltung_zuordnung` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Daten für Tabelle `benutzer_veranstaltung_zuordnung`
--

INSERT INTO `benutzer_veranstaltung_zuordnung` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(2, 1, 2),
(4, 4, 2),
(3, 5, 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_karteikarte`
--

CREATE TABLE IF NOT EXISTS `bewertung_karteikarte` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Trigger `bewertung_karteikarte`
--
DELIMITER //
CREATE TRIGGER `updateBewKarteik` AFTER INSERT ON `bewertung_karteikarte`
 FOR EACH ROW BEGIN
	UPDATE karteikarte SET Bewertung = Bewertung +
    	NEW.Bewertung
    WHERE NEW.KarteikarteID = ID;
END
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_kommentar`
--

CREATE TABLE IF NOT EXISTS `bewertung_kommentar` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KommentarID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `karteikarte`
--

CREATE TABLE IF NOT EXISTS `karteikarte` (
  `ID` int(11) NOT NULL,
  `Titel` text NOT NULL,
  `Inhalt` text NOT NULL,
  `Typ` enum('TEXT','BILD','VIDEO','') NOT NULL,
  `Bewertung` int(11) NOT NULL DEFAULT '0',
  `Aenderungsdatum` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Veranstaltung` int(11) NOT NULL,
  `Satz` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Lemma` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Beweis` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Definition` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Wichtig` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Grundlagen` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Zusatzinformation` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Exkurs` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Beispiel` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma',
  `Uebung` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Karteikarten Attribut Lemma'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `karteikarte`
--

INSERT INTO `karteikarte` (`ID`, `Titel`, `Inhalt`, `Typ`, `Bewertung`, `Aenderungsdatum`, `Veranstaltung`, `Satz`, `Lemma`, `Beweis`, `Definition`, `Wichtig`, `Grundlagen`, `Zusatzinformation`, `Exkurs`, `Beispiel`, `Uebung`) VALUES
(20, 'SW-Prüfung', '', 'TEXT', 0, '2015-06-04 13:34:01', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(21, 'Hintergrund und Motivation', '<ul><li>Akzeptanz eines Produkts wird durch Qualit&auml;t bestimmt<ul><li>&Uuml;bereinstimmung mit den Anforderungen</li><li>Leistungsumfang, Benutzbarkeit und Komfort</li><li>Flexibilit&auml;t (bez&uuml;glich &Auml;nderungen)</li></ul></li><li>Verbesserung der Qualit&auml;t (von Artefakten der System und&nbsp;Softwareentwicklung) durch<ul><li>Disziplinierte Erstellung (&bdquo;Vorgehensmodelle&ldquo;) und</li><li>Systematische Pr&uuml;fung und Behebung von Defekten</li></ul></li></ul>', 'TEXT', 0, '2015-06-04 13:37:06', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(22, 'Qualitätssicherung - Review', '', 'TEXT', 0, '2015-06-04 13:38:20', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(23, 'Wichtigste Fragen', '<ul><li>Was ist (Software-)Qualit&auml;t?</li><li>Wie stellt sich Qualit&auml;t dar? (Qualit&auml;ts-Modelle und Merkmale)</li><li>Wie kann man Qualit&auml;t messen und vergleichen? (Metriken)</li><li>Welche Ma&szlig;nahmen dienen der Hebung der Qualit&auml;t? (Qualit&auml;tssicherung)</li></ul><p>&nbsp;</p>', 'TEXT', 0, '2015-06-04 13:41:10', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(24, 'Qualität', '<ul><li>Quality (IEEE-Standard)<ul><li>(1) The degree to which a system, component, or process meets specified&nbsp;requirements</li><li>(2) The degree to which a system, component, or process meets customer or&nbsp;user needs or expectations</li></ul></li><li>Qualit&auml;t ist die Gesamtheit von Merkmalen einer Einheit bez&uuml;glich ihrer Eignung,&nbsp;festgelegte und vorausgesetzte Erfordernisse zu erf&uuml;llen (DIN ISO 8402)</li></ul>', 'TEXT', 0, '2015-06-04 13:43:34', 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
(25, 'Lernziele', '<ul><li>Grundlagen, wichtigste Aspekte und verschiedene Formen statischer Pr&uuml;fungen beschreiben k&ouml;nnen</li><li>Das Technische Review erkl&auml;ren und anwenden k&ouml;nnen</li><li>Praxisrelevante Aspekte von Reviews (z.B. Spielregeln f&uuml;r die Sitzung, Tipps f&uuml;r die Beteiligten) benennen k&ouml;nnen</li><li>St&auml;rken und Schw&auml;chen von Reviews auflisten k&ouml;nnen</li></ul>', 'TEXT', 0, '2015-06-04 13:46:40', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(26, 'Qualitätssicherung – Test', '<p><strong>Lernziele</strong></p><ul><li>Grundlagen und wichtigste Aspekte<br />hinsichtlich Tests beschreiben k&ouml;nnen</li><li>Die wichtigsten Testformen (Black-<br />Box- und Glass-Box-Tests) sowie<br />Verfahren zur Testfallauswahl<br />erkl&auml;ren und anwenden k&ouml;nnen</li><li>Andere Testverfahren beschreiben<br />k&ouml;nnen</li><li>St&auml;rken und Schw&auml;chen von Tests<br />wiedergeben k&ouml;nnen</li></ul>', 'TEXT', 0, '2015-06-04 13:47:16', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(66, 'Fakten über Software-Qualität', '<p>Gr&ouml;&szlig;te Kostentreiber sind Fehlerfindung und -behebung sowie Dokumentenerstellung</p><ul><li>30% der Softwarekosten sind Kosten f&uuml;r Dokumenterstellung</li><li>Ca. 50 Dokumente (insgesamt: 6000 Seiten, 200 000 W&ouml;rter, 5000 Diagramme)</li><li>Dokumente haben 3 Defekte/Seite (18 000 insgesamt), die ohne Pr&uuml;fma&szlig;nahmen in die&nbsp;Software gehen oder beim Kunden landen</li><li>SW-Systeme haben insgesamt ca. 50 000 Defekte&nbsp;(von denen 80% durch Pr&uuml;fma&szlig;nahmen, insbesondere Tests, gefunden werden)</li><li>Tests erfordern insgesamt ca. 55 000 Testf&auml;lle</li><li>25% der Testf&auml;lle haben Defekte&nbsp;(Defektdichte bei Testf&auml;llen oft h&ouml;her als im System selbst)</li><li>7% der (40 000 s.o.) Bug-Fixes sind Bad-Fixes (2800, die zu den 10 000 v.o. dazukommen)</li><li>Von den insgesamt 12 800 ausgelieferten Defekten sind 20 % (d.h. ca. 2500) gravierend</li></ul>', 'TEXT', 0, '2015-06-04 13:48:17', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(68, 'Statische Prüfungen (1/7)', '<h3><span style="font-family:arial,helvetica,sans-serif"><strong>Statische Pr&uuml;fung</strong></span></h3><ul><li>Pr&uuml;fung eines Dokuments (allein oder mit Gespr&auml;chspartner) nach vorgegebenen Kriterien</li></ul><h3><span style="font-family:arial,helvetica,sans-serif"><strong>Gemeinsamkeiten aller statischen Pr&uuml;fmethoden</strong></span></h3><ul><li>Wesentliche T&auml;tigkeit: dokumentierte (Teil-)Produkte untersuchen und beurteilen</li><li>Hauptziel: Defekte aller Art (Fehler, Widerspr&uuml;che, Unvollst&auml;ndigkeiten, etc.) finden</li><li>Beteiligte: (kleines) Team mit klarer Rollenverteilung</li><li>Neben individueller Pr&uuml;ft&auml;tigkeit meist auch Gruppensitzung (mit allen Beteiligten) Sinnvolle Voraussetzungen (f&uuml;r den Erfolg statischer Pr&uuml;fungen)</li></ul><h3><span style="font-family:arial,helvetica,sans-serif"><strong>Der erforderliche Aufwand (Personal, Zeit) muss fest eingeplant sein</strong></span></h3><ul><li>Es muss bekannt sein, welchem Zweck die Pr&uuml;fung dient</li><li>Es muss ein zug&auml;ngliches Pr&uuml;fdokument (Text, Programm oder Zeichnung) vorliegen</li><li>Die Mitglieder des Pr&uuml;fteams sollten in der jeweiligen Pr&uuml;fmethode geschult sein</li><li>Die Pr&uuml;fmethode sollte dokumentiert sein und ihre Einhaltung &uuml;berpr&uuml;ft werden</li><li>Pr&uuml;fergebnisse d&uuml;rfen nicht zur Personalbeurteilung missbraucht werden</li></ul>', 'TEXT', 0, '2015-06-04 13:56:49', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(79, 'Softwaretechnik II', '', 'TEXT', 0, '2015-06-04 13:21:47', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(80, 'Qualitätssicherung - Allgemeines', '', 'TEXT', 0, '2015-06-04 13:28:06', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(81, 'Qualität', '', 'TEXT', 0, '2015-06-04 13:28:21', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(82, 'Qualitätssicherung', '', 'TEXT', 0, '2015-06-04 13:28:34', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(83, 'Qualitätsmodelle', '', 'TEXT', 0, '2015-06-04 13:31:43', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(84, 'Metriken', '', 'TEXT', 0, '2015-06-04 13:32:01', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(85, 'SW-Qualität', '', 'TEXT', 0, '2015-06-04 13:32:16', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(86, 'Beispiele für Qualitäts-Metriken', '', 'TEXT', 0, '2015-06-04 13:32:43', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(87, 'Zyklomatische  Komplexität', '', 'TEXT', 0, '2015-06-04 13:33:05', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(88, 'Kohäsions-Metrik', '', 'TEXT', 0, '2015-06-04 13:33:21', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(89, 'Halstead-Metrik', '', 'TEXT', 0, '2015-06-04 13:33:36', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(90, 'Fehler', '', 'TEXT', 0, '2015-06-04 13:33:46', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `kommentar`
--

CREATE TABLE IF NOT EXISTS `kommentar` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Erstelldatum` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Benutzer` int(11) NOT NULL,
  `Karteikarte` int(11) DEFAULT NULL,
  `Vaterkommentar` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Stellvertreter-Struktur des Views `kommentaranzkinder`
--
CREATE TABLE IF NOT EXISTS `kommentaranzkinder` (
`ID` int(11)
,`Inhalt` text
,`Erstelldatum` datetime
,`Benutzer` int(11)
,`Karteikarte` int(11)
,`Vaterkommentar` int(11)
,`AnzKinder` bigint(21)
);
-- --------------------------------------------------------

--
-- Stellvertreter-Struktur des Views `kommentarbewertungen`
--
CREATE TABLE IF NOT EXISTS `kommentarbewertungen` (
`ID` int(11)
,`Inhalt` text
,`Erstelldatum` datetime
,`Benutzer` int(11)
,`Karteikarte` int(11)
,`Vaterkommentar` int(11)
,`Bewertung` decimal(32,0)
);
-- --------------------------------------------------------

--
-- Stellvertreter-Struktur des Views `kommentaruebersicht`
--
CREATE TABLE IF NOT EXISTS `kommentaruebersicht` (
`ID` int(11)
,`Inhalt` text
,`Erstelldatum` datetime
,`Benutzer` int(11)
,`Karteikarte` int(11)
,`Vaterkommentar` int(11)
,`AnzKinder` bigint(21)
,`Bewertung` decimal(32,0)
);
-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `moderator`
--

CREATE TABLE IF NOT EXISTS `moderator` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `moderator`
--

INSERT INTO `moderator` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(2, 1, 2),
(3, 4, 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `notiz`
--

CREATE TABLE IF NOT EXISTS `notiz` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `semester`
--

CREATE TABLE IF NOT EXISTS `semester` (
`ID` int(11) NOT NULL,
  `Name` varchar(30) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15 ;

--
-- Daten für Tabelle `semester`
--

INSERT INTO `semester` (`ID`, `Name`) VALUES
(11, 'WiSe2014/15'),
(12, 'SoSe2015'),
(13, 'WiSe2015/16'),
(14, 'SoSe2016');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `studiengang`
--

CREATE TABLE IF NOT EXISTS `studiengang` (
  `Name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `studiengang`
--

INSERT INTO `studiengang` (`Name`) VALUES
('Biologie'),
('Chemie'),
('Informatik'),
('Medieninformatik'),
('Physik'),
('Sonstiges');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung` (
`ID` int(11) NOT NULL,
  `Beschreibung` text NOT NULL,
  `Semester` varchar(30) NOT NULL,
  `Kennwort` varchar(30) DEFAULT NULL,
  `KommentareErlaubt` tinyint(1) NOT NULL,
  `BewertungenErlaubt` tinyint(1) NOT NULL,
  `ModeratorKarteikartenBearbeiten` tinyint(1) NOT NULL,
  `Ersteller` int(11) NOT NULL,
  `Titel` varchar(255) NOT NULL,
  `ErsteKarteikarte` int(11) DEFAULT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Daten für Tabelle `veranstaltung`
--

INSERT INTO `veranstaltung` (`ID`, `Beschreibung`, `Semester`, `Kennwort`, `KommentareErlaubt`, `BewertungenErlaubt`, `ModeratorKarteikartenBearbeiten`, `Ersteller`, `Titel`, `ErsteKarteikarte`) VALUES
(2, '<p>Die Vorlesung Softwaretechnik II ist die Fortsetzungsveranstaltung der&nbsp;Softwaretechnik I.</p><p>Themen der Vorlesung Softwaretechnik II (im Sommersemester) sind:</p><ul><li>Qualit&auml;tssicherung (Metriken, Systematisches Testen, Reviews)</li><li>Projektmanagement (Planung, Kostensch&auml;tzung, Controlling, Konfigurationsmanagement, Qualit&auml;tsmanagement, Prozessverbesserung)</li></ul>', 'SoSe2015', NULL, 1, 1, 1, 5, 'Softwaretechnik II', 79);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung_studiengang_zuordnung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung_studiengang_zuordnung` (
`ID` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Daten für Tabelle `veranstaltung_studiengang_zuordnung`
--

INSERT INTO `veranstaltung_studiengang_zuordnung` (`ID`, `Veranstaltung`, `Studiengang`) VALUES
(4, 2, 'Informatik'),
(5, 2, 'Medieninformatik');

-- --------------------------------------------------------

--
-- Struktur des Views `kommentaranzkinder`
--
DROP TABLE IF EXISTS `kommentaranzkinder`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `kommentaranzkinder` AS select `k`.`ID` AS `ID`,`k`.`Inhalt` AS `Inhalt`,`k`.`Erstelldatum` AS `Erstelldatum`,`k`.`Benutzer` AS `Benutzer`,`k`.`Karteikarte` AS `Karteikarte`,`k`.`Vaterkommentar` AS `Vaterkommentar`,count(`k2`.`ID`) AS `AnzKinder` from (`kommentar` `k` left join `kommentar` `k2` on((`k`.`ID` = `k2`.`Vaterkommentar`))) group by `k`.`ID`;

-- --------------------------------------------------------

--
-- Struktur des Views `kommentarbewertungen`
--
DROP TABLE IF EXISTS `kommentarbewertungen`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `kommentarbewertungen` AS select `k`.`ID` AS `ID`,`k`.`Inhalt` AS `Inhalt`,`k`.`Erstelldatum` AS `Erstelldatum`,`k`.`Benutzer` AS `Benutzer`,`k`.`Karteikarte` AS `Karteikarte`,`k`.`Vaterkommentar` AS `Vaterkommentar`,sum(`b`.`Bewertung`) AS `Bewertung` from (`kommentar` `k` left join `bewertung_kommentar` `b` on((`k`.`ID` = `b`.`KommentarID`))) group by `k`.`ID`;

-- --------------------------------------------------------

--
-- Struktur des Views `kommentaruebersicht`
--
DROP TABLE IF EXISTS `kommentaruebersicht`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `kommentaruebersicht` AS select `stmt1`.`ID` AS `ID`,`stmt1`.`Inhalt` AS `Inhalt`,`stmt1`.`Erstelldatum` AS `Erstelldatum`,`stmt1`.`Benutzer` AS `Benutzer`,`stmt1`.`Karteikarte` AS `Karteikarte`,`stmt1`.`Vaterkommentar` AS `Vaterkommentar`,`stmt1`.`AnzKinder` AS `AnzKinder`,`stmt2`.`Bewertung` AS `Bewertung` from (`kommentaranzkinder` `stmt1` join `kommentarbewertungen` `stmt2` on((`stmt1`.`ID` = `stmt2`.`ID`)));

--
-- Indexes for dumped tables
--

--
-- Indexes for table `benachrichtigung`
--
ALTER TABLE `benachrichtigung`
 ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Benachrichtigung` (`Benachrichtigung`);

--
-- Indexes for table `benachrichtigung_karteikartenaenderung`
--
ALTER TABLE `benachrichtigung_karteikartenaenderung`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benachrichtigung` (`Benachrichtigung`), ADD KEY `Benutzer` (`Benutzer`);

--
-- Indexes for table `benachrichtigung_neuer_kommentar`
--
ALTER TABLE `benachrichtigung_neuer_kommentar`
 ADD PRIMARY KEY (`ID`), ADD KEY `Kommentar` (`Kommentar`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Benachrichtigung` (`Benachrichtigung`);

--
-- Indexes for table `benachrichtigung_profil_geaendert`
--
ALTER TABLE `benachrichtigung_profil_geaendert`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benachrichtigung` (`Benachrichtigung`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Admin` (`Admin`);

--
-- Indexes for table `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
 ADD PRIMARY KEY (`ID`), ADD KEY `VeranstaltungID` (`Veranstaltung`), ADD KEY `Benachrichtigung` (`Benachrichtigung`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Benutzer` (`Benutzer`);

--
-- Indexes for table `benutzer`
--
ALTER TABLE `benutzer`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `eMail` (`eMail`), ADD KEY `Studiengang` (`Studiengang`);

--
-- Indexes for table `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `Benutzer` (`Benutzer`,`Veranstaltung`), ADD UNIQUE KEY `Benutzer_2` (`Benutzer`,`Veranstaltung`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `VeranstaltungID` (`Veranstaltung`), ADD KEY `VeranstaltungID_2` (`Veranstaltung`);

--
-- Indexes for table `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `Benutzer` (`Benutzer`,`KarteikarteID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `KarteikarteID` (`KarteikarteID`);

--
-- Indexes for table `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
 ADD PRIMARY KEY (`ID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `KommentarID` (`KommentarID`);

--
-- Indexes for table `karteikarte`
--
ALTER TABLE `karteikarte`
 ADD PRIMARY KEY (`ID`), ADD KEY `VeranstaltungID` (`Veranstaltung`);

--
-- Indexes for table `kommentar`
--
ALTER TABLE `kommentar`
 ADD PRIMARY KEY (`ID`), ADD KEY `ErstellerID` (`Benutzer`), ADD KEY `DiskussionID` (`Karteikarte`), ADD KEY `VaterkommentarID` (`Vaterkommentar`);

--
-- Indexes for table `moderator`
--
ALTER TABLE `moderator`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Veranstaltung` (`Veranstaltung`);

--
-- Indexes for table `notiz`
--
ALTER TABLE `notiz`
 ADD PRIMARY KEY (`ID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `KarteikarteID` (`KarteikarteID`);

--
-- Indexes for table `semester`
--
ALTER TABLE `semester`
 ADD PRIMARY KEY (`Name`), ADD UNIQUE KEY `ID` (`ID`);

--
-- Indexes for table `studiengang`
--
ALTER TABLE `studiengang`
 ADD PRIMARY KEY (`Name`);

--
-- Indexes for table `veranstaltung`
--
ALTER TABLE `veranstaltung`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `Semester` (`Semester`,`Titel`), ADD KEY `Ersteller` (`Ersteller`), ADD KEY `Semester_2` (`Semester`), ADD KEY `ErsteKarteikarte` (`ErsteKarteikarte`);

--
-- Indexes for table `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
 ADD PRIMARY KEY (`ID`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Studiengang` (`Studiengang`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `benachrichtigung`
--
ALTER TABLE `benachrichtigung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `benachrichtigung_karteikartenaenderung`
--
ALTER TABLE `benachrichtigung_karteikartenaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `benachrichtigung_neuer_kommentar`
--
ALTER TABLE `benachrichtigung_neuer_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `benachrichtigung_profil_geaendert`
--
ALTER TABLE `benachrichtigung_profil_geaendert`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `benutzer`
--
ALTER TABLE `benutzer`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `kommentar`
--
ALTER TABLE `kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `moderator`
--
ALTER TABLE `moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `notiz`
--
ALTER TABLE `notiz`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `semester`
--
ALTER TABLE `semester`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
--
-- AUTO_INCREMENT for table `veranstaltung`
--
ALTER TABLE `veranstaltung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
ADD CONSTRAINT `benachrichtigung_einladung_moderator_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_einladung_moderator_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_einladung_moderator_ibfk_4` FOREIGN KEY (`Benachrichtigung`) REFERENCES `benachrichtigung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `benachrichtigung_karteikartenaenderung`
--
ALTER TABLE `benachrichtigung_karteikartenaenderung`
ADD CONSTRAINT `benachrichtigung_karteikartenaenderung_ibfk_1` FOREIGN KEY (`Benachrichtigung`) REFERENCES `benachrichtigung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_karteikartenaenderung_ibfk_2` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `benachrichtigung_neuer_kommentar`
--
ALTER TABLE `benachrichtigung_neuer_kommentar`
ADD CONSTRAINT `benachrichtigung_neuer_kommentar_ibfk_1` FOREIGN KEY (`Benachrichtigung`) REFERENCES `benachrichtigung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_neuer_kommentar_ibfk_2` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_neuer_kommentar_ibfk_3` FOREIGN KEY (`Kommentar`) REFERENCES `kommentar` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `benachrichtigung_profil_geaendert`
--
ALTER TABLE `benachrichtigung_profil_geaendert`
ADD CONSTRAINT `benachrichtigung_profil_geaendert_ibfk_1` FOREIGN KEY (`Benachrichtigung`) REFERENCES `benachrichtigung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_profil_geaendert_ibfk_2` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_profil_geaendert_ibfk_3` FOREIGN KEY (`Admin`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
ADD CONSTRAINT `benachrichtigung_veranstaltungsaenderung_ibfk_1` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_veranstaltungsaenderung_ibfk_2` FOREIGN KEY (`Benachrichtigung`) REFERENCES `benachrichtigung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benachrichtigung_veranstaltungsaenderung_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `benutzer`
--
ALTER TABLE `benutzer`
ADD CONSTRAINT `benutzer_ibfk_1` FOREIGN KEY (`Studiengang`) REFERENCES `studiengang` (`Name`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints der Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
ADD CONSTRAINT `benutzer_veranstaltung_zuordnung_ibfk_3` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `benutzer_veranstaltung_zuordnung_ibfk_4` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
ADD CONSTRAINT `bewertung_karteikarte_ibfk_1` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `bewertung_karteikarte_ibfk_2` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
ADD CONSTRAINT `bewertung_kommentar_ibfk_2` FOREIGN KEY (`KommentarID`) REFERENCES `kommentar` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `bewertung_kommentar_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `karteikarte`
--
ALTER TABLE `karteikarte`
ADD CONSTRAINT `karteikarte_ibfk_1` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `kommentar`
--
ALTER TABLE `kommentar`
ADD CONSTRAINT `kommentar_ibfk_1` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `kommentar_ibfk_2` FOREIGN KEY (`Karteikarte`) REFERENCES `karteikarte` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `kommentar_ibfk_3` FOREIGN KEY (`Vaterkommentar`) REFERENCES `kommentar` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `moderator`
--
ALTER TABLE `moderator`
ADD CONSTRAINT `moderator_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `moderator_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `notiz`
--
ALTER TABLE `notiz`
ADD CONSTRAINT `notiz_ibfk_1` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
ADD CONSTRAINT `veranstaltung_ibfk_2` FOREIGN KEY (`Ersteller`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `veranstaltung_ibfk_3` FOREIGN KEY (`Semester`) REFERENCES `semester` (`Name`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints der Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
ADD CONSTRAINT `veranstaltung_studiengang_zuordnung_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `veranstaltung_studiengang_zuordnung_ibfk_3` FOREIGN KEY (`Studiengang`) REFERENCES `studiengang` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
