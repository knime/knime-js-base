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
 *   10 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.slider;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.settings.numberFormat.NumberFormatSettings;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MaxValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;

/**
 * Utility class to create the WebUI dialog for number format settings (see: {@link NumberFormatSettings}).
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@LoadDefaultsForAbsentFields
public final class NumberFormatParameters implements NodeParameters {

    private static final String CFG_DECIMALS = "decimals";

    private static final String CFG_MARK = "mark";

    private static final String CFG_THOUSAND = "thousand";

    private static final String CFG_PREFIX = "prefix";

    private static final String CFG_POSTFIX = "postfix";

    private static final String CFG_NEGATIVE = "negative";

    private static final String CFG_NEGATIVE_BEFORE = "negativeBefore";

    private static final String CFG_NEGATIVE_CLASSES = "negativeClasses";

    private static final String CFG_ENCODER = "encoder";

    private static final String CFG_DECODER = "decoder";

    private static final String CFG_EDIT = "edit";

    private static final String CFG_UNDO = "undo";

    @Widget(title = "Decimal digits",
        description = "The number of decimal digits to be shown. Use 0 for no decimal digits. Maximum is 7 digits.")
    @NumberInputWidget(minValidation = IsNonNegativeValidation.class, maxValidation = IsMax7Validation.class)
    @Persistor(DecimalsPersistor.class)
    int m_decimals = 2;

    @Widget(title = "Decimal separator", description = "The decimal separator character. Defaults to '.'")
    @Persist(configKey = CFG_MARK)
    String m_mark = ".";

    @Widget(title = "Thousands separator",
        description = "The thousands separator character. Leave blank for displaying no separator character.")
    @Persist(configKey = CFG_THOUSAND)
    String m_thousandSeparator;

    @Widget(title = "Custom prefix",
        description = "A custom prefix string. A common use case for this is a currency symbol.")
    @Persist(configKey = CFG_PREFIX)
    String m_prefix;

    @Widget(title = "Custom postfix", description = "A custom string rendered after the number.")
    @Persist(configKey = CFG_POSTFIX)
    String m_postfix;

    @Widget(title = "Negative sign", description = "The string used to denote a negative number. Defaults to '-'")
    @Persist(configKey = CFG_NEGATIVE)
    String m_negative = "-";

    @Widget(title = "Negative before string",
        description = "A custom string rendered before any custom prefix, when number is negative.")
    @Persist(configKey = CFG_NEGATIVE_BEFORE)
    String m_negativeBefore;

    @Persist(configKey = CFG_NEGATIVE_CLASSES)
    String m_negativeClasses;

    @Persist(configKey = CFG_ENCODER)
    String m_encoder;

    @Persist(configKey = CFG_DECODER)
    String m_decoder;

    @Persist(configKey = CFG_EDIT)
    String m_edit;

    @Persist(configKey = CFG_UNDO)
    String m_undo;

    private static final class IsMax7Validation extends MaxValidation {
        @Override
        protected double getMax() {
            return 7.0;
        }
    }

    private static final class DecimalsPersistor implements NodeParametersPersistor<Integer> {

        @Override
        public Integer load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return loadDecimals(settings);
        }

        @Override
        public void save(final Integer param, final NodeSettingsWO settings) {
            saveDecimals(param, settings);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_DECIMALS}};
        }

    }

    private static int loadDecimals(final NodeSettingsRO settings) throws InvalidSettingsException {
        String decimalString = settings.getString(CFG_DECIMALS);
        try {
            return Integer.parseInt(decimalString);
        } catch (NumberFormatException e) {
            throw new InvalidSettingsException("Could not parse decimals as integer: " + decimalString, e);
        }
    }

    private static void saveDecimals(final Integer decimals, final NodeSettingsWO settings) {
        settings.addString(CFG_DECIMALS, String.valueOf(decimals));
    }

    /**
     * Loads the number format parameters from the given settings.
     *
     * @param settings the settings to load from
     * @return the loaded number format parameters
     * @throws InvalidSettingsException if loading fails
     */
    public static NumberFormatParameters loadNumberFormatParameters(final NodeSettingsRO settings)
        throws InvalidSettingsException {
        final var numberFormatParameters = new NumberFormatParameters();
        numberFormatParameters.m_decimals = loadDecimals(settings);
        numberFormatParameters.m_mark = settings.getString(CFG_MARK);
        numberFormatParameters.m_thousandSeparator = settings.getString(CFG_THOUSAND);
        numberFormatParameters.m_prefix = settings.getString(CFG_PREFIX);
        numberFormatParameters.m_postfix = settings.getString(CFG_POSTFIX);
        numberFormatParameters.m_negative = settings.getString(CFG_NEGATIVE);
        numberFormatParameters.m_negativeBefore = settings.getString(CFG_NEGATIVE_BEFORE);
        numberFormatParameters.m_negativeClasses = settings.getString(CFG_NEGATIVE_CLASSES);
        numberFormatParameters.m_encoder = settings.getString(CFG_ENCODER);
        numberFormatParameters.m_decoder = settings.getString(CFG_DECODER);
        numberFormatParameters.m_edit = settings.getString(CFG_EDIT);
        numberFormatParameters.m_undo = settings.getString(CFG_UNDO);
        return numberFormatParameters;
    }

    /**
     * Saves the given number format parameters into the given settings.
     *
     * @param param the number format parameters to save
     * @param settings the settings to save into
     */
    public static void saveNumberFormatParameters(final NumberFormatParameters param, final NodeSettingsWO settings) {
        saveDecimals(param.m_decimals, settings);
        settings.addString(CFG_MARK, param.m_mark);
        settings.addString(CFG_THOUSAND, param.m_thousandSeparator);
        settings.addString(CFG_PREFIX, param.m_prefix);
        settings.addString(CFG_POSTFIX, param.m_postfix);
        settings.addString(CFG_NEGATIVE, param.m_negative);
        settings.addString(CFG_NEGATIVE_BEFORE, param.m_negativeBefore);
        settings.addString(CFG_NEGATIVE_CLASSES, param.m_negativeClasses);
        settings.addString(CFG_ENCODER, param.m_encoder);
        settings.addString(CFG_DECODER, param.m_decoder);
        settings.addString(CFG_EDIT, param.m_edit);
        settings.addString(CFG_UNDO, param.m_undo);
    }

    /**
     * @return the config paths for the number format parameters
     */
    public static String[][] getConfigPaths() {
        return new String[][]{{CFG_DECIMALS}, //
            {CFG_MARK}, //
            {CFG_THOUSAND}, //
            {CFG_PREFIX}, //
            {CFG_POSTFIX}, //
            {CFG_NEGATIVE}, //
            {CFG_NEGATIVE_BEFORE}};
    }

}
