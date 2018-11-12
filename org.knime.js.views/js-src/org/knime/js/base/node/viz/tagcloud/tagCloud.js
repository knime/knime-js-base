knime_tag_cloud = function() {
	
	var wordCloud = {};
	var _representation, _value;
	var _sizeMin = Number.POSITIVE_INFINITY;
	var _sizeMax = Number.NEGATIVE_INFINITY;
	var _prevSize;
	var _data;
	var _colorScheme, _resizeTimeout, _animDuration;
	var _publishSelection, _currentFilter, _filterTable, _filteredDataSize;

	wordCloud.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		if (!_representation.data) {
			d3.select("body").append("p").text("Error: No data available");
			return;
		}
		
		if (_representation.showWarningsInView && _representation.warningMessages) {
			for (var key in _representation.warningMessages) {
				knimeService.setWarningMessage(_representation.warningMessages[key], key);
			}
		}
		
		if (_representation.filterTable) {
			_filterTable = new kt();
			_filterTable.setDataTable(_representation.filterTable);
		}
		
		// d3 scheme set 2
		_colorScheme = ["#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854", "#ffd92f", "#e5c494", "#b3b3b3"];
		// d3 scheme set 3
		/*_colorScheme = ["#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3", "#fdb462", "#b3de69", 
			"#fccde5", "#d9d9d9", "#bc80bd", "#ccebc5", "#ffed6f"];*/

		var disableAnimation = _representation.imageGeneration || _representation.disableAnimations;
		_animDuration = disableAnimation ? 0 : 500;
		
		if (_value.svgFromView) {
			if (_representation.imageGeneration) {
				return;
			}
			//restore previously saved layout
	        var parser = new DOMParser(); 
	        var xmlDoc = parser.parseFromString(_value.svgFromView, "text/xml"); 
	        var elemXML = xmlDoc.documentElement;
	        var elemSVG = document.adoptNode(elemXML);
			document.getElementsByTagName('body')[0].appendChild(elemSVG);
			
			//re-join data, using key function on previously assigned id
			if (_representation.data && _representation.data[0].id) {
				d3.select('svg g.vis').selectAll("text")
				.data(_representation.data, function (d) {
					return d ? d.id : this.getAttribute('id');
				})
				.on("click", function(d) {
					textClicked(d);
				});
				applySelection(true);
				if (_value.publishSelection && _value.selection) {
					knimeService.setSelectedRows(_representation.tableID, _value.selection, selectionChanged);
				}
			}
		}
		
		// Sorting is done in node model
		/*_representation.data.sort(function(x, y) {
			return d3.descending(x.size, y.size);
		});*/
		for (var i = 0; i < _representation.data.length; i++) {
			var curSize = _representation.data[i].size;
			_sizeMin = Math.min(_sizeMin, curSize);
			_sizeMax = Math.max(_sizeMax, curSize);
			_representation.data[i]._size = _representation.data[i].size;
		}
		
		_prevSize = getSize(false);
		
		if (!_value.svgFromView) {
			generateLayout();
		}
		drawControls();
		if (!_representation.imageGeneration && _representation.resizeToWindow) {
			var win = document.defaultView || document.parentWindow;
			win.onresize = resize;
		}
	}
	
	function getSize(withTitleMargin) {
		var mTop = 0;
		if (withTitleMargin) {
			if (_value.title && _value.subtitle) {
				mTop += 56;        	
			} else if (_value.title) {
				mTop += 36;
			} else if (_value.subtitle) {
				mTop += 26;
			}
		}
		if (_representation.imageGeneration || !_representation.resizeToWindow) {
			return [_representation.imageWidth, _representation.imageHeight - mTop, mTop];
		}
		var pad = 5;
		var doc = document.documentElement;
		return [doc.clientWidth - pad, doc.clientHeight - pad - mTop, mTop];
	}
	
	function resize() {
		var curSize = getSize(false);
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
		.size(getSize(true))
		.words(getFilteredData())
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
		.timeInterval(100)
		.spiral(_value.spiralType)
		.overflow(true)
		.on("end", draw).start();
	}
	
	function draw(words, scale) {
		var size = getSize(true);
		var locS = 1;
		//determine scale factor
		if (scale) {
			var sX1 = size[0] / Math.abs(scale[1].x - size[0] / 2)
			var sX2 = size[0] / Math.abs(scale[0].x - size[0] / 2);
			var sY1 = size[1] / Math.abs(scale[1].y - size[1] / 2);
			var sY2 = size[1] / Math.abs(scale[0].y - size[1] / 2);
			locS = Math.min(sX1,sX2, sY1, sY2) / 2;
		}
		
		//set or clear warning
		if (words.length < _filteredDataSize) {
			if (_representation.showWarningsInView) {
				knimeService.setWarningMessage("Not all words could be displayed due to space restrictions or words are overlapping." 
					+ " Adapt the font size settings or enlarge the view area.", "tooFewWords");
			}
		} else {
			knimeService.clearWarningMessage("tooFewWords");
		}
		
		var svg = d3.select("svg");
		if (svg.empty()) {
			//build basic structure
			svg = d3.select("body").append("svg");
			//createSVGFilters(svg);
			svg.append("g")
				.attr("class", "titles")
				.attr("transform", "translate(2,0)");
			svg.append("g")
				.attr("class", "vis")
				.attr("transform", "translate(" 
						+ [size[0] >> 1, (size[1] >> 1) + size[2]] + ")");
		}
		var svgSize = getSize(false);
		svg.attr("width", svgSize[0])
			.attr("height", svgSize[1]);
		
		// create titles
		var titleG = svg.select("g.titles");
		titleG.selectAll("*").remove();
		if (_value.title) {
        	titleG.append("text")
	        	.text(_value.title)
				.attr("id", "title")
				.attr("class", "knime-title")
	        	.attr("y", 24);
        }
		if (_value.subtitle) {
        	titleG.append("text")        
	        	.text(_value.subtitle)
				.attr("id", "subtitle")
				.attr("class", "knime-subtitle")
	        	.attr("y", size[2] - 12);
        }
		
		//add empty rect for batik bounds calculation on empty plot
		if (_representation.imageGeneration && words.length < 1) {
			svg.select("g.vis").remove();
			svg.append("rect")
				.attr("width", svgSize[0])
				.attr("height", svgSize[1] - size[2])
				.attr("transform", "translate(0," + size[2] + ")")
				.style("fill", "white");
			if (!_value.title && !_value.subtitle) {
				svg.select("g.titles").remove();
			}
			var svgNode = svg.node();
			_value.svgFromView = (new XMLSerializer()).serializeToString(svgNode);
			return;
		}
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
			})
			.style("fill", function(d, i) {
				if (_representation.useColorProperty) {
					return d.color;
				} else {
					return _colorScheme[~~(Math.random() * _colorScheme.length)];
				}
			})
			.text(function(d) {
				return d.text;
			})
			.transition().duration(_animDuration)
			.style("font-size", function(d) {
		        return d.size + "px"
		    });
		//add new words
		data.enter()
			.append("text")
			.attr("id", function (d) {
				return d.id;
			})
			.style("font-size", function(d) {
				return (_representation.imageGeneration ? d.size : 1) + "px";
			})
			.style("font-family", _representation.font)
			.style("font-weight", _representation.fontBold ? "bold" : "normal")
			.style("fill", function(d, i) {
				if (_representation.useColorProperty) {
					return d.color;
				} else {
					return _colorScheme[~~(Math.random() * _colorScheme.length)];
				}
			})
			.attr("text-anchor", "middle")
			.attr("transform", function(d) {
				return "translate("
					+ [ d.x, d.y ]
					+ ")rotate(" + d.rotate
					+ ")";
			})
			.style("cursor", "pointer")
			.style("-webkit-user-select", "none")
			.style("-moz-user-select", "none")
			.style("-ms-user-select", "none")
			.style("user-select", "none")
			.on("click", function(d) {
				textClicked(d, d3.event);
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
			.attr("transform", "translate(" + [size[0] >> 1, (size[1] >> 1) + size[2]] 
		        + ")scale(" + locS + ")")
		    .on("end", function() {
		    	//save new svg
				var svgNode = svg.node();
				_value.svgFromView = (new XMLSerializer()).serializeToString(svgNode);
		    });
		applySelection(true);
	}
	
	function createSVGFilters(svg) {
		var defs = svg.append("defs");
		createOuterGlowFilter(defs, "selectionGlow", _representation.selectionColor);
		createOuterGlowFilter(defs, "partialSelectionGlow", "#DDDDDD");
	}
	
	function createOuterGlowFilter(defs, id, color) {
		var rgb = d3.rgb(color);
		var selFilter = defs.append("filter");
		selFilter.attr("id", id)
			.attr("width", "140%")
			.attr("height", "140%")
			.attr("x", "-20%")
			.attr("y", "-20%");
		/*selFilter.append("feMorphology")
			.attr("operator", "dilate")
			.attr("radius", "4")
			.attr("in", "SourceAlpha")
			.attr("result", "thicken");*/
		/*selFilter.append("feColorMatrix")
			.attr("type", "matrix")
			.attr("in", "SourceGraphic")
			.attr("result", "colored")
			.attr("values", "0 0 0 " + rgb.r + " 0 0 0 0 0 " + rgb.g + " 0 0 0 0 " + rgb.b + " 0 0 0 1 0");*/
		selFilter.append("feGaussionBlur")
			/*.attr("in", "thicken")*/
			.attr("stdDeviation", "4")
			/*.attr("in", "colored")*/
			/*.attr("result", "coloredBlur");*/
		/*selFilter.append("feFlood")
			.attr("flood-color", color)
			.attr("result", "glowColor");
		selFilter.append("feComposite")
			.attr("in", "glowColor")
			.attr("in2", "blurred")
			.attr("operator", "in")
			.attr("result", "softGlow_colored");*/
		/*var merge = selFilter.append("feMerge");
		merge.append("feMergeNode").attr("in", "coloredBlur");
		merge.append("feMergeNode").attr("in", "SourceGraphic");*/
	}
	
	function updateTitles() {
		var title = d3.select("svg g.titles text#title");
		var subtitle = d3.select("svg g.titles text#subtitle");
		if (!_value.title != title.empty() || !_value.subtitle != subtitle.empty()) {
			// if titles get introduced/removed sizes change
			redraw();
		} else {
			// otherwise simply update
			if (_value.title) {
				title.text(_value.title);
			}
			if (_value.subtitle) {
				subtitle.text(_value.subtitle);
			}
		}
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
		
		if (_representation.enableSelection && _representation.displayClearSelectionButton) {
			knimeService.addButton('clear-selection-button', 'minus-square-o', 'Clear Selection', function() {
				clearSelection();
			});
		}
		
		// -- Initial interactivity settings --
		if (knimeService.isInteractivityAvailable()) {
        	if (_value.subscribeSelection) {
				knimeService.subscribeToSelection(_representation.tableID, selectionChanged);
			}
        	var filterIds = _representation.subscriptionFilterIds;
        	if (filterIds && filterIds.length > 0 && _value.subscribeFilter) {
				knimeService.subscribeToFilter(_representation.tableID, filterChanged, filterIds);
			}
        }
		
		// -- Menu Items --
	    if (!_representation.enableViewConfig) {
	    	return;
	    }
	    var pre = false;
	    
	    if (_representation.enableTitleChange || _representation.enableSubtitleChange) {
	    	if (_representation.enableTitleChange) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.title, function() {
	    			_value.title = this.value;
	    			updateTitles();
	    		}, false);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
	    	}
	    	if (_representation.enableSubtitleChange) {
	    		var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.subtitle, function() {
	    			_value.subtitle = this.value;
	    			updateTitles();
	    		}, false);
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
	    			if (size < 1 || isNaN(size)) {
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
	    		}, false);
	    		knimeService.addMenuItem('Minimum Font Size:', 'text-height', minFontSizeField, null, knimeService.SMALL_ICON);
	    		
	    		var maxFontSizeField = knimeService.createMenuNumberField('maxFontSizeField', _value.maxFontSize, 1, null, 0.5, function() {
	    			var size = parseFloat(this.value);
	    			var minSize = parseFloat(d3.select("#minFontSizeField").node().value);
	    			if (size < minSize || isNaN(size)) {
	    				this.value = minSize;
	    				size = minSize;
	    			}
	    			if (_value.maxFontSize != size) {
	    				_value.maxFontSize = size;
	    				redraw();
	    			}
	    		}, false);
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
	    			if (num < 1 || isNaN(num)) {
	    				this.value = 1;
	    				num = 1;
	    			}
	    			if (_value.numOrientations != num) {
	    				_value.numOrientations = num;
	    				redraw();
	    			}
	    		}, false);
	    		knimeService.addMenuItem('Number Orientations:', 'sort', numOrientField);
	    	}
	    	if (_representation.enableAnglesChange) {
	    		var startAngleField = knimeService.createMenuNumberField('startAngleField', _value.startAngle, -90, 90, 1, function() {
	    			var angle = parseInt(this.value);
	    			if (angle < -90 || isNaN(angle)) {
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
	    		}, false);
	    		knimeService.addMenuItem('Start Angle:', 'chevron-left', startAngleField);
	    		
	    		var endAngleField = knimeService.createMenuNumberField('endAngleField', _value.endAngle, -90, 90, 1, function() {
	    			var angle = parseInt(this.value);
	    			if (angle > 90 || isNaN(angle)) {
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
	    		}, false);
	    		knimeService.addMenuItem('End Angle:', 'chevron-right', endAngleField);
	    	}
	    	pre = true;
	    }
	    if (_representation.enableShowSelectedOnly || knimeService.isInteractivityAvailable()) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableShowSelectedOnly) {
	    		var showSelectedOnlyCheckbox = knimeService.createMenuCheckbox('showSelectedOnlyCheckbox', _value.showSelectedOnly, function() {
					_value.showSelectedOnly = this.checked;
					redraw();
				});
				knimeService.addMenuItem('Show selected rows only', 'filter', showSelectedOnlyCheckbox);
	    	}
	    	if (knimeService.isInteractivityAvailable()) {
	    		var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
				var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.publishSelection, function() {
					if (this.checked) {
						_value.publishSelection = true;
						knimeService.setSelectedRows(_representation.tableID, getSelection());
					} else {
						_value.publishSelection = false;
					}
				});
				knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
				
				var subSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
				var subSelCheckbox = knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.subscribeSelection, function() {
					if (this.checked) {
						knimeService.subscribeToSelection(_representation.tableID, selectionChanged);
					} else {
						knimeService.unsubscribeSelection(_representation.tableID, selectionChanged);
					}
				});
				knimeService.addMenuItem('Subscribe to selection', subSelIcon, subSelCheckbox);
				
				if (_representation.subscriptionFilterIds && _representation.subscriptionFilterIds.length > 0) {
					var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');
					var subFilCheckbox = knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.subscribeFilter, function() {
						if (this.checked) {
							knimeService.subscribeToFilter(_representation.tableID, filterChanged, _representation.subscriptionFilterIds);
						} else {
							knimeService.unsubscribeFilter(_representation.tableID, filterChanged);
						}
					});
					knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);
				}
	    	}
	    }
	}
	
	function textClicked(d, event) {
		var selection = _value.selection || [];
		if (d3.event.ctrlKey || d3.event.metaKey) {
			if (d.selected) {
				for (var i = 0; i < d.rowIDs.length; i++) {
					var index = selection.indexOf(d.rowIDs[i]);
					if (index > -1) {
						selection.splice(index, 1);
					}
				}
				_value.selection = selection;
				if (_value.publishSelection) {
					knimeService.removeRowsFromSelection(_representation.tableID, d.rowIDs, selectionChanged);
				}
			} else {
				_value.selection = selection.concat(d.rowIDs);
				if (_value.publishSelection) {
					knimeService.addRowsToSelection(_representation.tableID, d.rowIDs, selectionChanged);
				}
			}
		} else {
			_value.selection = d.rowIDs;
			if (_value.publishSelection) {
				knimeService.setSelectedRows(_representation.tableID, d.rowIDs, selectionChanged);
			}
		}
    	applySelection(true);
	}
	
	function applySelection(redraw) {
		var selection = _value.selection || [];
		d3.select("svg").select("g.vis").selectAll("text").each(function (d, i) {
			var selectedRowIDs = [];
			for (var r = 0; r < d.rowIDs.length; r++) {
				var curRowID = d.rowIDs[r];
				if (selection.indexOf(curRowID) > -1) {
					selectedRowIDs.push(curRowID);
				}
			}
			d.selectedRowIDs = selectedRowIDs;
			if (selectedRowIDs.length == d.rowIDs.length) {
				d.selected = true;
				d.partialSelected = false;
			} else {
				d.selected = false;
				d.partialSelected = selectedRowIDs.length > 0;
			}
			if (redraw) {
				var strokeWidth = Math.max(1,~~(Math.log(d.size)/Math.log(5)));
				d3.select(this)
					.attr("stroke", d.selected ? _representation.selectionColor : d.partialSelected ? "#DDDDDD" : null)
					.attr("stroke-width", (d.selected || d.partialSelected) ? strokeWidth : null)
					.attr("stroke-opacity", (d.selected || d.partialSelected) ? 1 : null)
					.attr("stroke-dasharray", (d.selected || d.partialSelected) ? "5,1" : null)
					/*.style("filter", d.selected ? "url(#selectionGlow)" : d.partialSelected ? "url(#partialSelectionGlow)" : null)*/
					/*.style("filter", (d.selected || d.partialSelected) ? "blur(4px)" : null)*/;
			}
		});
	}
	
	function getSelection() {
		return _value.selection;
	}
	
	function clearSelection() {
		_value.selection = [];
		if (_value.showSelectedOnly) {
			redraw();
		} else {
			applySelection(true);
		}
		if (_value.publishSelection) {
			knimeService.setSelectedRows(_representation.tableID, getSelection());
		}
	}
	
	function selectionChanged(data) {
		if (data.changeSet) {
			if (data.changeSet.removed && _value.selection) {
				for (var i = 0; i < data.changeSet.removed.length; i++) {
					var removed = data.changeSet.removed[i];
					var index = _value.selection.indexOf(removed);
					if (index > -1) {
						_value.selection.splice(index, 1);
					}
				}
			}
			if (data.changeSet.added) {
				if (!_value.selection) {
					_value.selection = [];
				}
				for (var i = 0; i < data.changeSet.added.length; i++) {
					var added = data.changeSet.added[i];
					if (_value.selection.indexOf(added) < 0) {
						_value.selection.push(added);
					}
				}
			}
		} else {
			_value.selection = knimeService.getAllRowsForSelection(_representation.tableID);
		}
		if (_value.showSelectedOnly) {
			redraw();
		} else {
			applySelection(true);
		}
	}
	
	function getFilteredData() {
		if (!_currentFilter && !_value.showSelectedOnly) {
			return JSON.parse(JSON.stringify(_representation.data));
		}
		var data = [];
		var selection = _value.selection || [];
		for (var i = 0; i < _representation.data.length; i++) {
			var include = true;
			var cD = _representation.data[i];
			if (_value.showSelectedOnly) {
				include = false;
				for (var r = 0; r < cD.rowIDs.length; r++) {
					if (selection.indexOf(cD.rowIDs[r]) > -1) {
						include = true;
						break;
					}
				}
			}
			if (include && _currentFilter && _filterTable) {
				include = false;
				for (var r = 0; r < cD.rowIDs.length; r++) {
					if (_filterTable.isRowIncludedInFilter(cD.rowIDs[r], _currentFilter)) {
						include = true;
						break;
					}
				}
			}
			if (include) {
				data.push(JSON.parse(JSON.stringify(cD)));
			}
		}
		_filteredDataSize = data.length;
		return data;
	}
	
	function filterChanged(data) {
		_currentFilter = data;
		redraw();
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
		var parser = new DOMParser();
		var svg = parser.parseFromString(_value.svgFromView, "image/svg+xml");
		// remove selection outlines
		d3.select(svg).selectAll("text")
			.attr("stroke", null)
			.attr("stroke-width", null)
			.attr("stroke-opacity", null)
			.attr("stroke-dasharray", null);
		
		var svgElement = svg.getElementsByTagName('svg')[0];
		knimeService.inlineSvgStyles(svgElement);
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return wordCloud;

}();