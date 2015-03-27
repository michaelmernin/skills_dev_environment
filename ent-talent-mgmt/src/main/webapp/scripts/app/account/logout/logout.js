'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('logout', {
    parent: 'account',
    url: '/logout',
    data: {
      roles: []
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/main/main.html',
        controller: 'LogoutController'
      }
    }
  });
});
