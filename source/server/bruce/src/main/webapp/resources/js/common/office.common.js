
String.prototype.rtrim = function() {
	var i = this.length;
	while(i--) {
		if(this.charCodeAt(i)!=32) {
			return this.substring(0, i+1);
		}
	}
	return "";
}

String.prototype.ltrim = function() {
	var i = -1, l = this.length;
	while(i++<l) {
		if(this.charCodeAt(i)!=32) {
			return this.substring(i);
		}
	}
	return "";
}

String.prototype.trim = function() {
	return this.rtrim().ltrim();
}

String.prototype.cut = function(start, length) {
	return this.substring(0, start) + this.substr(start + length);
}

String.prototype.replaceAll = function(matchStr, newStr) {
	var result = this;
	if(matchStr=="") return result;
	while(result.indexOf(matchStr)!=-1) {
		result = result.replace(matchStr, newStr);
	}
	return result;
}


function sleep(milliseconds) {
	var start = new Date().getTime();
	for (var i = 0; i < 1e7; i++) {
		if ((new Date().getTime() - start) > milliseconds){
			break;
		}
	}
}
	     
function fnValidateRequired(object, message) {
	var checkResult = true;

    if (object.value.trim()=="") {
        var checkResult = false;
    }

	if(!checkResult) {
		alert(message);
		if(object.type.indexOf("select")==-1) object.select();
		object.focus();
		return false;
	}

	return true;
}

function fnValidateNotRequired(object, message) {
	var checkResult = false;

    if (object.value.trim()=="") {
        var checkResult = true;
    }

	if(!checkResult) {
		alert(message);
		if(object.type.indexOf("select")==-1) object.select();
		object.focus();
		return false;
	}

	return true;
}

function fnValidateNumber(object, message) {
	var input = object.value;
	
	for(var i=0; i< input.length;i++) {
		if(input.charAt(i)==" " || isNaN(nput.charAt(i))) {
			alert(message);
			if(object.type.indexOf("select")==-1) object.select();
			object.focus();			
			return false;
		}
	}
	
	return true;
}
