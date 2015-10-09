kc.sampleView = function() {
	
	var sampleView = {};
	
	sampleView.name = "sampleView";
	
	sampleView.extensionNames = [ "hilite" ];

	sampleView.run = function() {
		var numHilited = 0;
		var hiliteHandler = kc.getExtension("hilite");
		for ( var i = 0; i < kc.getNumRows(); i++) {
			if (hiliteHandler.isHilited(i))
				numHilited++;
		}
		console.log("Number Hilited items: " + numHilited);
	};
	
	return sampleView;
};