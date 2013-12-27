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
  $page_arg = new stdClass();
  $page_arg->page = $get_data['page']; // get the requested page
  $page_arg->limit = $get_data['rows']; // get how many rows we want to have into the grid
  $page_arg->sidx = $get_data['sidx']; // get index row - i.e. user click to sort
  $page_arg->sord = $get_data['sord']; // get the direction
  if (!$page_arg->sidx) {
    $page_arg->sidx = -1;
  }
  $page_arg->cur_user_name = get_current_user_name();
  $page_arg->my_counselee_names = get_counselees_from_db_with_username($page_arg->cur_user_name);
  $page_arg->is_counselor = is_counselor_by_counselee_names($page_arg->my_counselee_names);
  $page_arg->gm_counselee_names = get_gm_counselee_from_db_by_username($page_arg->cur_user_name);
  $page_arg->is_gm = is_gm_by_counselee_names($page_arg->gm_counselee_names);
  if ($get_data['_search'] == 'true') {
    $swhere = '';
    if (isset($get_data['type']) && $get_data['type'] != -1) {

      $swhere .='type=' . $get_data['type'];
    }
    else {
      $swhere .='1=1';
    }
    if (isset($get_data['status']) && $get_data['status'] != -1) {

      $swhere .=' and rstatus=' . $get_data['status'];
    }

    if (isset($get_data['employeeName'])) {
      $employeename_unformat = str_replace(" ", ".", $get_data['employeeName']);
      $swhere .= ' and employeeName like \'%' . $employeename_unformat . '%\'';
    }
    $review_name = isset($get_data['review_name']) ? ( $swhere .= ' and description like \'%' . $get_data['review_name'] . '%\'') : '';
    $page_arg->records_count = get_user_status_table_page_count($page_arg, $swhere);
    if ($page_arg->records_count > 0) {
      $page_arg->total_pages = ceil($page_arg->records_count / $page_arg->limit);
    }
    else {
      $page_arg->total_pages = 0;
    }
    if ($page_arg->page > $page_arg->total_pages) {
      $page_arg->page = $page_arg->total_pages;
    }
    $page_arg->start = $page_arg->limit * $page_arg->page - $page_arg->limit; // do not put $limit*($page - 1), start index


    $json_data = get_user_status_table_json($page_arg, $swhere);
  }
  else {
    /**
     * total page count number
     */
    $page_arg->records_count = get_user_status_table_page_count($page_arg);
    if ($page_arg->records_count > 0) {
      $page_arg->total_pages = ceil($page_arg->records_count / $page_arg->limit);
    }
    else {
      $page_arg->total_pages = 0;
    }
    if ($page_arg->page > $page_arg->total_pages) {
      $page_arg->page = $page_arg->total_pages;
    }
    $page_arg->start = $page_arg->limit * $page_arg->page - $page_arg->limit; // do not put $limit*($page - 1), start index

    $json_data = get_user_status_table_json($page_arg);
  }
  return $json_data;
}

/**
 * get data from database
 * @return type
 */
function get_user_status_table_json($page_arg, $swhere = NULL) {
  dd($page_arg, '$page_arg');
  if ($page_arg->sidx != -1) {
    $orderby = ' ' . $page_arg->sidx . ' ' . $page_arg->sord . ',';
  }
  else {
    $orderby = '';
  }
//  dd($orderby, '$orderby');
  $responce = new stdClass();
  $responce->page = $page_arg->page;
  $responce->total = $page_arg->total_pages;
  $responce->records = $page_arg->records_count;
  /**
   * get user status data from db (include counselor data and counselee data)
   */
  $results = array();
  if ($page_arg->start >= 0) {
    $results = get_user_data_db($page_arg, $orderby, $swhere);
  }
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
function get_user_status_table_page_count($page_arg, $swhere = NULL, $gm_mode = TRUE) {
  $results = get_pages_count_from_db($page_arg, $swhere, $gm_mode);
  foreach ($results as $result) {
    $records_count = $result->count;
  }
//  dd($records_count, '$records_count');
  return $records_count;
}

/**
 * return pages count number.
 * @param type $my_name
 * @param type $my_counselee_names
 * @param type $is_gm
 * @param type $gm_mode
 * @return type
 */
function get_pages_count_from_db($page_arg, $swhere = NULL, $gm_mode = TRUE) {

//  dd($page_arg, '$page_arg');
  if ($page_arg->is_gm && $gm_mode) {
    //gm
    $sql = get_user_status_pages_count_gm_sql($swhere);
//    dd($sql, '$sql');
    $results = db_query($sql, array(':counselees' => $page_arg->gm_counselee_names))->fetchAll();
//    dd($results,'$results');
  }
  else {
    if ($page_arg->is_counselor) {
//counselor
      $sql = get_user_status_pages_count_counselor_sql($swhere);
//      dd($sql, '$sql');
      $results = db_query($sql, array(':myname' => $page_arg->cur_user_name, ':counselees' => $page_arg->my_counselee_names))->fetchAll();
//      dd($results,'$results');
    }
    else {
      //counselee
      $sql = get_user_status_pages_count_counselee_sql($swhere);
//      dd($sql, '$sql');
      $results = db_query($sql, array(':myname' => $page_arg->cur_user_name))->fetchAll();
//      dd($results,'$results');
    }
  }
  return $results;
}

function get_user_data_db($page_arg, $orderby, $swhere, $gm_mode = TRUE) {
  if ($page_arg->is_gm && $gm_mode) {
    //gm
    $sql = get_user_status_gm_data_from_db_sql($orderby, $swhere);
//    dd($sql,'$sql');
    $results = db_query_range($sql, $page_arg->start, $page_arg->limit, array(':counselees' => $page_arg->gm_counselee_names))->fetchAll();
//    dd($results,'$results');
  }
  else {
    //is not GM
    if ($page_arg->is_counselor) {
      //counselor
      $sql = get_user_status_counselor_data_from_db_sql($orderby, $swhere);
      $results = db_query_range($sql, $page_arg->start, $page_arg->limit, array(':myname' => $page_arg->cur_user_name, ':counselees' => $page_arg->my_counselee_names))->fetchAll();
    }
    else {
      $sql = get_user_status_counselee_data_from_db_sql($orderby, $swhere);
      $results = db_query_range($sql, $page_arg->start, $page_arg->limit, array(':myname' => $page_arg->cur_user_name))->fetchAll();
    }
  }
  return $results;
}

function get_user_status_pages_count_gm_sql($swhere) {
  $where_sql = isset($swhere) ? (' and ' . $swhere) : '';
  return '
		SELECT
COUNT(p1.rreid) AS count
			FROM
				{prs_rel_review_employee} p1
			LEFT JOIN {prs_general} pg ON p1.prid = pg.prid
      WHERE p1.employeeName IN (:counselees)'
      . $where_sql;
}

function get_user_status_pages_count_counselor_sql($swhere) {

  $where_sql = isset($swhere) ? (' and ' . $swhere) : '';
  return 'SELECT
	COUNT(rreid) AS count
FROM
	(
		(
			SELECT
				p1.employeeName,
				p1.rreid,
				p1.rstatus AS STATUS,
				pg.time_frame_from AS period_from,
				pg.time_frame_to AS period_to,
				pg.type,
				pg.description AS review_name,
				\'1\' AS counselorflag
			FROM
				{prs_rel_review_employee} p1
			LEFT JOIN {prs_general} pg ON p1.prid = pg.prid
			WHERE
				p1.employeeName = :myname' . $where_sql . '
		)
		UNION
			(
				SELECT
					p2.employeeName,
					p2.rreid,
					p2.rstatus AS STATUS,
					pg2.time_frame_from AS period_from,
					pg2.time_frame_to AS period_to,
					pg2.type,
					pg2.description AS review_name,
					\'2\' AS counselorflag
				FROM
					{prs_rel_review_employee} p2
				LEFT JOIN {prs_general} pg2 ON p2.prid = pg2.prid
				WHERE
					p2.employeeName IN (:counselees)' . $where_sql . '
			)
	) p3';
}

function get_user_status_pages_count_counselee_sql($swhere) {
  $where_sql = isset($swhere) ? (' and ' . $swhere) : '';
  return '
		SELECT
COUNT(p1.rreid) AS count
			
			FROM
				{prs_rel_review_employee} p1
			LEFT JOIN {prs_general} pg ON p1.prid = pg.prid
			WHERE
				p1.employeeName = :myname' . $where_sql;
}

function get_user_status_gm_data_from_db_sql($orderby, $swhere) {
  $where_sql = isset($swhere) ? (' and ' . $swhere) : '';
  return 'SELECT
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
			p1.employeeName IN (:counselees)' . $where_sql . '
  ORDER BY' . $orderby . ' counselorflag ASC, rreid DESC';
}

function get_user_status_counselor_data_from_db_sql($orderby, $swhere) {
  $where_sql = isset($swhere) ? (' and ' . $swhere) : '';
  return '(
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
		p1.employeeName = :myname' . $where_sql . '
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
			p2.employeeName IN (:counselees)' . $where_sql . '
	)
ORDER BY' . $orderby . ' counselorflag ASC, rreid DESC';
}

function get_user_status_counselee_data_from_db_sql($orderby, $swhere) {
  $where_sql = isset($swhere) ? (' and ' . $swhere) : '';
  return '
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
		p1.employeeName = :myname' . $where_sql . '
ORDER BY' . $orderby . ' counselorflag ASC, rreid DESC';
}