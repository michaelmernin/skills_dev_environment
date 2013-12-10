<?php

/**
 * @file
 * Page callback file for the counselee module.
 */
function watch_basic_information($review_id) {
  access_authen($review_id);
  $counsellor_path = drupal_get_path('module', 'counsellor');
  drupal_add_js($counsellor_path . '/js/counsellor.js');
  $basic_info = get_basic_review_information($review_id);
//  dd($basic_info, '$basic_info');
  if (count($basic_info) != 0) {
    if ($basic_info[0]->employeeName != '') {
      $my_info = get_my_anniversary_counselor($basic_info[0]->employeeName);
    }
    $pageContent = theme('watch_basic_info', array(
      'basic_info' => $basic_info[0],
      'myInfo' => $my_info,
    ));
  }
  return $pageContent;
}

/**
 * @file
 * Page callback file for counselee module
 * in order to differ page display according to the role of the login userId_flag
 * */
function display_select_peers_page($rreid = NULL) {
  access_authen($rreid);
  $reviewee = get_reviewee_from_db($rreid);
  $all_employee_array = get_all_peer_candidates_from_db($rreid);
  $selected_peers_array = get_selected_peers_from_db($rreid);
  $unselected_peers_array = get_unselected_peers_from_db($reviewee, $selected_peers_array);
  $select_peers_status = get_peers_status($rreid);
  $userId_flag = get_user_identity($rreid);
  $pageContent = display_peer_selection_page($rreid, $userId_flag, $all_employee_array, $selected_peers_array, $unselected_peers_array, $select_peers_status);
  return $pageContent;
}

/**
 * @file
 * Page callback function for self assessment.
 * */
function display_self_assessment($rreid = NULL) {
//  access_authen($rreid);
  // all the page display, access control and other function will be implemented later!
  $typeset = get_basic_review_information($rreid);
  if (count($typeset)) {
    $type = $typeset[0]->type;
  }
  else {
    $type = 0;
  }
//  dd($type,'$type');
//  dd(get_user_identity_by_nid($node->nid));
  if ($type == 0 && get_user_identity($rreid) != 0) {
    drupal_goto('accessdenied');
  }
  $result = get_provider_info($rreid);
  if (count($result)) {
    $nid = $result[0]->nid;
    $node = node_load($nid);
//    dd($node,'$node');
  }

  $pageContent = theme('self_assessment', array(
    'rreid' => $rreid,
    'node' => $node
  ));
  return $pageContent;
}

/**
 * display_firsttime_select
 * The first time select peer, depending the role of login user, func returns different $spstatus
 * */
function display_firsttime_select($rreid, $all_employee_array, $spstatus, $userId_flag, $select_peers_status) {
  return theme('select_peers', array(
    'rreid' => $rreid,
    'all_employee_array' => $all_employee_array,
    'spstatus' => $spstatus,
    'userId_flag' => $userId_flag, // 0 for counselee, 1 for counselor
    'peers_status' => $select_peers_status,
  ));
}

function display_select($rreid, $selected_peers_array, $unselected_peers_array, $spstatus, $userId_flag, $select_peers_status) {
  return theme('select_peers', array(
    'rreid' => $rreid,
    'selected_peers_array' => $selected_peers_array,
    'unselected_peers_array' => $unselected_peers_array,
    'spstatus' => $spstatus,
    'userId_flag' => $userId_flag,
    'peers_status' => $select_peers_status,
  ));
}

function display_update($rreid, $selected_peers_array, $unselected_peers_array, $spstatus, $userId_flag, $select_peers_status) {
  return theme('select_peers', array(
    'rreid' => $rreid,
    'selected_peers_array' => $selected_peers_array,
    'unselected_peers_array' => $unselected_peers_array,
    'spstatus' => $spstatus,
    'userId_flag' => $userId_flag,
    'peers_status' => $select_peers_status,
  ));
}

/**
 * the function that manupulates to display different pages
 * */
function display_peer_selection_page($rreid, $userId_flag, $all_employee_array, $selected_peers_array, $unselected_peers_array, $select_peers_status) {
  $spstatus = get_review_spstatus_from_db($rreid);
  if ($spstatus == 0) {
    return display_firsttime_select($rreid, $all_employee_array, $spstatus, $userId_flag, $select_peers_status);  // selects for the 1st time, and no selection record yet.
  }
  else if (($spstatus == 1 && $userId_flag == 1) || ($spstatus == 2 && $userId_flag == 0)) {
    // $sptatus == '1' && $userId_flag == '1' ==>  couselee has selected and counselor selects for the 1st time
    // $sptatus == '0' && $userId_flag == '2' ==>  counselor has selected and counselee selects for the 1st time

    return display_select($rreid, $selected_peers_array, $unselected_peers_array, $spstatus, $userId_flag, $select_peers_status);
  }
  else {
    // both have selected their first time and from now on selection means updating.
    return display_update($rreid, $selected_peers_array, $unselected_peers_array, $spstatus, $userId_flag, $select_peers_status);
  }
}

/**
 * Page callback function of view self assessment content
 * */
function load_self_assessment_content($rreid = NULL) {
  access_authen($rreid);
  $cur_user_name = get_current_user_name();
  $reviewee = get_reviewee_from_db($rreid);
  $counselor = get_counselor($reviewee);
  $review_status = get_review_status_from_db($rreid);
  module_load_include('inc', 'counselee', 'files/review_assessment_modification');

  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);

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

  $pageContent = '';
  for ($i = 0; $i < $review_info_content_num; $i++) {
    $clientdate = $review_info['clientdate'][$i]->DATA;
    $project_roles_and_responsibilities = $review_info['project_roles_and_responsibilities'][$i]->DATA;
    $startdate = $review_info['startdate'][$i]->DATA;
    $enddate = $review_info['enddate'][$i]->DATA;
    if (validate_numbers($review_info, $i)) {
      $content = theme('project_roles_and_responsibilities', array(
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'review_info_content_num' => $i,
        'clientdate' => $clientdate,
        'project_roles_and_responsibilities' => $project_roles_and_responsibilities,
        'startdate' => $startdate,
				'enddate' => $enddate,
      ));
      $pageContent .= $content;
    }
  }

  // dd($self_question_rating_comment_result, 'self_question_rating_comment_result');
  foreach ($self_question_rating_comment_result as $key => $value) {
    $content = theme('view_self_assessment_item', array(
      'item_num' => $key,
      'self_dataset' => $value,
      'overall_rating' => $overall_rating,
    ));
    $pageContent .= $content;
  }

  foreach ($self_comments_array as $key => $value) {
    $content = theme('view_self_assessment_comment_only', array(
      'item_num' => $self_item_count + $key,
      'self_comment' => $value,
    ));
    $pageContent .= $content;
  }

  return $pageContent;
}

/**
 * Page callback function of review content.
 * */
function load_review_content($rreid = NULL) {
  access_authen($rreid);
  $cur_user_name = get_current_user_name();
  $reviewee = get_reviewee_from_db($rreid);
  $counselor = get_counselor($reviewee);
  $review_status = get_review_status_from_db($rreid);

  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);
  module_load_include('inc', 'counselee', 'files/review_assessment_modification');

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
    $clientdate = $review_info['clientdate'][$i]->DATA;
    $project_roles_and_responsibilities = $review_info['project_roles_and_responsibilities'][$i]->DATA;
		$startdate = $review_info['startdate'][$i]->DATA;
		$enddate = $review_info['enddate'][$i]->DATA;
    if (validate_numbers($review_info, $i)) {
      $content = theme('project_roles_and_responsibilities', array(
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'review_info_content_num' => $i,
        'clientdate' => $clientdate,
        'project_roles_and_responsibilities' => $project_roles_and_responsibilities,
				'startdate' => $startdate,
				'enddate' => $enddate,
      ));
      $pageContent .= $content;
    }
  }

  foreach ($self_question_rating_comment_result as $key => $value) {
//=====================the question which have pie chart ============CONST===============================
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
      $content = theme('review_content', array(
        'item_num' => $key,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_dataset' => $value,
        'pie_data' => $pie_data,
        'peer_json' => $peer_json,
        'clor_rating_comment' => $revised_comments[$key],
        'total_item_count' => $total_item_count,
      ));
      $pageContent .= $content;
    }
    else {
      $content = theme('review_content_without_chart', array(
        'item_num' => $key,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_dataset' => $value,
        'clor_rating_comment' => $revised_comments[$key],
        'total_item_count' => $total_item_count,
        'overall_rating' => $overall_rating,
      ));
      $pageContent .= $content;
    }
  }
  foreach ($self_comments_array as $key => $value) {
    $content = theme('review_content_comment_only', array(
      'item_num' => $self_item_count + $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_comment' => $value,
      'clor_rating_comment' => $revised_comments[$self_item_count + $key],
      'total_item_count' => $total_item_count,
    ));
    $pageContent .= $content;
  }
  // generate comment only content here.
  return $pageContent;
}

/**
 * update_review_spstatus_manager
 * according to the user id and current spstatus of the review, call the corresponding update spstatus 
 * executing functions. The attribute "spstatus" is an integer in table "prs_rel_review_employee". Status of 
 * counselee and counselor select peers for review:
 * 0 for no one selected;
 * 1 for only counselee selected;
 * 2 for * only counselor selected;
 * 3 for both selected 
 * @param int $rreid
 * @param int @userId_flag
 */
function update_review_spstatus_manager($rreid, $userId_flag) {
  $cur_spstatus = get_review_spstatus_from_db($rreid);
  if ($cur_spstatus == 0 && $userId_flag == 0) {
    // counselee select first, set spstatus to 1.
    return update_review_spstatus($rreid, 1);
  }
  else if ($cur_spstatus == 0 && $userId_flag == 1) {
    // counselor select first, set spstatus to 2.
    return update_review_spstatus($rreid, 2);
  }
  else if ($cur_spstatus == 1 && $userId_flag == 1) {
    // counselee has selected, and counselor select for the 1st time. After selection, spstatus will be 3.
    return update_review_spstatus($rreid, 3);
  }
  else if ($cur_spstatus == 2 && $userId_flag == 0) {
    // counselor has selected, and counselee select for the 1st time. After selection, spstatus will be 3.
    return update_review_spstatus($rreid, 3);
  }
  else {
    // counselor and conselee have both selected (The case that spstatus already equals to 3), or one of them has selected several times but the other does not start his/her first time selection. There is no need to change the spstatus.
    return;
  }
}

/**
 * update_review_spstatus
 * the func that executing db_update operation actually.
 * @param int $rreid
 * @param int $dest_spstatus
 * */
function update_review_spstatus($rreid, $dest_spstatus) {
  $result = db_update('prs_rel_review_employee')
      ->fields(array('spstatus' => $dest_spstatus))
      ->condition('rreid', $rreid)
      ->execute();
}

/**
 * function _insert_items_to_DB
 * @param int $rreid
 * @param array $providers
 * @param array reference $email_receivers
 * @return array $rpeids_array
 * */
function _insert_items_to_DB($rreid, $providers, &$email_receivers) {
  $rpeid = '';
  $employeeName = _get_provider_employeeName($rreid);
  $prid = _get_provider_prid($rreid);
  $selected_array = get_selected_peers_from_db($rreid);
  $rpeids_array = array();
  foreach ($providers as $provider) {
    if (!in_array($provider, $selected_array)) {
      $rpeid = db_insert('prs_rel_provider_employee')
              ->fields(array(
                'employeeName' => $employeeName,
                'prid' => $prid,
                'rreid' => $rreid,
                'providerName' => $provider,
                  // 'token' => $token,
              ))->execute();
      if ($rpeid != -1) {
        array_push($rpeids_array, $rpeid);
        array_push($email_receivers, $provider);
      }
    }
  }
  if (count($rpeids_array) != 0)
    return $rpeids_array;
  else {
    $msg = 'You did not update any peers!';
    drupal_set_message($msg, 'status');
    return;
  }
}

/**
 * _get_provider_employeeName()
 * @param int $rreid
 * @return string employeeName
 * */
function _get_provider_employeeName($rreid) {
  $query = db_select('prs_rel_review_employee', 'prre')
      ->fields('prre', array('employeeName'))
      ->condition('prre.rreid', $rreid, '=');
  $result = $query->execute()->fetchAll();
  $employeeName = generate_array($result, 'employeeName');
  return $employeeName[0];
}

function _get_provider_prid($rreid) {
  $query = db_select('prs_rel_review_employee', 'prre')
      ->fields('prre', array('prid'))
      ->condition('prre.rreid', $rreid, '=');
  $result = $query->execute()->fetchAll();
  $prid = generate_array($result, 'prid');
  return $prid[0];
}

/**
 * format the employee, from "name-name" style to array
 * @param string $employees
 * @return array $employee_array
 */
function explode_employee($employees) {
  $employee_array = array();
  $employee_array = explode('-', $employees);
  return $employee_array;
}

/**
 * page callback function to handle submit peer info creation or update
 * callback params
 * @param type $rreid
 * @param type $providers
 * @param type $spstatus
 * @param type $userId_flag
 * */
function submit_peer_update() {
  $result_val = '1';
  $rreid = $_POST['rreid'];
  $providers = explode_employee($_POST['providers']);
  $spstatus = $_POST['spstatus'];
  $userId_flag = $_POST['userId_flag'];
  $email_receivers = array();

  update_review_spstatus_manager($rreid, $userId_flag);
  $rpeids_array = _insert_items_to_DB($rreid, $providers, $email_receivers);
  $count = count($rpeids_array);
  if ($count != 0) {
    $msg = $count . ' peer(s) selection updated successfully!';
    drupal_set_message($msg, 'status');
  }
  send_peer_email_notification($rreid, $email_receivers, $userId_flag, $rpeids_array);

  echo $rval;
}

function _generate_headerInfo($headerInfo) {
  return explode('---', $headerInfo);
}

/**
 * generate couselor submittion data from array $hederInfo, $ratings and $comments
 * @param array by reference $headerInfo
 * @param array by reference $ratings
 * @param array by reference $comments
 * @return array that migrating above array together.
 * */
function _generate_counselor_submition_data(&$headerInfo, &$ratings, &$comments) {
  $target_array = array();
  for ($i = 0; $i < 24; $i++) {
    if ($i == 0 || $i == 1) {
      $target_array[$i] = array(
        'count' => $i,
        'header' => $headerInfo[$i],
        'rating' => 0,
        'comment' => '',
      );
    }
    else if ($i == 2 || $i == 9) {
      continue;
    }
    else if (in_array($i, range(3, 8)) || in_array($i, range(10, 14))) {
      $target_array[$i] = array(
        'count' => $i,
        'header' => '',
        'rating' => $ratings[$i],
        'comment' => $comments[$i],
      );
    }
    else if (in_array($i, range(15, 20))) {
      $target_array[$i] = array(
        'count' => $i,
        'header' => '',
        'rating' => 0,
        'comment' => $comments[$i],
      );
    }
    else if ($i == 23) {
      $target_array[$i] = array(
        'count' => $i,
        'header' => '',
        'rating' => $ratings[$i],
        'comments' => '',
      );
    }
  }
  return $target_array;
}

function get_peers_status($rreid) {
  $query = db_select('prs_rel_provider_employee', 'prpe')
      ->fields('prpe')
      ->where('prpe.employeeName!=prpe.providerName')
      ->condition('prpe.rreid', $rreid);
  $result = $query->execute()->fetchAll();
  return $result;
}

function is_provider_item_existing($rreid, $provider) {
  $result = db_select('prs_rel_provider_employee', 'prpe')
          ->fields('prpe')
          ->condition('prpe.rreid', $rreid, '=')
          ->condition('prpe.providerName', $provider, '=')
          ->execute()->rowCount();

  if ($result != 0) {
    return TRUE;
  }
  else {
    return FALSE;
  }
}

function send_peer_email_notification($rreid, $email_receivers, $userId_flag, $rpeids_array) {
  if (module_exists('common') && module_enable(array('common'))) {
    $info = get_basic_review_information($rreid);
  }
  else {
    $msg = 'Email notify module does not exist or enable!';
    drupal_set_message($msg, 'error');
    return;
  }
  if (count($email_receivers) == 0)
    return;

  for ($i = 0; $i < count($email_receivers); $i++) {
    //foreach ($email_receivers as $email_receiver)
    $email_receiver = $email_receivers[$i];
    $employeeName = $info[0]->employeeName;

    //Generate peer review form for each peer  --function format_login_name($login_name) {
    $receiver = format_login_name($email_receiver);
    $counselee = format_login_name($employeeName);
    $form_title = "Annual Performace Peer Review $receiver for $counselee";
    $peer_review_template_name = 'For Demo Peer Review Form Template';
    $node_msg = create_node($form_title, $peer_review_template_name);
    db_update('prs_rel_provider_employee')
        ->fields(array('nid' => $node_msg[0], 'token' => $node_msg[1]))
        ->condition('rpeid', $rpeids_array[$i])
        ->execute();

    $review_description = $info[0]->description;

    $review_end_date = unixTimestampToDateArray($info[0]->period_end);
    $date = date_to_format($review_end_date);
    $base_path = get_curPage_base_url();
    $peer_select_url = $base_path . $node_msg[1];
    $peer_info_params = array(
      'employeeName' => $employeeName,
      'reviewname' => $review_description,
      'datetime' => $date,
      'peersurl' => $peer_select_url,
      'type' => $userId_flag,
      'peername' => $email_receiver,
    );
    $email_address = select_email_from_prs_reviewers(array($email_receiver));
    mail_sender_template_adapter($email_address[0]->mail, $peer_info_params, 1);
  }
}

