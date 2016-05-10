$(function(){
    var height = $(window).height();
    var width = $(window).width();
    var $jp2 = $(".jp-container2");
    var contentHeight = height - (50);
    var contentHeight2 = height - (110);
    var contentWidth = width-(200);
    var contentHeight3 = height - (90);
    $(".sidebar-wrap").css("height",contentHeight);
    $(".jp-container").css("max-height",contentHeight2);
    $(".main-wrap").css("height",contentHeight);
    $jp2.css("width",contentWidth);
    $jp2.css("max-height",contentHeight3);
});
$(window).resize(function () {
    var height = $(window).height();
    var width = $(window).width();
    var $jp2 = $(".jp-container2");
    var contentHeight = height - (50);
    var contentHeight2 = height - (110);
    var contentWidth = width-(200);
    var contentHeight3 = height - (90);
    $(".sidebar-wrap").css("max-height",contentHeight);
    $(".jp-container").css("max-height",contentHeight2);
    $(".main-wrap").css("height",contentHeight);
    $jp2.css("width",contentWidth);
    $jp2.css("max-height",contentHeight3);
    //刷新
    window.location.href = window.location.href.replace(/#/g,'');
});