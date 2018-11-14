-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: backupisreal
-- ------------------------------------------------------
-- Server version	5.6.40-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `checks`
--

DROP TABLE IF EXISTS `checks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checks` (
  `chk_seq` bigint(20) NOT NULL AUTO_INCREMENT,
  `chk_num` varchar(45) DEFAULT NULL,
  `empl_id` int(11) DEFAULT NULL,
  `empl_device_id` int(11) DEFAULT NULL,
  `chk_open` int(11) DEFAULT NULL,
  `tblno` int(11) DEFAULT NULL,
  `storeid` int(11) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sub_ttl` decimal(10,2) DEFAULT NULL,
  `tax_ttl` decimal(10,2) DEFAULT NULL,
  `pymnt_ttl` decimal(10,2) DEFAULT NULL,
  `due_ttl` decimal(10,2) DEFAULT NULL,
  `voidable` int(11) DEFAULT '0',
  `gst` decimal(10,2) DEFAULT '0.00',
  `sales_tax` decimal(10,2) DEFAULT '0.00',
  `service_tax` decimal(10,2) DEFAULT '0.00',
  `other_tax` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`chk_seq`),
  UNIQUE KEY `chk_num` (`chk_num`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checks`
--

LOCK TABLES `checks` WRITE;
/*!40000 ALTER TABLE `checks` DISABLE KEYS */;
INSERT INTO `checks` VALUES (1,'229',1,NULL,3,3,1,'2017-04-20 09:00:44',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(2,'230',1,NULL,3,3,1,'2017-04-20 09:52:21',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(3,'231',1,NULL,3,4,1,'2017-04-14 08:27:07',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(4,'232',1,NULL,3,2,1,'2017-04-14 08:24:07',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(5,'233',1,NULL,3,5,1,'2017-05-15 03:24:44',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(6,'234',1,NULL,3,12,1,'2017-05-15 03:42:39',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(7,'235',1,NULL,3,11,1,'2017-05-15 03:42:31',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(8,'236',1,NULL,3,5,1,'2017-04-14 08:24:33',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(9,'237',1,NULL,3,9,1,'2017-05-15 03:25:50',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(10,'238',1,NULL,3,11,1,'2017-05-15 03:42:06',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(11,'239',1,NULL,3,1,1,'2017-04-14 02:17:22',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(12,'240',1,NULL,3,7,1,'2017-04-20 07:03:26',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(13,'241',1,NULL,3,6,1,'2017-04-18 09:30:46',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(14,'242',1,NULL,3,1,1,'2017-04-14 03:08:36',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(15,'243',1,NULL,3,1,1,'2017-04-14 08:20:46',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(16,'244',1,NULL,3,8,1,'2017-05-15 03:25:03',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(17,'245',1,NULL,3,2,1,'2017-04-14 08:26:55',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(18,'246',1,NULL,3,4,1,'2017-04-18 08:50:37',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(19,'247',1,NULL,3,4,1,'2017-05-15 03:24:54',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(20,'248',1,NULL,3,2,1,'2017-04-19 09:29:28',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(21,'249',1,NULL,3,1,1,'2017-04-14 08:34:36',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(22,'250',1,NULL,3,1,1,'2017-04-14 08:37:22',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(23,'251',1,NULL,3,1,1,'2017-04-17 08:37:27',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(24,'252',1,NULL,3,1,1,'2017-04-17 08:54:11',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(25,'253',1,NULL,3,10,1,'2017-05-15 03:31:11',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(26,'254',1,NULL,3,13,1,'2017-05-15 06:16:39',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(27,'255',1,NULL,3,2,1,'2017-04-19 09:34:42',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(28,'256',1,NULL,3,2,1,'2017-04-19 09:39:04',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(29,'257',1,NULL,3,2,1,'2017-04-19 10:14:41',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(30,'258',1,NULL,3,2,1,'2017-04-17 09:52:52',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(31,'259',1,NULL,3,1,1,'2017-04-18 09:31:08',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(32,'260',1,NULL,3,1,1,'2017-04-18 09:31:21',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(33,'261',1,NULL,3,1,1,'2017-04-19 09:40:41',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(34,'262',1,NULL,3,1,1,'2017-04-19 10:13:36',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(35,'263',1,NULL,3,1,1,'2017-04-20 09:54:23',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(36,'264',1,NULL,3,2,1,'2017-04-20 09:55:09',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(37,'265',1,NULL,3,1,1,'2017-05-15 03:30:31',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(38,'266',1,NULL,3,6,1,'2017-05-15 05:59:36',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(39,'267',1,NULL,3,4,1,'2017-05-15 06:16:25',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(40,'268',1,NULL,3,3,1,'2017-05-15 06:16:17',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(41,'269',1,NULL,3,3,1,'2017-05-15 06:00:19',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(42,'270',1,NULL,3,12,1,'2017-05-15 06:20:47',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(43,'271',1,NULL,3,5,1,'2017-05-15 06:20:41',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(44,'272',1,NULL,3,13,1,'2017-05-15 06:20:31',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(45,'273',1,NULL,3,6,1,'2017-05-15 08:45:27',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(46,'274',1,NULL,3,30,1,'2017-05-18 08:46:57',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(47,'275',1,NULL,3,3,1,'2017-05-15 08:45:56',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(48,'276',1,NULL,3,3,1,'2017-05-15 08:46:27',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(49,'277',1,NULL,3,5,1,'2017-05-18 08:46:02',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(50,'278',1,NULL,3,1,1,'2017-05-15 09:12:19',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(51,'279',1,NULL,3,1,1,'2017-05-15 09:17:33',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(52,'280',1,NULL,3,1,1,'2017-05-15 09:18:52',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(53,'281',1,NULL,3,3,1,'2017-05-18 08:46:13',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(54,'282',1,NULL,3,1,1,'2017-05-18 08:46:36',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(55,'283',1,NULL,3,6,1,'2017-05-18 08:46:23',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(56,'284',1,NULL,3,5,1,'2017-05-25 08:47:33',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(57,'285',1,NULL,3,4,1,'2017-05-25 08:12:42',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(58,'286',1,NULL,2,12,1,'2017-05-18 08:48:48',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(59,'287',1,NULL,3,2,1,'2017-05-18 09:19:27',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(60,'288',1,NULL,3,5,1,'2017-05-25 08:13:40',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(61,'289',1,NULL,3,1,1,'2017-05-25 08:13:08',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(62,'290',1,NULL,3,1,1,'2017-05-25 08:12:59',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(63,'291',1,NULL,3,3,1,'2017-05-25 08:12:50',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(64,'292',1,NULL,3,2,1,'2017-05-25 08:12:35',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(65,'293',1,NULL,2,100,1,'2017-05-19 03:10:42',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(66,'294',1,NULL,2,70,1,'2017-05-19 08:22:38',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(67,'295',1,NULL,3,11,1,'2017-05-25 08:26:03',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(68,'296',1,NULL,2,8,1,'2017-05-19 09:31:32',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(69,'297',1,NULL,2,8,1,'2017-05-19 09:31:40',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(70,'298',1,NULL,2,8,1,'2017-05-19 09:31:45',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(71,'299',1,NULL,2,8,1,'2017-05-19 09:31:48',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(72,'300',1,NULL,3,6,1,'2017-05-25 08:12:00',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(73,'301',1,NULL,2,7,1,'2017-05-19 09:52:18',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(74,'302',1,NULL,3,9,1,'2017-05-25 08:13:33',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(75,'303',1,NULL,3,10,1,'2017-05-25 08:26:18',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(76,'304',1,NULL,2,13,1,'2017-05-19 09:53:33',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(77,'305',1,NULL,2,14,1,'2017-05-19 09:53:39',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(78,'306',1,NULL,3,15,1,'2017-05-25 08:25:51',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(79,'307',1,NULL,2,16,1,'2017-05-19 10:02:22',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(80,'308',1,NULL,3,17,1,'2017-05-25 08:12:14',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(81,'309',1,NULL,2,18,1,'2017-05-19 10:02:34',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(82,'310',1,NULL,3,1,1,'2017-05-25 08:13:19',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(83,'311',1,NULL,2,5,1,'2017-05-25 10:08:19',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(84,'312',1,NULL,2,10,1,'2017-05-26 02:49:34',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(85,'313',1,NULL,3,10,1,'2017-05-26 02:51:16',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(86,'314',1,NULL,3,1,1,'2017-05-26 02:52:01',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(87,'315',1,NULL,2,5,1,'2017-05-26 02:52:26',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(88,'316',1,NULL,3,2,1,'2017-05-26 02:53:04',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(89,'317',1,NULL,3,1,1,'2017-05-26 03:56:58',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(90,'318',1,NULL,3,1,1,'2017-06-29 07:57:07',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(91,'319',1,NULL,3,1,1,'2017-06-29 07:58:09',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(92,'320',1,NULL,3,2,1,'2017-06-29 06:02:17',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(93,'321',1,NULL,3,2,1,'2017-06-29 07:49:49',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(94,'322',1,NULL,3,3,1,'2017-06-29 07:56:48',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(95,'323',1,NULL,3,2,1,'2017-06-29 07:55:21',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(96,'324',1,NULL,3,3,1,'2017-06-29 07:59:49',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(97,'325',1,NULL,2,4,1,'2017-07-11 10:18:00',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00),(98,'326',1,NULL,3,6,1,'2018-03-22 01:31:13',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,0.00);
/*!40000 ALTER TABLE `checks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `details`
--

DROP TABLE IF EXISTS `details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chk_seq` bigint(20) DEFAULT NULL,
  `dtl_seq` int(11) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `chk_ttl` decimal(10,2) DEFAULT NULL,
  `detail_item_status` int(11) DEFAULT '0',
  `detail_type` varchar(15) NOT NULL,
  `detail_item_price` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=333 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `details`
--

LOCK TABLES `details` WRITE;
/*!40000 ALTER TABLE `details` DISABLE KEYS */;
INSERT INTO `details` VALUES (1,2,0,15,'Drink 2',14.50,0,'',0.00),(2,2,0,14,'Drink 1',14.50,0,'',0.00),(3,1,0,10,'Combo Meal 10',14.50,0,'',0.00),(4,1,0,11,'Combo Meal 11',14.50,0,'',0.00),(5,1,0,3,'Combo Meal 3',14.50,0,'',0.00),(6,1,0,4,'Combo Meal 4',14.50,0,'',0.00),(7,1,0,1,'Combo Meal 1',15.00,0,'',0.00),(8,1,0,11,'Combo Meal 11',14.50,0,'',0.00),(9,1,0,4,'Combo Meal 4',14.50,0,'',0.00),(10,1,0,8,'Combo Meal 8',14.50,0,'',0.00),(11,1,0,9,'Combo Meal 9',14.50,0,'',0.00),(12,1,0,5,'Combo Meal 5',14.50,0,'',0.00),(13,1,0,7,'Combo Meal 7',14.50,0,'',0.00),(14,3,0,1,'Combo Meal 1',15.00,0,'',0.00),(15,3,0,10,'Combo Meal 10',14.50,0,'',0.00),(16,3,0,11,'Combo Meal 11',14.50,0,'',0.00),(17,6,0,9,'Combo Meal 9',14.50,0,'',0.00),(18,6,0,5,'Combo Meal 5',14.50,0,'',0.00),(19,6,0,11,'Combo Meal 11',14.50,0,'',0.00),(20,0,0,12,'Ala-cart 1',14.50,0,'',0.00),(21,0,0,13,'Ala-cart 2',14.50,0,'',0.00),(22,0,0,12,'Ala-cart 1',14.50,0,'',0.00),(23,3,0,1,'Combo Meal 1',15.00,0,'',0.00),(24,3,0,10,'Combo Meal 10',14.50,0,'',0.00),(25,9,0,1,'Combo Meal 1',15.00,0,'',0.00),(26,9,0,10,'Combo Meal 10',14.50,0,'',0.00),(27,9,0,11,'Combo Meal 11',14.50,0,'',0.00),(28,9,0,2,'Combo Meal 2',14.00,0,'',0.00),(29,9,0,3,'Combo Meal 3',14.50,0,'',0.00),(30,9,0,4,'Combo Meal 4',14.50,0,'',0.00),(31,9,0,5,'Combo Meal 5',14.50,0,'',0.00),(32,9,0,6,'Combo Meal 6',14.50,0,'',0.00),(33,9,0,7,'Combo Meal 7',14.50,0,'',0.00),(34,9,0,9,'Combo Meal 9',14.50,0,'',0.00),(35,9,0,5,'Combo Meal 5',14.50,0,'',0.00),(36,4,0,0,'OpenItem_001',12.50,0,'',0.00),(37,4,0,0,'OpenItem_002',25.00,0,'',0.00),(38,4,0,0,'OpenItem_003',3.60,0,'',0.00),(39,10,0,1,'Combo Meal 1',15.00,0,'',0.00),(40,10,0,3,'Combo Meal 3',14.50,0,'',0.00),(41,10,0,1,'Combo Meal 1',15.00,0,'',0.00),(42,10,0,3,'Combo Meal 3',14.50,0,'',0.00),(43,4,0,1,'Combo Meal 1',15.00,0,'',0.00),(44,4,0,2,'Combo Meal 2',14.00,0,'',0.00),(45,4,0,4,'Combo Meal 4',14.50,0,'',0.00),(46,4,0,6,'Combo Meal 6',14.50,0,'',0.00),(47,4,0,5,'Combo Meal 5',14.50,0,'',0.00),(48,1,0,0,'OpenItem_001',2.00,0,'',0.00),(49,1,0,0,'OpenItem_002',3.00,0,'',0.00),(50,1,0,13,'Ala-cart 2',14.50,0,'',0.00),(51,1,0,12,'Ala-cart 1',14.50,0,'',0.00),(52,1,0,13,'Ala-cart 2',14.50,0,'',0.00),(53,4,0,1,'Combo Meal 1',15.00,0,'',0.00),(54,4,0,2,'Combo Meal 2',14.00,0,'',0.00),(55,4,0,3,'Combo Meal 3',14.50,0,'',0.00),(56,4,0,1,'Combo Meal 1',15.00,0,'',0.00),(57,4,0,2,'Combo Meal 2',14.00,0,'',0.00),(58,4,0,3,'Combo Meal 3',14.50,0,'',0.00),(59,4,0,4,'Combo Meal 4',14.50,0,'',0.00),(60,4,0,5,'Combo Meal 5',14.50,0,'',0.00),(61,4,0,6,'Combo Meal 6',14.50,0,'',0.00),(62,4,0,1,'Combo Meal 1',15.00,0,'',0.00),(63,4,0,2,'Combo Meal 2',14.00,0,'',0.00),(64,4,0,3,'Combo Meal 3',14.50,0,'',0.00),(65,4,0,4,'Combo Meal 4',14.50,0,'',0.00),(66,4,0,5,'Combo Meal 5',14.50,0,'',0.00),(67,4,0,6,'Combo Meal 6',14.50,0,'',0.00),(68,11,0,2,'Combo Meal 2',14.00,0,'',0.00),(69,11,0,3,'Combo Meal 3',14.50,0,'',0.00),(70,11,0,2,'Combo Meal 2',14.00,0,'',0.00),(71,11,0,3,'Combo Meal 3',14.50,0,'',0.00),(72,11,0,2,'Combo Meal 2',14.00,0,'',0.00),(73,11,0,2,'Combo Meal 2',14.00,0,'',0.00),(74,11,0,3,'Combo Meal 3',14.50,0,'',0.00),(75,11,0,2,'Combo Meal 2',14.00,0,'',0.00),(76,11,0,3,'Combo Meal 3',14.50,0,'',0.00),(77,14,0,1,'Combo Meal 1',15.00,0,'',0.00),(78,14,0,1,'Combo Meal 1',15.00,0,'',0.00),(79,14,0,1,'Combo Meal 1',15.00,0,'',0.00),(80,14,0,3,'Combo Meal 1',15.00,0,'',0.00),(81,14,0,1,'Combo Meal 1',15.00,0,'',0.00),(82,15,0,1,'Custom Amount',25.00,0,'',0.00),(83,15,0,1,'Custom Amount',25.00,0,'',0.00),(84,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(85,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(86,15,0,2,'Combo Meal 1',15.00,0,'',0.00),(87,15,0,2,'Custom Amount',25.00,0,'',0.00),(88,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(89,15,0,4,'Combo Meal 1',15.00,0,'',0.00),(90,15,0,3,'Custom Amount',25.00,0,'',0.00),(91,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(92,15,0,1,'Combo Meal 2',14.00,0,'',0.00),(93,15,0,1,'Combo Meal 3',14.50,0,'',0.00),(94,15,0,1,'Combo Meal 4',14.50,0,'',0.00),(95,15,0,3,'Combo Meal 2',14.00,0,'',0.00),(96,15,0,6,'Combo Meal 1',15.00,0,'',0.00),(97,15,0,2,'Combo Meal 2',14.00,0,'',0.00),(98,15,0,1,'Combo Meal 3',14.50,0,'',0.00),(99,15,0,1,'Combo Meal 4',14.50,0,'',0.00),(100,15,0,4,'Custom Amount',25.00,0,'',0.00),(101,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(102,15,0,8,'Combo Meal 1',15.00,0,'',0.00),(103,15,0,3,'Combo Meal 2',14.00,0,'',0.00),(104,15,0,2,'Combo Meal 3',14.50,0,'',0.00),(105,15,0,2,'Combo Meal 4',14.50,0,'',0.00),(106,15,0,5,'Custom Amount',25.00,0,'',0.00),(107,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(108,15,0,10,'Combo Meal 1',15.00,0,'',0.00),(109,15,0,4,'Combo Meal 2',14.00,0,'',0.00),(110,15,0,3,'Combo Meal 3',14.50,0,'',0.00),(111,15,0,3,'Combo Meal 4',14.50,0,'',0.00),(112,15,0,6,'Custom Amount',25.00,0,'',0.00),(113,15,0,1,'Custom Amount',15.00,0,'',0.00),(114,15,0,11,'Combo Meal 1',15.00,0,'',0.00),(115,15,0,5,'Combo Meal 2',14.00,0,'',0.00),(116,15,0,4,'Combo Meal 3',14.50,0,'',0.00),(117,15,0,4,'Combo Meal 4',14.50,0,'',0.00),(118,15,0,8,'Custom Amount',25.00,0,'',0.00),(119,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(120,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(121,15,0,14,'Combo Meal 1',15.00,0,'',0.00),(122,15,0,6,'Combo Meal 2',14.00,0,'',0.00),(123,15,0,5,'Combo Meal 3',14.50,0,'',0.00),(124,15,0,5,'Combo Meal 4',14.50,0,'',0.00),(125,15,0,9,'Custom Amount',25.00,0,'',0.00),(126,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(127,16,0,1,'Combo Meal 1',15.00,0,'',0.00),(128,16,0,1,'Combo Meal 1',15.00,0,'',0.00),(129,16,0,2,'Combo Meal 1',15.00,0,'',0.00),(130,16,0,1,'Combo Meal 1',15.00,0,'',0.00),(131,16,0,4,'Combo Meal 1',15.00,0,'',0.00),(132,16,0,1,'Combo Meal 1',15.00,0,'',0.00),(133,15,0,16,'Combo Meal 1',15.00,0,'',0.00),(134,15,0,7,'Combo Meal 2',14.00,0,'',0.00),(135,15,0,6,'Combo Meal 3',14.50,0,'',0.00),(136,15,0,6,'Combo Meal 4',14.50,0,'',0.00),(137,15,0,10,'Custom Amount',25.00,0,'',0.00),(138,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(139,15,0,18,'Combo Meal 1',15.00,0,'',0.00),(140,15,0,8,'Combo Meal 2',14.00,0,'',0.00),(141,15,0,7,'Combo Meal 3',14.50,0,'',0.00),(142,15,0,7,'Combo Meal 4',14.50,0,'',0.00),(143,15,0,11,'Custom Amount',25.00,0,'',0.00),(144,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(145,15,0,20,'Combo Meal 1',15.00,0,'',0.00),(146,15,0,9,'Combo Meal 2',14.00,0,'',0.00),(147,15,0,8,'Combo Meal 3',14.50,0,'',0.00),(148,15,0,8,'Combo Meal 4',14.50,0,'',0.00),(149,15,0,12,'Custom Amount',25.00,0,'',0.00),(150,15,0,1,'Combo Meal 1',15.00,0,'',0.00),(151,4,0,4,'Combo Meal 1',15.00,0,'',0.00),(152,4,0,4,'Combo Meal 2',14.00,0,'',0.00),(153,4,0,3,'Combo Meal 3',14.50,0,'',0.00),(154,4,0,3,'Combo Meal 4',14.50,0,'',0.00),(155,4,0,3,'Combo Meal 5',14.50,0,'',0.00),(156,4,0,3,'Combo Meal 6',14.50,0,'',0.00),(157,4,0,1,'OpenItem_001',12.50,0,'',0.00),(158,4,0,1,'OpenItem_002',25.00,0,'',0.00),(159,4,0,1,'OpenItem_003',3.60,0,'',0.00),(160,4,0,1,'Combo Meal 1',15.00,0,'',0.00),(161,8,0,1,'Custom Amount',35.00,0,'',0.00),(162,17,0,1,'Custom Amount',35.00,0,'',0.00),(163,19,0,1,'Custom Amount',35.00,0,'',0.00),(164,21,0,1,'Custom Amount',25.00,0,'',0.00),(165,22,0,1,'Combo Meal 2',14.00,0,'',0.00),(166,23,0,1,'Custom Amount',35.00,0,'',0.00),(167,23,0,1,'Combo Meal 1',15.00,0,'',0.00),(168,23,0,1,'Combo Meal 3',14.50,0,'',0.00),(169,24,0,1,'Custom Amount',25.00,0,'',0.00),(170,24,0,1,'Custom Amount',25.00,0,'',0.00),(171,24,0,1,'Combo Meal 1',15.00,0,'',0.00),(172,24,0,1,'Combo Meal 5',14.50,0,'',0.00),(173,30,0,1,'Combo Meal 1',15.00,0,'',0.00),(174,1,0,1,'Ala-cart 1',14.50,0,'',0.00),(175,1,0,2,'Ala-cart 2',14.50,0,'',0.00),(176,1,0,1,'Combo Meal 1',15.00,0,'',0.00),(177,1,0,1,'Combo Meal 10',14.50,0,'',0.00),(178,1,0,2,'Combo Meal 11',14.50,0,'',0.00),(179,1,0,1,'Combo Meal 3',14.50,0,'',0.00),(180,1,0,2,'Combo Meal 4',14.50,0,'',0.00),(181,1,0,1,'Combo Meal 5',14.50,0,'',0.00),(182,1,0,1,'Combo Meal 7',14.50,0,'',0.00),(183,1,0,1,'Combo Meal 8',14.50,0,'',0.00),(184,1,0,1,'Combo Meal 9',14.50,0,'',0.00),(185,1,0,1,'OpenItem_001',2.00,0,'',0.00),(186,1,0,1,'OpenItem_002',3.00,0,'',0.00),(187,1,0,1,'Combo Meal 2',14.00,0,'',0.00),(188,2,0,1,'Drink 1',14.50,0,'',0.00),(189,2,0,1,'Drink 2',14.50,0,'',0.00),(190,2,0,1,'Combo Meal 2',14.00,0,'',0.00),(191,2,0,1,'Combo Meal 1',15.00,0,'',0.00),(192,2,0,1,'Combo Meal 1',15.00,0,'',0.00),(193,2,0,1,'Combo Meal 2',14.00,0,'',0.00),(194,2,0,2,'Drink 1',14.50,0,'',0.00),(195,2,0,2,'Drink 2',14.50,0,'',0.00),(196,2,0,1,'Combo Meal 6',14.50,0,'',0.00),(197,2,0,1,'Combo Meal 5',14.50,0,'',0.00),(198,1,0,1,'Combo Meal 1',15.00,0,'',0.00),(199,18,0,1,'Combo Meal 1',15.00,0,'',0.00),(200,32,0,1,'Custom Amount',35.00,0,'',0.00),(201,13,0,1,'Combo Meal 1',15.00,0,'',0.00),(202,31,0,1,'Combo Meal 1',15.00,0,'',0.00),(203,20,0,1,'Combo Meal 3',14.50,0,'',0.00),(204,27,0,1,'Combo Meal 3',14.50,0,'',0.00),(205,28,0,1,'Combo Meal 2',14.00,0,'',0.00),(206,33,0,1,'Combo Meal 3',14.50,0,'',0.00),(207,33,0,1,'Combo Meal 3',14.50,0,'',0.00),(208,33,0,1,'Combo Meal 3',14.50,0,'',0.00),(209,34,0,1,'Combo Meal 1',15.00,0,'',0.00),(210,34,0,1,'Combo Meal 3',14.50,0,'',0.00),(211,34,0,1,'Combo Meal 1',15.00,0,'',0.00),(212,29,0,1,'Combo Meal 1',15.00,0,'',0.00),(213,12,0,1,'Custom Amount',35.00,0,'',0.00),(214,35,0,1,'Combo Meal 1',15.00,0,'',0.00),(215,35,0,2,'Combo Meal 5',14.50,0,'',0.00),(216,36,0,1,'Custom Amount',21.00,0,'',0.00),(217,25,0,0,'OpenItem_001',28.88,0,'',0.00),(218,25,0,0,'OpenItem_002',70.00,0,'',0.00),(219,25,0,15,'Drink 2',14.50,0,'',0.00),(220,25,0,14,'Drink 1',14.50,0,'',0.00),(221,5,0,1,'Combo Meal 2',14.00,0,'',0.00),(222,37,0,1,'Combo Meal 3',14.50,0,'',0.00),(223,7,0,1,'Combo Meal 1',15.00,0,'',0.00),(224,38,0,1,'Combo Meal 4',14.50,0,'',0.00),(225,39,0,1,'Combo Meal 1',15.00,0,'',0.00),(226,40,0,1,'Combo Meal 1',15.00,0,'',0.00),(227,41,0,1,'Combo Meal 1',15.00,0,'',0.00),(228,41,0,1,'Combo Meal 2',14.00,0,'',0.00),(229,41,0,1,'Combo Meal 3',14.50,0,'',0.00),(230,26,0,1,'Combo Meal 5',14.50,0,'',0.00),(231,42,0,1,'Combo Meal 4',14.50,0,'',0.00),(232,44,0,1,'Combo Meal 3',14.50,0,'',0.00),(233,43,0,1,'Combo Meal 5',14.50,0,'',0.00),(234,45,0,1,'Custom Amount',655.56,0,'',0.00),(235,46,0,1,'Custom Amount',9900.00,0,'',0.00),(236,46,0,1,'Custom Amount',900.00,0,'',0.00),(237,47,0,1,'Custom Amount',999.99,0,'',0.00),(238,48,0,1,'Custom Amount',900.00,0,'',0.00),(239,50,0,1,'Custom Amount',158.00,0,'',0.00),(240,52,0,1,'Custom Amount',23.12,0,'',0.00),(241,49,0,1,'Custom Amount',35.00,0,'',0.00),(242,53,0,1,'Custom Amount',35.00,0,'',0.00),(243,53,0,1,'Combo Meal 4',14.50,0,'',0.00),(244,53,0,2,'Combo Meal 5',14.50,0,'',0.00),(245,53,0,3,'Combo Meal 3',14.50,0,'',0.00),(246,53,0,1,'Combo Meal 2',14.00,0,'',0.00),(247,53,0,4,'Combo Meal 9',14.50,0,'',0.00),(248,53,0,1,'Combo Meal 6',14.50,0,'',0.00),(249,54,0,1,'Custom Amount',35.00,0,'',0.00),(250,54,0,1,'Combo Meal 6',14.50,0,'',0.00),(251,54,0,2,'Combo Meal 2',14.00,0,'',0.00),(252,54,0,2,'Combo Meal 4',14.50,0,'',0.00),(253,55,0,1,'Custom Amount',35.00,0,'',0.00),(254,56,0,1,'Combo Meal 1',15.00,0,'',0.00),(255,56,0,1,'Combo Meal 2',14.00,0,'',0.00),(256,56,0,1,'Combo Meal 3',14.50,0,'',0.00),(257,56,0,1,'Combo Meal 6',14.50,0,'',0.00),(258,57,0,1,'Combo Meal 1',15.00,0,'',0.00),(259,57,0,1,'Combo Meal 2',14.00,0,'',0.00),(260,57,0,1,'Combo Meal 3',14.50,0,'',0.00),(261,57,0,3,'Combo Meal 5',14.50,0,'',0.00),(262,58,0,2,'Combo Meal 1',15.00,0,'',0.00),(263,58,0,1,'Combo Meal 5',14.50,0,'',0.00),(264,58,0,1,'Combo Meal 6',14.50,0,'',0.00),(265,58,0,1,'Combo Meal 3',14.50,0,'',0.00),(266,59,0,1,'Combo Meal 1',15.00,0,'',0.00),(267,57,0,1,'Combo Meal 3',14.50,0,'',0.00),(268,57,0,1,'Custom Amount',0.04,0,'',0.00),(269,57,0,1,'Combo Meal 3',14.50,0,'',0.00),(270,60,0,1,'Combo Meal 2',14.00,0,'',0.00),(271,60,0,1,'Combo Meal 2',14.00,0,'',0.00),(272,60,0,2,'Combo Meal 2',14.00,0,'',0.00),(273,60,0,1,'Custom Amount',0.65,0,'',0.00),(274,61,0,1,'Combo Meal 1',15.00,0,'',0.00),(275,61,0,1,'Combo Meal 2',14.00,0,'',0.00),(276,61,0,1,'Combo Meal 3',14.50,0,'',0.00),(277,61,0,2,'Combo Meal 1',15.00,0,'',0.00),(278,61,0,2,'Combo Meal 1',15.00,0,'',0.00),(279,62,0,2,'Combo Meal 1',15.00,0,'',0.00),(280,63,0,1,'Custom Amount',35.00,0,'',0.00),(281,63,0,1,'Combo Meal 3',14.50,0,'',0.00),(282,63,0,1,'Combo Meal 2',14.00,0,'',0.00),(283,63,0,1,'Combo Meal 1',15.00,0,'',0.00),(284,63,0,3,'Combo Meal 4',14.50,0,'',0.00),(285,63,0,1,'Combo Meal 3',14.50,0,'',0.00),(286,63,0,2,'Combo Meal 2',14.00,0,'',0.00),(287,56,0,2,'Combo Meal 5',14.50,0,'',0.00),(288,56,0,1,'Combo Meal 1',15.00,0,'',0.00),(289,64,0,2,'Combo Meal 1',15.00,0,'',0.00),(290,65,0,1,'Custom Amount',35.00,0,'',0.00),(291,66,0,1,'Combo Meal 3',14.50,0,'',0.00),(292,66,0,2,'Combo Meal 6',14.50,0,'',0.00),(293,64,0,1,'Ducksss',369.00,0,'',0.00),(294,64,0,3,'Chicks',36.90,0,'',0.00),(295,64,0,3,'French Fries',8.00,0,'',0.00),(296,72,0,1,'Custom Amount',0.65,0,'',0.00),(297,80,0,1,'Custom Amount',6.00,0,'',0.00),(298,64,0,1,'Custom Amount',6.00,0,'',0.00),(299,82,0,1,'Custom Amount',6.00,0,'',0.00),(300,74,0,1,'Custom Amount',9.00,0,'',0.00),(301,78,0,1,'Combo Meal 1',15.00,0,'',0.00),(302,78,0,1,'Combo Meal 4',14.50,0,'',0.00),(303,78,0,1,'Combo Meal 5',14.50,0,'',0.00),(304,78,0,1,'Combo Meal 6',14.50,0,'',0.00),(305,67,0,1,'Custom Amount',28.00,0,'',0.00),(306,75,0,1,'Custom Amount',5.00,0,'',0.00),(307,56,0,1,'Ducksss',369.00,0,'',0.00),(308,56,0,1,'Quekkkk',25.00,0,'',0.00),(309,56,0,1,'Chicks',36.90,0,'',0.00),(310,83,0,1,'Combo Meal 2',14.00,0,'',0.00),(311,83,0,1,'Combo Meal 3',14.50,0,'',0.00),(312,83,0,1,'Combo Meal 1',15.00,0,'',0.00),(313,85,0,1,'Custom Amount',6586.00,0,'',0.00),(314,86,0,1,'Combo Meal 1',15.00,0,'',0.00),(315,86,0,1,'Combo Meal 2',14.00,0,'',0.00),(316,86,0,1,'French Fries',8.00,0,'',0.00),(317,86,0,1,'Chicks',36.90,0,'',0.00),(318,88,0,1,'Custom Amount',2542735.75,0,'',0.00),(319,89,0,1,'French Fries',8.00,0,'',0.00),(320,90,0,1,'Combo Meal 3',14.50,0,'',0.00),(321,91,0,1,'Combo Meal 1',15.00,0,'',0.00),(322,92,0,1,'Combo Meal 8',14.50,0,'',0.00),(323,92,0,1,'Combo Meal 6',14.50,0,'',0.00),(324,93,0,1,'Custom Amount',35.00,0,'',0.00),(325,94,0,2,'Combo Meal 3',14.50,0,'',0.00),(326,95,0,1,'Custom Amount',35.00,0,'',0.00),(327,96,0,1,'Combo Meal 3',14.50,0,'',0.00),(328,97,0,3,'Combo Meal 1',15.00,0,'',0.00),(329,98,0,1,'Combo Meal 2',14.00,0,'',0.00),(330,98,0,2,'Combo Meal 3',14.50,0,'',0.00),(331,98,0,1,'Combo Meal 6',14.50,0,'',0.00),(332,98,0,1,'Combo Meal 3',14.50,0,'',0.00);
/*!40000 ALTER TABLE `details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empldef`
--

DROP TABLE IF EXISTS `empldef`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empldef` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `storeid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empldef`
--

LOCK TABLES `empldef` WRITE;
/*!40000 ALTER TABLE `empldef` DISABLE KEYS */;
INSERT INTO `empldef` VALUES (1,'admin','admin','Administrator',1,1);
/*!40000 ALTER TABLE `empldef` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itemgroup`
--

DROP TABLE IF EXISTS `itemgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `itemgroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupname` varchar(100) DEFAULT NULL,
  `grouptype` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `grouptype` (`grouptype`),
  UNIQUE KEY `groupname_UNIQUE` (`groupname`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itemgroup`
--

LOCK TABLES `itemgroup` WRITE;
/*!40000 ALTER TABLE `itemgroup` DISABLE KEYS */;
INSERT INTO `itemgroup` VALUES (1,'Combo',1),(2,'Ala-carte',2),(3,'Drink',3),(4,'Hello',NULL),(5,'Hello1',NULL),(6,'Hello2',NULL),(7,'Hello3',NULL),(8,'Hello4',NULL),(9,'Hello5',NULL),(10,'Hello6',NULL),(11,'Hello7',NULL),(12,'Hello8',NULL),(13,'Hello9',NULL),(14,'Snack',NULL),(15,'CHICKS',NULL);
/*!40000 ALTER TABLE `itemgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `masterchecks`
--

DROP TABLE IF EXISTS `masterchecks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `masterchecks` (
  `chk_num` bigint(20) DEFAULT '1',
  UNIQUE KEY `chk_num` (`chk_num`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `masterchecks`
--

LOCK TABLES `masterchecks` WRITE;
/*!40000 ALTER TABLE `masterchecks` DISABLE KEYS */;
INSERT INTO `masterchecks` VALUES (326);
/*!40000 ALTER TABLE `masterchecks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mastertransaction`
--

DROP TABLE IF EXISTS `mastertransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mastertransaction` (
  `transaction_no` int(11) NOT NULL,
  `record_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mastertransaction`
--

LOCK TABLES `mastertransaction` WRITE;
/*!40000 ALTER TABLE `mastertransaction` DISABLE KEYS */;
INSERT INTO `mastertransaction` VALUES (0,'2018-11-12 09:09:53');
/*!40000 ALTER TABLE `mastertransaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menudef`
--

DROP TABLE IF EXISTS `menudef`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menudef` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `itemcode` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `itemgroup_id` int(11) DEFAULT NULL,
  `itemstatus` int(11) DEFAULT '0',
  `gstgroup_id` int(11) DEFAULT NULL,
  `itemtype` int(11) DEFAULT '0',
  `sststatus` int(11) DEFAULT '1',
  `image_path` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `itemcode` (`itemcode`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menudef`
--

LOCK TABLES `menudef` WRITE;
/*!40000 ALTER TABLE `menudef` DISABLE KEYS */;
INSERT INTO `menudef` VALUES (1,'1001','Combo Meal 1',15.00,1,0,2,0,1,NULL),(2,'1002','Combo Meal 2',14.00,1,0,2,0,1,NULL),(3,'1003','Combo Meal 3',14.50,1,0,2,0,1,NULL),(4,'1004','Combo Meal 4',14.50,1,0,2,0,1,NULL),(5,'1005','Combo Meal 5',14.50,1,0,2,0,1,NULL),(6,'1006','Combo Meal 6',14.50,1,0,2,0,1,NULL),(7,'1007','Combo Meal 7',14.50,1,0,2,0,1,NULL),(8,'1008','Combo Meal 8',14.50,1,0,2,0,1,NULL),(9,'1009','Combo Meal 9',14.50,1,0,2,0,1,NULL),(10,'1010','Combo Meal 10',14.50,1,0,2,0,1,NULL),(11,'1011','Combo Meal 11',14.50,1,0,2,0,1,NULL),(12,'2001','Ala-cart 1',14.50,2,0,2,0,1,NULL),(13,'2002','Ala-cart 2',14.50,2,0,2,0,1,NULL),(14,'3001','Drink 1',14.50,3,0,2,0,1,NULL),(15,'3002','Drink 2',14.50,3,0,2,0,1,NULL),(16,'14001','French Fries',8.00,14,0,1,0,1,'/img/itemimg/ITM_14002.jpg'),(18,'15001','Chicks',36.90,15,0,1,0,1,'/img/itemimg/ITM_15001.jpg'),(19,'15002','Ducksss',369.00,15,0,1,0,1,'/img/itemimg/ITM_15002.jpg'),(20,'1012','Comno',33.30,1,0,1,0,1,NULL),(21,'1013','Fgh',56.90,1,0,1,0,1,'/img/itemimg/ITM_1013.jpg'),(22,'15003','Quekkkk',25.00,15,0,1,0,1,'/img/itemimg/ITM_15003.jpg');
/*!40000 ALTER TABLE `menudef` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `printer`
--

DROP TABLE IF EXISTS `printer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `printer` (
  `printer_model` varchar(45) NOT NULL,
  `paper_size` int(11) DEFAULT '0',
  `port_name` varchar(45) NOT NULL,
  UNIQUE KEY `port_name_UNIQUE` (`port_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `printer`
--

LOCK TABLES `printer` WRITE;
/*!40000 ALTER TABLE `printer` DISABLE KEYS */;
/*!40000 ALTER TABLE `printer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlement`
--

DROP TABLE IF EXISTS `settlement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement` (
  `id` int(11) NOT NULL,
  `merchant_info` varchar(45) NOT NULL,
  `bank_tid` varchar(45) NOT NULL,
  `bank_mid` varchar(45) NOT NULL,
  `batch_no` varchar(45) NOT NULL,
  `tranx_date` varchar(45) NOT NULL,
  `tranx_time` varchar(45) NOT NULL,
  `batch_ttl` int(11) DEFAULT '0',
  `nii` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlement`
--

LOCK TABLES `settlement` WRITE;
/*!40000 ALTER TABLE `settlement` DISABLE KEYS */;
/*!40000 ALTER TABLE `settlement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storegroup`
--

DROP TABLE IF EXISTS `storegroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storegroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `storename` varchar(100) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `contact` varchar(100) DEFAULT NULL,
  `tbl` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `storename` (`storename`),
  UNIQUE KEY `address` (`address`),
  UNIQUE KEY `contact` (`contact`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storegroup`
--

LOCK TABLES `storegroup` WRITE;
/*!40000 ALTER TABLE `storegroup` DISABLE KEYS */;
INSERT INTO `storegroup` VALUES (1,'Administrator','Jalan 22, PJU1, Petaling Jaya','0102219948',20),(2,'KFC Subang','KFC Subang','KFC Subangtable=10',0),(3,'KFC KLCC','KFC KLCC','KFC KLCCtable=30',0),(4,'KFC PJS','KFC PJS','KFC PJStable=30',0);
/*!40000 ALTER TABLE `storegroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `syslu_gstgroup`
--

DROP TABLE IF EXISTS `syslu_gstgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syslu_gstgroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `syslu_gstgroup`
--

LOCK TABLES `syslu_gstgroup` WRITE;
/*!40000 ALTER TABLE `syslu_gstgroup` DISABLE KEYS */;
INSERT INTO `syslu_gstgroup` VALUES (1,'SR','Standard Rate'),(2,'ZR','Zero Rate');
/*!40000 ALTER TABLE `syslu_gstgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system`
--

DROP TABLE IF EXISTS `system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system` (
  `version` varchar(100) DEFAULT NULL,
  `propertyname` varchar(100) DEFAULT NULL,
  `licensekey` varchar(100) DEFAULT NULL,
  `license_expiredate` varchar(100) DEFAULT NULL,
  `businessdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `table_count` int(11) DEFAULT NULL,
  `gst_percentage` int(11) DEFAULT '0',
  `sales_tax_percentage` int(11) DEFAULT '0',
  `service_tax_percentage` int(11) DEFAULT '0',
  `other_tax_percentage` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system`
--

LOCK TABLES `system` WRITE;
/*!40000 ALTER TABLE `system` DISABLE KEYS */;
INSERT INTO `system` VALUES ('alpha1.0','Managepay Technology','##121212##',NULL,'2017-01-26 04:01:37',100,0,0,0,0);
/*!40000 ALTER TABLE `system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tax`
--

DROP TABLE IF EXISTS `tax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tax` (
  `rate` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tax`
--

LOCK TABLES `tax` WRITE;
/*!40000 ALTER TABLE `tax` DISABLE KEYS */;
INSERT INTO `tax` VALUES (6);
/*!40000 ALTER TABLE `tax` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terminal`
--

DROP TABLE IF EXISTS `terminal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `terminal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `terminalName` varchar(45) NOT NULL,
  `wifiIP` varchar(45) NOT NULL,
  `wifiPort` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `wifiIP_UNIQUE` (`wifiIP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal`
--

LOCK TABLES `terminal` WRITE;
/*!40000 ALTER TABLE `terminal` DISABLE KEYS */;
/*!40000 ALTER TABLE `terminal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `tran_id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` varchar(45) DEFAULT NULL,
  `check_no` varchar(45) DEFAULT NULL,
  `tran_type` varchar(15) DEFAULT '',
  `mtrx_id` int(11) DEFAULT '0',
  `tran_status` varchar(45) DEFAULT '',
  `auth_code` varchar(15) DEFAULT '',
  `payment_type` varchar(25) DEFAULT '',
  `tran_datetime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invoice_no` varchar(45) DEFAULT '',
  `trace_no` varchar(15) DEFAULT '',
  `batch_no` varchar(15) DEFAULT '',
  `bank_mid` varchar(45) DEFAULT '',
  `bank_tid` varchar(45) DEFAULT '',
  `aid` varchar(45) DEFAULT '',
  `app_label` varchar(45) DEFAULT '',
  `masked_cardno` varchar(45) DEFAULT '',
  `cardholder_name` varchar(45) DEFAULT '',
  `tc` varchar(45) DEFAULT '',
  `performBy` int(11) DEFAULT NULL,
  `isSettlement` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`tran_id`),
  KEY `TRAN_CHECK_NO_idx` (`check_no`),
  KEY `tran_staffid_idx` (`performBy`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,'41.1','232','Sales',181017,'APPROVED','028614','CHIP','2017-03-27 11:24:19','','150159','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','1C93802962FCDD91',1,0),(2,'29.00','230','Sales',181023,'APPROVED','03524','CHIP','2017-03-28 10:11:42','','150199','000005','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','1C93802962FCDD91',1,0),(3,'69.5','239','Sales',181177,'APPROVED','036715','CHIP','2017-04-01 14:01:16','','150163','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','723970424BC5764C',1,0),(4,'49.75','240','Sales',181178,'APPROVED','057561','CHIP','2017-04-01 15:31:12','','150164','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','BF19AF467723688A',1,0),(5,'22.49','241','Sales',181179,'APPROVED','088849','CHIP','2017-04-01 15:35:38','','150165','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','D2B2BE6B5FC3A416',1,0),(6,'26','242','Sales',181180,'APPROVED','022503','CHIP','2017-04-01 15:41:43','','150166','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','2BAA9F47B3B4077D',1,0),(7,'43.5','234','Sales',181183,'APPROVED','061259','CHIP','2017-04-01 15:48:25','','150169','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','476A22926C411B8F',1,0),(8,'25.8','245','Sales',181184,'APPROVED','067152','CHIP','2017-04-01 15:50:08','','150170','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','D53A7E6D601C0D13',1,0),(9,'33','246','Sales',181185,'APPROVED','062375','CHIP','2017-04-01 15:52:53','','150171','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','F42F5A4C75007FD4',1,0),(10,'35.9','248','Sales',181237,'APPROVED','072191','CHIP','2017-04-04 10:41:15','','150173','000004','000011111100104','11100104','a0000000041010','MasterCard','546015******1175','PING/CHOI JIEN           ','41A235FA5FA18EE9',1,0),(19,'0.0','239','Sales',0,'APPROVED','','Cash','2017-04-11 12:13:21','','','','','','','','','','',NULL,0),(20,'0.0','239','Sales',0,'APPROVED','','Cash','2017-04-14 10:17:22','','','','','','','','','','',NULL,0),(21,'60.0','242','Sales',0,'APPROVED','','Cash','2017-04-14 11:08:35','','','','','','','','','','',NULL,0),(22,'0.0','243','Sales',0,'APPROVED','','Cash','2017-04-14 04:20:45','','','','','','','','','','',NULL,0),(23,'346.1','232','Sales',0,'APPROVED','','Cash','2017-04-14 04:24:06','','','','','','','','','','',NULL,0),(24,'35.0','236','Sales',0,'APPROVED','','Cash','2017-04-14 04:24:32','','','','','','','','','','',NULL,0),(25,'35.0','245','Sales',0,'APPROVED','','Cash','2017-04-14 04:26:54','','','','','','','','','','',NULL,0),(26,'0.0','231','Sales',0,'APPROVED','','Cash','2017-04-14 04:27:06','','','','','','','','','','',NULL,0),(27,'25.0','249','Sales',0,'APPROVED','','Cash','2017-04-14 04:34:35','','','','','','','','','','',NULL,0),(28,'14.0','250','Sales',0,'APPROVED','','Cash','2017-04-14 04:37:21','','','','','','','','','','',NULL,0),(29,'64.5','251','Sales',0,'APPROVED','','Cash','2017-04-17 03:18:42','','','','','','','','','','',NULL,0),(30,'0.0','252','Sales',0,'APPROVED','','Cash','2017-04-17 03:35:26','','','','','','','','','','',NULL,0),(31,'15.0','258','Sales',0,'APPROVED','','Cash','2017-04-17 04:34:07','','','','','','','','','','',NULL,0),(32,'15.0','246','Sales',0,'APPROVED','','Cash','2017-04-18 04:50:36','','','','','','','','','','',NULL,0),(33,'15.0','241','Sales',0,'APPROVED','','Cash','2017-04-18 05:30:45','','','','','','','','','','',NULL,0),(34,'15.0','259','Sales',0,'APPROVED','','Cash','2017-04-18 05:31:07','','','','','','','','','','',NULL,0),(35,'0.0','260','Sales',0,'APPROVED','','Cash','2017-04-18 05:31:20','','','','','','','','','','',NULL,0),(36,'14.5','248','Sales',0,'APPROVED','','Cash','2017-04-19 05:29:27','','','','','','','','','','',NULL,0),(37,'14.5','255','Sales',0,'APPROVED','','Cash','2017-04-19 05:34:42','','','','','','','','','','',NULL,0),(38,'14.0','256','Sales',0,'APPROVED','','Cash','2017-04-19 05:39:03','','','','','','','','','','',NULL,0),(39,'0.0','261','Sales',0,'APPROVED','','Cash','2017-04-19 05:40:40','','','','','','','','','','',NULL,0),(40,'44.5','262','Sales',0,'APPROVED','','Cash','2017-04-19 06:13:35','','','','','','','','','','',NULL,0),(41,'15.0','257','Sales',0,'APPROVED','','Cash','2017-04-19 06:14:41','','','','','','','','','','',NULL,0),(42,'35.0','240','Sales',0,'APPROVED','','Cash','2017-04-20 03:03:26','','','','','','','','','','',NULL,0),(43,'0.0','229','Sales',0,'APPROVED','','Cash','2017-04-20 05:00:43','','','','','','','','','','',NULL,0),(44,'0.0','230','Sales',0,'APPROVED','','Cash','2017-04-20 05:52:21','','','','','','','','','','',NULL,0),(45,'44.0','263','Sales',0,'APPROVED','','Cash','2017-04-20 05:54:23','','','','','','','','','','',NULL,0),(46,'21.0','264','Sales',0,'APPROVED','','Cash','2017-04-20 05:55:09','','','','','','','','','','',NULL,0),(47,'14.0','233','Sales',0,'APPROVED','','Cash','2017-05-15 11:24:43','','','','','','','','','','',NULL,0),(48,'0.0','247','Sales',0,'APPROVED','','Cash','2017-05-15 11:24:53','','','','','','','','','','',NULL,0),(49,'0.0','244','Sales',0,'APPROVED','','Cash','2017-05-15 11:25:02','','','','','','','','','','',NULL,0),(50,'0.0','237','Sales',0,'APPROVED','','Cash','2017-05-15 11:25:49','','','','','','','','','','',NULL,0),(51,'14.5','265','Sales',0,'APPROVED','','Cash','2017-05-15 11:30:31','','','','','','','','','','',NULL,0),(52,'0.0','253','Sales',0,'APPROVED','','Cash','2017-05-15 11:31:10','','','','','','','','','','',NULL,0),(53,'59.0','238','Sales',0,'APPROVED','','Cash','2017-05-15 11:42:05','','','','','','','','','','',NULL,0),(54,'15.0','235','Sales',0,'APPROVED','','Cash','2017-05-15 11:42:30','','','','','','','','','','',NULL,0),(55,'43.5','234','Sales',0,'APPROVED','','Cash','2017-05-15 11:42:38','','','','','','','','','','',NULL,0),(56,'14.5','266','Sales',0,'APPROVED','','Cash','2017-05-15 01:59:36','','','','','','','','','','',NULL,0),(57,'43.5','269','Sales',0,'APPROVED','','Cash','2017-05-15 02:00:18','','','','','','','','','','',NULL,0),(58,'15.0','268','Sales',0,'APPROVED','','Cash','2017-05-15 02:16:17','','','','','','','','','','',NULL,0),(59,'15.0','267','Sales',0,'APPROVED','','Cash','2017-05-15 02:16:24','','','','','','','','','','',NULL,0),(60,'14.5','254','Sales',0,'APPROVED','','Cash','2017-05-15 02:16:38','','','','','','','','','','',NULL,0),(61,'14.5','272','Sales',0,'APPROVED','','Cash','2017-05-15 02:20:30','','','','','','','','','','',NULL,0),(62,'14.5','271','Sales',0,'APPROVED','','Cash','2017-05-15 02:20:40','','','','','','','','','','',NULL,0),(63,'14.5','270','Sales',0,'APPROVED','','Cash','2017-05-15 02:20:47','','','','','','','','','','',NULL,0),(64,'655.55','273','Sales',0,'APPROVED','','Cash','2017-05-15 04:45:27','','','','','','','','','','',NULL,0),(65,'1000.0','275','Sales',0,'APPROVED','','Cash','2017-05-15 04:45:56','','','','','','','','','','',NULL,0),(66,'900.0','276','Sales',0,'APPROVED','','Cash','2017-05-15 04:46:27','','','','','','','','','','',NULL,0),(67,'158.0','278','Sales',0,'APPROVED','','Cash','2017-05-15 05:12:19','','','','','','','','','','',NULL,0),(68,'23.1','279','Sales',0,'APPROVED','','Cash','2017-05-15 17:17:33','','','','','','','','','','',NULL,0),(69,'23.1','280','Sales',0,'APPROVED','','Cash','2017-05-15 17:18:52','','','','','','','','','','',NULL,0),(70,'35.0','277','Sales',0,'APPROVED','','Cash','2017-05-18 14:58:39','','','','','','','','','','',NULL,0),(71,'121.5','281','Sales',0,'APPROVED','','Cash','2017-05-18 14:58:49','','','','','','','','','','',NULL,0),(72,'35.0','283','Sales',0,'APPROVED','','Cash','2017-05-18 14:59:00','','','','','','','','','','',NULL,0),(73,'78.0','282','Sales',0,'APPROVED','','Cash','2017-05-18 14:59:13','','','','','','','','','','',NULL,0),(74,'19800.0','274','Sales',0,'APPROVED','','Cash','2017-05-18 14:59:34','','','','','','','','','','',NULL,0),(75,'15.0','287','Sales',0,'APPROVED','','Cash','2017-05-18 15:32:03','','','','','','','','','','',NULL,0),(76,'0.65','300','Sales',0,'APPROVED','','Cash','2017-05-25 16:12:04','','','','','','','','','','',NULL,0),(77,'6.0','308','Sales',0,'APPROVED','','Cash','2017-05-25 16:12:18','','','','','','','','','','',NULL,0),(78,'539.7','292','Sales',0,'APPROVED','','Cash','2017-05-25 16:12:39','','','','','','','','','','',NULL,0),(79,'87.05','285','Sales',0,'APPROVED','','Cash','2017-05-25 16:12:46','','','','','','','','','','',NULL,0),(80,'122.0','291','Sales',0,'APPROVED','','Cash','2017-05-25 16:12:53','','','','','','','','','','',NULL,0),(81,'30.0','290','Sales',0,'APPROVED','','Cash','2017-05-25 16:13:03','','','','','','','','','','',NULL,0),(82,'43.5','289','Sales',0,'APPROVED','','Cash','2017-05-25 16:13:12','','','','','','','','','','',NULL,0),(83,'6.0','310','Sales',0,'APPROVED','','Cash','2017-05-25 16:13:23','','','','','','','','','','',NULL,0),(84,'9.0','302','Sales',0,'APPROVED','','Cash','2017-05-25 16:13:36','','','','','','','','','','',NULL,0),(85,'14.65','288','Sales',0,'APPROVED','','Cash','2017-05-25 16:13:44','','','','','','','','','','',NULL,0),(86,'58.5','306','Sales',0,'APPROVED','','Cash','2017-05-25 16:25:55','','','','','','','','','','',NULL,0),(87,'28.0','295','Sales',0,'APPROVED','','Cash','2017-05-25 16:26:07','','','','','','','','','','',NULL,0),(88,'5.0','303','Sales',0,'APPROVED','','Cash','2017-05-25 16:26:22','','','','','','','','','','',NULL,0),(89,'517.9','284','Sales',0,'APPROVED','','Cash','2017-05-25 16:47:33','','','','','','','','','','',NULL,0),(90,'6586.0','313','Sales',0,'APPROVED','','Cash','2017-05-26 10:51:20','','','','','','','','','','',NULL,0),(91,'73.9','314','Sales',0,'APPROVED','','Cash','2017-05-26 10:52:05','','','','','','','','','','',NULL,0),(92,'2288462.2','316','Sales',0,'APPROVED','','Cash','2017-05-26 10:53:08','','','','','','','','','','',NULL,0),(93,'8.0','317','Sales',0,'APPROVED','','Cash','2017-05-26 11:57:03','','','','','','','','','','',NULL,0),(94,'29.0','320','0200',0,'APPROVED','','VISA DEBIT','2017-06-29 14:02:17','','','','','','','','','','',NULL,0),(95,'35.0','321','0200',0,'APPROVED','','MASTERCARD','2017-06-29 15:49:48','','','','','','','','','','',NULL,0),(96,'35.0','323','0200',0,'APPROVED','','VISA CREDIT','2017-06-29 15:55:21','','','','','','','','','','',NULL,0),(97,'29.0','322','0200',0,'APPROVED','','VISA CREDIT','2017-06-29 15:56:48','','','','','','','','','','',NULL,0),(98,'14.5','318','Sales',0,'APPROVED','','Cash','2017-06-29 15:57:07','','','','','','','','','','',NULL,0),(99,'15.0','319','Sales',0,'APPROVED','','Cash','2017-06-29 15:58:09','','','','','','','','','','',NULL,0),(100,'14.5','324','0200',0,'APPROVED','','VISA CREDIT','2017-06-29 15:59:49','','','','','','','','','','',NULL,0),(101,'57.5','326','Sales',0,'APPROVED','','Cash','2018-03-22 09:31:13','','','','','','','','','','',NULL,0);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-12 17:27:03
