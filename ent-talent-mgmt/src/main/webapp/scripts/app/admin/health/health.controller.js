'use strict';

angular.module('etmApp').controller('HealthController', function ($scope, MonitoringService) {
  $scope.updatingHealth = true;

  $scope.refresh = function () {
    $scope.updatingHealth = true;
    MonitoringService.checkHealth().then(function (reponse) {
      $scope.healthCheck = reponse;
      $scope.updatingHealth = false;
    }, function (reponse) {
      $scope.healthCheck = reponse.data;
      $scope.updatingHealth = false;
    });
  };

  $scope.refresh();

  $scope.getIconClass = function (statusState) {
    return statusState === 'UP' ? 'fa-check' : 'fa-times';
  };

  $scope.getIconColor = function (statusState) {
    return statusState === 'UP' ? '#4CAF50' : '#dd2c00';
  }
});
