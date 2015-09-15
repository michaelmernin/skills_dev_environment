'use strict';

angular.module('etmApp').controller('EvaluationDetailController', function ($scope, $mdDialog, question, review) {
  $scope.question = question;
  $scope.review = review;

  $scope.close = function () {
    $mdDialog.hide($scope.question);
  };
});
