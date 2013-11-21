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