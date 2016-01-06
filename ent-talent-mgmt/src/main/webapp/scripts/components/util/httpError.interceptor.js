'use strict';

angular.module('etmApp').factory('HttpErrorInterceptor', function ($q, $injector) {
  return {
    responseError: function (rejection) {
      if (rejection.status === 404) {
        $injector.get('$state').go('notfound');
      } else if (rejection.status === 403) {
        $injector.get('$state').go('accessdenied');
      } else if (rejection.status >= 500 && rejection.status <= 599) {
        $injector.get('$state').go('error', {errorMessage: rejection.statusText});
      }
      return $q.reject(rejection);
    }
  };
});