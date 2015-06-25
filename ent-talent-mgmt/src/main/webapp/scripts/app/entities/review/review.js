'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('review', {
                parent: 'entity',
                url: '/review',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.review.home.title'
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
            })
            .state('reviewDetail', {
                parent: 'entity',
                url: '/review/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.review.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/review/review-detail.html',
                        controller: 'ReviewDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('review');
                        return $translate.refresh();
                    }]
                }
            });
    });
