<fieldset class="webform-component-fieldset webform-catalogue ">
  <legend>
    <span class="fieldset-legend"> <?php print ucfirst($node['description']); ?>
    </span>
  </legend>

  <div class="webform-submission-info clearfix">
    <div class="wellwarp">
      <b style="font-weight: 700">Self Description:  </b>
      <?php print $node['self_description']; ?>
    </div>
    <div class="wellwarp">  
      <b style="font-weight: 700">Counselor Description:  </b>
      <?php print $node['counselor_description']; ?>
    </div>
  </div>
</fieldset>
<hr>
