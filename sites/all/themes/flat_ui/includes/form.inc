<?php
/**
 * @file
 * Theme and preprocess functions for forms
 */

/**
 * Implements theme_button().
 */
function flat_ui_button($variables) {
  $element = $variables['element'];
  $element['#attributes']['type'] = 'submit';
  element_set_attributes($element, array('id', 'name', 'value'));

  $element['#attributes']['class'][] = 'form-' . $element['#button_type'];
  $element['#attributes']['class'][] = 'btn';
  $element['#attributes']['class'][] = 'btn-large';
  if (!empty($element['#attributes']['disabled'])) {
    $element['#attributes']['class'][] = 'form-button-disabled';
  }

  // Add a btn-primary class if submit button.
  if (isset($element['#parents']) && ($element['#parents'][0] == 'submit')) {
    $element['#attributes']['class'][] = 'btn-primary';
  }

  return '<input' . drupal_attributes($element['#attributes']) . ' />';
}
