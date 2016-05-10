$.extend({

});

$.fn.extend({
    trim: function () { return $.trim(this.val()); },
    lTrim: function () { return this.val().replace(/^\s+/, ''); },
    rTrim: function () { return this.val().replace(/\s+$/, ''); },

    setDisabled: function (disabled) {
        return this.each(function () { $(this).attr('disabled', disabled).css('opacity', disabled ? 0.5 : 1.0); });
    },
    setReadOnly: function (readonly) {
        return this.each(function () { $(this).attr('readonly', readonly).css('opacity', readonly ? 0.5 : 1.0); });
    },
    setChecked: function (checked, value) {
        return this.each(function () { if (value == undefined) { $(this).attr('checked', checked); } else if ($(this).val() == value.toString()) { $(this).attr('checked', checked); } });
    }
});

var J = J || {};

$.extend(J, {
    IsIE: $.browser.msie != undefined,
    IsIE6: $.browser.msie && parseInt($.browser.version) === 6,

    CopyText: function (obj) { var str = J.IsElement(obj) ? obj.value : ($(obj).size() > 0 ? $(obj).val() : obj); if (window.clipboardData && clipboardData.setData && window.clipboardData.setData("Text", str)) { return true; } else { if (J.IsElement(obj)) o.select(); return false; } },
    AddBookMark: function (url, title) { try { if (window.sidebar) { window.sidebar.addPanel(title, url, ''); } else if (J.IsIE) { window.external.AddFavorite(url, title); } else if (window.opera && window.print) { return true; } } catch (e) { alert("Your browser does not support it."); } },
    SetHomePage: function (url) { try { document.body.style.behavior = 'url(#default#homepage)'; document.body.setHomePage(url); } catch (e) { if (window.netscape) { try { netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect"); } catch (e) { alert("Your browser does not support it."); } var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch); prefs.setCharPref('browser.startup.homepage', url); } } },

    GetCookie: function (name) { var r = new RegExp('(^|;|\\s+)' + name + '=([^;]*)(;|$)'); var m = document.cookie.match(r); return (!m ? '' : decodeURIComponent(m[2])); },
    SetCookie: function (name, value, expire, domain, path) { var s = name + '=' + encodeURIComponent(value); if (!J.IsUndefined(path)) s = s + '; path=' + path; if (expire > 0) { var d = new Date(); d.setTime(d.getTime() + expire * 1000); if (!J.IsUndefined(domain)) s = s + '; domain=' + domain; s = s + '; expires=' + d.toGMTString(); } document.cookie = s; },
    RemoveCookie: function (name, domain, path) { var s = name + '='; if (!J.IsUndefined(domain)) s = s + '; domain=' + domain; if (!J.IsUndefined(path)) s = s + '; path=' + path; s = s + '; expires=Fri, 02-Jan-1970 00:00:00 GMT'; document.cookie = s; },

    IsUndefined: function (obj) { return typeof obj == 'undefined'; },
    IsObject: function (obj) { return typeof obj == 'object'; },
    IsNumber: function (obj) { return typeof obj == 'number'; },
    IsString: function (obj) { return typeof obj == 'string'; },
    IsElement: function (obj) { return obj && obj.nodeType == 1; },
    IsFunction: function (obj) { return typeof obj == 'function'; },
    IsArray: function (obj) { return Object.prototype.toString.call(obj) === '[object Array]'; },

    IsInt: function (str) { return /^-?\d+$/.test(str); },
    IsFloat: function (str) { return /^(-?\d+)(\.\d+)?$/.test(str); },
    IsIntPositive: function (str) { return /^[0-9]*[1-9][0-9]*$/.test(str); },
    IsFloatPositive: function (str) { return /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/.test(str); },
    IsLetter: function (str) { return /^[A-Za-z]+$/.test(str); },
    IsChinese: function (str) { return /^[\u0391-\uFFE5]+$/.test(str); },
    IsZipCode: function (str) { return /^[1-9]\d{5}$/.test(str); },
    IsEmail: function (str) { return /^[A-Z_a-z0-9-\.]+@([A-Z_a-z0-9-]+\.)+[a-z0-9A-Z]{2,4}$/.test(str); },
    IsMobile: function (str) { return /^1[1-9][0-9]{9}$/.test(str); },
    IsTel: function (str) { return /^((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)$/.test(str); },
    IsUrl: function (str) { return /^(http:|ftp:)\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"])*$/.test(str); },
    IsIpAddress: function (str) { return /^(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5]).(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5]).(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5]).(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5])$/.test(str); },
    IsMoney: function (str) { return /^(0|-?[0-9]\d*(\.\d{1,2})?)$/.test(str); },

    Encode: function (str) { return encodeURIComponent(str); },
    Decode: function (str) { return decodeURIComponent(str); },
    FormatString: function () { if (arguments.length == 0) return ''; if (arguments.length == 1) return arguments[0]; var args = J.CloneArray(arguments); args.splice(0, 1); return arguments[0].replace(/{(\d+)?}/g, function ($0, $1) { return args[parseInt($1)]; }); },

    EscapeHtml: function (str) { return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); },
    UnEscapeHtml: function (str) { return str.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&nbsp;/g, " ").replace(/&quot;/g, "\"").replace(/&amp;/g, "&"); },
    FilterHtml: function (str) { str = str.replace(/\<(.*?)\>/g, '', str); str = str.replace(/\<\/(.*?)\>/g, '', str); return str; },

    CloneArray: function (arr) { var cloned = []; for (var i = 0, j = arr.length; i < j; i++) { cloned[i] = arr[i]; } return cloned; },

    GetKeyCode: function (e) { var evt = window.event || e; return evt.keyCode ? evt.keyCode : evt.which ? evt.which : evt.charCode; },
    CertainNumberContainPoint: function (event) { var e = event ? event : (window.event ? window.event : null); if (e.keyCode == 13) { e.keyCode = 9; } if ((e.keyCode < 48 || e.keyCode > 57)) { if ((e.keyCode != 8)) { if ((e.keyCode < 96 || e.keyCode > 105) && e.keyCode != 190 && e.keyCode != 110) { event.returnValue = false; } } } },
    CertainNumber: function (event) { var e = event ? event : (window.event ? window.event : null); if (e.keyCode == 13) { e.keyCode = 9; } if ((e.keyCode < 48 || e.keyCode > 57)) { if ((e.keyCode != 8)) { if ((e.keyCode < 96 || e.keyCode > 105)) { event.returnValue = false; } } } },
    EnterSubmit: function (e, v) { if (J.GetKeyCode(e) == 13) { if (J.IsFunction(v)) { v(); } else if (J.IsString(v)) { $(v)[0].click(); } } },
    CtrlEnterSubmit: function (e, v) { var evt = window.event || e; if (evt.ctrlKey && J.GetKeyCode(evt) == 13) { if (J.IsFunction(v)) { v(); } else if (J.IsString(v)) { $(v)[0].click(); } } },

    GetUrlQuery: function (key, Decode, url) { url = url || window.location.href; if (url.indexOf("#") !== -1) url = url.substring(0, url.indexOf("#")); var rts = [], rt; queryReg = new RegExp("(^|\\?|&)" + key + "=([^&]*)(?=&|#|$)", "g"); while ((rt = queryReg.exec(url)) != null) { if (Decode && Decode == true) rts.push(DecodeURIComponent(rt[2])); else rts.push(rt[2]); } return rts.length == 0 ? '' : (rts.length == 1 ? rts[0] : rts); },

    PostJson: function (url, data, success, error, option) {
        var op = {
            type: 'POST',
            url: url,
            data: data,
            dataType: 'json',
            cache: false,
            success: function (data, textStatus) {
                if (data == null || data == undefined) {
                    if (typeof error == 'function') error();
                }
                else {
                    if (typeof error == 'function') success(data, textStatus);
                }
            },
            error: error
        };
        $.extend(op, option);
        $.ajax(op);
    },
    GetJsonRespons: function (url, data, success, error, type) {
        var op = {
            type: type,
            url: url,
            data: data,
            dataType: 'json',
            success: function (data) {
                if (data == null || data == undefined) {
                    if (typeof error == 'function') error();
                } else {
                    if (typeof success == 'function') success(data);
                }
            },
            error: error
        };
        $.ajax(op);
    },
    AjaxJson: function (url, data, success, error, option) {
        var op = {
            type: 'POST',
            url: url,
            data: data,
            dataType: 'json',
            cache: false,
            success: success,
            error: error
        };

        $.extend(op, option);
        $.ajax(op);
    },
    GetJson: function (url, data, success, error, option) {
        var op = {
            type: "get",
            async: false,
            url: url,
            data: data,
            dataType: "jsonp",
            jsonpCallback: "jsonpcallback",
            success: function (d, textStatus) {
                if (d == null || d == undefined) {
                    if (typeof error == 'function') error();
                }
                else {
                    if (typeof error == 'function') success(d, textStatus);
                }
            },
            error: error
        };
        $.extend(op, option);
        $.ajax(op);
    },
    GetAjax: function (url, data, success, error, type, option) {
        type = type == "post" ? type : "get";
        var op = {
            type: type,
            url: url,
            data: data,
            success: function (d, textStatus) {
                if (d == null || d == undefined) {
                    if (typeof error == 'function') error();
                }
                else {
                    if (typeof error == 'function') success(d, textStatus);
                }
            },
            error: error
        };
        $.extend(op, option);
        $.ajax(op);
    },
    ShowDialog: function (contentid, title, func1, func2) {
        $("#" + contentid).dialog({
            modal: true,
            width: 'auto',
            height: 'auto',
            resizable: false,
            title: title,
            position: ["50%", 180],
            buttons: {
                "确定": func1,
                "取消": func2
            },
            show: {
                effect: "drop",
                direction: "up"
            },
            hide: {
                effect: "drop",
                direction: "up"
            }
        });
    },
    ShowDialog2: function (contentid, title, func1, func2, position) {
        $("#" + contentid).dialog({
            modal: true,
            width: 'auto',
            height: 'auto',
            resizable: false,
            title: title,
            position: position,
            buttons: {
                "确定": func1,
                "取消": func2
            },
            show: {
                effect: "drop",
                direction: "up"
            },
            hide: {
                effect: "drop",
                direction: "up"
            }
        });
    },
    PreloadImages: function () {
        for (var i = 0; i < arguments.length; i++) {
            var img = new Image();
            img.src = arguments[i];
        }
    },
    getQueryString: function (strName) {
        ///<summary>
        ///获取url 参数
        ///</summary>
        var strHref = window.document.location.href;
        var intPos = strHref.indexOf("?");
        var strRight = strHref.substr(intPos + 1);
        var arrTmp = strRight.split("&");
        for (var i = 0; i < arrTmp.length; i++) {
            var arrTemp = arrTmp[i].split("=");
            if (arrTemp[0].toUpperCase() == strName.toUpperCase()) return arrTemp[1];
        }
        if (arguments.length == 1)
            return "";
        if (arguments.length == 2)
            return arguments[1];
    },
    PopupIFrame: function (url, PopupTitle, width, height, iframeID, buttons, iframeScrolling, bottomText, callback) {
        ///<summary>
        ///弹出层（iframe）,显示页面必须有submitMenu函数
        ///</summary>
        ///<param name="url" type="String">弹出层显示页面地址</param>
        ///<param name="PopupTitle" type="String">弹出层标题</param>
        ///<param name="width" type="Number">弹出层宽度</param>
        ///<param name="height" type="Number">弹出层高度</param>
        ///<param name="iframeID" type="String">弹出层ID</param>
        ///<param name="buttons" type="jQuery">弹出层显示页面地址,如：{ "确定": true, "关闭窗体": false }</param>
        ///<param name="iframeScrolling" type="String">弹出层是否显示滚动条,如'auto','yes','no'</param>
        ///<param name="bottomText" type="String">窗口的按钮左边的内容，当没有按钮时此设置无效</param>
        ///<param name="callback" type="jQuery">回调函数</param>
        $.jBox.open("iframe:" + url,
                PopupTitle,
                width,
                height,
                {
                    id: iframeID,
                    buttons: buttons,
                    bottomText: bottomText,
                    showScrolling: false,
                    iframeScrolling: iframeScrolling,
                    submit: function (v, h, f) {
                        if (v == true) {
                            var result = $.jBox.getIframe(iframeID).contentWindow.submitMenu(); //调用子窗口里的方法如：myFrame.window.functionName();
                            if (result == -1)
                                return false;
                            if (callback) {
                                callback(result);
                            }
                            else
                                return false;
                        }
                        return true;
                    }
                }
             );
    },
    JumpMicroSitePanel: function (customerid, type) {
        ///<summary>
        ///跳转微网站面板
        ///</summary>
        ///<param name="customerid" type="Number">客户ID</param>
        ///<param name="type" type="Number">1跳转微网站面板</param>
        var url = J.FormatString("/3rdParty/ajax/FunctionMudeleHandler.aspx");
        var datas = {
            action: "IsJudgeOldUser",
            customerid: customerid
        }
        $.ajax({
            type: "post",
            async: false,
            url: url,
            data: datas,
            success: function (d) {
                if (d.code == 1) {
                    switch (type) {
                        case 1: {
                            Index.Open("/CustomerManage/MicroSiteConfigListV2.aspx?customerid=" + customerid);
                            break;
                        }
                    }
                }
                else {
                    switch (type) {
                        case 1: {
                            Index.Open("/CustomerManage/MicroSiteConfigList.aspx?customerid=" + customerid);
                            break;
                        }
                    }
                }
            },
            error: function (e) {
                switch (type) {
                    case 1: {
                        Index.Open("/CustomerManage/MicroSiteConfigList.aspx?customerid=" + customerid);
                        break;
                    }
                }
            }

        });
    },
    Dialog: function (options) {
        ///<summary>
        ///弹出层（DIV层）,显示页面必须引用jquery-ui JS库
        ///</summary>
        var defaults = {
            control: "control",
            width: 800,
            height: 400,
            title: "",
            top: 100,
            left: "50%",
            submit: function () { },
            open: function () { },
            close: function () { }
        }
        if (options)
            $.extend(defaults, options);

        $("#" + defaults.control).dialog({
            open: function () {
                $(".ui-widget-header").css("background", "none");
                $(".ui-dialog-title").css({ "float": "none", "color": "rgb(122, 117, 126)" });
                $(".ui-state-default").css({ "background": "#3399FF", color: "#fff" }).addClass("button");
                $(".ui-widget").css("font-size", "1em");
                defaults.open();
            },
            close: function () { defaults.close(); },
            modal: true,
            width: defaults.width,
            height: defaults.height,
            resizable: false,
            draggable: false,
            title: defaults.title,
            position: [defaults.left, defaults.top],
            buttons: {
                "确定": function () {
                    defaults.submit();
                },
                "取消": function () {
                    $(this).dialog("close");
                }
            },
            show: {
                effect: "drop",
                direction: "up"
            },
            hide: {
                effect: "drop",
                direction: "up"
            }
        });
    },
    formatFloat: function (src, pos) {
        return Math.round(src * Math.pow(10, pos)) / Math.pow(10, pos);
    }
});

function getIEVersion() {
    var e = -1;
    if (navigator.appName == "Microsoft Internet Explorer") {
        var t = navigator.userAgent,
        n = new RegExp("MSIE ([0-9]{1,}[.0-9]{0,})");
        n.exec(t) != null && (e = parseFloat(RegExp.$1))
    }
    return e;
}

$.fn.OnlyNum = function () {//文本框只允许输入数字，使用：$('.OnlyNum').OnlyNum()
    $(this).live("keydown",
    function (e) {
        if (e.ctrlKey) return !0;
        var t = t || window.event,
        n = t.charCode || t.keyCode;
        n != 8 && n != 9 && n != 46 && (n < 37 || n > 40) && (n < 48 || n > 57) && (n < 96 || n > 105) && (t.preventDefault ? t.preventDefault() : t.returnValue = !1, $(this).show("highlight", 150))
    }).live("focus",
    function () {
        this.style.imeMode = "disabled"
    }).live("blur",
    function () {
        $(this).val($(this).val().replace(/[^\d]/g, ""))
    }),
    getIEVersion() > 0 && $(this).bind("beforepaste",
    function (e) {
        clipboardData.setData("text", clipboardData.getData("text").replace(/[^\d]/g, ""))
    })
};

$.fn.OnlyFloat = function () {//文本框只允许输入浮点类型
    $(this).live("keydown",
    function (e) {
        function r(e, t) {
            t.preventDefault ? t.preventDefault() : t.returnValue = !1,
            $(e).show("highlight", 150)
        }
        if (e.ctrlKey) return !0;
        var t = t || window.event,
        n = t.charCode || t.keyCode;
        n == 110 || n == 190 ? ($(this).val().indexOf(".") >= 0 || !$(this).val().length) && r(this, t) : n != 8 && n != 9 && n != 46 && (n < 37 || n > 40) && (n < 48 || n > 57) && (n < 96 || n > 105) && r(this, t)
    }).live("focus",
    function () {
        this.style.imeMode = "disabled"
    }).live("blur",
    function () {
        $(this).val($(this).val().replace(/[^\d.]/g, "").replace(/^\./g, ""))
    }),
    getIEVersion() > 0 && $(this).bind("beforepaste",
    function (e) {
        clipboardData.setData("text", clipboardData.getData("text").replace(/(^[0-9]([.][0-9]{1,2})?$)|(^1[0-9]([.][0-9]{1,2})?$)|(^2[0-3]([.][0-9]{1,2})?$)|(^24([.]0{1,2})?$)/g, ""))
    })
};





// 对Date的扩展，将 Date 转化为指定格式的String 
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1,                 //月份 
        "d+": this.getDate(),                    //日 
        "h+": this.getHours(),                   //小时 
        "m+": this.getMinutes(),                 //分 
        "s+": this.getSeconds(),                 //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds()             //毫秒 
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}





var Paging = {};
/***分页代码 begin**/
var LoadObj = null;
Paging.LoadPaging = function (obj, pageIndex, pageSize, totalCount, callback) {
    ///<summary>
    ///分页函数 优化人：郭孟稳
    ///</summary>
    ///<param name="obj" type="Object">输出元素位置</param>
    ///<param name="pageIndex" type="String">当前页码</param>
    ///<param name="pageSize" type="jQuery">每页数量</param>
    ///<param name="totalCount" type="String">数据总数</param>
    ///<param name="callback" type="jQuery">回调函数</param>
    if (obj) {
        LoadObj = obj;
        if (totalCount == 0) {
            obj.html('');
            return;
        }
        var pageCount = totalCount;// totalCount % pageSize == 0 ? parseInt(totalCount / pageSize) : parseInt(totalCount / pageSize + 1);
        if (pageIndex <= 1) pageIndex = 1;
        if (pageIndex >= pageCount) pageIndex = pageCount;
        if (pageIndex == 1 && pageCount == 1) {
            obj.html('');
            return;
        }
        var html = '';
        if (pageIndex > 1)
            html += '<span class="first paginate_button"  onclick="Paging.goto(1,' + pageSize + ',' + totalCount + ',' + callback + ')">第一页</span><span class="previous paginate_button "  onclick="Paging.goto(' + (pageIndex - 1) + ',' + pageSize + ',' + totalCount + ',' + callback + ')">上一页</span>';
        else
            html += '<span class="first paginate_button_end">第一页</span><span class="previous paginate_button_end ">上一页</span>';
        var number = '<span>';
        for (m = pageIndex - 4; m <= pageIndex + 4; m++) {
            if (m > 0 && m <= pageCount) {
                number += '<span ' + (pageIndex == m ? ' class="paginate_active"' : 'class="paginate_button"') + ' onclick="Paging.goto(' + m + ',' + pageSize + ',' + totalCount + ',' + callback + ')">' + m + '</span>';
            }
        }
        number += "</span>";
        html += number;
        if (pageIndex < pageCount)
            html += '<span class="next paginate_button"  onclick="Paging.goto(' + (pageIndex + 1) + ',' + pageSize + ',' + totalCount + ',' + callback + ')">下一页</span><span class="last paginate_button "  onclick="Paging.goto(' + pageCount + ',' + pageSize + ',' + totalCount + ',' + callback + ')">最后一页</span>';
        else
            html += '<span class="next paginate_button_end">下一页</span><span class="last paginate_button_end " >最后一页</span>';
        obj.html(html);
    }
}
Paging.goto = function (pageIndex, pageSize, totalCount, callback) {
    if (callback)
        callback(LoadObj, pageIndex, pageSize, totalCount);
}