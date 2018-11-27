// 定义基础控制器
app.controller('baseController', function ($scope, $http) {

    // 获取登录用户名
    $scope.loadUsername = function () {
        // 获取url问号后台参数 ?r=0.88888
        var param = location.search;
        var url = location.href.replace(param, "");
        // 定义重定向的请求URL
        $scope.redirectUrl = window.encodeURIComponent(url + "?r=" + Math.random());

        $http.get("/user/showName").then(function (response) {
            // 获取响应数据
            $scope.loginName = response.data.loginName;
        });
    };

});