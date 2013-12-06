<header id="header" class="header" role="header">
  <hgroup>
  <div class="container">
    <div id="navigation" class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container clearfix">
          <?php if ($logo): ?>
<!--            <a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>" rel="home" id="logo" class="pull-left brand">
              <?php print $site_name; ?>
            </a>-->
              <a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>"  class="pull-left brand"><img src="<?php print $logo; ?>" alt="<?php print t('Home'); ?>" /></a>
          <?php endif; ?>
            
            
            <div class="nav-collapse nav-menu-collapse">
            <div class="inner">
              <h2><a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>" class="pull-left brand"><?php print $site_name; ?></a></h2>
              
            <p><?php if ($site_slogan): ?><?php print $site_slogan; ?><?php endif; ?></p>
            </div>
          </div>
       <?php if($logged_in):?>
        <div id="login_user" style="float: right; padding: 30px 0 0 10px;">
            Hi, <?php global $user; 
            $format_name= format_login_name($user->name);
            echo $format_name; ?>
        </div>
              <?php endif;?>
<!--          <div class="nav-collapse nav-menu-collapse">
            <div class="inner">
              <?php // if ($main_menu): ?>
                <nav class="main-menu pull-left" role="navigation">
                  <?php // print render($main_menu); ?>
                </nav>  /#main-menu 
              <?php // endif; ?>
            </div>
          </div>

          <div class="nav-collapse nav-search-collapse">
            <div class="inner">
              <?php // if ($search_form): ?>
                <?php // print $search_form; ?>
              <?php // endif; ?>
            </div>
          </div>-->

      </div>
    </div> 
  </div> <!-- /#navigation -->
 </hgroup>
</header>

