'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('rating', {
                parent: 'entity',
                url: '/rating',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Ratings'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/rating/ratings.html',
                        controller: 'RatingController'
                    }
                },
                resolve: {
                }
            })
            .state('ratingDetail', {
                parent: 'entity',
                url: '/rating/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Rating'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/rating/rating-detail.html',
                        controller: 'RatingDetailController'
                    }
                },
                resolve: {
                }
            });
    });
