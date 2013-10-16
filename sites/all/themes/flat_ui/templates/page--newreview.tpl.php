<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript">
    function sendReminderEmail(providerName, employeeName, token, key, prId){
        $('#send_email_loading_'+key).css("display", 'block');
        $.ajax({
            type: "POST",
            url: '<?php echo $base_path ?>myworkstage/sendremindemail/'+providerName+'/'+employeeName+'/'+token+'/'+prId,
            success: function(msg){
                $('#send_email_loading_'+key).css("display", 'none');
            }
        })
    }
    
    
    function submitNewReview(){
    
    
    
    }
</script>
    <?php require_once 'header.tpl.php'; ?>
<div id="pr_mywokingstage_page" class="container">
    <div id="pr_mywokingstage_content" class="row">
        <div id="pr_mywokingstage_content_left" class="span3">
            <?php
            $navigation_tree = menu_tree(variable_get('menu_main_links_source', 'navigation'));
            print drupal_render($navigation_tree);
            ?>
            <script>
                jQuery(document).ready(function() {
                    switchProjectName(0);
                    jQuery('#review_start_date').val(formatDateTime('0','0','0'));
                    jQuery('#review_end_date').val(formatDateTime('0','1','0'));
                    jQuery('#review_from_date').val(formatDateTime('-1','0','0'));
                    jQuery('#review_to_date').val(formatDateTime('0','0','0'));
                    jQuery("#tree ul").hide();
                    jQuery("#tree li").each(function() {
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
            </script>


        </div>
        <div id="pr_mywokingstage_content_right" class="span9">
<!--            <div class="pr_workingstage_connent">
                <div id="pr_right_content">-->
                    <?php print render($page['basic_info'])
                    ?>

				

        <a id="modal-912871" href="#modal-container-912871" role="button" class="btn" data-toggle="modal">Start Review</a>
        
        
			<div class="row-fluid">
		<div class="span12">
			<div id="modal-container-912871" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-header">
					 <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
					<h3 id="myModalLabel">
						Confirm to start review
					</h3>
				</div>
				<div class="modal-body">
					<p>
            Do you want to start this review? <br>
            Once the review has been started, you cannot configure the timeslot and participants any more.
					</p>
				</div>
				<div class="modal-footer">
					 <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-primary" onclick="submitNewReview()">Submit</button>
				</div>
			</div>
    </div>
      </div>
      
<!--                </div>
            </div>-->
        </div>
    </div>
<!--    <div id="pr_mywokingstage_footer" class="pr_footer">
        <?php // require_once 'footer.tpl.php'; ?>
    </div>-->
</div>
<?php require_once 'footer.tpl.php'; ?>
