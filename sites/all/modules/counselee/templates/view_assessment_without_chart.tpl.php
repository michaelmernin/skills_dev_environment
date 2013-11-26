<?php
	// dd($item_num, 'item_num: ');
	// dd($rreid, 'rreid');
	// dd($nid, 'nid');
	// dd($reviewee, 'reviewee');
	// dd($self_dataset, 'self_dataset');
?>
<div class="webform-submission-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-<?php print $item_num; ?>" value="<?php print $self_dataset->form_key; ?>"  >
      <?php print $self_dataset->question; ?> 
    </div>
    <br>
    <div style="float:right; margin: 4px 0px 0px; height: 200px; width: 50%;">
        <div id="withoupie-chart-no-data-<?php print $item_num; ?>" style="height: 200px; width: 100%;"></div>

        <script>
          // Build the no data chart
          jQuery('#withoupie-chart-no-data-<?php print $item_num; ?>').highcharts({
            chart: {
              plotBackgroundColor: null,
              plotBorderWidth: null,
              plotShadow: false
            },
            title: {
              text: null
            },
            exporting: {
              //true for exporting
              enabled: false
            },
            series: [{
                type: 'pie',
                name: 'no data',
                data: []
              }],
            lang: {
              // Custom language option            
              //noData: "Nichts zu anzeigen"    
            },
            /* Custom options */
            noData: {
              // Custom positioning/aligning options
              position: {
                //align: 'right',
                //verticalAlign: 'bottom'
              },
              // Custom svg attributes
              attr: {
                //'stroke-width': 1,
                //stroke: '#cccccc'
              },
              // Custom css
              style: {
                //fontWeight: 'bold',     
                //fontSize: '15px',
                //color: '#202030'        
              }
            },
            plotOptions: {
              pie: {
                allowPointSelect: false,
                cursor: 'pointer',
                dataLabels: {
                  enabled: false
                },
                showInLegend: true
              }
            },
          });

        </script>

      </div>

    <div style="margin: 4px 0px 0px; height: 17px; width: 350px;">
      <div style="font-weight: 600;float: left">· Self Comment | </div>
      <div id="assessment-content-value-<?php print $item_num; ?>" 
           style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">· Self Rating:
        <font color="red"> <?php print $self_dataset->rating; ?>
        </font>
      </div>

    </div>
    <div style="margin: 4px 0px 0px; height: 112px; width: 200px;">
      <div class="additionalbubbleP">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">

          <div id="comment-content-value-<?php print $item_num; ?>" 
               style="margin: 4px 0px 0px; height: 112px; width: 96%;overflow-y:auto;">
                 <?php print $self_dataset->comment ?>
          </div>
        </div>
      </div>
    </div>

  </div>
  <br>
  <!--Display the counselor comment message-->
  <div class="webform-submission-info clearfix">
    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">
        · Counselor Comment | · Counselor Rating:
        <select id="counselor-rating-<?php print $item_num; ?>">
          <option value="0"> N/A </option>
          <option value="1"> 1 </option>
          <option value="2"> 2 </option>
          <option value="3" selected="selected"> 3 </option>
          <option value="4"> 4 </option>
          <option value="5"> 5 </option>
        </select>
      </div>

      <div class="additionalbubble">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
            <textarea id="counselor-comment-<?php print $item_num; ?>" cols="20" rows="5" style="margin: 4px 0px 0px; height: 112px; width: 98%;"></textarea>
        </div>
      </div>
    </div>
  </div>
  <br>
<input type="hidden" id="rreid-<?php print $item_num; ?>" value="<?php print $rreid ?>"/>
<input type="hidden" id="nid-<?php print $item_num; ?>" value="<?php print $nid ?>"/>
<input type="hidden" id="total_item_count-<?php print $item_num; ?>" value="<?php print $total_item_count ?>"/>


  <!--Display the peer comment message-->
<!--   <div class="webform-submission-info clearfix">

    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Peer Comment | ·Peer Rating:
      </div>

      <div class="additionalbubble">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="peer-comment-" 
                    cols="20" rows="5" 
                    style="margin: 4px 0px 0px; height: 112px; width: 98%;">

          </textarea>
        </div>
      </div>
    </div>
  </div>
 -->
</div>
<hr>

