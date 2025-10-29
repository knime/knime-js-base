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
 *   20 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget;

import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_DESCRIPTION;
import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_LABEL;
import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_REQUIRED;

import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.widget.text.TextInputWidget;
import org.knime.node.parameters.widget.text.util.ColumnNameValidationUtils.ColumnNameValidation;

/**
 * This class specifies the common settings of widget nodes.
 *
 * @author Robin Gerling
 */
public abstract class WidgetNodeParameters implements NodeParameters {
    /**
     * Default constructor
     *
     * @param nodeConfigClass the nodeConfigClass to determine the default flow variable name from
     */
    protected WidgetNodeParameters(final Class<?> nodeConfigClass) {
        final var defaultParamName = SubNodeContainer.getDialogNodeParameterNameDefault(nodeConfigClass);
        m_flowVariableName = defaultParamName;
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the view.")
    @Layout(FormFieldSection.class)
    String m_label = DEFAULT_LABEL;

    @Widget(title = "Description",
        description = "Some lines of description that will be shown in the view, for instance by means of a tooltip.")
    @Layout(FormFieldSection.class)
    String m_description = DEFAULT_DESCRIPTION;

    @Widget(title = "Variable name", description = "The name of the exported flow variable.")
    @Layout(OutputSection.Bottom.class)
    @TextInputWidget(patternValidation = ColumnNameValidation.class)
    String m_flowVariableName;

    /**
     * A legacy setting from the old nodes which can be enabled from the flow variables tab or the layout editor. See
     * {@link WizardNode#isHideInWizard()}.
     */
    boolean m_hideInWizard = WidgetConfig.DEFAULT_HIDE_IN_WIZARD;

    /**
     * This setting was not shown in the dialog previously and is not recommended anymore, but is needed for backwards
     * compatibility.
     */
    String m_customCSS = "";

    /**
     * See {@link LabeledConfig}. This setting was initially thought to be a useful feature to have, but it was never
     * implemented in any client. We probably want to remove it in the future, but if we do so now, the node model will
     * not be able to load the settings.
     */
    boolean m_required = DEFAULT_REQUIRED;
}
