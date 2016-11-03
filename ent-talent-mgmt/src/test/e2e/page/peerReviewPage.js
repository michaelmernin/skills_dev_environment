'use strict';


var PeerReviewPage = function () {
  this.ui = {

    peerTabContainer: element(by.css('md-tab-item:nth-child(1)')),
    peerSearchInput: element(by.css('md-tab-content#tab-content-1')).element(by.model('$mdAutocompleteCtrl.scope.searchText')),

    removePeerModaldialog: element(by.tagName('md-dialog')),
    cancelButton: element(by.tagName('md-dialog-actions')).element(by.cssContainingText('button', 'Cancel')),
    removeButton: element(by.tagName('md-dialog-actions')).element(by.cssContainingText('button', 'Remove')),
    
    modalDialogTitle: element(by.tagName('md-dialog-content')).element(by.css('.md-title')),
    modalDialogDescription: element(by.tagName('md-dialog-content')).element(by.css('.md-dialog-content-body')),


  };

    Object.defineProperties(this, {

    peerSearchInput: {
    get: function(){
    return this.ui.peerSearchInput.getAttribute('value');
  }
}

});

this.get = function(PeerReviewPage){
  this.ui.peerTabContainer.click();
};

this.search = function (searchInput) {
  this.ui.peerSearchInput.clear();
  this.ui.peerSearchInput.sendKeys(searchInput);
};

this.getPeerOptions = function(searchKey){
  this.ui.peerSearchInput.clear();
  this.ui.peerSearchInput.sendKeys(searchKey);
  var peerListContainer = element(by.css('.md-autocomplete-suggestions'));
  browser.wait(function() {
    return browser.isElementPresent(peerListContainer);
  }, 10000);
  var peers = [];
  peers = peerListContainer.all(by.tagName('li'));
  return peers;
};

this.selectPeer = function(peerName){
  var peerListContainer = element(by.css('.md-autocomplete-suggestions'));
 //var desiredPeer = peerListContainer.element(by.tagName('li')).element(by.cssContainingText('md-autocomplete-parent-scope', peerName));
  
  var desiredPeer = peerListContainer.element(by.cssContainingText('li', peerName));
  desiredPeer.click();
};
  
this.getSelectedPeer = function(itemNumber){
  var listedPeerContainer = element(by.css('md-tab-content:nth-child(1)'));
  var listedPeers = [];
  listedPeers = listedPeerContainer.all(by.tagName('md-list-item'));
  var selectedPeer= listedPeers.get(itemNumber);
  return selectedPeer;  
  
};

this.deletePeer = function(peerName){
  var deleteButton = element(by.cssContainingText('md-list-item', peerName)).element(by.tagName('button'));
  deleteButton.click();
  
  var confirmButton = this.ui.removeButton;
  confirmButton.click();
  

};

};
module.exports = PeerReviewPage;
