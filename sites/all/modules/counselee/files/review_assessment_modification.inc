<?php

/**
 * @file
 * Page callback function for view assessment.
 * */
function load_self_assessment($rreid = NULL) {
//  access_authen($rreid);
  $cur_user_name = get_current_user_name();
  $reviewee = get_reviewee_from_db($rreid);
  $counselor = get_counselor($reviewee);
  $review_status = get_review_status_from_db($rreid);
  $allowed_roles = AccessRoles::getViewAssessment();
  $allowed_status = AccessStatus::getViewAssessment();

  if (!identify_accessibility($cur_user_name, $reviewee, $counselor, $allowed_roles, $allowed_status, $review_status)) {
    drupal_goto('accessdenied');
  }

  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);
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
  $have_counselor_data = TRUE;
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
  appendCSSJS();
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
  $pageContent .= '<hr>';
  foreach ($self_question_rating_comment_result as $key => $value) {
//=====================the question which have pie chart ============CONST===============================
    $pie_form_key_array = array('technical_abilities', 'consulting_skills', 'professionalism', 'leadership', 'teamwork');
    if (in_array($value->form_key, $pie_form_key_array)) {
      $form_key = $value->form_key . '_category';
      if ($peer_information_set->have_peer_data) {
        $fieldset_cid = get_fieldset_key_in_peer_table($form_key, $peer_information_set->cid_formkey_type_array);
        $children_array = get_children_array_by_cid($fieldset_cid, $peer_information_set->adj_list);
        $peer_name = array();
        $peer_json = '';
        $json_count=0;
        _manage_data_return_three_array($children_array, $peer_information_set->peer_question_rating_comment_result, $peer_name, $peer_json,$json_count);
        $pie_data = _manage_pie_chart($peer_name, $form_key);
      }
      else {
        $json_count=0;
        $peer_json = '';
        $pie_data = new stdClass();
        $pie_data->id = $form_key;
        $pie_data->all_avg = NO_DATA;
        $pie_data->have_peer_data = FALSE;
      }
//=====================pie chart end=====================================================================
      $content = theme('view_assessment', array(
        'item_num' => $key,
        'rreid' => $rreid,
        'nid' => $nid,
        'reviewee' => $reviewee,
        'self_dataset' => $value,
        'json_count'=>$json_count,
        'pie_data' => $pie_data,
        'peer_json' => $peer_json,
        'clor_rating_comment' => $revised_comments[$key],
        'total_item_count' => $total_item_count,
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
    ));
    $pageContent .= $content;
  }
  // generate comment only content here.
  return $pageContent;
}

function get_peer_comment_information_json($key, $peer_comment_info_set) {
  $form_key = $key . '_category';
  if ($peer_comment_info_set->have_peer_data) {
    $fieldset_cid = get_fieldset_key_in_peer_table($form_key, $peer_comment_info_set->cid_formkey_type_array);
    $children_array = get_children_array_by_cid($fieldset_cid, $peer_comment_info_set->adj_list);
    $peer_json = get_peer_json($children_array, $peer_comment_info_set->peer_question_rating_comment_result);
  }
  else {
    $peer_json = '';
  }
  return $peer_json;
}

/**
 * match question_and_answer array and return json
 * @param type $children_array
 * @param type $question_and_answer
 * @return string
 */
function get_peer_json($children_array, $question_and_answer) {
  $peer_json_array = array();
  foreach ($children_array as $one_child) {
    foreach ($question_and_answer as $one_question_answer) {
      if ($one_child == $one_question_answer->cid) {
        foreach ($one_question_answer->ratings as $key => $one_ratings) {
          if ($one_ratings['rating'] != 'N/A') {
            if ($one_ratings['isDisplay'] == 0 && $one_ratings['isRead']) {
              $title = get_title_form_peer_array($one_question_answer);
              $peer_json_std = get_peer_json_without_peer_name_std($one_ratings['rating'], $one_ratings['comment'], $one_ratings['nid'], $one_question_answer->cid, $title);
              array_push($peer_json_array, $peer_json_std);
            }
          }
        }
        break;
      }
    }
  }
  $peer_json = drupal_json_encode($peer_json_array);
  return $peer_json;
}

/**
 * get basic peerinfomation
 * @param type $rreid
 * @param type $item_count
 * @return \stdClass
 */
function get_peer_comment_information_array($rreid, $item_count = -1) {

  $cid_formkey_type_array = array();
  $adj_list = array();
  $have_peer_data = TRUE;
  $providerInfo_table = get_all_providers_info_from_db($rreid);
  if (count($providerInfo_table)) {
    //selected peers
    $nid_pname_sid_array = _fetch_provider_nodeID_and_submissionID($providerInfo_table);
    $pr_data_array = _get_provider_review_content($nid_pname_sid_array);
    $mapping_table = reorder_form_contents($pr_data_array, $cid_formkey_type_array, $adj_list);
    $peer_question_rating_comment_result = synthesize_all_peer_review_ratings_and_comments($nid_pname_sid_array, $pr_data_array, $mapping_table, $item_count);
    $peer_info = new stdClass();
    $peer_info->cid_formkey_type_array = $cid_formkey_type_array;
    $peer_info->adj_list = $adj_list;
    $peer_info->have_peer_data = $have_peer_data;
    $peer_info->peer_question_rating_comment_result = $peer_question_rating_comment_result;
//    dd($peer_question_rating_comment_result, '$peer_question_rating_comment_result');
  }
  else {
    // no peer
    $peer_info = new stdClass();
    $peer_info->cid_formkey_type_array = NULL;
    $peer_info->adj_list = NULL;
    $peer_info->have_peer_data = FALSE;
    $peer_info->peer_question_rating_comment_result = NULL;
  }
  return $peer_info;
}

function _manage_pie_chart($peer_name, $form_key) {
  $count0_1 = 0;
  $countNA = 0;
  $count1_1_2 = 0;
  $count2_1_3 = 0;
  $count3_1_4 = 0;
  $count4_1_5 = 0;

  $allcount = 0;
  $all_avg = 0;
  $allusefulcount = 0;
  $allrating = 0;
  $tooltiphtmlNA = '';
  $tooltiphtml0_1 = '';
  $tooltiphtml1_1_2 = '';
  $tooltiphtml2_1_3 = '';
  $tooltiphtml3_1_4 = '';
  $tooltiphtml4_1_5 = '';
  foreach ($peer_name as $key => $one_peer) {
    if ($one_peer['avgrating'] > 0 && $one_peer['avgrating'] <= 1) {
//      $tooltiphtml0_1.=$key . ':<br/>' . $comment_name[$key] . '<br/>';
      $tooltiphtml0_1.=format_login_name($key) . ':' . round($one_peer['avgrating'], 1) . '<br/>';
      $count0_1++;
    }
    elseif ($one_peer['avgrating'] > 1 && $one_peer['avgrating'] <= 2) {
      $tooltiphtml1_1_2.=format_login_name($key) . ':' . round($one_peer['avgrating'], 1) . '<br/>';
      $count1_1_2++;
    }
    elseif ($one_peer['avgrating'] > 2 && $one_peer['avgrating'] <= 3) {
      $tooltiphtml2_1_3.=format_login_name($key) . ':' . round($one_peer['avgrating'], 1) . '<br/>';
      $count2_1_3++;
    }
    elseif ($one_peer['avgrating'] > 3 && $one_peer['avgrating'] <= 4) {
      $tooltiphtml3_1_4.=format_login_name($key) . ':' . round($one_peer['avgrating'], 1) . '<br/>';
      $count3_1_4++;
    }
    elseif ($one_peer['avgrating'] > 4 && $one_peer['avgrating'] <= 5) {
      $tooltiphtml4_1_5.=format_login_name($key) . ':' . round($one_peer['avgrating'], 1) . '<br/>';
      $count4_1_5++;
    }
    elseif ($one_peer['avgrating'] == 0) {
      $tooltiphtmlNA.=format_login_name($key) . ':N/A<br/>';
      $countNA++;
    }
    $all_avg = $all_avg + $one_peer['avgrating'];
//    if ($one_peer['avgrating'] != 0) {
//      $allusefulcount++;
//    }
    $allusefulcount = $allusefulcount + $one_peer['count'];
    $allrating = $allrating + $one_peer['allrating'];
    $allcount++;
  }
//  dd($form_key,'$form_key');
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
      $pie_data->all_avg = round($allrating / $allusefulcount, 1);
    }
    else {
      $pie_data->all_avg = 'N/A';
    }
  }
  else {
    $pie_data->all_avg = NO_DATA;
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
 * @param type $peer_json json string
 */
function _manage_data_return_three_array($children_array, $peer_question_rating_comment_result, &$peer_name, &$peer_json,&$json_count) {
  $peer_json_array = array();
  $is_first = TRUE;
  $number = 0;
  foreach ($children_array as $one_child) {
    foreach ($peer_question_rating_comment_result as $one_question_answer) {
      if ($one_child == $one_question_answer->cid) {
        if ($is_first) {
          foreach ($one_question_answer->ratings as $key => $one_ratings) {
            $peer_name_temp[$key]['flag'] = TRUE;
            if ($one_ratings['rating'] != 'N/A') {
              if ($one_ratings['rating'] != 0) {
                $peer_name_temp[$key]['rating'] = $one_ratings['rating'];
                $peer_name_temp[$key]['count'] = 1;
              }
              else {
                $peer_name_temp[$key]['rating'] = 0;
                $peer_name_temp[$key]['count'] = 0;
              }
              $title = get_title_form_peer_array($one_question_answer);
              $number++;
              $peer_json_std = get_peer_json_std($key, $one_ratings['rating'], $one_ratings['comment'], $one_ratings['isDisplay'], $one_ratings['nid'], $one_question_answer->cid, $title, $number);
              array_push($peer_json_array, $peer_json_std);
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
              if ($one_ratings['rating'] != 0) {

                $peer_name_temp[$key]['rating'] = $peer_name_temp[$key]['rating'] + $one_ratings['rating'];
                $peer_name_temp[$key]['count'] = $peer_name_temp[$key]['count'] + 1;
              }
              $title = get_title_form_peer_array($one_question_answer);
              $number++;
              $peer_json_std = get_peer_json_std($key, $one_ratings['rating'], $one_ratings['comment'], $one_ratings['isDisplay'], $one_ratings['nid'], $one_question_answer->cid, $title,$number);
              array_push($peer_json_array, $peer_json_std);
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
        $peer_name[$key]['avgrating'] = $peer_avg['rating'] / $peer_avg['count'];
        $peer_name[$key]['count'] = $peer_avg['count'];
        $peer_name[$key]['allrating'] = $peer_avg['rating'];
      }
      else {
        //peer select all NA
        $peer_name[$key]['avgrating'] = 0;
        $peer_name[$key]['count'] = 0;
        $peer_name[$key]['allrating'] = 0;
      }
    }
  }
  $json_count=count($peer_json_array);
  $peer_json = drupal_json_encode($peer_json_array);
}

function get_peer_json_without_peer_name_std($rating, $comment, $nid, $cid, $title) {
  $peer_json_std = new stdClass();
  $peer_json_std->rating = (($rating == 0) ? 'N/A' : $rating);
  $peer_json_std->comment = ((trim(filter_xss_and_check_plain($comment)) == '') ? NO_COMMENT : filter_xss_and_check_plain($comment));
  $peer_json_std->nid = $nid;
  $peer_json_std->cid = $cid;
  $peer_json_std->title = $title;
  return $peer_json_std;
}

function get_peer_json_std($peer_name, $rating, $comment, $display, $nid, $cid, $title,$number) {
  $peer_json_std = new stdClass();
  $peer_json_std->peer_name = format_login_name($peer_name);
  $peer_json_std->rating = (($rating == 0) ? 'N/A' : $rating);
  $peer_json_std->comment = ((trim(filter_xss_and_check_plain($comment)) == '') ? NO_COMMENT : filter_xss_and_check_plain($comment));
  $peer_json_std->display = (($display == 0) ? 'true' : 'false');
  $peer_json_std->nid = $nid;
  $peer_json_std->cid = $cid;
  $peer_json_std->title = $title;
  $peer_json_std->number = $number;
  return $peer_json_std;
}

function _manage_data_return_three_array_readonly($children_array, $peer_question_rating_comment_result, &$peer_name, &$peer_json) {
  $peer_json_array = array();
  $is_first = TRUE;
  foreach ($children_array as $one_child) {
    foreach ($peer_question_rating_comment_result as $one_question_answer) {
      if ($one_child == $one_question_answer->cid) {
        if ($is_first) {
          foreach ($one_question_answer->ratings as $key => $one_ratings) {
            $peer_name_temp[$key]['flag'] = TRUE;
            if ($one_ratings['rating'] != 'N/A') {
              if ($one_ratings['rating'] != 0) {
                $peer_name_temp[$key]['rating'] = $one_ratings['rating'];
                $peer_name_temp[$key]['count'] = 1;
              }
              else {
                $peer_name_temp[$key]['rating'] = 0;
                $peer_name_temp[$key]['count'] = 0;
              }

              if ($one_ratings['isDisplay'] == 0 && $one_ratings['isRead']) {
                $title = get_title_form_peer_array($one_question_answer);

                $peer_json_std = get_peer_json_without_peer_name_std($one_ratings['rating'], $one_ratings['comment'], $one_ratings['nid'], $one_question_answer->cid, $title);
                array_push($peer_json_array, $peer_json_std);
              }

//              dd($peer_json, '$peer_json');
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
              if ($one_ratings['rating'] != 0) {

                $peer_name_temp[$key]['rating'] = $peer_name_temp[$key]['rating'] + $one_ratings['rating'];
                $peer_name_temp[$key]['count'] = $peer_name_temp[$key]['count'] + 1;
              }
              if ($one_ratings['isDisplay'] == 0 && $one_ratings['isRead']) {
                $title = get_title_form_peer_array($one_question_answer);
                $peer_json_std = get_peer_json_without_peer_name_std($one_ratings['rating'], $one_ratings['comment'], $one_ratings['nid'], $one_question_answer->cid, $title);
                array_push($peer_json_array, $peer_json_std);
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
        $peer_name[$key]['avgrating'] = $peer_avg['rating'] / $peer_avg['count'];
        $peer_name[$key]['count'] = $peer_avg['count'];
        $peer_name[$key]['allrating'] = $peer_avg['rating'];
      }
      else {
        //peer select all NA
        $peer_name[$key]['avgrating'] = 0;
        $peer_name[$key]['count'] = 0;
        $peer_name[$key]['allrating'] = 0;
      }
    }
  }
  $peer_json = drupal_json_encode($peer_json_array);
}

function get_title_form_peer_array($peer_array) {
  $title_array_temp = explode('-', $peer_array->question);
  $title_temp = array_shift($title_array_temp);
  $title_array = explode('–', $title_temp);
  $title = array_shift($title_array);
//  dd($peer_array->question,'$peer_array->question');
//  dd($title_array,'$title_array');
//  dd($title,'$title');
  return $title;
}

function get_fieldset_key_in_peer_table($form_key, $cid_formkey_type_array) {
  foreach ($cid_formkey_type_array as $key => $one_cid) {
    if ($one_cid['form_key'] == $form_key) {
      $fieldset_cid = $key;
      break;
    }
  }
  return $fieldset_cid;
}

function get_children_array_by_cid($fieldset_cid, $adj_list) {
  return $adj_list[$fieldset_cid];
}

function generate_synthesized_peer_comment($form_key, &$unread_comment, $revised_comments) {
  foreach ($revised_comments as $comment) {
    if ($comment->form_key == $form_key) {
//      if (!array_key_exists('revised', $unread_comment)) {
      $unread_comment['revised'] = $comment->peer_comments;
//      }
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
    $comment_item->clor_comment = filter_xss_and_check_plain($value->counselor_comment);
    $comment_item->peer_comments = filter_xss_and_check_plain($value->peer_comments);

    array_push($revised_comments, $comment_item);
  }
  return $revised_comments;
}

/**
 * Page callback function of save conselor's assessment as draft
 * */
function save_counselor_assessment_draft() {
  $result_val = 1;
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $overall_rating = $_POST['overallRating'];

  $decoded_items = decode_json_object($items);
  $result = _submit_counselor_draft_to_DB($rreid, $nid, $decoded_items, $overall_rating);
  echo $result_val;
}

function decode_json_object($json_context) {
  $decoded_items = array();
  foreach ($json_context as $key => $value) {
    $text = json_decode($value);
    array_push($decoded_items, $text);
  }
  return $decoded_items;
}

/**
 * Page callback function of approve counselee's assessment.
 * */
function approve_counselee_assessment() {
  $result_val = 1;
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $overall_rating = $_POST['overallRating'];

  $decoded_items = decode_json_object($items);

  $result = _submit_counselor_approve_info_to_DB($rreid, $nid, $decoded_items, $overall_rating);
  $is_approved = TRUE;
  send_approval_email_notification($rreid, $is_approved);
  echo $result_val;
}

/**
 * reject_counselor_assessment
 * @file
 * Page callback function for reject counselee assessment
 * */
function reject_counselee_assessment() {
  $result_val = 1;
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $reject_comments = $_POST['rejectComments'];
  $overall_rating = $_POST['overallRating'];

  $decoded_items = decode_json_object($items);

  $result = _append_counselor_reject_info_to_DB($rreid, $nid, $decoded_items, $reject_comments, $overall_rating);
  $is_approved = FALSE;
  send_approval_email_notification($rreid, $is_approved, $reject_comments);
  echo $result_val;
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
    $dataset = get_latest_peer_submitted_data_from_db($nid, $sid);
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

function process_self_assessment_data($self_dataset, &$self_cid_formkey_type_array) {
  $indexed_dataset = _indexed_by_cid($self_dataset);
  $self_cid_formkey_type_array = _generate_self_cid_formkey_type($self_dataset);
  $self_adj_list = dfs_generate_adjacent_list($self_cid_formkey_type_array);
  $self_sequential_list = dfs_travel_all_node($self_adj_list);
  $self_mapping_table = adjust_question_and_answer($self_cid_formkey_type_array, $self_sequential_list);
  $self_question_rating_comment_result = synthesize_self_review_ratings_and_comments($indexed_dataset, $self_mapping_table);
  $self_sorted_question_rating_comment_result = _resort_item_by_weight($self_question_rating_comment_result);
  return $self_sorted_question_rating_comment_result;
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

function init_review_info_content() {
  $review_info = array();
  $review_info['clientdate'] = array();
  $review_info['project_roles_and_responsibilities'] = array();
  $review_info['startdate'] = array();
  $review_info['enddate'] = array();
  return $review_info;
}

function _matching_none_selection_items($none_selection_item, $self_dataset, &$review_info, &$self_comments_array, &$overall_rating) {
  for ($i = count($self_dataset) - 1; $i >= 0; $i--) {
    if ($self_dataset[$i]->form_key == 'project_roles_and_responsibilities' && $self_dataset[$i]->type == 'textarea') {
      $review_info_item = new stdClass();
      $review_info_item->no = $self_dataset[$i]->no;
      $review_info_item->DATA = $self_dataset[$i]->DATA;
      array_push($review_info['project_roles_and_responsibilities'], $review_info_item);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'clientdate' && $self_dataset[$i]->type == 'textfield') {
      $review_info_item = new stdClass();
      $review_info_item->no = $self_dataset[$i]->no;
      $review_info_item->DATA = $self_dataset[$i]->DATA;
      array_push($review_info['clientdate'], $review_info_item);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'overall_rating' && $self_dataset[$i]->type == 'select') {
      $overall_rating->counselee_rating = $self_dataset[$i]->DATA;
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'start_date' && $self_dataset[$i]->type == 'textfield') {
      $review_info_item = new stdClass();
      $review_info_item->no = $self_dataset[$i]->no;
      $review_info_item->DATA = $self_dataset[$i]->DATA;
      array_push($review_info['startdate'], $review_info_item);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'end_date' && $self_dataset[$i]->type == 'textfield') {
      $review_info_item = new stdClass();
      $review_info_item->no = $self_dataset[$i]->no;
      $review_info_item->DATA = $self_dataset[$i]->DATA;
      array_push($review_info['enddate'], $review_info_item);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'project_roles_and_responsibilities_category') {
      continue;
    }
    else if ($self_dataset[$i]->type == 'textarea') {
      foreach ($none_selection_item as $item) {
        $temp = explode('_', $item['form_key']);
        array_pop($temp);
        if (array_search('label', $temp)) {
          array_pop($temp);
        }
        $ori_type = implode("_", $temp);
        $text_array = explode('_', $self_dataset[$i]->form_key);
        array_pop($text_array);
        $text = implode("_", $text_array);
        if ($text === $ori_type) {
          if (!array_key_exists($self_dataset[$i]->cid, $self_comments_array)) {
            $content = new stdClass();
            $content->form_key = $self_dataset[$i]->form_key;
            if ($self_dataset[$i]->form_key == 'achievements_text') {
              $content->question = $self_dataset[$i]->name;
            }
            else {
              $content->question = $item['name'];
            }
            $content->comment = $self_dataset[$i]->DATA;
            array_unshift($self_comments_array, $content);
          }
        }
      }
    }
    else {
      continue;
    }
  }
}

/**
 * synthesize_self_review_ratings_and_comments
 * @param
 * @return
 * */
function synthesize_self_review_ratings_and_comments($self_dataset, $mapping_table) {
  $self_review_content = array();
  $temp_idx = 0;
  for ($i = 0; $i < count($self_dataset); $i++) {
    if (!array_key_exists($i, $self_dataset)) {
      continue;
    }
    if ($self_dataset[$i]->type != 'select' || $self_dataset[$i]->form_key == 'overall_rating') {
      continue;
    }
    $obj = new stdClass();
    $obj->cid = $self_dataset[$i]->cid;
    $obj->form_key = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->form_key;
    $obj->question = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->name;

    $obj->rating = $self_dataset[$mapping_table[$temp_idx]['rating_idx']]->DATA;
    $obj->comment = filter_xss_and_check_plain($self_dataset[$mapping_table[$temp_idx]['comment_idx']]->DATA);
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
function synthesize_all_peer_review_ratings_and_comments(&$nid_pname_array, &$pr_data_array, &$mapping_table, $item_count = -1) {
  $all_peer_review_content = array();
//  $count = count($nid_pname_array);
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
    if ($pr_data_array[$providerName]->dataset[$i]->type != 'select' || $pr_data_array[$providerName]->dataset[$i]->form_key == 'overall_rating') {
      continue;
    }
    $obj = new stdClass();
    $obj->cid = $pr_data_array[$providerName]->dataset[$i]->cid;
    $obj->form_key = $pr_data_array[$providerName]->dataset[$i]->form_key;
    $obj->question = $pr_data_array[$providerName]->dataset[$i]->name;
    $index = $mapping_table[$iterate_counting]['rating_idx'];
    $obj->ratings = $rating_array[$index];
    foreach ($nid_pname_array as $item) {
      $pname = $item['providerName'];
      $obj->ratings[$pname]['comment'] = $comment_array[$mapping_table[$iterate_counting]['comment_idx']][$pname]->comment;
      $obj->ratings[$pname]['isRead'] = $comment_array[$mapping_table[$iterate_counting]['comment_idx']][$pname]->isRead;
    }
    $iterate_counting++;
    array_push($all_peer_review_content, $obj);
  }
  // dd($all_peer_review_content, 'all_peer_review_content');
  return $all_peer_review_content;
}

/**
 * _generate_provider_syn_ratings_array
 * @param array $pr_data_array
 * @param int $count
 * @return array
 * */
function _generate_provider_syn_ratings_array($pr_data_array, $count = -1) {
  if ($count != -1) {
    $rating_array = range(0, $count - 1);
    for ($i = 0; $i < $count; $i++) {
      $rating_array[$i] = array();
    }
  }
  foreach ($pr_data_array as $item) {
    foreach ($item->dataset as $key => $value) {
      if ($value->type == 'select' && $value->DATA !== '') {
        $rating_array[$value->cid][$item->providerName] = array();
        $rating_array[$value->cid][$item->providerName]['nid'] = $item->nid;
        $rating_array[$value->cid][$item->providerName]['rating'] = $value->DATA;
        $rating_array[$value->cid][$item->providerName]['isDisplay'] = $value->isDisplay;
      }
      else if ($value->type == 'select' && $value->DATA == NULL) {
        $rating_array[$value->cid][$item->providerName] = array();
        $rating_array[$value->cid][$item->providerName]['nid'] = $item->nid;
        $rating_array[$value->cid][$item->providerName]['rating'] = 'N/A';
        $rating_array[$value->cid][$item->providerName]['isDisplay'] = $value->isDisplay;
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
function _generate_provider_syn_comments_array($pr_data_array, $count = -1) {
  if ($count != -1) {
    $comment_array = range(0, $count - 1);
    for ($i = 0; $i < $count; $i++) {
      $comment_array[$i] = array();
    }
  }
  foreach ($pr_data_array as $item) {
    foreach ($item->dataset as $key => $value) {
      if ($value->type == 'textarea' && $value->DATA !== '') {
        $comment_array[$value->cid][$item->providerName] = new stdClass();
				$comment_array[$value->cid][$item->providerName]->comment = filter_xss_and_check_plain($value->DATA);
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
    if ($value->form_key == 'overall_rating' && isset($value->rating)) {
      return $value->rating;
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
      $cid_formkey_type_array[$value->cid]['name'] = $value->name;
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
 * @param int $sid
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
        IF (wd.DATA IS NULL, \'\', wd.DATA) AS DATA,
        IF (wd.no IS NULL, \'\', wd.no) AS no
        FROM
          webform_component
        LEFT JOIN (
          SELECT
            DATA,
            sid,
            cid,
            nid,
            no
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
 * get_latest_peer_submitted_data_from_db
 * @param int $nid
 * @param int $sid
 * @return array $dataset
 * */
function get_latest_peer_submitted_data_from_db($nid, $sid) {
  $dataset = db_query('SELECT
      a.cid,
      a.form_key,
      a.name,
      a.type,
      a.pid,
      a.weight,

    IF (a.nid IS NULL, :nid, a.nid) AS nid,
     a.sid,
     a.DATA,
     b.isDisplay
    FROM
      (
        SELECT
          wc.cid,
          wc.form_key,
          wc.name,
          wc.type,
          wc.pid,
          wc.weight,
          wc.nid,

        IF (wd.sid IS NULL, :sid, wd.sid) AS sid,

      IF (wd.DATA IS NULL, \'\', wd.DATA) AS DATA
      FROM
        webform_component wc
      LEFT JOIN (
        SELECT
          DATA,
          cid,
          nid,
          sid
        FROM
          webform_submitted_data
        WHERE
          nid = :nid
        AND sid = :sid
      ) wd ON wc.cid = wd.cid
      WHERE
        wc.nid = :nid
      ORDER BY
        wc.cid
      ) a
    LEFT JOIN prs_rel_peer_comment_display b ON a.cid = b.cid
    WHERE
      a.nid = :nid
    AND a.sid = :sid
    AND (b.nid = :nid OR b.nid IS NULL)', array(':sid' => $sid, ':nid' => $nid))->fetchAll();
  return $dataset;
}

/**
 * insert counselor's submission info to db table prs_counselor_submit_info
 * @param int $nid
 * @return int $csiid
 * */
function _insert_counselor_submittion_info_to_DB($nid, $is_draft = FALSE) {
  $timestamp = time();
  if ($is_draft == FALSE) {
    $csiid = db_insert('prs_counselor_submit_info')
            ->fields(array(
              'nid' => $nid,
              'submitTimestamp' => $timestamp,
              'isDraft' => 0,
            ))->execute();
  }
  else {
    $csiid = db_insert('prs_counselor_submit_info')
            ->fields(array(
              'nid' => $nid,
              'submitTimestamp' => $timestamp,
              'isDraft' => 1,
            ))->execute();
  }
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
    if (isset($value->rating) && in_array($value->rating, $range) && (isset($value->peerAvgRating))) {
      $csdid = db_insert('prs_counselor_submit_data')
              ->fields(array(
                'nid' => $nid,
                'csiid' => $csiid,
                'count' => $key,
                'title' => $value->header,
                'rating' => $value->rating,
                'peer_avg_rating' => $value->peerAvgRating,
                'counselor_comment' => $value->comment,
//                'peer_comments' => $value->revisedComment,
              ))->execute();
      if ($csdid != -1) {
        array_push($csdid_array, $csdid);
      }
      $update_peer_display = update_peer_display($value->revisedComment);
    }
    else if (isset($value->rating) && in_array($value->rating, $range) && (!isset($value->peerAvgRating))) {
      $csdid = db_insert('prs_counselor_submit_data')
              ->fields(array(
                'nid' => $nid,
                'csiid' => $csiid,
                'count' => $key,
                'title' => $value->header,
                'rating' => $value->rating,
                'counselor_comment' => $value->comment,
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
              ))->execute();
      if ($csdid != -1) {
        array_push($csdid_array, $csdid);
      }
    }
  }
  $overall_rating_csdid = db_insert('prs_counselor_submit_data')
          ->fields(array(
            'nid' => $nid,
            'csiid' => $csiid,
            'count' => count($csdid_array),
            'title' => 'overall_rating',
            'rating' => $overall_rating,
          ))->execute();

  if (count($csdid_array) != 0 && $overall_rating_csdid != -1) {
    return $csdid_array;
  }
  else {
    $msg = 'You did not add any content to DB!';
    drupal_set_message($msg, 'status');
  }
}

function update_peer_display($peer_json_array) {

  $where_display = '';
  $where_not_display = '';
  $is_display_first = TRUE;
  $is_not_display_first = TRUE;
  foreach ($peer_json_array as $one_peer_comment) {
    $temp = json_decode($one_peer_comment);
    dd($temp,'$temp');
    $where_one_string = '(nid=' . $temp->nid . ' and cid=' . $temp->cid . ')';
    if ($temp->display == 'true') {
      //set 0
      if ($is_display_first) {

        $where_display.=$where_one_string;
        $is_display_first = FALSE;
      }
      else {
        $where_display.=' or ' . $where_one_string;
      }
    }
    else {
      //set 1
      if ($is_not_display_first) {

        $where_not_display.=$where_one_string;
        $is_not_display_first = FALSE;
      }
      else {
        $where_not_display.=' or ' . $where_one_string;
      }
    }
  }
//  dd($where_display, '$where_display');
//  dd($where_not_display, '$where_not_display');
  if ($where_display != '') {
    $peerdisply_info_true = db_update('prs_rel_peer_comment_display')
        ->fields(array(
          'isDisplay' => 0, //display peer comment
        ))
        ->where($where_display)
        ->execute();
  }
  if ($where_not_display != '') {
    $peerdisply_info_false = db_update('prs_rel_peer_comment_display')
        ->fields(array(
          'isDisplay' => 1, //not display peer comment
        ))
        ->where($where_not_display)
        ->execute();
  }
  return $peerdisply_info_true + $peerdisply_info_false;
}

function _submit_counselor_draft_to_DB($rreid, $nid, &$clor_items, $overall_rating) {
  $is_draft = TRUE;
  $sid = get_last_sid_by_nid($nid);
  $csiid = _insert_counselor_submittion_info_to_DB($nid, $is_draft);
  $csdid_array = _insert_counselor_submittion_data_to_DB($rreid, $nid, $csiid, $clor_items, $overall_rating);

  $provider_table = db_update('prs_rel_provider_employee')
      ->fields(array(
        'isRead' => 1,
      ))
      ->condition('nid', $nid)
      ->execute();

  $provider_array = db_select('prs_rel_provider_employee')
          ->fields('prs_rel_provider_employee', array(
            'providerName'))
          ->condition('rreid', $rreid)
          ->condition('nid', $nid, '!=')
          ->condition('status', 1)
          ->execute()->fetchAll();
  foreach ($provider_array as $key => $value) {
    $provider_info = db_update('prs_rel_provider_employee')
        ->fields(array(
          'isRead' => 1,
        ))
        ->condition('rreid', $rreid)
        ->condition('providerName', $value->providerName)
        ->execute();
  }
  if (isset($csdid_array)) {
    $msg = 'Submission saved. You may return to this form later and it will restore the current values.';
    drupal_set_message($msg, 'status');
    return TRUE;
  }
  else {
    return FALSE;
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
  $provider_array = db_select('prs_rel_provider_employee')
          ->fields('prs_rel_provider_employee', array(
            'providerName'))
          ->condition('rreid', $rreid)
          ->condition('nid', $nid, '!=')
          ->condition('status', 1)
          ->execute()->fetchAll();
  foreach ($provider_array as $key => $value) {
    $provider_info = db_update('prs_rel_provider_employee')
        ->fields(array(
          'isRead' => 1,
        ))
        ->condition('rreid', $rreid)
        ->condition('providerName', $value->providerName)
        ->execute();
  }

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
  $provider_array = db_select('prs_rel_provider_employee')
          ->fields('prs_rel_provider_employee', array(
            'providerName'))
          ->condition('rreid', $rreid)
          ->condition('nid', $nid, '!=')
          ->condition('status', 1)
          ->execute()->fetchAll();
  foreach ($provider_array as $key => $value) {
    $provider_info = db_update('prs_rel_provider_employee')
        ->fields(array(
          'isRead' => 1,
        ))
        ->condition('rreid', $rreid)
        ->condition('providerName', $value->providerName)
        ->execute();
  }
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

function send_approval_email_notification($rreid, $is_approved, $reject_comments = NULL) {
  $provider_info = get_provider_info($rreid);
  if (isset($provider_info)) {
    $counselee_name = $provider_info[0]->employeeName;
    $counselor_name = get_counselor($counselee_name);
    $token = $provider_info[0]->token;
    $mail_receiver_array = array();
    array_push($mail_receiver_array, $counselee_name);
    $email_addr = select_email_from_prs_reviewers($mail_receiver_array);
  }
  if (isset($email_addr)) {
    $address = $email_addr[0]->mail;
  }

  if ($reject_comments != NULL) {
    counselor_audit_review_remind($address, $counselee_name, $counselor_name, $token, $rreid, $is_approved, $reject_comments);
  }
  else {
    counselor_audit_review_remind($address, $counselee_name, $counselor_name, $token, $rreid, $is_approved);
  }
}

function validate_numbers($review_info_array, $index) {
  if (array_key_exists($index, $review_info_array['clientdate']) && array_key_exists($index, $review_info_array['project_roles_and_responsibilities']) && array_key_exists($index, $review_info_array['startdate']) && array_key_exists($index, $review_info_array['enddate'])) {
    $cl_str = $review_info_array['clientdate'][$index]->no;
    $pr_str = $review_info_array['project_roles_and_responsibilities'][$index]->no;

    $cl_pos = strpos($cl_str, '#');
    $cl_num = substr($cl_str, $cl_pos + 1, 1);
    $pr_pos = strpos($pr_str, '#');
    $pr_num = substr($pr_str, $pr_pos + 1, 1);
    if ($cl_num == $pr_num) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  else {
    return FALSE;
  }
}
