<table border="0" cellpadding="1" cellspacing="1" style="margin-top: 30px" id="watch-table">
  <tbody>
<!--    <tr>
      <td>
        <h6>Review Name:</h6>
      </td>
      <td colspan="3"><input type="text" value="" id="review_from_description" style="color:#34495E;background-color:#ffffff"></td>
    </tr>-->
    <tr>
       <td><h6>Type:</h6></td>
      <td style="text-align:left;vertical-align:middle" id="watch_review_type"> 
      </td>
      <?php if ($basic_info->type == 1): ?>
        <td>
          <h6 id="Project_Name_Lable">Project Name:</h6>
        </td>
        <td style="text-align:left;vertical-align:middle"><?php print render($basic_info->project_name) ?></td>
      <?php else: ?>
        <td>&nbsp;
        </td>
        <td> &nbsp; </td>
      <?php endif; ?>

    </tr>
    <tr>
      <td><h6>Counselee:</h6></td>
      <td style="text-align:left;vertical-align:middle"><?php
        $Login_name = format_login_name($basic_info->employeeName);
        print render($Login_name)
        ?></td>
      <td>
        <h6>Status:</h6>
      </td>
      <td id='watch_review_status' style="text-align:left;vertical-align:middle"></td>
    </tr>
    <tr>
      <td><h6>Anniversary Month:</h6></td>
      <td style="text-align:left;vertical-align:middle"><?php print render($myInfo['my_anniversary_month']) ?></td>
      <td><h6>Counselor:</h6></td>
      <td style="text-align:left;vertical-align:middle"><?php print render($basic_info->counselor_name) ?></td>
    </tr>
<!--    <tr>
      <td>
        <h6>Start Date:</h6>
      </td>
      <td style="text-align:left;vertical-align:middle"> 
        <?php
//        $watch_start_date = unixTimestampToDateArray($basic_info->period_start);
//        $watch_format_start_date = date_to_format($watch_start_date);
//        print render($watch_format_start_date);
        ?>
      </td>
      <td><h6>End Date:</h6></td>
      <td style="text-align:left;vertical-align:middle"> 
        <?php
//        $watch_end_date = unixTimestampToDateArray($basic_info->period_end);
//        $watch_format_end_date = date_to_format($watch_end_date);
//        print render($watch_format_end_date);
        ?>
      </td>
    </tr>-->
    <tr>
      <td>
        <h6>Period From:</h6>
      </td>
      <td style="text-align:left;vertical-align:middle"> 
        <?php
        $watch_from_date = unixTimestampToDateArray($basic_info->time_frame_from);
        $watch_format_from_date = date_to_format($watch_from_date);
        print render($watch_format_from_date);
        ?>
      </td>
      <td><h6>Period To:</h6></td>
      <td style="text-align:left;vertical-align:middle"> 
        <?php
        $watch_to_date = unixTimestampToDateArray($basic_info->time_frame_to);
        $watch_format_to_date = date_to_format($watch_to_date);
        print render($watch_format_to_date);
        ?>
      </td>
    </tr>
  </tbody>
</table>

<script>
  jQuery(document).ready(function() {
    var status_link = get_review_status_tiplink(<?php print render($basic_info->rstatus) ?>);
    jQuery('#watch_review_status').html(status_link);
    
    var type_link =get_review_type_tiplink(<?php print render($basic_info->type) ?>);
    jQuery('#watch_review_type').html(type_link);
    
  });

</script>