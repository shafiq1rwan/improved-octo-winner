<script>
	app
			.controller(
					'Connection_QR_CTRL',
					function($scope, $http, $timeout) {

						$scope.getQRConnection = function() {

							$http
									.post(
											"${pageContext.request.contextPath}/ecposcontroller/getconnection_QR")
									.then(
											function(response) {

												console.log("Data: "
														+ response.data.QR);

												if (response.data.QR) {
													console.log("Success");

													document
															.getElementById("QRtext").value = response.data;

													var qrcode = new QRCode(
															document
																	.getElementById("qrcode"),
															{
																width : 150,
																height : 150
															});
													
													function makeCode() {
														var elText = document.getElementById("QRtext");
														if (!elText.value) {
															elText.focus();
															return;
														}
														qrcode.makeCode(elText.value);
													}

													makeCode();
												} else {
													console.log("No Data");
												}
											},
											function(response) {
												alert("Cannot Generate QR Connection!");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											});
						}

			
					});
</script>
