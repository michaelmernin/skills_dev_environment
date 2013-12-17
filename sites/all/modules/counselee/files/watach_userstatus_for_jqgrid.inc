<?php

/**
 * @file
 * Page callback file for the counselee module.
 */
function show_status_table() {
  appendCSSJS();
//  dd($_POST,'$_POST');
//  dd($_GET,'$_GET');
//  dd($_REQUEST,'$_REQUEST');
  switch ($_GET['q']) {
    case 'userstatus':
      $json_data = get_user_status_table_data($_GET);
      break;

    default :
      $json_data = '';
      break;
  }
  echo $json_data;
}

/**
 * get user status table's data.
 * @param type $get_data
 * @return type
 * @author Mars
 */
function get_user_status_table_data($get_data) {
  $page = $get_data['page']; // get the requested page
  $limit = $get_data['rows']; // get how many rows we want to have into the grid
  $sidx = $get_data['sidx']; // get index row - i.e. user click to sort
  $sord = $get_data['sord']; // get the direction
  if (!$sidx) {
    $sidx = -1;
  }
//  dd($sidx, '$sidx');
  /**
   * total page count number
   */
  $cur_user_name = get_current_user_name();
  $my_counselee_names = get_counselees_from_db_with_username($cur_user_name);
  $records_count = get_user_status_table_page_count($cur_user_name, $my_counselee_names);
  if ($records_count > 0) {
    $total_pages = ceil($records_count / $limit);
  }
  else {
    $total_pages = 0;
  }
  if ($page > $total_pages) {
    $page = $total_pages;
  }
  $start = $limit * $page - $limit; // do not put $limit*($page - 1), start index
//  dd($total_pages,'$total_pages');

  $json_data = get_user_status_table_json($page, $total_pages, $records_count, $sidx, $sord, $start, $limit, $cur_user_name, $my_counselee_names);
  return $json_data;
}

/**
 * get data from database
 * @return type
 */
function get_user_status_table_json($page, $total_pages, $records_count, $sidx, $sord, $start, $limit, $cur_user_name, $my_counselee_names) {
  if ($sidx != -1) {
    $orderby = ' ' . $sidx . ' ' . $sord . ',';
  }
  else {
    $orderby = '';
  }
//  dd($orderby, '$orderby');
  $responce = new stdClass();
  $responce->page = $page;
  $responce->total = $total_pages;
  $responce->records = $records_count;
  /**
   * get user status data from db (include counselor data and counselee data)
   */
  $results = get_user_data_db($start, $limit, $cur_user_name, $my_counselee_names, $orderby);

  $i = 0;
//  dd($results, '$results');
  foreach ($results as $result) {
//    $responce->rows[$i]['id'] = $row[id];
//    $responce->rows[$i]['id'] = $i;
    $counselee_name = format_login_name($result->employeeName);
    $period_from = unixTimestampToDateArray($result->period_from);
    $period_from_format = date_to_format($period_from);
    $period_to = unixTimestampToDateArray($result->period_to);
    $period_to_format = date_to_format($period_to);
    $type_string = get_review_type_string($result->type);
    $status_string = get_review_status_string($result->status);
//    dd($result,'$result');
    $responce->rows[$i]['cell'] = array($counselee_name, $result->review_name, $period_from_format, $period_to_format, $type_string, $status_string, $result->rreid, $result->counselorflag, $result->status);
    $i++;
  }
  $json_data = json_encode($responce);
  return $json_data;
}

/**
 * get user status table's total pages number.
 * @return type
 */
function get_user_status_table_page_count($my_name, $my_counselee_names) {
 $results=get_pages_count_from_db($my_name,$my_counselee_names);
  foreach ($results as $result) {
    $records_count = $result->count;
  }
//  dd($records_count, '$records_count');
  return $records_count;
}

function get_pages_count_from_db($my_name,$my_counselee_names){
  
  if (count($my_counselee_names)) {
//counselor
    $sql = 'SELECT
	COUNT(p3.rreid) AS count
  FROM
	(
		SELECT
			rreid
		FROM
			{prs_rel_review_employee} p1
		WHERE
			p1.employeeName = :myname
      UNION
			SELECT
				rreid
			FROM
				{prs_rel_review_employee} p2
			WHERE
				p2.employeeName IN (:counselees)
	) p3';
    $results = db_query($sql, array(':myname' => $my_name, ':counselees' => $my_counselee_names))->fetchAll();
  }
  else {
    //counselee
    $sql = '
		SELECT
			count(rreid) as count
		FROM
			{prs_rel_review_employee} p1
		WHERE
			p1.employeeName = :myname';
    $results = db_query($sql, array(':myname' => $my_name))->fetchAll();
  }
  return $results;
}


function get_user_data_db($start, $limit, $cur_user_name, $my_counselee_names, $orderby) {
  //is not GM
  if (count($my_counselee_names)) {
    //counselor
    $sql = '(
	SELECT
		p1.employeeName,
		p1.rreid,
		p1.rstatus as status,
		pg.time_frame_from as period_from,
		pg.time_frame_to as period_to,
		pg.type,
		pg.description as review_name,
		\'1\' AS counselorflag
	FROM
		prs_rel_review_employee p1
	LEFT JOIN prs_general pg ON p1.prid = pg.prid
	WHERE
		p1.employeeName = :myname
)
UNION
	(
		SELECT
			p2.employeeName,
			p2.rreid,
			p2.rstatus as status,
			pg2.time_frame_from as period_from,
			pg2.time_frame_to as period_to,
			pg2.type,
			pg2.description as review_name,
			\'2\' AS counselorflag
		FROM
			prs_rel_review_employee p2
		LEFT JOIN prs_general pg2 ON p2.prid = pg2.prid
		WHERE
			p2.employeeName IN (:counselees)
	)
ORDER BY' . $orderby . ' counselorflag ASC, rreid DESC';
    $results = db_query_range($sql, $start, $limit, array(':myname' => $cur_user_name, ':counselees' => $my_counselee_names))->fetchAll();
  }
  else {
    $sql = '
	SELECT
		p1.employeeName,
		p1.rreid,
		p1.rstatus as status,
		pg.time_frame_from as period_from,
		pg.time_frame_to as period_to,
		pg.type,
		pg.description as review_name,
		\'0\' AS counselorflag
	FROM
		prs_rel_review_employee p1
	LEFT JOIN prs_general pg ON p1.prid = pg.prid
	WHERE
		p1.employeeName = :myname
ORDER BY' . $orderby . ' counselorflag ASC, rreid DESC';
    $results = db_query_range($sql, $start, $limit, array(':myname' => $cur_user_name,))->fetchAll();
  }
  return $results;
}