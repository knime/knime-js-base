window.table_editor = (function() {

	/**
	 * CellEditor abstract class
	 */
	var CellEditor = function() {
		this.component = undefined;
	};

	CellEditor.prototype.getComponent = function() {
		return this.component;
	};

	CellEditor.prototype.getValue = function() {
		return this.component.val();
	};

	CellEditor.prototype.setValue = function(value) {
		this.component.val(value);
	};

	/**
	 * String values editor
	 */
	var StringEditor = function() {
		this.component = $('<input type="text" class="knime-table-control-text knime-string knime-single-line"/>');
	};

	StringEditor.prototype = Object.create(CellEditor.prototype);

	StringEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		}
		return value;
	};

	/**
	 * Integer or Long values editor
	 */
	var IntEditor = function() {
		this.component = $('<input type="number" class="knime-table-control-text knime-integer knime-single-line"/>');
	};

	IntEditor.prototype = Object.create(CellEditor.prototype);

	IntEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		} else {
			return parseInt(value, 10);
		}
	};

	/**
	 * Double values editor
	 */
	var DoubleEditor = function() {
		this.component = $('<input type="number" step="any" class="knime-table-control-text knime-double knime-single-line"/>');
	};

	DoubleEditor.prototype = Object.create(CellEditor.prototype);

	DoubleEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		} else {
			return parseFloat(value);
		}
	};

	/**
	 * Boolean values editor
	 */
	var BooleanEditor = function() {
		this.component = $('<input type="text" class="knime-boolean"/>');
	};

	BooleanEditor.prototype = Object.create(CellEditor.prototype);

	BooleanEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		} else {
			return value.toLowerCase() === 'true' || value === '1';
		}
	};

	/**
	 * Editor factory
	 */
	var createEditor = function(type) {
		var editor;
		switch (type) {
			case 'String':
				editor = new StringEditor();
				break;
			case 'Number (integer)':
			case 'Number (long)':
				editor = new IntEditor();
				break;
			case 'Number (double)':
				editor = new DoubleEditor();
				break;
			case 'Boolean value':
				editor = new BooleanEditor();
				break;
		}
		return editor;
	};

	var isMacOS = function() {
		return navigator.platform.indexOf('Mac') !== -1;
	};




	/**
	 * @class TableEditor
	 * @extends KnimeBaseTableViewer
	 * TableViewer with added editing functionality
	 */
	var TableEditor = function () {
		KnimeBaseTableViewer.apply(this);

		this._selectedCell = undefined;
		this._curCells = null;

		this._cellClickHandler = this._cellClickHandler.bind(this);
		this._editableCellDoubleClickHandler = this._editableCellDoubleClickHandler.bind(this);
		this._selectedCellKeyDownHandler = this._selectedCellKeyDownHandler.bind(this);
		this._selectedCellFocusOutHandler = this._selectedCellFocusOutHandler.bind(this);
		this._cellArrowKeyDownHandler = this._cellArrowKeyDownHandler.bind(this);
		this._pasteHandler = this._pasteHandler.bind(this);
	};

	TableEditor.prototype = Object.create(KnimeBaseTableViewer.prototype);
	TableEditor.prototype.constructor = TableEditor;


	/**
	 * @override
	 */
	TableEditor.prototype._addTableListeners = function () {
		// skip
	};

	/**
	 * @override
	 */
	TableEditor.prototype._buildDataTableConfig = function () {
		KnimeBaseTableViewer.prototype._buildDataTableConfig.apply(this);
		delete this._dataTableConfig.select;
	};

	/**
	 * @override
	 */
	TableEditor.prototype._prepare = function () {
		KnimeBaseTableViewer.prototype._prepare.apply(this);
		this._applyEditorChanges();
	};

	/**
	 * @override
	 */
	TableEditor.prototype._dataTablePreDrawCallback = function () {
		var self = this;
		if (self._dataTable && self._curCells) {
			self._unselectCurrentCell();
			self._curCells.off('click', self._cellClickHandler);
			self._curCells.off('dblclick', self._editableCellDoubleClickHandler);
		}
	};

	/**
	 * @override
	 */
	TableEditor.prototype._dataTableDrawCallback = function () {
		var self = this;
		if (!self._representation.displayColumnHeaders) {
			$('#knimePagedTable thead').remove();
	    }
		if (this._dataTableConfig.searching && !this._representation.enableSearching) {
			$('#knimePagedTable_filter').remove();
		}
		if (self._dataTable) {
			self._curCells = self._dataTable.columns(function(ind) {
				return ind >= self._infoColsCount;
			}, {page: 'current'}).nodes().flatten().to$();
			self._curCells.on('click', self._cellClickHandler);
			self._curCells.on('dblclick', self._editableCellDoubleClickHandler);
		}
		self._setDynamicCssStyles();
	};

	/**
	 * @override
	 */
	TableEditor.prototype._buildColumnDefinitions = function () {
		var columnCountBefore = this._dataTableConfig.columns.length;

		KnimeBaseTableViewer.prototype._buildColumnDefinitions.apply(this);

		for (var i = columnCountBefore; i < this._dataTableConfig.columns.length; i++) {
			var colDef = this._dataTableConfig.columns[i];
			var title = colDef.title;
			if (this._representation.editableColumns.indexOf(title) !== -1) {
				colDef.title += '<span class="glyphicon glyphicon-pencil"></span>';
				colDef.className += ' knime-editable';
			}
		}
	};

	TableEditor.prototype._applyEditorChanges = function () {
		if (this._representation.table.dataHash == this._value.tableHash) {
			var editorChanges = this._value.editorChanges.changes;
			for (var rowKey in editorChanges) {
				var rowFilter = this._representation.table.rows.filter(function(row) { return row.rowKey === rowKey; });
				// since not all rows from the input table can be shown, we need to check, if the row is present in the view
				if (rowFilter.length > 0) {
					var row = rowFilter[0];
					var rowEntry = editorChanges[rowKey];
					for (var colName in rowEntry) {
						if (this._representation.editableColumns.indexOf(colName) != -1) {  // check, if the column is still editable
							var colIndex = this._representation.table.spec.colNames.indexOf(colName);
							if (colIndex != -1) {
								var cellValue = rowEntry[colName];
								row.data[colIndex] = cellValue;
							}
						}
					}
				}
			}
		} else {
			this._value.tableHash = this._representation.table.dataHash;
			this._value.editorChanges.changes = {};
		}
	};

	TableEditor.prototype._cellClickHandler = function(e) {
		var td = e.currentTarget;
		var cell = this._dataTable.cell(td);
		this._selectCell(cell);
	};

	TableEditor.prototype._editableCellDoubleClickHandler = function(e) {
		var td = e.currentTarget;
		var cell = this._dataTable.cell(td);
		this._createCellEditor(cell);
	};

	TableEditor.prototype._selectedCellFocusOutHandler = function(e) {
		if (!this._selectedCell) {
			return;
		}
		var $td = $(this._selectedCell.node());
		if ($(e.target).is($td)) {
			this._unselectCurrentCell();
		}
	};

	TableEditor.prototype._selectCell = function(cell) {
		if (!cell) {
			cell = this._selectedCell;
		}

		var $td = $(cell.node());

		if (!this._isEqualCell(cell, this._selectedCell)) {
			this._unselectCurrentCell();
			this._selectedCell = cell;
			$td.addClass('knime-selected selected');
			this._catchTdEventsOn($td);
		}

		$td.attr('tabindex', -1);
		$td.focus();
	};

	TableEditor.prototype._unselectCurrentCell = function() {
		if (!this._selectedCell) {
			return;
		}

		var $td = $(this._selectedCell.node());
		$td.removeClass('knime-selected selected');
		this._catchTdEventsOff($td);

		this._selectedCell = undefined;
	};

	TableEditor.prototype._createCellEditor = function(cell, cellValue) {
		var self = this;
		var $td = $(cell.node());
		this._catchTdEventsOff($td);

		var editor = this._createCellEditorComponent(cell, cellValue);
		var editorComponent = editor.getComponent();

		var tdHeight = this._getTableCellContentHeight(cell);
		$td.empty()
			.append(editorComponent);
		// need to set up height after adding the editor to the cell, otherwise it won't work in FF
		editorComponent.height(tdHeight);
		editorComponent.focus();

		$td.off('click', this._cellClickHandler);
		$td.off('dblclick', this._editableCellDoubleClickHandler);

		var editFinishCallback = function() {
			restoreListeners();
			var newValue = editor.getValue();
			$td.empty()
				.append(newValue);
			self._setCellValue(cell, newValue);
		};

		var editCancelCallback = function() {
			restoreListeners();
			$td.attr('tabindex', -1);
			$td.focus();
			cell.invalidate();
		};

		var restoreListeners = function() {
			$td.on('dblclick', self._editableCellDoubleClickHandler);
			$td.on('click', self._cellClickHandler);
			self._catchTdEventsOn($td);
			editorComponent.off('focusout');
			editorComponent.off('keydown');
		};

		editorComponent.on('focusout', editFinishCallback);
		editorComponent.on('keydown', function(e) {
			switch (e.key) {
				case 'Enter':
					editFinishCallback();
					self._selectCell(self._getCellByShift(cell, 1, 0));
					break;
				case 'Escape':
					e.stopPropagation();
					editCancelCallback();
					break;
				case 'Tab':
					e.preventDefault();
					editFinishCallback();
					if (e.shiftKey) {
						self._selectCell(self._getCellByShift(cell, 0, -1));
					} else {
						self._selectCell(self._getCellByShift(cell, 0, 1));
					}
				case 'ArrowUp':
				case 'ArrowDown':
				case 'ArrowLeft':
				case 'ArrowRight':
					if (cellValue) {
						// presence of cellValue <=> user entered editor mode by typing
						editFinishCallback();
						self._cellArrowKeyDownHandler(e);
					} else if ((e.key == 'ArrowUp' || e.key == 'ArrowDown') && (editor instanceof IntEditor || editor instanceof DoubleEditor)) {
						// don't trigger spinner for numeric editors
						e.preventDefault();
					}
					break;
			}
		});
	};

	TableEditor.prototype._createCellEditorComponent = function(cell, cellValue) {
		// get column type
		var colInd = cell.index().column;
		var dataInd = this._dataIndexFromColIndex(colInd);
		var colType = this._knimeTable.getKnimeColumnTypes()[dataInd];
		var editor = createEditor(colType);
		editor.setValue(cellValue !== undefined ? cellValue : cell.data());
		return editor;
	};

	TableEditor.prototype._getCellByShift = function(cell, rowShift, columnShift) {
		if (!cell) {
			return null;
		}
		var ind = cell.index();
		var newColInd = ind.column + columnShift;

		var dataIndex = this._dataIndexFromColIndex(newColInd);
		var isDataCell = typeof dataIndex !== 'undefined';
		if (!isDataCell) {
			return null;
		}

	    // here we need to take into account the actual order of rows (because of sorting or filtering)
		var indexes = this._dataTable.rows( { page: 'current', search: 'applied' } ).indexes().toArray();
		var newRowInd = indexes.indexOf(ind.row) + rowShift;
		if (newRowInd >= indexes.length || newRowInd < 0) {
			return null;
		}
		newRowInd = indexes[newRowInd];

		return this._dataTable.cell(newRowInd, newColInd);
	};

	TableEditor.prototype._setCellValue = function(cell, newValue) {
		var index = cell.index();
		this._dataTable.data()[index.row][index.column] = newValue;

		var rowKey = this._knimeTable.getRows()[index.row].rowKey;
		var dataIndex = this._dataIndexFromColIndex(index.column);
		var colName = this._knimeTable.getColumnNames()[dataIndex];
		if (this._value.editorChanges.changes[rowKey] === undefined) {
			this._value.editorChanges.changes[rowKey] = {};
		}
		this._value.editorChanges.changes[rowKey][colName] = newValue;

		cell.invalidate();
	};

	TableEditor.prototype._catchTdEventsOn = function($td) {
		$td.on('keydown', this._selectedCellKeyDownHandler);
		$td.on('focusout', this._selectedCellFocusOutHandler);
		$(document).on('paste', this._pasteHandler);
	};

	TableEditor.prototype._catchTdEventsOff = function($td) {
		$td.off('keydown', this._selectedCellKeyDownHandler);
		$td.off('focusout', this._selectedCellFocusOutHandler);
		$(document).off('paste', this._pasteHandler);
		$td.removeAttr('tabindex');
	};

	TableEditor.prototype._selectedCellKeyDownHandler = function(e) {
		var ctrlKey = isMacOS() ? e.metaKey : e.ctrlKey;
		switch (e.key) {
			case 'ArrowUp':
			case 'ArrowDown':
		    case 'ArrowLeft':
			case 'ArrowRight':
				this._cellArrowKeyDownHandler(e);
		    	break;
		    case 'Home':
		    	if (ctrlKey) {
		    		this._selectCell(this._getTopLeftCell());
		    	} else {
		    		this._selectCell(this._getFirstCellInRow(this._selectedCell));
		    	}
		    	e.preventDefault();
		    	break;
		    case 'End':
		    	if (ctrlKey) {
		    		this._selectCell(this._getBottomRightCell());
		    	} else {
		    		this._selectCell(this._getLastCellInRow(this._selectedCell));
		    	}
		    	e.preventDefault();
		    	break;
			case 'Enter':
				this._selectCell(this._getCellByShift(this._selectedCell, 1, 0));  // same as ArrowDown
				break;
		    case 'Delete':
		    	if (this._isEditableCell(this._selectedCell)) {
		    		this._setCellValue(this._selectedCell, null);
		    	}
		    	break;
		    case 'Backspace':
		    	if (this._isEditableCell(this._selectedCell)) {
		    		e.preventDefault();
		    		this._createCellEditor(this._selectedCell, null);
		    	}
		    	break;
			default:
			   if (this._isEditableCell(this._selectedCell) && e.key.length == 1 && !ctrlKey) {  // test whether the key is printable and no CTRL is pressed
				   e.preventDefault();
			       this._createCellEditor(this._selectedCell, e.key);
			   }
		}
	};

	TableEditor.prototype._cellArrowKeyDownHandler = function(e) {
		var ctrlKey = isMacOS() ? e.metaKey : e.ctrlKey;
		switch (e.key) {
			case 'ArrowUp':
				if (ctrlKey) {
					this._selectCell(this._getFirstCellInColumn(this._selectedCell));
				} else {
					this._selectCell(this._getCellByShift(this._selectedCell, -1, 0));
				}
				break;
			case 'ArrowDown':
				if (ctrlKey) {
					this._selectCell(this._getLastCellInColumn(this._selectedCell));
				} else {
					this._selectCell(this._getCellByShift(this._selectedCell, 1, 0));
				}
				break;
			case 'ArrowLeft':
				if (ctrlKey) {
					this._selectCell(this._getFirstCellInRow(this._selectedCell));  // same as Home
				} else {
					this._selectCell(this._getCellByShift(this._selectedCell, 0, -1));
				}
				break;
			case 'ArrowRight':
				if (ctrlKey) {
					this._selectCell(this._getLastCellInRow(this._selectedCell));  // same as End
				} else {
					this._selectCell(this._getCellByShift(this._selectedCell, 0, 1));
				}
				break;
		}
	};

	TableEditor.prototype._pasteHandler = function(e) {
		var data = e.originalEvent.clipboardData.getData('text');
		var values = [];
		var lines = data.replace(/\r/g, '').split('\n');
		if (lines.length > 0 && lines[lines.length - 1] === '') {
			// when copying from Excel the last line is always an empty string,
			// while from Google Sheets this is not the case
			lines.pop();
		}
		for (var i = 0; i < lines.length; i++) {
			values.push(lines[i].split('\t'));
		}
		this._pasteMultipleValues(this._selectedCell, values);
	};

	TableEditor.prototype._pasteMultipleValues = function(cell, values) {
		var startCell = cell;

		// validation
		var isCompatible = this._iterateSubtable(startCell, values, function(cell, value) {
			if (!cell) {
				alert('Cannot paste the values. Range out of bounds.');
				return false;
			}

			var dataIndex = this._dataIndexFromColIndex(cell.index().column);
			var colName = this._knimeTable.getColumnNames()[dataIndex];
			if (!this._isEditableCell(cell)) {
				alert('Cannot paste the values as column "' + colName + '" is not editable.');
				return false;
			}

			var convertRes = this._convertValueToCellType(cell, value);
			if (!convertRes.status) {
				alert('Cannot paste the values as value "' + value + '" is not compatible with type "' + convertRes.type + '" of column "' + colName + '".');
				return false;
			}

			return true;
		});

		if (!isCompatible) {
			return;
		}

		// pasting
		this._iterateSubtable(startCell, values, function(cell, value) {
			this._setCellValue(cell, this._convertValueToCellType(cell, value).value);
			return true;
		});
	};

	TableEditor.prototype._iterateSubtable = function(cell, values, callback) {
		for (var i = 0; i < values.length; i++) {
			var row = values[i];
			for (var j = 0; j < row.length; j++) {
				if (!callback.call(this, cell, row[j])) {
					return false;
				}
				if (!cell) {
					return;
				}
				if (j < row.length - 1) {
					cell = this._getCellByShift(cell, 0, 1);
				}
			}
			if (i < values.length - 1) {
				cell = this._getCellByShift(cell, 1, -(row.length - 1));
			}
		}
		return true;
	};

	TableEditor.prototype._convertValueToCellType = function(cell, value) {
		var dataIndex = this._dataIndexFromColIndex(cell.index().column);
		var res = {
			status: false,
			value: undefined,
			type: this._knimeTable.getKnimeColumnTypes()[dataIndex]
		};
		switch (res.type) {
			case 'String':
				res.value = value.toString();
				res.status = true;
				break;
			case 'Number (integer)':
			case 'Number (long)':
				res.value = this._filterInt(value);
				res.status = !isNaN(res.value);
				break;
			case 'Number (double)':
				res.value = Number(value.replace(',', '.'));
				res.status = !isNaN(res.value);
				break;
		}
		return res;
	};

	/**
	 * taken from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseInt
	 */
	TableEditor.prototype._filterInt = function(value) {
		if (/^(\-|\+)?([0-9]+)$/.test(value)) {
			return Number(value);
		}
		return NaN;
	};

	TableEditor.prototype._getFirstCellInRow = function(cell) {
		return this._dataTable.cell(cell.index().row, this._infoColsCount);
	};

	TableEditor.prototype._getLastCellInRow = function(cell) {
		return this._dataTable.cell(cell.index().row, this._dataTableConfig.columns.length - 1);
	};

	TableEditor.prototype._getFirstCellInColumn = function(cell) {
		return this._dataTable.cell(this._dataTable.page.info().start, cell.index().column);
	};

	TableEditor.prototype._getLastCellInColumn = function(cell) {
		return this._dataTable.cell(this._dataTable.page.info().end - 1, cell.index().column);
		// see the comment in getCellByShift about end - 1
	};

	TableEditor.prototype._getTopLeftCell = function() {
		return this._dataTable.cell(this._dataTable.page.info().start, this._infoColsCount);
	};

	TableEditor.prototype._getBottomRightCell = function() {
		return this._dataTable.cell(this._dataTable.page.info().end - 1, this._dataTableConfig.columns.length - 1);
		// see the comment in getCellByShift about end - 1
	};

	TableEditor.prototype._isEditableCell = function(cell) {
		var dataIndex = this._dataIndexFromColIndex(cell.index().column);
		var colName = this._knimeTable.getColumnNames()[dataIndex];
		return this._representation.editableColumns.indexOf(colName) !== -1;
	};

	TableEditor.prototype._isEqualCell = function(cell1, cell2) {
		if (cell1 && cell2) {
			var index1 = cell1.index();
			var index2 = cell2.index();
			return index1.row === index2.row && index1.column === index2.column;
		} else {
			return cell1 === cell2;
		}
	};

	TableEditor.prototype._getTableCellContentHeight = function(cell) {
		// e.g.: "12px" -> 12
		var _removePx = function(size) {
			return Number(size.substr(0, size.length - 2));
		};
		var $td = $(cell.node());
		// We have to calculate the height of td in this way because $.height() don't give a precise value if a page was zoomed in the browser
		// (see the additional note at http://api.jquery.com/height/)
		// Also we need to use getBoundingClientRect() to get the precise fraction value, as offsetHeight is rounded to be integer
		// (see the note at https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/offsetHeight)
		return cell.node().getBoundingClientRect().height - _removePx($td.css('border-top-width')) - _removePx($td.css('border-bottom-width')) - _removePx($td.css('padding-top')) - _removePx($td.css('padding-bottom'));
	};

	return new TableEditor();
})();
