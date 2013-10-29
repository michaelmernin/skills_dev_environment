<?php

/**
 * @file
 * Page callback file for the counselee module.
 */
function watch_basic_information($review_id) {
  $pageContent = '';
  return $pageContent;
}

function display_select_peers_page($rreid = NULL) {
    access_authen($rreid);
    $all_employee_array = get_all_employees_from_db();
    $selected_peers_array = get_selected_peers_from_db($rreid);
    $unselected_peers_array = get_unselected_peers_from_db($rreid);
    $select_peers_status = get_peers_status($rreid);
    $userId_flag = get_user_identity($rreid);
    $pageContent = display_peer_selection_page($rreid, $userId_flag, $all_employee_array, $selected_peers_array, $unselected_peers_array, $select_peers_status);

  return $pageContent;
}


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
/*       
*/        $result = db_update('prs_rel_review_employee');
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
          array_push($email_receivers, $provider);
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
        array_push($email_receivers, $provider);*/
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
        if ($rpeid != -1) {
          array_push($rpeids_array, $rpeid);
        }
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
  $providers = array();
  $providers = explode_employee($_POST['providers']);
  $spstatus = $_POST['spstatus'];
  $userId_flag = $_POST['userId_flag'];
  $email_receivers = array();
  $email_addr_array = array();
  if ($spstatus == 0) {
    $rpeid = insert_peer_item_to_DB($rreid, $providers, $sptatus, $userId_flag, $email_receivers);
  }
  else {
    $rpeid = update_peer_item_to_DB($rreid, $providers, $spstatus, $userId_flag, $email_receivers);
  }
  dd($rpeid);
  $count = count($rpeid);
  if ($count != 0) {
    $msg = $count . ' peer(s) selection updated successfully!';
    drupal_set_message($msg, 'status');
  }
  else {
    $msg = 'Update failed!!!';
    drupal_set_message($msg, 'error');
  }

  foreach($email_receivers as $email_receiver) {
    if (module_exists('common') && module_enable(array('common'))) {
      $tmp = get_basic_review_infomation($rreid);
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
  echo $rval;
}

function get_peers_status($rreid) {
  $query = db_select('prs_rel_provider_employee', 'prpe')
      ->fields('prpe')
      ->condition('prpe.rreid', $rreid);
  $result = $query->execute()->fetchAll();
//  dd('$result');
//  dd($result);
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

