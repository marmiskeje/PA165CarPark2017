Web.Controllers.BranchesController = function ($rootScope, $scope, $http, $mdDialog, notificationsService, contractConverter, settingsProvider, carService, branchesService, sessionManager) {
    var initList = function () {
        var request = new Web.Data.GetBranchesRequest();
        branchesService.getBranches(request, function (httpResponse) {
            var response = httpResponse.data;
            if (response !== null) {
                var data = response.data;
                if (response.isSuccess && data !== null) {
                    $scope.viewModel.branches = [];
                    for (var i = 0; i < data.length; i++) {
                        var toAdd = contractConverter.convertBranchToViewModel(data[i]);
                        angular.forEach(toAdd.employees, function(employee){
                            if (employee.userType === "BRANCH_MANAGER") 
                                toAdd.manager = employee;
                        })
                        $scope.viewModel.branches.push(toAdd);
                    }
                } else {
                    notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                }
            } else {
                notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
            }
            $scope.viewModel.selectedEvent = null;
        }, function (httpResponse) {
            $scope.viewModel.selectedEvent = null;
            notificationsService.showSimple("BRANCHES.UNKNOWN_SERVER_ERROR");
        });
        var requestUsers = new Web.Data.GetUsersRequest();
        branchesService.getAllUsers(requestUsers, function (httpResponse) {
            var response = httpResponse.data;
            if (response !== null) {
                var data = response.data;
                if (response.isSuccess && data !== null) {
                    $scope.viewModel.users = [];
                    for (var i = 0; i < data.length; i++) {
                        var toAdd = contractConverter.convertUserToViewModel(data[i]);
                        $scope.viewModel.users.push(toAdd);
                    }
                } else {
                    notificationsService.showSimple("USERS.UNKNOWN_ERROR");
                }
            } else {
                notificationsService.showSimple("USERS.UNKNOWN_ERROR");
            }
            $scope.viewModel.selectedEvent = null;
        }, function (httpResponse) {
            $scope.viewModel.selectedEvent = null;
            notificationsService.showSimple("USERS.UNKNOWN_SERVER_ERROR");
        });
        var request = new Web.Data.GetCarsRequest(sessionManager);
        if(sessionManager.currentSession.userType == 'ADMIN'){
            request.getAllCars = true;
        }
            carService.getCars(request, function (httpResponse) {
                var response = httpResponse.data;
                if (response !== null) {
                    var data = response.data;
                    if (response.isSuccess && data !== null) {
                        $scope.viewModel.cars = [];
                        var cars = []
                        for (var i = 0; i < data.length; i++) {
                            var toAdd = contractConverter.convertCarToViewModel(data[i]);
                            $scope.viewModel.cars.push(toAdd);
                        }
                        //callback(cars);
                    } else {
                        notificationsService.showSimple("CARS.UNKNOWN_ERROR");
                    }
                } else {
                    notificationsService.showSimple("CARS.UNKNOWN_ERROR");
                }
            }, function (httpResponse) {
                notificationsService.showSimple("CARS.UNKNOWN_SERVER_ERROR");
        });
        $scope.viewModel.selectedItem = null;
    }

    $scope.actions = new Object();
    $scope.actions.deleteSelectedRegionalBranch = function () {
        if ($scope.viewModel.selectedItem !== null) {
            var selectedEvent = $scope.viewModel.selectedItem;
            var selectedHashKey = $scope.viewModel.selectedItem.$$hashKey;
            for(var i = 0; i < $scope.viewModel.branches.length; i++) {
                if ($scope.viewModel.branches[i].$$hashKey === selectedHashKey) {
                    var request = {id: $scope.viewModel.selectedItem.id};
                    branchesService.deleteBranch(request, function (httpResponse) {
                        var response = httpResponse.data;
                        if (response !== null) {
                            var data = response.data;
                            if (response.isSuccess && data !== null) {
                                $scope.viewModel.branches.splice(i, 1);
                            } else {
                                notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                            }
                        } else {
                            notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                        }
                    }, function (httpResponse) {
                        notificationsService.showSimple("BRANCHES.UNKNOWN_SERVER_ERROR");
                    });
                }
            }
            $scope.viewModel.selectedItem = null;
            $scope.viewModel.manager = null;
        }
    };
    
    $scope.actions.showAddBranchDialog = function (ev) {
        $scope.viewModel.isBeingAdded = true;
        $scope.viewModel.addBranch = new Web.ViewModels.BranchesViewModel();
        angular.forEach($scope.viewModel.cars, function(car){
            car.selected = false;
        })
        $mdDialog.show({
            contentElement: '#addBranchDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.actions.openAssignCar = function (ev) {
        $scope.viewModel.carToAssign = new Web.ViewModels.CarViewModel();
        $mdDialog.show({
            contentElement: '#assignCarDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.actions.openAssignUser = function (ev) {
        $scope.viewModel.userToAssign = new Web.ViewModels.UserViewModel();
        $mdDialog.show({
            contentElement: '#assignUserDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.actions.assignUser = function () {
        var branchToUpdate = $scope.viewModel.selectedItem;
        var userToBeAssigned = $scope.viewModel.userToAssign;
        var request = {id: $scope.viewModel.selectedItem.id, user: $scope.viewModel.userToAssign};
        branchesService.assignUser(request, function (httpResponse) {
            var response = httpResponse.data;
            if (response !== null) {
                var data = response.data;
                if (response.isSuccess && data !== null) {
                    angular.forEach($scope.viewModel.branches, function(branch){
                        if (branch.id === $scope.viewModel.selectedItem.id) {
                            branch.employees.push(userToBeAssigned);
                            $scope.viewModel.selectedItem = branchToUpdate;
                        }   
                    });
                } else {
                    notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                }
            } else {
                notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
            }
        }, function (httpResponse) {
            notificationsService.showSimple("BRANCHES.UNKNOWN_SERVER_ERROR");
        });
        $scope.viewModel.userToAssign = null;
        $mdDialog.cancel();
    };
    
    $scope.actions.assignCar = function () {
        var branchToUpdate = $scope.viewModel.selectedItem;
        var carToBeAssigned = $scope.viewModel.carToAssign;
        var request = {id: $scope.viewModel.selectedItem.id, car: carToBeAssigned};
        branchesService.assignCar(request, function (httpResponse) {
            var response = httpResponse.data;
            if (response !== null) {
                var data = response.data;
                if (response.isSuccess && data !== null) {
                    angular.forEach($scope.viewModel.branches, function(branch){
                        if (branch.id === $scope.viewModel.selectedItem.id) {
                            branch.cars.push(carToBeAssigned);
                            $scope.viewModel.selectedItem = branchToUpdate;
                        }   
                    });
                } else {
                    notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                }
            } else {
                notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
            }
        }, function (httpResponse) {
            notificationsService.showSimple("BRANCHES.UNKNOWN_SERVER_ERROR");
        });
        $scope.viewModel.carToAssign = null;
        $mdDialog.cancel();
    };
    
    $scope.actions.addBranch = function () {
        var managerToSet = $scope.viewModel.selectedItem.manager;
        var selectedCars = [];
        var employees = [];
        employees.push(managerToSet);
        angular.forEach($scope.viewModel.cars, function(car){
            if (car.selected) selectedCars.push(car);
        })
        
        var request = {id: $scope.viewModel.addBranch.id, name: $scope.viewModel.addBranch.name, cars: selectedCars, employees: employees};
        branchesService.createBranch(request, function (httpResponse) {
            var response = httpResponse.data;
            if (response !== null) {
                var data = response.data;
                if (response.isSuccess && data !== null) {
                    $scope.viewModel.branches.push(data.data);
                    $scope.viewModel.selectedItem = branches[branches.length - 1];
                    data.data.manager = managerToSet;
                } else {
                    notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                }
            } else {
                notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
            }
        }, function (httpResponse) {
            notificationsService.showSimple("BRANCHES.UNKNOWN_SERVER_ERROR");
        });
        $scope.viewModel.addBranch = null;
        $scope.viewModel.manager = null;
        $scope.viewModel.isBeingEdited = false;
        $scope.viewModel.isBeingAdded = false;
        $mdDialog.cancel();
    }
    
    $scope.actions.updateBranch = function () {
        var managerToSet = $scope.viewModel.selectedItem.manager;
        var selectedCars = [];
        var employees = [];
        angular.forEach($scope.viewModel.selectedItem.employees, function(employee){
            if (employee.userType !== "BRANCH_MANAGER") employees.push(employee);
        })
        employees.push(managerToSet);
        angular.forEach($scope.viewModel.cars, function(car){
            if (car.selected) selectedCars.push(car);
        })
        var request = {id: $scope.viewModel.addBranch.id, name: $scope.viewModel.addBranch.name, cars: selectedCars, employees: employees};
        branchesService.updateBranch(request, function (httpResponse) {
            var response = httpResponse.data;
            if (response !== null) {
                var data = response.data;
                if (response.isSuccess && data !== null) {
                    angular.forEach($scope.viewModel.branches, function(branch){
                        if (branch.id === $scope.viewModel.selectedItem.id) {
                            branch.cars = [];
                            branch.manager = managerToSet;
                            
                        angular.forEach($scope.viewModel.cars, function(car){
                            if (car.selected) {
                                branch.cars.push(car);
                            };
                        });
                        $scope.viewModel.selectedItem = branch;
                        }   
                    });
                } else {
                    notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
                }
            } else {
                notificationsService.showSimple("BRANCHES.UNKNOWN_ERROR");
            }
        }, function (httpResponse) {
            notificationsService.showSimple("BRANCHES.UNKNOWN_SERVER_ERROR");
        });
        $scope.viewModel.addBranch = null;
        $scope.viewModel.manager = null;
        $scope.viewModel.isBeingEdited = false;
        $scope.viewModel.isBeingAdded = false;
        $mdDialog.cancel();
    }
    
    $scope.actions.editSelectedBranch = function (ev) {
        $scope.viewModel.isBeingEdited = true;
        $scope.viewModel.addBranch = $scope.viewModel.selectedItem;
        $mdDialog.show({
            contentElement: '#addBranchDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    }
    
    $scope.actions.cancelAddBranch = function () {
        $mdDialog.cancel();
        $scope.viewModel.selectedItem = null;
        $scope.viewModel.manager = null;
        $scope.viewModel.carToAssign = null;
        $scope.viewModel.userToAssign = null;
        $scope.viewModel.addBranch = null;
        $scope.viewModel.isBeingEdited = false;
        $scope.viewModel.isBeingAdded = false;
        angular.forEach($scope.viewModel.cars, function(car){
            car.selected = false;
        })
    }
    
    $scope.setSelected = function(item) {
        $scope.viewModel.selectedItem = item;
        angular.forEach($scope.viewModel.cars, function(car){
            car.selected = false;
            angular.forEach(item.cars, function(itemCar){
                if (car.id === itemCar.id) 
                    car.selected = true;
            })
        })
        angular.forEach(item.employees, function(employee){
            if (employee.userType === "BRANCH_MANAGER") {
                $scope.viewModel.manager = employee;
            }
        })
    }
    
    $rootScope.pageSubtitle = "BRANCHES.PAGE_SUBTITLE";
    $scope.branchesList = $('#branches_list');
    $scope.viewModel = new Web.ViewModels.BranchesViewModel();
    $scope.viewModel.branches = [];
    $scope.viewModel.users = [];
    $scope.viewModel.cars = [];
    
    $scope.viewModel.selectedItem = null;
    $scope.viewModel.manager = null;
    $scope.viewModel.carToAssign = null;
    $scope.viewModel.userToAssign = null;
    
    $scope.viewModel.addBranch = null;
    $scope.viewModel.isBeingAdded = false;
    $scope.viewModel.isBeingEdited = false;
    initList();
}
angular.module('CarParSystemWebApp').controller('BranchesController', ['$rootScope', '$scope', '$http', '$mdDialog', 'notificationsService', 'contractConverter', 'settingsProvider', 'carsService', 'branchesService', 'sessionManager', Web.Controllers.BranchesController]);