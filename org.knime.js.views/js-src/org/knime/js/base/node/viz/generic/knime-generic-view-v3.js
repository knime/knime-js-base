knime_generic_view = function() {
	
	view = {};
	var _representation;
	var _value;
	var SETTINGS;
	var FLOW_VARIABLES;
	
	view.init = function(representation, value) {
		_representation = representation;
		_value = value;
		if (representation.jsCode == null) {
			document.body.innerHTML = 'Error: No script available.';
		} else {
			// Define KNIME table and set data
			if (representation.table) {
				var knimeDataTable = new kt();
				knimeDataTable.setDataTable(representation.table);
			}
			// Define settings object
			SETTINGS = _value.settings ? JSON.parse(_value.settings) : {};
			// Define variables object
			FLOW_VARIABLES = _value.flowVariables || {}; 
						
			// Import style dependencies
			var head = document.getElementsByTagName('head')[0];
			if (representation.cssDependencies) {
				for ( var j = 0; j < representation.cssDependencies.length; j++) {
					var styleDep = document.createElement('link');
					styleDep.type = 'text/css';
					styleDep.rel = 'stylesheet';
					styleDep.href = representation.cssDependencies[j];
					head.appendChild(styleDep);
				}
			}
			// Import own style declaration
			var styleElement = document.createElement('style');
			styleElement.type = 'text/css';
			styleElement.appendChild(document.createTextNode(representation.cssCode));
			head.appendChild(styleElement);
			
			// Import JS dependencies and call JS code after loading
			var libs = representation.jsDependencies;
			if (!libs) {
				libs = [];
			}
			if (parent != undefined && parent.KnimePageLoader != undefined) {
				for (var i = 0; i < libs.length; i++) {
					libs[i] = "./VAADIN/src-js/" + libs[i];
				}
			}
			
			require(libs, function() {
				try {
				    eval(representation.jsCode);
				    view.validate();
				} catch (e) {
					var errorString = "Error in script\n";
					if (e.stack) {
						errorString += e.stack;
					} else {
						errorString += e;
					}
				    alert(errorString);
				}
			});
		}
	};
	
	view.validate = function() {
		var error;
		if (FLOW_VARIABLES) {
			for (key in FLOW_VARIABLES) {
				var variable = FLOW_VARIABLES[key];
				if (!variable || !variable.type) {
					error = "Variable " + key + " was not correctly defined!";
					break;
				}
				if (["INTEGER", "DOUBLE", "STRING"].indexOf(variable.type) < 0) {
					error = "Variable " + key + " needs to be of a valid type, but was " + variable.type;
					break;
				}
			}
		}
		if (error) {
			view.setValidationError(error);
			return false;
		}
		return true;
	};
	
	view.setValidationError = function(error) {
		alert(error);
	}
	
	view.getComponentValue = function() {
		_value.settings = JSON.stringify(SETTINGS);
		_value.flowVariables = FLOW_VARIABLES;
		return _value;
	};
	
	view.getSVG = function() {
		try {
			return eval('(function() {' + _representation.jsSVGCode + '}())');
		} catch (e) {
			return null;
		}
	}
	
	return view;
}();