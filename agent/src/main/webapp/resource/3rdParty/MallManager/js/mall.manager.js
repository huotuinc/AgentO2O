var pageIndex = 1;
var pageCount = 1;
var viewModel;
var ajaxUrl = "operators";
var Type = 0;

var baseData = {
    pageIndex: pageIndex
}

//加载出现
function showLoading() {
    $("#content").hide();
    $("#bg").show();
}
//加载完成
function hideLoading() {
    $("#bg").hide();
    $("#content").show();
}

$(document).ready(function () {
    $("input[name='menuId']").change(function () {
        var changeCode = $(".code" + $(this).val());
        if ($(this).attr("checked")) {
            changeCode.attr("checked", "checked");
            changeCode.change();
        } else {
            changeCode.removeAttr("checked");
            changeCode.change();
        }
    })

    function UserViewModel() {
        var self = this;

        self.userList = ko.observableArray([]);
        self.userInfo = ko.observable();
        self.pageIndex = ko.observable(pageIndex);
        self.pageCount = ko.observable(pageCount);
        self.recordCount = ko.observable();

        //得到用户列表
        self.getUserList = function () {
            baseData.pageIndex = pageIndex;
            hideLoading();
            setTimeout(function () {
                $.ajax({
                    type: "GET",
                    url: ajaxUrl,
                    dataType: "json",
                    success: function (json) {
                        hideLoading();
                        self.setList(json);
                    },
                    error: function () {
                        $.jBox.tip("发生错误", "error");
                    }
                });
            }, 400);
        }

        self.setList = function (json) {
            $("#current-index").val(pageIndex);
            self.userList(json.data);
            self.pageCount(json.pageCount);
            pageCount = json.pageCount;
            self.recordCount(json.recordCount);
            self.pageIndex(pageIndex);
        }

        //搜索
        self.search = function () {
            pageIndex = 1;
            self.pageIndex(1);
            showLoading();
            $.ajax({
                type: "get",
                url: ajaxUrl,
                data: {
                    customerId: customerId,
                    action: "getuserlist",
                    pageIndex: pageIndex,
                    searchkey: $("#txtSearchKey").val(),
                    managerSearch: $("#searchType").val()
                },
                dataType: "json",
                success: function (json) {
                    hideLoading();
                    self.setList(json);
                },
                error: function () {
                    $.jBox.tip("发生错误", "error");
                }
            });
        }
        //显示所有
        self.showAll = function () {
            $("#txtSearchKey").val("");
            pageIndex = 1;
            self.pageIndex(1);
            showLoading();
            self.getUserList();
        }

        showLoading();
        self.getUserList();

        //下一页
        self.nextPage = function () {
            pageIndex = self.pageIndex();
            if (self.pageIndex() < self.pageCount()) {
                pageIndex++;
                showLoading();
                self.getUserList();
            }
        }

        //上一页
        self.previewsPage = function () {
            pageIndex = self.pageIndex();
            if (pageIndex > 1) {
                pageIndex--;
                showLoading();
                self.getUserList();
            }
        }

        //首页
        self.firstPage = function () {
            if (self.pageIndex() != 1) {
                pageIndex = 1;
                showLoading();
                self.getUserList();
            }
        }

        //最后一页
        self.lastPage = function () {
            if (self.pageIndex() != self.pageCount()) {
                pageIndex = self.pageCount();
                showLoading();
                self.getUserList();
            }
        }

        self.addManager = function () {
            $("#loginName").val("");
            $("#loginName").removeAttr("readonly");
            $("#realName").val("");
            $("#password").val("");
            $("#confirmPass").val("");
            $("#passtr").show();
            $("#mobile").val("");
            $("#email").val("");
            $("#authority").val("");
            $("#roleName").val("");
            setCheckedMenu("");

            self.infoDialog(0);
        }

        self.editManager = function (data) {
            $("#loginName").val(data.LoginName);
            $("#loginName").attr("readonly", "readonly");
            $("#realName").val(data.RealName);
            $("#password").val("");
            $("#confirmPass").val("");
            $("#passtr").hide();
            $("#mobile").val(data.MobilePhone);
            $("#email").val(data.Email);
            $("#authority").val(data.Authority);
            $("#roleName").val(data.RoleName);
            setCheckedMenu(data.Authority);

            self.infoDialog(data.ManagerID);
        }

        self.infoDialog = function (managerid) {
            J.ShowDialog("managerInfoDialog", managerid > 0 ? "编辑" : "新增", function () {
                var self2 = this;

                self.submitForm(managerid, function (json) {

                    if (json.result == 1) {
                        self.setList(json);
                        $.jBox.tip(json.msg, "success");
                        $(self2).dialog('close');
                    } else {
                        $.jBox.tip(json.msg, "error");
                    }
                })
            }, function () {
                $(this).dialog('close');
            })
        }

        self.submitForm = function (managerid, callback) {
            var loginName = $("#loginName").val();
            var realName = $("#realName").val();
            var password = $("#password").val();
            var confirmPass = $("#confirmPass").val();
            var mobile = $("#mobile").val();
            var email = $("#email").val();
            var authority = $("input[name='authorityIds']:checked").map(function(){return this.value}).get().join(',');
            var roleName = $("#roleName").val();

            if (loginName.length == 0) {
                $.jBox.tip("请输入登录名");
                return;
            }
            if (realName.length == 0) {
                $.jBox.tip("请输入姓名");
                return;
            }
            if (managerid == 0 && password.length == 0) {
                $.jBox.tip("请输入密码");
                return;
            }
            if (managerid == 0 && (password != confirmPass)) {
                $.jBox.tip("两次密码输入不相同");
                return;
            }
            if (mobile.length == 0) {
                $.jBox.tip("请输入手机号码");
                return;
            }
            if (!J.IsMobile(mobile)) {
                $.jBox.tip("手机号码格式不正确");
                return;
            }
            if (email.length > 0 && !J.IsEmail(email)) {
                $.jBox.tip("邮箱格式不正确");
                return;
            }
            if (roleName.length == 0) {
                $.jBox.tip("请输入角色名");
                return;
            }
            if (authority.length < 1) {
                $.jBox.tip("还没有设置权限");
                return;
            }
            $.jBox.tip("保存中...", "loading");

            baseData.action = "editmanager";
            var requestData = $.extend({}, baseData, {
                loginName: loginName,
                realName: realName,
                password: password,
                confirmPass: confirmPass,
                mobile: mobile,
                email: email,
                managerid: managerid,
                authorityIds: authority,
                roleName: roleName
            });

            setTimeout(function () {
                J.GetJsonRespons(ajaxUrl, requestData, function (json) {
                    callback(json);
                }, function () { $.jBox.tip("保存失败，请重试", "error"); }, "post");
            }, 400);
        }

        self.setAuthority = function () {
            J.ShowDialog("menuDialog", "设置权限", function () {
                var self2 = this;
                getCheckedMenu();
                $(self2).dialog('close');
            }, function () {
                $(this).dialog('close');
            })
        }

        self.updateState = function (data) {
            var msg = data.State == 1 ? "确定要解冻该管理员吗?" : "确定要冻结该管理员吗？";

            jBox.confirm(msg, '提示', function (v, h, f) {
                if (v == "ok") {
                    $.jBox.tip("操作中...", 'loading');
                    baseData.action = "updatestate"

                    var requestData = $.extend({}, baseData, {
                        managerid: data.ManagerID,
                        changeState: data.State == 1 ? 0 : 1
                    })

                    setTimeout(function () {
                        J.GetJsonRespons(ajaxUrl, requestData, function (json) {
                            if (json.result == 1) {
                                self.setList(json);
                                $.jBox.tip("操作成功", "success");
                            } else {
                                $.jBox.tip("操作失败", "error");
                            }
                        }, function () { }, "post");
                    }, 500);
                }
                return true;
            });
        }
    }
    viewModel = new UserViewModel();
    ko.applyBindings(viewModel);
});

IsInt = function (str) { return /^\d+$/.test(str); }

function goToCertain(event) {
    var currKey;
    var e = event ? event : (window.event ? window.event : null)
    currKey = e.keyCode || e.which || e.charCode;

    if (!(currKey == 46) && !(currKey == 8) && !(currKey == 37) && !(currKey == 39))
        if (!((currKey >= 48 && currKey <= 57) || (currKey >= 96 && currKey <= 105)) || currKey == 189)
            e.returnValue = false;

    if (currKey == 13) {
        jump();
    }
}

//跳转
function jump() {
    if (!IsInt($("#current-index").val().trim())) {
        pageIndex = 1;
    } else if ($("#current-index").val().trim().length == 0) {
        pageIndex = 1;
    } else {
        pageIndex = $("#current-index").val().trim();
        if (pageIndex > pageCount)
            pageIndex = pageCount;
        if (pageIndex < 1)
            pageIndex = 1;
    }
    viewModel.getUserList();
}


function getCheckedMenu() {
    var checkedObj = $("input[name='menuId']:checked");
    var checkedMenu = "|";

    checkedObj.each(function () {
        checkedMenu += $(this).val() + "|";
    })

    $("#authority").val(checkedMenu);
}

function setCheckedMenu(authority) {
    $("input[name='menuId']").removeAttr("checked");

    var formatStr = authority.substring(1, authority.length - 1);
    var authorityList = formatStr.split('|');

    $.each(authorityList, function (o, item) {
        $("#c" + item).attr("checked", "checked");
    })
}