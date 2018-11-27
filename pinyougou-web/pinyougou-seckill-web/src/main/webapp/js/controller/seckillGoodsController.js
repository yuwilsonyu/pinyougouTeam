/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function($scope,$controller,$location,$timeout,baseService){

    /** 指定继承cartController */
    $controller("baseController", {$scope:$scope});

    /** 查询正在秒杀的商品 */
    $scope.findSeckillGoods = function () {
        baseService.sendGet("/seckill/findSeckillGoods").then(function(response){
            // 获取响应数据[{},{}]
            $scope.seckillGoodsList = response.data;
        });
    };


    /** 根据id查询秒杀商品 */
    $scope.findOne = function () {
        // 获取请求参数
        var id = $location.search().id;
        baseService.sendGet("/seckill/findOne?id=" + id).then(function (response) {
            // 获取响应数据
            $scope.entity = response.data;

            // 显示倒计时
            $scope.downCount($scope.entity.endTime);
        });
    };

    /** 倒计时函数 */
    $scope.downCount = function (endTime) {
        // 计算出与当前时间相差的毫秒数
        var millsSeconds =  endTime - new Date().getTime();
        // 计算出相差的秒数
        var seconds = Math.floor(millsSeconds / 1000);

        if(seconds > 0){
            // 计算出相差的分钟
            var minutes = Math.floor(seconds / 60);
            // 计算出相差的小时
            var hours = Math.floor(minutes / 60);
            // 计算出相差的天数
            var days = Math.floor(hours / 24);

            // 定义数组封装时间
            var resArr = [];
            if (days > 0){
                resArr.push(calc(days) + "天 ");
            }
            if (hours > 0){
                resArr.push(calc(hours - days * 24) + ":");
            }
            if (minutes > 0){
                resArr.push(calc(minutes - hours * 60) + ":");
            }
            resArr.push(calc(seconds - minutes * 60));
            // 得到最终显示的时间字符串
            $scope.timeStr = resArr.join("");

            // 开启定时器
            $timeout(function(){
                $scope.downCount(endTime);
            }, 1000);

        }else{
           $scope.timeStr = "秒杀时间已结束！";
        }
    };

    // 计算的方法
    var calc = function (num) {
        return num > 9 ? num : "0" + num;
    };

    /** 立即抢购 */
    $scope.submitOrder = function () {
        // 判断用户是否登录
        if ($scope.loginName){ // 已登录
            baseService.sendGet("/order/submitOrder?id="
                + $scope.entity.id).then(function(response){
                // 获取响应数据
                if (response.data){ // 抢购成功
                    // 跳转到微信扫码支付的页面
                    location.href = "/order/pay.html";
                }else{
                    alert("抢购失败！");
                }
            });
        }else{ // 未登录
            // 跳转到单点登录系统
            location.href = "http://sso.pinyougou.com?service=" + $scope.redirectUrl;
        }
    };
});