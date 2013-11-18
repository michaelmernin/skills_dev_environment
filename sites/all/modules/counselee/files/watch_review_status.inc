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
//  dd($pageContent,'$pageContent');
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
    drupal_goto('mydashboard');
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
 * @file 
 * Page callback function for view assessment.
 * */
function load_self_assessment($rreid=NULL) {
  access_authen($rreid);
  $reviewee = get_reviewee_from_db($rreid);
  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $dataset = get_latest_submitted_data_from_db($nid);
  $dataset = generate_selfassessment_result_array($dataset);
  $pageContent = theme('view_assessment', array(
    'rreid'     => $rreid,
    'nid'       => $nid,
    'reviewee'  => $reviewee,
    'dataset'  => $dataset,
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

/**
  * Page callback function of submit counselor's rating and comments.
**/
function submit_counselor_assessment() {
  $result_val = '1';
  $nid = $_POST['nid'];
  $headerInfo = $_POST['headerInfo'];
  $ratings = $_POST['ratings'];
  $comments = $_POST['comments'];
  // dd($nid, 'nid');
  // dd($headerInfo, 'headerInfo');
  // dd($ratings, 'ratings');
  // dd($comments, 'comments');
  $headerInfo = _generate_headerInfo($headerInfo);
  $data_array = _generate_counselor_submition_data($headerInfo, $ratings, $comments);
  $csiid = _insert_counselor_submittion_info_to_DB($nid);
  if ($csiid == -1) {
    $msg = 'Inset submittion info failed!';
    drupal_set_message($msg, 'error');
    return;
  }
  $csdid_array = _insert_counselor_submittion_data_to_DB($nid, $csiid, $data_array);
  $count = count($csdid_array);
  if ($count != 0) {
    $msg = 'You have submitted your ratings and comments for your conselee.';
    drupal_set_message($msg, 'status');
  }
  // send_counselor_reviewed_notification($nid, $reviewee);
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
**/
function _generate_counselor_submition_data(&$headerInfo, &$ratings, &$comments) {
  $target_array = array() ;
  for($i = 0; $i < 24; $i++) {
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
    $receiver =  format_login_name($email_receiver);
    $counselee =  format_login_name($employeeName);
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
    $peer_select_url = $base_path.$node_msg[1];
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

/**
 * get_latest_submitted_data_from_db
 * @param int $nid
 * @return array $dataset,data which contains $cid, $sid, $pid, $form_key, $type, $weight, $name, $data.
 * if you would like to accuire additional data from here, feel free to adjust the db_query statement.
**/
function get_latest_submitted_data_from_db($nid) {
  $sid = get_last_sid_by_nid($nid);
  $dataset = db_query('SELECT
        webform_component.cid,
        webform_component.form_key,
        webform_component.name,
        webform_component.type,
        webform_component.pid,
        webform_component.weight,
        IF (wd.sid IS NULL, \'NIL\', wd.sid) AS sid,
        IF (wd.DATA IS NULL, \'NIL\', wd.DATA) AS DATA
        FROM
          webform_component
        LEFT JOIN (
          SELECT
            DATA,
            sid,
            cid,
            nid
          FROM
            webform_submitted_data
          WHERE
            nid = :nid
          AND sid = :sid
        ) wd ON webform_component.cid = wd.cid
        WHERE
          webform_component.nid = :nid
        ORDER BY
          webform_component.cid', 
    array(':sid'=> $sid, ':nid' => $nid ))->fetchAll();
  return $dataset;
}

/**
 * insert counselor's submission info to db table prs_counselor_submit_info
 * @param int $nid
 * @return int $csiid
**/
function _insert_counselor_submittion_info_to_DB($nid) {
  $csiid = '';
  $timestamp = time();
  $csiid = db_insert('prs_counselor_submit_info')
        ->fields(array(
          'nid' => $nid,
          'submitTimestamp' => $timestamp,
          'isDraft' => 1,
          ))->execute();
  return $csiid;
}

/**
 * insert counselor's submission data to db table prs_counselor_submit_data
 * @param int $nid
 * @param int $csiid
 * @param array by reference $data_array
 * @return array $csdid_array
**/
function _insert_counselor_submittion_data_to_DB($nid, $csiid, &$data_array) {
  $csdid_array = array();
  foreach ($data_array as $data) {
    $csdid = db_insert('prs_counselor_submit_data')
          ->fields(array(
            'nid' => $nid,
            'csiid' => $csiid,
            'count' => $data['count'],
            'header' => $data['header'],
            'rating' => $data['rating'],
            'comment' => $data['comment'],
            ))->execute();
    if ($csdid != -1) {
      array_push($csdid_array, $csdid);
    }
  }
  if (count($csdid_array) != 0) {
    return $csdid_array;
  }
  else {
    $msg = 'You did not add any content to DB!';
    drupal_set_message($msg, 'status');
  }
}

// // for future dev.
// function display_tree_structure($qa_table) {
//   $count = count($qa_table);
//   $unvisited_array = array();
//   $visited_array = array();
//   $sibling_array = array();
//   $cur_parrent = 0;
//   do {
//     foreach ($qa_table as $item) {
//       if ($item->pid == $cur_parrent) {
//         array_push($unvisited_array, $item);
//       }
//     }
//     $current = array_shift($unvisited_array);
//     $cur_parrent = $current->cid;
//     array_push($visited_array, $current);
//     array_push($sibling_array, $unvisited_array);
//     foreach ($qa_table as $item) {
//       if ($item->pid == $cur_parrent) {
//         array_push($unvisited_array);
//       }
//     }
//     // No element was added to unvisited array, that means this is a leaf node.
//     if (count($unvisited_array) == 0) {
//       $sib_queue = array_pop($sibling_array);
//       while (count($sibling_array) != 0) {
//         if (count($sib_queue) != 0) {
//           $current = array_shift($sib_queue);
//         }
//         else {
//           $sib_queue = array_pop($sibling_array);
//         }
//       }
//     }
//     else {
//       $current = array_shift($unvisited_array);
//     }
//   } while(count($unvisited_array) != 0);
// }

// function generate_qa_result($qa_table) {
//   foreach ($qa_table as $item) {
//     if ($item->type == 'markup') {
//       continue;
//     }
//     $cid = $item->cid;
//     $question = $item->name;
//     $answer = $item->data;
//     $temp = array();
//   }
// }
// function iteration($qa_table) {
//   $target = array();
//   for ($i = 0; $i < count($qa_table); $i++) {
//     $key = $qa_table[$i]->cid;
//     $value = $i;
//     // $temp = array($key, $value);
//     $target[$key] = $value;
//   }
//   dd($target, 'target');
// }

/**
  *This func is temporarily used to map the header and its result of the survey by hard code.
  *It will be replaced later.
**/
function generate_selfassessment_result_array($qa_table) {
  $target = array();
  for ($i = 0; $i < count($qa_table); $i++) {
/*    if ($qa_table[$i]->cid == 'NIL') {
      continue;
    }
*/    $key = $qa_table[$i]->cid;
    $value = $i;
    $target[$key] = $value;
  }
  return array(
    // header core competencies
    // The 'identifier' property is for identify the array member, 0 for header, 1 for subtitle, 2 for content, 3 for fillform,
    '0' => array(
      'description' => 'Project Roles And Responsibilities',
      'identifier' => 0,
      'question' => 'Project Roles And Responsibilities',
      'rating' => '',
      'comment' => $qa_table[$target[55]]->DATA,
      ),
    '1' => array(
      'description' => 'Client(Date)',
      'identifier' => 0,
      'question' => 'Client(Date)',
      'rating' => '',
      'comment' => $qa_table[$target[54]]->DATA,
      ),
    '2' => array(
      'description' => 'Core competencies',
      'identifier' => 1,
      'question' => 'Core Competencies',
      'rating' => '',
      'comment' => '',
      ),
    '3' => array(
      'description' => 'client engagement',
      'identifier' => 2,
      'question' => $qa_table[$target[8]]->name,
      'rating' => $qa_table[$target[8]]->DATA,
      'comment' => $qa_table[$target[11]]->DATA,
      ),
    '4' => array(
      'description' => 'consulting skills',
      'identifier' => 2,
      'question' => $qa_table[$target[10]]->name,
      'rating' => $qa_table[$target[10]]->DATA,
      'comment' => $qa_table[$target[12]]->DATA,
      ),
    '5' => array(
      'description' => 'technical ability',
      'identifier' => 2,
      'question' => $qa_table[$target[9]]->name,
      'rating' => $qa_table[$target[9]]->DATA,
      'comment' => $qa_table[$target[13]]->DATA,
      ),
    '6' => array(
      'description' => 'professionalism',
      'identifier' => 2,
      'question' => $qa_table[$target[25]]->name,
      'rating' => $qa_table[$target[25]]->DATA,
      'comment' => $qa_table[$target[26]]->DATA,
      ),
    '7' => array(
      'description' => 'leadership',
      'identifier' => 2,
      'question' => $qa_table[$target[27]]->name,
      'rating' => $qa_table[$target[27]]->DATA,
      'comment' => $qa_table[$target[28]]->DATA,
      ),
    '8' => array(
      'description' => 'teamwork',
      'identifier' => 2,
      'question' => $qa_table[$target[29]]->name,
      'rating' => $qa_table[$target[29]]->DATA,
      'comment' => $qa_table[$target[30]]->DATA,
      ),
    '9' => array(
      'description' => 'Internal Contributions',
      'identifier' => 1,
      'question' => 'Internal Contributions',
      'rating' => '',
      'comment' => ''
      ),
    '10' => array(
      'description' => 'business dev',
      'identifier' => 2,
      'question' => $qa_table[$target[15]]->name,
      'rating' => $qa_table[$target[15]]->DATA,
      'comment' => $qa_table[$target[16]]->DATA,
      ),
    '11' => array(
      'description' => 'career counseling',
      'identifier' => 2,
      'question' => $qa_table[$target[17]]->name,
      'rating' => $qa_table[$target[17]]->DATA,
      'comment' => $qa_table[$target[18]]->DATA,
      ),
    '12' => array(
      'description' => 'recruiting assist',
      'identifier' => 2,
      'question' => $qa_table[$target[19]]->name,
      'rating' => $qa_table[$target[19]]->DATA,
      'comment' => $qa_table[$target[20]]->DATA,
      ),
    '13' => array(
      'description' => 'internal contribution',
      'identifier' => 2,
      'question' => $qa_table[$target[21]]->name,
      'rating' => $qa_table[$target[21]]->DATA,
      'comment' => $qa_table[$target[22]]->DATA,
      ),
    '14' => array(
      'description' => 'perficient basis',
      'identifier' => 2,
      'question' => $qa_table[$target[23]]->name,
      'rating' => $qa_table[$target[23]]->DATA,
      'comment' => $qa_table[$target[24]]->DATA,
      ),
    '15' => array(
      'description' => 'Strength',
      'identifier' => 3,
      'question' => 'Strength',
      'rating' => '',
      'comment' => $qa_table[$target[32]]->DATA,
      ),  
    '16' => array(
      'description' => 'Opportunities For Improvement',
      'identifier' => 3,
      'question' => 'Opportunities For Improvement',
      'rating' => '',
      'comment' => $qa_table[$target[34]]->DATA,
      ),
    '17' => array(
      'description' => 'Summary',
      'identifier' => 3,
      'question' => 'Summary',
      'rating' => '',
      'comment' => $qa_table[$target[36]]->DATA,
      ),
    '18' => array(
      'description' => 'Goals',
      'identifier' => 3,
      'question' => 'Goals',
      'rating' => '',
      'comment' => $qa_table[$target[38]]->DATA,
      ),
    '19' => array(
      'description' => 'Development Opportunities',
      'identifier' => 3,
      'question' => 'Development Opportunities',
      'rating' => '',
      'comment' => $qa_table[$target[40]]->DATA,
      ),
    '20' => array(
      'description' => 'Longer Term Goals',
      'identifier' => 3,
      'question' => 'Longer Term Goals',
      'rating' => '',
      'comment' => $qa_table[$target[42]]->DATA,
      ),
    '21' => array(
      'description' => 'Last Year Goal',
      'identifier' => 3,
      'question' => 'Last Year Goal',
      'rating' => '',
      'comment' => $qa_table[$target[43]]->DATA,
      ),
    '22' => array(
      'description' => 'Achievements',
      'identifier' => 3,
      'question' => 'Achievements',
      'rating' => '',
      'comment' => $qa_table[$target[46]]->DATA,
      ),
    '23' => array(
      'description' => 'Overall Scores',
      'identifier' => 4,
      'question' => 'Overall Scores',
      'rating' => '',
      'comment' => $qa_table[$target[49]]->DATA,
      ),
    );
}

// function generate_peerassessment_result_array($qa_table) {
//   $target = array();
//   for ($i = 0; $i < count($qa_table); $i++) {
// /*    if ($qa_table[$i]->cid == 'NIL') {
//       continue;
//     }
// */    $key = $qa_table[$i]->cid;
//     $value = $i;
//     $target[$key] = $value;
//   }
//   return array(
//     // header core competencies
//     // The 'identifier' property is for identify the array member, 0 for header, 1 for subtitle, 2 for content, 3 for fillform,
//     '0' => array(
//       'description' => 'Project Roles And Responsibilities',
//       'identifier' => 0,
//       'question' => 'Project Roles And Responsibilities',
//       'rating' => '',
//       'comment' => $qa_table[$target[55]]->DATA,
//       ),
//     '1' => array(
//       'description' => 'Client(Date)',
//       'identifier' => 0,
//       'question' => 'Client(Date)',
//       'rating' => '',
//       'comment' => $qa_table[$target[54]]->DATA,
//       ),
//     '2' => array(
//       'description' => 'Core competencies',
//       'identifier' => 1,
//       'question' => 'Core Competencies',
//       'rating' => '',
//       'comment' => '',
//       ),
//     '3' => array(
//       'description' => 'client engagement',
//       'identifier' => 2,
//       'question' => $qa_table[$target[8]]->name,
//       'rating' => $qa_table[$target[8]]->DATA,
//       'comment' => $qa_table[$target[11]]->DATA,
//       ),
//     '4' => array(
//       'description' => 'consulting skills',
//       'identifier' => 2,
//       'question' => $qa_table[$target[10]]->name,
//       'rating' => $qa_table[$target[10]]->DATA,
//       'comment' => $qa_table[$target[12]]->DATA,
//       ),
//     '5' => array(
//       'description' => 'technical ability',
//       'identifier' => 2,
//       'question' => $qa_table[$target[9]]->name,
//       'rating' => $qa_table[$target[9]]->DATA,
//       'comment' => $qa_table[$target[13]]->DATA,
//       ),
//     '6' => array(
//       'description' => 'professionalism',
//       'identifier' => 2,
//       'question' => $qa_table[$target[25]]->name,
//       'rating' => $qa_table[$target[25]]->DATA,
//       'comment' => $qa_table[$target[26]]->DATA,
//       ),
//     '7' => array(
//       'description' => 'leadership',
//       'identifier' => 2,
//       'question' => $qa_table[$target[27]]->name,
//       'rating' => $qa_table[$target[27]]->DATA,
//       'comment' => $qa_table[$target[28]]->DATA,
//       ),
//     '8' => array(
//       'description' => 'teamwork',
//       'identifier' => 2,
//       'question' => $qa_table[$target[29]]->name,
//       'rating' => $qa_table[$target[29]]->DATA,
//       'comment' => $qa_table[$target[30]]->DATA,
//       ),
//     '9' => array(
//       'description' => 'Internal Contributions',
//       'identifier' => 1,
//       'question' => 'Internal Contributions',
//       'rating' => '',
//       'comment' => ''
//       ),
//     '10' => array(
//       'description' => 'business dev',
//       'identifier' => 2,
//       'question' => $qa_table[$target[15]]->name,
//       'rating' => $qa_table[$target[15]]->DATA,
//       'comment' => $qa_table[$target[16]]->DATA,
//       ),
//     '11' => array(
//       'description' => 'career counseling',
//       'identifier' => 2,
//       'question' => $qa_table[$target[17]]->name,
//       'rating' => $qa_table[$target[17]]->DATA,
//       'comment' => $qa_table[$target[18]]->DATA,
//       ),
//     '12' => array(
//       'description' => 'recruiting assist',
//       'identifier' => 2,
//       'question' => $qa_table[$target[19]]->name,
//       'rating' => $qa_table[$target[19]]->DATA,
//       'comment' => $qa_table[$target[20]]->DATA,
//       ),
//     '13' => array(
//       'description' => 'internal contribution',
//       'identifier' => 2,
//       'question' => $qa_table[$target[21]]->name,
//       'rating' => $qa_table[$target[21]]->DATA,
//       'comment' => $qa_table[$target[22]]->DATA,
//       ),
//     '14' => array(
//       'description' => 'perficient basis',
//       'identifier' => 2,
//       'question' => $qa_table[$target[23]]->name,
//       'rating' => $qa_table[$target[23]]->DATA,
//       'comment' => $qa_table[$target[24]]->DATA,
//       ),
//     '15' => array(
//       'description' => 'Strength',
//       'identifier' => 3,
//       'question' => 'Strength',
//       'rating' => '',
//       'comment' => $qa_table[$target[32]]->DATA,
//       ),  
//     '16' => array(
//       'description' => 'Opportunities For Improvement',
//       'identifier' => 3,
//       'question' => 'Opportunities For Improvement',
//       'rating' => '',
//       'comment' => $qa_table[$target[34]]->DATA,
//       ),
//     '17' => array(
//       'description' => 'Summary',
//       'identifier' => 3,
//       'question' => 'Summary',
//       'rating' => '',
//       'comment' => $qa_table[$target[36]]->DATA,
//       ),
//     '18' => array(
//       'description' => 'Goals',
//       'identifier' => 3,
//       'question' => 'Goals',
//       'rating' => '',
//       'comment' => $qa_table[$target[38]]->DATA,
//       ),
//     '19' => array(
//       'description' => 'Development Opportunities',
//       'identifier' => 3,
//       'question' => 'Development Opportunities',
//       'rating' => '',
//       'comment' => $qa_table[$target[40]]->DATA,
//       ),
//     '20' => array(
//       'description' => 'Longer Term Goals',
//       'identifier' => 3,
//       'question' => 'Longer Term Goals',
//       'rating' => '',
//       'comment' => $qa_table[$target[42]]->DATA,
//       ),
//     '21' => array(
//       'description' => 'Last Year Goal',
//       'identifier' => 3,
//       'question' => 'Last Year Goal',
//       'rating' => '',
//       'comment' => $qa_table[$target[43]]->DATA,
//       ),
//     '22' => array(
//       'description' => 'Achievements',
//       'identifier' => 3,
//       'question' => 'Achievements',
//       'rating' => '',
//       'comment' => $qa_table[$target[46]]->DATA,
//       ),
//     '23' => array(
//       'description' => 'Overall Scores',
//       'identifier' => 4,
//       'question' => 'Overall Scores',
//       'rating' => '',
//       'comment' => $qa_table[$target[49]]->DATA,
//       ),
//     );
// }