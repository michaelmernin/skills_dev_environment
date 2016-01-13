'use strict';

angular.module('etmApp')
    .controller('ReviewAuditController', function ($scope, $state, $modal, ReviewAudit, ParseLinks) {
      
        $scope.reviewAudits = [];
        $scope.page = 0;
        $scope.loadAll = function () {
            ReviewAudit.query({page: $scope.page, size: 20}, function (result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.reviewAudits = result;
            });
        };
        $scope.loadPage = function (page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.reviewAudit = {
                reviewAuditId: null,
                date: null,
                comment: null,
                id: null
            };
        };
    });
