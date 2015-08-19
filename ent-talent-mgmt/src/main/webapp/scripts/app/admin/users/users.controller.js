'use strict';

angular.module('etmApp').controller('UsersController', function ($scope, $mdDialog, $filter, User) {
  $scope.users = [];
  $scope.loadAll = function () {
    User.query(function (result) {
      $scope.users = result;
    });
  };
  $scope.loadAll();

  function userHasRole(role, user) {
    return user.authorities.some(function (authority) {
      return role === authority.name;
    });
  }

  $scope.viewUserDetails = function (user, ev) {
    var counselors = $scope.users.filter(userHasRole.bind(null, 'ROLE_COUNSELOR'));
    var generalManagers = $scope.users.filter(userHasRole.bind(null, 'ROLE_GENERAL_MANAGER'));
    $mdDialog.show({
      controller: 'UserDetailController',
      templateUrl: 'scripts/app/admin/users/user.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        user: user,
        counselors: counselors,
        generalManagers: generalManagers
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
      User.delete({id: user.id}, function () {
        $scope.users.splice($scope.users.indexOf(user), 1);
      });
    });
  };
});
