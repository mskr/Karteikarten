-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 04. Jun 2015 um 13:33
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=94 ;

--
-- Daten für Tabelle `benachrichtigung`
--

INSERT INTO `benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`) VALUES
(93, 'Sie wurden zur Veranstaltung Softwaretechnik II als Moderator hinzugefügt!', '2015-06-04 10:34:22');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

--
-- Daten für Tabelle `benachrichtigung_einladung_moderator`
--

INSERT INTO `benachrichtigung_einladung_moderator` (`ID`, `Benachrichtigung`, `Benutzer`, `Veranstaltung`, `Gelesen`, `Angenommen`) VALUES
(8, 93, 3, 16, 0, NULL);

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

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
(1, 'admin@sopra.de', 'Der', 'Admin', 'default.png', 0, 'Sonstiges', 'ADMIN', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$QsVI0z2TUIdYSDi39XCiJ.n0WoTiAqaGNzUywpARCOTziN6vqHeB2'),
(2, 'andreas.rottach@uni-ulm.de', 'Andreas', 'Rottach', 'default.png', 9876543, 'Informatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$GGMClCKC4cCXVsZ7Ra6LYe/R4/u0DLok1xUSsgZrfgrFSOKk2qXhq'),
(3, 'marius.kircher@uni-ulm.de', 'Marius', 'Kircher', 'default.png', 12345677, 'Medieninformatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$XBYHkhU0oGtb/OexGWf21OvLVEoGWCFiZv0.GiSBQ8bqyekRRVa6i'),
(4, 'matthias.englert@uni-ulm.de', 'Matthias', 'Englert', 'default.png', 23456789, 'Informatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$u9GkPn5g43n0U2T9DUWGm.RSEgsqVOrEhS4W2WDl4zqZCp4L.mOhi'),
(5, 'helmut.partsch@uni-ulm.de', 'Helmut', 'Partsch', 'default.png', 3333333, 'Informatik', 'DOZENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$xKK4YRCGADT8RNzhj93OXeNIhMFj0QrzIwNDtVdpruSxr8QoOZxUC');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer_veranstaltung_zuordnung`
--

CREATE TABLE IF NOT EXISTS `benutzer_veranstaltung_zuordnung` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=40 ;

--
-- Daten für Tabelle `benutzer_veranstaltung_zuordnung`
--

INSERT INTO `benutzer_veranstaltung_zuordnung` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(38, 3, 16),
(39, 5, 16);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_karteikarte`
--

CREATE TABLE IF NOT EXISTS `bewertung_karteikarte` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=84 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;

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
(70, 'Softwaretechnik II', '', 'TEXT', 0, '2015-06-04 10:34:22', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(71, 'Qualitätssicherung - Allgemeines', '', 'TEXT', 0, '2015-06-04 10:39:15', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(74, 'Qualität', '', 'TEXT', 0, '2015-06-04 10:44:04', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(75, 'Qualitätssicherung', '', 'TEXT', 0, '2015-06-04 10:44:17', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(76, 'Qualitätsmodelle', '', 'TEXT', 0, '2015-06-04 10:44:28', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(77, 'Metriken', '', 'TEXT', 0, '2015-06-04 10:44:41', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(78, 'SW-Qualität', '', 'TEXT', 0, '2015-06-04 10:45:46', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(79, 'Beispiele für Qualitäts-Metriken', '', 'TEXT', 0, '2015-06-04 10:46:21', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(80, 'Zyklomatische Komplexität', '', 'TEXT', 0, '2015-06-04 10:46:35', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(81, 'Kohäsions-Metrik', '', 'TEXT', 0, '2015-06-04 10:47:11', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(82, 'Halstead-Metrik', '', 'TEXT', 0, '2015-06-04 10:47:29', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(83, 'Anhang', '', 'TEXT', 0, '2015-06-04 10:50:02', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(84, 'Fehler', '', 'TEXT', 0, '2015-06-04 10:53:44', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(85, 'SW-Prüfung', '', 'TEXT', 0, '2015-06-04 11:11:39', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(86, 'Hintergrund und Motivation', '<ul><li>Akzeptanz eines Produkts wird durch Qualit&auml;t bestimmt<ul><li>&Uuml;bereinstimmung mit den Anforderungen</li><li>Leistungsumfang, Benutzbarkeit und Komfort</li><li>Flexibilit&auml;t (bez&uuml;glich &Auml;nderungen)</li></ul></li><li>Verbesserung der Qualit&auml;t (von Artefakten der Systemund Softwareentwicklung) durch<ul><li>Disziplinierte Erstellung (&bdquo;Vorgehensmodelle&ldquo;) und</li><li>Systematische Pr&uuml;fung und Behebung von Defekten</li></ul></li></ul>', 'TEXT', 0, '2015-06-04 11:13:28', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(87, 'Wichtigste Fragen', '<ul><li>Was ist (Software-)Qualit&auml;t?</li><li>Wie stellt sich Qualit&auml;t dar?</li><li>Wie kann man Qualit&auml;t messen und vergleichen?&nbsp;</li><li>Welche Ma&szlig;nahmen dienen der Hebung der Qualit&auml;t?</li></ul>', 'TEXT', 0, '2015-06-04 11:15:59', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(88, 'Qualität', '<ul><li>Quality (IEEE-Standard)<ul><li>(1) The degree to which a system, component, or process meets specified requirements</li><li>(2) The degree to which a system, component, or process meets customer or user needs or expectations</li></ul></li><li>Qualit&auml;t ist die Gesamtheit von Merkmalen einer Einheit bez&uuml;glich ihrer Eignung, festgelegte und vorausgesetzte Erfordernisse zu erf&uuml;llen (DIN ISO 8402)</li></ul><p><strong>Beispiel </strong>(Qualit&auml;tsmerkmale)</p><ul><li>Testkriterien bei Stiftung Warentest</li></ul>', 'TEXT', 0, '2015-06-04 11:27:16', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(89, 'Softwarequalität', '<p>Softwarequalit&auml;t ist die Gesamtheit der Merkmale eines Softwareprodukts, die sich auf dessen Eignung beziehen, festgelegte und vorausgesetzte Erfordernisse zu erf&uuml;llen.</p>', 'TEXT', 0, '2015-06-04 11:28:41', 16, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0),
(90, 'Spezifische Probleme der Softwarequalität', '<ul><li>Keine anerkannten Marken (die nur qualitativ hochwertige Produkte liefern).</li><li>Keine Qualit&auml;ts-Standards ! Nach klaren Kriterien gepr&uuml;ft ! Von anerkannten Organisationen.</li><li>Keine De-facto-Standards (z.B. &bdquo;made in Germany&ldquo;)).</li></ul>', 'TEXT', 0, '2015-06-04 11:32:45', 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=23 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15 ;

--
-- Daten für Tabelle `moderator`
--

INSERT INTO `moderator` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(14, 3, 16);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `notiz`
--

CREATE TABLE IF NOT EXISTS `notiz` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;

--
-- Daten für Tabelle `veranstaltung`
--

INSERT INTO `veranstaltung` (`ID`, `Beschreibung`, `Semester`, `Kennwort`, `KommentareErlaubt`, `BewertungenErlaubt`, `ModeratorKarteikartenBearbeiten`, `Ersteller`, `Titel`, `ErsteKarteikarte`) VALUES
(16, '<p>Die Vorlesung Softwaretechnik II ist die Fortsetzungsveranstaltung der&nbsp;Softwaretechnik I.</p><p>Themen der Vorlesung Softwaretechnik II (im Sommersemester) sind:</p><ul><li>Qualit&auml;tssicherung (Metriken, Systematisches Testen, Reviews)</li><li>Projektmanagement (Planung, Kostensch&auml;tzung, Controlling, Konfigurationsmanagement, Qualit&auml;tsmanagement, Prozessverbesserung)</li></ul><p><strong>Klausur</strong></p><p>Die Ergebnisse der Nachklausur stehen ab sofort im Hochschulportal.</p><p>Die Klausureinsicht findet am Di. 12.11.2013 von 15:00&nbsp;-&nbsp;16:30 Uhr&nbsp;im Raum 412 statt.</p>', 'SoSe2015', '1234', 1, 1, 1, 5, 'Softwaretechnik II', 70);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung_studiengang_zuordnung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung_studiengang_zuordnung` (
`ID` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=35 ;

--
-- Daten für Tabelle `veranstaltung_studiengang_zuordnung`
--

INSERT INTO `veranstaltung_studiengang_zuordnung` (`ID`, `Veranstaltung`, `Studiengang`) VALUES
(33, 16, 'Informatik'),
(34, 16, 'Medieninformatik');

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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=94;
--
-- AUTO_INCREMENT for table `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `benutzer`
--
ALTER TABLE `benutzer`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=40;
--
-- AUTO_INCREMENT for table `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=84;
--
-- AUTO_INCREMENT for table `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=17;
--
-- AUTO_INCREMENT for table `kommentar`
--
ALTER TABLE `kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=23;
--
-- AUTO_INCREMENT for table `moderator`
--
ALTER TABLE `moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
--
-- AUTO_INCREMENT for table `notiz`
--
ALTER TABLE `notiz`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `semester`
--
ALTER TABLE `semester`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
--
-- AUTO_INCREMENT for table `veranstaltung`
--
ALTER TABLE `veranstaltung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=17;
--
-- AUTO_INCREMENT for table `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=35;
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
