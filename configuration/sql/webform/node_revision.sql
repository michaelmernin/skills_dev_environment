/*
Navicat MySQL Data Transfer

Source Server         : 10.2.1.205
Source Server Version : 50154
Source Host           : 10.2.1.205:3306
Source Database       : PRSDB

Target Server Type    : MYSQL
Target Server Version : 50154
File Encoding         : 65001

Date: 2013-11-07 16:16:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for node_revision
-- ----------------------------
DROP TABLE IF EXISTS `node_revision`;
CREATE TABLE `node_revision` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node this version belongs to.',
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The primary identifier for this version.',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The users.uid that created this version.',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT 'The title of this version.',
  `log` longtext NOT NULL COMMENT 'The log entry explaining the changes in this version.',
  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'A Unix timestamp indicating when this version was created.',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT 'Boolean indicating whether the node (at the time of this revision) is published (visible to non-administrators).',
  `comment` int(11) NOT NULL DEFAULT '0' COMMENT 'Whether comments are allowed on this node (at the time of this revision): 0 = no, 1 = closed (read only), 2 = open (read/write).',
  `promote` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the node (at the time of this revision) should be displayed on the front page.',
  `sticky` int(11) NOT NULL DEFAULT '0' COMMENT 'Boolean indicating whether the node (at the time of this revision) should be displayed at the top of lists in which it appears.',
  PRIMARY KEY (`vid`),
  KEY `nid` (`nid`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=834 DEFAULT CHARSET=utf8 COMMENT='Stores information about each saved version of a node.';
TRUNCATE TABLE node_revision;
-- ----------------------------
-- Records of node_revision
-- ----------------------------
INSERT INTO `node_revision` VALUES ('1', '1', '1', 'Annual Performace Review Tempate', '', '1386556538', '1', '1', '1', '0');
INSERT INTO `node_revision` VALUES ('2', '2', '1', 'For Demo Peer Review Form Template', '', '1384397775', '1', '1', '1', '0');
INSERT INTO `node_revision` VALUES ('3', '3', '1', 'Project Review Form Template', '', '1387354588', '1', '1', '1', '0');