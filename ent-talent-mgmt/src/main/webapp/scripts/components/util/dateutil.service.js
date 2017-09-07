'use strict';

angular.module('etmApp').service('DateUtils', function () {
  var self = this;
  this.convertLocaleDateToServer = function (date) {
    if (date) {
      var utcDate = new Date();
      utcDate.setUTCDate(date.getDate());
      utcDate.setUTCMonth(date.getMonth());
      utcDate.setUTCFullYear(date.getFullYear());
      return utcDate;
    } else {
      return null;
    }
  };

  /**
   * Transforms the properties with given names on the passed obj from server to local date
   * @param data the object/array to transform
   * @param the properties on obj to transform
   */
  this.covertDatePropertiesFromServer = function (data, properties)
  {
    if(!data) return data;
    if(!properties || !properties.length) console.error('cannot transform obj wit no property names');
    // if data is array
    if(data.length) {
      angular.forEach(data, function(item){
        item = self.covertDatePropertiesFromServer(item, properties);
      });
      return data;
    }
    // if data is object
    else {
      angular.forEach(properties, function(prop){
        // if obj is actually an array, 
        if (data.hasOwnProperty(prop)) {
          // convert the date from server
          data[prop] = self.convertLocaleDateFromServer(data[prop]);
        }
        else console.warn('object does not have property '+ prop, data);
      });
      return data;
    }
  };

  this.convertLocaleDateFromServer = function (date) {
    if (date) {
      var dateString = date.split("-");
      return new Date(dateString[0], dateString[1] - 1, dateString[2]);
    }
    return null;
  };

  this.convertDateTimeFromServer = function (date) {
    if (date) {
      return new Date(date);   
    } else {
      return null;
    }
  }
});
