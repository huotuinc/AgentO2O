/**
* 横向柱形图
*/
var Statsbar = function (id, title, data) {
    //展示的id
    this.id = '';
    //标题
    this.title = '';
    //数据
    this.data = '';
    //宽
    this.width = "100%";
    //背景图片位置
    this.bgimg = '/3rdParty/js/images/plan.gif';
    //动画速度
    this.speed = 1000;

    //投票总数
    var num_all = 0;
    this.show = function () {
        //添加一个table对象
        $("#" + this.id).append("<table width='" + this.width + "' cellpadding=0 cellspacing=6 border=0 style='font-size:12px;' ></table>")

        $("#" + this.id + " table").append("<tr><td colspan=3 align='center' ><span style='font:900 14px ;color:#444'><strong>" + this.title + "</strong></span></td></tr>")

        //起始
        var s_img = [];
        //中间点起始坐标
        var c_img = [];
        //结束
        var e_img = [];

        //52
        var s1 = [52 * 0, -52 * 1, -52 * 2, -52 * 3, -52 * 4, -52 * 5];
        //中间点起始坐标 27
        var c1 = [-312 - 27 * 0, -312 - 27 * 1, -312 - 27.5 * 2, -312 - 27.8 * 3, -312 - 27 * 4, -312 - 27 * 5];
        //结束 52
        var e1 = [-26, -52 * 1 - 26, -52 * 2 - 26, -52 * 3 - 26, -52 * 4 - 26, -52 * 5 - 26];


        //计算总数
        $(this.data).each(function (i, n) {
            num_all += parseInt(n[1]);
            var r = Math.round(Math.random() * 5);
            s_img[i] = s1[r];
            c_img[i] = c1[r];
            e_img[i] = e1[r];
        })
        var that = this;
        var div;
        $(this.data).each(function (i, n) {

            //计算比例
            var c = parseInt(n[1]) * 100;
            var bili = 0;
            if (num_all > 0)
                bili = (c / num_all).toFixed(2);
            //计算图片长度, *0.96是为了给前后图片留空间
            var img = parseFloat(bili) * 0.96;

            if (img > 0) {
                div = "<div style='width:3px;height:16px;background:url(" + that.bgimg + ") 0px " + s_img[i] + "px ;float:left;'></div><div fag='" + img + "' style='width:0%;height:16px;background:url(" + that.bgimg + ") 0 " + c_img[i] + "px repeat-x ;float:left;'></div><div style='width:3px;height:16px;background:url(" + that.bgimg + ") 0px " + e_img[i] + "px ;float:left;'></div>";
            }
            else {
                div = '';
            }
            $("#" + that.id + " table").append("<tr><td width='30%'  align='center' >" + n[0] + "</td><td  width='50%' bgcolor='#fffae2' >" + div + "</td><td width='20%' nowrap >" + n[1] + "(" + bili + "%)</td></tr>")
        })

        this.play();
    }

    this.play = function () {
        var that = this;
        $("#" + this.id + " div").each(function (i, n) {
            if ($(n).attr('fag')) {
                $(n).animate({ width: $(n).attr('fag') + '%' }, that.speed)
            }
        })
    }
}