<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String http_message = (String) (request.getAttribute("http_message") == null ? "" : request.getAttribute("http_message"));
%>

<!DOCTYPE html>
<html style="-webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; -khtml-user-select: none; user-select: none;" oncontextmenu="return false">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon" href="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" type="image/x-icon">
<title>ManagePay | VERNPOS</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/demo.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/agent_login.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/mygroup.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<script src='https://www.google.com/recaptcha/api.js'></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/font-awesome.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/jquery/jeffect.css">
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>

		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jqKeyboard/jqkeyboard.css">
        <script type="text/javascript" src="${pageContext.request.contextPath}/jqKeyboard/jqk.layout.en.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/jqKeyboard/jqkeyboard-min.js"></script>

<style>
input[type="text"]:focus, input[type="password"]:focus, input[type="datetime"]:focus,
	input[type="datetime-local"]:focus, input[type="date"]:focus, input[type="month"]:focus,
	input[type="time"]:focus, input[type="week"]:focus, input[type="number"]:focus,
	input[type="email"]:focus, input[type="url"]:focus, input[type="search"]:focus,
	input[type="tel"]:focus, input[type="color"]:focus, input[type="phone"]:focus,
	select[name="gender"]:focus, select[name="country"]:focus, select[name="state"]:focus,
	.uneditable-input:focus {
	border-color: #00FA9A;
	/* box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset, 0 0 8px #00FA9A; */
	outline: 0 none;
}

@font-face {
	font-family: 'robotofontregular';
	src: url('bodydiagnosis/font/Roboto-Regular.ttf');
}

.font-roboto-regular {
	font-family: robotofontregular;
}

.form-signin input[type="text"] {
	margin-bottom: 5px;
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
}

.form-signin input[type="password"] {
	margin-bottom: 10px;
	border-top-left-radius: 0;
	border-top-right-radius: 0;
}

.form-signin .form-control {
	position: relative;
	font-size: 16px;
	font-family: 'Open Sans', Arial, Helvetica, sans-serif;
	height: auto;
	padding: 10px;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
}

#jq-keyboard {
	position: absolute;
	background: #FFF;
	width: auto;
	max-height: 0;
	bottom: 20px;
	right: 20px;
	box-shadow: 0 2px 7px 0 rgba(0, 0, 0, .5);
	border-radius: 3px;
	border-bottom: 0 solid #CCC;
	padding-left: 10px;
	padding-right: 10px;
	overflow: hidden;
	transition: max-height .5s ease, padding .3s linear
}

#jq-keyboard.show {
	padding-top: 10px;
	padding-bottom: 5px;
	border-bottom-width: 3px;
	max-height: 500px
}

#jqk-lang-cont {
	margin-bottom: 10px;
	overflow: hidden
}

#jq-keyboard .btn-row {
	text-align: center;
	margin-bottom: 5px;
	height: 35px
}

#jqk-lang-cont button.jqk-lang-btn {
	background: #EEE;
	font-size: 10px;
	border: none;
	margin-left: 0;
	color: #666;
	cursor: pointer
}

#jqk-lang-cont button.jqk-lang-btn:hover {
	color: #222
}

#jqk-lang-cont button.jqk-lang-btn.selected {
	background: #1c94c4;
	color: #FFF
}

#jqk-lang-cont>button.jqk-lang-btn:first-of-type {
	border-top-left-radius: 3px;
	border-bottom-left-radius: 3px
}

#jqk-lang-cont>button.jqk-lang-btn:last-child {
	border-top-right-radius: 3px;
	border-bottom-right-radius: 3px
}

#jqk-lang-cont>button.jqk-lang-btn:only-child {
	border-radius: 3px
}

#jqk-lang-cont>.minimize-btn {
	width: 18px;
	height: 18px;
	float: right;
	transition: .3s ease;
	cursor: pointer;
	background:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIxOHB4IiBoZWlnaHQ9IjE4cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDczIDczIiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uZmlsMCB7ZmlsbDojQ0NDQ0NDfTwvc3R5bGU+PC9kZWZzPjxnPjxtZXRhZGF0YS8+PHBhdGggY2xhc3M9ImZpbDAiIGQ9Ik0yOSA0OGwtMjgtMjljLTEgMC0xLTIgMC0zbDE1LTE1YzEtMSAzLTEgMyAwbDI5IDI4IDktOWMxLTEgMi0xIDMgMCAwIDAgMSAxIDEgMWwwIDM3YzAgMi0xIDMtMyAzbC0zNyAwYy0xIDAtMi0xLTItMyAwIDAgMC0xIDEtMWwwIDAgOS05em0tMjItN2MwLTIgMi0zIDQtMyAyIDAgNCAxIDQgM2wwIDI0IDUwIDAgMC01MC0yNCAwYy0yIDAtNC0yLTQtNCAwLTIgMi00IDQtNGwyOCAwYzIgMCA0IDIgNCA0bDAgNThjMCAyLTIgNC00IDRsLTU4IDBjLTIgMC00LTItNC00bDAtMjh6Ii8+PC9nPjwvc3ZnPg==)
		no-repeat
}

#jqk-lang-cont>.minimize-btn:hover {
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIxOHB4IiBoZWlnaHQ9IjE4cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDczIDczIiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uZmlsMCB7ZmlsbDojMUM5NEM0fTwvc3R5bGU+PC9kZWZzPjxnPjxtZXRhZGF0YS8+PHBhdGggY2xhc3M9ImZpbDAiIGQ9Ik0yOSA0OGwtMjgtMjljLTEgMC0xLTIgMC0zbDE1LTE1YzEtMSAzLTEgMyAwbDI5IDI4IDktOWMxLTEgMi0xIDMgMCAwIDAgMSAxIDEgMWwwIDM3YzAgMi0xIDMtMyAzbC0zNyAwYy0xIDAtMi0xLTItMyAwIDAgMC0xIDEtMWwwIDAgOS05em0tMjItN2MwLTIgMi0zIDQtMyAyIDAgNCAxIDQgM2wwIDI0IDUwIDAgMC01MC0yNCAwYy0yIDAtNC0yLTQtNCAwLTIgMi00IDQtNGwyOCAwYzIgMCA0IDIgNCA0bDAgNThjMCAyLTIgNC00IDRsLTU4IDBjLTIgMC00LTItNC00bDAtMjh6Ii8+PC9nPjwvc3ZnPg==)
}

#jq-keyboard button.jqk-btn {
	position: relative;
	width: 37px;
	height: 35px;
	background: #EEE;
	border-radius: 4px;
	border: none;
	border-bottom: 3px solid #CCC;
	margin: 0 2px;
	padding: 0;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 15px;
	font-weight: 700;
	color: #666;
	overflow: hidden;
	cursor: pointer
}

#jq-keyboard button.jqk-btn:hover {
	box-shadow: 0 0 2px 2px #1c94c4;
	transition: box-shadow .3s ease
}

#jq-keyboard button.jqk-btn.clicked, #jq-keyboard button.jqk-btn.selected
	{
	position: relative;
	height: 33px;
	top: 2px;
	border-bottom: 1px solid #CCC
}

#jq-keyboard button.jqk-btn.selected::after {
	content: "";
	position: absolute;
	background: #4eb305;
	width: 7px;
	height: 7px;
	top: 5px;
	left: 5px;
	border-radius: 7px
}

#jq-keyboard button.jqk-btn::-moz-focus-inner {
	border: 0
}

#jq-keyboard button.jqk-btn:focus {
	outline: 0
}

#jq-keyboard button.jqk-btn.special {
	background-repeat: no-repeat
}

#jq-keyboard button.jqk-btn.special.space {
	width: 230px
}

#jq-keyboard button.jqk-btn.special.backspace {
	width: 60px;
	background-position: 16px 8px;
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIyNXB4IiBoZWlnaHQ9IjE2cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDEwNCA2NSIgc3R5bGU9ImNsaXAtcnVsZTpldmVub2RkO2ZpbGwtcnVsZTpldmVub2RkO2ltYWdlLXJlbmRlcmluZzpvcHRpbWl6ZVF1YWxpdHk7c2hhcGUtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbjt0ZXh0LXJlbmRlcmluZzpnZW9tZXRyaWNQcmVjaXNpb24iPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+LmZpbDAge2ZpbGw6I0IzQjNCM308L3N0eWxlPjwvZGVmcz48Zz48bWV0YWRhdGEvPjxwYXRoIGNsYXNzPSJmaWwwIiBkPSJNMzYgMGw2MSAwYzQgMCA3IDMgNyA3bDAgNTFjMCA0LTMgNy03IDdsLTYxIDBjLTIgMC0zLTEtNS0ybC0yOS0yNWMtMi0zLTMtNyAwLTEwIDAgMCAwLTEgMC0xbDMwLTI1YzEtMSAzLTIgNC0ybDAgMHptNDggMTJjMS0yIDQtMiA2IDAgMiAyIDIgNSAwIDdsLTEzIDEzIDEzIDEzYzIgMiAyIDUgMCA2LTIgMi01IDItNiAwbC0xNC0xMy0xMyAxM2MtMiAyLTQgMi02IDAtMi0xLTItNCAwLTZsMTMtMTMtMTMtMTNjLTItMi0yLTUgMC03IDItMiA0LTIgNiAwbDEzIDEzIDE0LTEzeiIvPjwvZz48L3N2Zz4=)
}

#jq-keyboard button.jqk-btn.special.enter {
	width: 70px;
	background-position: 23px 8px;
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIyMXB4IiBoZWlnaHQ9IjE2cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDg0IDYzIiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uZmlsMCB7ZmlsbDojQjNCM0IzO2ZpbGwtcnVsZTpub256ZXJvfTwvc3R5bGU+PC9kZWZzPjxnPjxtZXRhZGF0YS8+PHBhdGggY2xhc3M9ImZpbDAiIGQ9Ik03MyA1YzAtMyAzLTUgNi01IDMgMCA1IDIgNSA1bDAgMzBjMCAzLTIgNi01IDZsLTYwIDAgMTMgMTNjMiAyIDIgNSAwIDctMyAyLTYgMi04IDBsLTIyLTIyYy0xLTEtMi0yLTItNCAwLTEgMC0yIDEtM2wwIDAgMCAwIDAgMGMwIDAgMCAwIDEtMWwyMi0yMmMyLTIgNS0yIDcgMCAzIDIgMyA2IDAgOGwtMTIgMTMgNTQgMCAwLTI1eiIvPjwvZz48L3N2Zz4=)
}

#jq-keyboard button.jqk-btn.special.tab {
	width: 50px;
	background-position: 16px 7px;
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIxOHB4IiBoZWlnaHQ9IjE5cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDc4IDgyIiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uZmlsMCB7ZmlsbDojQjNCM0IzO2ZpbGwtcnVsZTpub256ZXJvfTwvc3R5bGU+PC9kZWZzPjxnPjxtZXRhZGF0YS8+PHBhdGggY2xhc3M9ImZpbDAiIGQ9Ik03MCA0NGMwLTIgMi00IDQtNCAyIDAgNCAyIDQgNGwwIDM0YzAgMi0yIDQtNCA0LTIgMC00LTItNC00bDAtMTYgMCAwIDAgMCAwIDAgMCAwIDAgMSAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwLTEgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMSAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAtMTcgMTZjLTEgMi00IDItNSAwLTItMS0yLTQgMC01bDktMTAtNTIgMGMtMiAwLTQtMi00LTQgMC0yIDItNCA0LTRsNTIgMC05LTEwYy0yLTItMi00IDAtNiAxLTEgNC0xIDUgMGwxNyAxNyAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAxIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAxIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwLTE1em0tMzktOWMyIDEgMiA0IDAgNS0xIDItNCAyLTUgMGwtMTctMTYgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAtMSAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAtMSAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwLTEgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAxNWMwIDMtMiA1LTQgNS0yIDAtNC0yLTQtNWwwLTMzYzAtMiAyLTQgNC00IDIgMCA0IDIgNCA0bDAgMTUgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDEgMCAwLTEgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMTctMTdjMS0xIDQtMSA1IDAgMiAyIDIgNCAwIDZsLTkgMTAgNTIgMGMyIDAgNCAyIDQgNCAwIDItMiA0LTQgNGwtNTIgMCA5IDEweiIvPjwvZz48L3N2Zz4=)
}

#jq-keyboard button.jqk-btn.special.shift {
	width: 70px;
	background-position: 25px 6px;
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIyMHB4IiBoZWlnaHQ9IjE5cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDc4IDc1IiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uZmlsMCB7ZmlsbDojQjNCM0IzfTwvc3R5bGU+PC9kZWZzPjxnPjxtZXRhZGF0YS8+PHBhdGggY2xhc3M9ImZpbDAiIGQ9Ik02MCA0N2wwIDI1YzAgMi0yIDMtNCAzbC0zNCAwYy0yIDAtNC0xLTQtM2wwLTI1LTE0IDBjLTIgMC00LTEtNC0zIDAtMSAwLTIgMS0zbDM1LTQwYzItMSA0LTEgNSAwIDEgMCAxIDAgMSAwbDM1IDQwYzEgMiAxIDQgMCA1LTEgMS0yIDEtMyAxbDAgMC0xNCAweiIvPjxwYXRoIGNsYXNzPSJmaWwwIiBkPSJNLTI0MDUtNTYwbDYxIDBjNCAwIDcgMyA3IDdsMCA1MWMwIDQtMyA3LTcgN2wtNjEgMGMtMSAwLTMtMS00LTJsLTMwLTI1Yy0yLTMtMy03IDAtMTAgMCAwIDAgMCAxLTFsMjktMjVjMS0xIDMtMiA0LTJsMCAwem00OCAxMmMxLTIgNC0yIDYgMCAyIDIgMiA1IDAgN2wtMTMgMTMgMTMgMTNjMiAyIDIgNSAwIDYtMiAyLTUgMi02IDBsLTE0LTEzLTEzIDEzYy0yIDItNCAyLTYgMC0yLTEtMi00IDAtNmwxMy0xMy0xMy0xM2MtMi0yLTItNSAwLTcgMi0yIDQtMiA2IDBsMTMgMTMgMTQtMTN6Ii8+PC9nPjwvc3ZnPg==)
}

#jq-keyboard button.jqk-btn.special.capslock {
	width: 60px;
	background-position: 20px 6px;
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIyMXB4IiBoZWlnaHQ9IjIwcHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDg3IDg0IiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uZmlsMCB7ZmlsbDojQjNCM0IzO2ZpbGwtcnVsZTpub256ZXJvfTwvc3R5bGU+PC9kZWZzPjxnPjxtZXRhZGF0YS8+PHBhdGggY2xhc3M9ImZpbDAiIGQ9Ik03MCA1NmwwIDEwIDEwIDBjMiAwIDQgMiA0IDRsMCAxMGMwIDItMiA0LTQgNC0yNCAwLTQ5IDAtNzMgMC0yIDAtNC0yLTQtNGwwLTEwYzAtMiAyLTQgNC00bDEwIDAgMC0xMC0xMyAwYy0zIDAtNS00LTMtN2wzOS00OGMyLTIgNS0xIDcgMWwzOSA0N2MyIDIgMSA0LTEgNiAwIDEtMSAxLTIgMWwwIDAtMTMgMHptLTE2IDEwYzAgMiAyIDMgNCAzIDEtMSAyLTMgMi00bC0xMy00MGMtMS0yLTItMi0zLTJsLTEgMGMtMSAwLTIgMC0zIDJsLTEzIDQwYzAgMSAwIDMgMiA0IDIgMCA0LTEgNC0zbDQtMTAgMTMgMCA0IDEwem0tNS0xNmwtMTEgMGMyLTYgNC0xMSA1LTE3bDAgMGMyIDYgNCAxMiA2IDE3eiIvPjwvZz48L3N2Zz4=)
}

#jqk-toggle-btn {
	position: fixed;
	width: 34px;
	height: 18px;
	bottom: 20px;
	right: 20px;
	cursor: pointer;
	opacity: .3;
	transition: .3s ease;
	box-shadow: 1px 1px 10px white;
	background-color: white;
	content: "Keyboard Here"
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIzNHB4IiBoZWlnaHQ9IjE4cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDc1IDQwIiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uanFra2ItaW5wYnRuLWZpbGwge2ZpbGw6IzAwMDAwMH08L3N0eWxlPjwvZGVmcz48Zz48bWV0YWRhdGEvPjxwYXRoIGNsYXNzPSJqcWtrYi1pbnBidG4tZmlsbCIgZD0iTTMgMGw2OSAwYzIgMCAzIDEgMyAzbDAgMzRjMCAyLTEgMy0zIDNsLTY5IDBjLTIgMC0zLTEtMy0zbDAtMzRjMC0yIDEtMyAzLTN6bTE2IDI3bDM3IDAgMCA2LTM3IDAgMC02em0tNy0yMWwwIDYtNSAwIDAtNiA1IDB6bTU2IDBsMCA2LTYgMCAwLTYgNiAwem0tMTEgMGwwIDYtNiAwIDAtNiA2IDB6bS0xMSAwbDAgNi02IDAgMC02IDYgMHptLTEyIDBsMCA2LTUgMCAwLTYgNSAwem0tMTEgMGwwIDYtNSAwIDAtNiA1IDB6bS02IDExbDAgNS02IDAgMC01IDYgMHptNDQgMGwwIDUtNSAwIDAtNSA1IDB6bS0xMSAwbDAgNS02IDAgMC01IDYgMHptLTExIDBsMCA1LTYgMCAwLTUgNiAwem0tMTEgMGwwIDUtNiAwIDAtNSA2IDB6Ii8+PC9nPjwvc3ZnPg==)
}

#jqk-toggle-btn:hover {
	right: 27px;
}

#jqk-toggle-btn.dark {
	color: white;
	background-color: white;
	background-image:
		url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbDpzcGFjZT0icHJlc2VydmUiIHdpZHRoPSIzNHB4IiBoZWlnaHQ9IjE4cHgiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDc1IDQwIiBzdHlsZT0iY2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbC1ydWxlOmV2ZW5vZGQ7aW1hZ2UtcmVuZGVyaW5nOm9wdGltaXplUXVhbGl0eTtzaGFwZS1yZW5kZXJpbmc6Z2VvbWV0cmljUHJlY2lzaW9uO3RleHQtcmVuZGVyaW5nOmdlb21ldHJpY1ByZWNpc2lvbiI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIj4uanFra2ItaW5wYnRuLWZpbGwge2ZpbGw6I0ZGRkZGRn08L3N0eWxlPjwvZGVmcz48Zz48bWV0YWRhdGEvPjxwYXRoIGNsYXNzPSJqcWtrYi1pbnBidG4tZmlsbCIgZD0iTTMgMGw2OSAwYzIgMCAzIDEgMyAzbDAgMzRjMCAyLTEgMy0zIDNsLTY5IDBjLTIgMC0zLTEtMy0zbDAtMzRjMC0yIDEtMyAzLTN6bTE2IDI3bDM3IDAgMCA2LTM3IDAgMC02em0tNy0yMWwwIDYtNSAwIDAtNiA1IDB6bTU2IDBsMCA2LTYgMCAwLTYgNiAwem0tMTEgMGwwIDYtNiAwIDAtNiA2IDB6bS0xMSAwbDAgNi02IDAgMC02IDYgMHptLTEyIDBsMCA2LTUgMCAwLTYgNSAwem0tMTEgMGwwIDYtNSAwIDAtNiA1IDB6bS02IDExbDAgNS02IDAgMC01IDYgMHptNDQgMGwwIDUtNSAwIDAtNSA1IDB6bS0xMSAwbDAgNS02IDAgMC01IDYgMHptLTExIDBsMCA1LTYgMCAwLTUgNiAwem0tMTEgMGwwIDUtNiAwIDAtNSA2IDB6Ii8+PC9nPjwvc3ZnPg==)
}

.jqk-hide {
	display: none
}
</style>
</head>

<body
	style="background: url(${pageContext.request.contextPath}/img/cover/Cover.jpg) no-repeat; background-size: cover; min-height: 100vh; opacity: 0.9;">
	<div id="loadingPanel" style="min-height: 100vh; display: flex; align-items: center; position: absolute;">
		<div style="min-width: 100vw; display: flex; flex-direction: column; align-items: center;">
			<div class="panel"
				style="background-color: rgba(0, 0, 0, 0.9); text-align: center;">
				<span id="progressText" style="color: white; margin-top: 10px;">Initializing...</span>
				<div class="progress" style="width: 90vh; margin: 10px;">
				  	<div id="progressBar" class="progress-bar progress-bar-info progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
				    	<span class="sr-only">0%</span>
				  	</div>
				</div>
				<p id="progressPercentage" style="color: white; margin-bottom: 10px;">0%</p>
				<button id="retryBtn" type="button" class="btn btn-primary" onclick="beginLoading()">Retry</button>
				<button id="skipBtn" type="button" class="btn btn-primary" onclick="skipLoading()">Skip</button>
			</div>
		</div>
	</div>
	<div id="loginPanel" style="min-height: 100vh; display: flex; align-items: center; position: absolute;">
		<div style="min-width: 100vw; display: flex; flex-direction: column; align-items: center;">
			<div class="panel" style="background-color: rgba(0, 0, 0, 0.9);">
				<div class="panel-heading text-center">
					<img
						src="${pageContext.request.contextPath}/meta/img/ecpos_logo.png"
						style="height: 200px; padding-top: 15px;">
				</div>

				<div class="panel-body text-center">
					<%if (!http_message.equals("")) { %>
					<span style="color: red;">${http_message}</span>
					<%} %>
					<form id="normalForm" action="${pageContext.request.contextPath}/authentication" method="post" accept-charset="UTF-8" role="form" class="form-signin" autocomplete="off">
						<fieldset>
							<label class="login-label" style="color: white;">User Name</label>
							<input class="form-control jQKeyboard" name="username" placeholder="User Name" type="text" required> 
							<br>
							<label class="login-label"  style="color: white;">Password</label>
							<input class="form-control" name="password" placeholder="Password" type="password" required>
							<br> 
							<input class="btn btn-lg btn-block" style="background-color: #00FA9A; color: white;" type="submit" value="Login">
						</fieldset>
					</form>
					<form id="qrForm" action="${pageContext.request.contextPath}/authenticationQR" method="post" accept-charset="UTF-8" role="form" class="form-signin" autocomplete="off">
						<fieldset>
							<input type="hidden" id="qrContent" name="qrContent" value="">
							<input id="showQRLoginBtn" class="btn btn-lg btn-block" style="background-color: #00FA9A; color: white;" type="button" value="Login">
						</fieldset>
					</form>
					<br>
					<button id="switchBtn" type="button" class="btn btn-primary btn-xs">Switch to Form Login</button>
				</div>
				<div id="keyboard"></div>
			</div>
		</div>
	</div>

	<!-- Scan QR Modal [START] -->
	<div class="modal fade" id="scan_qr_modal" tabindex="-1" role="dialog"
		aria-labelledby="scan_qr_modal" aria-hidden="true"
		data-keyboard="false" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">Login</h4>
				</div>
				<div class="modal-body text-center">
					<form autocomplete="off">
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<h3>Scan QR to login</h3>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<!-- Scan QR Modal [END] -->

	<!-- Loading Modal [START] -->
	<div class="modal fade" data-backdrop="static" id="loading_modal"
		role="dialog">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-body">
					<div class="text-center">
						<img style="width: 75%"
							src="${pageContext.request.contextPath}/img/gif/loading.gif"><br>
						<span>Logging In...</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Loading Modal [END] -->
	<div id="balloon" style="color: white;"></div>
	<script type="text/javascript">
		$(function() {
			"use strict";

			var $balloon = $("#balloon"), $infoTxt = $("#info-txt");

			setTimeout(function() {
				$balloon.addClass("shrink");
			}, 500);

			$infoTxt.delay(1000).fadeIn();

			$(this).click(function() {
				$("#button-hint").fadeOut();
				$balloon.fadeOut();
				$infoTxt.fadeOut();
			});

			jqKeyboard.init();
		});
	</script>
</body>

<script>
<%if (!http_message.equals("")) { %>
var isLoad = false;
<%} else {%>
var isLoad = true;
<%} %>

var isSyncMenu = false;
var isSyncStore = false;

var formTypeID = ${loginType};
var isFormSwitchable = ${isLoginSwitch};

var isQRLoginExecuted = true;
var loginQRContent = "";

$("div#loadingPanel").hide();
$("div#loginPanel").hide();
$("button#retryBtn").hide();
$("button#skipBtn").hide();

function updateLoginUI() {
	if (formTypeID == 1) {
		$("#switchBtn").html("Switch to QR Login");
		$("form#normalForm").show();
		$("form#qrForm").hide();
	} else if (formTypeID == 2) {
		$("#switchBtn").html("Switch to Form Login");
		$("form#normalForm").hide();
		$("form#qrForm").show();
	}

	if (!isFormSwitchable) {
		$("#switchBtn").hide();
	}
}

$("#switchBtn").click(function() {
	formTypeID = (formTypeID + 1) % 3;
	if (formTypeID == 0) {
		formTypeID += 1;
	}
	
	updateLoginUI();
});

$("#showQRLoginBtn").click(function() {
	$("#scan_qr_modal").modal("show");
	$("#scan_qr_modal").modal({
	    backdrop: 'static'
	});
	loginQRContent = "";
	isQRLoginExecuted = false;
	
	$(document).off("keydown");
	$(document).keydown(function(e){
		if (!isQRLoginExecuted) {
			if (e.which == 16) {
				return;
			} else if (e.which == 13){
				$("#loading_modal").modal("show");
				
				isQRLoginExecuted = true;
				
				$("input#qrContent").val(loginQRContent);
				$("form#qrForm").submit();
				
				/* $(document).off("keydown"); */
				$("#scan_qr_modal").modal("hide");
			} else if (e.which == 191) {
				loginQRContent += "/";
			} else if (e.which == 186) {
				loginQRContent += ";";
			} else if (e.which == 107) {
				loginQRContent += "+";
			} else if (e.which == 109) {
				loginQRContent += "-";
			} else if (e.which == 189) {
				loginQRContent += "_";
			} else {
				if (e.shiftKey) {
					loginQRContent += String.fromCharCode(e.keyCode || e.which).toUpperCase();
				} else {
					loginQRContent += String.fromCharCode(e.keyCode || e.which).toLowerCase();
				}
			}
		}
	});
});

function syncMenu() {
	updateProgressbar("Checking Menu Update...", 10);
	$.ajax({
	    type: "POST",
	    url: "${pageContext.request.contextPath}/syncMenu",
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    timeout: 30 * 1000,
	    success: function(response) {
	    	if (response != null && response.resultCode != null) {
				if (response.resultCode == "00") {
					isSyncMenu = true;
					setTimeout(function(){
						syncStore();
					}, 500);
				} else if (response.resultCode=='E02' || response.resultCode=='E03') {
					location.reload();
				} else {
					loadFailed(response.resultMessage);
				}
			} else {
				loadFailed("Invalid Server Response.");
			}
	    },
	    failure: function(errMsg) {
	    	loadFailed("System Error. Please Try Again.");
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	loadFailed("System Error. Please Try Again.");
	    }
	});
}

function syncStore() {
	updateProgressbar("Checking Store Update...", 50);
	$.ajax({
	    type: "POST",
	    url: "${pageContext.request.contextPath}/syncStore",
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    timeout: 30 * 1000,
	    success: function(response) {
	    	if (response != null && response.resultCode != null) {
				if (response.resultCode == "00") {
					formTypeID = response.loginType;
					isFormSwitchable = response.loginSwitch;
					updateLoginUI();
					
					isSyncStore = true;
					loadSuccess();
				} else {
					loadFailed(response.resultMessage);
				}
			} else {
				loadFailed("Invalid Server Response.");
			}
	    },
	    failure: function(errMsg) {
	    	loadFailed("System Error. Please Try Again.");
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	loadFailed("System Error. Please Try Again.");
	    }
	});
}

function updateProgressbar(message, percentage) {
	$("span#progressText").html(message);
	$("div#progressBar").css("width", percentage + "%");
	$("p#progressPercentage").html(percentage + "%");
}

function updateProgressbarMessage(message) {
	$("span#progressText").html(message);
}

function loadFailed(message) {
	$("div#progressBar").removeClass("progress-bar-info active");
	$("div#progressBar").addClass("progress-bar-danger");
	updateProgressbarMessage(message);
	$("button#retryBtn").show();
	$("button#skipBtn").show();
}

function loadSuccess() {
	$("div#progressBar").removeClass("progress-bar-info progress-bar-danger");
	$("div#progressBar").addClass("progress-bar-success active");
	updateProgressbar("Loading Completed.", 100);
	setTimeout(function(){
		$("div#loginPanel").fadeIn();
		$("div#loadingPanel").fadeOut();
	}, 1500);
}

function skipLoading() {
	$("div#progressBar").removeClass("progress-bar-info progress-bar-danger");
	$("div#progressBar").addClass("progress-bar-success active");
	$("button#retryBtn").hide();
	$("button#skipBtn").hide();
	updateProgressbar("Loading Skipped.", 100);
	setTimeout(function(){
		$("div#loginPanel").fadeIn();
		$("div#loadingPanel").fadeOut();
	}, 1500);
}

function beginLoading() {
	$("div#progressBar").addClass("progress-bar-info active");
	$("div#progressBar").removeClass("progress-bar-danger");
	$("button#retryBtn").hide();
	$("button#skipBtn").hide();
	updateProgressbar("Loading...", 0);
	setTimeout(function(){
		if (!isSyncMenu) {
			syncMenu();
		} else if (!isSyncStore) {
			syncStore();
		} else {
			loadSuccess();
		}
	}, 500);
}

$(document).ready(function() {
	updateLoginUI();
	console.log(isLoad);
	if (isLoad) {
		$("div#loadingPanel").show();
		beginLoading();
	} else {
		$("div#loginPanel").show();
		$("div#loadingPanel").hide();
	}
});
</script>
</html>