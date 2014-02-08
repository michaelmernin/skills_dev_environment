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

<link rel="stylesheet" type="text/css" media="screen" href="<?php echo $module_path ?>/assets/stylesheets/ui.jqgrid.css" />

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
						<!-- Render main content -->
            <?php print render($page['content']) ?>
						 <!--Add the overall rating display part-->
            <?php print get_overall_rating_part(current_path()); ?>
						<!-- Render submit button -->
						<?php	$url = $_SERVER['REQUEST_URI'];
						$url_array = explode('/', $url);
						$rreid = $url_array[count($url_array)-1];
						$button_content = theme('page_audit_review_btn_approve', array('rreid' => $rreid));
						print render($button_content); ?>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<?php require_once 'footer.tpl.php'; ?>
