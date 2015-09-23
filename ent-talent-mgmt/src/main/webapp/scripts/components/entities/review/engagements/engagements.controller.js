'use strict';

angular.module('etmApp').controller('EngagementsController', function ($scope, $stateParams, Review) {
  $scope.engagements = [];
  $scope.load = function (id) {
    Review.engagements({id: id}, function (result) {
      $scope.engagements = result;
    });
  };
  $scope.load($stateParams.id);
});
