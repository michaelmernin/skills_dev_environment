<?php

/**
 * @file
 * Default theme implementation to display a single Drupal page.
 *
 * @see template_preprocess()
 * @see template_preprocess_page()
 * @see template_process()
 * @see html.tpl.php
 */
?>
<?php require_once 'header.tpl.php'; ?>

<div id="main-wrapper">
  <div id="main" class="main <?php print (!$is_panel) ? 'container' : ''; ?>">
    <?php if ($breadcrumb): ?>
      <div id="breadcrumb" class="visible-desktop">
        <div class="container">
          <?php print $breadcrumb; ?>
        </div>
      </div>
    <?php endif; ?>
    <?php if ($messages): ?>
      <div id="messages">
        <div class="container">
          <?php print $messages; ?>
        </div>
      </div>
    <?php endif; ?>
    <div id="content">
      <a id="main-content"></a>
      <?php print render($page['content']); ?>
     
page template
          
      
      
    </div>
  </div>
</div> <!-- /#main-wrapper -->
<?php require_once 'footer.tpl.php'; ?>
