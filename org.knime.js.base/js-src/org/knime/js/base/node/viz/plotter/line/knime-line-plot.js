knime_line_plot = function() {
	
	view = {};
	var _representation = null;
	var _value = null;
	var _keyedDataset = null;
	var chartManager = null;
	var containerID = "lineContainer";
	
	var minWidth = 400;
	var minHeight = 300;
	var defaultFont = "sans-serif";
	var defaultFontSize = 12;
	
	view.init = function(representation, value) {
		if (!representation.keyedDataset) {
			d3.select("body").text("Error: No data available");
			return;
		}
		_representation = representation;
		_value = value;
		try {
			//console.time("Parse and build 2DDataset");
			//console.time("Total init time");
			_keyedDataset = new jsfc.KeyedValues2DDataset();
			//_keyedDataset.load(_representation.keyedDataset);
			//var seriesKey = _representation.keyedDataset.series[0].seriesKey;
			for (var rowIndex = 0; rowIndex < _representation.keyedDataset.rows.length; rowIndex++) {
				var rowKey = _representation.keyedDataset.rows[rowIndex].rowKey;
				var row = _representation.keyedDataset.rows[rowIndex];
				var properties = row.properties;
				for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
					var columnKey = _representation.keyedDataset.columnKeys[col];
					_keyedDataset.add(rowKey, columnKey, row.values[col]);
				}
				for (var propertyKey in properties) {
					_keyedDataset.setRowProperty(rowKey, propertyKey, properties[propertyKey]);
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
				var columnColor = _representation.keyedDataset.columnColors[col];
				if (columnColor) {
					_keyedDataset.setColumnProperty(columnKey, "color", columnColor);
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
				.style("min-width", minWidth + "px").style("min-height", (minHeight + getControlHeight()) + "px");
			
			drawChart(layoutContainer);
			if (_representation.enableViewConfiguration || _representation.showZoomResetButton) {
				drawControls(layoutContainer);
			}
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
		var xyDataset;
		if (_keyedDataset.rowCount() > 0) {
			xyDataset = new jsfc.TableXYDataset(_keyedDataset, _value.xColumn, _value.yColumns);
		} else {
			xyDataset = jsfc.DatasetUtils.extractXYDatasetFromColumns2D(_keyedDataset, _value.xColumn, _value.yColumns[0]);
		}
		//console.timeEnd("Building XYDataset");
		return xyDataset;
	};
	
	drawChart = function(layoutContainer) {
		if (!_value.yColumns) {
			alert("No columns set for y axis!");
			return;
		}
		var xAxisLabel = _value.xAxisLabel ? _value.xAxisLabel : _value.xColumn;
		var yAxisLabel = _value.yAxisLabel ? _value.yAxisLabel : "";
		
		var dataset = buildXYDataset();

		//console.time("Building chart");
		
		//var chartHeight = _representation.enableViewConfiguration ? "80%" : "100%";
		var chartHeight = "calc(100% - " + getControlHeight() + "px)";
		d3.select("#"+layoutContainer).append("div")
			.attr("id", containerID)
			.style("width", "100%")
			.style("height", chartHeight)
			.style("min-width", minWidth + "px")
			.style("min-height", minHeight + "px")
			.style("box-sizing", "border-box")
			.style("overflow", "hidden")
			.style("margin", "0");
		
		//chart.build(container);
				
		var plot = new jsfc.XYPlot(dataset);
		plot.setStaggerRendering(_representation.enableStaggeredRendering);
		var xAxis = plot.getXAxis();
        xAxis.setLabel(xAxisLabel);
        xAxis.setLabelFont(new jsfc.Font(defaultFont, defaultFontSize, true));
        //xAxis.setTickLabelFont(new jsfc.Font("sans-serif", 10));
        if (_value.xAxisMin && _value.xAxisMax) {
        	xAxis.setBounds(_value.xAxisMin, _value.xAxisMax, false, false);
        }
        
        var yAxis = plot.getYAxis();
        yAxis.setLabel(yAxisLabel);
        yAxis.setLabelFont(new jsfc.Font(defaultFont, defaultFontSize, true));
        //yAxis.setTickLabelFont(new jsfc.Font("sans-serif", 10));
        if (_value.yAxisMin && _value.yAxisMax) {
        	yAxis.setBounds(_value.yAxisMin, _value.yAxisMax, true, false);
        }
        
		if (_value.xColumn) {
			var dateProp = dataset.getSeriesProperty(_value.xColumn, "date");
			if (dateProp) {
				plot.getXAxis().setTickLabelFormatOverride(new jsfc.DateFormat(dateProp));
			} else {
				plot.getXAxis().setTickLabelFormatOverride(null);
			}
		}
        
        plot.setRenderer(new jsfc.XYLineRenderer(plot));
        var chart = new jsfc.Chart(plot);
        chart.setTitleAnchor(new jsfc.Anchor2D(jsfc.RefPt2D.TOP_LEFT));
        var chartTitle = _value.chartTitle ? _value.chartTitle : "";
        var chartSubtitle = _value.chartSubtitle ? _value.chartSubtitle : "";
        chart.setTitle(chartTitle, chartSubtitle, chart.getTitleAnchor());
        //chart.setLegendBuilder(null);
        var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		document.getElementById(containerID).appendChild(svg);
		d3.select(svg).attr("id", "chart_svg").style("width", "100%").style("height", "100%");
        var zoomEnabled = _representation.enableZooming;
        var dragZoomEnabled = _representation.enableDragZooming;
        var panEnabled = _representation.enablePanning;
        chartManager = new jsfc.ChartManager(svg, chart, dragZoomEnabled, zoomEnabled, false);
        
        if (panEnabled) {
        	var panModifier = new jsfc.Modifier(false, false, false, false);
        	if (dragZoomEnabled) {
        		panModifier = new jsfc.Modifier(false, true, false, false);
        	}
            var panHandler = new jsfc.PanHandler(chartManager, panModifier);
            chartManager.addLiveHandler(panHandler);
        }
        
        var selectionHandler = new jsfc.ClickSelectionHandler(chartManager);
        chartManager.addLiveHandler(selectionHandler);
        
        var polygonSelectionModifier = new jsfc.Modifier(true, false, false, false);
        var polygonSelectionHandler = new jsfc.PolygonSelectionHandler(chartManager, polygonSelectionModifier);
        chartManager.addLiveHandler(polygonSelectionHandler);
        
        setChartDimensions();
        //console.timeEnd("Building chart");
        //console.time("Refreshing Display");
        chartManager.refreshDisplay();
        //console.timeEnd("Refreshing Display");
        //console.debug(svg.outerHTML);
        var win = document.defaultView || document.parentWindow;
        win.onresize = resize;
	};
	
	resize = function(event) {
		setChartDimensions();
        chartManager.refreshDisplay();
	};
	
	setChartDimensions = function() {
		var container = document.getElementById(containerID);
		var w = Math.max(minWidth, container.clientWidth);
        var h = Math.max(minHeight, container.clientHeight);
        chartManager.getChart().setSize(w, h);
	};
	
	updateChart = function() {
		var plot = chartManager.getChart().getPlot();
		plot.setDataset(buildXYDataset());
		if (_value.xColumn) {
			var dateProp = plot.getDataset().getSeriesProperty(_value.xColumn, "date");
			if (dateProp) {
				plot.getXAxis().setTickLabelFormatOverride(new jsfc.DateFormat(dateProp));
			} else {
				plot.getXAxis().setTickLabelFormatOverride(null);
			}
		}
		plot.getXAxis().setAutoRange(true);
		plot.getYAxis().setAutoRange(true);
		//chartManager.refreshDisplay();
		//plot.update(chart);
	};
	
	updateTitle = function() {
		_value.chartTitle = document.getElementById("chartTitleText").value;
		chartManager.getChart().setTitle(_value.chartTitle, _value.chartSubtitle, chartManager.getChart().getTitleAnchor());
	};
	
	updateSubtitle = function() {
		_value.chartSubtitle = document.getElementById("chartSubtitleText").value;
		chartManager.getChart().setTitle(_value.chartTitle, _value.chartSubtitle, chartManager.getChart().getTitleAnchor());
	};
	
	updateXAxisLabel = function() {
		_value.xAxisLabel = document.getElementById("xAxisText").value;
		var newAxisLabel = _value.xAxisLabel;
		if (!_value.xAxisLabel) {
			newAxisLabel = _value.xColumn;
		}
		chartManager.getChart().getPlot().getXAxis().setLabel(newAxisLabel);
	};
	
	updateYAxisLabel = function() {
		_value.yAxisLabel = document.getElementById("yAxisText").value;
		var newAxisLabel = _value.yAxisLabel;
		if (!_value.xAxisLabel) {
			newAxisLabel = _value.yColumn;
		}
		chartManager.getChart().getPlot().getYAxis().setLabel(newAxisLabel);
	};
	
	drawControls = function(layoutContainer) {
		
	    var controlContainer = d3.select("#"+layoutContainer).insert("table", "#" + containerID + " ~ *")
	    	.attr("id", "scatterControls")
	    	/*.style("width", "100%")*/
	    	.style("padding", "10px")
	    	.style("margin", "0 auto")
	    	.style("box-sizing", "border-box")
	    	.style("font-family", defaultFont)
	    	.style("font-size", defaultFontSize+"px")
	    	.style("border-spacing", 0)
	    	.style("border-collapse", "collapse");
	    
	    if (_representation.showZoomResetButton) {
	    	var resetButtonContainer = controlContainer.append("tr").append("td").attr("colspan", "4").style("text-align", "center");
	    	resetButtonContainer.append("button").text("Reset Zoom").on("click", function() {
	    		var plot = chartManager.getChart().getPlot();
	    		plot.getXAxis().setAutoRange(true);
	    		plot.getYAxis().setAutoRange(true);
	    	});
	    }
	    
	    if (!_representation.enableViewConfiguration) return;
	    
	    if (_representation.enableTitleChange || _representation.enableSubtitleChange) {
	    	var titleEditContainer = controlContainer.append("tr");
	    	if (_representation.enableTitleChange) {
	    		titleEditContainer.append("td").append("label").attr("for", "chartTitleText").text("Chart Title:").style("margin-right", "5px");
	    		var chartTitleText = titleEditContainer.append("td").append("input")
	    			.attr("type", "text")
	    			.attr("id", "chartTitleText")
	    			.attr("name", "chartTitleText")
	    			.style("font-family", defaultFont)
	    			.style("font-size", defaultFontSize+"px")
	    		.on("blur", function() {
	    			updateTitle();
	    		})
	    		.on("keypress", function() {
	    			if ( d3.event.keyCode == 13 ) {
	    				updateTitle();
	    			}
	    		});
	    		if (_representation.enableYAxisLabelEdit) {
	    			chartTitleText.style("margin-right", "10px");
	    		}
	    		document.getElementById("chartTitleText").value = _value.chartTitle;
	    	}
	    	if (_representation.enableSubtitleChange) {
	    		titleEditContainer.append("td").append("label").attr("for", "chartSubtitleText").text("Chart Subtitle:").style("margin-right", "5px");
	    		titleEditContainer.append("td").append("input")
	    			.attr("type", "text")
	    			.attr("id", "chartSubtitleText")
	    			.attr("name", "chartSubtitleText")
	    			.style("font-family", defaultFont)
	    			.style("font-size", defaultFontSize+"px")
	    		.on("blur", function() {
	    			updateSubtitle();
	    		})
	    		.on("keypress", function() {
	    			if ( d3.event.keyCode == 13 ) {
	    				updateSubtitle();
	    			}
	    		});
	    		document.getElementById("chartSubtitleText").value = _value.chartSubtitle;
	    	}
	    }
	    
    	if (_representation.enableXColumnChange) {
    		var columnChangeContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
    		columnChangeContainer.append("td").append("label").attr("for", "xColumnSelect").text("X Column:").style("margin-right", "5px");
    		var xSelect = columnChangeContainer.append("td").append("select")
    			.attr("id", "xColumnSelect")
    			.attr("name", "xColumnSelect")
    			.style("font-family", defaultFont)
    			.style("font-size", defaultFontSize+"px");
    		var columnKeys = _keyedDataset.columnKeys();
    		xSelect.append("option").attr("value", "<RowID>").text("<RowID>");
    		for (var colID = 0; colID < columnKeys.length; colID++) {
    			xSelect.append("option").attr("value", columnKeys[colID]).text(columnKeys[colID]);
    		}
    		document.getElementById("xColumnSelect").value = _value.xColumn;
    		xSelect.on("change", function() {
    			var newXCol = document.getElementById("xColumnSelect").value;
    			if (newXCol == "<RowID>") {
    				newXCol = null;
    			}
    			_value.xColumn = newXCol;
    			if (!_value.xAxisLabel) {
    				chartManager.getChart().getPlot().getXAxis().setLabel(_value.xColumn, false);
    			}
    			updateChart();
    		});
    		if (_representation.enableYColumnChange) {
    			xSelect.style("margin-right", "10px");
    		}
    	}
	    if (_representation.enableYColumnChange) {
    		var columnChangeContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
	    	columnChangeContainer.append("td").append("label").attr("for", "yColumnSelect").text("Y Columns:").style("margin-right", "5px");
	    	var ySelect = new twinlistMultipleSelections();
	    	columnChangeContainer.append("td").node().appendChild(ySelect.getComponent().get(0));
	    	ySelect.setChoices(_keyedDataset.columnKeys());
	    	ySelect.setSelections(_value.yColumns);
	    	ySelect.addValueChangedListener(function() {
	    		_value.yColumns = ySelect.getSelections();
	    		updateChart();
	    	});
	    }
	    if (_representation.enableXAxisLabelEdit || _representation.enableYAxisLabelEdit) {
	    	var axisLabelContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
	    	if (_representation.enableXAxisLabelEdit) {
	    		axisLabelContainer.append("td").append("label").attr("for", "xAxisText").text("X Axis Label:").style("margin-right", "5px");
	    		var xAxisText = axisLabelContainer.append("td").append("input")
	    			.attr("type", "text")
	    			.attr("id", "xAxisText")
	    			.attr("name", "xAxisText")
	    			.style("font-family", defaultFont)
	    			.style("font-size", defaultFontSize+"px")
	    		.on("blur", function() {
	    			updateXAxisLabel();
	    		})
	    		.on("keypress", function() {
	    			if ( d3.event.keyCode == 13 ) {
	    				updateXAxisLabel();
	    			}
	    		});
	    		if (_representation.enableYAxisLabelEdit) {
	    			xAxisText.style("margin-right", "10px");
	    		}
	    		document.getElementById("xAxisText").value = _value.xAxisLabel;
	    	}
	    	if (_representation.enableYAxisLabelEdit) {
	    		axisLabelContainer.append("td").append("label").attr("for", "yAxisText").text("Y Axis Label:").style("margin-right", "5px");
	    		axisLabelContainer.append("td").append("input")
	    			.attr("type", "text")
	    			.attr("id", "yAxisText")
	    			.attr("name", "yAxisText")
	    			.style("font-family", defaultFont)
	    			.style("font-size", defaultFontSize+"px")
	    		.on("blur", function() {
	    			updateYAxisLabel();
	    		})
	    		.on("keypress", function() {
	    			if ( d3.event.keyCode == 13 ) {
	    				updateYAxisLabel();
	    			}
	    		});
	    		document.getElementById("yAxisText").value = _value.yAxisLabel;
	    	}
	    }
	    if (_representation.enableDotSizeChange) {
	    	var dotSizeContainer = controlContainer.append("tr")/*.style("margin", "5px auto").style("display", "table")*/;
	    	dotSizeContainer.append("td").append("label").attr("for", "dotSizeInput").text("Dot Size:").style("margin-right", "5px");
	    	dotSizeContainer.append("td").append("input")
	    		.attr("type", "number")
	    		.attr("id", "dotSizeInput")
	    		.attr("name", "dotSizeInput")
	    		.attr("value", _value.dotSize)
	    		.style("font-family", defaultFont)
	    		.style("font-size", defaultFontSize+"px");
	    }
	};
	
	getControlHeight = function() {
		var rows = 0;
		var sizeFactor = 25;
		var padding = 10;
		if (_representation.showZoomResetButton) rows++;
		if (_representation.enableViewConfiguration) {
			if (_representation.enableTitleChange || _representation.enableSubtitleChange) rows++;
			if (_representation.enableXColumnChange) rows++;
			if (_representation.enableXAxisLabelEdit || _representation.enableYAxisLabelEdit) rows++;
			if (_representation.enableDotSizeChange) rows++;
		}
		var height = rows * sizeFactor;
		if (_representation.enableYColumnChange) {
			//rows
			height += _keyedDataset.columnKeys().length * 17;
			//header
			height += 25;
		}
		if (height > 0) height += padding;
		return height;
	};
	
	getSelection = function() {
		var selections = chartManager.getChart().getPlot().getDataset().selections;
		var selectionsArray = [];
		if (selections) {
			for ( var i = 0; i < selections.length; i++) {
				if (selections[i].id === "selection") {
					selectionsArray = selections[i].items;
					break;
				}
			}
		}
		var selectionIDs = [];
		for (var i = 0; i < selectionsArray.length; i++) {
			selectionIDs.push(_keyedDataset.rowKey(selectionsArray[i].itemKey));
		}
		return selectionIDs;
	};
	
	view.validate = function() {
		return true;
	};
	
	view.getSVG = function() {
		var svg = chartManager.getElement();
		d3.select(svg).selectAll("circle").each(function() {
			this.removeAttributeNS("http://www.jfree.org", "ref");
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
		_value.xAxisMin = xAxis.getLowerBound();
		_value.xAxisMax = xAxis.getUpperBound();
		_value.yAxisMin = yAxis.getLowerBound();
		_value.yAxisMax = yAxis.getUpperBound();
	};
	
	return view;
}();