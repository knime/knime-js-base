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
 *   May 25, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.slider;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.js.base.node.configuration.ConfigurationNodeFactory;

/**
 * Factory for the slider configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"deprecation", "restriction"})
public class IntegerSliderDialogNodeFactory extends ConfigurationNodeFactory<IntegerSliderDialogNodeModel> {

    private static final WebUINodeConfiguration CONFIG =
        WebUINodeConfiguration.builder().name("Integer Slider Configuration").icon("./configuration_slider.png")
            .shortDescription("Provides a slider configuration option to an encapsulating component's dialog. "
                + "Outputs a integer flow variable with the set value.")
            .fullDescription("""
                    <p>Outputs an integer flow variable with a set value from a component's dialog.</p>
                    <p>
                    This configuration node allows you to create an integer slider parameter that can be used in
                     component dialogs. The slider can use values from a domain column in the input data to set the
                     minimum and maximum range, or you can specify custom range values.
                    </p>
                    """).modelSettingsClass(IntegerSliderDialogNodeParameters.class).nodeType(NodeType.Configuration)
            .addInputTable("Table Input with applicable domain values",
                "Input table which contains at least one integer "
                    + "column with domain values set, which can be used to control the minimum and maximum values of"
                    + " the slider.")
            .addOutputTable("Flow Variable Output", "Variable output (integer) with the given variable defined.")
            .sinceVersion(5, 4, 0).build();

    public IntegerSliderDialogNodeFactory() {
        super(CONFIG, IntegerSliderDialogNodeParameters.class);
    }

    @Override
    public IntegerSliderDialogNodeModel createNodeModel() {
        return new IntegerSliderDialogNodeModel();
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new IntegerSliderDialogNodeNodeDialog();
    }
}
