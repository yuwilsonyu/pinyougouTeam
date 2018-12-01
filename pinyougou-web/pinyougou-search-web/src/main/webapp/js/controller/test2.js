var app = angular.module('myApp2',[]);
app.controller('myController', ['$scope', function ($scope) {
    $scope.list = [
        {'id': 101,'qq':21},
        {'id': 102,'qq':22},
        {'id': 103,'qq':23},
        {'id': 104,'qq':23},
        {'id': 105,'qq':24},
        {'id': 106,'qq':25},
        {'id': 107,'qq':26}
    ];
    $scope.m = [];
    $scope.checked = [];
    $scope.selectAll = function () {
        if($scope.select_all) {
            $scope.checked = [];
            angular.forEach($scope.list, function (i) {
                i.checked = true;
                $scope.checked.push(i.id);
            })
        }else {
            angular.forEach($scope.list, function (i) {
                i.checked = false;
                $scope.checked = [];
            })
        }
        console.log($scope.checked);
    };
    $scope.selectOne = function () {
        angular.forEach($scope.list , function (i) {
            var index = $scope.checked.indexOf(i.id);
            if(i.checked && index == -1) {
                $scope.checked.push(i.id);
            } else if (!i.checked && index !== -1){
                $scope.checked.splice(index, 1);
            };
        })

        if ($scope.list.length === $scope.checked.length) {
            $scope.select_all = true;
        } else {
            $scope.select_all = false;
        }
        console.log($scope.checked);
    }

    // 定义数组封装用户选中的id
    $scope.ids = [];

    // 为checkbox绑定点击事件
    $scope.updateSelection = function ($event, id) {
        // 判断checkbox是否选中
        // $event.target: 获取checkbox对应的dom元素
        if ($event.target.checked){ // 选中
            // 添数组中添加元素
            $scope.ids.push(id);
            alert($scope.ids);
        }else{ // 取消选中
            // 获取一个元素在数组中的索引号
            var idx = $scope.ids.indexOf(id);
            // 从数组中删除一个元素
            // 第一个参数: 元素在数组中的索引号
            // 第二个参数: 删除的个数
            $scope.ids.splice(idx, 1);
            alert($scope.ids);
        }
    };
    /** 监控 user.provinceId变量，查询城市*/
    $scope.$watch('i.checked', function (newValue, oldValue) {
        if (newValue) {
            /** 根据选择的值查询城市 */
          alert(1);
        } else {
          alert(2);
        }
    });
}]);