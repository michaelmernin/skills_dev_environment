'use strict';

angular.module('etmApp').controller('EvaluationController', function ($scope, $mdDialog, $state, $mdMedia, $window, Principal, Rating, Evaluation) {
  var review = {};
  var user = {};

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
      updateDirtyRating(question.ratings.reviewee);
      updateDirtyRating(question.ratings.reviewer);
      angular.forEach(question.ratings.peer, updateDirtyRating);
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
    return !Evaluation.showReviewerRating(review, user)
      && !Evaluation.showRevieweeRating(review, user)
      && Evaluation.showPeerRating(review, user, peerRating);
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
        visible: rating.visible,
        question: {id: rating.question.id}
      });
    }
  }

});
