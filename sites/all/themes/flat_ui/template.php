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
}

// function flat_ui_theme() { 
//   return array( 
//     'user_login_block' => array('template' => 'user_login', 
//     'arguments' => array('form' => NULL),
//      ),);
//   } 
//   
//   function flat_ui_preprocess_user_login_block(&$variables)
//   { 
//     $variables['form']['name']['#title']='xxxxxx'; 
//     $variables['form']['pass']['#title']='oooooo'; 
//     $variables['form']['submit']['#value']='ssssss'; 
//     $variables['form']['links']['#value']=''; 
//     $variables['rendered']=drupal_render($variables['form']);
//   }

//function  flat_ui_theme() {
//  return array(
//    'user_login_block' => array(
//      'template' => 'user_login',
//      'variables' => array('form' => NULL),
//      'preprocess functions' => array('flat_ui_preprocess_user_login_block'),
//    ),
//  );
//}

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
  $variables['form']['name']['#attributes']['class']= array('class' => 'form-control login-field');
  unset($variables['form']['name']['#prefix']);
  unset($variables['form']['name']['#suffix']);
//  $variables['form']['name']['#prefix']='<div class="form-group">';
//  $variables['form']['name']['#suffix']='</div>';
//    $variables['form']['name']['#attributes']['width']='370';
//    
//     $variables['form']['pass']['#attributes']['width']='370';
  $variables['form']['name']['#attributes']['id']='login-name';
  $variables['form']['pass']['#attributes']['id']='login-pass';
//  $variables['form']['submit']['#attributes']['class']=array('class' => 'btn btn-primary btn-lg btn-block');
  $variables['form']['name']['#attributes']['placeholder']='Enter your name';
  $variables['form']['pass']['#attributes']['placeholder']='Enter your password';
  $variables['form']['pass']['#attributes']['class']=array('class' => 'form-control login-field');
  $variables['form']['actions']['submit']['#value']='login';
  $variables['form']['actions']['submit']['#attributes']['class']=array('class' => 'btn btn-primary btn-lg btn-block');
}
//function flat_ui_form_alter(&$form, &$form_state, $form_id) {
//
//    //dsm($form_id);
//
////    if ($form_id == 'user-login-form') {
////        // add prefix and suffix to form
////        $form['#prefix'] = '<div class="form-class">';
////        $form['#suffix'] = '</div>';
////    }
//  
//  if ($form['#id'] == 'user-login-form') {
//    dpm($form);
//    
//    
//  
//  }
//}


//function flat_ui_preprocess_user_login_block(&$variables) {
//
////注释代码，你需要就可以去掉
//$variables['form']['name']['#title']='xxxxxx'; 
//     $variables['form']['pass']['#title']='oooooo'; 
//     $variables['form']['submit']['#value']='ssssss'; 
//     $variables['form']['links']['#value']=''; 
//
////  $variables['form']['name']['#title']='';
////  $variables['form']['pass']['#title']='';
//$variables['rendered']=drupal_render($variables['form']);
//
//}