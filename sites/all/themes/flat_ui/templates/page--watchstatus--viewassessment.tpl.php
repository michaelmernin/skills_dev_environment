<?php $base_path = get_curPage_base_url() ?>
<?php $base_relatively_path = base_path() ?>
<?php $module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<?php $counselee_path = get_curPage_base_url() . drupal_get_path('module', 'counselee') ?>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/highcharts.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/hightchart-no-data.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/modules/exporting.js"></script>

<script src="<?php echo $module_path ?>/assets/javascripts/grid.locale-en.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/jquery.jqGrid.min.js"></script>
<script src="<?php echo $counselee_path ?>/js/calculate.js"></script>
<script src="<?php echo $counselee_path ?>/js/checkCounselorSubmit.js"></script>

<link rel="stylesheet" type="text/css" media="screen" href="<?php echo $module_path ?>/assets/stylesheets/ui.jqgrid.css" />

<script type="text/javascript">
  jQuery(document).ready(function() {
    //add a attr to hightchart tooltip for hide a rect
//    jQuery(".highcharts-tooltip").attr("visibility", "hidden");
//    var highChartsTooltipJqueryObj;
//    jQuery(".highcharts-tooltip span").hover(
//            function() {
//              jQuery(this).mousemove(function(e) {
//
//
////                jQuery(this).parent(".highcharts-tooltip").css("visibility", "visible");
//                jQuery(this).parentsUntil(".highcharts-container > g").find(".highcharts-tooltip").attr("visibility", "visiable");
//
//              });
////              jQuery(this).addClass("hover");
//            },
//            function() {
////              jQuery(this).removeClass("hover");
//            }
//    );

  });

  function submitCounselorAssessment() {
    var rreid = getRreid();
    var nid = getNid();
    var count = getCount();
    var items = iterateItems(count);
    var overallRating = getOverallRating();

    if (items != false) {
      jQuery.ajax({
        type: "POST",
        data: {'rreid': rreid, 'nid': nid, 'count': count, 'items': items, 'overallRating': overallRating},
        url: '<?php echo $base_relatively_path ?>watchstatus/submitcounselorassessment',
        success: function(text) {
          // window.location.href = "<?php print base_path() . 'watchstatus/basicinfo/' ?>" + rreid;
          location.reload();
          // if(typeof(jQuery("#submit_button").attr("disabled"))!="undefined") {
          // jQuery('#submit_button').removeAttr("disabled");
        }
        // else {
        //   window.location.href = "<?php // print base_path() . 'viewassessment'                 ?>";
        //   return;
        // }
      });
    }

  }

  function approveCounseleeAssessment() {
    counselor_approve_clickSubmitButton();

    // var area
    var rreid = getRreid();
    var nid = getNid();
    var count = getCount();
    var items = iterateItems(count);
    var overallRating = getOverallRating();

    if (items != false) {
      jQuery.ajax({
        type: "POST",
        data: {'rreid': rreid, 'nid': nid, 'count': count, 'items': items, 'overallRating': overallRating},
        url: '<?php echo $base_relatively_path ?>watchstatus/approvecounseleeassessment',
        success: function(text) {
          if (text == 1) {
            window.location.href = "<?php print base_path() . 'watchstatus/reviewcontent/' ?>" + rreid;
          }
          else {
            location.reload();
          }
        }
      });
    }
    else {
      hideConfirmdialog();
      return;
    }
  }

  function disapproveCounseleeSelfAssessment() {
    counselor_disapprove_clickSubmitButton();
    //variables
    var rreid = getRreid();
    var nid = getNid();
    var count = getCount();
    var items = iterateItems(count);
    var overallRating = getOverallRating();

    var rejectComments = getCounselorRejectComment();

    if (rejectComments != false) {
      jQuery.ajax({
        type: "POST",
        data: {'rreid': rreid, 'nid': nid, 'count': count, 'items': items, 'overallRating': overallRating, 'rejectComments': rejectComments},
        url: '<?php echo $base_relatively_path ?>watchstatus/rejectcounseleeassessment',
        success: function(text) {
          if (text == 1) {
            window.location.href = "<?php print base_path() . 'watchstatus/reviewcontent/' ?>" + rreid;
          }
          else {
            location.reload();
          }
        }
      });
    }
    else {
      // hideConfirmdialog();
      return;
    }
  }

  function iterateItems(count) {
    var items = new Array();
    for (var i = 0; i < count; i++) {
      var header = getHeaderInfo(i);
      var rating = getCounselorRating(i);
      var peerAvgRating = getPeerAvgRating(i);
      var comment = getCounselorComment(i);
      var revisedComment = getCounselorRevisedComment(i);
      var content = {
        "header": header,
        "rating": rating,
        "comment": comment,
        "peerAvgRating": peerAvgRating,
        "revisedComment": revisedComment,
      }
      var jsonString = JSON.stringify(content);
      items[i] = jsonString;
    }
    ;
    return items;
  }

  function getCount() {
    return jQuery("#total_item_count-1").val();
  }

  function getRreid() {
    return jQuery("#rreid-0").val();
  }

  function getNid() {
    return jQuery("#nid-0").val();
  }

  function getHeaderInfo(num) {
    var val = "";
    val = jQuery("#header-" + num).attr("value");
    return val;
  }

  function getCounselorRating(num) {
    return jQuery("#counselor-rating-" + num + " option:selected").val();
  }

  function getPeerAvgRating(num) {
    return jQuery("#pie-chart-all-avg-" + num).val();
  }

  function getCounselorComment(num) {
    return jQuery("#counselor-comment-" + num).val();
  }

  function getCounselorRevisedComment(num) {
    var ids = jQuery("#peer-hidjsoncount-" + num).val();
//    console.log(ids);
    var rowDataRel = new Array();
    for (var i = 1; i <= ids; i++)
    {
      var rowId = i;
      var rowData = jQuery("#peer-table-" + num).jqGrid('getLocalRow', rowId);
//      var json_string = "{\"nid\":"+ rowData.nid + ",\"cid\"=" + rowData.cid + ",\"display\":" + rowData.display + "}";

      var json_string = {
        "nid": rowData.nid,
        "cid": rowData.cid,
        "display": rowData.display,
      }
      rowDataRel[i - 1] = JSON.stringify(json_string);
//      console.log(rowDataRel[i]);
//      console.log(rowId);
    }

//    return JSON.stringify(jQuery("#peer-table-" + num).getRowData());
    return rowDataRel;


  }

  function getCounselorRejectComment() {
    return jQuery("#counselor_reject_reason").val();
  }

  function getOverallRating() {
    return jQuery("#counselor-overall_rating-content").val();
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
        <div class="pr_workingstage_connent">
          <div id="pr_right_content">
            <?php if ($messages): ?>
              <div id="messages">
                <div class="container">
                  <?php print $messages; ?>
                </div>
              </div>
            <?php endif; ?>
            <?php
            $tabs = menu_local_tasks();
            if ($tabs['tabs']['count'] >= 1):
              ?><div class="tab"><ul class="pr360-tabs"><?php print_render_tabs($tabs);
              ?></ul></div><?php endif; ?>


            <a href="#" id="gotoTopBtn" style="display: none">click here to go to page top</a>
            <div class='page-title'></div>

            <?php print render($page['content']) ?>

            <div class="view-self-comment-title">
              <div style="font-weight: 600;float: left">·Counselor Overall Rating: | </div>
              <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Self Overall Rating: </div>
              <div class="color-rating-box" id="counselee-overall_rating-content"></div>
              <br />
              <br />
            </div>
            <div id="counselor-overall_rating" style="clear: left;margin-top: 3px;">
              <select id="counselor-overall_rating-content" >
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
              </select>
            </div>
            <br />
            <br />

            <!--Add the overall rating display part-->
            <?php print get_overall_rating_part(current_path()); ?>

            <div>
              <div class="draftbutton" style="float: left; margin-right: 15px;">
                <input class="webform-draft form-submit btn btn-large" type="submit" value="Save As Draft" name="op" formnovalidate="formnovalidate" onclick="submitCounselorAssessment()">
              </div>
              <div class="draftbutton" style="float: left; margin-right: 15px;">
                <a id="modal-912872" href="#modal-container-912872" role="button" data-toggle="modal"><input formnovalidate="formnovalidate" class="webform-draft form-submit btn btn-large" type="submit" name="op" value="Disapprove"></a>
                <div class="row-fluid">
                  <div class="span12">
                    <div id="modal-container-912872" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                      <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
                        <h3 id="myModalLabel">
                          Confirm to Disapprove.
                        </h3>
                      </div>
                      <div class="modal-body">
                        <p>
                          Do you want to disapprove this self assessment? <br>
                          Please write down reason below.
                        </p>
                        <textarea id="counselor_reject_reason" 
                                  cols="30"
                                  rows="5"
                                  style="width: 500px" 
                                  maxlength="500"
                                  onchange="textareaLimitWord(this, 255)"
                                  onkeydown ="textareaLimitWord(this, 255)"
                                  placeholder="Please write reasons here..."></textarea>
                        <label>Limited to 255 words.</label>
                      </div>
                      <div class="modal-footer">
                        <img class="loading_img" id="disapprove_status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
                        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-danger" id="counselor_disapprove_btn" onclick="disapproveCounseleeSelfAssessment()">Disapprove</button>

                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="draftbutton" style="float: left; margin-right: 15px;">
                <!-- <a id="modal-912871" href="#modal-container-912871" role="button" class="btn btn-danger" data-toggle="modal">Save as draft</a> -->
                <a id="modal-912871" href="#modal-container-912871" role="button" data-toggle="modal"><input class="webform-submit button-primary form-submit btn btn-large btn-primary" type="submit" name="op" value="Approve"></a>
                <div class="row-fluid">
                  <div class="span12">
                    <div id="modal-container-912871" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                      <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
                        <h3 id="myModalLabel">
                          Confirm to Submit sheet.
                        </h3>
                      </div>
                      <div class="modal-body">
                        <p>
                          Do you want to submit this form? <br>
                          When you submit this form, all the contents cannot be revised!
                        </p>
                      </div>
                      <div class="modal-footer">
                        <img class="loading_img" id="approve_status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
                        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-danger" id="counselor_submit_btn" onclick="approveCounseleeAssessment()">APPROVE</button>

                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>


          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<?php require_once 'footer.tpl.php'; ?>


<script>

              function textareaLimitWord(ta, maxLength)
              {
                if (ta.value.length > maxLength) {
                  ta.value = ta.value.substring(0, maxLength);
                }
              }

</script>
