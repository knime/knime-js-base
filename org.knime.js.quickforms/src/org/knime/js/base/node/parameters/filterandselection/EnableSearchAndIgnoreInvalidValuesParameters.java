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
 *   30 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.filterandselection;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.filterandselection.MultipleSelectionComponentParameters.IsComboboxSelectionType;
import org.knime.js.base.node.widget.selection.multiple.MultipleSelectionWidgetNodeParameters;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.Migrate;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;

/**
 * The enable search and ignore invalid values parameters for use in the {@link MultipleSelectionWidgetNodeParameters}.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
public final class EnableSearchAndIgnoreInvalidValuesParameters implements NodeParameters {

    /**
     * The config key for the ignore invalid values setting.
     */
    public static final String CFG_IGNORE_INVALID_VALUES = "ignore_invalid_values";

    /**
     * The default value for the ignore invalid values setting.
     */
    public static final boolean DEFAULT_IGNORE_INVALID_VALUES = true;

    @Effect(predicate = IsComboboxSelectionType.class, type = EffectType.HIDE)
    @PersistWithin.PersistEmbedded
    EnableSearchParameter m_enableSearchParameter = new EnableSearchParameter();

    @Widget(title = "Ignore missing selected values",
        description = "If this option is checked, selected values that are missing from the input data will not be"
            + " shown in the widget and they will be removed from the list of selected values once settings are"
            + " applied. If this option is not checked missing values will be shown in the widget and need to be"
            + " removed manually to pass input validation.")
    @Persist(configKey = CFG_IGNORE_INVALID_VALUES)
    @Migrate(loadDefaultIfAbsent = true)
    @Layout(FormFieldSection.class)
    boolean m_ignoreInvalidValues = DEFAULT_IGNORE_INVALID_VALUES;

}
