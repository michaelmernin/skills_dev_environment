<?php $base_path = get_curPage_base_url();
 $module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui') ?>
<script src="<?php echo $module_path ?>/assets/javascripts/grid.locale-en.js"></script>
<script src="<?php echo $module_path ?>/assets/javascripts/jquery.jqGrid.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<?php echo $module_path ?>/assets/stylesheets/ui.jqgrid.css" />
<div class="jQgrid-center">
    <table id="employeeCounselorGmTable"></table>
    <div id="employeeCounselorGmTableBar"></div>
</div>
<br />
<!--<a id="modal-89760" 
   href="#modal-container-89760" 
   role="button"
   class="btn btn-small btn-primary" 
   data-toggle="modal">
	Add Employee
</a>
modal-89760
!-->
<div id="modal-container-89760" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" 
            data-dismiss="modal" 
            aria-hidden="true" 
            id="close_btn" 
            value="0">×</button>
    <h3 id="myModalLabel">
	    Add and manage roles.
    </h3>
  </div>
  
  <div class="modal-body">
      <div class="form-item webform-component webform-component-textfield webform-component--engagement-summary-catetory--client webform-container-above">
        <label for="client">Client
          <span class="form-required" 
                title="This field is required.">*
          </span>
        </label>
        <input type="text" id="client" name="submitted[client]" value="" size="60" maxlength="128" class="form-text required">
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
      <div class="form-item webform-component webform-component-textarea webform-component--engagement-summary-catetory--project-roles-and-responsibilities">
        <label>Project Roles And Responsibilities<span class="form-required" title="This field is required.">*</span></label>
        <div class="form-textarea-wrapper resizable textarea-processed resizable-textarea">
          <textarea id="project-roles-and-responsibilities" cols="60" rows="5" class="form-textarea required"></textarea>
          <div class="grippie"></div>
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
	var lastsel;
  jQuery(document).ready(function(){
      jQuery("#employeeCounselorGmTable").jqGrid({
    url: '<?php print $base_path . 'loadrelationship' ?>',
    datatype: "json",
    height: 400,
//        width: 800,
    colNames: ['Employee Name', 'Counselor', 'GM', 'Number', 'recgid'],
		    colModel: [
			    {name: 'employeeName', index: 'employeeName', width: 60, align: 'center', sorttype: "text", editable:true, editoptions:{size:25}},
			    {name: 'counselorName', index: 'counselorName', width: 60, align: 'center', sorttype: "text", editable:true, editoptions:{size:25}},
			    {name: 'gmName', index: 'gmName', width: 60, align: 'center', sorttype: "text", editable:true, editoptions:{size:25}},
			    {name: 'number', index: 'number', hidden: true},
			    {name: 'recgid', index: 'recgid', hidden: true}
		    ],
		    multiselect: false,
		    gridview: true,
		    rownumbers: true,
		    autowidth: true,
		    caption: "·Manage Roles",
		    pager: 'employeeCounselorGmTableBar',
		    rowNum: 20,
		    rowList: [20, 30],
		    pginput: false,
		    viewrecords: true,
				sortorder: "desc",
				ondblClickRow: function(id) {
				   var gr = jQuery("#editgrid").jqGrid('getGridParam','selrow'); 
				   jQuery("#editgrid").jqGrid('editGridRow',gr,{height:280, width:300, reloadAfterSubmit:false});  
        }
//		    editurl: 'clientArray'
//	    ondblClickRow: function(id) {
//		    }
	    });
	    jQuery("#employeeCounselorGmTable").jqGrid('navGrid','#employeeCounselorGmTableBar',{edit:true,add:true,del:true});
      jQuery("#employeeCounselorGmTable").jqGrid('inlineNav',"#employeeCounselorGmTable");
	    jQuery("#employeeCounselorGmTable").jqGrid('filterToolbar',{searchOnEnter: false});
	    jQuery("#add_employeeCounselorGmTable").click(function(){
		    jQuery("#editgrid").jqGrid('editGridRow',"new",{height:280, width:300, reloadAfterSubmit:false});
	    });
	    jQuery("#add_employeeCounselorGmTable").click(function(){
		    var gr = jQuery("#editgrid").jqGrid('getGridParam','selrow');
		    if( gr != null )
			    jQuery("#editgrid").jqGrid('editGridRow',gr,{height:280, width:300, reloadAfterSubmit:false}); 
		    else
			    alert("Please select row!");
	    });
	    });
//	    navGrid('#peer-div-?php print $item_num ?>',
//		{view: false, add: false, edit: false, del: false, search: false},
//	    {}, // use default settings for edit  
//
//		{}, // use default settings for add  
//
//		{}, // delete instead that del:false we need this  
//
//		{} // enable the advanced searching  
//
//
	   
</script>


