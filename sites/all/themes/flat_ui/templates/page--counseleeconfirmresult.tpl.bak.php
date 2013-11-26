<?php $base_path = get_curPage_base_url(); ?>
<script type="text/javascript">

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


        <?php
        $tabs = menu_local_tasks();
        if ($tabs['tabs']['count'] >= 1):
          ?><div class="tab"><ul class="pr360-tabs"><?php print_render_tabs($tabs);
          ?></ul></div><?php endif; ?>

        <?php print render($page['content']); ?>
        <div class="pr_workingstage_connent">
          <div id="pr_right_content">           

          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<?php require_once 'footer.tpl.php'; ?>
<!--    <div id="pr_mywokingstage_footer" class="pr_footer">
    </div>-->
