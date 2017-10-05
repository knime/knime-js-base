/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
  * History
  *   Dec 27, 2011 (morent): created
  */

package org.knime.quickform.nodes.in.sketcher;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.SketcherInputQuickFormInElement;
import org.knime.quickform.nodes.in.QuickFormInNodeModel;

/**
 *
 * @author Dominik Morent, KNIME.com, Zurich, Switzerland
 * @since 2.6
 *
 */
public class SketcherInputQuickFormInNodeModel
        extends QuickFormInNodeModel<SketcherInputQuickFormInConfiguration> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        SketcherInputQuickFormInConfiguration cfg = getConfiguration();
        SketcherInputQuickFormInElement e = new SketcherInputQuickFormInElement(
                cfg.getLabel(), cfg.getDescription(), cfg.getWeight());
        SketcherInputQuickFormValueInConfiguration config =
                cfg.getValueConfiguration();
        e.setValue(config.getValue());
        e.setFormat(config.getFormat());
        e.setInline(config.isInline());
        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromQuickFormElement(final AbstractQuickFormInElement e)
            throws InvalidSettingsException {
        SketcherInputQuickFormInConfiguration cfg = getConfiguration();
        SketcherInputQuickFormInElement ki =
            AbstractQuickFormInElement.cast(
                    SketcherInputQuickFormInElement.class, e);
        SketcherInputQuickFormValueInConfiguration config =
                cfg.getValueConfiguration();
        config.setValue(ki.getValue());
        config.setFormat(ki.getFormat());
        config.setInline(ki.isInline());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SketcherInputQuickFormInConfiguration createConfiguration() {
        return new SketcherInputQuickFormInConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        SketcherInputQuickFormInConfiguration cfg = getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available");
        }
        String variableName = cfg.getVariableName();
        SketcherInputQuickFormValueInConfiguration config =
                cfg.getValueConfiguration();
        pushFlowVariableString(variableName, config.getValue());
        pushFlowVariableString("molecule_format", config.getFormat());
    }

}
