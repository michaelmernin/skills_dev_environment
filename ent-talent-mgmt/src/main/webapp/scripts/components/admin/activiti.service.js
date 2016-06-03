'use strict';

angular.module('etmApp').factory('ActivitiService', function ($rootScope, $http) {
  return {
    getInfo: function () {
      return $http.get('activiti').then(function (response) {
        return response.data;
      });
    }
  };
});
