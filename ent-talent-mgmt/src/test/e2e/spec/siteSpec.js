'use strict';

describe('Enterprise Talent Management', function () {
  it('should have a title', function () {
    browser.get('/');

    expect(browser.getTitle()).toEqual('Enterprise Talent Management');
  });
});
