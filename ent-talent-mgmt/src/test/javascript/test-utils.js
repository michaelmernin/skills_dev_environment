'use strict';

function stubInitRequests($httpBackend) {
  //Request on app init
  $httpBackend.whenGET('i18n/en/global.json').respond(200, '');
  $httpBackend.whenGET('i18n/en/language.json').respond(200, '');
  $httpBackend.whenGET('scripts/components/toolbar/toolbar.html').respond({});
  $httpBackend.whenGET('scripts/components/sidenav/sidenav.html').respond({});
  $httpBackend.whenGET('scripts/components/main/todo/todo-list.html').respond({});
  $httpBackend.whenGET('scripts/components/main/review/review-list.html').respond({});
  $httpBackend.whenGET('i18n/en/main.json').respond(200, '');
  $httpBackend.whenGET('scripts/app/main/main.html').respond({});
}
