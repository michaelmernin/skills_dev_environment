<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
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
              ?><div class="tab"><ul class="pr360-tabs"><?php print render($tabs['tabs']['output']);
              ?></ul></div><?php endif; ?>



          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<?php require_once 'footer.tpl.php'; ?>