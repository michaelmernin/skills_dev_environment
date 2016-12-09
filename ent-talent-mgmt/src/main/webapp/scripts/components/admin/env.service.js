'use strict';

angular.module('etmApp').factory('Env', function ($http) {

  var environments = {
    test: 'test',
    dev: 'dev',
    prod: 'prod',
    uat: 'uat'
  };

  function getEnvs() {
    return $http.get('api/env').then(function(response){
      return response.data;
    });
  }

  return {
    getEnvs: getEnvs
  };
});

