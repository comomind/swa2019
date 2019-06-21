if (!xui) var xui = {};

xui.ajax = function (url, method) {
    this.url = url;
    this.method = method ? method : xui.ajax.options.method;
    this.async = xui.ajax.options.async;
    this.dataType = xui.ajax.options.dataType;
    this.timeout = xui.ajax.options.timeout;
    this.query = "";
}

xui.ajax.prototype.addQuery = function(name, value) {
    if (this.query) this.query += "&";
    this.query += encodeURIComponent(name) + "=" + encodeURIComponent(value);
}

xui.ajax.prototype.result = function(name, value) {
    if (this.query) this.query += "&";
    this.query += encodeURIComponent(name) + "=" + encodeURIComponent(value);
}

xui.ajax.prototype.send = function(callback) {

	callback = callback ? callback : "fnCallback()";
	$.ajax({
		   type: this.method
		   , dataType: this.dataType
		   , url: this.url
		   , data: this.query
		   , async: this.async
		   , success: function( json ) {
			    data = eval(json);

				if (data.error=='true') {
			    	xui.ajax.alert(data);
				}
		    	return eval('(' + callback + ')');
			}
	       , error: this.onError
		});
}

xui.ajax.prototype.bind = function(comboId) {
	if ( comboId == undefined || comboId == null ) alert(comboId + " 객체가없습니다.");

	$.ajax({
		   type: this.method
		   , dataType: this.dataType
		   , url: this.url
		   , data: this.query
		   , async: this.async
		   , success: function( json ) {
			    data = eval(json);

			    if (data.error=='true') {
			    	xui.ajax.alert(data);
			    } else {
			    	$("#"+comboId).empty().append("<option value=''>" +"선택하세요" + "</option>" );
			    	for(inx = 0; inx < data.resultList.length; inx++) {
			    		$("#"+comboId).append("<option value='"+ data.resultList[inx].code+ "'>" +data.resultList[inx].value + "</option>" );
			    	}
			    }
			}
	       , error: this.onError
		});
}

xui.ajax.prototype.addQuery = function(name, value) {
    if (this.query) this.query += "&";
    this.query += encodeURIComponent(name) + "=" + encodeURIComponent(value);
}

xui.ajax.prototype.addUrl = function(url) {
    this.query = url;
}

xui.ajax.options = {
	method       : "POST",
	async        : true,
	progressbar  : false,
	mute         : false,
	timeout      : 3 * 60 * 1000,
	dataType     : "json"
}

xui.ajax.messages = {
	"ERROR0"      : "서버와 연결하지 못하였습니다.\n\nPlease Check Your Network ",
	"ERROR404"    : "요청하신 주소를 찾지 못하였습니다.\n\nRequst URL not found ",
	"ERROR200"    : "응답유형이 다릅니다.\n\nParsing JSON Request failed ",
	"ERROR500"    : "서버에서 오류가 발생하였습니다. \n\nInternel Server Error ",
	"timeout"     : "통신 중 오류가 발생하였습니다.\n\nRequest Time out ",
	"parsererror" : "통신 중 오류가 발생하였습니다.\n\nParsing JSON Request failed ",
	"error"       : "Unknow Error "
}

xui.ajax.data = new Object();


xui.ajax.prototype.onError = function( xhr, errorType, exception ) {
//      alert("[onError] \n" +
//    		 "xhr := " + xhr + "\n" +
//    		 "errorType := " + errorType + "\n" +
//    		 "exception := " + exception + "\n" +
//    		 "xhr.status := " + xhr.status + "\n" +
//    		 "xhr.readyState := " + xhr.readyState + "\n" +
//     		 "xhr.statusText := " + xhr.statusText + "\n" +
//    		 "xhr.responseText := " + xhr.responseText + "\n" +
//    		 "[onError] \n");

 	  var errorCode = "ERROR" + xhr.status;
	  xui.ajax.alert( errorCode, xhr );
}

xui.ajax.prototype.onSuccess = function( json ) {
    data = eval(json);
    return eval('(' + callback + ')');
}

xui.ajax.getMessage = function (code) {
	var message =  xui.ajax.messages[code];
	return xui.ajax.messages[code] ? xui.ajax.messages[code] : code;
}

xui.ajax.alert = function(data, xhr) {
	if (xui.ajax.options.mute == true) return;

	var code = "";
	var message = "";
    var resultMessage = "";

	if (typeof(xhr) == "undefined") {
		message = data.message;
		code = data.code;
		if (typeof(code) == "undefined") {
			resultMessage = message;
		} else {
			resultMessage = "[" + code + "] " + message;
		}
	} else {
		if (typeof(data) == "string") {
			message = xui.ajax.getMessage(data);
			code = (message == data) ? "errors" : data;
			if (!message) message = data;
			resultMessage = message + "[" + code + "] ";
		} else {
			alert("not message");
		}
	}
	alert(resultMessage);
}


