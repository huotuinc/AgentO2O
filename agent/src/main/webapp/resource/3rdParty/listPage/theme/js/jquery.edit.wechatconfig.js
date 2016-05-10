/*  提交表单 */
SubmitConfigForm = function (cleraValue) {
    if (checkForm(0, cleraValue)) {
        $("#submitConfig").click();
    }
}
/*  提交一列图文 */
SubmitNewsForm = function (clearValue) {
    setActivitySelectorAddress();
    if (checkForm(1, clearValue)) {
        $("#sumitbtnAdd").click();
    }
}
/* 提交一条关键字 */
SubmitKeyWordForm = function (clearValue) {
    if (checkForm(1,clearValue)) {
        $("#submitbtnAddKeyword").click();
    }
}


/* 验证文本框内容是否已填入 */
function checkForm(code,clearValue) {

    var html = UE.getEditor('editor').getContent();
    $("#litContent").val(html);

    if ($.trim($("#txtConfigTitle").val()).length == 0) {
        Login.Tip("请输入微信通用配置名称！", "warning");
        $("#txtConfigTitle").focus();
        return false;
    }
    clearValue = parseInt(clearValue);
    if (clearValue == 0) {
        if (checkForm1())
            return true;
        else
            return false;
    }
    else {
        if (code == 1) {
            if (checkForm1())
                return true;
            else
                return false;
        }
    }
    return true;

}

function checkForm1() {
    var e = $("#txtEventType").find(":selected").val();
    e = parseInt(e);
    if (e == 1) {
        if ($.trim($("#txtKeyword").val()).length == 0) {
            Login.Tip("请输入关键字！", "warning");
            $("#txtKeyword").focus();
            return false;
        }
    }
    var v = $("#txtReplyType").find(":selected").val();
    v = parseInt(v);
    if (v == 1) {
        if ($.trim($("#txtReplyContent").val()).length == 0) {
            Login.Tip("请输入回复内容！", "warning");
            $("#txtReplyContent").focus();
            return false;
        }
    }
    else {
        if ($.trim($("#txtReplyTitle").val()).length == 0) {
            Login.Tip("请输入标题！", "warning");
            $("#txtReplyTitle").focus();
            return false;
        }

        if ($.trim($("#txtReplyUrl1").val()).length == 0) {
            Login.Tip("请上传图片！", "warning");
            $("#txtReplyUrl1").focus();
            return false;
        }
    }

    return true;
}



$().ready(function () {
    $("#txtExternalService").change(function () {
        isHide();
    });
    $("#txtEventType").change(function () {
        var eventname = $(this).find(":selected").text();
        $("#txtConfigTitle").val(eventname + "事件配置");
        var v = $(this).find(":selected").val();
        v = parseInt(v);
        if (v == 2 || v == 3 || v == 4 || v == 5) {
            $("#tr_reply").hide();
            $("#rptkeylist").hide();
            $("input[name='txtExternalService']").eq(1).attr('checked', 'checked');
            $("input[name='txtExternalService']").attr('disabled', 'disabled');
        }
        else {
            if (v == 1) {
                $("#replykeyword").show();
                $("#rptkeylist").show();
            }
            else {
                $("#replykeyword").hide();
                $("#rptkeylist").hide();
            }
            $("#tr_reply").show();
            $("input[name='txtExternalService']").eq(0).attr('checked', 'checked');
            $("input[name='txtExternalService']").attr('disabled', '');
        }
        isHide();
    });

    $("#txtReplyType").change(function () {
        var v = $(this).find(":selected").val();
        $("#txtReplyContent").val("");
        isHide();
    });

    $("#urlEventtype").change(function () {
        var _v = $("input[name='urlEventtype']:checked").val();
        if (parseInt(_v) == 1) {
            $("#content_div").hide();
            $("#txtReplyUrl2").show();
            $("#get_url").show();
            $('#get_url_taobaoc').show();
            $("#activity_div").hide();
        }
        else {
            if (parseInt(_v) == 3) {
                $("#activity_div").show();
                $("#content_div").hide();
            }
            else {
                $("#content_div").show();
                $("#activity_div").hide();

            }
            $("#get_url").hide();
            $('#get_url_taobaoc').hide();
            $("#txtReplyUrl2").hide();
        }
    });
    isHide();
});

function isHide() {
    var e = $("#txtEventType").find(":selected").val();
    e = parseInt(e);
    if (e == 1 || e == 6 || e == 7 || e == 8) {
        $("#span400").hide();
        $("#tr_reply").show();
        $("#eventtip").hide();
        $("#trpreview").hide();
        $("#activity_div").hide();
        if (e == 1 || e == 8) {
            $("#replykeyword").show();
            $("#addKeyword").show();
            $("#rptkeylist").show();
            if (e == 8) {
                $("#replykeyword").hide();
                $("#replyMenukey").show();
            }
            else
                $("#replyMenukey").hide();
        }
        else {
            $("#replykeyword").hide();
            $("#addKeyword").hide();
            $("#rptkeylist").hide();
            $("#replyMenukey").hide();
           
        }
        var v = $("#txtReplyType").find(":selected").val();
        v = parseInt(v);
        if (v == 1) {
            $("#replycontent").show();
            $("#addNews").hide();
            $("#iframeContent").hide();
            $("#content_div").hide();
            $("#trpreview").hide();
            $("#saveBtn").show();
        }
        else {
            $("#saveBtn").hide();
            $("#trpreview").show();
            if (v == -2) {
                $(".childrenpreview").show();
                $(".sub-add").show();
                $("#content_div").hide();
                var _v = $("input[name='urlEventtype']:checked").val();
                if (parseInt(_v) == 1) {
                    $("#content_div").hide();
                    $("#txtReplyUrl2").show();
                    $("#get_url").show();
                    $("#activity_div").hide();
                }
                else {
                    if (parseInt(_v) == 3) {
                        $("#activity_div").show();
                        $("#content_div").hide();
                    }
                    else {
                        $("#content_div").show();
                        $("#activity_div").hide();

                    }
                    $("#get_url").hide();
                    $("#txtReplyUrl2").hide();
                }

            }
            $("#addNews").hide();
            $("#iframeContent").hide();            
            $("#urltype").hide();
            $("#replycontent").hide();
            $("#addKeyword").hide();
        }
        var _ExternalService = $("input[name='txtExternalService']:checked").val();
        if (parseInt(_ExternalService) == 1) {
            $("#tr_reply").hide();
            $("#replycontent").hide();
            $("#iframeContent").hide();
            $("#replykeyword").hide();
            $("#addKeyword").hide();
            $("#addMusic").hide();
            $("#urltype").hide();
            $("#content_div").hide();
            $("#span400").show();
            $("#replyMenukey").hide();
        }

    }
    else {
        $("#tr_reply").hide();
        $("#replycontent").hide();
        $("#addNews").hide();
        $("#iframeContent").hide();
        $("#replykeyword").hide();
        $("#addKeyword").hide();
        $("#eventtip").show();
        $("#content_div").hide();
        $("#span400").show();
        $("#replyMenukey").hide();
        $("#trpreview").hide();
        $("#content_div").hide();
    }
}
