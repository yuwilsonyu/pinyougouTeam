// 定义购物车控制器
app.controller("cartController", function ($scope,$controller,baseService) {

    // 指定继承baseController
    $controller("baseController", {$scope:$scope});

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

    /** 定义总计对象 */
    $scope.totalEntity = {totalNum : 0, totalMoney : 0.00};
    // 查询购物车
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function(response){
            // 获取响应数据
            $scope.carts = response.data;
        });
    };

    // 查询cookie购物车
    $scope.findCookieCart = function () {
        baseService.sendGet("/cart/findCookieCart").then(function(response){
            // 获取响应数据
            $scope.carts = response.data;
            /** 定义总计对象 */
            $scope.totalEntity2 = {totalNum : 0, totalMoney : 0.00};
            for (var i = 0; i < $scope.carts.length; i++){
                // 获取一个商家的购物车
                var cart = $scope.carts[i];
                for (var j = 0; j < cart.orderItems.length; j++){
                    // 合计购买总件数
                    $scope.totalEntity2.totalNum += cart.orderItems[j].num;
                    // 合计购买总金额
                    $scope.totalEntity2.totalMoney += cart.orderItems[j].totalFee;
                }
            }

        });
    };


    // ++++++++++++checkbox全选反选++++++++++++++++/
    //全选方法，并将所有的id一并传入ids数组中
    $scope.all = function($event,carts){
        var checkbox = $event.target ;
        var checked = checkbox.checked ;
        if(checked){
            $scope.sellerx=true;
            $scope.x=true;
            // 循环购物车集合
            for (var i = 0; i < $scope.carts.length; i++){
                // 获取一个商家的购物车
                var cart = $scope.carts[i];
                for (var j = 0; j < cart.orderItems.length; j++){
                    // 合计购买总件数
                    $scope.totalEntity.totalNum += cart.orderItems[j].num;
                    // 合计购买总金额
                    $scope.totalEntity.totalMoney += cart.orderItems[j].totalFee;
                    $scope.itemIds.push(cart.orderItems[j].itemId);
                    $scope.nums.push(cart.orderItems[j].num);
                }
            }
            console.log($scope.ids);
        } else{
            $scope.x=false;
            $scope.sellerx=false;
            $scope.totalEntity = {totalNum : 0, totalMoney : 0.00};
            $scope.itemIds=[];
            $scope.nums=[];
        }
    };

    /**单个复选框事件*/
        $scope.itemIds=[];
        $scope.nums=[];
    $scope.updateTotalEntity=function ($event,num,priceAll,itemId) {
        var checkbox = $event.target ;
        var checked = checkbox.checked ;
        if(checked) {

            // 合计购买总件数
            $scope.totalEntity.totalNum += num;
            // 合计购买总金额
            $scope.totalEntity.totalMoney += priceAll;
            $scope.itemIds.push(itemId);
            $scope.nums.push(num);
        }else{

            // 合计购买总件数
            $scope.totalEntity.totalNum -= num;
            // 合计购买总金额
            $scope.totalEntity.totalMoney -= priceAll;
            var idx = $scope.itemIds.indexOf(itemId);
            var numIdx = $scope.nums.indexOf(num);
            $scope.itemIds.splice(idx,1);
            $scope.nums.splice(numIdx,1);
        }
    }
    $scope.addNewCarts=function () {
        //让原始购物车更新


        baseService.sendGet("/cart/addCountCart?itemIds="
            + $scope.itemIds + "&nums=" + $scope.nums).then(function(response){
            if (response.data){ // true
                // 重新查询购物车
                $scope.findCart();
                $scope.itemIds=[];
                $scope.nums=[];
            }else{
                alert("添加购物车失败！");
            }
        });

        // for(var i=0;i<$scope.itemIds.length;i++){
        //
        //
        // }


    }
});