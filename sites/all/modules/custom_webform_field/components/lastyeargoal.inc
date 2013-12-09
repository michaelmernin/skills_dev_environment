<?php

/**
 * @file
 * the custom file in webform
 */

/**
 * _webform_defaults_component()
 * @return type
 */
function _webform_defaults_lastyeargoal() {
  return array(
    'name' => '',
    'form_key' => NULL,
    'pid' => 0,
    'weight' => 0,
    'value' => '',
    'mandatory' => 0,
    'extra' => array(
      'cols' => '',
      'rows' => '',
      'title_display' => 0,
      'resizable' => 1,
      'disabled' => 1,
      'description' => '',
      'placeholder' => '',
      'attributes' => array(),
      'private' => FALSE,
      'analysis' => FALSE,
      'goal_string' => GOAL_STRING,
    ),
  );
}

/**
 * Implements _webform_edit_component().
 */
function _webform_edit_lastyeargoal($component) {
  $form = array();
  $form['value'] = array(
    '#type' => 'textarea',
    '#title' => t('Default value'),
    '#default_value' => $component['value'],
    '#description' => t('The default value of the field.') . ' ' . theme('webform_token_help'),
    '#cols' => 60,
    '#rows' => 5,
    '#weight' => 0,
  );
  $form['goal'] = array(
    '#type' => 'textfield',
    '#title' => t('Goal form key'),
    '#default_value' => $component['extra']['goal_string'],
    '#description' => t('key of the goal field.') . ' ' . t('Leaving blank will use the default key "goals_text".'),
    '#size' => 5,
    '#maxlength' => 10,
//    '#parents' => array('extra', 'cols'),
  );
  $form['display']['cols'] = array(
    '#type' => 'textfield',
    '#title' => t('Width'),
    '#default_value' => $component['extra']['cols'],
    '#description' => t('Width of the textarea in columns. This property might not have a visual impact depending on the CSS of your site.') . ' ' . t('Leaving blank will use the default size.'),
    '#size' => 5,
    '#maxlength' => 10,
    '#parents' => array('extra', 'cols'),
  );
  $form['display']['rows'] = array(
    '#type' => 'textfield',
    '#title' => t('Height'),
    '#default_value' => $component['extra']['rows'],
    '#description' => t('Height of the textarea in rows.') . ' ' . t('Leaving blank will use the default size.'),
    '#size' => 5,
    '#maxlength' => 10,
    '#parents' => array('extra', 'rows'),
  );
  $form['display']['resizable'] = array(
    '#type' => 'checkbox',
    '#title' => t('Resizable'),
    '#description' => t('Make this field resizable by the user.'),
    '#weight' => 2,
    '#default_value' => $component['extra']['resizable'],
    '#parents' => array('extra', 'resizable'),
  );
  $form['display']['placeholder'] = array(
    '#type' => 'textfield',
    '#title' => t('Placeholder'),
    '#default_value' => $component['extra']['placeholder'],
    '#description' => t('The placeholder will be shown in the field until the user starts entering a value.'),
    '#parents' => array('extra', 'placeholder'),
  );
//  $form['display']['disabled'] = array(
//    '#type' => 'checkbox',
//    '#title' => t('Disabled'),
//    '#return_value' => 1,
//    '#description' => t('Make this field non-editable. Useful for setting an unchangeable default value.'),
//    '#weight' => 11,
//    '#default_value' => $component['extra']['disabled'],
//    '#parents' => array('extra', 'disabled'),
//  );
  return $form;
}

/**
 * _webform_theme_component()
 * @return type
 */
function _webform_theme_lastyeargoal() {
  return array(
    'webform_display_lastyeargoal' => array(
      'render element' => 'element',
    ),
  );
}

/**
 * _webform_display_component()
 * @param type $component
 * @param type $value
 * @param type $format
 * @return type
 */
function _webform_display_lastyeargoal($component, $value, $format = 'html') {
  return array(
    '#title' => 'last year goal',
    '#weight' => $component['weight'],
    '#theme' => 'webform_display_lastyeargoal',
    '#theme_wrappers' => $format == 'html' ? array('webform_element') : array('webform_element_text'),
    '#format' => $format,
    '#value' => $value[0],
    '#translatable' => array('title'),
  );
}

function theme_webform_display_lastyeargoal($variables) {
  $element = $variables['element'];
  $value = $element['#value'];
  return trim($value) !== '' ? $value : ' ';
}

function _webform_table_lastyeargoal($component, $value) {
  return check_plain(empty($value[0]) ? '' : $value[0]);
}

function _webform_render_lastyeargoal($component, $value = NULL, $filter = TRUE) {
  $is_first_year = FALSE;
  $node = isset($component['nid']) ? node_load($component['nid']) : NULL;
  $last_nid = get_last_year_review_nid($node);
  if ($last_nid != 0) {
    $sid = get_last_sid_by_nid($last_nid);
    if ($sid != 0) {
      $cid = get_cid_by_string(($filter ? _webform_filter_xss($component['extra']['goal_string']) : $component['extra']['goal_string']), $last_nid);
      if ($cid != 0) {
        $display = get_last_year_goal($last_nid, $sid, $cid);
      }
      else {
        $display = 'config error! please connection Admin.';
      }
    }
    else {
      $display = 'Please submit last annaul review first';
    }
  }
  else {
    $display = '';
    $is_first_year = TRUE;
  }
  $element = array(
//    '#type' => 'hidden',
//    '#default_value' => $filter ? _webform_filter_values($component['value'], $node, NULL, NULL, FALSE) : $component['value'],
//    '#required' => $component['mandatory'],
//    '#weight' => $component['weight'],
//    '#prefix' => '<div class="slider-wrapper clearfix">' . $display . '</div>',
//    '#theme_wrappers' => array('webform_element'),
    '#type' => 'textarea',
//    '#title' => $filter ? webform_filter_xss($component['name']) : $component['name'],
    '#title_display' => $component['extra']['title_display'] ? $component['extra']['title_display'] : 'before',
//    '#prefix' => '<div style="color:#9d9c9a">',
    '#default_value' => $display,
//    '#subfix' => '</div>',
    '#required' => $component['required'],
    '#weight' => $component['weight'],
    '#description' => $filter ? webform_filter_descriptions($component['extra']['description'], $node) : $component['extra']['description'],
    '#rows' => !empty($component['extra']['rows']) ? $component['extra']['rows'] : 5,
    '#cols' => !empty($component['extra']['cols']) ? $component['extra']['cols'] : 60,
    '#attributes' => $component['extra']['attributes'],
    '#resizable' => (bool) $component['extra']['resizable'], // MUST be FALSE to disable.
    '#theme_wrappers' => array('webform_element'),
    '#translatable' => array('title', 'description'),
  );
//  if (isset($value)) {
//    $element['#default_value'] = $value[0];
//  }
//  else {
//    $element['#default_value'] = 3;
//  }
  $element['#attributes']['style'] = 'color:#9d9c9a';
//  if ($component['extra']['disabled']) {
  if (!$is_first_year) {
    if ($filter) {
      $element['#attributes']['readonly'] = 'readonly';
    }
    else {
      $element['#disabled'] = TRUE;
    }
  }
  else {
    $element['#disabled'] = FALSE;
  }
  return $element;
}

function get_last_year_review_nid($node) {
  $title_array = explode(' ', $node->title);
  $title_array[2] = $title_array[2] - 1;
  $n = count($title_array);
  $title = '';
  for ($i = 0; $i < $n; $i++) {
    if ($i !== $n - 1) {
      $title.=$title_array[$i] . ' ';
    }
    else {
      $title.=$title_array[$i];
    }
  }
  $nid = get_temp_node_id($title);
  return $nid;
}

function get_cid_by_string($string, $last_nid) {
  $cid = 0;
  $query = db_select('webform_component', 'wc');
  $query->fields('wc', array('cid'));
  $query->condition('wc.form_key', $string);
  $query->condition('wc.nid', $last_nid);
  $result = $query->execute()->fetchAll();
  foreach ($result as $value) {
    $cid = $value->cid;
  }
//dd($cid,'$cid');
  return $cid;
}

/**
 * 
 * @param type $nid
 * @param type $sid
 * @param type $cid
 * @return type
 */
function get_last_year_goal($nid, $sid, $cid) {
  $data = '';
  $query = db_select('webform_submitted_data', 'wsd');
  $query->fields('wsd', array('data'));
  $query->condition('wsd.nid', $nid);
  $query->condition('wsd.sid', $sid);
  $query->condition('wsd.cid', $cid);
  $result = $query->execute()->fetchAll();
  foreach ($result as $value) {
    $data = $value->data;
  }
  return $data;
}