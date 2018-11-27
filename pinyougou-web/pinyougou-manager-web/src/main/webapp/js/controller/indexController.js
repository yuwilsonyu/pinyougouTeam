// 定义后台主面的控制器
app.controller('indexController', function ($scope, baseService) {

    /** 定义获取登录用户名的方法 */
    $scope.showLoginName = function () {
        baseService.sendGet("/showLoginName").then(function (response) {
            // 获取响应数据
            $scope.loginName = response.data.loginName;
        });
    };
});