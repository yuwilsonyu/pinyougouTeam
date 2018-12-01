angular.module("myApp",[]).controller("mainCtrl", function ($scope) {
        $scope.selectAll=true;
        $scope.all= function (m) {
            for(var i=0;i<$scope.persons.length;i++){
                if(m===true){
                    $scope.persons[i].state=true;
                }else {
                    $scope.persons[i].state=false;
                }
            }
        };
        $scope.persons=[
            {name:"a",state:true},
            {name:"b",state:true},
            {name:"c",state:true},
            {name:"d",state:true}
        ]
    });
