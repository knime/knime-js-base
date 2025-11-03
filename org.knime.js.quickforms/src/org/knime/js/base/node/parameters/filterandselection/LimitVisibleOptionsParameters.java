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
 *   28 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.filterandselection;

import org.knime.core.util.Pair;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.configuration.ConfigurationNodeParametersUtility.IsMin2Validation;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.number.NumberInputWidget;

/**
 * The common settings of selection/filter configuration/widgets nodes regarding the limitation of visible options in
 * the frontend component.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
public class LimitVisibleOptionsParameters implements NodeParameters {

    /**
     * Default constructor
     */
    LimitVisibleOptionsParameters() {
    }

    /**
     *
     * @param limitNumberOfVisibleOptions whether to limit the number of visible options
     */
    public LimitVisibleOptionsParameters(final boolean limitNumberOfVisibleOptions) {
        m_limitNumberOfVisibleOptions = limitNumberOfVisibleOptions;
    }

    /**
     * The config key for the limit number of visible options setting.
     */
    public static final String CFG_LIMIT_NUMBER_VIS_OPTIONS = "limit_number_visible_options";

    /**
     * The default value for the limit number of visible options setting.
     */
    public static final boolean DEFAULT_LIMIT_NUMBER_VIS_OPTIONS = false;

    /**
     * The config key for the number of visible options setting.
     */
    public static final String CFG_NUMBER_VIS_OPTIONS = "number_visible_options";

    /**
     * The default value for the limit number of visible options setting.
     */
    public static final Integer DEFAULT_NUMBER_VIS_OPTIONS = 5;

    @Persist(configKey = CFG_LIMIT_NUMBER_VIS_OPTIONS)
    @ValueReference(LimitNumberOfVisibleOptionsValueReference.class)
    @Modification.WidgetReference(LimitNumberOfVisibleOptionsModificationReference.class)
    boolean m_limitNumberOfVisibleOptions = DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    @NumberInputWidget(minValidation = IsMin2Validation.class)
    @Persist(configKey = CFG_NUMBER_VIS_OPTIONS)
    @Modification.WidgetReference(NumberOfVisibleOptionsModificationReference.class)
    @Effect(predicate = ShowNumberOfVisibleOptions.class, type = EffectType.SHOW)
    int m_numberOfVisibleOptions = DEFAULT_NUMBER_VIS_OPTIONS;

    private static final class LimitNumberOfVisibleOptionsValueReference implements ParameterReference<Boolean> {
    }

    /**
     * Effect predicate provider determining whether to show the number of visible options setting
     */
    public static final class ShowNumberOfVisibleOptions implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(LimitNumberOfVisibleOptionsValueReference.class).isTrue();
        }
    }

    private static final class LimitNumberOfVisibleOptionsModificationReference implements Modification.Reference {
    }

    private static final class NumberOfVisibleOptionsModificationReference implements Modification.Reference {
    }

    abstract static class LimitVisibleOptionsParametersModifier implements Modification.Modifier {
        @Override
        public void modify(final WidgetGroupModifier group) {
            group.find(LimitNumberOfVisibleOptionsModificationReference.class) //
                .addAnnotation(Widget.class) //
                .withProperty("title", getLimitNumVisOptionsTitle()) //
                .withProperty("description", getLimitNumVisOptionsDescription()) //
                .modify();

            group.find(NumberOfVisibleOptionsModificationReference.class) //
                .addAnnotation(Widget.class) //
                .withProperty("title", getNumVisOptionsTitle()) //
                .withProperty("description", getNumVisOptionsDescription()) //
                .modify();
            final var effectPredicates = getEffectPredicates();
            if (effectPredicates != null) {
                group.find(LimitNumberOfVisibleOptionsModificationReference.class) //
                    .addAnnotation(Effect.class) //
                    .withProperty("predicate", effectPredicates.getFirst()) //
                    .withProperty("type", EffectType.SHOW) //
                    .modify();
                group.find(NumberOfVisibleOptionsModificationReference.class) //
                    .modifyAnnotation(Effect.class) //
                    .withProperty("predicate", effectPredicates.getSecond()) //
                    .withProperty("type", EffectType.SHOW) //
                    .modify();
            }
        }

        String getLimitNumVisOptionsTitle() {
            return "Limit number of visible options";
        }

        abstract String getLimitNumVisOptionsDescription();

        String getNumVisOptionsTitle() {
            return "Number of visible options";
        }

        abstract String getNumVisOptionsDescription();

        /**
         * Use if the options should be hidden/shown based on another setting.
         *
         * @return a pair of the predicate to show the options at all and the predicate to show the number of visible
         *         options
         */
        Pair<Class<? extends EffectPredicateProvider>, Class<? extends AbstractShowNumberOfVisibleOptions>>
            getEffectPredicates() {
            return null;
        }

        static abstract class AbstractShowNumberOfVisibleOptions implements EffectPredicateProvider {

            Class<? extends EffectPredicateProvider> m_limitNumVisOptionsEffectPredicate;

            /**
             * Pass in the same class as the first value of the pair returned by
             * {@link LimitVisibleOptionsParametersModifier#getNumVisOptionsEffectPredicate()}.
             *
             * @param limitNumVisOptionsEffectPredicate the effect predicate to determine whether to show the number of
             *            visible options setting
             */
            AbstractShowNumberOfVisibleOptions(
                final Class<? extends EffectPredicateProvider> limitNumVisOptionsEffectPredicate) {
                m_limitNumVisOptionsEffectPredicate = limitNumVisOptionsEffectPredicate;
            }

            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return i.getPredicate(ShowNumberOfVisibleOptions.class)
                    .and(i.getPredicate(m_limitNumVisOptionsEffectPredicate));
            }
        }

    }

}
