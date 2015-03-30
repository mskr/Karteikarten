-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 30. Mrz 2015 um 17:25
-- Server Version: 5.6.21
-- PHP-Version: 5.6.3

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
  `Inhalt` text NOT NULL,
  `Erstelldatum` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benachrichtigung`
--

INSERT INTO `benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`) VALUES
(1, 'Einladung als Moderator für die Vorlesung Softwaretechnik', '2015-03-20 23:00:00'),
(2, 'Einladung als Moderator für die Vorlesung Algorithmen und Datenstrukturen', '2015-03-02 23:00:00'),
(3, 'Prüfungstermine sind nun in der Beschreibung bekannt gegeben', '2015-03-10 23:00:00'),
(40, '', '2015-03-28 22:39:30');

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
  `Angenommen` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benachrichtigung_einladung_moderator`
--

INSERT INTO `benachrichtigung_einladung_moderator` (`ID`, `Benachrichtigung`, `Benutzer`, `Veranstaltung`, `Gelesen`, `Angenommen`) VALUES
(1, 1, 4, 1, 0, 0),
(2, 2, 6, 4, 0, 0);

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benachrichtigung_profil_geaendert`
--

INSERT INTO `benachrichtigung_profil_geaendert` (`ID`, `Benachrichtigung`, `Benutzer`, `Admin`, `Gelesen`) VALUES
(3, 40, 6, 2, 0);

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benachrichtigung_veranstaltungsaenderung`
--

INSERT INTO `benachrichtigung_veranstaltungsaenderung` (`ID`, `Benachrichtigung`, `Veranstaltung`, `Benutzer`, `Gelesen`) VALUES
(1, 3, 1, 5, 0);

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
  `Kennwort` varchar(30) NOT NULL,
  `Nutzerstatus` enum('STUDENT','DOZENT','ADMIN','') NOT NULL DEFAULT 'STUDENT',
  `NotifyKommentare` enum('KEINE','VERANSTALTUNG_TEILGENOMMEN','DISKUSSION_TEILGENOMMEN','') NOT NULL DEFAULT 'KEINE',
  `NotifyVeranstAenderung` tinyint(1) NOT NULL DEFAULT '0',
  `NotifyKarteikartenAenderung` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benutzer`
--

INSERT INTO `benutzer` (`ID`, `eMail`, `Vorname`, `Nachname`, `Profilbild`, `Matrikelnummer`, `Studiengang`, `Kennwort`, `Nutzerstatus`, `NotifyKommentare`, `NotifyVeranstAenderung`, `NotifyKarteikartenAenderung`) VALUES
(1, 'abc@def.de', 'abc', 'def', 'default.png', 123, 'Informatik', '1234', 'STUDENT', 'KEINE', 0, 0),
(2, 'admin@blablub.de', 'Der', 'Admin', 'default.png', 222222, 'Sonstiges', '1234', 'ADMIN', 'VERANSTALTUNG_TEILGENOMMEN', 1, 1),
(3, 'alber.einstein@uni-ulm.de', 'Albert', 'Einstein', 'default.png', 333333, 'Physik', '1234', 'DOZENT', 'KEINE', 0, 0),
(4, 'andreas.rottach@uni-ulm.de', 'Andreas', 'Rottach', 'default.png', 12345, 'Informatik', '1234', 'DOZENT', 'DISKUSSION_TEILGENOMMEN', 1, 1),
(5, 'marius.kircher@uni-ulm.de', 'Marius', 'Kircher', 'default.png', 111111, 'Medieninformatik', '1234', 'STUDENT', 'VERANSTALTUNG_TEILGENOMMEN', 1, 0),
(6, 'matthias.englert@uni-ulm.de', 'Matthias', 'Englert', 'default.png', 828584, 'Informatik', '1234', 'STUDENT', 'KEINE', 0, 0),
(7, 'heinz.harald@uni-ulm.de', 'Heinz Harald', 'Haraldson', 'default.png', 123456, 'Chemie', '1234', 'STUDENT', 'KEINE', 0, 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer_veranstaltung_zuordnung`
--

CREATE TABLE IF NOT EXISTS `benutzer_veranstaltung_zuordnung` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benutzer_veranstaltung_zuordnung`
--

INSERT INTO `benutzer_veranstaltung_zuordnung` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(4, 1, 2),
(7, 1, 4),
(9, 2, 3),
(5, 3, 5),
(1, 4, 1),
(8, 4, 6),
(2, 5, 1),
(3, 6, 1),
(10, 6, 2),
(6, 6, 4),
(12, 6, 7);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_karteikarte`
--

CREATE TABLE IF NOT EXISTS `bewertung_karteikarte` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_kommentar`
--

CREATE TABLE IF NOT EXISTS `bewertung_kommentar` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` varchar(30) NOT NULL,
  `KommentarID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `karteikarte`
--

CREATE TABLE IF NOT EXISTS `karteikarte` (
`ID` int(11) NOT NULL,
  `Titel` text NOT NULL,
  `Typ` enum('Text','Bild','Video','') NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Aenderungsdatum` date NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `karteikarten_struktur`
--

CREATE TABLE IF NOT EXISTS `karteikarten_struktur` (
`ID` int(11) NOT NULL,
  `Position` int(11) NOT NULL,
  `SohnKarteik` int(11) NOT NULL,
  `VaterKarteik` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `kommentar`
--

CREATE TABLE IF NOT EXISTS `kommentar` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Erstelldatum` date NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL,
  `VaterkommentarID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `moderator`
--

CREATE TABLE IF NOT EXISTS `moderator` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `moderator`
--

INSERT INTO `moderator` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(1, 4, 1),
(2, 6, 4),
(3, 1, 1),
(4, 5, 4);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `notiz`
--

CREATE TABLE IF NOT EXISTS `notiz` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `semester`
--

CREATE TABLE IF NOT EXISTS `semester` (
  `Name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `semester`
--

INSERT INTO `semester` (`Name`) VALUES
('SoSe2013'),
('SoSe2014'),
('SoSe2015'),
('SoSe2016'),
('SoSe2017'),
('WiSe2013/14'),
('WiSe2014/15'),
('WiSe2015/16'),
('WiSe2016/17');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `studiengang`
--

CREATE TABLE IF NOT EXISTS `studiengang` (
  `Name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `studiengang`
--

INSERT INTO `studiengang` (`Name`) VALUES
('Biologie'),
('Chemie'),
('Chemie Ingenieure'),
('Elektrotechnik'),
('Informatik'),
('Informationssystemtechnik'),
('Mathematik'),
('Medieninformatik'),
('Medizin'),
('Physik'),
('Softwareengeneering'),
('Sonstiges'),
('Wirtschaftsmathematik');

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
  `Titel` varchar(50) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `veranstaltung`
--

INSERT INTO `veranstaltung` (`ID`, `Beschreibung`, `Semester`, `Kennwort`, `KommentareErlaubt`, `BewertungenErlaubt`, `ModeratorKarteikartenBearbeiten`, `Ersteller`, `Titel`) VALUES
(1, 'Softwaretechnik Vorlesung', 'WiSe2014/15', '1234', 1, 1, 1, 5, 'Softwaretechnik'),
(2, 'Softwaretechnik Vorlesung', 'WiSe2015/16', '1234', 1, 1, 1, 4, 'Softwaretechnik'),
(3, 'Medizin I', 'SoSe2013', '1234', 1, 0, 0, 2, 'Medizin I'),
(4, 'Algorithmen und Datenstrukturen Vorlesung', 'WiSe2014/15', '1234', 1, 0, 1, 6, 'Algorithmen und Datenstrukture'),
(5, 'Physik I für Ingenieure Vorlesung', 'SoSe2015', '1234', 1, 0, 0, 3, 'Physik I für Ingenieure'),
(6, 'Latex Kurs', 'WiSe2014/15', '1234', 1, 1, 1, 2, 'Latex Kurs'),
(7, 'Softwaregrundproekt', 'SoSe2015', NULL, 1, 1, 1, 6, 'Sopra'),
(8, 'Grundlagen der Betriebssysteme Vorlesung', 'WiSe2014/15', NULL, 1, 0, 0, 5, 'Grundlagen der Betriebssysteme');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung_studiengang_zuordnung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung_studiengang_zuordnung` (
`ID` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `veranstaltung_studiengang_zuordnung`
--

INSERT INTO `veranstaltung_studiengang_zuordnung` (`ID`, `Veranstaltung`, `Studiengang`) VALUES
(1, 1, 'Informatik'),
(2, 1, 'Medieninformatik'),
(3, 1, 'Softwareengeneering'),
(4, 2, 'Informatik'),
(5, 2, 'Medieninformatik'),
(6, 2, 'Softwareengeneering'),
(9, 4, 'Informatik'),
(10, 4, 'Medieninformatik'),
(11, 5, 'Physik'),
(12, 5, 'Informatik'),
(13, 5, 'Elektrotechnik'),
(14, 5, 'Informationssystemtechnik'),
(15, 6, 'Sonstiges'),
(16, 3, 'Biologie'),
(17, 3, 'Medizin'),
(18, 7, 'Informatik'),
(19, 7, 'Medieninformatik'),
(20, 7, 'Softwareengeneering'),
(21, 8, 'Informatik'),
(22, 8, 'Medieninformatik');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `benachrichtigung`
--
ALTER TABLE `benachrichtigung`
 ADD PRIMARY KEY (`ID`);

--
-- Indizes für die Tabelle `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Benachrichtigung` (`Benachrichtigung`);

--
-- Indizes für die Tabelle `benachrichtigung_karteikartenaenderung`
--
ALTER TABLE `benachrichtigung_karteikartenaenderung`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benachrichtigung` (`Benachrichtigung`), ADD KEY `Benutzer` (`Benutzer`);

--
-- Indizes für die Tabelle `benachrichtigung_neuer_kommentar`
--
ALTER TABLE `benachrichtigung_neuer_kommentar`
 ADD PRIMARY KEY (`ID`), ADD KEY `Kommentar` (`Kommentar`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Benachrichtigung` (`Benachrichtigung`);

--
-- Indizes für die Tabelle `benachrichtigung_profil_geaendert`
--
ALTER TABLE `benachrichtigung_profil_geaendert`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benachrichtigung` (`Benachrichtigung`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Admin` (`Admin`);

--
-- Indizes für die Tabelle `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
 ADD PRIMARY KEY (`ID`), ADD KEY `VeranstaltungID` (`Veranstaltung`), ADD KEY `Benachrichtigung` (`Benachrichtigung`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Benutzer` (`Benutzer`);

--
-- Indizes für die Tabelle `benutzer`
--
ALTER TABLE `benutzer`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `eMail` (`eMail`), ADD KEY `Studiengang` (`Studiengang`);

--
-- Indizes für die Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `Benutzer` (`Benutzer`,`Veranstaltung`), ADD UNIQUE KEY `Benutzer_2` (`Benutzer`,`Veranstaltung`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `VeranstaltungID` (`Veranstaltung`), ADD KEY `VeranstaltungID_2` (`Veranstaltung`);

--
-- Indizes für die Tabelle `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
 ADD PRIMARY KEY (`ID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `KarteikarteID` (`KarteikarteID`);

--
-- Indizes für die Tabelle `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
 ADD PRIMARY KEY (`ID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `KommentarID` (`KommentarID`);

--
-- Indizes für die Tabelle `karteikarte`
--
ALTER TABLE `karteikarte`
 ADD PRIMARY KEY (`ID`), ADD KEY `VeranstaltungID` (`Veranstaltung`);

--
-- Indizes für die Tabelle `karteikarten_struktur`
--
ALTER TABLE `karteikarten_struktur`
 ADD PRIMARY KEY (`ID`), ADD KEY `SohnKarteik` (`SohnKarteik`), ADD KEY `VaterKarteik` (`VaterKarteik`);

--
-- Indizes für die Tabelle `kommentar`
--
ALTER TABLE `kommentar`
 ADD PRIMARY KEY (`ID`), ADD KEY `ErstellerID` (`Benutzer`), ADD KEY `DiskussionID` (`KarteikarteID`), ADD KEY `VaterkommentarID` (`VaterkommentarID`);

--
-- Indizes für die Tabelle `moderator`
--
ALTER TABLE `moderator`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Veranstaltung` (`Veranstaltung`);

--
-- Indizes für die Tabelle `notiz`
--
ALTER TABLE `notiz`
 ADD PRIMARY KEY (`ID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `KarteikarteID` (`KarteikarteID`);

--
-- Indizes für die Tabelle `semester`
--
ALTER TABLE `semester`
 ADD PRIMARY KEY (`Name`);

--
-- Indizes für die Tabelle `studiengang`
--
ALTER TABLE `studiengang`
 ADD PRIMARY KEY (`Name`);

--
-- Indizes für die Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `Semester` (`Semester`,`Titel`), ADD KEY `Ersteller` (`Ersteller`), ADD KEY `Semester_2` (`Semester`);

--
-- Indizes für die Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
 ADD PRIMARY KEY (`ID`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Studiengang` (`Studiengang`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `benachrichtigung`
--
ALTER TABLE `benachrichtigung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=41;
--
-- AUTO_INCREMENT für Tabelle `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `benachrichtigung_karteikartenaenderung`
--
ALTER TABLE `benachrichtigung_karteikartenaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `benachrichtigung_neuer_kommentar`
--
ALTER TABLE `benachrichtigung_neuer_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `benachrichtigung_profil_geaendert`
--
ALTER TABLE `benachrichtigung_profil_geaendert`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT für Tabelle `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT für Tabelle `benutzer`
--
ALTER TABLE `benutzer`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT für Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=13;
--
-- AUTO_INCREMENT für Tabelle `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `karteikarte`
--
ALTER TABLE `karteikarte`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `karteikarten_struktur`
--
ALTER TABLE `karteikarten_struktur`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `kommentar`
--
ALTER TABLE `kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `moderator`
--
ALTER TABLE `moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT für Tabelle `notiz`
--
ALTER TABLE `notiz`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT für Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=23;
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
ADD CONSTRAINT `benutzer_ibfk_1` FOREIGN KEY (`Studiengang`) REFERENCES `studiengang` (`Name`);

--
-- Constraints der Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
ADD CONSTRAINT `benutzer_veranstaltung_zuordnung_ibfk_3` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

--
-- Constraints der Tabelle `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
ADD CONSTRAINT `bewertung_karteikarte_ibfk_2` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`);

--
-- Constraints der Tabelle `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
ADD CONSTRAINT `bewertung_kommentar_ibfk_2` FOREIGN KEY (`KommentarID`) REFERENCES `kommentar` (`ID`);

--
-- Constraints der Tabelle `karteikarte`
--
ALTER TABLE `karteikarte`
ADD CONSTRAINT `karteikarte_ibfk_1` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

--
-- Constraints der Tabelle `karteikarten_struktur`
--
ALTER TABLE `karteikarten_struktur`
ADD CONSTRAINT `karteikarten_struktur_ibfk_1` FOREIGN KEY (`SohnKarteik`) REFERENCES `karteikarte` (`ID`),
ADD CONSTRAINT `karteikarten_struktur_ibfk_2` FOREIGN KEY (`VaterKarteik`) REFERENCES `karteikarte` (`ID`);

--
-- Constraints der Tabelle `kommentar`
--
ALTER TABLE `kommentar`
ADD CONSTRAINT `kommentar_ibfk_4` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`),
ADD CONSTRAINT `kommentar_ibfk_5` FOREIGN KEY (`VaterkommentarID`) REFERENCES `kommentar` (`ID`) ON DELETE CASCADE;

--
-- Constraints der Tabelle `moderator`
--
ALTER TABLE `moderator`
ADD CONSTRAINT `moderator_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`),
ADD CONSTRAINT `moderator_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`ID`);

--
-- Constraints der Tabelle `notiz`
--
ALTER TABLE `notiz`
ADD CONSTRAINT `notiz_ibfk_2` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`);

--
-- Constraints der Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
ADD CONSTRAINT `veranstaltung_ibfk_1` FOREIGN KEY (`Semester`) REFERENCES `semester` (`Name`);

--
-- Constraints der Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
ADD CONSTRAINT `veranstaltung_studiengang_zuordnung_ibfk_1` FOREIGN KEY (`Studiengang`) REFERENCES `studiengang` (`Name`),
ADD CONSTRAINT `veranstaltung_studiengang_zuordnung_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
