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
  $nid = 21;
  $self_review = get_self_review_comment($nid);
  $counselor_review = get_counselor_review_comment($nid);

  //Define One Problem Structure
  $node = array(
    'description' => 'Jack',
    'self_rating' => null,
    'counselor_rating' => null,
    'peer_rating' => null,
    'self_comment' => 'It is hard',
    'counselor_comment' => 'Very nice',
    'peer_comment' => 'No comment',
  );

  $content = theme('component_select', array('node' => $node));
  dd($content);
  $pageContent = $content;

  //dd($pageContent);
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
  //dd($result);
  //Array to Map
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
}