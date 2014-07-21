<?php $base_path = get_curPage_base_url();
$module_path = get_curPage_base_url() . drupal_get_path('theme', 'flat_ui')
?>
<script type="text/javascript" src="<?php echo $module_path ?>/assets/javascripts/grid.locale-en.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/assets/javascripts/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/assets/javascripts/jquery.flexbox.js"></script>
<script type="text/javascript" src="<?php echo $module_path ?>/assets/javascripts/jquery.flexbox.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<?php echo $module_path ?>/assets/stylesheets/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" href="<?php echo $module_path ?>/assets/stylesheets/jquery.flexbox.css" />
<style type="text/css">
	/*body{
	text-align:center;
	}*/
	#gridMain td.countryHolder{
		overflow:visible;
		margin-bottom:20px;
	}
	#gridMain .ffb{
		margin-top:22px;
	}
	.ui-jqgrid{
		margin:0 auto;
	}
</style>
<table id="gridMain"></table>
<div id="pagernav"></div>
<div id="testData" style="margin:0 auto"></div>
<br />
<script>

	jQuery(document).ready(function() {
		var lastsel2;
		jQuery("#gridMain").jqGrid({
			url: '<?php print $base_path . 'loadrelationship' ?>',
			datatype: "json",
			gridview: true,
			rownumbers: true,
			autowidth: true,
			pager: '#pagernav',
			rowNum: 20,
			pgbuttons: true,
			rowList: [20, 30],
			pginput: true,
			sortname: 'employeeName',
			sortorder: "asc",
			sortable: true,
			height: 300,
			editurl: '<?php print $base_path . 'editjqgridrow' ?>',
			onSelectRow: function(id) {
				if (id && id !== lastsel2) {
					jQuery('#gridMain').restoreRow(lastsel2);
					jQuery('#gridMain').editRow(id, true);
					lastsel2 = id;
					jQuery('#saveBtn').attr('disabled', false);
				}
			},
			colNames: ['Index', 'Employee Name', 'Counselor', 'GM'],
			colModel: [
				{name: 'recgid', index: 'recgid', key: true, width: 40, hidden: true},
				{name: 'employeeName', index: 'employeeName', width: 60, align: 'center', sorttype: "text", editable: false},
				{name: 'counselorName', index: 'counselorName', width: 60, classes: "countryHolder", align: 'center', editable: true, edittype: 'custom', editoptions: {custom_element: myelem, custom_value: myval}},
				{name: 'gmName', index: 'gmName', width: 60, classes: "countryHolder", align: 'center', editable: true, edittype: 'custom', editoptions: {custom_element: yourelem, custom_value: yourval}}
			],
			caption: "·Manage Roles"
		});
		jQuery("#gridMain").jqGrid('navGrid', '#pagernav', {add: false, edit: false, del:true, search:false});
		jQuery("#gridMain").jqGrid('filterToolbar', {searchOnEnter: false});
	});

	function fetchCounselor(data) {
		var index = "#" + data.recgid + "_counselorName";
		var content = jQuery(index).find(".ffb-sel");
		return content.attr("val");
	}

	function fetchGM(data) {
		var index = "#" + data.recgid + "_gmName";
		var content = jQuery(index).find(".ffb-sel");
		return content.attr("val");
	}

	function myelem(value, options) {
		var ret = jQuery('<div id="country"></div>');
		ret.flexbox('autocomplete', {
			method: 'post',
			initialValue: value,
			onSelect: function() {
				jQuery('#country_input').blur();
				jQuery('#'+options.id).parent().parent().focus();
				jQuery('#'+options.id).parent().parent().click();
			},
			paging: {
				pageSize: 10, // acts as a threshold.  if <= pageSize results, paging doesn't appear  
			}
		});
		return ret;
	}

	function myval(elem) {
		return jQuery('#country_input').val();
	}

	function yourelem(value, options) {
		var ret = jQuery('<div id="gmName-autocomplete"></div>');
		ret.flexbox('autocomplete', {
			method: 'post',
			initialValue: value,
			onSelect: function() {
				jQuery('#gmName-autocomplete_input').blur();
				jQuery('#'+options.id).parent().parent().focus();
				jQuery('#'+options.id).parent().parent().click();
			},
			paging: {
				pageSize: 10, // acts as a threshold.  if <= pageSize results, paging doesn't appear  
			}
		});
		return ret;
	}

	function yourval(elem) {
		return jQuery('#gmName-autocomplete_input').val();
	}
</script>
