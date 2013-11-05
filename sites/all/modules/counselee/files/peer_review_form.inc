<?php

/**
 * @file
 * Page callback file for the counselee module.
 */


function get_peer_basic_information() {

  //$templatePath = drupal_get_path('module', 'test') . '/templates/' . 'peer-review-form.tpl.php';
  //$content = file_get_contents($templatePath);

  $basic_info = get_review_basic_information_by_nid(1);
  $content = theme('peer-review-form', array('basic_info' => $basic_info));
  echo $content;
}

/**
 * Get peer reiview form basic informaiton 
 * 
 * @param string $nid The webform node id
 * 
 */
function get_review_basic_information_by_nid($nid) {
  $query = db_select('prs_rel_provider_employee', 'provider');
  $query->join('prs_rel_employee_counselor_gm', 'relation', 'relation.employeeName = provider.employeeName');
  $query->addField('provider', 'employeeName', 'colleague');
  $query->addField('relation', 'counselorName', 'counselor');
  $query->addField('provider', 'providerName', 'reviewer');
  $query->condition('provider.nid', $nid);
  $result = $query->execute()->fetchAll();

  $result_set = array();
  $index = 0;

  foreach ($result as $value) {
    $temp = array(
      'colleague' => $value->colleague,
      'counselor' => $value->counselor,
      'reviewer' => $value->reviewer,
    );
    $result_set[$index++] = $temp;
  }
  return $result_set;
}

/**
 * 
 * 
 * 
 */
function get_peer_review_score_overall() {

  $peer_review_overall [0] = array('category' => 'Technical Abilities', 'tdid' => 'rating_technical_abilities');
  $peer_review_overall [1] = array('category' => 'Consulting Skills', 'tdid' => 'rating_consulting_skills');
  $peer_review_overall [2] = array('category' => 'Professionalism', 'tdid' => 'rating_professionalism');
  $peer_review_overall [3] = array('category' => 'Leadership', 'tdid' => 'rating_leadership');
  $peer_review_overall [4] = array('category' => 'Teamwork', 'tdid' => 'rating_teamwork');
  $peer_review_overall [5] = array('category' => 'All', 'tdid' => 'rating_all');

  for ($i = 0; $i < count($peer_review_overall); $i++) {
    $peer_review_overall[$i]['reviewer'] = 'Perficient';
    $peer_review_overall[$i]['rating'] = '';
  }

  $content = theme('peer-review-form-overall', array('score_overall' => $peer_review_overall));
  echo $content;
}






