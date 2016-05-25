'use strict';

angular.module('etmApp').controller('MailController', function ($scope, $mdDialog, Mail) {
  $scope.messages = Mail.findAll();

  $scope.sendTest = function () {
    Mail.testMessage({}, function () {
      $scope.messages = Mail.findAll();
    });
  };
  $scope.clear = function () {
    Mail.clear({}, function () {
      $scope.messages = Mail.findAll();
    });
  };
  $scope.newMail = function (ev) {
    $mdDialog.show({
      controller: 'MailDetailController',
      templateUrl: 'scripts/app/admin/mail/mail.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        mail: new Mail()
      }
    }).then(function (mail) {
      mail.$send({}, function (savedmail) {
        $scope.messages = Mail.findAll();
      });
    });
  };
  $scope.previewMessage = function (message,ev) {
    $mdDialog.show({
      controller: 'MessageDetailController',
      templateUrl: 'scripts/app/admin/mail/message.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        message: message
      }
    });
  };
});
