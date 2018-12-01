// 定义用户中心首页控制器
app.controller('indexController', function ($scope, baseService) {

    $scope.user = {"address":{}}

    // 获取登录用户名
    $scope.showName = function () {
        baseService.sendGet("/user/showName").then(function (response) {
            // 获取响应数据
            $scope.loginName = response.data.loginName;
            $scope.user.userName = $scope.loginName;
            $scope.showInfo();
        });
    };

    /** 监控 user.provinceId变量，查询城市*/
    $scope.$watch('user.address.provinceId', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择的值查询城市 */
            $scope.findCityId(newValue);
        } else {
            $scope.cityList = [];
        }
    });
    /** 监控 user.cityId 变量，查询城市*/
    $scope.$watch('user.address.cityId', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择省份查询城市 */
            $scope.findAreasId(newValue);
        } else {
            $scope.areasList = [];
        }
    });

    /** 根据城市ID查询省区 */
    $scope.findAreasId = function (provinceId) {
        baseService.sendGet("/areas/findAreasId?citiesId=" + provinceId).then(function (response) {
            $scope.areasList = response.data;
        });
    };

    /** 根据省份ID查询城市 */
    $scope.findCityId = function (provinceId) {
        baseService.sendGet("/cities/findCityId?provinceId=" + provinceId).then(function (response) {
            $scope.cityList = response.data;
        });
    };
    /** 省份查询 */
    $scope.findProvinceId = function () {
        baseService.sendGet("/province/findProvinceId").then(function (response) {
            $scope.provinceList = response.data;
        });
    };

    //定义封装数据
    $scope.user = {"headPic": "", 'userName': ''}
    // 图片异步上传方法
    $scope.uploadFile = function () {
        // 调用服务层方法
        baseService.uploadFile().then(function (response) {
            // 获取响应数据
            // 需要显示上传的的图片 {url:'', status : 200}
            if (response.data.status == 200) {
                // 获以图片URL
                // {"color":"","url":""}
                alert("上传成功！");
                $scope.user.headPic = response.data.url;
            } else {
                alert("上传失败！");
            }
        });
    };

    /** 添加或修改 */
    $scope.update = function () {
        /** 发送post请求 */
        baseService.sendPost("/user/update", $scope.user)
            .then(function (response) {
                if (response.data) {
                    alert("保存成功！");
                } else {
                    alert("添加失败！");
                }
            });
    };

    $scope.showInfo = function () {
        baseService.sendGet("/user/selectOneByUserName?userName=" + $scope.user.userName).then(function (response) {
            if (response.data) {
                $scope.user = response.data;
                $scope.user.address=angular.fromJson(response.data.address);

            }
        })

    }
});