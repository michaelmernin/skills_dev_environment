'use strict';

angular.module('etmApp').controller('GoalsController', function ($scope, $mdDialog, $stateParams, $state, $window, $mdMedia, Goal, Principal) {
  var review = {};
  $scope.goals = [];
  var user = {};
  var isReviewee = {};
  Principal.identity().then(function (account) {
    user = account;
  });

  $scope.$parent.$watch('review', function (parentReview) {
    review = parentReview;
    if (review.id) {

      Goal.query({reviewId: review.id}, function (goals) {
        $scope.goals = goals;
      });
    }
  });

  $scope.addGoal = function (ev) {
    var goal = new Goal();
    if (user.id == review.reviewee.id) {
      goal.isReviewee = true;
    } else {
      goal.isReviewee = false;
    }
    var isEngagementReview = review.reviewType.id === 2;
    var templateUrl = 'scripts/components/entities/review/goals/';
    templateUrl += isEngagementReview ? 'deliverable.detail.html' : 'goal.detail.html';
    $mdDialog.show({
      controller: 'GoalDetailController',
      templateUrl: templateUrl,
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        goal: goal
      }
    }).then(function (goal) {
      goal.author = user;
      goal.$save({reviewId: review.id}, function (savedGoal) {
        $scope.goals.push(savedGoal);
      });
    });
  };

  $scope.editGoal = function (goal, ev) {
    if (goal.targetDate != null) {
      var tempGoal = new Date(goal.targetDate);
      goal.targetDate = new Date(tempGoal.getTime() + tempGoal.getTimezoneOffset() * 60000);
    }
    if (goal.completionDate != null) {
      var tempGoal = new Date(goal.completionDate);
      goal.completionDate = new Date(tempGoal.getTime() + tempGoal.getTimezoneOffset() * 60000);
    }
    if (user.id == review.reviewee.id) {
      goal.isReviewee = true;
    } else {
      goal.isReviewee = false;
    }
    var isEngagementReview = review.reviewType.id === 2;
    $mdDialog.show({
      controller: 'GoalDetailController',
      templateUrl: isEngagementReview ? $state.current.data.deliverablesConfig : $state.current.data.goalsConfig,
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        goal: goal
      }
    }).then(function (goal) {
      goal.review = goal.review || {};
      goal.review.id = goal.review.id || review.id;
      goal.$update({reviewId: review.id});
    });
  };

  $scope.deleteGoal = function (goal, ev) {
    var isEngagementReview = review.reviewType.id === 2;
    var label = isEngagementReview ? 'Deliverable' : 'Goal';
    var confirmDelete = $mdDialog.confirm()
      .title('Confirm ' + label + ' Deletion')
      .ariaLabel('Delete ' + label + ' Confirm')
      .content('Delete ' + label + ': ' + goal.name + '?')
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
    if (completionDate) {
      return 'green;'
    } else {
      return ''
    }
  };

  $scope.isReviewee = function () {
    return review.reviewee && user.id == review.reviewee.id;
  };

  $scope.isEngagementReview = function () {
    return review.reviewType.id === 2;
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
