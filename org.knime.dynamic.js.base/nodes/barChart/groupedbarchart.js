(grouped_bar_chart_namespace = function() {

	var barchart = {};
	var layoutContainer;
	var MIN_HEIGHT = 200, MIN_WIDTH = 300;
	var _representation, _value;
	var chart, svg;
	var staggerCheckbox;
	var knimeTable;
		
	var plotData;
	var colorRange;
	var categories;
	var freqCols;
	var missValFreqCols;
	var missValPairs;
	var missValCatValues;
	var excludeDataCatMap;

	var MISSING_VALUES_LABEL = "Missing values";
	var MISSING_VALUES_ONLY = "missingValuesOnly";
	var FREQ_COLUMN_MISSING_VALUES_ONLY = "freqColumnMissingValuesOnly";
	var CATEGORY_MISSING_VALUES_ONLY = "categoryMissingValuesOnly";
	
	barchart.init = function(representation, value) {  
		_value = value;
		_representation = representation;
		
		if (_representation.warnMessage) {
			knimeService.setWarningMessage(_representation.warnMessage);
		}
				
		if (_representation.options.enableViewControls) {
			drawControls();
		}
		drawChart();
		
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	}

	function drawChart(redraw) {
		d3.selectAll("html, body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");

		/*
		 * Process options
		 */
		var viewControls = _representation.options.enableViewControls;
		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];

		var optTitle = _value.options["title"];
		var optSubtitle = _value.options["subtitle"];
		var optCatLabel = _value.options["catLabel"];
		var optFreqLabel = _value.options["freqLabel"];
		
		var optStaggerLabels = _representation.options["staggerLabels"];
		var optLegend = _representation.options["legend"];		

		var optOrientation = _value.options["orientation"];	

		var optFullscreen = _representation.options["svg"]["fullscreen"] && _representation.runningInView;
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]
		
		var isTitle = optTitle || optSubtitle;

		var body = d3.select("body");

		var width = optWidth + "px";
		var height = optHeight + "px";
		if (optFullscreen) {
			width = "100%";
			height = (isTitle) ? "100%" : "calc(100% - " + knimeService.headerHeight() + "px)";
		}
		
		var div;
		if (redraw) {
			d3.select("svg").remove();
			div = d3.select("#svgContainer");
		} else {
			layoutContainer = body.append("div")
				.attr("id", "layoutContainer")
				.style('display', 'block')
				.style("width", width)
				.style("height", height)
				.style("min-width", MIN_WIDTH + "px")
				.style("min-height", MIN_HEIGHT + "px");
			
			div = layoutContainer.append("div")
				.attr("id", "svgContainer")
				.style("min-width", MIN_WIDTH + "px")
				.style("min-height", MIN_HEIGHT + "px")
				.style("box-sizing", "border-box")
				.style("overflow", "hidden")
				.style("margin", "0");
		}


		var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		div[0][0].appendChild(svg1);

		svg = d3.select("svg")
			.style("font-family", "sans-serif")
			.style("display", "block")
			.classed("colored", true);

		if (!optFullscreen) {
			if (optWidth > 0) {
				div.style("width", optWidth+"px")
				svg.attr("width", optWidth);
				// Looks like the below doesn't work,
				// above does work...
				//chart.width(optWidth);
			}
			if (optHeight > 0) {
				svg.attr("height", optHeight);
				div.style("height", optHeight+"px");
				// Looks like the below doesn't work,
				// above does work...
				//chart.height(optHeight);
			}	    	
		} else {
			// Set full screen height/width
			div.style("width", "100%");
			div.style("height", height);

			svg.attr("width", "100%");
			svg.attr("height", "100%");
		}
		
		if (!redraw) {		
			/* 
			 * Process data
			 */		
			knimeTable = new kt();
			// Add the data from the input port to the knimeTable.
			var port0dataTable = _representation.inObjects[0];
			knimeTable.setDataTable(port0dataTable);
			
			processData();
		}

		
		/*
		 * Plot chart
		 */
		nv.addGraph(function() {
			if (optOrientation) {
				chart = nv.models.multiBarHorizontalChart();
			} else {
				chart = nv.models.multiBarChart();
				chart.reduceXTicks(false);
			}
			
			var stacked = _value.options.chartType == 'Stacked';
			if (stacked) {
				fixStackedData(true);
			}
			chart.stacked(stacked);			
			
			
			chart
				.color(colorRange)
				.duration(300)
				.margin({right: 20, top: 60})
				.groupSpacing(0.1);
			
			updateTitles(false);

	        chart.showControls(false);  // all the controls moved to Settings menu
			chart.showLegend(optLegend);

			updateAxisLabels(false);

			svg.datum(plotData)
				.transition().duration(300)
				.call(chart);
			nv.utils.windowResize(chart.update);
			
			return chart;
		})	
	}
	
	processData = function() {
		var optMethod = _representation.options["aggr"];
		var optFreqCol = _representation.options["freq"];
		var optCat = _representation.options["cat"];
		
		var customColors, colorScale;
		if (_representation.inObjects[1]) {
			// Custom color scale
			var colorTable = new kt();
			colorTable.setDataTable(_representation.inObjects[1]);
			if (colorTable.getColumnTypes()[0] == 'string') {
				customColors = {};
				var colorCol = colorTable.getColumn(0);
				for (var i = 0; i < colorCol.length; i++) {
					customColors[colorCol[i]] = colorTable.getRowColors()[i];
				}
				colorScale = [];
			}
		}
		
		categories = knimeTable.getColumn(optCat);
		var numCat = categories.length;
		
		// Default color scale
		if (!customColors) {
			colorScale = d3.scale.category10();
			if (categories.length > 10) {
				colorScale = d3.scale.category20();
			}
		}
		
		if (optMethod == "Occurence\u00A0Count") {
			optFreqCol = [knimeTable.getColumnNames()[1]];
		}

		// Get the frequency columns
		var valCols = [];
		var isDuplicate = false;
		freqCols = [];

		for (var k = 0; k < optFreqCol.length; k++) {
			var valCol = knimeTable.getColumn(optFreqCol[k]);
			// ToDo: Add an isDuplicate test here...
			if (isDuplicate != true) {
				valCols.push( valCol );
				freqCols.push(optFreqCol[k]);
			}
		}		
		
		plotData = [];
		missValFreqCols = [];
		missValPairs = new Array(numCat);
		for (var i = 0; i < numCat; i++) {
			missValPairs[i] = [];
		}
		missValCatValues = [];
		if (valCols.length > 0) {
			var numDataPoints = valCols[0].length;
			for (var j = 0; j < freqCols.length; j++) {	

				var key = freqCols[j];
				if (optMethod == "Occurence\u00A0Count") {
					key = "Occurence Count";
				}
				var values = [];
				var onlyMissValCol = true;
				var valueOnMissValCat = false;

				for (var i = 0; i < numDataPoints; i++) {
					if (categories != undefined) {
						if (isDuplicate == true)  {
							alert("Duplicate categories found in column.");
							return "duplicate";
						}
						
						var cat = categories[i];
						var val = valCols[j][i];
						
						if (cat !== null) {							
							if (val !== null) {
								onlyMissValCol = false;
								values.push({"x": cat, "y": val});								
							}
						} else {
							// Missing values category
							if (val !== null) {
								missValCatValues.push({"col": key, "value": val});
								valueOnMissValCat = true;
							}
						}
						
						if (val !== null) {
							
						} else {
							missValPairs[i].push(key);
						}
					}
				}
				
				if (!onlyMissValCol) {			
					var plotStream = {"key": key, "values": values};
					plotData.push(plotStream);
				
					if (customColors) {
						var color = customColors[key];
						if (!color) {
							color = "#7C7C7C";
						}
						colorScale.push(color);
					}
				} else {
					missValFreqCols.push({"col": key, "valueOnMissValCat": valueOnMissValCat});
				}
			}
		} else {
			if (hasNull == false) {
				alert("No numeric columns detected.");
				return "numeric";
			} else {
				alert("Numeric columns detected, but contains missing values.");
				return "missing";
			}

		}
		
		colorRange = customColors ? colorScale : colorScale.range();
		
		processMissingValues();
	}
	
	processMissingValues = function(switched) {
		// Make a list of freq columns to exclude
		var excludeCols = [];
		for (var i = 0; i < missValFreqCols.length; i++) {
			var col = missValFreqCols[i];
			if (!col.valueOnMissValCat || col.valueOnMissValCat && !_value.options.includeMissValCat) {
				excludeCols.push(col.col);				
			}			
		}
		
		// Make a list of excluded bars per category or whole categories
		var excludeBars = [];
		var excludeCats = [];
		var numLeftCols = freqCols.length - excludeCols.length;
		var missValCat;
		var excludeWholeMissValCat = false;
		excludeDataCatMap = {};
		for (var i = 0; i < missValPairs.length; i++) {
			var cat = categories[i];
			var cols = missValPairs[i].filter(function(x) { return excludeCols.indexOf(x) == -1 });			
			if (cols.length > 0) {
				if (cols.length == numLeftCols) {
					if (cat !== null) {
						excludeCats.push(cat);
					} else {
						excludeWholeMissValCat = true;
					}					
				} else {
					var label = cat !== null ? cat : MISSING_VALUES_LABEL;
					var str = label + " - " + cols.join(", ");
					if (cat !== null) {
						excludeBars.push(str);						
					} else {
						missValCat = str;
					}
					if (cat !== null || _value.options.includeMissValCat) {
						cols.forEach(function(col) {
							if (excludeDataCatMap[col] != undefined) {
								excludeDataCatMap[col].push(cat);
							} else {
								excludeDataCatMap[col] = [cat];
							}
						});
					}
				}
			}
		}
		if (_value.options.includeMissValCat) {
			if (missValCat !== undefined) {
				excludeBars.push(missValCat);
			} else if (excludeWholeMissValCat) {
				excludeCats.push(MISSING_VALUES_LABEL);
			}
		}
		
		for (var i = 0; i < missValCatValues.length; i++) {
			var item = missValCatValues[i];
			if (excludeCols.indexOf(item.col) != -1 && !(!_value.options.includeMissValCat && switched)) {
				continue;
			}
			var data = undefined;
			var dataInd;
			for (var j = 0; j < plotData.length; j++) {
				if (plotData[j].key == item.col) {
					data = plotData[j];
					dataInd = j;
					break;
				}	
			}			
			if (_value.options.includeMissValCat) {
				var val = {"x": MISSING_VALUES_LABEL, "y": item.value};
				if (data !== undefined) {
					data.values.push(val);
				} else {
					plotData.push({"key": item.col, "values": [val]})
				}				
			} else if (switched) {
				if (data !== undefined) {
					data.values.pop();
					if (data.values.length == 0) {
						plotData.splice(dataInd, 1);
					}
				}			
			}
		}
		
		// Set warning messages
		if (excludeCols.length > 0) {
			knimeService.setWarningMessage("Following frequency columns are not present or contain only missing values and were excluded from the view:\n    " + excludeCols.join(", "), FREQ_COLUMN_MISSING_VALUES_ONLY);
		} else {
			knimeService.clearWarningMessage(FREQ_COLUMN_MISSING_VALUES_ONLY);
		}
		
		if (excludeCats.length > 0) {
			knimeService.setWarningMessage("Following categories contain only missing values and were excluded from the view:\n    " + excludeCats.join(", "), CATEGORY_MISSING_VALUES_ONLY);
		} else {
			knimeService.clearWarningMessage(CATEGORY_MISSING_VALUES_ONLY);
		}
		
		if (excludeBars.length > 0) {
			knimeService.setWarningMessage("Following bars contain only missing values in frequency column and were excluded from the view:\n    " + excludeBars.join("\n    "), MISSING_VALUES_ONLY);
		} else {
			knimeService.clearWarningMessage(MISSING_VALUES_ONLY);
		}
	}
	
	fixStackedData = function(addDummy) {
		plotData.forEach(function(dataValues) {
			var excludeCats = excludeDataCatMap[dataValues.key];
			if (excludeCats == undefined) {
				return;
			}
			if (addDummy) {
				var i = 0, j = 0;
				var values = dataValues.values;
				var newValues = [];
				categories.forEach(function(cat) {
					if (cat == null) {
						return;
					}
					if (i < values.length && values[i].x == cat) {
						newValues.push(values[i]);
						i++;
					} else if (j < excludeCats.length && excludeCats[j] == cat) {
						newValues.push({"x": cat, "y": null});
						j++;
					}
				});
				if (i < values.length && values[i].x == MISSING_VALUES_LABEL) {
					newValues.push(values[i]);
				} else if (j < excludeCats.length && excludeCats[j] == null) {
					newValues.push({"x": MISSING_VALUES_LABEL, "y": null});					
				}
				dataValues.values = newValues;
			} else {
				dataValues.values = dataValues.values.filter(function(value) {
					return value.y !== null;
				});
			}
		});
	}
	
	function updateTitles(updateChart) {
		if (chart) {
			var curTitle = d3.select("#title");
			var curSubtitle = d3.select("#subtitle");
			var chartNeedsUpdating = curTitle.empty() != !(_value.options.title) 
				|| curSubtitle.empty() != !(_value.options.subtitle);
			if (!_value.options.title) {
				curTitle.remove();
			}
			if (_value.options.title) {
				if (curTitle.empty()) {
					svg.append("text")
						.attr("x", 20)             
						.attr("y", 30)
						.attr("font-size", 24)
						.attr("id", "title")
						.text(_value.options.title);
				} else {
					curTitle.text(_value.options.title);
				}
			}
			if (!_value.options.subtitle) {
				curSubtitle.remove();
			} 
			if (_value.options.subtitle) {
				if (curSubtitle.empty()) {
					svg.append("text")
						.attr("x", 20)             
						.attr("y", _value.options.title ? 46 : 20)
						.attr("font-size", 12)
						.attr("id", "subtitle")
						.text(_value.options.subtitle);
				} else {
					curSubtitle.text(_value.options.subtitle)
					.attr("y", _value.options.title ? 46 : 20);
				}
			}
			
			var topMargin = 10;
			topMargin += _value.options.title ? 10 : 0;
			topMargin += _value.options.subtitle ? 8 : 0;
			chart.legend.margin({top: topMargin, bottom: topMargin});
			
			var isTitle = _value.options.title || _value.options.subtitle;
			knimeService.floatingHeader(isTitle);
			
			if (updateChart && chartNeedsUpdating) {
				if (_representation.options.svg.fullscreen && _representation.runningInView ) {
					var height = (isTitle) ? "100%" : "calc(100% - " + knimeService.headerHeight() + "px)";
					layoutContainer.style("height", height)
						// two rows below force to invalidate the container which solves a weird problem with vertical scroll bar in IE
						.style('display', 'none')
						.style('display', 'block');
					d3.select("#svgContainer").style("height", height); 
				}
				chart.update();
			}
		}
	}
	
	function updateAxisLabels(updateChart) {
		if (chart) {
			var optOrientation = _value.options["orientation"];
			var optStaggerLabels = _value.options["staggerLabels"];
			var curCatAxisLabel, curFreqAxisLabel;
			var curCatAxisLabelElement = d3.select(".nv-x.nv-axis .nv-axis-label");
			var curFreqAxisLabelElement = d3.select(".nv-y.nv-axis .nv-axis-label");
			if (!curCatAxisLabelElement.empty()) {
				curCatAxisLabel = curCatAxisLabelElement.text();
			}
			if (!curFreqAxisLabelElement.empty()) {
				curFreqAxisLabel = curCatAxisLabelElement.text();
			}
			var chartNeedsUpdating = curCatAxisLabel != _value.options.catLabel
				|| curFreqAxisLabel != _value.options.freqLabel;
			if (!chartNeedsUpdating) return;
			
			chart.xAxis
				.axisLabel(_value.options.catLabel)
				.axisLabelDistance(optOrientation ? 30 : optStaggerLabels ? 10 : -5)
				.showMaxMin(false);

			// tick format needed?
			chart.yAxis
				.axisLabel(_value.options.freqLabel)
				.axisLabelDistance(optOrientation ? -5 : 0)
				/*.tickFormat(d3.format(',.01f'))*/;
			
			var leftMargin = optOrientation ? 100 : 70;
			var bottomMargin = 35;
			if (!_value.options.catLabel) {
				bottomMargin = optOrientation ? bottomMargin : 25;
				leftMargin = optOrientation ? 70 : leftMargin;
			}
			if (!_value.options.freqLabel) {
				bottomMargin = optOrientation ? 25 : bottomMargin;
				leftMargin = optOrientation ? leftMargin : 50;
			}
			if (!optOrientation) {
				chart.staggerLabels(optStaggerLabels);
				if (optStaggerLabels) {
					bottomMargin += _value.options.catLabel ? 25 : 15;
				}
			}
			
			chart.margin({left: leftMargin, bottom: bottomMargin})
			
			if (updateChart) {
				chart.update();
			}
		}
	}
	
	function updateChartType() {
		if (this.value != _value.options.chartType) {
			_value.options.chartType = this.value;
			var stacked = this.value == 'Stacked';
			fixStackedData(stacked);
			chart.stacked(stacked);									
			chart.update();
		}		
	}
	
	drawControls = function() {		
		if (!knimeService) {
			// TODO: error handling?
			return;
		}
		
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
	    if (!_representation.options.enableViewControls) return;
	    
	    var titleEdit = _representation.options.enableTitleEdit;
	    var subtitleEdit = _representation.options.enableSubtitleEdit;
	    var axisEdit = _representation.options.enableAxisEdit;
	    var chartTypeEdit =  _representation.options.enableStackedEdit;
	    var orientationEdit = _representation.options.enableHorizontalToggle;
		var staggerLabels = _representation.options.enableStaggerToggle;
		var switchMissValCat = _representation.options.enableSwitchMissValCat;
	    
	    if (titleEdit || subtitleEdit) {	    	    
	    	if (titleEdit) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.options.title, function() {
	    			if (_value.options.title != this.value) {
						_value.options.title = this.value;
						updateTitles(true);
					}
	    		}, true);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
	    	}
	    	if (subtitleEdit) {
	    		var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.options.subtitle, function() {
	    			if (_value.options.subtitle != this.value) {
						_value.options.subtitle = this.value;
						updateTitles(true);
					}
	    		}, true);
	    		var mi = knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
	    	}	
	    	if (axisEdit || orientationEdit || staggerLabels) {
	    		knimeService.addMenuDivider();
	    	}
	    }
	    
	    if (axisEdit) {
	    	var catAxisText = knimeService.createMenuTextField('catAxisText', _value.options.catLabel, function() {
	    		_value.options.catLabel = this.value;
				updateAxisLabels(true);
	    	}, true);
    		knimeService.addMenuItem('Category axis label:', 'ellipsis-h', catAxisText);
    		
    		var freqAxisText = knimeService.createMenuTextField('freqAxisText', _value.options.freqLabel, function() {
    			_value.options.freqLabel = this.value;
				updateAxisLabels(true);
    		}, true);    		
    		knimeService.addMenuItem('Frequency axis label:', 'ellipsis-v', freqAxisText);
    		
    		if (switchMissValCat || orientationEdit || staggerLabels || chartTypeEdit) {
    			knimeService.addMenuDivider();
    		}
    	}
	    
	    if (switchMissValCat /*&& missValCatValue !== undefined*/) {
	    	var switchMissValCatCbx = knimeService.createMenuCheckbox('switchMissValCatCbx', _value.options.includeMissValCat, function() {
	    		if (_value.options.includeMissValCat != this.checked) {
	    			_value.options.includeMissValCat = this.checked;
	    			var stacked = _value.options.chartType == 'Stacked';
	    			if (stacked) {
	    				fixStackedData(false);
	    			}
	    			processMissingValues(true);
	    			if (stacked) {
	    				fixStackedData(true);
	    			}
	    			chart.update();
	    		}
	    	});
	    	knimeService.addMenuItem("Include 'Missing values' category: ", 'question', switchMissValCatCbx);
	    	
	    	if (orientationEdit || staggerLabels || chartTypeEdit) {
	    		knimeService.addMenuDivider();
	    	}
	    }
	    
	    if (chartTypeEdit) {
	    	var groupedRadio = knimeService.createMenuRadioButton('groupedRadio', 'chartType', 'Grouped', updateChartType);	    	
	    	groupedRadio.checked = (_value.options.chartType == groupedRadio.value);
	    	knimeService.addMenuItem('Grouped:', 'align-left fa-rotate-270', groupedRadio);	    	
	    	
	    	var stackedRadio = knimeService.createMenuRadioButton('stackedRadio', 'chartType', 'Stacked', updateChartType);
	    	stackedRadio.checked = (_value.options.chartType == stackedRadio.value);
	    	knimeService.addMenuItem('Stacked:', 'tasks fa-rotate-270', stackedRadio);
	    	
	    	if (orientationEdit || staggerLabels) {
    			knimeService.addMenuDivider();
    		}
	    }
	    
	    if (orientationEdit) {
	    	var orientationCbx = knimeService.createMenuCheckbox('orientationCbx', _value.options.orientation, function () {
	    		if (_value.options.orientation != this.checked) {
					_value.options.orientation = this.checked;
					d3.select("#staggerCbx").property("disabled", this.checked);
					drawChart(true);
				}
	    	});
	    	knimeService.addMenuItem('Plot horizontal bar chart:', 'align-left', orientationCbx);
	    }
	    
	    if (staggerLabels) {
	    	var staggerCbx = knimeService.createMenuCheckbox('staggerCbx', _value.options.staggerLabels, function () {
    			if (_value.options.staggerLabels != this.checked) {
					_value.options.staggerLabels = this.checked;
					drawChart(true);
				}
	    	});
	    	staggerCbx.disabled = _value.options.orientation;
	    	knimeService.addMenuItem('Stagger labels:', 'map-o', staggerCbx);
	    }
	};

	/*function createControls(controlsContainer) {
		if (_representation.options.enableViewControls) {
			
			var controlTable = controlsContainer.append("table")
	    		.attr("id", "barControls")
	    		.style("padding", "10px")
	    		.style("margin", "0 auto")
	    		.style("box-sizing", "border-box")
	    		.style("font-family", "sans-serif")
	    		.style("font-size", "12px")
	    		.style("border-spacing", 0)
	    		.style("border-collapse", "collapse");
			
			var orientationEdit = _representation.options.enableHorizontalToggle;
			var staggerLabels = _representation.options.enableStaggerToggle;
			var categoryEdit = _representation.options.enableCategoryChooser;
			
			if (orientationEdit || staggerLabels) {
				var orientationContainer = controlTable.append("tr");
				if (orientationEdit) {
					orientationContainer.append("td").append("label").attr("for", "orientation").text("Plot horizontal bar chart:").style("margin", "0 5px");
		    		var orientationCheckbox = orientationContainer.append("td").append("input")
	    				.attr("type", "checkbox")
	    				.attr("id", "orientation")
	    				.style("margin-right", "15px")
	    				.property("checked", _value.options.orientation)
	    				.on("click", function() {
	    					if (_value.options["orientation"] != this.checked) {
	    						_value.options["orientation"] = this.checked;
	    						d3.select("#stagger").property("disabled", this.checked);
	    						drawChart(true);
	    					}
	    				});
				}
				if (staggerLabels) {
					orientationContainer.append("td").append("label").attr("for", "stagger").text("Stagger labels:").style("margin", "0 5px");
		    		var staggerCheckbox = orientationContainer.append("td").append("input")
	    				.attr("type", "checkbox")
	    				.attr("id", "stagger")
	    				.style("margin-right", "15px")
	    				.property("checked", _value.options.staggerLabels)
	    				.property("disabled", _value.options["orientation"])
	    				.on("click", function() {
	    					if (_value.options.staggerLabels != this.checked) {
	    						_value.options.staggerLabels = this.checked;
	    						drawChart(true);
	    					}
	    				});
				}
				
			}
			
			var titleEdit = _representation.options.enableTitleEdit;
			var subtitleEdit = _representation.options.enableSubtitleEdit;
			
			if (titleEdit || subtitleEdit) {
				var titleEditContainer = controlTable.append("tr");
		    	if (titleEdit) {
		    		titleEditContainer.append("td").append("label").attr("for", "chartTitleText").text("Chart Title:").style("margin", "0 5px");
		    		var chartTitleText = titleEditContainer.append("td").append("input")
	    				.attr("type", "text")
	    				.attr("id", "chartTitleText")
	    				.attr("name", "chartTitleText")
	    				.attr("value", _value.options.title)
	    				.style("font-family", "sans-serif")
	    				.style("font-size", "12px")
	    				.style("width", "150px")
	    				.style("margin-right", "15px")
	    				.on("keyup", function() {
	    					if (_value.options.title != this.value) {
	    						_value.options.title = this.value;
	    						updateTitles(true);
	    					}
	    			});
		    	}
		    	if (subtitleEdit) {
		    		titleEditContainer.append("td").append("label").attr("for", "chartSubtitleText").text("Chart Subtitle:").style("margin", "0 5px");
		    		var chartTitleText = titleEditContainer.append("td").append("input")
	    				.attr("type", "text")
	    				.attr("id", "chartSubtitleText")
	    				.attr("name", "chartSubtitleText")
	    				.attr("value", _value.options.subtitle)
	    				.style("font-family", "sans-serif")
	    				.style("font-size", "12px")
	    				.style("width", "150px")
	    				.style("margin-right", "15px")
	    				.on("keyup", function() {
	    					if (_value.options.subtitle != this.value) {
	    						_value.options.subtitle = this.value;
	    						updateTitles(true);
	    					}
	    			});
		    	}
			}
			
			if (_representation.options.enableAxisEdit) {
				var axisContainer = controlTable.append("tr");
				
				axisContainer.append("td").append("label").attr("for", "catAxisLabel").text("fre:").style("margin", "0 5px");
				var categoryBox = axisContainer.append("td").append("input")
					.attr("id", "catAxisLabel")
					.attr("type", "text")
					.attr("value", _value.options.catLabel)
					.style("width", "150px")
					.style("margin-right", "15px")
					.on("keyup", function() {
						_value.options.catLabel = this.value;
						updateAxisLabels(true);
					});
				
				axisContainer.append("td").append("label").attr("for", "freqAxisLabel").text("Frequency axis label:").style("margin", "0 5px");
				var categoryBox = axisContainer.append("td").append("input")
					.attr("id", "freqAxisLabel")
					.attr("type", "text")
					.attr("value", _value.options.freqLabel)
					.style("width", "150px")
					.style("margin-right", "15px")
					.on("keyup", function() {
						_value.options.freqLabel = this.value;
						updateAxisLabels(true);
					});
					
			}
				
			if (d3.selectAll("#controlContainer table *").empty()) {
				controlContainer.remove();
			}
		}
	}*/

	barchart.validate = function() {
		return true;
	}

	barchart.getComponentValue = function() {
		return _value;
	}

	barchart.getSVG = function() {
		// inline global style declarations for SVG export
		var styles = document.styleSheets;
		for (i = 0; i < styles.length; i++) {
			if (!styles[i].cssRules && styles[i].rules) {
				styles[i].cssRules = styles[i].rules;
			}
			// empty style declaration
			if (!styles[i].cssRules) continue;
			
			for (var j = 0; j < styles[i].cssRules.length; j++) {
				try {
					var rule = styles[i].cssRules[j];
					d3.selectAll(rule.selectorText).each(function(){
						for (var k = 0; k < rule.style.length; k++) {
							var curStyle = this.style.getPropertyValue(rule.style[k]);
							var curPrio = this.style.getPropertyPriority(rule.style[k]);
							var rulePrio = rule.style.getPropertyPriority(rule.style[k]);
							//only overwrite style if not set or priority is overruled
							if (!curStyle || (curPrio != "important" && rulePrio == "important")) {
								d3.select(this).style(rule.style[k], rule.style[rule.style[k]]);
							}
						}
					});
				} catch(exception) {
					continue;
				}
			}
		}
		var svgElement = d3.select("svg")[0][0];
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return barchart;

}());