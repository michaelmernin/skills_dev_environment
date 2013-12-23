<?php

/**
 * @file
 * Webform module fieldset component.
 */

/**
 * Implements _webform_defaults_component().
 */
function _webform_defaults_overallscore() {
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
function _webform_edit_overallscore($component) {
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
function _webform_render_overallscore($component, $value = NULL, $filter = TRUE) {
  $node = isset($component['nid']) ? node_load($component['nid']) : NULL;

  $description = $component['extra']['description'];
  if ($description == 'peer') {
    $content = get_peer_review_score_overall();
  }
  else {
    $content = get_self_review_score_overall();
  }
  $element = array(
    '#type' => 'Overall Composite Scores',
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
function webform_overallscore_prerender($element) {
  $element['#id'] = NULL;
  $element['#attributes']['class'][] = 'webform-component--' . str_replace('_', '-', implode('--', array_slice($element['#parents'], 1)));
  return $element;
}

/**
 * Implements _webform_display_component().
 */
function _webform_display_overallscore($component, $value, $format = 'html') {
  if ($format == 'text') {
    $element = array(
      '#title' => $component['name'],
      '#weight' => $component['weight'],
      '#theme_wrappers' => array('webform_element_text'),
      '#translatable' => array('title'),
    );
  }
  else {
    $element = _webform_render_overallscore($component, $value);
  }
  $element['#format'] = $format;

  return $element;
}

function get_peer_review_score_overall() {

  $table_head = array('Reviewer Composite Scores', 'Reviewer');

  $peer_review_overall [0] = array('category' => 'Technical Abilities', 'tdid' => 'rating_technical_abilities');
  $peer_review_overall [1] = array('category' => 'Consulting Skills', 'tdid' => 'rating_consulting_skills');
  $peer_review_overall [2] = array('category' => 'Professionalism', 'tdid' => 'rating_professionalism');
  $peer_review_overall [3] = array('category' => 'Leadership', 'tdid' => 'rating_leadership');
  $peer_review_overall [4] = array('category' => 'Teamwork', 'tdid' => 'rating_teamwork');
  $peer_review_overall [5] = array('category' => 'All', 'tdid' => 'rating_all');

  for ($i = 0; $i < count($peer_review_overall); $i++) {
    $peer_review_overall[$i]['rating'] = '';
  }
  $content = theme('peer-review-form-overall', 
      array('score_overall' => $peer_review_overall,
        'table_head' => $table_head));
  return $content;
}

function get_self_review_score_overall() {

  $table_head = array('Reviewer Composite Scores', 'Self');

  $self_review_overall [0] = array('category' => 'Client Engagements', 'tdid' => 'rating_client_engagements');
  $self_review_overall [1] = array('category' => 'Technical Abilities', 'tdid' => 'rating_technical_abilities');
  $self_review_overall [2] = array('category' => 'Consulting Skills', 'tdid' => 'rating_consulting_skills');
  $self_review_overall [3] = array('category' => 'Professionalism', 'tdid' => 'rating_professionalism');
  $self_review_overall [4] = array('category' => 'Leadership', 'tdid' => 'rating_leadership');
  $self_review_overall [5] = array('category' => 'Teamwork', 'tdid' => 'rating_teamwork');
  $self_review_overall [6] = array('category' => 'Internal Contributions', 'tdid' => 'rating_internal_contributions');
  $self_review_overall [7] = array('category' => 'All', 'tdid' => 'rating_all');

  for ($i = 0; $i < count($self_review_overall); $i++) {
    $self_review_overall[$i]['rating'] = '';
  }
  $content = theme('peer-review-form-overall',
      array('score_overall' => $self_review_overall,
        'table_head' => $table_head)
      );
  return $content;
}