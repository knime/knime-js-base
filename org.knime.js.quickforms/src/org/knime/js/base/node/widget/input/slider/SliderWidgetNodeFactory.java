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
 *   May 27, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.widget.input.slider;

import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.core.webui.node.impl.WebUINodeFactory;
import org.knime.js.base.node.base.input.slider.SliderNodeRepresentation;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;

/**
 * Factory for the slider widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class SliderWidgetNodeFactory extends WebUINodeFactory<SliderWidgetNodeModel> implements
    WizardNodeFactoryExtension<SliderWidgetNodeModel, SliderNodeRepresentation<SliderNodeValue>, SliderNodeValue> {

    private static final WebUINodeConfiguration CONFIGURATION = WebUINodeConfiguration.builder()
        .name("Slider Widget")
        .icon("./widget_slider.png")
        .shortDescription("Outputs a double flow variable with a value controlled by a slider.")
        .fullDescription("""
                Outputs a double flow variable with a value controlled by a slider widget in the WebPortal.
                The slider can be configured with custom ranges, step sizes, and appearance options.
                """)
        .modelSettingsClass(SliderWidgetNodeSettings.class)
        .addOutputPort("Flow Variable Output", FlowVariablePortObject.TYPE,
            "Variable output with the given variable defined.")
        .nodeType(NodeType.Widget)
        .sinceVersion(5, 8, 0)
        .build();

    /**
     * Constructor.
     */
    public SliderWidgetNodeFactory() {
        super(CONFIGURATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SliderWidgetNodeModel createNodeModel() {
        return new SliderWidgetNodeModel(getInteractiveViewName());
    }

}
