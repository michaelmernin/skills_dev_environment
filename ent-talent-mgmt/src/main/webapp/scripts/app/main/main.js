'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('home', {
    parent: 'site',
    url: '/',
    data: {
      roles: []
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/main/main.html',
        controller: 'MainController'
      },
      'dashboard.reviews@home': {
        templateUrl: 'scripts/components/main/review/review-list.html',
        controller: 'ReviewListController'
      }
    },
    resolve: {
      mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
        $translatePartialLoader.addPart('main');
        return $translate.refresh();
      }]
    }
  });
});
