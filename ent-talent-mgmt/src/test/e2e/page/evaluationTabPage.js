/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals require: false,  browser: false, element: false, by: false, module:false */

require('../locators.js');

var EvaluationTabPage = function() {
  'use strict';
  this.ui = {

    //evaluation tab elements
    evaluationTabBtn: element(by.css('md-tab-item:nth-child(4)')),
    evaluationTabContent: element(by.css('md-tab-content:nth-child(4)')),


    //evaluation form elements - reviewee
    evaluationForm: element(by.name('evalForm')),
    revieweeRatingSwitch: element(by.model('question.ratings.reviewee')).element(by.name('form.ratingForm')).element(by.css('md-input-container:nth-child(1)')).element(by.tagName('md-switch')),
    revieweeRatingSlider: element(by.model('question.ratings.reviewee')).element(by.name('form.ratingForm')).element(by.css('md-input-container:nth-child(1)')).element(by.model('rating.score')),
    revieweeRatingValue: element(by.model('question.ratings.reviewee')).element(by.name('form.ratingForm')).element(by.css('md-input-container:nth-child(1)')).element(by.css('md-headline')),

    //evaluation form elements - reviewer
    reviewerRatingSwitch: element(by.model('question.ratings.reviewer')).element(by.name('form.ratingForm')).element(by.css('md-input-container:nth-child(1)')).element(by.tagName('md-switch')),

    reviewerRatingSlider: element(by.model('question.ratings.reviewer')).element(by.name('form.ratingForm')).element(by.css('md-input-container:nth-child(1)')).element(by.model('rating.score')),

    reviewerRatingValue: element(by.xpath('/html/body/div[3]/md-dialog/form/md-dialog-content/div[1]/etm-rating/ng-form/md-input-container[1]/div[3]/span')),

    reviewerComment: element(by.model('question.ratings.reviewer')).element(by.name('form.ratingForm')).element(by.css('md-input-container:nth-child(2)')).element(by.model('rating.comment')),

    //evaluation form elements - close button
    closeBtn: element.all(by.css('[ng-click="close()"]')),

  };

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

  this.getToggleQuestionnaire = function(toggleName) {
    element.all(by.repeater("category in keys(categories) | orderBy:'toString()'")).filter(function(result) {
      return result.getText().then(function(text) {
          return text.includes(toggleName);
        });
    }).first().click();
    return element.all(by.repeater('question in questions'));

  };

  this.clickQuestion = function(questionContainer) {
    var questionBtn = questionContainer.element(by.tagName('button'));
    questionBtn.click();
  };
  this.slideRating = function(slider, ratingNum) {

    var i = -400;
    switch (ratingNum) {

      case 1:
        {
          browser.actions().dragAndDrop(
            slider, {
              x: i,
              y: 0
            }
          ).perform();
        }
        break;

      case 2:
        {
          browser.actions().dragAndDrop(
            slider, {
              x: i + (200),
              y: 0
            }
          ).perform();
        }
        break;

      case 3:
        {
          browser.actions().dragAndDrop(
            slider, {
              x: i + (200 * 2),
              y: 0
            }
          ).perform();
        }
        break;
      case 4:
        {
          browser.actions().dragAndDrop(
            slider, {
              x: i + (200 * 3),
              y: 0
            }
          ).perform();
        }
        break;
      case 5:
        {
          browser.actions().dragAndDrop(
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
