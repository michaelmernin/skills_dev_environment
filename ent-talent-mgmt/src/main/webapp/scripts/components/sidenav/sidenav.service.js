'use strict';

angular.module('etmApp').factory('Sidenav', function Sidenav($mdUtil, $mdSidenav) {

  function toogleRightNav() {
    $mdSidenav('right-nav').toggle();
  }

  return {
    toggle: $mdUtil.debounce(toogleRightNav, 300)
  };
});
