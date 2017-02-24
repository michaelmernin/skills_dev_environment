'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('skillsSearch', {
    parent: 'entity',
    url: '/skillsSearch',
    data: {
      roles: ['ROLE_GENERAL_MANAGER'],
      pageTitle: 'global.menu.skillsSearch'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/skills/skillsSearch.html',
        controller: 'SkillsSearchController'
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
