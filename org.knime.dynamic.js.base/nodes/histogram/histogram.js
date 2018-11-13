(histogram_namespace = function() {

	var histogram = {};
	var _representation, _value;

	histogram.init = function(representation, value) {
		_value = value;
		_representation = representation;
		var binningResult = _representation.inObjects[0];
		var binColName = binningResult.binnedColumn;
		_representation.inObjects[0] = binningResult.groups;
		_representation.options['cat'] = binColName;
		_representation.isHistogram = true;
		grouped_bar_chart_namespace.init(_representation, _value);
	}

	histogram.validate = function() {
		return grouped_bar_chart_namespace.validate();
	};

	histogram.getComponentValue = function() {
		return grouped_bar_chart_namespace.getComponentValue();
	};

	histogram.getSVG = function() {
		return grouped_bar_chart_namespace.getSVG();
	}

	return histogram;

}());