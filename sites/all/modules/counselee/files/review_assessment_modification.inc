<?php

/**
 * @file 
 * Page callback function for view assessment.
 * */
function load_self_assessment($rreid=NULL) {
  access_authen($rreid);
  $reviewee = get_reviewee_from_db($rreid);
  $provider_info = get_provider_info($rreid, $reviewee);
  $nid = $provider_info[0]->nid;
  $sid = get_last_sid_by_nid($nid);
  ////////////////////////////////////////////////////////
  // Item below is for self assessment
  ////////////////////////////////////////////////////////
  $self_dataset = get_latest_submitted_data_from_db($nid, $sid);
  $item_count = count($self_dataset);
  $self_dataset = generate_selfassessment_result_array($self_dataset);
  $rating_qa_sheet = _generate_rating_qa_sheet($self_dataset);
  $self_question_rating_comment_result = synthesize_self_review_ratings_and_comments();
  ////////////////////////////////////////////////////////
  // Item below is for peers
  ////////////////////////////////////////////////////////
  $providerInfo_table = get_all_providers_info_from_db($rreid);
  $nid_pname_sid_array = _fetch_provider_nodeID_and_submissionID($providerInfo_table);
  $pr_data_array = _get_provider_review_content($nid_pname_sid_array);
  $cid_formkey_type_array = array();
  $adj_list = array();
  $mapping_table = reorder_form_contents($pr_data_array, $cid_formkey_type_array, $adj_list);
  // dd($cid_formkey_type_array, 'cid_formkey_type_array');
  // dd($adj_list, 'adj_list');
  $peer_question_rating_comment_result = synthesize_all_peer_review_ratings_and_comments($rreid, $nid_pname_sid_array, $pr_data_array, $mapping_table, $item_count);
  ////////////////////////////////////////////////////////
 // Just for display
  // dd($peer_question_rating_comment_result, 'q_r_c result');
  // dd($self_question_rating_comment_result, 'self_question_rating_comment_result');
  $pageContent = '';
  foreach ($self_question_rating_comment_result as $key => $value) {
     $temp = theme('view_assessment', array(
      'item_num' => $value->item_num,
      'rreid'     => $value->rreid,
      'nid'       => $value->nid,
      'reviewee'  => $value->reviewee,
      'self_dataset'  => $value->self_dataset,
    ));
     $pageContent .= $temp;
  
  }
  return $pageContent;
}

/**
  * Page callback function of submit counselor's rating and comments.
**/
function submit_counselor_assessment() {
  $result_val = '1';
  $nid = $_POST['nid'];
  $headerInfo = $_POST['headerInfo'];
  $ratings = $_POST['ratings'];
  $comments = $_POST['comments'];
  $headerInfo = _generate_headerInfo($headerInfo);
  $data_array = _generate_counselor_submition_data($headerInfo, $ratings, $comments);
  $csiid = _insert_counselor_submittion_info_to_DB($nid);
  if ($csiid == -1) {
    $msg = 'Inset submittion info failed!';
    drupal_set_message($msg, 'error');
    return;
  }
  $csdid_array = _insert_counselor_submittion_data_to_DB($nid, $csiid, $data_array);
  $count = count($csdid_array);
  if ($count != 0) {
    $msg = 'You have submitted your ratings and comments for your conselee.';
    drupal_set_message($msg, 'status');
  }
  // send_counselor_reviewed_notification($nid, $reviewee);
  echo $rval;
}

/**
 * reject_counselor_assessment
 * @file
 * Page callback function for reject counselee assessment
**/
function reject_counselee_assessment() {
  $result_val = 1;
  $nid = $_POST['nid'];
  $count = $_POST['count'];
  $headerInfo = $_POST['headerInfo'];
  $ratings = $_POST['ratings'];
  $comments = $_POST['comments'];
  $reject_comments = $_POST['rejectComments'];

  $result = _append_counselor_reject_info_to_DB($nid, $reject_comments);

}

function display_questions_ratings_and_comments($peer_question_rating_comment_result) {
  foreach ($peer_question_rating_comment_result as $key => $value) {
    $content = theme('view_assessment_item', array(
      'item_num' => $key+1,
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
**/
function _fetch_provider_nodeID_and_submissionID($data_src) {
	$nid_pName_sid_array = array();
	foreach ($data_src as $item) {
		$providerName = $item->providerName;
		$nid = $item->nid;
		$sid = get_last_sid_by_nid($nid); 
		$temp = array(
			'providerName' => $providerName,
			'nid' => $nid,
			'sid' => $sid,
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
**/
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
		$provider_submission_data_set[$providerName] = $objInstance;
	}
	return $provider_submission_data_set;
}

/**
 * get_counselor_latest_submissionID
 * @param int $nid 
 * @return int $csiid
**/
function get_counselor_latest_submissionID($nid) {
	$query = db_select('prs_counselor_submit_info', 'pcsi')
			->fields('pcsi', array('csiid'))
			->condition('pcsi.nid', $nid)
			->orderBy('pcsi.submitTimestamp', 'DESC')
			->execute()->fetchAll();
	return $query[0]->csiid;
}

/**
 * get_synthesized_provider_comment_from_db
 * @param int $nid 
 * @param int $csiid
 * @return array 
**/
function get_synthesized_provider_comment_from_db($nid, $csiid) {
	$query = db_select('prs_counselor_submit_data', 'pcsd')
			->fields('pcsd', array('count', 'title', 'peer_comments'))
			->condition('pcsd.nid', $nid)
			->condition('pcsd.csiid', $csiid)
			->orderBy('pcsd.count')
			->execute()->fetchAll();
	return $query;
}

/**
  * synthesize_self_review_ratings_and_comments
  * @param
  * @return 
**/
function synthesize_self_review_ratings_and_comments() {
  $result = array();
  for($i = 0; $i < 12; $i++) {
    $temp = new stdClass();
    $temp->item_num = 'num';
    $temp->rreid = 'rreid';
    $temp->nid = 'nid';
    $temp->reviewee = 'reviewee';
    $temp->self_dataset = 'dataset';
    array_push($result, $temp);
    // 'item_num' => $value->$i,
    //   'rreid'     => $value->rreid,
    //   'nid'       => $value->nid,
    //   'reviewee'  => $value->reviewee,
    //   'self_dataset'  => $value->self_dataset
  }
  return $result;
}

/**
 * synthesize_all_peer_review_ratings_and_comments
 * @param int $rreid
 * @param array by reference $nid_pname_array
 * @param array by reference $pr_data_array
 * @param array by reference $mapping_table
 * @param int $item_count
 * @return array 
**/
function synthesize_all_peer_review_ratings_and_comments($rreid, &$nid_pname_array, &$pr_data_array, &$mapping_table, $item_count) {
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
    $obj->question = array();
    $obj->cid = $pr_data_array[$providerName]->dataset[$i]->cid;
    $obj->form_key = $pr_data_array[$providerName]->dataset[$i]->form_key;
    $obj->question['title'] = $pr_data_array[$providerName]->dataset[$i]->name;

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
**/
function _generate_provider_syn_ratings_array($pr_data_array, $count) {
	$rating_array = range(0, $count-1);
	for($i = 0; $i < $count; $i++) {
		$rating_array[$i] = array();
	}
	foreach($pr_data_array as $item) {
		for($i = 0; $i < $count; $i++) {
			if ($item->dataset[$i]->type == 'select' && $item->dataset[$i]->DATA != 'NIL') {
				$rating_array[$item->dataset[$i]->cid][$item->providerName] = $item->dataset[$i]->DATA;
			}
			else {
				continue;
			}
		}
	}
	return $rating_array;
}

/**
 * _generate_provider_syn_comments_array
 * @param array $pr_data_array
 * @param int $count
 * @return array 
**/
function _generate_provider_syn_comments_array($pr_data_array, $count) {
	$comment_array = range(0, $count-1);
  for($i = 0; $i < $count; $i++) {
    $comment_array[$i] = array();
  }
  foreach($pr_data_array as $item) {
    for($i = 0; $i < $count; $i++) {
      if ($item->dataset[$i]->type == 'textarea' && $item->dataset[$i]->DATA != 'NIL') {
        $comment_array[$item->dataset[$i]->cid][$item->providerName] = $item->dataset[$i]->DATA;
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
**/
function _fetch_cid_formkey_from_dataset($data_src) {
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
**/
function reorder_form_contents($pr_data_array, &$cid_formkey_type_array, &$adj_list) {
  /////////////////////////////////////
  // $cid_formkey_type mapping ralations
  /////////////////////////////////////
  $cid_formkey_type_array = _fetch_cid_formkey_from_dataset($pr_data_array);
  // dd($cid_formkey_type_array, 'cid_formkey_type_array');
  /////////////////////////////////////
  // $adj_list adjacent list
  /////////////////////////////////////
  $adj_list = dfs_generate_adjacent_list($cid_formkey_type_array);
  // dd($adj_list, 'adj_list');
  $sequential_list = dfs_travel_all_node($adj_list);
  $mapping_table = adjust_question_and_answer($cid_formkey_type_array, $sequential_list);
  // for($i = 0; $i < count($sequential_list); $i++) {

  // }
  return $mapping_table;
}

/**
 * adjust_question_and_answer
 * matching questions, ratings and answers
 * @param array $src_array
 * @param array $dfs_tree
 * @return array $qa_array
**/
function adjust_question_and_answer($src_array, $dfs_tree) {
  $qa_array = array();
  $comment_array = array();
  $count = count($src_array);
  for($i = 0; $i < $count; $i++) {
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
  for($i = 0; $i < count($dfs_tree); $i++) {
    if (!array_key_exists($dfs_tree[$i], $src_array)) {
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
**/
//////////////////////////////////////////////////////////////////////////////////////////////

/**
 * dfs_generate_adjacent_list
 * generate the adjacent list which represents a tree datastructure. Each subarray stands for all the children nodes of their index node
 * @param array $cid_formkey_type_array
 * @return array $adj_list
**/
function dfs_generate_adjacent_list($cid_formkey_type_array) {
  $cid_index = array_keys($cid_formkey_type_array);
  $adj_list = array();
  foreach ($cid_index as $item) {
    $adj_list[$item] = array();
  }
  for($i = 0; $i < count($cid_index); $i++) {
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
**/
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
**/
function dfs_push(&$target, $source) {
  for ($i = count($source)-1; $i >= 0; $i--) {
    array_push($target, $source[$i]);
  }
}
//////////////////////////////////////////////////////////////////////////////////////////////

/**
 * get_latest_submitted_data_from_db
 * @param int $nid
 * @return array $dataset,data which contains $cid, $sid, $pid, $form_key, $type, $weight, $name, $data.
 * if you would like to accuire additional data from here, feel free to adjust the db_query statement.
**/
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
          webform_component.cid', 
    array(':sid'=> $sid, ':nid' => $nid ))->fetchAll();
  return $dataset;
}

/**
 * insert counselor's submission info to db table prs_counselor_submit_info
 * @param int $nid
 * @return int $csiid
**/
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
**/
function _insert_counselor_submittion_data_to_DB($nid, $csiid, &$data_array) {
  $csdid_array = array();
  foreach ($data_array as $data) {
    $csdid = db_insert('prs_counselor_submit_data')
          ->fields(array(
            'nid' => $nid,
            'csiid' => $csiid,
            'count' => $data['count'],
            'header' => $data['header'],
            'rating' => $data['rating'],
            'comment' => $data['comment'],
            ))->execute();
    if ($csdid != -1) {
      array_push($csdid_array, $csdid);
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

/**
 * _append_counselor_reject_info_to_DB 
 * @param int $nid
 * @param string by referen $reject_comments
 * @return bool 
**/
function _append_counselor_reject_info_to_DB($nid, &$reject_comments) {
  $sid = get_last_sid_by_nid($nid);
  $result1 = db_update('prs_rel_provider_employee')
          ->fields(array(
            'status' => 0,
            'isRead' => 1,
            'isCounselorApproved' => 2,
            'deniedInfo' => $reject_comments,
            ))
          ->condition('nid', $nid)
          ->execute();
  $result2 = db_update('webform_submissions')
          ->fields(array(
            'is_draft' => 1,))
          ->condition('nid', $nid)
          ->condition('sid', $sid)
          ->execute();
  if (($result1 != 0) && ($result2 != 0)) {
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
**/
function _generate_counselor_submition_data(&$headerInfo, &$ratings, &$comments) {
  $target_array = array() ;
  for($i = 0; $i < 24; $i++) {
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
	foreach($src_array as $item) {
		if ($item['identifier'] == 2) {
			array_push($target_array, $item);
		}
	}
	return $target_array;
}

/**
  *This func is temporarily used to map the header and its result of the survey by hard code.
  *It will be replaced later.
**/
function generate_selfassessment_result_array($qa_table) {
  $target = array();
  for ($i = 0; $i < count($qa_table); $i++) {
/*    if ($qa_table[$i]->cid == 'NIL') {
      continue;
    }
*/    $key = $qa_table[$i]->cid;
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