<?php

/**
 * @file
 * 
 * 
 */

/**
 * page callback URL:newreview/submitreview/%/%
 * @param type $data
 * @param type $employees
 * @return type
 */
function submit_new_review($data, $employees) {
  $rval = '1';
  $general_id = insertToGeneral_DB($data);
//  $general_id = '-1';
  if ($general_id != '-1') {
    if (isset($employees)) {

      // counsellor select
    }
    else {

      //counselee
    }
  }
  else {
    $rval = '-1';
//    drupal_goto('newreview');
  }
  echo $rval;
}

/**
 * Insert info into prs_general Table
 * @param type $general_info this info is from front site ajax
 * @return type general_id
 */
function insertToGeneral_DB($general_info) {
  $general_id = null;
  $fields_array = array();
  if (set_fields_array($general_info, $fields_array)) {
    $general_id = db_insert('prs_general')
            ->fields($fields_array)->execute();
  }
  if (isset($general_id)) {

    $message = 'Review created successful.';
    drupal_set_message($message, 'status');
  }
  else {
    $general_id = '-1';
    $message = 'Insert information Error!';
    drupal_set_message($message, 'error');
  }

  return $general_id;
}

/**
 * set general infomation
 * @param type $general_info format:review_type-review_start_date-review_end_date-review_from_date-review_to_date-Project_Name_Text;
 * @param type $fields_array
 */
function set_fields_array($general_info, &$fields_array) {
  $starttime = NULL;
  $endtime = NULL;
  $periodFtime = NULL;
  $periodTTime = NULL;
  $flag = TRUE;
  if (strpos($general_info, '-')) {
    $tmp = explode('-', $general_info);
  }
  //type
  if (isset($tmp['0'])) {
    $type = check_plain($tmp['0']);
    $fields_array['type'] = $type;
    // project type
    if ($tmp['0'] == '1') {
      $projectName = check_plain($tmp['5']);
      $fields_array['project_name'] = $projectName;
    }
  }

  //start time
  if (isset($tmp['1'])) {
    $stime = explodeTime($tmp['1']);
    $starttime = explodeTimeStand($stime);
    $startTime = dateArrayTounixTimestamp($stime);
    $fields_array['period_start'] = $startTime;
  }
  //end time
  if (isset($tmp['2'])) {
    $etime = explodeTime($tmp['2']);
    $endtime = explodeTimeStand($etime);
    $endTime = dateArrayTounixTimestamp($etime);
    $fields_array['period_end'] = $endTime;
  }
  //period from
  if (isset($tmp['3'])) {
    $pftime = explodeTime($tmp['3']);
    $periodFtime = explodeTimeStand($pftime);
    $periodFrom = dateArrayTounixTimestamp($pftime);
    $fields_array['time_frame_from'] = $periodFrom;
  }

  //period to
  if (isset($tmp['4'])) {
    $pttime = explodeTime($tmp['4']);
    $periodTTime = explodeTimeStand($pttime);
    $periodTo = dateArrayTounixTimestamp($pttime);
    $fields_array['time_frame_to'] = $periodTo;
  }
//  if (isset($form_state['values']['review_status'])) {
//    $status = $form_state['values']['review_status'];
//    $fields_array['active'] = $status;
//  }
  if (isset($starttime) && isset($endtime) && isset($periodFtime) && isset($periodTTime)) {
    if ($starttime >= $endtime) {

      $flag = FALSE;
    }
    if ($periodFtime >= $periodTTime) {

      $flag = FALSE;
    }
  }else{
    
    $flag = FALSE;
    
  }
  return $flag;
}

function explodeTimeStand($date) {
  $ntime = NULL;
  if (isset($date['month']) && isset($date['day']) && isset($date['year'])) {
    $ntime = (int) ($date['year'] . $date['month'] . $date['day']);
  }
  else {
    $ntime = NULL;
  }
  return $ntime;
}

/**
 * explode Time
 * @param type $date mm:dd:yyyy
 * @return type
 */
function explodeTime($date) {
  if (strpos($date, ':')) {
    $tmp = explode(':', $date);
    $ntime = array();
    if (isset($tmp['0'])) {
      $ntime['month'] = $tmp['0'];
    }else{
      $ntime['month']=NULL;
      
    }
    if (isset($tmp['1'])) {
      $ntime['day'] = $tmp['1'];
    }else{
      $ntime['day']=NULL;
      
    }
    if (isset($tmp['2'])) {
      $ntime['year'] = $tmp['2'];
    }else{
      $ntime['year']=NULL;
      
    }
  }
  else {
    $ntime['month'] = NULL;
    $ntime['day'] = NULL;
    $ntime['year'] = NULL;
  }
  return $ntime;
}