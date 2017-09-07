'use strict';

angular.module('etmApp').service('Notification', function ($mdToast) {

  /**
   * Show an mdtoast notification
   */
  this.notify = function notify(message, hideDelay) {
    return $mdToast
    .show($mdToast.simple()
      .textContent(message)
      .hideDelay(hideDelay ? hideDelay: 3000) // use hide delay if available, else use default 3 seconds
    );
  };
});
