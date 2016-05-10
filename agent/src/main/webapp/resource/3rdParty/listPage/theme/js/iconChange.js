
// 初始化下面的变量
Admin.IsLoginPage = true;
Admin.IndexUrl = 'main.aspx';
Admin.LoginUrl = '3rdParty/ajax/LoginAndLogoutAjax.aspx?action=UserLogin';
Admin.VerifyImageUrl = '3rdParty/Verify/VerifyImage.aspx';
var icon = "favicon.ico";

var domain = document.domain;
function getInfo() {
    $.ajax({
        url: "/Customization.ashx",
        data: {
            domain: domain,
            type: "login"
        },
        success: function (data) {
            if (data != "" && data != null) {
                if (data.loginLogo != "") {
                    $(".log").attr("src", data.loginLogo);
                }
                if (data.netName != "") {
                    document.title = data.netName + "管理后台登陆Beta1.1";
                }
                if (data.userIcon != "") {
                    $("<link>").attr({
                        rel: "Shortcut Icon",
                        href: data.userIcon
                    }).appendTo("head");

                } else {
                    $("<link>").attr({
                        rel: "Shortcut Icon",
                        href: icon
                    }).appendTo("head");
                }
                if (data.netName) {
                    $("#netName").html(data.netName);
                }
            }
            $("#loginLogo").show();
        }
    });
}

$(function () {
    if (domain.indexOf("fanmore.cn") > 0) {
        window.location.href = "http://login.fanmore.cn/";
    }
    else
        getInfo();

});