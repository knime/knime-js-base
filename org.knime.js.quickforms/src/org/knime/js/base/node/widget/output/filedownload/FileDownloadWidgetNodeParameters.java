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
 *   7 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.output.filedownload;

import java.util.List;

import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.VariableType.StringType;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.filehandling.core.data.location.variable.FSLocationVariableType;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.widget.WidgetNodeParametersLabeled;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.Migrate;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.legacy.AutoGuessValueProvider;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.FlowVariableChoicesProvider;

/**
 * Settings for the file download widget node.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public final class FileDownloadWidgetNodeParameters extends WidgetNodeParametersLabeled {

    @Widget(title = "Link title", description = "Title for the link shown in the view.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = FileDownloadConfig.CFG_LINK_TITLE)
    String m_linkTitle = FileDownloadConfig.DEFAULT_LINK_TITLE;

    @Widget(title = "File path variable",
        description = "Select the name of the variable whose value points to the download file.")
    @Layout(FormFieldSection.class)
    @ChoicesProvider(StringAndPathFlowVariablesProvider.class)
    @ValueProvider(FlowVariableValueProvider.class)
    @ValueReference(FlowVariableValueReference.class)
    @Persist(configKey = FileDownloadConfig.CFG_FLOW_VARIABLE)
    String m_flowVariable = FileDownloadConfig.DEFAULT_FLOW_VARIABLE;

    @Widget(title = "Output resource name", description = "A name for the output resource, used for web service calls.")
    @Layout(OutputSection.class)
    @Persist(configKey = FileDownloadConfig.CFG_RESOURCE_NAME)
    @Migrate(loadDefaultIfAbsent = true) // new with 2.12
    String m_resourceName = FileDownloadConfig.DEFAULT_RESOURCE_NAME;

    private static final class FlowVariableValueReference implements ParameterReference<String> {
    }

    private static final class FlowVariableValueProvider extends AutoGuessValueProvider<String> {

        protected FlowVariableValueProvider() {
            super(FlowVariableValueReference.class);
        }

        @Override
        protected boolean isEmpty(final String value) {
            return value == null || value.isEmpty();
        }

        @Override
        protected String autoGuessValue(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var possibleVariables = getPossibleFlowVariables(parametersInput);
            if (possibleVariables.isEmpty()) {
                throw new StateComputationFailureException();
            }
            return possibleVariables.stream()
                .filter(flowVar -> flowVar.getVariableType().equals(FSLocationVariableType.INSTANCE)).findFirst()
                .orElse(possibleVariables.get(0)).getName();
        }

    }

    private static final class StringAndPathFlowVariablesProvider implements FlowVariableChoicesProvider {
        @Override
        public List<FlowVariable> flowVariableChoices(final NodeParametersInput nodeParametersInput) {
            return getPossibleFlowVariables(nodeParametersInput);
        }
    }

    private static List<FlowVariable> getPossibleFlowVariables(final NodeParametersInput nodeParametersInput) {
        return nodeParametersInput.getAvailableInputFlowVariables(StringType.INSTANCE, FSLocationVariableType.INSTANCE)
            .values().stream().toList();
    }

}
