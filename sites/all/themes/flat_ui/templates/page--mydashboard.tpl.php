<?php $base_path = get_curPage_base_url() ?>
<?php $module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<script src="<?php echo $module_path ?>/assets/javascripts/grid.locale-en.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/jquery.jqGrid.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<?php echo $module_path ?>/assets/stylesheets/ui.jqgrid.css" />
<script>
  jQuery(document).ready(function() {
    jQuery('#userStatusSearchIcon').toggle(
            function() {
              jQuery(this).hide();
            },
            function() {
              jQuery(this).show();
            }
    );




  });



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
            <?php print render($page['my_working_stage']); ?>
            <?php print render($page['user_status']) ?>

            <!--<div id="containertest" style="min-width: 310px; height: 400px; margin: 0 auto"></div>-->
            <h3>Review Status</h3>
            <hr>
            <div>
              <table id="userStatusTable"></table>
              <div id="userStatusTableBar"></div>
            </div>
            <!--            <div id="userStatusSearch">
                          <span class="todo-search-icon" id="userStatusSearchIcon"></span>
                          <div id="userStatusSearch-content" class="userStatusSearch-content-none"></div>

                        </div>-->
            <script>


              jQuery("#userStatusTable").jqGrid({
                url: '<?php print $base_path . 'userstatus' ?>',
                datatype: "json",
                height: 250,
                colNames: ['Counselee', 'Review Name', 'Period From', 'Period To', 'Type', 'Status', 'ReviewID', 'CounselorFlag', 'StatusNum'],
                colModel: [
                  {name: 'employeeName', index: 'employeeName', width: 60, align: 'center', sortable: true, cellattr: function(rowId, val, rawObject) {
                      return rowhint(rowId, val, rawObject);
                    }},
                  {name: 'review_name', index: 'review_name', width: 100, align: "center", cellattr: function(rowId, val, rawObject) {
                      return rowhint(rowId, val, rawObject);
                    }},
                  {name: 'period_from', index: 'period_from', search: false, width: 70, align: "center", cellattr: function(rowId, val, rawObject) {
                      return rowhint(rowId, val, rawObject);
                    }},
                  {name: 'period_to', index: 'period_to', search: false, width: 70, align: "center", cellattr: function(rowId, val, rawObject) {
                      return rowhint(rowId, val, rawObject);
                    }},
                  {name: 'type', index: 'type', width: 70, align: "center", stype: "select", searchoptions: {
                      value: "0:Annual review;1:Project review;2:Three-month review",
                      defaultValue: "0"
                    }, cellattr: function(rowId, val, rawObject) {
                      return rowhint(rowId, val, rawObject);
                    }},
                  {name: 'status', index: 'status', width: 100, align: "center", stype: "select", searchoptions: {
                      value: "1:Review in Draft;2:Review by Counselor;3:Approved by Counselor;4:Joint review;5:GM Review;6:GM Approve",
                      defaultValue: "1"
                    }, cellattr: function(rowId, val, rawObject) {
//                 console.log(rawObject[7]);
                      return statusrowhint(rowId, val, rawObject);
                    }},
                  {name: 'review_id', index: 'review_id', hidden: true},
                  {name: 'counselor_flag', index: 'counselor_flag', hidden: true},
                  {name: 'status_num', index: 'status_num', hidden: true}
                ],
                gridview: true,
                rowattr: function(rd) {
//                  console.log(rd);
                  if (rd.counselor_flag === "1") { // verify that the testing is correct in your case
                    return {"class": "myAltRowClass"};
                  } else {
                    return {"class": "counseleeAltRowClass"};
                  }
                },
                onSelectRow: function(id, status) {
//                  console.log(id);
//                  console.log(status);
                  var rowData = jQuery(this).getRowData(id);
                  var rreid = rowData.review_id;
                  window.location.href = "<?php print $base_path . 'watchstatus/basicinfo/' ?>" + rreid;
//                  console.log(rreid);
                },
                multiselect: false,
                rownumbers: true,
                autowidth: true,
                caption: "Review Status",
                rowNum: 10,
                pginput: false,
//                sortable: true,
                rowList: [10, 20, 30],
                pager: '#userStatusTableBar',
                //    sortname: 'counselee',
                //    sortorder: "desc",
                viewrecords: true
              });
              jQuery("#userStatusTable").jqGrid('navGrid', '#userStatusTableBar', {view: false, add: false, edit: false, del: false, search: false});
              jQuery("#userStatusTable").jqGrid('filterToolbar', {searchOnEnter: false});
            </script>

          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<?php require_once 'footer.tpl.php'; ?>
<!--    <div id="pr_mywokingstage_footer" class="pr_footer">
    </div>-->
