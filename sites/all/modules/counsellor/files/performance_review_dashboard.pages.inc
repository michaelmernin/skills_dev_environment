<?php

/**
 * @file
 * Page callback file for the performance review module.
 */

/**
 * Display dashboard page
 * @global type $user
 * @return string 
 */
function show_dashboard_page() {
    if (!user_is_anonymous()) {
        return '';
    } else {
        drupal_goto('login');
    }
}

/**
 * Display login page
 * @return string 
 */
function show_login_page() {
    if (!user_is_anonymous()) {
        drupal_goto('mydashboard');
    }
    return '';
}

/**
 * Display performance review list management for special user
 * @return string 
 */
function show_pr_list() {
    if (!user_is_anonymous()) {
        return '';
    } else {
        drupal_goto('login');
    }
}
