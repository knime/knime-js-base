dynamicJSNode = function() {
	
	var node = {}
	var _innerNamespace;
	var VAADIN_PREFIX = "./VAADIN/src-js/";
	
	node.init = function(representation, value) {
		if (representation.jsCode == null) {
			document.body.innerHTML = 'Error: No script available.';
			return;
		}
		_innerNamespace = representation.jsNamespace;
		
		// Import style dependencies
		var head = document.getElementsByTagName('head')[0];
		for (var j = 0; j < representation.cssDependencies.length; j++) {
			var href = representation.cssDependencies[j];
			if (parent != undefined && parent.KnimePageLoader != undefined) {
				href = VAADIN_PREFIX + href;
			}
			var styleDep = document.createElement('link');
			styleDep.type = 'text/css';
			styleDep.rel = 'stylesheet';
			styleDep.href = href;
			head.appendChild(styleDep);
		}
		// Import own style declaration
		for (var j = 0; j < representation.cssCode.length; j++) {
			var styleElement = document.createElement('style');
			styleElement.type = 'text/css';
			styleElement.appendChild(document.createTextNode(representation.cssCode[j]));
			head.appendChild(styleElement);
		}
		
		// Import JS dependencies and call JS code after loading
		var libs = representation.jsDependencies;
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			for (var i = 0; i < libs.length; i++) {
				libs[i] = VAADIN_PREFIX + libs[i];
			}
		}
		//FIXME: Does not export global variables correctly!
		libs.push(representation.urlDependencies);
		
		require(libs, function() {
			try {
				for (var i = 0; i < representation.jsCode.length; i++) {
					if (window.execScript) {
				        window.execScript(representation.jsCode[i]);
				        break;
				    }
					var fn = function() {
				        window.eval.call(window, representation.jsCode[i]);
				    };
				    fn();
				}
				window[_innerNamespace].init(representation, value);
			} catch (e) {
				var errorString = "Error in script\n";
				if (e.stack) {
					errorString += e + "\n" + e.stack;
				} else {
					errorString += e;
				}
			    alert(errorString);
			}
		});
	};
	
	node.validate = function() {
		return window[_innerNamespace].validate();
	}
	
	node.setValidationError = function(err) {
		window[_innerNamespace].setValidationError(err);
	}
	
	node.getComponentValue = function() {
		return window[_innerNamespace].getComponentValue();
	}
	
	return node;
	
}();