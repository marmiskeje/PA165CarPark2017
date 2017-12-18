Web.Services.BranchesService = function ($http) {
    this.getBranches = function (request, onSuccess, onError) {    
    	var urlBranch = urlBase.concat("/branch");     
        var response = {};
    	$http.get(urlBranch)
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
    
    this.getAllUsers = function (request, onSuccess, onError) {
    	var urlUsers = urlBase.concat("/user");     
        var response = {};
    	$http.get(urlUsers)
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
    
    this.deleteBranch = function (request, onSuccess, onError) {    
        var urlBranch = urlBase.concat("/branch/");
        var urlDelete = urlBranch.concat(request.id);
    	var response = {};
    	var req = {
                    method: 'DELETE',
                    url: urlBranch,
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
    
    this.createBranch = function (request, onSuccess, onError) {    
        var urlBranch = urlBase.concat("/branch");     
    	var response = {};
    	var name = request.name;
    	var req = {
                    method: 'PUT',
                    url: urlBranch,
                    headers: {
                      'Content-Type': 'application/json'
                    },
                    data: {name}
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
    
    this.updateBranch = function (request, onSuccess, onError) {    
        var urlBranch = urlBase.concat("/branch");     
    	var response = {};
    	var name = request.name;
        
        var cars = JSON.stringify(request.cars, this.replacer);
        
    	var req = {
                    method: 'PUT',
                    url: urlBranch,
                    headers: {
                      'Content-Type': 'application/json'
                    },
                    data: {name, cars}
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
    
    this.assignCar = function (request, onSuccess, onError) {    
    	var response = {};
    	var id = request.id;
        var car = JSON.stringify(request.car, this.replacer);
        var urlBranch = urlBase.concat("/branch/").concat(id).concat("/assignCar");
    	var req = {
                    method: 'PUT',
                    url: urlBranch,
                    headers: {
                      'Content-Type': 'application/json'
                    },
                    data: {car}
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
    
    this.assignUser = function (request, onSuccess, onError) {    
    	var response = {};
    	var id = request.id;
        var user = JSON.stringify(request.user, this.replacer);
        var urlBranch = urlBase.concat("/branch/").concat(id).concat("/assignUser");
    	var req = {
                    method: 'PUT',
                    url: urlBranch,
                    headers: {
                      'Content-Type': 'application/json'
                    },
                    data: {user}
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
    
    this.replacer = function replacer(key,value) {
        if (key=="$$hashKey") return undefined;
        else if (key=="object") return undefined;
        else if (key=="selected") return undefined;
        else if (key=="$$mdSelectId") return undefined;
        else return value;
    }
}
Web.App.service('branchesService', ['$http', Web.Services.BranchesService]);