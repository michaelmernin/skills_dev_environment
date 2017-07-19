/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require: false, describe: false, beforeAll: false, it: false, expect: false, browser: false */

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var AnnualReviewPage = require('../page/annualReviewPage.js');

describe('Enterprise Talent Management', function () {
  'use strict';

  describe('Annual Review Page', function () {
    var annualReviewPage;
    var loginPage;

    beforeAll(function () {
      loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      annualReviewPage = new AnnualReviewPage();
      annualReviewPage.get(1);
    });

    it('should have a page title', function () {
      expect(browser.getTitle()).toEqual('Edit Review');
    });

    it('should have Review Name', function (){
      expect(annualReviewPage.reviewName.getText()).toBe('2015-2016 Annual Review for Dev UserFour');
    });

    it('should have Review Information section', function (){
      expect(annualReviewPage.reviewInformation.getText()).toBe('Review Information');
    });

    it('should have Review Type information', function (){
      expect(annualReviewPage.reviewTypeLabel.getText()).toBe('Review Type:');
      expect(annualReviewPage.reviewTypeValue.getText()).toBe('Annual Review');
    });

    it('should have Start Date information', function (){
      expect(annualReviewPage.startDateLabel.getText()).toBe('Start Date:');
      expect(annualReviewPage.startDateValue.getText()).toBe('04/10/2015');
    });

    it('should have End Date information', function (){
      expect(annualReviewPage.endDateLabel.getText()).toBe('End Date:');
      expect(annualReviewPage.endDateValue.getText()).toBe('04/10/2016');
    });

    it('should have Status information', function (){
      expect(annualReviewPage.statusLabel.getText()).toBe('Status:');
      expect(annualReviewPage.statusValue.getText()).toBe('Open');
    });

    // TODO: failing test, were these fields removed on purpose?
    xit('should have Counselor information', function (){
      expect(annualReviewPage.counselorLabel.getText()).toBe('Counselor:');
      expect(annualReviewPage.counselorValue.getText()).toBe('Dev UserThree');
    });

    it('should have Peers tab that is active when clicked', function (){
      expect(annualReviewPage.ui.peerTabContainer.getText()).toBe('PEERS');
      expect(annualReviewPage.ui.peerTabContainer.getAttribute('class')).toBe('md-tab ng-scope ng-isolate-scope md-ink-ripple md-active');
    });

    it('should have Engagements tab that is active when clicked', function (){
      expect(annualReviewPage.ui.engagementTabContainer.getText()).toBe('ENGAGEMENTS');
      expect(annualReviewPage.ui.engagementTabContainer.getAttribute('class')).toBe('md-tab ng-scope ng-isolate-scope md-ink-ripple');
      annualReviewPage.ui.engagementTabContainer.click();
      expect(annualReviewPage.ui.engagementTabContainer.getAttribute('class')).toContain('md-active');
    });

    it('should have Goals tab that is active when clicked', function (){
      expect(annualReviewPage.ui.goalsTabContainer.getText()).toBe('GOALS');
      expect(annualReviewPage.ui.goalsTabContainer.getAttribute('class')).toBe('md-tab ng-scope ng-isolate-scope md-ink-ripple');
      annualReviewPage.ui.goalsTabContainer.click();
      expect(annualReviewPage.ui.goalsTabContainer.getAttribute('class')).toContain('md-active');
    });

    it('should have Evaluation tab that is active when clicked', function (){
      expect(annualReviewPage.ui.evaluationTabContainer.getText()).toBe('EVALUATION');
      expect(annualReviewPage.ui.evaluationTabContainer.getAttribute('class')).toBe('md-tab ng-scope ng-isolate-scope md-ink-ripple');
      annualReviewPage.ui.evaluationTabContainer.click();
      expect(annualReviewPage.ui.evaluationTabContainer.getAttribute('class')).toContain('md-active');
    });

    it('should have Overall tab that is active when clicked', function (){
      expect(annualReviewPage.ui.overallTabContainer.getText()).toBe('OVERALL');
      expect(annualReviewPage.ui.overallTabContainer.getAttribute('class')).toBe('md-tab ng-scope ng-isolate-scope md-ink-ripple');
      annualReviewPage.ui.overallTabContainer.click();
      expect(annualReviewPage.ui.overallTabContainer.getAttribute('class')).toContain('md-active');
    });

    afterAll(function() {
      loginPage.get();
      loginPage.logout();
    });

  });
});
