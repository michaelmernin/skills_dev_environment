'use strict';

angular.module('etmApp').controller('GoalsController', function ($scope, $mdDialog, $stateParams, Goal, $window, $mdMedia) {
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
      goal.$save({reviewId: review.id}, function (savedGoal) {
        $scope.goals.push(savedGoal);
      });      
    });
  };
  
  $scope.editGoal = function (goal, ev) {
    if (goal.targetDate != null) {
      goal.targetDate = new Date(goal.targetDate);
    }
    if (goal.completionDate != null) {
      goal.completionDate = new Date(goal.completionDate);
    }    
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
      Goal.delete({reviewId: review.id, id: goal.id});
    });
  };
  
  $scope.getIcon = function (completionDate) {
    if (completionDate) {
      return 'fa fa-lg fa-check-circle-o'
    } else {
      return 'fa fa-lg fa-circle-o'
    }
  };
  
  $scope.getColor = function (completionDate) {
    if(completionDate) {
      return 'green;'
    } else {
      return ''
    }
  };
  
  $scope.fieldLimit = function () {
    var width = Math.max(320, $window.innerWidth);
    var padding = 100;
    if ($mdMedia('gt-lg')) {
      width = 0.5 * width;
      padding += 240;
    }
    if ($mdMedia('lg')) {
      width = 0.6 * width;
      padding += 300;
    }
    return Math.floor((width - padding) * 0.136);
  };
});
