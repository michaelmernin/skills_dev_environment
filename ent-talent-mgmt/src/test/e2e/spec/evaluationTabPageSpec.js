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
    
 it('- should allow to rate on Technical Abilities category', function(){    
      
      var categoryName = "Consulting Skills";
      var techAbilitiesquestions = evaluationTab.getToggleQuestionnaire(categoryName);
             
      techAbilitiesquestions.then(function (resultArray ) {
        
        for(var i=0; i<resultArray.length; i++ ){
          
            evaluationTab.clickQuestion(resultArray[i]);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            
           // testing default values of the form    
           /* expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();
            */

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment'+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment'+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();
            expect(i).toBe(1);
        };
        
      });
            
      /*evaluationTab.clickToggle('Technical Abilities'); 
      expect(techAbilitiesquestions.count()).toBe(0);*/
     
    });
  /*  
   it('- should allow to rate on Consulting Skills category', function(){    
     
     var consultingSkillsQuestions = evaluationTab.getToggleQuestionnaire('Consulting Skills');
           
      consultingSkillsQuestions.then(function (resultArray ) {
        
        
        for(var a=1; a<=resultArray.length; a++ ){
       
           evaluationTab.clickQuestion(2, a);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment'+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment'+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();

        };
          
      }); 
      
     
    });
    
    it('- should allow to rate on Professionalism category', function(){    
     
      var professionbalismQuestions = evaluationTab.getToggleQuestionnaire('Professionalism');
           
      professionbalismQuestions.then(function (resultArray ) {
        
        
        for(var b=1; b<=resultArray.length; b++ ){
       
           evaluationTab.clickQuestion(3, b);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment '+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment '+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();

        };
          
      }); 
     
    });
    
    it('- should allow to rate on Leadership category', function(){    
     
      var leadershipQuestions = evaluationTab.getToggleQuestionnaire('Leadership');
           
      leadershipQuestions.then(function (resultArray ) {
        
        
        for(var c=1; c<=resultArray.length; c++ ){
       
           evaluationTab.clickQuestion(4, c);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment '+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment '+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();

        };
          
      }); 
     
    });
     
   it('- should allow to rate on Teamwork category', function(){    
     
      var teamworkQuestions = evaluationTab.getToggleQuestionnaire('Teamwork');
           
      teamworkQuestions.then(function (resultArray ) {
        
        
        for(var d=1; d<=resultArray.length; d++ ){
       
           evaluationTab.clickQuestion(5, d);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment '+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment '+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();

        };
          
      }); 
     
    });
  
     it('- should allow to rate on Core Competencies category', function(){    
     
      var coreCompQuestions = evaluationTab.getToggleQuestionnaire('Core Competencies');
       
      coreCompQuestions.then(function (resultArray ) {
        
        
        for(var e=1; e<=resultArray.length; e++ ){
       
           evaluationTab.clickQuestion(6, e);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment '+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment '+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();

        };
          
      }); 
     
    });
    
       it('- should allow to rate on Internal Contributions category', function(){    
     
      var internalContriQuestions = evaluationTab.getToggleQuestionnaire('Internal Contributions');
      
      internalContriQuestions.then(function (resultArray ) {
        
        
        for(var f=1; f<=resultArray.length; f++ ){
       
           evaluationTab.clickQuestion(7, f);
           // verify the default values for switch, slider, score
            expect(evaluationTab.ui.evaluationForm.isPresent()).toBe(true);
            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe(null);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('');

            evaluationTab.ui.reviewerRatingSwitch.click();

            expect(evaluationTab.ui.reviewerRatingSwitch.getAttribute('class')).toContain('md-checked');
            expect(evaluationTab.ui.reviewerRatingSlider.getAttribute('disabled')).toBe('true');

            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe('N/A');

            evaluationTab.ui.reviewerRatingSwitch.click();

            var randVal = Math.floor(Math.random() * 5) + 1 ;
            // provide rating for the category
            evaluationTab.slideRating(evaluationTab.ui.reviewerRatingSlider, randVal);
            expect(evaluationTab.ui.reviewerRatingValue.getText()).toBe(randVal.toString());

            //testing entering reviewer comments
            evaluationTab.ui.reviewerComment.click();
            evaluationTab.ui.reviewerComment.clear();
            evaluationTab.ui.reviewerComment.sendKeys('Test Comment '+randVal);
            expect(evaluationTab.ui.reviewerComment.getAttribute('value')).toBe('Test Comment '+randVal);

            //click Close button to close the modal window form
            evaluationTab.ui.closeBtn.click();

        };
          
      }); 
     
    });
  */
  });
});
