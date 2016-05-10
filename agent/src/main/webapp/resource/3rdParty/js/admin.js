
var Admin = {};
var Login = {};
var Index = {};
var Pager = {};
var CheckBox = {};

/* -----  Admin ----- */

Admin.Loaded = false;
Admin.Init = function () {
    // 其它初始化
    $('a').live('focus', function () { $(this).blur(); });
    $('input[type=radio]').live('focus', function () { $(this).blur(); });
    $('input[type=checkbox]').live('focus', function () { $(this).blur(); });
    $('input[type=checkbox]').css('border', 'none');
    $('.btn-middle').css({ 'margin-bottom': (J.IsIE6 ? 1 : 3) + 'px' }); //修改按钮水平对齐
    // 隔行变色
    $('table.data-table tr:even').addClass('even');

    // 三态按钮
    $('.btn')
        .live('mousedown', function () { $(this).addClass('btn-active'); })
        .live('mouseup', function () { $(this).removeClass('btn-active'); })
        .live('mouseover', function () { $(this).addClass('btn-hover'); })
        .live('mouseout', function () { $(this).removeClass('btn-active'); $(this).removeClass('btn-hover'); });
    $('.btn-lit')
        .live('mousedown', function () { $(this).addClass('btn-lit-active'); })
        .live('mouseup', function () { $(this).removeClass('btn-lit-active'); })
        .live('mouseover', function () { $(this).addClass('btn-lit-hover'); })
        .live('mouseout', function () { $(this).removeClass('btn-lit-active'); $(this).removeClass('btn-lit-hover'); });

    // 浏览改大小
    $(window).bind('resize', function () {
        var win = $(window);
        var height = win.height() - 64 - 10;
        var height_c = height - 29 - 8 - 7;
        var width = win.width() - $('#con_left').width();
        $("#content").attr("width", width);
        $("#con_left").height(height);
        $("#con_right").height(height);
        $('#loading').css('padding-top', height_c / 2).width(width);
    });
};

//收缩/展开
Admin.Shrink = function () {
    var win = $(window);
    var height = win.height() - 64 - 10;
    var height_c = height - 29 - 8 - 7;
    var width = 0;
    if ($("#con_left").is(":hidden")) {
        $("#con_left").show();
        $("#shrink_span img").attr("src", "/images/ss.gif");
        $("#shrink_span").css("left", "210px");
        $("#con_right").css("left", "210px");
        width = win.width() - $('#con_left').width();
    }
    else {
        $("#con_left").hide();
        $("#shrink_span img").attr("src", "/images/ss2_03.gif");
        $("#shrink_span").css("left", "0px");
        $("#con_right").css("left", "0px");
        width = win.width();
    }
    $("#content").attr("width", width);
    $("#con_right").height(height).width(width);
    $('#loading').css('padding-top', height_c / 2).width(width);
}

Admin.Logout = function () {
    top.jBox.confirm('确定要退出系统吗？', '提示', function (v, h, f) {
        if (v == 'ok') {
            Login.Tip('正在注销...', 'loading');
            var url = window.location.href;
            var doname = /http:\/\/(\w+\.){2,3}((com)|(net)|(org)|(gov\.cn)|(info)|(cc)|(com\.cn)|(net\.cn)|(org\.cn)|(name)|(biz)|(tv)|(cn)|(mobi)|(name)|(sh)|(ac)|   (io)|(tw)|(com\.tw)|(hk)|(com\.hk)|(ws)|(travel)|(us)|(tm)|(la)|(me\.uk)|(org\.uk)|(ltd\.uk)|(plc\.uk)|(in)|(eu)|(it)|(jp))/;
            var flag_domain = doname.test(url);
            if (!flag_domain) {
                $.ajax({
                    type: "post",
                    url: Admin.LogoutAjaxUrl,
                    data: "&action=UserLogOut",
                    success: function (data) {
                        top.window.location.reload();
                    }
                });
            }
            else {
                if (Admin.ClearCurrentDomainAllCookie()) {
                    top.window.location.reload();
                }
            }
        }
        return true;
    });
};

Admin.ClearCurrentDomainAllCookie = function () {
    ///<summary>
    ///清除当前域名所有cookie
    ///</summary>
    var url = window.location.href;
    var regex = /http\:\/\/[^\/.]*.([^/]*)*/;
    var match = url.match(regex);
    var host = "";
    if (typeof match != "undefined" && null != match)
        host = match[1];
    var strCookie = document.cookie;
    var arrCookie = strCookie.split("; "); // 将多cookie切割为多个名/值对
    for (var i = 0; i < arrCookie.length; i++) { // 遍历cookie数组，处理每个cookie对
        var arr = arrCookie[i].split("=");
        if (arr.length > 0) {
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            var t = exp.toGMTString();
            document.cookie = arr[0] + "=" + 0 + ";Domain=" + host + "; expire=" + exp.toGMTString();
        }
    }
    document.cookie = "LoginType=0;Domain=" + host;
    return true;
}

Admin.LeftShow = function () {
    var url = top.window.location.href;

    if (url.indexOf("homepage.aspx") != 0) {
        $("#con_left").show();
        $('#con_left').css("width", "210px");
        $("#con_right").css("left", "210px");

        var win = $(window);
        var width = win.width() - 210;

        $("#content").attr("width", width);
        $("#con_right").width(width);

    }
}

Admin.LeftDefaultSrc = function () {
    var url = top.window.location.href;
    if (url.indexOf("homepage.aspx") > 0) {
        $("#content_left").attr("src", "about:blank");
    }
    else {
        $("#content_left").attr("src", "left.html");
    }
}

Admin.LeftHide = function () {
    var url = top.window.location.href;
    if (url.indexOf("homepage.aspx") > 0) {
        $("#con_left").hide();
        $('#con_left').css("width", "0px");
        $("#con_right").css("left", "0px");

        var win = $(window);

        $("#content").attr("width", win.width());
    }

}

Admin.SubmitForm = function (name) {
    var form = (name == undefined || name == '') ? document.forms[0] : document.forms[name];
    if (J.IsObject(form)) {
        $('.btn-lit').setDisabled(true).click(function () { return false; });
        if (jBox != undefined) {
            jBox.tip('正在提交...', 'loading');
        }
        form.submit();
    }
};

/* -----  Login ----- */

Login.Init = function () {
    // IE情况下修正自适应高度
    if ($.browser.msie) {
        var height = $(document).height() - 338;
        if (J.IsIE6) {
            height = height - 20;
        }
        $('#login_auto_height').css({ height: height - ($('#login_form tr').length - 2) * 38 - 53 + 30 });
    }
    // 验证码
    $('#refresh_verify_image').click(Login.LoadCaptcha);
    $('#verify_image').click(Login.LoadCaptcha).load(function () { $(this).show(); });
    Login.LoadCaptcha();
    // 提交登录
    $('#login_btn').click(Login.Submit);
    $('#password').keydown(function (event) { J.EnterSubmit(event, Login.Submit); });
    $('#verifycode').keydown(function (event) { J.EnterSubmit(event, Login.Submit); });
    $('#username').keydown(function (event) { J.EnterSubmit(event, Login.Submit); }).focus();
};

Login.LoadCaptcha = function () {
    $('#verify_image').attr('src', Admin.VerifyImageUrl + '?t=' + Math.random());
};

Login.Tip = function (text, icon) {
    jBox.tip(text, icon, { top: '100px' });
};

Login.Submit = function () {
    var username = $('#username').trim();
    var password = $('#password').trim();
    var customerName = $("#customerName").trim();
    var verifycode = $('#verifycode').trim();
    var logingtype = "0";
    $("input[name='logingType']").each(function () {
        if ($(this).attr("checked")) {
            logingtype = this.value;
        }
    });
    if (username == '') {
        Login.Tip('请输入用户！', 'warning');
        $('#username').focus();
        return;
    }
    if (password == '') {
        Login.Tip('请输入密码！', 'warning');
        $('#password').focus();
        return;
    }
    if (verifycode == '') {
        Login.Tip('请输入验证码！', 'warning');
        $('#verifycode').focus();
        return;
    }

    Login.Tip('正在登录，请稍后...', 'loading');
    if ($("#remember").attr("checked")) {
        localStorageData.set("loginname", localStorageData.EncodeEight(username));
        localStorageData.set("password", localStorageData.EncodeEight(password));
        localStorageData.set("customerName", localStorageData.EncodeEight(customerName));
        localStorageData.set("loginType", localStorageData.EncodeEight(logingtype));
        localStorageData.set("t", "1");
    }
    else {
        localStorageData.set("loginname", "");
        localStorageData.set("password", "");
        localStorageData.set("customerName", "");
        localStorageData.set("loginType", "0");
        localStorageData.set("t", "0");
    }
    $.ajax({
        type: "post",
        url: Admin.LoginUrl,
        data: "&action=UserLogin&username=" + username + "&password=" + password + "&logingtype=" + logingtype + "&verifycode=" + verifycode + "&customerName=" + customerName,
        dataType: "json",
        success: function (data) {
            if (data.code == 1) {
                if (logingtype == 2) {
                    Login.Tip("登陆成功！", 'success');
                    window.location.href = "/CustomerManage/MallBigBuddy/MallSuperBuddyMain.aspx?customerid=" + data.customerid + "&superbuddyid=" + data.superBuddyid;
                } else if (logingtype == 3) {
                    Login.Tip("登录成功！", 'success');
                    window.location.href = "/CustomerManage/MallManager/MallManagerIndex.aspx";
                } else {
                    Login.Tip("登陆成功！", 'success');
                    window.location.href = Admin.IndexUrl;
                }
            } else {
                Login.LoadCaptcha();
                if (data.code == 2) {
                    Login.Tip('验证码已失效，请重新输入！', 'error');
                    $('#verifycode').val('').focus();
                } else if (data.code == 3) {
                    Login.Tip('验证码错误，请重新输入！', 'error');
                    $('#verifycode').select();
                } else if (data.code == 4) {
                    Login.Tip("未找到该商户");
                } else if (data.code == 5) {
                    Login.Tip("该商户未开启小伙伴功能");
                } else {
                    Login.Tip("用户名或密码错误", 'error');
                    $('#username').select();
                }
            }
        }
    });
};

/* -----  Index ----- */
Index.MenuIndex = 0;
Index.MenuSpeed = 250;
Index.Init = function () {
    // 初始化高与宽
    var win = $(window);
    var height = win.height() - 64 - 10;
    var height_c = height - 29 - 8 - 7;
    var width = win.width() - $('#con_left').width();
    $('#con_left').height(height);
    $('#con_right').height(height).width(width);

    $('#loading').css('padding-top', height_c / 2).width(width);
    Index.Open(Admin.IndexStartPage);
};

Index.OutputIframe = function () {
    var win = $(window);
    var width = win.width() - $('#con_left').width();
    var scrolling = $.isIE6 == true ? 'yes' : 'auto';

    document.write('<iframe id="content" width="' + width + '" height="100%" class="hide" marginwidth="0" marginheight="0" frameborder="0" scrolling="' + scrolling + '" onload="$(\'#loading\').hide();$(this).show();" src=""></iframe>');
};
Index.OutputLeftIframe = function (loginType) {
    var win = $(window);
    var width = $('#con_left').width();
    //var scrolling = 'no';// $.isIE6 == true ? 'yes' : 'auto';
    var scrolling = 'auto';
    var height = win.height() - 78;

    var ifSrc = "left.html";

    document.write('<iframe id="content_left" width="' + width + '" height="98%" class="hide" marginwidth="0" marginheight="0" frameborder="0" scrolling="' + scrolling + '" onload="$(\'#loading\').hide();$(this).show();" src="left.html" style="overflow-x:hidden;"></iframe>');
};
Index.OutputLeftIframe2 = function () {
    var win = $(window);
    var width = $('#con_left').width();
    var scrolling = 'auto';// $.isIE6 == true ? 'yes' : 'auto';
    var height = win.height() - 78;
    document.write('<iframe id="content_left" width="' + width + '" height="98%" class="hide" marginwidth="0" marginheight="0" frameborder="0" scrolling="' + scrolling + '" onload="$(\'#loading\').hide();$(this).show();"  style="overflow-x:hidden;"></iframe>');
};

Index.Open = function (url) {
    if (window.location.href.indexOf('use.fanmore.cn') != -1) {
        //alert('');
        window.location.href = '/' + url.replace(/^\//, '');
        return;
    }
    if (url != '') {
        //top.$('#loading').show();
        if (url.indexOf('#') == -1) {
            url = url + (url.indexOf('?') == -1 ? '?___t=' : '&___t=') + Math.random();
        } else {
            var arr = url.split('#');
            url = arr[0] + (arr[0].indexOf('?') == -1 ? '?___t=' : '&___t=') + Math.random() + '#' + arr[1];
        }
        var v = top.$.jBox.getIframe("product_menu");
        if (typeof v == "undefined") {
            top.$('#loading').show();
            top.$('#content').hide().attr('src', url);
            // 可能在jBox里点击跳转，同时关闭它
            if (jBox) {
                jBox.close();
            }
        }
        else {
            if (url.toLowerCase().indexOf('micrositeconfiglist') > 0) return;
            window.location.href = '/' + url.replace(/^\//, '');
        }
    }
};

Index.SetTitle = function (title) {
    //top.$('#index-title').html(title);
};

Pager.Data = {};
/**
 * 表格分页显示
 * @param urlFormat FORM表单ID
 * @param paramPageNo 页数参数名称
 * @param pageSize  每页大小
 * @param pageIndex 当前页数
 * @param pageCount 总页数
 * @param recordCount 总记录数
 * @constructor
 */
Pager.Output = function (urlFormat,paramPageNo, pageSize, pageIndex, pageCount, recordCount) {
    pageSize = parseInt(pageSize, 10);
    pageIndex = parseInt(pageIndex, 10);
    pageCount = parseInt(pageCount, 10);
    recordCount = parseInt(recordCount, 10);

    if (pageIndex < 1)
        pageIndex = 1;
    if (pageIndex > pageCount)
        pageIndex = pageCount;

    Pager.Data.urlFormat = urlFormat;
    Pager.Data.pageCount = pageCount;

    function _getLink(text, enabled, urlFormat, index) {
        if (enabled == false)
            return J.FormatString(' <a class="button-white" style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"><span>{0}</span></a>', text);
        else
            return J.FormatString(' <a class="button-white" href="javascript:$(\'#current-index\').attr(\'value\', '+index+');$(\'#'+urlFormat+'\').submit();"><span>{1}</span></a>', index, text);
            //return J.FormatString(' <a class="button-white" href="javascript:Index.Open(\'' + urlFormat + '\');"><span>{1}</span></a>', index, text);
    }

    var html = [];
    html.push('<div class="pager-bar">');
    html.push(J.FormatString('<div class="msg">共{0}条记录，当前第{1}/{2}，每页{3}条记录</div>', recordCount, (pageIndex > 0 ? pageIndex :1), (pageCount > 0 ? pageCount :1), pageSize));
    html.push(_getLink('首页', pageIndex > 1, urlFormat, 1));
    html.push(_getLink('上一页', pageIndex > 1, urlFormat, pageIndex - 1));
    html.push(_getLink('下一页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageIndex + 1));
    html.push(_getLink('末页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageCount));
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('第<input id="current-index" name=\''+ paramPageNo +'\'onkeydown="J.EnterSubmit(event, Pager.Jump);" class="input-small" style="text-align:center;" type="text" value="' + (pageIndex > 0 ? pageIndex :1) + '" />页');
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('<a class="button-white"' + (pageCount <= 1 ? ' style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"' : ' href="javascript:Pager.Jump();"') + '><span>跳转</span></a>');
    html.push('</div>');

    document.write(html.join(''));
};

Pager.Output1 = function (urlFormat, pageSize, pageIndex, pageCount, recordCount) {
    pageSize = parseInt(pageSize, 10);
    pageIndex = parseInt(pageIndex, 10);
    pageCount = parseInt(pageCount, 10);
    recordCount = parseInt(recordCount, 10);

    if (pageIndex < 1)
        pageIndex = 1;
    if (pageIndex > pageCount)
        pageIndex = pageCount;

    Pager.Data.urlFormat = urlFormat;
    Pager.Data.pageCount = pageCount;

    function _getLink(text, enabled, urlFormat, index) {
        if (enabled == false)
            return J.FormatString(' <a class="button-white" style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"><span>{0}</span></a>', text);
        else
            return J.FormatString(' <a class="button-white" href="javascript:Index.Open(\'' + urlFormat + '\');"><span>{1}</span></a>', index, text);
    }

    var html = [];
    html.push('<div class="pager-bar">');
    html.push(J.FormatString('<div class="msg">共{0}条记录，当前第{1}/{2}，每页{3}条记录</div>', recordCount, pageIndex, pageCount, pageSize));
    html.push(_getLink('首页', pageIndex > 1, urlFormat, 1));
    html.push(_getLink('上一页', pageIndex > 1, urlFormat, pageIndex - 1));
    html.push(_getLink('下一页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageIndex + 1));
    html.push(_getLink('末页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageCount));
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('第<input id="current-index" onkeydown="J.EnterSubmit(event, Pager.Jump);" class="input-small" style="text-align:center;" type="text" value="' + (pageIndex > 0 ? pageIndex : '') + '" />页');
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('<a class="button-white"' + (pageCount <= 1 ? ' style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"' : ' href="javascript:Pager.Jump();"') + '><span>跳转</span></a>');
    html.push('</div>');

    document.write(html.join(''));
};

Pager.Output2 = function (urlFormat, pageSize, pageIndex, pageCount, recordCount) {
    pageSize = parseInt(pageSize, 10);
    pageIndex = parseInt(pageIndex, 10);
    pageCount = parseInt(pageCount, 10);
    recordCount = parseInt(recordCount, 10);

    if (pageIndex < 1)
        pageIndex = 1;
    if (pageIndex > pageCount)
        pageIndex = pageCount;

    Pager.Data.urlFormat = urlFormat;
    Pager.Data.pageCount = pageCount;

    function _getLink(text, enabled, urlFormat, index) {
        if (enabled == false)
            return J.FormatString(' <a class="button-white" style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"><span>{0}</span></a>', text);
        else
            return J.FormatString(' <a class="button-white" href="javascript:window.location.href=\'' + urlFormat + '\';"><span>{1}</span></a>', index, text);
    }

    var html = [];
    html.push('<div class="pager-bar">');
    html.push(J.FormatString('<div class="msg">共{0}条记录，当前第{1}/{2}，每页{3}条记录</div>', recordCount, pageIndex, pageCount, pageSize));
    html.push(_getLink('上一页', pageIndex > 1, urlFormat, pageIndex - 1));
    html.push(_getLink('下一页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageIndex + 1));
    html.push('</div>');

    document.write(html.join(''));
};
Pager.Output3 = function (urlFormat, pageSize, pageIndex, pageCount, recordCount) {
    pageSize = parseInt(pageSize, 10);
    pageIndex = parseInt(pageIndex, 10);
    pageCount = parseInt(pageCount, 10);
    recordCount = parseInt(recordCount, 10);

    if (pageIndex < 1)
        pageIndex = 1;
    if (pageIndex > pageCount)
        pageIndex = pageCount;

    Pager.Data.urlFormat = urlFormat;
    Pager.Data.pageCount = pageCount;

    function _getLink(text, enabled, urlFormat, index) {
        if (enabled == false)
            return J.FormatString(' <a class="button-white" style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"><span>{0}</span></a>', text);
        else
            return J.FormatString(' <a class="button-white" href="javascript:window.location.href=\'' + urlFormat + '\'"><span>{1}</span></a>', index, text);
    }

    var html = [];
    html.push('<div class="pager-bar">');
    html.push(J.FormatString('<div class="msg">共{0}条记录，当前第{1}/{2}，每页{3}条记录</div>', recordCount, pageIndex, pageCount, pageSize));
    html.push(_getLink('首页', pageIndex > 1, urlFormat, 1));
    html.push(_getLink('上一页', pageIndex > 1, urlFormat, pageIndex - 1));
    html.push(_getLink('下一页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageIndex + 1));
    html.push(_getLink('末页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageCount));
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('第<input id="current-index" onkeydown="J.EnterSubmit(event, Pager.Jump);" class="input-small" style="text-align:center;" type="text" value="' + (pageIndex > 0 ? pageIndex : '') + '" />页');
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('<a class="button-white"' + (pageCount <= 1 ? ' style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"' : ' href="javascript:Pager.Jump();"') + '><span>跳转</span></a>');
    html.push('</div>');

    document.write(html.join(''));
};

Pager.Output4 = function (urlFormat, pageSize, pageIndex, pageCount, recordCount, div) {
    pageSize = parseInt(pageSize, 10);
    pageIndex = parseInt(pageIndex, 10);
    pageCount = parseInt(pageCount, 10);
    recordCount = parseInt(recordCount, 10);

    if (pageIndex < 1)
        pageIndex = 1;
    if (pageIndex > pageCount)
        pageIndex = pageCount;

    Pager.Data.urlFormat = urlFormat;
    Pager.Data.pageCount = pageCount;

    function _getLink(text, enabled, urlFormat, index) {
        if (enabled == false)
            return J.FormatString(' <a class="button-white" style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"><span>{0}</span></a>', text);
        else
            return J.FormatString(' <a class="button-white" href="javascript:Index.Open(\'' + urlFormat + '\');"><span>{1}</span></a>', index, text);
    }

    var html = [];
    html.push('<div class="pager-bar">');
    html.push(J.FormatString('<div class="msg">共{0}条记录，当前第{1}/{2}，每页{3}条记录</div>', recordCount, pageIndex, pageCount, pageSize));
    html.push(_getLink('首页', pageIndex > 1, urlFormat, 1));
    html.push(_getLink('上一页', pageIndex > 1, urlFormat, pageIndex - 1));
    html.push(_getLink('下一页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageIndex + 1));
    html.push(_getLink('末页', pageCount > 0 && pageIndex < pageCount, urlFormat, pageCount));
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('第<input id="current-index" onkeydown="J.EnterSubmit(event, Pager.Jump1);" class="input-small" style="text-align:center;" type="text" value="' + (pageIndex > 0 ? pageIndex : '') + '" />页');
    html.push('&nbsp;&nbsp;&nbsp;&nbsp;');
    html.push('<a class="button-white"' + (pageCount <= 1 ? ' style="filter:Alpha(Opacity=60);opacity:0.6;" href="javascript:void(0);"' : ' href="javascript:Pager.Jump1();"') + '><span>跳转</span></a>');
    html.push('</div>');

    $("#" + div).html(html.join(''));
    //document.write(html.join(''));
};


Pager.Jump = function () {
    var index = $('#current-index').val().trim();
    if (J.IsIntPositive(index) == false || parseInt(index) < 1 || parseInt(index) > Pager.Data.pageCount) {
        $('#current-index').val('').focus();
        return;
    }
    //Index.Open(J.FormatString(Pager.Data.urlFormat, index));
    $("#current").attr('value',index);
    $("#"+Pager.Data.urlFormat).submit();
}

Pager.Jump1 = function () {
    var index = $('#current-index').val().trim();
    if (J.IsIntPositive(index) == false || parseInt(index) < 1 || parseInt(index) > Pager.Data.pageCount) {
        $('#current-index').val('').focus();
        return;
    }
    Index.Open(J.FormatString(Pager.Data.urlFormat, index));
}

CheckBox.CheckAll = function (checkBox, containerId) {
    if (containerId == undefined)
        $("input[type=checkbox]").each(function () { this.checked = checkBox.checked; });
    else {
        $("#" + containerId + " input[type=checkbox]").each(function () { this.checked = checkBox.checked; });
    }
};
CheckBox.GetCheckedIds = function (containerId) {
    if (containerId == undefined)
        return $("input.check-box:checked").map(function () { return $(this).attr("rel"); }).get();
    else
        return $("#" + containerId + " input.check-box:checked").map(function () { return $(this).attr("rel"); }).get();
};

// 初始化
$(function () {
    Admin.Init();
    if (Admin.IsLoginPage) {
        Login.Init();
    }
    if (Admin.IsIndexPage) {
        Index.Init();
    }
});

////只调用最顶的jBox
try {
    if (top.jBox != undefined) {
        window.jBox = top.jBox;
    }
} catch (e) {

}

function getcookie(name) {
    var cookie_start = document.cookie.indexOf(name);
    var cookie_end = document.cookie.indexOf(";", cookie_start);
    return cookie_start == -1 ? '' : unescape(document.cookie.substring(cookie_start + name.length + 1, (cookie_end > cookie_start ? cookie_end : document.cookie.length)));
}

function setcookie(cookieName, cookieValue, seconds, path, domain, secure) {
    var expires = new Date();
    expires.setTime(expires.getTime() + seconds);
    document.cookie = escape(cookieName) + '=' + escape(cookieValue)
    + (expires ? '; expires=' + expires.toGMTString() : '')
    + (path ? '; path=' + path : '/')
    + (domain ? '; domain=' + domain : '')
    + (secure ? '; secure' : '');
}

function AjaxJson(method, url, datas, callbak, errorCallbak, async) {
    ///<summary>
    ///ajax请求
    ///</summary>
    ///<param name="method" type="String">请求类型post,get</param>
    ///<param name="async" type="Boolean">异步(true)或同步(false)</param>
    ///<param name="url" type="String">请求地址</param>
    ///<param name="datas" type="JQuery">请求参数</param>
    ///<param name="callbak" type="JQuery">回调函数</param>
    ///<param name="errorCallbak" type="JQuery">错误回调函数</param>	
    async = async || true;
    $.ajax({
        type: method,
        async: async,
        url: url,
        data: datas,
        dataType: "json",
        success: function (d) {
            if (callbak) {
                callbak(d);
            }
        },
        error: function (e) {
            if (errorCallbak) {
                errorCallbak(e);
            }
        }
    });
}

String.prototype.format = function () {
    var e = arguments;
    return this.replace(/{(\d+)}/g,
    function (t, n) {
        return typeof e[n] != "undefined" ? e[n] : t
    })
}


$(function () {
    /*var hideJumpMicroSitePanel = window.location.href.indexOf('use.fanmore.cn') != -1//任务平台使用的页面
        || top.window.location.href.toLowerCase().indexOf('/test.html') != -1;//pdmall模式下,home.aspx为框架页面
    if (hideJumpMicroSitePanel) {
        $('.btn-lit').each(function () {
            if ($(this).attr('href')) {
                if ($(this).attr('href').indexOf('JumpMicroSitePanel') != -1) {
                    $(this).hide();
                } else if ($(this).attr('href').toLowerCase().indexOf('micrositeconfiglistv2') != -1) {
                    $(this).hide();
                }
            }
        });
    }*/

    $("input[name='logingType']").change(function () {
        var loginType = $(this).val();

        if (loginType == 2 || loginType == 3) {
            $("#customernametr").show();
        } else {
            $("#customernametr").hide();
        }
    });
});