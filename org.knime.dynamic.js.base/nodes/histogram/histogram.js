/* global knimeGroupedBarChart:false */
window.knimeHistogram = (function () {

    var histogram = {};
    var _representation, _value;

    histogram.init = function (representation, value) {
        _value = value;
        _representation = representation;
        var binningResult = _representation.inObjects[0];
        var binColName = binningResult.binnedColumn;
        var orgColName = _representation.options.cat;
        _representation.inObjects[0] = binningResult.groups;
        _representation.options.cat = binColName;
        _representation.isHistogram = true;
        var optMethod = _representation.options.aggr;
        if (optMethod === 'Occurence\u00A0Count') {
            _representation.inObjects[0].table.spec.colNames[1] = orgColName;
        }
        knimeGroupedBarChart.init(_representation, _value);
    };

    histogram.validate = function () {
        return knimeGroupedBarChart.validate();
    };

    histogram.getComponentValue = function () {
        return knimeGroupedBarChart.getComponentValue();
    };

    histogram.getSVG = function () {
        return knimeGroupedBarChart.getSVG();
    };

    return histogram;

})();
