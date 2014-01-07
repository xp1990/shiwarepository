-- MySQL dump 10.13  Distrib 5.1.63, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: shiwa_repo
-- ------------------------------------------------------
-- Server version	5.1.63-0+squeeze1

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
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` int(11) NOT NULL,
  `name` varchar(300) NOT NULL,
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
-- Temporary table structure for view `web_role`
--

DROP TABLE IF EXISTS `web_role`;
/*!50001 DROP VIEW IF EXISTS `web_role`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `web_role` (
  `login_name` varchar(32),
  `role_name` varchar(16)
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
  `login_name` varchar(32),
  `pass_hash` varchar(32)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

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

-- Dump completed on 2013-07-31 17:13:49
