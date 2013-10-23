<?php $module_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() . '/' . drupal_get_path('module', 'counsellor') ?>
<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/ui.multiselect.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/flatui-radio.js"></script>
<link type="text/css" href="<?php echo $module_path ?>/css/ui.multiselect.css" rel="stylesheet" />
<link type="text/css" href="<?php echo $module_path ?>/css/jquery-ui.css" rel="stylesheet" />

<h3 colspan="4">Select Counselee(s)</h3>

<h6>You can select persons from the right list box below</h6>

<div style="width:800px;">
<select id="users" class="multiselect" multiple="multiple" name="users[]" style="display: none; width:600px;height:257px;" >
<?php display_counselees($counselees)  ?>                
</select>

      <div class="col-md-3">
          <h3 class="demo-panel-title">Start</h3>
          <!-- 
          <label class="radio">
            <span class="icons"><span class="first-icon fui-radio-unchecked"></span><span class="second-icon fui-radio-checked"></span></span><input type="radio" name="optionsRadios" id="optionsRadios1" value="0" data-toggle="radio" checked="checked">
            Individually.
          </label>
          <label class="radio">
            <span class="icons"><span class="first-icon fui-radio-unchecked"></span><span class="second-icon fui-radio-checked"></span></span><input type="radio" name="optionsRadios" id="optionsRadios2" value="1" data-toggle="radio">
            All.
          </label>
        </div> -->
        <form action="">
<input type="radio" name="radio_val" value="0">Individually.<br>
<input type="radio" name="radio_val" value="1" checked="checked">All.
</form>
        <br >
</div>
<script type="text/javascript">
    jQuery(function(){
    renderMultiselect();
});

function destoryMultiselect(){
    jQuery('.multiselect').multiselect('destroy');
}

function renderMultiselect(){
    jQuery(".multiselect").multiselect({
        sortable: true, 
        searchable: true, 
        dividerLocation: 0.6
    });
}

<?php   
function display_counselees($counselees) {
  $ceeElem;
  foreach ($counselees as $ceeElem) {
    $content1 = '<option value="';
    $content2 = '">';
    $content3 = '</option>';
    print $content1;
    print $ceeElem;
    print $content2;
    print $ceeElem;
    print $content3;
  }
}
?>

</script>