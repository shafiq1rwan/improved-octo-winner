<script>
	app.controller('transactions_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getTransactionsList();
				} else {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.getTransactionsList = function() {
			var table = $('#datatable_transactions').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/transaction/get_transaction_list",
					"error" : function() {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}";
					}
				},
				"searching": false,
				"pageLength": 9,
				"bLengthChange": false,
				"order" : [ [ 0, "desc" ] ],
				destroy : true,
				"columns" : [{"data" : "id"},
					{"data" : "checkNumber"},
					{"data" : "staffName"}, 
					{"data" : "transactionType"},
					{"data" : "paymentType"},
					{"data" : "transactionAmount"},
					{"data" : "transactionStatus"},
					{"data" : "transactionDate"}/* ,
					{"render" : function(data, type, full, meta) {
						return '<div><button class="btn btn-sm btn-info" ng-click="getTransactionDetails('+ full.id + ')">Details</button></div>';
					}} */],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
		}
	});
</script>