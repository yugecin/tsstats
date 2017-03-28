-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2
-- http://www.phpmyadmin.net

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `tsstats`
--

-- --------------------------------------------------------

--
-- Table structure for table `bans`
--

CREATE TABLE IF NOT EXISTS `bans` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL,
  `invokeruid` varchar(32) NOT NULL,
  `reason` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `length` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

-- --------------------------------------------------------

--
-- Table structure for table `channels`
--

CREATE TABLE IF NOT EXISTS `channels` (
  `cid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  `name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `topic` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `password` tinyint(1) NOT NULL DEFAULT '0',
  `maxclients` int(11) NOT NULL,
  `totalusers` int(11) NOT NULL DEFAULT '0',
  `lastseen` int(11) NOT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `kicks`
--

CREATE TABLE IF NOT EXISTS `kicks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL,
  `invokeruid` varchar(32) NOT NULL,
  `reason` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `time` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

--
-- Table structure for table `serverinfo`
--

CREATE TABLE IF NOT EXISTS `serverinfo` (
  `timeofday` varchar(512) NOT NULL,
  `days` int(11) NOT NULL,
  `lastday` int(11) NOT NULL,
  `lastupdate` int(11) NOT NULL,
  PRIMARY KEY (`timeofday`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `serverinfo`
--

INSERT INTO `serverinfo` (`timeofday`, `days`, `lastday`, `lastupdate`) VALUES
('0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0', 0, DAYOFYEAR(NOW()), UNIX_TIMESTAMP());

-- --------------------------------------------------------

--
-- Table structure for table `tod`
--

CREATE TABLE IF NOT EXISTS `tod` (
  `uid` varchar(32) NOT NULL,
  `tod` int(5) NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`tod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `usedchannels`
--

CREATE TABLE IF NOT EXISTS `usedchannels` (
  `uid` varchar(32) NOT NULL,
  `cid` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `usednames`
--

CREATE TABLE IF NOT EXISTS `usednames` (
  `uid` varchar(32) NOT NULL,
  `name` varchar(35) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `uid` varchar(32) NOT NULL,
  `currentname` varchar(35) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `timeofday` varchar(170) NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0',
  `updates` int(11) NOT NULL,
  `channels` text,
  `lastonline` int(11) NOT NULL,
  `timeouts` int(11) NOT NULL DEFAULT '0',
  `days` int(11) NOT NULL DEFAULT '0',
  `lastday` int(11) NOT NULL DEFAULT '-1',
  `country` varchar(16) NOT NULL,
  `serverchats` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
