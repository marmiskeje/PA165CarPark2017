Web.Services.CarsService = function ($http) {
    this.getCars = function (request, onSuccess, onError) {
        var urlCars = urlBase.concat("/car");
        if(!request.getAllCars){
            urlCars = urlBase.concat("/car?branchId=" + request.branchId);
        }
        var response = {};
        $http.get(urlCars)
            .then(function(httpResponse) {
                response.data = httpResponse;
                response.data.isSuccess = true;
                onSuccess(response);
            }, function(httpResponse) {
                response.data = httpResponse;
                response.data.isSuccess = false;
                onError(response);
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

    this.editSelectedCar = function(data, onSuccess, onError) {
        var req = {
            method: 'PUT',
            url: urlBase.concat("/car"),
            headers: {
                'Content-Type': 'application/json'
            },
            data: data
        }

        $http(req).then(function (httpResponse) {
            if (httpResponse.status === 200) {
                onSuccess(httpResponse.data.isSuccess, httpResponse.data.errorCodes);
            } else {
                onError(["CARS.UNKNOWN_ERROR"]);
            }
        }, function (httpResponse) {
            onError(["CARS.UNKNOWN_ERROR"]);
        });
    }

}
Web.App.service('carsService', ['$http', Web.Services.CarsService]);