<?php

/**
 * @file
 * Theme functions
 */
require_once dirname(__FILE__) . '/includes/structure.inc';
require_once dirname(__FILE__) . '/includes/form.inc';
require_once dirname(__FILE__) . '/includes/menu.inc';
require_once dirname(__FILE__) . '/includes/comment.inc';
require_once dirname(__FILE__) . '/includes/node.inc';

/**
 * Implements hook_css_alter().
 */
function flat_ui_css_alter(&$css) {
  $radix_path = drupal_get_path('theme', 'radix');

  // Radix now includes compiled stylesheets for demo purposes.
  // We remove these from our subtheme since they are already included 
  // in compass_radix.
  unset($css[$radix_path . '/assets/stylesheets/radix-style.css']);
  unset($css[$radix_path . '/assets/stylesheets/radix-print.css']);
}

/**
 * Implements template_preprocess_page().
 */
function flat_ui_preprocess_page(&$variables) {
  // Add copyright to theme.
  if ($copyright = theme_get_setting('copyright')) {
    $variables['copyright'] = check_markup($copyright['value'], $copyright['format']);
  }
  if (isset($variables['node'])) {
    if ($variables['node']->type == 'webform') {
//      dd('$variables[\'node\']');
//      dd($variables['node']);
      $variables['theme_hook_suggestions'][] = 'page__' . str_replace('_', '--', $variables['node']->type);
    }
  }
}

function flat_ui_theme() {
  return array(
    'user_login_block' => array(
      'render element' => 'form',
      'path' => drupal_get_path('theme', 'flat_ui') . '/templates',
      'template' => 'user_login',
      'preprocess functions' => array(
        'flat_ui_preprocess_user_login_block',
      ),
//      'counsellor_create_review_form' => array(        
//      'render element' => 'form',        
//      'path' => drupal_get_path('theme', 'flat_ui') . '/templates',        
//      'template' => 'page--newreview',        
//    
//        ),
    ),
  );
}

function flat_ui_preprocess_user_login_block(&$variables) {
  unset($variables['form']['name']['#title']);
  unset($variables['form']['pass']['#title']);
  unset($variables['form']['links']);
  $variables['form']['name']['#attributes']['class'] = array('class' => 'form-control login-field');
  unset($variables['form']['name']['#prefix']);
  unset($variables['form']['name']['#suffix']);
  $variables['form']['name']['#attributes']['id'] = 'login-name';
  $variables['form']['pass']['#attributes']['id'] = 'login-pass';
  $variables['form']['name']['#attributes']['placeholder'] = 'Enter your name';
  $variables['form']['pass']['#attributes']['placeholder'] = 'Enter your password';
  $variables['form']['pass']['#attributes']['class'] = array('class' => 'form-control login-field');
  $variables['form']['actions']['submit']['#value'] = 'login';
  $variables['form']['actions']['submit']['#attributes']['class'] = array('class' => 'btn btn-primary btn-lg btn-block');
}


//function flat_ui_preprocess_webform_view(&$variables){
//  if($variables['webform']['#form']['#node']->title=='test webform'){
//    dd($variables);
//    unset($variables['webform']['#form']['#theme']);
////    $variables['theme_hook_suggestions'][0] = 'page__webform--form--test';
//    $variables['webform']['#form']['#theme']=array('0'=>'webform_form_test');
////    dd($variables['webform']['#form']['#theme'],'111111111111');
////    $variables['webform']['#form']['submitted']['project_roles_and_responsibilities']['#attributes']['style']='background-color:red';
////      $variables['webform']['#form']['submitted']['project_roles_and_responsibilities']['p_date']['#title']['#prefix']='<div style="width:40px">';
////      $variables['webform']['#form']['submitted']['project_roles_and_responsibilities']['p_date']['#title']['#suffix']='</div>';
//    
//  }
////  dd($variables);
//  return "flat_ui_preprocess_webform_view";
//  
//}

//function flat_ui_preprocess_webform_form(&$variables){
//  
////  $variables['element']['#title']['#prefix']='<div style="width:40px">Date111</div>';
//  dd($variables,'dd($variables)');
//  
//}
