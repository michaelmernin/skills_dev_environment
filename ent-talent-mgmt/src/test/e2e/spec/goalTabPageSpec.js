'use strict';

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var GoalTabPage = require('../page/goalTabPage.js');

describe('Enterprise Talent Management', function () {
  describe('Goal Tab', function () {
    var goalTabPage;

    beforeAll(function () {
      var loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      goalTabPage = new GoalTabPage();
      goalTabPage.get(9);
    });

    it('should be displayed when Goal tab is clicked', function () {
      expect(goalTabPage.ui.goalTabBtn.getText()).toBe('GOALS');
      expect(goalTabPage.ui.goalTabBtn.getAttribute('class')).toContain('md-ink-ripple md-active');
      expect(goalTabPage.ui.goalTabContent.getAttribute('class')).toContain('md-no-scroll md-active');
    });
    
    it('should allow users to submit goal details', function () {
      
      goalTabPage.addGoal('Learn Protractor', 'I want to learn protractor framework to learn automation testing.', '2014-11-12', 'Learning it while writing test cases for ETM project');
      
    });
    
     it('should allow users to update goal details', function () {
      
      goalTabPage.updateGoal('2', 'Updated Goal - Learn Protractor', 'Updated - I want to learn protractor framework to learn automation testing.');
      
       
    });
    
    it('should allow users to delete goal', function () {
      
      goalTabPage.deleteGoal('2');
  
      
       
    });
    
    it('should require Goal name to be entered', function(){
      
      goalTabPage.ui.addGoalBtn.click();
      goalTabPage.saveForm();
      
      expect(goalTabPage.ui.goalRequiredMessage.getText()).toBe('Please enter a Goal.');
      goalTabPage.cancelForm();
    
      
    });
    
    
    

  });
});
