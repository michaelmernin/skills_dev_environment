'use strict';

describe('Controllers Tests ', function () {

  beforeEach(module('etmApp'));

  describe('MainController', function () {
    var $httpBackend, $scope, spiedPrincipal, createController;
    var account = {
      email: 'Dev.User1@email.com',
      firstName: 'Dev',
      lastName: 'UserOne',
      login: 'dev.user1',
      password: null,
      langKey: 'en',
      roles: ['ROLE_USER', 'ROLE_ADMIN']
    };
    var accountError = {
      timestamp: (new Date()).getTime(),
      status: 401,
      error: 'Unauthorized',
      message: 'Access Denied',
      path: '/api/account'
    };

    beforeEach(inject(function ($injector, $rootScope, $controller, Principal) {
      $httpBackend = $injector.get('$httpBackend');
      $scope = $rootScope.$new();
      spiedPrincipal = Principal;

      stubInitRequests($httpBackend);

      createController = function () {
        return $controller('MainController', {$scope: $scope, Principal: Principal});
      };
    }));

    afterEach(function () {
      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
    });

    it('should set account properties when logged in', function () {
      $httpBackend.expectGET(/api\/account\?cacheBuster=\d+/).respond(account);
      $httpBackend.expectGET(/api\/account\?cacheBuster=\d+/).respond(account);
      spyOn(spiedPrincipal, 'identity').and.callThrough();

      createController();
      $httpBackend.flush();

      expect(spiedPrincipal.identity).toHaveBeenCalled();
      expect($scope.isAuthenticated()).toBeTruthy();
      expect($scope.account).toBeDefined();
      expect($scope.account.login).toEqual(account.login);
      expect($scope.account.email).toEqual(account.email);
      expect($scope.account.firstName).toEqual(account.firstName);
      expect($scope.account.lastName).toEqual(account.lastName);
      expect($scope.account.roles).toEqual(account.roles);
      expect($scope.account.password).toBeNull();
    });

    it('should not set account properties when logged out', function () {
      $httpBackend.expectGET(/api\/account\?cacheBuster=\d+/).respond(401, accountError);
      $httpBackend.expectGET(/api\/account\?cacheBuster=\d+/).respond(401, accountError);
      $httpBackend.expectGET(/i18n\/en\/login.json/).respond(401, accountError);
      spyOn(spiedPrincipal, 'identity').and.callThrough();

      createController();
      $httpBackend.flush();

      expect(spiedPrincipal.identity).toHaveBeenCalled();
      expect($scope.isAuthenticated()).toBeFalsy();
      expect($scope.account).toBeDefined();
      expect($scope.account.login).toBeUndefined();
      expect($scope.account.email).toBeUndefined();
      expect($scope.account.firstName).toBeUndefined();
      expect($scope.account.lastName).toBeUndefined();
      expect($scope.account.roles).toBeUndefined();
      expect($scope.account.password).toBeUndefined();
    });
  });
});
