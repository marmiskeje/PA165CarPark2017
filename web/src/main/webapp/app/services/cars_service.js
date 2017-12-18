﻿Web.Services.CarsService = function ($http) {
    this.getCars = function (request, onSuccess, onError) {
    	var urlCars = urlBase.concat("/car");     
        var response = {};
        var req = {
            method: 'POST',
            url: urlCars,
            headers: {
                'Content-Type': 'application/json'
            },
        }
    	$http(req)
    		.then(function(httpResponse) {
                response.data = httpResponse;
                response.data.isSuccess = true;
                onSuccess(response);
            }, function(httpResponse) {
                response.data = httpResponse;
                response.data.isSuccess = false;
                error(response);
		    });
    }

    this.deleteCar = function(id, onSuccess, onError){
        $http.delete(urlBase.concat("/car/" + id)).then(function(httpResponse){
            if (httpResponse.status === 200){
                onSuccess();
            } else {
                onError();
            }
        }, function(httpResponse){
            onError();
        });
    }

    this.getCars = function (request, onSuccess, onError) {
        var urlAllReservations = urlBase.concat("/car");
        var response = {};
        $http.get(urlAllReservations)
            .then(function(httpResponse) {
                response.data = httpResponse;
                response.data.isSuccess = true;
                onSuccess(response);
            }, function(httpResponse) {
                response.data = httpResponse;
                response.data.isSuccess = false;
                error(response);
            });
    }

    this.createCar = function(data, onSuccess, onError){
        var req = {
            method: 'POST',
            url: urlBase.concat("/car"),
            headers: {
                'Content-Type': 'application/json'
            },
            data: data
        }
        $http(req).then(function(httpResponse){
            if (httpResponse.status === 200){
                onSuccess(httpResponse.data.isSuccess, httpResponse.data.errorCodes);
            } else {
                onError(["CARS.UNKNOWN_ERROR"]);
            }
        }, function(httpResponse){
            onError(["CARS.UNKNOWN_ERROR"]);
        });
    }

    this.editSelectedCar = function(id, onSuccess, onError){
        $http.update(urlBase.concat("/car/" + id)).then(function(httpResponse){
            if (httpResponse.status === 200){
                onSuccess();
            } else {
                onError();
            }
        }, function(httpResponse){
            onError();
        });
    }
}
Web.App.service('carsService', ['$http', Web.Services.CarsService]);