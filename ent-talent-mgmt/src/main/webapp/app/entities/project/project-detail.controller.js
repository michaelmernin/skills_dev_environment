(function() {
    'use strict';

    angular
        .module('etmApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Project', 'User'];

    function ProjectDetailController($scope, $rootScope, $stateParams, previousState, entity, Project, User) {
        var vm = this;

        vm.project = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('etmApp:projectUpdate', function(event, result) {
            vm.project = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
