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

  /**
   * Show the user will select peeers
   * 
   * @param {array} items The items will to show
   * @param {string} userType The user type
   * 
   * */
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

      url = "./watchstatus/selectpeers/" + rreid;
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

  /**
   * Show the list of the user to do form
   * 
   * @param {array} items The items will to show
   * 
   * */
  function showReviewList(items)
  {
    var obj, name, rpeid, reviewType, description, token, url, li;
    for (var i = 0; i < items.length; i++)
    {
      obj = items[i];
      name = obj.employeeName;
      rpeid = obj.rpeid;
      reviewType = obj.reviewType;
      description = obj.description;
      token = obj.token;

      basePath = '<?php print $base_path = get_curPage_base_url(); ?>';
      url = basePath + token;
      if (name == '<?php print get_current_user_name(); ?>')
      {
        li = " <li> <a href='" + url + "'>" + description + " is started.Please fill up your Self-Review form.(" + reviewName(reviewType) + ")</a> </li>";
        jQuery('#myWorkForMe').append(li);
      }
      else
      {
        li = " <li> <a href='" + url + "'>" + description + " is started.Please fill up Peer Review form for [" + name + " ].(" + reviewName(reviewType) + ")</a> </li>";
        jQuery('#myWorkForOther').append(li);
      }
    }
  }

  /**
   * Show self review finished review list
   * 
   * 
   * */
  function showSelfReviewFinished(items)
  {
    var obj, name, rreid, reviewType, description, token, url, li;
    for (var i = 0; i < items.length; i++)
    {
      obj = items[i];
      name = obj.employeeName;
      rreid = obj.rreid;
      reviewType = obj.reviewType;
      description = obj.description;
      url = "./watchstatus/viewassessment/" + rreid;
      li = " <li> <a href='" + url + "'>[" + name + "] has finished " + description + " self reivew.(" + reviewName(reviewType) + ")</a> </li>";
      jQuery('#myWorkForOther').append(li);
    }

  }

  var peers = '<?php print json_encode($counseleePeers); ?>';
  var counseleePeers = eval(peers);
  showSelPeersList(counseleePeers, 'counselee');

  peers = '<?php print json_encode($counselorPeers); ?>';
  var counselorPeers = eval(peers);
  showSelPeersList(counselorPeers, 'counselor');

  var review = '<?php print json_encode($reviewList); ?>';
  var reviewList = eval(review);
  showReviewList(reviewList);

  var self_review_finished_josn = '<?php print json_encode($self_review_finished); ?>';
  var self_review_finished = eval(self_review_finished_josn);
  showSelfReviewFinished(self_review_finished);

  var workingItemsNum = counseleePeers.length + counselorPeers.length + reviewList.length + self_review_finished.length;
  if (workingItemsNum == 0)
  {
    var li = " <li> You have no work to do!</li>";
    jQuery('#myWorkForMe').append(li);
  }

</script>

