'use strict';

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var SidebarPage = require('../page/sidebarPage.js');

describe('Enterprise Talent Management', function () {
  describe('Sidebar', function () {
    var sidebar;

    beforeAll(function () {
      var loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.admin);
      
      sidebar = new SidebarPage();
      sidebar.get();
    });

    it('should have a heading', function () {
      expect(sidebar.sidebarHeading).toEqual('Navigation');
    });
    
    it('should have 18 list items', function(){
    	expect(sidebar.sidebarElements.count()).toBe(18);
    });
    
    it('should go to Dashboard', function(){
    	expect(sidebar.getTextAndClick('global.menu.dashboard')).toBe('Dashboard');
    	expect(sidebar.getHeader('main.title')).toEqual('My Dashboard');
    });
    
    it('should go to Create a Review', function(){
    	expect(sidebar.getTextAndClick('global.menu.createReview')).toBe('Create a Review');
    	expect(sidebar.getHeader('review.new.title')).toEqual('Create a Review');
    });
    
    it('should go to Manage Users', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.users')).toBe('Manage Users');
    	expect(sidebar.getHeader('users.title')).toEqual('Manage Users');
    });
    
    it('should go to Status', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.status')).toBe('Status');
    	expect(sidebar.getHeader('status.title')).toEqual('Application Status');
    });
    
    it('should go to Health', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.health')).toBe('Health');
    	expect(sidebar.getHeader('health.title')).toEqual('Health checks');
    });
    
    it('should go to Configuration', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.configuration')).toBe('Configuration');
    	expect(sidebar.getHeader('configuration.title')).toEqual('Configuration');
    });
    
    it('should go to Audits', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.audits')).toBe('Audits');
    	expect(sidebar.getHeader('audits.title')).toEqual('Audits');
    });
    
    it('should go to Logs', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.logs')).toBe('Logs');
    	expect(sidebar.getHeader('logs.title')).toEqual('Logs');
    });
    
    it('should go to API Docs', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.apidocs')).toBe('API Docs');
    	expect(browser.getTitle()).toEqual('API Docs');
    });
    
    it('should go to Mail', function(){
    	expect(sidebar.getTextAndClick('global.menu.admin.mail')).toBe('Mail');
    	expect(browser.getTitle()).toEqual('Mail');
    });
    
    it('should go to Profile', function(){
    	expect(sidebar.getTextAndClick('global.menu.account.settings')).toBe('Profile');
    	expect(browser.getTitle()).toEqual('Profile');
    });

    it('should go to Log out', function(){
    	expect(sidebar.getTextAndClick('global.menu.account.logout')).toBe('Log out');
    	expect(sidebar.getHeader('login.title')).toEqual('Log In');
    });
    
    
  });
});
