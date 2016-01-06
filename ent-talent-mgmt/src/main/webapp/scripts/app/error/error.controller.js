'use strict';

angular.module('etmApp').controller('ErrorController', function ($scope, $stateParams) {
  $scope.errorMessage = $stateParams.errorMessage;
});
