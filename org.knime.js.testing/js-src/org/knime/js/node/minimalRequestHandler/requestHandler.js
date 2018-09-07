requestHandler = function() {
	
	var handler = {};
	var _representation, _value;
	var textArea;
	var curRequests = [];
	
	if (knimeService.isViewRequestsSupported()) {
		knimeService.loadConditionally(["org/knime/js/node/minimalRequestHandler/requestHandlerLazyLoad"]);
	}
	
	handler.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		var body = document.querySelector("body");
		var header = document.createElement("h2");
		header.style.marginBottom = "5px";
		header.appendChild(document.createTextNode("Minimal Request Test View"));
		body.appendChild(header);
		if (_representation.stallRequests) {
			var subtitle = document.createElement("h4");
			subtitle.style.marginBottom = "5px";
			subtitle.style.marginTop = "0";
			var subText = "Requests stall in node model randomly up to 5 seconds. ";
			if (_representation.cancelPrevious) {
				subText += "Previous issued, not resolved requests will always be cancelled."
			} else {
				if (_representation.keepOrder) {
					subText += "Responses should still arrive in order.";
				} else {
					subText += "Responses should arrive out of order.";
				}
			}
			subtitle.appendChild(document.createTextNode(subText));
			body.appendChild(subtitle);
		}
		textArea = document.createElement("textarea");
		textArea.setAttribute("rows", 40);
		textArea.setAttribute("columns", 100);
		textArea.style.display = "block";
		textArea.style.width = "calc(100% - 40px)";
		textArea.style.margin = "0 20px";
		textArea.setAttribute("readonly", "");
		textArea.setAttribute("placeholder", "Press the button on the top right to see responses displayed here.")
		body.appendChild(textArea);
		
		knimeService.addButton("clear-button", "ban", "Clear All", function() {
			textArea.value = "";
			for (var i = 0; i < curRequests.length; i++) {
				curRequests[i].cancel(false);
			}
			curRequests = [];
		});
		
		knimeService.addNavSpacer();
		
		knimeService.addButton("dummy-request", "paper-plane", "Send Dummy Request", function() {
			var request = {"dummy": "I am sending a request"};
			if (_representation.cancelPrevious) {
				for (var i = 0; i < curRequests.length; i++) {
					curRequests[i].cancel(true);
				}
				curRequests = [];
			}
			
			if (knimeService.isViewRequestsSupported()) {
				requestHandler.initRequest(request, textArea, _representation, curRequests);
			} else {
				alert("The current browser does not support lazy loading!");
			}
		});
	}
	
	pad = function (number, padAmount) {
		return number.toString().padStart(padAmount, "0");
	}
	
	getNowTimestamp = function() {
		var now = new Date();
		var text = "[" + now.getFullYear() + "-" + pad((now.getMonth()+1), 2) + "-" + pad(now.getDate(), 2);
		text += "T" + pad(now.getHours(), 2) + ":" + pad(now.getMinutes(), 2) + ":" + pad(now.getSeconds(), 2);
		text += "." + pad(now.getMilliseconds(), 3) + "]";
		return text;
	}
	
	handler.displayProgress = function(monitor) {
		if (!monitor.progress) {
			return;
		}
		var textToAdd = getNowTimestamp();
		var percent = (monitor.progress * 100).toFixed(2);
		textToAdd += ": PROGRESS for sequence [" + monitor.requestSequence + "] - " + percent + "%\n";
		textArea.value += textToAdd;
		textArea.scrollTop = textArea.scrollHeight;
	}
	
	handler.displayResponse = function(response) {
		var textToAdd = getNowTimestamp();
		textToAdd += ": RESPONSE for sequence [" + response.sequence + "] - " + response.dummy + "\n";
		textArea.value += textToAdd;
		textArea.scrollTop = textArea.scrollHeight;
		for (var i = 0; i < curRequests.length; i++) {
			if (response.sequence === curRequests[i].monitor.requestSequence) {
				curRequests.splice(i, 1);
				break;
			}
		}
	}
	
	handler.displayError = function(sequence, error) {
		var textToAdd = getNowTimestamp();
		textToAdd += ": CATCH for sequence [" + sequence + "] - ";
		if (error) {
			textToAdd += error;
		} else {
			textToAdd += "No further information available.";
		}
		textToAdd += "\n";
		textArea.value += textToAdd;
		textArea.scrollTop = textArea.scrollHeight;
		for (var i = 0; i < curRequests.length; i++) {
			if (sequence === curRequests[i].monitor.requestSequence) {
				curRequests.splice(i, 1);
				break;
			}
		}
	}
	
	handler.validate = function() {
		return true;
	}
	
	handler.setValidationError = function() {}
	
	handler.getComponentValue = function() {
		return _value;
	}
	
	return handler;
	
}();