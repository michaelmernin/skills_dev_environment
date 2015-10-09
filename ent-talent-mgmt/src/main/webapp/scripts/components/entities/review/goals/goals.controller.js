'use strict';

angular.module('etmApp').controller('GoalsController', function ($scope, $mdDialog, $stateParams, Goal) {
  var review = {};
  $scope.goals = [];

  $scope.$parent.$watch('review', function (parentReview) {
    review = parentReview;
    $scope.goals = review.goals || [];
  });
  
  $scope.addGoal = function (ev) {
    $mdDialog.show({
      controller: 'GoalDetailController',
      templateUrl: 'scripts/components/entities/review/goals/goal.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        goal: new Goal()
      }
    })
    .then(function (goal) {
      goal.$save();
      $scope.goals.push(goal);
    });
  };
  
  $scope.editGoal = function (goal, ev) {
    $mdDialog.show({
      controller: 'GoalDetailController',
      templateUrl: 'scripts/components/entities/review/goals/goal.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        goal: goal
      }
    })
    .then(function (goal) {
      goal.$update();
    });
  };
  
  $scope.deleteGoal = function (goal, ev) {
    var confirmDelete = $mdDialog.confirm()
      .title('Confirm Goal Deletion')
      .ariaLabel('Delete Goal Confirm')
      .content('Delete Goal: ' + goal.name + '?')
      .ok('Delete')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(function () {
      $scope.goals.splice($scope.goals.indexOf(goal), 1);
    });
  };
  
});
