<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
</head>
<style>
.sectioncalibrator {
	height: calc(100vh - 50px);
	overflow-y: scroll;
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: calc(100vh - 100px);
		overflow-y: scroll;
	}
}
</style>

<script>
	//Make it global for easily access by other javascript file
	var table;
	var item_table;

	var selected_id;
	var selected_groupname;
	var selected_item_id;

	$(document).ready(function($) {
		CloseItemModal();
		DisplayGroupTableData();
	});

	//Datatable onclick on selected row
	/* 	$('#datatable tbody').on('click', 'tr', function() {

	 selected_id = table.row(this).data().groupid;
	 var selected_grouptype = table.row(this).data().grouptype;
	 var selected_groupname = table.row(this).data().groupname;

	 $('#edit_remove_group_modal').modal('show');

	 $("#edit_group_name_input").val(selected_groupname);
	 $("#delete_group_name").text(selected_groupname);

	 console.log(selected_grouptype);
	 $("#edit_group_type_selection").val(selected_grouptype);
	 });  */

	function ShowItemModal() {
		$('#item_modal').show();
	}

	function CloseItemModal() {
		$('#item_modal').hide();
	}

	function removed_selected_group() {
		angular.element($('#show_item_controller_container')).scope()
				.removed_selected_group(selected_id);
	}

	function DisplayGroupTableData() {
		$('#loading_modal').show();
		table = $('#datatable')
				.DataTable(
						{
							"responsive" : true,
							"ajax" : {
								"url" : "${pageContext.request.contextPath}/memberapi/show_sales/get_group_list",
								"dataSrc" : "group_list",
								"error" : function() {
									alert("The group list item cannot displayed");
									$(location)
											.attr('href',
													'${pageContext.request.contextPath}/member');
								}
							},
							"columns" : [
									{
										"data" : "groupid"
									},
									{
										"data" : "groupname"
									},
									{
										"data" : "grouptype",
										"visible" : false
									},
									{
										"sortable" : false,
										"render" : function(data, type, row) {

											//console.log(row.groupid);

											return '<a class="btn btn-info btn-sm" onclick="trigger_row_action('
													+ row.groupid
													+ ','
													+ row.grouptype
													+ ',\''
													+ row.groupname
													+ '\')"'
													+ '>'
													+ 'View Items'
													+ '</a>';
										}
									} ],
							"initComplete" : function(settings, json) {
								$('#loading_modal').hide();
							}
						});

	}

	function trigger_row_action(groupid, grouptype, groupname) {

		selected_id = groupid;
		selected_groupname = groupname;

		//var selected_grouptype = grouptype;

		$('#edit_remove_group_modal').modal('show');

		document.getElementById('hidden_edit_group_id').value = groupid;
		$("#edit_group_name_input").val(selected_groupname);

		$("#delete_group_name").text(selected_groupname);
		//$("#edit_group_type_selection").val(selected_grouptype);

		table_get_group_item_list(selected_id);

		//alert("Selected_id " + selected_id);

		//item_table.ajax.reload();
	}

	/* Group Item List */
	function table_get_group_item_list(groupid) {

		console.log("gg");
		console.log(groupid);

		//$('#loading_modal').show();
		item_table = $('#datatable_item')
				.DataTable(
						{
							"ajax" : {
								"url" : "${pageContext.request.contextPath}/memberapi/show_items/manager_get_group_items/"
										+ groupid,
								"dataSrc" : "item_list",
								"error" : function() {
									alert("The group item cannot displayed");
									$(location)
											.attr('href',
													'${pageContext.request.contextPath}/ecpos/#!sales');
								}
							},
							"columns" : [ {
								"data" : "item_id"
							}, {
								"data" : "item_code"
							}, {
								"data" : "name"
							}, {
								"data" : "item_price"
							}, {
								"sortable" : false,
								"data" : "edit_remove_group_item_btns"
							/* 	"render" : function(data, type, row) {
									
								//console.log(row.image_path);
								
								if(row.image_path == null){
									
									return '<button class="btn btn-default btn-sm" onclick="update_selected_item('
									+ row.item_id
									+ ',\''
									+ row.name
									+ '\','
									+ row.item_price
									+ ',\'' 
									+ row.item_code
									+ '\',' 
									+ row.gstgroup_id
									+ ')"><i class="fa fa-pencil" aria-hidden="true"></i></button>'
									+ '<button class="btn btn-danger btn-sm" onclick="removed_selected_item('
									+ row.item_id
									+ ','
									+ row.itemgroup_id
									+ ')"><i class="fa fa-trash" aria-hidden="true"></i></button>';

								}
								else {
									return '<button class="btn btn-default btn-sm" onclick="update_selected_item('
									+ row.item_id
									+ ',\''
									+ row.name
									+ '\','
									+ row.item_price
									+ ',\'' 
									+ row.item_code
									+ '\',' 
									+ row.gstgroup_id
									+ ',\''
									+ row.image_path
									+ '\''
									+ ')"><i class="fa fa-pencil" aria-hidden="true"></i></button>'
									+ '<button class="btn btn-danger btn-sm" onclick="removed_selected_item('
									+ row.item_id
									+ ','
									+ row.itemgroup_id
									+ ')"><i class="fa fa-trash" aria-hidden="true"></i></button>';
								}
								
								} */
							}

							],

							"destroy" : true,

							"initComplete" : function(settings, json) {
								$('#loading_modal').hide();
							}
						});

	}

	function get_group_name() {
		$("#add_new_item_modal_title").text(selected_groupname);
	}

	//Edit and Updating selected item
	function update_selected_item(item_id, name, item_price, item_code,
			gstgroup_id, item_type, sst_status, image_path) {
		image_path = image_path || '';

		$('#edit_remove_group_modal').modal('hide');
		$(".modal-backdrop").remove();

		$('#edit_existing_item_modal').modal({
			backdrop : 'static',
			keyboard : false,
			show : true
		});

		$('#hidden_edit_item_id').val(parseInt(item_id));
		$('#edit_item_name_input').val(name);
		$('#edit_item_code_input').val(item_code);
		$('#edit_item_price_input').val(item_price);
		$('#edit_item_gst_group_selection').val(gstgroup_id);
		$('#edit_item_type_selection').val(item_type);
		$('#edit_item_sst_status_selection').val(sst_status);	
		//document.getElementById('hidden_edit_item_image_path').value = image_path;

		if (image_path === '') {
			console.log("no pic");
		} else {
			angular.element($('#show_item_controller_container')).scope()
					.set_existing_img(image_path);
		}

	}

	function removed_selected_item(item_id, group_id) {
		angular.element($('#show_item_controller_container')).scope()
				.removed_selected_item(item_id, group_id);
	}

	//For adding new item
	/* 	function preview_uploaded_img() {
	 var process_item_img = ($('#item_img_upload'))[0].files;

	 if (process_item_img.length > 0) {
	 var reader = new FileReader();
	 reader.readAsDataURL(process_item_img[0]);
	 reader.onload = function() {
	 var B64RESULT = reader.result;
	 document.getElementById('hidden_item_img_upload').value = B64RESULT;

	 }
	 }
	 } */

	//Get the Cropped Image (Base64) and Set it into hidden fields for server processing purpose
	function img2Base64() {
		var img_src_result = $('#cropped_img_container img').attr('src');
		//console.log(img_src_result);
		document.getElementById('hidden_item_img_upload').value = img_src_result;
	}

	//Get the Cropped Image (Base64) and Set it into hidden fields for server processing purpose
	function editImg2Base64() {
		var img_src_result = $('#edit_cropped_img_container img').attr('src');
		//console.log(img_src_result);
		document.getElementById('hidden_edit_item_img_upload').value = img_src_result;
	}

	function setTwoNumberDecimal(el) {
		if (el.value.start)
			el.value = parseFloat(el.value).toFixed(2);
	};

	//For editing existing item
	/* 	function preview_edit_uploaded_img() {
	 var process_existing_item_img = ($('#edit_item_img_upload'))[0].files;

	 if (process_existing_item_img.length > 0) {
	 var reader = new FileReader();
	 reader.readAsDataURL(process_existing_item_img[0]);
	 reader.onload = function() {
	 var B64RESULT = reader.result;
	 document.getElementById('hidden_edit_item_img_upload').value = B64RESULT;
	 }
	 }
	 } */
</script>

<body>
	<div ng-controller="Show_items_CTRL"
		id="show_item_controller_container">
		<div class="content-wrapper" style="font-size: 0.9em;">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>
					ECPOS <small>Item Group List</small>
				</h1>
				<ol class="breadcrumb">
					<li>MAIN NAVIGATION</li>
					<li>Item Group List</li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content" style="height: 85vh; overflow-y: scroll;">
				<div class="row">
					<div class="col-xs-12">

						<div class="box">
							<div class="box-header">
								<div>
									<h3 class="box-title">ECPOS Group List</h3>
									<button class="pull-right btn btn-sm btn-primary" type="button"
										data-toggle="modal" data-target="#add_new_group_modal"
										data-keyboard="false" data-backdrop="static">Add new
										group</button>
								</div>
							</div>
							<!-- /.box-header -->
							<div class="box-body">
								<table id="datatable"
									class="table table-bordered table-striped table-hover">
									<thead>
										<tr>
											<th>ID</th>
											<th>Group Name</th>
											<th>Group Type</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody id="tbody2">
									</tbody>

									<tfoot>
									</tfoot>
								</table>

							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
					</div>
					<!-- /.col -->
				</div>
				<!-- /.row -->
			</section>
			<!-- /.content -->
		</div>

		<div id="loading_modal" class="outer-div modal-content" tabindex="-1"
			role="dialog">
			<div class="inner-div">
				<div style="display: inline-block" class="loader"></div>
				</br>
				<div class="modal-body text-center">
					<p>Loading, please wait...</p>
				</div>
			</div>
		</div>

		<!-- 		<div id="item_modal" class="outer-div modal-content" tabindex="-1"
			role="dialog" style="max-width: 100vw; width: 650px;">
			<div class="inner-div">
				<div class="modal-body text-center">
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6"
						ng-repeat="item in list_of_item.item_list">
						<a>
							<div class="panel panel-default text-center">
								<div class="panel-body center-block"
									style="color: grey; font-weight: bold; font-size: small">
									{{item.name}}</div>
							</div>
						</a>
					</div>


				</div>
				<br>
				<div class="modal-body col-xs-12 text-center">
					<button type="button" class="btn btn-primary"
						onclick="CloseItemModal()">Close</button>
				</div>
				<br>
			</div>
		</div> -->

		<!-- Add New Group Modal [Started] -->
		<div id="add_new_group_modal" class="modal" tabindex="-1"
			role="dialog" aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title">Add new group</h3>
					</div>

					<div class="modal-body">
						<form name="add_new_group_form"
							ng-submit="submit_new_group_data()">
							<div class="form-group row">
								<label for="group_name_input" class="col-sm-3 col-form-label">Group
									Name</label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="group_name_input"
										name="group_name_input" placeholder="Group name"
										ng-model="group_fields.group_name"
										ng-change="resetAddNewGroupDuplicationCheck()" maxlength="25"
										required />
									<div ng-messages="add_new_group_form.group_name_input.$error"
										style="color: red" role="alert">
										<div ng-message="duplicationError">Group name duplicate</div>
									</div>
								</div>
							</div>
							<!-- 					<div class="form-group row">
								<label for="group_type_selection"
									class="col-sm-3 col-form-label">Group Type</label>
								<div class="col-sm-6">
									<select class="form-control" id="group_type_selection"
										ng-model="selected" disabled>
										<option ng-repeat="grouptype in group_type_name"
											value="{{grouptype.id}}">{{grouptype.label}}</option>
									</select>
								</div>
							</div> -->

							<div class="text-right">
								<button type="submit" class="btn btn-primary"
									ng-disabled="add_new_group_form.$invalid">Add</button>
								<button type="button" class="btn btn-secondary"
									data-dismiss="modal" ng-click="reset_form_data_fields()">Close</button>
							</div>

						</form>
					</div>

					<!-- <div class="modal-footer">
			
					</div> -->
				</div>
			</div>
		</div>
		<!-- Add New Group Modal [End] -->

		<!-- Add New Item Modal [Start] -->
		<div id="add_new_item_modal" class="modal" tabindex="-1" role="dialog"
			aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title">
							Add new item into <span id="add_new_item_modal_title"></span>
						</h3>
					</div>
					<div class="modal-body">
						<form id="add_new_item_form" name="add_new_item_form"
							method="post" enctype="multipart/form-data"
							ng-submit="create_new_item()">

							<div class="form-group row">
								<label for="item_name_input" class="col-sm-3 col-form-label">Item
									Name</label>
								<div class="col-sm-8">
									<input id="item_name_input" name="item_name_input" type="text"
										class="form-control" placeholder="Item name"
										ng-model="item_fields.item_name" maxlength="25" required /> <span></span>
								</div>
							</div>

							<div class="form-group row">
								<label for="item_code_input" class="col-sm-3 col-form-label">Item
									Code</label>
								<div class="col-sm-8">
									<input id="item_code_input" name="item_code_input" type="text"
										class="form-control" placeholder="Item code"
										ng-model="item_fields.item_code" maxlength="10" ng-change="resetItemCodeDuplicationCheck()" required />
										<div ng-messages="add_new_item_form.item_code_input.$error"
											style="color: red" role="alert">
											<div ng-message="duplicationItemCode">Item Code Duplicate</div>
										</div>
								</div>
							</div>

							<div class="form-group row">
								<label for="item_price_input" class="col-sm-3 col-form-label">Item
									Price</label>
								<div class="col-sm-8">
									<input id="item_price_input" name="item_price_input"
										type="number" class="form-control"
										onblur="setTwoNumberDecimal(this)" min="0" value="0.00"
										ng-model="item_fields.item_price"
										ng-pattern="/^[0-9]+(\.[0-9]{1,2})?$/" step="0.01" required />
								</div>
								<span></span>
							</div>

							<!-- 				<div class="form-group row">
								<label for="item_gst_group_selection"
									class="col-sm-3 col-form-label">Gst Group</label>
								<div class="col-sm-8">
									<select id="item_gst_group_selection"
										name="item_gst_group_selection" class="form-control"
										ng-model="item_fields.gst_group" required>
										<option value="" selected>--Please select--</option>
										<option ng-repeat="gst_group_code in gst_group"
											value="{{gst_group_code.id}}">
											{{gst_group_code.code}}</option>
									</select> <span></span>
								</div>
							</div>  -->

							<div class="form-group row">
								<label for="item_type_selection" class="col-sm-3 col-form-label">Item
									Type</label>
								<div class="col-sm-8">
									<select id="item_type_selection" name="item_type_selection"
										class="form-control" ng-model="item_fields.item_type" required>
										<option value="" selected>--Please select--</option>
										<option ng-repeat="item_type in itemType"
											value="{{item_type.id}}">{{item_type.type}}</option>
									</select> <span></span>
								</div>
							</div>

							<div class="form-group row">
								<label for="item_sst_status_selection"
									class="col-sm-3 col-form-label">Sst Group</label>
								<div class="col-sm-8">
									<select id="item_sst_status_selection"
										name="item_sst_status_selection" class="form-control"
										ng-model="item_fields.sst_group" required>
										<option value="" selected>--Please select--</option>
										<option ng-repeat="sst_status in sstStatus"
											value="{{sst_status.id}}">{{sst_status.code}}</option>
									</select> <span></span>
								</div>
							</div>

							<!-- Image upload -->
							<div class="form-group row">
								<label for="item_img_upload" class="col-sm-3 col-form-label">Image
									Upload</label>
								<div class="col-sm-8">
									<input id="item_img_upload" name="item_img_upload" type="file"
										accept="image/*" ng-model="item_fields.upload_img_result" />
									<input type="hidden" id="hidden_item_img_upload" />

									<!-- 						
														<input id="item_img_upload" name="item_img_upload" type="file"
										accept="image/*" onchange="preview_uploaded_img()"
										ng-model="item_fields.upload_img_result" /> <input
										type="hidden" id="hidden_item_img_upload" /> -->
								</div>
							</div>

							<div class="form-group row" id="cropped_img_container">

								<div class="col-md-6">

									<ui-cropper image="itemImage" result-image="croppedItemImage"
										area-type="{{type}}" area-min-size="selMinSize"
										area-init-size="selInitSize"
										result-image-quality="resImgQuality"
										result-image-size="resImgSize"
										result-image-format="image/jpeg" aspect-ratio="aspectRatio"
										allow-crop-resize-on-corners="false"
										disable-keyboard-access="false" canvas-scalemode="true">
									</ui-cropper>

								</div>
								<div class="col-md-6" ng-show="croppedItemImage">
									<p>Cropped Image:</p>
									<img src="{{croppedItemImage}}">
								</div>
							</div>

							<div class="text-right">
								<button type="submit" class="btn btn-primary"
									onclick="img2Base64()" ng-disabled="add_new_item_form.$invalid">Add</button>
								<button type="button" class="btn btn-secondary"
									data-toggle="modal" data-dismiss="modal"
									ng-click="reset_item_form_data_fields()"
									data-target="#edit_remove_group_modal">Close</button>
							</div>
						</form>
					</div>

				</div>
			</div>
		</div>

		<!-- Add New Item Modal [End] -->


		<!-- Edit existing item Modal [Start] -->

		<div id="edit_existing_item_modal" class="modal" tabindex="-1"
			role="dialog" aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title">
							Edit Item Detail <span id="edit_item_modal_title"></span>
						</h3>
					</div>
					<div class="modal-body">
						<form id="edit_existing_item_form" name="edit_existing_item_form"
							method="post" enctype="multipart/form-data"
							ng-submit="edit_existing_item()">

							<input type="hidden" id="hidden_edit_item_id"
								ng-model="item_fields.item_id" /> <input type="hidden"
								id="hidden_edit_item_image_path"
								ng-model="item_fields.image_path" />

							<div class="form-group row">
								<label for="edit_item_name_input"
									class="col-sm-3 col-form-label">Item Name</label>
								<div class="col-sm-8">
									<input id="edit_item_name_input" name="edit_item_name_input"
										type="text" class="form-control" placeholder="Item name"
										ng-model="item_fields.item_name" required />
								</div>
								<span></span>
							</div>

							<div class="form-group row">
								<label for="edit_item_code_input"
									class="col-sm-3 col-form-label">Item Code</label>
								<div class="col-sm-8">
									<input id="edit_item_code_input" name="edit_item_code_input"
										type="text" class="form-control" placeholder="Item code"
										ng-change="resetEditItemCodeDuplicationCheck()"
										ng-model="item_fields.item_code" maxlength="10" required />
										<div ng-messages="edit_existing_item_form.edit_item_code_input.$error"
											style="color: red" role="alert">
											<div ng-message="duplicationItemCode">Item Name Duplicate</div>
										</div>
								</div>
							</div>

							<div class="form-group row">
								<label for="edit_item_price_input"
									class="col-sm-3 col-form-label">Item Price</label>
								<div class="col-sm-8">
									<input id="edit_item_price_input" name="edit_item_price_input"
										type="number" class="form-control" placeholder="Item price"
										ng-model="item_fields.item_price"
										ng-pattern="/^[0-9]+(\.[0-9]{1,2})?$/" step="0.01" required />
									<span></span>
								</div>
							</div>

							<!-- <div class="form-group row">
								<label for="edit_item_gst_group_selection"
									class="col-sm-3 col-form-label">Gst Group</label>
								<div class="col-sm-8">
									<select id="edit_item_gst_group_selection"
										name="edit_item_gst_group_selection"
										ng-model="item_fields.gst_group" required>
										<option value="" selected>--Please select--</option>
										<option ng-repeat="gst_group_code in gst_group"
											value="{{gst_group_code.id}}">
											{{gst_group_code.code}}</option>
									</select> <span></span>
								</div>
							</div> -->
							
							<div class="form-group row">
								<label for="edit_item_type_selection" class="col-sm-3 col-form-label">Item
									Type</label>
								<div class="col-sm-8">
									<select id="edit_item_type_selection" name="edit_item_type_selection"
										class="form-control" ng-model="item_fields.item_type" required>
										<option value="" selected>--Please select--</option>
										<option ng-repeat="item_type in itemType"
											value="{{item_type.id}}">{{item_type.type}}</option>
									</select> <span></span>
								</div>
							</div>

							<div class="form-group row">
								<label for="edit_item_sst_status_selection"
									class="col-sm-3 col-form-label">Sst Group</label>
								<div class="col-sm-8">
									<select id="edit_item_sst_status_selection"
										name="edit_item_sst_status_selection" class="form-control"
										ng-model="item_fields.sst_group" required>
										<option value="" selected>--Please select--</option>
										<option ng-repeat="sst_status in sstStatus"
											value="{{sst_status.id}}">{{sst_status.code}}</option>
									</select> <span></span>
								</div>
							</div>

							<!-- Image upload -->
							<div class="form-group row">
								<label for="edit_item_img_upload"
									class="col-sm-3 col-form-label">Image Upload</label>
								<div class="col-sm-8">
									<input id="edit_item_img_upload" name="edit_item_img_upload"
										type="file" accept="image/*"
										ng-model="item_fields.upload_img_result" /> <input
										type="hidden" id="hidden_edit_item_img_upload" />

									<!-- 			<input id="edit_item_img_upload" name="edit_item_img_upload"
										type="file" accept="image/*"
										ng-model="item_fields.upload_img_result"
										onchange="preview_edit_uploaded_img()" /> <input
										type="hidden" id="hidden_edit_item_img_upload" /> -->
								</div>
							</div>


							<div class="form-group row" ng-show="image_path_holder">
								<div class="col-md-12">
									<h3>Uploaded Image:</h3>
									<img class="img-responsive" ng-src="{{image_path_holder}}" />
								</div>
							</div>


							<div class="form-group row" id="edit_cropped_img_container">

								<div class="col-md-6">
									<ui-cropper image="itemImage" result-image="croppedItemImage"
										area-type="{{type}}" area-min-size="selMinSize"
										area-init-size="selInitSize"
										result-image-quality="resImgQuality"
										result-image-size="resImgSize"
										result-image-format="image/jpeg" aspect-ratio="aspectRatio"
										allow-crop-resize-on-corners="false"
										disable-keyboard-access="false" canvas-scalemode="true">
									</ui-cropper>

								</div>
								<div class="col-md-6" ng-show="croppedItemImage">
									<p>Cropped Image:</p>
									<img src="{{croppedItemImage}}">
								</div>
							</div>

							<div class="text-right">
								<button type="submit" class="btn btn-primary"
									onclick="editImg2Base64()">Update</button>
								<button type="button" class="btn btn-secondary"
									data-toggle="modal" data-dismiss="modal"
									ng-click="reset_edit_existing_item_form_data_fields()"
									data-target="">Close</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- Edit existing item Modal [End] -->




		<!-- Edit Group Data Modal [Start] -->
		<div id="edit_remove_group_modal" class="modal" tabindex="-1"
			role="dialog" aria-hidden="true" data-keyboard="false"
			data-backdrop="static">
			<div class="modal-dialog" role="document">
				<div class="modal-content">

					<!-- <div class="modal-header"></div> -->


					<div class="modal-body">

						<ul class="nav nav-tabs">
							<li class="active"><a data-toggle="tab"
								data-target="#edit_group_name">Modified Group</a></li>
							<li><a data-toggle="tab" data-target="#remove_group_name">Item
									Group </a></li>

							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</ul>



						<div class="tab-content">

							<!-- Edit Group Name -->

							<div id="edit_group_name" class="tab-pane fade in active">
								<div class="inner-div">
									<form name="edit_group_form" ng-submit="update_group_data()">

										<input type="hidden" id="hidden_edit_group_id" />

										<div class="form-group row">
											<label for="edit_group_name_input"
												class="col-sm-3 col-form-label">Group Name</label>
											<div class="col-sm-8">
												<input type="text" class="form-control"
													id="edit_group_name_input" name="edit_group_name_input"
													ng-model="group_fields.group_name"
													ng-change="resetEditNewGroupDuplicationCheck()"
													placeholder="Group name" required />
												<div
													ng-messages="edit_group_form.edit_group_name_input.$error"
													style="color: red" role="alert">
													<div ng-message="duplicationEditError">Group name
														duplicate</div>
												</div>
											</div>

										</div>
										<!-- 				<div class="form-group row">
											<label for="edit_group_type_selection"
												class="col-sm-3 col-form-label">Group Type</label>
											<div class="col-sm-6">
												<select class="form-control" id="edit_group_type_selection"
													ng-model="selected" disabled>
													<option ng-repeat="grouptype in group_type_name"
														value="{{grouptype.id}}">{{grouptype.label}}</option>
												</select>
											</div>
										</div> -->


										<div id="modified_group_buttons_container">
											<div class="text-left" style="display: inline;">
												<button class="btn btn-danger"
													onclick="removed_selected_group()">Delete</button>
											</div>
											<div class="pull-right" style="display: inline;">
												<button class="btn btn-primary" style="display: inline;"
													type="submit">Update</button>
											</div>
										</div>

									</form>
								</div>
							</div>

							<!-- Remove Group Name -->

							<div id="remove_group_name" class="tab-pane fade">


								<div class="box">
									<div class="box-header">
										<div>
											<h3 class="box-title">ECPOS Item List</h3>
											<button class="pull-right btn btn-sm btn-primary"
												type="button" data-toggle="modal"
												data-target="#add_new_item_modal" data-dismiss="modal"
												onclick="get_group_name()" data-keyboard="false"
												data-backdrop="static">Add new Item</button>
										</div>
									</div>

									<div>
										<div class="box-body table-responsive">
											<table id="datatable_item"
												class="table table-bordered table-striped table-hover">
												<thead>
													<tr>
														<th>Id</th>
														<th>Item Code</th>
														<th>Name</th>
														<th>Price</th>
														<th>Action</th>
													</tr>
												</thead>
												<tbody id="tbody2">
												</tbody>

												<tfoot>
												</tfoot>
											</table>
										</div>
									</div>







								</div>





							</div>
						</div>

					</div>

				</div>
			</div>
		</div>


		<!-- Edit Group Data Modal [End] -->




	</div>
</body>
</html>




