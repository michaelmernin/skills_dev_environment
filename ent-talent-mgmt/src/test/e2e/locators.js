'use strict';

if (!by.translateKey) {
  by.addLocator('translateKey', function (key, using, rootSelector) {
    var root = document.querySelector(rootSelector || 'body');
    using = using || document;
    
    var elements = using.querySelectorAll('[translate="' + key + '"]');
    if (elements.length) {
      return elements;
    }
  });
}
