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
 *   19 Nov 2025 (robin): created
 */
package org.knime.js.base.node.parameters.filterandselection;

import java.util.function.Supplier;

import org.knime.core.util.Pair;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters.LimitVisibleOptionsParametersModifier;
import org.knime.js.base.node.parameters.filterandselection.MultipleSelectionComponentParameters.SelectionTypeValueReference;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * The visible options modification for multiple selection configuration nodes. Configurations allow to limit the number
 * of visible options for the List and Twinlist component.
 */
@SuppressWarnings("restriction")
public final class LimitVisibleOptionsConfigurationModification extends LimitVisibleOptionsParametersModifier {

    private static final int MIN_NUM_VIS_OPTIONS_TWINLIST = 5;

    @Override
    public String getLimitNumVisOptionsDescription() {
        return """
                By default the List and Twinlist components adjust their height to display all possible \
                choices without a scroll bar. If the setting is enabled, you will be able to limit the number \
                of visible options in case you have too many of them. The setting is available only for List \
                or Twinlist selection type.""";
    }

    @Override
    public String getNumVisOptionsDescription() {
        return """
                A number of options visible in the List or Twinlist component without a vertical scroll bar. \
                Changing this value will also affect the component's height. Notice that for Twinlist the \
                height cannot be less than the overall height of the control buttons in the middle. The \
                setting is available only for List or Twinlist selection type.""";
    }

    @Override
    public Pair<Class<? extends EffectPredicateProvider>, Class<? extends AbstractShowNumberOfVisibleOptions>>
        getEffectPredicates() {
        return new Pair<>(IsListOrTwinlistSelectionType.class, IsListOrTwinlistShowNumberOfVisibleOptions.class);
    }

    @Override
    public Pair<Class<? extends AbstractNumVisOptionsValidationProvider>, //
            Class<? extends AbstractNumVisOptionsValueProvider>> getNumVisOptionsProviders() {
        return new Pair<>(NumVisOptionsMinValidationProvider.class, NumVisOptionsValueProvider.class);
    }

    private static final class IsListOrTwinlistSelectionType implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getString(SelectionTypeValueReference.class).isEqualTo(MultipleSelectionsComponentFactory.LIST).or(
                i.getString(SelectionTypeValueReference.class).isEqualTo(MultipleSelectionsComponentFactory.TWINLIST));
        }
    }

    private static final class IsListOrTwinlistShowNumberOfVisibleOptions extends AbstractShowNumberOfVisibleOptions {
        IsListOrTwinlistShowNumberOfVisibleOptions() {
            super(IsListOrTwinlistSelectionType.class);
        }
    }

    private static final class NumVisOptionsMinValidationProvider extends AbstractNumVisOptionsValidationProvider {
        private Supplier<String> m_selectionTypeSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_selectionTypeSupplier = initializer.computeFromValueSupplier(SelectionTypeValueReference.class);
        }

        @Override
        public MinValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            if (!m_selectionTypeSupplier.get().equals(MultipleSelectionsComponentFactory.TWINLIST)) {
                return super.computeState(parametersInput);
            }
            return new MinValidation() {

                @Override
                protected double getMin() {
                    return MIN_NUM_VIS_OPTIONS_TWINLIST;
                }

            };

        }
    }

    private static final class NumVisOptionsValueProvider extends AbstractNumVisOptionsValueProvider {

        private Supplier<String> m_selectionTypeSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_selectionTypeSupplier = initializer.computeFromValueSupplier(SelectionTypeValueReference.class);
        }

        @Override
        public Integer computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var numVisOptions = m_numVisOptionsSupplier.get();
            final var isTwinlist = m_selectionTypeSupplier.get().equals(MultipleSelectionsComponentFactory.TWINLIST);
            return isTwinlist ? Math.max(numVisOptions, MIN_NUM_VIS_OPTIONS_TWINLIST) : numVisOptions;
        }

    }

}
