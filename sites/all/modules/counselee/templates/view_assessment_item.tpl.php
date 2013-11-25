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
    <div id="header-<?php print $item_num; ?>" >
      <?php print $self_dataset->question; ?> 
    </div>
    <br>

    <!--PieChart-->
    <div style="float:right; margin: 4px 0px 0px; height: 200px; width: 50%;">
      <div id="pie-chart-<?php print $pie_data->id ?>" style="height: 200px; width: 100%;"></div>
      <?php print $pie_data->hiddenvalue ?>
      <script>
        // Build the chart
        jQuery('#pie-chart-<?php print $pie_data->id ?>').highcharts({
          chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
          },
//            title: {
//                text: 'Browser market shares at a specific website, 2010'
//            },
          tooltip: {
            formatter: function() {
              var val = jQuery("#" + this.point.name+"_<?php print $pie_data->id ?>").val();
              // alert(this.point.name);
              var title = this.point.name;

              var textval = title + '<br/>' + val;
              return textval;
            },
            shared: true
          },
          plotOptions: {
            pie: {
              allowPointSelect: true,
              cursor: 'pointer',
              dataLabels: {
                enabled: false
              },
              showInLegend: true
            }
          },
          series: [{
              type: 'pie',
              name: 'Browser share',
              data: [
                {name: '0-1',
                  y:<?php print $pie_data->avg['avg0_1'] ?>,
                  color: '#CC0000'

                },
                {name: '1-2',
                  y:<?php print $pie_data->avg['avg1_1_2'] ?>,
                     color:'#FF6600'

                },
                {name: '2-3',
                  y:<?php print $pie_data->avg['avg2_1_3'] ?>,
                     color:'#FFCC00'

                },
                {name: '3-4',
                  y:<?php print $pie_data->avg['avg3_1_4'] ?>,
                     color:'#CCCC00'

                },
                {name: '4-5',
                  y:<?php print $pie_data->avg['avg4_1_5'] ?>,
                     color:'#99FF00'

                }
              ]
            }]
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
          <textarea id="counselor-comment-<?php print $item_num; ?>" 
                    cols="20" rows="5" 
                    style="margin: 4px 0px 0px; height: 112px; width: 98%;">
          </textarea>
        </div>
      </div>
    </div>
  </div>
  <br>
<input type="hidden" id="rreid" value="<?php print $rreid ?>"/>
<input type="hidden" id="nid" value="<?php print $nid ?>"/>

  <!--Display the peer comment message-->
  <div class="webform-submission-info clearfix">

    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Peer Comment | ·Peer Rating:
      </div>

      <div class="additionalbubble">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="peer-comment-<?php print $item_num ?>" 
                    cols="20" rows="5" 
                    style="margin: 4px 0px 0px; height: 112px; width: 98%;">

          </textarea>
        </div>
      </div>
    </div>
  </div>
</div>
<hr>

