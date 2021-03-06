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
    // TODO: remove this unused template and controller
    /*
      'content@': {
        templateUrl: 'scripts/app/entities/review/reviews.html',
        controller: 'ReviewController'
      }
     */
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
      pageTitle: 'review.detail.title',
      goalsConfig: 'scripts/components/entities/review/goals/goal.detail.summary.html',
      deliverablesConfig: 'scripts/components/entities/review/goals/deliverable.detail.summary.html',
      evaluationConfig: 'scripts/components/entities/review/evaluation/evaluation.detail.summary.html'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/review/review-detail.html',
        controller: 'ReviewDetailController'
      },
      'evaluation@review.detail': {
        templateUrl: 'scripts/components/entities/review/evaluation/evaluation.html',
        controller: 'EvaluationController'
      },
      'engagements@review.detail': {
        templateUrl: 'scripts/components/entities/review/engagements/engagements.html',
        controller: 'EngagementsController'
      },
      'goals@review.detail': {
        templateUrl: 'scripts/components/entities/review/goals/goal.summary.html',
        controller: 'GoalsController'
      },
      'deliverables@review.detail': {
        templateUrl: 'scripts/components/entities/review/goals/deliverable.summary.html',
        controller: 'GoalsController'
      },
      'peers@review.detail': {
        templateUrl: 'scripts/components/entities/review/peers/peers.summary.html',
        controller: 'PeersController'
      },
      'overall@review.detail': {
        templateUrl: 'scripts/components/entities/review/overall/overall.summary.html',
        controller: 'OverallController'
      },
      'todo@review.detail': {
        templateUrl: 'scripts/components/entities/review/todo/todo.html',
        controller: 'TodoController'
      }
    }
  }).state('review.edit', {
    parent: 'review.detail',
    url: '/edit',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'review.detail.edit.title',
      goalsConfig: 'scripts/components/entities/review/goals/goal.detail.html',
      deliverablesConfig: 'scripts/components/entities/review/goals/deliverable.detail.html',
      evaluationConfig: 'scripts/components/entities/review/evaluation/evaluation.detail.html'
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
      },
      'todo@review.edit': {
        templateUrl: 'scripts/components/entities/review/todo/todo.html',
        controller: 'TodoController'
      }
    }
  });
});
