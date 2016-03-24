'use strict';

require('../locators.js');

var Sidebar = function () {
  this.ui = {
  	sidebarHeading: element(by.tagName('md-sidenav')).element(by.className('md-toolbar-tools')),
    sidebarElements: element(by.tagName('md-sidenav')).all(by.tagName('md-list-item'))
  };

  Object.defineProperties(this, {
  	sidebarElements: {
      get: function () {
        return this.ui.sidebarElements;
      }
    },
    sidebarHeading: {
      get: function () {
        return this.ui.sidebarHeading.getText();
      }
    },
  });

  this.get = function () {
    browser.get('/');
  };
  
  this.goToIndex = function(index){
  	this.sidebarElements.get(index).click();
  	return this.sidebarElements.get(index).element(by.tagName('span')).getAttribute('innerHTML');
  }
  
  this.getTextAndClick = function(translateKey){
  	var elem = element(by.translateKey(translateKey));
  	var innerText = elem.getAttribute('innerHTML');
  	elem.click();
  	return innerText;
  }
  
  this.getHeader = function(translateKey){
  	return element(by.translateKey(translateKey)).getText();
  }
};

module.exports = Sidebar;
