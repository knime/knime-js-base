knime_word_cloud = function() {

	var wordCloud = {};
	var _representation
	var _value;
	var _sizeMin = Number.POSITIVE_INFINITY;
	var _sizeMax = Number.NEGATIVE_INFINITY;

	wordCloud.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		if (_representation.imageGeneration && _value.svgFromView) {
			// take last generated svg from view if available
			return;
		}
		
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
		drawControls();
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
				return d.text;
			});
		var svg = d3.select("svg").node();
		_value.svgFromView = (new XMLSerializer()).serializeToString(svg);
	}
	
	function drawControls() {
		if (!knimeService) {
			return;
		}
		
		// -- Buttons --
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
		// -- Menu Items --
	    if (!_representation.enableViewConfig) return;
	    var pre = false;
	    
	    if (_representation.enableTitleChange || _representation.enableSubtitleChange) {
	    	if (_representation.enableTitleChange) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.title, function() {}, false);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
	    	}
	    	if (_representation.enableSubtitleChange) {
	    		var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.subtitle, function() {}, false);
	    		knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
	    	}
	    	pre = true;
	    }
	    
	    if (_representation.enableFontSizeChange || _representation.enableScaleTypeChange) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableScaleTypeChange) {
	    		var scaleOptions = ['linear', 'logarithmic', 'square root', 'exponential'];
	    		var scaleSelect = knimeService.createMenuSelect('scaleTypeSelect', _value.fontScaleType, scaleOptions, function() {});
	    		knimeService.addMenuItem('Font Scale:', 'expand', scaleSelect);
	    	}
	    	if (_representation.enableFontSizeChange) {
	    		var minFontSizeField = knimeService.createMenuNumberField('minFontSizeField', _value.minFontSize, 1, null, 0.5, function() {});
	    		knimeService.addMenuItem('Minimum Font Size:', 'text-height', minFontSizeField, null, knimeService.SMALL_ICON);
	    		
	    		var maxFontSizeField = knimeService.createMenuNumberField('maxFontSizeField', _value.maxFontSize, 1, null, 0.5, function() {});
	    		knimeService.addMenuItem('Maximum Font Size:', 'text-height', maxFontSizeField);
	    	}
	    	pre = true;
	    }
	    
	    if (_representation.enableSpiralTypeChange || _represenation.enableNumOrientationsChange || _representation.enableAnglesChange) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableSpiralTypeChange) {
	    		var spiralOptions = ['archimedean', 'rectangular'];
	    		var spiralSelect = knimeService.createMenuSelect('spiralSelect', _value.spiralType, spiralOptions, function() {});
	    		knimeService.addMenuItem('Spiral Type:', 'repeat', spiralSelect);
	    	}
	    	if (_representation.enableNumOrientationsChange) {
	    		var numOrientField = knimeService.createMenuNumberField('numOrientField', _value.numOrientations, 1, null, 1, function() {});
	    		knimeService.addMenuItem('Number Orientations:', 'sort', numOrientField);
	    	}
	    	if (_representation.enableAnglesChange) {
	    		var startAngleField = knimeService.createMenuNumberField('startAngleField', _value.startAngle, -90, 90, 1, function() {});
	    		knimeService.addMenuItem('Start Angle:', 'chevron-left', startAngleField);
	    		
	    		var endAngleField = knimeService.createMenuNumberField('endAngleField', _value.endAngle, -90, 90, 1, function() {});
	    		knimeService.addMenuItem('End Angle:', 'chevron-right', endAngleField);
	    	}
	    	pre = true;
	    }
	    
	    
	}

	wordCloud.validate = function() {
		return true;
	}

	wordCloud.setValidationError = function() {
		/* no validation on node model done */
	}

	wordCloud.getComponentValue = function() {
		return _value;
	}
	
	wordCloud.getSVG = function() {
		return _value.svgFromView;
	}

	return wordCloud;

}();