/*
 * jQuery UI iPicture 1.0.0
 *
 * Copyright 2011 D'Alia Sara
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	jquery.ui.position.js
 *	jquery.ui.draggable.js
 *	jquery.ui.droppable.js
 *  jquery.ui.effects.core.js
 */

(function ($) {

    $.widget("justmybit.iPicture", {
        options: {
            animation: false,
            animationBg: "bgblack",
            animationType: "ltr-slide",
            button: "moreblack",
            modify: false,
            initialize: false,
            moreInfos: {},
            pictures: ["picture1"],
            toolTipTile: "tooltip"
        },

        // Set up the widget
        _create: function () {
            var self = this;
            if (self.options.initialize) {
                this.initialization();
            } else {

                //each picture
                $.each(self.options.pictures, function (index, value) {
                    var picture = $('#' + value);
                    var info = (self.options.moreInfos[value]);
                    if (info != undefined) {
                        // each more infos on that picture
                        $.each(info, function (index, value) {
                            var div = $('<div id="' + value.id + '" class="more more32"></div>')
                                .css('top', value.top).css('left', value.left).appendTo(picture);
                            var imgButton;
                            if (self.options.modify) {
                                imgButton = $('<div class="imgButtonDrag ' + self.options.button + '" title="拖动改变标注位置"></div>').appendTo(div);
                                var divInput = $('<div class="inputDiv"></div>').insertAfter(imgButton);
                                var input = $('<input type="text" title="标注描述" value="' + value.descr + '" />').appendTo(divInput);
                                $('<p class="pDelete" title="点击删除标注"></p>').insertAfter(input).bind('click', function () {
                                    $(div).remove();
                                });
                            } else {
                                imgButton = $('<div class="imgButton ' + self.options.button + '"></div>').appendTo(div);
                                $('<span class="descr">' + value.descr + '</span>').appendTo(div);
                            }
                            // href populating
                            if (value.href) {
                                $('#' + value.id + ' a').attr('href', value.href);
                            }

                            //modify option management
                            if (self.options.modify) {
                                var descr;
                                $('#' + value.id).draggable({
                                    containment: picture
                                });
                            }
                        });
                    }
                });
                //move option management
                if (self.options.modify) {

                    //add selected animationBg if animation set true
                    if (self.options.animation) {
                        $(".more").addClass(self.options.animationBg);
                    } else {
                        $(".more").addClass('noAnimation');
                    }
                    //workaround for firefox issue on trimming border-radius content
                    $(".more32").css('overflow', 'visible');
                    this.initialization();
                    return;
                }
                //set animation
                if (self.options.animation) {
                    this.animation();
                }
            }
        },

        //animation for tooltips
        animation: function () {
            var self = this;
            switch (self.options.animationType) {
                case "ltr-slide":
                    $(".more").addClass('ltr-slide ' + self.options.animationBg);

                    //Animation function left to right sliding
                    $(".more").hover(function () {
                        $(this).stop().animate({ width: '225px' }, 200).css({ 'z-index': '10' });
                    }, function () {
                        $(this).stop().animate({ width: '32px' }, 200).css({ 'z-index': '1' });
                    });
                    break;
            }
        },

        initialization: function () {
            var self = this;

            $.each(self.options.pictures, function (index, value) {
                var picture = $('#' + value);

                // list of buttons to change tooltip color               
                var listContainer = $('.listContainer');
                chooseButtons = $('ul.buttons').find('li');
                $.each(chooseButtons, function (index, button) {
                    $(button).bind('click', function () {
                        $.each(self.options.pictures, function (index, pic) {
                            $('.' + pic + '-button').removeClass(self.options.button);
                            $('.' + pic + '-button').addClass(button.id);
                        });
                        buttons = self.element.find('.imgButtonDrag');
                        $.each(buttons, function (index, value) {
                            $(value).removeClass(self.options.button);
                            $(value).addClass(button.id);
                        });
                        self.options.button = button.id;
                        clickCounter = 0;
                        return false;
                    });
                    $(button).bind('mouseover', function () {
                        $(button).css('width', '36');
                        $(button).css('height', '36');
                        $(button).css('background-size', '36px');
                        $(button).css('z-index', '10');
                    });
                    $(button).bind('mouseout', function () {
                        $(button).css('width', '32');
                        $(button).css('height', '36');
                        $(button).css('background-size', '32px');
                        $(button).css('z-index', '1');
                    });
                });

                //Create a new tooltip
                $('.' + value + '-button').draggable({
                    helper: 'clone',
                    //containment: picture,
                    stop: function (event, ui) {
                        $('<p class="top">' + ui.position.top + '</p><p class="left">' + ui.position.left + '</p>').appendTo(picture);
                    }
                });

                var plus = self.options.moreInfos[value].length;
                $('#' + value).droppable({
                    accept: '.' + value + '-button',
                    drop: function (event, ui) {
                        plus++;
                        var div = $('<div id="' + self.options.toolTipTile + plus + '" class=" more more32"></div>')
                            .css('top', ui.position.top - $(picture).offset().top + "px").css('left', ui.position.left - $(picture).offset().left + "px").draggable(
                           { containment: picture }
                          ).appendTo(picture);
                        //add selected animationBg if animation set true
                        if (self.options.animation) {
                            $(".more").addClass(self.options.animationBg);
                        } else {
                            $(".more").addClass('noAnimation');
                        }
                        //workaround for firefox issue on trimming border-radius content
                        $(".more32").css('overflow', 'visible').css('position', 'absolute');
                        var imgButton = $('<div class="imgButtonDrag ' + self.options.button + '" title="拖动改变标注位置"></div>').appendTo(div);
                        var divInput = $('<div clas="inputDiv"></div>').insertAfter(imgButton);
                        var input = $('<input type="text" title="标注描述"/>').appendTo(divInput).focus();

                        $('<p class="pDelete" title="点击删除标注"></p>').insertAfter(input).bind('click', function () {
                            $(div).remove();
                        });
                    }
                });

            });
        },

        // Use the _setOption method to respond to changes to options

        _setOption: function (key, value) {

            // In jQuery UI 1.8, you have to manually invoke the _setOption method from the base widget	
            $.Widget.prototype._setOption.apply(this, arguments);
            // In jQuery UI 1.9 and above, you use the _super method instead
            //this._super( "_setOption", key, value );
        },

        // Use the destroy method to clean up any modifications your widget has made to the DOM
        destroy: function () {
            // In jQuery UI 1.8, you must invoke the destroy method from the base widget
            $.Widget.prototype.destroy.call(this);
            // In jQuery UI 1.9 and above, you would define _destroy instead of destroy and not call the base method
        }
    });

} (jQuery));

