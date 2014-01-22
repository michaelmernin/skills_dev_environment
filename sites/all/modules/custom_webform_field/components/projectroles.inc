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
  if(module_exists('common') && module_enable(array('common'))){
	  $current_user_name = get_current_user_name();
    $project_roles = get_project_roles($nid, $current_user_name);
    $project_role_add = get_added_project_roles($nid);
  }
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


function delete_from_project_roles($nid) {
  $num_deleted = db_delete('prs_annual_projects')
      ->condition('nid', $nid)
      ->execute();
}

/**
 *  Page callback of annual/save_project_roles
 */
function save_project_roles() {
	$eval = 'save project role OK';
	$nid = $_POST['nid'];
	$proj_roles= $_POST['proj_roles'];
//  delete_from_project_roles($nid);
	module_load_include('inc', 'counselee', 'files/review_assessment_modification');
  $decoded_items = decode_json_object($proj_roles);
	$pid_array = _insert_project_roles_into_annual_table($nid, $decoded_items);
  echo $eval;
}

function _insert_project_roles_into_annual_table($nid, $decoded_items){
  $pid_array = array();
  foreach($decoded_items as $key => $item) {
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