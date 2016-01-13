'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('review', {
    parent: 'entity',
    url: '/review',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'review.home.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/review/reviews.html',
        controller: 'ReviewController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('review');
        return $translate.refresh();
      }]
    }
  }).state('review.new', {
    parent: 'review',
    url: '/new',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'review.new.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/review/review-new.html',
        controller: 'ReviewNewController'
      }
    }
  }).state('review.detail', {
    parent: 'review',
    url: '/:id',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'review.detail.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/review/review-detail.html',
        controller: 'ReviewDetailController'
      }
    }
  }).state('review.edit', {
    parent: 'review.detail',
    url: '/edit',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'review.detail.edit.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/review/review-edit.html',
        controller: 'ReviewEditController'
      },
      'engagements@review.edit': {
        templateUrl: 'scripts/components/entities/review/engagements/engagements.html',
        controller: 'EngagementsController'
      },
      'evaluation@review.edit': {
        templateUrl: 'scripts/components/entities/review/evaluation/evaluation.html',
        controller: 'EvaluationController'
      },
      'peers@review.edit': {
        templateUrl: 'scripts/components/entities/review/peers/peers.html',
        controller: 'PeersController'
      },
      'goals@review.edit': {
        templateUrl: 'scripts/components/entities/review/goals/goals.html',
        controller: 'GoalsController'
      },
      'overall@review.edit': {
          templateUrl: 'scripts/components/entities/review/overall/overall.html',
          controller: 'OverallController'
        }
    }
  });
});
