<?php

/**
 * @file
 * Webform module fieldset component.
 */

/**
 * Implements _webform_defaults_component().
 */
function _webform_defaults_compositescore() {
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
function _webform_edit_compositescore($component) {
  $form = array();

  $form['value'] = array(
    '#type' => 'text_format',
    '#title' => t('Value'),
    '#default_value' => 'Composite Score:',
    '#description' => t('Markup allows you to enter custom HTML or PHP logic into your form.') . theme('webform_token_help'),
    '#weight' => -1,
    '#format' => null,
    '#element_validate' => array('_webform_edit_markup_validate'),
  );


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
function _webform_render_compositescore($component, $value = NULL, $filter = TRUE) {
  $node = isset($component['nid']) ? node_load($component['nid']) : NULL;

  $form_key = $component['form_key'];
  $value = $component['value'];
  $content = _webform_page_content($form_key, $value);
  
  $element = array(
    '#type' => 'Composite Score',
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
function webform_compositescore_prerender($element) {
  $element['#id'] = NULL;
  $element['#attributes']['class'][] = 'webform-component--' . str_replace('_', '-', implode('--', array_slice($element['#parents'], 1)));
  return $element;
}

/**
 * Implements _webform_display_component().
 */
function _webform_display_compositescore($component, $value, $format = 'html') {
  if ($format == 'text') {
    $element = array(
      '#title' => $component['name'],
      '#weight' => $component['weight'],
      '#theme_wrappers' => array('webform_element_text'),
      '#translatable' => array('title'),
    );
  }
  else {
    $element = _webform_render_compositescore($component, $value);
  }
  $element['#format'] = $format;

  return $element;
}

function _webform_page_content($form_key, $value) {
  $td = '<td>' . $value . '</td><td id="' . $form_key . '"> </td>';
  $content = '<table class="table"><tbody><tr class="info">' . $td . '</tr></tbody></table>';
  return $content;
}