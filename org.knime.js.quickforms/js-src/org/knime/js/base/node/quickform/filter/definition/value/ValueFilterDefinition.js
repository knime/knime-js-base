/* globals $:true, checkBoxesMultipleSelections:true, listMultipleSelections:true, twinlistMultipleSelections:true, 
   radioButtonSingleSelection:true, listSingleSelection:true, dropdownSingleSelection:true */
window.org_knime_js_base_node_quickform_filter_definition_value = function() {
    
    var filter = {};
    var _representation, _value;
    var selector;
    
    filter.init = function(representation, value) {
        _representation = representation;
        _value = value;
        var body = $('body');
        var qfdiv = $('<div class="quickformcontainer knime-qf-container">');
        body.append(qfdiv);
        if (_representation.label) {
            qfdiv.attr("aria-label", _representation.label);
            qfdiv.append('<div class="label knime-qf-title">' + _representation.label + '</div>');
        }
        if (_representation.possibleValues == null) {
            qfdiv.append("Error: No data available");
            return;
        }
        if (_representation.multipleValues) {
            if (_representation.type == 'Check boxes (vertical)') {
                selector = new checkBoxesMultipleSelections(true);
            } else if (_representation.type == 'Check boxes (horizontal)') {
                selector = new checkBoxesMultipleSelections(false);
            } else if (_representation.type == 'List') {
                selector = new listMultipleSelections();
            } else {
                selector = new twinlistMultipleSelections();
            }
        } else {
            if (_representation.type == 'Radio buttons (vertical)') {
                selector = new radioButtonSingleSelection(true);
            } else if (_representation.type == 'Radio buttons (horizontal)') {
                selector = new radioButtonSingleSelection(false);
            } else if (_representation.type == 'List') {
                selector = new listSingleSelection();
            } else {
                selector = new dropdownSingleSelection();
            }
        }
        qfdiv.append(selector.getComponent());
        if ((representation.type == 'List' || representation.type == 'Twinlist') 
                && representation.limitNumberVisOptions) {
            selector.setChoices(_representation.possibleValues, representation.numberVisOptions);               
        } else {
            selector.setChoices(_representation.possibleValues);
        }
        var defSelection = [];
        if (_value.filter && _value.filter.columns && _value.filter.columns[0].values) {
            defSelection = _value.filter.columns[0].values;
        }
        if (_representation.multipleValues) {
            selector.setSelections(defSelection);
        } else {
            if (defSelection.length > 0) {
                defSelection = defSelection[0];
            }
            selector.setSelection(defSelection);
        }
        selector.addValueChangedListener(selectionChanged);
        knimeService.addToFilter(_representation.tableID, _value.filter);
    }
    
    function selectionChanged() {
        if (setFilterOnValue()) {
            knimeService.addToFilter(_representation.tableID, _value.filter);
        }
    }
    
    function setFilterOnValue() {
        var changed = true;
        var curValues;
        if (_representation.multipleValues) {
            curValues = selector.getSelections();
        } else {
            curValues = [selector.getSelection()];
        }
        if (!_value.filter) {
            _value.filter = {"id": _representation.filterID, "columns": []}
            _value.filter.columns.push({"values": null});
        }
        changed = JSON.stringify(_value.filter.columns[0].values) !== JSON.stringify(curValues);
        _value.filter.columns[0].values = curValues;
        return changed;
    }
    
    filter.validate = function() {
        return true;
    }
    
    filter.setValidationError = function(err) {
        /* not needed atm */
    }
    
    filter.getComponentValue = function() {
        setFilterOnValue();
        return _value;
    }
    
    return filter;    
    
}();