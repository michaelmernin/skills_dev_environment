'use strict';

angular.module('etmApp')
  .controller('EngagementsController', function ($scope, $stateParams, Engagement, Review, ReviewType, ReviewStatus, User) {
      $scope.engagements = [];
      $scope.load = function (id) {
        Engagement.get({id: id}, function (result) {
          $scope.engagements = result;
        })
      };
      $scope.load($stateParams.id);
    });