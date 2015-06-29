-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 29. Jun 2015 um 13:11
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=28 ;

--
-- Daten für Tabelle `benachrichtigung`
--

INSERT INTO `benachrichtigung` (`ID`, `Inhalt`, `Erstelldatum`) VALUES
(1, 'Ihr Profil wurde geändert!', '2015-06-04 13:17:57'),
(2, 'Sie wurden zur Veranstaltung Softwaretechnik II als Moderator hinzugefügt!', '2015-06-04 13:21:47'),
(3, 'Die Veranstaltung Softwaretechnik II wurde bearbeitet.', '2015-06-04 13:29:03'),
(4, 'Es wurde eine neuer Kommentar zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-08 08:07:16'),
(5, 'Es wurde eine neuer Kommentar zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-08 08:15:56'),
(6, 'Es wurde eine neuer Kommentar zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-08 08:19:42'),
(7, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-08 09:39:51'),
(8, 'Sie wurden zur Veranstaltung Bedienungsanleitung eLearning-System als Moderator hinzugefügt!', '2015-06-09 07:12:00'),
(9, 'Die Veranstaltung Bedienungsanleitung eLearning-System wurde bearbeitet.', '2015-06-09 07:12:00'),
(10, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-12 07:51:48'),
(11, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-12 07:52:07'),
(12, 'Die Veranstaltung Bedienungsanleitung eLearning-System wurde bearbeitet.', '2015-06-12 07:56:06'),
(13, 'Die Veranstaltung Bedienungsanleitung eLearning-System wurde bearbeitet.', '2015-06-12 07:56:39'),
(14, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Qualität" verfasst.', '2015-06-12 11:46:28'),
(15, 'Die Veranstaltung Bedienungsanleitung eLearning-System wurde bearbeitet.', '2015-06-12 15:02:51'),
(16, 'Sie wurden zur Veranstaltung fsef als Moderator hinzugefügt!', '2015-06-15 12:05:34'),
(17, 'Sie wurden zur Veranstaltung esrdhtfjzgfhtdegrse als Moderator hinzugefügt!', '2015-06-15 12:13:43'),
(18, 'Die Karteikarte Export wurde bearbeitet.', '2015-06-22 06:50:48'),
(19, 'Die Karteikarte Export wurde bearbeitet.', '2015-06-22 07:04:19'),
(20, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Qualitätssicherung – Test" verfasst.', '2015-06-22 07:05:29'),
(21, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Qualitätssicherung – Test" verfasst.', '2015-06-22 07:05:39'),
(22, 'Die Karteikarte Profildaten bearbeiten wurde bearbeitet.', '2015-06-22 07:28:13'),
(23, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-25 10:23:45'),
(24, 'Sie wurden zur Veranstaltung Softwaretechnik II als Moderator hinzugefügt!', '2015-06-25 10:28:10'),
(25, 'Die Veranstaltung Softwaretechnik II wurde bearbeitet.', '2015-06-25 10:28:10'),
(26, 'Die Veranstaltung Softwaretechnik II wurde bearbeitet.', '2015-06-25 10:31:34'),
(27, 'Es wurde eine neuer Kommentar in der Veranstaltung "Softwaretechnik II" zur Karteikarte "Hintergrund und Motivation" verfasst.', '2015-06-29 08:54:50');

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `benachrichtigung_einladung_moderator`
--

INSERT INTO `benachrichtigung_einladung_moderator` (`ID`, `Benachrichtigung`, `Benutzer`, `Veranstaltung`, `Gelesen`, `Angenommen`) VALUES
(1, 2, 1, 2, 1, NULL),
(2, 8, 2, 3, 1, NULL),
(3, 24, 2, 2, 0, NULL);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Daten für Tabelle `benachrichtigung_karteikartenaenderung`
--

INSERT INTO `benachrichtigung_karteikartenaenderung` (`ID`, `Benachrichtigung`, `Benutzer`, `Karteikarte`, `Gelesen`) VALUES
(1, 18, 4, 123, 0),
(2, 19, 4, 123, 0),
(3, 22, 1, 58, 1),
(4, 22, 4, 58, 0);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Daten für Tabelle `benachrichtigung_neuer_kommentar`
--

INSERT INTO `benachrichtigung_neuer_kommentar` (`ID`, `Benachrichtigung`, `Benutzer`, `Kommentar`, `Gelesen`) VALUES
(1, 27, 1, 4, 0);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=23 ;

--
-- Daten für Tabelle `benachrichtigung_veranstaltungsaenderung`
--

INSERT INTO `benachrichtigung_veranstaltungsaenderung` (`ID`, `Benachrichtigung`, `Veranstaltung`, `Benutzer`, `Gelesen`) VALUES
(1, 3, 2, 1, 1),
(2, 3, 2, 5, 1),
(3, 9, 3, 1, 1),
(4, 9, 3, 2, 1),
(5, 12, 3, 1, 1),
(6, 12, 3, 2, 1),
(8, 13, 3, 1, 1),
(9, 13, 3, 2, 1),
(10, 15, 3, 1, 1),
(11, 15, 3, 2, 1),
(12, 25, 2, 1, 0),
(13, 25, 2, 2, 1),
(14, 25, 2, 4, 0),
(15, 25, 2, 11, 0),
(19, 26, 2, 1, 0),
(20, 26, 2, 2, 0),
(21, 26, 2, 4, 0),
(22, 26, 2, 11, 0);

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
  `CryptedPW` varchar(60) NOT NULL COMMENT 'hash = salt(29 Zeichen) + crypted (Rest)',
  `Theme` enum('DAY','NIGHT') NOT NULL DEFAULT 'NIGHT'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=12 ;

--
-- Daten für Tabelle `benutzer`
--

INSERT INTO `benutzer` (`ID`, `eMail`, `Vorname`, `Nachname`, `Profilbild`, `Matrikelnummer`, `Studiengang`, `Nutzerstatus`, `NotifyKommentare`, `NotifyVeranstAenderung`, `NotifyKarteikartenAenderung`, `CryptedPW`, `Theme`) VALUES
(1, 'admin@sopra.de', 'Der', 'Admin', 'default.png', 0, 'Sonstiges', 'ADMIN', 'VERANSTALTUNG_TEILGENOMMEN', 1, 1, '$2a$10$AoMut6JI4GkLJZxbnqGMkeMC/ozfbynBdKZr90N5Wjs2CJ7aR59oi', 'NIGHT'),
(2, 'andreas.rottach@uni-ulm.de', 'Andreas', 'Rottach', 'default.png', 1234567, 'Informatik', 'STUDENT', 'VERANSTALTUNG_TEILGENOMMEN', 1, 1, '$2a$10$a7C2vqqPpl0GiF1D5gTKXO/3EdVTIWGgGN9v7Az7x24W6vECiTLaC', 'NIGHT'),
(3, 'matthias.englert@uni-ulm.de', 'Matthias', 'Englert', 'default.png', 815, 'Informatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$3/LOgBMsK8Lu2fYo3j0WLOIjrCC/w/MTjBit.aezhz9ED.apKE5hS', 'NIGHT'),
(4, 'mk@ulm.de', 'Marius', 'Kircher', 'default.png', 123456, 'Medieninformatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$7LlnqoZqrmtgSH4tHVdMjOFT50RwLillAq0p3Z.nz87tjTgHzxn9O', 'NIGHT'),
(5, 'helmut.partsch@uni-ulm.de', 'Helmut', 'Partsch', 'default.png', 2345678, 'Sonstiges', 'DOZENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$3s5UOGTQ6PtnW144JmofdOJKzk7WBYDPSEsbb3vrNl281tAJDrPbq', 'NIGHT'),
(11, 'max.mustermann@uni-ulm.de', 'Max', 'Mustermann', 'default.png', 12345678, 'Informatik', 'STUDENT', 'DISKUSSION_TEILGENOMMEN', 1, 1, '$2a$10$9hViHA/KrCdrGx31DifAF.nJJn0H40eycrnk2JEiz8oYK7ftD9Db.', 'NIGHT');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer_veranstaltung_zuordnung`
--

CREATE TABLE IF NOT EXISTS `benutzer_veranstaltung_zuordnung` (
`ID` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=26 ;

--
-- Daten für Tabelle `benutzer_veranstaltung_zuordnung`
--

INSERT INTO `benutzer_veranstaltung_zuordnung` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(23, 1, 2),
(6, 1, 3),
(25, 2, 2),
(11, 2, 3),
(24, 4, 2),
(12, 4, 3),
(3, 5, 2),
(19, 11, 2),
(17, 11, 3);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bewertung_karteikarte`
--

CREATE TABLE IF NOT EXISTS `bewertung_karteikarte` (
`ID` int(11) NOT NULL,
  `Bewertung` int(11) NOT NULL,
  `Benutzer` int(11) NOT NULL,
  `KarteikarteID` int(11) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `bewertung_karteikarte`
--

INSERT INTO `bewertung_karteikarte` (`ID`, `Bewertung`, `Benutzer`, `KarteikarteID`) VALUES
(1, -1, 2, 24),
(2, 1, 2, 21),
(3, -1, 11, 21);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Daten für Tabelle `bewertung_kommentar`
--

INSERT INTO `bewertung_kommentar` (`ID`, `Bewertung`, `Benutzer`, `KommentarID`) VALUES
(1, 1, 11, 1);

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
(0, 'Anderes Profil anzeigen', '<p>Sie können sich andere&nbsp;Profile anzeigen lassen, indem sie mit der Suchfunktion nach einem Benutzer suchen. Klicken sie auf einen Benutzer, so werden sie zu seinem Profil weitergeleitet.</p><p>Desweiteren sind meist alle Benutzernamen, die auf der Website vorkommen mit einem Link versehrt, der sie ebenfalls zu dem zugehörigen Profil weiterleitet.</p>', 'TEXT', 0, '2015-06-15 12:45:43', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(1, 'Suchfunktion benutzen', '<p>Die Suchfunktion ist hilfreich, wenn sie den Namen der Veranstaltung, für die sie sich einschreiben wollen, schon wissen. Suchen sie einfach nach dieser Veranstaltung und wählen sie das passende Ergebnis aus.</p><p>Sollte sie eine Veranstaltung nicht finden, suchen sie im Reiter "Alle" explizit nach dieser Veranstaltung (wenn sie dort nicht zu finden ist, existiert sie auch nicht). Siehe dazu Karteikarte&nbsp;"Alle Veranstaltungen".</p><p>Weiterhin kann man mit der Suchfunktion nach Benutzern suchen. Mit einem Klick auf ein Ergebnis gelangen sie zu dem Profil des Benutzers.</p><p>Die Suche brechen sie ab, indem sie auf das hellgraue "x" oben rechts bei den Ergebnissen klicken.</p>', 'TEXT', 0, '2015-06-15 12:18:08', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(2, 'Zu Veranstaltungen einschreiben / von Veranstaltungen ausschreiben', '<p>Um sich in eine Veranstaltung ein- oder auszuschreiben, klicken sie auf den grauen Pfeil rechts, der bei jeder aktuell aufgelisteten Veranstaltung erscheint. Nun wird die Beschreibung der Veranstaltung gezeigt, sowie rechts unten entweder ein Raketen-Symbol mit dem Schriftzug "Einschreiben" oder ein "x" mit dem Schriftzug "Ausschreiben".</p><p>Im ersten Fall können sie hier klicken um sich für diese Veranstaltung einzuschreiben. Bedenken sie, dass es für manche Veranstaltungen Kennworte gibt, um die Veranstaltung zu schützen. Dieses Kennwort erfahren sie im Regelfall von ihrem Dozenten. Geben sie dieses Hier ein und drücken sie anschließend auf "Einschreiben".</p><p>Im zweiten Fall können sind sie bereits für diese Veranstaltung ausgeschrieben. Wenn sie hier klicken, werden sie folglich ausgeschrieben. Bedenken sie, dass sie sich um die Veranstaltung einsehen zu können, wieder einschreiben müssen.</p><p> </p>', 'TEXT', 0, '2015-06-15 12:33:49', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(3, 'Meine Veranstaltungen', '<p>Im Reiter "Meine" finden sie alle Veranstaltungen, in die sie momentan eingeschrieben sind.</p><p>In dieser Veranstaltung (Bedienungsanleitung eLearning-System) sind sie automatisch eingeschrieben worden.&nbsp;</p>', 'TEXT', 0, '2015-06-15 12:00:00', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(4, 'Veranstaltungsübersicht', '<p>Auf dieser Seite haben sie oben ein Suchfeld, in der sie nach Veranstaltungen und Personen suchen können.</p><p>Weiterhin existieren zwei Reiter "Meine" und "Alle", in dem sie Veranstaltungen finden können (siehe "Meine Veranstaltungen" und "Alle Veranstaltungen".</p><p>Sie finden zu jeder aufgelisteten Veranstaltung mittig den zugehörigen Dozent, die aktuelle Teilnehmeranzahl, sowie das Semester in dem diese gehalten wird.</p><p>Sie können mit dem grauen Pfeil nach unten (rechts bei jeder aufgelisteten Veranstaltung) mehr Informationen über die Veranstaltung in Erfahrung bringen. Hier erscheint dann zur jeweiligen Veranstaltung eine Beschreibung. Rechts unten ist entweder ein Raketen-Symbol zum Einschreiben in den Kurs oder ein "x" zum Ausschreiben (siehe "zu Veranstaltung einschreiben/ von Veranstaltung ausschreiben).</p>', 'TEXT', 0, '2015-06-15 12:11:21', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(5, 'Karteikarten Typen', '', 'TEXT', 0, '2015-06-15 08:28:35', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(6, 'Text-Karteikarten', '<p>Beispiel für eine Textkarteikarte</p>', 'BILD', 0, '2015-06-15 13:37:54', 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(7, 'Bild-Karteikarten', '<p>Dies ist eine Beispiel Bild-Karteikarte. Bilder können zusätlich eine Bildunterschrift beinhalten.</p>', 'BILD', 0, '2015-06-15 13:45:00', 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(8, 'Video-Karteikarten', '<p>Dies ist ein Beispiel für eine Video-Karteikarte. Diese können wie Bild-Karteikarten eine Beschreibung haben</p>', 'VIDEO', 0, '2015-06-15 13:46:51', 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(9, 'Karteikarten erstellen', '', 'TEXT', 0, '2015-06-15 10:39:09', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(10, 'Karteikarten bearbeiten', '<p>Der Button für das Bearbeiten von Karteikarten befindet sich rechts neben Karteikarten. Es öffnet sich der gleiche Dialog, wie er beim Erstellen schon beschrieben wurde.</p>', 'BILD', 0, '2015-06-15 14:28:46', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(11, 'Karteikarten löschen', '<p>Um eine Karteikarte zu löschen, brauchen sie zunächst die nötigen Rechte. Berechtigt sind sie, wenn sie der Dozent der Veranstaltung sind oder Moderator sind. Moderatoren können jedoch nur Karteikarten löschen, wenn sie vom Dozenten berechtigt wurden.</p><p>Im Fall der Berechtigung, erscheinen beim Hovern (Schweben der Maus) über der Karteikarte Symbole.&nbsp;Diese erscheinen entweder rechts (bei einer Überschriftskarteikarte) oder links bei allen anderen. Eines dieser Symbole ist ein Mülleimer. Klicken sie auf diesen und Bestätigen sie, dass sie sich sicher sind. Danach sind die Karteikarte und alle ihre untergeordneten Karteikarten ("Kinder") gelöscht. Prüfen sie vorher ausführlich, ob sie dies wirklich wollen, da dieser Vorgang irreversibel ist.</p>', 'BILD', 0, '2015-06-15 14:33:07', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(20, 'SW-Prüfung', '', 'TEXT', 0, '2015-06-04 13:34:01', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(21, 'Hintergrund und Motivation', '<ul><li>Akzeptanz eines Produkts wird durch Qualit&auml;t bestimmt<ul><li>&Uuml;bereinstimmung mit den Anforderungen</li><li>Leistungsumfang, Benutzbarkeit und Komfort</li><li>Flexibilit&auml;t (bez&uuml;glich &Auml;nderungen)</li></ul></li><li>Verbesserung der Qualit&auml;t (von Artefakten der System und&nbsp;Softwareentwicklung) durch<ul><li>Disziplinierte Erstellung (&bdquo;Vorgehensmodelle&ldquo;) und</li><li>Systematische Pr&uuml;fung und Behebung von Defekten</li></ul></li></ul>', 'TEXT', -1, '2015-06-04 13:37:06', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(22, 'Qualitätssicherung - Review', '', 'TEXT', 0, '2015-06-04 13:38:20', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(23, 'Wichtigste Fragen', '<ul><li>Was ist (Software-)Qualit&auml;t?</li><li>Wie stellt sich Qualit&auml;t dar? (Qualit&auml;ts-Modelle und Merkmale)</li><li>Wie kann man Qualit&auml;t messen und vergleichen? (Metriken)</li><li>Welche Ma&szlig;nahmen dienen der Hebung der Qualit&auml;t? (Qualit&auml;tssicherung)</li></ul><p>&nbsp;</p>', 'TEXT', 0, '2015-06-04 13:41:10', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(24, 'Qualität', '<ul><li>Quality (IEEE-Standard)<ul><li>(1) The degree to which a system, component, or process meets specified&nbsp;requirements</li><li>(2) The degree to which a system, component, or process meets customer or&nbsp;user needs or expectations</li></ul></li><li>Qualit&auml;t ist die Gesamtheit von Merkmalen einer Einheit bez&uuml;glich ihrer Eignung,&nbsp;festgelegte und vorausgesetzte Erfordernisse zu erf&uuml;llen (DIN ISO 8402)</li></ul>', 'TEXT', -1, '2015-06-04 13:43:34', 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
(25, 'Lernziele', '<ul><li>Grundlagen, wichtigste Aspekte und verschiedene Formen statischer Pr&uuml;fungen beschreiben k&ouml;nnen</li><li>Das Technische Review erkl&auml;ren und anwenden k&ouml;nnen</li><li>Praxisrelevante Aspekte von Reviews (z.B. Spielregeln f&uuml;r die Sitzung, Tipps f&uuml;r die Beteiligten) benennen k&ouml;nnen</li><li>St&auml;rken und Schw&auml;chen von Reviews auflisten k&ouml;nnen</li></ul>', 'TEXT', 0, '2015-06-04 13:46:40', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(26, 'Qualitätssicherung – Test', '<p><strong>Lernziele</strong></p><ul><li>Grundlagen und wichtigste Aspekte<br />hinsichtlich Tests beschreiben k&ouml;nnen</li><li>Die wichtigsten Testformen (Black-<br />Box- und Glass-Box-Tests) sowie<br />Verfahren zur Testfallauswahl<br />erkl&auml;ren und anwenden k&ouml;nnen</li><li>Andere Testverfahren beschreiben<br />k&ouml;nnen</li><li>St&auml;rken und Schw&auml;chen von Tests<br />wiedergeben k&ouml;nnen</li></ul>', 'TEXT', 0, '2015-06-04 13:47:16', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(36, 'Eigenes Profil anzeigen', '<p>Sie können sich ihr eigenes Profil anzeigen lassen, indem sie auf ihren eigenen Namen klicken. Dieser ist befindet sich im linken Block, direkt unter ihrem Profilbild. Sie werden mit dem Klick auf ihr eigenes Profil weitergeleitet.</p>', 'TEXT', 0, '2015-06-15 12:42:25', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(57, 'Veranstaltungsseite', '', 'TEXT', 0, '2015-06-09 07:15:45', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(58, 'Profildaten bearbeiten', '<p>Hier können Sie Ihre im System gespeicherten Daten sehen. Die Daten lassen sich direkt in den entsprechenden Feldern ändern.&nbsp;</p><p>Außerdem können Sie einstellen, wann Sie benachrichtigt werden wollen.</p><p><u>Kommentare</u></p><ul><li>Sie können sich über alle Kommentare zu Veranstaltungen, zu denen Sie eingeschrieben sind informieren lassen.</li><li>Sie können sich nur über Kommentare benachrichtigen lassen, bei denen Sie selbst einen Kommentar verfasst haben.</li><li>Sie können diese Benachrichtigung deaktivieren.</li></ul><p><u>Änderungen</u></p><ul><li>Sie können sich über Änderungen an Veranstaltungen informieren lassen.</li><li>Sie können sich über Änderungen an Karteikarten informieren lassen.</li></ul>', 'BILD', 0, '2015-06-22 07:28:13', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(65, 'Profil bearbeiten', '', 'TEXT', 0, '2015-06-09 07:16:04', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(66, 'Fakten über Software-Qualität', '<p>Gr&ouml;&szlig;te Kostentreiber sind Fehlerfindung und -behebung sowie Dokumentenerstellung</p><ul><li>30% der Softwarekosten sind Kosten f&uuml;r Dokumenterstellung</li><li>Ca. 50 Dokumente (insgesamt: 6000 Seiten, 200 000 W&ouml;rter, 5000 Diagramme)</li><li>Dokumente haben 3 Defekte/Seite (18 000 insgesamt), die ohne Pr&uuml;fma&szlig;nahmen in die&nbsp;Software gehen oder beim Kunden landen</li><li>SW-Systeme haben insgesamt ca. 50 000 Defekte&nbsp;(von denen 80% durch Pr&uuml;fma&szlig;nahmen, insbesondere Tests, gefunden werden)</li><li>Tests erfordern insgesamt ca. 55 000 Testf&auml;lle</li><li>25% der Testf&auml;lle haben Defekte&nbsp;(Defektdichte bei Testf&auml;llen oft h&ouml;her als im System selbst)</li><li>7% der (40 000 s.o.) Bug-Fixes sind Bad-Fixes (2800, die zu den 10 000 v.o. dazukommen)</li><li>Von den insgesamt 12 800 ausgelieferten Defekten sind 20 % (d.h. ca. 2500) gravierend</li></ul>', 'TEXT', 0, '2015-06-04 13:48:17', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(68, 'Statische Prüfungen (1/7)', '<h3><span style="font-family:arial,helvetica,sans-serif"><strong>Statische Pr&uuml;fung</strong></span></h3><ul><li>Pr&uuml;fung eines Dokuments (allein oder mit Gespr&auml;chspartner) nach vorgegebenen Kriterien</li></ul><h3><span style="font-family:arial,helvetica,sans-serif"><strong>Gemeinsamkeiten aller statischen Pr&uuml;fmethoden</strong></span></h3><ul><li>Wesentliche T&auml;tigkeit: dokumentierte (Teil-)Produkte untersuchen und beurteilen</li><li>Hauptziel: Defekte aller Art (Fehler, Widerspr&uuml;che, Unvollst&auml;ndigkeiten, etc.) finden</li><li>Beteiligte: (kleines) Team mit klarer Rollenverteilung</li><li>Neben individueller Pr&uuml;ft&auml;tigkeit meist auch Gruppensitzung (mit allen Beteiligten) Sinnvolle Voraussetzungen (f&uuml;r den Erfolg statischer Pr&uuml;fungen)</li></ul><h3><span style="font-family:arial,helvetica,sans-serif"><strong>Der erforderliche Aufwand (Personal, Zeit) muss fest eingeplant sein</strong></span></h3><ul><li>Es muss bekannt sein, welchem Zweck die Pr&uuml;fung dient</li><li>Es muss ein zug&auml;ngliches Pr&uuml;fdokument (Text, Programm oder Zeichnung) vorliegen</li><li>Die Mitglieder des Pr&uuml;fteams sollten in der jeweiligen Pr&uuml;fmethode geschult sein</li><li>Die Pr&uuml;fmethode sollte dokumentiert sein und ihre Einhaltung &uuml;berpr&uuml;ft werden</li><li>Pr&uuml;fergebnisse d&uuml;rfen nicht zur Personalbeurteilung missbraucht werden</li></ul>', 'TEXT', 0, '2015-06-04 13:56:49', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(70, 'Profil anzeigen', '', 'TEXT', 0, '2015-06-09 07:16:24', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(71, 'Passwort ändern', '<p>Wechseln Sie zuerst auf Ihr Profil indem sie links oben auf Ihren Namen klicken.&nbsp;</p><p>Tragen Sie zuerst das alte Passwort ein. Tragen Sie dann das neue Passwort ein und wiederholen Sie es. Klicken Sie danach auf "Speichern".&nbsp;Wenn alle Angaben in Ordnung sind, wird Ihr Passwort gespeichert.</p>', 'BILD', 0, '2015-06-12 17:26:37', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(74, 'Profilbild ändern / löschen', '<p>Nachdem Sie auf Ihr Profil gewechselt haben (Klick auf Ihren Namen links oben), können Sie Ihr</p><p><u>Profilbild ändern</u></p><ul><li>Klicken Sie auf Ihr aktuelles Bild auf der Profilseite links oben. (Innerhalb der Seite, nicht in der Box links.) (Siehe Bild 1).</li><li>Es öffnet sich ein Datei-Dialog zum Auswählen eines Bildes. Es sind nur die Dateiendungen png, jpg und&nbsp;jpeg erlaubt.</li><li>Nach dem Auswählen der Datei wird sie entweder abgelehnt, andernfalls sehen Sie&nbsp;das mittlere Bild. Durch den Klick auf "Avatar hochladen" wird das Bild auf den Server geladen und gespeichert.&nbsp;</li><li>Wenn dies funktioniert hat, sehen Sie Bild 3.</li></ul><p><u>Profilbild löschen</u></p><p>Um Ihr Profilbild zu löschen, klicken Sie einfach auf "Profilbild löschen" (Siehe Bild 3).</p>', 'BILD', 0, '2015-06-13 13:27:39', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(75, 'Bedienungsanleitung eLearning-System', '', 'TEXT', 0, '2015-06-08 11:52:50', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(76, 'Hauptseite', '', 'TEXT', 0, '2015-06-09 07:15:02', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(77, 'Profil', '', 'TEXT', 0, '2015-06-09 07:12:32', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(78, 'Karteikarten', '', 'TEXT', 0, '2015-06-15 08:34:42', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
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
(90, 'Fehler', '', 'TEXT', 0, '2015-06-04 13:33:46', 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(91, 'Idee', '<p>Das E-Learning System nutzt als Konzept sogenannte Karteikarten. Der Sinn dahinter liegt darin ein Vorlesungsskript nicht komplett linear aufzubauen, sondern modularer. Durch das Inhaltsverzeichnis soll dennoch eine hierarchische Struktur wie sie bei einem linearen Skript üblich ist beibehalten werden. Zusätzlich können Karteikarten verschiedene Attribute und Verweise auf andere Karteikarten haben. Diese Konzepte werden im Folgenden genauer erläutert</p>', 'TEXT', 0, '2015-06-15 08:39:12', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(92, 'Attribute und Verweise', '<p>Die Karteikarte behandelt ein Beispiel aus der Stochastik und zeigt wie man Attribute und Verweise verwenden kann.</p><p><u>Attribute </u>spezifizieren den Inhalt der Karteikarte. Eine Karteikarte kann mehrere Attribute haben. In dem Beispiel handelt es sich um eine Definition. Im Bereich der Stochastik kann die Karteikarte aber auch als wichtig und als Grundlagenwissen angesehen werden.</p><p>Es gibt 4 Arten von <u>Verweisen</u>. Die Verweise müssen auf eine Karteikarte in der gleichen Veranstaltung verweisen. In dem Beispiel ist es als Voraussetzung wichtig zu wissen was Riemann Integrale sind um die Definition der Verteilung von absolutstetigen Zufallsvariablen zu verstehen. Klickt man auf einen Verweis dann springt man zur entsprechenden Karteikarte.</p><p>Für Übungsaufgaben kann beispielsweise am Ende des Vorlesungsskripts ein Kapitel mit Übungsaufgaben erstellt werden.</p>', 'BILD', 0, '2015-06-15 10:13:27', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(93, 'Inhaltsverzeichnis', '', 'TEXT', 0, '2015-06-15 10:08:39', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(95, 'Wo erstelle ich Karteikarten?', '<p>Karteikarten haben eine feste Position in der Baumstruktur. Daher legen Sie neue Karteikarten durch Klicken an die entsprechende Position im Inhaltsverzeichnis an.</p><p><em><strong>Hinweis:&nbsp;</strong></em>Die grünen Buttons, die auf dem Screenshot zu sehen sind, tauchen erst auf wenn Sie die&nbsp;Maus über das Inhaltsverzeichnis bewegen (und natürlich auch nur, wenn sie entsprechende <strong>Rechte </strong>besitzen).</p>', 'BILD', 0, '2015-06-15 14:37:56', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(97, 'Welche Informationen kann eine Karteikarte enthalten? (1/3)', '<p>Jede Karteikarte&nbsp;<strong>muss&nbsp;</strong>einen Titel haben.</p><p>Wenn Sie eine Überschrift erstellen wollen, markieren die entsprechende Checkbox. Bitte nehmen Sie zur Kenntnis, dass Sie dann&nbsp;<strong>keinen Inhalt</strong>&nbsp;(in Form von Text, Bild oder Video) mehr zu dieser Karteikarte hinzufügen können.</p><p>Eine Karteikarte, die keine Überschrift ist,&nbsp;<strong>muss</strong>&nbsp;einen Inhalt enthalten.&nbsp;</p><p>Sie können zum einen&nbsp;reine&nbsp;<strong>Textkarteikarten&nbsp;</strong>erstellen. Verfassen Sie dafür mithilfe des Editors einen Inhalt. Weiterführendes über die Möglichkeiten des Editors finden Sie auf der Karteikarte zum Editor.</p><p>Zum anderen können Sie&nbsp;<strong>Bild- bzw. Videokarteikarten</strong>&nbsp;erstellen. Wählen Sie dazu im Upload-Feld eine Datei aus. Diese wird im Hintergrund hochgeladen und Sie erhalten eine Meldung, sobald dies erfolgreich war. Sie können nun einen&nbsp;<strong>zusätzlichen&nbsp;Text</strong>&nbsp;verfassen, der das Bild oder Video ergänzt.</p><p>Über den Button&nbsp;<span style="font-family:computer modern serif"><em>Weiter</em>&nbsp;</span>gelangen Sie zur nächsten Seite des Erstellen-Dialogs.</p>', 'BILD', 0, '2015-06-15 14:15:15', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(100, 'Alle Veranstaltungen', '<p>Auf der Hauptseite finden sie den Reiter "Alle Veranstaltungen". Hier finden sie alle Veranstaltungen im System. Wenn sie nach einer Veranstaltungen suchen stehen ihnen zu Spezifikation ihrer Suche zwei Comboboxen zur Verfügung.</p><p>Die linke Combobox ermöglicht ihnen die Auswahl des Semesters, in der die Veranstaltung gehalten wird/wurde. Mit der rechten Combobox können sie einen Studiengang auswählen (meist ihren eigenen), für den die Veranstaltung gehalten wird. Finden sie ihre Veranstaltung hier nicht, existiert sie möglicherweise nicht. Wenden sie sich an den Dozenten.</p>', 'TEXT', 0, '2015-06-15 12:26:28', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(101, 'Beschreibung', '<div>Das Inhaltsverzeichnis ordnet die Karteikarten in einer hierarchischen Struktur an. Jede Karteikarte der Veranstaltung muss sich in dieser Hierarchie wiederfinden. Das heißt es darf keine losen Verweise geben. Will man von Karteikarten auf Übungsaufgaben verweisen, dann müssen die Übungsaufgaben z.B. am Ende vom Skript in die Hierarchie eingefügt werden.</div>', 'TEXT', 0, '2015-06-15 12:41:14', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(102, 'Wie navigiere ich im Inhaltsverzeichnis?', '<div>Zu Beginn sind nur die Karteikarten auf der obersten Hierarchie-Stufe zu sehen. Die Karteikarten im Inhaltsverzeichnis sind klickbar. Beim Klicken wird der Baum aufgeklappt und es werden die Unterkapitel der Karteikarte angezeigt. Fährt man mit der Maus über eine Karteikarte, erscheint neben dem Namen ein Pfeil. Wird dieser angeklickt springt der Content und das Inhaltsverzeichnis an die entprechende Stelle.</div>', 'TEXT', 0, '2015-06-15 12:53:05', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(103, 'Notizen', '<p><u>Beispiel Notiz:</u></p><ul><li>Es gibt nur eine Notiz pro Karteikarte.</li><li>Mit Klick auf das Feld Notizen kann dieses aus-und eingeklappt werden</li><li>Der weiße Punkt neben dem Wort Notizen heißt, dass für diese Karteikarte Notizen gemacht wurden</li></ul>', 'BILD', 0, '2015-06-15 13:36:33', 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(104, 'Kommentare', '<p><u>Beispiel Kommentar:</u></p><p>Kommentare sind nur möglich, wenn der Dozent der Veranstaltung dies zulässt</p><p>Kommentare werden nach ihrem Erstelldatum absteigend sortiert</p><p>Zu einer Karteikarten kann es verschiedene Themenkommentare geben. Themenkommentare können von jedem Teilnehmer der Veranstaltung bewertet werden. Dabei sind die Bewertungen 1 und -1 möglich (entsprechend Pfeil nach oben oder unten neben dem Kommentar wählen).</p><p>Für einen Themenkommentar kann es beliebig viele Antwortkommentare geben. Antwortkommentare können nicht bewertet werden</p>', 'BILD', 0, '2015-06-15 13:35:35', 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(106, 'Welche Informationen kann eine Karteikarte enthalten? (2/3)', '<p>Optional kann eine Karteikarte mit <strong>Attributen </strong>ausgestattet werden. Diese können Sie auf der zweiten Seite des Erstellen-Dialogs auswählen.</p>', 'BILD', 0, '2015-06-15 13:42:45', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(107, 'Welche Informationen kann eine Karteikarte enthalten? (3/3)', '<p>Eine Karteikarte kann optional mit <strong>Verweisen </strong>auf andere Karteikarten ausgestattet werden. Nutzen Sie dies, um Ihren Inhalt durch sinnvolle Querbezüge zu vernetzen.</p>', 'BILD', 0, '2015-06-15 13:45:46', 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
(108, 'Benachrichtigungen', '', 'TEXT', 0, '2015-06-15 13:47:54', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(109, 'Rechte', '', 'TEXT', 0, '2015-06-15 13:50:15', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(111, 'Rolle: Studenten', '', 'TEXT', 0, '2015-06-15 14:28:05', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(112, 'Rolle: Dozent', '', 'TEXT', 0, '2015-06-15 14:28:17', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(113, 'Rechte in Profilen', '<p>In der Profilansicht gibt es zwei Arten von Rechten.</p><p>Der Besitzer des Profils kann alle seine Stammdaten einsehen und verändern.&nbsp;Weiterhin darf er sein Passwort verädern und kann&nbsp;sein Farbschema anpassen. Außer dem Besitzer selbst kann niemand diese sensitiven Daten einsehen (ausgenommen Administrator).</p><p>Alle anderen können nur folgende Informationen über das Profil einsehen: Vorname, Nachname, Studiengang und seine Rolle im System (Student, Dozent, Administrator). Für Nicht-Besitzer gibt es weiterhin auch kein Bearbeitungsrecht.</p>', 'TEXT', 0, '2015-06-15 14:21:22', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(114, 'Editor', '<p>In diesem System wird ein Editor verwendet, der es erlaubt formatierten Text ohne Kenntnisse einer Markup-Sprache zu verfassen. Wie bekannte Textverarbeitungsprogramme verfolgt er dabei das&nbsp;<em>What-You-See-Is-What-You-Get&nbsp;</em>Prinzip. Daher ist es für durchschnittlich erfahrene&nbsp;Computernutzer leicht möglich reichhaltigen Inhalt zu erstellen.</p><p>Auf dem Screenshot sind einige der Möglichkeiten dargestellt.</p><p>Besondere Erwähnung soll hier noch der integrierte TeX-Formel-Editor finden. Nutzer, die TeX Syntax beherrschen, können damit komplexere Formeln einfügen. Klicken Sie dazu auf das Sigma in der Editor-Toolbar.</p>', 'BILD', 0, '2015-06-15 14:14:22', 3, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
(115, 'Bewertungen', '<p>Wenn in einer Veranstaltungen Bewertungen zugelassen sind, können deren Karteikarten von allen eingeschriebenen Nutzern bewertet werden. Jeder Nutzer kann höchstens eine positive <em>(Upvote)&nbsp;</em>oder negative Bewertung&nbsp;<em>(Downvote)&nbsp;</em>abgeben. Die Summe aller Bewertungen wird als Gesamtbewertung angezeigt.</p>', 'BILD', 0, '2015-06-15 14:22:51', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(116, 'Einstufung der Rechte', '<p>Die Benutzer des Systems sind in 3 Rechtegruppen unterteilt:</p><p>1. Studenten</p><p>2. Dozenten</p><p>3. Administrator</p><p>(4. Moderatoren von Veranstaltungen, siehe "Rechte innheralb von Veranstaltungen")</p>', 'TEXT', 0, '2015-06-15 14:26:47', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(117, 'Rechte in Veranstaltungen', '<p>In Veranstaltungen darf sich ein Student (ggf. mit Kennwort) einschreiben.</p><p>Innerhalb der Veranstaltung hat er folgende Rechte:</p><ul><li>das Skript/die Karteikarten&nbsp;ansehen</li><li>das Skript exportieren</li><li>Karteikarten bewerten</li><li>Notizen verfassen/bearbeiten (wenn in Veranstaltung erlaubt)</li><li>Kommentare verfassen/bearbeiten&nbsp;(wenn in Veranstaltung erlaubt)</li><li>eigene Kommentare löschen</li><li>den externen Link zu einer Karteikarte kopieren</li><li>Informationen über die Veranstaltung einsehen (Beschreibung, Semester, Dozent, usw.)</li></ul><p> </p><p>Wurder er zum Moderator von dem Dozenten der Veranstaltung ernannt, kommen folgende Rechte dazu:</p><ul><li>Kommentare löschen</li><li>wenn Dozent Bearbeitungsrechte vergeben hat:<ul><li>Karteikarten hinzufügen</li><li>Karteikarten bearbeiten</li><li>Karteikarten löschen</li></ul></li></ul><p>&nbsp;</p>', 'TEXT', 0, '2015-06-15 14:53:15', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(118, 'Rechte in Veranstaltungen', '<p>In Veranstaltungen darf sich ein Dozent (ggf. mit Kennwort) einschreiben.</p><p>Weiterhin darf er auch selbst Veranstaltungen erstellen.</p><p>Innerhalb einer&nbsp;Veranstaltung, die er nicht besitzt,&nbsp;hat er folgende Rechte:</p><ul><li>das Skript/die Karteikarten&nbsp;ansehen</li><li>das Skript exportieren</li><li>Karteikarten bewerten</li><li>Notizen verfassen/bearbeiten (wenn in Veranstaltung erlaubt)</li><li>Kommentare verfassen/bearbeiten&nbsp;(wenn in Veranstaltung erlaubt)</li><li>eigene Kommentare löschen</li><li>den externen Link zu einer Karteikarte kopieren</li><li>Informationen über die Veranstaltung einsehen (Beschreibung, Semester, Dozent, usw.)</li></ul><p> </p><p>Wurder er zum Moderator von einer Veranstaltung ernannt, die er nicht besitzt,&nbsp;kommen folgende Rechte dazu:</p><ul><li>Kommentare löschen</li><li>wenn Dozent Bearbeitungsrechte vergeben hat:<ul><li>Karteikarten hinzufügen</li><li>Karteikarten bearbeiten</li><li>Karteikarten löschen</li></ul></li></ul><p> </p><p>Handelt es sich um seine eigene Veranstaltung, hat er zusätzlich folgende Berechtigungen:</p><ul><li>Veranstaltung bearbeiten<ul><li>Titel, Beschreibung, Semester, Studiengänge ändern</li><li>Kennwort setzen/ändern</li><li>Moderatoren hinzufügen</li><li>Rechte der Moderatoren ändern</li><li>Kommentare und Bewertungen erlauben oder verbieten</li></ul></li><li>Karteikarten hinzufügen</li><li>Karteikarten bearbeiten</li><li>Karteikarten löschen</li></ul>', 'TEXT', 0, '2015-06-15 14:59:21', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(119, 'Smartphoneansicht', '<p>Sie können das System komfortabel auf Smartphones nutzen, da sich die GUI automatisch an die Breite Ihres Bildschirms anpasst.</p>', 'BILD', 0, '2015-06-15 14:51:10', 3, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
(120, 'Veranstaltungen erstellen', '<p>Ein Dozent hat das Recht, eine Veranstaltung zu erstellen.</p>', 'TEXT', 0, '2015-06-15 15:00:35', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(121, 'Wann werden neue Benachrichtigungen erzeugt?', '<p>Neue Benachrichtigungen tauchen in der Box mit Ihrem Nutzernamen auf, wenn</p><ul><li>eine Veranstaltung, in die Sie eingeschrieben sind, bearbeitet wurde.</li><li>sie als Moderator zu einer Veranstaltung hinzugefügt wurden.</li><li>eine Karteikarte in einer Veranstaltung, in die Sie eingeschrieben sind, bearbeitet wurde.</li><li>eine Karteikarte in einer Veranstaltung, in die Sie eingeschrieben sind, kommentiert wurde (Sie können in Ihrem Profil einstellen, ob Sie über alle Kommentare benachrichtigt werden möchten, oder nur über Kommentare in Diskussionen, an denen Sie teilgenommen haben)</li><li>ihre Profil von Ihnen oder einem Administrator bearbeitet wurde.</li></ul>', 'TEXT', 0, '2015-06-15 15:16:18', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(122, 'Status von Benachrichtigungen', '<p><strong>Ungelesene Benachrichtigungen</strong> werden farbig hervorgehoben.</p><p>Bei Klick auf eine Benachrichtigung wird diese als gelesen markiert und sie springen zur betreffenden&nbsp;Veranstaltung oder zum&nbsp;Profil.</p>', 'TEXT', 0, '2015-06-15 15:22:54', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(123, 'Export', '<p>Eine Veranstaltung kann als PDF-Datei exportiert und heruntergeladen werden.</p><p>Dafür wird LaTeX Code erzeugt, den Sie als TEX-Datei oder als kompiliertes PDF&nbsp;herunterladen können.</p><p>Sie haben die Möglichkeit verschiedene Export Optionen zu wählen:</p><ul><li>Export von Notizen als Paragraph-Block unter der eigentlichen Karteikarte</li><li>Export von Attributen als Fußnoten</li><li>Export von Querverweisen als Paragraph-Block unter der eigentlichen Karteikarte. Diese Verweise sind klickbar.</li></ul><p>Nachdem Sie im Dialog auf PDF erstellen geklickt haben, wird das Latex-Dokument kompiliert. Dies kann einen Augenblick dauern. Nach beenden des Kompilierungsvorgangs erhalten Sie das PDF und das Tex-Dokument zum download.</p>', 'TEXT', 0, '2015-06-22 07:04:19', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Daten für Tabelle `kommentar`
--

INSERT INTO `kommentar` (`ID`, `Inhalt`, `Erstelldatum`, `Benutzer`, `Karteikarte`, `Vaterkommentar`) VALUES
(1, '<p>Sehr interessant!</p>', '2015-06-12 09:51:48', 2, 21, NULL),
(2, '<p>Naja vielleicht doch nicht so sehr...</p><p> </p>', '2015-06-12 09:52:06', 2, NULL, 1),
(3, '<p><span class="mathjax_formel">\\(x = {-b \\pm \\sqrt{b^2-4ac} \\over 2a}\\)</span></p>', '2015-06-12 13:46:28', 2, 24, NULL),
(4, '<p><span class="mathjax_formel">\\(x = {-b \\pm \\sqrt{b^2-4ac} \\over 2a}\\)</span></p>', '2015-06-29 10:54:50', 2, 21, NULL);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=14 ;

--
-- Daten für Tabelle `moderator`
--

INSERT INTO `moderator` (`ID`, `Benutzer`, `Veranstaltung`) VALUES
(7, 2, 3),
(11, 1, 2),
(12, 4, 2),
(13, 2, 2);

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
('Mathematik'),
('Medieninformatik'),
('Physik'),
('Sonstiges'),
('Wirtschaftswissenschaften');

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `veranstaltung`
--

INSERT INTO `veranstaltung` (`ID`, `Beschreibung`, `Semester`, `Kennwort`, `KommentareErlaubt`, `BewertungenErlaubt`, `ModeratorKarteikartenBearbeiten`, `Ersteller`, `Titel`, `ErsteKarteikarte`) VALUES
(2, '<p>Die Vorlesung Softwaretechnik II ist die Fortsetzungsveranstaltung der&nbsp;Softwaretechnik I.</p><p>Themen der Vorlesung Softwaretechnik II (im Sommersemester) sind:</p><ul><li>Qualitätssicherung (Metriken, Systematisches Testen, Reviews)</li><li>Projektmanagement (Planung, Kostenschätzung, Controlling, Konfigurationsmanagement, Qualitätsmanagement, Prozessverbesserung)</li></ul>', 'SoSe2015', NULL, 1, 1, 1, 5, 'Softwaretechnik II', 79),
(3, '<p>Dies ist die Bedienungsanleitung des Systems</p>', 'SoSe2015', NULL, 0, 0, 1, 1, 'Bedienungsanleitung eLearning-System', 75);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `veranstaltung_studiengang_zuordnung`
--

CREATE TABLE IF NOT EXISTS `veranstaltung_studiengang_zuordnung` (
`ID` int(11) NOT NULL,
  `Veranstaltung` int(11) NOT NULL,
  `Studiengang` varchar(30) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=18 ;

--
-- Daten für Tabelle `veranstaltung_studiengang_zuordnung`
--

INSERT INTO `veranstaltung_studiengang_zuordnung` (`ID`, `Veranstaltung`, `Studiengang`) VALUES
(12, 3, 'Sonstiges'),
(13, 3, 'Biologie'),
(16, 2, 'Informatik'),
(17, 2, 'Medieninformatik');

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
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `eMail` (`eMail`), ADD UNIQUE KEY `Matrikelnummer` (`Matrikelnummer`), ADD UNIQUE KEY `Matrikelnummer_2` (`Matrikelnummer`), ADD UNIQUE KEY `Matrikelnummer_3` (`Matrikelnummer`), ADD KEY `Studiengang` (`Studiengang`);

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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=28;
--
-- AUTO_INCREMENT for table `benachrichtigung_einladung_moderator`
--
ALTER TABLE `benachrichtigung_einladung_moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `benachrichtigung_karteikartenaenderung`
--
ALTER TABLE `benachrichtigung_karteikartenaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `benachrichtigung_neuer_kommentar`
--
ALTER TABLE `benachrichtigung_neuer_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `benachrichtigung_profil_geaendert`
--
ALTER TABLE `benachrichtigung_profil_geaendert`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `benachrichtigung_veranstaltungsaenderung`
--
ALTER TABLE `benachrichtigung_veranstaltungsaenderung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=23;
--
-- AUTO_INCREMENT for table `benutzer`
--
ALTER TABLE `benutzer`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `benutzer_veranstaltung_zuordnung`
--
ALTER TABLE `benutzer_veranstaltung_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=26;
--
-- AUTO_INCREMENT for table `bewertung_karteikarte`
--
ALTER TABLE `bewertung_karteikarte`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `bewertung_kommentar`
--
ALTER TABLE `bewertung_kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `kommentar`
--
ALTER TABLE `kommentar`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `moderator`
--
ALTER TABLE `moderator`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=14;
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
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `veranstaltung_studiengang_zuordnung`
--
ALTER TABLE `veranstaltung_studiengang_zuordnung`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=18;
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
