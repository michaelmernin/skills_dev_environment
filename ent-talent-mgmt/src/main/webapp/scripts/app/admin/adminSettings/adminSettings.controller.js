'use strict';

angular.module('etmApp').controller('AdminSettingsController', function ($scope, AdminSetting, Notification) {
  $scope.settings = [];
  $scope.erStartDate = {key:'engagementStartDate', value:null, description:'Engagement Review start date'};
  $scope.erEndDate = {key:'engagementEndDate',   value:null, description:'Engagement Review end date'};

  $scope.save = function(setting, doNotNotify){
    var create = AdminSetting.create(setting);
    if(doNotNotify) return;
    create.$promise
    .then(function(){Notification.notify('Saved!');});
  };

  $scope.delete = function(setting){
    AdminSetting
    .delete({key:setting.key}, function(){
      setting.value = null;
      Notification.notify('Deleted!');
    });
  };

  AdminSetting
  .get()
  .$promise
  .then(function(response){
    $scope.settings = response;
    // if setting is stored server-side, use it
    angular.forEach(response,function(setting) {
      if(setting.key === $scope.erStartDate.key){
        $scope.erStartDate = setting;
        $scope.erStartDate.value = new Date($scope.erStartDate.value);
      }
      else if(setting.key === $scope.erEndDate.key){
        $scope.erEndDate = setting;
        $scope.erEndDate.value = new Date($scope.erEndDate.value);
      }
    });


  });
});
