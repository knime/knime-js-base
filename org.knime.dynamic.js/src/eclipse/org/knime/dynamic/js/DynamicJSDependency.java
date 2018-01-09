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
 *   Jun 11, 2015 (albrecht): created
 */
package org.knime.dynamic.js;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
public class DynamicJSDependency {

    private static final String NAME = "name";
    private static final String PATH = "path";
    private static final String USES_DEFINE = "usesDefine";
    private static final String EXPORTS = "exports";
    private static final String DEPENDENCIES = "dependencies";
    private static final String LOCAL = "local";

    private String m_name;
    private String m_path;
    private boolean m_usesDefine;
    private String m_exports;
    private List<String> m_dependencies = new ArrayList<String>();
    private boolean m_local;

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        m_name = name;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return m_path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(final String path) {
        m_path = path;
    }

    /**
     * @return if dependency uses define call
     */
    public boolean getUsesDefine() {
        return m_usesDefine;
    }

    /**
     * @param usesDefine the usesDefine to set
     */
    public void setUsesDefine(final boolean usesDefine) {
        m_usesDefine = usesDefine;
    }

    /**
     * @return the exports
     */
    public String getExports() {
        return m_exports;
    }

    /**
     * @param exports the exports to set
     */
    public void setExports(final String exports) {
        m_exports = exports;
    }

    /**
     * @return the dependencies
     */
    public String[] getDependencies() {
        return m_dependencies.toArray(new String[0]);
    }

    /**
     * @param dependencies the dependencies to set
     */
    public void setDependencies(final String[] dependencies) {
        m_dependencies = Arrays.asList(dependencies);
    }

    /**
     * @param dependencies the dependencies to add
     */
    @JsonIgnore
    public void addDependencies(final String... dependencies) {
        m_dependencies.addAll(Arrays.asList(dependencies));
    }

    /**
     * @return the local
     */
    public boolean getLocal() {
        return m_local;
    }

    /**
     * @param local the local to set
     */
    public void setLocal(final boolean local) {
        m_local = local;
    }

    /**
     * @param settings
     */
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(NAME, m_name);
        settings.addString(PATH, m_path);
        settings.addBoolean(USES_DEFINE, m_usesDefine);
        settings.addString(EXPORTS, m_exports);
        settings.addStringArray(DEPENDENCIES, getDependencies());
        settings.addBoolean(LOCAL, m_local);
    }

    /**
     * @param settings
     * @throws InvalidSettingsException
     */
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_name = settings.getString(NAME);
        m_path = settings.getString(PATH);
        m_usesDefine = settings.getBoolean(USES_DEFINE);
        m_exports = settings.getString(EXPORTS);
        setDependencies(settings.getStringArray(DEPENDENCIES));
        m_local = settings.getBoolean(LOCAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        DynamicJSDependency other = (DynamicJSDependency)obj;
        return new EqualsBuilder()
                .append(m_name, other.m_name)
                .append(m_path, other.m_path)
                .append(m_usesDefine, other.m_usesDefine)
                .append(m_exports, other.m_exports)
                .append(m_dependencies, other.m_dependencies)
                .append(m_local, other.m_local)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_name)
                .append(m_path)
                .append(m_usesDefine)
                .append(m_exports)
                .append(m_dependencies)
                .append(m_local)
                .toHashCode();
    }

}
