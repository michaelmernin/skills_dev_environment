'use strict';

angular.module('etmApp').controller('SkillsSearchChartsDetailController', function ($scope, $mdDialog, $mdToast, selectedSkills, Skill) {

  $scope.selectedSkills = angular.copy(selectedSkills);
  $scope.toggle = false;
  
  $scope.cancel = function () {
    $mdDialog.cancel();
  };
  

  
  var users = [];
  var data = [];
  var skillsArray = [];

  //populating skills array for x-axis
  angular.forEach($scope.selectedSkills, function (skill) {
    skillsArray.push(skill.name);
  });

  //populating users array for y-axis
  angular.forEach($scope.selectedSkills, function (skill) {
    angular.forEach(skill.rankings, function (ranking) {
     if(users.indexOf(ranking.user.login) == -1){
       users.push(ranking.user.login);
     }
    });
  });
  
  //populating data array
  var count =0;
  for(var i=0;i< selectedSkills.length;i++){
    for(var k=0; k<users.length;k++){
      var temp = [];
      var rank = 1;
      for(var j =0; j< selectedSkills[i].rankings.length; j++){
        if(selectedSkills[i].rankings[j].user.login == users[k]){
          rank = selectedSkills[i].rankings[j].rank;
        }
      }
      temp.push(i, k, rank);
      data[count++] = temp;
    }
  }
  
  //render highcharts heatmap
  
  $scope.showChart = function () {
    $scope.toggle = ($scope.toggle ? false : true);
    
    $('#container').highcharts({

      chart: {
        type: 'heatmap',
        marginTop: 40,
        marginBottom: 180,
        spacingRight: 0,
        plotBorderWidth: 1
      },

      title: {
        text: 'Skill Ranking per employee per skill'
      },

      xAxis: {
        categories: skillsArray,
        title: 'Skills',
        labels: {
          text: 'skills',
          autoRotation: [-50]
        },
        style: {
          "font-size": "9px"
        }
      },

      yAxis: {
        categories: users,
        title: 'Users',
        labels: {
          text: 'Users',
        },
      },

      colors: ['red','orange','yellow','green','blue'],

      colorAxis: {
        dataClasses: [{
          from: 1,
          to:  1,
          color:'red'
        }, {
          from: 2,
          to: 2,
          color:'orange'
        },
        {
          from: 3,
          to: 3,
          color:'yellow'
        }, {
          from: 4,
          to: 4,
          color:'#33CC33'
        }, {
          from: 5,
          to: 5,
          color:'green'
        }]
      },

      legend: {
        title: {
          style: {
            color: (Highcharts.theme && Highcharts.theme.textColor) || 'black'
          }
        },
        align: 'right',
        verticalAlign: 'bottom',
        layout: 'horizontal',
        valueDecimals: 0,
        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || 'rgba(255, 255, 255, 0.85)',
        symbolRadius: 0,
        symbolHeight: 14
      },

      tooltip: {
        formatter: function () {
          return '<b>' + this.series.yAxis.categories[this.point.y] + '</b> has rated <b>' +
          this.point.value + '<br></b> for Skill:  <br><b>' + this.series.xAxis.categories[this.point.x] + '</b>';
        }
      },

      credits:{
        enabled: false
      },

      series: [{
        name: 'Ranking per employee',
        borderWidth: 1,
        borderColor: '#505050',
        data: data,
        dataLabels: {
          enabled: true,
          color: '#000000'
        }
      }]

    });
  }
  
});
