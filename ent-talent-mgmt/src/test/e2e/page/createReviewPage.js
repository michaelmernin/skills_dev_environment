'use strict';

require('../locators.js');

var CreateReviewPage = function () {
  this.ui = {
    startDateInput: element(by.model('review.startDate')),
    endDateInput: element(by.model('review.endDate')),
    saveButton: element(by.translateKey('entity.action.save')),
    reviewTypeError: element(by.xpath('/html/body/div/div[1]/div[2]/div/div/md-content/form/div/div[1]/md-input-container[1]/div/div/span')),
    revieweeError:   element(by.xpath('/html/body/div/div[1]/div[2]/div/div/md-content/form/div/div[1]/md-input-container[2]/div/div/span')),
    startDateError: element(by.xpath('/html/body/div/div[1]/div[2]/div/div/md-content/form/div/div[2]/md-input-container[1]/div/div/span')),
    endDateError: element(by.xpath('/html/body/div/div[1]/div[2]/div/div/md-content/form/div/div[2]/md-input-container[2]/div/div/span')),
    modalWindowContainer: element(by.tagName('md-dialog')),
    modalCancelButton: element(by.xpath('html/body/div[5]/md-dialog/md-dialog-actions/button[1]')),
    modalAcceptButton: element(by.xpath('html/body/div[5]/md-dialog/md-dialog-actions/button[2]')),
    linkToCreateReviewPage: element(by.translateKey('global.menu.createReview')),
    logoutButton: element(by.translateKey('global.menu.account.logout'))



  };

  Object.defineProperties(this, {
    startDate: {
      get: function () {
        return this.ui.startDateInput.getAttribute('value');
      },
      set: function (startDate) {
        this.ui.startDateInput.clear();
        this.ui.startDateInput.sendKeys(startDate);
      }
    },
    endDate: {
      get: function () {
        return this.ui.endDateInput.getAttribute('value');
      },
      set: function (endDate) {
        this.ui.endDateInput.clear();
        this.ui.endDateInput.sendKeys(endDate);
      }
    },

  });

  this.get = function () {
    browser.get('/#/review/new');
  };

  this.save = function () {
    this.ui.saveButton.click();
  };

  this.cancel = function(){
    this.ui.modalCancelButton.click();

  };

  this.accept = function(){
    this.ui.modalAcceptButton.click();

  };

   this.logout = function(){
    this.ui.logoutButton.click();

  };

  this.getDropdownOptions = function (dropdownSelect){
    element(by.model(dropdownSelect)).click();
    var optionsContainer = element(by.css('.md-select-menu-container.md-active.md-clickable')).element(by.tagName('md-select-menu')).element(by.tagName('md-content'));
    var options = optionsContainer.all(by.css('.md-ink-ripple'));
    return options;

  };

  this.selectDropdownOption = function (dropdownSelect, value){
    var optionsContainer = element(by.css('.md-select-menu-container.md-active.md-clickable'));
    var desiredOption = optionsContainer.element(by.cssContainingText('.md-text', value));
    desiredOption.click();
    var selectedValue = element(by.model(dropdownSelect)).element(by.cssContainingText('.md-select-value', value));
    return selectedValue;
  };


  this.verifyDisplayText = function (elementClass, expectedValue){
    var modalContainer = element(by.tagName('md-dialog'));
    var textContainer = modalContainer.element(by.css('.md-dialog-content'));
    var text = textContainer.element(by.cssContainingText(elementClass, expectedValue));
    return text;
  };

};

module.exports = CreateReviewPage;
