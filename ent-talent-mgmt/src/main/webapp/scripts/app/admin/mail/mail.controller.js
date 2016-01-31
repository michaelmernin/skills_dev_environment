'use strict';

angular.module('etmApp').controller('MailController', function ($scope, MailService) {
  $scope.messages = MailService.findAll();

  $scope.sendTest = function () {
    MailService.testMessage({}, function () {
      $scope.messages = MailService.findAll();
    });
  };
});
