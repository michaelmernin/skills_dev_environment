/*
Navicat MySQL Data Transfer

Source Server         : 10.2.1.205
Source Server Version : 50154
Source Host           : 10.2.1.205:3306
Source Database       : PRSDB

Target Server Type    : MYSQL
Target Server Version : 50154
File Encoding         : 65001

Date: 2013-12-27 11:16:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for webform_roles
-- ----------------------------
DROP TABLE IF EXISTS `webform_roles`;
CREATE TABLE `webform_roles` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node identifier of a webform.',
  `rid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The role identifier.',
  PRIMARY KEY (`nid`,`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds access information regarding which roles are...';
TRUNCATE TABLE webform_roles;
-- ----------------------------
-- Records of webform_roles
-- ----------------------------
