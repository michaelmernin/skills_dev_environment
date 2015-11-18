'use strict';

angular.module('etmApp').controller('GoalsController', function ($scope, $mdDialog, $stateParams, Goal) {
  var review = {};
  $scope.goals = [];

  $scope.$parent.$watch('review', function (parentReview) {
    review = parentReview;
    if (review.id) {
      Goal.query({reviewId: review.id}, function (goals) {
        $scope.goals = goals;
      });
    }
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
    }).then(function (goal) {
      goal.$save({reviewId: review.id});
      $scope.goals.push(goal);
    });
  };
  
  $scope.editGoal = function (goal, ev) {
    goal.targetDate = new Date(goal.targetDate);
    goal.completionDate = new Date(goal.completionDate);
    $mdDialog.show({
      controller: 'GoalDetailController',
      templateUrl: 'scripts/components/entities/review/goals/goal.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        goal: goal
      }
    }).then(function (goal) {
      goal.$update({reviewId: review.id});
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
      Goal.delete(goal.review.id, goal.id);
    });
  };
  
});
