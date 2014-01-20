/*
Navicat MySQL Data Transfer

Source Server         : 10.2.1.205
Source Server Version : 50154
Source Host           : 10.2.1.205:3306
Source Database       : PRSDB

Target Server Type    : MYSQL
Target Server Version : 50154
File Encoding         : 65001

Date: 2013-12-27 11:18:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for webform_submissions
-- ----------------------------
DROP TABLE IF EXISTS `webform_submissions`;
CREATE TABLE `webform_submissions` (
  `sid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique identifier for this submission.',
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node identifier of a webform.',
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The id of the user that completed this submission.',
  `is_draft` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Is this a draft of the submission?',
  `submitted` int(11) NOT NULL DEFAULT '0' COMMENT 'Timestamp of when the form was submitted.',
  `remote_addr` varchar(128) DEFAULT NULL COMMENT 'The IP address of the user that submitted the form.',
  PRIMARY KEY (`sid`),
  UNIQUE KEY `sid_nid` (`sid`,`nid`),
  KEY `nid_uid_sid` (`nid`,`uid`,`sid`),
  KEY `nid_sid` (`nid`,`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds general information about submissions outside of...';
TRUNCATE TABLE webform_submissions;