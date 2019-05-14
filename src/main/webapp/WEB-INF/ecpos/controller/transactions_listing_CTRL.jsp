<script>
	app.controller('transactions_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		
		$scope.transaction = {};
		$scope.voidMessage = "";
		
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getTransactionsList();
				} else {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
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
					"dataSrc": function ( json ) {                
		                return json.data;
		            },
					"error" : function() {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
				},
				"searching": false,
				"pageLength": 8,
				"bLengthChange": false,
				"order" : [ [ 0, "desc" ] ],
				destroy : true,
				"columns" : [{"data" : "checkNumber"},
					{"data" : "staffName"}, 
					{"data" : "transactionType"},
					{"data" : "paymentMethod"},
					{"data" : "paymentType"},
					{"data" : "transactionAmount"},
					{"data" : "transactionStatus"},
					{"data" : "transactionDate"},
				 	{"data" : "id", "visible": false, "searchable": false}
					],
				rowCallback: function(row, data, index){
			    	$(row).find('td:eq(1)').css('color', 'blue');
			    	
			    	$(row).mouseenter (function() {
			    		$(row).find('td:eq(1)').css('text-decoration', 'underline');
		    		});
			    	
			    	$(row).mouseleave (function() {
			    		$(row).find('td:eq(1)').css('text-decoration', 'none');
		    		});
				},
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
			
			$('#datatable_transactions tbody').off('click', 'td');
			$('#datatable_transactions tbody').on('click', 'td', function(){				
	 			if ($(this).index() == 1) {
					$http({
						method : 'GET',
						headers : {'Content-Type' : 'application/json'},
						url : '${pageContext.request.contextPath}/rc/transaction/get_transaction_details?id=' + table.row(this.closest('tr')).data().id			
					})
					.then(function(response) {
						if(response.status == "200") {
							$scope.transaction.id = response.data.id;
							$scope.transaction.isVoid = response.data.isVoid;
							$scope.transaction.isApproved = response.data.isApproved;
							$('#transactionDetailsModal').modal('show');
						}
					});
				} 
			});
		}
		
		$scope.voidTransaction = function(transactionId){
			var jsonData = JSON.stringify({
				"transactionId" : transactionId
			});
			
			$scope.voidMessage = "Void In Progress";
			$('#transactionDetailsModal').modal('hide');
			
			$('#loading_modal').modal('show');

 			$http.post("${pageContext.request.contextPath}/rc/transaction/void_transaction", jsonData)
			.then(function(response) {
				if(response.data.response_code == '00'){
					alert(response.data.response_message);
					$('#loading_modal').modal('hide');
					$scope.getTransactionsList();
				} else {
					alert(response.data.response_message);
					$('#loading_modal').modal('hide');
				}
				$scope.voidMessage = "";
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
	});
</script>