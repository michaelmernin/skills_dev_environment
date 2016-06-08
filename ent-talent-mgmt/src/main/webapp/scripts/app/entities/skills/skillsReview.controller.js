'use strict';

angular.module('etmApp').controller('SkillsReviewController', function ($scope, $mdDialog,$mdToast,$q, SkillCategory, Skill, User) { 

  $scope.skillCategories = [];

  var displaySkillCategories = function() {
    SkillCategory.skillsReview(function (result) {
      $scope.skillCategories = result;
    });
  };

  displaySkillCategories();

  $scope.toggleCategory = function (category) {
    $scope.toggle = ($scope.toggle === category ? null : category);

    var users = [];
    var data = [];
    var skillCategory = [];
    skillCategory = category;
    var skillsArray = [];
    var rankings = skillCategory.skills[0] != null? skillCategory.skills[0].rankings : [];

    //populating skills array for x-axis
    for(var i=0;i<skillCategory.skills.length;i++){
      skillsArray[i]=skillCategory.skills[i].name;
    }

    //populating users array for y-axis
    for(var i=0;i<rankings.length;i++){
      users[i]=rankings[i].user.login;
    }
    
    //populating data array
    var count = 0;
    for(var i=0; i< skillCategory.skills.length;i++){
      for(var k=0;k<users.length;k++){
        if(skillCategory.skills != null){
          var temp =[];
          temp.push(i, k, skillCategory.skills[i].rankings[k].rank);
          data[count++]= temp;
        }
      }
    }
    
    //render highcharts in the div with id as skillCategory id
    var title = "#"+skillCategory.id;
    $(title).highcharts({

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

      /* Gradient color axis
       * colorAxis: {
        min: 0,
        minColor: '#FFFFFF',
        maxColor: Highcharts.getOptions().colors[0]
      },
      */

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
  };
});
