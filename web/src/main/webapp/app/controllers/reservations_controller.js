﻿Web.Controllers.ReservationsController = function ($rootScope, $scope, $mdDialog, notificationsService, contractConverter, settingsProvider, reservationsService, carsService) {
    var initCalendar = function () {
        $scope.calendarElement.fullCalendar('destroy');
        $scope.calendarElement.fullCalendar({
            schedulerLicenseKey: 'GPL-My-Project-Is-Open-Source',
            defaultView: 'agendaDay',
            defaultDate: '2017-11-07',
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
                request.dateFrom = new Date(start);
                request.dateTo = new Date(end);
                reservationsService.getReservations(request, function (httpResponse) {
                    var response = httpResponse.data;
                    if (response != null) {
                        var data = response.data;
                        if (response.isSuccess && data != null) {
                            $scope.viewModel.reservations = [];
                            var eventsForCalendar = []
                            for (var i = 0; i < data.length; i++) {
                                var toAdd = contractConverter.convertReservationToViewModel(data[i]);
                                $scope.viewModel.reservations.push(toAdd);
                                eventsForCalendar.push(toAdd.convertToEvent());
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
                    $scope.$digest();
                }, function (httpResponse) {
                    $scope.viewModel.selectedEvent = null;
                    notificationsService.showSimple("RESERVATIONS.UNKNOWN_SERVER_ERROR");
                });
            },
            resources: function (callback) {
                var request = new Web.Data.GetCarsRequest();
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
                    $scope.$digest();
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
            var selectedEvent = $scope.viewModel.selectedEvent;
            $scope.calendarElement.fullCalendar('removeEvents', selectedEvent.id);
            $scope.viewModel.selectedEvent = null;
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
        $mdDialog.show({
            contentElement: '#newReservationDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    $scope.actions.createNewReservation = function () {
        $mdDialog.cancel();
    }
    $scope.actions.cancelNewReservation = function () {
        $mdDialog.cancel();
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

angular.module('CarParSystemWebApp').controller('ReservationsController', ['$rootScope', '$scope', '$mdDialog', 'notificationsService', 'contractConverter', 'settingsProvider', 'reservationsService', 'carsService', Web.Controllers.ReservationsController]);