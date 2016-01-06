'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  var translateLoader = ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
    $translatePartialLoader.addPart('error');
    return $translate.refresh();
  }];

  $stateProvider.state('error', {
    parent: 'site',
    url: '/error',
    data: {
      roles: []
    },
    params: {
      errorMessage: "Unexpected Error"
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/error/error.html',
        controller: 'ErrorController'
      }
    },
    resolve: {
      mainTranslatePartialLoader: translateLoader
    }
  }).state('accessdenied', {
    parent: 'site',
    url: '/accessdenied',
    data: {
      roles: []
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/error/accessdenied.html'
      }
    },
    resolve: {
      mainTranslatePartialLoader: translateLoader
    }
  }).state('notfound', {
    parent: 'site',
    url: '/notfound',
    data: {
      roles: []
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/error/notfound.html'
      }
    },
    resolve: {
      mainTranslatePartialLoader: translateLoader
    }
  });
});
