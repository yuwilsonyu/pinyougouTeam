// 定义购物车控制器
app.controller("cartController", function ($scope,$controller,baseService) {

    // 指定继承baseController
    $controller("baseController", {$scope:$scope});

    // 查询购物车
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function(response){
            // 获取响应数据
            $scope.carts = response.data;

            /** 定义总计对象 */
            $scope.totalEntity = {totalNum : 0, totalMoney : 0.00};
            // 循环购物车集合
            for (var i = 0; i < $scope.carts.length; i++){
                // 获取一个商家的购物车
                var cart = $scope.carts[i];
                for (var j = 0; j < cart.orderItems.length; j++){
                    // 合计购买总件数
                    $scope.totalEntity.totalNum += cart.orderItems[j].num;
                    // 合计购买总金额
                    $scope.totalEntity.totalMoney += cart.orderItems[j].totalFee;
                }
            }

        });
    };

    // 添加商品到购物车
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart?itemId="
            + itemId + "&num=" + num).then(function(response){
                if (response.data){ // true
                    // 重新查询购物车
                    $scope.findCart();
                }else{
                    alert("添加购物车失败！");
                }
        });
    };
});