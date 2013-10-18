<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript">

</script>

<?php require_once 'header.tpl.php'; ?>
<div id="pr_mywokingstage_page" class="container">
  <div id="pr_mywokingstage_content" class="row">
    <div id="pr_mywokingstage_content_left" class="span3">
      <?php
      $navigation_tree = menu_tree(variable_get('menu_main_links_source', 'navigation'));
      print drupal_render($navigation_tree);
      ?>
<!--            <script>
            $(document).ready(function() {
                jQuery("#tree ul").hide();
                $("#tree li").each(function() {
                    var handleSpan = jQuery("<span></span>");
                    handleSpan.addClass("handle");
                    handleSpan.prependTo(this);

                    if(jQuery(this).has("ul").size() > 0) {
                        handleSpan.addClass("collapsed");
                        handleSpan.click(function() {
                            var clicked = jQuery(this);
                            clicked.toggleClass("collapsed expanded");
                            clicked.siblings("ul").toggle();
                        });
                    }
                });
            });
        </script>-->


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
          <?php print render($page['user_status']) ?>
        </div>
      </div>
    </div>
  </div>
</div>
<!--    <div id="pr_mywokingstage_footer" class="pr_footer">
    </div>-->
<?php require_once 'footer.tpl.php'; ?>
