'use strict';

angular.module('etmApp').controller('GoalDetailController', function ($scope, $mdDialog, goal) {
  $scope.goal = goal;
  $scope.switchCompletion = goal.completionDate ? true : false;
  
  $scope.cancel = function () {
    $mdDialog.cancel();
  };
  
  $scope.hide = function () {
    if ($scope.goalForm.$valid) {
      $mdDialog.hide($scope.goal);
    }
  };
  
  $scope.isReviewee = function () {
    if (goal.isReviewee) {
      return true;
    } else {
      return false;
    }
  };
  
  $scope.isPopulated = function () {
    if ($scope.goal.completionDate) {
      return true;
    } else {
      return false;
    }
  }
  
});
