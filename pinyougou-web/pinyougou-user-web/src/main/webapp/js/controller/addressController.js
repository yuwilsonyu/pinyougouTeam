/** 定义控制器层 */
app.controller('addressController', function ($scope, baseService) {


    // 获取登录用户名和address数据
    $scope.findAddressByUser = function () {
        baseService.sendGet("/address/findAddressByUser").then(function (response) {
            // 获取响应数据
            $scope.addressList = response.data;
            $scope.userName=$scope.addressList[0].userId;

        });
    };
    /** 监控 user.provinceId变量，查询城市*/
    $scope.$watch('address.provinceId', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择的值查询城市 */
            $scope.findCityId(newValue);
        } else {
            $scope.cityList = [];
        }
    });
    /** 监控 user.cityId 变量，查询城市*/
    $scope.$watch('address.cityId', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择省份查询城市 */
            $scope.findAreasId(newValue);
        } else {
            $scope.areasList = [];
        }
    });

    /** 根据城市ID查询省区 */
    $scope.findAreasId = function (CityId) {
        baseService.sendGet("/areas/findAreasId?citiesId=" + CityId).then(function (response) {
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

    $scope.saveOrUpdate=function () {
        // 添加URL
        var url = "/save";
        // 判断是否有id
        if ($scope.address.id){
            url = "/update"; // 修改
        }else{
            $scope.address.userId= $scope.userName;
        }
        // 发送异步请求
        baseService.sendPost("/address" + url, $scope.address)
            .then(function(response){
                // 获取响应数据
                // response.data : true|false
                if (response.data){
                    // 重新加载品牌品牌
                    $scope.reload();
                }else{
                    alert("操作失败！");
                }
            });
    }
    // 重新加载数据方法
    $scope.reload = function () {
        $scope.findAddressByUser();
    };

    // 为修改按钮绑定点击事件
    $scope.show = function(entity){
        // 把entity转化成json字符串
        var jsonStr = JSON.stringify(entity);
        // 把jsonStr转化成json对象
        $scope.address = JSON.parse(jsonStr);
    };

// 为删除按钮绑定点击事件
    $scope.delete=function(id){
            alert(id);
            baseService.sendGet("/address/delete?id="+id)
                .then(function(response){
                    if (response.data){ // true
                        // 删除成功，重新加载品牌数据
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
    })
    }
});