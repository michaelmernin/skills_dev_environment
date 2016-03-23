'use strict';

require('../locators.js');

var GoalTabPage = function () {
  this.ui = {

    goalTabBtn: element(by.css('md-tab-item:nth-child(3)')),
    goalTabContent: element(by.css('md-tab-content:nth-child(3)')),
    addGoalBtn: element(by.translateKey('review.annual.goals.addGoal')),

  };

    Object.defineProperties(this, {

    goalTabBtn: {
    get: function(){
    return this.ui.goalTabBtn.getAttribute('value');
  }
}
      


});

    this.get = function (id) {
    browser.get('/#/review/' + id + '/edit');
    this.ui.goalTabBtn.click();
  };

};
module.exports = GoalTabPage;
