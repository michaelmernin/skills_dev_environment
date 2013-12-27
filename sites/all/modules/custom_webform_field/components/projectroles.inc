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
  $project_roles = get_project_roles($nid);
  $project_role_add = get_added_project_roles($nid);

  $content = theme('project-roles-and-responsebilities', array(
    'projects' => $project_roles,
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

function get_project_roles($nid) {

  $period = get_annual_review_period($nid);
  $query_nid = get_project_roles_nid($period);
  $project_roles = get_project_roles_array($query_nid);
  return $project_roles;
}

function get_annual_review_period($nid) {
  $query = db_query("SELECT annual.time_frame_from,annual.time_frame_to 
    FROM prs_rel_provider_employee provider INNER JOIN prs_general annual
        ON provider.prid = annual.prid WHERE provider.nid = $nid");

  $result = $query->fetchAll();
  if (count($result) == 1) {
    return $result[0];
  }
}

function get_project_roles_nid($period) {
  $query = db_query("SELECT pro.nid ,review.rstatus,review.rreid  FROM
    prs_general project INNER JOIN prs_rel_review_employee review
        ON project.prid = review.prid INNER JOIN prs_rel_provider_employee pro
        ON pro.prid = project.prid
    WHERE project.type = 1  AND review.rstatus in (1,2,3,4,5,6)
    AND project.time_frame_from >=$period->time_frame_from 
    AND project.time_frame_to <= $period->time_frame_to");
  $result = $query->fetchAll();

  if (count($result) == 0)
    return null;

  return $result;
}

function get_project_roles_array($query_nid) {

  if ($query_nid == null)
    return null;
  
//  dd($query_nid,'$query_nid');
  
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
        'client' ,
        'project' ,
        'start_date' ,
        'end_date' ,
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
    
    $project_roles[$key]['status'] = $status;
    $project_roles[$key]['nid'] = $key;
    $project_roles[$key]['rreid'] = $rreid;
  }
//  dd($project_roles,'$project_roles');
  
  return $project_roles;
}

function get_added_project_roles($nid) {
  $query = db_query("SELECT nid, client, rating, roles, pnid
         FROM {prs_annual_projects} where nid=$nid and pnid=-1");

  $result = $query->fetchAll();
  if (count($result) == 0)
    return null;
}

function insert_into_project_roles($values) {
  $query = db_insert('prs_annual_projects')
      ->fields(array('nid', 'client', 'rating', 'roles', 'pnid'));
  foreach ($values as $value)
    $query->values($value);
  $query->execute();
}

function delete_from_project_roles($nid) {
  $num_deleted = db_delete('prs_annual_projects')
      ->condition('nid', $nid)
      ->execute();
}

function save_project_roles($nid, $values) {
  delete_from_project_roles($nid);
  insert_into_project_roles($values);
}

