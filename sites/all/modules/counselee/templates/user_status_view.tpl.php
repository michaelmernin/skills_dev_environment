<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<p>&nbsp;</p>
<hr />
<script>
</script>
<table class="table table-hover" id="status_review">
  <caption>
    <h3>Review Status</h3>
  </caption>
  <thead>
    <tr>
      <th>Employee<br></th>
      <th>Type<br></th>
      <th>Description<br></th>
      <th>Start Date<br></th>
      <th>End Date<br></th>
      <th>Status</th>
    </tr>
  </thead>
  <tbody>
    <?php
    $count = 0;
    foreach ($status_info as $status) {
      if ($status->rstatus == 7) {
        //Deleted review
        continue;
      }
      $count++;
      $start_date = unixTimestampToDateArray($status->period_start);
      $end_date = unixTimestampToDateArray($status->period_end);
      $start_date_format = date_to_format($start_date);
      $end_date_format = date_to_format($end_date);
      $startid = $status->rreid;
      $url = '/EnterpriseReview/watchstatus/' . $startid;
      switch ($status->rstatus) {
        case 0:
          //0 for New review start
          $content = '<button id="start_button_' . $startid . '" class="btn btn-danger btn-sm" onclick="start_review(' . $startid . ',' . base_path() . ')" title="Click to start this review." style="color:#ffffff;font-size:15px;">
                      Start Review
                      </button>';
          break;
        case 1:
          //1 for review in draft;
          $content = '<a href="javascript:{void(0)}" title="This review is in draft, reviewer can edit it before submit.">
                     Review in Draft
                     </a>';
          break;
        case 2:
          // 2 for review by counsellor;
          $content = '<a href="javascript:{void(0)}" title="This review now is review by counselor.">
                     Review by Counselor
                     </a>';
          break;
        case 3:
          // 3 for approved by counsellor;
          $content = '<a href="javascript:{void(0)}" title="This review is approved by counselor.">
                     Approved by Counselor
                     </a>';
          break;
        case 4:
          // 4 for joint review;
          $content = '<a href="javascript:{void(0)}" title="Joint review.">
                      Joint review
                      </a>';
          break;
        case 5:
          // 5 for GM review;
          $content = '<a href="javascript:{void(0)}" title="This review is review by GM.">
                      GM Review
                      </a>';
          break;
        case 6:
          // 6 for GM approved;
          $content = '<a href="javascript:{void(0)}" title="This review is approved by GM.">
                      GM Approve
                      </a>';
          break;
      }
      switch ($status->type){
        case 0:
          //  0 for annual review;
          $review_type='Annual review';
          break;
        case 1:
          // 1 for project review;
          $review_type='Project review';
          break;
        case 2:
          // 2 for 3-month review
          $review_type='Three-month review';
          break;
        
        
      }
      
      print '<tr>';
      if ($status->rstatus == 0) {
        print '<td style="vertical-align:middle">'.render($status->employeeName) .'</td>';
      }
      else {
        print '<td style="vertical-align:middle"><a href="javascript:{void(0)}" title="Click to watch this review\'s status." onclick="watch_reveiw_status(\'' . $url . '\')" name="' . $status->rreid . '" style="text-decoration: underline;">' . render($status->employeeName) . '</a></td>';
      }
      print '<td style="text-align:center;vertical-align:middle">'. $review_type.'</td>';
      print '<td style="text-align:center;vertical-align:middle">'. render($status->description).'</td>';
      print '<td style="text-align:center;vertical-align:middle">' . render($start_date_format) . '</td>';
      print '<td style="text-align:center;vertical-align:middle">' . render($end_date_format) . '</td>';
      print '<td style="text-align:center;vertical-align:middle" id="review_status_' . $startid . '">' . $content;
      print '<img class="loading_img" id="status_loading_img_' . $startid . '" title="loading..." style="width: 25px; height: 25px; display: none; text-align:center;" src="' . base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif"></td>';
      print '</tr>';
    }
    //no review data
    if ($count == 0) {
      print '<tr>
             <td colspan="4">There is no review for you.</td>
             </tr>';
    }
    ?>
  </tbody>
</table>
