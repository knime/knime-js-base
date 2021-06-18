#!/bin/bash
echo "Setting JS image generation mode to '${KNIME_JS_IMAGE_GENARATION_MODE}' for ${KNIME_OS}" 
cp "${WORKSPACE}/workflow-tests/templates/preferences-${KNIME_JS_IMAGE_GENARATION_MODE}-${KNIME_OS}.epf" "${WORKSPACE}/workflow-tests/preferences-${KNIME_OS}.epf"