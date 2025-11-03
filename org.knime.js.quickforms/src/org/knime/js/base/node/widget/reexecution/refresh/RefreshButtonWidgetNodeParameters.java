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
 *   3 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.reexecution.refresh;

import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.widget.ReExecutableConfig;
import org.knime.js.base.node.widget.WidgetNodeParametersFlowVariable;
import org.knime.js.base.node.widget.reexecution.refresh.RefreshButtonWidgetNodeParameters.ExplainFlowVariableName;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;

/**
 * Settings for the refresh button widget node.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
@Modification(ExplainFlowVariableName.class)
public final class RefreshButtonWidgetNodeParameters extends WidgetNodeParametersFlowVariable {

    RefreshButtonWidgetNodeParameters() {
        super(RefreshButtonWidgetNodeConfig.class);
    }

    private static final class DefaultValue implements NodeParameters {
        @Persist(configKey = RefreshButtonWidgetViewValue.CFG_REFRESH_COUNTER)
        int m_refreshCounter = RefreshButtonWidgetViewValue.DEFAULT_REFRESH_COUNTER;

        @Persist(configKey = RefreshButtonWidgetViewValue.CFG_REFRESH_TIMESTAMP)
        String m_refreshTimestamp = RefreshButtonWidgetViewValue.DEFAULT_REFRESH_TIMESTAMP;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Button text", description = "The text of the button.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = RefreshButtonWidgetNodeConfig.CFG_BUTTON_TEXT)
    String m_buttonText = RefreshButtonWidgetNodeConfig.DEFAULT_TEXT;

    @Persist(configKey = ReExecutableConfig.CFG_TRIGGER_REEXECUTION)
    boolean m_triggerReExecution = true;

    static final class ExplainFlowVariableName implements Modification.Modifier {

        @Override
        public void modify(final WidgetGroupModifier group) {
            group.find(WidgetNodeParametersFlowVariable.FlowVariableNameRef.class).modifyAnnotation(Widget.class)
                .withProperty("description", """
                        Variable identifier. Two variables are created with the suffix \
                        <ul>\
                          <li> -counter: Number of refreshes the button has triggered</li>\
                          <li> -timestamp: An ISO formatted timestamp from the last refresh \
                        triggered by the button</li>\
                        </ul>
                            """).modify();
        }

    }

}
