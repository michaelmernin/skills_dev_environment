'use strict';

require('../locators.js');

var AnnualReviewPage = function () {
  this.ui = {
    
    reviewName: element(by.css('.md-display-1')),
    reviewInformation: element(by.translateKey('review.annual.reviewInformation')),
    reviewTypeLabel: element(by.translateKey('review.annual.reviewType')),
    reviewTypeValue: element(by.css('.layout-gt-xs-row:nth-child(1)')).element(by.css('.layout-row.flex-gt-xs:nth-child(1)')).element(by.tagName('span')),
    startDateLabel: element(by.translateKey('review.annual.startDate')),
    startDateValue: element(by.css('.layout-gt-xs-row:nth-child(1)')).element(by.css('.layout-row.flex-gt-xs:nth-child(2)')).element(by.tagName('span')),
    endDateLabel: element(by.translateKey('review.annual.endDate')),
    endDateValue: element(by.css('.layout-gt-xs-row:nth-child(2)')).element(by.css('.layout-row.flex-order-gt-xs-2.flex-gt-xs')).element(by.tagName('span')),
    statusLabel: element(by.translateKey('review.annual.status')),
    statusValue: element(by.css('.layout-gt-xs-row:nth-child(3)')).element(by.css('.layout-row.flex-gt-xs:nth-child(1)')).element(by.tagName('span')),
    counselorLabel: element(by.translateKey('review.annual.counselor')),
    counselorValue: element(by.css('.layout-gt-xs-row:nth-child(2)')).element(by.css('.layout-row.flex-order-gt-xs-1.flex-gt-xs')).element(by.tagName('span')),
    
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
      get: function(){
        return this.ui.reviewTypeValue.getAttribute('value');
      }
    },
    
    startDateLabel: {
      get: function(){
        return this.ui.startDateLabel.getAttribute('value');
      }
    },
    
    startDateValue: {
      get: function(){
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
      get: function(){
        return this.ui.statusValue.getAttribute('value');
      }
    },
    
    counselorLabel: {
      get: function(){
        return this.ui.counselorLabel.getAttribute('value');
      }
    },
    
    counselorValue: {
      get: function(){
        return this.ui.counselorValue.getAttribute('value');
      }
    },
      
  });

  this.get = function (id) {
    browser.get('/#/review/' + id + '/edit');
  };

};
module.exports = AnnualReviewPage;
