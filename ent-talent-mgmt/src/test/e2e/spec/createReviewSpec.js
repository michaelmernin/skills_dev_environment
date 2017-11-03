/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require: false, describe: false, beforeAll: false, it: false, expect: false, browser: false, Modernizr:false*/

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var CreateReviewPage = require('../page/createReviewPage.js');

describe('Enterprise Talent Management', function () {
  'use strict';

  describe('Create a Review Page ', function () {
    var createReviewPage;
    var loginPage;
    var cannotCreateReview = false;
    createReviewPage = new CreateReviewPage();

    beforeAll(function () {
      loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      createReviewPage.get();
    });

    it('should have a page title.', function () {
      expect(browser.getTitle()).toEqual('Create a Review');
    });

    it('should require all fields to create a review.', function () {
      createReviewPage.save()
      .then( function(){
        expect(createReviewPage.ui.startDateError.getText()).toEqual('Please enter a value.');
      })
    });

    it('should be Engagement Review in ReviewType text.', function () {
      var reviewType = element(by.name('reviewType'));
      expect(reviewType.getAttribute('value')).toBe('Engagement Review');
    });

    it('should search all applicable reviewee for the logged in user.', function () {
      var listedUsers = createReviewPage.getRevieweeOptions('Dev ');
      // expect(listedUsers.count()).toBe(6);
      expect(listedUsers.get(0).getText()).toBe('Dev UserOne');
      expect(listedUsers.get(1).getText()).toBe('Dev UserTwo');
      expect(listedUsers.get(2).getText()).toBe('Dev UserFour');
      expect(listedUsers.get(3).getText()).toBe('Dev UserSeven');
      // expect(listedUsers.get(4).getText()).toBe('Dev UserEight');
      // expect(listedUsers.get(5).getText()).toBe('Dev UserNine');
    });

    it('should select a desired reviewee in the dropdown.', function (){
      var selectedReviewee = 'Dev UserOne';
      createReviewPage.selectReviewee(selectedReviewee);
      var selectedValue = createReviewPage.getSelectedReviewee();
      expect(selectedValue.getAttribute('value')).toBe(selectedReviewee);
    });

    xit('should open a modal window when all required values are provided.', function () {
      // createReviewPage.getDropdownOptions('review.reviewType');
      // createReviewPage.selectDropdownOption('review.reviewType', 'Engagement Review');
      // createReviewPage.getDropdownOptions('review.reviewee');
      // createReviewPage.selectDropdownOption('review.reviewee', 'Dev UserThree');
      createReviewPage.save();
      expect(createReviewPage.ui.modalWindowContainer.isPresent()).toBe(true);
    });

    xit('should contain desired text in modal window.', function () {
      var errorText = createReviewPage.verifyDisplayText('.md-title', 'Cannot create annual review');
      cannotCreateReview = errorText.isDisplayed();

      // if annual review(s) are already created, handle it instead of failing tests
      if (!cannotCreateReview) {
        var titleText = createReviewPage.verifyDisplayText('.md-title', 'Create annual review for this year?');
        expect(titleText.isDisplayed()).toBe(true);
        var descriptionText = createReviewPage.verifyDisplayText('.md-dialog-content-body', 'Once you have initiated a review, it cannot be deleted. Are you sure you want to continue?');
        expect(descriptionText.isDisplayed()).toBe(true);
      } else {
        expect(errorText.isDisplayed()).toBe(true);
        var errorDescriptionText = createReviewPage.verifyDisplayText('.md-dialog-content-body', 'You already have an annual review created for this year and next year');
        expect(errorDescriptionText.isDisplayed()).toBe(true);
      }
    });

    // if annual review(s) are already created, handle it instead of failing tests
    if (cannotCreateReview) {
      it('should close the modal window when Cancel button is clicked.', function () {
        expect(createReviewPage.ui.modalCancelButton.getText()).toBe('CANCEL');
        createReviewPage.cancel();
        expect(createReviewPage.ui.modalWindowContainer.isPresent()).toBe(false);
      });

      it('should create the Review and close the modal window when Accept button is clicked', function () {
        createReviewPage.save();
        expect(createReviewPage.ui.modalWindowContainer.isPresent()).toBe(true);
        createReviewPage.accept();
        expect(browser.getTitle()).toEqual('Edit Review');
        createReviewPage.logout();
      });
    } else {
      xit('should close the modal, since a Review cannot be created.', function () {
        expect(createReviewPage.ui.modalAcceptButton.getText()).toBe('OKAY');
        createReviewPage.accept();
        expect(createReviewPage.ui.modalWindowContainer.isPresent()).toBe(false);
      });
    }

    afterAll(function() {
      loginPage.get();
      loginPage.logout();
    });

  });
});
