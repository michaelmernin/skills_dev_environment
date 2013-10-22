<div>
  <h3>My Work List</h3>
  <hr />
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

  function showReviewList(items)
  {
    var obj, name, rpeid, reviewType, url, li;
    for (var i = 0; i < items.length; i++)
    {
      obj = items[i];
      name = obj.employeeName;
      rpeid = obj.rpeid;
      reviewType = obj.reviewType;
      url = "#?rreid=" + rpeid + "&name=" + name + "&reviewType=" + reviewType;
      li = " <li> <a href='" + url + "'> Please help [" + name + "] do peers review for her/his [Annual Review]! </a> </li>";
      jQuery('#myWorkingList').append(li);
    }
  }

  var peers = '<?php print json_encode($counseleePeers); ?>';
  var counseleePeers = eval(peers);
  showSelPeersList(counseleePeers, 'counselee');

  peers = '<?php print json_encode($counselorPeers); ?>';
  var counselorPeers = eval(peers);
  showSelPeersList(counselorPeers, 'counselor');

  var review = '<?php print json_encode($reviewList); ?>'
  var reviewList = eval(review);
  showReviewList(reviewList);


  if (counseleePeers.length === 0 && counselorPeers.length === 0)
  {
    var li = " <li> You have no work to do!</li>";
    jQuery('#myWorkingList').append(li);
  }




</script>

