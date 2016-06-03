'use strict';

angular.module('etmApp').controller('MailController', function ($scope, $mdDialog, Mail) {
  $scope.messages=[];
  
  function updateMessages(){
    Mail.findAll().$promise.then(function(val){
    val.forEach(function(m){
      // text preview of email
      m.text = getText(m.body);
    });
    $scope.messages = val;
  });
  }
  updateMessages();
  
  function getText(s) {
    var el = document.createElement('html');
    el.innerHTML = s;
    var styles = el.getElementsByTagName('style');
    var i = styles.length;
    while (i--) {
      styles[i].parentNode.removeChild(styles[i]);
    }
    return el.textContent;
  }

  
  $scope.clear = function () {
    Mail.clear({}, function () {
      $scope.messages = Mail.findAll();
    });
  };
  $scope.previewMessage = function (message,ev) {
    $mdDialog.show({
      controller: 'MessageDetailController',
      templateUrl: 'scripts/app/entities/mail/message.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        message: message
      }
    }).finally(function(){
      updateMessages();
    });
  };
});
