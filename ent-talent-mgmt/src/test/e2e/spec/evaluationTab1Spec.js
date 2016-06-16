'use strict';

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var EvaluationTabPage = require('../page/evaluationTabPage.js');

describe('Enterprise Talent Management', function () {
  describe('Evaluation Tab Page', function () {
    var evaluationTab;

    beforeAll(function () {
      var loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      evaluationTab = new EvaluationTabPage();
      evaluationTab.get(9);
    });
    
    it('should be displayed when Evaluation tab is clicked', function () {
      expect(evaluationTab.ui.evaluationTabBtn.getText()).toBe('EVALUATION');
      expect(evaluationTab.ui.evaluationTabBtn.getAttribute('class')).toContain('md-ink-ripple md-active');
      expect(evaluationTab.ui.evaluationTabContent.getAttribute('class')).toContain('md-no-scroll md-active');
    });
    
    
    it('- should allow to rate on Consulting Skills category', function(){    
           
      
        var techAbilitiesquestions = evaluationTab.getToggleQuestionnaire('Consulting Skills');
           
        techAbilitiesquestions.then(function(resultArray){
          
           for(var i=0; i<=resultArray.length; i++){
          
            evaluationTab.clickQuestion(resultArray[o]);
                      
           // expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            
           // evaluationTab.ui.closeBtn.click();
             
          };
          
        });
       
     
    });
  
  });
});
