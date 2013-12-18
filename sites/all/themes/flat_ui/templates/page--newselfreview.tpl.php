<?php $base_path = get_curPage_base_url() ?>
<?php $base_relatively_path = base_path() ?>
<script type="text/javascript">
  function submitSelfReview(flag) {
    clickSubmitButton();
    var review_type = jQuery('#review_type').val();
    var Project_Name_Text_Not_Trim = jQuery('#Project_Name_Text').val();
    var Project_Name_Text = jQuery.trim(Project_Name_Text_Not_Trim);
//    var review_start_date = jQuery('#review_start_date').val();
//    var review_end_date = jQuery('#review_end_date').val();
    var review_from_date = jQuery('#review_from_date').val();
    var review_to_date = jQuery('#review_to_date').val();
//    var review_from_description = jQuery('#review_from_description').val().trim();

//    if (review_start_date == '') {
//      alert('please enter start date!');
//      hideConfirmdialog();
//      return;
//
//    }
//    if (review_end_date == '') {
//      alert('please enter end date!');
//      hideConfirmdialog();
//      return;
//
//    }
    if (review_from_date == '') {
      alert('please enter period from date!');
      hideConfirmdialog();
      return;

    }
    if (review_to_date == '') {
      alert('please enter period to date!');
      hideConfirmdialog();
      return;

    }

    if (review_type == '1') {
      if (Project_Name_Text == '') {
        alert('please enter a project name!');
        hideConfirmdialog();
        return;
      }
    }
//    if (review_from_description == '') {
//      if (Project_Name_Text == '') {
//        alert('please enter a review name!');
//        hideConfirmdialog();
//        return;
//      }
//    }
//    var nstime = review_start_date.split('/');
//    var start_date_string = nstime[2] + nstime[0] + nstime[1];
//    var real_nstime = parseInt(start_date_string, 10);
//    var netime = review_end_date.split('/');
//    var end_date_string = netime[2] + netime[0] + netime[1];
//    var real_netime = parseInt(end_date_string, 10);
//    if (real_nstime >= real_netime) {
//      alert('Start Date must early than End Date!!');
//      hideConfirmdialog();
//      return;
//    }
    var nrstime = review_from_date.split('/');
    var real_nrstime = parseInt(nrstime[2] + nrstime[0] + nrstime[1], 10);
    var nretime = review_to_date.split('/');
    var real_nretime = parseInt(nretime[2] + nretime[0] + nretime[1], 10);
    if (real_nrstime >= real_nretime) {
      alert('Period From date must early than Period To date!');
      hideConfirmdialog();
      return;
    }

    jQuery.ajax({
      type: "POST",
      data: {'review_type': review_type,  'review_from_date': review_from_date, 'review_to_date': review_to_date, 'Project_Name_Text': Project_Name_Text, 'self_draft_flag': flag},
      url: '<?php echo $base_relatively_path ?>newreview/submitselfreview',
      success: function(text) {
        if (text != '-1') {
          window.location.href = "<?php print base_path() . 'mydashboard' ?>";
        } else {
          window.location.href = "<?php print base_path() . 'newselfreview' ?>";
          return;
        }
      },
      error: function(text) {
        window.location.href = "<?php print base_path() . 'newselfreview' ?>";
        return;
      }
    });
  }

</script>
<?php require_once 'header.tpl.php'; ?>
<div id="pr_mywokingstage_page" class="container">
  <div class="minheight">
    <div id="pr_mywokingstage_content" class="row">
      <div id="pr_mywokingstage_content_left" class="span3">
        <div class="well sidebar-nav">

          <?php
          $navigation_tree = menu_tree(variable_get('menu_main_links_source', 'navigation'));
          print drupal_render($navigation_tree);
          ?>

        </div>
        <script>
          jQuery(document).ready(function() {
            switchProjectName(0);
            jQuery('#review_start_date').val(formatDateTime('0', '0', '0'));
            //          jQuery('#review_end_date').val(formatDateTime('0', '1', '0'));
            //          jQuery('#review_from_date').val(formatDateTime('-1', '0', '0'));
            //          jQuery('#review_to_date').val(formatDateTime('0', '0', '0'));
            jQuery("#tree ul").hide();
            jQuery("#tree li").each(function() {
              var handleSpan = jQuery("<span></span>");
              handleSpan.addClass("handle");
              handleSpan.prependTo(this);

              if (jQuery(this).has("ul").size() > 0) {
                handleSpan.addClass("collapsed");
                handleSpan.click(function() {
                  var clicked = jQuery(this);
                  clicked.toggleClass("collapsed expanded");
                  clicked.siblings("ul").toggle();
                });
              }
            });
          });
        </script>


      </div>
      <div id="pr_mywokingstage_content_right" class="span9">
        <!--            <div class="pr_workingstage_connent">
                        <div id="pr_right_content">-->
        <?php if ($messages): ?>
          <div id="messages">
            <div class="container">
              <?php print $messages; ?>
            </div>
          </div>
        <?php endif; ?>
        <?php print render($page['basic_info'])
        ?>



        <a id="modal-912871" href="#modal-container-912871" role="button" class="btn btn-danger" data-toggle="modal">Start Review</a>
        <!--<a role="button" class="btn" onclick="submitSelfReview(0);">Save as Draft</a>-->

        <div class="row-fluid">
          <div class="span12">
            <div id="modal-container-912871" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn">×</button>
                <h3 id="myModalLabel">
                  Confirm to start review
                </h3>
              </div>
              <div class="modal-body">
                <p>
                  Do you want to start this review? <br>
                  Once the review has been started, you cannot configure the timeslot and participants any more.
                </p>
              </div>
              <div class="modal-footer">
                <img class="loading_img" id="status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
                <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-danger" id="submit_button" onclick="submitSelfReview(1)">Submit</button>
              </div>
            </div>
          </div>
        </div>

        <!--                </div>
                    </div>-->
      </div>
    </div>
  </div>
  <!--    <div id="pr_mywokingstage_footer" class="pr_footer">
  <?php // require_once 'footer.tpl.php'; ?>
      </div>-->
</div>
<?php require_once 'footer.tpl.php'; ?>
