'use strict';

describe('Controllers Tests ', function () {

  beforeEach(module('etmApp'));

  var $scope, q, Principal;

  // define the mock Principal
  beforeEach(function () {
    Principal = {
      identity: function () {
        var deferred = q.defer();
        return deferred.promise;
      }
    };
  });

  describe('SettingsController', function () {

    beforeEach(inject(function ($rootScope, $controller, $q) {
      $scope = $rootScope.$new();
      q = $q;
      $controller('SettingsController',{$scope:$scope, Principal:Principal});
    }));

  });
});
