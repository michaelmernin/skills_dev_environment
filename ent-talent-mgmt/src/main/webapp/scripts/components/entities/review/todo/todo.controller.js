'use strict';

angular.module('etmApp').controller('TodoController', function ($scope, $stateParams, $mdDialog, Principal, Todo, Review, ReviewStatus, FeedbackType, Feedback, Evaluation, EvaluationUtil) {
  $scope.todo = Todo.query();
  var reviewerFeedback = {};
  var review = {};
  var user = {};

  Principal.identity().then(function (account) {
    user = account;
    $scope.$parent.$watch('review', function (parentReview) {
      if (parentReview.id) {
        review = parentReview;
        $scope.categories = Evaluation.getCategories(review, user);
        loadTodo();
      }
    });

    $scope.$parent.$watch('review.feedback', function (parentFeedback) {
      if (parentFeedback && parentFeedback.length) {
        parentFeedback.forEach(function (feedbackItem) {
          if (feedbackItem.feedbackType.id === FeedbackType.REVIEWER.id) {
            reviewerFeedback = feedbackItem;
          }
        });
      }
    });
  });

  $scope.confirm = function (action, categories, questions, ev) {
    var confirmAction = $mdDialog.confirm()
      .title(action.name)
      .ariaLabel('Confirm Action Dialog')
      .content(action.confirm)
      .ok(action.result)
      .cancel('Cancel')
      .targetEvent(ev);
    var missingQuestionDialog = $mdDialog.confirm()
      .title("Feedback questions missing rating")
      .ariaLabel('Missing Feedbacks Dialog')
      .content("One or more feedback questions are missing a rating.")
      .ok("Okay")
      .targetEvent(ev);
    var categories = Evaluation.getCategories(review, user);
    var hasEmptyQuestion = false;
    var isDirector = EvaluationUtil.isDirector(review, user);
    var isGM = EvaluationUtil.isGM(review, user);
    if(!isDirector && !isGM){
      hasEmptyQuestion = checkForEmptyQuestions(categories);
    }
    if (!hasEmptyQuestion) {
      $mdDialog.show(confirmAction).then(function () {
          Todo.update({id: action.todoId}, action, loadTodo);
      });
    } else {
      $mdDialog.show(missingQuestionDialog);
    }
  };

  function checkForEmptyQuestions(categories) {
    var hasEmpty;
    var stopSubmit;
    var mdTitleSelector;
    $.each(categories, function(key, category) {
      hasEmpty = false;
      $.each(category, function(key, question) {
        if (question.editableRating.score === null || question.editableRating.score === undefined) {
          hasEmpty = true;
          stopSubmit = true;
          $.each($("div[ui-view='evaluation'] md-list-item[role='listitem'][ng-repeat]"), function(key, value) {
            if (question.text.indexOf($(value).find("h4.ng-binding").text()) > -1){
              $(this).find("h4.ng-binding").addClass("evaluation-error");
            }
          });
        }
      });
      if (hasEmpty) {
        mdTitleSelector = ".md-title:contains('" + key + "')";
        $("div[ui-view='evaluation'] md-list-item[role='listitem']:not([ng-repeat])").find(mdTitleSelector).addClass("evaluation-error");
      }
    });
    return stopSubmit;
  }

  function loadTodo() {
    Feedback.query({reviewId: review.id}, function (feedback) {
      review.feedback = feedback;

      Review.todo({id: review.id}, function (result) {
        $scope.todo = result;
        $scope.actions = Todo.getActions($scope.todo, review, reviewerFeedback);
      });
    });
  }
});
