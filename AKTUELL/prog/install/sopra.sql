-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 22. Mai 2015 um 19:03
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=77 ;

--
-- Daten für Tabelle `benachrichtigung`
--

INSERT INTO `benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`) VALUES
(1, 'Einladung als Moderator für die Vorlesung Softwaretechnik', '2015-04-22 14:15:42'),
(2, 'Einladung als Moderator für die Vorlesung Algorithmen und Datenstrukturen', '2015-04-22 14:15:47'),
(3, 'Prüfungstermine sind nun in der Beschreibung bekannt gegeben', '2015-04-22 14:15:53'),
(40, '', '2015-03-28 22:39:30'),
(41, 'Sie werden zur Veranstaltung test als Moderator eingeladen', '2015-05-03 08:56:44'),
(42, 'Sie werden zur Veranstaltung qwerwqer als Moderator eingeladen', '2015-05-18 12:41:35'),
(43, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 05:57:20'),
(44, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 05:59:59'),
(45, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 06:00:47'),
(46, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:15:56'),
(47, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:16:08'),
(48, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:16:22'),
(49, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:17:24'),
(50, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:18:29'),
(51, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:19:01'),
(53, 'Die Veranstaltung asdfsadf wurde bearbeitet.', '2015-05-19 14:21:50'),
(54, 'Die Veranstaltung  wurde bearbeitet.', '2015-05-19 14:23:49'),
(55, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:24:45'),
(56, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:27:44'),
(57, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:28:11'),
(58, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:28:46'),
(59, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:29:09'),
(60, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:30:43'),
(61, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:38:39'),
(62, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:38:54'),
(64, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:41:55'),
(65, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:42:06'),
(66, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:42:46'),
(67, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:43:57'),
(68, 'Die Veranstaltung  wurde bearbeitet.', '2015-05-19 14:45:25'),
(69, 'Die Veranstaltung ??yxcvxycv wurde bearbeitet.', '2015-05-19 14:46:56'),
(70, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-19 14:47:44'),
(71, 'Die Veranstaltung   wurde bearbeitet.', '2015-05-19 14:48:28'),
(72, 'Die Veranstaltung  ???????dasfsdf wurde bearbeitet.', '2015-05-19 14:48:51'),
(73, 'Die Veranstaltung  ???????dasfsdf wurde bearbeitet.', '2015-05-19 14:49:03'),
(74, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-20 14:00:12'),
(75, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-20 14:01:58'),
(76, 'Die Veranstaltung ???sdfsadfasdf wurde bearbeitet.', '2015-05-20 14:02:53');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `benachrichtigung_einladung_moderator`
--

INSERT INTO `benachrichtigung_einladung_moderator` (`ID`, `Benachrichtigung`, `Benutzer`, `Veranstaltung`, `Gelesen`, `Angenommen`) VALUES
(1, 1, 4, 1, 1, 0),
(2, 2, 6, 4, 1, 0);

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

--
-- Daten für Tabelle `benachrichtigung_profil_geaendert`
--

INSERT INTO `benachrichtigung_profil_geaendert` (`ID`, `Benachrichtigung`, `Benutzer`, `Admin`, `Gelesen`) VALUES
(3, 40, 6, 2, 1);

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

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
  `Nutzerstatus` enum('STUDENT','DOZENT','ADMIN','') NOT NULL DEFAULT 'STUDENT',
  `NotifyKommentare` enum('KEINE','VERANSTALTUNG_TEILGENOMMEN','DISKUSSION_TEILGENOMMEN','') NOT NULL DEFAULT 'KEINE',
  `NotifyVeranstAenderung` tinyint(1) NOT NULL DEFAULT '0',
  `NotifyKarteikartenAenderung` tinyint(1) NOT NULL DEFAULT '0',
  `CryptedPW` varchar(60) NOT NULL COMMENT 'hash = salt(29 Zeichen) + crypted (Rest)'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=14 ;

--
-- Daten für Tabelle `benutzer`
--

INSERT INTO `benutzer` (`ID`, `eMail`, `Vorname`, `Nachname`, `Profilbild`, `Matrikelnummer`, `Studiengang`, `Nutzerstatus`, `NotifyKommentare`, `NotifyVeranstAenderung`, `NotifyKarteikartenAenderung`, `CryptedPW`) VALUES
(1, 'abc@def.de', 'abc', 'def', 'default.png', 123, 'Informatik', 'STUDENT', 'KEINE', 0, 0, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(2, 'admin@blablub.de', 'Der', 'Admin', 'default.png', 222222, 'Sonstiges', 'ADMIN', 'VERANSTALTUNG_TEILGENOMMEN', 1, 1, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(3, 'alber.einstein@uni-ulm.de', 'Albert', 'Einstein', 'default.png', 333333, 'Physik', 'DOZENT', 'KEINE', 0, 0, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(4, 'andreas.rottach@uni-ulm.de', 'Andreas', 'Rottach', 'default.png', 12345, 'Informatik', 'DOZENT', 'DISKUSSION_TEILGENOMMEN', 0, 1, '$2a$10$M99vE1vmcU6eJy4qKpYOrOWJK4ba9bAfljcV8FJGaxM8EltiPPvge'),
(5, 'marius.kircher@uni-ulm.de', 'Marius', 'Kircher', 'default.png', 111111, 'Medieninformatik', 'STUDENT', 'VERANSTALTUNG_TEILGENOMMEN', 1, 0, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(6, 'matthias.englert@uni-ulm.de', 'Matthias', 'Englert', 'default.png', 828584, 'Informatik', 'STUDENT', 'KEINE', 0, 0, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(7, 'heinz.harald@uni-ulm.de', 'Heinz Harald', 'Haraldson', 'default.png', 123456, 'Chemie', 'STUDENT', 'KEINE', 0, 0, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(8, 'julius.friedrich@uni-ulm.de', 'Julius', 'Friedrich', 'default.png', 841963, 'Informatik', 'ADMIN', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(12, 'jhdazw3jio@euhw.de', 'ejwahuwdi', 'juwia', 'default.png', 187372, 'Biologie', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK'),
(13, 'felixrottler@gmx.de', 'Felix', 'Rottler', 'default.png', 193182, 'Medieninformatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$RRbFAzRfSguYO/GWHVOTT.dA7wnAIKXyKCwDD0gDWQlnvEArGWspK');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer_veranstaltung_zuordnung`
--

CREATE TABLE IF NOT EXISTS `benutzer_veranstaltung_zuordnung` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=23 ;

--
-- Daten für Tabelle `benutzer_veranstaltung_zuordnung`
--

INSERT INTO `benutzer_veranstaltung_zuordnung` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(4, 1, 2),
(7, 1, 4),
(9, 2, 3),
(16, 2, 7),
(5, 3, 5),
(1, 4, 1),
(8, 4, 6),
(2, 5, 1),
(3, 6, 1),
(10, 6, 2),
(6, 6, 4),
(12, 6, 7),
(15, 8, 1),
(14, 8, 5),
(13, 8, 7);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_karteikarte`
--

CREATE TABLE IF NOT EXISTS `bewertung_karteikarte` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=74 ;

--
-- Daten für Tabelle `bewertung_karteikarte`
--

INSERT INTO `bewertung_karteikarte` (`ID`, `Bewertung`, `Benutzer`, `KarteikarteID`) VALUES
(1, 1, 1, 0),
(4, 1, 2, 0),
(6, -1, 1, 1),
(9, 1, 1, 2),
(10, 1, 1, 4),
(11, 1, 1, 5),
(13, 1, 4, 5),
(18, 1, 7, 5),
(23, 1, 13, 5),
(26, 1, 4, 6),
(27, 1, 7, 6),
(30, 1, 8, 6),
(31, 1, 2, 6),
(32, 1, 6, 6),
(33, 2, 8, 7),
(38, 2, 7, 7),
(41, 2, 6, 8),
(43, 2, 8, 8),
(49, 2, 5, 9),
(50, 2, 6, 9),
(52, 2, 8, 9),
(54, 2, 4, 9),
(55, 2, 1, 9),
(56, 2, 3, 9),
(58, 2, 8, 10),
(59, 2, 2, 10),
(60, 2, 7, 10),
(61, 2, 3, 10),
(62, 2, 6, 10),
(63, 2, 4, 10),
(65, 1, 4, 26),
(66, -1, 4, 21),
(67, 1, 4, 22),
(68, 1, 4, 23),
(69, 1, 4, 25),
(70, 1, 4, 20),
(71, 1, 4, 24),
(72, -1, 4, 2);

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

--
-- Daten für Tabelle `bewertung_kommentar`
--

INSERT INTO `bewertung_kommentar` (`ID`, `Bewertung`, `Benutzer`, `KommentarID`) VALUES
(1, 1, 5, 1),
(2, -1, 3, 2),
(3, 1, 3, 1),
(4, -5, 1, 2),
(5, 1, 4, 4),
(6, 1, 2, 4),
(7, -1, 4, 8),
(8, -1, 4, 9),
(10, 1, 4, 3),
(12, 1, 4, 16),
(13, -1, 4, 14),
(14, 1, 4, 17),
(15, 1, 4, 19),
(16, -1, 4, 22);

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
(0, 'Softwaretechnik I', '', 'TEXT', 2, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(1, 'Einführung und Motivation', '', 'TEXT', -1, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(2, 'Grundlagen', '', 'TEXT', 0, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(3, 'System-Engineering', '', 'TEXT', 0, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(4, 'Organisatorisches', '', 'TEXT', 1, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(5, 'Bedeutung von Software', '', 'TEXT', 4, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(6, 'Software-Probleme, "Software-Krise"', '', 'TEXT', 5, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(7, 'Software als Wirtschaftsfaktor', '', 'TEXT', 4, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(8, 'Grundbegriffe', '', 'TEXT', 4, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(9, 'Übungsaufgaben', '', 'TEXT', 12, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(10, 'Einfache Übungsaufgaben', '', 'TEXT', 12, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(11, 'Mittelschwere Übungsaufgaben', '', 'TEXT', 0, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(12, 'Schwierige Übungsaufgaben', '', 'TEXT', 0, '2015-04-30 22:00:00', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(13, 'Software Engineering – gestern und heute', '......', 'TEXT', 0, '2015-05-20 15:11:47', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(14, 'Software Engineering – morgen', '.........', 'TEXT', 0, '2015-05-20 15:12:16', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(15, 'Modelle und Modellierung', '..........', 'TEXT', 0, '2015-05-20 15:14:09', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(16, 'System', '..........', 'TEXT', 0, '2015-05-20 15:15:18', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(17, 'Bedeutung einiger Folienmarkierungen', '..........', 'TEXT', 0, '2015-05-20 15:17:38', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(18, 'Hauptsächliche Hintergrund-Literatur', '..........', 'TEXT', 0, '2015-05-20 15:18:28', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(19, 'Weitere Literatur I', '.......', 'TEXT', 0, '2015-05-20 15:19:04', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(20, 'Test1', '', 'BILD', -8, '2015-05-11 12:23:39', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(21, 'Test2', '', 'VIDEO', -4, '2015-05-11 12:25:28', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(22, 'Test3', 'dies ist ein beispielinhalt von test3', 'TEXT', -2, '2015-05-11 12:25:28', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(23, 'Test4', 'dies ist ein beispielinhalt von test4', 'TEXT', -2, '2015-05-11 12:25:28', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(24, 'Test5', 'dies ist ein beispielinhalt von test5', 'TEXT', -2, '2015-05-11 12:25:28', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(25, 'Test6', 'dies ist ein beispielinhalt von test6', 'TEXT', -2, '2015-05-11 12:25:28', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(26, 'test7', 'dies ist ein beispielinhalt von test7', 'TEXT', -2, '2015-05-17 15:09:38', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(27, 'Charakteristika der Software-Erstellung', '....', 'TEXT', 0, '2015-05-20 15:22:19', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

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

--
-- Daten für Tabelle `kommentar`
--

INSERT INTO `kommentar` (`ID`, `Inhalt`, `Erstelldatum`, `Benutzer`, `Karteikarte`, `Vaterkommentar`) VALUES
(1, 'sdfgsdfgsdfgdsfgsdfgsdfg', '2015-05-11 12:49:50', 3, 2, NULL),
(2, 'asdfasdfasdfasdf', '2015-05-11 12:50:14', 5, NULL, 1),
(3, 'ewr', '2015-05-11 13:44:12', 5, 2, NULL),
(4, 'Das ist aber ein interessantes Thema ! :o', '2015-05-12 17:45:09', 4, 26, NULL),
(5, 'Ja finde ich auch !', '2015-05-12 17:45:32', 2, NULL, 4),
(6, 'Haha !', '2015-05-12 17:45:41', 4, NULL, 4),
(7, 'Was gibts denn da zu lachen ! -.-', '2015-05-12 17:46:58', 2, NULL, 4),
(8, 'Ich wollte nur mal Hallo sagen !', '2015-05-12 17:48:04', 4, 21, NULL),
(9, '<p>Was f&uuml;r ein m&uuml;ll!</p>\n', '2015-05-13 20:13:24', 4, 26, NULL),
(14, '<p>aSDs</p>\n', '2015-05-19 08:04:44', 4, 6, NULL),
(16, '<p>asdsdf</p>\n', '2015-05-19 08:05:09', 4, 6, NULL),
(17, '<p>asdf</p>\n', '2015-05-19 16:10:25', 4, 5, NULL),
(18, '<p>sdaf</p>\n', '2015-05-19 16:10:43', 4, NULL, 16),
(19, '<p>dgdh</p>\n', '2015-05-20 17:31:31', 4, 19, NULL),
(20, '<p>fhgh</p>\n', '2015-05-20 17:31:34', 4, NULL, 19),
(21, '<p>ghdgh</p>\n', '2015-05-20 17:31:35', 4, NULL, 19),
(22, '<p>fghj</p>\n', '2015-05-20 17:31:43', 4, 19, NULL);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Daten für Tabelle `notiz`
--

INSERT INTO `notiz` (`ID`, `Inhalt`, `Benutzer`, `KarteikarteID`) VALUES
(2, '<p>Das muss ich mir gut merken !!</p>\n', 4, 21),
(3, '<p>Das muss ich mir gut merken !!</p>\n', 4, 26),
(4, '<p>Das muss ich mir gut merken !!</p>\n', 4, 23),
(5, '<p>Das muss ich mir gut merken !!</p>\n', 4, 22),
(6, '<p>sdfgsdfgdsfg</p>\n', 4, 4);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `semester`
--

CREATE TABLE IF NOT EXISTS `semester` (
`ID` int(11) NOT NULL,
  `Name` varchar(30) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

--
-- Daten für Tabelle `semester`
--

INSERT INTO `semester` (`ID`, `Name`) VALUES
(1, 'SoSe2013'),
(2, 'WiSe2013/14'),
(3, 'SoSe2014'),
(4, 'WiSe2014/15'),
(5, 'SoSe2015'),
(6, 'WiSe2015/16'),
(7, 'SoSe2016'),
(8, 'WiSe2016/17'),
(9, 'SoSe2017');

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
  `Titel` varchar(255) NOT NULL,
  `ErsteKarteikarte` int(11) DEFAULT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

--
-- Daten für Tabelle `veranstaltung`
--

INSERT INTO `veranstaltung` (`ID`, `Beschreibung`, `Semester`, `Kennwort`, `KommentareErlaubt`, `BewertungenErlaubt`, `ModeratorKarteikartenBearbeiten`, `Ersteller`, `Titel`, `ErsteKarteikarte`) VALUES
(1, 'Softwaretechnik Vorlesung', 'WiSe2014/15', '1234', 1, 1, 1, 5, 'Softwaretechnik', 0),
(2, 'Softwaretechnik Vorlesung', 'WiSe2015/16', '1234', 1, 1, 1, 4, 'Softwaretechnik', 0),
(3, 'Medizin I', 'SoSe2013', '1234', 1, 0, 0, 2, 'Medizin I', 0),
(4, 'Algorithmen und Datenstrukturen Vorlesung', 'WiSe2014/15', '1234', 1, 0, 1, 6, 'Algorithmen und Datenstrukture', 0),
(5, 'Physik I für Ingenieure Vorlesung', 'SoSe2015', '1234', 1, 0, 0, 3, 'Physik I für Ingenieure', 0),
(6, 'Latex Kurs', 'WiSe2014/15', '1234', 1, 1, 1, 2, 'Latex Kurs', 0),
(7, 'Softwaregrundproekt', 'SoSe2015', NULL, 1, 1, 1, 6, 'Sopra', 0),
(8, 'Grundlagen der Betriebssysteme Vorlesung', 'WiSe2014/15', NULL, 1, 0, 0, 5, 'Grundlagen der Betriebssysteme', 0),
(9, 'nur für pimmelberger', 'SoSe2015', 'pimmelberger', 1, 1, 1, 8, 'Digitale Medien', 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung_studiengang_zuordnung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung_studiengang_zuordnung` (
`ID` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=63 ;

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
(22, 8, 'Medieninformatik'),
(23, 9, 'Medieninformatik');

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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=77;
--
-- AUTO_INCREMENT for table `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `benutzer`
--
ALTER TABLE `benutzer`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=14;
--
-- AUTO_INCREMENT for table `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=23;
--
-- AUTO_INCREMENT for table `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=74;
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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `notiz`
--
ALTER TABLE `notiz`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `semester`
--
ALTER TABLE `semester`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `veranstaltung`
--
ALTER TABLE `veranstaltung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=16;
--
-- AUTO_INCREMENT for table `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=63;
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
