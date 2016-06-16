'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('activiti', {
    parent: 'admin',
    url: '/activiti',
    data: {
      roles: ['ROLE_ADMIN'],
      pageTitle: 'Activiti'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/activiti/activiti.html',
        controller: 'ActivitiController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('activiti');
        return $translate.refresh();
      }]
    }
  });
});