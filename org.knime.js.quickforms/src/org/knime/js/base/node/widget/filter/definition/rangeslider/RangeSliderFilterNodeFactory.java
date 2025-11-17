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
 */
package org.knime.js.base.node.widget.filter.definition.rangeslider;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.port.viewproperty.FilterDefinitionHandlerPortObject;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.js.base.node.widget.WidgetNodeFactory;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetValue;

/**
 * Factory for the range slider filter appender node.
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"deprecation", "restriction"})
public class RangeSliderFilterNodeFactory extends
    WidgetNodeFactory<RangeSliderFilterWidgetNodeModel, RangeSliderFilterWidgetRepresentation, RangeFilterWidgetValue> {

    private static final String NAME = "Interactive Range Slider Filter Widget";

    static final WebUINodeConfiguration CONFIG = WebUINodeConfiguration.builder()//
        .name(NAME) //
        .icon("./range_slider.png") //
        .shortDescription("Defines a filter definition to the input table and provides an interactive slider view.") //
        .fullDescription("""
                <p>A slider which can be used to trigger interactive filter events in a layout of views \
                (e.g. the Hub). The node appends a filter definition to the table spec.</p> \
                <p>Only view nodes downstream of this node can receive interactive filter events.</p>
                """) //
        .modelSettingsClass(RangeSliderFilterWidgetNodeParameters.class) //
        .addInputTable("Input Table",
            "Input table which contains at least one column with domain values set, "
                + "which can be used to control the minimum and maximum values of the slider.") //
        .addOutputTable("Table with Filter Definition", "Input table with filter definition appended to one column.") //
        .addOutputPort("Filter Definition", FilterDefinitionHandlerPortObject.TYPE,
            "Filter definition applied to the input column.") //
        .nodeType(NodeType.Widget) //
        .build();

    @SuppressWarnings("javadoc")
    public RangeSliderFilterNodeFactory() {
        super(CONFIG, RangeSliderFilterWidgetNodeParameters.class);
    }

    @Override
    public RangeSliderFilterWidgetNodeModel createNodeModel() {
        return new RangeSliderFilterWidgetNodeModel(getInteractiveViewName());
    }

    @Override
    public String getInteractiveViewName() {
        return NAME;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new RangeSliderFilterWidgetDialog();
    }
}
