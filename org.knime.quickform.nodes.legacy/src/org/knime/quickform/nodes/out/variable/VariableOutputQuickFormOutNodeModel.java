/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * ------------------------------------------------------------------------
 *
 */
package org.knime.quickform.nodes.out.variable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.util.node.quickform.out.VariableOutputQuickFormOutElement;
import org.knime.quickform.nodes.out.QuickFormOutNodeModel;

/**
 * Model to flow variable selector.
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
class VariableOutputQuickFormOutNodeModel extends
        QuickFormOutNodeModel<VariableOutputQuickFormOutConfiguration> {

    private VariableOutputQuickFormOutElement m_element;

    /** {@inheritDoc} */
    @Override
    public VariableOutputQuickFormOutElement getQuickFormElement() {
        return m_element;
    }

    /** {@inheritDoc} */
    @Override
    protected VariableOutputQuickFormOutConfiguration createConfiguration() {
        return new VariableOutputQuickFormOutConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void peekFlowVariable() throws InvalidSettingsException {
        VariableOutputQuickFormOutConfiguration cfg = getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No configuration available");
        }
        String varName = cfg.getVariableName();
        if (varName == null || varName.length() == 0) {
            throw new InvalidSettingsException("Invalid (empty) variable name");
        }

        Object value = null;
        try {
            value = peekFlowVariableString(varName);
        } catch (NoSuchElementException e) {
            // ignore, handle later
        }
        if (value == null) {
            try {
                value = peekFlowVariableInt(varName);
            } catch (NoSuchElementException e) {
                // ignore, handle later
            }
        }
        if (value == null) {
            try {
                value = peekFlowVariableDouble(varName);
            } catch (NoSuchElementException e) {
                // ignore, handle later
            }
        }
        if (value == null) {
            throw new InvalidSettingsException("Unknown variable with "
                    + "identifier \"" + varName + "\"");
        }
        m_element = createQuickFormElement(cfg, value);
    }

    /**
     * @param cfg
     * @param value
     * @return
     */
    private VariableOutputQuickFormOutElement createQuickFormElement(
        final VariableOutputQuickFormOutConfiguration cfg, final Object value) {
        return new VariableOutputQuickFormOutElement(cfg.getLabel(), cfg.getDescription(), cfg.getWeight(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File f = new File(nodeInternDir, "variable_qf.xml");
        if (!f.exists()) {
            // in 2.8.2 and before there was no file saved. This is not a problem in the desktop but in
            // the KNIME WebPortal ... we leave it to fail then.
            return;
        }
        NodeSettingsRO s = NodeSettings.loadFromXML(new BufferedInputStream(new FileInputStream(f)));
        try {
            String type = s.getString("type");
            Object o;
            if ("String".equals(type)) {
                o = s.getString("value");
            } else if ("Integer".equals(type)) {
                o = s.getInt("value");
            } else if ("Double".equals(type)) {
                o = s.getDouble("value");
            } else {
                throw new IOException("Unsupported type: " + type);
            }
            m_element = createQuickFormElement(getConfiguration(), o);
        } catch (InvalidSettingsException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        NodeSettings s = new NodeSettings("variable");
        Object o = m_element.getValue();
        if (o instanceof String) {
            s.addString("type", "String");
            s.addString("value", (String)o);
        } else if (o instanceof Integer) {
            s.addString("type", "Integer");
            s.addInt("value", (Integer)o);
        } else if (o instanceof Double) {
            s.addString("type", "Double");
            s.addDouble("value", (Double)o);
        } else {
            throw new IOException("Unsupported variable type: " + o == null ? "<null>" : o.getClass().getName());
        }
        s.saveToXML(new BufferedOutputStream(new FileOutputStream(new File(nodeInternDir, "variable_qf.xml"))));
    }


}
