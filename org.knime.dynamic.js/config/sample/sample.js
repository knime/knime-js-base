(sample_namespace = function() {
	
	var sample = {};
	var _representation, _value;
	
	sample.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		var body = document.getElementsByTagName('body')[0];
		var text = document.createElement("h1");
		body.appendChild(text);
		if (_representation.options["sample_checkbox_option"]) {
			text.appendChild(document.createTextNode("Checkbox was checked"));
		} else {
			text.appendChild(document.createTextNode("Checkbox was not checked"));
		}
		var p = document.createElement("p");
		body.appendChild(p);
		var string = "Checking dependencies: ";
		if (typeof d3 != 'undefined') {
			string += "D3 present. Check!";
			p.className = "success";
		} else {
			string += "D3 not loaded. FAILURE!";
			p.className = "failure";
		}
		p.appendChild(document.createTextNode(string));
		p = document.createElement("p");
		body.appendChild(p);
		if (typeof jQuery != 'undefined') {
			string = "jQuery present. Check!";
			p.className = "success";				
		} else {
			string = "jQuery not loaded. FAILURE!";
			p.className = "failure";
		}
		p.appendChild(document.createTextNode(string));
		p = document.createElement("p");
		body.appendChild(p);
		string = " Checking tables: ";
		if (representation.dataTables.length > 0) {
			string += "Found " + representation.dataTables.length + " tables. Check!";
			p.className = "success";
		} else {
			string += "No tables found. FAILURE!";
			p.className = "failure";
		}
		p.appendChild(document.createTextNode(string));
	}
	
	sample.validate = function() {
		return true;
	}
	
	sample.getComponentValue = function() {
		return _value;
	}
	
	return sample;
	
}());