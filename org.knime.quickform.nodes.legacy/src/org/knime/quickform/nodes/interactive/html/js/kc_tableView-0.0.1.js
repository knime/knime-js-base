kc.tableView = function() {
	
	var tableView = {};
	
	tableView.name = "kc_tableView";
	
	tableView.extensionNames = [ "hilite" ];

	tableView.init = function() {
		console.log("loading table view");
		tableView.refresh();
	};
	
	tableView.hiliteChangeListener = function(changedRowIDs) {
		tableView.refresh();
	};
	
	tableView.hiliteClearListener = function() {
		tableView.refresh();
	};
	
	tableView.refresh = function() {
		var hiliteHandler = kc.getExtension("hilite");
		var hilites = [];
		for (var i = 0; i < kc.getNumRows(); i++) {
			hilites.push([i, Boolean(hiliteHandler.isHilited(i))]);
		}
		
	    $('#view2').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="dynTable"></table>');
		$('#dynTable').dataTable(
			{
				"aaData": hilites,
				"aoColumns": [{sTitle : "row nr"}, {sTitle: "hilite"}],
				"bJQueryUI": true //use theme roller style
			}
		);		
	};
	
	return tableView;
};