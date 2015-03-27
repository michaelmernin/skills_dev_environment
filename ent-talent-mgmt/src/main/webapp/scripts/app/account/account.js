'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('account', {
    abstract: true,
    parent: 'site'
  });
});
