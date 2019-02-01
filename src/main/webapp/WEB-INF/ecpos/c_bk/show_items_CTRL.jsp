<script>
	app
			.controller(
					'Show_items_CTRL',
					function($scope, $http, $window) {

						$scope.list_of_group = {};
						$scope.list_of_item = {};
						$scope.new_group_name = "";
						$scope.group_type_name = [];

						$scope.item_fields = {};
						$scope.item_fields.item_price = 0;

						$scope.group_fields = {};

						retrieve_staff_name();

						//GST related variables and functions

						$scope.gst_group = [ {
							id : 1,
							code : "SR"
						}, {
							id : 2,
							code : "ZR"
						} ];
						
						$scope.sstStatus = [{
							id : 1,
							code : "SR"
						}, {
							id : 2,
							code : "ZR"
						}];
						
						$scope.itemType = [{
							id : 1,
							type : "Goods"
						}, {
							id : 2,
							type : "Service"
						}];
						
						//UI Cropper
						$scope.itemImage = "";
						$scope.croppedItemImage = "";
						$scope.resImgQuality = 1;
						$scope.selMinSize = 100;
						$scope.selInitSize = [ {
							w : 200,
							h : 80
						} ];
						$scope.resImgSize = [ {
							w : 200,
							h : 150
						}, {
							w : 400,
							h : 300
						} ];
						$scope.type = 'square';

						$scope.get_group_items = function(groupid) {
							$http
									.get(
											"${pageContext.request.contextPath}/memberapi/show_sales/get_group_items/"
													+ groupid)
									.then(
											function(response) {
												$scope.list_of_item = response.data;

												console.log(response.data);
												ShowItemModal();
											},
											function(response) {
												alert("Error Occured While Displaying Items");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/member');
											});
						}

						//Create new group. Once success, refresh the table and crete group modal 
						$scope.submit_new_group_data = function() {

							console.log("Create form data");
							console.log($scope.group_fields.group_name);

							if ($scope.group_fields.group_name) {
								if ($scope.staff_name) {

									$scope.group_fields.staff_name = $scope.staff_name;

									var jsonData = JSON
											.stringify($scope.group_fields);
									console.log(jsonData);

									$http
											.post(
													"${pageContext.request.contextPath}/memberapi/show_items/manager_create_menugroup",
													jsonData)
											.then(
													function(response) {
														$scope.create_menugroup = response.data;

														console
																.log("Create new group");
														console
																.log(response.data);

														if (response.data.response_code == "00") {
															//Refresh the datatable
															table.ajax.reload();

															$(
																	'#add_new_group_modal')
																	.modal(
																			'hide');
															$(".modal-backdrop")
																	.remove();
														}
														else {
															$scope.add_new_group_form.group_name_input.$setValidity('duplicationError', false);
														}
													});

								}
							}

							$scope.reset_form_data_fields();
						}

						//Edit Existing Group Data
						$scope.update_group_data = function() {

							$scope.group_fields.group_id = document
									.getElementById("hidden_edit_group_id").value;
							$scope.group_fields.group_name = $(
									"#edit_group_name_input").val();

							console.log("Group Id");
							console.log($scope.group_fields.group_id);
							console.log("Group name");
							console.log($scope.group_fields.group_name);

							var jsonData = JSON.stringify($scope.group_fields);
							console.log(jsonData);

							$http
									.post(
											"${pageContext.request.contextPath}/memberapi/show_items/update_menugroup",
											jsonData)
									.then(
											function(response) {
												if (response.data.response_code == "00") {
													//Refresh the datatable
													table.ajax.reload();
													
													$scope.reset_edit_group_form_data_fields();

													$('#edit_remove_group_modal').modal('hide');
													$(".modal-backdrop").remove();											
												} else {
													console.log(response.data);
													$scope.edit_group_form.edit_group_name_input.$setValidity('duplicationEditError', false);
												}
											});

					
						}

						//Retrieve logged in staff name for further usage
						function retrieve_staff_name() {

							$http
									.get(
											"${pageContext.request.contextPath}/memberapi/show_sales/get_staff_name")
									.then(
											function(response) {

												$scope.staff_name = response.data.staff_name;

											},
											function(response) {
												alert("Session TIME OUT");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/member');
											});

						}

						//Reset the Create Group Modal and Form Fields
						$scope.reset_form_data_fields = function() {
							$('#group_name_input').val("");
							$scope.group_fields = {};
						}

						//Reset the Edit Group Modal and Form Fields
						$scope.reset_edit_group_form_data_fields = function() {
							$('#hidden_edit_group_id').val("");
							$('#edit_group_name_input').val("");
							console.log("After");
							console.log($('#hidden_edit_group_id').val());
							$scope.group_fields = {};
						}

						//Reset the Add Item Modal and Form Fields
						$scope.reset_item_form_data_fields = function() {
							$('#item_name_input').val("");
							$('#item_price_input').val(0.00);
							/* 		$('#item_gst_group_selection').val(""); */
							$('#item_img_upload').val("");
							document.getElementById('hidden_item_img_upload').value = "";

							$scope.itemImage = "";
							$scope.croppedItemImage = "";
							$scope.item_fields = {};
						}

						//Reset the Edit Item Modal and Form Fields
						$scope.reset_edit_existing_item_form_data_fields = function() {
							$('#hidden_edit_item_id').val("");
							$('#edit_item_name_input').val("");
							$('#edit_item_price_input').val(0.00);
							$('#edit_item_gst_group_selection').val("");
							$('#edit_item_img_upload').val("");
							document
									.getElementById('hidden_edit_item_img_upload').value = "";

							$scope.itemImage = "";
							$scope.croppedItemImage = "";

							$scope.image_path_holder = "";

							$scope.item_fields = {};
						}
						
						
						//Handle Image Upload Selection and Update Cropped Image Field
						var handleFileSelect = function(evt) {
							
							//Dismiss the Uploaded Image if Input File got new Image
							$scope.image_path_holder = ""; 
							
							var file = evt.currentTarget.files[0];
							reader = new FileReader();

							reader.onload = function(evt) {
								$scope.$apply(function($scope) {
									console.log(evt.target.result);
									$scope.itemImage = evt.target.result;

								});
							};
							reader.readAsDataURL(file);

							/* 		if (navigator.userAgent.match(/iP(hone|od|ad)/i)) {
									var canvas = document.createElement('canvas'), mpImg = new MegaPixImage(
											file);

									canvas.width = mpImg.srcImage.width;
									canvas.height = mpImg.srcImage.height;

									EXIF.getData(file, function() {
										var orientation = EXIF.getTag(this,
												'Orientation');

										mpImg.render(canvas, {
											maxHeight : $scope.resImgSize,
											orientation : orientation
										});
										
											var tt = canvas.toDataURL("image/jpeg",1);
											$scope.$apply(function($scope) {
												$scope.itemImage = tt;
											});
											
									});
								} else {
									reader.onload = function(evt) {
										$scope
												.$apply(function($scope) {
													//console.log(evt.target.result);
													$scope.itemImage = evt.target.result;
												});
									};
									reader.readAsDataURL(file);
								}   */
						};

						//Handle Input File Upload for Add Item Modal
						angular.element(
								document.querySelector('#item_img_upload')).on(
								'change', handleFileSelect);

						//Handle Input File Upload for Edit Item Modal
						angular
								.element(
										document
												.querySelector('#edit_item_img_upload'))
								.on('change', handleFileSelect);

						/* 	$scope.removed_selected_item = function(selected_itemid){
						 var itemid = selected_itemid.toString();
						
						 $http.post("${pageContext.request.contextPath}/memberapi/show_items/remove_menuitem", itemid)
						 .then(
						 function(response) {
						
						 if(response.data.response_code == "00")
						 {					
						 $("#").modal("hide");
						 $(".modal-backdrop").remove();
						
						 table.ajax.reload();
						 console.log("Success");
						 }
						 },
						 function(response) {
						 alert("Error Occured While Removing Selected Item");
						 $(location)
						 .attr('href',
						 '${pageContext.request.contextPath}/member');
						 }

						 );
						
						
						
						
						 } */

						//Create new item
						$scope.create_new_item = function() {

							if ($scope.staff_name) {
								$scope.item_fields.staff_name = $scope.staff_name;
								$scope.item_fields.group_name = selected_groupname;
								$scope.item_fields.gst_group = $scope.gst_group[0].id;

								if (!$scope.item_fields.upload_img_result) {
									$scope.item_fields.upload_img_result = document
											.getElementById("hidden_item_img_upload").value;
								} else {
									$scope.item_fields.upload_img_result = "";
								}

								var jsonData = JSON
										.stringify($scope.item_fields);
								console.log(jsonData);

								$http
										.post(
												"${pageContext.request.contextPath}/memberapi/show_items/manager_create_menuitem",
												jsonData)
										.then(
												function(response) {

													console
															.log(response.data.response_message);

													if (response.data.response_code == "00") {
														$("#add_new_item_modal")
																.modal("hide");
														$(".modal-backdrop")
																.remove();

														$scope
																.reset_item_form_data_fields();

														$(
																"#edit_remove_group_modal")
																.modal("show");
														table_get_group_item_list($window.selected_id);

														console
																.log("Success add record");
													} else if (response.data.response_code == "01"
															&& response.data.response_message == "INVALID GST GROUP") {
														console
																.log("INVALID GST GROUP");
													} else if (response.data.response_code == "01"
															&& response.data.response_message == "INVALID GROUP NAME") {
														console
																.log("INVALID GROUP NAME");
													} else if (response.data.response_code == "01"
															&& response.data.response_message == "STAFF NOT FOUND") {
														console
																.log("STAFF NOT FOUND");
													} else if (response.data.response_code == "01"
															&& response.data.response_message == "INVALID REQUEST") {
														console
																.log("INVALID REQUEST");
													} else if (response.data.response_code == "02") {
														console
																.log("Duplicate ItemId founded");
													} else {
														console.log("Failure");
														$scope.add_new_item_form.item_code_input.$setValidity('duplicationItemCode', false);
													}
												},
												function(response) {
													alert("Error Occured While Adding New Item");
													$(location)
															.attr('href',
																	'${pageContext.request.contextPath}/member');
												});
							} else {
								alert("SESSION TIMEOUT");
								$(location)
										.attr('href',
												'${pageContext.request.contextPath}/member');
							}

						}

						//Set the Uploaded Image when Edit Item Modal is trigged
						$scope.set_existing_img = function(image_path) {
							$scope.image_path_holder = image_path;
							$scope.$apply();
							//console.log($scope.image_path_holder);
						}

						//Edit existing item
						$scope.edit_existing_item = function() {

							/* var item_price = $('#edit_item_price_input').val().toString();
								console.log(item_price); */

							$scope.item_fields.item_id = document
									.getElementById("hidden_edit_item_id").value;
							$scope.item_fields.item_name = $(
									'#edit_item_name_input').val();
							$scope.item_fields.item_code = $(
									'#edit_item_code_input').val();
							$scope.item_fields.item_price = parseFloat($(
									'#edit_item_price_input').val());
					/* 		$scope.item_fields.gst_group = $(
									'#edit_item_gst_group_selection').val(); */
							$scope.item_fields.item_type = $('#edit_item_type_selection').val();
							$scope.item_fields.sst_group = $('#edit_item_sst_status_selection').val();
							
							if (!$scope.item_fields.upload_img_result) {
								$scope.item_fields.upload_img_result = document
										.getElementById("hidden_edit_item_img_upload").value;
							} else {
								$scope.item_fields.upload_img_result = "";
							}

							var jsonData = JSON.stringify($scope.item_fields);
							console.log(jsonData);

							 				$http
												.post(
														"${pageContext.request.contextPath}/memberapi/show_items/manager_update_menuitem",
														jsonData)
												.then(
														function(response) {
															if (response.data.response_code == "00") {

																table_get_group_item_list($window.selected_id);
																
																$('#edit_existing_item_modal').modal('hide');
																$(".modal-backdrop").remove();
																
																$('#edit_remove_group_modal').modal({
																	backdrop : 'static',
																	keyboard : false,
																	show : true
																});
															}
															else {
																$scope.edit_existing_item_form.edit_item_code_input.$setValidity('duplicationItemCode', false);
															}
														}); 

						}

						//Remove selected item
						$scope.removed_selected_item = function(
								selected_itemid, selected_groupid) {
							var item_id = selected_itemid.toString();

							$http
									.post(
											"${pageContext.request.contextPath}/memberapi/show_items/manager_remove_menuitem",
											item_id)
									.then(
											function(response) {
												console.log("Deleted Result");
												console
														.log(response.data.response_code);

												if (response.data.response_code == "00") {
													table_get_group_item_list(selected_groupid);
												}
											},
											function(response) {
												alert("Error Occured While Removing Selected Item");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos#!sales');
											});

						}

						$scope.removed_selected_group = function(
								selected_groupid) {
							var group_id = selected_groupid.toString();

							$http
									.post(
											"${pageContext.request.contextPath}/memberapi/show_items/remove_menugroup",
											group_id)
									.then(
											function(response) {

												if (response.data.response_code == "00") {
													$(
															"#edit_remove_group_modal")
															.modal("hide");
													$(".modal-backdrop")
															.remove();

													table.ajax.reload();
													console.log("Success");
												}
											},
											function(response) {
												alert("Error Occured While Removing Item Group");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											});
						}
						
						//Success
						$scope.resetAddNewGroupDuplicationCheck = function(){
							$scope.add_new_group_form.group_name_input.$setValidity('duplicationError', true);
						}
						
						//Success
						$scope.resetEditNewGroupDuplicationCheck = function(){
							$scope.edit_group_form.edit_group_name_input.$setValidity('duplicationEditError', true);
						}
						
						//Success
						$scope.resetEditItemCodeDuplicationCheck = function(){
							$scope.edit_existing_item_form.edit_item_code_input.$setValidity('duplicationItemCode', true);
						}
						
						//Success
						$scope.resetItemCodeDuplicationCheck = function(){
							$scope.add_new_item_form.item_code_input.$setValidity('duplicationItemCode', true);
						}
						
					})
			.directive(
					'duplicateItemCode',
					function($http, $q, $timeout) {
						return {
							restrict : 'A',
							require : 'ngModel',
							link : function(scope, element, attributes, control) {

								control.$asyncValidators.hahaha = function(
										modelValue, viewValue) {

									console.log(viewValue);

									if (control.$isEmpty(modelValue)) {
										return $q.resolve();
									}

									var deferred = $q.defer();

									$http
											.get(
													"${pageContext.request.contextPath}/memberapi/show_items/check_duplicate_item_code/"
															+ viewValue)
											.then(
													function(response) {

														$timeout(
																function() {
																	if (response.data.response_code == "00") {
																		deferred
																				.reject();
																	} else {
																		deferred
																				.resolve();
																	}
																}, 500);

													});

									return deferred.promise;
								}

							}
						};
					})

			.directive(
					'duplicateGroupName',
					function($q, $timeout, $http) {
						return {
							restrict : 'A',
							require : 'ngModel',
							link : function(scope, element, attributes, control) {

								control.$asyncValidators.duplicate_group_name = function(
										modelValue, viewValue) {
									if (control.$isEmpty(modelValue)) {
										return $q.when();
									}

									var defer = $q.defer();

									$http
											.get(
													"${pageContext.request.contextPath}/memberapi/show_items/check_duplicate_item_group_name/"
															+ viewValue)
											.then(
													function(response) {

														$timeout(
																function() {
																	if (response.data.response_code == "00") {
																		defer
																				.reject();
																	} else {
																		defer
																				.resolve();
																	}
																}, 500);

													});

									return defer.promise;
								};
							}
						};
					});

</script>
