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
 *   4 Apr 2018 (albrecht): created
 */
package org.knime.js.node.minimalRequestHandler;

import java.util.concurrent.ThreadLocalRandom;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.interactive.ViewRequestHandlingException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONViewRequestHandler;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MinimalRequestHandlerNodeModel
    extends AbstractWizardNodeModel<MinimalRequestHandlerViewRepresentation, MinimalRequestHandlerViewValue>
    implements JSONViewRequestHandler<MinimalRequestHandlerViewRequest, MinimalRequestHandlerViewResponse> {

    private final MinimalRequestHandlerConfig m_config;

    /**
     * @param viewName
     */
    public MinimalRequestHandlerNodeModel(final String viewName) {
        super(new PortType[]{}, new PortType[]{}, viewName);
        m_config = new MinimalRequestHandlerConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinimalRequestHandlerViewRequest createEmptyViewRequest() {
        return new MinimalRequestHandlerViewRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinimalRequestHandlerViewResponse handleRequest(final MinimalRequestHandlerViewRequest request,
        final ExecutionMonitor exec)
        throws ViewRequestHandlingException, InterruptedException, CanceledExecutionException {
        if (m_config.isStallRequests()) {
            int stallTime = ThreadLocalRandom.current().nextInt(0, 5000);
            int remainingStallTime = stallTime;
            while (remainingStallTime > 0) {
                int nextIntervall = Math.min(remainingStallTime, 500);
                Thread.sleep(nextIntervall);
                remainingStallTime -= nextIntervall;
                exec.checkCanceled();
                exec.setProgress(1 - (remainingStallTime/(double)stallTime));
            }
        }
        String rString = request.getDummy();
        MinimalRequestHandlerViewResponse response = new MinimalRequestHandlerViewResponse(request);
        response.setDummy("View said: " + rString);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinimalRequestHandlerViewRepresentation createEmptyViewRepresentation() {
        return new MinimalRequestHandlerViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinimalRequestHandlerViewValue createEmptyViewValue() {
        return new MinimalRequestHandlerViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.testing.minimalRequestHandler";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.isHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final MinimalRequestHandlerViewValue viewContent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        return new PortObjectSpec[]{};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        MinimalRequestHandlerViewRepresentation rep = getViewRepresentation();
        rep.setStallRequests(m_config.isStallRequests());
        rep.setKeepOrder(m_config.isKeepOrder());
        rep.setCancelPrevious(m_config.isCancelPrevious());
        return new PortObject[]{};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveToSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new MinimalRequestHandlerConfig()).loadFromSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadFromSettings(settings);
    }

}
