'use strict';

angular.module('etmApp').controller('EvaluationController', function ($scope, $mdDialog, $state, $mdMedia, $window, Principal, Rating, Evaluation, $rootScope, EvaluationUtil, Notification) {
  var review = {};
  var user = {};
  var hasSubmitted = false;

  $scope.categories = {};
  $scope.getScore = Evaluation.score;
  $scope.getAvgScore = Evaluation.avgScore;
  $scope.getRatings = Evaluation.getRatings;
  $scope.keys = Object.keys;
  $scope.toggle = null;

  Principal.identity().then(function (account) {
    user = account;
    $scope.$parent.$watch('review', function (parentReview) {
      if (parentReview.id) {
        review = parentReview;
        $scope.categories = Evaluation.getCategories(review, user);
      }
    });
  });

  $rootScope.$on('evaluation-has-errors', function(){
    _validateEvaluations();
    hasSubmitted = true;
  });

  $scope.toggleCategory = function (category) {
    $scope.toggle = ($scope.toggle === category ? null : category);
  };

  $scope.viewEvaluation = function (question, ev) {
    $mdDialog.show({
      controller: 'EvaluationDetailController',
      templateUrl: $state.current.data.evaluationConfig,
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        question: question,
        review: review,
        user: user
      }
    }).then(function (question) {
      if (question) {
        updateDirtyRating(question.ratings.reviewee);
        updateDirtyRating(question.ratings.reviewer);
        angular.forEach(question.ratings.peer, updateDirtyRating);
      }
      var categories = Evaluation.getCategories(review, user);
      if(hasSubmitted) _validateEvaluations();
    });
  };

  $scope.titleLimit = function () {
    var width = Math.max(320, $window.innerWidth);
    var padding = 100;
    if ($mdMedia('gt-lg')) {
      width = 0.8 * width;
      padding += 240;
    }
    if ($mdMedia('lg')) {
      width = 0.9 * width;
      padding += 300;
    }
    return Math.floor((width - padding) * 0.136);
  };

  $scope.showRevieweeRating = function () {
    return Evaluation.showRevieweeRating(review, user);
  };

  $scope.showReviewerRating = function () {
    return Evaluation.showReviewerRating(review, user);
  };

  $scope.showPeerAverage = function () {
    return Evaluation.showReviewerRating(review, user);
  };

  $scope.showPeerRating = function (peerRating) {
    return !Evaluation.showReviewerRating(review, user) &&
      !Evaluation.showRevieweeRating(review, user) &&
      Evaluation.showPeerRating(review, user, peerRating);
  };

  /**
   * validates all evaluations
   */
  function _validateEvaluations() {

    cleanCategories();
    var hasErrors = false;
    angular.forEach($scope.categories, function(questions)
    {
      angular.forEach(questions, function(q){
        if(EvaluationUtil.isEmptyQuestion(q)){
          questions.hasError = true;
          q.hasError = true;
          hasErrors = true;
        }
      });
    });
    // emit events based on validity of evaluation feedback
    if(hasErrors) $rootScope.$emit('evaluation-invalid');
    else $rootScope.$emit('evaluation-valid');
  }
  /**
   * remove errors from categories 
   */
  function cleanCategories(){
    angular.forEach($scope.categories, function(questions)
    {
      angular.forEach(questions, function(q) {
        questions.hasError = false;
        q.hasError = false;
      });
    });
  }

  function updateDirtyRating(rating) {
    if (rating && rating.$dirty) {
      Rating.update({
        reviewId: review.id,
        feedbackId: rating.feedback.id,
        id: rating.id
      }, {
        id: rating.id,
        score: rating.score,
        comment: rating.comment,
        visible: rating.visible
      })
      .$promise
      .then(function(res){
        Notification.notify('Saved!');
      })
      .catch(function(err){
        Notification.notify('An error occured while saving your rating!');
        console.log(err);
      });
    }
  }

});
