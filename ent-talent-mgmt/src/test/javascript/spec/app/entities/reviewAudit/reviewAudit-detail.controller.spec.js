'use strict';

describe('ReviewAudit Detail Controller', function () {
    var $scope, $rootScope;
    var MockEntity, MockReviewAudit, MockReview, MockUser;
    var createController;

    beforeEach(inject(function ($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockReviewAudit = jasmine.createSpy('MockReviewAudit');
        MockReview = jasmine.createSpy('MockReview');
        MockUser = jasmine.createSpy('MockUser');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'ReviewAudit': MockReviewAudit,
            'Review': MockReview,
            'User': MockUser
        };
        createController = function () {
            $injector.get('$controller')("ReviewAuditDetailController", locals);
        };
    }));


    xdescribe('Root Scope Listening', function () {
        it('Unregisters root scope listener upon scope destruction', function () {
            var eventType = 'etmApp:reviewAuditUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
