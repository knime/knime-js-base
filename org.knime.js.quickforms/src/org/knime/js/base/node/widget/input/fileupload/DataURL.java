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
 *   2 Sep 2020 (albrecht): created
 */
package org.knime.js.base.node.widget.input.fileupload;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Simple parser and handler for data protocol URLs of the format: <br>
 * <code>data:[//][&lt;mediatype&gt;][;base64],&lt;data&gt;</code>
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DataURL {

    /** The scheme part of the data URL (omitting possible '//') */
    public static final String SCHEME = "data:";

    /** The optional base64 encoding string which can be present in data URLs */
    public static final String BASE64_ENCODING = "base64";

    private final Optional<String> m_mediaType;
    private final Optional<String> m_encoding;
    private final String m_data;

    /**
     * Creates a {@code DataURL} object from the {@code String} representation.
     * <p>
     * No additional checks are performed to determine the validity of the optional media type.
     * </p>
     *
     * @param spec the {@code String} to parse as a DataURL.
     * @throws MalformedURLException if {@code spec} is null or does not comply to the data URL specifications.
     */
    public DataURL(final String spec) throws MalformedURLException {
        if (spec == null || !spec.startsWith(SCHEME)) {
            throw new MalformedURLException(String.format("URL does not start with data protocol %s", spec));
        }
        String urlWithoutScheme = spec.substring(SCHEME.length());
        if (urlWithoutScheme.startsWith("//")) {
            urlWithoutScheme = urlWithoutScheme.substring(2);
        }
        int dataSeparatorIndex = urlWithoutScheme.indexOf(',');
        if (dataSeparatorIndex < 0) {
            throw new MalformedURLException(String.format("No comma present in invalid data URL %s", spec));
        }
        if (urlWithoutScheme.length() <= dataSeparatorIndex + 1) {
            throw new MalformedURLException(String.format("No data segment present in URL %s", spec));
        }
        m_data = urlWithoutScheme.substring(dataSeparatorIndex + 1);
        String mediaTypeAndEncoding = urlWithoutScheme.substring(0, dataSeparatorIndex);
        int encodingSeparator = mediaTypeAndEncoding.lastIndexOf(';');
        if (encodingSeparator >= 0 && mediaTypeAndEncoding.length() > encodingSeparator) {
            String encoding = mediaTypeAndEncoding.substring(encodingSeparator + 1);
            if (BASE64_ENCODING.equals(encoding)) {
                m_encoding = Optional.of(BASE64_ENCODING);
                mediaTypeAndEncoding = mediaTypeAndEncoding.substring(0, encodingSeparator);
            } else {
                m_encoding = Optional.empty();
            }
        } else {
            m_encoding = Optional.empty();
        }
        if (mediaTypeAndEncoding.length() > 0) {
            m_mediaType = Optional.of(mediaTypeAndEncoding);
        } else {
            m_mediaType = Optional.empty();
        }
    }

    /**
     * @return an optional media type as parsed from the URL
     */
    public Optional<String> getMediaType() {
        return m_mediaType;
    }

    /**
     * @return an optional encoding as parsed from the URL. Can only be {@link #BASE64_ENCODING} or empty.
     */
    public Optional<String> getEncoding() {
        return m_encoding;
    }

    /**
     * Decodes the raw data string according to the data URL specifications and returns the result.
     *
     * @return A byte array deduced from the decoded Base64 data string. If data was in plain text, the string is
     * returned as UTF-8 byte array.
     * @throws IllegalArgumentException if data is in invalid Base64 format
     */
    public byte[] getDecodedData() throws IllegalArgumentException {
        if (m_encoding.orElse("").equals(BASE64_ENCODING)) {
            return Base64.getDecoder().decode(m_data);
        } else {
            return getRawData().getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * @return the raw data string as parsed from the URL
     */
    public String getRawData() {
        return m_data;
    }

}
