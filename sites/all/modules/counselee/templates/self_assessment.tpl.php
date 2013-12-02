<?php
/**
 * @file
 * Customize the display of a complete webform.
 *
 * This file may be renamed "webform-form-[nid].tpl.php" to target a specific
 * webform on your site. Or you can leave it "webform-form.tpl.php" to affect
 * all webforms on your site.
 *
 * Available variables:
 * - $form: The complete form array.
 * - $nid: The node ID of the Webform.
 *
 * The $form array contains two main pieces:
 * - $form['submitted']: The main content of the user-created form.
 * - $form['details']: Internal information stored by Webform.
 */
?>
<div id="wrap" style="padding-top: 20px;width:auto" class="container">
  <a href="#" name="pageTopPosition" ></a>
  <a href="#pageTopPosition" id="gotoTopBtn" style="display: none">click here to go to page top</a>
  <h1 class="page-title"></h1>

  <?php
//dd($node,'node');
//dd($page['content']['system_main']['nodes'][3]['webform']['#node']->webform['components']);
//print render($page['content']['system_main']['nodes'][3]['webform']['#node']->webform['components']);
//dd($page['content'],'====================');
//  print render($page['content']


  $calculate_js_path = drupal_get_path('module', 'counselee') . "/js/calculate.js";
  drupal_add_js($calculate_js_path);






  $temp_node = node_view($node);
  print drupal_render($temp_node);
  // Print out the main part of the form.
  // Feel free to break this up and move the pieces within the array.
  //  print drupal_render($form['submitted']);
  //
  //  // Always print out the entire $form. This renders the remaining pieces of the
  //  // form that haven't yet been rendered above.
  //  print drupal_render_children($form);
  ?>


</div>

<div class="row-fluid">
  <div class="span12">
    <div id="modal-container-1752468" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="left: auto; width: auto; margin-left: 10%; margin-right: 10%;">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
        <h3 id="myModalLabel">
          Performance Rating Scale
        </h3>
      </div>
      <div class="modal-body">
        <img class="Performance-Rating-Scale" style="width: auto; height: auto;" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/Performance_Rating_Scale.jpg' ?>">
      </div>
      <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
      </div>
    </div>
  </div>
</div>
<script>
  jQuery(document).ready(function() {
    jQuery('#node-span10').removeClass();


  })

</script>