/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals element:false, by:false, browser, module*/

var GoalTabPage = function() {
  'use strict';
  this.ui = {

    //goal button elements
    goalTabBtn: element(by.css('md-tab-item:nth-child(3)')),
    goalTabContent: element(by.css('md-tab-content:nth-child(3)')),
    addGoalBtn: element(by.translateKey('review.annual.goals.addGoal')),

    // form dialog and header element
    goalForm: element(by.name('goalForm')),
    goalFormHeader: element(by.translateKey('review.annual.goals.goalDetailHeader')),

    // goal input elements
    goalLabel: element(by.translateKey('review.annual.goals.goal')),

    goalTextArea: element(by.model('goal.name')),

    goalRequiredMessage: element(by.tagName('md-dialog-content')).element(by.css('md-input-container')).element(by.translateKey('global.messages.validate.field.enter.required')),

    goalCharCounter: element(by.tagName('md-dialog-content')).element(by.css('md-input-container:nth-child(1)')).element(by.xpath('.//div[@class="md-char-counter"]')),

    goalMaxLengthError: element(by.css('md-input-container:nth-child(1)')).element(by.translateKey('global.messages.validate.field.maxlength')),

    // description input elements
    descriptionLable: element(by.translateKey('review.annual.goals.description')),
    descriptionTextArea: element(by.model('goal.description')),

    descriptionCharCounter: element(by.tagName('md-dialog-content')).element(by.css('md-input-container:nth-child(2)')).element(by.xpath('.//div[@class="md-char-counter"]')),

    descriptionMaxLengthError: element(by.css('md-input-container:nth-child(2)')).element(by.translateKey('global.messages.validate.field.maxlength')),


    //target date input elements
    targetDateLabel: element(by.translateKey('review.annual.goals.targetDate')),
    targetDateInput: element(by.name('targetDate')),
    targetDateError: element(by.xpath('/html/body/div[3]/md-dialog/form/md-dialog-content/div/div[1]/md-input-container/div[2]/div/span')),


    // competion date input elements
    completeInputButton: element(by.model('switchCompletion')),
    completionDateLabel: element(by.translateKey('review.annual.goals.completionDate')),
    completionDateInput: element(by.model('goal.completionDate')),
    completionDateError: element(by.xpath('/html/body/div[3]/md-dialog/form/md-dialog-content/div/div[2]/md-input-container/div[2]/div/span')),


    // status notes input elements
    statusLabel: element(by.translateKey('review.annual.goals.note')),
    statusTextArea: element(by.name('note')),

    statusCharCounter: element(by.tagName('md-dialog-content')).element(by.css('md-input-container:nth-child(4)')).element(by.xpath('.//div[@class="md-char-counter"]')),

    statusMaxLengthError: element(by.css('md-input-container:nth-child(4)')).element(by.translateKey('global.messages.validate.field.maxlength')),


    // form action buttons
    cancelBtn: element(by.translateKey('review.annual.goals.cancel')),
    saveBtn: element(by.translateKey('review.annual.goals.save')),

    confirmDeleteBtn: element(by.xpath('/html/body/div[3]/md-dialog/md-dialog-actions/button[2]')),


  };

  Object.defineProperties(this, {

    goalTabBtn: {
      get: function() {
        return this.ui.goalTabBtn.getAttribute('value');
      }
    },

    goalCharCounter: {
      get: function() {
        return this.ui.goalCharCounter.getAttribute('value');
      }
    },

    goalTextArea: {
      get: function() {
        return this.ui.goalTextArea.getAttribute('value');
      },
      set: function(textValue) {
        this.ui.goalTextArea.clear();
        this.ui.goalTextArea.sendKeys(textValue);
      }

    },

    goalRequiredMessage: {
      get: function() {
        return this.ui.goalRequiredMessage.getAttribute('value');
      }
    },

    goalMaxLengthError: {
      get: function() {
        return this.ui.goalMaxLengthError.getAttribute('value');
      }
    },

    descriptionTextArea: {
      get: function() {
        return this.ui.descriptionTextArea.getAttribute('value');
      },
      set: function(textValue) {
        this.ui.descriptionTextArea.clear();
        this.ui.descriptionTextArea.sendKeys(textValue);
      }

    },

    targetDateInput: {
      get: function() {
        return this.ui.targetDateInput.getAttribute('value');
      },
      set: function(date) {
        this.ui.targetDateInput.clear();
        this.ui.targetDateInput.sendKeys(date);
      }

    },

    completionDateInput: {
      get: function() {
        return this.ui.completionDateInput.getAttribute('value');
      },
      set: function(date) {
        this.ui.completionDateInput.clear();
        this.ui.completionDateInput.sendKeys(date);
      }

    },

    statusTextArea: {
      get: function() {
        return this.ui.statusTextArea.getAttribute('value');
      },
      set: function(textValue) {
        this.ui.statusTextArea.clear();
        this.ui.statusTextArea.sendKeys(textValue);
      }
    }
  });

  this.get = function(id) {
    browser.get('/#/review/' + id + '/edit');
    this.ui.goalTabBtn.click();
  };

  this.addGoal = function(goalText, descriptionText, targetDate, statusNotes) {
    this.ui.addGoalBtn.click();

    // enter Goal
    this.ui.goalTextArea.clear();
    this.ui.goalTextArea.sendKeys(goalText);

    // enter description
    this.ui.descriptionTextArea.clear();
    this.ui.descriptionTextArea.sendKeys(descriptionText);

    // enter targetDate
    this.ui.targetDateInput.clear();
    this.ui.targetDateInput.sendKeys(targetDate);


    // enter statusNotes
    this.ui.statusTextArea.clear();
    this.ui.statusTextArea.sendKeys(statusNotes);

    //click save button to save Goal
    this.ui.saveBtn.click();

  };

  function getGoal(index) {
    return element(by.repeater("goal in goals").row(index));
  }


  this.deleteGoal = function(itemNum) {

    var deleteButton = getGoal(itemNum).element(by.css('button:nth-child(2)'));

    browser.wait(function() {
      return deleteButton.isPresent();
    }, 10000);

    deleteButton.click();
    this.ui.confirmDeleteBtn.click();

  };

  this.markGoalComplete = function(itemNum, completionDate) {

    var selectedGoal = getGoal(itemNum).element(by.css('button:nth-child(1)'));

    selectedGoal.click();
    this.ui.completeInputButton.click();
    this.ui.completionDateInput.clear();
    this.ui.completionDateInput.sendKeys(completionDate);
    this.cancelForm();

  };

  this.saveForm = function() {
    this.ui.saveBtn.click();

  };

  this.cancelForm = function() {
    this.ui.cancelBtn.click();
  };
};
module.exports = GoalTabPage;
