(newTableTest = function() {
	
	var view = {};
	var _representation, _value;
	
	view.init = function(representation, value) {
	    _representation = representation;
	    _value = value;
	    
	    //TODO create tables that differ from input
	    //for now this test only covers default behavior
	    //(output after configure and execute)
	}
	
	view.validate = function() {
	    return true;
	  }
	
	view.getComponentValue = function() {
		return _value;
	}
	
	return view;
	
}());