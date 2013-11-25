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
//  dd('accessdashboard_displaydenied');
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
//  dd('show_pr_list');
  if (!user_is_anonymous()) {
    return '';
  }
  else {
    drupal_goto('login');
  }
}

function accessdenied() {
//  dd('accessdenied');
  if (!user_is_anonymous()) {
//    dd('user_is_anonymous');
    drupal_goto('mydashboard');
  }
  else {
//    dd('login');
    drupal_goto('login');
  }
}

function feedbacksubmitsuccess() {
  $pageContent = '';
  return $pageContent;
}