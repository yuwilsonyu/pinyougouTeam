/** 定义控制器层 */
app.controller('userController', function ($scope, baseService) {

    $scope.user = {}

    /** 用户注册 */
    $scope.save = function () {

        // 判断两次密码是否一致
        if ($scope.password && $scope.password == $scope.user.password) {
            // 发送异步请求
            baseService.sendPost("/user/save?smsCode=" + $scope.smsCode, $scope.user).then(function (response) {
                // 获取响应数据
                if (response.data) { // true
                    alert("注册成功！");
                    $scope.user = {};
                    $scope.password = "";
                    $scope.smsCode = "";
                } else {
                    alert("注册失败！");
                }
            });
        } else {
            alert("两次密码不一致！");
        }
    };

    /** 发送短信验证码到用户手机 */
    $scope.sendCode = function () {
        if ($scope.user.phone && /^1[3|5|7|8|9]\d{9}$/.test($scope.user.phone)) {
            baseService.sendGet("/user/sendCode?phone="
                + $scope.user.phone).then(function (response) {
                if (response.data) {
                    alert("发送成功！");
                } else {
                    alert("发送失败！");
                }
            });
        } else {
            alert("手机号码格式不正确！");
        }
    };

    // 获取登录用户名
    $scope.showName = function () {
        baseService.sendGet("/user/showName").then(function (response) {
            // 获取响应数据
            $scope.loginName = response.data.loginName;
        });
    };

    //修改用户密码
    $scope.updateUserpassword = function () {
        if ($scope.newpassword1 == $scope.newpassword2) {
            baseService.sendGet("/user/updateUserpassword?password=" + $scope.newpassword2).then(function (response) {
                if (response.data) {
                    alert("修改密码成功,请重新登录");
                    location.href = "/logout";
                } else {
                    alert("修改密码失败");
                }
            });
        } else {
            alert("两次输入密码不一致,请重新输入");
        }
    };


    $scope.phone = '';
    //获取用户已绑定手机号码
    $scope.getUserPhone = function () {
        baseService.sendGet("/user/getUserPhone").then(function (response) {
            $scope.phone = response.data;
            $scope.mobile = $scope.phone.substr(0, 3) + "****" + $scope.phone.substr(8);
        });
    };

    //发送短信验证码
    $scope.sendCode = function (phone) {
        baseService.sendGet("/user/sendCode?phone=" + phone).then(function (response) {
            if (response.data) {
                $scope.success = true;
            } else {
                alert("验证码发送失败");
            }
        });
    };

    //检查短信验证码是否正确
    $scope.checkCode = function (phone, code, href) {
        baseService.sendGet("user/checkCode?phone=" + phone + "&code=" + code).then(function (response) {
            if (!response.data) {
                alert("验证码不正确,请重新输入");
                $scope.rightCode = false;
            } else {
                $scope.rightCode = true;
            }
        });
    };

    //更新用户手机号码
    $scope.updateUserPhone = function (newPhone, code) {
        baseService.sendGet("user/checkCode?phone=" + phone + "&code=" + code).then(function (response) {
            if (!response.data) {
                alert("验证码不正确,请重新输入");
                $scope.rightCode = false;
            } else {
                baseService.sendGet("/user/updateUserPhone?newPhone=" + newPhone + "&code=" + code).then(function (response1) {
                    if (response1.data){
                        $scope.rightCode = true;
                    }else {
                        $scope.rightCode = false;
                    }
                });
            }
        });

    };

});