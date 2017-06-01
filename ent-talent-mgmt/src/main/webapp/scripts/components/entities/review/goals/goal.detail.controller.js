'use strict';

angular.module('etmApp').controller('GoalDetailController', function ($scope, $mdDialog, goal) {
  $scope.goal = goal;
  $scope.switchCompletion = goal.completionDate ? true : false;
  $scope.load = function() {
    $scope.isReviewee = goal.isReviewee;
    $scope.isReviewer = goal.isReviewer;
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
  }

});