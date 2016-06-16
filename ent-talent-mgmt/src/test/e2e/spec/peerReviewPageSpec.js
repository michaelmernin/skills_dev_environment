'use strict';

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var AnnualReviewPage = require('../page/annualReviewPage.js');
var PeerReviewPage = require('../page/peerReviewPage.js');

describe('Enterprise Talent Management', function () {
  describe('Peer Review Tab', function () {
    var annualReviewPage;
    var peerReviewPage;

    beforeAll(function () {
      var loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      annualReviewPage = new AnnualReviewPage();
      annualReviewPage.get(1);
      peerReviewPage = new PeerReviewPage();
      peerReviewPage.get();
    });


    it('should take input value', function () {
      peerReviewPage.search('value');

    });

    it('shoud give error response if search term does not match available peer', function(){
      var availablePeers = peerReviewPage.getPeerOptions('test');
      expect(availablePeers.count()).toBe(1);
      expect(availablePeers.get(0).getText()).toBe('No peers matching "test" were found.');

    });
    
    
    it('shoud give valid response if search term match available peer', function(){
      var availablePeers = peerReviewPage.getPeerOptions('dev');
      expect(availablePeers.count()).toBe(4);
      expect(availablePeers.get(1).getText()).toBe('Dev UserTwo');
      expect(availablePeers.get(2).getText()).toBe('Dev UserSeven');
      expect(availablePeers.get(3).getText()).toBe('Dev UserEight');
      expect(availablePeers.get(4).getText()).toBe('Dev UserNine');
    });
    
    it('shoud allow users to select a peer', function(){
      
      var availablePeers = peerReviewPage.getPeerOptions('dev');
      peerReviewPage.selectPeer('Dev UserSeven');
     
      var selectedPeer = peerReviewPage.getSelectedPeer();
      expect(selectedPeer.element(by.css('.peer-name')).getText()).toBe('Dev UserTwo');
      
    });
    
    it('should list selected peer', function(){
       
       
       var selectedPeer = peerReviewPage.getSelectedPeer(1);
       expect(selectedPeer.getText()).toContain('Dev UserSeven');
       
       
       });
   
    it('should allow users to delete a peer', function(){
      
     peerReviewPage.deletePeer('Dev UserSeven');
     
     expect(element(by.xpath('//md-tab-content[contains(@class, "active")]')).element(by.tagName('md-list-item')).element(by.cssContainingText('.peer-name', 'Dev UserSeven')).isPresent()).toBe(false);
    });
  });
});


