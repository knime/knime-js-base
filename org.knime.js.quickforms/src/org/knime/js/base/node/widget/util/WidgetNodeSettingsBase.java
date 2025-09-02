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
 *   AI Migration - Base class for widget node settings
 */
package org.knime.js.base.node.widget.util;

import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Base class providing common sections and settings shared across all widget nodes.
 * This class contains the standard widget configuration options that appear in most widget dialogs.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public class WidgetNodeSettingsBase {

    /**
     * Common section interfaces used across widget nodes.
     */
    public static class CommonSections {
        
        @Section(title = "Label and Description")
        public interface LabelSection {
        }

        @Section(title = "Parameter")
        @After(LabelSection.class)
        public interface ParameterSection {
        }

        @Section(title = "Input Configuration")
        @After(ParameterSection.class)
        public interface InputSection {
        }

        @Section(title = "Validation")
        @After(InputSection.class)
        public interface ValidationSection {
        }

        @Section(title = "Editor Settings")
        @After(ValidationSection.class)
        public interface EditorSection {
        }
    }

    // Common widget settings fields that can be inherited by specific widget implementations

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    protected String m_label = "Label";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    protected String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution). This will also be the name of the exported flow variable.")
    @Layout(CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    protected String m_parameter = "input";

    @Widget(title = "Required", description = "If checked, the widget must be configured before the workflow can be executed")
    @Layout(CommonSections.ParameterSection.class)
    @Persist(configKey = "required")
    protected boolean m_required = true;

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    protected boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    protected String m_customCSS = "";

    // Getters for the common settings (if needed by subclasses)

    public String getLabel() {
        return m_label;
    }

    public String getDescription() {
        return m_description;
    }

    public String getParameter() {
        return m_parameter;
    }

    public boolean isRequired() {
        return m_required;
    }

    public boolean isHideInWizard() {
        return m_hideInWizard;
    }

    public String getCustomCSS() {
        return m_customCSS;
    }
}
