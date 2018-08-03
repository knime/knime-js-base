(heatmap_namespace = function () {

    var heatmap = {};
    var _representation, _value, _table;


    heatmap.init = function (representation, value) {

        debugger;

        if (!representation.inObjects[0]) {
            //todo: error
            return;
        }

        if (!representation.options.heatmapCols.length) {
            //todo: error
            return;
        }

        _representation = representation;
        _value = value;
        _table = new kt();

        _table.setDataTable(representation.inObjects[0]);

        createPage();
    }

    /**
     * Start Javascript for the node
     */
    function createPage() {
        // Settings
        var imageColumnName = 'svg';
        var colorRange = ['#FF0700', '#fff', '#00FF56'];
        var itemSize = 18;
        var margin = { top: 150, left: 100 };

        // State managment objects
        viewValues = {
            selectedRows: [],
            scaleType: 'linear',
            currentPage: 1,
            rowsPerPage: 100,
            tooltipsEnabled: true,
            zoomEnabled: true,
            selectionEnabled: false,
            initialZoomLevel: {
                x: 0,
                y: 0,
                k: 1
            }
        };

        run();

        // Run everything
        function run() {
            // prepare data
            var paginationData = createPagination(_table.getRows());

            // prepare Html
            var pagination = getPaginationHtml(paginationData);
            var wrapper = '<div class="heatmap"></div>';
            var toolTip = '<div class="tooltip"></div>';
            var controls = getControlHtml();

            var body = document.getElementsByTagName('body')[0];
            body.innerHTML = controls + wrapper + pagination + toolTip;

            // Build svg based on the current data
            buildSvg(paginationData.rows);

            // Events
            registerDomEvents();
        }

        function getControlHtml() {
            return (
                '<form class="wrapper">\
             <div class="form-group col-xs-3 rowsPerPage">\
                     <label for="rowsPerPage">Rows per page</label>\
                     <select id="rowsPerPage" class="form-control">\
                     <option ' +
                (viewValues.rowsPerPage === 50 ? 'selected ' : '') +
                'value="50">50</option>\
                         <option ' +
                (viewValues.rowsPerPage === 100 ? 'selected ' : '') +
                'value="100">100</option>\
                         <option ' +
                (viewValues.rowsPerPage === 200 ? 'selected ' : '') +
                'value="200">200</option>\
             <option ' +
                (viewValues.rowsPerPage === 500 ? 'selected ' : '') +
                'value="500">500</option>\
             <option ' +
                (viewValues.rowsPerPage === 1000 ? 'selected ' : '') +
                'value="1000">1000</option>\
                     </select>\
                 </div>\
             <div class="form-group col-xs-3 scaleselector">\
                     <label for="scale">Scale type</label>\
                     <select id="scale" class="form-control">\
                         <option ' +
                (viewValues.scaleType === 'linear' ? 'selected ' : '') +
                'value="linear">Linear</option>\
                         <option ' +
                (viewValues.scaleType === 'quantize' ? 'selected ' : '') +
                'value="quantize">Quantize</option>\
                     </select>\
                 </div>\
                 <div class="checkbox enableTooltips col-xs-3">\
                 <label>\
                     <input type="checkbox" ' +
                (viewValues.tooltipsEnabled ? 'checked ' : '') +
                '> Show tooltips\
                 </label>\
                 </div>\
                 <div class="checkbox enableZoom col-xs-3">\
                 <label>\
                     <input type="checkbox" ' +
                (viewValues.zoomEnabled ? 'checked ' : '') +
                '> Enable Drag and Zoom\
                 </label>\
                 </div>\
                 <div class="checkbox enableSelection col-xs-3">\
                 <label>\
                     <input type="checkbox" ' +
                (viewValues.selectionEnabled ? 'checked ' : '') +
                '> Enable Selection\
                 </label>\
                 </div>\
                 <button type="submit" class="btn btn-default hidden">Submit</button>\
             </form>'
            );
        }

        function getPaginationHtml(pagination) {
            if (pagination.pageCount <= 1) {
                return '';
            }
            var html = '<div class="paginationWrapper"><ul class="pagination">';

            if (pagination.prev) {
                html += '<li><a href="#' + pagination.prev + '">&laquo;</a></li>';
            } else {
                html += '<li class="disabled"><span>&laquo;</span></li>';
            }

            for (var i = 1; i <= pagination.pageCount; i++) {
                html +=
                    '<li class="' +
                    (viewValues.currentPage === i ? 'active' : '') +
                    '"><a href="#' +
                    i +
                    '">' +
                    i +
                    '</a></li>';
            }

            if (pagination.next) {
                html += '<li><a href="#' + pagination.next + '">&raquo;</a></li>';
            } else {
                html += '<li class="disabled"><span>&raquo;</span></li>';
            }
            html += '</ul></div>';
            return html;
        }

        function registerDomEvents() {
            var body = document.getElementsByTagName('body')[0];

            var pagination = body.querySelector('.pagination');
            if (pagination) {
                body.querySelector('.pagination').addEventListener(
                    'click',
                    function (e) {
                        if (e.target.tagName === 'A') {
                            var pageNumber = parseInt(
                                e.target.getAttribute('href').substr(1),
                                10
                            );
                            viewValues.currentPage = pageNumber;
                            run();
                        }
                    }
                );
            }
            body.querySelector('.scaleselector').addEventListener(
                'change',
                function (e) {
                    viewValues.scaleType = e.target.value;
                    run();
                }
            );
            body.querySelector('.rowsPerPage').addEventListener('change', function (
                e
            ) {
                viewValues.rowsPerPage = parseInt(e.target.value, 10);
                run();
            });
            body.querySelector('.enableTooltips input').addEventListener(
                'change',
                function (e) {
                    viewValues.tooltipsEnabled = e.target.checked;
                }
            );
            body.querySelector('.enableSelection input').addEventListener(
                'change',
                function (e) {
                    viewValues.selectionEnabled = e.target.checked;
                }
            );
            body.querySelector('.enableZoom input').addEventListener(
                'change',
                function (e) {
                    viewValues.zoomEnabled = e.target.checked;
                    initializeZoom();
                }
            );
        }

        /**
         * Create very basic pagination data from rows
         * @param {Array} data 
         */
        function createPagination(data) {
            var pageCount = Math.ceil(data.length / viewValues.rowsPerPage);

            // jump to page 1 if total number of pages exceeds current page
            viewValues.currentPage =
                viewValues.currentPage <= pageCount ? viewValues.currentPage : 1;

            var currentPage = viewValues.currentPage;
            var nextPageRowEnd = viewValues.rowsPerPage * currentPage;
            var nextPageRowStart = viewValues.rowsPerPage * (currentPage - 1);
            var rows = data.slice(nextPageRowStart, nextPageRowEnd);

            return {
                rows: rows,
                pageCount: pageCount,
                next: nextPageRowEnd < data.length ? currentPage + 1 : false,
                prev: nextPageRowStart > 0 ? currentPage - 1 : false
            };
        }

        function formatData(rows) {
            var minimum = Number.POSITIVE_INFINITY;
            var maximum = Number.NEGATIVE_INFINITY;
            var images = [];
            var rowNames = [];
            var colNames = [];

            // Get valid indexes for heatmap columns by comparing them to input colNames
            repColNames = _representation.inObjects[0].spec.colNames;
            _representation.options.heatmapCols.map(function (hmColName) {
                colNames[repColNames.indexOf(hmColName)] = hmColName;
            });

            var allValues = rows.reduce(function (accumulator, row) {
                rowNames.push(row.rowKey);
                var rowIsSelected = viewValues.selectedRows.indexOf(row.rowKey) != -1; // a bit slow

                // Storing images in an separate array is enough
                if (imageColumnName) {
                    images[row.rowKey] = _table.getCell(row.rowKey, imageColumnName);
                }

                // Set values for each cell
                var vals = row.data.reduce(function (rowAcc, value, currentIndex) {
                    if (colNames[currentIndex] === undefined) {
                        return rowAcc;
                    }
                    var newItem = {};
                    newItem.y = row.rowKey;
                    newItem.x = colNames[currentIndex];
                    newItem.value = value;
                    newItem.initallySelected = rowIsSelected;

                    // Good opportunity to determine min and max
                    minimum = Math.min(minimum, newItem.value);
                    maximum = Math.max(maximum, newItem.value);
                    rowAcc.push(newItem);
                    return rowAcc;
                }, []);
                return accumulator.concat(vals);
            }, []);

            return {
                images: images,
                data: allValues,
                rowNames: rowNames,
                colNames: colNames,
                minimum: minimum,
                maximum: maximum
            };
        }

        function getLinearColorDomain(minimum, maximum) {
            var domain = [];
            var interpolator = d3.interpolateNumber(minimum, maximum);
            for (var i = 0; i < colorRange.length; i++) {
                domain.push(interpolator(i / (colorRange.length - 1)));
            }
            return domain;
        }

        function getScales(formattedDataset) {
            return {
                x: d3
                    .scaleBand()
                    .domain(formattedDataset.colNames)
                    .range([
                        margin.left,
                        formattedDataset.colNames.length * itemSize - 1 + margin.left
                    ]),
                y: d3
                    .scaleBand()
                    .domain(formattedDataset.rowNames)
                    .range([
                        margin.top,
                        formattedDataset.rowNames.length * itemSize - 1 + margin.top
                    ]),
                colorScale:
                    viewValues.scaleType === 'quantize'
                        ? d3
                            .scaleQuantize()
                            .domain([
                                formattedDataset.minimum,
                                formattedDataset.maximum
                            ])
                            .range(colorRange)
                        : d3
                            .scaleLinear()
                            .domain(
                                getLinearColorDomain(
                                    formattedDataset.minimum,
                                    formattedDataset.maximum
                                )
                            )
                            .range(colorRange)
            };
        }

        function getAxis(scales) {
            return {
                x: d3.axisTop(scales.x).tickFormat(function (d) {
                    return d;
                }),

                y: d3.axisLeft(scales.y).tickFormat(function (d) {
                    return d;
                })
            };
        }

        function formatImage(string) {
            return 'data:image/svg+xml;base64,' + btoa(string);
        }

        function initializeZoom() {
            var svgEl = getSvgElement();
            var svgD3 = d3.select(svgEl);

            var wrapper = svgD3.select(':scope .wrapper');
            var xAxisD3El = svgD3.select('.xAxis');
            var yAxisD3El = svgD3.select('.yAxis');

            // Zoom and pan
            var zoom = d3
                .zoom()
                .scaleExtent([0, 1])
                .on('zoom', function () {
                    var t = d3.event.transform;

                    // Limit zoom
                    t.x = d3.min([t.x, (1 - t.k) * margin.left]);
                    t.y = d3.min([t.y, (1 - t.k) * margin.top]);

                    // Save current zoom level
                    viewValues.initialZoomLevel = t;

                    xAxisD3El.attr(
                        'transform',
                        'translate(' +
                        t.x +
                        ', ' +
                        margin.top +
                        ') scale(' +
                        t.k +
                        ')'
                    );
                    yAxisD3El.attr(
                        'transform',
                        'translate(' +
                        margin.left +
                        ', ' +
                        t.y +
                        ') scale(' +
                        t.k +
                        ')'
                    );

                    wrapper.attr(
                        'transform',
                        'translate(' + t.x + ', ' + t.y + ') scale(' + t.k + ')'
                    );
                });

            if (viewValues.zoomEnabled) {
                svgD3.call(zoom);
            } else {
                svgD3.on('.zoom', null);
            }
            return zoom;
        }

        function showTooltip(e, innerHtml) {
            if (!viewValues.tooltipsEnabled && innerHtml) {
                return;
            }
            var tooltip = document.querySelector('.tooltip');
            tooltip.classList.add('active');
            e.target.classList.add('active');
            tooltip.innerHTML = innerHtml;
            tooltip.style.left = event.clientX + itemSize + 'px';
            tooltip.style.top = event.clientY - itemSize + 'px';
        }

        function hideTooltip() {
            var tooltip = document.querySelector('.tooltip');
            tooltip.classList.remove('active');
        }

        function buildSvg(rows) {
            var formattedDataset = formatData(rows);

            var scales = getScales(formattedDataset);
            var axis = getAxis(scales);

            var svg = d3
                .select('.heatmap')
                .append('svg');


            var viewport = svg
                .append('g')
                .attr('class', 'viewport')
                .attr('clip-path', 'url(#clip)');

            var wrapper = viewport.append('g').attr('class', 'wrapper');

            var defs = svg.append('defs');
            defs.append('clipPath')
                .attr('id', 'clip')
                .append('rect')
                .attr('y', margin.top)
                .attr('x', margin.left)
                .attr('width', '100%')
                .attr('height', '100%');

            var cells = wrapper
                .selectAll('rect')
                .data(formattedDataset.data)
                .enter()
                .append('g')
                .append('rect')
                .attr('class', 'cell')
                .attr('width', itemSize - 1)
                .attr('height', itemSize - 1)
                .attr('y', function (d) {
                    return scales.y(d.y);
                })
                .attr('x', function (d) {
                    return scales.x(d.x);
                })
                .attr('fill', function (d) {
                    return scales.colorScale(d.value);
                })
                .attr('selection', function (d) {
                    //initialize selection if already selected
                    return d.initallySelected ? 'active' : 'inactive';
                });

            var domWrapper = getSvgElement().querySelector('.wrapper');

            // Highlight mouseover cell and show tooltip
            domWrapper.addEventListener('mouseover', function (e) {
                if (!e.target.classList.contains('cell')) {
                    return;
                }

                var data = d3.select(e.target).data()[0];

                // Select rows
                if (event.button || event.which) {
                    selectCell(data);
                }

                toolTipInnerHTML =
                    '<span class="position">x:' +
                    data.x +
                    ' y:' +
                    data.y +
                    '</span><span class="value">' +
                    data.value +
                    '</span>';

                showTooltip(e, toolTipInnerHTML);
            });

            // Deactivation relies on gaps in the wrapper between the cells
            domWrapper.addEventListener('mouseout', function (e) {
                hideTooltip();
                e.target.classList.remove('active');
            });

            // Row selection
            domWrapper.addEventListener('mousedown', function (e) {
                if (e.target.tagName !== 'rect') {
                    return;
                }
                var data = d3.select(e.target).data()[0];
                selectCell(data);
            });

            // Append axis
            var maskAxis = defs.append('mask').attr('id', 'maskAxis');
            maskAxis
                .append('rect')
                .attr('y', 0)
                .attr('x', 0)
                .attr('width', '100%')
                .attr('height', '100%')
                .attr('fill', 'white');
            maskAxis
                .append('rect')
                .attr('y', 0)
                .attr('x', 0)
                .attr('width', margin.left + 1)
                .attr('height', margin.top + 1)
                .attr('fill', 'black');

            var axisWrapper = svg
                .append('g')
                .attr('class', 'axisWrapper')
                .attr('mask', 'url(#maskAxis)');
            axisWrapper
                .append('g')
                .attr('class', 'yAxis')
                .call(axis.y)
                .selectAll('text')
                .attr('font-weight', 'normal')
                .on('mouseover', function (d) {
                    if (!formattedDataset.images[d]) {
                        return;
                    }
                    d3.event.target.classList.add('active');
                    tooltipInnerHTML =
                        '<img src="' +
                        formatImage(formattedDataset.images[d]) +
                        '" alt/>';
                    showTooltip(d3.event, tooltipInnerHTML);
                })
                .on('mouseleave', function () {
                    hideTooltip();
                    d3.event.target.classList.remove('active');
                });

            axisWrapper
                .append('g')
                .attr('class', 'xAxis')
                .call(axis.x)
                .selectAll('text')
                .attr('font-weight', 'normal')
                .style('text-anchor', 'start')
                .attr('dx', '1em')
                .attr('dy', '.5em')
                .attr('transform', function (d) {
                    return 'rotate(-65)';
                });

            // Initialize zoom
            var zoom = initializeZoom();
            resetZoom(zoom);

            // Legend
            var legendWidth = 200;
            var legendHeight = 25;
            var legendGradient = defs
                .append('linearGradient')
                .attr('id', 'legendGradient')
                .attr('transform', 'translate(100, 100)');

            var legend = svg
                .append('g')
                .attr('transform', 'translate(' + margin.left + ', 0)');

            var colorDomain = getLinearColorDomain(
                formattedDataset.minimum,
                formattedDataset.maximum
            );

            // append a single rect to display a gradient
            legend
                .append('rect')
                .attr('width', legendWidth)
                .attr('height', legendHeight)
                .attr('fill', 'url(#legendGradient)');

            // set gradient stops
            if (viewValues.scaleType === 'linear') {
                for (var i = 0; i < colorDomain.length; i++) {
                    var percentage = (100 / (colorDomain.length - 1)) * i;
                    legendGradient
                        .append('stop')
                        .attr('offset', percentage + '%')
                        .attr(
                            'style',
                            'stop-opacity:1; stop-color:' +
                            scales.colorScale(colorDomain[i])
                        );
                }
            } else if (viewValues.scaleType === 'quantize') {
                var legendCellPercentage = 100 / colorDomain.length;
                var previousPercentage = 0;
                var interpolator = d3.interpolateNumber(
                    formattedDataset.minimum,
                    formattedDataset.maximum
                );
                var tickValues = [];
                tickValues.push(formattedDataset.minimum, formattedDataset.maximum);

                for (var i = 0; i < colorDomain.length; i++) {
                    var currentPercentage = legendCellPercentage * (i + 1);

                    tickValues.push(interpolator(currentPercentage / 100));

                    legendGradient
                        .append('stop')
                        .attr('offset', previousPercentage + '%')
                        .attr(
                            'style',
                            'stop-opacity:1; stop-color:' +
                            scales.colorScale(colorDomain[i])
                        );
                    legendGradient
                        .append('stop')
                        .attr('offset', currentPercentage + '%')
                        .attr(
                            'style',
                            'stop-opacity:1; stop-color:' +
                            scales.colorScale(colorDomain[i])
                        );
                    previousPercentage = currentPercentage;
                }
            }

            var legendScale = d3
                .scaleLinear()
                .domain([formattedDataset.minimum, formattedDataset.maximum])
                .range([0, legendWidth]);

            var legendAxis = d3
                .axisBottom(legendScale)
                .tickValues(tickValues || colorDomain)
                .tickFormat(function (d) {
                    return Math.round(d * 1000) / 1000;
                });

            legend
                .append('g')
                .attr('transform', 'translate(0, ' + legendHeight + ')')
                .attr('class', 'legendAxis')
                .call(legendAxis)
                .selectAll('text')
                .attr('font-weight', 'normal');
        }

        function resetZoom(zoom) {
            var svg = d3.select('.heatmap svg');
            zoom.transform(svg, function () {
                return d3.zoomIdentity
                    .translate(
                        viewValues.initialZoomLevel.x,
                        viewValues.initialZoomLevel.y
                    )
                    .scale(viewValues.initialZoomLevel.k);
            });
        }

        function selectCell(d) {
            var selectedRowId = d.y;
            d3.selectAll('.cell').attr('selection', function (d) {
                var selected = d3.select(this).attr('selection');
                if (viewValues.selectionEnabled && d.y === selectedRowId) {
                    if (selected === 'active') {
                        // remove them from our selected rows and set inactive
                        if (viewValues.selectedRows[d.y]) {
                            delete viewValues.selectedRows[d.y];
                        }
                        return 'inactive';
                    } else {
                        // add to selected rows and set to active
                        viewValues.selectedRows[d.y] = d.y;
                        return 'active';
                    }
                }
                return selected;
            });
        }

        function getSvgElement() {
            return document.querySelector('.heatmap svg');
        }
    }


    return heatmap;
}());