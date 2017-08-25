'use strict';

angular.module('etmApp').controller('AdminSettingsController', function ($scope, AdminSettingService, $mdToast) {
  $scope.settings = [];
  $scope.erStartDate = {key:'engagement.startDate', value:'', description:'Engagement Review start date'};
  $scope.erEndDate = {key:'engagement.endDate',   value:'', description:'Engagement Review end date'};


  function hasSetting(setting){
    return $scope.settings
    .some(function(s){
      return s.key === setting.key;
    });
  }

  $scope.save = function(setting){
    AdminSettingService
    .create(setting)
    .$promise
    .then(function(){_showToast('Saved!');});
  };

  $scope.delete = function(setting){
    console.log(setting);
    AdminSettingService
    .delete({key:setting.key}, function(){_showToast('Deleted!');});
  };


  function _showToast(msg){
    $mdToast
    .show($mdToast.simple()
       .textContent(msg)
       .hideDelay(3000));
  }

  function saveDefault(setting){
    if(!hasSetting(setting)){
      $scope.save(setting);
    }
  }


  AdminSettingService
  .get()
  .$promise
  .then(function(response){
    $scope.settings = response;
    saveDefault($scope.erEndDate);
    saveDefault($scope.erStartDate);
    // if setting is stored server-side, use it
    response.forEach(function(setting) {
      if(setting.key === $scope.erStartDate.key){
        $scope.erStartDate = setting;
      }
      else if(setting.key === $scope.erEndDate.key){
        $scope.erEndDate = setting;
      }
    });
    $scope.erEndDate.value = new Date($scope.erEndDate.value);
    $scope.erStartDate.value = new Date($scope.erStartDate.value);


  });
});
