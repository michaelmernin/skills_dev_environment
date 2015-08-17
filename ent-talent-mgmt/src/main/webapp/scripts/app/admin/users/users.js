'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('users', {
    parent: 'admin',
    url: '/users',
    data: {
      roles: ['ROLE_ADMIN'],
      pageTitle: 'users.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/users/users.html',
        controller: 'UsersController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('users');
        return $translate.refresh();
      }]
    }
  });
});
