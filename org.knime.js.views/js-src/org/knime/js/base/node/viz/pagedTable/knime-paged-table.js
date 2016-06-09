knime_paged_table = function() {
	
	var table_viewer = {};
	var _representation = null;
	var _value = null;
	var knimeTable = null;
	var dataTable = null;
	var selection = {};
	
	table_viewer.init = function(representation, value) {
		$(document).ready(function() {
			var body = $('body');
			if (!representation.table) {
				body.append("Error: No data available");
				return;
			}
			_representation = representation;
			_value = value;
			if (representation.enableSelection && _value.selection) {
				for (var i = 0; i < _value.selection.length; i++) {
					selection[_value.selection[i]] = true;
				}
			}
			try {
				knimeTable = new kt();
				knimeTable.setDataTable(representation.table);
				
				var wrapper = $('<div id="knimePagedTableContainer" style="margin: 10px">');
				body.append(wrapper);
				if (representation.title != null) {
					wrapper.append('<h1>' + representation.title + '</h1>')
				}
				if (representation.subtitle != null) {
					wrapper.append('<h2>' + representation.subtitle + '</h2>')
				}
				var table = $('<table id="knimePagedTable" class="table table-striped table-bordered" width="100%">');
				wrapper.append(table);
				if (representation.enableColumnSearching) {
					$('#knimePagedTable').append('<tfoot><tr></tr></tfoot>');
					var footerRow = $('#knimePagedTable tfoot tr');
					if (_representation.enableSelection) {
						footerRow.append('<th></th>');
					}
					if (_representation.displayRowIndex) {
						footerRow.append('<th></th>');						
					}
					if (_representation.displayRowColors || _representation.displayRowIds) {
						footerRow.append('<th></th>');
					}
					for (var i = 0; i < knimeTable.getColumnNames().length; i++) {
						if (isColumnSearchable(knimeTable.getColumnTypes()[i])) {
							footerRow.append('<th>' + knimeTable.getColumnNames()[i] + '</th>')
						} else {
							footerRow.append('<th></th>');
						}
					}
					
					$('#knimePagedTable tfoot th').each(function() {
				        var title = $(this).text();
				        if (title == '') {
				        	return;
				        }
				        $(this).html('<input type="text" placeholder="Search '+title+'" />' );
				    });
				}
				
				var colArray = [];
				var colDefs = [];
				if (representation.enableSelection) {
					var all = _value.selectAll;
					colArray.push({'title': '<input name="select_all" value="1" id="checkbox-select-all" type="checkbox"' + (all ? ' checked' : '')  + ' />'})
					colDefs.push({
						'targets': 0,
						'searchable':false,
						'orderable':false,
						'className': 'dt-body-center',
						'render': function (data, type, full, meta) {
							var selected = selection[data] ? !all : all;
							setTimeout(function(){
								var el = $('#checkbox-select-all').get(0);
								if (all && selection[data] && el && ('indeterminate' in el)) {
									el.indeterminate = true;
								}
							}, 0);
							return '<input type="checkbox" name="id[]"'
								+ (selected ? ' checked' : '')
								+' value="' + $('<div/>').text(data).html() + '">';
						}
					});
				}
				if (_representation.displayRowIndex) {
					colArray.push({
						'title': "Row Index",
						'searchable': false
					})
				}
				if (representation.displayRowIds || representation.displayRowColors) {
					var title = representation.displayRowIds ? 'RowID' : '';
					var orderable = representation.displayRowIds;
					colArray.push({
						'title': title, 
						'orderable' : orderable
					});
				}
				for (var i = 0; i < knimeTable.getColumnNames().length; i++) {
					var colType = knimeTable.getColumnTypes()[i];
					var colDef = {
						'title': knimeTable.getColumnNames()[i],
						'orderable' : isColumnSortable(colType),
						'searchable': isColumnSearchable(colType)
					}
					if (colType == 'dateTime' && representation.globalDateFormat) {
						colDef.render = function (data, type, full, meta) {
							return moment(data).format(representation.globalDateFormat);
						}
					}
					if (colType == 'number' && representation.enableGlobalNumberFormat) {
						colDef.render = function (data, type, full, meta) {
							if (!$.isNumeric(data)) {
								return data;
							}
							return Number(data).toFixed(representation.globalNumberFormatDecimals);
						}
					}
					if (colType == 'png') {
						colDef.render = function (data, type, full, meta) {
							return '<img src="data:image/png;base64,' + data + '" />';
						}
					}
					colArray.push(colDef);
					
				}
				var pageLength = representation.initialPageSize;
				if (value.pageSize) {
					pageLength = value.pageSize;
				}
				var pageLengths = representation.allowedPageSizes;
				if (representation.pageSizeShowAll) {
					var first = pageLengths.slice(0);
					first.push(-1);
					var second = pageLengths.slice(0);
					second.push("All");
					pageLengths = [first, second];
				}
				var order = [];
				if (_value.currentOrder) {
					order = _value.currentOrder;
				}
				dataTable = $('#knimePagedTable').DataTable( {
					'columns': colArray,
					'columnDefs': colDefs,
					'order': order,
					'paging': representation.enablePaging,
					'pageLength': pageLength,
					'lengthMenu': pageLengths,
					'searching': representation.enableSearching,
					'ordering': representation.enableSorting,
					'processing': true,
					'deferRender': !representation.enableSelection,
					'data': getDataSlice(0, representation.initialPageSize),
				});
				
				$('#knimePagedTable_paginate').css('display', 'none');

				$('#knimePagedTable_info').html(
					'<strong>Loading data</strong> - Displaying '
					+ 1 + ' to ' + Math.min(knimeTable.getNumRows(), representation.initialPageSize)
					+ ' of ' + knimeTable.getNumRows() + ' entries.');
				
				if (representation.enableSelection) {
					// Handle click on "Select all" control
					$('#checkbox-select-all').on('click', function() {
						// Check/uncheck all checkboxes in the table
						var rows = dataTable.rows({ 'search': 'applied' }).nodes();
						$('input[type="checkbox"]', rows).prop('checked', this.checked);
						_value.selectAll = this.checked ? true : false;
						selection = {};
					});

					// Handle click on checkbox to set state of "Select all" control
					$('#knimePagedTable tbody').on('change', 'input[type="checkbox"]', function() {
						var el = $('#checkbox-select-all').get(0);
						var selected = el.checked ? !this.checked : this.checked;
						// we could call delete _value.selection[this.value], but the call is very slow 
						// and we can assume that a user doesn't click on a lot of checkboxes
						selection[this.value] = selected;
						// If checkbox is not checked
						if(!this.checked){
							// If "Select all" control is checked and has 'indeterminate' property
							if(el && el.checked && ('indeterminate' in el)){
								// Set visual state of "Select all" control 
								// as 'indeterminate'
								el.indeterminate = true;
							}
						}
					});
				}
				
				if (representation.enableColumnSearching) {
					dataTable.columns().every(function () {
				        var that = this;
				        $('input', this.footer()).on('keyup change', function () {
				            if (that.search() !== this.value) {
				                that.search(this.value).draw();
				            }
				        });
				    });
				}
				
				//load all data
				setTimeout(function() {
					var initialChunkSize = 100;
					addDataToTable(representation.initialPageSize, initialChunkSize);
				}, 0);

			} catch (err) {
				if (err.stack) {
					alert(err.stack);
				} else {
					alert (err);
				}
			}
		});
	};
	
	addDataToTable = function(startIndex, chunkSize) {
		var startTime = new Date().getTime();
		var tableSize = knimeTable.getNumRows()
		var endIndex  = Math.min(tableSize, startIndex + chunkSize);
		var chunk = getDataSlice(startIndex, endIndex);
		dataTable.rows.add(chunk);
		var endTime = new Date().getTime();
		var chunkDuration = endTime - startTime;
		var newChunkSize = chunkSize;
		if (startIndex + chunkSize < tableSize) {
			$('#knimePagedTable_info').html(
				'<strong>Loading data ('
				+ endIndex + ' of ' + tableSize + ' records)</strong> - Displaying '
				+ 1 + ' to ' + Math.min(tableSize, _representation.initialPageSize) 
				+ ' of ' + tableSize + ' entries.');
			if (chunkDuration > 300) {
				newChunkSize = Math.max(1, Math.floor(chunkSize / 2));
			} else if (chunkDuration < 100) {
				newChunkSize = chunkSize * 2;
			}
			setTimeout((function(i, c) {
				return function() {
					addDataToTable(i, c);
				};
			})(startIndex + chunkSize, newChunkSize), chunkDuration);
		} else {
			$('#knimePagedTable_paginate').css('display', 'block');
			applyViewValue();
			dataTable.draw();
		}
	}
	
	getDataSlice = function(start, end) {
		if (typeof end == 'undefined') {
			end = knimeTable.getNumRows();
		}
		var data = [];
		for (var i = start; i < Math.min(end, knimeTable.getNumRows()); i++) {
			var row = knimeTable.getRows()[i];
			var dataRow = [];
			if (_representation.enableSelection) {
				dataRow.push(row.rowKey);
			}
			if (_representation.displayRowIndex) {
				dataRow.push(i);
			}
			if (_representation.displayRowIds || _representation.displayRowColors) {
				var string = '';
				if (_representation.displayRowColors) {
					string += '<div class="knimeTableRowColor" style="background-color: '
							+ knimeTable.getRowColors()[i]
							+ '; width: 16px; height: 16px; '
							+ 'display: inline-block; margin-right: 5px; vertical-align: text-bottom;"></div>'
				}
				if (_representation.displayRowIds) {
					string += row.rowKey;
				}
				dataRow.push(string);
			}
			var dataRow = dataRow.concat(row.data);
			data.push(dataRow);
		}
		return data;
	}
	
	applyViewValue = function() {
		if (_representation.enableSearching && _value.filterString) {
			dataTable.search(_value.filterString);
		}
		if (_representation.enableColumnSearching && _value.columnFilterStrings) {
			for (var i = 0; i < _value.columnFilterStrings.length; i++) {
				var curValue = _value.columnFilterStrings[i];
				if (curValue.length > 0) {
					var column = dataTable.column(i);
					$('input', column.footer()).val(curValue);
					column.search(curValue);
				}
			}
		}
		if (_representation.enablePaging && _value.currentPage) {
			setTimeout(function() {
				dataTable.page(_value.currentPage).draw('page');
			}, 0);
		}
	}
	
	isColumnSortable = function (colType) {
		var allowedTypes = ['boolean', 'string', 'number', 'dateTime'];
		return allowedTypes.indexOf(colType) >= 0;
	}
	
	isColumnSearchable = function (colType) {
		var allowedTypes = ['boolean', 'string', 'number', 'dateTime', 'undefined'];
		return allowedTypes.indexOf(colType) >= 0;
	}
	
	table_viewer.validate = function() {
		return true;
	};
	
	table_viewer.getComponentValue = function() {
		_value.selection = [];
		for (var id in selection) {
			if (selection[id]) {
				_value.selection.push(id);
			}
		}
		if (_value.selection.length == 0) {
			_value.selection = null;
		}
		var pageNumber = dataTable.page();
		if (pageNumber > 0) {
			_value.currentPage = pageNumber;
		}
		var pageSize = dataTable.page.len();
		if (pageSize != _representation.initialPageSize) {
			_value.pageSize = pageSize;
		}
		var searchString = dataTable.search();
		if (searchString.length) {
			_value.filterString = searchString;
		}
		var order = dataTable.order();
		if (order.length > 0) {
			_value.currentOrder = order;
		}
		if (_representation.enableColumnSearching) {
			_value.columnFilterStrings = [];
			var filtered = false;
			dataTable.columns().every(function (index) {
		        var input = $('input', this.footer());
		        if (input.length) {
		        	var filterString = input.val();
		        	_value.columnFilterStrings.push(filterString);
		        	filtered |= filterString.length;
		        } else {
		        	_value.columnFilterStrings.push("");
		        }
		    });
			if (!filtered) {
				_value.columnFilterStrings = null;
			}
		}
		return _value;
	};
	
	return table_viewer;
	
}();