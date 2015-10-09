// JavaScript Document

kc = function() {
	var kc = { version: "0.0.1" };
	
	var dataTable = {};
	var extensions = [];
		
	kc.setDataTable = function(jsonTable) {
		dataTable = JSON.parse(jsonTable);
	};
	
	kc.getData = function() {
		return dataTable.data;
	};
	
	kc.getColumn = function(columnID) {
		if (columnID < dataTable.spec.numColumns) {
			var col = [];
			
			for (var i = 0; i < kc.getNumRows(); i++) {
				col.push(dataTable.data[i][columnID]);
			}
			return col;
		}
	};
	
	kc.getColumnNames = function() {
		return dataTable.spec.colNames;
	};
	
	kc.getColumnTypes = function() {
		return dataTable.spec.colTypes;
	};
	
	kc.getNumRows = function() {
		return dataTable.spec.numRows;
	};
	
	kc.registerView = function(view) {
		for (var i = 0; i < view.extensionNames.length; i++) {
			kc_registerViewExtension(view, view.extensionNames[i]);
		}
	};
	
	kc.getExtension = function(extensionName) {
		var extension = {};
		for (var i = 0; i < extensions.length; i++) {
			if(extensions[i].name == extensionName) {
				extension = extensions[i];
				break;
			};
		};
		return extension;
	};
	
	kc_registerViewExtension = function(view, extensionName) {
		var extensionID;
		for (var i = 0; i < extensions.length; i++) {
			if (extensions[i].name === extensionName) {
				extensionID = i;
				break;
			}
		}
		if (typeof extensionID == 'undefined') {
			extensions.push(kc_createExtension(extensionName));
			extensionID = extensions.length-1;
		}
		extensions[extensionID].registerView(view);
	};
	
	kc_createExtension = function(name) {
		if (name === "hilite") {
			return kc_createHiliteExtension();
		}
	};
	
	kc_createHiliteExtension = function() {
		var defaultValue = false;
		var hiliteTable = [];
		var clearListeners = [];
		var changeListeners = [];
		var changeTable = [];
		var hCol = kc_getExtColumnID ("hilite");
		if (typeof hCol != 'undefined') {
			var pos = hiliteTable.push({name: "native", values: []}) - 1;
			for (var i = 0; i < dataTable.spec.numRows; i++) {
				hiliteTable[pos].values.push(dataTable.extensions[i][hCol]);
			};
		}
		return {
			name: "hilite",
			isHilited: function(rowID) {
				var hilited = false;
				for (var i = 0; i < hiliteTable.length; i++) {
					hilited |= hiliteTable[i].values[rowID];
				}
				return hilited;
			},
			setHilited: function(viewName, rowID, hilited) {
				var previousValue = this.isHilited(rowID);
				for (var i = 0; i < hiliteTable.length; i++) {
					if (hiliteTable[i].name === viewName) {
						hiliteTable[i].values[rowID] = hilited;
						break;
					};
				};
				if (this.isHilited(rowID) !== previousValue) {
					changeTable.push(rowID);
				}
			},
			registerView: function(view) {
				var pos = hiliteTable.push({name: view.name, values: []}) - 1;
				for (var i = 0; i < dataTable.spec.numRows; i++) {
					hiliteTable[pos].values.push(defaultValue);
				};
				changeListeners.push(view.hiliteChangeListener);
				clearListeners.push(view.hiliteClearListener);
			},
			fireHiliteChanged: function() {
				for (var i = 0; i < changeListeners.length; i++) {
					changeListeners[i](changeTable);
				}
				changeTable = [];
				pushData(JSON.stringify(this.exportExtension()));
			},
			fireClearHilite: function() {
				console.log("fire clear hilite");
				changeTable = [];
				for (var i = 0; i < hiliteTable.length; i++) {
					for (var j = 0; j < hiliteTable[i].values.length; j++) {
						hiliteTable[i].values[j] = false;
					}
				}
				for (var i = 0; i < clearListeners.length; i++) {
					clearListeners[i]();
				}
				pushData(JSON.stringify(this.exportExtension()));
			},
			exportExtension: function() {
				var exportHilite = new Array();
				for (var i = 0; i < dataTable.spec.numRows; i++) {
					exportHilite.push(this.isHilited(i));
				};
				return exportHilite;
			}
		};
	};
	
	kc_getDataColumnID = function(columnName) {
		var colID = null;
		for (var i = 0; i < dataTable.spec.numColumns; i++) {
			if (dataTable.spec.colNames[i] === columnName) {
				colID = i;
				break;
			};
		};
		return colID;
	};
	
	kc_getExtColumnID = function(columnName) {
		var colID = null;
		for (var i = 0; i < dataTable.spec.numExtensions; i++) {
			if (dataTable.spec.extensionNames[i] === columnName) {
				colID = i;
				break;
			};
		};
		return colID;
	};

	return kc;
}();