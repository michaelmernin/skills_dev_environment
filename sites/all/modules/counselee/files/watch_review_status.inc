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
    // access_authen($rreid);
    $all_employee_array = get_all_employees_from_db();
    $selected_peers_array = get_selected_peers_from_db($rreid);
    $unselected_peers_array = get_unselected_peers_from_db($rreid);
    $userId_flag = get_user_identity($rreid);
    $pageContent = display_peer_selection_page($rreid);

    return $pageContent;
}

function display_firsttime_select($userId_flag) {
    return theme('select_peers', array(
        'all_employee_array' => $all_employee_array,
        'userId flag' => $userId_flag,     // 0 for counselee, 1 for counselor 
        ));
}

function display_select($userId_flag) {
    return theme('select_peers', array(
        'selected_peers_array' => $selected_peers_array,
        'unselected_peers_array' => $unselected_peers_array,
        'userId flag' => $userId_flag,
        ));
}

function display_update($userId_flag) {
    return theme('select_peers', array(
        'selected_peers_array' => $selected_peers_array,
        'unselected_peers_array' => $unselected_peers_array,
        'userId flag' => $userId_flag,
        ));
}

function display_peer_selection_page($rreid, $userId_flag) {
  $spstatus = get_review_spstatus_from_db($rreid);
  // dd($spstatus);
  if ($spstatus == 0) {
    return display_firsttime_select($userId_flag);  // selects for the 1st time, and no selection record yet.
  }
  else if (($spstatus == 1 && $userId_flag == 1) || ($spstatus == 1 && $userId_flag == 0)) {
    // $sptatus == '1' && $userId_flag == '1' ==>  couselee has selected and counselor selects for the 1st time
    // $sptatus == '0' && $userId_flag == '2' ==>  counselor has selected and counselee selects for the 1st time
    return display_select($userId_flag);
  }  
  else {
    // both have selected their first time and from now on selection means updating.
    return display_update($userId_flag);
  }
}

function insert_peer_item_to_DB($rreid, $providers, $spstatus, $userId_flag) {
  if ($userId_flag == -1) {
    drupal_goto('login');
  }
  $rpeid = '';
  $rpeids_array = array();
  $employeeName = _get_provider_employeeName($rreid);
  $prid = _get_provider_prid($rreid);

  foreach($providers as $provider) {
    $rpeid = db_insert('prs_rel_review_provider')
          ->fields(array(
            'employeeName' => $employeeName,
            'prid' => $prid,
            'provider' => $provider,
           /* if ($spstatus == 0) {
              if ($userId_flag == 0) {
                'spstatus' = 1;
              }
              else if ($userId_flag == 1) {
                'spstatus' = 2;
              }
              else {

              }
            }
            else if (($spstatus == 1 && $userId_flag == 1)||( $spstatus == 2 && $userId_flag == 0)) {*/
              'spstatus' => 3,
          /*  }
            else {
              // 'spstatus' shall not be changed, do nothing.
            }
*/
          ))
          ->execute();
    if ($rpeid != -1) {
      array_push($rpeids_array, $rpeid);
    }
  }
  return $rpeids_array;
}

function _get_provider_employeeName($rreid) {
  $query = db_select('prs_rel_review_employee', 'prre')
        ->fields('prre', array('employeeName'))
        ->condition('prre.rreid', $rreid, '=');
  $result = $query->execute()->fetchAll();
  $employeeName = genernate_array($result, 'employeeName');
  return $employeeName;
}

function _get_provider_prid($rreid) {
  $query = db_select('prs_rel_review_employee', 'prre')
        ->fields('prre', array('prid'))
        ->condition('prre.rreid', $rreid, '=');
  $result = $query->execute()->fetchAll();
  $employeeName = genernate_array($result, 'prid');
  return $employeeName;
}

/**
 * page callback function to handle submit peer info creation or update
 * callback params
 * @param type $rreid
 * @param type $providers
**/
function submit_peer_update() {
  $result_val = '-1';
  $email_addr_array = array();
  $rpeid = insert_peer_item_to_DB($rreid, $providers, $sptatus, $userId_flag);
  $count = count($repid);
  if ($count != 0) {
    $msg = $count . 'peer(s) selection updated successfully!';
    drupal_set_message($message, 'status');
  }
  else {
  }
  select_email_from_prs_reviewers($providers);
  // $peer_params = array()
}


