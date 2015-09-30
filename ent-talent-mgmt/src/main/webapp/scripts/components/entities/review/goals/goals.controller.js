'use strict';

angular.module('etmApp').controller('GoalsController', function ($scope, $stateParams, Goal) {
  var review = {};
  $scope.goals = [];

  $scope.$parent.$watch('review', function (parentReview) {
    review = parentReview;
    $scope.goals = review.goals;
  });
  
});
