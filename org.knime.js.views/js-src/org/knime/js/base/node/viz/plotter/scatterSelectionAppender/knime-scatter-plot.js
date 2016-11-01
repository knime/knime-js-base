knime_scatter_plot_selection_appender = function() {
	
	// register helper methods
	// detect Linux
	if (!jsfc.Utils.isLinux) {
		jsfc.Utils.isLinux = function() {
			return navigator.appVersion.indexOf("X11") != -1 || navigator.appVersion.indexOf("Linux") != -1;
		}
	}
	
	// create modifier that works on Windows, Mac and Linux
	if (!jsfc.Modifier.createModifierWML) {
		jsfc.Modifier.createModifierWML = function(altKey, ctrlKey, shiftKey, shiftExtends) {
			var m;
			if (jsfc.Utils.isMacOS()) {
				// Mac can't use ctrl key -> map to cmd key (meta key) 
				m = new jsfc.Modifier(altKey, false, ctrlKey, shiftKey);
			} else if (jsfc.Utils.isLinux()) { 
				// Linux can't use alt key -> map to Windows key (meta key)
				m = new jsfc.Modifier(false, ctrlKey, altKey, shiftKey);
				m.matchEvent = function(e) {
					var metaKey = e.metaKey || e.getModifierState("OS") || e.getModifierState("Super");
				    return m.match(e.altKey, e.ctrlKey, metaKey, e.shiftKey);
				}
				m.matchEventWithExtension = function(e) {
					var metaKey = e.metaKey || e.getModifierState("OS") || e.getModifierState("Super");
					return m.matchWithExtension(e.altKey, e.ctrlKey, metaKey, e.shiftKey);
				}
			} else {
				// Windows, can't use the Windows key (meta key)
				m = new jsfc.Modifier(altKey, ctrlKey, false, shiftKey);
			}
			if (shiftExtends) {
				m.extension = new jsfc.Modifier(false, false, false, true);
			}
			return m;
		}
	}
	
	var view = {};
	var _representation = null;
	var _value = null;
	var _keyedDataset = null;
	var chartManager = null;
	var containerID = "scatterContainer";
	var initialAxisBounds;
	var publishSelection = false;
	
	var minWidth = 400;
	var minHeight = 300;
	var defaultFont = "sans-serif";
	var defaultFontSize = 12;
	
	view.init = function(representation, value) {
		if (!representation.keyedDataset) {
			d3.select("body").text("Error: No data available");
			return;
		}
		if (representation.keyedDataset.columnKeys.indexOf(value.xColumn) == -1) {
			d3.select("body").text("Error: Selected column for x-axis: \"" + value.xColumn + "\" not available.");
			return;
		}
		_representation = representation;
		_value = value;
		try {
			//console.time("Parse and build 2DDataset");
			//console.time("Total init time");
			_keyedDataset = new jsfc.KeyedValues2DDataset();
			//_keyedDataset.load(_representation.keyedDataset);
			
			// workaround for https://bugs.knime.org/show_bug.cgi?id=6229, remove when solved
			if (_representation.keyedDataset.rows.length == 1) {
				alert("Chart with only one data sample not supported at this time. Please provide a data set with at least 2 samples.")
			} else {
				for (var rowIndex = 0; rowIndex < _representation.keyedDataset.rows.length; rowIndex++) {
					var rowKey = _representation.keyedDataset.rows[rowIndex].rowKey;
					var row = _representation.keyedDataset.rows[rowIndex];
					var properties = row.properties;
					for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
						var columnKey = _representation.keyedDataset.columnKeys[col];
						_keyedDataset.add(rowKey, columnKey, row.values[col]);
					}
					for ( var propertyKey in properties) {
						_keyedDataset.setRowProperty(rowKey, propertyKey,
								properties[propertyKey]);
					}
				}
			}
			
			for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
				var columnKey = _representation.keyedDataset.columnKeys[col];
				var symbolProp = _representation.keyedDataset.symbols[col];
				if (symbolProp) {
					var symbols = [];
					for (var symbolKey in symbolProp) {
						symbols.push({"symbol": symbolProp[symbolKey], "value": symbolKey});
					}
					_keyedDataset.setColumnProperty(columnKey, "symbols", symbols);
				}
				var dateTimeFormat = _representation.keyedDataset.dateTimeFormats[col];
				if (dateTimeFormat) {
					_keyedDataset.setColumnProperty(columnKey, "date", dateTimeFormat);
				}
			}
			
			if (_value.selection) {
				for (var selection = 0; selection < _value.selection.length; selection++) {
					for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
						// Select all cols of selected row
						_keyedDataset.select("selection", _value.selection[selection],  _representation.keyedDataset.columnKeys[col]);
					}
				}
			}
			//console.timeEnd("Parse and build 2DDataset");

			d3.select("html").style("width", "100%").style("height", "100%")/*.style("overflow", "hidden")*/;
			d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");
			var layoutContainer = "layoutContainer";
			d3.select("body").attr("id", "body").append("div").attr("id", layoutContainer)
				.style("width", "100%").style("height", "100%")
				.style("min-width", minWidth + "px").style("min-height", minHeight + "px");
			
			drawChart(layoutContainer);
			drawControls(layoutContainer);
			
			//console.timeEnd("Total init time");
		} catch(err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert (err);
			}
		}
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	};
	
	buildXYDataset = function() {
		//console.time("Building XYDataset");
		var yCol = null;
		if (_value.yColumn) {
			yCol = _value.yColumn;
		}
		if (!yCol) {
			yCol = "[EMPTY]";
		}
		var xyDataset = jsfc.DatasetUtils.extractXYDatasetFromColumns2D(_keyedDataset, _value.xColumn, yCol);
		//console.timeEnd("Building XYDataset");
		return xyDataset;
	};
	
	drawChart = function(layoutContainer) {
		if (!_value.xColumn) {
			alert("No column set for x axis!");
			return;
		}
		if (!_value.yColumn) {
			alert("No column set for y axis!");
			return;
		}
		var xAxisLabel = _value.xAxisLabel ? _value.xAxisLabel : _value.xColumn;
		var yAxisLabel = _value.yAxisLabel ? _value.yAxisLabel : _value.yColumn;
		
		var dataset = buildXYDataset();
		
		//console.time("Building chart");
		
		var chartWidth = _representation.imageWidth + "px;"
		var chartHeight = _representation.imageHeight + "px";
		if (_representation.resizeToWindow) {
			chartWidth = "100%";
			chartHeight = "100%";
		}
		d3.select("#"+layoutContainer).append("div")
			.attr("id", containerID)
			.style("width", chartWidth)
			.style("height", chartHeight)
			.style("min-width", minWidth + "px")
			.style("min-height", minHeight + "px")
			.style("box-sizing", "border-box")
			.style("overflow", "hidden")
			.style("margin", "0");
		
		//chart.build(container);
				
		var plot = new jsfc.XYPlot(dataset);
		//TODO: how to handle this best?
		//plot.setStaggerRendering(_representation.enableStaggeredRendering);
		var xAxis = plot.getXAxis();
        xAxis.setLabel(xAxisLabel);
        xAxis.setLabelFont(new jsfc.Font(defaultFont, defaultFontSize, true));
        xAxis.setTickLabelFont(new jsfc.Font("sans-serif", 11));
        xAxis.setGridLinesVisible(_representation.showGrid, false);
        xAxis.setAutoRange(_representation.autoRangeAxes, false);
        if (_value.xAxisMin != null && _value.xAxisMax != null) {
        	xAxis.setBounds(_value.xAxisMin, _value.xAxisMax, false, false);
        }
        
        var yAxis = plot.getYAxis();
        yAxis.setLabel(yAxisLabel);
        yAxis.setLabelFont(new jsfc.Font(defaultFont, defaultFontSize, true));
        yAxis.setTickLabelFont(new jsfc.Font("sans-serif", 11));
        yAxis.setGridLinesVisible(_representation.showGrid, false);
        yAxis.setAutoRange(_representation.autoRangeAxes, false);
        if (_value.yAxisMin != null && _value.yAxisMax != null) {
        	yAxis.setBounds(_value.yAxisMin, _value.yAxisMax, true, false);
        }
        if (_representation.gridColor) {
        	var gColor = getJsfcColor(_representation.gridColor);
        	xAxis.setGridLineColor(gColor, false);
        	yAxis.setGridLineColor(gColor, false);
        }
        
        if (_value.xColumn) {
			var dateProp = _keyedDataset.getColumnProperty(_value.xColumn, "date");
			if (dateProp) {
				plot.getXAxis().setTickLabelFormatOverride(new jsfc.UniversalDateFormat(dateProp));
			} else {
				plot.getXAxis().setTickLabelFormatOverride(null);
			}
		}
        
        plot.renderer = new jsfc.ScatterRenderer(plot);
        var chart = new jsfc.Chart(plot);
        if (_representation.backgroundColor) {
        	chart.setBackgroundColor(getJsfcColor(_representation.backgroundColor), false);
        }
        if (_representation.dataAreaColor) {
			plot.setDataBackgroundColor(getJsfcColor(_representation.dataAreaColor), false);
		}
        chart.setTitleAnchor(new jsfc.Anchor2D(jsfc.RefPt2D.TOP_LEFT));
        var chartTitle = _value.chartTitle ? _value.chartTitle : "";
        var chartSubtitle = _value.chartSubtitle ? _value.chartSubtitle : "";
        chart.setTitle(chartTitle, chartSubtitle, chart.getTitleAnchor());
        chart.updateTitle(null, new jsfc.Font("sans-serif", 24, false, false));
        chart.updateSubtitle(null, new jsfc.Font("sans-serif", 12, false, false));
        if (_representation.showLegend) {
        	var legendBuilder = new jsfc.StandardLegendBuilder();
        	legendBuilder.setFont(new jsfc.Font("sans-serif", 12));
        	chart.setLegendBuilder(legendBuilder);
        } else {
        	chart.setLegendBuilder(null);
        }
		var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		document.getElementById(containerID).appendChild(svg);
		if (_representation.resizeToWindow) {
			chartHeight = "100%";
		}
		d3.select(svg).attr("id", "chart_svg").style("width", chartWidth).style("height", chartHeight);
        var zoomEnabled = _representation.enableZooming;
        var dragZoomEnabled = _representation.enableDragZooming;
        var panEnabled = _representation.enablePanning;
        
        // override installMouseDownHandler for Linux mapping -- see AP-5737
        jsfc.ChartManager.prototype.installMouseDownHandler = function(element) {
        	var my = this;
        	element.onmousedown = function(event) {
        		if (my._liveMouseHandler !== null) {
        			my._liveMouseHandler.mouseDown(event);
        		} else {
        			// choose one of the available mouse handlers to be "live"
        			var metaKey = event.metaKey;
        			if (jsfc.Utils.isLinux()) {
        				metaKey = metaKey || event.getModifierState("OS") || event.getModifierState("Super");
        			}
        			var h = my._matchLiveHandler(event.altKey, event.ctrlKey, metaKey, event.shiftKey);
        			if (h) {
        				my._liveMouseHandler = h;
        				my._liveMouseHandler.mouseDown(event);
        			}
        		}
        		
        		// pass the event to the auxiliary mouse handlers
        		my._auxiliaryMouseHandlers.forEach(function(h) {
        			h.mouseDown(event);
        		}); 
        		event.preventDefault();
        	};
        };
        
        chartManager = new jsfc.ChartManager(svg, chart, dragZoomEnabled, zoomEnabled, false);

        if (panEnabled) {
        	var panModifier = new jsfc.Modifier.createModifier(false, false, false, false);
        	if (dragZoomEnabled) {
        		panModifier = new jsfc.Modifier.createModifier(false, true, false, false);
        	}
            var panHandler = new jsfc.PanHandler(chartManager, panModifier);
            chartManager.addLiveHandler(panHandler);
        }
        
        var selectionEnabled = _representation.enableSelection;
        var recSelEnabled = _representation.enableRectangleSelection;
        var lasSelEnabled = _representation.enableLassoSelection;
        
        if (selectionEnabled) {
        	var selectionModifier = new jsfc.Modifier.createModifierWML(true, false, false, true);
        	var clickSelectionHandler = new jsfc.ClickSelectionHandler(chartManager, selectionModifier);
        	if (recSelEnabled) {
        		var rectangleSelectionHandler = new jsfc.RectangleSelectionHandler(chartManager, selectionModifier);
        		var selectionHandler = new jsfc.DualHandler(chartManager, selectionModifier, clickSelectionHandler, rectangleSelectionHandler);
        		chartManager.addLiveHandler(selectionHandler);
        	} else {
        		chartManager.addLiveHandler(clickSelectionHandler);
        	}
        	if (lasSelEnabled) {
        		var polygonSelectionModifier = new jsfc.Modifier.createModifierWML(true, true, false, true);
        		var polygonSelectionHandler = new jsfc.PolygonSelectionHandler(chartManager, polygonSelectionModifier);
        		chartManager.addLiveHandler(polygonSelectionHandler);
        	}
        }
        
        if (_representation.showCrosshair) {
        	var crosshairHandler = new jsfc.XYCrosshairHandler(chartManager);
        	crosshairHandler.setSnapToItem(_representation.snapToPoints);
        	chartManager.addAuxiliaryHandler(crosshairHandler);
        }
                
        setChartDimensions();
        //console.timeEnd("Building chart");
        //console.time("Refreshing Display");
        chartManager.refreshDisplay();
        //console.timeEnd("Refreshing Display");
        //console.debug(svg.outerHTML);
        var win = document.defaultView || document.parentWindow;
        win.onresize = resize;
        
        if (knimeService && knimeService.isInteractivityAvailable()) {
        	chartManager.getChart().getPlot().addListener(function() {
        		if (_value.publishSelection) {
        			knimeService.setSelectedRows(_representation.keyedDataset.id, getSelection());
        		}
        	});
		}
        
		initialAxisBounds = {xMin: xAxis.getLowerBound(), xMax: xAxis.getUpperBound(), yMin: yAxis.getLowerBound(), yMax: yAxis.getUpperBound()};
	};
	
	getJsfcColor = function(colorString) {
		var colC = colorString.slice(5,-1).split(",");
		var color = new jsfc.Color(parseInt(colC[0]), parseInt(colC[1]), parseInt(colC[2]), parseInt(colC[3])*255);
		return color;
	};
	
	resize = function(event) {
		setChartDimensions();
        chartManager.refreshDisplay();
	};
	
	setChartDimensions = function() {
		var container = document.getElementById(containerID);
		var w = _representation.imageWidth;
		var h = _representation.imageHeight;
		if (_representation.resizeToWindow) {
			w = Math.max(minWidth, container.clientWidth);
			h = Math.max(minHeight, container.clientHeight);
		}
        chartManager.getChart().setSize(w, h);
	};
	
	updateChart = function() {
		var plot = chartManager.getChart().getPlot();
		var oldSelections = plot.getDataset().selections;
		var dataset = buildXYDataset();
		plot.setDataset(dataset);
		dataset.selections = oldSelections;
		dataset.notifyListeners();		
		if (_value.xColumn) {
			var dateProp = plot.getDataset().getSeriesProperty(_value.xColumn, "date");
			if (dateProp) {
				plot.getXAxis().setTickLabelFormatOverride(new jsfc.DateFormat(dateProp));
			} else {
				plot.getXAxis().setTickLabelFormatOverride(null);
			}
		}
		if (_representation.autoRangeAxes) {
			plot.getXAxis().setAutoRange(true);
			plot.getYAxis().setAutoRange(true);
		}
		//chartManager.refreshDisplay();
		//plot.update(chart);
	};
	
	updateTitle = function() {
		var oldTitle = _value.chartTitle;
		_value.chartTitle = document.getElementById("chartTitleText").value;
		if (_value.chartTitle !== oldTitle || typeof _value.chartTitle !== typeof oldTitle) {
			setTitles();
		}
	};
	
	updateSubtitle = function() {
		var oldTitle = _value.chartSubtitle;
		_value.chartSubtitle = document.getElementById("chartSubtitleText").value;
		if (_value.chartSubtitle !== oldTitle || typeof _value.chartTitle !== typeof oldTitle) {
			setTitles();
		}
	};
	
	setTitles = function() {
		var chart = chartManager.getChart();
		chart.setTitle(_value.chartTitle, _value.chartSubtitle, chart.getTitleAnchor(), false);
		chart.updateTitle(null, new jsfc.Font("sans-serif", 24, false, false));
		chart.updateSubtitle(null, new jsfc.Font("sans-serif", 12, false, false));
		chart.notifyListeners();
	}
	
	updateXAxisLabel = function() {
		var oldLabel = _value.xAxisLabel;
		_value.xAxisLabel = document.getElementById("xAxisText").value;
		if (_value.xAxisLabel !== oldLabel) {
			var newAxisLabel = _value.xAxisLabel;
			if (!_value.xAxisLabel) {
				newAxisLabel = _value.xColumn;
			}
			chartManager.getChart().getPlot().getXAxis().setLabel(newAxisLabel);
		}
	};
	
	updateYAxisLabel = function() {
		var oldLabel = _value.yAxisLabel;
		_value.yAxisLabel = document.getElementById("yAxisText").value;
		if (_value.yAxisLabel !== oldLabel) {
			var newAxisLabel = _value.yAxisLabel;
			if (!_value.xAxisLabel) {
				newAxisLabel = _value.yColumn;
			}
			chartManager.getChart().getPlot().getYAxis().setLabel(newAxisLabel);
		}
	};
	
	drawControls = function(layoutContainer) {
		
		if (!knimeService) {
			// TODO: error handling?
			return;
		}
		
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
	    if (_representation.showZoomResetButton) {
	    	knimeService.addButton('scatter-zoom-reset-button', 'search-minus', 'Reset Zoom', function() {
	    		var plot = chartManager.getChart().getPlot();
	    		plot.getXAxis().setAutoRange(true);
	    		plot.getYAxis().setAutoRange(true);
	    	});
	    }
	    
	    if (!_representation.enableViewConfiguration) return;
	    var pre = false;
	    
	    if (_representation.enableTitleChange || _representation.enableSubtitleChange) {
	    	pre = true;
	    	if (_representation.enableTitleChange) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.chartTitle, updateTitle, false);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
	    	}
	    	if (_representation.enableSubtitleChange) {
	    		var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.chartSubtitle, updateSubtitle, false);
	    		var mi = knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
	    	}
	    }
	    
	    if (_representation.enableXColumnChange || _representation.enableYColumnChange) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableXColumnChange) {
	    		var xSelect = knimeService.createMenuSelect('xColumnSelect', _value.xColumn, _keyedDataset.columnKeys(), function() {
	    			_value.xColumn = this.value;
	    			if (!_value.xAxisLabel) {
	    				chartManager.getChart().getPlot().getXAxis().setLabel(_value.xColumn, false);
	    			}
	    			updateChart();
	    		});
	    		knimeService.addMenuItem('X Column:', 'long-arrow-right', xSelect);
	    		
	    	}
	    	if (_representation.enableYColumnChange) {
	    		var ySelect = knimeService.createMenuSelect('yColumnSelect', _value.yColumn, _keyedDataset.columnKeys(), function() {
	    			_value.yColumn = this.value;
	    			if (!_value.yAxisLabel) {
	    				chartManager.getChart().getPlot().getYAxis().setLabel(_value.yColumn, false);
	    			}
	    			updateChart();
	    		});
	    		knimeService.addMenuItem('Y Column:', 'long-arrow-up', ySelect);
	    	}
	    }
	    if (_representation.enableXAxisLabelEdit || _representation.enableYAxisLabelEdit) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableXAxisLabelEdit) {
	    		var xAxisText = knimeService.createMenuTextField('xAxisText', _value.xAxisLabel, updateXAxisLabel, false);
	    		knimeService.addMenuItem('X Axis Label:', 'ellipsis-h', xAxisText);
	    	}
	    	if (_representation.enableYAxisLabelEdit) {
	    		var yAxisText = knimeService.createMenuTextField('yAxisText', _value.yAxisLabel, updateYAxisLabel);
	    		knimeService.addMenuItem('Y Axis Label:', 'ellipsis-v', yAxisText);
	    	}
	    }
	    if (_representation.enableDotSizeChange) {
	    	// TODO enable once implemented
	    	/*var dotSizeContainer = controlContainer.append("tr").style("margin", "5px auto").style("display", "table");
	    	dotSizeContainer.append("td").append("label").attr("for", "dotSizeInput").text("Dot Size:").style("margin-right", "5px");
	    	dotSizeContainer.append("td").append("input")
	    		.attr("type", "number")
	    		.attr("id", "dotSizeInput")
	    		.attr("name", "dotSizeInput")
	    		.attr("value", _value.dotSize)
	    		.style("font-family", defaultFont)
	    		.style("font-size", defaultFontSize+"px");*/
	    }
	    if (knimeService.isInteractivityAvailable()) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableSelection) {
	    		var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
				var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.publishSelection, function() {
					if (this.checked) {
						_value.publishSelection = true;
						knimeService.setSelectedRows(_representation.keyedDataset.id, getSelection());
					} else {
						_value.publishSelection = false;
					}
				});
				knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
				
				/*var subSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
				var subSelCheckbox = knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.subscribeSelection, function() {
					if (this.checked) {
						knimeService.subscribeToSelection(_representation.keyedDataset.id, selectionChanged);
					} else {
						knimeService.unsubscribeSelection(_representation.keyedDataset.id, selectionChanged);
					}
				});
				knimeService.addMenuItem('Subscribe to selection', subSelIcon, subSelCheckbox);
				if (_value.subscribeSelection) {
					knimeService.subscribeToSelection(_representation.keyedDataset.id, selectionChanged);
				}*/
	    	}
	    	/*var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');
			var subFilCheckbox = knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.subscribeFilter, function() {
				if (this.checked) {
					//knimeService.subscribeFilter
				} else {
					//knimeService.unsubscribeFilter
				}
			});
			knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);*/
	    }
	};
	
	selectionChanged = function(data) {
		//TODO: implement
	}
	
	getSelection = function() {
		var selections = chartManager.getChart().getPlot().getDataset().selections;
		var selectionsArray = [];
		for (var i = 0; i < selections.length; i++) {
			if (selections[i].id === "selection") {
				selectionsArray = selections[i].items;
				break;
			}
		}
		var selectionIDs = [];
		for (var i = 0; i < selectionsArray.length; i++) {
			selectionIDs.push(_keyedDataset.rowKey(selectionsArray[i].itemKey));
		}
		if (selectionsArray.length == 0) {
			return null;
		}
		return selectionIDs;
	};
	
	view.validate = function() {
		return true;
	};
	
	view.getSVG = function() {
		if (!chartManager || !chartManager.getElement()) {
			return null;
		}
		var svg = chartManager.getElement();
		d3.select(svg).selectAll("circle").each(function() {
			this.removeAttribute("ref");
			this.removeAttribute("xmlns");
		});
		return (new XMLSerializer()).serializeToString(svg);
	};
	
	view.getComponentValue = function() {
		setAxisBoundsToValue();
		_value.selection = getSelection();
		return _value;
	};
	
	setAxisBoundsToValue = function() {
		var plot = chartManager.getChart().getPlot();
		var xAxis = plot.getXAxis();
		var yAxis = plot.getYAxis();
		var xMin = xAxis.getLowerBound();
		var xMax = xAxis.getUpperBound();
		var yMin = yAxis.getLowerBound();
		var yMax = yAxis.getUpperBound();
		if (xMin != initialAxisBounds.xMin || xMax != initialAxisBounds.xMax 
				|| yMin != initialAxisBounds.yMin || yMax != initialAxisBounds.yMax) {
			_value.xAxisMin = xMin;
			_value.xAxisMax = xMax;
			_value.yAxisMin = yMin;
			_value.yAxisMax = yMax;
		}
	};
	
	return view;
}();