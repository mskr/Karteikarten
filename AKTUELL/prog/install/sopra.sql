-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 24. Mrz 2015 um 16:49
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

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer`
--

CREATE TABLE IF NOT EXISTS `benutzer` (
  `eMail` varchar(30) NOT NULL,
  `Vorname` varchar(30) NOT NULL,
  `Nachname` varchar(30) NOT NULL,
  `Matrikelnummer` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL,
  `Kennwort` varchar(30) NOT NULL,
  `Nutzerstatus` enum('STUDENT','DOZENT','ADMIN','') NOT NULL,
  `NotifyKommentare` enum('KEINE','VERANSTALTUNG_TEILGENOMMEN','DISKUSSION_TEILGENOMMEN','') NOT NULL,
  `NotifyVeranstAenderung` tinyint(1) NOT NULL,
  `NotifyKarteikartenAenderung` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benutzer`
--

INSERT INTO `benutzer` (`eMail`, `Vorname`, `Nachname`, `Matrikelnummer`, `Studiengang`, `Kennwort`, `Nutzerstatus`, `NotifyKommentare`, `NotifyVeranstAenderung`, `NotifyKarteikartenAenderung`) VALUES
('abc@def.de', 'abc', 'def', 123, 'Informatik', '1234', 'STUDENT', 'KEINE', 0, 0),
('admin@blablub.de', 'Der', 'Admin', 222222, 'Sonstiges', '1234', 'ADMIN', 'VERANSTALTUNG_TEILGENOMMEN', 1, 1),
('alber.einstein@uni-ulm.de', 'Albert', 'Einstein', 333333, 'Physik', '1234', 'DOZENT', 'KEINE', 0, 0),
('andreas.rottach@uni-ulm.de', 'Andreas', 'Rottach', 12345, 'Informatik', '1234', 'DOZENT', 'DISKUSSION_TEILGENOMMEN', 1, 1),
('marius.kircher@uni-ulm.de', 'Marius', 'Kircher', 111111, 'Medieninformatik', '1234', 'STUDENT', 'VERANSTALTUNG_TEILGENOMMEN', 1, 0),
('matthias.englert@uni-ulm.de', 'Matthias', 'Englert', 828584, 'Informatik', '1234', 'STUDENT', 'KEINE', 0, 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer_veranstaltung_zuordnung`
--

CREATE TABLE IF NOT EXISTS `benutzer_veranstaltung_zuordnung` (
`ID` int(11) NOT NULL,
  `Benutzer` varchar(30) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benutzer_veranstaltung_zuordnung`
--

INSERT INTO `benutzer_veranstaltung_zuordnung` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(1, 'marius.kircher@uni-ulm.de', 1),
(2, 'matthias.englert@uni-ulm.de', 1),
(3, 'andreas.rottach@uni-ulm.de', 1),
(4, 'abc@def.de', 2),
(5, 'alber.einstein@uni-ulm.de', 5),
(6, 'marius.kircher@uni-ulm.de', 4),
(7, 'matthias.englert@uni-ulm.de', 4),
(9, 'andreas.rottach@uni-ulm.de', 6),
(10, 'admin@blablub.de', 3);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_karteikarte`
--

CREATE TABLE IF NOT EXISTS `bewertung_karteikarte` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` varchar(30) NOT NULL,
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
-- Tabellenstruktur für Tabelle `einladung_moderator_benachrichtigung`
--

CREATE TABLE IF NOT EXISTS `einladung_moderator_benachrichtigung` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Erstelldatum` date NOT NULL,
  `Benutzer` varchar(30) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `einladung_moderator_benachrichtigung`
--

INSERT INTO `einladung_moderator_benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`, `Benutzer`, `Veranstaltung`) VALUES
(1, 'Einladung als Moderator für die Vorlesung Softwaretechnik', '2015-03-21', 'andreas.rottach@uni-ulm.de', 1),
(2, 'Einladung als Moderator für die Vorlesung Algorithmen und Datenstrukturen', '2015-03-03', 'matthias.englert@uni-ulm.de', 4);

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
  `Benutzer` varchar(30) NOT NULL,
  `KarteikarteID` int(11) NOT NULL,
  `VaterkommentarID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `moderator`
--

CREATE TABLE IF NOT EXISTS `moderator` (
`ID` int(11) NOT NULL,
  `Benutzer` varchar(30) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `moderator`
--

INSERT INTO `moderator` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(1, 'andreas.rottach@uni-ulm.de', 1),
(2, 'matthias.englert@uni-ulm.de', 4);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `notiz`
--

CREATE TABLE IF NOT EXISTS `notiz` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Benutzer` varchar(30) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `Kennwort` varchar(30) NOT NULL,
  `KommentareErlaubt` tinyint(1) NOT NULL,
  `BewertungenErlaubt` tinyint(1) NOT NULL,
  `ModeratorKarteikartenBearbeiten` tinyint(1) NOT NULL,
  `Ersteller` varchar(30) NOT NULL,
  `Titel` varchar(50) NOT NULL,
  `ErsteKarteik` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `veranstaltung`
--

INSERT INTO `veranstaltung` (`ID`, `Beschreibung`, `Semester`, `Kennwort`, `KommentareErlaubt`, `BewertungenErlaubt`, `ModeratorKarteikartenBearbeiten`, `Ersteller`, `Titel`, `ErsteKarteik`) VALUES
(1, 'Softwaretechnik Vorlesung', 'WiSe2014/15', '1234', 1, 1, 1, 'matthias.englert@uni-ulm.de', 'Softwaretechnik', 1),
(2, 'Softwaretechnik Vorlesung', 'WiSe2015/16', '1234', 1, 1, 1, 'matthias.englert@uni-ulm.de', 'Softwaretechnik', 1),
(3, 'Medizin I', 'SoSe2013', '1234', 1, 0, 0, 'admin@blablub.de', 'Medizin I', 30),
(4, 'Algorithmen und Datenstrukturen Vorlesung', 'WiSe2014/15', '1234', 1, 0, 1, 'abc@def.de', 'Algorithmen und Datenstrukture', 2),
(5, 'Physik I für Ingenieure Vorlesung', 'SoSe2015', '1234', 1, 0, 0, 'alber.einstein@uni-ulm.de', 'Physik I für Ingenieure', 4),
(6, 'Latex Kurs', 'WiSe2014/15', '1234', 1, 1, 1, 'admin@blablub.de', 'Latex Kurs', 15);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltungs_benachrichtigung`
--

CREATE TABLE IF NOT EXISTS `veranstaltungs_benachrichtigung` (
`ID` int(11) NOT NULL,
  `Inhalt` text NOT NULL,
  `Erstelldatum` date NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `veranstaltungs_benachrichtigung`
--

INSERT INTO `veranstaltungs_benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`, `Veranstaltung`) VALUES
(1, 'Prüfungstermine sind nun in der Beschreibung bekannt gegeben', '2015-03-11', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung_studiengang_zuordnung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung_studiengang_zuordnung` (
`ID` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

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
(17, 3, 'Medizin');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `benutzer`
--
ALTER TABLE `benutzer`
 ADD PRIMARY KEY (`eMail`), ADD KEY `Studiengang` (`Studiengang`);

--
-- Indizes für die Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
 ADD PRIMARY KEY (`ID`), ADD KEY `BenutzerID` (`Benutzer`), ADD KEY `VeranstaltungID` (`Veranstaltung`), ADD KEY `VeranstaltungID_2` (`Veranstaltung`);

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
-- Indizes für die Tabelle `einladung_moderator_benachrichtigung`
--
ALTER TABLE `einladung_moderator_benachrichtigung`
 ADD PRIMARY KEY (`ID`), ADD KEY `Benutzer` (`Benutzer`), ADD KEY `Veranstaltung` (`Veranstaltung`);

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
-- Indizes für die Tabelle `studiengang`
--
ALTER TABLE `studiengang`
 ADD PRIMARY KEY (`Name`);

--
-- Indizes für die Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `Semester` (`Semester`,`Titel`), ADD KEY `Ersteller` (`Ersteller`), ADD KEY `ErsteKarteik` (`ErsteKarteik`);

--
-- Indizes für die Tabelle `veranstaltungs_benachrichtigung`
--
ALTER TABLE `veranstaltungs_benachrichtigung`
 ADD PRIMARY KEY (`ID`), ADD KEY `VeranstaltungID` (`Veranstaltung`);

--
-- Indizes für die Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
 ADD PRIMARY KEY (`ID`), ADD KEY `Veranstaltung` (`Veranstaltung`), ADD KEY `Studiengang` (`Studiengang`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=11;
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
-- AUTO_INCREMENT für Tabelle `einladung_moderator_benachrichtigung`
--
ALTER TABLE `einladung_moderator_benachrichtigung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT für Tabelle `notiz`
--
ALTER TABLE `notiz`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT für Tabelle `veranstaltungs_benachrichtigung`
--
ALTER TABLE `veranstaltungs_benachrichtigung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT für Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=18;
--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `benutzer`
--
ALTER TABLE `benutzer`
ADD CONSTRAINT `benutzer_ibfk_1` FOREIGN KEY (`Studiengang`) REFERENCES `studiengang` (`Name`);

--
-- Constraints der Tabelle `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
ADD CONSTRAINT `benutzer_veranstaltung_zuordnung_ibfk_2` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`) ON UPDATE CASCADE,
ADD CONSTRAINT `benutzer_veranstaltung_zuordnung_ibfk_3` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

--
-- Constraints der Tabelle `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
ADD CONSTRAINT `bewertung_karteikarte_ibfk_2` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`),
ADD CONSTRAINT `bewertung_karteikarte_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`) ON UPDATE CASCADE;

--
-- Constraints der Tabelle `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
ADD CONSTRAINT `bewertung_kommentar_ibfk_2` FOREIGN KEY (`KommentarID`) REFERENCES `kommentar` (`ID`),
ADD CONSTRAINT `bewertung_kommentar_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`);

--
-- Constraints der Tabelle `einladung_moderator_benachrichtigung`
--
ALTER TABLE `einladung_moderator_benachrichtigung`
ADD CONSTRAINT `einladung_moderator_benachrichtigung_ibfk_1` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`) ON UPDATE CASCADE,
ADD CONSTRAINT `einladung_moderator_benachrichtigung_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

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
ADD CONSTRAINT `kommentar_ibfk_5` FOREIGN KEY (`VaterkommentarID`) REFERENCES `kommentar` (`ID`) ON DELETE CASCADE,
ADD CONSTRAINT `kommentar_ibfk_6` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`);

--
-- Constraints der Tabelle `moderator`
--
ALTER TABLE `moderator`
ADD CONSTRAINT `moderator_ibfk_1` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`) ON UPDATE CASCADE,
ADD CONSTRAINT `moderator_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

--
-- Constraints der Tabelle `notiz`
--
ALTER TABLE `notiz`
ADD CONSTRAINT `notiz_ibfk_2` FOREIGN KEY (`KarteikarteID`) REFERENCES `karteikarte` (`ID`),
ADD CONSTRAINT `notiz_ibfk_3` FOREIGN KEY (`Benutzer`) REFERENCES `benutzer` (`eMail`) ON UPDATE CASCADE;

--
-- Constraints der Tabelle `veranstaltung`
--
ALTER TABLE `veranstaltung`
ADD CONSTRAINT `veranstaltung_ibfk_1` FOREIGN KEY (`Ersteller`) REFERENCES `benutzer` (`eMail`) ON UPDATE CASCADE;

--
-- Constraints der Tabelle `veranstaltungs_benachrichtigung`
--
ALTER TABLE `veranstaltungs_benachrichtigung`
ADD CONSTRAINT `veranstaltungs_benachrichtigung_ibfk_1` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

--
-- Constraints der Tabelle `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
ADD CONSTRAINT `veranstaltung_studiengang_zuordnung_ibfk_1` FOREIGN KEY (`Studiengang`) REFERENCES `studiengang` (`Name`),
ADD CONSTRAINT `veranstaltung_studiengang_zuordnung_ibfk_2` FOREIGN KEY (`Veranstaltung`) REFERENCES `veranstaltung` (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
