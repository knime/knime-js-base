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
 *   20 Oct 2025 (Robin Gerling, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.parameters;

import org.knime.node.parameters.Advanced;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.widget.text.TextInputWidgetValidation;

/**
 * This class contains common node parameters related functionality of configuration and widget nodes.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
public final class ConfigurationAndWidgetNodeParametersUtil {

    private ConfigurationAndWidgetNodeParametersUtil() {
        // utility
    }

    /**
     * The form field section of a configuration/widget node
     */
    @Section(title = "Form Field")
    public interface FormFieldSection {
    }

    /**
     * The output section of a configuration/widget node
     */
    @Section(title = "Output")
    @After(FormFieldSection.class)
    public interface OutputSection {
        /**
         * The elements at the top of the output section
         */
        interface Top {
        }

        /**
         * The elements at the bottom of the output section
         */
        @After(Top.class)
        interface Bottom {

        }
    }

    /**
     * The advanced settings section of a configuration/widget node
     */
    @Section(title = "Advanced Settings")
    @Advanced
    @After(OutputSection.class)
    public interface AdvancedSettingsSection {
    }

    /**
     * Checks whether the flow variable name is valid, i.e., whether it starts and ends with a letter, and only contains
     * letters, digits, and single dashes.
     */
    public static final class IsValidFlowVariableNameValidation extends TextInputWidgetValidation.PatternValidation {

        @Override
        protected String getPattern() {
            return "[A-Za-z]((?:[A-Za-z0-9]|-(?=[A-Za-z0-9]))*[A-Za-z])?";
        }

        @Override
        public String getErrorMessage() {
            return "Value must start and end with a letter, and may contain only letters, digits, and single dashes.";
        }

    }

}
