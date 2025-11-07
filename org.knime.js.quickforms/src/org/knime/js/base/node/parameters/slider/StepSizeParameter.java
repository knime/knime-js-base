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
 *   11 Nov 2025 (robin): created
 */
package org.knime.js.base.node.parameters.slider;

import java.util.Optional;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.migration.Migrate;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.OptionalWidget;
import org.knime.node.parameters.widget.OptionalWidget.DefaultValueProvider;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;

/**
 * Parameter for the step size of a slider widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
public final class StepSizeParameter implements NodeParameters {

    @Widget(title = "Step size", description = "A step size. If set the slider only outputs values in set intervals.")
    @Persistor(StepSizePersistor.class)
    @Migrate(loadDefaultIfAbsent = true)
    @NumberInputWidget(minValidation = IsNonNegativeValidation.class)
    @OptionalWidget(defaultProvider = ProvideOneAsDefaultStepSize.class)
    Optional<Double> m_step = Optional.empty();

    static final class ProvideOneAsDefaultStepSize implements DefaultValueProvider<Double> {

        @Override
        public Double computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return 1.0;
        }

    }

    private static final class StepSizePersistor implements NodeParametersPersistor<Optional<Double>> {

        private static final String CFG_STEP = "step";

        @Override
        public Optional<Double> load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return Optional.ofNullable(settings.getDoubleArray(CFG_STEP)) //
                .filter(arr -> arr.length > 0) //
                .map(arr -> arr[0]);
        }

        @Override
        public void save(final Optional<Double> param, final NodeSettingsWO settings) {
            settings.addDoubleArray(CFG_STEP, param.map(value -> new double[]{value}).orElse(null));
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_STEP}};
        }

    }
}
