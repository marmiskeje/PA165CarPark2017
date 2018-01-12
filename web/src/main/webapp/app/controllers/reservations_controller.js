Web.Controllers.ReservationsController = function ($rootScope, $scope, $mdDialog, notificationsService, contractConverter, settingsProvider, reservationsService, carsService, sessionManager) {
    var initCalendar = function () {
        $scope.calendarElement.fullCalendar('destroy');
        $scope.calendarElement.fullCalendar({
            schedulerLicenseKey: 'GPL-My-Project-Is-Open-Source',
            defaultView: 'agendaDay',
            timezone: 'local',
            defaultDate: new Date().toISOString(),
            locale: settingsProvider.currentLanguage,
            editable: false,
            selectable: true,
            unselectAuto: true,
            eventLimit: true, // allow "more" link when too many events
            header: {
                left: '',
                center: 'title',
                right: 'prev,next today'
            },
            views: {
                agendaTwoDay: {
                    type: 'agenda',
                    duration: { days: 2 },
                    groupByResource: true
                }
            },
            refetchResourcesOnNavigate: true,
            allDaySlot: false,
            events: function (start, end, timezone, callback) {
                var request = new Web.Data.GetReservationsRequest();
                request.branchId = sessionManager.currentSession.branchId;
                request.dateFrom = new Date(start);
                request.dateTo = new Date(end);
                var fillInCalendar = function (httpResponse) {
                    var response = httpResponse.data;
                    if (response != null) {
                        var data = response.data;
                        if (response.isSuccess && data != null) {
                            $scope.viewModel.reservations = [];
                            var eventsForCalendar = []
                            for (var i = 0; i < data.length; i++) {
                                var toAdd = contractConverter.convertReservationToViewModel(data[i]);
                                $scope.viewModel.reservations.push(toAdd);
                                if(toAdd.user.id!=sessionManager.currentSession.userId & sessionManager.currentSession.userType == 'USER'){
                                    eventsForCalendar.push(toAdd.convertToSecretEvent()); //another user reservation and user is not admin or manager
                                }else{
                                    eventsForCalendar.push(toAdd.convertToEvent());
                                }
                            }
                            callback(eventsForCalendar);
                            $scope.calendarElement.fullCalendar('refetchResources');
                        } else {
                            notificationsService.showSimple("RESERVATIONS.UNKNOWN_ERROR");
                        }
                    } else {
                        notificationsService.showSimple("RESERVATIONS.UNKNOWN_ERROR");
                    }
                    $scope.viewModel.selectedEvent = null;
                    //$scope.$digest();
                };
                var handleError = function (httpResponse) {
                    $scope.viewModel.selectedEvent = null;
                    notificationsService.showSimple("RESERVATIONS.UNKNOWN_SERVER_ERROR");
                };

                switch(sessionManager.currentSession.userType) {
                    case 'ADMIN':
                        reservationsService.getAllReservations(request, fillInCalendar, handleError);
                        break;
                    case 'BRANCH_MANAGER':
                        reservationsService.getReservations(request, fillInCalendar, handleError);
                        break;
                    case 'USER':
                        reservationsService.getReservations(request, fillInCalendar, handleError);
                        break;
                    default:
                        handleError();
                }
            },
            resources: function (callback) {
                var request = new Web.Data.GetCarsRequest(sessionManager);
                if(sessionManager.currentSession.userType == 'ADMIN'){
                    request.getAllCars = true;
                }
                carsService.getCars(request, function (httpResponse) {
                    var response = httpResponse.data;
                    if (response != null) {
                        var data = response.data;
                        if (response.isSuccess && data != null) {
                            $scope.viewModel.cars = [];
                            var carsForCalendar = []
                            for (var i = 0; i < data.length; i++) {
                                var toAdd = contractConverter.convertCarToViewModel(data[i]);
                                $scope.viewModel.cars.push(toAdd);
                                carsForCalendar.push(toAdd.convertToResource());
                            }
                            callback(carsForCalendar);
                        } else {
                            notificationsService.showSimple("RESERVATIONS.UNKNOWN_ERROR");
                        }
                    } else {
                        notificationsService.showSimple("RESERVATIONS.UNKNOWN_ERROR");
                    }
                    //$scope.$digest();
                }, function (httpResponse) {
                    notificationsService.showSimple("RESERVATIONS.UNKNOWN_SERVER_ERROR");
                });
            },
            select: function (start, end, jsEvent, view, resource) {
                if ($scope.viewModel.selectedEvent != null) {
                    updateEventColor($scope.viewModel.selectedEvent, $scope.viewModel.selectedEvent.notSelectedColor);
                }
                $scope.viewModel.selectedEvent = null;
                $scope.$digest();
                $scope.actions.showNewReservationDialog(null, new Date(start), new Date(end), resource.source);
                console.log('select', start.format(), end.format(), resource ? resource.id : '(no resource)' );
            },
            unselect: function (jsEvent, view) {
                if ($scope.viewModel.selectedEvent != null) {
                    updateEventColor($scope.viewModel.selectedEvent, $scope.viewModel.selectedEvent.notSelectedColor);
                }
                $scope.viewModel.selectedEvent = null;
                $scope.$digest();
                console.log('unselect');
            },
            eventClick: function (calEvent, jsEvent, view) {
                if ($scope.viewModel.selectedEvent != null) {
                    updateEventColor($scope.viewModel.selectedEvent, $scope.viewModel.selectedEvent.notSelectedColor);
                }
                if(calEvent.isSecret){ //only info event, user cannot click
                    return;
                }
                $scope.viewModel.selectedEvent = calEvent;
                updateEventColor(calEvent, "red");
                $scope.$digest();
                console.log('eventClick', calEvent.title);
            }
        });
    }

    var updateEventColor = function (calEvent, color) {
        calEvent.color = color;
        $scope.calendarElement.fullCalendar('updateEvent', calEvent);
    }

    $scope.actions = new Object();
    $scope.actions.deleteSelectedReservation = function () {
        if ($scope.viewModel.selectedEvent != null) {
            reservationsService.deleteReservation($scope.viewModel.selectedEvent.id, function(){
            var selectedEvent = $scope.viewModel.selectedEvent;
            $scope.calendarElement.fullCalendar('removeEvents', selectedEvent.id);
            $scope.viewModel.selectedEvent = null;
            notificationsService.showSimple("RESERVATIONS.DELETE_SUCCESS");
            }, function(){
                notificationsService.showSimple("RESERVATIONS.DELETE_FAIL");
            });
        }
    };
    $scope.actions.showNewReservationDialog = function (ev, startDate, endDate, car) {
        $scope.viewModel.newReservation = new Web.ViewModels.ReservationViewModel();
        if (typeof startDate !== "undefined") {
            $scope.viewModel.newReservation.startDate = startDate;
        }
        if (typeof endDate !== "undefined") {
            $scope.viewModel.newReservation.endDate = endDate;
        }
        if (typeof car !== "undefined") {
            $scope.viewModel.newReservation.car = car;
        }
        $scope.actions.validateNewReservation($scope.newReservationDialogFrom);
        $mdDialog.show({
            contentElement: '#newReservationDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    $scope.actions.createNewReservation = function () {
        reservationsService.createReservation(
                {
                    car: { id: $scope.viewModel.newReservation.car.id },
                    user: { id: sessionManager.currentSession.userId },
                    reservationStartDate: new Date($scope.viewModel.newReservation.startDate.toUTCString()),
                    reservationEndDate: new Date($scope.viewModel.newReservation.endDate.toUTCString())
                }, function(isSuccess, errors){
                    if (isSuccess){
                        notificationsService.showSimple("RESERVATIONS.NEW_CREATED");
                        $scope.calendarElement.fullCalendar('refetchEvents');
                    } else {
                        notificationsService.showSimple(contractConverter.convertReservationErrors(errors));
                    }
                },function(errors){
                    notificationsService.showSimple(contractConverter.convertReservationErrors(errors));
                });
        $mdDialog.cancel();
    }
    $scope.actions.cancelNewReservation = function () {
        $mdDialog.cancel();
    }
    $scope.actions.validateNewReservation = function(form){
        if ($scope.viewModel.newReservation.startDate){
            form.newReservationStartDate.$setValidity('required', true);
            form.newReservationStartDate.$setValidity('startDateGreaterThenEndDate', $scope.viewModel.newReservation.startDate <= $scope.viewModel.newReservation.endDate);
            form.newReservationStartDate.$setPristine();
        } else {
            form.newReservationStartDate.$setValidity('required', false);
        }
        if ($scope.viewModel.newReservation.endDate){
            form.newReservationEndDate.$setValidity('required', true);
            form.newReservationEndDate.$setValidity('startDateGreaterThenEndDate', $scope.viewModel.newReservation.startDate <= $scope.viewModel.newReservation.endDate);
        } else {
            form.newReservationEndDate.$setValidity('required', false);
        }
        if ($scope.viewModel.newReservation.car){
            form.newReservationCar.$setValidity('required', true);
        }else {
            form.newReservationCar.$setValidity('required', false);
        }
    }
    $scope.actions.updateSelectedReservationState = function(state){
        if ($scope.viewModel.selectedEvent != null) {
            var selectedEvent = $scope.viewModel.selectedEvent;
            reservationsService.updateReservation(
                {
                    id: selectedEvent.id,
                    car: { id: selectedEvent.car.id },
                    user: { id: selectedEvent.user.id },
                    reservationStartDate: new Date(new Date(selectedEvent.start).toUTCString()),
                    reservationEndDate: new Date(new Date(selectedEvent.end).toUTCString()),
                    state: state
                }, function(isSuccess, errors){
                    if (isSuccess){
                        var message = "RESERVATIONS.UPDATED";
                        if (state === 'APPROVED' || state === 'DENIED'){
                            message = "RESERVATIONS." + state;
                        }
                        notificationsService.showSimple(message);
                        $scope.calendarElement.fullCalendar('refetchEvents');
                    } else {
                        notificationsService.showSimple(contractConverter.convertReservationErrors(errors));
                    }
                },function(errors){
                    notificationsService.showSimple(contractConverter.convertReservationErrors(errors));
                });
        }
    }

    $rootScope.pageSubtitle = "RESERVATIONS.PAGE_SUBTITLE";
    $scope.calendarElement = $('#reservations_calendar');
    $scope.viewModel = new Object();
    $scope.viewModel.cars = [];
    $scope.viewModel.reservations = [];
    $scope.viewModel.selectedEvent = null;
    $scope.viewModel.newReservation = null;
    initCalendar();
}

angular.module('CarParSystemWebApp').controller('ReservationsController', ['$rootScope', '$scope', '$mdDialog', 'notificationsService', 'contractConverter', 'settingsProvider', 'reservationsService', 'carsService', 'sessionManager', Web.Controllers.ReservationsController]);