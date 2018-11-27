/** 定义控制器层 */
app.controller('goodsController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        // 获取富文本编辑器中的内容
        $scope.goods.goodsDesc.introduction = editor.html();

        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function(response){
                if (response.data){
                    // 清空表单
                    $scope.goods = {};
                    // 清空富文本编辑器中的内容
                    editor.html('');
                }else{
                    alert("添加失败！");
                }
            });
    };

    // 定义数据存储格式
    $scope.goods = {goodsDesc : { itemImages : [], specificationItems : []}};


    // 图片异步上传方法
    $scope.uploadFile = function () {
        // 调用服务层方法
        baseService.uploadFile().then(function(response){
            // 获取响应数据
            // 需要显示上传的的图片 {url:'', status : 200}
            if (response.data.status == 200){
                // 获以图片URL
                // {"color":"","url":""}
                $scope.picEntity.url = response.data.url;
            }else{
                alert("上传失败！");
            }
        });
    };

    // 保存图片到商品描述对象中
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };

    // 从商品描述对象中删除图片
    $scope.removePic = function (idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx, 1);
    };

    // 查询商品分类
    $scope.findItemCatByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemCatByParentId?parentId="
            + parentId).then(function (response) {
                // 获取响应数据 [{},{}]
                $scope[name] = response.data;
        });
    };

    // $scope.$watch()方法，它可以监听$scope中所有变量发生改变.
    // 监听"goods.category1Id"一级分类变量发生改变，查询二级分类
    $scope.$watch("goods.category1Id", function (newVal, oldVal) {
       // alert("新值：" + newVal + ", 旧值：" + oldVal);
        if (newVal){ // 判断newVal不是undefined、null
            // 查询商品的二级分类
            $scope.findItemCatByParentId(newVal, "itemCatList2");
        }else{
            $scope.itemCatList2 = null;
        }
    });

    // 监听"goods.category2Id"二级分类变量发生改变，查询三级分类
    $scope.$watch("goods.category2Id", function (newVal, oldVal) {
        if (newVal){ // 判断newVal不是undefined、null
            // 查询商品的三级分类
            $scope.findItemCatByParentId(newVal, "itemCatList3");
        }else{
            $scope.itemCatList3 = null;
        }
    });

    // 监听"goods.category3Id"三级分类变量发生改变，查找类型模板id
    $scope.$watch("goods.category3Id", function (newVal, oldVal) {
        if (newVal){ // 判断newVal不是undefined、null
            // 迭代三级商品分类数组 List<ItemCat> [{},{}]
            for (var i = 0; i < $scope.itemCatList3.length; i++){
                // 取一个数组元素
                var obj = $scope.itemCatList3[i];
                if (obj.id == newVal){
                    // 获取类型模板id
                    $scope.goods.typeTemplateId = obj.typeId;
                }
            }
        }else{
            $scope.goods.typeTemplateId = null;
        }
    });

    // 监听"goods.typeTemplateId"类型模板变量发生改变，根据主键id查询类型模板对象
    $scope.$watch("goods.typeTemplateId", function (newVal, oldVal) {
        if (newVal){ // 判断newVal不是undefined、null
           baseService.sendGet("/typeTemplate/findOne?id=" + newVal)
               .then(function(response){
                   // 获取响应数据 (品牌)
                   $scope.brandIds = JSON.parse(response.data.brandIds);
                   // 获取扩展属性
                   $scope.goods.goodsDesc.customAttributeItems = JSON
                       .parse(response.data.customAttributeItems);
           });

           // 发送异步请求，查询规格选项数据
           baseService.sendGet("/typeTemplate/findSpecByTemplateId?id="
               + newVal).then(function(response){
               // 获取响应数据
               /**
                * [{"id":27,"text":"网络","options" : [{},{}]},
                  {"id":32,"text":"机身内存","options" : [{},{}]}]
                */
               $scope.specList = response.data;
           });

        }else{
            $scope.brandIds = null;
        }
    });

    // 保存用户选中的规格选项
    $scope.updateSpecAttr = function ($event, specName, optionName) {
        // goods.goodDesc.specificationItems = [];
        /**
         * [{"attributeValue":["联通4G","移动4G"],"attributeName":"网络"},
            {"attributeValue":["64G","128G"],"attributeName":"机身内存"}]
         */
        var obj = searchJson2Arr($scope.goods.goodsDesc.specificationItems,
            "attributeName", specName);
        if (obj){ // {"attributeValue":["联通4G","移动4G"],"attributeName":"网络"}
            // 判断checkbox是否选中
            if ($event.target.checked){
                obj.attributeValue.push(optionName);
            }else{
                // 得到索引号
                var idx = obj.attributeValue.indexOf(optionName);
                // 从attributeValue数组中删除一个元素
                obj.attributeValue.splice(idx,1);
                // 判断attributeValue数组的长度
                if (obj.attributeValue.length == 0){
                    // 得到索引号 specificationItems数组
                    var idx = $scope.goods.goodsDesc.specificationItems.indexOf(obj);
                    // 从specificationItems数组中删除一个元素
                    $scope.goods.goodsDesc.specificationItems.splice(idx, 1);
                }
            }
        }else {
            $scope.goods.goodsDesc.specificationItems
                .push({'attributeValue': [optionName], 'attributeName': specName});
        }
    };

    /** 从json数组中根据指定的key对应的value搜索一个json对象返回 */
    var searchJson2Arr = function (jsonArr, key, value) {
        /**
         * [{"attributeValue":["联通4G","移动4G"],"attributeName":"网络"},
           {"attributeValue":["64G","128G"],"attributeName":"机身内存"}]
         */
        for (var i = 0; i < jsonArr.length; i++){
            // 取一个数组元素
            // {"attributeValue":["联通4G","移动4G"],"attributeName":"网络"}
            var json = jsonArr[i];
            if (json[key] == value){ // json.attributeName == "网络"
                return json;
            }
        }
        return null;
    };

    /** 生成SKU列表 */
    $scope.createItems = function () {
        // 获取用户选中的规格选项列表
        // [{"attributeValue":["联通3G","联通4G"],"attributeName":"网络"}]
        var specItems = $scope.goods.goodsDesc.specificationItems;
        // 定义SKU列表，并且初始化
        // spec: {"网络":"电信4G","机身内存":"64G"}
        $scope.goods.items = [{spec : {}, isDefault : '0', status : '0', price : 0, num : 9999}];

        // 迭代获取用户选中的规格选项列表([{"attributeValue":["联通3G","联通4G"],"attributeName":"网络"})
        for (var i = 0; i < specItems.length; i++){
            // 取一个数组元素
            // {"attributeValue":["联通3G","联通4G"],"attributeName":"网络"}
            var json = specItems[i];
            // 对原来的SKU列表，不断扩充
            $scope.goods.items = swapItems($scope.goods.items,
                    json.attributeName,json.attributeValue);
        }
    };

    // 对原来的SKU列表，不断扩充方法
    var swapItems = function (items, specName, options) {
        // 定义新的SKU数组
        var newItems = [];
        // [{spec : {}, isDefault : '0', status : '0', price : 0, num : 9999}]
        for (var i = 0; i < items.length; i++) {
            // 取数组元素 {spec : {}, isDefault : '0', status : '0', price : 0, num : 9999}
            var item = items[i];
            // 循环规格选项 "attributeValue":["联通3G","联通4G"]
            for (var j = 0; j < options.length; j++) {
                // spec: {"网络":"电信4G","机身内存":"64G"}
                // 在原来的SKU基础上，产生新的sku
                var newItem = JSON.parse(JSON.stringify(item));
                // 增加新的规格选项
                newItem.spec[specName] = options[j];
                newItems.push(newItem);
            }
        }
        return newItems;
    };

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/goods/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };


    /** 定义商品状态码数组 */
    $scope.status = ['未审核','已审核','审核未通过','关闭'];


    /** 商品上下架 */
    $scope.updateMarketable = function (status) {
        if ($scope.ids.length > 0){
            // 迭代dataList
            for (var i = 0; i < $scope.dataList.length; i++){
                // 获取一个商品
                var obj = $scope.dataList[i];
                // 判断是否为用户选择的商品
                if ($scope.ids.indexOf(obj.id) >= 0){
                    // 判断是否已审核通过
                    if (obj.auditStatus != 1){
                        alert("该【" + obj.goodsName + "】商品未审核，不能上下架！");
                        return;
                    }
                }
            }
            /** 发送异步请求 */
            baseService.sendGet("/goods/updateMarketable?ids="
                + $scope.ids + "&status=" + status).then(function(response){
                    if (response.data){
                        /** 清空ids */
                        $scope.ids = [];
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("操作失败！");
                    }
            });
        }else{
            alert("请选择要上下架的商品！");
        }
    };


});