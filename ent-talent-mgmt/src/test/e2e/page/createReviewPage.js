/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals element: false, by: false, browser: false, module: false*/

var CreateReviewPage = function() {
  'use strict';

  this.reviewForm = element(by.tagName('form'));

  this.ui = {
    revieweeInput : this.reviewForm.element(by.name('reviewee')).element(by.css('input')),
    startDateInput : this.reviewForm.element(by.model('review.startDate')),
    endDateInput : this.reviewForm.element(by.model('review.endDate')),
    saveButton : this.reviewForm.element(by.translateKey('entity.action.save')),
    revieweeError : this.reviewForm.element(by
        .css('[ng-messages="reviewForm.reviewee.$error"]')),
    startDateError : this.reviewForm.element(by
        .css('[ng-messages="reviewForm.startDate.$error"]')),
    endDateError : this.reviewForm.element(by
        .css('[ng-messages="reviewForm.endDate.$error"]')),
    modalWindowContainer : element(by.tagName('md-dialog')),
    modalCancelButton : element(by.css('[ng-click="dialog.abort()"]')),
    modalAcceptButton : element(by.css('[ng-click="dialog.hide()"]')),
    linkToCreateReviewPage : element(by
        .translateKey('global.menu.createReview')),
    logoutButton : element(by.translateKey('global.menu.account.logout'))
  };

  Object.defineProperties(this, {
    startDate : {
      get : function() {
        return this.ui.startDateInput.getAttribute('value');
      },
      set : function(startDate) {
        this.ui.startDateInput.clear();
        this.ui.startDateInput.sendKeys(startDate);
      }
    },
    endDate : {
      get : function() {

        return this.ui.endDateInput.getAttribute('value');
      },
      set : function(endDate) {
        this.ui.endDateInput.clear();
        this.ui.endDateInput.sendKeys(endDate);
      }
    },

  });

  this.get = function() {
    browser.get('/#/review/new');
  };

  this.save = function() {
    return this.ui.saveButton.click();
  };

  this.cancel = function() {
    this.ui.modalCancelButton.click();
  };

  this.accept = function() {
    this.ui.modalAcceptButton.click();
  };

  this.logout = function() {
    this.ui.logoutButton.click();
  };

  function getElementByModelAndTagName(model, tagname) {
    return element.all(by.model(model)).filter(function(el) {
      return el.getTagName().then(function(tagName) {
        return tagName === tagname;
      });
    }).first();
  }

  this.verifyDisplayText = function(elementClass, expectedValue) {
    var modalContainer = element(by.tagName('md-dialog'));
    var textContainer = modalContainer.element(by.css('.md-dialog-content'));
    var text = textContainer.element(by.cssContainingText(elementClass,
        expectedValue));
    return text;
  };

  this.fillReviewee = function(searchKey) {
    this.ui.revieweeInput.clear();
    this.ui.revieweeInput.sendKeys(searchKey);
  };

  this.getRevieweeOptions = function(searchKey){
    this.fillReviewee(searchKey);
    var revieweeListContainer = element(by.css('.md-autocomplete-suggestions'));
    var reviewees = [];
    reviewees = revieweeListContainer.all(by.tagName('li'));
    return reviewees;
  };

  this.selectReviewee = function(reviewee){
    var revieweeListContainer = element(by.css('.md-autocomplete-suggestions'));
    var desiredReviewee = revieweeListContainer.element(by.cssContainingText('li', reviewee));
    desiredReviewee.click();
    return desiredReviewee;
  };

  this.getSelectedReviewee = function() {
    return this.ui.revieweeInput;
  }
};

module.exports = CreateReviewPage;
