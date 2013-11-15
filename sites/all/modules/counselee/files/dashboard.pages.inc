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

  dd($rpeid, 'rpeid');
  $nid = 21;
  get_self_review_comment($nid);
  
  dd('theme');
  $pageContent = theme('component_select', array());
  dd($pageContent);
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
}