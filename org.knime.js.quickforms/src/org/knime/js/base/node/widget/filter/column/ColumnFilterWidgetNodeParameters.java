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
package org.knime.js.base.node.widget.filter.column;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.filterandselection.ColumnFilterNodeParameters;
import org.knime.js.base.node.parameters.filterandselection.EnableSearchParameter;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters.LimitVisibleOptionsParametersModifier;
import org.knime.js.base.node.widget.ReexecutionWidgetNodeParameters;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * WebUI Node Parameters for the Column Filter Widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
public final class ColumnFilterWidgetNodeParameters extends ReexecutionWidgetNodeParameters {

    ColumnFilterWidgetNodeParameters() {
        super(ColumnFilterWidgetConfig.class);
    }

    @PersistWithin.PersistEmbedded
    ColumnFilterNodeParameters m_columnFilterNodeParameters = new ColumnFilterNodeParameters();

    @PersistWithin.PersistEmbedded
    @Modification(LimitVisibleOptionsModification.class)
    @Layout(FormFieldSection.class)
    LimitVisibleOptionsParameters m_limitVisibleOptionsParameters = new LimitVisibleOptionsParameters(true);

    @PersistWithin.PersistEmbedded
    EnableSearchParameter m_enableSearchParameter = new EnableSearchParameter();

    private static final class LimitVisibleOptionsModification extends LimitVisibleOptionsParametersModifier {

        @Override
        public String getLimitNumVisOptionsDescription() {
            return """
                    By default the filter component adjusts its height to display all possible choices without a \
                    scroll bar. If the setting is enabled, you will be able to limit the number of visible options in \
                    case you have too many of them.""";
        }

        @Override
        public String getNumVisOptionsDescription() {
            return """
                    A number of options visible in the filter component without a vertical scroll bar. Changing this \
                    value will also affect the component's height. Notice that the height cannot be less than the \
                    overall height of the control buttons in the middle.""";
        }

        @Override
        public Class<? extends MinValidation> getMinNumVisOptions() {
            return IsMin5Validation.class;
        }

    }

    private static final class IsMin5Validation extends MinValidation {

        @Override
        protected double getMin() {
            return 5;
        }

    }

}
