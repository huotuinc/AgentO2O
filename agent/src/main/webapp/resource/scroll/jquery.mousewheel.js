/*!
 * jQuery Mousewheel 3.1.12
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

(function (factory) {
    if ( typeof define === 'function' && define.amd ) {
        // AMD. Register as an anonymous module.
        define(['jquery'], factory);
    } else if (typeof exports === 'object') {
        // Node/CommonJS style for Browserify
        module.exports = factory;
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    var toFix  = ['wheel', 'mousewheel', 'DOMMouseScroll', 'MozMousePixelScroll'],
        toBind = ( 'onwheel' in document || document.documentMode >= 9 ) ?
            ['wheel'] : ['mousewheel', 'DomMouseScroll', 'MozMousePixelScroll'],
        slice  = Array.prototype.slice,
        nullLowestDeltaTimeout, lowestDelta;

    if ( $.event.fixHooks ) {
        for ( var i = toFix.length; i; ) {
            $.event.fixHooks[ toFix[--i] ] = $.event.mouseHooks;
        }
    }

    var special = $.event.special.mousewheel = {
        version: '3.1.12',

        setup: function() {
            if ( this.addEventListener ) {
                for ( var i = toBind.length; i; ) {
                    this.addEventListener( toBind[--i], handler, false );
                }
            } else {
                this.onmousewheel = handler;
            }
            // Store the line height and page height for this particular element
            $.data(this, 'mousewheel-line-height', special.getLineHeight(this));
            $.data(this, 'mousewheel-page-height', special.getPageHeight(this));
        },

        teardown: function() {
            if ( this.removeEventListener ) {
                for ( var i = toBind.length; i; ) {
                    this.removeEventListener( toBind[--i], handler, false );
                }
            } else {
                this.onmousewheel = null;
            }
            // Clean up the data we added to the element
            $.removeData(this, 'mousewheel-line-height');
            $.removeData(this, 'mousewheel-page-height');
        },

        getLineHeight: function(elem) {
            var $elem = $(elem),
                $parent = $elem['offsetParent' in $.fn ? 'offsetParent' : 'parent']();
            if (!$parent.length) {
                $parent = $('body');
            }
            return parseInt($parent.css('fontSize'), 10) || parseInt($elem.css('fontSize'), 10) || 16;
        },

        getPageHeight: function(elem) {
            return $(elem).height();
        },

        settings: {
            adjustOldDeltas: true, // see shouldAdjustOldDeltas() below
            normalizeOffset: true  // calls getBoundingClientRect for each event
        }
    };

    $.fn.extend({
        mousewheel: function(fn) {
            return fn ? this.bind('mousewheel', fn) : this.trigger('mousewheel');
        },

        unmousewheel: function(fn) {
            return this.unbind('mousewheel', fn);
        }
    });


    function handler(event) {
        var orgEvent   = event || window.event,
            args       = slice.call(arguments, 1),
            delta      = 0,
            deltaX     = 0,
            deltaY     = 0,
            absDelta   = 0,
            offsetX    = 0,
            offsetY    = 0;
        event = $.event.fix(orgEvent);
        event.type = 'mousewheel';

        // Old school scrollwheel delta
        if ( 'detail'      in orgEvent ) { deltaY = orgEvent.detail * -1;      }
        if ( 'wheelDelta'  in orgEvent ) { deltaY = orgEvent.wheelDelta;       }
        if ( 'wheelDeltaY' in orgEvent ) { deltaY = orgEvent.wheelDeltaY;      }
        if ( 'wheelDeltaX' in orgEvent ) { deltaX = orgEvent.wheelDeltaX * -1; }

        // Firefox < 17 horizontal scrolling related to DOMMouseScroll event
        if ( 'axis' in orgEvent && orgEvent.axis === orgEvent.HORIZONTAL_AXIS ) {
            deltaX = deltaY * -1;
            deltaY = 0;
        }

        // Set delta to be deltaY or deltaX if deltaY is 0 for backwards compatabilitiy
        delta = deltaY === 0 ? deltaX : deltaY;

        // New school wheel delta (wheel event)
        if ( 'deltaY' in orgEvent ) {
            deltaY = orgEvent.deltaY * -1;
            delta  = deltaY;
        }
        if ( 'deltaX' in orgEvent ) {
            deltaX = orgEvent.deltaX;
            if ( deltaY === 0 ) { delta  = deltaX * -1; }
        }

        // No change actually happened, no reason to go any further
        if ( deltaY === 0 && deltaX === 0 ) { return; }

        // Need to convert lines and pages to pixels if we aren't already in pixels
        // There are three delta modes:
        //   * deltaMode 0 is by pixels, nothing to do
        //   * deltaMode 1 is by lines
        //   * deltaMode 2 is by pages
        if ( orgEvent.deltaMode === 1 ) {
            var lineHeight = $.data(this, 'mousewheel-line-height');
            delta  *= lineHeight;
            deltaY *= lineHeight;
            deltaX *= lineHeight;
        } else if ( orgEvent.deltaMode === 2 ) {
            var pageHeight = $.data(this, 'mousewheel-page-height');
            delta  *= pageHeight;
            deltaY *= pageHeight;
            deltaX *= pageHeight;
        }

        // Store lowest absolute delta to normalize the delta values
        absDelta = Math.max( Math.abs(deltaY), Math.abs(deltaX) );

        if ( !lowestDelta || absDelta < lowestDelta ) {
            lowestDelta = absDelta;

            // Adjust older deltas if necessary
            if ( shouldAdjustOldDeltas(orgEvent, absDelta) ) {
                lowestDelta /= 40;
            }
        }

        // Adjust older deltas if necessary
        if ( shouldAdjustOldDeltas(orgEvent, absDelta) ) {
            // Divide all the things by 40!
            delta  /= 40;
            deltaX /= 40;
            deltaY /= 40;
        }

        // Get a whole, normalized value for the deltas
        delta  = Math[ delta  >= 1 ? 'floor' : 'ceil' ](delta  / lowestDelta);
        deltaX = Math[ deltaX >= 1 ? 'floor' : 'ceil' ](deltaX / lowestDelta);
        deltaY = Math[ deltaY >= 1 ? 'floor' : 'ceil' ](deltaY / lowestDelta);

        // Normalise offsetX and offsetY properties
        if ( special.settings.normalizeOffset && this.getBoundingClientRect ) {
            var boundingRect = this.getBoundingClientRect();
            offsetX = event.clientX - boundingRect.left;
            offsetY = event.clientY - boundingRect.top;
        }

        // Add information to the event object
        event.deltaX = deltaX;
        event.deltaY = deltaY;
        event.deltaFactor = lowestDelta;
        event.offsetX = offsetX;
        event.offsetY = offsetY;
        // Go ahead and set deltaMode to 0 since we converted to pixels
        // Although this is a little odd since we overwrite the deltaX/Y
        // properties with normalized deltas.
        event.deltaMode = 0;

        // Add event and delta to the front of the arguments
        args.unshift(event, delta, deltaX, deltaY);

        // Clearout lowestDelta after sometime to better
        // handle multiple device types that give different
        // a different lowestDelta
        // Ex: trackpad = 3 and mouse wheel = 120
        if (nullLowestDeltaTimeout) { clearTimeout(nullLowestDeltaTimeout); }
        nullLowestDeltaTimeout = setTimeout(nullLowestDelta, 200);

        return ($.event.dispatch || $.event.handle).apply(this, args);
    }

    function nullLowestDelta() {
        lowestDelta = null;
    }

    function shouldAdjustOldDeltas(orgEvent, absDelta) {
        // If this is an older event and the delta is divisable by 120,
        // then we are assuming that the browser is treating this as an
        // older mouse wheel event and that we should divide the deltas
        // by 40 to try and get a more usable deltaFactor.
        // Side note, this actually impacts the reported scroll distance
        // in older browsers and can cause scrolling to be slower than native.
        // Turn this off by setting $.event.special.mousewheel.settings.adjustOldDeltas to false.
        return special.settings.adjustOldDeltas && orgEvent.type === 'mousewheel' && absDelta % 120 === 0;
    }

}));

$(function() {

    // the element we want to apply the jScrollPane
    var $el	= $('.jp-container').jScrollPane({
            verticalGutter 	: -16
        }),

    // the extension functions and options
        extensionPlugin 	= {

            extPluginOpts	: {
                // speed for the fadeOut animation
                mouseLeaveFadeSpeed	: 500,
                // scrollbar fades out after hovertimeout_t milliseconds
                hovertimeout_t		: 1000,
                // also, it will be shown when we start to scroll and hidden when stopping
                useTimeout			: true,
                // the extension only applies for devices with width > deviceWidth
                deviceWidth			: 980
            },
            hovertimeout	: null, // timeout to hide the scrollbar
            isScrollbarHover: false,// true if the mouse is over the scrollbar
            elementtimeout	: null,	// avoids showing the scrollbar when moving from inside the element to outside, passing over the scrollbar
            isScrolling		: false,// true if scrolling
            addHoverFunc	: function() {

                // run only if the window has a width bigger than deviceWidth
                if( $(window).width() <= this.extPluginOpts.deviceWidth ) return false;

                var instance		= this;

                // functions to show / hide the scrollbar
                $.fn.jspmouseenter 	= $.fn.show;
                $.fn.jspmouseleave 	= $.fn.fadeOut;

                // hide the jScrollPane vertical bar
                var $vBar			= this.getContentPane().siblings('.jspVerticalBar').hide();

                /*
                 * mouseenter / mouseleave events on the main element
                 * also scrollstart / scrollstop - @James Padolsey : http://james.padolsey.com/javascript/special-scroll-events-for-jquery/
                 */
                $el.bind('mouseenter.jsp',function() {

                    // show the scrollbar
                    $vBar.stop( true, true ).jspmouseenter();

                    if( !instance.extPluginOpts.useTimeout ) return false;

                    // hide the scrollbar after hovertimeout_t ms
                    clearTimeout( instance.hovertimeout );
                    instance.hovertimeout 	= setTimeout(function() {
                        // if scrolling at the moment don't hide it
                        if( !instance.isScrolling )
                            $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    }, instance.extPluginOpts.hovertimeout_t );


                }).bind('mouseleave.jsp',function() {

                    // hide the scrollbar
                    if( !instance.extPluginOpts.useTimeout )
                        $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    else {
                        clearTimeout( instance.elementtimeout );
                        if( !instance.isScrolling )
                            $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    }
                    //手动添加，解决果文字先变大再变小还出现滚动条的BUG
                    var $window_height = $(window).height()-110;
                    var $jp_height = $('.jp-container').height();
                    if($jp_height < $window_height){
                        $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    }
                });

                if( this.extPluginOpts.useTimeout ) {

                    $el.bind('scrollstart.jsp', function() {

                        // when scrolling show the scrollbar
                        clearTimeout( instance.hovertimeout );
                        instance.isScrolling	= true;
                        $vBar.stop( true, true ).jspmouseenter();

                    }).bind('scrollstop.jsp', function() {

                        // when stop scrolling hide the scrollbar (if not hovering it at the moment)
                        clearTimeout( instance.hovertimeout );
                        instance.isScrolling	= false;
                        instance.hovertimeout 	= setTimeout(function() {
                            if( !instance.isScrollbarHover )
                                $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                        }, instance.extPluginOpts.hovertimeout_t );

                    });

                    // wrap the scrollbar
                    // we need this to be able to add the mouseenter / mouseleave events to the scrollbar
                    var $vBarWrapper	= $('<div/>').css({
                        position	: 'absolute',
                        left		: $vBar.css('left'),
                        top			: $vBar.css('top'),
                        right		: $vBar.css('right'),
                        bottom		: $vBar.css('bottom'),
                        width		: $vBar.width(),
                        height		: $vBar.height()
                    }).bind('mouseenter.jsp',function() {

                        clearTimeout( instance.hovertimeout );
                        clearTimeout( instance.elementtimeout );

                        instance.isScrollbarHover	= true;

                        // show the scrollbar after 100 ms.
                        // avoids showing the scrollbar when moving from inside the element to outside, passing over the scrollbar
                        instance.elementtimeout	= setTimeout(function() {
                            $vBar.stop( true, true ).jspmouseenter();
                        }, 100 );

                    }).bind('mouseleave.jsp',function() {

                        // hide the scrollbar after hovertimeout_t
                        clearTimeout( instance.hovertimeout );
                        instance.isScrollbarHover	= false;
                        instance.hovertimeout = setTimeout(function() {
                            // if scrolling at the moment don't hide it
                            if( !instance.isScrolling )
                                $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                        }, instance.extPluginOpts.hovertimeout_t );

                    });

                    $vBar.wrap( $vBarWrapper );

                }

            }

        },

    // the jScrollPane instance
        jspapi = $el.data('jsp');

    // extend the jScollPane by merging
    $.extend( true, jspapi, extensionPlugin );
    jspapi.addHoverFunc();

});

$(function() {

    // the element we want to apply the jScrollPane
    var $el	= $('.jp-container2').jScrollPane({
            verticalGutter 	: -16
        }),

    // the extension functions and options
        extensionPlugin 	= {

            extPluginOpts	: {
                // speed for the fadeOut animation
                mouseLeaveFadeSpeed	: 500,
                // scrollbar fades out after hovertimeout_t milliseconds
                hovertimeout_t		: 1000,
                // also, it will be shown when we start to scroll and hidden when stopping
                useTimeout			: true,
                // the extension only applies for devices with width > deviceWidth
                deviceWidth			: 980
            },
            hovertimeout	: null, // timeout to hide the scrollbar
            isScrollbarHover: false,// true if the mouse is over the scrollbar
            elementtimeout	: null,	// avoids showing the scrollbar when moving from inside the element to outside, passing over the scrollbar
            isScrolling		: false,// true if scrolling
            addHoverFunc	: function() {

                // run only if the window has a width bigger than deviceWidth
                if( $(window).width() <= this.extPluginOpts.deviceWidth ) return false;

                var instance		= this;

                // functions to show / hide the scrollbar
                $.fn.jspmouseenter 	= $.fn.show;
                $.fn.jspmouseleave 	= $.fn.fadeOut;

                // hide the jScrollPane vertical bar
                var $vBar			= this.getContentPane().siblings('.jspVerticalBar').hide();

                /*
                 * mouseenter / mouseleave events on the main element
                 * also scrollstart / scrollstop - @James Padolsey : http://james.padolsey.com/javascript/special-scroll-events-for-jquery/
                 */
                $el.bind('mouseenter.jsp',function() {

                    // show the scrollbar
                    $vBar.stop( true, true ).jspmouseenter();

                    if( !instance.extPluginOpts.useTimeout ) return false;

                    // hide the scrollbar after hovertimeout_t ms
                    clearTimeout( instance.hovertimeout );
                    instance.hovertimeout 	= setTimeout(function() {
                        // if scrolling at the moment don't hide it
                        if( !instance.isScrolling )
                            $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    }, instance.extPluginOpts.hovertimeout_t );


                }).bind('mouseleave.jsp',function() {

                    // hide the scrollbar
                    if( !instance.extPluginOpts.useTimeout )
                        $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    else {
                        clearTimeout( instance.elementtimeout );
                        if( !instance.isScrolling )
                            $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    }
                    //手动添加，解决果文字先变大再变小还出现滚动条的BUG
                    var $window_height = $(window).height()-(90);
                    var $jp_height = $('.jp-container2').height();
                    if($jp_height < $window_height){
                        $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                    }
                });

                if( this.extPluginOpts.useTimeout ) {

                    $el.bind('scrollstart.jsp', function() {

                        // when scrolling show the scrollbar
                        clearTimeout( instance.hovertimeout );
                        instance.isScrolling	= true;
                        $vBar.stop( true, true ).jspmouseenter();

                    }).bind('scrollstop.jsp', function() {

                        // when stop scrolling hide the scrollbar (if not hovering it at the moment)
                        clearTimeout( instance.hovertimeout );
                        instance.isScrolling	= false;
                        instance.hovertimeout 	= setTimeout(function() {
                            if( !instance.isScrollbarHover )
                                $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                        }, instance.extPluginOpts.hovertimeout_t );

                    });

                    // wrap the scrollbar
                    // we need this to be able to add the mouseenter / mouseleave events to the scrollbar
                    var $vBarWrapper	= $('<div/>').css({
                        position	: 'absolute',
                        left		: $vBar.css('left'),
                        top			: $vBar.css('top'),
                        right		: $vBar.css('right'),
                        bottom		: $vBar.css('bottom'),
                        width		: $vBar.width(),
                        height		: $vBar.height()
                    }).bind('mouseenter.jsp',function() {

                        clearTimeout( instance.hovertimeout );
                        clearTimeout( instance.elementtimeout );

                        instance.isScrollbarHover	= true;

                        // show the scrollbar after 100 ms.
                        // avoids showing the scrollbar when moving from inside the element to outside, passing over the scrollbar
                        instance.elementtimeout	= setTimeout(function() {
                            $vBar.stop( true, true ).jspmouseenter();
                        }, 100 );

                    }).bind('mouseleave.jsp',function() {

                        // hide the scrollbar after hovertimeout_t
                        clearTimeout( instance.hovertimeout );
                        instance.isScrollbarHover	= false;
                        instance.hovertimeout = setTimeout(function() {
                            // if scrolling at the moment don't hide it
                            if( !instance.isScrolling )
                                $vBar.stop( true, true ).jspmouseleave( instance.extPluginOpts.mouseLeaveFadeSpeed || 0 );
                        }, instance.extPluginOpts.hovertimeout_t );

                    });

                    $vBar.wrap( $vBarWrapper );

                }

            }

        },

    // the jScrollPane instance
        jspapi = $el.data('jsp');

    // extend the jScollPane by merging
    $.extend( true, jspapi, extensionPlugin );
    jspapi.addHoverFunc();

});