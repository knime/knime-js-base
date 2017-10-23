knime_word_cloud = function() {
	
	/* TODO: 
	 * general node description
	 * missing values + warnings, 
	 * generic colors if no colors present,
	 * title + subtitle,
	 * warning for too few words in JS table,
	 * test view in combined view
	 */

	var wordCloud = {};
	var _representation
	var _value;
	var _sizeMin = Number.POSITIVE_INFINITY;
	var _sizeMax = Number.NEGATIVE_INFINITY;
	var _prevSize;
	var _data;
	var _resizeTimeout;
	var _animDuration;
	var _clearSVG = false;

	wordCloud.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		if (!_representation.data) {
			d3.select("body").text("Error: No data available");
			return;
		}
		
		d3.select("html").style("width", "100%").style("height", "100%")/*.style("overflow", "hidden")*/;
		d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");
		
		_animDuration = _representation.imageGeneration || _representation.disableAnimations ? 0 : 500;
		
		if (_value.svgFromView) {
			if (_representation.imageGeneration) {
				return;
			}
			d3.select('body').html(_value.svgFromView);
		}
		
		_representation.data.sort(function(x, y) {
			return d3.descending(x.size, y.size);
		});
		for (var i = 0; i < _representation.data.length; i++) {
			var curSize = _representation.data[i].size;
			_sizeMin = Math.min(_sizeMin, curSize);
			_sizeMax = Math.max(_sizeMax, curSize);
			_representation.data[i]._size = _representation.data[i].size;
		}
		
		_prevSize = getSize();
		
		if (!_value.svgFromView) {
			generateLayout();
		}
		drawControls();
		if (!_representation.imageGeneration && _representation.resizeToWindow) {
			var win = document.defaultView || document.parentWindow;
			win.onresize = resize;
		}
	}
	
	function getSize() {
		if (_representation.imageGeneration || !_representation.resizeToWindow) {
			return [_representation.imageWidth, _representation.imageHeight];
		}
		var pad = 5;
		var doc = document.documentElement;
		return [doc.clientWidth - pad, doc.clientHeight - pad /* minus titles */];
	}
	
	function resize() {
		var curSize = getSize();
		if (Math.abs(_prevSize[0] - curSize[0]) > 5 || Math.abs(_prevSize[1] - curSize[1]) > 5) {
			_prevSize = curSize;
			redraw();
		}
	}
	
	function redraw() {
		// debounce events to avoid intermediate (expensive) calculations, e.g. on resize
		clearTimeout(_resizeTimeout);
		_resizeTimeout = setTimeout(generateLayout, 250 /*ms*/);
	}
	
	function getFontScale() {
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
		return scale;
	}
	
	function generateLayout() {
		var scale = getFontScale();
		d3.layout.cloud()
		.size(getSize())
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
			d.size = d._size;
			return scale(d.size);
		})
		.fontWeight(_representation.fontBold ? "bold" : "normal")
		.timeInterval(10)
		.spiral(_value.spiralType)
		.on("end", draw).start();
	}
	
	function draw(words, scale) {
		if (_clearSVG) {
			d3.selectAll("svg g > *").remove();
			_clearSVG = false;
		}
		//set or clear warning
		if (words.length < _representation.data.length) {
			if (_representation.showWarningsInView) {
				knimeService.setWarningMessage("Not all words could be displayed due to space restrictions or words are overlapping." 
					+ "Adapt the font size settings or enlarge the view area.", "tooFewWords");
			}
			_clearSVG = true;
		} else {
			knimeService.clearWarningMessage("tooFewWords");
		}
		var size = getSize();
		var locS = 1;
		if (scale) {
			var sX1 = size[0] / Math.abs(scale[1].x - size[0] / 2)
			var sX2 = size[0] / Math.abs(scale[0].x - size[0] / 2);
			var sY1 = size[1] / Math.abs(scale[1].y - size[1] / 2);
			var sY2 = size[1] / Math.abs(scale[0].y - size[1] / 2);
			locS = Math.min(sX1,sX2, sY1, sY2) / 2;
		}
		var svg = d3.select("svg");
		if (svg.empty()) {
			svg = d3.select('body').append('svg');
			svg.append('g')
				.attr('class', 'vis')
				.attr("transform", "translate(" 
						+ [size[0] >> 1, size[1] >> 1] + ")");
		}
		svg.attr("width", size[0])
			.attr("height", size[1]);
		var data = svg.select("g.vis")
			.selectAll("text").data(words);
		//update existing words
		data.transition()
			.duration(_animDuration)
			.attr("transform", function(d) {
				return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
			})
			.style("font-size", function(d) {
				return d.size + "px";
			});
		//add new words
		data.enter()
			.append("text")
			.style("font-size", function(d) {
				return (_representation.imageGeneration ? d.size : 1) + "px";
			})
			.style("font-family", _representation.font)
			.style("font-weight", _representation.fontBold ? "bold" : "normal")
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
			})
			.transition().duration(_animDuration)
			.style("font-size", function(d) {
		        return d.size + "px"
		    });
		//fade out removed words
		data.exit()
			.transition()
			.duration(_animDuration)
			.style("opacity", 1e-6)
			.remove();
		svg.select("g.vis")
			.transition()
			.delay(_animDuration)
			.duration(_animDuration)
			.attr("transform", "translate(" + [size[0] >> 1, size[1] >> 1] 
		        + ")scale(" + locS + ")")
		    .on("end", function() {
		    	//save new svg
				var svgNode = svg.node();
				_value.svgFromView = (new XMLSerializer()).serializeToString(svgNode);
		    });
	}
	
	function drawControls() {
		if (!knimeService) {
			return;
		}
		
		// -- Buttons --
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
		if (_representation.displayRefreshButton) {
			knimeService.addButton('refresh-button', 'refresh', 'Refresh', function() {
				redraw();
			});
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
	    		var scaleSelect = knimeService.createMenuSelect('scaleTypeSelect', _value.fontScaleType, scaleOptions, function() {
	    			var scaleType = this.value;
	    			if (_value.fontScaleType != scaleType) {
	    				_value.fontScaleType = scaleType;
	    				redraw();
	    			}
	    		});
	    		knimeService.addMenuItem('Font Scale:', 'expand', scaleSelect);
	    	}
	    	if (_representation.enableFontSizeChange) {
	    		var minFontSizeField = knimeService.createMenuNumberField('minFontSizeField', _value.minFontSize, 1, null, 0.5, function() {
	    			var size = parseFloat(this.value);
	    			if (size < 1) {
	    				this.value = 1;
	    				size = 1;
	    			}
	    			var maxSize = parseFloat(d3.select("#maxFontSizeField").node().value);
	    			if (size > maxSize) {
	    				this.value = maxSize;
	    				size = maxSize;
	    			}
	    			if (_value.minFontSize != size) {
	    				_value.minFontSize = size;
	    				redraw();
	    			}
	    		});
	    		knimeService.addMenuItem('Minimum Font Size:', 'text-height', minFontSizeField, null, knimeService.SMALL_ICON);
	    		
	    		var maxFontSizeField = knimeService.createMenuNumberField('maxFontSizeField', _value.maxFontSize, 1, null, 0.5, function() {
	    			var size = parseFloat(this.value);
	    			var minSize = parseFloat(d3.select("#minFontSizeField").node().value);
	    			if (size < minSize) {
	    				this.value = minSize;
	    				size = minSize;
	    			}
	    			if (_value.maxFontSize != size) {
	    				_value.maxFontSize = size;
	    				redraw();
	    			}
	    		});
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
	    		var spiralSelect = knimeService.createMenuSelect('spiralSelect', _value.spiralType, spiralOptions, function() {
	    			var type = this.value;
	    			if (_value.spiralType != type) {
	    				_value.spiralType = type;
	    				redraw();
	    			}
	    		});
	    		knimeService.addMenuItem('Spiral Type:', 'repeat', spiralSelect);
	    	}
	    	if (_representation.enableNumOrientationsChange) {
	    		var numOrientField = knimeService.createMenuNumberField('numOrientField', _value.numOrientations, 1, null, 1, function() {
	    			var num = parseInt(this.value);
	    			if (num < 1) {
	    				this.value = 1;
	    				num = 1;
	    			}
	    			if (_value.numOrientations != num) {
	    				_value.numOrientations = num;
	    				redraw();
	    			}
	    		});
	    		knimeService.addMenuItem('Number Orientations:', 'sort', numOrientField);
	    	}
	    	if (_representation.enableAnglesChange) {
	    		var startAngleField = knimeService.createMenuNumberField('startAngleField', _value.startAngle, -90, 90, 1, function() {
	    			var angle = parseInt(this.value);
	    			if (angle < -90) {
	    				this.value = -90;
	    				angle = -90;
	    			}
	    			var endAngle = parseInt(d3.select("#endAngleField").node().value);
	    			if (angle > endAngle) {
	    				this.value = endAngle;
	    				angle = endAngle;
	    			}
	    			if (_value.startAngle != angle) {
	    				_value.startAngle = angle;
	    				redraw();
	    			}
	    		});
	    		knimeService.addMenuItem('Start Angle:', 'chevron-left', startAngleField);
	    		
	    		var endAngleField = knimeService.createMenuNumberField('endAngleField', _value.endAngle, -90, 90, 1, function() {
	    			var angle = parseInt(this.value);
	    			if (angle > 90) {
	    				this.value = 90;
	    				angle = 90;
	    			}
	    			var startAngle = parseInt(d3.select("#startAngleField").node().value);
	    			if (angle < startAngle) {
	    				this.value = startAngle;
	    				angle = startAngle;
	    			}
	    			if (_value.endAngle != angle) {
	    				_value.endAngle = angle;
	    				redraw();
	    			}
	    		});
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