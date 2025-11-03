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
 */
package org.knime.js.base.node.widget.reexecution.refresh;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.js.base.node.widget.WidgetNodeFactory;

/**
 * Factory for the refresh button widget node.
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class RefreshButtonWidgetNodeFactory extends WidgetNodeFactory< //
        RefreshButtonWidgetNodeModel, //
        RefreshButtonWidgetViewRepresentation<RefreshButtonWidgetViewValue>, RefreshButtonWidgetViewValue> {

    static final String NAME = "Refresh Button Widget";

    static final String DESCRIPTION =
        "Creates a button widget with configurable text which emits reactivity events in component visualizations.";

    @SuppressWarnings({"deprecation", "restriction"})
    static final WebUINodeConfiguration CONFIG = WebUINodeConfiguration.builder()//
        .name(NAME) //
        .icon("./widget_refresh.png") //
        .shortDescription(DESCRIPTION) //
        .fullDescription("""
                Adds a button widget with configurable text which can be used to refresh the composite view of the \
                component. The button widget emits reactivity events that trigger the re-execution of downstream \
                nodes in a component when clicked. It allows users to create interactive Data Apps and \
                visualizations.<br /><br />\
                To use the node, connect the variable output port to the nodes which should be re-executed. <b>The \
                downstream nodes of those connected nodes will also be re-executed when the widget is clicked.</b>""")
        .modelSettingsClass(RefreshButtonWidgetNodeParameters.class) //
        .addOutputPort("Flow Variable Output", FlowVariablePortObject.TYPE, """
                Variable output port to connect to nodes which should be re-executed when the widget is clicked. \
                Downstream nodes of a node connected to this flow variable output port will also be re-executed, \
                so it's only necessary to connect to the root node in each workflow branch for which re-execution \
                is desired. The flow variable value is trivial and does not contain any information.""") //
        .nodeType(NodeType.Widget) //
        .build();

    @SuppressWarnings("javadoc")
    public RefreshButtonWidgetNodeFactory() {
        super(CONFIG, RefreshButtonWidgetNodeParameters.class);
    }

    @Override
    public RefreshButtonWidgetNodeModel createNodeModel() {
        return new RefreshButtonWidgetNodeModel(getInteractiveViewName());
    }

    @Override
    public String getInteractiveViewName() {
        return NAME;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new RefreshButtonWidgetNodeDialog();
    }
}
