Web.Controllers.CarsController = function ($rootScope, $scope, $http, $mdDialog, notificationsService, contractConverter, settingsProvider, carsService, sessionManager) {
    var initList = function () {
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
                    for (var i = 0; i < data.length; i++) {
                        var toAdd = contractConverter.convertCarToViewModel(data[i]);
                        $scope.viewModel.cars.push(toAdd);
                    }
                    //callback(cars);
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
    }

    $scope.actions = new Object();
    $scope.actions.deleteSelectedCar = function () {
        if ($scope.viewModel.selectedItem != null) {
            carsService.deleteCar($scope.viewModel.selectedItem.id, function(){
                $scope.viewModel.selectedItem = null;
                notificationsService.showSimple("CARS.DELETE_SUCCESS");
            }, function(){
                notificationsService.showSimple("CARS.DELETE_FAIL");
            });
        }
    };

    $scope.actions.showAddCarDialog = function (ev) {
        $scope.viewModel.addCar = new Web.ViewModels.CarViewModel();
        $mdDialog.show({
            contentElement: '#addCarDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };

    $scope.actions.addCar = function () {
        carsService.createCar($scope.viewModel.addCar
            , function(isSuccess, errors){
                if (isSuccess){
                    notificationsService.showSimple("CARS.NEW_CREATED");
                    $scope.viewModel.selectedItem = null;
                    initList();
                } else {
                    notificationsService.showSimple("CARS.UNKNOWN_ERROR");
                }
            },function(errors){
                notificationsService.showSimple("CARS.UNKNOWN_ERROR");
            });
        $scope.viewModel.addCar = null;
        $mdDialog.cancel();
    }

    $scope.actions.editSelectedCar = function (ev) {
        $scope.viewModel.addCar = $scope.viewModel.selectedItem;
        $mdDialog.show({
            contentElement: '#addCarDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    }

    $scope.actions.cancelAddCar = function () {
        $mdDialog.cancel();
    }

    $scope.setSelected = function(item) {
        $scope.viewModel.selectedItem = item;
    }

    $rootScope.pageSubtitle = "CARS.PAGE_SUBTITLE";
    $scope.carsList = $('#cars_list');
    $scope.viewModel = new Web.ViewModels.CarViewModel();//new Object();
    $scope.viewModel.cars = [];
    $scope.viewModel.selectedItem = null;
    $scope.viewModel.addCar = null;
    initList();
}

angular.module('CarParSystemWebApp').controller('CarsController', ['$rootScope', '$scope', '$http', '$mdDialog', 'notificationsService', 'contractConverter', 'settingsProvider', 'carsService', 'sessionManager', Web.Controllers.CarsController]);