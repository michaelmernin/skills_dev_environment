'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('skillsReview', {
    parent: 'entity',
    url: '',
    data: {
      roles: ['ROLE_COUNSELOR','ROLE_GENERAL_MANAGER'],
      pageTitle: 'global.menu.skillsReview'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/skills/skillsReview.html',
        controller: 'SkillsReviewController'
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
