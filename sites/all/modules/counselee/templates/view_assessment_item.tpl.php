<?php
// dd($item_num, 'item_num: ');
// dd($rreid, 'rreid');
// dd($nid, 'nid');
// dd($reviewee, 'reviewee');
// dd($self_dataset, 'self_dataset');
?>
<div class="webform-ssion-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-<?php print $item_num; ?>" value="<?php print $self_dataset->form_key; ?>">
      <?php print $self_dataset->question; ?>
    </div>
    <br>
    <div class="view-self-comment-chart">
      <!--PieChart-->
      <?php if ($pie_data->have_peer_data): ?>

        <div style="float:right; margin: 4px 0px 0px; height: 200px; width: 50%;">
          <div id="pie-chart-<?php print $pie_data->id ?>" style="height: 200px; width: 100%;"></div>
          <?php print $pie_data->hiddenvalue ?>
          <input type="hidden" id="pie-chart-all-avg-<?php print $item_num; ?>" value="<?php print $pie_data->all_avg ?>"/>
          <script>
            // Build the chart
            jQuery('#pie-chart-<?php print $pie_data->id ?>').highcharts({
              chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
              },
              title: {
                text: null
              },
              credits: {
                enabled: false
              },
              tooltip: {
                useHTML: true,
                formatter: function() {
                  var val = jQuery("#" + this.point.name + "_<?php print $pie_data->id ?>").val();
                  // alert(this.point.name);
                  var title = this.point.name;

                  var textval = '<div class="tooltipbox">' + title + '<br/><table class="toolbox"><tr><td><div class="tooldiv">' + val + '</div></td></tr><table></div>';
                  return textval;
                },
                shared: true
              },
              exporting: {
                //true for exporting
                enabled: false
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
                  name: 'Rating Pie Chart',
                  data: [
                    {name: 'NA',
                      y:<?php print $pie_data->avg['avgNA'] ?>,
                      color: '#000000'

                    },
                    {name: '0-1',
                      y:<?php print $pie_data->avg['avg0_1'] ?>,
                      color: '#990000'

                    },
                    {name: '1-2',
                      y:<?php print $pie_data->avg['avg1_1_2'] ?>,
                      color: '#FF6600'

                    },
                    {name: '2-3',
                      y:<?php print $pie_data->avg['avg2_1_3'] ?>,
                      color: '#FFCC00'

                    },
                    {name: '3-4',
                      y:<?php print $pie_data->avg['avg3_1_4'] ?>,
                      color: '#99CC33'

                    },
                    {name: '4-5',
                      y:<?php print $pie_data->avg['avg4_1_5'] ?>,
                      color: '#009900'

                    }
                  ]
                }]
            });
	
          </script>
        </div>
      <?php else: ?>
        <div style="float:right; margin: 4px 0px 0px; height: 200px; width: 50%;">
          <div id="pie-chart-no-data-<?php print $pie_data->id ?>" style="height: 200px; width: 100%;"></div>
          <input type="hidden" id="pie-chart-all-avg-<?php print $item_num; ?>" value="<?php print $pie_data->all_avg ?>"/>
          <script>
            // Build the no data chart
            jQuery('#pie-chart-no-data-<?php print $pie_data->id ?>').highcharts({
              chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
              },
              title: {
                text: null
              },
              credits: {
                enabled: false
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

      <?php endif ?>

      <div style="margin: 4px 0px 0px; height: 17px; width: 50%;">
        <div style="font-weight: 600;float: left">·Self Comment |</div>
        <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Self Rating:</div>
        <div class="color-rating-box" id="assessment-content-value-<?php print $item_num; ?>">
          <?php
          if ($self_dataset->rating != 0) {
            print $self_dataset->rating;
          }
          elseif ($self_dataset->rating == 0) {
            print 'N/A';
          }
          ?>
        </div>


      </div>
      <div style="margin: 4px 0px 0px; height: 150px; width: 50%;">
        <div class="additionalbubbleP">
          <div style="padding: 0 10px 5px;margin-bottom: 5px;">

            <div id="comment-content-value-<?php print $item_num; ?>"
                 style="margin: 4px 0px 0px; height: 140px; width: 96%;overflow-y:auto;">
                   <?php print $self_dataset->comment ?>
            </div>
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
        ·Counselor Comment | ·Counselor Rating:
        <select id="counselor-rating-<?php print $item_num; ?>">
          <option value="0">N/A</option>
          <option value="1">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
          <option value="4">4</option>
          <option value="5">5</option>
        </select>
      </div>
		
      <!--<div class="additionalbubble">-->
      <div>
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="counselor-comment-<?php print $item_num; ?>" cols="20" rows="5" style="margin: 4px 0px 0px; height: 112px; width: 98%;"><?php
          if (isset($clor_rating_comment) && isset($clor_rating_comment->clor_comment)) {
            print $clor_rating_comment->clor_comment; 
          }
          else {
            print '';
          }
          ?></textarea>
        </div>
      </div>
    </div>
  </div>
  <br>

  <input type="hidden" id="rreid-<?php print $item_num; ?>" value="<?php print $rreid ?>"/>
  <input type="hidden" id="nid-<?php print $item_num; ?>" value="<?php print $nid ?>"/>
  <input type="hidden" id="total_item_count-<?php print $item_num; ?>" value="<?php print $total_item_count ?>"/>

  <!--Display the peer comment message-->
  <div class="webform-submission-info clearfix">

    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Peer Comment | ·Peer Rating: <?php 
      if (isset($clor_rating_comment) && isset($clor_rating_comment->peer_avg_rating)) {
        print $clor_rating_comment->peer_avg_rating;
      }
      else {
        print '';
      } ?>
      </div>

      <!--<div class="additionalbubble">-->
      <div>
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="peer-comment-<?php print $item_num ?>"  cols="20" rows="5" style="margin: 4px 0px 0px; height: 112px; width: 98%;"><?php
                   foreach ($unread_comment as $item) {
                     print $item;
                   }
                   ?></textarea>
        </div>
      </div>
    </div>
  </div>
</div>
<hr>
<script>
		checkValue();	
		function checkValue() {
			var range = [0, 1, 2, 3, 4, 5];
			var rating = <?php 
      if (isset($clor_rating_comment) && isset($clor_rating_comment->rating)) {
        print $clor_rating_comment->rating;
      }
      else {
        print '';
      } ?>;
			if (inArray(rating, range)) {
				jQuery("#counselor-rating-<?php print $item_num; ?>").val(rating);
			}
			else {
				jQuery("#counselor-rating-<?php print $item_num; ?>").val(3);
			}
		}
		

		function inArray(needle, haystack) {
			var length = haystack.length;
			for (var i = 0; i < length; ++i) {
				if (haystack[i] == needle) {
					return true;
				}
			}
			return false;
		}
</script>

