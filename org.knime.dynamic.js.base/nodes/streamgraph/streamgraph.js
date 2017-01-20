(streamgraph_namespace = function() {

//	TODO
//  - react to external filters: see tableview / parallel coordinates
//  - missing values and other stuff?
//	- wait for christian's number-formatter-component for x- and y-axis
//	- wait for christian's date-format-selector-component
//  - Check for other date/time types in new knime release


	var view = {};
	var _representation, _value;
	var _data;
	var _colorRange;
	var layoutContainer;
	var MIN_HEIGHT = 300, MIN_WIDTH = 400;
	var chart, svg;
	var knimeTable1, knimeTable2;
	var xAxisType, xAxisData;
	var currentFilter = null;

	var stackStyleByType = {
		"Stacked-Area-Chart": "stack",
		"Percentage-Area-Chart": "expand",
		"Stream-Graph": "stream-center"
	}


	view.init = function(representation, value) {
		_representation = representation;
		_value = value;

		// Create Knime tables from data.
		// Load data from port 1.
		knimeTable1 = new kt();
		knimeTable1.setDataTable(_representation.inObjects[0]);
		var columnKeys = _representation.options.columns;

		// Load data from port 2.
		knimeTable2 = null;
		if (_representation.inObjects[1] !== null) {
			knimeTable2 = new kt();
			knimeTable2.setDataTable(_representation.inObjects[1]);
		}


		if (_representation.options.enableViewControls) {
			drawControls();
		}
		setColors();
		setXAxisConf();
		transformData();
		drawChart();
		toggleFilter();
	}

	var drawChart = function() {
		// Remove earlier chart.
		d3.select("#layoutContainer").remove();

		/*
		 * Parse the options.
		 */
		var optTitle = _value.options["title"];
		var optSubtitle = _value.options["subtitle"];

		var stackStyle = stackStyleByType[_value.options.chartType];
		var interpolation = _value.options["interpolation"];
		var enableLegend = _value.options["legend"];
		var enableInteractiveGuideline = _value.options["interactiveGuideline"];
		var runningInView = _representation.runningInView;
		var optFullscreen = _representation.options["svg"]["fullscreen"]
				&& runningInView;
		var optWidth = _representation.options["svg"]["width"];
		var optHeight = _representation.options["svg"]["height"];

		var isTitle = optTitle || optSubtitle;

		/*
		 * Create HTML for the view.
		 */
		d3.selectAll("html, body").style("width", "100%").style("height",
				"100%").style("margin", "0").style("padding", "0");

		var body = d3.select("body");

		var width = optWidth + 'px';
		var height = optHeight + 'px';
		if (optFullscreen) {
			width = "100%";
			height = (isTitle) ? "100%" : "calc(100% - "
					+ knimeService.headerHeight() + "px)";
		}

		layoutContainer = body.append("div").attr("id", "layoutContainer")
				.style("width", width).style("height", height).style(
						"min-width", MIN_WIDTH + "px").style("min-height",
						MIN_HEIGHT + "px");

		// create div container to hold svg
		var svgContainer = layoutContainer.append("div").attr("id",
				"svgContainer").style("min-width", MIN_WIDTH + "px").style(
				"min-height", MIN_HEIGHT + "px").style("box-sizing",
				"border-box").style("overflow", "hidden").style("margin", "0")
				.style("width", width).style("height", height);

		// Create the SVG object
		svg = svgContainer.append("svg")
			.attr("id", "svg")
			.style("font-family", "sans-serif")
			.attr("width", width)
			.attr("height", height);

		if (optFullscreen) {
			svg.attr("width", "100%");
			svg.attr("height", "100%");
		}

		// create the stacked area chart
		nv.addGraph(function() {
			chart = nv.models.stackedAreaChart()
				.margin({ right: 50 })
				.x(function(d) { return d[0]; })
				.y(function(d) { return d[1]; })
				.color(_colorRange)
				.interpolate(interpolation)
				.style(stackStyle)
				.showControls(false)
				.margin({ "top" : 10, "bottom" : 10 })
				.showLegend(enableLegend)
				.useInteractiveGuideline(enableInteractiveGuideline)
				.interactive(false)
				.duration(0);

			chart.xAxis
				.tickFormat(createXAxisFormatter());



			// Format y-axis
			// chart.yAxis
			//	.tickFormat(d3.format(".2s"));

			updateTitles(false);

			svg.datum(_data).call(chart);

			nv.utils.windowResize(chart.update);

			if  ("disabled" in _value.options) {
				var state = chart.defaultState();
				state.disabled = _value.options.disabled;
				chart.dispatch.changeState(state);
			}

			return chart;
		});
	}

	var setXAxisConf = function() {
		// Set data and data type for the x-axis.
		var xAxisColumn = _representation.options.xAxisColumn;
		if (typeof xAxisColumn !== "undefined") {
			var columnIndex = knimeTable1.getColumnNames().indexOf(xAxisColumn);
		  xAxisType = knimeTable1.getColumnTypes()[columnIndex];
		  xAxisData = knimeTable1.getColumn(columnIndex);
		} else {
			// If undefined: The user selected RowId as x-Axis.
		  xAxisType = "string";
			xAxisData = [];

			var rows = knimeTable1.getRows();
			for (var i = 0; i < rows.length; i++) {
				xAxisData.push(rows[i].rowKey);
			}
		}
	}

	// Transform the tabular format into a JSON format.
	// CHECK: This could be done more efficient,
	// i.e. create the json representation once and then
	// _data_filtered = filter(_data) in funct filterData()
	// In this case, we could combine function transformData with function setXAxisConf.
	var transformData = function() {
		_data = [];
		var columns = _representation.options.columns
		// Loop over all columns.
		for (var i = 0; i < columns.length; i++) {
			var columnKey = columns[i];
			var columnIndex = knimeTable1.getColumnNames().indexOf(columnKey);

			_data.push({
				"key" : columnKey,
				"values" : knimeTable1.getColumn(columnIndex).map(
						// This loops over all rows.
						function(d, i) {

							// Apply filter.
							if (!currentFilter ||
									knimeTable.isRowIncludedInFilter(index, currentFilter)) {

								if (xAxisType === 'dateTime' || xAxisType === 'number') {
									// If data type of x-axis column can be interpreted as numeric,
									// use the data for the x-axis.
									return [xAxisData[i], d]
								} else {
									// If not, just use an integer index [0, n[.
									return [i, d];
								}
							}
						})
			});
		}
	};

	var toggleFilter = function() {
		if (_value.options.subscribeFilter) {
			knimeService.subscribeToFilter(
				knimeTable1.getTableId(), filterChanged, knimeTable1.getFilterIds()
			);
		} else {
			knimeService.unsubscribeFilter(knimeTable1.getTableId(), filterChanged);
		}
	}

	var filterChanged = function(filter) {
	  currentFilter = filter;
	  transformData();
	  svg.datum(_data);
	  chart.update();
	}

	// Set color scale: custom or default.
	var setColors = function() {
		var colorScale = [];
		var columns = _representation.options.columns;
		if (knimeTable2 !== null) {
			var rowColors = knimeTable2.getRowColors();
			var numColumns = columns.length;
			for (var i = 0; i < numColumns; i++) {
				var columnName = columns[i]
				var rowIndex = knimeTable1.getColumnNames().indexOf(columnName);
				var color = rowColors[rowIndex];

				if (!color) {
					color = "#7C7C7C";
				}
				colorScale.push(color);
			}
			_colorRange = colorScale;
		} else {
			colorScale = d3.scale.category10();
			if (columns.length > 10) {
				colorScale = d3.scale.category20();
			}
			_colorRange = colorScale.range();
		}
	}

	// Return a function to format the x-axis-ticks.
	var createXAxisFormatter = function() {
		switch (xAxisType) {
		  case "dateTime":
				var dateFormat = _representation.options.dateFormat;
				return function(timestamp) {
					return moment(timestamp).format(dateFormat);
				};
			case "string":
        return function(i) { return xAxisData[i]; };
			case "number":
	      return function(x) { return x; };
	    default:
	      return function(i) { return i; };
		}
	}

	var updateTitles = function(updateChart) {
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
					svg.append("text").attr("x", 20).attr("y", 30).attr(
							"font-size", 24).attr("id", "title").text(
							_value.options.title);
				} else {
					curTitle.text(_value.options.title);
				}
			}
			if (!_value.options.subtitle) {
				curSubtitle.remove();
			}
			if (_value.options.subtitle) {
				if (curSubtitle.empty()) {
					svg.append("text").attr("x", 20).attr("y",
							_value.options.title ? 46 : 20).attr("font-size",
							12).attr("id", "subtitle").text(
							_value.options.subtitle);
				} else {
					curSubtitle.text(_value.options.subtitle).attr("y",
							_value.options.title ? 46 : 20);
				}
			}

			var topMargin = 10;
			topMargin += _value.options.title ? 10 : 0;
			topMargin += _value.options["legend"] ? 0 : 30;
			topMargin += _value.options.subtitle ? 8 : 0;
			chart.legend.margin({
				top : topMargin,
				bottom : topMargin
			});
			chart.margin({
				top : topMargin,
				bottom : topMargin
			});

			var isTitle = _value.options.title || _value.options.subtitle;
			knimeService.floatingHeader(isTitle);

			if (updateChart && chartNeedsUpdating) {
				if (_representation.options.svg.fullscreen
						&& _representation.runningInView) {
					var height = (isTitle) ? "100%" : "calc(100% - "
							+ knimeService.headerHeight() + "px)";
					layoutContainer.style("height", height)
					// two rows below force to invalidate the container which solves a weird problem with vertical scroll bar in IE
					.style('display', 'none').style('display', 'block');
					d3.select("#svgContainer").style("height", height);
				}
				chart.update();
			}
		}
	}

	var drawControls = function() {
		if (!knimeService) {
			// TODO: error handling?
			return;
		}

		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}

		if (!_representation.options.enableViewControls) {
			return;
		}

		// Title / Subtitle Configuration
		var titleEdit = _representation.options.enableTitleEdit;
		var subtitleEdit = _representation.options.enableSubtitleEdit;
		if (titleEdit || subtitleEdit) {
			if (titleEdit) {
				var chartTitleText = knimeService.createMenuTextField(
						'chartTitleText', _value.options.title, function() {
							if (_value.options.title != this.value) {
								_value.options.title = this.value;
								updateTitles(true);
							}
						}, true);
				knimeService.addMenuItem('Chart Title:', 'header',
						chartTitleText);
			}
			if (subtitleEdit) {
				var chartSubtitleText = knimeService.createMenuTextField(
						'chartSubtitleText', _value.options.subtitle,
						function() {
							if (_value.options.subtitle != this.value) {
								_value.options.subtitle = this.value;
								updateTitles(true);
							}
						}, true);
				var mi = knimeService.addMenuItem('Chart Subtitle:', 'header',
						chartSubtitleText, null, knimeService.SMALL_ICON);
			}
		}

		// Chart Type / Interpolation Method / Custom Color
		var chartTypeChange = _representation.options.enableChartTypeChange;
		var interpolationEdit = _representation.options.enableInterpolationMethodEdit;
		if (chartTypeChange || interpolationEdit || customColorToggle) {
			knimeService.addMenuDivider();

			if (chartTypeChange) {
				var chartTypes = Object.keys(stackStyleByType);
				var chartTypeSelector =
					knimeService.createMenuSelect('chartTypeSelector', _value.options.chartType, chartTypes, function() {
						_value.options.chartType = this.options[this.selectedIndex].value;
						chart.style(stackStyleByType[_value.options.chartType]);
						chart.update();
					});
				knimeService.addMenuItem('Chart Type:', 'bathtub', chartTypeSelector);
			}

			if (interpolationEdit) {
				var interpolationMethods = [ 'basis', 'linear', 'step' ];
				var interpolationMethodSelector =
					knimeService.createMenuSelect('interpolationMethodSelector', _value.options.interpolation, interpolationMethods, function() {
						_value.options.interpolation = this.options[this.selectedIndex].value;
						chart.interpolate(_value.options.interpolation);
						chart.update();
					});
				knimeService.addMenuItem('Interpolation:', 'line-chart', interpolationMethodSelector);
			}
		}

		// Controls Legend and Interactive Guideline
		var legendToggle = _representation.options.enableLegendToggle;
		var interactiveGuidelineToggle = _representation.options.enableInteractiveGuidelineToggle;
		if (legendToggle || interactiveGuidelineToggle) {
			knimeService.addMenuDivider();

			if (legendToggle) {
				var legendCheckbox = knimeService.createMenuCheckbox(
						'legendCheckbox', _value.options.legend, function() {
							_value.options.legend = this.checked;
							drawChart();
						});
				knimeService.addMenuItem('Legend:', 'info-circle', legendCheckbox);
			}

			if (interactiveGuidelineToggle) {
				var interactiveGuidelineCheckbox = knimeService.createMenuCheckbox(
								'interactiveGuidelineCheckbox',
								_value.options.interactiveGuideline,
								function() {
									_value.options.interactiveGuideline = this.checked;
									drawChart();
								});

				knimeService.addMenuItem('Pop-up', 'comment',
						interactiveGuidelineCheckbox);
			}
		}

		// Controls filter event checkbox.
		debugger;
		var subscribeFilterToggle = _representation.options.subscribeFilterToggle;
		if (knimeService.isInteractivityAvailable() && subscribeFilterToggle) {
			knimeService.addMenuDivider();
			var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');

			var subFilCheckbox = knimeService.createMenuCheckbox(
					'filterCheckbox', _value.options.subscribeFilter, function() {
						_value.options.subscribeFilter = this.checked;
						toggleFilter();
					});
			knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);
		}
	};

	view.validate = function() {
		return true;
	}

	view.getComponentValue = function() {
		// Save disabled-state of the series from the chart if:
		//   - it was saved in _value before
		//   - some series are disabled

		var container = d3.select("#svgContainer");
		var disabled = container.selectAll('g .nv-series').data().map(function(o) { return !!o.disabled })

		if  (("disabled" in _value.options) || disabled.some(Boolean)) {
			_value.options.disabled = disabled;
		}

		return _value;
	}

	view.getSVG = function() {
		// inline global style declarations for SVG export
		var styles = document.styleSheets;
		for (i = 0; i < styles.length; i++) {
			if (!styles[i].cssRules && styles[i].rules) {
				styles[i].cssRules = styles[i].rules;
			}
			// empty style declaration
			if (!styles[i].cssRules)
				continue;

			for (var j = 0; j < styles[i].cssRules.length; j++) {
				var rule = styles[i].cssRules[j];
				// rule.selectorText might not be defined for print media queries.
				if (typeof rule.selectorText !== "undefined") {
					d3.selectAll(rule.selectorText).each(function() {
						for (var k = 0; k < rule.style.length; k++) {
							var curStyle = this.style
									.getPropertyValue(rule.style[k]);
							var curPrio = this.style
									.getPropertyPriority(rule.style[k]);
							var rulePrio = rule.style
									.getPropertyPriority(rule.style[k]);
							// only overwrite style if not set or
							// priority is overruled
							if (!curStyle || (curPrio != "important" && rulePrio === "important")) {
								d3.select(this).style(
										rule.style[k],
										rule.style[rule.style[k]]);
							}
						}
					});
				}
			}
		}
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
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	};

	return view;

}());
