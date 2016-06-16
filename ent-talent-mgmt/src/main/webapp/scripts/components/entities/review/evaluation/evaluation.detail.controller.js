'use strict';

angular.module('etmApp').controller('EvaluationDetailController', function ($scope, $mdDialog, Evaluation, question, review, user) {
  $scope.question = question;
  $scope.review = review;

  $scope.close = function () {
    if ($scope.evalForm.$valid) {
      $mdDialog.hide($scope.question);
    }
  };

  $scope.showRevieweeRating = function () {
    return Evaluation.showRevieweeRating($scope.review, user);
  };

  $scope.showReviewerRating = function () {
    return Evaluation.showReviewerRating($scope.review, user);
  };

  $scope.showPeerRatings = function () {
    return Evaluation.showPeerRatings($scope.review, user);
  };

  $scope.showPeerRating = function (peerRating) {
    return Evaluation.showPeerRating($scope.review, user, peerRating);
  };
});
