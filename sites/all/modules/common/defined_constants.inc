<?php

defined('ANNUAL_TEMP') or define('ANNUAL_TEMP', 'Annual Performace Review Tempate');
defined('PEER_TEMP') or define('PEER_TEMP', 'For Demo Peer Review Form Template');
defined('PROJECT_TEMP') or define('PROJECT_TEMP', 'Project Review Form Template');

defined('GOAL_STRING') or define('GOAL_STRING', 'goals_text');
defined('FIRST_LAST_YEAR_GOAL') or define('FIRST_LAST_YEAR_GOAL', 'last_year_goal');
defined('NO_COMMENT') or define('NO_COMMENT', 'No comment.');
defined('NO_ANSWER') or define('NO_ANSWER', 'No answer.');
defined('NO_DATA') or define('NO_DATA', 'No data');

// Start of defined the review status constant.
defined('NEW_REVIEW_START') or define('NEW_REVIEW_START', 0);
defined('REVIEW_IN_DRAFT') or define('REVIEW_IN_DRAFT', 1);
defined('REVIEW_BY_COUNSELOR') or define('REVIEW_BY_COUNSELOR', 2);
defined('APPROVED_BY_COUNSELOR') or define('APPROVED_BY_COUNSELOR', 3);
defined('JOINT_REVIEW') or define('JOINT_REVIEW', 4);
defined('GM_REVIEW') or define('GM_REVIEW', 5);
defined('GM_APPROVED') or define('GM_APPROVED', 6);
defined('DELETED_REVIEW') or define('DELETED_REVIEW', 7);
// End of defined review status constant.
// Start of defined status of select peers
defined('NO_SELECTION') or define('NO_SELECTION', 0);
defined('COUNSELEE_SELECTED') or define('COUNSELEE_SELECTED', 1);
defined('COUNSELOR_SELECTED') or define('COUNSELOR_SELECTED', 2);
defined('BOTH_SELECTED') or define('BOTH_SELECTED', 3);
// End of defined select peer status constant.
//Define the review type
defined('ANNUAL_REVIEW') or define('ANNUAL_REVIEW', 0);
defined('PROJECT_REVIEW') or define('PROJECT_REVIEW', 1);
defined('THREE_MONTH_REVIEW') or define('THREE_MONTH_REVIEW', 2);

class AccessRoles {

  private static $self_assessment = array('counselee');
  private static $self_assessment_read = array('counselee');
  private static $view_assessment = array('counselor');
  private static $review_content = array('counselor');

  public static function getSelfAssessment() {
    return self::$self_assessment;
  }

  public static function getSelfAssessmentRead() {
    return self::$self_assessment_read;
  }

  public static function getViewAssessment() {
    return self::$view_assessment;
  }

  public static function getReviewContent() {
    return self::$review_content;
  }

}

// Start of defined a tab (MENU_LOCAL_TASK) accessibility of status
class AccessStatus {

//	private static $self_assessment = array(REVIEW_IN_DRAFT);
//	private static $self_assessment_read = array(REVIEW_BY_COUNSELOR);
//	private static $view_assessment = array(REVIEW_BY_COUNSELOR);
//	private static $review_content = array(APPROVED_BY_COUNSELOR);


  private static $self_assessment = array(
    '0' => array(REVIEW_IN_DRAFT),
    '1' => array(),
    '2' => array());
  
  private static $self_assessment_read = array(
    '0' => array(REVIEW_BY_COUNSELOR),
    '1' => array(),
    '2' => array());
  
  private static $view_assessment = array(
    '0' => array(),
    '1' => array(REVIEW_BY_COUNSELOR),
    '2' => array());
  
  private static $review_content = array(
    '0' => array(JOINT_REVIEW, GM_REVIEW, GM_APPROVED),
    '1' => array(APPROVED_BY_COUNSELOR, GM_REVIEW, GM_APPROVED),
    '2' => array(GM_APPROVED));

  public static function getSelfAssessment() {
    return self::$self_assessment;
  }

  public static function getSelfASSessmentRead() {
    return self::$self_assessment_read;
  }

  public static function getViewAssessment() {
    return self::$view_assessment;
  }

  public static function getReviewContent() {
    return self::$review_content;
  }

}

// End
?>
