/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.7.15 : Database - phrv2
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`phrv2` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `phrv2`;

/* Procedure structure for procedure `procInsertData` */

/*!50003 DROP PROCEDURE IF EXISTS  `procInsertData` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `procInsertData`(IN json text)
BEGIN
   /*
     SET @json = '{"dataTable":"phr_grda_jbzl","data":{"grbh":"1114","grda_xm":"胡永刚","grda_csrq":"2016-12-30", "tjbj": 2}}';
     {"dataTable":"phr_grda_jbzl","data":{"grbh":"1114","grda_xm":"胡永刚","grda_csrq":"2016-12-30","tjbj":2}}
     CALL test(@json)
   */
   DECLARE i INT;
   DECLARE keyStrUnQuote TEXT;
   DECLARE valuesStr TEXT;
   
   SET @dataTable = JSON_UNQUOTE(JSON_EXTRACT(json, '$.dataTable'));
   SET @data = JSON_EXTRACT(json, '$.data');
   
   SET @keysArr = JSON_KEYS(@data);
   SET @keysArrLength = JSON_LENGTH(@data);
   
	SET i=0;
	SET keyStrUnQuote = '';
	SET valuesStr = '';
	WHILE i<@keysArrLength DO
	     SET @variable= CONCAT('$[',i,']');
	     
		SET @sql = 'set @temp_key = JSON_UNQUOTE(JSON_EXTRACT(?,?))';
		PREPARE stmt FROM @sql;
		EXECUTE stmt USING @keysArr, @variable;
		
	     SET @sql = 'set @temp_value = JSON_EXTRACT(?,CONCAT("$.",JSON_UNQUOTE(JSON_EXTRACT(?, ?))))';
		PREPARE stmt FROM @sql ;
		EXECUTE stmt USING @data, @keysArr, @variable;
		
		IF (i = @keysArrLength - 1) THEN
			SET keyStrUnQuote = CONCAT(keyStrUnQuote,@temp_key);
			SET valuesStr = CONCAT(valuesStr,@temp_value);
		ELSE
			SET keyStrUnQuote = CONCAT(keyStrUnQuote,CONCAT(@temp_key,","));
			SET valuesStr = CONCAT(valuesStr,CONCAT(@temp_value,","));
		END IF;
		
		SET i=i+1;
	END WHILE;
	SET @sql = CONCAT('insert into ',@dataTable,' (',keyStrUnQuote,') values (',valuesStr,')');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
END */$$
DELIMITER ;

/* Procedure structure for procedure `procJBZLSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `procJBZLSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `procJBZLSelect`(IN pageNo INT, IN pageSize INT)
BEGIN
  -- 查询档案列表
  IF(pageNo < 1) 
  THEN SET @start = 0 ;
  ELSE SET @start = (pageNo - 1) * pageSize ;
  END IF ;
  
  if(pageSize < 1) 
  then set @end = 10 ;
  else set @end = pageSize ;
  end if ;
  
  SET @sql = 'SELECT * FROM phr_grda_jbzl order by grda_jdrq desc, grbh asc limit ?,?' ;
  PREPARE stmt FROM @sql ;
  EXECUTE stmt USING @start,
  @end ;
  DEALLOCATE PREPARE stmt ;
END */$$
DELIMITER ;

/* Procedure structure for procedure `procUpdateData` */

/*!50003 DROP PROCEDURE IF EXISTS  `procUpdateData` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `procUpdateData`(IN json VARCHAR (1000))
BEGIN
   /*
    SET @json = '{"dataTable":"phr_grda_jbzl","identification":{"grbh":1114,"grda_csrq":"2011-12-30"},"data":{"grbh":"1114","grda_xm":"胡","grda_csrq":"2011-12-30", "tjbj": 3}}';
    CALL procUpdateData(@json);
   */
   
   DECLARE i INT;
   DECLARE j INT;
   DECLARE keyStrUnQuote VARCHAR(600);
   DECLARE idsKeyStrUnQuote VARCHAR(400);
   DECLARE conditionStr VARCHAR(800);
   
   SET @dataTable = JSON_UNQUOTE(JSON_EXTRACT(json, '$.dataTable'));
   SET @data = JSON_EXTRACT(json, '$.data');
   SET @identification = JSON_EXTRACT(json, '$.identification');
   
   SET @keysArr = JSON_KEYS(@data);
   SET @keysArrLength = JSON_LENGTH(@data);
   
   SET @idsKeyArr = JSON_KEYS(@identification);
   SET @idsKeyArrLength = JSON_LENGTH(@identification);
   
	SET i=0;
	set conditionStr = '';
	WHILE i<@keysArrLength DO
	     SET @variable= CONCAT('$[',i,']');
	     
		SET @sql = 'set @temp_key = JSON_UNQUOTE(JSON_EXTRACT(?,?))';
		PREPARE stmt FROM @sql;
		EXECUTE stmt USING @keysArr, @variable;
		
		SET @sql = 'set @temp_value = JSON_EXTRACT(?,CONCAT("$.",JSON_UNQUOTE(JSON_EXTRACT(?, ?))))';
		PREPARE stmt FROM @sql ;
		EXECUTE stmt USING @data, @keysArr, @variable;
		
		IF (i = @keysArrLength - 1) THEN
			set conditionStr = CONCAT(conditionStr, @temp_key, '=', @temp_value);
		ELSE
			SET conditionStr = CONCAT(conditionStr, @temp_key, '=', @temp_value, ',');
		END IF;
		SET i=i+1;
	END WHILE;
	
	set j=0;
	set idsKeyStrUnQuote = '';
	while j<@idsKeyArrLength do
		SET @variable= CONCAT('$[',j,']');
		
		SET @sql = 'set @temp_idsKey = JSON_UNQUOTE(JSON_EXTRACT(?,?))';
		PREPARE stmt FROM @sql;
		EXECUTE stmt USING @idsKeyArr, @variable;
		
		SET @sql = 'set @temp_idsValue = JSON_EXTRACT(?,CONCAT("$.",JSON_UNQUOTE(JSON_EXTRACT(?, ?))))';
		PREPARE stmt FROM @sql;
		EXECUTE stmt USING @identification, @idsKeyArr, @variable;
		
		SET idsKeyStrUnQuote = CONCAT(idsKeyStrUnQuote,' and ', @temp_idskey, '=', @temp_idsValue);
	
		set j=j+1;
	end while;
	
	SET @sql = CONCAT('update ',@dataTable,' set ',conditionStr,' where 1=1',idsKeyStrUnQuote);
	select @sql;
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
