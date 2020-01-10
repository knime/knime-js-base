/* global require: false, requirejs: false */
window.dynamicJSNode = (function () {

    var node = {};
    var _innerNamespace;
    var VAADIN_PREFIX = './VAADIN/src-js/'; // legacy web portal
    var errorRendered = false;
    var renderError;

    /** @since 4.2 support output type APPEND_SELECTION_COLUMN */
    node.APPEND_SELECTION_GLOBAL_OUT_VALUE_KEY = 'append_selection_out_columns';

    node.init = function (representation, value) {
        if (representation.errorMessage) {
            renderError(representation.errorMessage);
            return;
        }
        if (!representation.jsNamespace) {
            document.body.innerHTML = '<p>No data to display.</p>';
            return;
        }
        _innerNamespace = representation.jsNamespace;

        // Define endsWith on strings
        String.prototype.endsWith = function (suffix) {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        };

        // Import style dependencies
        var head = document.getElementsByTagName('head')[0];
        var linkBefore = document.getElementsByTagName('link')[0] || head.firstChild;
        for (var j = 0; j < representation.cssDependencies.length; j++) {
            var href = representation.cssDependencies[j];
            if (knimeService.resourceBaseUrl) { // resourceBaseUrl is injected by web portal / pagebuilder
                href = knimeService.resourceBaseUrl + '/' + href;
            } else if (knimeService.isRunningInWebportal()) { // legacy web portal
                href = VAADIN_PREFIX + href;
            }
            var styleDep = document.createElement('link');
            styleDep.type = 'text/css';
            styleDep.rel = 'stylesheet';
            styleDep.href = href;
            head.insertBefore(styleDep, linkBefore);
        }

        // Import own style declaration
        var styleBefore = document.getElementsByTagName('style')[0];
        for (var k = 0; k < representation.cssCode.length; k++) {
            var styleElement = document.createElement('style');
            styleElement.type = 'text/css';
            styleElement.appendChild(document.createTextNode(representation.cssCode[k]));
            if (styleBefore) {
                head.insertBefore(styleElement, styleBefore);
            } else {
                head.appendChild(styleElement);
            }
        }

        // Import JS dependencies and call JS code after loading
        var libs = representation.jsDependencies;
        if (knimeService.resourceBaseUrl) { // resourceBaseUrl is injected by web portal / pagebuilder
            for (var i = 0; i < libs.length; i++) {
                if (libs[i].local) {
                    libs[i].path = knimeService.resourceBaseUrl + '/' + libs[i].path;
                }
            }
        }

        // Build config object for RequireJS
        var depArray = [];
        var configObj = {};
        configObj.paths = {};
        configObj.shim = {};
        for (var l = 0; l < libs.length; l++) {
            if (libs[l].path.endsWith('.js')) {
                libs[l].path = libs[l].path.substr(0, libs[l].path.length - 3);
            }
            configObj.paths[libs[l].name] = libs[l].path;
            depArray.push(libs[l].name);
            if (!libs[l].usesDefine) {
                var shim = configObj.shim[libs[l].name] = {};
                if (libs[l].dependencies) {
                    shim.deps = libs[l].dependencies;
                }
                if (libs[l].exports) {
                    shim.exports = libs[l].exports;
                }
            }
        }
        requirejs.config(configObj);

        // Load dependencies with RequireJS
        require(depArray, function () {
            try {
                for (var i = 0; i < representation.jsCode.length; i++) {
                    // Execute node's JavaScript code
                    if (window.execScript) {
                        window.execScript(representation.jsCode[i]);
                        break;
                    }
                    var fn = function () {
                        window.eval.call(window, representation.jsCode[i]);
                    };
                    fn();
                }
                // Call init function on newly created global object
                window[_innerNamespace].init(representation, value, arguments);
            } catch (e) {
                var errorString = 'Error in script\n';
                if (e.stack) {
                    errorString += e + '\n' + e.stack;
                } else {
                    errorString += e;
                }
                alert(errorString);
            }
        });
    };

    renderError = function (errorMessage) {
        var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        document.getElementsByTagName('body')[0].appendChild(svg);
        svg.setAttribute('width', '600px');
        svg.setAttribute('height', '40px');
        var text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        svg.appendChild(text);
        text.setAttribute('x', '0');
        text.setAttribute('y', '20');
        text.setAttribute('font-family', 'sans-serif');
        text.setAttribute('font-size', '12');
        text.appendChild(document.createTextNode(errorMessage));
        errorRendered = true;
    };

    node.validate = function () {
        if (!window[_innerNamespace]) {
            return false;
        }
        return window[_innerNamespace].validate();
    };

    node.setValidationError = function (err) {
        if (window[_innerNamespace]) {
            window[_innerNamespace].setValidationError(err);
        }
    };

    node.getComponentValue = function () {
        if (window[_innerNamespace]) {
            return window[_innerNamespace].getComponentValue();
        }
    };

    node.getSVG = function () {
        var svg;
        if (errorRendered) {
            svg = document.getElementsByTagName('svg')[0];
        } else if (window[_innerNamespace]) {
            svg = window[_innerNamespace].getSVG();
        }
        if (svg) {
            if (typeof svg === 'string') {
                return svg;
            }
            if (typeof svg === 'object' && svg.nodeType > 0) {
                return new XMLSerializer().serializeToString(svg);
            }
        }
    };

    return node;

})();
