<div>
  <h3>My Work List</h3>
  <hr />
  <ul id="myWorkForMe">
  </ul>
  <ul id="myWorkForOther">
  </ul>
  <br>
</div>

<script>

  var workingItemsNum = 0;

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

  function reviewName(type)
  {
    if (type === '0')
      return 'Annual Review';
    else if (type === '1')
      return 'Project Review';
    else if (type === '2')
      return '3-Mouth Review';
    else
      return 'Review';
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
      description = obj.description;
      url = "#?rreid=" + rreid + "&name=" + name + "&reviewType=" + reviewType;

      if (userType == 'counselee') {
        li = " <li> <a href='" + url + "'>" + description + " is started.Please select peers for yourself" + ".(" + reviewName(reviewType) + ") </a> </li>";
        jQuery('#myWorkForMe').append(li);
      }
      else if (userType == 'counselor') {
        li = " <li> <a href='" + url + "'>" + description + " is started.Please select peers for [" + name + "] " + ".(" + reviewName(reviewType) + ")</a> </li>";
        jQuery('#myWorkForOther').append(li);
      }

    }
  }

  function showReviewList(items)
  {
    var obj, name, rpeid, reviewType, description, url, li;
    for (var i = 0; i < items.length; i++)
    {
      obj = items[i];
      name = obj.employeeName;
      rpeid = obj.rpeid;
      reviewType = obj.reviewType;
      description = obj.description;

      url = "#?rreid=" + rpeid + "&name=" + name + "&reviewType=" + reviewType;
      if (name == '<?php print get_current_user_name(); ?>')
      {
        li = " <li> <a href='" + url + "'>" + description + " is started.Please do peers review for yourself.(" + reviewName(reviewType) + ")</a> </li>";
        jQuery('#myWorkForMe').append(li);
      }
      else
      {
        li = " <li> <a href='" + url + "'>" + description + " is started.Please do peers review for [" + name + " ].(" + reviewName(reviewType) + ")</a> </li>";
        jQuery('#myWorkForOther').append(li);
      }
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


  var workingItemsNum = counseleePeers.length + counselorPeers.length + reviewList.length;
  if (workingItemsNum == 0)
  {
    var li = " <li> You have no work to do!</li>";
    jQuery('#myWorkForMe').append(li);
  }


<?php
$address = '673021974@qq.com';
$subject = 'Notification';
$params['username'] = 'jack';
$params['reviewname'] = 'Annual Review';
$params['peersurl'] = 'www.baidu.com';
$params['reviewurl'] = 'www.google.com';
?>


  var message = '<?php print mail_sander_do_task()?>';
  var li = " <li> " + message + "</li>";
  jQuery('#myWorkForMe').append(li);
</script>

