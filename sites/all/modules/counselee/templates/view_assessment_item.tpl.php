<?php
// dd($item_num, 'item_num: ');
// dd($rreid, 'rreid');
// dd($nid, 'nid');
// dd($reviewee, 'reviewee');
// dd($clor_rating_comment, '$clor_rating_comment');
// dd($pie_data, 'pie_data');
//
////sites/all/themes/flat_ui/assets/stylesheets/screen.css
//
//$screen_js_path = drupal_get_path('themes', 'flat_ui') . "/assets/stylesheets/screen.css";
//drupal_add_css($screen_js_path);
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
                //                enabled: false,
                useHTML: true,
                formatter: function() {
                  var val = jQuery("#" + this.point.name + "_<?php print $pie_data->id ?>").val();
                  // alert(this.point.name);
                  var title = this.point.name;

                  var textval = '<div class="tooltipbox">' + title + '<br/><table class="toolbox"><tr><td><div class="tooldiv">' + val + '</div></td></tr><table></div>';
                  return textval;
                },
                //                pointFormat: '{series.name}: <b>{point.y}</b><br/>',
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
                   <?php
                   if (trim($self_dataset->comment) != '') {
                     print $self_dataset->comment;
                   }
                   else {
                     print NO_COMMENT;
                   }
                   ?>
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
  <div class="webform-submission-info clearfix" style=" margin-bottom: 10px">

    <div class="webform-submission-info-text">
      <!--<div style="font-weight: 600;float: left">·Peer Comment |</div>-->
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Peer Rating:</div>
      <div class="color-rating-box" id="assessment-content-value-<?php print $item_num; ?>">
        <?php
        if (isset($pie_data) && isset($pie_data->all_avg)) {
          print $pie_data->all_avg;
        }
        else {
          print '';
        }
        ?>

      </div>

    </div>
  </div>

  <br />
  <div>
    <table id="peer-table-<?php print $item_num ?>"></table>
    <div id="peer-div-<?php print $item_num ?>"></div>
    <script>
      var lastSel;
      var peer_data_<?php print $item_num ?> = <?php
        print (trim($peer_json) == '') ? '[]' : $peer_json;
        ?>;
      jQuery("#peer-table-<?php print $item_num ?>").jqGrid({
        datatype: "local",
        height: 250,
        width: 800,
        data: peer_data_<?php print $item_num ?>,
        colNames: ['Peer Name', 'Title', 'Rating', 'Comment', 'Display', 'Nid', 'Cid'],
        colModel: [
          {name: 'peer_name', index: 'peer_name', width: 60, align: 'center', sorttype: "text", cellattr: function(rowId, val, rawObject) {
              return 'title="' + rawObject.peer_name + '\nDouble click for more information."';
            }},
          {name: 'title', index: 'title', width: 100, cellattr: function(rowId, val, rawObject) {
              return 'title="' + rawObject.title + '\nDouble click for more information."';
            }},
          {name: 'rating', index: 'rating', align: 'center', width: 50, cellattr: function(rowId, val, rawObject) {
              return 'title="' + rawObject.rating + '\nDouble click for more information."';
            }},
          {name: 'comment', index: 'comment', width: 300, sorttype: "text", cellattr: function(rowId, val, rawObject) {
              var comment_text = rawObject.comment;
              var i = 200;
              if (comment_text.length > i) {
                comment_text = comment_text.substr(0, i) + '...';
              }
              return 'title="' + comment_text + '\nDouble click for more information."';
            }},
          {name: 'display', index: 'display', align: 'center', width: 50, viewable: false, editable: true, edittype: 'checkbox', editoptions: {value: "True:False"}, formatter: "checkbox", formatoptions: {disabled: false}},
          {name: 'nid', index: 'nid', hidden: true},
          {name: 'cid', index: 'cid', hidden: true}],
        multiselect: false,
        caption: "·Peer Comment",
        pager: '#peer-div-<?php print $item_num ?>',
        rowNum: 10,
        rowList: [10, 20, 30],
        pginput: false,
        viewrecords: true,
        editurl: 'clientArray',
        ondblClickRow: function(id) {
          if (id && id !== lastSel) {
            jQuery('#peer-table-<?php print $item_num ?>').restoreRow(lastSel);
            lastSel = id;
          }
          jQuery('#peer-table-<?php print $item_num ?>').viewGridRow(id, {width: 700, height: 320, dataheight: 200});
//jQuery('#peer-table-<?php print $item_num ?>').editGridRow(id, {width: 700, height: 320, dataheight: 200});
        }
      }).navGrid('#peer-div-<?php print $item_num ?>',
              {view: false, add: false, edit: false, del: false, search: false},
      {}, // use default settings for edit  

              {}, // use default settings for add  

              {}, // delete instead that del:false we need this  

              {} // enable the advanced searching  


      );
    </script>


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
          print -1;
        }
        ?>;
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

