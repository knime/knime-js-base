/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   29 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.selection.column;

import java.util.Map;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultKaiNodeInterface;
import org.knime.core.webui.node.dialog.kai.KaiNodeInterface;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeValue;
import org.knime.js.base.node.widget.WidgetNodeFactory;

/**
 * Factory for the column selection widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnSelectionWidgetNodeFactory extends WidgetNodeFactory< //
        ColumnSelectionWidgetNodeModel, //
        ReExecutableColumnSelectionNodeRepresentation<ColumnSelectionNodeValue>, //
        ColumnSelectionNodeValue> {

    private static final String NAME = "Column Selection Widget";

    private static final String DESCRIPTION = "Creates a column selection widget for use in components views. "
        + "Outputs a string flow variable with the name of the selected column.";

    @SuppressWarnings({"deprecation", "restriction"})
    private static final WebUINodeConfiguration CONFIG = WebUINodeConfiguration.builder()//
        .name(NAME) //
        .icon("./widget_column_select.png") //
        .shortDescription(DESCRIPTION) //
        .fullDescription(DESCRIPTION) //
        .modelSettingsClass(ColumnSelectionWidgetNodeParameters.class) //
        .addInputTable("Table Input", "Table containing the columns to be selected.") //
        .addOutputPort("Flow Variable Output", FlowVariablePortObject.TYPE,
            "Variable output (string) with the selected column name.") //
        .nodeType(NodeType.Widget) //
        .build();

    @SuppressWarnings("javadoc")
    public ColumnSelectionWidgetNodeFactory() {
        super(CONFIG, ColumnSelectionWidgetNodeParameters.class);
    }

    @Override
    public ColumnSelectionWidgetNodeModel createNodeModel() {
        return new ColumnSelectionWidgetNodeModel(getInteractiveViewName());
    }

    @Override
    public String getInteractiveViewName() {
        return NAME;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new ColumnSelectionWidgetNodeDialog();
    }

    @SuppressWarnings("restriction")
    @Override
    public KaiNodeInterface createKaiNodeInterface() {
        return new DefaultKaiNodeInterface(Map.of(SettingsType.MODEL, ColumnSelectionWidgetNodeParameters.class));
    }

}
