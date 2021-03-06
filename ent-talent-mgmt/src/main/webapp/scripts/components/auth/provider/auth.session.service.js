'use strict';

angular.module('etmApp').factory('AuthServerProvider', function loginService($http, localStorageService, $window) {
  var DOMAIN_REGEX = /@perficient\.com$/;

  return {
    login: function (credentials) {
      if (DOMAIN_REGEX.test(credentials.username)) {
        credentials.username = credentials.username.replace(DOMAIN_REGEX, '');
      }
      var data = 'j_username=' + encodeURIComponent(credentials.username) +
          '&j_password=' + encodeURIComponent(credentials.password) +
          '&_spring_security_remember_me=' + credentials.rememberMe + '&submit=Login';
      return $http.post('api/authentication', data, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }).success(function (response) {
        localStorageService.set('token', $window.btoa(credentials.username + ':' + credentials.password));
        return response;
      });
    },
    logout: function () {
      // logout from the server
      return $http.post('api/logout').success(function (response) {
        localStorageService.clearAll();
        // to get a new csrf token call the api
        $http.get('api/account');
        return response;
      });
    },
    getToken: function () {
      var token = localStorageService.get('token');
      return token;
    },
    hasValidToken: function () {
      var token = this.getToken();
      return !!token;
    }
  };
});
