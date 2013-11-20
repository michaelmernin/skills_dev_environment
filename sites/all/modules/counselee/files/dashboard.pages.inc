<?php

/**
 * @file
 * Page callback file for the performance review module.
 */

/**
 * Display dashboard and return ''
 * @return string 
 */
function dashboard_display() {
  if (!user_is_anonymous()) {
    return '';
  }
  else {
    drupal_goto('login');
  }
}

/**
 * Display performance review list management for special user
 * @return string 
 */
function show_pr_list() {
  if (!user_is_anonymous()) {
    return '';
  }
  else {
    drupal_goto('login');
  }
}

function accessdenied() {
  $pageContent = '';
  return $pageContent;
}

function feedbacksubmitsuccess() {
  $pageContent = '';
  return $pageContent;
}

/**
 * Generate the page that counselee approve or disapprove the counselor's review results
 * 
 * procedure:
 * 1,get self review comment
 * 2,get counselor and peer comment
 * 3,theme content
 * 
 */
function counselee_confirm_review_result($rpeid) {
  
  $rreid = 1;
  $nid = 21;
  $pageContent = '';
  $self_review = get_self_review_comment($nid);
  $counselor_review = get_counselor_review_comment($nid);
  $self_review_select_map = annual_review_select_key_map();
  $self_review_comment_map = annual_review_description_map();

//  dd($self_review_select_map, 'self_review_map');
  //Define One Problem Structure
  $problem_node = array(
    'description' => null,
    'self_rating' => null,
    'counselor_rating' => null,
    'peer_rating' => null,
    'self_comment' => null,
    'counselor_comment' => null,
    'peer_comment' => null,
  );

//  dd($self_review);

  foreach ($self_review_select_map as $review) {
    $problem_node['description'] = $self_review[$review['selfkey']]->name;
    $problem_node['self_rating'] = $self_review[$review['selfkey']]->data;
    $problem_node['self_comment'] = $self_review[$review['commentkey']]->data;

    $pageContent .= theme('page_audit_review_question', array('node' => $problem_node));
  }

//  dd($pageContent,'pageContent');

  $extend_description = array(
    'description' => null,
    'self_description' => null,
    'counselor_description' => null,
  );
  foreach ($self_review_comment_map as $review) {
    $extend_description['description'] = $self_review[$review['selfkey']]->name;
    $extend_description['self_description'] = $self_review[$review['selfkey']]->data;
    $pageContent .= theme('page_audit_review_summay_description', array('node' => $extend_description));
  }


//  $templatePath = drupal_get_path('module', 'counselee') . '/templates/' . 'page_audit_review_btn.tpl.php';
//  if (file_exists($templatePath))
//    $button = file_get_contents($templatePath);

  $pageContent .= theme('page_audit_review_btn', array('rreid' => $rreid));

  $pageContent .=$button;
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
  $self_review_map = Array();
  foreach ($result as $node) {
    $key = $node->form_key;
    $self_review_map[$key] = $node;
  }
  //dd($annual_review_map,'$annual_review_map');

  return $self_review_map;
}

/**
 * Get counselor review comment
 * 
 * 
 */
function get_counselor_review_comment($nid) {

  $sql = "SELECT
    data.csdid ,
    data.rating ,
    data.title,
    data.counselor_comment,
    data.peer_comments
FROM
    prs_counselor_submit_data data ,
    prs_counselor_submit_info info
WHERE
    info.nid = data.nid
    AND info.csiid = data.csiid
    AND data.nid = $nid";

  $result = db_query($sql)->fetchAll();

  //dd($result, 'Annual Performance');
}

function annual_review_select_key_map() {
  $self_counselor_map = array(
    array(
      'selfkey' => 'client_engagements',
      'commentkey' => 'ce-comments',
      'counselorkey' => '2',
    ),
    array(
      'selfkey' => 'technical_abilities',
      'commentkey' => 'ta-comments',
      'counselorkey' => '1',
    ),
    array(
      'selfkey' => 'consulting_skill',
      'commentkey' => 'cs-comments',
      'counselorkey' => '',
    ),
    array(
      'selfkey' => 'professionalism',
      'commentkey' => 'p-comments',
      'counselorkey' => '1',
    ),
    array(
      'selfkey' => 'leadership',
      'commentkey' => 'l-comments',
      'counselorkey' => '1',
    ),
    array(
      'selfkey' => 'teamwork',
      'commentkey' => 't-comments',
      'counselorkey' => '1',
    ),
    array(
      'selfkey' => 'business_development',
      'commentkey' => 'bd-comments',
      'counselorkey' => '1',
    ),
    array(
      'selfkey' => 'career_counseling',
      'commentkey' => 'cc-comments',
      'counselorkey' => '1',
    ), array(
      'selfkey' => 'internal_contributions',
      'commentkey' => 'ic-comments',
      'counselorkey' => '1',
    ),
    array(
      'selfkey' => 'perficient_basics',
      'commentkey' => 'pb-comments',
      'counselorkey' => '1',
    ),
  );

  return $self_counselor_map;
}

/**
 * Get textarea
 * 
 */
function annual_review_description_map() {
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
  dd($rreid,'rreid');
  audit_review_result_update($rreid, 4);
  return true;
}

function disapprove_review_result() {
  $rreid = $_POST['rreid'];
  audit_review_result_update($rreid, 2);
  return true;
}


/**
 * 
 * 
 */
function audit_review_result_update($rreid, $rstatus) {
  $num_updated = db_update('prs_rel_review_employee')
      ->fields(array(
        'rstatus' => $rstatus,
      ))
      ->condition('rreid', $rreid)
      ->execute();
  
  if ($num_updated == 1) {
    //rstatus == 4 stand for that counselee disapprove the result
    if ($rstatus == 4) {
      counselee_aduit_review_result_remind($rreid, TRUE);
    }
    else if ($rstatus == 2) {
      counselee_aduit_review_result_remind($rreid, FALSE);
    }
    return 'success';
  }
  return 'failed';
}