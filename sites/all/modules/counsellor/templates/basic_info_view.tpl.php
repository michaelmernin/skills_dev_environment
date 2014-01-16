<table border="0" cellpadding="1" cellspacing="1">
  <caption>
    <h3>Basic Information</h3>
  </caption>
  <tbody>
<!--    <tr>
      <td>
        <h6>Review Name<span class="form-required" title="This field is required.">*</span>:</h6>
      </td>
      <td colspan="3"><input type="text" value="" id="review_from_description" style="color:#34495E;background-color:#ffffff"></td>
    </tr>-->
    <tr id="myInfo">
      <td><h6>Anniversary Month:</h6></td>
      <td style="text-align:left;vertical-align:middle"><?php print render($myInfo['my_anniversary_month'])?></td>
      <td><h6>My Counselor:</h6></td>
      <td style="text-align:left;vertical-align:middle"><?php print render($myInfo['my_counselor_name'])?></td>
    </tr>
    <tr>
      <td>
        <h6>Period From<span class="form-required" title="This field is required.">*</span>:</h6>
      </td>
      <td><input type="text" value="" id="review_from_date" class="datetimepicker"  readonly="readonly" style="color:#34495E;background-color:#ffffff"></td>
      <td><h6>Period To<span class="form-required" title="This field is required.">*</span>:</h6></td>
      <td><input type="text" value="" id="review_to_date" class="datetimepicker"  readonly="readonly" style="color:#34495E;background-color:#ffffff"></td>
    </tr>
    <tr>
      <td>
        <h6>Type<span class="form-required" title="This field is required.">*</span>:</h6>
      </td>
      <td><select class="form-select" id="review_type" name="review_type"><option value="0" selected>Annual Review</option><option value="1">Project Review</option><option value="2">Three-Month Review</option></select></td>
      <td></td><td></td>
    </tr>
    <tr>
      <td>
        <h6 id="Project_Name_Lable">Project Name<span class="form-required" title="This field is required.">*</span>:</h6>
      </td>
      <td><input placeholder="Type Project Name…" type="text" id="Project_Name_Text" /></td>
      <td>
        <h6 id="Project_Client_Lable">Client<span class="form-required" title="This field is required.">*</span>:</h6>
      </td>
      <td><input placeholder="Type Client…" type="text" id="Project_Client_Text" /></td>
    </tr>
<!--    <tr>
      <td>
        <h6>Start Date<span class="form-required" title="This field is required.">*</span>:</h6>
      </td>
      <td>    <input type="text" value="" id="review_start_date" class="datetimepicker" readonly="readonly" style="color:#34495E;background-color:#ffffff">

      </td>
      <td><h6>End Date<span class="form-required" title="This field is required.">*</span>:</h6></td>
      <td><input type="text" value="" id="review_end_date" class="datetimepicker"  readonly="readonly" style="color:#34495E;background-color:#ffffff"></td>
    </tr>-->
  </tbody>
</table>
<hr />
<p>&nbsp;</p>
<script>

  function formatDateTime(year, month, day) {
    var nyear = '<?php print render($nTime['year']) ?>';
    var nmonth = '<?php print render($nTime['month']) ?>';
    var nday = '<?php print render($nTime['day']) ?>';
    var fdate = '';
    var cyear = parseInt(nyear) + parseInt(year);
    var cmonth = parseInt(nmonth) + parseInt(month);
    var cday = parseInt(nday) + parseInt(day);
    fdate = cmonth + '/' + cday + '/' + cyear;
    return fdate;

  }

  jQuery('#review_type').change(function() {
    switch (jQuery('#review_type').val()) {
      case '0':
        switchProjectName(0);
//        jQuery('#review_start_date').val(formatDateTime('0', '0', '0'));
//        jQuery('#review_end_date').val(formatDateTime('0', '1', '0'));
//        jQuery('#review_from_date').val(formatDateTime('-1', '0', '0'));
//        jQuery('#review_to_date').val(formatDateTime('0', '0', '0'));
        break;
      case '1':
        switchProjectName(1);
//        jQuery('#review_start_date').val(formatDateTime('0', '0', '0'));
//        jQuery('#review_end_date').val(formatDateTime('0', '1', '0'));
//        jQuery('#review_from_date').val(formatDateTime('0', '-1', '0'));
//        jQuery('#review_to_date').val(formatDateTime('0', '0', '0'));
        break;
      case '2':
        switchProjectName(0);
//        jQuery('#review_start_date').val(formatDateTime('0', '0', '0'));
//        jQuery('#review_end_date').val(formatDateTime('0', '1', '0'));
//        jQuery('#review_from_date').val(formatDateTime('0', '-3', '0'));
//        jQuery('#review_to_date').val(formatDateTime('0', '0', '0'));
        break;

    }



  });
  jQuery(".datetimepicker").datepicker({
    firstDay: 1,
    changeMonth: true,
    changeYear: true
  });
</script>
