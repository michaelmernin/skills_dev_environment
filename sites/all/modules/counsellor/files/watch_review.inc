<?php

/**
 * @file
 * 
 * 
 */

/**
 * callback watchpeerreview/%
 * @param type $nid
 */
function watch_peer_review($nid) {
  //==================access control====================================================
  $user_id = get_user_identity_by_nid($nid);
  if ($user_id != 1) {
    drupal_goto('accessdenied');
  }
  //====================Get source from database=========================================
  $webform_data = get_webform_data($nid);
  $answer_array = get_answer($nid);
  $cid_array = array();
  $basic_info = get_webform_basic_info($nid);
  $render_data = _manage_webform_data($webform_data, $nid, $answer_array, $cid_array);
  //===================Theme it==========================================================
  $pagecontent = get_watchpeerreview_page_content($basic_info, $render_data);
  return $pagecontent;
}

/**
 * 
 * @param type $webform_datas
 * @param type $nid
 * @param type $answer_array
 * @param type $cid_array
 * @return array
 */
function _manage_webform_data($webform_datas, $nid, &$answer_array, &$cid_array) {
  $QandA = array();
  foreach ($webform_datas as $webform_data) {
    $cid = $webform_data->cid;
    if (!in_array($cid, $cid_array)) {
      if ($webform_data->type == 'fieldset') {
        //=================================is fieldset===================================================
        array_push($cid_array, $cid);
        $chilren_data = get_webform_children_data($nid, $cid);
        $QandA_date = _manage_webform_data($chilren_data, $nid, $answer_array, $cid_array);
        $QandA_std_data = get_QandA_std_data($webform_data->type, $cid, $webform_data->nid, $webform_data->name, NULL, $QandA_date, $webform_data->form_key, $webform_data->weight, $webform_data->pid);
        array_push($QandA, $QandA_std_data);
      }
      else {
        //=================================normal component===============================================
        array_push($cid_array, $cid);
        switch ($webform_data->type) {
          case 'textfield':
            $answer = find_answer($nid, $cid, $answer_array);
            switch ($answer) {
              case 'NO_ANSWER':
                $answer = NO_ANSWER;
                break;
              default :
                break;
            }
            $QandA_std_data = get_QandA_std_data($webform_data->type, $cid, $webform_data->nid, $webform_data->name, $answer, NULL, $webform_data->form_key, $webform_data->weight, $webform_data->pid);
            array_push($QandA, $QandA_std_data);
            break;
          case 'select':
            $answer = find_answer($nid, $cid, $answer_array);
            $select_comment = find_select_comment($nid, $webform_datas, $webform_data->form_key, '_comment', $answer_array);
            switch ($answer) {
              case 'NO_ANSWER':
                $answer = NO_ANSWER;
                break;
              case '0':
                $answer = 'N/A';
                break;
              default :
                break;
            }
            $QandA_std_data = get_QandA_std_data($webform_data->type, $cid, $webform_data->nid, $webform_data->name, $answer, NULL, $webform_data->form_key, $webform_data->weight, $webform_data->pid, $select_comment);
            array_push($QandA, $QandA_std_data);
            break;
          case 'textarea':
            if ($webform_data->form_key == 'summary_comments') {
              $answer = find_answer($nid, $cid, $answer_array);
              switch ($answer) {
                case 'NO_ANSWER':
                  $answer = NO_COMMENT;
                  break;
                default :
                  break;
              }
              $QandA_std_data = get_QandA_std_data($webform_data->type, $cid, $webform_data->nid, $webform_data->name, $answer, NULL, $webform_data->form_key, $webform_data->weight, $webform_data->pid);
              array_push($QandA, $QandA_std_data);
            }
            break;
          default :
            break;
        }
      }
    }
    else {
      continue;
    }
  }
  return $QandA;
}

/**
 * get webfrom component from database
 * @param type $nid
 * @return type
 * @author Mars
 */
function get_webform_data($nid) {
  $query = db_select('webform_component', 'wc');
  $query->fields('wc');
  $query->condition('wc.nid', $nid);
  $query->orderBy('wc.weight');
  $result = $query->execute()->fetchAll();
  return $result;
}

/**
 * get answer from database
 * @param type $nid
 * @return type
 * @author Mars
 */
function get_answer($nid) {
  $query = db_select('webform_submitted_data', 'wsd');
  $query->fields('wsd');
  $query->condition('wsd.nid', $nid);
  $result = $query->execute()->fetchAll();
  return $result;
}

/**
 * get webfoem children data from database
 * @param type $nid
 * @param type $pid
 * @return type
 * @author Mars
 */
function get_webform_children_data($nid, $pid) {
  $query = db_select('webform_component', 'wc');
  $query->fields('wc');
  $query->condition('wc.pid', $pid);
  $query->condition('wc.nid', $nid);
  $query->orderBy('wc.weight');
  $result = $query->execute()->fetchAll();
  return $result;
}

/**
 * find answer from answer array
 * @param type $nid
 * @param type $cid
 * @param type $answer_array
 * @return type
 * @author Mars
 */
function find_answer($nid, $cid, &$answer_array) {
  $return_answer = 'NO_ANSWER';
  foreach ($answer_array as $key => $one_answer) {

    if ($one_answer->nid == $nid && $one_answer->cid == $cid) {
      $return_answer = $one_answer->data;
//      dd($answer_array, '$answer_array');
      unset($answer_array[$key]);
//      dd($answer_array, '$answer_array_1');
      break;
    }
    else {
      continue;
    }
  }
  return $return_answer;
}

/**
 * get question and answer std calss
 * @param type $type
 * @param type $cid
 * @param type $nid
 * @param type $name
 * @param type $answer
 * @param type $children
 * @param type $form_key
 * @param type $weight
 * @return \stdClass
 * @author Mars
 */
function get_QandA_std_data($type, $cid, $nid, $name, $answer, $children, $form_key, $weight, $pid, $select_comment = NULL) {
  $QandA_std_data = new stdClass();
  $QandA_std_data->type = $type;
  $QandA_std_data->cid = $cid;
  $QandA_std_data->nid = $nid;
  $QandA_std_data->name = $name;
  $QandA_std_data->answer = $answer;
  $QandA_std_data->children = $children;
  $QandA_std_data->form_key = $form_key;
  $QandA_std_data->weight = $weight;
  $QandA_std_data->comment = ((trim(filter_xss_and_check_plain($select_comment)) == '') ? NO_COMMENT : filter_xss_and_check_plain($select_comment));
  $QandA_std_data->pid = $pid;
  return $QandA_std_data;
}

/**
 * make a map between select and comment
 * @param type $nid
 * @param type $webform_component
 * @param type $form_key
 * @param type $subfix
 * @param type $answer_array
 * @return string
 * @author Mars
 */
function find_select_comment($nid, $webform_component, $form_key, $subfix, &$answer_array) {
  $return_comment = '';
  $webform_form_key = $form_key . $subfix;
  foreach ($webform_component as $componenet) {
    if ($componenet->form_key == $webform_form_key) {
      $cid = $componenet->cid;
      $return_comment = find_answer($nid, $cid, $answer_array);
      if ($return_comment == 'NO_ANSWER') {
        $return_comment = NO_COMMENT;
      }
      break;
    }
  }
  return $return_comment;
}

/**
 * get webform basic information from database
 * @param type $nid
 * @return type
 * @author Mars
 */
function get_webform_basic_info($nid) {
  $query = db_select('webform_submissions', 'ws');
  $query->fields('ws');
  $query->condition('ws.nid', $nid);
  $query->orderBy('ws.sid');
  $results = $query->execute()->fetchAll();
  foreach ($results as $one_result) {
    $result = $one_result;
  }
  $return_val = get_std_webform_basic_info($result);
  return $return_val;
}

/**
 * 
 * @param type $result
 * @return \stdClass
 * @author Mars
 */
function get_std_webform_basic_info($result) {
  $basic_info = new stdClass();
  if (count($result)) {
    $node = node_load($result->nid);
    $basic_info->title = $node->title;
    $basic_info->sid = $result->sid;
    $basic_info->nid = $result->nid;
    $basic_info->user = ($result->uid != 0) ? format_login_name(user_load($result->uid)->name) : 'Anonymous';
    $basic_info->creatdate = format_date($result->submitted, 'custom', 'Y-m-d');
    $basic_info->ip = $result->remote_addr;
  }
  return $basic_info;
}

/**
 * 
 * @param type $basic_info
 * @param type $content
 * @return type
 */
function get_watchpeerreview_page_content($basic_info, $content) {
  $html_content = get_watchpeerreview_basic_info_html($basic_info);
  $html_content.=get_watchpeerreview_content_html($content);
  return $html_content;
}

/**
 * return html with template watch_peer_review_basic_info.tpl.php
 * @param type $basic_info
 * @return type
 */
function get_watchpeerreview_basic_info_html($basic_info) {
  return theme('watch_peer_review_basic_info', array('basic_info' => $basic_info));
}

/**
 * generate html codes with theme method
 * @param type $contents
 * @return string
 */
function get_watchpeerreview_content_html($contents) {
//  $html_value
  //=================================html contents====================================================
  $html_value = _get_watchpeerreview_component_html($contents);
  //=================================add go back button===============================================
  $html_value.='<button id="btnPeerReviewGoBack" onclick="window.history.back();" role="button" class="btn btn-danger" >Go Back</button>';
  return $html_value;
}

/**
 * 
 * @param type $contents
 * @return type
 */
function _get_watchpeerreview_component_html($contents) {
  $html_text = '';
  foreach ($contents as $one_content) {
    if ($one_content->type == 'fieldset') {
      //=================================is fieldset===================================================
      if ($one_content->pid == 0) {
        $html_text.='<div class="peerfieldset">';
        $html_text.='<legend>' . $one_content->name . '</legend>';
        $html_text.=_get_watchpeerreview_component_html($one_content->children);
        $html_text.='</div>';
      }
      else {
        $html_text.='<div class="sec-peerfieldset">';
        $html_text.=$one_content->name;
        $html_text.='</div>';
        $html_text.=_get_watchpeerreview_component_html($one_content->children);
      }
    }
    else {
      //=================================normal component===============================================
      if ($one_content->type != 'select') {
        $html_text.=theme('watch_peer_review_component', array('component_content' => $one_content));
      }
      else {
        if ($one_content->form_key == 'overall_rating') {
          $html_text.=theme('watch_peer_review_component_overallrating', array('component_content' => $one_content));
        }
        else {
          $html_text.=theme('watch_peer_review_component_select', array('component_content' => $one_content));
        }
      }
    }
  }
  return $html_text;
}