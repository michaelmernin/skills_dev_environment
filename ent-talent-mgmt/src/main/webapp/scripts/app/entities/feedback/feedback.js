'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('feedback', {
                parent: 'entity',
                url: '/feedback',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Feedbacks'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedback/feedback.html',
                        controller: 'FeedbackController'
                    }
                },
                resolve: {
                }
            })
            .state('feedbackDetail', {
                parent: 'entity',
                url: '/feedback/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Feedback'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedback/feedback-detail.html',
                        controller: 'FeedbackDetailController'
                    }
                },
                resolve: {
                }
            });
    });
