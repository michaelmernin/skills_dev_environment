/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require: false, describe: false, beforeAll: false, it: false, expect: false, protractor: fasle*/

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var EvaluationTabPage = require('../page/evaluationTabPage.js');

describe('Enterprise Talent Management', function () {
  'use strict';
  describe('Evaluation Tab Page', function () {
    var evaluationTab;
    var EC;

    beforeAll(function () {
      var loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      evaluationTab = new EvaluationTabPage();
      evaluationTab.get(1);
      EC = protractor.ExpectedConditions;
    });
    
    it('should be displayed when Evaluation tab is clicked', function () {
      expect(evaluationTab.ui.evaluationTabBtn.getText()).toBe('EVALUATION');
      expect(evaluationTab.ui.evaluationTabBtn.getAttribute('class')).toContain('md-ink-ripple md-active');
      expect(evaluationTab.ui.evaluationTabContent.getAttribute('class')).toContain('md-no-scroll md-active');
    });
    
    //uncomment this once CDEV-456 is resolved
   /*
    it('- should allow to rate on Consulting Skills category', function(){
        evaluationTab.getToggleQuestionnaire('Consulting Skills')
          .then(function(questions){
            questions.forEach(function(question){
              // for loops need to be in a clousure to ensure proper execution
              (function(q){
                // wait for material animations to finish
                browser.sleep(400);
                q.click().then(function(){
                  expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
                  evaluationTab.ui.closeBtn.click();
                });
              })(question);
            });
          });
      });
    */
    });
});
