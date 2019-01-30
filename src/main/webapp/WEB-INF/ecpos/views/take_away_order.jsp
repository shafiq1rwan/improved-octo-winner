<!DOCTYPE html>
<html>
<head>
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

hr {
	margin-top: 5px;
	margin-bottom: 5px;
}
</style>
</head>

<body>
	<div ng-controller="take_away_order_CTRL">
		<div ng-init="initiation();"></div>
	</div>
</body>
</html>