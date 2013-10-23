<?php

/**
 * @file
 * 
 * 
 */

/**
 * callback URL:newreview/submitreview/%/%
 * 
 * @param type $employees
 * @param type $radio_val
 */
function submit_new_review($employees = NULL, $radio_val = NULL) {
  $rval = '1';
  if (isset($_POST)) {
    $general_id = insert_to_general_DB($_POST);
  }
  else {
    $general_id = '-1';
  }
  $review_ids;
//  $general_id = '-1';
  if ($general_id != '-1') {
    if (count($employees) != '0') {
      //counselor
      if (is_numeric($radio_val)) {
        //$radio_val is numeric
        $emp_array = array();
        $emp_array = explode_employee($employees);
        if ($radio_val == '0' && $emp_array['0'] != 'false') {
          //$radio_val == 0
          $review_ids = insert_reviews_to_DB($emp_array, $general_id);
          if ($review_ids != '-1') {
            set_review_status($review_ids, 0);
            //notify method will be called here.


            $message = 'Review created successful.';
            drupal_set_message($message, 'status');
          }
          else {
            $rval = '-1';
            $message = 'Review created fail!';
            drupal_set_message($message, 'error');
          }
        }
        else if ($radio_val == '1') {
          //$radio_val == 1
          $review_ids = insert_reviews_to_DB($emp_array, $general_id);
          if ($review_ids != '-1') {
            set_review_status($review_ids, 1);
            // notify method will be called here.


            $message = 'Review created successful.';
            drupal_set_message($message, 'status');
          }
          else {
            $rval = '-1';
            $message = 'Review created fail!';
            drupal_set_message($message, 'error');
          }
        }
        else {
          //$radio_val error number
          $rval = '-1';
          $message = 'Review created fail!';
          drupal_set_message($message, 'error');
        }
      }
      else {
        //$radio_val is not numeric
        $rval = '-1';
        $message = 'Review created fail!';
        drupal_set_message($message, 'error');
      }
    }
    else {
      //counselee
      $cur_counselee_name = get_current_user_name();
      $emp_array = array(
        $cur_counselee_name
      );
      $review_ids = insert_reviews_to_DB($emp_array, $general_id);
      if ($review_ids != '-1') {
        if ($_POST['flag'] == '1') {
          set_review_status($review_ids, 1);
          if (module_exists('mail_sender') && module_enable(array('mail_sender'))) {
//            $my_address=$cur_counselee_name.'@perficient.com';
//            $address = 'mars.shen@perficient.com';
//            $parems = array('username' => $cur_counselee_name);
//            dd('=================');
//            mail_sender_template_adapter($address, $parems, 0);
//            dd('-----------------');
          }
        }
        // notify method will be called here.

        $message = 'Review created successful.';
        drupal_set_message($message, 'status');
      }
      else {
        $rval = '-1';
        $message = 'Review created fail!';
        drupal_set_message($message, 'error');
      }
    }
  }
  else {
    $rval = '-1';
    $msg = 'Review creation attempt failed!';
    drupal_set_message($msg, 'error');
  }
  echo $rval;
}

/**
 * callback URL:newreview/startreview
 * @param type $review_id
 * @author Mars
 */
function start_new_review() {
  $rval = '-1';
  $review_id = isset($_POST['review_id']) ? $_POST['review_id'] : 0;
  if ($review_id != 0) {
    $review = query_review($review_id);
    if ($review[0]->rstatus == '0') {
      $update_num = start_review_update_change_status_0_to_1($review_id);
      // send email
      // code

      $rval = '1';
    }
    else {
      $rval = '-1';
    }
  }
  else {
    $rval = '-1';
  }
  echo $rval;
}

/**
 * start_review_update_change_status_0_to_1
 * @param type $review_id
 * @author Mars
 */
function start_review_update_change_status_0_to_1($review_id) {
  $query = db_update('prs_rel_review_employee');
  $query->fields(array(
        'rstatus' => 1,
      ))
      ->condition('rreid', $review_id)
      ->execute();
}

/**
 * query a review by review id
 * @param type $review_id
 * @return type
 * @author Mars
 */
function query_review($review_id) {
  $query = db_select('prs_rel_review_employee', 're');
  $query->fields('re', array('employeeName', 'rreid', 'rstatus'));
  $query->condition('re.rreid', $review_id);
  $result = $query->execute()->fetchAll();
  return $result;
}

/**
 * Insert info into prs_general Table
 * @param type $general_info this info is from front site ajax
 * @return type general_id
 * @author Mars
 */
function insert_to_general_DB($general_info) {
  $general_id = null;
  $fields_array = array();
  if (set_fields_array($general_info, $fields_array)) {
    $general_id = db_insert('prs_general')
            ->fields($fields_array)->execute();
  }
  if (isset($general_id)) {
    
  }
  else {
    $general_id = '-1';
  }

  return $general_id;
}

/**
 * set general infomation
 * @param type $general_info  $_post
 * @param type $fields_array
 * @author Mars
 */
function set_fields_array($general_info, &$fields_array) {
  $starttime = NULL;
  $endtime = NULL;
  $periodFtime = NULL;
  $periodTTime = NULL;
  $flag = TRUE;
//  if (strpos($general_info, '-')) {
//    $tmp = explode('-', $general_info);
//  }
  //type
  if (isset($general_info['review_type'])) {
    $type = check_plain($general_info['review_type']);
    $fields_array['type'] = $type;
    // project type
    if ($general_info['review_type'] == '1') {
      $projectName = check_plain($general_info['Project_Name_Text']);
      $fields_array['project_name'] = $projectName;
    }
  }

  //start time
  if (isset($general_info['review_start_date'])) {
    $stime = explode_time($general_info['review_start_date']);
    $starttime = explode_time_stand($stime);
    $startTime = dateArrayTounixTimestamp($stime);
    $fields_array['period_start'] = check_plain($startTime);
  }
  //end time
  if (isset($general_info['review_end_date'])) {
    $etime = explode_time($general_info['review_end_date']);
    $endtime = explode_time_stand($etime);
    $endTime = dateArrayTounixTimestamp($etime);
    $fields_array['period_end'] = check_plain($endTime);
  }
  //period from
  if (isset($general_info['review_from_date'])) {
    $pftime = explode_time($general_info['review_from_date']);
    $periodFtime = explode_time_stand($pftime);
    $periodFrom = dateArrayTounixTimestamp($pftime);
    $fields_array['time_frame_from'] = check_plain($periodFrom);
  }

  //period to
  if (isset($general_info['review_to_date'])) {
    $pttime = explode_time($general_info['review_to_date']);
    $periodTTime = explode_time_stand($pttime);
    $periodTo = dateArrayTounixTimestamp($pttime);
    $fields_array['time_frame_to'] = check_plain($periodTo);
  }
//  if (isset($form_state['values']['review_status'])) {
//    $status = $form_state['values']['review_status'];
//    $fields_array['active'] = $status;
//  }
  //description
  if (isset($general_info['review_from_description'])) {
    $description = check_plain($general_info['review_from_description']);
    $fields_array['description'] = $description;
  }
  if (isset($starttime) && isset($endtime) && isset($periodFtime) && isset($periodTTime)) {
    if ($starttime >= $endtime) {

      $flag = FALSE;
    }
    if ($periodFtime >= $periodTTime) {

      $flag = FALSE;
    }
  }
  else {

    $flag = FALSE;
  }
  return $flag;
}

/**
 * explode to stand time
 * @param type $date
 * @return null
 * @author Mars
 */
function explode_time_stand($date) {
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
 * @author Mars
 */
function explode_time($date) {
  if (strpos($date, '/')) {
    $tmp = explode('/', $date);
    $ntime = array();
    if (isset($tmp['0'])) {
      $ntime['month'] = $tmp['0'];
    }
    else {
      $ntime['month'] = NULL;
    }
    if (isset($tmp['1'])) {
      $ntime['day'] = $tmp['1'];
    }
    else {
      $ntime['day'] = NULL;
    }
    if (isset($tmp['2'])) {
      $ntime['year'] = $tmp['2'];
    }
    else {
      $ntime['year'] = NULL;
    }
  }
  else {
    $ntime['month'] = NULL;
    $ntime['day'] = NULL;
    $ntime['year'] = NULL;
  }
  return $ntime;
}

/**
 * explode employees
 * @param type $employees string 
 * @return $employee_array
 */
function explode_employee($employees) {
  $employee_array = array();
  $employee_array = explode('-', $employees);
  // dd($employee_array);
  return $employee_array;
}

/**
 * insert_reviews_to_DB prs_rel_review_employee table.
 * @param array $employees, int $prid
 * @return array $review_ids
 */
function insert_reviews_to_DB($employees, $prid) {
  $rreid = '';
  $review_ids = array();
  foreach ($employees as $employee) {
    $rreid = db_insert('prs_rel_review_employee')
        ->fields(array(
          'employeeName' => $employee,
          'prid' => $prid,
        ))
        ->execute();
    if ($rreid != '-1') {
      // $msg = 'Reviewee(s) set successfully.';
      // drupal_set_message($msg, 'status');
      array_push($review_ids, $rreid);
    }
    else {
      // $msg = 'Reviewee(s) set failed!';
      // drupal_set_message($meg, 'error');
      $review_ids = '-1';
      break;
    }
  }
  return $review_ids;
}

/**
 * set-review_status
 * @ param  array $rreid_array, int $status
 * @ return NULL
 */
function set_review_status($rreid_array, $status) {
  $result = db_update('prs_rel_review_employee');
  $result->fields(array('rstatus' => $status,))
      ->condition('rreid', $rreid_array, 'IN')
      ->execute();
}