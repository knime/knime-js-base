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
package org.knime.js.base.node.parameters.text;

import static org.knime.js.base.node.base.input.string.RegexPanel.EMAIL_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.EMAIL_LABEL;
import static org.knime.js.base.node.base.input.string.RegexPanel.EMAIL_REGEX;
import static org.knime.js.base.node.base.input.string.RegexPanel.IPV4_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.IPV4_LABEL;
import static org.knime.js.base.node.base.input.string.RegexPanel.IPV4_REGEX;
import static org.knime.js.base.node.base.input.string.RegexPanel.URL_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.URL_LABEL;
import static org.knime.js.base.node.base.input.string.RegexPanel.URL_REGEX;
import static org.knime.js.base.node.base.input.string.RegexPanel.WIN_FILE_PATH_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.WIN_FILE_PATH_LABEL;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.persistence.Persist;

/**
 * The node parameters for text inputs with a validation consisting of a regular expression and an error message.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
public final class TextValidationParameters implements NodeParameters {

    /**
     * The config key for the regex setting.
     */
    public static final String CFG_REGEX = "regex";

    /**
     * The default value for the regex setting.
     */
    public static final String DEFAULT_REGEX = "";

    /**
     * The config key for the error message setting.
     */
    public static final String CFG_ERROR_MESSAGE = "error_message";

    /**
     * The default value for the error message setting.
     */
    public static final String DEFAULT_ERROR_MESSAGE = "";

    // HTML-escaped version of RegexPanel.WIN_FILE_PATH_REGEX
    private static final String WIN_FILE_PATH_REGEX =
        "^((\\\\\\\\[a-zA-Z0-9-]+\\\\[a-zA-Z0-9`~!@#$%^&amp;(){}'._-]+([ ]+[a-zA-Z0-9`~!@#$%^&amp;(){}'._-]+)*)"
            + "|([a-zA-Z]:))(\\\\[^ \\\\/:*?&quot;&quot;&lt;&gt;|]+([ ]+[^ \\\\/:*?&quot;&quot;&lt;&gt;|]+)*)*\\\\?$";

    @Widget(title = "Regex pattern", description = """
            Regular expression defining valid values.
            Single-line editor only.
            Common regex patterns are as follows:
            """ //
        + "<ul>" //
        + "<li><b>" + EMAIL_LABEL + "</b>: " + EMAIL_REGEX + "</li>" //
        + "<li><b>" + URL_LABEL + "</b>: " + URL_REGEX + "</li>" //
        + "<li><b>" + IPV4_LABEL + "</b>: " + IPV4_REGEX + "</li>" //
        + "<li><b>" + WIN_FILE_PATH_LABEL + "</b>: " + WIN_FILE_PATH_REGEX + "</li>" //
        + "</ul>")
    @Persist(configKey = CFG_REGEX)
    String m_regex = DEFAULT_REGEX;

    @Widget(title = "Failure message", description = """
            Message shown if the value is not valid.
            '?' will be replaced by the invalid value.
            Single-line editor only.
            Failure messages corresponding to common regex patterns are as follows:
            """ //
        + "<ul>" //
        + "<li><b>" + EMAIL_LABEL + "</b>: " + EMAIL_ERROR + "</li>" //
        + "<li><b>" + URL_LABEL + "</b>: " + URL_ERROR + "</li>" //
        + "<li><b>" + IPV4_LABEL + "</b>: " + IPV4_ERROR + "</li>" //
        + "<li><b>" + WIN_FILE_PATH_LABEL + "</b>: " + WIN_FILE_PATH_ERROR + "</li>" //
        + "</ul>")
    @Persist(configKey = CFG_ERROR_MESSAGE)
    String m_errorMessage = DEFAULT_ERROR_MESSAGE;

}
