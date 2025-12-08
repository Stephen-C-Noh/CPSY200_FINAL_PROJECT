/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.1.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: cpsy200
-- ------------------------------------------------------
-- Server version	12.1.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int(10) unsigned NOT NULL,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`category_id`),
  CONSTRAINT `chk_category_two_digit` CHECK (`category_id` between 10 and 99)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `category` VALUES
(10,'Power tools'),
(20,'Yard equipment'),
(30,'Compressors'),
(40,'Generators'),
(50,'Air Tools');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `email` varchar(254) DEFAULT NULL,
  `is_active` char(1) NOT NULL DEFAULT 'Y',
  `discount_rate` decimal(5,2) NOT NULL DEFAULT 0.00,
  `note` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  CONSTRAINT `chk_is_active` CHECK (`is_active` in ('Y','N'))
) ENGINE=InnoDB AUTO_INCREMENT=1006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `customer` VALUES
(1001,'John','Doe','(555) 555-1212','jd@sample.net','Y',0.00,NULL),
(1002,'Jane','Smith','(555) 555-3434','js@live.com','Y',0.00,NULL),
(1003,'Michael','Lee','(555) 555-5656','ml@sample.net','Y',0.00,NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment` (
  `equipment_id` int(10) unsigned NOT NULL,
  `category_id` int(10) unsigned NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(254) NOT NULL,
  `daily_rate` decimal(7,2) NOT NULL,
  `seq_in_category` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`equipment_id`),
  KEY `fk_equipment_category` (`category_id`),
  CONSTRAINT `fk_equipment_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment`
--

LOCK TABLES `equipment` WRITE;
/*!40000 ALTER TABLE `equipment` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `equipment` VALUES
(101,10,'Hammer drill','Powerful drill for concrete and masonry',25.99,1),
(102,10,'Rotary Hammer','Heavy-duty rotary hammer for concrete',29.50,2),
(103,10,'Impact Driver','Cordless impact driver',22.00,3),
(104,10,'Angle Grinder','4-1/2 inch angle grinder',18.25,4),
(105,10,'Concrete Mixer','Portable mixer',34.99,5),
(106,10,'Rotary Hammer','Heavy-duty rotary hammer for concrete',29.50,6),
(201,20,'Chainsaw','Gas-powered chainsaw for cutting wood',49.99,1),
(202,20,'Lawn mower','Self-propelled lawn mower with mulching function',19.99,2),
(203,20,'Hedge Trimmer','Electric hedge trimmer',15.75,3),
(204,20,'Leaf Blower','Cordless leaf blower',12.99,4),
(205,20,'String Trimmer','Gas string trimmer',17.40,5),
(206,20,'Aerator','Lawn aerator',21.00,6),
(207,20,'Leaf Blower','Cordless leaf blower',12.99,7),
(208,20,'Hedge Trimmer','Electric hedge trimmer',15.75,8),
(301,30,'Small Compressor','5 Gallon Compressor-Portable',14.99,1),
(302,30,'Medium Compressor','12 Gallon compressor',19.50,2),
(303,30,'Air Hose','50 ft air hose',6.99,3),
(304,30,'Medium Compressor','12 Gallon compressor',19.50,4),
(501,50,'Brad Nailer','Brad Nailer. Requires 3/4 to 1 1/2 Brad Nails',10.99,1),
(502,50,'Finish Nailer','Finish nailer—16 gauge',13.49,2),
(503,50,'Staple Gun','Heavy-duty stapler',8.49,3),
(504,50,'Finish Nailer','Finish nailer—16 gauge',13.49,4);
/*!40000 ALTER TABLE `equipment` ENABLE KEYS */;
UNLOCK TABLES;
commit;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER equipment_bi
BEFORE INSERT ON equipment
FOR EACH ROW
BEGIN
    DECLARE v_next INT UNSIGNED;

    IF NEW.category_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'category_id is required';
    END IF;

    
    INSERT INTO equipment_seq (category_id, next_seq)
    VALUES (NEW.category_id, 1)
    ON DUPLICATE KEY UPDATE next_seq = next_seq;

    
    SELECT next_seq INTO v_next
    FROM equipment_seq
    WHERE category_id = NEW.category_id
    FOR UPDATE;

    
    IF NEW.equipment_id IS NULL THEN
        
        SET NEW.equipment_id = CAST(CONCAT(NEW.category_id, v_next) AS UNSIGNED);
    END IF;

    
    SET NEW.seq_in_category = v_next;

    
    UPDATE equipment_seq
    SET next_seq = v_next + 1
    WHERE category_id = NEW.category_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `equipment_seq`
--

DROP TABLE IF EXISTS `equipment_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_seq` (
  `category_id` int(10) unsigned NOT NULL,
  `next_seq` int(10) unsigned NOT NULL,
  PRIMARY KEY (`category_id`),
  CONSTRAINT `fk_equipmentseq_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_seq`
--

LOCK TABLES `equipment_seq` WRITE;
/*!40000 ALTER TABLE `equipment_seq` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `equipment_seq` VALUES
(10,7),
(20,9),
(30,5),
(50,5);
/*!40000 ALTER TABLE `equipment_seq` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `rental`
--

DROP TABLE IF EXISTS `rental`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rental` (
  `rental_id` int(11) NOT NULL AUTO_INCREMENT,
  `request_date` date NOT NULL,
  `customer_id` int(11) NOT NULL,
  PRIMARY KEY (`rental_id`),
  KEY `ix_rental_customer` (`customer_id`),
  CONSTRAINT `fk_rental_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rental`
--

LOCK TABLES `rental` WRITE;
/*!40000 ALTER TABLE `rental` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `rental` VALUES
(1000,'2024-02-15',1001),
(1001,'2024-02-16',1002);
/*!40000 ALTER TABLE `rental` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `rental_item`
--

DROP TABLE IF EXISTS `rental_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rental_item` (
  `rental_id` int(11) NOT NULL,
  `equipment_id` int(10) unsigned NOT NULL,
  `rental_date` date NOT NULL,
  `return_date` date NOT NULL,
  `daily_rate_snapshot` decimal(7,2) NOT NULL,
  `cost` decimal(10,2) GENERATED ALWAYS AS (`daily_rate_snapshot` * (to_days(`return_date`) - to_days(`rental_date`))) STORED,
  PRIMARY KEY (`rental_id`,`equipment_id`),
  KEY `ix_item_equipment` (`equipment_id`),
  KEY `ix_item_rental_dates` (`rental_id`,`rental_date`,`return_date`),
  CONSTRAINT `fk_item_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`),
  CONSTRAINT `fk_item_rental` FOREIGN KEY (`rental_id`) REFERENCES `rental` (`rental_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_item_dates` CHECK (`return_date` >= `rental_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rental_item`
--

LOCK TABLES `rental_item` WRITE;
/*!40000 ALTER TABLE `rental_item` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `rental_item` VALUES
(1000,201,'2024-02-20','2024-02-23',49.99,149.97),
(1001,501,'2024-02-21','2024-02-25',10.99,43.96);
/*!40000 ALTER TABLE `rental_item` ENABLE KEYS */;
UNLOCK TABLES;
commit;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER rental_item_bi
BEFORE INSERT ON rental_item
FOR EACH ROW
BEGIN
  DECLARE v_rate DECIMAL(7,2);
  SELECT e.daily_rate INTO v_rate
  FROM equipment AS e
  WHERE e.equipment_id = NEW.equipment_id;

  IF v_rate IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Unknown equipment_id';
  END IF;

  SET NEW.daily_rate_snapshot = v_rate;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER rental_item_bu
BEFORE UPDATE ON rental_item
FOR EACH ROW
BEGIN
  DECLARE v_rate DECIMAL(7,2);

  IF NEW.equipment_id <> OLD.equipment_id THEN
    SELECT e.daily_rate INTO v_rate
    FROM equipment AS e
    WHERE e.equipment_id = NEW.equipment_id;

    IF v_rate IS NULL THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Unknown equipment_id';
    END IF;

    SET NEW.daily_rate_snapshot = v_rate;
  END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-12-08 11:06:53
