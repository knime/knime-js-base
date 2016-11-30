(streamgraph_namespace = function() {

	var view = {};
	var _representation, _value;
	var _data = {};
	var _colorRange;
	var layoutContainer;
	var MIN_HEIGHT = 300, MIN_WIDTH = 400;
	var chart, svg;
	var knimeTable1, knimeTable2;

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

		// Error messages for missing inputs.
		// TODO: is there a nicer way than using alert-modals?

		// Check if there is data in first table.
		if (columnKeys.length === 0 || knimeTable1.getNumRows() === 0) {
			alert("No data given.");
			return;
		}

		// Check if table2 has right number of rows.
		if (knimeTable2 !== null
				&& knimeTable1.getNumColumns() !== knimeTable2.getNumRows()) {

			alert("Number of objects is not equal between data port one and two.");
		}

		if (_representation.options.enableViewControls) {
			drawControls();
		}
		setColors();
		transformData();
		drawChart(false);
	}

	var drawChart = function(redraw) {
		/*
		 * Parse the options.
		 */

		var optTitle = _value.options["title"];
		var optSubtitle = _value.options["subtitle"];

		var stackStyle = _value.options.stackStyle;
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
		svg = svgContainer.append("svg").attr("id", "svg").style("font-family",
				"sans-serif").attr("width", width).attr("height", height);

		// TODO
		// set svg dimensions (apache batik needs width/height to be declared)
		// if (!runningInView) {
		if (optFullscreen) {
			svg.attr("width", optWidth);
			svg.attr("height", optHeight);
		} else {
			svg.attr("width", "100%");
			svg.attr("height", "100%");
		}

		// create the streamgraph
		nv.addGraph(function() {
			chart = nv.models.stackedAreaChart()
				.x(function(d) { return d[0]; })
				.y(function(d) { return d[1]; })
				.color(_colorRange)
				.interpolate(interpolation)
				.style(stackStyle)
				.showControls(false)
				.margin({ "top" : 10, "bottom" : 10 })
				.showLegend(Boolean(enableLegend))

			// using if-clause because there is strange behavior otherwise
			if (enableInteractiveGuideline) {
				chart.useInteractiveGuideline(true);
			} else {
				chart.interactive(false);
			}

			//Format x-axis.
			// TODO: next 5 lines...
			var xAxisFormatter;
			if (true) { // xAxis-Column is date
				// TODO: check if _value.options.dateFormat is valid format
				xAxisFormatter = new function() {
					// TODO: import moment.js
					return moment(date).format(_value.options.dateFormat);
				};
			} else {
				
			}
			
			chart.xAxis
				.ticks(_value.options.xAxisTicks)
				.tickFormat(xAxisFormatter);

			//Format y-axis.
			chart.yAxis
				.ticks(_value.options.yAxisTicks)
				.tickFormat(d3.format(".2s"));

			updateTitles(false);

			svg.datum(_data).call(chart);

			nv.utils.windowResize(chart.update);

			return chart;
		});
	}

	// transform the tabular format into a JSON format
	// TODO: this could be done in Java.
	var transformData = function() {
		_data = [];
		var columns = _representation.options.columns
		for (var i = 0; i < columns.length; i++) {
			var columnKey = columns[i];
			var columnIndex = knimeTable1.getColumnNames().indexOf(columnKey);

			_data.push({
				"key" : columnKey,
				"values" : knimeTable1.getColumn(columnIndex).map(
						function(d, i) {
							return [ i, d ];
						})
			});
		}
	};

	var setColors = function() {
		// Set color scale: custom or default.
		var colorScale = [];
		var columns = _representation.options.columns;
		var customColors = _value.options.customColors;
		if (customColors && knimeTable2 !== null) {
			var rowColors = knimeTable2.getRowColors();
			var numColumns = columns.length;
			for (var i = 0; i < numColumns; i++) {
				setColors
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

		if (!_representation.options.enableViewControls)
			return;

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

		if (_representation.options.enableStackStyleChange) {
			knimeService.addMenuDivider();

			var stackStyles = [ 'stack', 'expand', 'stream', 'stream-center'];
			var stackStyleSelector =
				knimeService.createMenuSelect('stackStyleSelector', _value.options.stackStyle, stackStyles, function() {
					_value.options.stackStyle = this.options[this.selectedIndex].value;
					chart.style(_value.options.stackStyle);
					chart.update();
				});
			knimeService.addMenuItem('Stack style:', 'bathtub', stackStyleSelector);		
		}

		
		
		// Interpolation method Configuration
		if (_representation.options.enableInterpolationMethodEdit) {
			knimeService.addMenuDivider();

			var interpolationMethods = [ 'linear', 'step', 'basis' ];
			var interpolationMethodSelector =
				knimeService.createMenuSelect('interpolationMethodSelector', _value.options.interpolation, interpolationMethods, function() {
					_value.options.interpolation = this.options[this.selectedIndex].value;
					chart.interpolate(_value.options.interpolation);
					chart.update();
				});
			knimeService.addMenuItem('Interpolation:', 'line-chart', interpolationMethodSelector);
		}

		// Controls for toggling certain options
		var customColorToggle = _representation.options.enableCustomColorToggle;
		var legendToggle = _representation.options.enableLegendToggle;
		var interactiveGuidelineToggle = _representation.options.enableInteractiveGuidelineToggle;
		if (customColorToggle || legendToggle || interactiveGuidelineToggle) {
			knimeService.addMenuDivider();

			// TODO: Check: Is  this working?
			if (customColorToggle) {
				var customColorCheckbox = knimeService.createMenuCheckbox(
						'customColorCheckbox', _value.options.customColors,
						function() {
							_value.options.customColors = this.checked;
							setColors();
							chart.color(_colorRange);
							chart.update();
						});
				knimeService.addMenuItem('Custom colors:', 'paint-brush',
						customColorCheckbox);
			}

			// TODO: This is not working.
			if (legendToggle) {
				var legendCheckbox = knimeService.createMenuCheckbox(
						'legendCheckbox', _value.options.legend, function() {
							_value.options.legend = this.checked;
							chart.showLegend(_value.options.legend);
							chart.update();
						});
				knimeService.addMenuItem('Legend:', 'info-circle',
						legendCheckbox);
			}

			// TODO: This is not working.
			if (interactiveGuidelineToggle) {
				var interactiveGuidelineCheckbox = knimeService
						.createMenuCheckbox(
								'interactiveGuidelineCheckbox',
								_value.options.interactiveGuideline,
								function() {
									_value.options.interactiveGuideline = this.checked;

									// using if-clause because there is strange behavior otherwise
									if (_value.options.interactiveGuideline) {
										chart.useInteractiveGuideline(true);
									} else {
										chart.useInteractiveGuideline(false);
										chart.interactive(false);
									}

									chart.update();
								});
				knimeService.addMenuItem('Guideline:', 'bathtub',
						interactiveGuidelineCheckbox);
			}
		}
	};

	view.validate = function() {
		return true;
	}

	view.getComponentValue = function() {
		return _value;
	}

	// TODO: This does not work right now.
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
				d3
						.selectAll(rule.selectorText)
						.each(
								function() {
									for (var k = 0; k < rule.style.length; k++) {
										var curStyle = this.style
												.getPropertyValue(rule.style[k]);
										var curPrio = this.style
												.getPropertyPriority(rule.style[k]);
										var rulePrio = rule.style
												.getPropertyPriority(rule.style[k]);
										//only overwrite style if not set or priority is overruled
										if (!curStyle
												|| (curPrio != "important" && rulePrio == "important")) {
											d3.select(this).style(
													rule.style[k],
													rule.style[rule.style[k]]);
										}
									}
								});
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
