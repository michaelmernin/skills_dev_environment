<table class="block block-counsellor">
  <caption>
    <h3>Basic Information</h3>
  </caption>
  <tbody>
    <tr>
      <td>
        <h6> Colleague :</h6>
      </td>
      <td id="basic_info_colleague" style="text-align:left;vertical-align:middle">

      </td>
      <td>
        <h6> Colleague's Counselor:</h6>
      </td>

      <td id="basic_info_counselor" style="text-align:left;vertical-align:middle">

      </td>
    </tr>

    <tr>
      <td>
        <h6> Reviewer</h6>
      </td>

      <td id="basic_info_reviewer" style="text-align:left;vertical-align:middle"></td>

      <td>
        <h6> Date</h6>
      </td>
      <td id="basic_info_date">
       
      </td>
    </tr>

  </tbody>
</table>


<script>
  var basic_info_json = '<?php print json_encode($basic_info); ?>';
  var basic_info = eval(basic_info_json);

  if (basic_info.length == 1)
  {
    jQuery('#basic_info_colleague').html(basic_info[0].colleague);
    jQuery('#basic_info_counselor').html(basic_info[0].counselor);
    jQuery('#basic_info_reviewer').html(basic_info[0].reviewer);
  }
  var showtime = '<?php print date("Y-m-d H:i:s"); ?> ';
  jQuery('#basic_info_date').html(showtime);

</script>


