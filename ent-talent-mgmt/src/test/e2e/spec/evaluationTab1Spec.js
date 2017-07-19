/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require: false, describe: false, beforeAll: false, it: false, expect: false, protractor: fasle, Promise: false*/

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var EvaluationTabPage = require('../page/evaluationTabPage.js');

describe('Enterprise Talent Management', function() {
  'use strict';

  describe('Evaluation Tab Page', function() {
    var evaluationTab;
    var EC;
    var loginPage;

    beforeAll(function() {
      loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      evaluationTab = new EvaluationTabPage();
      evaluationTab.get(2);
      EC = protractor.ExpectedConditions;
    });

    it('should be displayed when Evaluation tab is clicked', function() {
      expect(evaluationTab.ui.evaluationTabBtn.getText()).toBe('EVALUATION');
      expect(evaluationTab.ui.evaluationTabBtn.getAttribute('class')).toContain('md-ink-ripple md-active');
      expect(evaluationTab.ui.evaluationTabContent.getAttribute('class')).toContain('md-no-scroll md-active');
    });

    it('should allow to rate on Consulting Skills category', function() {
    	var slider = evaluationTab.getToggleQuestionnaire('Consulting Skills');
    	evaluationTab.slideRating(slider, 4);
    	evaluationTab.clickSave();
    	expect(evaluationTab.ui.reviewCommentWarning.getText()).toBe('A comment is required for any rating other than 3');
    	evaluationTab.clickSave();
    	evaluationTab.fillReviewComment("This is a test comment");
    	evaluationTab.clickSave();
    	expect(evaluationTab.getCategoryRating('Consulting Skills')).toContain('Reviewer Rating: 4');
    });

    afterAll(function() {
      loginPage.get();
      loginPage.logout();
    });

  });
});
