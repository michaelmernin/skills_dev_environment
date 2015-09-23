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
        templateUrl: 'scripts/app/entities/review/new-review.html',
        controller: 'NewReviewController'
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
	    }
	  }
  });
});
