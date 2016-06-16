'use strict';

angular.module('etmApp').controller('ActivitiController', function ($scope, ActivitiService) {
  $scope.updatingActiviti = true;

  $scope.refresh = function () {
    $scope.updatingActiviti = true;
    ActivitiService.getInfo().then(function (reponse) {
      $scope.activitiInfo = reponse;
      $scope.updatingActiviti = false;
    }, function (reponse) {
     // $scope.getInfo = reponse.data;
      $scope.updatingActiviti = false;
    });
  };

  $scope.refresh();
});
