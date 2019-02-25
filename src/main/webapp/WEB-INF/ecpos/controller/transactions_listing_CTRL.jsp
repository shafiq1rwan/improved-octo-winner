<script>
	app.controller('transactions_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.initiation = function() {
			$scope.getTransactionsList();
		}
		
		$scope.getTransactionsList = function() {
			var table = $('#datatable_transactions').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/transaction/get_transaction_list",
					"error" : function() {
						alert("Tranasction list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"pageLength": 9,
				"bLengthChange": false,
				"order" : [ [ 0, "desc" ] ],
				destroy : true,
				"columns" : [{"data" : "id"}, 
					{"data" : "staffName"}, 
					{"data" : "checkNumber"},
					{"data" : "transactionDate"},
					{"data" : "transactionType"},
					{"data" : "paymentMethod"},
					{"data" : "paymentType"},
					{"data" : "terminalName"},
					{"data" : "transactionAmount"},
					{"data" : "transactionStatus"},
					{"render" : function(data, type, full, meta) {
						return '<div><button class="btn btn-sm btn-info" ng-click="getTransactionDetails('+ full.id + ')">Details</button></div>';
					}}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
		}
	});
</script>