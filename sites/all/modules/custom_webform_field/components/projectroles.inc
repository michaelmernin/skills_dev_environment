<?php

/**
 * @file
 * Webform module basic information component.
 */

/**
 * Implements _webform_defaults_component().
 */
function _webform_defaults_projectroles() {
  return array(
    'name' => '',
    'form_key' => NULL,
    'pid' => 0,
    'weight' => 0,
    'extra' => array(
      'title_display' => 0,
      'collapsible' => 0,
      'collapsed' => 0,
      'description' => '',
      'private' => FALSE,
    ),
  );
}

/**
 * Implements _webform_edit_component().
 */
function _webform_edit_projectroles($component) {
  $form = array();

  $form['display']['collapsible'] = array(
    '#type' => 'checkbox',
    '#title' => t('Collapsible'),
    '#default_value' => $component['extra']['collapsible'],
    '#description' => t('If this fieldset is collapsible, the user may open or close the fieldset.'),
    '#weight' => 0,
    '#parents' => array('extra', 'collapsible'),
  );
  $form['display']['collapsed'] = array(
    '#type' => 'checkbox',
    '#title' => t('Collapsed by Default'),
    '#default_value' => $component['extra']['collapsed'],
    '#description' => t('Collapsible fieldsets are "open" by default. Select this option to default the fieldset to "closed."'),
    '#weight' => 3,
    '#parents' => array('extra', 'collapsed'),
  );


  return $form;
}

/**
 * Implements _webform_render_component().
 */
function _webform_render_projectroles($component, $value = NULL, $filter = TRUE) {
	$node = isset($component['nid']) ? node_load($component['nid']) : NULL;
  $nid = $component['nid'];
	$current_user_name = get_current_user_name();
  $project_roles = get_project_roles($nid, $current_user_name);
//	dd($project_roles, 'project_roles');
  $project_role_add = get_added_project_roles($nid);
//	dd($project_role_add, 'project_added');
  appendCSSJS();

  $content = theme('project-roles-and-responsebilities', array(
    'projects' => $project_roles,
		'added_projects' => $project_role_add,
    'nid' => $nid,
  ));
  
  $element = array(
    '#type' => 'Project Roles',
    '#title' => $filter ? webform_filter_xss($component['name']) : $component['name'],
    '#title_display' => $component['extra']['title_display'] ? $component['extra']['title_display'] : NULL,
    '#weight' => $component['weight'],
    '#description' => $filter ? webform_filter_descriptions($component['extra']['description'], $node) : $component['extra']['description'],
    '#collapsible' => $component['extra']['collapsible'],
    '#collapsed' => $component['extra']['collapsed'],
    '#prefix' => $content,
    '#attributes' => array('class' => array('webform-component-fieldset')),
    '#pre_render' => array('webform_fieldset_prerender', 'webform_element_title_display'),
    '#translatable' => array('title', 'description'),
  );
  return $element;
}

/**
 * Pre-render function to set a unique fieldset class name.
 */
function webform_projectroles_prerender($element) {
  $element['#id'] = NULL;
  $element['#attributes']['class'][] = 'webform-component--' . str_replace('_', '-', implode('--', array_slice($element['#parents'], 1)));
  return $element;
}

/**
 * Implements _webform_display_component().
 */
function _webform_display_projectroles($component, $value, $format = 'html') {
  if ($format == 'text') {
    $element = array(
      '#title' => $component['name'],
      '#weight' => $component['weight'],
      '#theme_wrappers' => array('webform_element_text'),
      '#translatable' => array('title'),
    );
  }
  else {
    $element = _webform_render_projectroles($component, $value);
  }
  $element['#format'] = $format;

  return $element;
}

function get_project_roles($nid, $employeeName) {
  $period = get_annual_review_period($nid);
  $query_nid = get_project_roles_nid($employeeName, $period);
  $project_roles = get_project_roles_array($query_nid);
  return $project_roles;
}

function get_annual_review_period($nid) {
  $result = db_query('SELECT annual.time_frame_from,annual.time_frame_to 
    FROM prs_rel_provider_employee provider INNER JOIN prs_general annual
        ON provider.prid = annual.prid WHERE provider.nid = :nid', array(':nid' => $nid))->fetchAll();
  if (count($result) == 1) {
    return $result[0];
  }
}

function get_project_roles_nid($employeeName, $period) {
  if ((!isset($period->time_frame_from)) || (!isset($period->time_frame_to))) {
		return NULL;
	}
  $result= db_query('SELECT
	pro.nid,
	review.rstatus,
	review.rreid,
	project.time_frame_from,
	project.time_frame_to,
	project.project_name,
	project.client
FROM
	prs_general project
INNER JOIN prs_rel_review_employee review ON project.prid = review.prid
INNER JOIN prs_rel_provider_employee pro ON pro.prid = project.prid
WHERE
	project.type = 1
AND pro.employeeName = :employeename
AND review.employeeName= :employeename
AND pro.employeeName = pro.providerName
AND review.rstatus IN (1, 2, 3, 4, 5, 6)
AND project.time_frame_to >= :starttime
AND project.time_frame_to <= :endtime', array(':employeename'=>$employeeName, ':starttime' => $period->time_frame_from, ':endtime' => $period->time_frame_to))->fetchAll();
  if (count($result) == 0) 
    return null;
  return $result;
}

function get_project_roles_array($query_nid) {
  if ($query_nid == null)
    return null;
  $query_nid_period = '';
  foreach ($query_nid as $node) {
    if ($query_nid_period == '')
      $query_nid_period .= $node->nid;
    else
      $query_nid_period .= ',' . $node->nid;
  }
  $query_nid_period = '(' . $query_nid_period . ')';

//  dd($query_nid_period,'$query_nid_period');
//$query_nid_period
  
  
  $query_sql = "SELECT component.nid,component.cid ,
    component.form_key ,  component.name ,
    component.type , data.data 
    FROM
    {webform_component} component 
    LEFT JOIN {webform_submitted_data} data ON component.cid = data.cid
    AND data.nid = component.nid 
    WHERE component.nid in $query_nid_period
    AND component.form_key IN(
        'project_roles_and_responsibilities',
        'overall_rating'
    )";

  $result = db_query($query_sql)->fetchAll();
  $project_roles = array();

  foreach ($result as $node) {
    $key = $node->nid;
    $form_key = $node->form_key;
    $date = $node->data;
    $project_roles[$key][$form_key] = $date;
  }
  foreach ($query_nid as $node) {
    $key = $node->nid;
    $status = $node->rstatus;
    $rreid = $node->rreid;
    $client = $node->client;
    $project_name = $node->project_name;
    $start_date_timestamp = $node->time_frame_from;
    $end_date_timestamp = $node->time_frame_to;
    if(module_exists('common') && module_enable(array('common'))) {
			$start_date = date_to_format(unixTimestampToDateArray($start_date_timestamp));
			$end_date = date_to_format(unixTimestampToDateArray($end_date_timestamp));
    }

    $project_roles[$key]['status'] = $status;
    $project_roles[$key]['nid'] = $key;
    $project_roles[$key]['rreid'] = $rreid;
    $project_roles[$key]['client'] = $client;
    $project_roles[$key]['project_name'] = $project_name;
    $project_roles[$key]['startdate'] = $start_date;
    $project_roles[$key]['enddate'] = $end_date;
  }
//  dd($project_roles,'$project_roles');
  
  return $project_roles;
}

function get_added_project_roles($nid) {
	$result = db_select('prs_annual_projects')
			->fields('prs_annual_projects', array(
				'nid', 'client', 'rating', 'roles', 'pnid'
			))
			->condition('nid', $nid)
			->condition('pnid', 0, '=')
			->execute()->fetchAll();
  if (count($result) == 0){
		return NULL;
	} else {
		return $result;
	}
}

function delete_from_project_roles($nid) {
  $num_deleted = db_delete('prs_annual_projects')
      ->condition('nid', $nid)
      ->execute();
}

/**
 *  Page callback of annual/save-project-roles
 */
function save_project_roles() {
	$eval = 1;
	$nid = $_POST['nid'];
	$proj_roles= $_POST['proj_roles'];
//  delete_from_project_roles($nid);
	module_load_include('inc', 'counselee', 'files/review_assessment_modification');
  $decoded_items = decode_json_object($proj_roles);
//	dd($decoded_items, 'decoded_item');
	$pid_array = _insert_project_roles_into_annual_table($nid, $decoded_items);
  echo $eval;
}

function _insert_project_roles_into_annual_table($nid, $decoded_items){
  $pid_array = array();
  foreach($decoded_items as $key => $item) {
//		dd($item, 'item');
		if (isset($item->isAdded) && $item->isAdded == 1){
      $pid = db_merge('prs_annual_projects')
					->key(array('nid'=>$nid, 'client'=>$item->client, 'roles'=>$item->project_roles_and_responsibilities, 'rating'=>$item->rating))
          ->insertFields(array(
            'nid' => $nid,
            'client' => $item->client,
            'roles' => $item->project_roles_and_responsibilities,
            'rating' => $item->rating,
          ))
					->updateFields(array(
            'nid' => $nid,
            'client' => $item->client,
            'roles' => $item->project_roles_and_responsibilities,
            'rating' => $item->rating,
          ))
					->execute();
      } else if (isset($item->isAdded) && $item->isAdded == 0) {
			$pid =db_merge('prs_annual_projects')
					->key(array('nid'=>$nid, 'pnid'=>$item->nid))
					->insertFields(array(
						'nid' => $nid,
						'client' => $item->client,
						'roles' => $item->project_roles_and_responsibilities,
						'rating' =>($item->rating == 'N/A')?0:$item->rating,
						'pnid' => $item->nid,
					))
					->updateFields(array(
						'nid' => $nid,
						'client' => $item->client,
						'roles' => $item->project_roles_and_responsibilities,
						'rating' => ($item->rating == 'N/A')?0:$item->rating,
						'pnid' => $item->nid,
					))->execute();
			} else {
			}
			if ($pid != -1) {
				array_push($pid_array, $pid);
			}
  }
  if(count($pid_array) != 0){
    return $pid_array;
  } else {
    $msg = t("You did not update any project review information.");
    drupal_set_message($msg, 'status');
    return;
  }
}

function render_project_info_table($project) {
	if (!isset($project)) {
		print '<tr><td>No Project Review was found!</td></tr>';
		return;
	}
	$content = '';
	foreach ($project as $key => $value) {
		if (array_key_exists('client', $value)) {
			$content .= '<tr><td>' . $value['client'] . '</td>';
		}
		else {
			$content .= '<tr><td></td>';
		}
		if (array_key_exists('start_date', $value)) {
			$content .= '<td>' . $value['start_date'] . '</td>';
		}
		else {
			$content .= '<td></td>';
		}
		if (array_key_exists('end_date', $value)) {
			$content .= '<td>' . $value['end_date'] . '</td>';
		}
		else {
			$content .= '<td></td>';
		}
		if (array_key_exists('project_roles_and_responsibilities', $value)) {
			$content .= '<td>' . $value['project_roles_and_responsibilities'] . '</td>';
		}
		else {
			$content .= '<td></td>';
		}
		if (array_key_exists('status', $value)) {
			$content .= print_review_status($value['status']);
		}
		else {
			$content .= '<td></td>';
		}
		if (array_key_exists('overall_rating', $value)) {
			$content .= '<td>' . $value['overall_rating'] . '</td>';
		}
		else {
			$content .= '<td></td>';
		}
		$content .= '<td class="is-adding-proj">0</td></tr>';
	}
	print $content;
}

function print_review_status($review_status_num){
  switch ($review_status_num) {
    case '1':
      $status = 'REVIEW IN DRAFT';
      break;
    case '2':
      $status = 'REVIEWED_BY_COUNSELOR';
      break;
    case '3':
      $status = 'APPROVED_BY_COUNSELOR';
      break;
    case '4':
      $status = 'JOINT REVIEW';
      break;
    case '5':
      $status = 'GM_REVIEW';
      break;
    case '6':
      $status = 'GM_APPROVED';
      break;
    default:
      break;
  }
  return '<td>' . $status . '</td>';
}