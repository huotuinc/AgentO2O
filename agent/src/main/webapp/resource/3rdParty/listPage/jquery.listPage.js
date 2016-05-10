/// <reference path="../jquery-1.7.2.min.js" />
/**
* 数据列表插件（带分页）
* Copyright 2015, The Guo MengWen version 1.0.0
* Date:2015-06-15 10:15:00
* 返回数据格式如下：
*   {
	    code:1
	    msg:"成功",
	    recordCount:0,
	    resultData:[
            {
                id:1,
                title:"演示1"
            },
            {
                id:2,
                title:"演示2"
            }
        ]
    }
*/
var js = document.scripts;
var src = js[js.length - 1].src;
var getCurrentJsPath = src.substring(0, src.lastIndexOf("/") + 1);
(function ($) {
    /*初始化数据列表*/
    $.fn.listPage = function (options) {
        var self = this;
        var $listPageHtml = "";
        var paging = {};
        self.checkValue = "";
        self.checkPage = "";
        self.defaults = $.extend({
            skin: "gray",                               /****************样式主题 默认default */
            url: getCurrentJsPath + "testjson.js",      /****************数据来源 请求地址  ,测试数据只是模拟分页 返回json格式数据，格式如testjson.js */
            type: "post",                                /****************请求方式  get/post   请求方式，默认get */
            autoCss: true,                              /****************是否自动添加样式文件 */
            isPager: true,                              /****************是否启用分页模式，默认启用 */
            showCheck: false,                           /****************是否显示复选框,开启后，必须设置key值 */
            doubleLine: true,                           /****************是否显示双行颜色*/
            pageIndex: 1,                               /****************页码 */
            pageSize: 10,                               /****************每页数据*/
            recordCount: 0,                             /****************数据总记录*/
            pageDetail: true,                           /****************页码详情*/
            showNumber: false,                          /****************是否显示页码*/
            data: null,                                 /****************请求参数*/
            init: function () { },                      /****************开始加载数据时执行***/
            completed: function () { },                   /****************数据加载成功后执行*/
            error: function () { },                     /****************数据加载失败后执行 */
            key: "",                                    /****************一般为主键，用于绑定checkbox/radio value值*/
            row: {
                title: ['编号', '标题', "内容", '时间', '操作'],
                column: [
                    { type: "data", text: "id", style: "", format: function (item) { return item[this.text]; } },
                    { type: "data", text: "title", style: "", format: function (item) { return item[this.text]; } },
                    { type: "click", style: "", items: [{ type: "data", text: "content", format: function (item) { return item[this.text]; }, event: function (item) { } }] },
                    { type: "data", text: "createtime", style: "", format: function (item) { return item[this.text]; } },
                    {
                        type: "click", style: "", items: [
                            { type: "text", text: "编辑", format: function (item) { return this.text; }, event: function (item) { } },
                            { type: "text", text: "删除", format: function (item) { return this.text; }, event: function (item) { } }
                        ]
                    }
                ]
            },                                          /****************title:列表列标题数组 column:列表行数据数组---列表数据属性数组, 必须和url返回的数据列名一致  ,type=text/click/data  ，type=text时，直接显示设置的text的值,type=data时，显示数据源中text字段值，type=click时，可点击*/
        }, options);
        if (self.defaults.autoCss) {
            var link = "<link href=\"" + getCurrentJsPath + "theme/" + self.defaults.skin + "/skin.css\" rel=\"stylesheet\" type=\"text/css\" />";
            $("head").append(link);
        }
        $(self).hide();
        var $tempHtmlPath = "" + getCurrentJsPath + "theme/" + self.defaults.skin + "/skin.html";
        var setPaging = function (setting) {
            paging = {
                pageIndex: setting.pageIndex,
                pageSize: setting.pageSize
            }
        }

        /*分页代码*/
        var __loadPaging = function ($this) {
            $listPageHtml = $this;
            var currentSelector = $this.get(0).id;
            var totalCount = $this.defaults.recordCount;
            var pageCount = totalCount % $this.defaults.pageSize == 0 ? parseInt(totalCount / $this.defaults.pageSize) : parseInt(totalCount / $this.defaults.pageSize + 1);
            if ($this.defaults.pageIndex <= 1) $this.defaults.pageIndex = 1;
            if ($this.defaults.pageIndex >= pageCount) $this.defaults.pageIndex = pageCount;


            if ($this.defaults.pageDetail) {
                var numbers_text = "共" + totalCount + "条记录，当前" + $this.defaults.pageIndex + "/" + pageCount + "页，每页" + $this.defaults.pageSize + "条记录";
                $listPageHtml.find(".paging_full_numbers_text").html(numbers_text).show();
            }

            if ($this.defaults.pageIndex == 1 && pageCount == 1) {
                $listPageHtml.find(".paging_full_numbers").html('');
                return;
            }


            var _pagingHtml = '';
            if ($this.defaults.pageIndex > 1)
                _pagingHtml += '<span class="first paginate_button button-white" id="page_1' + currentSelector + '"  data-pageindex="1" ><span>首页</span></span><span id="page_' + ($this.defaults.pageIndex - 1) + currentSelector + '" class="previous paginate_button button-white"  data-pageindex="' + ($this.defaults.pageIndex - 1) + '"  ><span>上一页 </span></span>';
            else
                _pagingHtml += '<span class="first paginate_button paginate_button_disabled button-white button-white_disabled"><span>首页</span></span><span class="previous paginate_button paginate_button_disabled button-white button-white_disabled"><span>上一页</span> </span>';
            if ($this.defaults.showNumber) {
                var number = '<span>';
                for (m = $this.defaults.pageIndex - 4; m <= $this.defaults.pageIndex + 4; m++) {
                    if (m > 0 && m <= pageCount) {
                        number += '<span id="page_' + m + currentSelector + '" ' + ($this.defaults.pageIndex == m ? ' class="paginate_active button-white button-white_disabled"' : 'class="paginate_button button-white"') + ' data-pageindex="' + m + '"  ><span>' + m + '</span></span>';
                    }
                }
                number += "</span>";
                _pagingHtml += number;
            }
            if ($this.defaults.pageIndex < pageCount)
                _pagingHtml += '<span  id="page_' + ($this.defaults.pageIndex + 1) + currentSelector + '" class="next paginate_button button-white"  data-pageindex="' + ($this.defaults.pageIndex + 1) + '" ><span>下一页</span></span><span id="page_' + pageCount + currentSelector + '" class="last paginate_button button-white"  data-pageindex="' + pageCount + '" ><span>最后一页</span></span>';
            else
                _pagingHtml += '<span class="next paginate_button paginate_button_disabled button-white button-white_disabled"><span>下一页</span></span><span class="last paginate_button paginate_button_disabled button-white button-white_disabled"><span>最后一页</span></span>';
            $listPageHtml.find(".paging_full_numbers").html(_pagingHtml);


            /****绑定事件****/
            if ($this.defaults.pageIndex > 1) {
                $listPageHtml.find(".paging_full_numbers").find("#page_1" + currentSelector).unbind("click").bind("click", function () {
                    __checkValue($this);
                    $this.defaults.pageIndex = parseInt($(this).attr("data-pageindex"));
                    __getDataList($this);
                    $(this).unbind("click");
                });
                $listPageHtml.find(".paging_full_numbers").find("#page_" + ($this.defaults.pageIndex - 1) + currentSelector).unbind("click").bind("click", function () {
                    __checkValue($this);
                    $this.defaults.pageIndex = parseInt($(this).attr("data-pageindex"));
                    __getDataList($this);
                    $(this).unbind("click");
                });
            }
            for (m = $this.defaults.pageIndex - 4; m <= $this.defaults.pageIndex + 4; m++) {
                if (m > 0 && m <= pageCount) {
                    $listPageHtml.find(".paging_full_numbers").find("#page_" + m + currentSelector).unbind("click").bind("click", function () {
                        __checkValue($this);
                        $this.defaults.pageIndex = parseInt($(this).attr("data-pageindex"));
                        __getDataList($this);
                        $(this).unbind("click");
                    });
                }
            }
            if ($this.defaults.pageIndex < pageCount) {
                $listPageHtml.find(".paging_full_numbers").find("#page_" + ($this.defaults.pageIndex + 1) + currentSelector).unbind("click").bind("click", function () {
                    __checkValue($this);
                    $this.defaults.pageIndex = parseInt($(this).attr("data-pageindex"));
                    __getDataList($this);
                    $(this).unbind("click");
                });
                $listPageHtml.find(".paging_full_numbers").find("#page_" + pageCount + currentSelector).unbind("click").bind("click", function () {
                    __checkValue($this);
                    $this.defaults.pageIndex = parseInt($(this).attr("data-pageindex"));
                    __getDataList($this);
                    $(this).unbind("click");
                });
            }
        }
        /*获取数据*/
        var __getDataList = function ($this) {
            $listPageHtml = $this;
            if (typeof $this.defaults.init == 'function')
                $this.defaults.init();
            /*判断是否开启分页功能*/
            if ($this.defaults.isPager) {
                /*设置当前分页数据*/
                setPaging($this.defaults);
                $this.defaults.data = $.extend($this.defaults.data, paging);
            }

            $.ajax({
                type: $this.defaults.type,
                url: $this.defaults.url,
                data: $this.defaults.data,
                dataType: "json",
                success: function (json) {
                    /*获取当前元素id*/
                    var currentSelector = $this.get(0).id;
                    if (json.code == 1) {
                        var $bodyHtmlList = "";
                        var $bodyHtml = $listPageHtml.find(".listTr");
                        var $bodyTdHtml = $listPageHtml.find(".listTd");
                        var $bodyCheckBox = $listPageHtml.find(".bodyCheckBox");
                        var ids = []
                        var itemidx = 0;
                        if (typeof $this.defaults.row.column != 'undefined' && $this.defaults.row.column.length > 0) {
                            if (typeof json.resultData != 'undefined' && json.resultData.length > 0) {
                                $.each(json.resultData, function (i, item) {
                                    var _bodyHtml = $bodyHtml.html();
                                    var _tdHtmlList = '';
                                    if ($this.defaults.showCheck) {
                                        var _box = $bodyCheckBox.html();
                                        if (typeof _box != 'undefined' && _box != null && $this.defaults.key.length > 0) {
                                            _box = _box.replace(/{selector}/gm, currentSelector);
                                            _tdHtmlList += _box.replace(/{key}/gm, item[$this.defaults.key]);
                                        }
                                    }
                                    $.each($this.defaults.row.column, function (idx, r) {
                                        var _html = $bodyTdHtml.html();
                                        /*设置样式*/
                                        if (typeof r.style != 'undefined' && r.style.length > 0)
                                            _html = _html.replace(/{css}/gm, r.style);
                                        else
                                            _html = _html.replace(/{css}/gm, "");

                                        switch (r.type) {
                                            case "text": /*文本类型*/
                                                _tdHtmlList += _html.replace(/{tbody}/gm, r.text);
                                                return;
                                            case "data":  /*绑定类型*/
                                                var _value = "";
                                                if (typeof r.format == 'function')
                                                    _value = r.format(item);
                                                else {
                                                    _value = typeof item[r.text] == 'undefined' ? "--" : item[r.text];
                                                }
                                                _tdHtmlList += _html.replace(/{tbody}/gm, _value);
                                                return;
                                            case "click":  /*点击事件类型*/
                                                {
                                                    var _hrefHtml = "";
                                                    $.each(r.items, function (o, v) {
                                                        if (v.type == "data") {
                                                            var _value = "";
                                                            if (typeof v.format == 'function')
                                                                _value = v.format(item);
                                                            else
                                                                _value = typeof item[v.text] == 'undefined' ? "--" : item[v.text];
                                                            _hrefHtml += '<span class="spanEvent_' + currentSelector + "_" + itemidx + '" data-idx="' + itemidx + '">' + _value + '</span> ';
                                                        }
                                                        else if (v.type == "text") {
                                                            _hrefHtml += '<span class="spanEvent_' + currentSelector + "_" + itemidx + '" data-idx="' + itemidx + '">' + v.text + '</span> ';
                                                        }
                                                        var dataItems = {
                                                            idx: ".spanEvent_" + currentSelector + "_" + itemidx,
                                                            item: item,
                                                            click: v.event
                                                        };
                                                        ids.push(dataItems);
                                                        ++itemidx;
                                                    });
                                                    _tdHtmlList += _html.replace(/{tbody}/gm, _hrefHtml);
                                                    return;
                                                }
                                            default: {
                                                _tdHtmlList += _html.replace(/{tbody}/gm, "");
                                                console.log("is not find type");
                                            }
                                        }
                                    });
                                    if ($this.defaults.doubleLine)
                                        _bodyHtml = _bodyHtml.replace(/{clsName}/gm, (i + 1) % 2 == 0 ? 'over' : 'odd');
                                    else
                                        _bodyHtml = _bodyHtml.replace(/{clsName}/gm, 'odd');
                                    $bodyHtmlList += _bodyHtml.replace(/{tbodyHtml}/gm, _tdHtmlList);
                                });
                            }
                        }
                        $listPageHtml.find(".listBody").html($bodyHtmlList).show();
                        if (ids.length > 0) {
                            for (var i = 0; i < ids.length; i++) {
                                /*绑定事件*/
                                $listPageHtml.find(".listBody").find(ids[i].idx).bind("click", function () {
                                    var idx = parseInt($(this).attr("data-idx"));
                                    ids[idx].click(ids[idx].item);
                                });
                            }
                        }
                        if ($this.defaults.showCheck) {
                            /*选择checkbox时触发事件*/
                            $("input[name='chb_" + currentSelector + "']").bind("change", function () {
                                __checkValue($this);
                            });
                        }
                        $this.defaults.recordCount = json.recordCount;
                        if ($this.defaults.isPager)
                            __loadPaging($this);
                        if ($this.defaults.showCheck)
                            __setChecked($this);
                    }
                    else
                        $listPageHtml.find(".paging_full_numbers").hide();
                    if (typeof $this.defaults.completed == 'function')
                        $this.defaults.completed(json);
                },
                error: function (e) {
                    if (typeof $this.defaults.error == 'function')
                        $this.defaults.error(e);
                }
            });
        }
        /*初始化模版*/
        var __init = function () {
            $.get($tempHtmlPath, {}, function (html) {
                var $headHtml = '', $headHtmlList = "";
                $listPageHtml = $(self).html(html);
                $headHtml = $listPageHtml.find(".listHead");
                var currentSelector = self.get(0).id;
                if (typeof self.defaults.row.title != 'undefined' && self.defaults.row.title.length > 0) {
                    if (self.defaults.showCheck) {
                        var _box = $listPageHtml.find(".headCheckBox").html();
                        if (typeof _box != 'undefined' && _box != null && self.defaults.key.length > 0)
                            $headHtmlList += _box.replace(/{selector}/gm, currentSelector);
                        else
                            console.log("key is not empty");
                    }
                    $.each(self.defaults.row.title, function (i, item) {
                        var _html = $headHtml.html();
                        $headHtmlList += _html.replace(/{thead}/gm, item);
                    });
                }
                $listPageHtml.find(".listHead").html($headHtmlList);
                $listPageHtml.find(".listBody").hide();
                if (!self.defaults.isPager)
                    $listPageHtml.find(".paging_full_numbers").hide();
                else
                    $listPageHtml.find(".paging_full_numbers").show();
                $(self).show();

                if (self.defaults.showCheck) {
                    /*全部选择触发事件*/
                    $("input[name='chbAll_" + currentSelector + "']").bind("change", function () {
                        if ($(this).attr("checked")) {
                            $("input[name='chb_" + currentSelector + "']").attr("checked", "checked");
                        }
                        else {
                            $("input[name='chb_" + currentSelector + "']").removeAttr("checked");
                        }
                        __checkValue(self);
                    });
                }
                __getDataList(self);
            });
        }
        /*移除指定数组值*/
        Array.prototype.remove = function (value) {
            var dx = __getArrayIndex(this, value);
            if (isNaN(dx) || dx > this.length) { return false; }
            for (var i = 0, n = 0; i < this.length; i++) {
                if (this[i] != this[dx]) {
                    this[n++] = this[i]
                }
            }
            this.length -= 1;
        }

        /*根据数组指定内容获取对应的索引*/
        var __getArrayIndex = function (arrs, value) {
            var index = -1;
            for (var i = 0; i < arrs.length; i++) {
                if (arrs[i] == value) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        /*获取选中的数据*/
        var __checkValue = function ($this) {
            var currentSelector = $this.get(0).id;
            var _arr = [];
            if (typeof $this.checkPage != 'undefined')
                _arr = $this.checkPage.split("|");
            else
                _currPage = "";
            if (typeof $this.checkValue == 'undefined')
                $this.checkValue = "";
            if ($.inArray($this.defaults.pageIndex.toString(), _arr) == -1) {
                var chbVal = "";
                $("input[name='chb_" + currentSelector + "']").each(function (v, item) {
                    if ($(this).attr("checked")) {
                        if (typeof $this.checkValue != 'undefined') {
                            var _currV = $this.checkValue.split("|");
                            if ($.inArray($(this).val(), _currV) == -1) {
                                chbVal += $(this).val() + "|";
                            }
                        }
                        else
                            chbVal += $(this).val() + "|";
                    }
                    else
                        $this.checkValue = $this.checkValue.replace("|" + $(this).val() + "|", "|");
                });
                $this.checkValue += chbVal;
                if (chbVal.length > 0) {
                    $this.checkPage += $this.defaults.pageIndex + "|";
                }
            }
            else {
                $this.checkValue = "|" + $this.checkValue;
                $("input[name='chb_" + currentSelector + "']").each(function (v, item) {
                    if (!$(this).attr("checked")) {
                        $this.checkValue = $this.checkValue.replace("|" + $(this).val() + "|", "|");
                    }
                    else {
                        if (typeof $this.checkValue != 'undefined') {
                            var _currV = $this.checkValue.split("|");
                            if ($.inArray($(this).val(), _currV) == -1) {
                                $this.checkValue += $(this).val() + "|";
                            }
                        }
                    }
                });

                $this.checkValue = $this.checkValue.substr(1);
            }
            return $this.checkValue;
        }

        /*给当前页checkbox设置默认值*/
        var __setChecked = function ($this) {
            var currentSelector = $this.get(0).id;
            var _arr = [];
            if (typeof $this.checkValue != 'undefined')
                _arr = $this.checkValue.split("|");
            else
                $this.checkValue = "";
            if ($this.checkValue.length <= 0) {
                $("input[name='chbAll_" + currentSelector + "']").removeAttr("checked");
                return false;
            }
            else
                $("input[name='chbAll_" + currentSelector + "']").attr("checked", "checked");
            if (_arr.length > 0) {
                $("input[name='chb_" + currentSelector + "']").each(function (v, item) {
                    if ($.inArray($(this).val(), _arr) != -1) {
                        $(this).attr("checked", "checked");
                    }
                    else {
                        $(this).removeAttr("checked");
                        $("input[name='chbAll_" + currentSelector + "']").removeAttr("checked");
                    }
                });
            }
            else {
                $("input[name='chbAll_" + currentSelector + "']").removeAttr("checked");
            }
            return true;
        }

        /*刷新列表数据*/
        self.reload = function () {
            __getDataList(this);
        }
        /*搜索数据*/
        self.search = function (data) {
            ///<summary>
            ///搜索事件
            ///</summary>
            ///<param name="data" type="Object">自定义搜索参数对象</param>
            this.checkValue = "";
            this.checkPage = "";
            __setChecked(this);
            if (typeof data != 'undefined' && typeof data === "object")
                this.defaults.data = $.extend(this.defaults.data, data);
            this.defaults.pageIndex = 1;
            __getDataList(this);
        }

        /*获取checkbox选中的值*/
        self.getCheckValue = function () {
            return __checkValue(this);
        }
        /*获取当前对象数据*/
        self.getCurrentObject = function () {
            return this.defaults;
        }
        __init();
        return self;
    }
})(jQuery);
