'use strict';

angular.module('etmApp').controller('GoalDetailController', function ($scope, $mdDialog, goal) {

  $scope.goal = angular.copy(goal);
  $scope.switchCompletion = goal.completionDate ? true : false;
  $scope.isReviewee = goal.isReviewee;
  $scope.isReviewer = goal.isReviewer;
  $scope.isCounselor = goal.isCounselor
  $scope.isFeedbackSubmitted = goal.isFeedbackSubmitted;

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.hide = function () {
    if ($scope.goalForm.$valid) {
      $mdDialog.hide($scope.goal);
    }
  };

  $scope.goalComplete = function goalComplete(complete) {
    if(!complete) $scope.goal.completionDate = null;
  };

});