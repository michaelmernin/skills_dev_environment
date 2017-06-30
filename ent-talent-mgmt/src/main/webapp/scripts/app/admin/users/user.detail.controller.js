'use strict';

angular.module('etmApp').controller('UserDetailController', function ($scope, $mdDialog, Authority, User, user) {
  $scope.roles = [];
  Authority.query(function (result) {
    $scope.roles = result;
  });

  $scope.user = angular.copy(user);
  $scope.counselors = [];
  $scope.loadCounselors = function () {
    var users = [];
    User.query(function (result) {
      users = result;
      $scope.counselors = users.filter(userHasRole.bind(null, 'ROLE_COUNSELOR'));
    });
  };
  $scope.loadCounselors();
  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.save = function () {
    $mdDialog.hide($scope.user);
  };

  function userHasRole(role, user) {
    return user.authorities.some(function (authority) {
      return role === authority.name;
    });
  }

  function isCounselor(user) {
    var matchedCounselors = $scope.counselors.filter(function (c) { return c.id === user.id && c.id !== $scope.user.id; });
    return matchedCounselors.length !== 0;
  }

  $scope.getMatches = function (query) {
    return User.autocomplete({query: query, reviewId:""}).$promise
      .then(function(response){
        var matchedCounselors = response.filter(isCounselor);
        if (matchedCounselors.length > 10) {
          return matchedCounselors.splice(0, 11);
        } else {
          return matchedCounselors;
        }
      });
  };

});
