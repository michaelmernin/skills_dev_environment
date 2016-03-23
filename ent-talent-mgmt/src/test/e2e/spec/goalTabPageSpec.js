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
      expect(goalTabPage.ui.goalTabBtn.getAttribute('class')).contains('md-tab md-ink-ripple md-active');
      expect(goalTabPage.ui.goalTabContent.getAttribute('class')).contains('md-no-scroll md-active');
    });

  });
});
