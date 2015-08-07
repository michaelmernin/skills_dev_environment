'use strict';

angular.module('etmApp').factory('MonitoringService', function ($rootScope, $http) {
  return {
    getStatus: function () {
      return $http.get('status/status').then(function (response) {
        return response.data;
      });
    },

    checkHealth: function () {
      return $http.get('health').then(function (response) {
        return response.data;
      });
    },

    threadDump: function () {
      return $http.get('dump').then(function (response) {
        return response.data;
      });
    }
  };
});
