/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals by:false, document:false, module: false*/
function Locators() {
  'use strict';
  this.addLocators = function(ptor) {
  if (!by.translateKey) {
    ptor.by.addLocator('translateKey', function(key, opt_parentElement, opt_rootSelector) {
      var using = opt_parentElement || document,
        elements = using.querySelectorAll('[translate="' + key + '"]');
      if (elements.length) {
        return elements;
      }
    });
  }
  if (!by.ngMessages) {
    // protractor locator for finding elements by attribute [ng-messages]
    // generally used for validation messages with angular material
    ptor.by.addLocator('ngMessages', function(ngMessages, opt_parentElement, opt_rootSelector) {
      var using = opt_parentElement || document,
        elements = using.querySelectorAll('[ng-messages="' + ngMessages + '"]');
      if (elements.length) {
        return elements;
      }
    });
  }
}
}
module.exports = Locators;
