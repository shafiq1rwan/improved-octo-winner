$.getScript("./meta/jquery/is.js", function(){
	   //alert("Script loaded but not necessarily executed.");
var isAndroidApp = is.mobile();

//jQuery time
var current_fs, next_fs, previous_fs; //fieldsets
var left, opacity, scale; //fieldset properties which we will animate
var animating; //flag to prevent quick multi-click glitches
var pageNo = 1;
function showAndroidToast(toast) {
    /*Android.showToast(toast);*/
}

function nextPage(add, isValid)
{
		if(isValid){
			if(animating) return false;
			animating = true;
			
			//console.log(add);
			//console.log($(this).parent());
			//console.log($(this).next());
			current_fs = add.parent();
			next_fs = add.parent().next();
			//console.log(next_fs);
//			 current_fs = $('[name=next]').parent();
//			 next_fs = $('[name=next]').parent().next();
			
			
			//activate next step on progressbar using the index of next_fs
			$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
			
			//show the next fieldset
			next_fs.show(); 
			//hide the current fieldset with style
			current_fs.animate({opacity: 0}, {
				step: function(now, mx) {
					//as the opacity of current_fs reduces to 0 - stored in "now"
					//1. scale current_fs down to 80%
					scale = 1 - (1 - now) * 0.2;
					//2. bring next_fs from the right(50%)
					left = (now * 50)+"%";
					//3. increase opacity of next_fs to 1 as it moves in
					opacity = 1 - now;
					current_fs.css({
		        'transform': 'scale('+scale+')',
		        'position': 'absolute'
		      });
					next_fs.css({'left': left, 'opacity': opacity});
				}, 
				duration: 800, 
				complete: function(){
					current_fs.hide();
					animating = false;
				}, 
				//this comes from the custom easing plugin
				easing: 'easeInOutBack'
			});
			pageNo++;
			return true;
		}else{
			return false;
		}	
}

function checkUsername(username)
{
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	var url = "registration/checkUsername.jsp?username=" + username;
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == XMLHttpRequest.DONE) {
			if (xmlhttp.status == 200) {
				document.getElementById("isDuplicate").value =  xmlhttp.responseText.trim();
			}else if (xmlhttp.status == 400) {
				alert('There was an error 400')
			} else {
				alert('something else other than 200 was returned')
			}
			
		}
	}

	xmlhttp.open("POST", url, true);
	xmlhttp.send();
}

$(document).ready(function (){
	$('#rLoading_modal').hide();
	$('#rSuccess_modal').hide();
	
	$(".next").click(function(){
		console.log(pageNo);
		var isValid = true;
		if(pageNo == 1){	//account creation page
			var fullName = $("#fullName").val();
			var nric = $("#ic_passport").val();
			var dob = $("#dob").val();
			var race = $("#race").val();
			var gender = $("input[name=gender]").is(':checked');
			var tel = $("input[name=mobileNo]").val();
			var email = $("input[name=email]").val();
				var atpos = email.indexOf("@");
				var dotpos = email.lastIndexOf(".");
			var add1 = $("input[name=add1]").val();
			var postalCode = $("input[name=postalCode]").val();
			if(fullName==""){
				isValid = false;
				alert("Please insert Full Name.")
				$( "#fullName" ).focus();
				$('html, body').animate({
			        scrollTop: $("#fullName").offset().top-200
			    }, 1000);
			}
			else if(nric==""){
				isValid = false;
				alert("Please enter NRIC / Passport Number.")
				$( "input[name=ic_passport]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=ic_passport]").offset().top-200
			    }, 1000);
			}else if(isNaN(tel) || tel.length<10){
				isValid = false;
				alert("Please insert correct Phone Number.")
				$("input[name=mobileNo]").focus();
				$('html, body').animate({
			        scrollTop: $("input[name=mobileNo]").offset().top-200
			    }, 1000);
			}else if(dob ==""){
				isValid = false;
				alert("Please enter Date of Birth.")
				$( "input[name=dob]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=dob]").offset().top-200
			    }, 1000);
			}
			else if(race ==""){
				isValid = false;
				alert("Please select your race.")
				$( "#race" ).focus();
				$('html, body').animate({
			        scrollTop: $("#race").offset().top-200
			    }, 1000);
			}else if(!gender){
				isValid = false;
				alert("Please select a Gender.")
				$( "input[name=gender]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=gender]").offset().top-200
			    }, 1000);
			}else if(add1==""){
				isValid = false;
				alert("Please insert Address.")
				$( "input[name=add1]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=add1]").offset().top-200
			    }, 1000);
			}else if(isNaN(postalCode) || postalCode.length<5){
				isValid = false;
				alert("Please insert correct postal code.")
				$( "input[name=postalCode]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=postalCode]").offset().top-200
			    }, 1000);
			}
			else if(atpos<1 || dotpos<atpos+2 || dotpos+2>=email.length){
				isValid = false;
				alert("Please insert a valid Email.")
				$( "input[name=email]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=email]").offset().top-200
			    }, 1000);
			}
		}
		else if(pageNo == 2) {
			var packageOpt = $("input[name=packageOpt]").is(':checked');
			if(!packageOpt) {
				isValid = false;
				alert("Please select a package.")
				$( "input[name=packageOpt]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=packageOpt]").offset().top-200
			    }, 1000);
			}
		}
		
		if(pageNo == 1 && isValid) {
			$('#registration_details').contents().unwrap();
			$( "#registration_details" ).remove();
		}
		
		if(pageNo == 2) {
			$('#package_details').contents().unwrap();
			$( "#package_details" ).remove();
		}
		
		if(pageNo != 3) {
			var add = $(this);
			setTimeout(function(){nextPage(add, isValid)}, 500);
		}
		else {
			var password = $("#password").val();
			if(password =='') {
				alert("Please key in your password!")
				$( "input[name=password]" ).focus();
				$('html, body').animate({
			        scrollTop: $("input[name=password]").offset().top-200
			    }, 1000);
			}
			else {
				//$('.next').prop('disabled', true);
				$('.previous').prop('disabled', true);
		        $('#rLoading_modal').show();
		        sendEnrollmentReq();
			}
			
		}
	});

	$(".previous").click(function(){
		
		if(animating) return false;
		animating = true;
		
		current_fs = $(this).parent();
		previous_fs = $(this).parent().prev();
		//de-activate current step on progressbar
		$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
		
		//show the previous fieldset
		previous_fs.show(); 
		//hide the current fieldset with style
		current_fs.animate({opacity: 0}, {
			step: function(now, mx) {
				//as the opacity of current_fs reduces to 0 - stored in "now"
				//1. scale previous_fs from 80% to 100%
				scale = 0.8 + (1 - now) * 0.2;
				//2. take current_fs to the right(50%) - from 0%
				left = ((1-now) * 50)+"%";
				//3. increase opacity of previous_fs to 1 as it moves in
				opacity = 1 - now;
				current_fs.css({'left': left});
				previous_fs.css({'transform': 'scale('+scale+')', 'opacity': opacity});
			}, 
			duration: 800, 
			complete: function(){
				current_fs.hide();
				animating = false;
			}, 
			//this comes from the custom easing plugin
			easing: 'easeInOutBack'
		});
		pageNo--;
	});
	
	$('#signin').on('click', function() {
	    $(this).html('<i class="fa fa-spinner fa-spin  fa-fw margin-bottom"></i> Please wait...');
	    setTimeout(function() {
		       location="/managepay/ServiceMain";
		}, 3000);
	});
	
	$("a[name=request_activation]").click(function(){
		$(this).html('<i class="fa fa-spinner fa-spin  fa-fw margin-bottom"></i> Please wait...');
		setTimeout(function() {
			alert("Request approved. Activation code will send to your registered email withing 24 hours.");
			$("a[name=request_activation]").html('Request Activation Code &rarr;');
		}, 3000);
		
	});
});

});// $.getScript("./meta/mpay_responsive/MPAY_jquery_effect/is.js", function(){ closing