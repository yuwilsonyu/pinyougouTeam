/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/seller/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.entity = JSON.parse(JSON.stringify(entity));
    };

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
    /** 审核商家 */
   /* $scope.updateStatus = function(sellerId, status){
        baseService.sendGet("/seller/updateStatus?sellerId="
            + sellerId + "&status=" + status)
            .then(function(response){
                if (response.data){
                    /!** 重新加载数据 *!/
                    $scope.reload();
                }else{
                    alert("审核失败！");
                }
            });

    };*/
    $scope.updateStatus = function(sellerId,status){
        baseService.sendGet('/seller/updateStatus?sellerId='+sellerId+'&status='+status)
            .then(function (value) {
                if (value.data){
                    $scope.reload();
                }
            })
    };
});