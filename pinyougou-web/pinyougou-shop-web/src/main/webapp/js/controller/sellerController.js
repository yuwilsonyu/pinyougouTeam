/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 添加 */
    $scope.saveOrUpdate = function(){
        /** 发送post请求 */
        baseService.sendPost("/seller/save", $scope.seller)
            .then(function(response){
                if (response.data){
                    /** 跳转到商家登录页面 */
                    location.href = "/shoplogin.html";
                }else{
                    alert("申请入驻失败！");
                }
            });
    };
    /*定义数组*/
    $scope.seller = [];
    //商家修改资料
    $scope.showinformation=function () {
        baseService.sendGet("/seller/Merchant")
            .then(function (response) {
                if (response.data){
                    $scope.seller=response.data;
                    alert(JSON.stringify(response.date))
                }else {
                    alert("数据回显失败")
                }
            })
    };
    /*商家后台修改密码*/
    $scope.passwordOrUpdate = function () {
        if($scope.psw==$scope.newPassword){
            baseService.sendGet("/user/updateSellerPassword?oldPassword="+$scope.oldPassword+"&newPassword="+$scope.newPassword)
                .then(function (response) {
                    if (!response.date){
                        alert("保存成功！");
                        location.href = "/logout";
                    }else {
                        alert("操作失败")
                    };
                });
        }else {
            alert("密码不一致");
        }
    }
    /*定义数组*/

    //保存商家信息的数据
    $scope.SaveOrbusiness =function () {
        baseService.sendPost("/seller/SaveOrbusiness",$scope.seller)
            .then(function (response) {
                if (response.data){
                    alert("保存成功");
                    /*刷新页面*/
                    $scope.seller = {};
                }else {
                    alert("保存失败");
                }
            })
    };


    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/seller/delete", $scope.ids)
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