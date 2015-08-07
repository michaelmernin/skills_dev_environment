'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('status', {
    parent: 'admin',
    url: '/status',
    data: {
      roles: ['ROLE_ADMIN'],
      pageTitle: 'status.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/status/status.html',
        controller: 'StatusController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('status');
        return $translate.refresh();
      }]
    }
  });
});
