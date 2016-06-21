/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals element: false, by: false, browser:false, module: false*/


var AnnualReviewPage = function () {
  'use strict';
  
  
  this.getCard = function(){
    return element(by.tagName('md-card-content'));
  };
  this.ui = {
    reviewName: element(by.css('[role="header"]')),
    reviewInformation: element(by.translateKey('review.annual.reviewInformation')),
    reviewTypeLabel: element(by.translateKey('review.annual.reviewType')),
    reviewTypeValue: this.getCard().element(by.binding('review.reviewType.name')),
    startDateLabel: this.getCard().element(by.translateKey('review.annual.startDate')),
    startDateValue: this.getCard().element(by.binding('review.startDate')),
    endDateLabel: this.getCard().element(by.translateKey('review.annual.endDate')),
    endDateValue: this.getCard().element(by.binding('review.endDate')),
    statusLabel: this.getCard().element(by.translateKey('review.annual.status')),
    statusValue: this.getCard().element(by.binding('review.reviewStatus.name')),
    counselorLabel: this.getCard().element(by.translateKey('review.annual.counselor')),
    counselorValue: this.getCard().element(by.binding('review.reviewee.counselor.firstName')),
    peerTabContainer: element(by.css('md-tab-item:nth-child(1)')),
    engagementTabContainer: element(by.css('md-tab-item:nth-child(2)')),
    goalsTabContainer: element(by.css('md-tab-item:nth-child(3)')),
    evaluationTabContainer: element(by.css('md-tab-item:nth-child(4)')),
    overallTabContainer: element(by.css('md-tab-item:nth-child(5)')),
  };

  Object.defineProperties(this, {
    reviewName: {
      get: function () {
        return this.ui.reviewName.getAttribute('value');
      }
    },
    
    reviewInformation: {
      get: function () {
        return this.ui.reviewInformation.getAttribute('value');
      },
    },
    
    reviewTypeLabel: {
      get: function () {
        return this.ui.reviewTypeLabel.getAttribute('value');
      },
    },
      
    reviewTypeValue: {
      get: function (){
        return this.ui.reviewTypeValue.getAttribute('value');
      }
    },
    
    startDateLabel: {
      get: function (){
        return this.ui.startDateLabel.getAttribute('value');
      }
    },
    
    startDateValue: {
      get: function (){
        return this.ui.startDateValue.getAttribute('value');
      }
    },
      
    endDateLabel: {
      get: function () {
        return this.ui.endDateLabel.getAttribute('value');
      }
    },
    
    endDateValue: {
      get: function () {
        return this.ui.endDateValue.getAttribute('value');
      },
    },
    statusLabel: {
      get: function () {
        return this.ui.statusLabel.getAttribute('value');
      },
    },
    
    statusValue: {
      get: function (){
        return this.ui.statusValue.getAttribute('value');
      }
    },
    
    counselorLabel: {
      get: function (){
        return this.ui.counselorLabel.getAttribute('value');
      }
    },
    
    counselorValue: {
      get: function (){
        return this.ui.counselorValue.getAttribute('value');
      }
    },
      
  });

  this.get = function (id) {
    browser.get('/#/review/' + id + '/edit');
  };

};
module.exports = AnnualReviewPage;
