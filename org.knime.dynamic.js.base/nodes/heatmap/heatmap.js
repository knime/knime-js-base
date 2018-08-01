(heatmap_namespace = function () {

	var heatmap = {};
	var title;
	var subtitle;

	heatmap.init = function (representation, value) {

		debugger;

		// format table reader data
		var data = JSON.parse(representation.inObjects[0].rows[0].data[0]);
		var stat = representation.inObjects[0].rows[0].data[1];

		createPage(data, stat);
	}


	/**
	 * Start Javascript for the node
	 */
	function createPage(inputData, stat) {
		// Settings
		var imageColumnName = 'svg';
		var yAxisLabelColumn = 'ID';
		var colorRange = ['#FF5858', '#fff', '#C6FD57'];
		var itemSize = 20;
		var margin = { top: 100, left: 100 };

		// State managment objects
		viewValues = {
			selectedRows: [],
			scale: 'linear',
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
			// make sure rows have an id
			var dataWithId = createRowIds(inputData);

			// prepare data
			var paginationData = createPagination(dataWithId);

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

		/**
		 * If no yAxis label column is defined
		 * create a temporary label column based on the row index
		 */
		function createRowIds(rawData) {
			var dataWithId = rawData.map(function (row, index) {
				if (!yAxisLabelColumn) {
					yAxisLabelColumn = 'temporaryId';
				}
				if (!row[yAxisLabelColumn]) {
					row[yAxisLabelColumn] = index;
				}
				return row;
			});
			return dataWithId;
		}

		function getControlHtml() {
			return (
				'<form class="wrapper">\
				<div class="form-group col-xs-3 rowsPerPage">\
						<label for="rowsPerPage">Rows per page</label>\
						<select id="rowsPerPage" class="form-control">\
							<option ' +
				(viewValues.rowsPerPage === 10 ? 'selected ' : '') +
				'value="10">10</option>\
							<option ' +
				(viewValues.rowsPerPage === 20 ? 'selected ' : '') +
				'value="20">20</option>\
				<option ' +
				(viewValues.rowsPerPage === 50 ? 'selected ' : '') +
				'value="50">50</option>\
				<option ' +
				(viewValues.rowsPerPage === 100 ? 'selected ' : '') +
				'value="100">100</option>\
						</select>\
					</div>\
				<div class="form-group col-xs-3 scaleselector">\
						<label for="scale">Scale type</label>\
						<select id="scale" class="form-control">\
							<option ' +
				(viewValues.scale === 'linear' ? 'selected ' : '') +
				'value="linear">Linear</option>\
							<option ' +
				(viewValues.scale === 'quantize' ? 'selected ' : '') +
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
			var html = '<ul class="pagination">';

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
			html += '</ul>';
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
					viewValues.scale = e.target.value;
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

		function createPagination(data) {
			// Reduce duplicates based on row id
			if (yAxisLabelColumn) {
				var reducedData = data
					.reduce(function (accumulator, row) {
						var id = row[yAxisLabelColumn];
						if (!(id in accumulator)) {
							accumulator[id] = row;
						}
						return accumulator;
					}, [])
					.filter(function (row) {
						// filter empty rows by casting them to a boolean
						return !!row;
					});
			} else {
				var reducedData = data;
			}

			var pageCount = Math.ceil(reducedData.length / viewValues.rowsPerPage);

			// jump to page 1 if total number of pages exceeds current page
			viewValues.currentPage =
				viewValues.currentPage <= pageCount ? viewValues.currentPage : 1;

			var currentPage = viewValues.currentPage;
			var nextPageRowEnd = viewValues.rowsPerPage * currentPage;
			var nextPageRowStart = viewValues.rowsPerPage * (currentPage - 1);
			var rows = reducedData.slice(nextPageRowStart, nextPageRowEnd);

			return {
				rows: rows,
				pageCount: pageCount,
				next: nextPageRowEnd < reducedData.length ? currentPage + 1 : false,
				prev: nextPageRowStart > 0 ? currentPage - 1 : false
			};
		}

		function getValidKeys(row) {
			return Object.keys(row).filter(function (key) {
				return [yAxisLabelColumn, imageColumnName].indexOf(key) === -1;
			});
		}

		function formatTableReaderData(rows) {
			var minimum = Number.POSITIVE_INFINITY;
			var maximum = Number.NEGATIVE_INFINITY;
			var images = [];

			var allValues = rows.reduce(function (accumulator, row, currentIndex) {
				var id = row[yAxisLabelColumn];
				var rowIsSelected = viewValues.selectedRows.indexOf(id) != -1; // a bit slow

				// Storing images in an separate array is enough
				if (imageColumnName && row[imageColumnName]) {
					images[id] = row[imageColumnName];
				}

				// Set values for each cell
				var vals = getValidKeys(row).map(function (key) {
					var newItem = {};
					newItem.y = id;
					newItem.x = key;
					newItem.value = row[key];
					newItem.initallySelected = rowIsSelected;

					// Good opportunity to determine min and max
					minimum = Math.min(minimum, newItem.value);
					maximum = Math.max(maximum, newItem.value);
					return newItem;
				});
				return accumulator.concat(vals);
			}, []);

			return {
				images: images,
				data: allValues,
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
			var data = formattedDataset.data;
			var xElements = d3
				.set(
					data.map(function (item) {
						return item.x;
					})
				)
				.values();

			var yElements = d3
				.set(
					data.map(function (item) {
						return item.y;
					})
				)
				.values();

			return {
				x: d3
					.scaleBand()
					.domain(xElements)
					.range([
						margin.left,
						xElements.length * itemSize - 1 + margin.left
					]),
				y: d3
					.scaleBand()
					.domain(yElements)
					.range([
						margin.top,
						yElements.length * itemSize - 1 + margin.top
					]),
				colorScale:
					viewValues.scale === 'quantize'
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
			var svgRegex = RegExp(
				/^\s*(?:<\?xml[^>]*>\s*)?(?:<!doctype svg[^>]*\s*(?:\[?(?:\s*<![^>]*>\s*)*\]?)*[^>]*>\s*)?<svg[^>]*>[^]*<\/svg>\s*$/i
			);
			if (svgRegex.test(string)) {
				return 'data:image/svg+xml;base64,' + btoa(string);
			}
			return string;
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
				.translateExtent([
					[0, 0],
					[Number.POSITIVE_INFINITY, Number.POSITIVE_INFINITY]
				])
				.on('zoom', function () {
					var t = d3.event.transform;
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

		function buildSvg(rows) {
			var formattedDataset = formatTableReaderData(rows);

			var scales = getScales(formattedDataset);
			var axis = getAxis(scales);

			var svg = d3
				.select('.heatmap')
				.append('svg')
				.attr('width', '100%')
				.attr('height', '100%');

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

				var tooltip = document.querySelector('.tooltip');
				var data = d3.select(e.target).data()[0];

				// Select rows
				if (event.button || event.which) {
					selectCell(data);
				}

				if (!viewValues.tooltipsEnabled) {
					return;
				}
				// Show tooltip
				tooltip.classList.add('active');
				e.target.classList.add('active');

				tooltip.innerHTML =
					'<span class="position">x:' +
					data.x +
					' y:' +
					data.y +
					'</span><span class="value">' +
					data.value +
					'</span>';

				// Add some style
				tooltip.style.left = event.offsetX + itemSize + 'px';
				tooltip.style.top = event.offsetY + 100 + itemSize + 'px'; // TODO: fix this
			});

			// Deactivation relies on gaps in the wrapper between the cells
			domWrapper.addEventListener('mouseout', function (e) {
				var tooltip = document.querySelector('.tooltip');
				tooltip.classList.remove('active');
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

			var axisWrapper = svg
				.append('g')
				.attr('class', 'axisWrapper')
				.attr('mask', 'url(#maskAxis)');
			axisWrapper
				.append('g')
				.attr('class', 'yAxis')
				.call(axis.y)
				.selectAll('text')
				.attr('font-weight', 'normal');

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

			// initialize zoom
			var zoom = initializeZoom();

			// initial positioning via zoom
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