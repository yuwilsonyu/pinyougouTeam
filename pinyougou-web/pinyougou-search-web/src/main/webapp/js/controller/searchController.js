/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope,$sce,$location, baseService) {

    // 定义搜索参数对象
    $scope.searchParam = {keywords : '', category : '',
        brand : '', spec : {}, price : '',
        page : 1, rows : 10, sortField : '', sort : ''};

    /** 定义搜索方法 */
    $scope.search = function () {
        baseService.sendPost("/Search", $scope.searchParam)
            .then(function(response){
                // 获取响应数据 {} Map<String,Object>
                $scope.resultMap = response.data;
                // 重新赋值给新的变量
                $scope.keywords = $scope.searchParam.keywords;
                // 生成页码数组
                initPage();

            });
    };


    /** 初始化页码的方法 */
    var initPage = function () {
        // 页码数组
        $scope.pageNums = [];
        // 获取总页数
        var totalPages = $scope.resultMap.totalPages;

        // 开始页码
        var firstPage = 1;
        // 始束页码
        var lastPage = totalPages;

        // 前面加点
        $scope.firstDot = true;
        // 后面加点
        $scope.lastDot = true;

        // 判断总页数
        if (totalPages > 5){
            // 判断当前页码是否靠前面近些
            if ($scope.searchParam.page <= 3){
                lastPage = 5; // 尽量生成前面的页码
                // 前面不加点
                $scope.firstDot = false;
            }
            // 判断当前页码是否靠后面近些
            else if ($scope.searchParam.page >= totalPages - 3){
                firstPage = totalPages - 4; // 尽量生成后面的页码
                // 后面不加点
                $scope.lastDot = false;
            }else{ // 当前页码在中间位置
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }else{
            // 前面不加点
            $scope.firstDot = false;
            // 后面不加点
            $scope.lastDot = false;
        }

        // 循环生成页码放入数组中
        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageNums.push(i);
        }

    };


    // 将文本转化成html
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };


    // 添加搜索项方法(封装过滤条件)
    $scope.addSearchItem = function (key, value) {
        // 商品分类、品牌、价格
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchParam[key] = value;
        }else{ // 规格 spec
            $scope.searchParam.spec[key] = value;
        }
        // 执行搜索
        $scope.search();
    };

    // 删除搜索项方法
    $scope.removeSearchItem = function (key) {
        // 商品分类、品牌、价格
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchParam[key] = '';
        }else{ // 规格 spec
            // {"网络":"移动3G","机身内存":"64G"}
            delete $scope.searchParam.spec[key];
        }
        // 执行搜索
        $scope.search();
    };


    /** 根据页码查询 */
    $scope.pageSearch = function (page) {
        //alert(typeof page);
        var page = parseInt(page);
        // 判断页码的有效性
        if (page >= 1 && page != $scope.searchParam.page
            && page <= $scope.resultMap.totalPages){
            $scope.searchParam.page = page;
            // 执行搜索
            $scope.search();
        }

        if (page < 1){
            $scope.searchParam.page =1;
            $scope.jumpPage = $scope.searchParam.page;
            // 执行搜索
            $scope.search();
        }

        if (page > $scope.resultMap.totalPages){
            $scope.searchParam.page = $scope.resultMap.totalPages;
            $scope.jumpPage = $scope.searchParam.page;
            // 执行搜索
            $scope.search();
        }
    };

    // 搜索排序
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchParam.sortField = sortField;
        $scope.searchParam.sort = sort;
        // 执行搜索
        $scope.search();
    };


    /** 获取首页传来的关键字 */
    $scope.getKeywords = function () {
        // http://search.pinyougou.com/?keywords=%E5%8D%8E%E4%B8%BA
        // 请求URL ?keywords=华为&name=admin
        // $location.search()可以获取get请求url后台的参数，并且把参数转化成json对象
        $scope.searchParam.keywords = $location.search().keywords;
        // 执行搜索
        $scope.search();

    };
});
