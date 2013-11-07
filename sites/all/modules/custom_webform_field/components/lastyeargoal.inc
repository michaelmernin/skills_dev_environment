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
    ),
  );
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
  $node = isset($component['nid']) ? node_load($component['nid']) : NULL;
//  dd($node,'$node');
//  dd($node,'$node');
//  drupal_add_library('system', 'ui.slider');
//  drupal_add_js('jQuery(document).ready(function(){jQuery("#webform-slider-'. $component['form_key'] .'").slider({
//    min: 1,
//    max: 5,
//    step: 1,
//    value: jQuery("#"+ jQuery("#webform-slider-'. $component['form_key'] .'").attr("data-rel") +" input").val(),
//    slide: function(event, ui) {
//      jQuery("#"+ jQuery("#webform-slider-'. $component['form_key'] .'").attr("data-rel") +" input").val(ui.value);
//    }
//  });});', 'inline');
//  $slider = '<div class="slider-item slider-item-first">'. ($filter ? _webform_filter_xss($component['extra']['field_prefix']) : $component['extra']['field_prefix']) .'</div>';
//  $slider .= '<div class="slider-item slider" id="webform-slider-'. $component['form_key'] .'" data-rel="webform-component-'. $component['form_key'] .'"></div>';
//  $slider .= '<div class="slider-item slider-item-last">'. ($filter ? _webform_filter_xss($component['extra']['field_suffix']) : $component['extra']['field_suffix']) .'</div>';
  $element = array(
    '#type' => 'hidden',
    '#default_value' => $filter ? _webform_filter_values($component['value'], $node, NULL, NULL, FALSE) : $component['value'],
    '#required' => $component['mandatory'],
    '#weight' => $component['weight'],
    '#prefix' => '<div class="slider-wrapper clearfix">'. '$slider' .'</div>',
    '#theme_wrappers' => array('webform_element'),
  );
  if (isset($value)) {
    $element['#default_value'] = $value[0];
  }
  else {
    $element['#default_value'] = 3;
  }
  return $element;
}