<?php

/**
 * @file
 * Page callback function for view assessment.
 * */
function load_self_assessment($rreid = NULL) {
  access_authen($rreid);
  $cur_user_name = get_current_user_name();
  $reviewee = get_reviewee_from_db($rreid);
  $counselor = get_counelor($reviewee);
  $review_status = get_review_status_from_db($rreid);
  // identify_accessibility($cur_user_name, $reviewee, $counselor, $tgr_status, $cur_status);

  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);
////////////////////////////////////////////////////////
// Item below is for self assessment
////////////////////////////////////////////////////////

  $self_dataset = get_latest_submitted_data_from_db($nid, $sid);
  // dd($self_dataset, 'self_dataset');
  $cid_indexed_dataset = _indexed_by_cid($self_dataset);
  // dd($cid_indexed_dataset, 'cid_indexed_dataset');
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
  $review_info = new stdClass();
  $self_comments_array = array();
  $overall_rating = new stdClass();
  // dd($cid_indexed_dataset, 'cid_indexed_dataset');
  _matching_none_selection_items($none_selection_item, $self_cid_formkey_type_array, $cid_indexed_dataset, $review_info, $self_comments_array, $overall_rating);
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
    $overall_rating->counselor_rating = _fetch_counselor_overall_rating($revised_comments);
  }
  else {
    $have_counselor_data = FALSE;
  }

  $pageContent = '';
  $total_item_count = $self_item_count + count($self_comments_array);
  foreach ($self_question_rating_comment_result as $key => $value) {
//=====================the question which have pie chart ============CONST===============================
    $pie_form_key_array = array('technical_abilities', 'consulting_skills', 'professionalism', 'leadership', 'teamwork');
    $unread_comment = array();
    if (in_array($value->form_key, $pie_form_key_array)) {
      $form_key = $value->form_key . '_category';
      if ($have_peer_data) {
        $fieldset_cid = get_fieldset_key_in_peer_table($form_key, $cid_formkey_type_array);
        $children_array = get_children_array_by_cid($fieldset_cid, $adj_list);
        $peer_name = array();
        $comment_name = array();
        _manage_data_return_three_array($children_array, $peer_question_rating_comment_result, $peer_name, $comment_name, $unread_comment);
        $pie_data = _manage_pie_chart($peer_name, $comment_name, $form_key);
      }
      else {
        $pie_data = new stdClass();
        $pie_data->id = $form_key;
        $pie_data->all_avg = 'No Data.';
        $pie_data->have_peer_data = FALSE;
      }
//=====================pie chart end=====================================================================
      if ($have_counselor_data) {
        generate_synthesized_peer_comment($value->form_key, $unread_comment, $revised_comments);
      }
      $content = theme('view_assessment', array(
        'item_num' => $key,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_dataset' => $value,
        'pie_data' => $pie_data,
        'unread_comment' => $unread_comment,
        'clor_rating_comment' => $revised_comments[$key],
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
        'clor_rating_comment' => $revised_comments[$key],
        'total_item_count' => $total_item_count,
        'review_info' => $review_info,
        'overall_rating' => $overall_rating,
      ));
      $pageContent .= $content;
    }
  }
  foreach ($self_comments_array as $key => $value) {
    $content = theme('view_assessment_comment_only', array(
      'item_num' => $self_item_count + $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_comment' => $value,
      'clor_rating_comment' => $revised_comments[$self_item_count + $key],
      'total_item_count' => $total_item_count,
      'review_info' => $review_info,
    ));
    $pageContent .= $content;
  }
  // generate comment only content here.
  return $pageContent;
}

function _manage_pie_chart($peer_name, $comment_name, $form_key) {
  $count0_1 = 0;
  $countNA = 0;
  $count1_1_2 = 0;
  $count2_1_3 = 0;
  $count3_1_4 = 0;
  $count4_1_5 = 0;

  $allcount = 0;
  $all_avg = 0;
  $allusefulcount = 0;

  $tooltiphtmlNA = '';
  $tooltiphtml0_1 = '';
  $tooltiphtml1_1_2 = '';
  $tooltiphtml2_1_3 = '';
  $tooltiphtml3_1_4 = '';
  $tooltiphtml4_1_5 = '';
  foreach ($peer_name as $key => $one_peer) {
    if ($one_peer > 0 && $one_peer <= 1) {
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
    elseif ($one_peer == 0) {
      $tooltiphtmlNA.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $countNA++;
    }
    $all_avg = $all_avg + $one_peer;
    if ($one_peer != 0) {
      $allusefulcount++;
    }
    $allcount++;
  }
  $textNA = '<input type="hidden" id="NA_' . $form_key . '" value="' . $tooltiphtmlNA . '" />';
  $texth0_1 = '<input type="hidden" id="0-1_' . $form_key . '" value="' . $tooltiphtml0_1 . '" />';
  $texth1_1_2 = '<input type="hidden" id="1-2_' . $form_key . '" value="' . $tooltiphtml1_1_2 . '" />';
  $texth2_1_3 = '<input type="hidden" id="2-3_' . $form_key . '" value="' . $tooltiphtml2_1_3 . '" />';
  $texth3_1_4 = '<input type="hidden" id="3-4_' . $form_key . '" value="' . $tooltiphtml3_1_4 . '" />';
  $texth4_1_5 = '<input type="hidden" id="4-5_' . $form_key . '" value="' . $tooltiphtml4_1_5 . '" />';
  $pie_data = new stdClass();
  if ($allcount != 0) {
    $pie_data->avg = array(
      'avgNA' => $countNA / $allcount,
      'avg0_1' => $count0_1 / $allcount,
      'avg1_1_2' => $count1_1_2 / $allcount,
      'avg2_1_3' => $count2_1_3 / $allcount,
      'avg3_1_4' => $count3_1_4 / $allcount,
      'avg4_1_5' => $count4_1_5 / $allcount,
    );
    $pie_data->hiddenvalue = $texth0_1 . $texth1_1_2 . $texth2_1_3 . $texth3_1_4 . $texth4_1_5 . $textNA;
    $pie_data->id = $form_key;
    $pie_data->have_peer_data = TRUE;
    if ($allusefulcount != 0) {
      $pie_data->all_avg = round($all_avg / $allusefulcount, 2);
    }
    else {
      $pie_data->all_avg = 'N/A';
    }
  }
  else {
    $pie_data->all_avg = 'No Data.';
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
            $peer_name_temp[$key]['flag'] = TRUE;
            if ($one_ratings != 'N/A') {
              if ($one_ratings != 0) {
                $peer_name_temp[$key]['rating'] = $one_ratings;
                $peer_name_temp[$key]['count'] = 1;
              }
              else {
                $peer_name_temp[$key]['rating'] = 0;
                $peer_name_temp[$key]['count'] = 0;
              }
              $comment_name[$key] = $one_question_answer->comments[$key]->comment . ' <br/>';
              if ($one_question_answer->comments[$key]->isRead == 0) {
                $unread_comment[$key] = $one_question_answer->comments[$key]->comment . ' ';
              }
            }
            else {

              $peer_name_temp[$key]['flag'] = FALSE;
            }
          }
          $is_first = FALSE;
        }
        else {
          foreach ($one_question_answer->ratings as $key => $one_ratings) {
            if ($peer_name_temp[$key]['flag']) {
              if ($one_ratings != 0) {

                $peer_name_temp[$key]['rating'] = $peer_name_temp[$key]['rating'] + $one_ratings;
                $peer_name_temp[$key]['count'] = $peer_name_temp[$key]['count'] + 1;
              }
              $comment_name[$key] .=$one_question_answer->comments[$key]->comment . ' <br/>';
              if ($one_question_answer->comments[$key]->isRead == 0) {
                $unread_comment[$key].=$one_question_answer->comments[$key]->comment . ' ';
              }
            }
          }
        }
        break;
      }
    }
  }
  foreach ($peer_name_temp as $key => $peer_avg) {
    if ($peer_avg['flag']) {
      //peer submit feedback
      if ($peer_avg['count'] != 0) {
        $peer_name[$key] = $peer_avg['rating'] / $peer_avg['count'];
      }
      else {
        //peer select all NA
        $peer_name[$key] = 0;
      }
    }
  }
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
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $overall_rating = $_POST['overallRating'];

  $decoded_items = array();
  foreach ($items as $item) {
    $temp = json_decode($item);
  
    if ($temp->rating == 'undefined') {
      $temp->rating = -1;
    }

    if ($temp->peerAvgRating == 'undefined') {
      $temp->peerAvgRating = 'N/A';
    }
    if ($temp->revisedComment == 'undefined') {
      $temp->revisedComment = 'N/A';
    }
    array_push($decoded_items, $temp);
  }

  $result = _submit_counselor_approve_info_to_DB($rreid, $nid, $decoded_items, $overall_rating);
  $is_approved = TRUE;
  send_approval_email_notification($rreid, $is_approved);
  echo $result;
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
  $overall_rating = $_POST['overallRating'];
  $decoded_items = array();
  foreach ($items as $item) {
    $temp = json_decode($item);

    if ($temp->rating == 'undefined') {
      $temp->rating = -1;
    }

    if ($temp->peerAvgRating == 'undefined') {
      $temp->peerAvgRating = "N/A";
    }
    if ($temp->revisedComment == 'undefined') {
      $temp->revisedComment = 'N/A';
    }
    array_push($decoded_items, $temp);
  }
  $result = _append_counselor_reject_info_to_DB($rreid, $nid, $decoded_items, $reject_comments, $overall_rating)
  ;
  $is_approved = FALSE;
  send_approval_email_notification($rreid, $is_approved, $reject_comments);
  echo $result;
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
    if ($value['pid'] == 0 && $value['type'] == 'fieldset' && (strpos($value['form_key'], '_category') !== FALSE)) {
      if (!array_key_exists($key, $survey_item_question)) {
        $survey_item_question[$key] = array();
        $survey_item_question[$key] = $value;
      }
    }
  }
  return $survey_item_question;
}

function _matching_none_selection_items($none_selection_item, $self_cid_formkey_type_array, $cid_indexed_dataset, &$review_info, &$self_comments_array, &$overall_rating) {
  foreach ($self_cid_formkey_type_array as $key => $value) {
    if ($value['form_key'] == 'project_roles_and_responsibilities' && $value['type'] == 'textarea') {
      $review_info->project_roles_and_responsibilities = $cid_indexed_dataset[$key]->DATA;
      continue;
    }
    else if ($value['form_key'] == 'clientdate' && $value['type'] == 'textfield') {
      $review_info->clientdate = $cid_indexed_dataset[$key]->DATA;
      continue;
    }
    else if ($value['form_key'] == 'overall_rating' && $value['type'] == 'textfield') {
      $overall_rating->counselee_rating = $cid_indexed_dataset[$key]->DATA;
    }
    foreach ($none_selection_item as $item) {
      if ($value['form_key'] == 'project_roles_and_responsibilities_category') {
        continue;
      }
      $temp = explode('_', $item['form_key']);
      array_pop($temp);
      if (array_search('label', $temp)) {
        array_pop($temp);
      }
      $type = implode("_", $temp);
      if (strpos($value['form_key'], $type) !== FALSE && $value['type'] == 'textarea') {
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
  $iterate_counting = 0;
  for ($i = 0; $i < count($pr_data_array[$providerName]->dataset); $i++) {
    if ($pr_data_array[$providerName]->dataset[$i]->type != 'select') {
      continue;
    }
    $obj = new stdClass();
    $obj->cid = $pr_data_array[$providerName]->dataset[$i]->cid;
    $obj->form_key = $pr_data_array[$providerName]->dataset[$i]->form_key;
    $obj->question = $pr_data_array[$providerName]->dataset[$i]->name;

    $obj->ratings = $rating_array[$mapping_table[$iterate_counting]['rating_idx']];
    $obj->comments = $comment_array[$mapping_table[$iterate_counting]['comment_idx']];
    $iterate_counting++;
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
    foreach ($item->dataset as $key => $value) {
      if ($value->type == 'select' && $value->DATA !== '') {
        $rating_array[$value->cid][$item->providerName] = $value->DATA;
      }
      else if ($value->type == 'select' && $value->DATA == NULL) {
        $rating_array[$value->cid][$item->providerName] = 'N/A';
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
  foreach ($pr_data_array as $item) {
    foreach ($item->dataset as $key => $value) {
      if ($value->type == 'textarea' && $value->DATA !== '') {
        $comment_array[$value->cid][$item->providerName] = new stdClass();
        $comment_array[$value->cid][$item->providerName]->comment = $value->DATA;
        $comment_array[$value->cid][$item->providerName]->isRead = $item->isRead;
      }
      else if ($value->type == 'textarea' && $value->form_key != 'summary_comments' && $value->DATA == NULL) {
        $comment_array[$value->cid][$item->providerName] = new stdClass();
        $comment_array[$value->cid][$item->providerName]->comment = '';
        $comment_array[$value->cid][$item->providerName]->isRead = $item->isRead;
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

function _fetch_counselor_overall_rating($data_src) {
  foreach ($data_src as $key => $value) {
    if ($value->form_key == 'overall_rating' && isset($value->clor_comment)) {
      return $value->clor_comment;
    }
  }
  return NULL;
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
        IF (wd.sid IS NULL, \'\', wd.sid) AS sid,
        IF (wd.DATA IS NULL, \'\', wd.DATA) AS DATA
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
function _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, &$clor_items, $overall_rating) {
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
    else if (in_array($value->rating, $range) && $value->peerAvgRating == -1) {
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
  $overall_rating_csdid = db_insert('prs_counselor_submit_data')
                        -> fields(array(
                          'nid' => $nid,
                          'csiid' => $csiid,
                          'count' => count($csdid_array),
                          'title' => 'overall_rating',
                          'counselor_comment' => $overall_rating,
                          ))->execute();

  if (count($csdid_array) != 0 && $overall_rating_csdid != -1) {
    return $csdid_array;
  }
  else {
    $msg = 'You did not add any content to DB!';
    drupal_set_message($msg, 'status');
  }
}

function _submit_counselor_approve_info_to_DB($rreid, $nid, &$clor_items, $overall_rating) {
  $sid = get_last_sid_by_nid($nid);
  $csiid = _insert_counselor_submittion_info_to_DB($nid);
  $csdid_array = _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, $clor_items, $overall_rating);

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
function _append_counselor_reject_info_to_DB($rreid, $nid, &$clor_items, &$reject_comments, $overall_rating) {
  $sid = get_last_sid_by_nid($nid);
  $csiid = _insert_counselor_submittion_info_to_DB($nid);
  $csdid_array = _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, $clor_items, $overall_rating);

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

function send_approval_email_notification($rreid, $is_approved, $reject_comments=NULL) {
  $provider_info = get_provider_info($rreid);
  if (isset($provider_info)) {
    $counselee_name = $provider_info[0]->employeeName;
    $counselor_name = get_counelor($counselee_name);
    $token = $provider_info[0]->token;
    $mail_receiver_array = array();
    array_push($mail_receiver_array, $counselee_name);
    $email_addr = select_email_from_prs_reviewers($mail_receiver_array);
  }
  if (isset($email_addr)) {
    $address = $email_addr[0];
  }
  if ($reject_comments != NULL) {
    counselor_audit_review_remind($address, $counselee_name, $counselor_name, $token, $rreid, $is_approved, $reject_comments);
  }
  else {
    counselor_audit_review_remind($address, $counselee_name, $counselor_name, $token, $rreid, $is_approved);
  }
}