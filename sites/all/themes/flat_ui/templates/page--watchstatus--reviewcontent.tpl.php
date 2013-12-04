<?php $base_path = get_curPage_base_url() ?>
<?php $base_relatively_path = base_path() ?>
<?php $module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/highcharts.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/hightchart-no-data.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/modules/exporting.js"></script>

<script src="<?php echo $module_path ?>/assets/javascripts/grid.locale-en.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/jquery.jqGrid.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<?php echo $module_path ?>/assets/stylesheets/ui.jqgrid.css" />

<script type="text/javascript">
  jQuery(document).ready(function() {
    //add a attr to hightchart tooltip for hide a rect
    jQuery(".highcharts-tooltip").attr("visibility", "hidden");
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
            <?php
            $tabs = menu_local_tasks();
            if ($tabs['tabs']['count'] >= 1):
              ?><div class="tab"><ul class="pr360-tabs"><?php print_render_tabs($tabs);
              ?></ul></div><?php endif; ?>

            <?php print render($page['content']) ?>

            <div class="view-self-comment-title">
              <div style="font-weight: 600;float: left">·Overall Rating: | </div>
              <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Self Overall Rating: </div>
              <div class="color-rating-box" id="counselee-overall_rating-content"></div>
            </div>
            <br />
            <br />

            <div>
              <table class="table">

                <thead id="peer-review-form-overall-thead">
                  <tr><th>Reviewer Composite Scores</th>
                    <th>Self</th>
                    <th>Counselor</th>
                  </tr></thead>

                <tbody id="peer-review-form-overall-tbody">
                  <tr><td>Client Engagements</td>
                    <td id="rating_client_engagements">3</td>
                    <td id="counselor_rating_client_engagements">3</td>
                  </tr>
                  <tr><td>Technical Abilities</td>
                    <td id="rating_technical_abilities">3</td>
                    <td id="counselor_rating_technical_abilities">3</td>

                  </tr>
                  <tr><td>Consulting Skills</td>
                    <td id="rating_consulting_skills">3</td>
                    <td id="counselor_rating_consulting_skills">3</td>
                  </tr>
                  <tr><td>Professionalism</td>
                    <td id="rating_professionalism">3</td>
                    <td id="counselor_rating_professionalism">3</td>
                  </tr>
                  <tr><td>Leadership</td>
                    <td id="rating_leadership">3</td>
                    <td id="counselor_rating_leadership">3</td>
                  </tr>
                  <tr><td>Teamwork</td>
                    <td id="rating_teamwork">3</td>
                    <td id="counselor_rating_teamwork">3</td>
                  </tr>
                  <tr><td>Internal Contributions</td>
                    <td id="rating_internal_contributions">3.00</td>
                    <td id="counselor_rating_internal_contributions">3.00</td>
                  </tr>
                  <tr><td>All</td>
                    <td id="rating_all">3.00</td>
                    <td id="counselor_rating_all">3.00</td>
                  </tr>
                </tbody>
              </table>
            </div>




            <script type="text/javascript">
              var basePath = '<?php print get_curPage_base_url() ?>';
              var js_path = basePath + "sites/all/modules/counselee/js/annual_review_approve_overall.js";
              var new_element = document.createElement("script");
              new_element.setAttribute("type", "text/javascript");
              new_element.setAttribute("src", js_path);
              document.body.appendChild(new_element);

              setCounselorOverallRating();
              setCounseleeOverallRating();

              function setCounselorOverallRating() {
                var overallRating = jQuery("#counselor-overall_rating-0").val();
                if (overallRating != "undefined" && overallRating != 0) {
                  jQuery("#counselor-overall_rating-content").val(overallRating);
                }
              }

              function setCounseleeOverallRating() {
                var overallRating = jQuery("#counselee-overall_rating-0").val();
                if (overallRating != "undefined") {
                  jQuery("#counselee-overall_rating-content").append(overallRating);
                }
              }
            </script>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<?php require_once 'footer.tpl.php'; ?>
