'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('entity', {
    abstract: true,
    parent: 'site'
  });
});
