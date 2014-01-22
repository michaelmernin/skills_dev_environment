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

function load_relationship(){
	dd($_GET, 'GET');
	$page = $_GET['page'];
	$limit = $_GET['rows'];
	$sidx = $_GET['sidx'];
	$sord = $_GET['sord'];
  $cond = '';
	if (!$sidx){
		$sidx = 1;
	}
  $relation_result = array();
  $params = new stdClass();
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
    $relation_result = search_relationship_from_names($params);
	} else {
		$relation_result = get_all_employee_counselor_gm_relation_info_from_db();
	}
	$result_json = json_encode($relation_result);
	echo $result_json;
}

function get_relationship_from_names($params=NULL){
	if(!isset($params)){
		$result = get_all_employee_counselor_gm_relation_info_from_db();
    return $result;
	} else {
		$flag_a; $flag_b; $flag_c;
		if(isset($params->employeeName)){
			$flag_a = 4;
		} else {
			$flag_a = 0;
		}
		if(isset($params->counselorName)){
			$flag_b = 2;
		} else {
			$flag_b = 0;
		}
		if(isset($params->gmName)){
			$flag_c = 1;
		} else {
			$flag_c = 0;
		}
		$signal = $flag_a | $flag_b | $flag_c;
    switch ($signal){
      case 1:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE gmName LIKE  :gm';
        return db_query($query_statement, array(':gm' => "%" . $params->gmName . "%"))->fetchAll();
        break;
      case 2:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE counselorName LIKE  :counselor';
        return db_query($query_statement, array(':counselor' => "%" . $params->counselorName . "%"))->fetchAll();
        break;
      case 3:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE counselorName LIKE :counselor AND gmName LIKE  :gm';
        return db_query($query_statement, array(':counselor' => "%" . $params->counselorName . "%", ':gm' => "%" . $params->gmName . "%"))->fetchAll();
        break;
      case 4:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee';
        return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%"))->fetchAll();
        break;
      case 5:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE :employee AND gmName LIKE  :gm';
        return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':gm' => "%" . $params->gmName . "%"))->fetchAll();
        break;
      case 6:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee AND counselorName LIKE :counselor';
        return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':counselor' => "%" . $params->counselorName . "%"))->fetchAll();
        break;
      case 7:
        $query_statement = 'SELECT * FROM `prs_rel_employee_counselor_gm` WHERE employeeName LIKE  :employee AND counselorName LIKE :counselor AND gmName LIKE :gm';
        return db_query($query_statement, array(':employee' => "%" . $params->employeeName . "%", ':counselor' => "%" . $params->counselorName . "%", ':gm' => "%" . $params->gmName . "%"))->fetchAll();
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