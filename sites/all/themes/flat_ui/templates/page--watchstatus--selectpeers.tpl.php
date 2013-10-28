<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<?php $module_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() . '/' . drupal_get_path('module', 'counsellor') ?>


<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/ui.multiselect.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/flatui-radio.js"></script>
<link type="text/css" href="<?php echo $module_path ?>/css/ui.multiselect.css" rel="stylesheet" />
<link type="text/css" href="<?php echo $module_path ?>/css/jquery-ui.css" rel="stylesheet" />

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
     }
     */
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
    var val = "";
    var name = "";
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
<div class="minheight">
  <div id="pr_mywokingstage_page" class="container">
    <div id="pr_mywokingstage_content" class="row">
      <div id="pr_mywokingstage_content_left" class="span3">
        <div class="well sidebar-nav">
          <?php
          $navigation_tree = menu_tree(variable_get('menu_main_links_source', 'navigation'));
          print drupal_render($navigation_tree);
          ?>
        </div>
      </div>
      <div id="pr_mywokingstage_content_right" class="span9">
        <?php
        $tabs = menu_local_tasks();
        if ($tabs['tabs']['count'] >= 1):
          ?><div class="tab"><ul class="pr360-tabs"><?php print render($tabs['tabs']['output']);
          ?></ul></div><?php endif; ?>
            <?php print render($page['content']) ?>


      </div>
    </div>
    <!--    <div id="pr_mywokingstage_footer" class="pr_footer">
    <?php // require_once 'footer.tpl.php'; ?>
        </div>-->
  </div>
</div>
<?php require_once 'footer.tpl.php'; ?>