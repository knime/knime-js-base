/* globals $:true, checkBoxesMultipleSelections:true, listMultipleSelections:true, twinlistMultipleSelections:true,
   radioButtonSingleSelection:true, listSingleSelection:true, dropdownSingleSelection:true */
/* eslint-disable new-cap */
/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Sep 18, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
window.knimeValueFilterWidget = (function () {

    var filter = {
        name: 'KNIME Interactive Value Filter Widget',
        version: '2.0.0'
    };
    var _representation, _value, selector;
    
    function setFilterOnValue() {
        var changed = true;
        var curValues;
        if (_representation.multipleValues) {
            curValues = selector.getSelections();
        } else {
            curValues = [selector.getSelection()];
        }
        if (!_value.filter) {
            _value.filter = {
                id: _representation.filterID,
                columns: []
            };
            _value.filter.columns.push({
                values: null
            });
        }
        changed = JSON.stringify(_value.filter.columns[0].values) !== JSON.stringify(curValues);
        _value.filter.columns[0].values = curValues;
        return changed;
    }
    
    function selectionChanged() {
        if (setFilterOnValue()) {
            knimeService.addToFilter(_representation.tableID, _value.filter);
        }
    }

    filter.init = function (representation, value) {
        _representation = representation;
        _value = value;
        var body = $('body');
        var qfdiv = $('<div class="quickformcontainer knime-qf-container">');
        body.append(qfdiv);
        if (_representation.label) {
            qfdiv.attr('aria-label', _representation.label);
            qfdiv.append('<div class="label knime-qf-title">' + _representation.label + '</div>');
        }
        if (_representation.possibleValues === null) {
            qfdiv.append('Error: No data available');
            return;
        }
        if (_representation.multipleValues) {
            if (_representation.type === 'Check boxes (vertical)') {
                selector = new checkBoxesMultipleSelections(true);
            } else if (_representation.type === 'Check boxes (horizontal)') {
                selector = new checkBoxesMultipleSelections(false);
            } else if (_representation.type === 'List') {
                selector = new listMultipleSelections();
            } else {
                selector = new twinlistMultipleSelections();
            }
        } else {
            if (_representation.type === 'Radio buttons (vertical)') { // eslint-disable-line no-lonely-if
                selector = new radioButtonSingleSelection(true);
            } else if (_representation.type === 'Radio buttons (horizontal)') {
                selector = new radioButtonSingleSelection(false);
            } else if (_representation.type === 'List') {
                selector = new listSingleSelection();
            } else {
                selector = new dropdownSingleSelection();
            }
        }
        qfdiv.append(selector.getComponent());
        if ((representation.type === 'List' || representation.type === 'Twinlist') &&
            representation.limitNumberVisOptions) {
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
    };

    filter.validate = function () {
        return true;
    };

    filter.setValidationError = function (message) {
        /* not needed atm */
    };

    filter.getComponentValue = function () {
        setFilterOnValue();
        return _value;
    };

    return filter;

})();
