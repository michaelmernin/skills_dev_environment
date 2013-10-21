<div>
  <h2 style="background: #DFDDDD; -moz-border-radius: 5px; -webkit-border-radius: 5px; border-radius: 5px; text-shadow: white 1px 1px;">&nbsp;&nbsp;My Work List</h2>
  <ul id="myWorkingList">
  </ul>
  <br>
</div>

<script>

  function postwith(to, p) {
    var myForm = document.create_r_r_rElement_x("form");
    myForm.method = "post";
    myForm.action = to;
    for (var k in p) {
      var myInput = document.create_r_r_rElement_x("input");
      myInput.setAttribute("name", k);
      myInput.setAttribute("value", p[k]);
      myForm.a(myInput);
    }
    document.body.a(myForm);
    myForm.submit();
    document.body.removeChild(myForm);
  }

  function showSelPeersList(items, userType)
  {
    var obj, name, rreid, reviewType, url, li;
    for (var i = 0; i < items.length; i++)
    {
      obj = items[i];
      name = obj.employeeName;
      rreid = obj.rreid;
      reviewType = obj.reviewType;
      url = "#?rreid=" + rreid + "&name=" + name + "&reviewType=" + reviewType;

      if (userType == 'counselee') {
        li = " <li> <a href='" + url + "'> Please select peers for your [Annual Review] </a> </li>";
      }
      else if (userType == 'counselor') {
        li = " <li> <a href='" + url + "'> Please help [" + name + "] to select [Annual Review] peers  </a> </li>";
      }
      jQuery('#myWorkingList').append(li);
    }
  }

  var peers = '<?php print json_encode($counseleePeers) ?>';
  var counseleePeers = eval(peers);
  showSelPeersList(counseleePeers, 'counselee');

  peers = '<?php print json_encode($counselorPeers) ?>';
  var counselorPeers = eval(peers);
  showSelPeersList(counselorPeers, 'counselor');

  if (counseleePeers.length === 0 && counselorPeers.length === 0)
  {
    var li = " <li> You have no work to do!</li>";
    jQuery('#lists').append(li);
  }
</script>

