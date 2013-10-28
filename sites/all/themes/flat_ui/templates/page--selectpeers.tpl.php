<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript">

  function submitNewReview() {
    clickSubmitButton();
    /*var review_type = jQuery('#review_type').val();
    var Project_Name_Text = jQuery('#Project_Name_Text').val().trim();
    var review_start_date = jQuery('#review_start_date').val();
    var review_end_date = jQuery('#review_end_date').val();
    var review_from_date = jQuery('#review_from_date').val();
    var review_to_date = jQuery('#review_to_date').val();
    var review_from_description = jQuery('#review_from_description').val().trim();
    if (review_type == '1') {
      if (Project_Name_Text == '') {
        alert('please enter a project name!');
        hideConfirmdialog();
        return;
      }
    }
    if (review_from_description == '') {
      if (Project_Name_Text == '') {
        alert('please enter a review name!');
        hideConfirmdialog();
        return;
      }
    }
    var nstime = review_start_date.split('/');
    var real_nstime = parseInt(nstime[2] + nstime[0] + nstime[1]);
    var netime = review_end_date.split('/');
    var real_netime = parseInt(netime[2] + netime[0] + netime[1]);
    if (real_nstime >= real_netime) {
      alert('start date must early than end date!');
      hideConfirmdialog();
      return;
    }
    var nrstime = review_from_date.split('/');
    var real_nrstime = parseInt(nrstime[2] + nrstime[0] + nrstime[1]);
    var nretime = review_to_date.split('/');
    var real_nretime = parseInt(nretime[2] + nretime[0] + nretime[1]);
    if (real_nrstime >= real_nretime) {
      alert('start date must early than end date!');
      hideConfirmdialog();
      return;
    }*/
    var employees = getSelectedEmployees();
    var radio_val = get_radio_val();
    jQuery.ajax({
      type: "POST",
      data: {'review_type': review_type, 'review_start_date': review_start_date, 'review_end_date': review_end_date, 'review_from_date': review_from_date, 'review_to_date': review_to_date, 'Project_Name_Text': Project_Name_Text, 'review_from_description': review_from_description},
      url: '<?php echo $base_path ?>newreview/submitreview/' + employees + '/' + radio_val,
      success: function(text) {
        if (text != '-1') {
          window.location.href = "<?php print base_path() . 'mydashboard' ?>";
        } else {
          window.location.href = "<?php print base_path() . 'newreview' ?>";
          return;
        }
      }
    });

  }

  function getSelectedEmployees() {
    var val ="";
    var name ="";
    var count = 0;
    var pos = 0;
    jQuery("#users option:selected").each(function() {
      val += jQuery(this).val() + ",";
      count++;
    });
    if (count == 0) {
      alert("You should choose at least 1 people!");
      jQuery('#loading_data_participant').css("display", 'none');
      return false;
    }
    jQuery("#users option:selected").each(function() {
      name += jQuery(this).text() + "-";
    });
    pos = name.lastIndexOf("-");
    name = name.slice(0, pos);
    return name;
  }

  /**
   * get_radio_val
   * @return radio_val: 0 for individually, 1 for all.
  **/
 /* function get_radio_val(){
    //for Anfernee
    var flag = "";
    flag = jQuery("input:radio:checked").val();
    return flag;
  }*/
</script>
<?php require_once 'header.tpl.php'; ?>
<div id="pr_mywokingstage_page" class="container">
  <div id="pr_mywokingstage_content" class="row">
    <div id="pr_mywokingstage_content_left" class="span3">
      <?php
      $navigation_tree = menu_tree(variable_get('menu_main_links_source', 'navigation'));
      print drupal_render($navigation_tree);
      ?>
     
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
      <?php print render($page['select_peers']);
      ?>
      



      <a id="modal-912871" href="#modal-container-912871" role="button" class="btn" data-toggle="modal">Start Review</a>


      <div class="row-fluid">
        <div class="span12">
          <div id="modal-container-912871" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
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
              <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-primary" id="submit_button" onclick="submitNewReview()">Submit</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!--    <div id="pr_mywokingstage_footer" class="pr_footer">
  <?php // require_once 'footer.tpl.php'; ?>
      </div>-->
</div>
<?php require_once 'footer.tpl.php'; ?>