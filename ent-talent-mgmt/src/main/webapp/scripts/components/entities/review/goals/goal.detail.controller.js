'use strict';

angular.module('etmApp').controller('GoalDetailController', function ($scope, $mdDialog, goal) {

  $scope.goal = angular.copy(goal);
  $scope.switchCompletion = goal.completionDate ? true : false;
  $scope.load = function() {
    $scope.isReviewee = goal.isReviewee;
    $scope.isReviewer = goal.isReviewer;
    $scope.isCounselor = goal.isCounselor
  };
  $scope.load();

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.hide = function () {
    if ($scope.goalForm.$valid) {
      $mdDialog.hide($scope.goal);
    }
  };

  $scope.checkCompletion = function (completed) {
    if (completed) {
      $scope.goal.completionDate = "";
    }
  };

});