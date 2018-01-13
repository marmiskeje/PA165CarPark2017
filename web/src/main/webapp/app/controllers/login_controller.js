Web.Controllers.LoginController = function ($scope, $rootScope, $state, authService, sessionManager, contractConverter, settingsProvider) {
    $rootScope.pageSubtitle = "LOGIN.PAGE_SUBTITLE";
    $scope.viewModel = new Web.ViewModels.LoginViewModel();
    $scope.actions = new Object();
    $scope.actions.login = function () {
        var request = new Web.Data.AuthRequest();
        request.username = $scope.viewModel.username;
        request.password = $scope.viewModel.password;
        $scope.viewModel.infoMessage = "";
        authService.login(request, function (httpResponse) {
            response = httpResponse.data;
            if (response != null) {
                data = response.data.data;
                if (response.data.isSuccess && response.data.token != null && data != null) {
                    var sessionInfo = new Web.ViewModels.SessionInfoViewModel();
                    sessionInfo.userId = data.id;
                    sessionInfo.username = request.username;
                    sessionInfo.userType = data.userType;
                    if(data.regionalBranch != null){
                        sessionInfo.branchName = data.regionalBranch.name;
                        sessionInfo.branchId = data.regionalBranch.id;
                        sessionInfo.currentCulture = settingsProvider.currentCulture;
                        sessionInfo.currentLanguage = settingsProvider.currentLanguage;
                        sessionManager.createSession(response.data.token, sessionInfo);
                        $state.go(Web.Constants.StateNames.IMPLICIT);
                    }else{
                        $scope.viewModel.infoMessage = "LOGIN.LOGIN_FAILED_BRANCH_NULL";
                    }
                } else {
                    $scope.viewModel.infoMessage = contractConverter.convertAuthError(response.data.errorCodes[0]);
                }
            } else {
                $scope.viewModel.infoMessage = "LOGIN.LOGIN_FAILED_UNKNOWN";
            }
        }, function (httpResponse) {
            $scope.viewModel.infoMessage = "LOGIN.LOGIN_FAILED_SERVER_ERROR";
        });
    }
}

angular.module('CarParSystemWebApp').controller('LoginController', ['$scope', '$rootScope', '$state', 'authService', 'sessionManager', 'contractConverter', 'settingsProvider', Web.Controllers.LoginController]);