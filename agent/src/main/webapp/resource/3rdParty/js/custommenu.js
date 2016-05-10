openiFrame = function (editid, userid, appid, appsecret) {
    top.$.jBox.open("iframe:CustomerManage/EditCustomMenuInfo.aspx?editid=" + editid + "&userid=" + userid + "&appid=" + appid + "&appsecret=" + appsecret + "",
                    "编辑自定义菜单",
                    710,
                    360,
                    {
                        id: "update_menu",
                        buttons: { "提交": true },
                        iframeScrolling: "auto",
                        submit: function (v, h, f) {
                            top.$.jBox.getIframe("update_menu").contentWindow.submitMenu(); //调用子窗口里的方法如：myFrame.window.functionName();
                            return false;
                        }
                    }
                 );
}


// 根据ID删除用户
DeleteById = function (deleteid, userid, appid, appsecret) {
    jBox.confirm('确定要删除此信息吗？', '提示', function (v, h, f) {
        if (v == 'ok') {
            var url = 'CustomerManage/CustomMenuList.aspx';
            url = J.FormatString(url + '?deleteid={0}&action=delete&userid={1}&appid={2}&appsecret={3}', deleteid, userid, appid, appsecret);
            Index.Open(url);
        }
        return true;
    });
};
GetGrant = function (appid, appsecret) {
    jBox.confirm('每天可操作200次，确定要执行此操作吗？', '提示', function (v, h, f) {
        if (v == 'ok') {
            Login.Tip("正在获取凭证中...", "loading");
            $.ajax({
                type: "post",
                url: "../3rdParty/ajax/CommonAjax.aspx",
                data: "&action=granttype&appid=" + appid + "&appsecret=" + appsecret + "&rnd=" + Math.random(),
                success: function (data) {
                    if (parseInt(data) == 0) {
                        Login.Tip("获取凭证成功！", "success");
                    }
                    else {
                        Login.Tip("获取凭证失败！", "error");
                    }
                }
            });
        }
    });
}
OperationMenu = function (operationtype, userid, appid, appsecret) {
    jBox.confirm('每天可操作100次，确定要执行此操作吗？', '提示', function (v, h, f) {
        if (v == 'ok') {
            if (parseInt(operationtype) == 1)
                Login.Tip("正在创建中...", "loading");
            else
                Login.Tip("正在删除中...", "loading");
            $.ajax({
                type: "post",
                url: "../3rdParty/ajax/CommonAjax.aspx",
                data: "&action=createmenu&appid=" + appid + "&appsecret=" + appsecret + "&userid=" + userid + "&operationtype=" + operationtype + "&rnd=" + Math.random(),
                success: function (data) {
                    switch (parseInt(data)) {
                        case 0:
                            {
                                if (parseInt(operationtype) == 1)
                                    Login.Tip("创建自定义菜单成功！", "success");
                                else
                                    Login.Tip("删除自定义菜单成功！", "success");

                                break;
                            }
                        case 40001:
                            {
                                Login.Tip("验证失败！", "warning");
                                break;
                            }
                        case 41001:
                            {
                                Login.Tip("凭证失效,请重新获取！", "warning");
                                break;
                            }
                        case 41002:
                            {
                                Login.Tip("缺少appid参数！", "warning");
                                break;
                            }
                        case 44002:
                            {
                                Login.Tip("自定义菜单为空！", "warning");
                                break;
                            }
                        case 40023:
                            {
                                Login.Tip("不合法的子菜单按钮个数！", "warning");
                                break;
                            }
                        case 40016:
                            {
                                Login.Tip("不合法的按钮个数！", "warning");
                                break;
                            }
                        case 42001:
                            {
                                Login.Tip("凭证失效,请重新获取！", "warning");
                                break;
                            }
                        case 40019:
                            {
                                Login.Tip("如果按钮没有子级，请录入按钮KEY！", "warning");
                                break;
                            }

                        default:
                            {
                                if (parseInt(operationtype) == 1)
                                    Login.Tip("创建自定义菜单失败！", "success");
                                else
                                    Login.Tip("删除自定义菜单失败！", "success");
                                break;
                            }
                    }
                }
            });
        }
        return true;
    });
}