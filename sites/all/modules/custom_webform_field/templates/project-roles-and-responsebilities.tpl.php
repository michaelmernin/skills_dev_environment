<?php
drupal_add_library('system', 'ui.datepicker');
drupal_add_css(drupal_get_path('module', 'custom_webform_field'), '/css/project-roles-and-responsibilities.css');
?>
<!--<fieldset class="webform-component-fieldset webform-catalogue webform-component--project-roles-and-responsibilities-category form-wrapper"><legend><span class="fieldset-legend">Project Roles and Responsibilities</span></legend><div class="fieldset-wrapper"><div class="form-item webform-component webform-component-textarea webform-component--opportunities-for-improvement-category--opportunities-for-improvement-text">
      <label class="element-invisible" for="edit-submitted-project-roles-and-responsibilities-category-project-roles-and-responsibilities-text">Project Roles and Responsibilities</label>
    </div>
  </div>-->
  <div>
<!--    <caption>
      <h3>Project Roles And Responsibilities</h3>
    </caption>-->
    
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
    
    <a id="modal-89760" 
       href="#modal-container-89760" 
       role="button"
       class="btn btn-small btn-primary" 
       data-toggle="modal">
      Add Project
    </a>
  </div>
<!--</fieldset>-->

<input type="hidden" id="project_node_id" value="<?php print $nid ?>">

<!--modal-89760-->
<div id="modal-container-89760" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" 
            data-dismiss="modal" 
            aria-hidden="true" 
            id="close_btn" 
            value="0">×</button>
    <h3 id="myModalLabel">
      Add Project Roles And Responsibilities
    </h3>
  </div>
  
  <div class="modal-body">
    
    <div class="wfm-item">
      
      <div class="form-item webform-component webform-component-textfield webform-component--engagement-summary-catetory--client webform-container-above">
        <label for="client">Client
          <span class="form-required" 
                title="This field is required.">*
          </span>
        </label>
        <input type="text" id="client" name="submitted[client]" value="" size="60" maxlength="128" class="form-text required">
      </div>
      <!--      <div class="form-item webform-component webform-component-textfield webform-component--engagement-summary-catetory--startdate webform-container-above">
              <label for="startdate">Start Date<span class="form-required" title="This field is required.">*</span></label>
              <input type="text" value="" id="startdate" class="datetimepicker" readonly="readonly" onfocus="initDatePicker()" style="color:#34495E;background-color:#ffffff">
            </div>
            <div class="form-item webform-component webform-component-textfield webform-component--engagement-summary-catetory--enddate webform-container-above">
              <label for="enddate">End Date<span class="form-required" title="This field is required.">*</span></label>
              <input type="text" value="" id="enddate" class="datetimepicker" readonly="readonly" onfocus="initDatePicker()" style="color:#34495E;background-color:#ffffff">
            </div>-->
      <div class="form-item webform-component webform-component-select webform-component--rating">
        <label for="rating">Rating </label>
        <select id="rating" name="submitted[rating]" 
                class="form-select">
          <option value="1">1</option>
          <option value="2">2</option>
          <option value="3" selected="selected">3</option>
          <option value="4">4</option>
          <option value="5">5</option>
        </select>
      </div>
      <div class="form-item webform-component webform-component-textarea webform-component--engagement-summary-catetory--project-roles-and-responsibilities">
        <label>Project Roles And Responsibilities<span class="form-required" title="This field is required.">*</span></label>
        <div class="form-textarea-wrapper resizable textarea-processed resizable-textarea">
          <textarea id="project-roles-and-responsibilities" cols="60" rows="5" class="form-textarea required"></textarea>
          <div class="grippie"></div>
        </div>
      </div>
    </div>
    
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89760" 
         title="loading..." 
         style="width: 25px; height: 25px; display: none"
         src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn"
            data-dismiss="modal" 
            id="cancel-add-roles-button"
            aria-hidden="true">Cancel
    </button> 
    
    <button class="btn btn-danger" type="button"
            id="add-roles-button"
            onclick="addProjectRoles()">
      Submit
    </button>
  </div>
</div>


<script>
  function initDatePicker() {
    jQuery(".datetimepicker").datepicker({
      firstDay: 1,
      changeMonth: true,
      changeYear: true
    });
  }

  function initializeProjectRoles() {
    var client, rating, roles, obj, tbody;
    var projects_json_text = <?php print json_encode($projects) ?>;
    var projects_json = JSON.stringify(projects_json_text);
    var projects = JSON.parse(projects_json.replace("\r\n", "<br/>"));

    var tbody = jQuery("#project-roles-body").html();
    //    console.log(tbody);
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

      tbody += content;
    }
    tbody += display_added_project_roles();
    jQuery("#project-roles-body").html(tbody);
  }

	function display_added_project_roles() {
  	var content = "";
		var text = <?php $projects = json_encode($added_projects); 
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
        status = "selfassessment/";
        break;
      case "Review by counselor":
        status = "viewselfassessmentcontent/";
        break;
      case "Approved by counselor":
        status = "counseleeconfirmresult/";
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

  function addProjectRoles() {
    var client = getElementValue(jQuery('#client'));
    //    var startdate = getElementValue(jQuery('#startdate'));
    //    var enddate = getElementValue(jQuery('#enddate'));
    var roles = getElementValue(jQuery('#project-roles-and-responsibilities'));
    var rating = getElementValue(jQuery("#rating"));
    var errorMsg = '';
    if (client.trim().length < 1)
      errorMsg += 'Client field is required.\n';
    if (roles.trim().length < 1)
      errorMsg += 'Project Roles And responsibilities field is required.';
    if (errorMsg.length > 1) {
      alert(errorMsg);
      return;
    }

    var tbody = jQuery("#project-roles-body").html();
    var content = generateTableContent(client, rating, roles);
    restoreAddProjectButton(tbody, content);
  }

  function generateTableContent(client, rating, roles) {
    var content = "<tr><td>" + client + "</td><td></td><td></td><td>" + roles + "</td><td></td><td>" + rating + "</td><td style=\"display: none\"></td><td style=\"display: none\">1</td></tr>";
    return content;
  }

  function restoreAddProjectButton(tbody, content) {
    jQuery("#project-roles-body").html(tbody + content);
    var cancelBtn = jQuery("#cancel-add-roles-button");
    jQuery('#client').val("");
    jQuery('#rating').val("3");
    jQuery('#project-roles-and-responsibilities').val("");
    cancelBtn.click();
  }

  function deleteRoles(obj) {
    var bln = window.confirm("Are you sure to delete?");
    if (bln)
    {
      var temp = jQuery(obj);
      var par = temp.parent().parent();
      par.remove();
    }
  }

  function getNodeIdFromContent() {
    return jQuery("#project_node_id").val();
  }

  function save_project_roles() {
    var basepath = '<?php print get_curPage_base_url(); ?>';
    var nid = getNodeIdFromContent();
    var context = new Array();
    var COLUMN_NUM = 8;

    jQuery("#project-roles-body").find("tr").each(function() {
      jQuery(this).find("td").each(
        function() {
        context.push(jQuery(this).text());
      });
    });
    var client = new Array();
    var startdate = new Array();
    var enddate = new Array();
    var project_roles_and_responsibilities = new Array();
    var status = new Array();
    var rating = new Array();
    var nid_arr = new Array();
    var isAdded = new Array();

    for (var i = 0; i < context.length; i++) {
      var num = i % COLUMN_NUM;
      //    var index = Math.floor(i/COLUMN_NUM);
      switch (num) {
        case 0:
          client.push(context[i]);
          break;
        case 1:
          startdate.push(context[i]);
          break;
        case 2:
          enddate.push(context[i]);
          break;
        case 3:
          project_roles_and_responsibilities.push(context[i]);
          break;
        case 4:
          status.push(context[i]);
          break;
        case 5:
          rating.push(context[i]);
          break;
        case 6:
          nid_arr.push(context[i]);
          break;
        case 7:
          isAdded.push(context[i]);
          break;
        default:
          break;
      }
    }
    var proj_roles = new Array();
    for (var i = 0; i < ((context.length) / COLUMN_NUM); i++) {
      var tmp = {
        "client": client[i],
          "startdate": startdate[i],
          "enddate": enddate[i],
          "project_roles_and_responsibilities": project_roles_and_responsibilities[i],
          "status": status[i],
          "rating": rating[i],
          "nid": nid_arr[i],
          "isAdded": isAdded[i]
      };
      proj_roles[i] = JSON.stringify(tmp);
    }
//		console.log(proj_roles);
    jQuery.ajax({
      type: "POST",
      url: basepath + 'annual/save-project-roles',
      data: {'nid': nid, 'proj_roles': proj_roles},
      success: function(text) {

      }
    });

  }

  initDatePicker();
  initializeProjectRoles();
//  display_added_project_roles();
  jQuery(document).ready(function() {
    jQuery("input[name='op'][value='Submit']").bind("click", function() {
      save_project_roles();
    });
    jQuery("input[name='op'][value='Save Draft']").bind("click", function() {
      save_project_roles();
    });
  });
</script>

