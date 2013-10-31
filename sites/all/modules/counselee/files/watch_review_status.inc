<?php

/**
 * @file
 * Page callback file for the counselee module.
 */
function watch_basic_information($review_id) {
  $pageContent = '';
  return $pageContent;
}

/**
 * @file
 * Page callback file for counselee module
 * in order to differ page display according to the role of the login userId_flag
**/ 
function display_select_peers_page($rreid = NULL) {
    access_authen($rreid);
    $all_employee_array = get_all_peer_candidates_from_db($rreid);
    $selected_peers_array = get_selected_peers_from_db($rreid);
    $unselected_peers_array = get_unselected_peers_from_db($rreid, $selected_peers_array);
    $select_peers_status = get_peers_status($rreid);
    $userId_flag = get_user_identity($rreid);
    $pageContent = display_peer_selection_page($rreid, $userId_flag, $all_employee_array, $selected_peers_array, $unselected_peers_array, $select_peers_status);

  return $pageContent;
}

/**
 * display_firsttime_select
 * The first time select peer, depending the role of login user, func returns different $spstatus
**/
function display_firsttime_select($rreid, $all_employee_array, $spstatus, $userId_flag, $select_peers_status) {
    return theme('select_peers', array(
        'rreid' => $rreid,      
        'all_employee_array' => $all_employee_array,
        'spstatus' => $spstatus,
        'userId_flag' => $userId_flag,    // 0 for counselee, 1 for counselor
        'peers_status'=>$select_peers_status,  
        ));
}

function display_select($rreid, $selected_peers_array, $unselected_peers_array, $spstatus, $userId_flag, $select_peers_status) {
    return theme('select_peers', array(
        'rreid' => $rreid,
        'selected_peers_array' => $selected_peers_array,
        'unselected_peers_array' => $unselected_peers_array,
        'spstatus' => $spstatus,
        'userId_flag' => $userId_flag,
        'peers_status'=>$select_peers_status,  
        ));
}

function display_update($rreid, $selected_peers_array, $unselected_peers_array, $spstatus, $userId_flag, $select_peers_status) {
    return theme('select_peers', array(
        'rreid' => $rreid,
        'selected_peers_array' => $selected_peers_array,
        'unselected_peers_array' => $unselected_peers_array,
        'spstatus' => $spstatus,
        'userId_flag' => $userId_flag,
        'peers_status'=>$select_peers_status,  
        ));
}

/**
 * the function that manupulates to display different pages
**/
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
**/ 
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
**/
function _insert_items_to_DB($rreid, $providers, &$email_receivers) {
  $rpeid = '';
  $employeeName = _get_provider_employeeName($rreid);
  $prid = _get_provider_prid($rreid);
  $selected_array = get_selected_peers_from_db($rreid);
  $rpeids_array = array();
  foreach($providers as $provider) {
    if (!in_array($provider, $selected_array)) {
      $rpeid = db_insert('prs_rel_provider_employee')
              ->fields(array(
                'employeeName' => $employeeName,
                'prid' => $prid, 
                'rreid' => $rreid,
                'providerName' => $provider,
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

/*
function insert_peer_item_to_DB($rreid, $providers, $spstatus, $userId_flag, &$email_receivers) {
  if ($userId_flag == -1) {
    drupal_goto('mydashboard');
  }
  $rpeid = '';
  $rpeids_array = array();
  $employeeName = _get_provider_employeeName($rreid);
  $prid = _get_provider_prid($rreid);

  if ($spstatus == 0) {
    if ($userId_flag == 0) {
      foreach ($providers as $provider) {
        $result = db_update('prs_rel_review_employee');
                $result->fields(array('spstatus' => 1,));
                $result->condition('rreid', $rreid, '=');
                $result->execute();

        $rpeid = db_insert('prs_rel_provider_employee')
               ->fields(array(
                'employeeName' => $employeeName,
                'prid' => $prid,
                'rreid' => $rreid,
                'providerName' => $provider,
              ))->execute();
        dd($rpeid);
        array_push($email_receivers, $provider);
        if ($rpeid != -1) {
          array_push($rpeids_array, $rpeid);
        }
      }
    }
    else if ($userId_flag == 1) {
      foreach ($providers as $provider) {
       /* $rpeid = db_insert('prs_rel_review_provider')
              ->fields(array(
                'employeeName' => $employeeName,
                'prid' => $prid,
                'provider' => $provider,
                'spstatus' => 2,
                ))->execute();
        array_push($email_receivers, $provider);
        $result = db_update('prs_rel_review_employee');
                $result->fields(array('spstatus' => 2,));
                $result->condition('rreid', $rreid, '=');
                $result->execute();

          $rpeid = db_insert('prs_rel_provider_employee')
                 ->fields(array(
                  'employeeName' => $employeeName,
                  'prid' => $prid,
                  'rreid' => $rreid,
                  'providerName' => $provider,
                ))->execute();
          array_push($email_receivers, $provider);
       
      }
    }
    // else {

    // }
  }
  // else if (($spstatus == 1 && $userId_flag == 1) || ($spstatus == 2 && $userId_flag == 0)) {
  //   foreach ($providers as $provider) {
  //       $rpeid = db_insert('prs_rel_review_provider')
  //             ->fields(array(
  //               'employeeName' => $employeeName,
  //               'prid' => $prid,
  //               'provider' => $provider,
  //               'spstatus' => 3,
  //               ))->execute();
      // }
  // // }
  // else {

  // }
  return $rpeids_array;
}

function update_peer_item_to_DB($rreid, $providers, $spstatus, $userId_flag, &$email_receivers) {
   if ($userId_flag == -1) {
    drupal_goto('mydashboard');
  }
  $rpeid = '';
  $rpeids_array = array();
  $employeeName = _get_provider_employeeName($rreid);
  $prid = _get_provider_prid($rreid);

  if (($spstatus == 1 && $userId_flag == 1) || ($spstatus == 2 && $userId_flag == 0)) {
    $count = 0;
    foreach($providers as $provider) {
      if (!is_provider_item_existing($rreid, $provider)) {
          $result = db_update('prs_rel_review_employee');
                $result->fields(array('spstatus' => 3,));
                $result->condition('rreid', $rreid, '=');
                $result->execute();

          $rpeid = db_insert('prs_rel_provider_employee')
                 ->fields(array(
                  'employeeName' => $employeeName,
                  'prid' => $prid,
                  'rreid' => $rreid,
                  'providerName' => $provider,
                ))->execute();
          array_push($email_receivers, $provider);
          if ($rpeid != -1) {
        array_push($rpeids_array, $rpeid);
        }
        $count++;
      }
      else {
        continue;
      }
    }
  }
  else {
    foreach($providers as $provider) {
      if (!is_provider_item_existing($rreid, $provider)) {
          $rpeid = db_insert('prs_rel_provider_employee')
                 ->fields(array(
                  'employeeName' => $employeeName,
                  'prid' => $prid,
                  'rreid' => $rreid,
                  'providerName' => $provider,
                ))->execute();
          array_push($email_receivers, $provider);
        if ($rpeid != -1) {
        array_push($rpeids_array, $rpeid);
        }
      }
      else {
        continue;
      }
    }
  }

  return $rpeids_array;
}
*/
/**
 * _get_provider_employeeName()
 * @param int $rreid
 * @return string employeeName
**/
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
**/

function submit_peer_update() {
  $result_val = '1';
  $rreid = $_POST['rreid'];
  $providers = explode_employee($_POST['providers']);
  $spstatus = $_POST['spstatus'];
  $userId_flag = $_POST['userId_flag'];
  $email_receivers = array();
/*  if ($spstatus == 0) {
    $rpeid = insert_peer_item_to_DB($rreid, $providers, $sptatus, $userId_flag, $email_receivers);
  }
  else {
    $rpeid = update_peer_item_to_DB($rreid, $providers, $spstatus, $userId_flag, $email_receivers);
  }
*/
  update_review_spstatus_manager($rreid, $userId_flag);
  $rpeids_array = _insert_items_to_DB($rreid, $providers, $email_receivers);
  // dd('after db insert');
  $count = count($rpeids_array);
  if ($count != 0) {
    $msg = $count . ' peer(s) selection updated successfully!';
    drupal_set_message($msg, 'status');
  }
  // else if ($count == 0 && $spstatus != 0) {
  //   $msg = 'You did not update any peers!';
  //   drupal_set_message($msg, 'status');
  // }
  // else {
  //   $msg = 'Update failed!!!';
  //   drupal_set_message($msg, 'error');
  // }

/*  foreach($email_receivers as $email_receiver) {
    if (module_exists('common') && module_enable(array('common'))) {
      $tmp = get_basic_review_information($rreid);
    }
    $review_description = $tmp[0]->description;
    $employeeName = $tmp[0]->employeeName;
    $review_end_date = unixTimestampToDateArray($tmp[0]->period_end);
    $date = date_to_format($review_end_date);
    $url = '#';
    $peer_params = array(
      'employeeName' => $employeeName, 
      'reviewname' => $review_description,
      'datetime' => $date,
      'peersurl' => $url, 
      'type' => $userId_flag,
      'peername' => $email_receiver,
      );
    $temp_array = array($email_receiver);
    $email_address = select_email_from_prs_reviewers($temp_array);
    mail_sender_template_adapter($email_address[0]->mail, $peer_params, 1);
  }
*/  
  send_peer_email_notification($rreid, $email_receivers, $userId_flag);

  echo $rval;
}

function get_peers_status($rreid) {
  $query = db_select('prs_rel_provider_employee', 'prpe')
      ->fields('prpe')
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



function send_peer_email_notification($rreid, $email_receivers, $userId_flag) {
  if (module_exists('common') && module_enable(array('common'))) {
    // call func in common module to get basic info of this review
    $info = get_basic_review_information($rreid);
  } 
  else {
    $msg = 'Email notify module does not exist or enable!';
    drupal_set_message($msg, 'error');
    return;
  }
  if (count($email_receivers) == 0) 
    return;
  foreach ($email_receivers as $email_receiver) {
    $review_description = $info[0]->description;
    $employeeName = $info[0]->employeeName;
    $review_end_date = unixTimestampToDateArray($info[0]->period_end);
    $date = date_to_format($review_end_date);
    $peer_select_url = '#';
    $peer_info_params = array(
      'employeeName' => $employeeName,
      'reviewname' => $review_description,
      'datetime' => $date,
      'peersurl' => $peer_select_url,
      'type' => $userId_flag,
      );
    $email_address = select_email_from_prs_reviewers(array($email_receiver));
    mail_sender_template_adapter($email_address[0]->mail, $peer_info_params, 1);
  }
}

