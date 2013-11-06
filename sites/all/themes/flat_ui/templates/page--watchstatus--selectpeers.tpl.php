<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<?php $module_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() . '/' . drupal_get_path('module', 'counsellor') ?>


<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/ui.multiselect.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/flatui-radio.js"></script>
<link type="text/css" href="<?php echo $module_path ?>/css/ui.multiselect.css" rel="stylesheet" />
<link type="text/css" href="<?php echo $module_path ?>/css/jquery-ui.css" rel="stylesheet" />

<script type="text/javascript">
  function submitPeerSelection() {
    clickSubmitButton();

    var providers = "";
    var rreid = "";
    var spstatus = "";
    var userId_flag = "";
    
    providers = getSelectedEmployees();
    rreid = getParamRreid();
    spstatus = getParamSpstatus();
    userId_flag = getParamUserIdflag();
    if(providers!=false){
    jQuery.ajax({
      type: "POST",
      data: {'rreid': rreid, 'providers': providers, 'spstatus': spstatus, 'userId_flag': userId_flag },
      url: '<?php echo $base_path ?>watchstatus/submitupdate/',
      success: function(text) {
        // if (text != '-1') {
          location.reload();
          if(typeof(jQuery("#submit_button").attr("disabled"))!="undefined") {
          jQuery('#submit_button').removeAttr("disabled");
        }
          // window.location.href = "<?php print base_path() . 'mydashboard' ?>";
        // } else {
        //   // window.location.href = "<?php print base_path() . 'selectpeers' ?>";
        //   return;
        // }
      }
    });
  }else{
    hideConfirmdialog();
    return;
  }

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


  function getParamRreid() {
    return jQuery("#rreid").val();
  }
      
  function getParamSpstatus() {
    return jQuery("#spstatus").val();
  }

  function getParamUserIdflag() {
    return jQuery("#userId_flag").val();
  }

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
          ?><div class="tab"><ul class="pr360-tabs"><?php print_render_tabs($tabs);
          ?></ul></div><?php endif; ?>
     <?php if ($messages): ?>
              <div id="messages">
                <div class="container">
                  <?php print $messages; ?>
                </div>
              </div>
            <?php endif; ?>
      <?php print render($page['content'])
?>


      </div>
    </div>
    <!--    <div id="pr_mywokingstage_footer" class="pr_footer">
    <?php // require_once 'footer.tpl.php'; ?>
        </div>-->
  </div>
</div>
<?php require_once 'footer.tpl.php'; ?>