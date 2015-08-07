'use strict';

angular.module('etmApp').controller('LogsController', function ($scope, LogsService) {
  $scope.loggers = LogsService.findAll();

  $scope.changeLevel = function (name, level) {
    LogsService.changeLevel({name: name, level: level}, function () {
      $scope.loggers = LogsService.findAll();
    });
  };
  
  $scope.splitClass = function (className) {
    return className.replace(/\.([A-Z])/, ' $1')
  }
});
