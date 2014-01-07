-- MySQL dump 10.13  Distrib 5.5.32, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: shiwa_repo
-- ------------------------------------------------------
-- Server version	5.5.32-0ubuntu0.13.04.1

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
-- Table structure for table `app_attribute`
--

DROP TABLE IF EXISTS `app_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_attribute` (
  `id` int(11) NOT NULL,
  `app_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `attr_value` varchar(10000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_app_id_attr_name` (`app_id`,`name`) USING BTREE,
  CONSTRAINT `fk_app_attr_app_id` FOREIGN KEY (`app_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_attribute`
--

LOCK TABLES `app_attribute` WRITE;
/*!40000 ALTER TABLE `app_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `app_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_comment`
--

DROP TABLE IF EXISTS `app_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_comment` (
  `id` int(11) NOT NULL,
  `app_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `message` varchar(5000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_id_user_id_created` (`app_id`,`user_id`,`created`),
  KEY `fk_app_com_app_id` (`app_id`),
  KEY `fk_app_com_user_id` (`user_id`),
  CONSTRAINT `fk_app_com_app_id` FOREIGN KEY (`app_id`) REFERENCES `application` (`id`),
  CONSTRAINT `fk_app_com_user_id` FOREIGN KEY (`user_id`) REFERENCES `repo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_comment`
--

LOCK TABLES `app_comment` WRITE;
/*!40000 ALTER TABLE `app_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `app_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` int(11) NOT NULL,
  `name` varchar(250) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `description` varchar(5000) NOT NULL,
  `group_read` tinyint(1) NOT NULL DEFAULT '0',
  `group_download` tinyint(1) NOT NULL DEFAULT '0',
  `group_modify` tinyint(1) NOT NULL DEFAULT '0',
  `others_read` tinyint(1) NOT NULL DEFAULT '0',
  `others_download` tinyint(1) NOT NULL DEFAULT '0',
  `published` tinyint(1) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_appname` (`name`) USING BTREE,
  KEY `fk_app_owner_id` (`owner_id`),
  KEY `fk_app_group_id` (`group_id`),
  CONSTRAINT `fk_app_group_id` FOREIGN KEY (`group_id`) REFERENCES `repo_group` (`id`),
  CONSTRAINT `fk_app_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `repo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backend`
--

DROP TABLE IF EXISTS `backend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `backend` (
  `idBackend` int(11) NOT NULL AUTO_INCREMENT,
  `backendName` varchar(50) DEFAULT NULL,
  `version` varchar(50) DEFAULT NULL,
  `backendDesc` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`idBackend`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backend`
--

LOCK TABLES `backend` WRITE;
/*!40000 ALTER TABLE `backend` DISABLE KEYS */;
INSERT INTO `backend` VALUES (1,'GT4','4.0.23','A new Backend!'),(2,'GT2','2.0.1','A second Backend!'),(3,'gLite','3.2.1','An old backend!'),(4,'GT4','4.0.23','A new Backend!'),(5,'GT2','2.0.1','A second Backend!'),(6,'gLite','3.2.1','An old backend!');
/*!40000 ALTER TABLE `backend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `be_instance`
--

DROP TABLE IF EXISTS `be_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `be_instance` (
  `idBackendInst` int(11) NOT NULL AUTO_INCREMENT,
  `backendInstName` varchar(50) DEFAULT NULL,
  `idBackend` int(11) NOT NULL,
  `site` varchar(100) DEFAULT NULL,
  `backendOutput` varchar(200) DEFAULT NULL,
  `backendErrorOut` varchar(200) DEFAULT NULL,
  `maximumParallelism` int(11) DEFAULT NULL,
  `jobManager` varchar(50) DEFAULT NULL,
  `jobTypeId` int(11) NOT NULL,
  `idOS` int(11) NOT NULL,
  PRIMARY KEY (`idBackendInst`),
  KEY `idOS` (`idOS`),
  KEY `idBackend` (`idBackend`),
  KEY `jobTypeId` (`jobTypeId`),
  CONSTRAINT `be_instance_ibfk_1` FOREIGN KEY (`idOS`) REFERENCES `operating_systems` (`idOS`),
  CONSTRAINT `be_instance_ibfk_2` FOREIGN KEY (`idBackend`) REFERENCES `backend` (`idBackend`),
  CONSTRAINT `be_instance_ibfk_3` FOREIGN KEY (`jobTypeId`) REFERENCES `job_type` (`jobTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `be_instance`
--

LOCK TABLES `be_instance` WRITE;
/*!40000 ALTER TABLE `be_instance` DISABLE KEYS */;
INSERT INTO `be_instance` VALUES (1,'GT4',1,'ngs.wmin.ac.uk','qweqwe','qweqwe',NULL,'PBS',2,1);
/*!40000 ALTER TABLE `be_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `generator`
--

DROP TABLE IF EXISTS `generator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `generator` (
  `name` varchar(50) NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `generator`
--

LOCK TABLES `generator` WRITE;
/*!40000 ALTER TABLE `generator` DISABLE KEYS */;
INSERT INTO `generator` VALUES ('app_attr_gen',1000),('app_com_gen',1000),('app_gen',1000),('bridge_gen',1000),('group_gen',1000),('imp_attr_gen',1000),('imp_com_gen',1000),('imp_file_gen',1000),('imp_gen',1000),('plat_gen',1000),('user_gen',1000);
/*!40000 ALTER TABLE `generator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imp_attribute`
--

DROP TABLE IF EXISTS `imp_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `imp_attribute` (
  `id` int(11) NOT NULL,
  `imp_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `attr_value` varchar(10000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_imp_id_attr_name` (`imp_id`,`name`) USING BTREE,
  CONSTRAINT `fk_imp_attr_imp_id` FOREIGN KEY (`imp_id`) REFERENCES `implementation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imp_attribute`
--

LOCK TABLES `imp_attribute` WRITE;
/*!40000 ALTER TABLE `imp_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `imp_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imp_comment`
--

DROP TABLE IF EXISTS `imp_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `imp_comment` (
  `id` int(11) NOT NULL,
  `imp_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `message` varchar(5000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `imp_id_user_id_created` (`imp_id`,`user_id`,`created`),
  KEY `fk_imp_com_imp_id` (`imp_id`),
  KEY `fk_imp_com_user_id` (`user_id`),
  CONSTRAINT `fk_imp_com_imp_id` FOREIGN KEY (`imp_id`) REFERENCES `implementation` (`id`),
  CONSTRAINT `fk_imp_com_user_id` FOREIGN KEY (`user_id`) REFERENCES `repo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imp_comment`
--

LOCK TABLES `imp_comment` WRITE;
/*!40000 ALTER TABLE `imp_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `imp_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imp_embed`
--

DROP TABLE IF EXISTS `imp_embed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `imp_embed` (
  `id` int(11) NOT NULL,
  `imp_id` int(11) NOT NULL,
  `ext_user_id` varchar(50) NOT NULL,
  `ext_service_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_imp_id_embed` (`imp_id`,`ext_user_id`,`ext_service_id`) USING BTREE,
  CONSTRAINT `fk_imp_embed_imp_id` FOREIGN KEY (`imp_id`) REFERENCES `implementation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imp_embed`
--

LOCK TABLES `imp_embed` WRITE;
/*!40000 ALTER TABLE `imp_embed` DISABLE KEYS */;
/*!40000 ALTER TABLE `imp_embed` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imp_file`
--

DROP TABLE IF EXISTS `imp_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `imp_file` (
  `id` int(11) NOT NULL,
  `imp_id` int(11) NOT NULL,
  `pathname` varchar(255) NOT NULL,
  `url` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_imp_id_pathname` (`imp_id`,`pathname`),
  CONSTRAINT `fk_imp_file_imp_id` FOREIGN KEY (`imp_id`) REFERENCES `implementation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imp_file`
--

LOCK TABLES `imp_file` WRITE;
/*!40000 ALTER TABLE `imp_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `imp_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `implementation`
--

DROP TABLE IF EXISTS `implementation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `implementation` (
  `id` int(11) NOT NULL,
  `app_id` int(11) NOT NULL,
  `plat_id` int(11) NOT NULL,
  `version` varchar(32) NOT NULL,
  `status` varchar(32) NOT NULL COMMENT 'values: NEW, READY,  FAILED, VALIDATED, OLD, DEPRECATED, COMPROMISED',
  `validator_id` int(11) DEFAULT NULL,
  `gemlca_id` varchar(100) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_app_id_platform` (`app_id`,`plat_id`,`version`) USING BTREE,
  KEY `fk_imp_plat_id` (`plat_id`),
  KEY `fk_imp_validator_id` (`validator_id`),
  CONSTRAINT `fk_imp_app_id` FOREIGN KEY (`app_id`) REFERENCES `application` (`id`),
  CONSTRAINT `fk_imp_plat_id` FOREIGN KEY (`plat_id`) REFERENCES `platform` (`id`),
  CONSTRAINT `fk_imp_validator_id` FOREIGN KEY (`validator_id`) REFERENCES `repo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `implementation`
--

LOCK TABLES `implementation` WRITE;
/*!40000 ALTER TABLE `implementation` DISABLE KEYS */;
/*!40000 ALTER TABLE `implementation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_manager`
--

DROP TABLE IF EXISTS `job_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_manager` (
  `jobManagerId` int(11) NOT NULL AUTO_INCREMENT,
  `jobManagerName` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`jobManagerId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_manager`
--

LOCK TABLES `job_manager` WRITE;
/*!40000 ALTER TABLE `job_manager` DISABLE KEYS */;
INSERT INTO `job_manager` VALUES (1,'PBS'),(2,'Condor'),(3,'LSF'),(4,'PBS'),(5,'Condor'),(6,'LSF');
/*!40000 ALTER TABLE `job_manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_type`
--

DROP TABLE IF EXISTS `job_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_type` (
  `jobTypeId` int(11) NOT NULL AUTO_INCREMENT,
  `jobTypeName` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`jobTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_type`
--

LOCK TABLES `job_type` WRITE;
/*!40000 ALTER TABLE `job_type` DISABLE KEYS */;
INSERT INTO `job_type` VALUES (1,'single'),(2,'JavaJob'),(3,'MPI'),(4,'single'),(5,'JavaJob'),(6,'MPI');
/*!40000 ALTER TABLE `job_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operating_systems`
--

DROP TABLE IF EXISTS `operating_systems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operating_systems` (
  `idOS` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `version` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`idOS`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operating_systems`
--

LOCK TABLES `operating_systems` WRITE;
/*!40000 ALTER TABLE `operating_systems` DISABLE KEYS */;
INSERT INTO `operating_systems` VALUES (1,'SciLinux','5.0.1'),(2,'SciLinux','6.0.1'),(3,'Windows 8','9010'),(4,'Debian','6.1'),(5,'SciLinux','5.0.1'),(6,'SciLinux','6.0.1'),(7,'Windows 8','9010'),(8,'Debian','6.1');
/*!40000 ALTER TABLE `operating_systems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `platform`
--

DROP TABLE IF EXISTS `platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `platform` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(5000) NOT NULL,
  `version` varchar(50) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_index` (`name`,`version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `platform`
--

LOCK TABLES `platform` WRITE;
/*!40000 ALTER TABLE `platform` DISABLE KEYS */;
/*!40000 ALTER TABLE `platform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ratings` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `userID` varchar(32) NOT NULL,
  `versionID` int(11) NOT NULL,
  `rate` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `versionID` (`versionID`),
  KEY `userID` (`userID`),
  CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`versionID`) REFERENCES `implementation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ratings_ibfk_4` FOREIGN KEY (`userID`) REFERENCES `repo_user` (`login_name`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repo_group`
--

DROP TABLE IF EXISTS `repo_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repo_group` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `leader_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_name` (`name`),
  KEY `fk_repo_group_leader_id` (`leader_id`),
  CONSTRAINT `fk_repo_group_leader_id` FOREIGN KEY (`leader_id`) REFERENCES `repo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repo_group`
--

LOCK TABLES `repo_group` WRITE;
/*!40000 ALTER TABLE `repo_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `repo_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repo_user`
--

DROP TABLE IF EXISTS `repo_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repo_user` (
  `id` int(11) NOT NULL,
  `login_name` varchar(32) NOT NULL,
  `pass_hash` varchar(32) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `organization` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `admin` tinyint(4) NOT NULL,
  `validator` tinyint(4) NOT NULL,
  `active` tinyint(4) NOT NULL,
  `advancedView` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_name` (`login_name`),
  KEY `ix_login_name` (`login_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repo_user`
--

LOCK TABLES `repo_user` WRITE;
/*!40000 ALTER TABLE `repo_user` DISABLE KEYS */;
INSERT INTO `repo_user` VALUES (1,'admin','b91cd1a54781790beaa2baf741fa6789','SHIWA Repo Administrator','SHIWA','admin@dev.null',1,1,1,1);
/*!40000 ALTER TABLE `repo_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_flags`
--

DROP TABLE IF EXISTS `role_flags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_flags` (
  `admin` tinyint(4) NOT NULL,
  `validator` tinyint(4) NOT NULL,
  `role_name` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_flags`
--

LOCK TABLES `role_flags` WRITE;
/*!40000 ALTER TABLE `role_flags` DISABLE KEYS */;
INSERT INTO `role_flags` VALUES (1,0,'admin'),(0,1,'validator'),(0,0,'user');
/*!40000 ALTER TABLE `role_flags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supported_job_managers`
--

DROP TABLE IF EXISTS `supported_job_managers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supported_job_managers` (
  `jobManagerId` int(11) NOT NULL,
  `idBackend` int(11) NOT NULL,
  KEY `jobManagerId` (`jobManagerId`),
  KEY `idBackend` (`idBackend`),
  CONSTRAINT `supported_job_managers_ibfk_1` FOREIGN KEY (`jobManagerId`) REFERENCES `job_manager` (`jobManagerId`),
  CONSTRAINT `supported_job_managers_ibfk_2` FOREIGN KEY (`idBackend`) REFERENCES `backend` (`idBackend`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supported_job_managers`
--

LOCK TABLES `supported_job_managers` WRITE;
/*!40000 ALTER TABLE `supported_job_managers` DISABLE KEYS */;
INSERT INTO `supported_job_managers` VALUES (1,1),(1,2),(2,1),(3,2),(3,3),(1,3),(1,1),(1,2),(2,1),(3,2),(3,3),(1,3);
/*!40000 ALTER TABLE `supported_job_managers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uploaded_file`
--

DROP TABLE IF EXISTS `uploaded_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uploaded_file` (
  `idWEFile` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(300) DEFAULT NULL,
  `filePath` varchar(300) DEFAULT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `isData` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`idWEFile`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uploaded_file`
--

LOCK TABLES `uploaded_file` WRITE;
/*!40000 ALTER TABLE `uploaded_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `uploaded_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`group_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_group_id` (`group_id`),
  CONSTRAINT `fk_user_group_group_id` FOREIGN KEY (`group_id`) REFERENCES `repo_group` (`id`),
  CONSTRAINT `fk_user_group_user_id` FOREIGN KEY (`user_id`) REFERENCES `repo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `we_implementation`
--

DROP TABLE IF EXISTS `we_implementation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `we_implementation` (
  `idWEImp` int(11) NOT NULL AUTO_INCREMENT,
  `nameWEImp` varchar(50) DEFAULT NULL,
  `descriptionWEImp` varchar(5000) DEFAULT NULL,
  `preDeployed` tinyint(1) DEFAULT NULL,
  `shellPath` varchar(100) DEFAULT NULL,
  `shellWEFileId` int(11) DEFAULT NULL,
  `zipWEFileId` int(11) DEFAULT NULL,
  `prefixData` varchar(500) DEFAULT NULL,
  `idWE` int(11) NOT NULL,
  `idBackendInst` int(11) NOT NULL,
  PRIMARY KEY (`idWEImp`),
  KEY `idWE` (`idWE`),
  KEY `idBackendInst` (`idBackendInst`),
  KEY `shellWEFileId` (`shellWEFileId`),
  KEY `zipWEFileId` (`zipWEFileId`),
  CONSTRAINT `we_implementation_ibfk_1` FOREIGN KEY (`idWE`) REFERENCES `workflow_engine` (`idWE`),
  CONSTRAINT `we_implementation_ibfk_2` FOREIGN KEY (`idBackendInst`) REFERENCES `be_instance` (`idBackendInst`),
  CONSTRAINT `we_implementation_ibfk_3` FOREIGN KEY (`shellWEFileId`) REFERENCES `uploaded_file` (`idWEFile`),
  CONSTRAINT `we_implementation_ibfk_4` FOREIGN KEY (`zipWEFileId`) REFERENCES `uploaded_file` (`idWEFile`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `we_implementation`
--

LOCK TABLES `we_implementation` WRITE;
/*!40000 ALTER TABLE `we_implementation` DISABLE KEYS */;
/*!40000 ALTER TABLE `we_implementation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `web_role`
--

DROP TABLE IF EXISTS `web_role`;
/*!50001 DROP VIEW IF EXISTS `web_role`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `web_role` (
  `login_name` tinyint NOT NULL,
  `role_name` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `web_user`
--

DROP TABLE IF EXISTS `web_user`;
/*!50001 DROP VIEW IF EXISTS `web_user`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `web_user` (
  `login_name` tinyint NOT NULL,
  `pass_hash` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `workflow_engine`
--

DROP TABLE IF EXISTS `workflow_engine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workflow_engine` (
  `idWE` int(11) NOT NULL AUTO_INCREMENT,
  `nameWE` varchar(50) DEFAULT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`idWE`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_engine`
--

LOCK TABLES `workflow_engine` WRITE;
/*!40000 ALTER TABLE `workflow_engine` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_engine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_engine_files`
--

DROP TABLE IF EXISTS `workflow_engine_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workflow_engine_files` (
  `idWE` int(11) NOT NULL,
  `idWEFile` int(11) NOT NULL,
  KEY `idWE` (`idWE`),
  KEY `idWEFile` (`idWEFile`),
  CONSTRAINT `workflow_engine_files_ibfk_1` FOREIGN KEY (`idWE`) REFERENCES `workflow_engine` (`idWE`),
  CONSTRAINT `workflow_engine_files_ibfk_2` FOREIGN KEY (`idWEFile`) REFERENCES `uploaded_file` (`idWEFile`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_engine_files`
--

LOCK TABLES `workflow_engine_files` WRITE;
/*!40000 ALTER TABLE `workflow_engine_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_engine_files` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `web_role`
--

/*!50001 DROP TABLE IF EXISTS `web_role`*/;
/*!50001 DROP VIEW IF EXISTS `web_role`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`glassfish`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `web_role` AS select `u`.`login_name` AS `login_name`,`g`.`role_name` AS `role_name` from (`repo_user` `u` join `role_flags` `g`) where ((`u`.`active` = 1) and (((`u`.`admin` = 1) and (`g`.`admin` = '1')) or ((`u`.`validator` = 1) and (`g`.`validator` = 1)) or ((`g`.`validator` = 0) and (`g`.`admin` = 0)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `web_user`
--

/*!50001 DROP TABLE IF EXISTS `web_user`*/;
/*!50001 DROP VIEW IF EXISTS `web_user`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`glassfish`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `web_user` AS select `repo_user`.`login_name` AS `login_name`,`repo_user`.`pass_hash` AS `pass_hash` from `repo_user` where (`repo_user`.`active` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-08-21 16:27:01
