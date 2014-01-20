/*
Navicat MySQL Data Transfer

Source Server         : 10.2.1.205
Source Server Version : 50154
Source Host           : 10.2.1.205:3306
Source Database       : PRSDB

Target Server Type    : MYSQL
Target Server Version : 50154
File Encoding         : 65001

Date: 2013-11-07 15:43:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ldap_query
-- ----------------------------
DROP TABLE IF EXISTS `ldap_query`;
CREATE TABLE `ldap_query` (
  `query_numeric_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary ID field for the table.  Only used internally.',
  `qid` varchar(20) NOT NULL COMMENT 'Machine name for query.',
  `name` varchar(60) NOT NULL,
  `sid` varchar(20) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `base_dn_str` text,
  `filter` text,
  `attributes_str` text,
  `sizelimit` int(11) NOT NULL DEFAULT '0',
  `timelimit` int(11) NOT NULL DEFAULT '0',
  `deref` tinyint(4) NOT NULL DEFAULT '0',
  `scope` tinyint(4) NOT NULL DEFAULT '3',
  PRIMARY KEY (`query_numeric_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='LDAP Query Data leveraged by other LDAP Modules';

-- ----------------------------
-- Records of ldap_query
-- ----------------------------
INSERT INTO `ldap_query` VALUES ('1', 'FetchGDCUsers', 'FetchGDCUsers', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(|(employeeID=HE*)(employeeID=HI*))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('2', 'FetchUsersA', 'FetchUsersA', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=a*)(sAMAccountName=A*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('4', 'FetchUsersB', 'FetchUsersB', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=b*)(sAMAccountName=B*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('5', 'FetchUsersC', 'FetchUsersC', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=c*)(sAMAccountName=C*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('6', 'FetchUsersD', 'FetchUsersD', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=d*)(sAMAccountName=D*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('7', 'FetchUsersE', 'FetchUsersE', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=e*)(sAMAccountName=E*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('8', 'FetchUsersF', 'FetchUsersF', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=f*)(sAMAccountName=F*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('9', 'FetchUsersG', 'FetchUsersG', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=g*)(sAMAccountName=G*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('10', 'FetchUsersH', 'FetchUsersH', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=h*)(sAMAccountName=H*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('11', 'FetchUsersI', 'FetchUsersI', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=i*)(sAMAccountName=I*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('12', 'FetchUsersJ', 'FetchUsersJ', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=j*)(sAMAccountName=J*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('13', 'FetchUsersK', 'FetchUsersK', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=k*)(sAMAccountName=K*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('14', 'FetchUsersL', 'FetchUsersL', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=l*)(sAMAccountName=L*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('15', 'FetchUsersM', 'FetchUsersM', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=m*)(sAMAccountName=M*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('16', 'FetchUsersN', 'FetchUsersN', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=n*)(sAMAccountName=N*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('17', 'FetchUsersO', 'FetchUsersO', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=o*)(sAMAccountName=O*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('18', 'FetchUsersP', 'FetchUsersP', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=p*)(sAMAccountName=P*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('19', 'FetchUsersQ', 'FetchUsersQ', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=q*)(sAMAccountName=Q*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('20', 'FetchUsersR', 'FetchUsersR', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=r*)(sAMAccountName=R*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('21', 'FetchUsersS', 'FetchUsersS', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=s*)(sAMAccountName=S*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('22', 'FetchUsersT', 'FetchUsersT', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=t*)(sAMAccountName=T*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('23', 'FetchUsersU', 'FetchUsersU', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=u*)(sAMAccountName=U*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('24', 'FetchUsersV', 'FetchUsersV', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=v*)(sAMAccountName=V*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('25', 'FetchUsersW', 'FetchUsersW', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=w*)(sAMAccountName=W*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('26', 'FetchUsersX', 'FetchUsersX', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=x*)(sAMAccountName=X*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('27', 'FetchUsersY', 'FetchUsersY', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=y*)(sAMAccountName=Y*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
INSERT INTO `ldap_query` VALUES ('28', 'FetchUsersZ', 'FetchUsersZ', 'PerficientLDAP', '1', 'OU=Employees,DC=perficient,DC=com', '(&(|(sAMAccountName=z*)(sAMAccountName=Z*))(!(|(employeeID=HE*)(employeeID=HI*))))', 'sAMAccountName, mail, sn, givenname, title, employeeID,whenCreated', '0', '0', '0', '3');
