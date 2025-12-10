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
 *   14 Apr 2018 (albrecht): created
 */
package org.knime.js.base.node.widget.filter.definition.value;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.port.viewproperty.FilterDefinitionHandlerPortObject;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.js.base.node.widget.WidgetNodeFactory;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetValue;

/**
 * Factory for the value filter definition node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"deprecation", "restriction"})
public class ValueFilterDefinitionNodeFactory extends WidgetNodeFactory< //
        ValueFilterDefinitionWidgetNodeModel, ValueFilterDefinitionWidgetRepresentation, RangeFilterWidgetValue> {

    private static final String NAME = "Interactive Value Filter Widget";

    private static final String DESCRIPTION =
        "Defines a filter definition to the input table and provides an interactive value filter on nominal columns.";

    private static final String FULL_DESCRIPTION = """
            <p>A value filter which can be used to trigger interactive filter events in a layout of views (e.g. the \
            WebPortal). The node appends a filter definition to the table spec.</p>
            <p>Only view nodes downstream of this node can receive interactive filter events.</p>
            <p>Note that the filter uses the possible values set on a nominal column's domain as configuration. To \
            ensure the values are reflected and sorted correctly the <i>Domain Calculator</i> and/or \
            <i>Edit Nominal Domain</i> nodes can be used beforehand.</p>
            """;

    static final WebUINodeConfiguration CONFIG = WebUINodeConfiguration.builder() //
        .name(NAME) //
        .icon("./value_filter.png") //
        .shortDescription(DESCRIPTION) //
        .fullDescription(FULL_DESCRIPTION) //
        .modelSettingsClass(ValueFilterDefinitionWidgetNodeParameters.class) //
        .addInputTable("Input Table",
            "Input table which contains at least one nominal column with domain including possible values set, "
                + "which is used to control the filter view.") //
        .addOutputTable("Table with Filter Definition",
            "Input table with filter definition appended to the selected column.") //
        .addOutputPort("Filter Definition", FilterDefinitionHandlerPortObject.TYPE,
            "Filter definition applied to the selected column.") //
        .nodeType(NodeType.Widget) //
        .build();

    @SuppressWarnings("javadoc")
    public ValueFilterDefinitionNodeFactory() {
        super(CONFIG, ValueFilterDefinitionWidgetNodeParameters.class);
    }

    @Override
    public ValueFilterDefinitionWidgetNodeModel createNodeModel() {
        return new ValueFilterDefinitionWidgetNodeModel(getInteractiveViewName());
    }

    @Override
    public String getInteractiveViewName() {
        return NAME;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new ValueFilterDefinitionWidgetDialog();
    }

}
