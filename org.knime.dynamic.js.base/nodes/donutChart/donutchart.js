(pie_chart_namespace = function() {

	var pie = {};
	var _representation, _value;
	var layoutContainer;
	var MIN_HEIGHT = 100, MIN_WIDTH = 100;
	var chart, svg;
	var knimeTable;
	
	var plotData;
	var colorRange;
	var excludeCat;
	var missValCatValue;
    var _translator;
    var _keyNameMap;
	
	var showWarnings;
	
	var MISSING_VALUES_ONLY = "missingValuesOnly";
	var NO_DATA_AVAILABLE = "noDataAvailable";

	pie.init = function(representation, value) {
		_representation = representation;
		_value = value;
		_value.options['selection'] = _value.options['selection'] || [];
		
		if(_representation.inObjects[0].translator) {
        	_translator = _representation.inObjects[0].translator;
        	_translator.sourceID = _representation.inObjects[0].uuid;
        	_translator.targetIDs = [_representation.tableIds[0]];
        	knimeService.registerSelectionTranslator(_translator, _translator.sourceID);
        	subscribeToSelection(_value.options.subscribeToSelection);
        	_keyNameMap = new KeyNameMap(getClusterToRowMapping());
        }
		
		showWarnings = _representation.options.showWarnings;
		
		if (_representation.warnMessage && showWarnings) {
			knimeService.setWarningMessage(_representation.warnMessage);
		}

		drawChart(false);
		if (_representation.options.enableViewControls) {
			drawControls();
		}
	}

	function drawChart(redraw) {		
		// Parse the options
		var optTitle = _value.options["title"];
		var optSubtitle = _value.options["subtitle"];

		var showLabels = _value.options["showLabels"];
		var labelThreshold = _representation.options["labelThreshold"];
		var labelType = _value.options["labelType"].toLowerCase();
				
		var optDonutChart = _value.options["togglePie"];
		var holeSize = _value.options["holeSize"];
		var optInsideTitle = _value.options["insideTitle"];

		var showLegend = _representation.options["legend"];

		var optFullscreen = _representation.options["svg"]["fullscreen"] && _representation.runningInView;
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]
		
		var isTitle = optTitle || optSubtitle;

		/*
		 * Setup interactive controls
		 */
		
		d3.select("html").style("width", "100%").style("height", "100%");
        d3.select("body").style("width", "100%").style("height", "100%");

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
				.attr('class', 'knime-layout-container')				
				.style("width", width)
				.style("height", height)
				.style("min-width", MIN_WIDTH + "px")
				.style("min-height", MIN_HEIGHT + "px");		
			
			div = layoutContainer.append("div")
				.attr("id", "svgContainer")
				.attr('class', 'knime-svg-container')
				.style("min-width", MIN_WIDTH + "px")
				.style("min-height", MIN_HEIGHT + "px");
		}

		/*
		 * Process data
		 */
		knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0].table;
		knimeTable.setDataTable(port0dataTable);
		
		processData(true);	
		setColorRange();
		
		// Create the SVG object
		var svg1 = document
				.createElementNS('http://www.w3.org/2000/svg', 'svg');
		div[0][0].appendChild(svg1);

		svg = d3.select("svg")
			.style("display", "block");

		if (!optFullscreen) {
			if (optWidth > 0) {
				div.style("width", optWidth + "px")
				svg.attr("width", optWidth);
			}
			if (optHeight > 0) {
				svg.attr("height", optHeight);
				div.style("height", optHeight + "px");
			}
		} else {
			// Set full screen height/width
			div.style("width", "100%");
			div.style("height", height);

			svg.attr("width", "100%");
			svg.attr("height", "100%");
		}
	
		// Pie chart
		nv.addGraph(function() {
			chart = nv.models.pieChart()
				.x(function(d) { return d.label })
				.y(function(d) { return d.value })
				.color(colorRange)
				.duration(0)
				.showLegend(showLegend)
				.showLabels(showLabels)
				.labelThreshold(labelThreshold) 
				.labelType(labelType); // "key", "value" or "percent"

			chart.dispatch.on('renderEnd.css', function() {
				setCssClasses();
				redrawSelection();
			});
			// tooltip is re-created every time therefore we need to assign classes accordingly
			chart.pie.dispatch.on('elementMouseover.tooltipCss', setTooltipCssClasses);
			chart.pie.dispatch.on('elementMousemove.tooltipCss', setTooltipCssClasses);
			chart.legend.dispatch.on('legendClick', function(series, index) {
            	//drawChart(true);
            	removeHilightBar("",true);
            	d3.event.stopPropagation();
            });
			
			chart.width(optFullscreen ? "100%" : optWidth);
			chart.height(optFullscreen ? "100%" : optHeight);
			chart.margin({top: 0, bottom: 0, left: 20, right: 0});

			// TODO: Add a mechanism to remember the categories that are
			// switched on.

			chart.donut(optDonutChart);
			chart.donutRatio(holeSize);
			if (optInsideTitle) {
				chart.title(optInsideTitle);
			}
			updateTitles(false);

			// checking if all the pies are 0s
			if (plotData.filter(function(d) { return d.value !== 0 }).length === 0) {
				svg.append('text')
					.attr('x', 20)
					.attr('y', 80)
					.attr('font-size', 20)
					.attr('fill', 'red')
					.text('The plot is empty because all values are equal to 0.');
			} else {
				svg.datum(plotData).transition().duration(0).call(chart);
			}
			//nv.utils.windowResize(chart.update);
			nv.utils.windowResize(function () { 
				chart.update(); 
				removeHilightBar("",true); 
				redrawSelection();
			});
			
			// redraws selection
            redrawSelection();

			return chart;
		});
	}
	
	 function registerClickHandler () {
	    	d3.selectAll(".nv-slice").on('click',function(event) {
				handleHighlightClick(event);
				d3.event.stopPropagation();
	    	});
	    }
	    
	    function redrawSelection() {
	    	for(var i = 0; i < _value.options['selection'].length; i++) {
	    		createHilightBar(_keyNameMap.getNameFromKey(_value.options['selection'][i][0]), 
	    				_value.options['selection'][i][1]);
	    	}
	    }
	    
	    function subscribeToSelection(subscribeBool) {
	    	if(_representation.options.enableSelection) {
	    		if(subscribeBool) {
	    			knimeService.subscribeToSelection(_translator.sourceID, onSelectionChanged);
	    		} else {
	    			knimeService.unsubscribeSelection(_translator.sourceID, onSelectionChanged);
	    		}
	    	}
	    }
	    
	    function publishSelection(shouldPublish){
	    	if(shouldPublish) {
	    		knimeService.setSelectedRows(_translator.sourceID, getSelectedRowIDs(), _translator.sourceID);
	    	}
	    }
	    
		function checkClearSelectionButton(){
			var button = d3.select("#clearSelectionButton");
			if (button){
				button.classed("inactive", function(){return !_value.options['selection'].length > 0});
			}
		}
	    
	    function getSelectedRowIDs() {
	    	var selectedRowIDs = [];
	    	for (var i = 0; i< _value.options['selection'].length; i++) {
	    		selectedRowIDs.push( _value.options['selection'][i][0]);
	    	}
	    	return selectedRowIDs;
	    }
	    
	    // Removes the clusterName with the given cluster name. If "removeAll" is true all bars are removed
	    function removeHilightBar(clusterName, removeAll) {
	    	if(removeAll) {
	    		var length = _value.options['selection'].length;
		  		for(var i = 0; i < length; i++) {
		  			let selectedEntry = _value.options['selection'][i];
		  			d3.selectAll(".hilightBar_" + _keyNameMap.getNameFromKey(selectedEntry[0])).remove();
		  		}
	    	} else {
		    	var barIndex = getSelectedRowIDs().indexOf(_keyNameMap.getKeyFromName(clusterName));
		    	if(barIndex > -1) {
				  	d3.selectAll(".hilightBar_" + clusterName).remove();
		    	}
	    	}
	    } 
	    
	    // Create a hilight-bar above the cluster with the given name and assigns the given css class to it
	    function createHilightBar (clusterName, selectionClass) {
	    	var optOrientation = _value.options['orientation'];
		  	for(var j = 0; j < _representation.inObjects[0].table.rows.length; j++) {
		  		if(_representation.inObjects[0].table.rows[j].data[0] === clusterName) {
		  			var slices = d3.selectAll(".nv-slice");
		  			for(var i = 0; i < slices[0].length; i++) {
		  				if(i==j) {
		  					var slice = d3.select(slices[0][i]).select("path");
		  					var availableWidth = chart.width() - 20;
		  					var availableHeight = chart.height();
		  					var radius = Math.min(availableWidth, availableHeight) / 2;
		  	                var donutRatio = chart.donutRatio();
		  					var selectionTitle;
		  					if(selectionClass == "knime-selected") {
		  						selectionTitle = "Selected";
		  					} else {
		  						selectionTitle = "Partially selected";
		  					}
		  	                console.log(donutRatio,chart.margin().top, chart.margin().bottom, "donutChart");
		  					//PieChart Code
		  					var arc = d3.svg.arc()
			  				    .innerRadius((radius - radius / 5))
			  				    .outerRadius((radius - radius / 5)+8)
			  				    .startAngle(slice.data()[0].startAngle) //converting from degs to radians
			  				    .endAngle(slice.data()[0].endAngle); //just radians

			  				slice.select(function() { return this.parentNode; }).append("path")
			  				    .attr("d", arc)
			  				    .classed("hilightBar_" + clusterName,true)
			  				    .classed(selectionClass, true)
		    	  				.append("title")
		    	  				.classed('knime-tooltip', true)
		    	  				.text(selectionTitle);
		  				} 
		  			}
		  		}
		  	}
	    }
	    
	    function getClusterToRowMapping() {
	    	var map = {};
	    	for (var i = 0; i < _representation.inObjects[0].table.rows.length; i++) {
	    		map[_representation.inObjects[0].table.rows[i].data[0]] = _representation.inObjects[0].table.rows[i].rowKey;
	    	}
	    	return map;
	    }
	    
	    // Helper class to handle conversion from cluster name to row key
	    class KeyNameMap{
	    	constructor(map){
			   this.map = map;
			   this.reverseMap = {};
			   for(var key in map){
			      var value = map[key];
			      this.reverseMap[value] = key;   
			   }
	    	}
			getKeyFromName(name){ 
				return this.map[name]; 
			};
			getNameFromKey(key){
				return this.reverseMap[key];
			};
	    }
	    
	    function handleHighlightClick(event) {
	    	console.log(event);
	    	var clusterName = event.data.label;
	    	var clusterKey = _keyNameMap.getKeyFromName(clusterName);
	    	var barIndex = getSelectedRowIDs().indexOf(clusterKey);
	    	// Deselect already selected bar when clicking again on it
	    	if((barIndex > -1 && (!d3.event.ctrlKey && !d3.event.shiftKey && !d3.event.metaKey) 
	    			&& _value.options['selection'].length == 1)
	    			|| (barIndex > -1 && (d3.event.ctrlKey || d3.event.shiftKey || d3.event.metaKey))){
	    		if(_representation.options.enableSelection) {
	        		if(_value.options.publishSelection) {
	        			knimeService.removeRowsFromSelection(_translator.sourceID,[clusterKey], _translator.sourceID);
	        		}
	    		}
				removeHilightBar(clusterName, false);
				_value.options['selection'].splice(barIndex, 1);
	    	} else if(!d3.event.ctrlKey && !d3.event.shiftKey && !d3.event.metaKey) {
	    		// Deselect all previously selected bars and select the newly clicked one
	    		if(_representation.options.enableSelection) {
	        		if(_value.options.publishSelection) {
	        			knimeService.setSelectedRows(_translator.sourceID,[clusterKey], _translator.sourceID);
	        		}
	    		}
				removeHilightBar(clusterName, true);
				_value.options['selection']= [];
	    		createHilightBar(clusterName, "knime-selected");
	    		_value.options['selection'].push([clusterKey, "knime-selected"]);
	    	} else {
	    		// Select the clicked bar, as it is either a new selection or a additional selection
	    		if(_representation.options.enableSelection) {
	        		if(_value.options.publishSelection) {
	        			knimeService.addRowsToSelection(_translator.sourceID,[clusterKey], _translator.sourceID);
	        		}
	    		}
	    		createHilightBar(clusterName, "knime-selected");
	    		_value.options['selection'].push([clusterKey, "knime-selected"]);
	    	}
	    	checkClearSelectionButton();
	    }
	    
	    function onSelectionChanged(data) {
	    	if (data.reevaluate) {
	    		removeHilightBar("", true);
	    		var selectedRows = knimeService.getAllRowsForSelection(_translator.sourceID);
	    		var partiallySelectedRows = knimeService.getAllPartiallySelectedRows(_translator.sourceID);
	    		_value.options['selection'] = [];
	    		for (let selectedRow in selectedRows) {
	    			let length = _value.options['selection'].length;
	    			_value.options['selection'][length] = [selectedRows[selectedRow], "knime-selected"];
	    			createHilightBar(_keyNameMap.getNameFromKey(selectedRows[selectedRow]),
	    					"knime-selected");
	    		}
	    		for (let partiallySelectedRow in partiallySelectedRows) {
	    			let length = _value.options['selection'].length;
	    			_value.options['selection'][length] = [partiallySelectedRows[partiallySelectedRow], "knime-partially-selected"];
	    			createHilightBar(_keyNameMap.getNameFromKey(partiallySelectedRows[partiallySelectedRow]),
	    					"knime-partially-selected");
	    		}
	        } else if (data.changeSet) {
	        	if (data.changeSet.removed) {
	        		data.changeSet.removed.map(function(rowId) {
	        			var clusterName = rowId;
	        			var index = getSelectedRowIDs().indexOf(clusterName);
	        			if (index > -1) {
	        				removeHilightBar(_keyNameMap.getNameFromKey(rowId), false);
	        				_value.options['selection'].splice(index, 1);
	        			}
	        		});
	        	}
	        	if(data.changeSet.partialRemoved) {
	        		data.changeSet.partialRemoved.map(function(rowId) {
	        			var clusterName = rowId;
	        			var index = getSelectedRowIDs().indexOf(clusterName);
	        			if (index > -1) {
	        				removeHilightBar(_keyNameMap.getNameFromKey(rowId), false);
	        				_value.options['selection'].splice(index, 1);
	        			}
	        		});
	        	}
		        if (data.changeSet.added) {
		            data.changeSet.added.map(function(rowId) {
		                var index = getSelectedRowIDs().indexOf(rowId);
		                if (index === -1) {
		                	_value.options['selection'].push([rowId, "knime-selected"]);
		                	createHilightBar(_keyNameMap.getNameFromKey(rowId), "knime-selected");
		                }
		            });
		        }
		        if(data.changeSet.partialAdded) {
		        	data.changeSet.partialAdded.map(function(rowId) {
		                var index = getSelectedRowIDs().indexOf(rowId);
		                if (index === -1) {
		                	_value.options['selection'].push([rowId, "knime-partially-selected"]);
		                    createHilightBar(_keyNameMap.getNameFromKey(rowId), "knime-partially-selected");
		                }
		            });
		        }
		     }
	    	checkClearSelectionButton();
	    }

	
	function updateData(updateChart) {
		processData();
		if (updateChart) {
			chart.update();
		}
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
						.attr("id", "title")
						.attr('class', 'knime-title')
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
						.attr("id", "subtitle")
						.attr('class', 'knime-subtitle')
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
			chart.margin({top: topMargin, bottom: topMargin});
			
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
	
	processData = function(setColorRange) {
		var optMethod = _representation.options["aggr"];
		var optCat = _representation.options["cat"];
		var optFreqCol = _value.options["freq"];
		
		var categories = knimeTable.getColumn(optCat);
		
		var valCol;
		if (optMethod == "Occurence\u00A0Count") {
			valCol = knimeTable.getColumn(1);
		} else {
			valCol = knimeTable.getColumn(optFreqCol);
		}
		
		plotData = [];
		excludeCat = [];
		missValCatValue = undefined;
		if (valCol.length > 0) {
			var numDataPoints = valCol.length;
			for (var i = 0; i < numDataPoints; i++) {
				var label = categories[i];
				var value = valCol[i];
				
				if (label === null) {
					// missing values category					
					// save the value to append as the last item						
					missValCatValue = value;					
					continue;
				}
				
				if (value === null) {
					// category has only missing values - exclude it
					excludeCat.push(label);					
					continue;
				}
				
				var plotStream = {
					"label" : label,
					"value" : Math.abs(value)  // take abs value to prevent a damaged plot
				};				
				plotData.push(plotStream);
			}
		}
		
		processMissingValues(false);
	}
	
	setColorRange = function() {
		var numCat = plotData.length;
		if (missValCatValue !== undefined && missValCatValue !== null) {
			// We don't want the option "includeMissValCat" to influence on the number of categories,
			// because the option can be changed in the view and the color scale then can also be changed (if a border case) - and we don't want this.
			// Hence, only the real value matters.
			numCat++;
		}
		if (_representation.options.customColors) {
			colorRange = [];
			for (var i = 0; i < numCat; i++) {
				var color = knimeTable.getRowColors()[i];
				if (!color) {
					color = "#7C7C7C";
				}
				colorRange.push(color);
			}
		} else {
			var colorScale;
			if (numCat > 10) {
				colorScale = d3.scale.category20();
			} else {
				colorScale = d3.scale.category10();
			}
			colorRange = colorScale.range();
		}
	}
	
	/**
	 * switched - if the chart update was triggered by changing the "include 'Missing values' category" option in the view
	 */
	processMissingValues = function(switched) {
		// Missing values post-processing	
		if (missValCatValue !== undefined) {  // undefined means there's no missing value in the category column at all
			if (_value.options.includeMissValCat && _representation.options.reportOnMissingValues) {
				// add missing values category
				var label = "Missing values";
				if (missValCatValue !== null) {
					plotData.push({"label": label, "value": missValCatValue});
				} else {
					excludeCat.push(label);
				}
			} else if (switched) {
				// remove missing values category, but only if we have triggered switch from the view
				// otherwise there's nothing to remove yet
				if (missValCatValue !== null) {
					plotData.pop();
				} else {
					excludeCat.pop();
				}
			}
		}

		// Set warning messages
		if (!showWarnings) {
			return;
		}
		if (plotData.length == 0) {
			// No data available warnings
			var str;
			if (missValCatValue !== undefined && missValCatValue !== null && _representation.options.reportOnMissingValues) {
				str = "No chart was generated since the frequency column has only missing values.\nThere are values where the category name is missing.\nTo see them switch on the option \"Include 'Missing values' category\" in the view settings.";
			} else {
				str = "No chart was generated since the frequency column has only missing values or empty.\nRe-run the workflow with different data.";
			}
			knimeService.setWarningMessage(str, NO_DATA_AVAILABLE);
		} else if (excludeCat.length > 0 && _representation.options.reportOnMissingValues) {
			knimeService.setWarningMessage("Categories '" + excludeCat.join("', '") + "' have only missing values in the frequency column and were excluded from the view.", MISSING_VALUES_ONLY)
		} else {
			knimeService.clearWarningMessage(MISSING_VALUES_ONLY);
		}	
	}
		
	drawControls = function() {		
		if (!knimeService) {
			// TODO: error handling?
			return;
		}
		
		if (_representation.options.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
	    if (!_representation.options.enableViewControls) return;
	    
	    var titleEdit = _representation.options.enableTitleEdit;
		var subtitleEdit = _representation.options.enableSubtitleEdit;
		var donutToggle = _representation.options.enableDonutToggle;
		var holeEdit = _representation.options.enableHoleEdit;
		//var insideTitleEdit = _representation.options.enableInsideTitleEdit;
		//var colChooser = _representation.options.enableColumnChooser;
		var labelEdit = _representation.options.enableLabelEdit;
		var switchMissValCat = _representation.options.enableSwitchMissValCat;
		var enableSelection = _representation.options.enableSelection;
		var disableClearButton = _representation.options.disableClearButton;
	    
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
	    	if (/*colChooser ||*/ labelEdit || donutToggle || holeEdit /*|| insideTitleEdit*/) {
	    		knimeService.addMenuDivider();
	    	}
	    }
	    
	    /*if (colChooser) {
	    	// filter out non number columns
	    	var colNames = _representation.inObjects[0].spec.colNames;
			var colTypes = _representation.inObjects[0].spec.colTypes;
			var numberColumns = [];
			for (var i = 0; i < colNames.length; i++) {
				if (colTypes[i] == "number") {
					numberColumns.push(colNames[i]);					
				}
			}
    		var colSelect = knimeService.createMenuSelect('columnSelect', _value.options.freq, numberColumns, function() {
    			_value.options.freq = this.value;
				updateData(true);
    		});
    		knimeService.addMenuItem('Column:', 'minus-square fa-rotate-90', colSelect);
    		
    		if (labelEdit || donutToggle || holeEdit || insideTitleEdit) {
	    		knimeService.addMenuDivider();
	    	}
        }*/
	    
	    if (labelEdit) {
	    	var labelCbx = knimeService.createMenuCheckbox('labelCbx', _value.options.showLabels, function () {
	    		if (_value.options.showLabels != this.checked) {
					_value.options.showLabels = this.checked;
					chart.showLabels(this.checked);
					d3.selectAll("#labelType input").property("disabled", !_value.options.showLabels);
					//workaround for nvd3 bug, remove labels manually
					if (!this.checked) {
						d3.selectAll(".nv-pieLabels *").remove();
					}
					chart.update();
				}
	    	});
	    	knimeService.addMenuItem('Show labels:', 'comment-o', labelCbx);
	    	
	    	var labelTypeRadio = knimeService.createInlineMenuRadioButtons('labelType', 'labelType', 'Value', ['Key', 'Value', 'Percent'], function() {
	    		_value.options.labelType = this.value;
				chart.labelType(this.value.toLowerCase());
				chart.update();
	    	});
	    	knimeService.addMenuItem('Label type:', 'commenting-o', labelTypeRadio);
	    	
		    if (switchMissValCat || donutToggle || holeEdit || insideTitleEdit) {
	    		knimeService.addMenuDivider();
	    	}
	    }
	    
	    if (switchMissValCat && missValCatValue !== undefined && _representation.options.reportOnMissingValues) {
	    	var switchMissValCatCbx = knimeService.createMenuCheckbox('switchMissValCatCbx', _value.options.includeMissValCat, function() {
	    		if (_value.options.includeMissValCat != this.checked) {
	    			_value.options.includeMissValCat = this.checked;
	    			processMissingValues(true);
	    			chart.update();
	    		}
	    	});
	    	knimeService.addMenuItem("Include 'Missing values' category: ", 'question', switchMissValCatCbx);
	    	
	    	if (donutToggle || holeEdit || insideTitleEdit) {
	    		knimeService.addMenuDivider();
	    	}
	    }
	    
	    if (donutToggle || holeEdit /*|| insideTitleEdit*/) {
	    	if (donutToggle) {
		    	var donutCbx = knimeService.createMenuCheckbox('donutCbx', _value.options.togglePie, function () {
		    		if (_value.options.togglePie != this.checked) {
						_value.options.togglePie = this.checked;
						chart.donut(this.checked);
						d3.selectAll("#insideTitleText, #holeRatioText").property("disabled", !_value.options.togglePie);
						chart.update();
					}
		    	});
		    	knimeService.addMenuItem('Render donut chart:', knimeService.createStackedIcon('gear', 'circle-o'), donutCbx);
	    	}
	    	
	    	if (holeEdit) {
	    		var holeRatioText = knimeService.createMenuTextField('holeRatioText', _value.options.holeSize, function() {
	    			if (this.value < 0) {
    					this.value = 0;
    				} else if (this.value > 1) {
    					this.value = 1;
    				}
    				chart.donutRatio(this.value);
    				chart.update();
	    		}, true);
	    		holeRatioText.setAttribute("type", "number");
	    		holeRatioText.setAttribute("min", 0);
	    		holeRatioText.setAttribute("max", 1);
	    		holeRatioText.setAttribute("step", 0.1);
	    		holeRatioText.disabled = !_value.options.togglePie;
	    		knimeService.addMenuItem('Donut hole ratio:', 'adjust', holeRatioText);
	    	}
	    	
	    	/*if (insideTitleEdit) {
	    		var insideTitleText = knimeService.createMenuTextField('insideTitleText', _value.options.insideTitle, function() {
	    			if (_value.options.insideTitle != this.value) {
						_value.options.insideTitle = this.value;
						chart.title(this.value);
						chart.update();
					}
	    		}, true);
	    		insideTitleText.disabled = !_value.options.togglePie;
	    		knimeService.addMenuItem('Title inside:', 'header', insideTitleText, null, knimeService.SMALL_ICON);
	    	}*/
	    	
	        if (enableSelection) {
	        	knimeService.addMenuDivider();
	        	var subscribeToSelectionIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
	        	var subscribeToSelectionMenu = knimeService.createMenuCheckbox('subscribeToSelection', 
	        			_value.options.subscribeToSelection, function () {
	        		if (_value.options.subscribeToSelection != this.checked) {
	        			_value.options.subscribeToSelection = this.checked;
	        			subscribeToSelection(_value.options.subscribeToSelection);
	        		}
	        	});
	        	knimeService.addMenuItem('Subscribe to selection:', subscribeToSelectionIcon, subscribeToSelectionMenu);
	        	
	        	var publishSelectionIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
	            var publishSelectionMenu = knimeService.createMenuCheckbox('publishSelection', _value.options.publishSelection,
	                function () {
	                    if (_value.options.publishSelection != this.checked) {
	                        _value.options.publishSelection = this.checked;
	                        publishSelection(this.checked);
	                    }
	                });
	            knimeService.addMenuItem('Publish selection:', publishSelectionIcon, publishSelectionMenu);
	        }
	        
	        if (disableClearButton &&  _representation.options.enableSelection) {
				knimeService.addButton("clearSelectionButton", "minus-square-o", "Clear selection", function(){
					d3.selectAll(".row").classed({"selected": false, "knime-selected": false, "unselected": false });
					removeHilightBar("",true);
					_value.options['selection'] = [];
					publishSelection(true);
				});
				d3.select("#clearSelectionButton").classed("inactive", true);
			}
    	}
	};

	function setCssClasses() {		
		d3.selectAll('.nv-label')
			.classed('knime-label', true);

		// legend
		d3.selectAll('.nv-legendWrap')
			.classed('knime-legend', true);
		d3.selectAll('.nv-legend-symbol')
			.classed('knime-legend-symbol', true);
		d3.selectAll('.nv-legend-text')
			.classed('knime-legend-label', true);
		registerClickHandler();
	}
	
	function setTooltipCssClasses() {
		// tooltip
		var tooltip = d3.selectAll('.nvtooltip')
		.classed('knime-tooltip', true);
		tooltip.selectAll('.x-value')
		.classed('knime-tooltip-caption', true)
		.classed('knime-x', true);
		tooltip.selectAll('.legend-color-guide')
		.classed('knime-tooltip-color', true);
		tooltip.selectAll('.key')
		.classed('knime-tooltip-key', true);
		tooltip.selectAll('.value')
		.classed('knime-tooltip-value', true);
	}

	pie.validate = function() {
		return true;
	}

	pie.getComponentValue = function() {
		return _value;
	}

	pie.getSVG = function() {		
		// correct faulty rect elements
		d3.selectAll("rect").each(function() {
			var rect = d3.select(this);
			if (!rect.attr("width")) {
				rect.attr("width", 0);
			}
			if (!rect.attr("height")) {
				rect.attr("height", 0);
			}
		});
		
		var svgElement = d3.select("svg")[0][0];
		knimeService.inlineSvgStyles(svgElement);

		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return pie;

}());