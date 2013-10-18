<div>
  <h2 style="background: #DFDDDD; -moz-border-radius: 5px; -webkit-border-radius: 5px; border-radius: 5px; text-shadow: white 1px 1px;">&nbsp;&nbsp;My Work List</h2>
  <ul id="lists">
  </ul>
  <br>
</div>


<script>

  var peers = '<?php print json_encode($counseleePeers) ?>';
  var counseleePeers = eval(peers);

  for (var i = 0; i < counseleePeers.length; i++)
  {
    var obj = counseleePeers[i];
    var name = obj.employeeName;
    var li = " <li> <a href='#'> Please select peers for your [Annual Review] </a> </li>";
    jQuery('#lists').append(li);
  }

  peers = '<?php print json_encode($counselorPeers) ?>';
  var counselorPeers = eval(peers);
  jQuery('#worklist').html(peers);

  for (var i = 0; i < counselorPeers.length; i++)
  {
    var obj = counselorPeers[i];
    var name = obj.employeeName;
    var li = " <li> <a href='#'> Please help [" + name + "] to select [Annual Review] peers  </a> </li>";
    jQuery('#lists').append(li);
  }

  if (counseleePeers.length === 0 && counselorPeers.length === 0)
  {
    var li = " <li> <a href='#'> You have no work to do!</a> </li>";
    jQuery('#lists').append(li);
  }
</script>

