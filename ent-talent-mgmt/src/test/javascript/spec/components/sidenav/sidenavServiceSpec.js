'use strict';

describe('Services Tests ', function () {

  beforeEach(module('etmApp'));

  describe('Sidenav', function () {
    var $mdUtil, $mdSidenav, Sidenav, toggle;

    beforeEach(function () {
      $mdUtil = {
        debounce: jasmine.createSpy().and.callFake(function (callback) {
          return callback;
        })
      };

      toggle = jasmine.createSpy();
      $mdSidenav = jasmine.createSpy().and.returnValue({
        toggle: toggle
      });

      module(function ($provide) {
        $provide.value('$mdUtil', $mdUtil);
        $provide.value('$mdSidenav', $mdSidenav);
      });

      inject(function ($injector, _Sidenav_) {
        Sidenav = _Sidenav_;
      });
    });

    it('should call toggle right nav', function () {
      Sidenav.toggle();

      expect($mdUtil.debounce).toHaveBeenCalled();
      expect($mdUtil.debounce.calls.count()).toEqual(1);
      expect($mdSidenav).toHaveBeenCalled();
      expect($mdSidenav.calls.count()).toEqual(1);
      expect($mdSidenav.calls.argsFor(0)).toEqual(['right-nav']);
      expect(toggle).toHaveBeenCalled();
      expect(toggle.calls.count()).toEqual(1);
    });

  });
});
