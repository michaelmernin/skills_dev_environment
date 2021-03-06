/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require:false, describe:false, beforeAll:false, it:false, expect:false, element:false, by:false*/

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var GoalTabPage = require('../page/goalTabPage.js');

describe('Enterprise Talent Management', function () {
  'use strict';

  describe('Goal Tab', function () {
    var goalTabPage;
    var loginPage;

    beforeAll(function () {
      loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.colleague);
      goalTabPage = new GoalTabPage();
      goalTabPage.get(1);
    });

    it('should be displayed when Goal tab is clicked', function () {
      expect(goalTabPage.ui.goalTabBtn.getText()).toBe('GOALS');
      expect(goalTabPage.ui.goalTabBtn.getAttribute('class')).toContain('md-ink-ripple md-active');
      expect(goalTabPage.ui.goalTabContent.getAttribute('class')).toContain('md-no-scroll md-active');
    });

    it(' - Goal Form should require Goal name to be entered', function(){
      goalTabPage.ui.addGoalBtn.click();
      goalTabPage.saveForm();

      expect(goalTabPage.ui.goalRequiredMessage.getText()).toBe('Please enter a Goal.');
      goalTabPage.cancelForm();
    });


    it(' - Goal Form  should provide character count for Goal name, Goal Description, and Status Note fields', function(){
      goalTabPage.ui.addGoalBtn.click();
      var charCount = '20 / 250';

      goalTabPage.goalTextArea = '12345678901234567890';
      expect(goalTabPage.ui.goalCharCounter.getText()).toBe(charCount);

      goalTabPage.descriptionTextArea = '12345678901234567890';
      expect(goalTabPage.ui.descriptionCharCounter.getText()).toBe(charCount);

      goalTabPage.statusTextArea = '12345678901234567890';
      expect(goalTabPage.ui.statusCharCounter.getText()).toBe(charCount);
      goalTabPage.saveForm();
    });

    xit(' - Goal Form should allow user to mark a goal complete', function(){
      goalTabPage.ui.addGoalBtn.click();
      goalTabPage.ui.completeInputButton.click();
      expect(goalTabPage.ui.completeInputButton.getAttribute('class')).toContain('md-checked');
      goalTabPage.completionDateInput = '2016-05-12';
      goalTabPage.cancelForm();
    });

    xit('should allow users to submit goal details', function () {
      goalTabPage.addGoal('Learn Protractor', 'I want to learn protractor framework to learn automation testing.', '2014-11-12', 'Learning it while writing test cases for ETM Project');
      goalTabPage.deleteGoal(1);
      goalTabPage.addGoal('Automation testing Goal', 'This is for description of the goal', '2015-10-15', 'Status is not a required field');
    });

    xit('should mark a completed goal differently', function(){
      goalTabPage.markGoalComplete('1', '2016-05-12');
      var completionCircle = element(by.css('md-tab-content:nth-child(3)')).element(by.tagName('md-list')).element(by.tagName('md-list-item')).element(by.tagName('md-icon'));
      expect(completionCircle.getAttribute('class')).toContain('fa-circle-o');
    });

    it('should allow users to delete goal', function () {
      goalTabPage.deleteGoal(0);
      expect()
    });

    afterAll(function() {
      loginPage.get();
      loginPage.logout();
    });

  });
});
