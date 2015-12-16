'use strict';

angular.module('etmApp').factory('ReviewStatus', function ($resource) {
  return {
    OPEN: {id: 1, name: 'Open'},
    DIRECTOR_APPROVAL: {id: 2, name: 'Director Approval'},
    JOINT_APPROVAL: {id: 3, name: 'Joint Approval'},
    GM_APPROVAL: {id :4, name: 'GM Approval'},
    COMPLETE: {id: 5, name: 'Complete'},
    CLOSED: {id: 6, name: 'Closed'}
  };
});
