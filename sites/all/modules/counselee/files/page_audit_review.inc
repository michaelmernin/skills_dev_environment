<?php

/**
 * @file
 * Page callback file for the counselee audit the result
 */
/**
 * The page audit review divide six part
 * 1. Project Roles And Responsibilities
 * 2. Last Year Goal
 * 3. Achievements
 * 4. Performance Evaluation
 * 5. Extend Description
 * 6. Overall Rating
 */

/**
 * Generate the page that counselee approve or disapprove the counselor's review results
 * 
 * procedure:
 * 1,get self review comment
 * 2,get counselor and peer comment
 * 3,theme content
 * 
 */
function counselee_confirm_review_result($rreid) {

  $identity = get_user_identity($rreid);
  if ($identity != 0) {
    drupal_set_message('You have no right to access!');
    drupal_goto('accessdenied');
  }

  $nid = get_nid_by_rreid($rreid);
  $pageContent = '';
  $self_review = get_self_review_comment($nid);
  $counselor_review = get_counselor_review_comment($nid);
  module_load_include('inc', 'counselee', 'files/review_assessment_modification');
  $peer_comment_info_set = get_peer_comment_information_array($rreid);

  $pageContent .= get_project_message_part($self_review);
  $pageContent .= get_performance_evaluation($self_review, $counselor_review, $peer_comment_info_set);
  $pageContent .= get_extend_description_part($self_review, $counselor_review);
  $pageContent .= get_overall_rating_part($self_review, $counselor_review);
  $pageContent .= theme('page_audit_review_score', array());
  $pageContent .= theme('page_audit_review_btn_approve', array('rreid' => $rreid));
  return $pageContent;
}

function get_self_review_comment($nid) {
  $sql = "SELECT
    component.nid ,
    component.cid ,
    component.form_key ,
    component.name ,
    component.type ,
    data.data
FROM
    {webform_component} component LEFT JOIN {webform_submitted_data} data
        ON component.cid = data.cid
    AND data.nid = '$nid'
WHERE
    component.nid = '$nid'";
  $result = db_query($sql)->fetchAll();

  $self_review_data = Array();

  $project_node = array(
    'clientdate' => array(),
    'start_date' => array(),
    'end_date' => array(),
    'project_roles_and_responsibilities' => array(),
  );

  $project_key = array('clientdate' => true,
    'start_date' => true,
    'end_date' => true,
    'project_roles_and_responsibilities' => true);

  foreach ($result as $node) {
    $key = $node->form_key;
    $self_review_data[$key] = $node;

    if (isset($project_key[$key])) {
      $project_node[$key][count($project_node[$key])] = $node;
    }
  }


  $self_review_data['project'] = $project_node;

  return $self_review_data;
}

/**
 * Get counselor review comments and rating ,
 * @param int $nid The node ID
 */
function get_counselor_review_comment($nid) {

  $sql = "SELECT
    data.csdid ,
    data.rating ,
    data.title,
    data.counselor_comment,
    data.peer_comments,
    data.peer_avg_rating
FROM
    {prs_counselor_submit_data} data ,
    {prs_counselor_submit_info} info
WHERE
    info.nid = data.nid
    AND info.csiid = data.csiid
    AND data.nid = $nid";

  $result = db_query($sql)->fetchAll();

  $counselor_review = Array();
  foreach ($result as $node) {
    $key = $node->title;
    $counselor_review[$key] = $node;
  }

  return $counselor_review;
}

function get_core_competencies_map() {
  $core_competencies = array(
    array(
      'selfkey' => 'client_engagements',
      'commentkey' => 'client_engagements_comments',
      'exist_peer' => 0,
    ),
    array(
      'selfkey' => 'consulting_skills',
      'commentkey' => 'consulting_skills_comments',
      'exist_peer' => 1,
    ),
    array(
      'selfkey' => 'technical_abilities',
      'commentkey' => 'technical_abilities_comments',
      'exist_peer' => 1,
    ),
    array(
      'selfkey' => 'professionalism',
      'commentkey' => 'professionalism_comments',
      'exist_peer' => 1,
    ),
    array(
      'selfkey' => 'leadership',
      'commentkey' => 'leadership_comments',
      'exist_peer' => 1,
    ),
    array(
      'selfkey' => 'teamwork',
      'commentkey' => 'teamwork_comments',
      'exist_peer' => 1,
    ),
  );
  return $core_competencies;
}

function get_internal_contribution_map() {
  $internal_contributions = array(
    array(
      'selfkey' => 'business_development',
      'commentkey' => 'business_development_comments',
      'exist_peer' => 0,
    ),
    array(
      'selfkey' => 'career_counseling',
      'commentkey' => 'career_counseling_comments',
      'exist_peer' => 0,    
    ),
    array(
      'selfkey' => 'recruiting_assistance',
      'commentkey' => 'recruiting_assistance_comments',
      'exist_peer' => 0,
    ),
    array(
      'selfkey' => 'internal_contributions',
      'commentkey' => 'internal_contributions_comments',
      'exist_peer' => 0,
    ),
    array(
      'selfkey' => 'perficient_basics',
      'commentkey' => 'perficient_basics_comments',
      'exist_peer' => 0,
    ),
  );

  return $internal_contributions;
}

/**
 * Get textarea
 * 
 */
function get_extend_description_map() {
  $textarea_map = array(
    array(
      'selfkey' => 'strengths_text',
    ),
    array(
      'selfkey' => 'opportunities_for_improvement_text',
    ),
    array(
      'selfkey' => 'summary_text',
    ),
    array(
      'selfkey' => 'development_opportunities_text',
    ),
    array(
      'selfkey' => 'achievements_text',
    ),
  );

  return $textarea_map;
}

/**
 * Counselee approve review result,modify the database status
 * 
 */
function approve_review_result() {
  $rreid = $_POST['rreid'];
  audit_review_result_update($rreid, 4, 1, '');
  $msg = 'You had approved the review result.';
  drupal_set_message($msg, 'status');
  print true;
}

function disapprove_review_result() {
  $rreid = $_POST['rreid'];
  $disapprove_reason = $_POST['disapproveReason'];

  audit_review_result_update($rreid, 2, 2, $disapprove_reason);

  $msg = 'You had disapproved the review result.';
  drupal_set_message($msg, 'status');
  print true;
}

function submit_review_result() {
  $rreid = $_POST['rreid'];
  audit_review_result_update($rreid, 5, 1, '');
  $msg = 'You had submitted the review result.';
  drupal_set_message($msg, 'status');
  print TRUE;
}

/**
 * Modify the prs_rel_review_employee rstatus
 * 
 * @param number $rreid prs_rel_review_employee id
 * @param number $rstatus prs_rel_review_employee status
 * @parsm number $approveStatus 
 * @param string $reason The reason,user disapproved the review, 
 * 
 */
function audit_review_result_update($rreid, $rstatus, $approveStatus = 1, $reason) {
  $num_updated = db_update('prs_rel_review_employee')
      ->fields(array(
        'rstatus' => $rstatus,
        'isCounseleeApproved' => $approveStatus,
        'counseleeReason' => $reason,
      ))
      ->condition('rreid', $rreid)
      ->execute();

  if ($num_updated == 1) {

    //rstatus == 4 stand for that counselee disapprove the result
    if ($rstatus == 4) {
      counselee_audit_review_result_remind($rreid, TRUE, $reason);
    }
    else if ($rstatus == 2) {
      counselee_audit_review_result_remind($rreid, FALSE, $reason);
    }
    else if ($rstatus == 5) {
      counselor_submit_review_result_remind($rreid);
    }
    return 'success';
  }
  return 'failed';
}

function get_project_message_part($self_review) {

  $content = '';
  $project = $self_review['project'];
  $len = count($project['clientdate']);

  for ($i = 0; $i < $len; $i++) {
    $content.= theme('project_roles_and_responsibilities', array(
      'rreid' => null,
      'nid' => null,
      'reviewee' => null,
      'review_info_content_num' => $i,
      'clientdate' => $project['clientdate'][$i]->data,
      'project_roles_and_responsibilities' => $project['project_roles_and_responsibilities'][$i]->data,
      'startdate' => $project['start_date'][$i]->data,
      'enddate' => $project['end_date'][$i]->data,
    ));
  }
  return $content;
}

function get_achievements() {
  
}

function get_category_content($self_review, $counselor_review, $catetory_map, $peer_comment_info_set = NULL) {
  $problem_node = array(
    'key' => null,
    'description' => null,
    'self_rating' => null,
    'counselor_rating' => null,
    'peer_rating' => null,
    'self_comment' => null,
    'counselor_comment' => null,
    'peer_comment' => null,
    'exist_peer' => null,
    'category' => null,
  );

  $content = '';
  for ($i = 0; $i < count($catetory_map); $i++) {
    $review = $catetory_map[$i];
    $problem_node['key'] = $review['selfkey'];
    $problem_node['description'] = $self_review[$review['selfkey']]->name;
    $problem_node['self_rating'] = $self_review[$review['selfkey']]->data;
    $problem_node['self_comment'] = $self_review[$review['commentkey']]->data;
    $problem_node['exist_peer'] = $review['exist_peer'];

    $problem_node['counselor_rating'] = $counselor_review[$review['selfkey']]->rating;
    $problem_node['counselor_comment'] = $counselor_review[$review['selfkey']]->counselor_comment;
//    $problem_node['peer_comment'] = $counselor_review[$review['selfkey']]->peer_comments;
    $problem_node['peer_rating'] = $counselor_review[$review['selfkey']]->peer_avg_rating;
    if ($review['exist_peer']) {
//  dd($review['selfkey'],'$review[selfkey]');
//  dd($peer_comment_info_set,'$peer_comment_info_set');
//      module_load_include('inc', 'counselee', 'files/review_assessment_modification');
      $problem_node['peer_comment'] = get_peer_comment_information_json($review['selfkey'], $peer_comment_info_set);
    }
    else {
      $problem_node['peer_comment'] = '';
    }
//    dd($problem_node, '$problem_node');
    $content .= theme('page_audit_review_question', array('node' => $problem_node));
  }

  return $content;
}

function get_performance_evaluation($self_review, $counselor_review, $peer_comment_info_set) {
  $content = '';

  //CORE COMPETENCIES
  $core_competencies_map = get_core_competencies_map();
  $core_competencies_content = get_category_content($self_review, $counselor_review, $core_competencies_map, $peer_comment_info_set);
  $core_competencies_content = add_fieldset(1, 'CORE COMPETENCIES', $core_competencies_content);


  //INTERNAL CONTRIBUTIONS
  $internal_contributions = get_internal_contribution_map();
  $internal_contributions_content = get_category_content($self_review, $counselor_review, $internal_contributions);
  $internal_contributions_content = add_fieldset(1, 'INTERNAL CONTRIBUTIONS', $internal_contributions_content);

  return add_fieldset(0, "Performance Evaluation", $core_competencies_content . $internal_contributions_content);
}

function get_extend_description_part($self_review, $counselor_review) {
  $content = '';
  $self_review_comment_map = get_extend_description_map();
  $extend_description = array(
    'description' => null,
    'self_description' => null,
    'counselor_description' => null,
  );
  foreach ($self_review_comment_map as $review) {
    $extend_description['description'] = $self_review[$review['selfkey']]->name;
    $extend_description['self_description'] = $self_review[$review['selfkey']]->data;
    $extend_description['counselor_description'] = $counselor_review[$review['selfkey']]->counselor_comment;

    $content .= theme('page_audit_review_summay_description', array('node' => $extend_description));
  }
  return $content;
}

function add_fieldset($style_type, $name, $content) {

  switch ($style_type) {
    case 0:
      $fieldset = "<fieldset class=\"webform-component-fieldset collapsible\">
    <legend><span class=\"fieldset-legend\"><a class=\"fieldset-title\" href=\"#\">
        $name</a></legend>$content</fieldset>";
      return $fieldset;
      break;
    case 1:
      $category = "<fieldset class=\"webform-component-fieldset webform-catalogue \">
    <legend><span class=\"fieldset-legend\">$name</span></legend>$content</fieldset>";
      return $category;
      break;
    default :
      return '';
      ;
  }
}

function get_nid_by_rreid($rreid) {
  $sql = "select provider.nid from {prs_rel_provider_employee} provider where provider.employeename = provider.providername and provider.rreid=$rreid";
  $result = db_query($sql)->fetchAll();

  if (count($result) == 1) {
    return $result[0]->nid;
  }
}

function counselor_submit_review_result($rreid) {

  $identity = get_user_identity($rreid);
  if ($identity != 1) {
    drupal_set_message('You have no right to access!');
    drupal_goto('accessdenied');
  }


  $nid = get_nid_by_rreid($rreid);
  $pageContent = '';
  $self_review = get_self_review_comment($nid);
  $counselor_review = get_counselor_review_comment($nid);
  module_load_include('inc', 'counselee', 'files/review_assessment_modification');
  $peer_comment_info_set = get_peer_comment_information_array($rreid);

  $pageContent .= get_project_message_part($self_review);
  $pageContent .= get_performance_evaluation($self_review, $counselor_review, $peer_comment_info_set);
  $pageContent .= get_extend_description_part($self_review, $counselor_review);
  $pageContent .= get_overall_rating_part($self_review, $counselor_review);
  $pageContent .= theme('page_audit_review_score', array());
  $pageContent .= theme('page_audit_review_btn_submit', array('rreid' => $rreid));

  return $pageContent;
}

function get_overall_rating_part($self_review, $counselor_review) {

  $extend_description['description'] = $self_review['overall_rating']->name;
  $extend_description['self_description'] = $self_review['overall_rating']->data;
  $extend_description['counselor_description'] = $counselor_review['overall_rating']->rating;

  return theme('page_audit_review_summay_description', array('node' => $extend_description));
}