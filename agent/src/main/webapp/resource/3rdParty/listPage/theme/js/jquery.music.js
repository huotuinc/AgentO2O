/// <reference path="jquery-1.7.2.min.js" />
(function ($) {
    $.fn.audioPlay = function (options) {
        var defaults = {
            name: "audioPlay",
            urlMp3: "",
            urlOgg: "",
            clone: true,
            preload: false,
            loop: false
        };
        var params = $.extend(defaults, options || {}), audioHtml = "";

        $(this).each(function (i) {
            var strIdRoot = params.name;
            tmpAutioHtml = '<audio id="' + strIdRoot + i + '"  ' + params.loop ? "loop='loop'" : "" + '  ' + params.preload ? "preload='auto'" : "" + '  style="position:absolute; visibility:hidden;" >' +
                '<source src="' + params.urlMp3 + '"></source>' +
                '<source src="' + params.urlOgg + '"></source>' +
            '</audio>';
            if (params.clone) {
                audioHtml = audioHtml + tmpAutioHtml;
                $(this).data("targetId", strIdRoot + i);
            } else {
                if (!i) {
                    audioHtml = tmpAutioHtml;
                }
                $(this).data("targetId", strIdRoot + "0");
            }
        });
        $("body").append(audioHtml);
        $(this).mouseenter(function () {
            var targetId = $(this).data("targetId");
            $("#" + targetId).get(0).play();
        });
    };
})(jQuery);



$(function () {
    $(window).audioPlay({
        urlMp3: "http://www.zhangxinxu.com/study/media/beep.mp3"

    });
});
