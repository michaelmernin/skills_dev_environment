<?php

/**
 * @file
 * Webform module basic information component.
 */

/**
 * Implements _webform_defaults_component().
 */
function _webform_defaults_basicinformation() {
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
function _webform_edit_basicinformation($component) {
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
function _webform_render_basicinformation($component, $value = NULL, $filter = TRUE) {
  $node = isset($component['nid']) ? node_load($component['nid']) : NULL;
  
  $nid = $component['nid'];
  $basic_info = get_review_basic_information_by_nid($nid);
  $content = theme('peer-review-form', array('basic_info' => $basic_info));
  
  $element = array(
    '#type' => 'Basic Information',
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
function webform_basicinformation_prerender($element) {
  $element['#id'] = NULL;
  $element['#attributes']['class'][] = 'webform-component--' . str_replace('_', '-', implode('--', array_slice($element['#parents'], 1)));
  return $element;
}

/**
 * Implements _webform_display_component().
 */
function _webform_display_basicinformation($component, $value, $format = 'html') {
  if ($format == 'text') {
    $element = array(
      '#title' => $component['name'],
      '#weight' => $component['weight'],
      '#theme_wrappers' => array('webform_element_text'),
      '#translatable' => array('title'),
    );
  }
  else {
    $element = _webform_render_basicinformation($component, $value);
  }
  $element['#format'] = $format;

  return $element;
}

/**
 * Get peer reiview form basic informaiton 
 * 
 * @param string $nid The webform node id
 * 
 */
function get_review_basic_information_by_nid($nid) {
  $query = db_select('prs_rel_provider_employee', 'provider');
  $query->join('prs_rel_employee_counselor_gm', 'relation', 'relation.employeeName = provider.employeeName');
  $query->addField('provider', 'employeeName', 'colleague');
  $query->addField('relation', 'counselorName', 'counselor');
  $query->addField('provider', 'providerName', 'reviewer');
  $query->condition('provider.nid', $nid);
  $result = $query->execute()->fetchAll();

  $result_set = array();
  $index = 0;

  foreach ($result as $value) {
    $temp = array(
      'colleague' => $value->colleague,
      'counselor' => $value->counselor,
      'reviewer' => $value->reviewer,
    );
    $result_set[$index++] = $temp;
  }
  return $result_set;
}