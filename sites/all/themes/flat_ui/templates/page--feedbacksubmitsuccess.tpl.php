<?php $theme_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<?php $base_path = get_curPage_base_url(); ?>
<div id="wrap" style="min-height: 480px; padding-top: 20px;">
    <?php if ($title): ?><h1 class="page-title" style="margin-top: 20px;"><?php print $title; ?></h1><?php endif; ?>
    <div style="float: left; margin-left: 100px; margin-top: 120px;"><img src="<?php echo $theme_path ?>/assets/images/submitsuccess.png" /></div>
    <div style="float: left; margin-top: 150px; margin-left: 30px; font-size: 24px;">Thank you for taking time to complete the feedback!</div>
    <?php global $user;?>
    <?php if ($user->uid != 0): ?>
    <div style="float: right; margin-top: 100px;"><input type="button" value="Go to my dashboard"class="btn" onclick='window.location.href="<?php echo $base_path?>mydashboard"'/></div>
    <?php endif; ?>
    <?php if ($user->uid == 0): ?>
    <div style="float: right; margin-top: 100px;">If you want to login Perficient Review System, please click -->&nbsp;&nbsp;<input type="button" value="Login"class="btn" onclick='window.location.href="<?php echo $base_path?>"'/></div>
    <?php endif; ?>
</div>
