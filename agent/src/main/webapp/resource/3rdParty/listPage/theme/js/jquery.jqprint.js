(function ($) {
    var opt;
    $.fn.print = function (options) {
        opt = $.extend({}, $.fn.print.defaults, options);

        var $element = (this instanceof jQuery) ? this : $(this);

        if (opt.operaSupport && opt.operaBrowser) {
            var tab = window.open("", "jqPrint-preview");
            tab.document.open();

            var doc = tab.document;
        }
        else {
            var $iframe = $("<iframe  />");

            if (!opt.debug) { $iframe.css({ position: "absolute", width: "0px", height: "0px", left: "-600px", top: "-600px" }); }

            $iframe.appendTo("body");
            var doc = $iframe[0].contentWindow.document;
        }

        if (opt.importCSS) {
            if ($("link[media=print]").length > 0) {
                $("link[media=print]").each(function () {
                    doc.write("<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' media='print' />");
                });                
            }
            else {
                $("link").each(function () {
                    doc.write("<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' />");
                });                
            }
        }
        if (opt.printContainer) { doc.write($element.outer()); }
        else { $element.each(function () { doc.write($(this).html()); }); }

        doc.close();

        (opt.operaSupport && opt.operaBrowser ? tab : $iframe[0].contentWindow).focus();
        //setTimeout(function () { (opt.operaSupport && $.browser.opera ? tab : $iframe[0].contentWindow).print(); if (tab) { tab.close(); } }, 1000);

        (opt.operaSupport && opt.operaBrowser ? tab : $iframe[0].contentWindow).print(); if (tab) { tab.close(); }
    }

    $.fn.print.defaults = {
        debug: false,
        importCSS: true,
        printContainer: true,
        operaSupport: true,
        operaBrowser: false//$.browser.opera
    };

    jQuery.fn.outer = function () {
        return $($('<div></div>').html(this.clone())).html();
    }
})(jQuery);




function execPrint(element, options) {
    $(element).print(options);
}