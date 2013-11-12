<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript">
  function submitCounselorSheet() {
    clickSubmitButton();

    // var area
    var nid = getNid();
    var count = getCount();
    var headerInfo = getHeaderInfo();
    var ratings = new Array();
    ratings = generateRatingArray(ratings, count);
    var comments = new Array();
    comments = generateCommentArray(comments, count);

    if ((ratings!=false) && (comments != false)) {
      jQuery.ajax({
        type: "POST",
        data: {'nid': nid, 'headerInfo': headerInfo, 'ratings': ratings, 'comments': comments},
        url: '<?php echo $base_path ?>watchstatus/submitcounselorsheet/',
        success: function(text) {
          // window.location.href = "<?php print base_path() . 'mydashboard' ?>";
          location.reload();
          // if(typeof(jQuery("#submit_button").attr("disabled"))!="undefined") {
          // jQuery('#submit_button').removeAttr("disabled");
        }
        // else {
        //   window.location.href = "<?php print base_path() . 'viewassessment' ?>";
        //   return;
        // }
      });
    }
    else {
      hideConfirmdialog();
      return;
    }
  }

  function getNid() {
    return jQuery("#nid").val();
  }

  function getCount() {
    return jQuery("#count").val();
  }

  function getHeaderInfo() {
    var val = "";
    val += jQuery("#header-0").attr("value");
    val += "---"
    val += jQuery("#header-1").attr("value");
    return val;
  }
   
  function generateRatingArray(ratings, count) {
    for (var i = 3; i < count; i++) {
      ratings[i] = jQuery("#counselor-rating-" + i + " option:selected").val();
    }
    return ratings;
  }

  function generateCommentArray(comments, count) {
    for (var i = 3; i < count; i++) {
      comments[i] = jQuery("#counselor-comment-" + i).val();
    }
    return comments;
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

            <?php print render($page['content']) ?>

          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<?php require_once 'footer.tpl.php'; ?>
