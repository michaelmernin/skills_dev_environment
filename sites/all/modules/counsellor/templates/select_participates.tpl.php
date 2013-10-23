<?php $module_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() . '/' . drupal_get_path('module', 'counsellor') ?>
<?php $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path() ?>
<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/ui.multiselect.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/js/flatui-radio.js"></script>
<link type="text/css" href="<?php echo $module_path ?>/css/ui.multiselect.css" rel="stylesheet" />
<link type="text/css" href="<?php echo $module_path ?>/css/jquery-ui.css" rel="stylesheet" />

<h3 colspan="4">Select Counselee(s)</h3>

<h6>You can select people from the right list box below</h6>

<div style="width:800px;">
  <select id="users" class="multiselect" multiple="multiple" name="users[]" style="display: none; width:600px;height:257px;" >
    <?php display_counselees($counselees) ?>                
  </select>
</div>
<!--  <div style="width:400px">
    <form action="">
      <table border="0" cellpadding="1" cellspacing="1">
        <caption>
          <h6>Start pattern</h6>
        </caption>
        <tbody>
          <tr>
            <td>
              <input type="radio" name="radio_val" value="0">&nbsp;&nbsp;Individually.</td>
            <td>
              <input type="radio" name="radio_val" value="1" checked="checked">&nbsp;&nbsp;All.</td>
          </tr>
        </tbody>
      </table>
    </form>
  </div>-->
  <br >

  <script type="text/javascript">
    jQuery(function() {
      renderMultiselect();
    });

    function destoryMultiselect() {
      jQuery('.multiselect').multiselect('destroy');
    }

    function renderMultiselect() {
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