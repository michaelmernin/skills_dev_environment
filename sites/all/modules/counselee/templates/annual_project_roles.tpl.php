<?php
//	dd($project_roles, 'project_roles');
//	dd($added_project_roles, 'added_project_roles');
?>
<div>
<caption>
      <h3>Project Roles And Responsibilities</h3>
</caption>
    
	<table class="table table-hover" id="status_review">
		<thead>
			<tr>
				<th>Client</th>
				<th>Start Date</th>
				<th>End Date</th>
				<th>Project Roles And Responsibilities</th>
				<th>Status</th>
				<th>Rating</th>
				<th class="nid" style="display: none">NodeID</th>
				<th class="is-adding-proj" style="display: none">isAddingProj</th>
			</tr>

		</thead>

		<tbody id="project-roles-body"></tbody>
	</table>

</div>
<script>
	function initProjectRoles(){
		var projects_json_text = <?php print json_encode($project_roles) ?>;
    var projects_json = JSON.stringify(projects_json_text);
    var projects = JSON.parse(projects_json.replace("\r\n", "<br/>"));	

	var tableContent = jQuery("#project-roles-body").html();
    for (attribute in projects) {
      obj = projects[attribute];
//      console.log(obj);
      client = obj.client;
      startDate = obj.startdate;
      endDate = obj.enddate;
      rreid = obj.rreid;
      rating = (obj.overall_rating == null)?"N/A":obj.overall_rating;
      roles = (obj.project_roles_and_responsibilities == null)?"N/A":obj.project_roles_and_responsibilities;
      nid = obj.nid;
      status = displayReviewStatus(obj.status);
			
      var content = display_project_review_url_from_status(rreid, status);
      content += "<td>" + client
        + "</td><td>"
        + startDate
        + "</td><td>"
        + endDate
        + "</td><td>"
        + roles
        + "</td><td>"
        + status
        + "</td><td>"
        + rating
        + "</td><td style=\"display: none\">"
        + nid
        + "</td><td style=\"display: none\">0</td></tr>";

      tableContent += content;
    }
    tableContent += display_added_project_roles();
    jQuery("#project-roles-body").html(tableContent);
  }

  function display_added_project_roles() {
  	var content = "";
		var text = <?php $projects = json_encode($added_project_roles); 
		print $projects; ?>;
		var add_proj_json = JSON.stringify(text);
    var projects = JSON.parse(add_proj_json);
    for(attribute in projects){
      obj = projects[attribute];
      client = obj.client;
      nid = obj.nid;
      pnid = obj.pnid;
      rating = obj.rating;
      roles = obj.roles;

			content += "<tr><td>" + client + "</td><td></td><td></td><td>" + roles + "</td><td></td><td>" + rating + "</td><td style=\"display: none\">"+ nid + "</td><td style=\"display: none\">1</td></tr>";
    }
    return content;
	}

  function display_project_review_url_from_status(rreid, status) {
		switch(status){
			case "Review in draft":
				return "<tr>";
      case "Review by counselor":
        status = "viewselfassessment/";
        break;
      case "Approved by counselor":
        status = "reviewcontent/";
        break;
      default:
        status = "reviewcontent/";
        break;
  	}
	  var content = "<tr title=\"Click to watch this review\'s status.\"  onclick=\"window.open('" + '<?php print get_curPage_base_url() ?>' + "watchstatus/" + status + rreid + "')\"" + " style='cursor:pointer'>";
		return content;
  }

  function displayReviewStatus(status) {
    switch (status) {
      case '1':
        content = "Review in draft";
        break;
      case '2':
        content = "Review by counselor";
        break;
      case '3':
        content = "Approved by counselor";
        break;
      case '4':
        content = "Joint Review";
        break;
      case '5':
        content = "GM review";
        break;
      case '6':
        content = "GM Approved";
        break;
      default:
        break;
    }
    return content;
  }
	initProjectRoles();
</script>