<?php $module_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() . '/' . drupal_get_path('module', 'counsellor') ?>
<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/ui.multiselect.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/flatui-radio.js"></script>
<link type="text/css" href="<?php echo $module_path ?>/css/ui.multiselect.css" rel="stylesheet" />
<link type="text/css" href="<?php echo $module_path ?>/css/jquery-ui.css" rel="stylesheet" />

<h3 colspan="4">Select Peer(s)</h3>

<h6>You can select peers for feedback from the right list box below</h6>

<div style="width:800px;">
<select id="users" class="multiselect" multiple="multiple" name="users[]" style="display: none; width:600px;height:257px;" >
<?php    
    if ($all_employee_array != '') {
      display_unselected_peers($all_employee_array);
    }
    else {
      display_unselected_peers($unselected_array);
      display_selected_peers($selected_array);
    }
    
 
?>

</select>
</div>

<br>
    
      <a id="modal-912871" href="#modal-container-912871" role="button" class="btn" data-toggle="modal">Select</a>



      <div class="row-fluid">
        <div class="span12">
          <div id="modal-container-912871" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
              <h3 id="myModalLabel">
                Confirm to select peers
              </h3>
            </div>
            <div class="modal-body">
              <p>
                Do you want to select these peers? <br>
                Anyway you can add additional employees for peer review.
              </p>
            </div>
            <div class="modal-footer">
              <img class="loading_img" id="status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
              <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-primary" id="submit_button" onclick="submitNewReview()">Submit</button>
            </div>
          </div>
        </div>
      </div>
<script type="text/javascript">
    jQuery(function() {
      renderMultiselect();
    });

    function destoryMultiselect() {
      jQuery('.multiselect').multiselect('destroy');
    }

    function renderMultiselect() {
      jQuery(".multiselect").multiselect({
        sortable: true,
        searchable: true,
        dividerLocation: 0.6
      });
    }

<?php
function display_selected_peers($selected_array) {
  foreach ($selected_array as $item) {
    print '<option value="' . $item . '">' . $item . ' selected="selected"</option>';
  }
}

function display_unselected_peers($unselected_array) {
  foreach ($unselected_array as $item) {
    print '<option value="' . $item . '">' . $item . '"</option>';
  }
}
?>

</script>