'use strict';

angular.module('etmApp').factory('Todo', function ($resource, DateUtils, ReviewStatus) {
  function convertFromServer(data) {
    data.dueDate = DateUtils.convertLocaleDateFromServer(data.dueDate);
    return data;
  }

  function convertToServer(data) {
    if (data.dueDate) {
      data.dueDate = DateUtils.convertLocaleDateToServer(data.dueDate);
    }
    return data;
  }

  return angular.extend($resource('api/todos/:id', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    },
    'update': {
      method:'PUT',
      transformRequest: function (data) {
        return angular.toJson(convertToServer({
          todoId: data.todoId,
          result: data.result
        }));
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
            confirm: 'Confirm Feedback Submission?',
            todoId: todo.id,
            result: 'SUBMIT',
            theme: '',
            icon: 'fa-send'
          });
          break;
        default:
          actions.push({
            name: 'Approve',
            confirm: 'Confirm Review Approval?',
            todoId: todo.id,
            result: 'APPROVE',
            theme: '',
            icon: 'fa-check'
          }, {
            name: 'Reject',
            confirm: 'Confirm Review Rejection?',
            todoId: todo.id,
            result: 'REJECT',
            theme: 'md-warn',
            icon: 'fa-exclamation'
          });
          break;
        }
      }
      return actions;
    }
  });
});
