/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require: false, describe: false, beforeAll: false, it: false, expect: false, browser: false, Modernizr:false*/

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var CreateReviewPage = require('../page/createReviewPage.js');

describe('Enterprise Talent Management', function () {
  'use strict';
  describe('Create a Review Page ', function () {
    var createReviewPage;
    createReviewPage = new CreateReviewPage();


    beforeAll(function () {
      var loginPage = new LoginPage();
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
        expect(createReviewPage.ui.reviewTypeError.getText()).toEqual('Please select a Review Type.');
      })
    });

    it('should list all review types in dropdown.', function () {
      //ReviewTypes should have options Annual review, 3 month Review and Engagement review. For the purpose of this tesing in short term, we are expeting only Annual Review to be available.
      // Need to update this test case once all reviews are listed.
      var reviewTypes = createReviewPage.getDropdownOptions('review.reviewType');
      
      expect(reviewTypes.count()).toBe(2);
      expect(reviewTypes.get(0).getText()).toBe('Annual Review');
    });

    it('should select Annual Review in ReviewType dropdown.', function () {
      var selectedValue = createReviewPage.selectDropdownOption('review.reviewType', 'Annual Review');
      expect(selectedValue.isDisplayed()).toBe(true);
    });

    it('should list all applicable reviewee for the logged in user.', function () {
      var listedUsers = createReviewPage.getDropdownOptions('review.reviewee');
      expect(listedUsers.count()).toBe(4);
      expect(listedUsers.get(0).getText()).toBe('Dev UserThree');
      expect(listedUsers.get(1).getText()).toBe('Dev UserOne');
      expect(listedUsers.get(2).getText()).toBe('Dev UserFour');
      expect(listedUsers.get(3).getText()).toBe('Dev UserSeven');
    });

    it('should select a desired reviewee in the dropdown.', function (){
      var selectedValue = createReviewPage.selectDropdownOption('review.reviewee', 'Dev UserOne');
      expect(selectedValue.isDisplayed()).toBe(true);
    });

     it('should open a modal window when all required values are provided.', function () {
      createReviewPage.getDropdownOptions('review.reviewType');
      createReviewPage.selectDropdownOption('review.reviewType', 'Annual Review');
      createReviewPage.getDropdownOptions('review.reviewee');
      createReviewPage.selectDropdownOption('review.reviewee', 'Dev UserThree');
      createReviewPage.save();
      expect(createReviewPage.ui.modalWindowContainer.isPresent()).toBe(true);
    });

    it('should contain desired text in modal window.', function () {
      var titleText = createReviewPage.verifyDisplayText('.md-title', 'Create annual review for this year?');
      expect(titleText.isDisplayed()).toBe(true);
      var descriptionText = createReviewPage.verifyDisplayText('.md-dialog-content-body', 'Once you have initiated a review, it cannot be deleted. Are you sure you want to continue?');
      expect(descriptionText.isDisplayed()).toBe(true);
    });

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

  });
});
