Web.Services.AuthService = function ($http) {
    this.login = function (request, onSuccess, onError) {
        var urlBranch = urlBase.concat("/user/authenticate");
        var response = {};
        var req = {
            method: 'POST',
            url: urlBranch,
            data: request
        }
        $http(req).then(function (httpResponse) {
            response.data = httpResponse;
            response.data.isSuccess = true;
            onSuccess(response);
        }, function(httpResponse) {
            response.data = httpResponse;
            response.data.isSuccess = false;
            onError(response);
        });
    }

    this.logout = function (request, onSuccess, onError) {
        onSuccess(null);
        /*
        var req = {
            method: 'POST',
            url: 'api/session/logout',
            data: request
        }
        $http(req).then(function (response) {
            if (response.status == 200) {
                onSuccess(response);
            } else {
                onError(response);
            }
        }, function (response) {
            onError(response);
        });*/
    }
}
Web.App.service('authService', ['$http', Web.Services.AuthService]);