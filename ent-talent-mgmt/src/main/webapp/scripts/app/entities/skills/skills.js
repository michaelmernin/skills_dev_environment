'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('skills', {
    parent: 'entity',
    url: '/skills',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'global.menu.skills'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/skills/skills.html',
        controller: 'SkillsController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('skills');
        return $translate.refresh();
      }]
    }
  });
});
