<?php
/**
 * @file
 * Theme settings.
 */

/**
 * Implements theme_settings().
 */
function flat_ui_form_system_theme_settings_alter(&$form, &$form_state) {
  // Ensure this include file is loaded when the form is rebuilt from the cache.
  $form_state['build_info']['files']['form'] = drupal_get_path('theme', 'flat_ui') . '/theme-settings.php';

  // Add theme settings here.
  $form['flat_ui_theme_settings'] = array(
    '#title' => t('Theme Settings'),
    '#type' => 'fieldset',
  );

  // Copyright.
  $copyright = theme_get_setting('copyright');
  $form['flat_ui_theme_settings']['copyright'] = array(
    '#title' => t('Copyright'),
    '#type' => 'text_format',
    '#format' => $copyright['format'],
    '#default_value' => $copyright['value'] ? $copyright['value'] : t('Perficient Review System.'),
  );

  // Return the additional form widgets.
  return $form;
}
