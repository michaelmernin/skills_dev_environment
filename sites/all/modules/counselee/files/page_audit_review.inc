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
  //According to different review type to display the review
  $review_type = get_review_type_by_rreid($rreid);
  switch ($review_type) {
    case ANNUAL_REVIEW:
      $self_review = get_self_review_comment($nid);
      $counselor_review = get_counselor_review_comment($nid);
      module_load_include('inc', 'counselee', 'files/review_assessment_modification');
      $peer_comment_info_set = get_peer_comment_information_array($rreid);
      $pageContent .= get_project_message_part($self_review);
      $pageContent .= get_performance_evaluation($self_review, $counselor_review, $peer_comment_info_set);
      $pageContent .= get_extend_description_part($self_review, $counselor_review);
      $pageContent .= get_overall_rating_part_audit($self_review, $counselor_review);
      $pageContent .= theme('page_audit_review_score', array());
      $pageContent .= theme('page_audit_review_btn_approve', array('rreid' => $rreid));
      break;
    case PROJECT_REVIEW:

      $pageContent = get_page_review_content($rreid);
      $filename = drupal_get_path('module', 'counselee') . '/templates/' . 'overall_rating_project.tpl.php';
      $pageContent .= file_get_contents($filename);
      $pageContent .= theme('page_audit_review_btn_approve', array('rreid' => $rreid));


      break;
    case THREE_MONTH_REVIEW:
      break;
    default:
      ;
  }

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
  $user_identity = get_user_identity($rreid);
  if ($user_identity == 0)
    print approve_review_result_by_counselee($rreid);
  else if ($user_identity == 2)
    print approve_review_result_by_gm($rreid);
}

function approve_review_result_by_counselee($rreid) {
  $fields = array(
    'rstatus' => 4,
    'isCounseleeApproved' => 1,);

  audit_review_result_update($rreid, 4, $fields);
  $msg = 'You had approved the review result.';
  drupal_set_message($msg, 'status');
  return true;
}

function approve_review_result_by_gm($rreid) {
  $fields = array(
    'rstatus' => 6,
    'isGmApproved' => 1,);
  audit_review_result_update($rreid, 6, $fields);
  $msg = 'You had approved the review result.';
  drupal_set_message($msg, 'status');
  return true;
}

function disapprove_review_result() {
  $rreid = $_POST['rreid'];
  $disapprove_reason = $_POST['disapproveReason'];

  $user_identity = get_user_identity($rreid);
  switch ($user_identity) {
    case 0:
      disapprove_review_result_by_counselee($rreid, $disapprove_reason);
      break;
    case 2:
      disapprove_review_result_by_gm($rreid, $disapprove_reason);
      break;
    default:
      break;
  }
  print true;
}

function disapprove_review_result_by_counselee($rreid, $disapprove_reason) {
  $fields = array(
    'rstatus' => 2,
    'isCounseleeApproved' => 2,
    'counseleeReason' => $disapprove_reason);
  audit_review_result_update($rreid, 2, $fields);
  $msg = 'You had disapproved the review result.';
  drupal_set_message($msg, 'status');
  print true;
}

function disapprove_review_result_by_gm($rreid, $disapprove_reason) {
  $fields = array(
    'rstatus' => 2,
    'isGmApproved' => 2,
    'gmReason' => $disapprove_reason);
  audit_review_result_update($rreid, 2, $fields);
  $msg = 'You had disapproved the review result.';
  drupal_set_message($msg, 'status');
  print true;
}

function submit_review_result() {
  $rreid = $_POST['rreid'];

  $fields = array(
    'rstatus' => 5
  );

  audit_review_result_update($rreid, 5, $fields);
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
function audit_review_result_update($rreid, $rstatus, $fields) {
  $num_updated = db_update('prs_rel_review_employee')
      ->fields($fields)
      ->condition('rreid', $rreid)
      ->execute();

  if ($num_updated == 1) {

    switch ($rstatus) {
      case JOINT_REVIEW:
        counselee_audit_review_result_remind($rreid, TRUE);
        break;
      case REVIEW_BY_COUNSELOR:
        if (get_user_identity($rreid) == 0)
          counselee_audit_review_result_remind($rreid, FALSE, $fields['counseleeReason']);
        else if (get_user_identity($rreid) == 2)
          gm_disapprove_review_result_remind($rreid);
        break;
      case GM_REVIEW:
        counselor_submit_review_result_remind($rreid);
        break;
      case GM_APPROVED:
        gm_approve_review_result_remind($rreid);
        break;
      default:
        break;
    }
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
    case 1:
      $category = "<fieldset class=\"webform-component-fieldset webform-catalogue \">
    <legend><span class=\"fieldset-legend\">$name</span></legend>$content</fieldset>";
      return $category;
    default :
      return '';
  }
}

function get_nid_by_rreid($rreid) {
  $sql = "select provider.nid from {prs_rel_provider_employee} provider where provider.employeename = provider.providername and provider.rreid=$rreid";
  $result = db_query($sql)->fetchAll();

  if (count($result) == 1) {
    return $result[0]->nid;
  }
  else
    return null;
}

function counselor_submit_review_result($rreid) {

  $identity = get_user_identity($rreid);
  if ($identity != 1) {
    drupal_set_message('You have no right to access!');
    drupal_goto('accessdenied');
  }
  $pageContent = '';
  $nid = get_nid_by_rreid($rreid);
  //According to different review type to display the review
  $review_type = get_review_type_by_rreid($rreid);
  switch ($review_type) {
    case ANNUAL_REVIEW:
      $self_review = get_self_review_comment($nid);
      $counselor_review = get_counselor_review_comment($nid);
      module_load_include('inc', 'counselee', 'files/review_assessment_modification');
      $peer_comment_info_set = get_peer_comment_information_array($rreid);

      $pageContent .= get_project_message_part($self_review);
      $pageContent .= get_performance_evaluation($self_review, $counselor_review, $peer_comment_info_set);
      $pageContent .= get_extend_description_part($self_review, $counselor_review);
      $pageContent .= get_overall_rating_part_audit($self_review, $counselor_review);
      $pageContent .= theme('page_audit_review_score', array());
      $pageContent .= theme('page_audit_review_btn_submit', array('rreid' => $rreid));
      break;
    case PROJECT_REVIEW:
      $pageContent = get_page_review_content($rreid);
      $pageContent .= get_overall_rating_part_by_rreid($rreid);
      $pageContent .= theme('page_audit_review_btn_submit', array('rreid' => $rreid));

      break;
    case THREE_MONTH_REVIEW:
      break;
    default:
      ;
  }
  return $pageContent;
}

function get_overall_rating_part_audit($self_review, $counselor_review) {

  $extend_description['description'] = $self_review['overall_rating']->name;
  $extend_description['self_description'] = $self_review['overall_rating']->data;
  $extend_description['counselor_description'] = $counselor_review['overall_rating']->rating;

  return '<div>
              <div style="font-weight: 600;float: left">·Counselor Overall Rating:  </div>
              <div class="color-rating-box" id="counselor-overall_rating-content">' . $extend_description['counselor_description'] . '</div>
              <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">| ·Self Overall Rating: </div>
              <div class="color-rating-box" id="counselee-overall_rating-content">' . $extend_description['self_description'] . '</div>
            </div><br><br><br>';
}

function gm_audit_review_result($rreid) {

  $identity = get_user_identity($rreid);

  if ($identity != 2) {
    drupal_set_message('You have no right to access!');
    drupal_goto('accessdenied');
  }

  $nid = get_nid_by_rreid($rreid);
  $pageContent = '';

  $review_type = get_review_type_by_rreid($rreid);
  switch ($review_type) {
    case ANNUAL_REVIEW:

      $self_review = get_self_review_comment($nid);
      $counselor_review = get_counselor_review_comment($nid);
      module_load_include('inc', 'counselee', 'files/review_assessment_modification');
      $peer_comment_info_set = get_peer_comment_information_array($rreid);

      $pageContent .= get_project_message_part($self_review);
      $pageContent .= get_performance_evaluation($self_review, $counselor_review, $peer_comment_info_set);
      $pageContent .= get_extend_description_part($self_review, $counselor_review);
      $pageContent .= get_overall_rating_part_audit($self_review, $counselor_review);
      $pageContent .= theme('page_audit_review_score', array());
      $pageContent .= theme('page_audit_review_btn_approve', array('rreid' => $rreid));

      break;
    case PROJECT_REVIEW:

      $pageContent = get_page_review_content($rreid);
      $pageContent .= get_overall_rating_part_by_rreid($rreid);
      $pageContent .= theme('page_audit_review_btn_approve', array('rreid' => $rreid));

      break;
    case THREE_MONTH_REVIEW:
      break;
    default:
      ;
  }

  return $pageContent;


















  return $pageContent;
}

function get_page_review_content($rreid) {


  module_load_include('inc', 'counselee', 'files/watch_review_status');

  $cur_user_name = get_current_user_name();
  $reviewee = get_reviewee_from_db($rreid);
  $counselor = get_counselor($reviewee);
  $review_status = get_review_status_from_db($rreid);
  $review_basic_info = get_basic_review_information($rreid);
  $review_type = $review_basic_info[0]->type;

  module_load_include('inc', 'common', 'defined_constants');


  module_load_include('inc', 'counselee', 'files/review_assessment_modification');
  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);

////////////////////////////////////////////////////////
// Item below is for self assessment
////////////////////////////////////////////////////////

  $self_dataset = get_latest_submitted_data_from_db($nid, $sid);
  $item_count = count($self_dataset);
  $self_cid_formkey_type_array = array();
  $self_question_rating_comment_result = process_self_assessment_data($self_dataset, $self_cid_formkey_type_array);
  $none_selection_item = _fetch_all_none_selection_items($self_cid_formkey_type_array);
  $review_info = init_review_info_content();
  $self_comments_array = array();
  $overall_rating = new stdClass();
  _matching_none_selection_items($none_selection_item, $self_dataset, $review_info, $self_comments_array, $overall_rating);
  $review_info_content_num = count($review_info['clientdate']);
  $self_item_count = count($self_question_rating_comment_result);
  $total_item_count = $self_item_count + count($self_comments_array);
////////////////////////////////////////////////////////
// Item below is for peers
////////////////////////////////////////////////////////
  $peer_information_set = get_peer_comment_information_array($rreid, $item_count);
  $question_cid_array = array();
  if ($review_type == PROJECT_REVIEW) {
    $question_cid_array = _generate_question_id_from_form_key($peer_information_set->cid_formkey_type_array);
  }
  ///////////////////////////////////////////////////////
  //Item below is for counselor
  ///////////////////////////////////////////////////////
  $csiid = get_counselor_latest_submissionID($nid);
  if ($csiid != -1) {
    $counselor_submit_data = get_synthesized_provider_comment_from_db($nid, $csiid);
    $revised_comments = _fetch_counselor_revised_comments($counselor_submit_data);
    $overall_rating->counselor_rating = _fetch_counselor_overall_rating($revised_comments);
  }
  else {
    $have_counselor_data = FALSE;
    $revised_comments = array();
    for ($i = 0; $i < $total_item_count + 1; $i++) {
      $revised_comments[$i] = new stdClass();
    }
    $overall_rating->counselor_rating = 0;
  }

  $pageContent = '';
  for ($i = 0; $i < $review_info_content_num; $i++) {
    $proj_item = generate_project_roles_and_responsibilities_info($review_info, $i);
    if (validate_numbers($review_info, $i)) {
      $content = thematize_project_roles_and_responsibilities($rreid, $nid, $reviewee, $proj_item, $i);
      $pageContent .= $content;
    }
  }

  $is_read_only = TRUE;
  foreach ($self_question_rating_comment_result as $key => $value) {
//=====================the question which have pie chart ============CONST===============================
    if (!isset($value->rating) && !isset($value->comment)) {
      $content = thematize_subtitle($rreid, $nid, $reviewee, $value);
      $pageContent .= $content;
      continue;
    }

    $pie_form_key_array = array('technical_abilities', 'consulting_skills', 'professionalism', 'leadership', 'teamwork');
    if (in_array($value->form_key, $pie_form_key_array)) {
      $form_key = $value->form_key . '_category';
      if ($peer_information_set->have_peer_data) {
        $fieldset_cid = get_fieldset_key_in_peer_table($form_key, $peer_information_set->cid_formkey_type_array);
        $children_array = get_children_array_by_cid($fieldset_cid, $peer_information_set->adj_list);
        $peer_name = array();
//        $comment_name = array();
        $peer_json = '';
        _manage_data_return_three_array_readonly($children_array, $peer_information_set->peer_question_rating_comment_result, $peer_name, $peer_json);
        $pie_data = _manage_pie_chart($peer_name, $form_key);
//        dd($peer_json, '$peer_json');
      }
      else {
        $peer_json = '';
        $pie_data = new stdClass();
        $pie_data->id = $form_key;
        $pie_data->all_avg = NO_DATA;
        $pie_data->have_peer_data = FALSE;
      }
//=====================pie chart end=====================================================================
      $content = thematize_review_content_full($key, $rreid, $nid, $reviewee, $value, NULL, $pie_data, $peer_json, $revised_comments, $total_item_count, $overall_rating, $is_read_only);
      $pageContent .= $content;
    }
    else {
      $content = thematize_review_content_without_chart($key, $rreid, $nid, $reviewee, $value, $revised_comments, $total_item_count, $overall_rating, $is_read_only);
      $pageContent .= $content;
    }
  }
  foreach ($self_comments_array as $key => $value) {
    $content = thematize_review_content_comment_only($key, $self_item_count, $rreid, $nid, $reviewee, $value, $revised_comments, $total_item_count, $is_read_only);
    $pageContent .= $content;
  }
  return $pageContent;
}