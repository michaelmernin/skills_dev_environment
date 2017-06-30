'use strict';

angular.module('etmApp').controller('UsersController', function ($scope, $mdDialog, User) {
  $scope.selectedUser = null;

  $scope.selectUser = function(user) {
    $scope.selectedUser = user;
  };

  $scope.getMatches = function (query) {
    if (query.trim().length) {
      return User.autocomplete({query: query, reviewId:""}).$promise
        .then(function(response){
          var nbrUsers = response.length || 0;
          return nbrUsers > 10 ? response.slice(0,11) : response;
        });
    } else {
      return [];
    }
  };

  $scope.viewUserDetails = function (user, ev) {
    $mdDialog.show({
      controller: 'UserDetailController',
      templateUrl: 'scripts/app/admin/users/user.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        user: user
      }
    }).then(function (updatedUser) {
      angular.copy(updatedUser, user);
      User.update(user);
    });
  };

  $scope.deleteUser = function (user, ev) {
    var confirmDelete = $mdDialog.confirm()
      .title('Confirm User Deletion')
      .ariaLabel('Delete User Confirm')
      .content('Delete ' + user.firstName + ' ' + user.lastName + '?')
      .ok('Delete')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(function () {
      User.delete({id: user.id});
    });
  };
});
