// 定义基础控制器层
app.controller('baseController', function ($scope) {

    // 定义分页指令需要配置信息对象
    $scope.paginationConf = {
        currentPage : 1, // 当前页码
        perPageOptions : [10,15,20], // 页码下拉列表框需要数据
        itemsPerPage : 10, // 每页显示的记录数
        totalItems : 0, // 总记录数
        onChange : function(){ // 当页码发生改变，需要调用的函数
            $scope.reload();
        }
    };

    // 重新加载数据方法
    $scope.reload = function () {
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    };

    // 定义数组封装用户选中的id
    $scope.ids = [];

    // 为checkbox绑定点击事件
    $scope.updateSelection = function ($event, id) {
        // 判断checkbox是否选中
        // $event.target: 获取checkbox对应的dom元素
        if ($event.target.checked){ // 选中
            // 添数组中添加元素
            $scope.ids.push(id);
        }else{ // 取消选中
            // 获取一个元素在数组中的索引号
            var idx = $scope.ids.indexOf(id);
            // 从数组中删除一个元素
            // 第一个参数: 元素在数组中的索引号
            // 第二个参数: 删除的个数
            $scope.ids.splice(idx, 1);
        }
    };

    // 获取json数组中的元素的text，拼成字符串返回
    $scope.jsonArr2Str = function (jsonStr, key) {
        // jsonStr : [{id : 1, text : ''},{}]
        // 把jsonStr转化成json对象(数组)
        var jsonArr = JSON.parse(jsonStr);
        var resArr = [];
        // 循环数组
        for (var i = 0; i < jsonArr.length; i++){
            // 取一个数组元素 {id : 1, text : ''}
            var json = jsonArr[i];
            resArr.push(json[key]);
        }
        return resArr.join(",");
    };
});