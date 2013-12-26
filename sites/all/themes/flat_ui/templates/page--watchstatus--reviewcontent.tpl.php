<?php $base_path = get_curPage_base_url() ?>
<?php $base_relatively_path = base_path() ?>
<?php $module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<?php $calculate_path = get_curPage_base_url() . drupal_get_path('module', 'counselee') ?>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/highcharts.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/hightchart-no-data.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/highcharts/modules/exporting.js"></script>

<script src="<?php echo $module_path ?>/assets/javascripts/grid.locale-en.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/jquery.jqGrid.min.js"></script>
<script src="<?php echo $calculate_path ?>/js/calculate.js"></script>
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
              <div style="font-weight: 600;float: left">·Counselor Overall Rating:  </div>
              <div class="color-rating-box" id="counselor-overall_rating-content"></div>
              <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">| ·Self Overall Rating: </div>
              <div class="color-rating-box" id="counselee-overall_rating-content"></div>
            </div>
            <br />
            <br />

            <!--Add the overall rating display part-->
            <?php print get_overall_rating_part(current_path()); ?>


          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<?php require_once 'footer.tpl.php'; ?>
