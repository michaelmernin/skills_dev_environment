'use strict';

angular.module('etmApp').controller('OverallController', function ($scope, $mdDialog, $mdMedia, $window, Principal, Review, Feedback, Rating, Evaluation) {

  $scope.getCoreAvgScore = function (property) {
    var ratings = Evaluation.getCoreAverage(property);
    return Evaluation.avgScore(ratings);
  };

  $scope.getInternalAvgScore = function (property) {
	    var ratings = Evaluation.getInternalAverage(property);
	    return Evaluation.avgScore(ratings);
  };

  
  $scope.getOverall = function (property) {
	    return (this.getCoreAvgScore(property) + this.getInternalAvgScore(property))/2;
};
 

});
