var table;

function setData(jsonString) {
	table = JSON.parse(jsonString);
	//document.getElementById("specContent").innerHTML = table.spec.colNames;
	console.log("jsonString successfully parsed.");
	loadTableView();
}

function getData() {
	console.log("getData called");
	var returnSpec = {numRows: table.spec.numRows, numColumns: 0, colNames: [], colTypes: [], colKinds: []};
	for (var colID = 0; colID < table.spec.numColumns; colID++) {
		if (table.spec.colKinds[colID] !== "data") {
			returnSpec.colNames.push(table.spec.colNames[colID]);
			returnSpec.colTypes.push(table.spec.colTypes[colID]);
			returnSpec.colKinds.push(table.spec.colKinds[colID]);
		}
	}
	returnSpec.numColumns = returnSpec.colNames.length;
	
	var returnTable = {spec: {}, data:[]};
	for (var rowID = 0; rowID < table.spec.numRows; rowID++) {
		var row = [];
		for (var colID = 0; colID < table.spec.numColumns; colID++) {
			if (table.spec.colKinds[colID] !== "data")
				row.push(table.data[rowID][colID]);
		}
		returnTable.data.push(row);
	}
	returnTable.spec = returnSpec;
	
	pushData(JSON.stringify(returnTable));
}

function reverseHilite() {
	console.log("inverse Hilite called");
	var hCol;
	for (var i = 0; i < table.spec.numColumns; i++) {
		if (table.spec.colNames[i] === "hilite") {
			hCol = i;
			break;
		}
	}
	if (typeof hCol == 'undefined')
		return;
	var data = table.data;
	for (var i = 0; i < data.length; i++) {
		data[i][hCol] = !data[i][hCol];
	}
}

function loadTableView() {
	console.log("loading table view");
    $('#view1').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="dynTable"></table>');
	$('#dynTable').dataTable(
		{
			"aaData": table.data,
			"aoColumns": getColumnHeads(),
			"bJQueryUI": true //use theme roller style
		}
	); 
}

function getColumnHeads() {
	var ret = Array();
	var colNames = table.spec.colNames;

	for (var i = 0; i < colNames.length; i++) {
		ret.push({
			"sTitle" : colNames[i]
		});
	}
	return ret;
}
