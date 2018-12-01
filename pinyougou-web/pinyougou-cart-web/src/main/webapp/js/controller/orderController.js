// 定义订单控制器
app.controller('orderController', function ($scope,$controller,$interval,$location, baseService) {

    // 指定继承cartController
    $controller('cartController', {$scope:$scope});



    // 查询当前登录用户的收货地址
    $scope.findAddressByUser = function () {
        baseService.sendGet("/order/findAddressByUser").then(function (response) {
            // 获取响应数据
            $scope.addressList = response.data;

            // 循环地址数组
            for (var i = 0; i < $scope.addressList.length; i++){
                var address = $scope.addressList[i];
                if (address.isDefault == 1){
                    $scope.address = address;
                }
            }
        });
    };

    // 用户选择地址
    $scope.selectAddress = function (item) {
        $scope.address = item;
    };

    // 判断地址是否选中
    $scope.isSelectAddress = function (item) {
        return $scope.address == item;
    };

    // 定义order对象封装请求参数
    $scope.order = {paymentType : '1'};

    // 支付方式选择
    $scope.selectPayType = function (payType) {
        $scope.order.paymentType = payType;
    };

    // 提交订单
    $scope.saveOrder = function () {
        // 设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        // 设置收件人手机号码
        $scope.order.receiverMobile = $scope.address.mobile;
        // 设置收件人
        $scope.order.receiver = $scope.address.contact;
        // 设置订单来源(pc端)
        $scope.order.sourceType = "2";
        // 发送异步请求
        baseService.sendPost("/order/saveOrder", $scope.order).then(function (response) {
            // 获取响应数据
            if (response.data){ // 提交成功
                // 判断支付方式
                if ($scope.order.paymentType == '1'){
                    // 跳转到微信扫码支付的页面
                    location.href = "/order/pay.html";
                }else{
                    // 货到付款，跳转到成功页面
                    location.href = "/order/paysuccess.html";
                }
            }else {
                alert("提交订单失败！");
            }
        });
    };


    // 生成微信支付二维码
    $scope.genPayCode = function () {

        baseService.sendGet("/order/genPayCode").then(function(response){
            // 获取交易订单号 {outTradeNo : '', totalFee : '', codeUrl : ''}
            $scope.outTradeNo = response.data.outTradeNo;
            // 获取交易总金额
            $scope.money = (response.data.totalFee / 100).toFixed(2);

            // 生成二维码
            document.getElementById("img").src = "/barcode?url=" + response.data.codeUrl;
            /**
             * 开启定时器，间隔3秒发送异步请求，检测支付状态
             * 第一个参数：回调函数
             * 第二个参数；间隔毫秒
             * 第三个参数：调用的总次数
             */
            var timer = $interval(function(){
                // 间隔3秒发送异步请求，检测支付状态
                baseService.sendGet("/order/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo).then(function(response){
                        // 获取响应数据
                        if (response.data.status == 1){ // 支付成功
                            // 取消定时器
                            $interval.cancel(timer);
                            // 跳转到支付成功的页面
                            location.href = "/order/paysuccess.html?money=" + $scope.money;
                        }
                        if (response.data.status == 3){ // 支付失败
                            // 取消定时器
                            $interval.cancel(timer);
                            // 跳转到支付成功的页面
                            location.href = "/order/payfail.html";
                        }
                });
            }, 3000, 100);

            // 在总次数调用完之后，会调用该函数
            timer.then(function(){
                // 关闭订单
                $scope.tip = "二维码已过期。";
            });

        });
    };

    // 获取支付金额
    $scope.getMoney = function () {
        return $location.search().money;
    };
});