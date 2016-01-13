'use strict';

angular.module('etmApp')
    .controller('ReviewAuditDetailController', function ($scope, $rootScope, $stateParams, entity, ReviewAudit, Review, User) {
        $scope.reviewAudit = entity;
        $scope.load = function (id) {
            ReviewAudit.get({id: id}, function (result) {
                $scope.reviewAudit = result;
            });
        };
        var unsubscribe = $rootScope.$on('etmApp:reviewAuditUpdate', function (event, result) {
            $scope.reviewAudit = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
