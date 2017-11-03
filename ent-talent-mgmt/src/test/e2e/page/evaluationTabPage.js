/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals  browser: false, element: false, by: false, module:false */

var EvaluationTabPage = function() {
  'use strict';
  this.ui = {

    // evaluation tab elements
    evaluationTabBtn: element(by.css('md-tab-item:nth-child(4)')),
    evaluationTabContent: element(by.css('md-tab-content:nth-child(4)')),


    // evaluation form elements - reviewee
    evaluationForm: element(by.name('evalForm')),
    revieweeRatingSwitch: elementByModelAndChildModel('question.ratings.reviewee','na'),
    revieweeRatingSlider: elementByModelAndChildModel('question.ratings.reviewee', 'rating.score'),// .element(by.css('div.md-thumb')),
    revieweeRatingValue: elementByModelAndCSS('question.ratings.reviewee','[role="score"]'),

    // evaluation form elements - reviewer
    reviewerRatingSwitch: elementByModelAndChildModel('question.ratings.reviewer','na'),
    reviewerRatingSlider: elementByModelAndChildModel('question.ratings.reviewer','rating.score'),// .element(by.css('div.md-thumb')),
    reviewerRatingValue: elementByModelAndCSS('question.ratings.reviewer','[role="score"]'),

    reviewCommentWarning: element(by.translateKey('review.annual.evaluation.comment')),

    reviewerComment: elementByModelAndChildModel('question.ratings.reviewer','rating.comment'),
    // evaluation form elements - close button
    closeBtn: element.all(by.css('[ng-click="close()"]')).first(),

  };

  function elementByModelAndChildModel(parentModel, childModel){
    return element(by.model(parentModel)).element(by.model(childModel));
  }

  function elementByModelAndCSS(parentModel, childcss){
    return element(by.model(parentModel)).element(by.css(childcss));

  }

  Object.defineProperties(this, {
    evaluationTabBtn: {
      get: function() {
        return this.ui.evaluationTabBtn.getAttribute('value');
      }
    },

    evaluationTabContent: {
      get: function() {
        return this.ui.evaluationTabContent.getAttribute('value');
      }
    },

  });

  this.get = function(id) {
    browser.get('/#/review/' + id + '/edit');
    this.ui.evaluationTabBtn.click();
  };

  this.getToggleText = function(toggleNum) {

    var toggleButton = element(by.css('md-tab-content:nth-child(4)')).element(by.css('md-list > div:nth-child(' + toggleNum + ')')).element(by.tagName('md-list-item')).element(by.tagName('button'));

    var toggleText = toggleButton.getText();

    return toggleText;

  };

  this.clickToggle = function(toggleName) {

    var toggleBtn = element(by.css('md-tab-content:nth-child(4)')).element(by.tagName('md-list')).element(by.cssContainingText('button', toggleName));

    toggleBtn.click();
  };

  this.getCategoryElement = function(toggleName) {

    return element.all(by.repeater("question in questions | orderBy:'position'")).filter(function(result) {
      return result.getText().then(function(text) {
          return text.includes(toggleName);
        });
    });
  };

  this.getToggleQuestionnaire = function(toggleName) {
  	 element.all(by.repeater("category in keys(categories) | orderBy:'toString()'")).first().click();
  	 this.getCategoryElement(toggleName).click();
   // shouldn't use xpath! need a better way to do this
  	 return element(by.name('score'));
  };


  this.getCategoryRating = function(toggleName) {
  	return this.getCategoryElement(toggleName).first().getText();
  };


  this.fillReviewComment = function(comment) {
    element.all(by.model("rating.comment")).filter(function(result) {
      return result.isEnabled();
    }).sendKeys(comment);
  };


  this.clickSave = function(){
  	element(by.tagName('md-dialog-actions')).element(by.tagName('button')).element(by.className('md-primary'));
  };


  this.clickQuestion = function(questionContainer) {
    var questionBtn = questionContainer.element(by.tagName('button'));
    return questionBtn.click();
  };

  this.slideRating = function(slider, ratingNum) {

    var i = -400;
    switch (ratingNum) {

      case 1:
        {
          return browser.actions().dragAndDrop(
            slider, {
              x: i,
              y: 0
            }
          ).perform();
        }
        break;

      case 2:
        {
          return browser.actions().dragAndDrop(
            slider, {
              x: i + (200),
              y: 0
            }
          ).perform();
        }
        break;

      case 3:
        {
          return browser.actions().dragAndDrop(
            slider, {
              x: i + (200 * 2),
              y: 0
            }
          ).perform();
        }
        break;
      case 4:
        {
          return browser.actions().dragAndDrop(
            slider, {
              x: i + (200 * 3),
              y: 0
            }
          ).perform();
        }
        break;
      case 5:
        {
          return browser.actions().dragAndDrop(
            slider, {
              x: i + (200 * 5),
              y: 0
            }
          ).perform();
        }
        break;
    }
  };

};
module.exports = EvaluationTabPage;
