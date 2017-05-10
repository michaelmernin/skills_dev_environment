'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('projects', {
    parent: 'admin',
    url: '/projects',
    data: {
      roles: ['ROLE_ADMIN'],
      pageTitle: 'projects.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/projects/projects.html',
        controller: 'ProjectsController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('projects');
        return $translate.refresh();
      }]
    }
  });
});
