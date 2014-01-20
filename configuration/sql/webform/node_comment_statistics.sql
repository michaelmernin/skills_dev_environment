/*
Navicat MySQL Data Transfer

Source Server         : 10.2.1.205
Source Server Version : 50154
Source Host           : 10.2.1.205:3306
Source Database       : PRSDB

Target Server Type    : MYSQL
Target Server Version : 50154
File Encoding         : 65001

Date: 2013-11-07 16:15:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for node_comment_statistics
-- ----------------------------
DROP TABLE IF EXISTS `node_comment_statistics`;
CREATE TABLE `node_comment_statistics` (
  `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The node.nid for which the statistics are compiled.',
  `cid` int(11) NOT NULL DEFAULT '0' COMMENT 'The comment.cid of the last comment.',
  `last_comment_timestamp` int(11) NOT NULL DEFAULT '0' COMMENT 'The Unix timestamp of the last comment that was posted within this node, from comment.changed.',
  `last_comment_name` varchar(60) DEFAULT NULL COMMENT 'The name of the latest author to post a comment on this node, from comment.name.',
  `last_comment_uid` int(11) NOT NULL DEFAULT '0' COMMENT 'The user ID of the latest author to post a comment on this node, from comment.uid.',
  `comment_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'The total number of comments on this node.',
  PRIMARY KEY (`nid`),
  KEY `node_comment_timestamp` (`last_comment_timestamp`),
  KEY `comment_count` (`comment_count`),
  KEY `last_comment_uid` (`last_comment_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maintains statistics of node and comments posts to show ...';
TRUNCATE TABLE node_comment_statistics;
-- ----------------------------
-- Records of node_comment_statistics
-- ----------------------------
INSERT INTO `node_comment_statistics` VALUES ('1', '0', '1383638653', null, '1', '0');
INSERT INTO `node_comment_statistics` VALUES ('2', '0', '1383652481', null, '1', '0');
INSERT INTO `node_comment_statistics` VALUES ('3', '0', '1387354588', null, '1', '0');

