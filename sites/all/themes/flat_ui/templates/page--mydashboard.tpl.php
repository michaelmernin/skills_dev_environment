<?php $base_path = get_curPage_base_url() ?>
<?php $module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<!--<script src="<?php // echo $module_path ?>/assets/javascripts/highcharts/highcharts.js"></script>
<script src="<?php // echo $module_path ?>/assets/javascripts/highcharts/modules/exporting.js"></script>-->
<script type="text/javascript">
//jQuery(function () {
//    var chart;
//    
//    jQuery(document).ready(function () {
//    	
//    	// Build the chart
//        jQuery('#containertest').highcharts({
//            chart: {
//                plotBackgroundColor: null,
//                plotBorderWidth: null,
//                plotShadow: false
//            },
//            title: {
//                text: 'Browser market shares at a specific website, 2010'
//            },
//            tooltip: {
//        	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
//            },
//            plotOptions: {
//                pie: {
//                    allowPointSelect: true,
//                    cursor: 'pointer',
//                    dataLabels: {
//                        enabled: false
//                    },
//                    showInLegend: true
//                }
//            },
//            series: [{
//                type: 'pie',
//                name: 'Browser share',
//                data: [
//                    ['Firefox',   45.0],
//                    ['IE',       26.8],
//                    {
//                        name: 'Chrome',
//                        y: 12.8,
//                        sliced: true,
//                        selected: true
//                    },
//                    ['Safari',    8.5],
//                    ['Opera',     6.2],
//                    ['Others',   0.7]
//                ]
//            }]
//        });
//    });
//    
//});

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
            <?php print render($page['my_working_stage']); ?>
            <?php print render($page['user_status']) ?>

        <!--<div id="containertest" style="min-width: 310px; height: 400px; margin: 0 auto"></div>-->

          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<?php require_once 'footer.tpl.php'; ?>
<!--    <div id="pr_mywokingstage_footer" class="pr_footer">
    </div>-->
