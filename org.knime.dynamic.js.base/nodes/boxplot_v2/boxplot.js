window.boxplot_namespace = (function () {
    var boxplot = {};
    var _data = {};
    var layoutContainer;
    var MIN_HEIGHT = 100, MIN_WIDTH = 100;
    var maxY = 0, minY = 0;
    var Y_TICK_COUNT = 5;
    var Y_LABEL_MAX_WIDTH = 200;
    var _representation, _value;
    var drawControls, drawChart, updateTitle, updateSubtitle, processMissingValues, resize;

    var MISSING_VALUES_ONLY = 'missingValuesOnly';
    var IGNORED_MISSING_VALUES = 'ignoredMissingValues';
    var NO_DATA_AVAILABLE = 'noDataAvailable';
    var NO_DATA_COLUMN = 'noDataColumn';

    boxplot.init = function (representation, value) {
        // Store value and representation for later
        _value = value;
        _representation = representation;

        // No numeric columns available?
        if (_representation.options.columns.length == 0) {
            alert('No numeric columns selected');
            return;
        }

        // If no column to show is selected yet, we take the first from all candidates
        if (!_value.options.numCol) {
            _value.options.numCol = _representation.options.columns[0];
        }

        d3.select('html').style('width', '100%').style('height', '100%');
        d3.select('body').style('width', '100%').style('height', '100%');

        var body = d3.select('body');

        // Create container for our content
        layoutContainer = body.append('div')
            .attr('id', 'layoutContainer')
            .attr('class', 'knime-layout-container')
            .style('min-width', MIN_WIDTH + 'px')
            .style('min-height', MIN_HEIGHT + 'px');

        // Size layout container based on sizing settings
        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            layoutContainer.style('width', '100%')
                .style('height', '100%');
        } else {
            layoutContainer.style('width', _representation.options.svg.width + 'px')
                .style('height', _representation.options.svg.height + 'px');
        }

        // Add SVG element
        var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        layoutContainer[0][0].appendChild(svg1);

        var d3svg = d3.select('svg')
            .style('display', 'block');
        // Add rectangle for background color
        d3svg.append('rect')
            .attr('id', 'bgr')
            .attr('fill', _representation.options.backgroundColor);

        // Append a group for the plot and add a rectangle for the data area background
        d3svg.append('g')
            .attr('id', 'plotG')
            .append('rect')
            .attr('id', 'da')
            .attr('fill', _representation.options.daColor);

        // Title
        d3svg.append('text')
            .attr('id', 'title')
            .attr('class', 'knime-title')
            .attr('x', 20)
            .attr('y', 30)
            .text(_value.options.title);

        // Subtitle
        d3svg.append('text')
            .attr('id', 'subtitle')
            .attr('class', 'knime-subtitle')
            .attr('x', 20)
            .text(_value.options.subtitle);
        // y attr is set in drawChart

        drawChart();
        if (_representation.options.enableViewControls) {
            drawControls();
        }

        if (window.parent.KnimePageLoader) {
            window.parent.KnimePageLoader.autoResize(window.frameElement.id);
        }
    };

    drawControls = function () {
        if (_representation.options.displayFullscreen) {
            knimeService.allowFullscreen();
        }

        if (!_representation.options.enableViewControls) {
            return;
        }

        if (_representation.options.enableTitleEdit || _representation.options.enableSubtitleEdit) {
            if (_representation.options.enableTitleEdit) {
                var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.options.title, updateTitle, true);
                knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
            }

            if (_representation.options.enableSubtitleEdit) {
                var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.options.subtitle, updateSubtitle, true);
                knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
            }

            if (!_representation.options.multi && _representation.options.enableColumnSelection) {
                knimeService.addMenuDivider();
            }
        }

        if (!_representation.options.multi && _representation.options.enableColumnSelection) {
            var colSelect = knimeService.createMenuSelect('columnSelect', _value.options.numCol, _representation.options.columns, function () {
                _value.options.numCol = this.value;
                drawChart();
            });
            knimeService.addMenuItem('Selected column:', 'long-arrow-up', colSelect);
        }
    };

    updateTitle = function () {
        var hadTitle = (_value.options.title.length > 0);
        _value.options.title = document.getElementById('chartTitleText').value;
        var hasTitle = (_value.options.title.length > 0);
        if (hasTitle != hadTitle) {
            // if the title appeared or disappeared, we need to resize the chart
            drawChart(true);
        }
        d3.select('#title').text(_value.options.title);
    };

    updateSubtitle = function () {
        var hadTitle = (_value.options.subtitle.length > 0);
        _value.options.subtitle = document.getElementById('chartSubtitleText').value;
        var hasTitle = (_value.options.subtitle.length > 0);
        if (hasTitle != hadTitle) {
            // if the subtitle appeared or disappeared, we need to resize the chart
            drawChart(true);
        }
        d3.select('#subtitle').text(_value.options.subtitle);
    };

    // Draws the chart. If resizing is true, there are no animations.
    drawChart = function (resizing) {

        // Select the data to show
        if (_representation.options.multi) {
            _data = _representation.inObjects[0].stats;
        } else {
            _data = {};
            var numCol = _value.options.numCol;
            _data[numCol] = _representation.inObjects[0].stats[numCol];
            if (_data[numCol] === undefined) {
                delete _data[numCol];
            }
        }

        // Find the maximum y-value for the axis
        maxY = Number.NEGATIVE_INFINITY;
        minY = Number.POSITIVE_INFINITY;
        for (var key in _data) {
            maxY = Math.max(_data[key].max, maxY);
            minY = Math.min(_data[key].min, minY);
        }

        // Calculate the correct chart width
        var cw = Math.max(MIN_WIDTH, _representation.options.svg.width);
        var ch = Math.max(MIN_HEIGHT, _representation.options.svg.height);

        var chartWidth, chartHeight;
        // If we are fullscreen, we set the chart width to 100%
        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            chartWidth = '100%';
            chartHeight = '100%';
        } else {
            chartWidth = cw + 'px';
            chartHeight = ch + 'px';
        }

        // The margins for the plot area
        var topMargin = 10;
        if (_value.options.title && _value.options.subtitle) {
            topMargin += 50;
        } else if (_value.options.title) {
            topMargin += 36;
        } else if (_value.options.subtitle) {
            topMargin += 26;
        }

        var margins = {
            top: topMargin,
            bottom: 40,
            right: 10
        };

        d3.select('#subtitle').attr('y', topMargin - 14);

        var d3svg = d3.select('svg')
            .attr({ width: cw, height: ch })
            .style({ width: chartWidth, height: chartHeight });

        // Calculate height of the plot area (without x-axis)
        var h = Math.max(50, parseInt(d3svg.style('height'), 10) - margins.top - margins.bottom);

        // y-axis scale
        var yScale = d3.scale.linear().domain([minY, maxY]).range([h, 0]).nice();

        // determine required margin-left
        var yLabels = yScale.ticks(Y_TICK_COUNT).map(yScale.tickFormat(Y_TICK_COUNT)).map(String);
        var maxYLabelWidth = knimeService.measureAndTruncate(yLabels, {
            container: d3svg.node(),
            classes: 'knime-tick-label',
            maxWidth: Y_LABEL_MAX_WIDTH
        }).max.maxWidth;
        margins.left = 15 + maxYLabelWidth;

        // Calculate width of the plot area (without y-axis)
        var w = Math.max(50, parseInt(d3svg.style('width'), 10) - margins.left - margins.right);

        // x-axis scale
        var xScale = d3.scale.ordinal().domain(d3.keys(_data)).rangeBands([0, w], 0.75, 0.5);

        // Position the plot group based on the margins
        var plotG = d3svg.select('#plotG')
            .attr('transform', 'translate(' + margins.left + ',' + margins.top + ')');

        // Resize background rectangles
        plotG.select('#da').attr({
            width: w,
            height: h + 5
        });
        d3svg.select('#bgr').attr({
            width: w + margins.left + margins.right,
            height: h + margins.top + margins.bottom
        });

        // d3 axes
        var xAxis = d3.svg.axis().scale(xScale)
            .orient('bottom');
        var yAxis = d3.svg.axis().scale(yScale)
            .orient('left').ticks(Y_TICK_COUNT);
        // Remove axes so they are redrawn
        d3.selectAll('.axis').remove();

        // Append and style x-axis
        var d3XAxis = plotG.append('g')
            .attr('class', 'x axis knime-x knime-axis')
            .attr('transform', 'translate(0,' + (h + 5) + ')')
            .call(xAxis);
        d3XAxis.selectAll('line,path')
            .attr('fill', 'none')
            .attr('stroke', 'black')
            .attr('shape-rendering', 'crispEdges');

        // Append and style y-axis
        var d3YAxis = plotG.append('g')
            .attr('class', 'y axis knime-y knime-axis')
            .call(yAxis);
        d3YAxis.selectAll('line,path')
            .attr('fill', 'none')
            .attr('stroke', 'black')
            .attr('shape-rendering', 'crispEdges');

        d3.selectAll('.domain')
            .classed('knime-axis-line', true);
        var ticks = d3.selectAll('.tick')
            .classed('knime-tick', true);
        ticks.selectAll('line')
            .classed('knime-tick-line', true);
        ticks.selectAll('text')
            .classed('knime-tick-label', true);

        // Animate only when running in view and not resizing
        var duration = (_representation.runningInView && !resizing) ? 500 : 0;

        // Create a selection for each box with data that we created at the beginning

        var boxG = plotG.selectAll('g.box')
            .data(d3.entries(_data).map(function (d) {
                d.value.valid = d.value.upperQuartile >= d.value.lowerQuartile;
                return d;
            }), function (d) {
                return (_representation.options.multi) ? d.key : '__dummy__';
            });

        // Remove boxes that are not in the data anymore
        boxG.exit().remove();

        // Append a group element for each new box and shift it according to the class
        var box = boxG.enter().append('g')
            .attr('class', 'box')
            .attr('transform', function (d) { return 'translate(' + xScale(d.key) + ',0)'; });

        // Transition all boxes to their position
        d3.selectAll('.box').transition().duration(duration)
            .attr('transform', function (d) { return 'translate(' + xScale(d.key) + ',0)'; });

        // The main rectangle for the box
        box.append('rect')
            .attr('class', 'boxrect')
            .attr('stroke', 'black')
            .attr('fill', _representation.options.boxColor || 'none');

        // Update the box according to the data
        boxG.selectAll('.boxrect')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('y', function (d) { return yScale(d.value.upperQuartile); })
            .attr('height', function (d) { return yScale(d.value.lowerQuartile) - yScale(d.value.upperQuartile); })
            .attr('width', xScale.rangeBand());

        // The middle of the box on the x-axis
        var middle = xScale.rangeBand() / 2;

        // Text for the upper quartile
        box.append('text')
            .attr('x', -5)
            .attr('text-anchor', 'end')
            .attr('class', 'uqText knime-label');
        boxG.selectAll('.uqText')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('y', function (d) { return yScale(d.value.upperQuartile) + 3; })
            .text(function (d) { return Math.round(d.value.upperQuartile * 100) / 100; });

        // Text for the lower quartile
        box.append('text')
            .attr('x', -5)
            .attr('text-anchor', 'end')
            .attr('class', 'lqText knime-label');
        boxG.selectAll('.lqText')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('y', function (d) { return yScale(d.value.lowerQuartile) + 3; })
            .text(function (d) { return Math.round(d.value.lowerQuartile * 100) / 100; });


        // Median
        box.append('line')
            .attr('stroke', 'black')
            .attr('stroke-width', 3)
            .attr('x1', '0')
            .attr('class', 'median');

        boxG.selectAll('.median')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('x2', xScale.rangeBand())
            .attr('y1', function (d) { return yScale(d.value.median); })
            .attr('y2', function (d) { return yScale(d.value.median); });

        box.append('text')
            .attr('class', 'medianText knime-label');

        boxG.selectAll('.medianText')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('x', xScale.rangeBand() + 5)
            .attr('y', function (d) { return yScale(d.value.median) + 3; })
            .text(function (d) { return Math.round(d.value.median * 100) / 100; });

        // Upper whisker
        box.append('line')
            .attr('stroke', 'black')
            .attr('class', 'uwL1');

        boxG.selectAll('.uwL1')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('opacity', function (d) { return d.value.valid ? 1 : 0; })
            .attr('x1', middle)
            .attr('x2', middle)
            .attr('stroke-dasharray', '5,5')
            .attr('y1', function (d) { return yScale(d.value.upperQuartile); })
            .attr('y2', function (d) { return yScale(d.value.upperWhisker); });


        box.append('line')
            .attr('stroke', 'black')
            .attr('x1', '0')
            .attr('class', 'uwL2');

        boxG.selectAll('.uwL2')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('opacity', function (d) { return d.value.valid ? 1 : 0; })
            .attr('x2', xScale.rangeBand())
            .attr('y1', function (d) { return yScale(d.value.upperWhisker); })
            .attr('y2', function (d) { return yScale(d.value.upperWhisker); });

        box.append('text')
            .attr('class', 'uwText knime-label');

        boxG.selectAll('.uwText')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('opacity', function (d) { return d.value.valid ? 1 : 0; })
            .attr('x', xScale.rangeBand() + 5)
            .attr('y', function (d) { return yScale(d.value.upperWhisker) + 10; })
            .text(function (d) { return Math.round(d.value.upperWhisker * 100) / 100; });

        // Lower whisker
        box.append('line')
            .attr('stroke', 'black')
            .attr('class', 'ulL1');

        boxG.selectAll('.ulL1')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('opacity', function (d) { return d.value.valid ? 1 : 0; })
            .attr('x1', middle)
            .attr('x2', middle)
            .attr('stroke-dasharray', '5,5')
            .attr('y1', function (d) { return yScale(d.value.lowerQuartile); })
            .attr('y2', function (d) { return yScale(d.value.lowerWhisker); });

        box.append('line')
            .attr('stroke', 'black')
            .attr('x1', '0')
            .attr('class', 'ulL2');

        boxG.selectAll('.ulL2')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('opacity', function (d) { return d.value.valid ? 1 : 0; })
            .attr('x2', xScale.rangeBand())
            .attr('y1', function (d) { return yScale(d.value.lowerWhisker); })
            .attr('y2', function (d) { return yScale(d.value.lowerWhisker); });

        box.append('text')
            .attr('class', 'ulText knime-label');

        boxG.selectAll('.ulText')
            .data(function (d) { return [d]; })
            .transition().duration(duration)
            .attr('opacity', function (d) { return d.value.valid ? 1 : 0; })
            .attr('x', xScale.rangeBand() + 5)
            .attr('y', function (d) { return yScale(d.value.lowerWhisker) - 3; })
            .text(function (d) { return Math.round(d.value.lowerWhisker * 100) / 100; });

        // Mild outliers
        var outl = boxG.selectAll('circle.mo')
            .data(function (d) { return d.value.mildOutliers; });

        outl.enter().append('circle')
            .attr('class', 'mo')
            .attr('r', 5)
            .attr('fill', _representation.options.daColor)
            .attr('stroke', 'black')
            .attr('cx', middle)
            .attr('cy', function (d) { return yScale(d.value); })
            .append('title')
            .attr('class', 'knime-label')
            .text(function (d) { return d.rowKey; });

        outl.transition().duration(duration)
            .attr('cx', middle)
            .attr('cy', function (d) { return yScale(d.value); });

        outl.exit().transition().style('opacity', 0).each('end', function () { d3.select(this).remove(); });

        // Extreme outliers
        var exoutl = boxG.selectAll('g.eo')
            .data(function (d) { return d.value.extremeOutliers; });

        var enterG = exoutl.enter().append('g')
            .attr('class', 'eo')
            .attr('transform', function (d) { return 'translate(' + middle + ',' + yScale(d.value) + ')'; });

        var crossSize = 4;

        enterG.append('line')
            .attr({
                x1: -crossSize,
                y1: -crossSize,
                x2: crossSize,
                y2: crossSize,
                'stroke-width': 1.5,
                'stroke-linecap': 'round'
            })
            .append('title')
            .attr('class', 'knime-label')
            .text(function (d) { return d.rowKey; });

        enterG.append('line')
            .attr({
                x1: -crossSize,
                y1: crossSize,
                x2: crossSize,
                y2: -crossSize,
                'stroke-width': 1.5,
                'stroke-linecap': 'round'
            })
            .append('title')
            .attr('class', 'knime-label')
            .text(function (d) { return d.rowKey; });

        exoutl.transition().duration(duration)
            .attr('transform', function (d) { return 'translate(' + middle + ',' + yScale(d.value) + ')'; });

        // Fade out outliers
        exoutl.exit().transition().style('opacity', 0).each('end', function () { d3.select(this).remove(); });

        processMissingValues();

        // Set resize handler
        if (_representation.options.svg.fullscreen) {
            var win = document.defaultView || document.parentWindow;
            win.onresize = resize;
        }
    };

    resize = function (event) {
        drawChart(true);
    };

    processMissingValues = function () {
        if (!_representation.options.showWarnings) {
            return;
        }

        knimeService.clearWarningMessage(NO_DATA_AVAILABLE);
        knimeService.clearWarningMessage(MISSING_VALUES_ONLY);
        knimeService.clearWarningMessage(IGNORED_MISSING_VALUES);
        knimeService.clearWarningMessage(NO_DATA_COLUMN);

        var excludedDataCols = _representation.inObjects[0].excludedDataCols;
        var numMissValPerCol = _representation.inObjects[0].numMissValPerCol;
        var dataCols = _representation.options.columns;

        // temporary workaround for being able to select a data column which was not included in the node settings
        if (dataCols.indexOf(_value.options.numCol) == -1) {
            knimeService.setWarningMessage('No chart was generated since the selected column was not included in the node configuration dialog.\nPlease choose another column or add the selected column to the list of included columns.', NO_DATA_COLUMN);
            return;
        }

        if (_representation.options.multi) {
            // plot multiple boxes
            if (excludedDataCols.length == dataCols.length) {
                knimeService.setWarningMessage('No chart was generated since all data columns have only missing values or special doubles.\nRe-run the workflow with different data.', NO_DATA_AVAILABLE);
            } else {
                if (!_representation.options.reportOnMissingValues) {
                    return;
                }
                if (excludedDataCols.length > 0) {
                    knimeService.setWarningMessage('Following data columns contain only missing values or special doubles and were excluded from the view:\n    ' + excludedDataCols.join('\n    '), MISSING_VALUES_ONLY);
                }
                if (Object.keys(numMissValPerCol).length > 0) {
                    var str = '';
                    for (var key in numMissValPerCol) {
                        if (numMissValPerCol.hasOwnProperty(key)) {
                            str += '    ' + key + ' - ' + numMissValPerCol[key] + ' missing value(s) or special double(s)\n';
                        }
                    }
                    knimeService.setWarningMessage('Missing values or special doubles ignored during statistics calculations per data column:\n' + str, IGNORED_MISSING_VALUES);
                }
            }
        } else {
            // plot a box for only one data column
            if (excludedDataCols.indexOf(_value.options.numCol) != -1) {
                knimeService.setWarningMessage('No chart was generated since the selected data column has only missing values or special doubles.\nChoose another data column or re-run the workflow with different data.', NO_DATA_AVAILABLE);
            } else if (numMissValPerCol[_value.options.numCol] !== undefined && _representation.options.reportOnMissingValues) {
                knimeService.setWarningMessage('Missing values or special doubles ignored during statistics calculations:\n' + numMissValPerCol[_value.options.numCol] + ' missing value(s) or special double(s).', IGNORED_MISSING_VALUES);
            }
        }
    };

    boxplot.getSVG = function () {
        var svgElement = d3.select('svg')[0][0];
        knimeService.inlineSvgStyles(svgElement);
        // Return the SVG as a string.
        return (new XMLSerializer()).serializeToString(svgElement);
    };

    boxplot.validate = function () {
        return true;
    };

    boxplot.getComponentValue = function () {
        return _value;
    };

    return boxplot;

})();
