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
      display_unselected_peers($unselected_peers_array);
      // Temporary disable display peer who have been selected.
      // display_selected_peers($selected_peers_array);
    }

 
?>


  </select>
</div>
<br>

<a id="modal-912871" href="#modal-container-912871" role="button" class="btn btn-danger" data-toggle="modal">Select</a>
<br>
<br>

<table class="table table-hover" id="status_review">
  <caption>
    <h3>Peers Status</h3>
  </caption>
  <thead>
    <tr>
      <th>Name<br></th>
      <th>Status<br></th>
      <th>Remind<br></th>
    </tr>
  </thead>
  <tbody>
    <?php
    $count = 0;
    foreach ($peers_status as $status) {
      $count++;
      $startid = $status->rpeid;
      $url = base_path() . 'watchstatus/basicinfo/' . $startid;
      switch ($status->status) {
        case 0:
          //0 not finish
          $content = '<a href="javascript:{void(0)}" title="This review not finished!">
                     Not Finish
                     </a>';
          break;
        case 1:
          //1 finish
          $content = '<a href="javascript:{void(0)}" title="This review have finished!">
                     Finish
                     </a>';
          break;
      }

      if ($status->status == 1) {
        print '<tr title="Click to watch this review\'s status." onclick="watch_reveiw_status(\'' . $url . '\')" name="' . $status->rreid . '" style="cursor:pointer">';
      }
      else {
        print '<tr>';
//        print '<a href="javascript:{void(0)}" title="Click to watch this review\'s status." onclick="watch_reveiw_status(\'' . $url . '\')" name="' . $status->rreid . '" style="text-decoration: underline;"><tr>';
      }
      print '<td style="vertical-align:middle">' . render($status->providerName) . '</td>';
      print '<td style="text-align:center;vertical-align:middle">' . $content . '</td>';
      print '<td style="text-align:center;vertical-align:middle"><a href="javascript:{void(0)}" title="Remind peers">Send Email</a></td>';
      print '<img class="loading_img" id="status_loading_img_' . $startid . '" title="loading..." style="width: 25px; height: 25px; display: none; text-align:center;" src="' . base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif"></td>';
      print '</tr>';
    }
//no review data
    if ($count == 0) {
      print '<tr>
             <td colspan="3">There is no review for you.</td>
             </tr>';
    }
    ?>
  </tbody>
</table>

<br>

<input type="hidden" id="rreid" value="<?php print $rreid ?>"/>
<input type="hidden" id="spstatus" value="<?php print $spstatus ?>"></div>
<input type="hidden" id="userId_flag" value="<?php print $userId_flag ?>"></div>



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
          Anyway you can add additional employees for peer review. All the peers that have been selected cannot be removed!
        </p>
      </div>
      <div class="modal-footer">
        <img class="loading_img" id="status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-danger" id="submit_button" onclick="submitPeerSelection()">Submit</button>

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
    print '<option value="' . $item . '" selected="selected">' . $item . '</option>';
  }
}

function display_unselected_peers($unselected_array) {
  foreach ($unselected_array as $item) {
    print '<option value="' . $item . '">' . $item . '</option>';
  }
}
?>

</script>