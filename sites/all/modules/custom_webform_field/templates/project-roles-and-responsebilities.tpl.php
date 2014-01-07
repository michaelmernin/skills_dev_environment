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
        <th>Rating</th>
        <th>Project Roles And Responsibilities</th>
        <th>Status</th>
        <th>Delete</th>
      </tr>
    </thead>

    <tbody id="project-roles-body">
<!--      <tr>
        <td>Client Engagements</td>
        <td>3</td>
        <td>Project Roles And ResponsibilitiesProject Roles And Responsibilities</td>
        <td></td>
      </tr>

      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td><a id="modal-89760" 
               href="#modal-container-89760" 
               role="button"
               class="btn btn-small btn-primary" 
               data-toggle="modal">
            Add Project
          </a></td>
      </tr>-->

    </tbody>
  </table>

  <a id="modal-89760" 
     href="#modal-container-89760" 
     role="button"
     class="btn btn-small btn-primary" 
     data-toggle="modal">
    Add Project
  </a>
</div>





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
        <input type="text" 
               id="client" 
               name="submitted[client]"
               value="" 
               size="60"
               maxlength="128" 
               class="form-text required">
      </div>

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

      <!--      <div class="form-item webform-component webform-component-textfield webform-component--engagement-summary-catetory--client webform-container-above">
              <label for="rating">Rating
                <span class="form-required" 
                      title="This field is required.">*
                </span>
              </label>
              <input type="text" 
                     id="rating" 
                     name="submitted[rating]"
                     value="" 
                     size="60"
                     maxlength="128" 
                     class="form-text required">
            </div>-->

      <div class="form-item webform-component webform-component-textarea webform-component--engagement-summary-catetory--project-roles-and-responsibilities">
        <label for="project-roles-and-responsibilities">Project Roles And Responsibilities<span class="form-required" title="This field is required.">*
          </span>
        </label>
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



              function initializeProjectRoles()
              {
                var projects_json = '<?php print json_encode($projects); ?>';
                projects_json = projects_json.replace("\r\n", "<br/>");
                var projects = JSON.parse(projects_json);
                var client, rating, roles, obj, tr, tbody;

                var tbody = jQuery("#project-roles-body").html();
                for (attribute in projects)
                {
                  obj = projects[attribute];
                  client = obj.client;
                  rating = obj.overall_rating;
                  roles = obj.project_roles_and_responsibilities;

                  tr = "<tr title='Click to watch this review\'s status.'  onclick='location.href=\"https://localhost/EnterpriseReview/watchstatus/selfassessment/19\"' style='cursor:pointer'><td>"
                          + client
                          + "</td><td>"
                          + rating
                          + "</td><td>"
                          + roles
                          + "</td> <td>"
                          + ''
                          + "</td> <td>"
                          + "</td></tr>";

                  tbody += tr;
                }
                jQuery("#project-roles-body").html(tbody);
              }


              function addProjectRoles()
              {
                var client = getElementValue(jQuery('#client'));
                var rating = getElementValue(jQuery('#rating'));
                var roles = getElementValue(jQuery('#project-roles-and-responsibilities'));
                var errorMsg = '';
                if (client.trim().length < 1)
                  errorMsg += 'Client field is required.\n';
                if (roles.trim().length < 1)
                  errorMsg += 'Project Roles And responsibilities field is required.';
                if (errorMsg.length > 1)
                {
                  alert(errorMsg);
                  return;
                }

                var tbody = jQuery("#project-roles-body").html();
                var tr = "<tr><td>"
                        + client
                        + "</td><td>"
                        + rating
                        + "</td><td>"
                        + roles
                        + "</td> <td>"
                        + '<a onclick="deleteRoles(this)">Delete</a>'
                        + "</td></tr>";
                jQuery("#project-roles-body").html(tbody + tr);
                var cancelBtn = jQuery("#cancel-add-roles-button");
                jQuery('#client').val("");
                jQuery('#rating').val("3");
                jQuery('#project-roles-and-responsibilities').val("");
                cancelBtn.click();
              }
              function deleteRoles(obj)
              {
                var bln = window.confirm("Are you sure to delete?");
                if (bln)
                {
                  var temp = jQuery(obj);
                  var par = temp.parent().parent();
                  par.remove();
                }
              }


              function save_project_roles()
              {
                var basepath = '<?php print get_curPage_base_url(); ?>';

                jQuery("#project-roles-body").find("tr").each(
                        function() {

                          var value = new Array();
                          jQuery(this).find("td").each(
                                  function() {
                                    value.push(jQuery(this).text());
                                  });


                          alert(value);
                        });



                jQuery.ajax({
                  type: "POST",
                  url: basepath + 'annual/save-project-roles',
                  data: {'nid': nid, 'values': values},
                  success: function(date) {
                  }
                });

              }



              initializeProjectRoles();
//              save_project_roles();
</script>

