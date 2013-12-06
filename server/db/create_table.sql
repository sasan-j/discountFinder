-- phpMyAdmin SQL Dump
-- version 4.0.8
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 22, 2013 at 03:19 PM
-- Server version: 5.5.32-cll
-- PHP Version: 5.3.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `jafarnej_df`
--

-- --------------------------------------------------------

--
-- Table structure for table `discounts`
--

CREATE TABLE IF NOT EXISTS `discounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id which is primary key',
  `item_name` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT 'discount item name',
  `rate` varchar(8) CHARACTER SET utf8 NOT NULL COMMENT 'rates ketp as a text in db',
  `location_txt` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT 'location entered by user',
  `latitude` varchar(128) CHARACTER SET utf8 DEFAULT NULL COMMENT 'latitude ',
  `longitude` varchar(128) CHARACTER SET utf8 DEFAULT NULL COMMENT 'longitude',
  `image_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT 'name of image in server',
  `user_id` int(11) DEFAULT NULL COMMENT 'user id will used in future',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
