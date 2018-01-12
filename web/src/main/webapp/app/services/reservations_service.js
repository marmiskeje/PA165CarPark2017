Web.Services.ReservationsService = function ($http) {
    this.getReservations = function (request, onSuccess, onError) {
        var urlReservations = urlBase.concat("/reservation/" + request.branchId);
    	var response = {};
    	var dataFor ={};
        dataFor.start = request.dateFrom.toJSON();
        dataFor.end = request.dateTo.toJSON();
    	request.dateTo.setMilliseconds(1); //workaround, time is not importatn		
    	var req = {
			 method: 'POST',
			 url: urlReservations,
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: dataFor
		}
    	
    	$http(req)
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
    
    this.deleteReservation = function(id, onSuccess, onError){
        $http.delete(urlBase.concat("/reservation/" + id)).then(function(httpResponse){
            if (httpResponse.status === 200){
                onSuccess();
            } else {
                onError();
            }
        }, function(httpResponse){
            onError();
        });
    }
    
    this.createReservation = function(data, onSuccess, onError){
        var req = {
			 method: 'POST',
			 url: urlBase.concat("/reservation"),
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: data
		}
        $http(req).then(function(httpResponse){
           if (httpResponse.status === 200){
                onSuccess(httpResponse.data.isSuccess, httpResponse.data.errorCodes);
            } else {
                onError(["UNKNOWN_ERROR"]);
            }
        }, function(httpResponse){
           onError(["UNKNOWN_ERROR"]); 
        });
    }
    
    this.updateReservation = function(data, onSuccess, onError){
        var req = {
			 method: 'PUT',
			 url: urlBase.concat("/reservation"),
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: data
		}
        $http(req).then(function(httpResponse){
           if (httpResponse.status === 200){
                onSuccess(httpResponse.data.isSuccess, httpResponse.data.errorCodes);
            } else {
                onError(["UNKNOWN_ERROR"]);
            }
        }, function(httpResponse){
           onError(["UNKNOWN_ERROR"]); 
        });
    }
    
    this.getAllReservations = function (request, onSuccess, onError) {
    	var urlAllReservations = urlBase.concat("/reservation");     
        var response = {};
    	$http.get(urlAllReservations)
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
}
Web.App.service('reservationsService', ['$http', Web.Services.ReservationsService]);
