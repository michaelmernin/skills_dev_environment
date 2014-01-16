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
  $cur_name = get_current_user_name();
  $general_id = insert_new_review_into_general_db($_POST, $cur_name);
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
          $review_ids = insert_reviews_to_DB($emp_array, $general_id, $_POST);
          if ($review_ids != '-1') {
//            set_review_status($review_ids, 0);
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
          $review_ids = insert_reviews_to_DB($emp_array, $general_id, $_POST, 1);
          if ($review_ids != '-1') {
//            set_review_status($review_ids, 1);
            // notify method will be called here.
            //==========================================counselee email==================
            foreach ($review_ids as $review) {
              $counselee_address = select_email_from_prs_reviewers(array($review->employeeName));
              $address = $counselee_address[0]->mail;
              $counselee_parems = array('username' => $review->employeeName,
                'reviewname' => return_review_name($_POST),
                'peersurl' => get_curPage_base_url() . 'watchstatus/selectpeers/' . $review->rreid,
                'reviewurl' => get_curPage_base_url() . 'watchstatus/selfassessment/' . $review->rreid,
                'datetime' => $_POST['review_to_date'],
                'type' => 'counselee');
              mail_sender_template_adapter($address, $counselee_parems, 0);
            }
            //==========================================counselor email end==================         
            //======================counselor email added it later===========================
            //===============================================================================
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
      //there is no select counselee
      $rval = '-1';
      $message = 'Review created fail!';
      drupal_set_message($message, 'error');
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
 * insert_new_review_into_general_db
 * @param type $data
 * @return string
 */
function insert_new_review_into_general_db($data, $counselor_name = NULL) {

  if (isset($data)) {
    $general_id = insert_to_general_DB($data, $counselor_name);
  }
  else {
    $general_id = '-1';
  }
  return $general_id;
}

/**
 * callback URL:newreview/submitselfreview
 */
function submit_new_self_review() {
  $rval = '1';
  $cur_counselee_name = get_current_user_name();
  if (module_exists('common') && module_enable(array('common'))) {
    $counselor_name = my_counselor_gm_name($cur_counselee_name);
    $cur_counselor_name = $counselor_name[0]->counselorName;
  }
  if ($cur_counselor_name != '') {
    $general_id = insert_new_review_into_general_db($_POST, $cur_counselor_name);
    //counselee
    if ($general_id != '-1') {
      $emp_array = array(
        $cur_counselee_name
      );
      if ($_POST['self_draft_flag'] == '1') {
        $review_ids = insert_reviews_to_DB($emp_array, $general_id, $_POST, 1);
//          set_review_status($review_ids, 1);
        if (module_exists('mail_sender') && module_enable(array('mail_sender'))) {
          //==========================================counselee email=================
          $my_address = select_email_from_prs_reviewers($emp_array);
          $address = $my_address[0]->mail;
          $counselee_parems = array('username' => $cur_counselee_name,
            'reviewname' => return_review_name($_POST),
            'peersurl' => get_curPage_base_url() . 'watchstatus/selectpeers/' . $review_ids[0]->rreid,
            'reviewurl' => get_curPage_base_url() . 'watchstatus/selfassessment/' . $review_ids[0]->rreid,
            'datetime' => $_POST['review_to_date'], 'type' => 'counselee');
          mail_sender_template_adapter($address, $counselee_parems, 0);
          //==========================================counselee email end==============
          //==========================================counselor email==================
          $my_counselor_address = select_email_from_prs_reviewers(array($cur_counselor_name));
          $counselor_address = $my_counselor_address[0]->mail;

          $counselor_parems = array('username' => $cur_counselee_name, 'reviewname' => return_review_name($_POST), 'peersurl' => get_curPage_base_url() . 'watchstatus/selectpeers/' . $review_ids[0]->rreid, 'counselorname' => $cur_counselor_name, 'datetime' => $_POST['review_to_date'], 'type' => 'counselor');
          mail_sender_template_adapter($counselor_address, $counselor_parems, 0);
          //==========================================counselor email end===============
        }
      }
      else {
        //self review in draft
        $review_ids = insert_reviews_to_DB($emp_array, $general_id, $_POST);
      }
      $message = 'Review created successful.';
      drupal_set_message($message, 'status');
    }
    else {
      $rval = '-1';
      $msg = 'Error:Review creation attempt failed!';
      drupal_set_message($msg, 'error');
    }
  }
  else {
    $rval = '-1';
    $msg = 'Error:Please ask HR for set your counselor first!';
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
      $update_num = set_review_status(array($review_id), 1);
      // send email
      // code
      if (module_exists('common') && module_enable(array('common'))) {
        //counselee email
        $basic_info = get_basic_review_information($review_id);
        $rreid=$basic_info[0]->rreid;
        $datetime_timestamp = unixTimestampToDateArray($basic_info[0]->time_frame_to);
        $my_address = select_email_from_prs_reviewers(array($basic_info[0]->employeeName));
        $address = $my_address[0]->mail;
        $datetime = $datetime_timestamp['month'] . '/' . $datetime_timestamp['day'] . '/' . $datetime_timestamp['year'];
        $reviewurl = get_curPage_base_url() . 'watchstatus/selfassessment/' . $rreid;
        $counselee_parems = array('username' => $basic_info[0]->employeeName,
          'reviewname' => $basic_info[0]->description,
          'peersurl' => get_curPage_base_url() . 'watchstatus/selectpeers/' . $rreid,
          'reviewurl' => $reviewurl,
          'datetime' => $datetime, 'type' => 'counselee');
        mail_sender_template_adapter($address, $counselee_parems, 0);
        //counselor email
        $counselor_name = my_counselor_gm_name($basic_info[0]->employeeName);
        $cur_counselor_name = $counselor_name[0]->counselorName;
        if (count($cur_counselor_name) != '0') {
//            counselor email
          $my_counselor_address = select_email_from_prs_reviewers(array($cur_counselor_name));
          $counselor_address = $my_counselor_address[0]->mail;
          $counselor_parems = array('username' => $basic_info[0]->employeeName, 'reviewname' => $basic_info[0]->description, 'peersurl' => get_curPage_base_url() . 'watchstatus/selectpeers/' . $basic_info[0]->rreid, 'counselorname' => $cur_counselor_name, 'datetime' => $datetime, 'type' => 'counselor');
          mail_sender_template_adapter($counselor_address, $counselor_parems, 0);
        }
      }

      $rval = '1';
    }
    else {
      $rval = '-1';
      $msg = 'Error:Wrong review status!';
    drupal_set_message($msg, 'error');
    }
  }
  else {
    $rval = '-1';
    $msg = 'Error:Please select a review!';
    drupal_set_message($msg, 'error');
  }
  echo $rval;
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
function insert_to_general_DB($general_info,$counselor_name) {
  $general_id = null;
  $fields_array = array();
  if (set_fields_array($general_info, $counselor_name,$fields_array)) {
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
function set_fields_array($general_info,$counselor_name, &$fields_array) {
  $periodFtime = NULL;
  $periodTTime = NULL;
  $flag = TRUE;
  $review_type = NULL;
  //type
  if (isset($general_info['review_type'])) {
    $type = check_plain($general_info['review_type']);
    $fields_array['type'] = $type;
    // project type
    switch ($general_info['review_type']) {
      case '0':
        $review_type = 'ANNUAL_REVIEW';
        break;
      case '1':
        $review_type = 'PROJECT_REVIEW';
        $projectName = check_plain($general_info['Project_Name_Text']);
	$projectClient = check_plain($general_info['project_client']);
        $fields_array['project_name'] = $projectName;
	$fields_array['client'] = $projectClient;
        break;
      case '2':
        $review_type = 'THREE_MONTH_REVIEW';
        break;
    }
  }
  if(isset($counselor_name)){
    $counselor_name_check=  check_plain($counselor_name);
    $fields_array['counselor_name'] = $counselor_name_check;
  }
  //period from
  if (isset($general_info['review_from_date'])) {
    $pftime = explode_time($general_info['review_from_date']);
    $periodFtime = explode_time_stand($pftime);
    $periodFrom = dateArrayTounixTimestamp($pftime);
    $fields_array['time_frame_from'] = check_plain($periodFrom);
  }
  else {
    return FALSE;
  }
  //period to
  if (isset($general_info['review_to_date'])) {
    $pttime = explode_time($general_info['review_to_date']);
    $periodTTime = explode_time_stand($pttime);
    $periodTo = dateArrayTounixTimestamp($pttime);
    $fields_array['time_frame_to'] = check_plain($periodTo);
  }
  else {

    return FALSE;
  }
  $description = return_review_name($general_info);
  $fields_array['description'] = $description;
  if ( isset($periodFtime) && isset($periodTTime)) {
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
  return $employee_array;
}

/**
 * insert_reviews_to_DB prs_rel_review_employee table.
 * @param array $employees, int $prid
 * @return array $review_ids
 */
function insert_reviews_to_DB($employees, $prid, $review_basic_info, $rstatus = 0) {
  $rreid = '';
  $review_ids = array();
  if ($rstatus == 0) {
    foreach ($employees as $employee) {
      $rreid = db_insert('prs_rel_review_employee')
          ->fields(array(
            'employeeName' => $employee,
            'prid' => $prid,
          ))
          ->execute();
      // need create node later ==================================
      $nid_token = create_review_node($employee, $review_basic_info);
      $nid = $nid_token[0];
      $token = $nid_token[1];
      $rpeid = db_insert('prs_rel_provider_employee')
          ->fields(array(
            'employeeName' => $employee,
            'providerName' => $employee,
            'prid' => $prid,
            'rreid' => $rreid,
            'nid' => $nid,
            'token' => $token,
          ))
          ->execute();
      if ($rreid != '-1' && $rpeid != '-1') {
        // $msg = 'Reviewee(s) set successfully.';
        // drupal_set_message($msg, 'status');
        $review_info = new stdClass();
        $review_info->employeeName = $employee;
        $review_info->prid = $prid;
        $review_info->rreid = $rreid;
        $review_info->rpeid = $rpeid;
        $review_info->reviewurl = $token;
        array_push($review_ids, $review_info);
      }
      else {
        // $msg = 'Reviewee(s) set failed!';
        // drupal_set_message($meg, 'error');
        $review_ids = '-1';
        break;
      }
    }
  }
  else if ($rstatus == 1) {
    foreach ($employees as $employee) {
      $rreid = db_insert('prs_rel_review_employee')
          ->fields(array(
            'employeeName' => $employee,
            'prid' => $prid,
            'rstatus' => '1',
          ))
          ->execute();
      // need create node later ==================================
      $nid_token = create_review_node($employee, $review_basic_info);
      $nid = $nid_token[0];
      $token = $nid_token[1];
      $rpeid = db_insert('prs_rel_provider_employee')
          ->fields(array(
            'employeeName' => $employee,
            'providerName' => $employee,
            'prid' => $prid,
            'rreid' => $rreid,
            'nid' => $nid,
            'token' => $token,
          ))
          ->execute();
      if ($rreid != '-1' && $rpeid != '-1') {
        $review_info = new stdClass();
        $review_info->employeeName = $employee;
        $review_info->prid = $prid;
        $review_info->rreid = $rreid;
        $review_info->rpeid = $rpeid;
        $review_info->reviewurl = $token;
        array_push($review_ids, $review_info);
      }
      else {
        // $msg = 'Reviewee(s) set failed!';
        // drupal_set_message($meg, 'error');
        $review_ids = '-1';
        break;
      }
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

/**
 * return_review_name
 * @param type $date
 * @return type
 */
function return_review_name($date) {
  $review_type = NULL;
  switch ($date['review_type']) {
    case '0':
      $review_type = 'ANNUAL_REVIEW';
      $review_name = substr($date['review_to_date'], 6, 9) . '_' . $review_type;
      break;
    case '1' or 1:
      $review_type = 'PROJECT_REVIEW';
      $review_name = $date['Project_Name_Text'] . '_' . $review_type;
      break;
    case '2':
      $review_type = 'THREE_MONTH_REVIEW';
      break;
  }
  return $review_name;
}

/**
 * create webform node when create a new review
 * @param type $employee
 * @param type $review_basic_info
 * @return type
 */
function create_review_node($employee, $review_basic_info) {
//  dd($reviewtype,'$reviewtype');
  switch ($review_basic_info['review_type']) {
    //0 for annual review;
    case 0:
      $nid_token = create_annual_review_node($employee, $review_basic_info);
      break;
    // 1 for project review;
    case 1:
      $nid_token = create_project_review_node($employee, $review_basic_info);
      break;
    // 2 for 3-month review
    case 2:
      break;
  }
  return $nid_token;
}

/**
 * create a annual review node
 * @param type $employee
 * @param type $review_basic_info
 */
function create_annual_review_node($employee, $review_basic_info) {
  $employee_name = format_login_name($employee);
  $start_year = substr($review_basic_info['review_from_date'], 6, 9);
  $end_year = substr($review_basic_info['review_to_date'], 6, 9);
//  if ($start_year === $end_year) {
//    $title = $employee_name . ' ' . $end_year . ' Annual Performace Review.';
//  }
//  else {
//    $title = $employee_name . ' ' . $start_year . '-' . $end_year . ' Annual Performace Review.';
//  }
//  dd($title,'$title');
  $title = $employee_name . ' ' . $end_year . ' Annual Performace Review.';
  $nid_token = create_node($title, ANNUAL_TEMP, 1);
  return $nid_token;
}

function create_project_review_node($employee, $review_basic_info) {
  $employee_name = format_login_name($employee);
  $project_name = $review_basic_info['Project_Name_Text'];
  $title = $employee_name . ' Project Review About ' . $project_name;
  $nid_token = create_node($title, PROJECT_TEMP, 1);
  return $nid_token;
}

