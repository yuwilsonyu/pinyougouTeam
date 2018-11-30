app.controller("sellerController", function ($scope, $controller, baseService) {
    // 指定继承baseController
    $controller('baseController',{$scope:$scope});

    $scope.saveOrUpdate = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }
        baseService.sendPost("/seller/" + url, $scope.entity).then(
            function (value) {
                if (value.data) {
                    $scope.reload();
                } else {
                    alert("服务器忙");
                }
            });
    };

    $scope.show = function (entity) {
        // 把参数entity的json对象转化成$scope的json对象
        // $scope.entity = entity;
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 定义查询条件对象 */
    $scope.searchEntity = {};
    // 多条件分页查询品牌,商家审核页面设置status为0,商家管理界面待选
    $scope.search = function (page, rows) {
        baseService.findByPage("/seller/findByPage", page, rows, $scope.searchEntity).then(function (response) {
            $scope.entityList = response.data.rows;
            /** 更新总记录数 */
            $scope.paginationConf.totalItems = response.data.total;
        });
    };
    // findAllByPage
    $scope.statusArr = ["未审核","审核通过","审核不通过","关闭"];
    /*根据上面定义的ids数组 删除一个或多个数据*/
    $scope.delete = function () {
        /*先判断是否要发送异步请求*/
        if ($scope.ids.length > 0) {
            baseService.deleteById('/sellerCheck/delete', $scope.ids).then(function (value) {
                if (value.data) {
                    $scope.reload();
                } else {
                    alert("服务器忙")
                }
            });
        } else {
            alert("您还没有选择呢")
        }
    };
    $scope.updateStatus = function(sellerId,status){
        baseService.sendGet('/seller/updateStatus?sellerId='+sellerId+'&status='+status).then(function (value) {
            if (value.data){
                $scope.reload();
            }
        })
    };
});