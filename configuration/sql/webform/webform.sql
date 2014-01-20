/*
Navicat MySQL Data Transfer

Source Server         : 10.2.1.205
Source Server Version : 50154
Source Host           : 10.2.1.205:3306
Source Database       : PRSDB

Target Server Type    : MYSQL
Target Server Version : 50154
File Encoding         : 65001

Date: 2013-11-07 15:48:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for webform
-- ----------------------------
DROP TABLE IF EXISTS `webform`;
CREATE TABLE `webform` (
  `nid` int(10) unsigned NOT NULL COMMENT 'The node identifier of a webform.',
  `confirmation` text NOT NULL COMMENT 'The confirmation message or URL displayed to the user after submitting a form.',
  `confirmation_format` varchar(255) DEFAULT NULL COMMENT 'The filter_format.format of the confirmation message.',
  `redirect_url` varchar(255) DEFAULT '<confirmation>' COMMENT 'The URL a user is redirected to after submitting a form.',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Boolean value of a webform for open (1) or closed (0).',
  `block` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value for whether this form be available as a block.',
  `allow_draft` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value for whether submissions to this form be saved as a draft.',
  `auto_save` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value for whether submissions to this form should be auto-saved between pages.',
  `submit_notice` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Boolean value for whether to show or hide the previous submissions notification.',
  `submit_text` varchar(255) DEFAULT NULL COMMENT 'The title of the submit button on the form.',
  `submit_limit` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'The number of submissions a single user is allowed to submit within an interval. -1 is unlimited.',
  `submit_interval` int(11) NOT NULL DEFAULT '-1' COMMENT 'The amount of time in seconds that must pass before a user can submit another submission within the set limit.',
  `total_submit_limit` int(11) NOT NULL DEFAULT '-1' COMMENT 'The total number of submissions allowed within an interval. -1 is unlimited.',
  `total_submit_interval` int(11) NOT NULL DEFAULT '-1' COMMENT 'The amount of time in seconds that must pass before another submission can be submitted within the set limit.',
  `progressbar_bar` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value indicating if the bar should be shown as part of the progress bar.',
  `progressbar_page_number` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value indicating if the page number should be shown as part of the progress bar.',
  `progressbar_percent` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value indicating if the percentage complete should be shown as part of the progress bar.',
  `progressbar_pagebreak_labels` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value indicating if the pagebreak labels should be included as part of the progress bar.',
  `progressbar_include_confirmation` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Boolean value indicating if the confirmation page should count as a page in the progress bar.',
  `progressbar_label_first` varchar(255) DEFAULT NULL COMMENT 'Label for the first page of the progress bar.',
  `progressbar_label_confirmation` varchar(255) DEFAULT NULL COMMENT 'Label for the last page of the progress bar.',
  PRIMARY KEY (`nid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table for storing additional properties for webform nodes.';
TRUNCATE TABLE webform;
-- ----------------------------
-- Records of webform
-- ----------------------------
INSERT INTO `webform` VALUES ('1', '', 'filtered_html', '<confirmation>', '1', '0', '1', '0', '1', '', '-1', '-1', '-1', '-1', '1', '0', '0', '1', '1', 'Start', 'Complete');
INSERT INTO `webform` VALUES ('2', '', 'filtered_html', '<confirmation>', '1', '0', '1', '0', '1', '', '-1', '-1', '-1', '-1', '1', '0', '0', '1', '1', 'Start', 'Complete');
INSERT INTO `webform` VALUES ('3', '', 'filtered_html', '<confirmation>', '1', '0', '1', '1', '1', '', '-1', '-1', '-1', '-1', '1', '0', '0', '1', '1', 'Start', 'Complete');