<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/* 
 * Page Callback function of relationship management.
 */
function manage_relationship(){
	$relation_result = get_all_employee_counselor_gm_relation_info_from_db();
	$result_json = json_encode($relation_result);
	$pageContent = theme('relationship_management', array(
	'roles_json' => $result_json,
	));
	return $pageContent;
}

/*
 *  Page callback of autocomplete
 */
function jqgrid_auto_complete(){
	$POST = $_POST;
	$params = new stdClass();
	$params->query = $_POST['q'];
	$params->page = $_POST['p'];
	$params->count = $_POST['s'];
	$search_result = get_employees_login_name_from_db($params);
	$employee_array =generate_auto_complete_candidates($search_result);
	echo $employee_array;
}

/* 
 *  Page Callback function of adding editing and delete row
 */
function edit_relation_row(){
	$params = $_POST;
	$operation = $_POST['oper'];
	$update_item = new stdClass();
	$update_item->recgid = (array_key_exists('recgid', $_POST))?$_POST['recgid']:'';
	$update_item->employeeName = array_key_exists('employeeName', $_POST)?$_POST['employeeName']:'';
	$update_item->counselorName = $_POST['counselorName'];
	$update_item->gmName = $_POST['gmName'];
	if ($operation == 'del'){
		$update_item->recgid = $_POST['id'];
	}
	switch($operation){
		case 'add':
			$result = update_relationship_into_db($update_item);
			break;
		case 'edit':
			$result = update_relationship_into_db($update_item);
			break;
		case 'del':
			$result = delete_item_from_relation_table($update_item);
		default:
			break;
	}
	echo 1;
}

function load_relationship(){
  $params = new stdClass();
  $params->sidx = $_GET['sidx'];
  $params->sord = $_GET['sord'];
	$params->limit = $_GET['rows'];
	if (!$params->sidx){
		$params->sidx = 1;
	}
  if ($_GET['_search'] == 'true'){
    if (array_key_exists('employeeName', $_GET)) {
      $employeeName = str_replace(" ", ".", $_GET['employeeName']);
      $params->employeeName = $employeeName;
    }
    if (array_key_exists('counselorName', $_GET)){
      $counselorName = str_replace(" ", ".", $_GET['counselorName']);
      $params->counselorName = $counselorName;
    }
    if (array_key_exists('gmName', $_GET)){
      $gmName = str_replace(" ", ".", $_GET['gmName']);
      $params->gmName = $gmName;
    }
    $params->record_count = search_relation_quantity_from_names($params);
    if ($params->record_count > 0){
	    $params->total_page = ceil($params->record_count / $params->limit);
    } else {
	    $params->total_page = 0;
    }
    $params->page = ($_GET['page']>$params->total_page)?$params->total_page:$_GET['page'];
    $params->start = $params->limit * $params->page - $params->limit;
    $result_json = generate_search_result($params);
    } else {
	    $params->record_count = count(search_relation_quantity_from_names());
	    if ($params->record_count>0){
		    $params->total_page = ceil($params->record_count / $params->limit);
		  } else {
			  $params->total_page = 0; 
	    }
	    $params->page = ($_GET['page']>$params->total_page)?$params->total_page:$_GET['page'];
	    $params->start = $params->limit * $params->page - $params->limit;
	    $result_json = generate_search_result($params);
	}
	echo $result_json;
}

function generate_search_result($params){
	if(isset($params->sidx) && $params->sidx != -1){
		$order = ' ' . $params->sidx . ' ' . $params->sord;
	} else {
		$order = '';
	}
	$response = new stdClass();
	$response->page = $params->page;
	$response->total = $params->total_page;
	$response->records = $params->record_count;
	if ($params->start >= 0){
		$range_result = get_relation_from_db($params, $order);
	}
	$count = 0;
	foreach($range_result as $item){
		$response->rows[$count]['cell'] = array($item->recgid, $item->employeeName, $item->counselorName, $item->gmName);
		$count++;
	}
	$range_result_json = json_encode($response);
	return $range_result_json;
}

function get_relation_from_db($params, $order){
	$flag = select_db_statement($params);
	switch ($flag){
		case 1:
			$query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE gmName LIKE :gm ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':gm' => "%" . $params->gmName . "%"))->fetchAll();
      break;
    case 2:
      $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE counselorName LIKE :counselor ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':counselor' => "%" . $params->counselorName . "%"))->fetchAll();
      break;
    case 3:
      $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE counselorName LIKE :counselor AND gmName LIKE :gm ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':counselor' => "%" . $params->counselorName . "%", ':gm' => "%" . $params->gmName . "%"))->fetchAll();
      break;
    case 4:
      $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE :employee ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%"))->fetchAll();
      break; 
    case 5:
      $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE :employee AND gmName LIKE :gm ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':gm' => "%" . $params->gmName . "%"))->fetchAll();
      break;
    case 6:
      $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE :employee AND counselorName LIKE :counselor ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':counselor' => "%" . $params->counselorName . "%"))->fetchAll();
      break;
    case 7:
      $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE :employee AND counselorName LIKE :counselor AND gmName LIKE :gm ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':counselor' => "%" . $params->counselorName . "%", ':gm' => "%" . $params->gmName . "%"))->fetchAll();
      break;
    default:
	    $query_statement = "SELECT * FROM `prs_rel_employee_counselor_gm` ORDER BY $order ,employeeName ASC LIMIT $params->start, $params->limit";
	    return db_query($query_statement)->fetchAll();
      break;
	}
}
function select_db_statement($params){
		$flag_a = 0;
		$flag_b = 0;
		$flag_c = 0;
		if(isset($params->counselorName)){
			$flag_b = 2;
		}
		if(isset($params->employeeName)){
			$flag_a = 4;
		} 
		if(isset($params->gmName)){
			$flag_c = 1;
		}
		$sum = $flag_a+$flag_b+$flag_c;
    return $sum;
}

function search_relation_quantity_from_names($params=NULL){
	if(!isset($params)){
    return get_all_employee_counselor_gm_relation_info_from_db();
  } else {
    $signal = select_db_statement($params);
    switch ($signal){
      case 1:
        $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE gmName LIKE  :gm';
        return db_query($query_statement, array(':gm' => "%" . $params->gmName . "%"))->fetch();
        break;
      case 2:
	      $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE counselorName LIKE  :counselor';
	      return db_query($query_statement, array(':counselor' => "%" . $params->counselorName . "%"))->fetch();
	      break;
      case 3:
	      $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE counselorName LIKE :counselor AND gmName LIKE  :gm'; 
	      return db_query($query_statement, array(':counselor' => "%" . $params->counselorName . "%", ':gm' => "%" . $params->gmName . "%"))->fetch();
	      break;
      case 4:
	      $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee'; 
	      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%"))->fetch();
	      break;
      case 5:
	      $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee AND gmName LIKE  :gm';  
	      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':gm' => "%" . $params->gmName . "%"))->fetch();
	      break;
      case 6:
	      $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee AND counselorName LIKE :counselor';
	      return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':counselor' => "%" . $params->counselorName . "%"))->fetch();
	      break;
      case 7:
        $query_statement = 'SELECT COUNT(*) AS count FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee AND counselorName LIKE :counselor AND gmName LIKE :gm';
        return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':counselor' => "%" . $params->counselorName . "%", ':gm' => "%" . $params->gmName . "%"))->fetch();
        break;
      default:
        break;
    }
  }
}

function get_all_employee_counselor_gm_relation_info_from_db(){
  $result = db_select('prs_rel_employee_counselor_gm')
      ->fields('prs_rel_employee_counselor_gm')
      ->orderBy('employeeName', 'ASC')
      ->execute()->fetchAll();
  return $result;
}

function update_relationship_into_db($updated_item){
  $result = db_merge('prs_rel_employee_counselor_gm')->key(array('recgid' => $updated_item->recgid))
      ->insertFields(array(
	'employeeName' => $updated_item->employeeName,
	'counselorName' => $updated_item->counselorName,
	'gmName' => $updated_item->gmName
      ))
      ->updateFields(array(
	'counselorName' => $updated_item->counselorName,
	'gmName' => $updated_item->gmName
      ))->execute();
  return $result;
}

function generate_auto_complete_candidates($candidates_array){
	$json_array = array();
	$json_array['results'] = array();
	foreach($candidates_array as $candidate){
		$item = array("id"=> $candidate->recgid, "name"=>$candidate->login_name); 
		array_push($json_array['results'], $item);
	}
	$json_array['total'] = count($candidates_array);
	return json_encode($json_array);
}

function get_employees_login_name_from_db($params){
	$start = $params->count * $params->page - $params->count;
	$query_statement = "SELECT
	prs_reviewers.login_name,
	prs_rel_employee_counselor_gm.recgid
FROM
	prs_reviewers
JOIN prs_rel_employee_counselor_gm ON prs_reviewers.login_name = prs_rel_employee_counselor_gm.employeeName
WHERE
	login_name LIKE :employee
ORDER BY
	prs_reviewers.login_name ASC
LIMIT $start, $params->count";
	return db_query($query_statement, array(':employee' => "%" . $params->query . "%"))->fetchAll();
}

function delete_item_from_relation_table($updated_item){
	$number_deleted = db_delete('prs_rel_employee_counselor_gm')->condition('recgid', $updated_item->recgid)->execute();
	return $number_deleted;
}