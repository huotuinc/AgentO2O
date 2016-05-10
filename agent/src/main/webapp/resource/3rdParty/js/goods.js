


var goodsDataProvider = {};

goodsDataProvider.getBrandList = function (goodsTypeId,_jsonAllBrands) {//根据商品类型获取品牌列表
    var arrTemp = [];
    for (var i = 0; i < _jsonAllBrands.length; i++) {
        if (goodsTypeId == _jsonAllBrands[i].Type_Id) {
            arrTemp.push(_jsonAllBrands[i]);
        }
    }
    return arrTemp;
};

goodsDataProvider.getSpecList = function (goodsTypeId,_jsonAllSpecifications) {//根据商品类型获取规格列表

    var arrTemp = [];
    for (var i = 0; i < _jsonAllSpecifications.length; i++) {
        if (goodsTypeId == _jsonAllSpecifications[i].Type_Id) {
            arrTemp.push(_jsonAllSpecifications[i]);
        }
    }
    return arrTemp;
};

goodsDataProvider.getSpecValueList = function (specId,_jsonAllSpecValues) {//根据规格ID获取规格值ID
    var arrTemp = [];
    for (var i = 0; i < _jsonAllSpecValues.length; i++) {
        if (specId == _jsonAllSpecValues[i].Spec_Id) {
            arrTemp.push(_jsonAllSpecValues[i]);
        }
    }
    return arrTemp;
};

goodsDataProvider.getSpecValue = function (specValueId) {//根据规格值ID获取规格值对象
    for (var i = 0; i < _jsonAllSpecValues.length; i++) {
        if (specValueId == _jsonAllSpecValues[i].Spec_Value_Id) {
            return _jsonAllSpecValues[i];
        }
    }
    return null;
};
//#endregion

//#region 货品编辑
var specProductEditor = {};

//#region 属性变量
specProductEditor.selectedSpecValues = [];
specProductEditor.currentGoodsTypeId = 0;
specProductEditor.sellInfo = {};
specProductEditor.currentSpecList = [];
specProductEditor.exsitProductRows = {};
specProductEditor.isChangeProduct = 0;
//specProductEditor.propList=[];
//#endregion

specProductEditor.initSpec = function(list,bn){
    this.sellInfo['txtProductBN']=bn;
    if(list == null || list.length == 0){
        var noSpecRow = $('#tmplProductTableRow').html();
        noSpecRow = noSpecRow.replace("{$specs$}", "");
        noSpecRow = noSpecRow.replace("{$rowclass$}", "");
        noSpecRow = noSpecRow.replace("{$specids}", "").replace("{$specids}","");
        noSpecRow = noSpecRow.replace(/{specids}/g, "");
        $('#specProductList').append(noSpecRow);
        $("input[name='pro_bn']").val(bn);
    }else{
        this.currentSpecList = list;
    }
}
specProductEditor.initNoSpecProduct = function(product){
    $('input[name="pro_bn"]').val(product[0].bn);
    $('input[name="pro_id"]').val(product[0].productId);
    $('input[name="pro_store"]').val(product[0].store);
    $('input[name="pro_mktPrice"]').val(product[0].mktPrice);
    $('input[name="pro_price"]').val(product[0].price);
    $('input[name="pro_cost"]').val(product[0].cost);
    $('input[name="pro_weight"]').val(product[0].weight);
}
specProductEditor.initCheckSpec = function(specDesc){
    var checkedSpecList = JSON.parse(specDesc);
    for(var i = 0 ; i<checkedSpecList.length ; i ++){
        $("#chkSpecValue_" + checkedSpecList[i].SpecValueId).attr('checked',true);
        $("#spec_value_custom_" + checkedSpecList[i].SpecValueId).val(checkedSpecList[i].SpecValue);
        var gimages = checkedSpecList[i].GoodsImageIds;
        if(gimages != undefined){
            var temp = [];
            for (var k = 0; k < gimages.length; k++) {
                temp.push('0|' + gimages[k]);
            }
            $("#spec_rel_images_" + checkedSpecList[i].SpecValueId).val(temp.join('^'));
        }
    }
    specProductEditor.getCheckedSpecValue();
}
specProductEditor.initProduct = function(productList){
    for(var i = 0 ; i < productList.length ; i++){
        var propList = JSON.parse(productList[i].props);
        var propString = "";
        for(var j = 0 ; j <propList.length ; j++){
            if(propString.length > 0){
                propString += "_";
            }
            propString += (propList[j].SpecId + "|" + propList[j].SpecValueId);
        }
        var recordedRow = $('#specProductTable tr[ppid="' +propString + '"]');
        recordedRow.find('input[name="pro_id"]').val(productList[i].productId);
        recordedRow.find('input[name="pro_bn"]').val(productList[i].bn);
        recordedRow.find('input[name="pro_mktPrice"]').val(productList[i].mktPrice);
        recordedRow.find('input[name="pro_price"]').val(productList[i].price);
        recordedRow.find('input[name="pro_cost"]').val(productList[i].cost);
        recordedRow.find('input[name="pro_weight"]').val(productList[i].weight);
        recordedRow.find('input[name="pro_store"]').val(productList[i].store);
    }
}
specProductEditor.switchCustomSpec = function (flag) {//是否启用规格货品
    if (flag) {
        $('#tbProductNormal').slideUp();
        $('#divProductSepc').slideDown();

        this.sellInfo = {};
        var inputIds = ['txtPrice', 'txtCost', 'txtMktprice', 'txtProductBN', 'txtWeight', 'txtStore','lvPriceInfogood','lvIntegralInfogood'];
        for (var i = 0; i < inputIds.length; i++) {
            this.sellInfo[inputIds[i]] = $('#' + inputIds[i]).val();
        }
        $('#hidOpenCustomSpec').val(1);

        this.showSpecList($('#ddlTypeId').val());
        this.initSpecValueAliasListen();
    } else {
        if (confirm('关闭后现有已添加的货品数据将全部丢失，确定要关闭规格吗？')) {
            $('#tbProductNormal').slideDown();
            $('#divProductSepc').slideUp();
            $('#hidOpenCustomSpec').val(0);
        }
    }
};

specProductEditor.init = function () {
    specProductEditor.initSpecValueAliasListen();
};

specProductEditor.showSpecList = function (goodsTypeId) {//显示规格
    this.currentGoodsTypeId = goodsTypeId;
    //颜色、尺码
    var _tmplSpecContainer = $('#tmplSpecContainer').html();
    var _tmplSpecValueRow = $('#tmplSpecValueRow').html();

    var _finalHtml = [];
    var _specList = goodsDataProvider.getSpecList(goodsTypeId);

    this.currentSpecList = _specList;


    if (_specList.length == 0) {
        $('#divNoneSpecNote').show();
        $('#divSpecList').empty();
        $('#divSpecProductTable').empty();
        return;
    } else {
        $('#divNoneSpecNote').hide();
    }

    for (var i = 0; i < _specList.length; i++) {//规格块
        var _rowHtml = [];
        var _specValues = goodsDataProvider.getSpecValueList(_specList[i].Spec_Id);
        for (var j = 0; j < _specValues.length; j++) {//规格值列表
            var temprow = _tmplSpecValueRow.replace(/\{\$specvalue\$\}/ig, _specValues[j].Spec_Value);
            temprow = temprow.replace("{$rowclass$}", j % 2 == 0 ? 'even' : '');
            temprow = temprow.replace(/\{\$id\$\}/ig, _specValues[j].Spec_Id);
            temprow = temprow.replace(/\{\$valueid\$\}/ig, _specValues[j].Spec_Value_Id);
            _rowHtml.push(temprow);
        }
        var temp = _tmplSpecContainer.replace(/\{\$specname\$\}/ig, _specList[i].Spec_Name);
        temp = temp.replace(/\{\$id\$\}/ig, _specList[i].Spec_Id);
        temp = temp.replace("{$rows$}", _rowHtml.join(''));
        _finalHtml.push(temp);
    }

    $('#divSpecList').html(_finalHtml.join(''));

    specProductEditor.checkSpecValue();
    specProductEditor.initExsitProductRow();

    specProductEditor.getCheckedSpecValue();
};

specProductEditor.relateSpecToImages = function (specValueId) {//关联规格与商品图片

    //var url = 'SpecRelImages.aspx?specvalueid=' + specValueId + '&rnd=' + Math.random();
    //J.PopupIFrame(url, "关联商品图片", 600, 380, "product_menu22", { "确定": true }, "auto", "", function (result) {

    //});

    this._relateSpecToImages(specValueId);
};

specProductEditor._relateSpecToImages = function (specValueId) {//关联规格与商品图片，上传自定义主图
    var url = '../goods/specRelImages.html?specvalueid=' + specValueId.getAttribute('valueid') + '&rnd=' + Math.random();
    //var url = 'goods/specRelImages.html?specvalueid=' + specValueId.valueid + '&rnd=' + Math.random();
    J.PopupIFrame(url, "定义规格图片", 800, 470, "product_menu22", { "确定": true }, "auto", "", function (result) {

    });
};

specProductEditor.getCurrentGoodsImages = function () {//获取目前的商品图片
    var imgIds = [], imgPaths = [],imgSrcs = [];
    $('input[name="hidGoodsImageId"]').each(function () {
        imgIds.push(this.value);
    });
    $('input[name="hidGoodsImagePath"]').each(function () {
        imgPaths.push(this.value);
    });
    $('img[name="imgGoods"]').each(function(){
        imgSrcs.push(this.src);
    });
    return { ids: imgIds, paths: imgPaths,srcs: imgSrcs };
};

specProductEditor.getExsitGoodsImages = function (specValueId) {//获取已经选择过的商品图片
    var exsitImages = $('#spec_rel_images_' + specValueId).val();

    //'0|/sxxx.png|uri^3|dfd.png|uri'
    var arrImages = [];
    if (typeof (exsitImages) != 'undefined') {
        var arrSingle = exsitImages.split('^');
        for (var i = 0; i < arrSingle.length; i++) {
            var temp = arrSingle[i].split('|');
            if (temp.length >= 3) {
                arrImages.push({id: temp[0], path: temp[1],src:temp[2]});
            }
        }
    }
    return arrImages;
};

specProductEditor.setExsitGoodsImages = function (specValueId, result) {
    //alert(specValueId + '________' + result);
    $('#spec_rel_images_' + specValueId).val(result);
}

specProductEditor.checkSpecValue = function () {//修改模式下，勾上已经使用的规格项
    for (var i = 0; i < _jsonGoodsSpecIndex.length; i++) {
        var valueId = _jsonGoodsSpecIndex[i]['Spec_Value_Id'];
        var chkTemp = document.getElementById('chkSpecValue_' + valueId);
        if (chkTemp) {
            chkTemp.checked = true;
        }
    }
    //[{"SpecId":1,"ShowType":"text","SpecValue":"黑色","SpecValueId":7,"SpecImage":null,"GoodsImageIds":[]},{"SpecId":2,"ShowType":"text","SpecValue":"S","SpecValueId":8,"SpecImage":null,"GoodsImageIds":[]}];
    //_jsonProductsSpecDesc 中读取个性化的值规格值
    for (var i = 0; i < _jsonProductsSpecDesc.length; i++) {
        $('#spec_value_custom_' + _jsonProductsSpecDesc[i].SpecValueId).val(_jsonProductsSpecDesc[i].SpecValue);
        var gimages = _jsonProductsSpecDesc[i].GoodsImageIds;
        var temp = [];
        for (var k = 0; k < gimages.length; k++) {
            temp.push('0|' + gimages[k]);
        }
        $('#spec_rel_images_' + _jsonProductsSpecDesc[i].SpecValueId).val(temp.join('^'));
    }
}

specProductEditor.initExsitProductRow = function () {//修改前的货品行
    this.exsitProductRows = this.getRecordedRows();
};

specProductEditor.getCheckedSpecValue = function () {//获取所有勾上的规格

    specProductEditor.isChangeProduct=1;
    var arrTemp = [];
    for (var i = 0; i < this.currentSpecList.length; i++) {
        arrTemp[i] = [];

        $('input[name="chkSpec_Id_' + this.currentSpecList[i].specId + '"]').each(function () {
            var specValue = $(this).attr("specValue");
            var specValueId = $(this).attr("specValueId");
            var specId = $(this).attr("specId");
            //复选框选中，在 specValue_table_{specId} 中追加行
            if(this.checked){
                if($("#js-specValue-" + specValueId).length == 0){
                    var html = $("#tmplSpecValueRow").html();
                    html = html.replace(/\{specValue}/g,specValue);
                    html = html.replace(/\{specValueId}/g,specValueId);
                    $("#specValue_table_" + specId).append(html);
                }
            }else{
                if($("#js-specValue-" + specValueId).length > 0){
                    $("#js-specValue-" + specValueId).remove();
                }
            }
            if (this.checked) {
                var objVal = goodsDataProvider.getSpecValue(this.value,specProductEditor.currentSpecList[i].specValues);
                arrTemp[i].push(objVal);
            }
        });
    }
    this.selectedSpecValues = arrTemp;

    specProductEditor.updateProductTable();
};

goodsDataProvider.getSpecValue = function (specValueId,specValueList) {//根据规格值ID获取规格值对象
    for (var i = 0; i < specValueList.length; i++) {
        if (specValueId == specValueList[i].id) {
            return specValueList[i];
        }
    }
    return null;
};

specProductEditor._getTableHeaderHtml = function () {
    var _tmplProductTableHeader = $('#tmplProductTableHeader').html();

    //表格头
    var _specTitleHtml = [];
    for (var i = 0; i < _specList.length; i++) {
        _specTitleHtml.push('<th class="header">' + _specList[i].Spec_Name + '</th>');
    }
    var _headerHtml = _tmplProductTableHeader.replace("{$specs$}", _specTitleHtml.join(''));
    return _headerHtml;
};

specProductEditor.initSpecValueAliasListen = function () {
    $('.sv_custom').keyup(function () {
        var valueid = this.getAttribute('valueid');
        var valuetxt = this.value;
        if (valuetxt == '') {
            valuetxt = $('[name="spec_value_orignal_' + valueid+'"').val();
        }
        $('.cls_spec_value_' + valueid).val(valuetxt);
        $('[name="txtpro_spec_value_' + valueid + '"]').html(valuetxt);
    });
};

specProductEditor.updateProductTable = function () {//更新货品表格
    //获取已经存在的行
    var _recordedRows = specProductEditor.getRecordedRows();

    goodsEditor.showLoadding();

    var _specList = this.currentSpecList;
    var _bodyHtml = '';


    //读取模板
    var _tmplProductTableRow = $('#tmplProductTableRow').html();

    //表格行
    var _rowsHtml = [];

    var arrSelectedSpecVals = this.selectedSpecValues;
    if (arrSelectedSpecVals.length > 0) {
        descarteHelper.init(arrSelectedSpecVals);
        descarteHelper.Process(0);
        var result = descarteHelper.getResult();
        if(result.length > 0){
            $("#batch-update").show();
        }else{
            $("#batch-update").hide();
        }
        $('#specProductList').html('');

        for (var i = 0; i < result.length; i++) {//所有规格的组合
            var specValTds = '';
            var rowIds = [];
            for (var k = 0; k < result[i].length; k++) {
                var objSpecVal = result[i][k];

                //#region 规格别名处理 1013
                //spec_value_custom_23
                //spec_value_orignal_23
                var customName = $('#spec_value_custom_' + objSpecVal.id).val();
                if (customName == '') {
                    customName = $('#spec_value_orignal_' + objSpecVal.id).val();
                }
                if (customName == '') {
                    customName = objSpecVal.value;
                }
                _spec_Value = customName;
                //#endregion

                //{0}为规格值ID，{1}为规格值名称（别名）,{2}位规格ID
                specValTds += J.FormatString('<td class="txt40 c">'
                    + '<input type="hidden" name="pro_spec_value_id_{2}" value="{0}" />'
                    + '<input type="hidden" name="pro_spec_value_{2}" class="cls_spec_value_{0}" value="{1}" />'
                    + '<span style="color:#f96;" name="txtpro_spec_value_{0}">{1}</span>'
                    + '</td>',
                    objSpecVal.id,
                    _spec_Value,
                    objSpecVal.specId);

                rowIds.push(objSpecVal.specId + '|' + objSpecVal.id);
            }
            var clsname = i % 2 != 0 ? "even" : "";
            var ppid = rowIds.join('_');

            var tempRow = _tmplProductTableRow.replace("{$specs$}", specValTds);
            tempRow = tempRow.replace("{$rowclass$}", clsname);
            tempRow = tempRow.replace("{$specids}", ppid).replace("{$specids}",ppid);
            tempRow = tempRow.replace(/{specids}/g, ppid);
            //tempRow = tempRow.replace(/{$specids}/g, ppid);

            //修改模式下赋值，复选框取消后，恢复其在数据库中的数据
            //根据ppid，赋值
            for (var p in this.exsitProductRows) {
                if (p == ppid) {//使用修改前的行内容
                    tempRow = J.FormatString('<tr class="{1}" ppid="{2}"  name="trProductInfo">{0}</tr>',
                        this.exsitProductRows[p],
                        clsname,
                        ppid);
                    break;
                }
            }

            //----------------------------------
            for (var p in _recordedRows) {
                if (p == ppid) {//使用原来的行内容
                    tempRow = J.FormatString('<tr class="{1}" ppid="{2}"  name="trProductInfo">{0}</tr>',
                        _recordedRows[p],
                        clsname,
                        ppid);
                    break;
                }
            }
            //_rowsHtml.push(tempRow);
            $('#specProductList').append(tempRow);
        }
    }

    //_bodyHtml =  _rowsHtml.join('');
    //内容合并
    //$('#specProductList').html(_rowsHtml);

    //设置默认的参数
    specProductEditor.setProductParamas();
};

specProductEditor.checkForm = function () {//提交前的检查表单
    //#region 商家编码检查
    var objResult = { flag: false, message: '' };

    if ($('#hidOpenCustomSpec').val() == '0') {
        objResult.flag = true;
        return objResult;
    }

    var arr_pro_bn = [];
    $('input[name="pro_bn"]').each(function () {
        arr_pro_bn.push(this.value);
    });
    if (arr_pro_bn.length == 0) {
        objResult.message = '货品未录入';
        return objResult;
    }

    for (var i = 0; i < arr_pro_bn.length; i++) {
        if (arr_pro_bn == '') {
            objResult.message = '商家编码不能为空，请检查';
            return objResult;
        }
    }

    var hash = {};
    for (var i = 0; i < arr_pro_bn.length; i++) {
        if (hash[arr_pro_bn[i]]) {
            objResult.message = '存在相同的商家编码"' + arr_pro_bn[i] + '"，请检查';
            return objResult;
        } else {
            hash[arr_pro_bn[i]] = true;
        }
    }

    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var flag = 1;
    recordedRows.each(function(){
        var freez = $(this).find('span[id="pro_freez"]').text();
        var store = $(this).find('input[name="pro_store"]').val();
        var pro_bn = $(this).find('input[name="pro_bn"]').val();
        if(Number(store) < Number(freez)){
            objResult.message = '编码为"' + pro_bn + '"的货品 库存设置必须大于预占库存，请检查';
            flag=0;
            return;
        }
    });
    if(flag == 0){
        return objResult;
    }
    //#endregion

    objResult.flag = true;
    return objResult;
};

specProductEditor.buildProductBnByIndex = function (bn, index, exsitBns) {
    while (_checkBNExsit(bn, index, exsitBns)) {
        index++;
    }
    var newBN = bn + '-' + index;
    exsitBns.push(newBN);
    return newBN;
};

function _checkBNExsit(bn, index, exsitBns) {
    var _bn = bn + '-' + index;
    for (var i = 0; i < exsitBns.length; i++) {
        if (exsitBns[i] == _bn) {
            return true;
        }
    }
    return false;
}

specProductEditor.setBatchProductParams = function(){ //批量设置货品参数
    var arrExsitBNs = [];
    $('input[name="pro_bn"]').each(function (index) {
        if ($.trim($("#batch-seller-id").val()) > 0) {
            var bn = $.trim($("#batch-seller-id").val());
            if (bn != '') {
                //bn += '-' + (index + 1);//这里暂时使用排序号来自动生成
                bn = specProductEditor.buildProductBnByIndex(bn, index + 1, arrExsitBNs);
            }
            this.value = bn;
        }
    });
    $('input[name="pro_store"]').each(function (index) {
        if ($.trim($("#batch-store").val()) > 0) {
            this.value = $.trim($("#batch-store").val());
        }
    });

    $('input[name="pro_mktPrice"]').each(function (index) {
        if ($.trim($("#batch-mktPrice").val()) > 0) {
            this.value = $.trim($("#batch-mktPrice").val());
        }
    });

    $('input[name="pro_cost"]').each(function (index) {
        if ($.trim($("#batch-cost").val()) > 0) {
            this.value = $.trim($("#batch-cost").val());
        }
    });

    $('input[name="pro_weight"]').each(function (index) {
        if ($.trim($("#batch-weight").val()) > 0) {
            this.value = $.trim($("#batch-weight").val());
        }
    });

    $('input[name="pro_price"]').each(function (index) {
        if ($.trim($("#batch-price").val()) > 0) {
            this.value = $.trim($("#batch-price").val());
        }
    });
}

specProductEditor.setProductParamas = function () {//设置货品的价格等各项参数
    // var inputIds = ['txtPrice', 'txtCost', 'txtMktprice', 'txtProductBN', 'txtWeight', 'txtStore'];
    var objExsitBNs = {};
    for (var p in this.exsitProductRows) {
        $(this.exsitProductRows[p]).find('input[name="pro_bn"]').each(function () {
            objExsitBNs[this.value] = true;
        });
    }

    $('input[name="pro_bn"]').each(function (index) {
        objExsitBNs[this.value] = true;
    });

    var arrExsitBNs = [];
    for (var p in objExsitBNs) {
        arrExsitBNs.push(p);
    }

    $('input[name="pro_bn"]').each(function (index) {
        if (this.value == '') {
            var bn = specProductEditor.sellInfo['txtProductBN'];
            if (bn != '') {
                //bn += '-' + (index + 1);//这里暂时使用排序号来自动生成
                bn = specProductEditor.buildProductBnByIndex(bn, index + 1, arrExsitBNs);
            }
            this.value = bn;
        }
    });
/*

    $('input[name="pro_store"]').each(function (index) {
        if (this.value == '') {
            this.value = specProductEditor.sellInfo['txtStore'];
        }
    });

    $('input[name="pro_mktPrice"]').each(function (index) {
        if (this.value == '') {
            this.value = specProductEditor.sellInfo['txtMktPrice'];
        }
    });

    $('input[name="pro_cost"]').each(function (index) {
        if (this.value == '') {
            this.value = specProductEditor.sellInfo['txtCost'];
        }
    });

    $('input[name="pro_weight"]').each(function (index) {
        if (this.value == '') {
            this.value = specProductEditor.sellInfo['txtWeight'];
        }
    });

    $('input[name="pro_price"]').each(function (index) {
        if (this.value == '') {
            this.value = specProductEditor.sellInfo['txtPrice'];
        }
    });
*/

    $("input[name='lvPriceInfo']").each(function(index){
        if(this.value==''){
            this.value = specProductEditor.sellInfo['lvPriceInfogood'];
        }
    })

    $("input[name='lvIntegralInfo']").each(function(index){
        if(this.value==''){
            this.value = specProductEditor.sellInfo['lvIntegralInfogood'];
        }
    })
};

specProductEditor.getSpecDescList = function(){ //组合选中规格数据，用于提交后台
    var inputIds = ['SpecId','ShowType','SpecValue','SpecValueId','SpecImage','GoodsImageIds'];
    var specDescList = [];
    for (var i = 0; i < this.currentSpecList.length; i++) {
        $('input[name="chkSpec_Id_' + this.currentSpecList[i].specId + '"]:checked').each(function () {
            var specDesc = {};
            specDesc[inputIds[0]] = specProductEditor.currentSpecList[i].specId;
            specDesc[inputIds[1]] = specProductEditor.currentSpecList[i].showType;
            var objVal = goodsDataProvider.getSpecValue(this.value, specProductEditor.currentSpecList[i].specValues);
            var customName =$('#spec_value_custom_' + this.value ).val();
            specDesc[inputIds[2]] = customName.length>0?customName:objVal.value;
            specDesc[inputIds[3]] = objVal.id;
            specDesc[inputIds[4]] = objVal.image;
            var imgs = $("#spec_rel_images_" + this.value).val();
            var imagePaths = [];
            if(imgs.length > 0){
                var arrSingle = imgs.split('^');
                for(var j = 0 ; j < arrSingle.length ; j++){
                    var temp = arrSingle[j].split('|');
                    if(temp.length >= 2){
                        imagePaths.push(temp[1]);
                    }
                }
            }
            specDesc[inputIds[5]] =imagePaths;
            specDescList.push(specDesc);
        });
    }
    return JSON.stringify(specDescList);
}

specProductEditor.getRecordList = function(){ //组合货品数据，用于提交后台
    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var propList = [];
    var inputIds = ['productId','price', 'cost', 'mktPrice', 'bn', 'weight', 'store','pdtDesc','props'];
    var specProps = ['SpecId','SpecValueId','SpecValue'];
    recordedRows.each(function(){
        var prop = {};
        prop[inputIds[0]] = $(this).find('input[name="pro_id"]').val();
        prop[inputIds[1]] = $(this).find('input[name="pro_price"]').val();
        prop[inputIds[2]] = $(this).find('input[name="pro_cost"]').val();
        prop[inputIds[3]] = $(this).find('input[name="pro_mktPrice"]').val();
        prop[inputIds[4]] = $(this).find('input[name="pro_bn"]').val();
        prop[inputIds[5]] = $(this).find('input[name="pro_weight"]').val();
        prop[inputIds[6]] = $(this).find('input[name="pro_store"]').val();
        var pdtDest = "";
        var specPropList = [];
        var ppid = $(this).find('input[name="ppid"]').val();
        var ppidList = ppid.split("_");
        $(this).find('span[id!="pro_freez"]').each(function(i){
            if(pdtDest.length != 0){
                pdtDest += ",";
            }
            pdtDest += this.textContent;
            var specProp = {};
            specProp[specProps[0]] = ppidList[i].split("|")[0];
            specProp[specProps[1]] = ppidList[i].split("|")[1];
            specProp[specProps[2]] = this.textContent;
            specPropList.push(specProp);
        })
        prop[inputIds[7]] = pdtDest;
        prop[inputIds[8]] = JSON.stringify(specPropList);
        propList.push(prop);
    });
    return JSON.stringify(propList);
}

specProductEditor.getRecordedRows = function () {//获取已经录入过的规格，保证在组合规格时，这些已经录入过的数据不会被清除掉
    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var rowObjs = {};

    recordedRows.each(function () {
        //var temp = [];
        if (!window.ActiveXObject) {
            $(this).find('input').each(function () {//chrome等浏览器下坑爹的innerHTML不会带上input等的值
                //temp.push(this.name + ":" + this.value);
                switch (this.type) {
                    case 'radio':
                    case 'checkbox':
                        if (this.checked) this.setAttribute('checked', true);
                        else this.removeAttribute('checked');
                        break;
                    default:
                        this.setAttribute('value', this.value);
                }
            });
        }
        rowObjs[this.getAttribute('ppid')] = $(this).html();
    });
    return rowObjs;
};

specProductEditor.changeMarketable = function (obj) {//货品上下架状态更新
    $(obj).prev().val(obj.checked ? '1' : '0');
};
//#endregion

//#region 笛卡尔积助手
var descarteHelper = {};
descarteHelper.result = [];
descarteHelper.zz = [];

descarteHelper.init = function (arrDatas) {
    this.result = [];
    this.zz = arrDatas;
};

descarteHelper.getResult = function () {
    return this.result;
};

descarteHelper.Process = function (arrIndex, aresult) {
    if (arrIndex >= this.zz.length) { this.result.push(aresult); return; };
    var aArr = this.zz[arrIndex];
    if (!aresult) aresult = new Array();
    for (var i = 0; i < aArr.length; i++) {
        var theResult = aresult.slice(0, aresult.length);
        theResult.push(aArr[i]);
        descarteHelper.Process(arrIndex + 1, theResult);
    }
};
//#endregion

//#region 其他
var goodsEditor = {};
//加载loadding
goodsEditor.showLoadding = function (note, autoHide) {
    if (typeof (note) == 'undefined') {
        note = '正在处理';
    }
    $('.swt-messageBox-waittingText').html(note);
    $('#swt-messageBox-overlay').show();
    $('#swt-messageBox-waiting').show();
    setTimeout(function () {
        if (typeof (autoHide) == 'undefined' || autoHide == true) {
            goodsEditor.hideLoadding();
        }
    }, 600);
};

//隐藏loadding
goodsEditor.hideLoadding = function () {
    $('#swt-messageBox-overlay').hide();
    $('#swt-messageBox-waiting').hide();
};
//#endregion

