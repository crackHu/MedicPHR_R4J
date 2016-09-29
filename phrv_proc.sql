/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.6.19 : Database - phrv2
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

/* Procedure structure for procedure `procJbzlJWCMCSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `procJbzlJWCMCSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `procJbzlJWCMCSelect`(in jdzmc varchar(20))
begin
	SELECT  distinct grda_hkdz_jwcmc from phr_grda_jbzl where grda_hkdz_jdzmc = jdzmc and grda_hkdz_jwcmc!='';
end */$$
DELIMITER ;

/* Procedure structure for procedure `procJbzlListSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `procJbzlListSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `procJbzlListSelect`(in size int)
begin 
   select grbh , grda_xm , grda_xb , grda_csrq , grda_sfzhm , grda_hkdz_jdzmc , grda_hkdz_jwcmc , grda_hkdz_ljmc , grda_hklx , grda_brdh , grda_jtdh  
			from phr_grda_jbzl group by grda_lrrq desc limit  0 , size ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `procJbzlLJMCSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `procJbzlLJMCSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `procJbzlLJMCSelect`(in jwcmc varchar(20))
begin
	SELECT  distinct grda_hkdz_ljmc from phr_grda_jbzl where grda_hkdz_jwcmc = jwcmc and grda_hkdz_ljmc!='';
end */$$
DELIMITER ;

/* Procedure structure for procedure `procJBZLSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `procJBZLSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `procJBZLSelect`(IN pageNo INT, IN pageSize INT)
BEGIN
-- 查询档案列表
  IF(pageNo < 1) 
  THEN SET @start = 0 ;
  ELSE SET @start = (pageNo - 1) * pageSize ;
  END IF ;
  SET @end = pageSize ;
  SET @sql = 'SELECT * FROM phr_grda_jbzl limit ?,?' ;
  PREPARE stmt FROM @sql ;
  EXECUTE stmt USING @start,
  @end ;
  DEALLOCATE PREPARE stmt ;
END */$$
DELIMITER ;

/* Procedure structure for procedure `proGrdaGrbhSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proGrdaGrbhSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proGrdaGrbhSelect`(in grbh varchar(50))
begin 
	set @grbh = grbh ;
	SET @strSql1 = 'SELECT  * from phr_grda_jbzl where grbh in (';
	set @strSql2 = ' )';
	set @total = concat(@strSql1 , @grbh , @strSql2);
	prepare strSql from @total;
	execute strSql ;
	deallocate prepare strSql ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proGrdaJbzlSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proGrdaJbzlSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proGrdaJbzlSelect`(in grbh varchar(50) )
begin 
	#select j.id , j.grbh , j.grda_jtbh , j.grda_xm , j.grda_xb , j.grda_csrq  from  phr_grda_jbzl j where 
	set @grbh = grbh ;
	SET @strSql1 = 'select id , grbh , grda_jtbh , grda_xm , grda_xb , grda_csrq  from  phr_grda_jbzl j where grbh in (';
	set @strSql2 = ' )';
	set @total = concat(@strSql1 , @grbh , @strSql2);
	prepare strSql from @total;
	execute strSql ;
	deallocate prepare strSql ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proGrdaJwsSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proGrdaJwsSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proGrdaJwsSelect`(in grbh varchar(50) )
begin 
	#select j.id , j.grbh , j.grda_jtbh , j.grda_xm , j.grda_xb , j.grda_csrq  from  phr_grda_jbzl j where 
	set @grbh = grbh ;
	SET @strSql1 = 'select id , grbh , lb , jbmc , qzne from phr_grda_jws where grbh in (';
	set @strSql2 = ' )';
	set @total = concat(@strSql1 , @grbh , @strSql2);
	prepare strSql from @total;
	execute strSql ;
	deallocate prepare strSql ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proGrdaJzsSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proGrdaJzsSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proGrdaJzsSelect`(in grbh varchar(50))
begin 
	set @parms = grbh;
	set @strSql1 = 'select * from	phr_grda_jzs where grbh in (' ;
  set @strSql2 = ' )';
  set @total = concat(@strSql1 , @parms , @strSql2 );
	prepare str from @total;
  execute str ;
	deallocate prepare str;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proGrdaLbByConditionSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proGrdaLbByConditionSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proGrdaLbByConditionSelect`( in conditions varchar(1000) ,  in page int , in rows int )
begin 
	set @beginNum = ( page - 1 ) * rows;
  set @conditions = conditions;
  set @strSql = CONCAT( 'select id , grbh , grda_xm , grda_xb , grda_csrq , grda_sfzhm , grda_hkdz_jdzmc , grda_hkdz_jwcmc , grda_hkdz_ljmc , grda_hklx ,
        grda_brdh , grda_jtdh , grda_lxrxm , grda_lxrdh , grda_jdrq , grda_dazt  from phr_grda_jbzl ' , @conditions ,  'order by grda_lrrq
				limit ' , @beginNum , ' , ' , rows  );
	prepare strSql from  @strSql ;
	execute strSql;
  deallocate prepare  strSql ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proGrdaLbSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proGrdaLbSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proGrdaLbSelect`( in page int , in rows int )
begin 
	set @beginNum = ( page - 1 ) * rows;
  set @strSql = CONCAT( 'select id , grbh , grda_xm , grda_xb , grda_csrq , grda_sfzhm , grda_hkdz_jdzmc , grda_hkdz_jwcmc , grda_hkdz_ljmc , grda_hklx ,
        grda_brdh , grda_jtdh , grda_lxrxm , grda_lxrdh , grda_jdrq , grda_dazt  from phr_grda_jbzl order by grda_lrrq
				limit ' , @beginNum , ' , ' , rows , ' order by grda_jdrq desc, grbh' );
	prepare strSql from  @strSql ;
	execute strSql;
  deallocate prepare  strSql ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proLoginSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proLoginSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proLoginSelect`( in loginName varchar(50) , in loginPassword varchar(50) )
begin
	set @loginName = loginName;
	if( null = LoginPassword || "" = LoginPassword )
	then set @loginPassword = "";
	else set @loginPassword = loginPassword;
	end if ;
	set @sql = 'select loginName from rs_usr where psswrd = ? and loginName = ?';
	prepare stringSql from @sql;
	execute stringSql using @loginPassword , @loginName ; 
	deallocate prepare stringSql ;
end */$$
DELIMITER ;

/* Procedure structure for procedure `proNameSelect` */

/*!50003 DROP PROCEDURE IF EXISTS  `proNameSelect` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `proNameSelect`(in loginName varchar(50) )
begin 
	if ( null = loginName || "" = loginName )
	then set @loginName = "" ;
	else set @loginName = loginName ;
	end if ;
  set @sql = ' select loginName from rs_usr where loginName = ? ';
	prepare stringSql from @sql;
	execute stringSql USING @loginName ;
	deallocate prepare stringSql;
end */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
