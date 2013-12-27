<?php

// Define the result_val to specify different status of server validation.
defined('STATUS_OK') or define('STATUS_OK', 1);
defined('INVALID_SELECTION') or define('INVALID_SELECTION', 2);
defined('COMMENT_REQUIRED') or define('COMMENT_REQUIRED', 3);
defined('INVALID_OVERALL') or define('INVALID_OVERALL', 4);
defined('REJECT_COMMENT_REQUIRED') or define('REJECT_COMMENT_REQUIRED', 5);
// end of status of server validation.
// Define the review type.
defined('ANNUAL_REVIEW') or define('ANNUAL_REVIEW', 0);
defined('PROJECT_REVIEW') or define('PROJECT_REVIEW', 1);
defined('3_M_REVIEW') or define('3_M_REVIEW', 2);
// end of defined review type.

/**
 * @file
 * Page callback function for view assessment.
 * */
function load_self_assessment($rreid = NULL) {
	$cur_user_name = get_current_user_name();
	$reviewee = get_reviewee_from_db($rreid);
	$counselor = get_counselor($reviewee);
	$review_status = get_review_status_from_db($rreid);
	$review_basic_info = get_basic_review_information($rreid);
	$review_type = $review_basic_info[0]->type;
	$allowed_roles = AccessRoles::getViewAssessment();
	$allowed_status = AccessStatus::getViewAssessment();

  
    // Accessdenied
  $user_identity = get_user_identity($rreid);
  if (!tab_accessibility($user_identity, $allowed_status, $review_status))
    drupal_goto('accessdenied');
  
//	if (!identify_accessibility($cur_user_name, $reviewee, $counselor, $allowed_roles, $allowed_status, $review_status)) {
//		drupal_goto('accessdenied');
//	}

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
	$review_info_content_num = 0;
	if ($review_type == ANNUAL_REVIEW) {
		_matching_none_selection_items($none_selection_item, $self_dataset, $review_info, $self_comments_array, $overall_rating);
		$review_info_content_num = count($review_info['project_roles_and_responsibilities']);
	}
	else if ($review_type == PROJECT_REVIEW) {
		$review_info = _generate_engagement_and_evaluation($self_dataset, $self_comments_array, $overall_rating);
		$review_info_content_num = 1;
	}
	$item_counting = calculate_counting($self_question_rating_comment_result, $self_comments_array);
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
		for ($i = 0; $i < $item_counting->total_item_count + 1; $i++) {
			$revised_comments[$i] = new stdClass();
		}
		$overall_rating->counselor_rating = 0;
	}

	$pageContent = '';
	appendCSSJS();
	if ($review_type == ANNUAL_REVIEW) {
		for ($i = 0; $i < $review_info_content_num; $i++) {
			$proj_item = generate_project_roles_and_responsibilities_info($review_info, $i);
			if (validate_numbers($review_info, $i)) {
				$content = thematize_project_roles_and_responsibilities($rreid, $nid, $reviewee, $review_type, $proj_item, $i);
				$pageContent .= $content;
			}
		}
	}
	else if ($review_type == PROJECT_REVIEW) {
		$content = thematize_project_roles_and_responsibilities($rreid, $nid, $reviewee, $review_type, $review_info);
		$pageContent .= $content;
	}
	$pageContent .= '<hr>';
	$is_read_only = FALSE;
	$data_count = 0;
	foreach ($self_question_rating_comment_result as $key => $value) {
//=====================the question which have pie chart ============CONST===============================
		if (!isset($value->rating) && !isset($value->comment)) {
			$content = thematize_subtitle($rreid, $nid, $reviewee, $value);
			$pageContent .= $content;
			continue;
		}
		if ($review_type == ANNUAL_REVIEW) {
			$pie_form_key_array = array('technical_abilities', 'consulting_skills', 'professionalism', 'leadership', 'teamwork');
			if (in_array($value->form_key, $pie_form_key_array)) {
				$form_key = $value->form_key . '_category';

				if ($peer_information_set->have_peer_data) {
					$fieldset_cid = get_fieldset_key_in_peer_table($form_key, $peer_information_set->cid_formkey_type_array);
					$children_array = get_children_array_by_cid($fieldset_cid, $peer_information_set->adj_list);
					$peer_name = array();
					$peer_json = '';
					$json_count = 0;
					_manage_data_return_three_array($children_array, $peer_information_set->peer_question_rating_comment_result, $peer_name, $peer_json, $json_count);
					$pie_data = _manage_pie_chart($peer_name, $form_key);
				}
				else {
					$json_count = 0;
					$peer_json = '';
					$pie_data = new stdClass();
					$pie_data->id = $form_key;
					$pie_data->all_avg = NO_DATA;
					$pie_data->have_peer_data = FALSE;
				}
				//=====================pie chart end=====================================================================
				$content = thematize_review_content_full($data_count, $rreid, $nid, $reviewee, $value, $json_count, $pie_data, $peer_json, $revised_comments, $item_counting->total_item_count, $overall_rating, $is_read_only);
				$pageContent .= $content;
				$data_count++;
			}
			else {
				$content = thematize_review_content_without_chart($data_count, $rreid, $nid, $reviewee, $value, $revised_comments, $item_counting->total_item_count, $overall_rating, $is_read_only);
				$pageContent .= $content;
				$data_count++;
			}
		}
		else if ($review_type == PROJECT_REVIEW) {
			if ($peer_information_set->have_peer_data) {
				$peer_name = array();
				$peer_json = array();
				$peer_name_and_rating = _generate_peer_json_data($peer_information_set->peer_question_rating_comment_result, $value->form_key, $peer_json);
				$json_count = count($peer_json);
				$peer_json_context = drupal_json_encode($peer_json);
				$pie_data = _manage_pie_chart($peer_name_and_rating, $value->form_key);
			}
			else {
				$json_count = 0;
				$peer_json_context = '';
				$pie_data = new stdClass();
				$pie_data->id = $value->form_key;
				$pie_data->all_avg = NO_DATA;
				$pie_data->have_peer_data = FALSE;
			}
			$content = thematize_review_content_full($data_count, $rreid, $nid, $reviewee, $value, $json_count, $pie_data, $peer_json_context, $revised_comments, $item_counting->total_item_count, $overall_rating, $is_read_only);
			$pageContent .= $content;
			$data_count++;
		}
	}
	if ($review_type == PROJECT_REVIEW) {
		foreach ($self_comments_array as $key => $value) {
			if ($value->form_key == 'overall_evaluation_category') {
				$content = thematize_subtitle($rreid, $nid, $reviewee, $value);
				$pageContent .= $content;
			}
		}
	}
	foreach ($self_comments_array as $key => $value) {
		if ($review_type == PROJECT_REVIEW) {
			if ($value->form_key == 'overall_evaluation_category') {
				continue;
			}
			$content = thematize_review_content_comment_only($key, $item_counting->self_item_count, $rreid, $nid, $reviewee, $value, $revised_comments, $item_counting->total_item_count, $is_read_only);
			$pageContent .= $content;
		}
		else if ($review_type == ANNUAL_REVIEW) {
			$content = thematize_review_content_comment_only($key, $item_counting->self_item_count, $rreid, $nid, $reviewee, $value, $revised_comments, $item_counting->total_item_count, $is_read_only);
			$pageContent .= $content;
		}
	}
	return $pageContent;
}

function _generate_question_id_from_form_key($cid_formkey_type_array) {
	dd($cid_formkey_type_array, 'cid_formkey');
	$question_array = array();
	foreach ($cid_formkey_type_array as $key => $item) {
		if ($item['type'] == 'select' && $item['form_key'] != 'overall_rating') {
			array_push($question_array, $key);
		}
	}
	return $question_array;
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
function _manage_data_return_three_array($children_array, $peer_question_rating_comment_result, &$peer_name, &$peer_json, &$json_count) {
  $peer_json_array = array();
  $is_first = TRUE;
  $number = 0;
  $peer_name_temp = array();
  foreach ($children_array as $one_child) {
    iterate_peer_qa_result($peer_question_rating_comment_result, $is_first, $number, $peer_json_array, $peer_name_temp, $one_child);
  }
  iterate_peer_name_data($peer_name_temp, $peer_name);
  $json_count = count($peer_json_array);
  $peer_json = drupal_json_encode($peer_json_array);
}

function iterate_peer_qa_result($peer_question_rating_comment_result, &$is_first, &$number, &$peer_json_array, &$peer_name_temp, $child_cid) {
  foreach ($peer_question_rating_comment_result as $item) {
    if (!isset($item->ratings)) {
      continue;
    }
    if (!is_null($child_cid) && ($child_cid == $item->cid)) {
      if ($is_first) {
        foreach ($item->ratings as $key => $one_ratings) {
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
            $title = get_title_form_peer_array($item);
            $number++;
            $peer_json_std = get_peer_json_std($key, $one_ratings['rating'], $one_ratings['comment'], $one_ratings['isDisplay'], $one_ratings['nid'], $item->cid, $title, $number);
            array_push($peer_json_array, $peer_json_std);
          }
          else {
            $peer_name_temp[$key]['flag'] = FALSE;
          }
        }
        $is_first = FALSE;
      }
      else {
        foreach ($item->ratings as $key => $one_ratings) {
          if ($peer_name_temp[$key]['flag']) {
            if ($one_ratings['rating'] != 0) {

              $peer_name_temp[$key]['rating'] = $peer_name_temp[$key]['rating'] + $one_ratings['rating'];
              $peer_name_temp[$key]['count'] = $peer_name_temp[$key]['count'] + 1;
            }
            $title = get_title_form_peer_array($item);
            $number++;
            $peer_json_std = get_peer_json_std($key, $one_ratings['rating'], $one_ratings['comment'], $one_ratings['isDisplay'], $one_ratings['nid'], $item->cid, $title, $number);
            array_push($peer_json_array, $peer_json_std);
          }
        }
      }
    }
  }
}

function iterate_peer_question_answer_result_unique_mapping($peer_question_rating_comment_result, &$peer_name_temp) {
  foreach ($peer_question_rating_comment_result as $item) {
    if (!isset($item->ratings)) {
      continue;
    }
    if (is_array($item->ratings)) {

      foreach ($item->ratings as $key => $value) {
        $peer_name_temp[$key]['flag'] = TRUE;
        if ($value['rating'] != 'N/A') {
          if ($value['rating'] != 0) {
            $peer_name_temp[$key]['rating'] = $value['rating'];
            $peer_name_temp[$key]['count'] = 1;
          }
          else {
            $peer_name_temp[$key]['rating'] = 0;
            $peer_name_temp[$key]['count'] = 0;
          }
          $title = get_title_form_peer_array($item);
          $number++;
          $peer_json_std = get_peer_json_std($key, $value['rating'], $value['comment'], $value['isDisplay'], $value['nid'], $item->cid, $title, $number);
//					dd($peer_json_std, 'peer_json_std');
          array_push($peer_json_array, $peer_json_std);
        }
        else {
          $peer_name_temp[$key]['flag'] = FALSE;
        }
      }
    }
  }
}

function iterate_peer_name_data($peer_name_data_src, &$peer_name) {
  foreach ($peer_name_data_src as $key => $peer_avg) {
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
}

function _generate_peer_json_data(&$peer_question_rating_comment_result, $cur_form_key, &$peer_json) {
	$peer_name_and_rating = array();
	foreach ($peer_question_rating_comment_result as $item) {
		if (!isset($item->ratings)) {
			continue;
		}
		if ($item->form_key == $cur_form_key) {
			$number = 0;
			foreach ($item->ratings as $key => $value) {
				if ($value['rating'] == 'N/A') {
					continue;
				}
				if ($value['rating'] != 'N/A' && $value['rating'] != 0) {
					$peer_name_and_rating[$key] = array();
					$peer_name_and_rating[$key]['avgrating'] = $value['rating'];
					$peer_name_and_rating[$key]['count'] = 1;
					$peer_name_and_rating[$key]['allrating'] = $value['rating'];
					++$number;
				}
				else if ($value['rating'] == 0) {
					$peer_name_and_rating[$key] = array();
					$peer_name_and_rating[$key]['avgrating'] = 0;
					$peer_name_and_rating[$key]['count'] = 0;
					$peer_name_and_rating[$key]['allrating'] = 0;
					++$number;
				}
				$peer_json_std = get_peer_json_std($key, $value['rating'], $value['comment'], $value['isDisplay'], $value['nid'], $item->cid, get_title_form_peer_array($item), $number);
				array_push($peer_json, $peer_json_std);
			}
		}
	}
	return $peer_name_and_rating;
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

function get_peer_json_std($peer_name, $rating, $comment, $display, $nid, $cid, $title, $number) {
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
  $result_val = STATUS_OK;
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
  $result_val = STATUS_OK;
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $overall_rating = $_POST['overallRating'];

  $is_approval = TRUE;
  $decoded_items = decode_json_object($items);
  $result_val = counselor_sv_validation($decoded_items, $overall_rating, $is_approval);
  if ($result_val != STATUS_OK) {
    echo $result_val;
  }
  else {
    $result = _submit_counselor_approve_info_to_DB($rreid, $nid, $decoded_items, $overall_rating);
    $is_approved = TRUE;
    send_approval_email_notification($rreid, $is_approved);
    echo $result_val;
  }
}

/**
 * reject_counselor_assessment
 * @file
 * Page callback function for reject counselee assessment
 * */
function reject_counselee_assessment() {
  $result_val = STATUS_OK;
  $rreid = $_POST['rreid'];
  $nid = $_POST['nid'];
  $items = $_POST['items'];
  $reject_comments = $_POST['rejectComments'];
  $overall_rating = $_POST['overallRating'];

  $is_approval = FALSE;
  $decoded_items = decode_json_object($items);
  $result_val = counselor_sv_validation($decoded_items, $overall_rating, $is_approval, $reject_comments);
  if ($result_val != STATUS_OK) {
    echo $result_val;
  }
  else {
    $result = _append_counselor_reject_info_to_DB($rreid, $nid, $decoded_items, $reject_comments, $overall_rating);
    $is_approved = FALSE;
    send_approval_email_notification($rreid, $is_approved, $reject_comments);
    echo $result_val;
  }
}

function counselor_sv_validation($decoded_data, $overall_rating, $is_approval, $reject_comments = NULL) {
  $result_val = STATUS_OK;
  $rating_range = array(0, 1, 2, 3, 4, 5);
  $error_msg = '';
  foreach ($decoded_data as $key => $value) {
    if (isset($value->rating)) {
      if (!in_array($value->rating, $rating_range)) {
        $result_val = INVALID_SELECTION;
        $error_msg = 'Invalid rating. Rating selection has to be in the range of 0 to 5.';
        break;
      }
      else if ($value->rating != 3) {
        if (!isset($value->comment)) {
          $result_val = COMMENT_REQUIRED;
          $error_msg = 'The rating is not 3 and the corresponding comment is required.';
          break;
        }
      }
    }
  }
  if (!in_array($overall_rating, $rating_range)) {
    $result_val = INVALID_OVERALL;
    $error_msg = 'Invalid overall rating. Overall rating has to be in the range of 1 to 5.';
  }
  if ((is_approval == FALSE) && (is_NULL($reject_comments) == TRUE)) {
    $result_val = REJECT_COMMENT_REQUIRED;
    $error_msg = 'Reject comments is required.';
  }
  drupal_set_message($error_msg, 'error');
  return $result_val;
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
    if (isset($value->weight)) {
      $temp_array[$key] = $value->weight;
    }
  }
  $reordered_array = _sort_readjust_index($temp_array);
  $result = array();
  foreach ($self_question_rating_comment_result as $key => $value) {
    if (isset($value->weight)) {
      if ($value->weight != $reordered_array[$key]) {
        $item = find_item($self_question_rating_comment_result, 'weight', $reordered_array[$key]);
        array_push($result, $item);
      }
      else {
        array_push($result, $value);
      }
    }
    else {
      array_push($result, $value);
    }
  }
  return $result;
}

function _sort_readjust_index($src_array) {
  if (is_null($src_array)) {
    return NULL;
  }
  foreach ($src_array as $key => $value) {
    $cmp = $value;
    for ($j = $key; $j > 0; $j--) {
      if (!array_key_exists($j - 1, $src_array))
        break;
      if ($src_array[$j - 1] > $cmp) {
        $src_array[$j] = $src_array[$j - 1];
      }
      else
        break;
    }
    $src_array[$j] = $cmp;
  }
  return $src_array;
}

function find_item($search_array, $search_item, $target_val) {
  if (is_null($search_array)) {
    return NULL;
  }
  foreach ($search_array as $key => $value) {
    if (isset($value->$search_item)) {
      if ($value->$search_item == $target_val) {
        return $value;
      }
    }
  }
  return NULL;
}

function _fetch_all_none_selection_items($self_cid_formkey_type_array) {
  $survey_item_question = array();
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

function generate_project_roles_and_responsibilities_info($review_info, $index) {
  $content = new stdClass();
  $content->clientdate = $review_info['clientdate'][$index]->DATA;
  $content->project_roles_and_responsibilities = filter_xss_and_check_plain($review_info['project_roles_and_responsibilities'][$index]->DATA);
  $content->startdate = $review_info['startdate'][$index]->DATA;
  $content->enddate = $review_info['enddate'][$index]->DATA;

  return $content;
}

function thematize_project_roles_and_responsibilities($rreid, $nid, $reviewee, $review_type, $project_item, $index = NULL) {
  if ($review_type == ANNUAL_REVIEW) {
    $content = theme('project_roles_and_responsibilities', array(
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'review_info_content_num' => $index,
      'clientdate' => $project_item->clientdate,
      'project_roles_and_responsibilities' => $project_item->project_roles_and_responsibilities,
      'startdate' => $project_item->startdate,
      'enddate' => $project_item->enddate,
    ));
  }
  else if ($review_type == PROJECT_REVIEW) {
    $content = theme('project_roles_and_responsibilities', array(
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'project' => $project_item['Project'],
      'client' => $project_item['Client'],
      'project_roles_and_responsibilities' => $project_item['Project Roles and Responsibilities'],
      'startdate' => $project_item['StartDate'],
      'enddate' => $project_item['EndDate'],
    ));
  }

  return $content;
}

function thematize_subtitle($rreid, $nid, $reviewee, $value) {
  $content = theme('review_subtitle', array(
    'rreid' => $rreid,
    'nid' => $nid,
    'reviewee' => $reviewee,
    'subtitle' => $value,
  ));
  return $content;
}

function thematize_review_content_full($key, $rreid, $nid, $reviewee, $value, $json_count = NULL, $pie_data, $peer_json, $revised_comments, $total_item_count, $overall_rating, $is_read_only) {
  if ($is_read_only == FALSE) {
    $content = theme('view_assessment', array(
      'item_num' => $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_dataset' => $value,
      'json_count' => $json_count,
      'pie_data' => $pie_data,
      'peer_json' => $peer_json,
      'clor_rating_comment' => $revised_comments[$key],
      'total_item_count' => $total_item_count,
      'overall_rating' => $overall_rating,
    ));
  }
  else {
    $content = theme('review_content', array(
      'item_num' => $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_dataset' => $value,
      'pie_data' => $pie_data,
      'peer_json' => $peer_json,
      'clor_rating_comment' => $revised_comments[$key],
      'total_item_count' => $total_item_count,
      'overall_rating' => $overall_rating,
    ));
  }
  return $content;
}

function thematize_review_content_without_chart($key, $rreid, $nid, $reviewee, $value, $revised_comments, $total_item_count, $overall_rating, $is_read_only) {
  if ($is_read_only == FALSE) {
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
  }
  else {
    $content = theme('review_content_without_chart', array(
      'item_num' => $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_dataset' => $value,
      'clor_rating_comment' => $revised_comments[$key],
      'total_item_count' => $total_item_count,
      'overall_rating' => $overall_rating,
    ));
  }
  return $content;
}

function thematize_review_content_comment_only($key, $self_item_count, $rreid, $nid, $reviewee, $value, $revised_comments, $total_item_count, $is_read_only) {
  if ($is_read_only == FALSE) {
    $content = theme('view_assessment_comment_only', array(
      'item_num' => $self_item_count + $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_comment' => $value,
      'clor_rating_comment' => $revised_comments[$self_item_count + $key],
      'total_item_count' => $total_item_count,
    ));
  }
  else {
    $content = theme('review_content_comment_only', array(
      'item_num' => $self_item_count + $key,
      'rreid' => $rreid,
      'nid' => $nid,
      'reviewee' => $reviewee,
      'self_comment' => $value,
      'clor_rating_comment' => $revised_comments[$self_item_count + $key],
      'total_item_count' => $total_item_count,
    ));
  }
  return $content;
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
            $content->comment = filter_xss_and_check_plain($self_dataset[$i]->DATA);
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

function _generate_engagement_and_evaluation($self_dataset, &$self_comments_array, &$overall_rating) {
  $review_info = array();
  $review_info['Client'] = '';
  $review_info['Project'] = '';
  $review_info['StartDate'] = '';
  $review_info['EndDate'] = '';
  $review_info['Project Roles and Responsibilities'] = '';

  $self_comments_array = array();
//	$self_comments_array['Overall Evaluation'] = '';
//	$self_comments_array['Differentiators'] = '';
//	$self_comments_array['Opportunities for Improvement'] = '';
//	$self_comments_array['Summary'] = '';
  for ($i = count($self_dataset) - 1; $i >= 0; $i--) {
    if ($self_dataset[$i]->form_key == 'project_roles_and_responsibilities' && $self_dataset[$i]->type == 'textarea') {
      $review_info['Project Roles and Responsibilities'] = $self_dataset[$i]->DATA;
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'project' && $self_dataset[$i]->type == 'textfield') {
      $review_info['Project'] = $self_dataset[$i]->DATA;
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'client' && $self_dataset[$i]->type == 'textfield') {
      $review_info['Client'] = $self_dataset[$i]->DATA;
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'start_date' && $self_dataset[$i]->type == 'textfield') {
      $review_info['StartDate'] = $self_dataset[$i]->DATA;
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'end_date' && $self_dataset[$i]->type == 'textfield') {
      $review_info['EndDate'] = $self_dataset[$i]->DATA;
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'overall_evaluation_category' && $self_dataset[$i]->type == 'fieldset') {
      $temp = new stdClass();
      $pattern = '3. ';
      $temp->form_key = $self_dataset[$i]->form_key;
      $temp->question = str_replace($pattern, '', $self_dataset[$i]->name);
      $temp->comment = $self_dataset[$i]->DATA;
      array_push($self_comments_array, $temp);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'opportunities_for_improvement_text' && $self_dataset[$i]->type = 'textarea') {
      $temp = new stdClass();
      $temp->form_key = $self_dataset[$i]->form_key;
      $temp->question = $self_dataset[$i]->name;
      $temp->comment = $self_dataset[$i]->DATA;
      array_push($self_comments_array, $temp);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'differentiators_text' && $self_dataset[$i]->type == 'textarea') {
      $temp = new stdClass();
      $temp->form_key = $self_dataset[$i]->form_key;
      $temp->question = $self_dataset[$i]->name;
      $temp->comment = $self_dataset[$i]->DATA;
      array_push($self_comments_array, $temp);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'summary' && $self_dataset[$i]->type == 'textarea') {
      $temp = new stdClass();
      $temp->form_key = $self_dataset[$i]->form_key;
      $temp->question = $self_dataset[$i]->name;
      $temp->comment = $self_dataset[$i]->DATA;
      array_push($self_comments_array, $temp);
      continue;
    }
    else if ($self_dataset[$i]->form_key == 'overall_rating' && $self_dataset[$i]->type == 'select') {
      $overall_rating->counselee_rating = $self_dataset[$i]->DATA;
      continue;
    }
    else {
      continue;
    }
  }
  return $review_info;
}

/**
 * synthesize_self_review_ratings_and_comments
 * @param
 * @return
 * */
function synthesize_self_review_ratings_and_comments($self_dataset, $mapping_table) {
  $self_review_content = array();
  foreach ($mapping_table as $key => $value) {
    $obj = new stdClass();
    if (isset($value->index)) {
      $obj->cid = $self_dataset[$value->index]->cid;
      $obj->form_key = $self_dataset[$value->index]->form_key;
      $obj->question = $self_dataset[$value->index]->name;

      array_push($self_review_content, $obj);
    }
    else if (isset($value->rating_idx)) {
      $obj->cid = $self_dataset[$value->rating_idx]->cid;
      $obj->form_key = $self_dataset[$value->rating_idx]->form_key;
      $obj->question = $self_dataset[$value->rating_idx]->name;

      $obj->rating = $self_dataset[$value->rating_idx]->DATA;
      $obj->comment = filter_xss_and_check_plain($self_dataset[$value->comment_idx]->DATA);
      $obj->weight = $self_dataset[$value->rating_idx]->weight;

      array_push($self_review_content, $obj);
    }
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
  $indexed_pr_data = index_provider_data($pr_data_array);
  foreach ($mapping_table as $key => $value) {
    $obj = new stdClass();
    if (isset($value->index)) {
      $obj->cid = $indexed_pr_data[$providerName]->dataset[$value->index]->cid;
      $obj->form_key = $indexed_pr_data[$providerName]->dataset[$value->index]->form_key;
      $obj->question = $indexed_pr_data[$providerName]->dataset[$value->index]->name;
      array_push($all_peer_review_content, $obj);
    }
    else if (isset($value->rating_idx)) {
      $obj->cid = $indexed_pr_data[$providerName]->dataset[$value->rating_idx]->cid;
      $obj->form_key = $indexed_pr_data[$providerName]->dataset[$value->rating_idx]->form_key;
      $obj->question = $indexed_pr_data[$providerName]->dataset[$value->rating_idx]->name;
      $index = $value->rating_idx;
      $obj->ratings = $rating_array[$index];
      foreach ($nid_pname_array as $item) {
        $pname = $item['providerName'];
        $obj->ratings[$pname]['comment'] = $comment_array[$value->comment_idx][$pname]->comment;
        $obj->ratings[$pname]['isRead'] = $comment_array[$value->comment_idx][$pname]->isRead;
      }
      array_push($all_peer_review_content, $obj);
    }
  }

  return $all_peer_review_content;
}

function index_provider_data($src_pr_data_array) {
  if (is_null($src_pr_data_array)) {
    return NULL;
  }
  $indexed_pr_data = array();
  foreach ($src_pr_data_array as $value) {
    $item = new stdClass();
    $item->nid = $value->nid;
    $item->sid = $value->sid;
    $item->providerName = $value->providerName;
    $indexed_array = array();
    foreach ($value->dataset as $key => $val) {
      $cid = $val->cid;
      if (!array_key_exists($cid, $indexed_array)) {
        $indexed_array[$cid] = '';
      }
      $indexed_array[$cid] = $val;
    }
    $item->dataset = $indexed_array;
    $item->isRead = $value->isRead;
    $providerName = $value->providerName;
    if (!array_key_exists($providerName, $indexed_pr_data)) {
      $indexed_pr_data[$providerName] = '';
    }
    $indexed_pr_data[$providerName] = $item;
  }
  return $indexed_pr_data;
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
    $name = $item->name;
    $type = $item->type;
    $pid = $item->pid;
    $temp = array('form_key' => $form_key, 'name' => $name, 'type' => $type, 'pid' => $pid);
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
  $parent_formkey = 'performance_evaluation_category';
  $parent_cid = 0;
  for ($i = 0; $i < $count; $i++) {
    if (!array_key_exists($i, $src_array)) {
      continue;
    }
    if ($src_array[$i]['type'] == 'fieldset' && $src_array[$i]['form_key'] == 'performance_evaluation_category') {
      $parent_cid = $i;
      continue;
    }
    if ($src_array[$i]['type'] == 'textarea' && strpos($src_array[$i]['form_key'], '_comment') !== FALSE) {
      $comment_array[$i] = array();
      $comment_array[$i] = $src_array[$i];
      unset($src_array[$i]);
    }
  }
  for ($i = 0; $i < $max; $i++) {
    if (!array_key_exists($i, $dfs_tree) || !array_key_exists($dfs_tree[$i], $src_array)) {
      continue;
    }
    if ($src_array[$dfs_tree[$i]]['type'] == 'fieldset' && $src_array[$dfs_tree[$i]]['pid'] == $parent_cid) {
      $temp = new stdClass();
      $temp->index = $dfs_tree[$i];
      $temp->form_key = $src_array[$dfs_tree[$i]]['form_key'];
      $temp->name = $src_array[$dfs_tree[$i]]['name'];
      array_push($qa_array, $temp);
      continue;
    }
    if ($src_array[$dfs_tree[$i]]['type'] == 'select') {
      foreach ($comment_array as $key => $comment) {
        if (strpos($comment['form_key'], $src_array[$dfs_tree[$i]]['form_key']) !== FALSE) {
          $temp = new stdClass();
          $temp->rating_idx = $dfs_tree[$i];
          $temp->comment_idx = $key;
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

function calculate_counting($self_question_rating_comment_result, $self_comments_array) {
  $item_counting = new stdClass();
  $self_item_count = 0;
  $total_item_count = 0;
  foreach ($self_question_rating_comment_result as $key => $value) {
    if (isset($value->rating)) {
      $self_item_count++;
    }
  }
  $total_item_count = $self_item_count + count($self_comments_array);
  $item_counting->self_item_count = $self_item_count;
  $item_counting->total_item_count = $total_item_count;

  return $item_counting;
}

