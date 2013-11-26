<?php

/**
 * @file
 * Page callback function for view assessment.
 * */
function load_self_assessment($rreid = NULL) {
  access_authen($rreid);
  $reviewee = get_reviewee_from_db($rreid);
  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);
////////////////////////////////////////////////////////
// Item below is for self assessment
////////////////////////////////////////////////////////

  $self_dataset = get_latest_submitted_data_from_db($nid, $sid);
  $cid_indexed_dataset = _indexed_by_cid($self_dataset);
  $item_count = count($self_dataset);
// dd($cid_indexed_dataset, 'cid_indexed_dataset');
  $self_cid_formkey_type_array = _generate_self_cid_formkey_type($self_dataset);

  // dd($self_cid_formkey_type_array, 'self_cid_formkey_type_array');
  $self_adj_list = dfs_generate_adjacent_list($self_cid_formkey_type_array);
  $self_sequential_list = dfs_travel_all_node($self_adj_list);
  $self_mapping_table = adjust_question_and_answer($self_cid_formkey_type_array, $self_sequential_list);
  // dd($self_adj_list, 'self_adj_list');
  // dd($self_mapping_table, 'self_mapping_table');
  // dd($self_sequential_list, 'self_sequential_list');
  $none_selection_item = _fetch_all_none_selection_items($self_cid_formkey_type_array);
  // dd($none_selection_item, 'none_selection_item');
  $review_info = new stdClass();
  $self_comments_array = array();
  _matching_none_selection_items($none_selection_item, $self_cid_formkey_type_array, $cid_indexed_dataset, $review_info, $self_comments_array);

  // $self_dataset = generate_selfassessment_result_array($self_dataset);
  // $rating_qa_sheet = _generate_rating_qa_sheet($self_dataset);
  $self_question_rating_comment_result = synthesize_self_review_ratings_and_comments($cid_indexed_dataset, $self_mapping_table);
  // dd($self_question_rating_comment_result, 'self_question_rating_comment_result');
  $self_question_rating_comment_result = _resort_item_by_weight($self_question_rating_comment_result);
  $self_item_count = count($self_question_rating_comment_result);
// dd($self_question_rating_comment_result, 'self_question_rating_comment_result');
////////////////////////////////////////////////////////
// Item below is for peers
////////////////////////////////////////////////////////
  $cid_formkey_type_array = array();
  $adj_list = array();
  $have_peer_data = TRUE;
  $have_counselor_data = TRUE;
  $providerInfo_table = get_all_providers_info_from_db($rreid);
//  dd($providerInfo_table,'$providerInfo_table');
  if (count($providerInfo_table)) {
    //selected peers
    $nid_pname_sid_array = _fetch_provider_nodeID_and_submissionID($providerInfo_table);
//    dd($nid_pname_sid_array,'$nid_pname_sid_array');
    $pr_data_array = _get_provider_review_content($nid_pname_sid_array);
    $mapping_table = reorder_form_contents($pr_data_array, $cid_formkey_type_array, $adj_list);
//  dd($mapping_table,'$mapping_table');
// dd($cid_formkey_type_array, 'cid_formkey_type_array');
// dd($adj_list, 'adj_list');
    $peer_question_rating_comment_result = synthesize_all_peer_review_ratings_and_comments($nid_pname_sid_array, $pr_data_array, $mapping_table, $item_count);
    dd($peer_question_rating_comment_result, 'peer_question_rating_comment_result');
  }
  else {
    // no peer
    $have_peer_data = FALSE;
  }
  ///////////////////////////////////////////////////////
  //Item below is for counselor
  ///////////////////////////////////////////////////////
  $csiid = get_counselor_latest_submissionID($nid);
//    dd($csiid,'$csiid');
  if ($csiid != -1) {
    $counselor_submit_data = get_synthesized_provider_comment_from_db($nid, $csiid);
    $revised_comments = _fetch_counselor_revised_comments($counselor_submit_data);
  }
  else {
    $have_counselor_data = FALSE;
  }
////////////////////////////////////////////////////////
// Just for display
// dd($peer_question_rating_comment_result, 'q_r_c result');
// dd($self_question_rating_comment_result, 'self_question_rating_comment_result');

  $pageContent = '';
  $total_item_count = $self_item_count + count($self_comments_array);
  foreach ($self_question_rating_comment_result as $key => $value) {
//=====================the question which have pie chart ============CONST===============================
    $pie_form_key_array = array('technical_abilities', 'consulting_skills', 'professionalism', 'leadership', 'teamwork');
    $unread_comment = array();
    if (in_array($value->form_key, $pie_form_key_array)) {
      $form_key = $value->form_key . '_category';
      if ($have_peer_data) {
//        dd($have_peer_data, '$have_peer_data');
//      dd($value, '$value');
//      dd($cid_formkey_type_array, '$cid_formkey_type_array');
        $fieldset_cid = get_fieldset_key_in_peer_table($form_key, $cid_formkey_type_array);
        $children_array = get_children_array_by_cid($fieldset_cid, $adj_list);
//      dd($value->form_key, '$value->form_key');
//      dd($children_array, '$children_array');
//      dd($peer_question_rating_comment_result,'$peer_question_rating_comment_result');

        $peer_name = array();
        $comment_name = array();
        _manage_data_return_three_array($children_array, $peer_question_rating_comment_result, $peer_name, $comment_name, $unread_comment);
        // dd($unread_comment, 'unread_comment');
        $pie_data = _manage_pie_chart($peer_name, $comment_name, $form_key);
      }
      else {
        $pie_data = new stdClass();
        $pie_data->id = $form_key;
        $pie_data->all_avg = 'N/A';
        $pie_data->have_peer_data = FALSE;
      }
//=====================pie chart end=====================================================================
//      dd($unread_comment,'$unread_comment');
//      dd($revised_comments,'$revised_comments');
      if ($have_counselor_data) {
        generate_synthesized_peer_comment($value->form_key, $unread_comment, $revised_comments);
      }
//      dd($peer_name,'$peer_name');
//      dd($comment_name,'$comment_name');
      $content = theme('view_assessment', array(
        'item_num' => $key,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_dataset' => $value,
        'pie_data' => $pie_data,
        'unread_comment' => $unread_comment,
        'total_item_count' => $total_item_count,
        'review_info' => $review_info,
      ));
      $pageContent .= $content;
    }
    else {
      $content = theme('view_assessment_without_chart', array(
        'item_num' => $key,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_dataset' => $value,
        'total_item_count' => $total_item_count,
        'review_info' => $review_info,
      ));
      $pageContent .= $content;
    }
  }
  foreach ($self_comments_array as $key => $value) {
    $content = theme('view_assessment_comment_only', 
      array(
        'item_num' => $self_item_count + $key,
        'total_item_count' => $total_item_count,
        'review_info' => $review_info,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_comment' => $value,
        ));
    $pageContent .= $content;
  }
  // generate comment only content here.
  return $pageContent;
}

function _manage_pie_chart($peer_name, $comment_name, $form_key) {
  $count0_1 = 0;
  $count1_1_2 = 0;
  $count2_1_3 = 0;
  $count3_1_4 = 0;
  $count4_1_5 = 0;
  $allcount = 0;
  $all_avg = 0;

  $tooltiphtml0_1 = '';
  $tooltiphtml1_1_2 = '';
  $tooltiphtml2_1_3 = '';
  $tooltiphtml3_1_4 = '';
  $tooltiphtml4_1_5 = '';
  foreach ($peer_name as $key => $one_peer) {
    if ($one_peer >= 0 && $one_peer <= 1) {
      $tooltiphtml0_1.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $count0_1++;
    }
    elseif ($one_peer > 1 && $one_peer <= 2) {
      $tooltiphtml1_1_2.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $count1_1_2++;
    }
    elseif ($one_peer > 2 && $one_peer <= 3) {
      $tooltiphtml2_1_3.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $count2_1_3++;
    }
    elseif ($one_peer > 3 && $one_peer <= 4) {
      $tooltiphtml3_1_4.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $count3_1_4++;
    }
    elseif ($one_peer > 4 && $one_peer <= 5) {
      $tooltiphtml4_1_5.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $count4_1_5++;
    }
    $all_avg = $all_avg + $one_peer;
    $allcount++;
  }
  $texth0_1 = '<input type="hidden" id="0-1_' . $form_key . '" value="' . $tooltiphtml0_1 . '" />';
  $texth1_1_2 = '<input type="hidden" id="1-2_' . $form_key . '" value="' . $tooltiphtml1_1_2 . '" />';
  $texth2_1_3 = '<input type="hidden" id="2-3_' . $form_key . '" value="' . $tooltiphtml2_1_3 . '" />';
  $texth3_1_4 = '<input type="hidden" id="3-4_' . $form_key . '" value="' . $tooltiphtml3_1_4 . '" />';
  $texth4_1_5 = '<input type="hidden" id="4-5_' . $form_key . '" value="' . $tooltiphtml4_1_5 . '" />';
  $pie_data = new stdClass();
  if ($allcount != 0) {
    $pie_data->avg = array(
      'avg0_1' => $count0_1 / $allcount,
      'avg1_1_2' => $count1_1_2 / $allcount,
      'avg2_1_3' => $count2_1_3 / $allcount,
      'avg3_1_4' => $count3_1_4 / $allcount,
      'avg4_1_5' => $count4_1_5 / $allcount,
    );
    $pie_data->hiddenvalue = $texth0_1 . $texth1_1_2 . $texth2_1_3 . $texth3_1_4 . $texth4_1_5;
    $pie_data->id = $form_key;
    $pie_data->have_peer_data = TRUE;
    $pie_data->all_avg = $all_avg / $allcount;
  }
  else {
    $pie_data->all_avg = 'N/A';
    $pie_data->id = $form_key;
    $pie_data->have_peer_data = FALSE;
  }

  return $pie_data;
}

/**
 * find answer which in children array
 * @param type $children_array children array
 * @param type $peer_question_rating_comment_result answer array
 * @param type $peer_name empty array return avg rating for each peer
 * @param type $comment_name return all comment for each peer
 * @param type $unread_comment return unread comment for each peer
 */
function _manage_data_return_three_array($children_array, $peer_question_rating_comment_result, &$peer_name, &$comment_name, &$unread_comment) {

  $is_first = TRUE;
  foreach ($children_array as $one_child) {
    foreach ($peer_question_rating_comment_result as $one_question_answer) {
      if ($one_child == $one_question_answer->cid) {
        if ($is_first) {
          foreach ($one_question_answer->ratings as $key => $one_ratings) {
            ///want to test again/////////////////TEST/////////////////////////////
            if ($one_ratings != 0) {

              $peer_name_temp[$key]['rating'] = $one_ratings;
              $peer_name_temp[$key]['count'] = 1;
            }
            else {
              $peer_name_temp[$key]['rating'] = 0;
              $peer_name_temp[$key]['count'] = 0;
            }
            ///want to test again/////////////////TEST/////////////////////////////
//            dd($one_ratings,'$peer_name[$key]_first');
//            dd($one_question_answer, '$one_question_answer');
            $comment_name[$key] = $one_question_answer->comments[$key]->comment . '; <br/>';
            if ($one_question_answer->comments[$key]->isRead == 0) {
              $unread_comment[$key] = $one_question_answer->comments[$key]->comment . '; ';
            }
          }
          $is_first = FALSE;
//          dd($one_question_answer);
        }
        else {
          // dd($one_question_answer, 'one_question_answer');
          foreach ($one_question_answer->ratings as $key => $one_ratings) {
            ///want to test again/////////////////TEST/////////////////////////////
            if ($one_ratings != 0) {

              $peer_name_temp[$key]['rating'] = $peer_name_temp[$key]['rating'] + $one_ratings;
              $peer_name_temp[$key]['count'] = $peer_name_temp[$key]['count'] + 1;
            }
            ///want to test again/////////////////TEST/////////////////////////////
            if (!array_key_exists($key, $peer_name)) {
              $peer_name[$key] = '';  
            }
            $peer_name[$key] = $peer_name[$key] + $one_ratings;
           // dd($one_ratings,'$peer_name[$key]');
//            dd($one_question_answer,'dd($one_question_answer);');
            $comment_name[$key] .=$one_question_answer->comments[$key]->comment . '; <br/>';
            if ($one_question_answer->comments[$key]->isRead == 0) {
              $unread_comment[$key].=$one_question_answer->comments[$key]->comment . '; ';
            }
          }
        }
        break;
      }
    }
  }
//  dd($peer_name_temp, '$peer_name_f');
  foreach ($peer_name_temp as $key => $peer_avg) {
    $peer_name[$key] = $peer_avg['rating'] / $peer_avg['count'];
  }
//  dd($peer_name, '$peer_name');
//  dd($comment_name, '$comment_name');
//  dd($count, '$count');
}

function get_fieldset_key_in_peer_table($form_key, $cid_formkey_type_array) {
//  dd($form_key, '$form_key');
//  dd($cid_formkey_type_array, '$cid_formkey_type_array');
  foreach ($cid_formkey_type_array as $key => $one_cid) {
//    dd($key, '$key');
//    dd($one_cid['form_key'], '$one_cid->form_key');
    if ($one_cid['form_key'] == $form_key) {
      $fieldset_cid = $key;
      break;
    }
  }
//  dd($fieldset_cid, '$fieldset_cid');
  return $fieldset_cid;
}

function get_children_array_by_cid($fieldset_cid, $adj_list) {
  return $adj_list[$fieldset_cid];
}

function generate_synthesized_peer_comment($form_key, &$unread_comment, $revised_comments) {
  foreach ($revised_comments as $comment) {
    if ($comment->form_key == $form_key) {
      if (!array_key_exists('revised', $unread_comment)) {
        $unread_comment['revised'] = $comment->peer_comments;
      }
    }
  }
}

function _fetch_counselor_revised_comments($counselor_submit_data) {
  $revised_comments = array();
  foreach ($counselor_submit_data as $key => $value) {
    $comment_item = new stdClass();
    $comment_item->count = $value->count;
    $comment_item->form_key = $value->title;
    $comment_item->rating = $value->rating;
    $comment_item->peer_avg_rating = $value->peer_avg_rating;
    $comment_item->clor_comment = $value->counselor_comment;
    $comment_item->peer_comments = $value->peer_comments;

    array_push($revised_comments, $comment_item);
  }
  return $revised_comments;
}

/**
 * Page callback function of submit counselor's rating and comments.
 * */
function submit_counselor_assessment() {
  $result_val = '1';
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];

  $decoded_items = array();
  foreach ($items as $item) {
    $temp = json_decode($item);
    if ($temp->rating == 'undefined') {
      $temp->rating = -1;
    }

    if ($temp->peerAvgRating == 'undefined') {
      $temp->peerAvgRating = -1;
    }
    if ($temp->revisedComment == 'undefined') {
      $temp->revisedComment = 'No need';
    }
    array_push($decoded_items, $temp);
  }

  $result = _submit_counselor_approve_info_to_DB($rreid, $nid, $decoded_items);
  // send_counselor_reviewed_notification($nid, $reviewee);
  echo $result_val;
}

/**
 * reject_counselor_assessment
 * @file
 * Page callback function for reject counselee assessment
 * */
function reject_counselee_assessment() {
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $reject_comments = $_POST['rejectComments'];

  $decoded_items = array();
  foreach ($items as $item) {
    $temp = json_decode($item);

    if ($temp->rating == 'undefined') {
      $temp->rating = -1;
    }

    if ($temp->peerAvgRating == 'undefined') {
      $temp->peerAvgRating = -1;
    }
    if ($temp->revisedComment == 'undefined') {
      $temp->revisedComment = 'No need';
    }
    array_push($decoded_items, $temp);

  }

  $result = _append_counselor_reject_info_to_DB($rreid, $nid, $decoded_items, $reject_comments);
// email notification
  echo $result;
}

function display_questions_ratings_and_comments($peer_question_rating_comment_result) {
  foreach ($peer_question_rating_comment_result as $key => $value) {
    $content = theme('view_assessment_item', array(
      'item_num' => $key + 1,
      'item_question' => $value->question['title'],
      'item_rating' => $item_rating,
      'item_peer_rating' => $value->ratings,
      'item_comment' => $comments,
      'item_peer_comments' => $value->comments
    ));
  }
}

function _append_content($src, $tail) {
  return $src . $tail;
}

function display_rating_qa_sheet($qa_sheet) {
  $content = '';
  for ($i = 0; $i < count($qa_sheet); $i++) {
    $temp_content = theme(
        'view_assessment_item', array(
      'item_num' => $i,
      'item_question' => $item[$i]['question'],
      'item_rating' => $item[$i]['rating'],
      'item_comment' => $item[$i]['comment'],
      'item_peer_comments' => $item[$i]['peer_comments'],
    ));
    $content .= $temp_content;
  }
  return $content;
}

/**
 * _fetch_provider_nodeID_and_submissionID
 * fetch provider nid and sid from the data retrieved from database.
 * @param array $data_src
 * @return array $nid_pName_sid_array
 * */
function _fetch_provider_nodeID_and_submissionID($data_src) {
  $nid_pName_sid_array = array();
  foreach ($data_src as $item) {
    $providerName = $item->providerName;
    $nid = $item->nid;
    $isRead = _get_provider_isRead_status_from_db($nid);
    $sid = get_last_sid_by_nid($nid);
    $temp = array(
      'providerName' => $providerName,
      'nid' => $nid,
      'sid' => $sid,
      'isRead' => $isRead,
    );
    array_push($nid_pName_sid_array, $temp);
  }
  return $nid_pName_sid_array;
}

/**
 * _get_provider_review_content
 * get all the review contents whose provider node id is in $nid_sid_array
 * @param array $nid_sid_array
 * @return array $provider_submission_data_set
 * */
function _get_provider_review_content($nid_sid_array) {
  $provider_submission_data_set = array();
  foreach ($nid_sid_array as $nid_sid_pair) {
    $objInstance = new stdClass();
    $nid = $nid_sid_pair['nid'];
    $sid = $nid_sid_pair['sid'];
    $providerName = $nid_sid_pair['providerName'];
    $dataset = get_latest_submitted_data_from_db($nid, $sid);
    $objInstance->nid = $nid;
    $objInstance->sid = $sid;
    $objInstance->providerName = $providerName;
    $objInstance->dataset = $dataset;
    $objInstance->isRead = $nid_sid_pair['isRead'];
    $provider_submission_data_set[$providerName] = $objInstance;
  }
  return $provider_submission_data_set;
}

/**
 * get_counselor_latest_submissionID
 * @param int $nid
 * @return int $csiid
 * */
function get_counselor_latest_submissionID($nid) {
  $query = db_select('prs_counselor_submit_info', 'pcsi')
          ->fields('pcsi', array('csiid'))
          ->condition('pcsi.nid', $nid)
          ->orderBy('pcsi.submitTimestamp', 'DESC')
          ->execute()->fetchAll();
  if (count($query)) {
    return $query['0']->csiid;
  }
  else {
    return -1;
  }
}

/**
 * get_synthesized_provider_comment_from_db
 * @param int $nid
 * @param int $csiid
 * @return array
 * */
function get_synthesized_provider_comment_from_db($nid, $csiid) {
  $query = db_select('prs_counselor_submit_data', 'pcsd')
          ->fields('pcsd')
          ->condition('pcsd.nid', $nid)
          ->condition('pcsd.csiid', $csiid)
          ->orderBy('pcsd.count')
          ->execute()->fetchAll();
  return $query;
}

function _indexed_by_cid($self_dataset) {
  $index_array = array();
  foreach ($self_dataset as $key => $value) {
    $cid = $value->cid;
    if (!array_key_exists($cid, $index_array)) {
      $index_array[$cid] = '';
    }
    $index_array[$cid] = $value;
  }
  return $index_array;
}

function _resort_item_by_weight(&$self_question_rating_comment_result) {
  $temp_array = array();
  foreach ($self_question_rating_comment_result as $key => $value) {
    $val = $value->weight;
    $temp_array[$key] = $val;
  }
  asort($temp_array);
  $result = array();
  foreach ($temp_array as $key => $value) {
    array_push($result, $self_question_rating_comment_result[$key]);
  }
  return $result;
}

function _fetch_all_none_selection_items($self_cid_formkey_type_array) {
  $survey_item_question = array();
  $survey_item_count = 0;
  foreach ($self_cid_formkey_type_array as $key => $value) {
    if ($value['pid'] == 0 && $value['type'] == 'fieldset') {
      if (!array_key_exists($key, $survey_item_question)) {
        $survey_item_question[$key] = array();
        $survey_item_question[$key] = $value;
      }
    }
  }
  return $survey_item_question;
}

function _matching_none_selection_items($none_selection_item, $self_cid_formkey_type_array, $cid_indexed_dataset, &$review_info, &$self_comments_array) {
  foreach ($self_cid_formkey_type_array as $key => $value) {
    if ($value['form_key'] == 'project_roles_and_responsibilities' && $value['type'] == 'textarea') {
      $review_info->project_roles_and_responsibilities = $cid_indexed_dataset[$key]->DATA;
    }
    else if ($value['form_key'] == 'clientdate' && $value['type'] == 'textfield') {
      $review_info->clientdate = $cid_indexed_dataset[$key]->DATA;
    }
    foreach ($none_selection_item as $item) {
      if ($value['form_key'] == 'project_roles_and_responsibilities') {
        continue;
      }
      if (strpos($value['form_key'], $item['form_key']) !== FALSE && $value['type'] == 'textarea') {
        if (!array_key_exists($key, $self_comments_array)) {
          $temp = new stdClass();
          $temp->form_key = $cid_indexed_dataset[$key]->form_key;
          $temp->question = $cid_indexed_dataset[$key]->name;
          $temp->comment = $cid_indexed_dataset[$key]->DATA;
          array_push($self_comments_array, $temp);
        }
      }
    }
  }
}

/**
 * synthesize_self_review_ratings_and_comments
 * @param
 * @return
 * */
function synthesize_self_review_ratings_and_comments($self_dataset, $mapping_table) {
//  dd($self_dataset,'$self_dataset');
//  dd($mapping_table,'$mapping_table');
  $self_review_content = array();
  $temp_idx = 0;
  for ($i = 0; $i < count($self_dataset); $i++) {
    if (!array_key_exists($i, $self_dataset)) {
      continue;
    }
    if ($self_dataset[$i]->type != 'select' || $self_dataset[$i]->form_key == 'rating') {
      continue;
    }
    $obj = new stdClass();
    $obj->cid = $self_dataset[$i]->cid;
    $obj->form_key = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->form_key;
    $obj->question = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->name;

    $obj->rating = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->DATA;
    $obj->comment = $self_dataset[$mapping_table[$temp_idx]['comment_idx']]->DATA;
    $obj->weight = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->weight;
    $temp_idx++;
    array_push($self_review_content, $obj);
  }
  return $self_review_content;
}

/**
 * synthesize_all_peer_review_ratings_and_comments
 * @param array by reference $nid_pname_array
 * @param array by reference $pr_data_array
 * @param array by reference $mapping_table
 * @param int $item_count
 * @return array
 * */
function synthesize_all_peer_review_ratings_and_comments(&$nid_pname_array, &$pr_data_array, &$mapping_table, $item_count) {
  $all_peer_review_content = array();
  $count = count($nid_pname_array);
  if (count($nid_pname_array) != 0) {
    $providerName = $nid_pname_array[0]['providerName'];
  }
  else {
    $msg = 'No new provider comments was added.';
    drupal_set_message($msg, 'status');
    return;
  }
  $rating_array = _generate_provider_syn_ratings_array($pr_data_array, $item_count);
  $comment_array = _generate_provider_syn_comments_array($pr_data_array, $item_count);

// We should iterate the content according to the adjusted sequence as mapping table!.
  $temp_idx = 0;
  for ($i = 0; $i < count($pr_data_array[$providerName]->dataset); $i++) {
    if ($pr_data_array[$providerName]->dataset[$i]->type != 'select') {
      continue;
    }
    $obj = new stdClass();
    $obj->cid = $pr_data_array[$providerName]->dataset[$i]->cid;
    $obj->form_key = $pr_data_array[$providerName]->dataset[$i]->form_key;
    $obj->question = $pr_data_array[$providerName]->dataset[$i]->name;

    $obj->ratings = $rating_array[$mapping_table[$temp_idx]['rating_idx']];
    $obj->comments = $comment_array[$mapping_table[$temp_idx]['comment_idx']];
    $temp_idx++;
    array_push($all_peer_review_content, $obj);
  }
  return $all_peer_review_content;
}

/**
 * _generate_provider_syn_ratings_array
 * @param array $pr_data_array
 * @param int $count
 * @return array
 * */
function _generate_provider_syn_ratings_array($pr_data_array, $count) {
  $rating_array = range(0, $count - 1);
  for ($i = 0; $i < $count; $i++) {
    $rating_array[$i] = array();
  }
  foreach ($pr_data_array as $item) {
    for ($i = 0; $i < $count; $i++) {
      if ($item->dataset[$i]->type == 'select' && $item->dataset[$i]->DATA != 'NIL') {
        $rating_array[$item->dataset[$i]->cid][$item->providerName] = $item->dataset[$i]->DATA;
      }
      else if ($item->dataset[$i]->type == 'select' && $item->dataset[$i]->DATA == 'NIL'){
        $rating_array[$item->dataset[$i]->cid][$item->providerName] = 'No ratings';      
      }
    }
  }
  return $rating_array;
}

function _get_provider_isRead_status_from_db($nid) {
  $query = db_select('prs_rel_provider_employee', 'prpe')
          ->fields('prpe', array('isRead'))
          ->condition('prpe.nid', $nid)
          ->execute()->fetchAll();
  return $query[0]->isRead;
}

/**
 * _generate_provider_syn_comments_array
 * @param array $pr_data_array
 * @param int $count
 * @return array
 * */
function _generate_provider_syn_comments_array($pr_data_array, $count) {
  $comment_array = range(0, $count - 1);
  for ($i = 0; $i < $count; $i++) {
    $comment_array[$i] = array();
  }
  dd($pr_data_array, 'pr_data_array');
  foreach ($pr_data_array as $item) {
    for ($i = 0; $i < $count; $i++) {
      if ($item->dataset[$i]->type == 'textarea' && $item->dataset[$i]->DATA != 'NIL') {
        $comment_array[$item->dataset[$i]->cid][$item->providerName] = new stdClass();
        $comment_array[$item->dataset[$i]->cid][$item->providerName]->comment = $item->dataset[$i]->DATA;
        $comment_array[$item->dataset[$i]->cid][$item->providerName]->isRead = $item->isRead;
      }
      else if ($item->dataset[$i]->type == 'textarea' && $item->dataset[$i]->DATA == 'NIL') {
        $comment_array[$item->dataset[$i]->cid][$item->providerName] = new stdClass();
        $comment_array[$item->dataset[$i]->cid][$item->providerName]->comment = 'NO_COMMENT';
        $comment_array[$item->dataset[$i]->cid][$item->providerName]->isRead = $item->isRead;
      }
      else {
        continue;
      }
    }
  }
  return $comment_array;
}

/**
 * _fetch_cid_formkey_from_dataset
 * @param array $data_src
 * @rerturn array
 * */
function _fetch_cid_formkey_from_dataset($data_src) {
  if (count($data_src) == 0) {
    return;
  }
  $index = array_keys($data_src);
  $cid_formkey_type_array = array();
  foreach ($data_src[$index[0]]->dataset as $item) {
    $cid = $item->cid;
    $form_key = $item->form_key;
    $type = $item->type;
    $pid = $item->pid;
    $temp = array('form_key' => $form_key, 'type' => $type, 'pid' => $pid);
    $cid_formkey_type_array[$cid] = $temp;
  }
  return $cid_formkey_type_array;
}

/**
 * reorder_form_contents
 * @param array $pr_data_array
 * @return array
 * */
function reorder_form_contents($pr_data_array, &$cid_formkey_type_array, &$adj_list) {
/////////////////////////////////////
// $cid_formkey_type mapping ralations
/////////////////////////////////////
  $cid_formkey_type_array = _fetch_cid_formkey_from_dataset($pr_data_array);
/////////////////////////////////////
// $adj_list adjacent list
/////////////////////////////////////
  $adj_list = dfs_generate_adjacent_list($cid_formkey_type_array);
  $sequential_list = dfs_travel_all_node($adj_list);
  $mapping_table = adjust_question_and_answer($cid_formkey_type_array, $sequential_list);
  return $mapping_table;
}

function _generate_self_cid_formkey_type($self_dataset) {
  $cid_formkey_type_array = array();
  $count = count($self_dataset);
// dd($self_dataset, 'self_dataset');
  foreach ($self_dataset as $value) {
    if (!array_key_exists($value->cid, $cid_formkey_type_array)) {
      $cid_formkey_type_array[$value->cid] = array();
      $cid_formkey_type_array[$value->cid]['form_key'] = $value->form_key;
      $cid_formkey_type_array[$value->cid]['type'] = $value->type;
      $cid_formkey_type_array[$value->cid]['pid'] = $value->pid;
    }
  }

  return $cid_formkey_type_array;
}

/**
 * adjust_question_and_answer
 * matching questions, ratings and answers
 * @param array $src_array
 * @param array $dfs_tree
 * @return array $qa_array
 * */
function adjust_question_and_answer($src_array, $dfs_tree) {
  if (count($src_array) == 0 || count($dfs_tree) == 0) {
    return;
  }
  $qa_array = array();
  $comment_array = array();
  $count = count($src_array);

  if (count($src_array) != 0) {
    $max = max(array_keys($src_array));
  }
  else {
    $max = count($dfs_tree);
  }
  for ($i = 0; $i < $count; $i++) {
    if (!array_key_exists($i, $src_array)) {
      continue;
    }
    $form_key = $src_array[$i]['form_key'];
    $type = $src_array[$i]['type'];
    if ($src_array[$i]['type'] == 'textarea' && strpos($src_array[$i]['form_key'], '_comment') !== FALSE) {
      $comment_array[$i] = array();
      $comment_array[$i] = $src_array[$i];
      unset($src_array[$i]);
    }
  }
// dd($src_array, 'src_array');
// dd($comment_array, 'comment_array');
// dd(count($comment_array), 'count');
// dd(count($dfs_tree), 'count_dfs');
// dd($dfs_tree, 'dfs_tree');
  for ($i = 0; $i < $max; $i++) {
    if (!array_key_exists($i, $dfs_tree) || !array_key_exists($dfs_tree[$i], $src_array)) {
      continue;
    }
    if ($src_array[$dfs_tree[$i]]['type'] == 'select') {
      foreach ($comment_array as $key => $comment) {
        if (strpos($comment['form_key'], $src_array[$dfs_tree[$i]]['form_key']) !== FALSE) {
          $temp = array(
            'rating_idx' => $dfs_tree[$i],
            'comment_idx' => $key);
          array_push($qa_array, $temp);
        }
      }
    }
  }
  return $qa_array;
}

//////////////////////////////////////////////////////////////////////////////////////////////
/**
 * These functions which their signature start with a profix "dfs_" below implement node walk by Depth Fisrt according to their parent and children relationship.
 * by Anfernee
 * */
//////////////////////////////////////////////////////////////////////////////////////////////

/**
 * dfs_generate_adjacent_list
 * generate the adjacent list which represents a tree datastructure. Each subarray stands for all the children nodes of their index node
 * @param array $cid_formkey_type_array
 * @return array $adj_list
 * */
function dfs_generate_adjacent_list($cid_formkey_type_array) {
  if (count($cid_formkey_type_array) == 0) {
    return;
  }
  $cid_index = array_keys($cid_formkey_type_array);
  $adj_list = array();
  foreach ($cid_index as $item) {
    $adj_list[$item] = array();
  }
  for ($i = 0; $i < count($cid_index); $i++) {
    $pnode = $cid_formkey_type_array[$cid_index[$i]]['pid'];
    if (!array_key_exists($pnode, $adj_list)) {
      $adj_list[$pnode] = array();
    }
    array_push($adj_list[$pnode], $cid_index[$i]);
  }
  return $adj_list;
}

/**
 * dfs_travel_all_node
 * travel all the node by Depth First
 * @param array $adj_list
 * @return array $visited_array
 * */
function dfs_travel_all_node($adj_list) {
// The nodes those have been visited.
  $visited_array = array();
// The unvisited array used as a stack.
  $unvisited_array = array();
  $current = 0;
  array_push($unvisited_array, 0);
// unset($adj_list[0]);
  while (count($unvisited_array) != 0) {
    $current = array_pop($unvisited_array);
    if (count($adj_list[$current]) != 0) {
      dfs_push($unvisited_array, $adj_list[$current]);
    }

// if ()
    array_push($visited_array, $current);
  }
  return $visited_array;
}

/**
 * dfs_push
 * push all the element in array $source into array $target on by one, which behaves like a stack.
 * @param array by reference $target
 * @param array $source
 * */
function dfs_push(&$target, $source) {
  for ($i = count($source) - 1; $i >= 0; $i--) {
    array_push($target, $source[$i]);
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////

/**
 * get_latest_submitted_data_from_db
 * @param int $nid
 * @return array $dataset,data which contains $cid, $sid, $pid, $form_key, $type, $weight, $name, $data.
 * if you would like to accuire additional data from here, feel free to adjust the db_query statement.
 * */
function get_latest_submitted_data_from_db($nid, $sid) {
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
          webform_component.cid', array(':sid' => $sid, ':nid' => $nid))->fetchAll();
  return $dataset;
}

/**
 * insert counselor's submission info to db table prs_counselor_submit_info
 * @param int $nid
 * @return int $csiid
 * */
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
 * */
function _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, &$clor_items) {
  $range = array(0, 1, 2, 3, 4, 5);
  $csdid_array = array();
  foreach ($clor_items as $key => $value) {
    if (in_array($value->rating, $range) && ($value->peerAvgRating >= 0 && $value->peerAvgRating <= 5)) {
        $csdid = db_insert('prs_counselor_submit_data')
              ->fields(array(
                'nid' => $nid,
                'csiid' => $csiid,
                'count' => $key,
                'title' => $value->header,
                'rating' => $value->rating,
                'peer_avg_rating' => $value->peerAvgRating,
                'counselor_comment' => $value->comment,
                'peer_comments' => $value->revisedComment,
              ))->execute();
      if ($csdid != -1) {
        array_push($csdid_array, $csdid);
      }
    }
    else if (in_array($value->rating, $range) && $value->peerAvgRating == -1){
      $csdid = db_insert('prs_counselor_submit_data')
              ->fields(array(
                'nid' => $nid,
                'csiid' => $csiid,
                'count' => $key,
                'title' => $value->header,
                'rating' => $value->rating,
                'counselor_comment' => $value->comment,
                'peer_comments' => $value->revisedComment,
              ))->execute();
      if ($csdid != -1) {
        array_push($csdid_array, $csdid);
      }
    }
    else {
      $csdid = db_insert('prs_counselor_submit_data')
              ->fields(array(
                'nid' => $nid,
                'csiid' => $csiid,
                'count' => $key,
                'title' => $value->header,
                'counselor_comment' => $value->comment,
                'peer_comments' => $value->revisedComment,
              ))->execute();
      if ($csdid != -1) {
        array_push($csdid_array, $csdid);
      }
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

function _submit_counselor_approve_info_to_DB($rreid, $nid, &$clor_items) {
  dd($clor_items, 'clor_items');
  $sid = get_last_sid_by_nid($nid);
  $csiid = _insert_counselor_submittion_info_to_DB($nid);
  $csdid_array = _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, $clor_items);

  $provider_table = db_update('prs_rel_provider_employee')
      ->fields(array(
        'isRead' => 1,
        'isCounselorApproved' => 1,
        'deniedInfo' => 'Approved',
      ))
      ->condition('nid', $nid)
      ->execute();

  $review_table = db_update('prs_rel_review_employee')
      ->fields(array(
        'rstatus' => 3,))
      ->condition('rreid', $rreid)
      ->execute();

  if (($provider_table != 0) && ($review_table != 0)) {
    return TRUE;
  }
  else {
    return FALSE;
  }
}

/**
 * _append_counselor_reject_info_to_DB
 * @param int $rreid
 * @param int $nid
 * @param array of stdClass Object $clor_items
 * @param string by referen $reject_comments
 * @return bool
 * */
function _append_counselor_reject_info_to_DB($rreid, $nid, &$clor_items, &$reject_comments) {
  $sid = get_last_sid_by_nid($nid);
  $csiid = _insert_counselor_submittion_info_to_DB($nid);
  $csdid_array = _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, $clor_items);

  $provider_table = db_update('prs_rel_provider_employee')
      ->fields(array(
        'status' => 0,
        'isRead' => 1,
        'isCounselorApproved' => 2,
        'deniedInfo' => $reject_comments,
      ))
      ->condition('nid', $nid)
      ->execute();
  $webform_table = db_update('webform_submissions')
      ->fields(array(
        'is_draft' => 1,))
      ->condition('nid', $nid)
      ->condition('sid', $sid)
      ->execute();
  $review_table = db_update('prs_rel_review_employee')
      ->fields(array(
        'rstatus' => 1,))
      ->condition('rreid', $rreid)
      ->execute();

  if (($provider_table != 0) && ($webform_table != 0) && ($review_table != 0)) {
    return TRUE;
  }
  else {
    return FALSE;
  }
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

function _generate_rating_qa_sheet($src_array) {
  $target_array = array();
  foreach ($src_array as $item) {
    if ($item['identifier'] == 2) {
      array_push($target_array, $item);
    }
  }
  return $target_array;
}

/**
 * This func is temporarily used to map the header and its result of the survey by hard code.
 * It will be replaced later.
 * */
function generate_selfassessment_result_array($qa_table) {
  $target = array();
  for ($i = 0; $i < count($qa_table); $i++) {
    /*    if ($qa_table[$i]->cid == 'NIL') {
      continue;
      }
     */ $key = $qa_table[$i]->cid;
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
