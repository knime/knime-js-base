knime_word_cloud = function() {

	var wordCloud = {};
	var _representation
	var _value;
	var _sizeMin = Number.POSITIVE_INFINITY;
	var _sizeMax = Number.NEGATIVE_INFINITY;

	wordCloud.init = function(representation, value, layout_cloud) {
		_representation = representation;
		_value = value;
		_representation.data.sort(function(x, y) {
			return d3.descending(x.size, y.size);
		});
		for (var i = 0; i < _representation.data.length; i++) {
			var curSize = _representation.data[i].size;
			_sizeMin = Math.min(_sizeMin, curSize);
			_sizeMax = Math.max(_sizeMax, curSize);
		}
		var scale;
		switch (_value.fontScaleType) {
			case "logarithmic":
				scale = d3.scaleLog();
				break;
			case "square root":
				scale = d3.scaleSqrt();
				break;
			case "exponential":
				scale = d3.scalePow().exponent(2);
				break;
			default:
				scale = d3.scaleLinear();
		}
		scale.domain([_sizeMin, _sizeMax]);
		scale.range([_value.minFontSize, _value.maxFontSize]);
						
		d3.layout.cloud()
			.size([800, 600])
			.words(_representation.data)
			/*.padding(5)*/
			.rotate(function() {
				if (_value.numOrientations < 2) {
					return _value.startAngle;
				} else {
					var range = (_value.endAngle - _value.startAngle) / (_value.numOrientations - 1);
					return (~~(Math.random() * _value.numOrientations) * range) + _value.startAngle;
				}
			})
			.font(_representation.font)
			.fontSize(function(d) {
				return scale(d.size);
			})
			.timeInterval(10)
			.spiral(_value.spiralType)
			.on("end", draw).start();
	}
	
	function draw(words, scale) {
		d3.select("body").append("svg")
			.attr("width", 800)
			.attr("height", 600)
			.append("g")
			.attr("transform", "translate(400,300)")
			.selectAll("text").data(words).enter()
			.append("text")
			.style("font-size", function(d) {
				return d.size + "px";
			})
			.style("font-family", _representation.font)
			.style("fill", function(d, i) {
				return d.color;
			})
			.attr("text-anchor", "middle")
			.attr("transform", function(d) {
				return "translate("
					+ [ d.x, d.y ]
					+ ")rotate(" + d.rotate
					+ ")";
			})
			.text(function(d) {
				return d.word;
			});
	}

	normalizeWord = function(value) {
		var range = _value.maxFontSize - _value.minFontSize;
		var normalizedValue = (value - _sizeMin) / (_sizeMax - _sizeMin);
		normalizedValue = normalizedValue * range + _value.minFontSize;
		return normalizedValue;
	}

	wordCloud.validate = function() {
		return true;
	}

	wordCloud.setValidationError = function() {

	}

	wordCloud.getComponentValue = function() {
		return _value;
	}
	
	wordCloud.getSVG = function() {
		var svg = d3.select("svg")[0][0];
		return (new XMLSerializer()).serializeToString(svg);
	}

	return wordCloud;

}();