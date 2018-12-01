// 品牌控制器层
app.controller('brandController', function ($scope,$controller, baseService) {

    // 继承baseController
    $controller('baseController', {$scope:$scope});


    // 分页查询方法
    $scope.search = function (page, rows) {
        // 调用服务层
        baseService.findByPage("/brand/findByPage", page, rows,
            $scope.searchEntity).then(function(response){
            // 响应数据 {total: 100, rows : [{},{}]} rows: List<Brand>
            $scope.dataList = response.data.rows;
            $scope.list = response.data.rows;
            // 更新总记录数
            $scope.paginationConf.totalItems = response.data.total;
        });
    };


    // 添加或修改品牌
    $scope.saveOrUpdate = function () {
        // 添加URL
        var url = "/save";
        // 判断是否有id
        if ($scope.entity.id){
            url = "/update"; // 修改
        }
        // 发送异步请求
        baseService.sendPost("/brand" + url, $scope.entity)
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
    };

    // 为修改按钮绑定点击事件
    $scope.show = function(entity){
        // 把entity转化成json字符串
        var jsonStr = JSON.stringify(entity);
        // 把jsonStr转化成json对象
        $scope.entity = JSON.parse(jsonStr);
    };


    // 为删除按钮绑定点击事件
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/brand/delete", $scope.ids)
                .then(function(response){
                if (response.data){ // true
                    // 删除成功，重新加载品牌数据
                    $scope.reload();
                    // 清空ids数组
                    $scope.ids = [];
                }else{
                    alert("删除失败！");
                }
            });
        }else{
            alert("请选择要删除的品牌！");
        }
    };

});