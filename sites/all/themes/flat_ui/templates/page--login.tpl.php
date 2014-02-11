<?php
/**
 * @file
 * Default theme implementation to display a single Drupal page.
 *
 * Available variables:
 *
 * General utility variables:
 * - $base_path: The base URL path of the Drupal installation. At the very
 *   least, this will always default to /.
 * - $directory: The directory the template is located in, e.g. modules/system
 *   or themes/garland.
 * - $is_front: TRUE if the current page is the front page.
 * - $logged_in: TRUE if the user is registered and signed in.
 * - $is_admin: TRUE if the user has permission to access administration pages.
 *
 * Site identity:
 * - $front_page: The URL of the front page. Use this instead of $base_path,
 *   when linking to the front page. This includes the language domain or
 *   prefix.
 * - $logo: The path to the logo image, as defined in theme configuration.
 * - $site_name: The name of the site, empty when display has been disabled
 *   in theme settings.
 * - $site_slogan: The slogan of the site, empty when display has been disabled
 *   in theme settings.
 *
 * Navigation:
 * - $main_menu (array): An array containing the Main menu links for the
 *   site, if they have been configured.
 * - $secondary_menu (array): An array containing the Secondary menu links for
 *   the site, if they have been configured.
 * - $breadcrumb: The breadcrumb trail for the current page.
 *
 * Page content (in order of occurrence in the default page.tpl.php):
 * - $title_prefix (array): An array containing additional output populated by
 *   modules, intended to be displayed in front of the main title tag that
 *   appears in the template.
 * - $title: The page title, for use in the actual HTML content.
 * - $title_suffix (array): An array containing additional output populated by
 *   modules, intended to be displayed after the main title tag that appears in
 *   the template.
 * - $messages: HTML for status and error messages. Should be displayed
 *   prominently.
 * - $tabs (array): Tabs linking to any sub-pages beneath the current page
 *   (e.g., the view and edit tabs when displaying a node).
 * - $action_links (array): Actions local to the page, such as 'Add menu' on the
 *   menu administration interface.
 * - $feed_icons: A string of all feed icons for the current page.
 * - $node: The node object, if there is an automatically-loaded node
 *   associated with the page, and the node ID is the second argument
 *   in the page's path (e.g. node/12345 and node/12345/revisions, but not
 *   comment/reply/12345).
 *
 * Regions:
 * - $page['help']: Dynamic help text, mostly for admin pages.
 * - $page['content']: The main content of the current page.
 * - $page['sidebar_first']: Items for the first sidebar.
 * - $page['sidebar_second']: Items for the second sidebar.
 * - $page['header']: Items for the header region.
 * - $page['footer']: Items for the footer region.
 *
 * @see template_preprocess()
 * @see template_preprocess_page()
 * @see template_process()
 */
?>

<header id="header" class="header" role="header">
  <hgroup>
    <div class="container">
      <div id="navigation" class="navbar navbar-inverse">
        <div class="navbar-inner">
          <div class="container clearfix">
            <?php if ($logo): ?>
            <a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>"  class="pull-left brand"><img src="<?php print $logo; ?>" alt="<?php print t('Home'); ?>" /></a>
          <?php endif; ?>


          <div class="nav-collapse nav-menu-collapse">
            <div class="inner">
              <h2><a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>" class="pull-left brand"><?php print $site_name; ?></a></h2>
              
              <p><?php if ($site_slogan): ?><?php print $site_slogan; ?><?php endif; ?></p>
            </div>
          </div>
        </div> 
      </div> <!-- /#navigation -->
    </div>
  </div>  <!-- /#main-wrapper -->
</hgroup>
</header>
<div class="structWebHeaderContainer">
 <div id="navMain" class="structWebMainMenu"></div>
</div>

<!--Test code start -->
<div class="container-fluid">
  <div class="row-fluid">
    <div class="span12">
      <h3>
        Welcome to Perficient Review System!
      </h3>
    </div>
  </div>
  <div class="row-fluid">
    <div class="span8">
      <!-- <div class="custom-content"> -->
      <?php print render($page['custom_content']); ?>
      <!-- </div> -->
    </div>
    <div class="span4">
      <div class="login-form">
        <?php print render($page['user_login']); ?>
      </div>
    </div>
  </div>
</div>


<footer id="footer" class="footer" role="footer">
  <div class="container">
    <?php if ($copyright): ?>
    <small class="copyright pull-left"><?php print $copyright; ?></small>
  <?php endif; ?>
  <small class="pull-right"><a href="#"><?php print t('Back to Top'); ?></a></small>
</div>
</footer>

<script>
$ = jQuery.noConflict();
$(document).ready(function(){
  var contentHeader = $(".title").closest("header");
  var contentFooter = $(".field-name-field-tags").closest("footer");
  var fieldLabel = $(".field-label");
  hideElement(contentHeader);
  hideElement(contentFooter);
  hideElement(fieldLabel);
  $(".row").css("margin-left", "30px");
});

function hideElement(elem) {
  elem.hide();
}
</script>
