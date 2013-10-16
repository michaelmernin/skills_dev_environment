<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


?>


            <div class="form-group">
              <!--<input type="password" class="form-control login-field" value="" placeholder="Password" id="login-pass" />-->
              <?php print drupal_render($form['name']); ?>
              <!--<span class="login-field-icon fui-user" for="login-name"></span>-->
            </div>

            <div class="form-group">
              <?php print drupal_render($form['pass']); ?>
              <!--<label class="login-field-icon fui-lock" for="login-pass"></label>-->
            </div>
            
             <?php print drupal_render_children($form);?>
