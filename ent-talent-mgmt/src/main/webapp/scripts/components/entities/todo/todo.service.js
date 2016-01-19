'use strict';

angular.module('etmApp').factory('Todo', function ($resource, DateUtils, ReviewStatus) {
  function convertFromServer(data) {
    data.dueDate = DateUtils.convertLocaleDateFromServer(data.dueDate);
  }

  function convertToServer(data) {
    data.dueDate = DateUtils.convertLocaleDateToServer(data.dueDate);
  }

  return angular.extend($resource('api/todos', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    }
  }), {
    getActions: function (todo, review) {
      var actions = [];
      if (todo.id && review.reviewStatus && review.reviewStatus.id) {
        switch (review.reviewStatus.id) {
        case ReviewStatus.OPEN.id:
          // TODO if reviewer and reviewee feedback are both ready
          // Then action is Reviewer Approve/Reject
          actions.push({
            name: 'Submit Feedback',
            todoId: todo.id,
            result: 'SUCCESS',
            theme: '',
            icon: 'fa-send'
          });
          break;
        default:
          actions.push({
            name: 'Approve',
            todoId: todo.id,
            result: 'SUCCESS',
            theme: '',
            icon: 'fa-check'
          }, {
            name: 'Reject',
            todoId: todo.id,
            result: 'FAILURE',
            theme: 'md-warn',
            icon: 'fa-cross'
          });
          break;
        }
      }
      return actions;
    }
  });
});
