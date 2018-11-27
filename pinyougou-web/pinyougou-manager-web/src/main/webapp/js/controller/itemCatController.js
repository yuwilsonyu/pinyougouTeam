/** 定义控制器层 */
app.controller('itemCatController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 根据父级id查询商品分类 */
    $scope.findItemCatByParentId = function (parentId) {
        // 发送异步请求
        baseService.sendGet("/itemCat/findItemCatByParentId",
            "parentId=" + parentId).then(function (response) {
                // 获取响应数据 List<ItemCat> [{},{}]
                $scope.dataList = response.data;
        });
    };

    // 定义级别变量
    $scope.grade = 0;

    // 为查询下级绑定事件
    $scope.selectList = function (entity, grade) {
        // 改变它的值
        $scope.grade = grade;

        if (grade == 1) { // 一级分类
            $scope.itemCat_1 = entity;
        }
        if (grade == 2){ // 二级分类
            $scope.itemCat_2 = entity;
        }
        $scope.findItemCatByParentId(entity.id);
    };





    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        if ($scope.entity.id){
            url = "update";
        }
        /** 发送post请求 */
        baseService.sendPost("/itemCat/" + url, $scope.entity)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    $scope.reload();
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/itemCat/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };
});