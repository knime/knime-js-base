/* eslint-env jquery */
/* global checkMissingData:false, callUpdate:false, checkBoxesMultipleSelections:false, listMultipleSelections:false,
   twinlistMultipleSelections:false */
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
 *   May 27, 2019 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
window.knimeColumnFilterWidget = (function () {
    
    var columnFilter = {
        version: '2.0.0'
    };
    columnFilter.name = 'KNIME Column Filter Widget';
    var selector;
    var viewValid = false;

    columnFilter.init = function (representation) {
        if (checkMissingData(representation)) {
            return;
        }
        var body = $('body');
        var qfdiv = $('<div class="quickformcontainer knime-qf-container">');
        body.append(qfdiv);
        qfdiv.attr('title', representation.description);
        qfdiv.append('<div class="label knime-qf-title">' + representation.label + '</div>');
        if (representation.possibleColumns === null) {
            qfdiv.append('Error: No data available');
        } else {
            if (representation.type === 'Check boxes (vertical)') {
                selector = new checkBoxesMultipleSelections(true);
            } else if (representation.type === 'Check boxes (horizontal)') {
                selector = new checkBoxesMultipleSelections(false);
            } else if (representation.type === 'List') {
                selector = new listMultipleSelections();
            } else {
                selector = new twinlistMultipleSelections();
            }
            qfdiv.append(selector.getComponent());
            if ((representation.type === 'List' || representation.type === 'Twinlist') &&
                representation.limitNumberVisOptions) {
                selector.setChoices(representation.possibleColumns, representation.numberVisOptions);
            } else {
                selector.setChoices(representation.possibleColumns);
            }
            var selections = representation.currentValue.columns;
            selector.setSelections(selections);
            selector.addValueChangedListener(callUpdate);
        }
        viewValid = true;
    };
    
    columnFilter.validate = function () {
        if (!viewValid) {
            return false;
        }
        return true;
    };
    
    columnFilter.setValidationErrorMessage = function (message) {
        if (!viewValid) {
            return;
        }
        // TODO: display error
    };

    columnFilter.value = function () {
        if (!viewValid) {
            return null;
        }
        var viewValue = {};
        viewValue.columns = selector.getSelections();
        return viewValue;
    };

    return columnFilter;

})();
