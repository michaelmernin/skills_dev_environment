<?php
defined('ANNUAL_TEMP') or define('ANNUAL_TEMP', 'Annual Performace Review Tempate');
defined('GOAL_STRING') or define('GOAL_STRING', 'goals_text');
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
	private static $self_assessment = array(REVIEW_IN_DRAFT);
	private static $self_assessment_read = array(REVIEW_BY_COUNSELOR);
	private static $view_assessment = array(REVIEW_BY_COUNSELOR);
	private static $review_content = array(APPROVED_BY_COUNSELOR);

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
