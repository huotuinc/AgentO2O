//jquery警告框弹窗
(function ($) {
    $.MsgBox = {
        Alert: function (title, msg) {
            GenerateHtml("alert", title, msg);
            btnOk();  //alert只是弹出消息，因此没必要用到回调函数callback
            btnNo();
        },
        Confirm: function (title, msg, callback) {
            GenerateHtml("confirm", title, msg);
            btnOk(callback);
            btnNo();
        },
        AjaxAlert: function (title, msg, callback) {
            GenerateHtml("ajaxAlert", title, msg);
            btnOk(callback);
            btnNo(callback);
        }
    };
    //生成Html
    var GenerateHtml = function (type, title, msg) {
        var _html = "";
        _html += '<div id="mb_box"></div><div id="mb_con_pop"><span id="mb_tit">' + title + '</span>';
        if (type == "alert") {
            _html += '<a id="mb_ico">x</a><div id="mb_msg"><div id="mb_img"><img src="images/info.gif" alt="警告"/></div><div id="mb_txt">' + msg + '</div></div><div id="mb_btnbox">';
            _html += '<input id="mb_btn_ok" type="button" value="确定" />';
        }
        if (type == "confirm" || type == "Save") {
            _html += '<a id="mb_ico">x</a><div id="mb_msg"><div id="mb_img"></div><div id="mb_txt">' + msg + '</div></div><div id="mb_btnbox">';
            _html += '<input id="mb_btn_ok" type="button" value="确定" />';
            _html += '<input id="mb_btn_no" type="button" value="取消" />';
        }
        if (type == "ajaxAlert") {
            _html += '<div id="mb_msg"><div id="mb_img"><img src="images/info.gif" alt="警告"/></div><div id="mb_txt">' + msg + '</div></div><div id="mb_btnbox">';
            _html += '<input id="mb_btn_ok" type="button" value="确定" />';
        }
        _html += '</div></div>';
        //必须先将_html添加到body，再设置Css样式
        $("body").append(_html);
        GenerateCss();
    }
    //生成Css
    var GenerateCss = function () {
        var $mb_ico = $("#mb_ico"),
            $mb_con_pop = $("#mb_con_pop");

        $("#mb_box").css({
            width: '100%', height: '100%', zIndex: '99999', position: 'fixed',
            filter: 'Alpha(opacity=60)', backgroundColor: 'black', top: '0', left: '0', opacity: '0.6'
        });
        $mb_con_pop.css({
            zIndex: '999999', width: '400px', position: 'fixed',
            backgroundColor: 'White', borderRadius: '15px'
        });
        $("#mb_tit").css({
            height:'14px', display: 'block', fontSize: '14px', color: '#444', padding: '10px 15px',
            backgroundColor: '#DDD', borderRadius: '15px 15px 0 0',
            borderBottom: '3px solid #006EFF', fontWeight: 'bold'
        });
        $("#mb_img").css({
            width:'20px',float:'left', padding: '14px 20px 14px 20px',borderBottom: '1px dashed #DDD'
        });
        $("#mb_txt").css({
            width:'300px',float:'left', padding: '20px', borderBottom: '1px dashed #DDD'
        });
        $("#mb_msg").css({
            lineHeight: '20px', border:'1px', fontSize: '13px'
        });
        $mb_ico.css({
            display: 'block', position: 'absolute', right: '10px', top: '9px',
            border: '1px solid Gray', width: '18px', height: '18px', textAlign: 'center',
            lineHeight: '16px', cursor: 'pointer', borderRadius: '12px', fontFamily: '微软雅黑'
        });
        $("#mb_btnbox").css({clear:'both',margin: '73px 0 10px 0', textAlign: 'center'});
        $("#mb_btn_ok,#mb_btn_no").css({width: '85px', height: '30px', color: 'white', border: 'none','border-radius': '5px',cursor: 'pointer'});
        $("#mb_btn_ok").css({backgroundColor: '#006EFF'});
        $("#mb_btn_no").css({backgroundColor: 'gray', marginLeft: '20px'});
        //右上角关闭按钮hover样式
        $mb_ico.hover(function () {
            $(this).css({backgroundColor: 'Red', color: 'White'});
        }, function () {
            $(this).css({backgroundColor: '#DDD', color: 'black'});
        });
        var _widht = document.documentElement.clientWidth;  //屏幕宽
        var _height = document.documentElement.clientHeight; //屏幕高
        var boxWidth = $mb_con_pop.width();
        var boxHeight = $mb_con_pop.height();
        //让提示框居中
        $mb_con_pop.css({top: (_height - boxHeight) / 2 + "px", left: (_widht - boxWidth) / 2 + "px"});
    }
    //确定按钮事件
    var btnOk = function (callback) {
        $("#mb_btn_ok").click(function () {
            $("#mb_box,#mb_con_pop").remove();
            if (typeof (callback) == 'function') {
                callback();
            }
        });
    }
    //取消按钮事件
    var btnNo = function () {
        $("#mb_btn_no,#mb_ico").click(function () {
            $("#mb_box,#mb_con_pop").remove();
        });
    }
})(jQuery);

$(document).ready(function () {
    $(".link-delete").bind("click", function () {
        $.MsgBox.Confirm("友情提示", "执行删除后将无法恢复，确定继续吗？", function () {
            /*alert("你居然真的删除了...");*/
        });
    });
//            function test() {
//                alert("你点击了确定,进行了修改");
//            }
//            //也可以传方法名 test
//            $("#update").bind("click", function () {
//                $.MsgBox.Confirm("温馨提示", "确定要进行修改吗？", test);
//            });
});

//获取同td下的第3个超链接，真正删除的a href
//注意传递的参数，$.MsgBox已经将this改为弹出框了
//Confirm函数的在这个js里面定义，第三个功能为点击确定后的功能
//$(h).parents().children().eq(3)[0]就是获取到了同td下的第三个孩子 .find()不能准确找到 .next()也选不中，这里用前台调试
//.click() 就是点击当前链接的效果
function check_del(h) {
    $.MsgBox.Confirm("友情提示", "执行删除后将无法恢复，确定继续吗？", function () {
          $(h).siblings(".real-delete")[0].click();
    });
}

function delVideoByFileId(obj) {
    var fileIds = $(obj).parent().children().eq(0).val();
    $.MsgBox.Confirm("友情提示", "执行删除后将无法恢复，确定继续吗？", function () {
        $.ajax({
            url:"delVideo",
            type:"post",
            data:{"fileIds":fileIds},
            dataType:"json",
            success:function(result){
                if(result.status==1){
                    $.MsgBox.Alert("提示信息","删除成功");
                }else {
                    $.MsgBox.Alert("错误信息","网络异常，删除失败");
                }
            },
            error:function(){
                alert("系统异常,删除失败");
            }
        });
    });

}

function check_save(h) {
    $.MsgBox.Confirm("友情提示", "即将保存内容，确定继续吗？", function () {
        //editor.sync();
        $("#editorForm").submit();
    });
}


function check_add() {
    $.MsgBox.Alert("消息", "添加成功！");
}