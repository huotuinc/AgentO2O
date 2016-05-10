/*
 * $Id: om-core.js,v 1.27 2012/06/19 08:40:21 licongping Exp $
 * operamasks-ui om-core @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 */
(function( $, undefined ) {
// prevent duplicate loading
// this is only a problem because we proxy existing functions
// and we don't want to double proxy them
$.om = $.om || {};
if ( $.om.version ) {
	return;
}

$.extend( $.om, {
	version: "2.0",
	keyCode: {
	    TAB: 9,
	    ENTER: 13,
	    ESCAPE: 27,
	    SPACE: 32,
		LEFT: 37,
		UP: 38,
		RIGHT: 39,
		DOWN: 40
	},
	lang : {
		// 获取属性的国际化字符串，如果组件的options中已经设置这个值就直接使用，否则从$.om.lang[comp]中获取
		_get : function(options, comp, attr){
			return options[attr] ? options[attr] : $.om.lang[comp][attr]; 
		}
	}
});
// plugins
$.fn.extend({
	propAttr: $.fn.prop || $.fn.attr,
	_oldFocus: $.fn.focus,//为避免与jQuery ui冲突导致死循环，这里不要取名为'_focus'
	//设置元素焦点（delay：延迟时间）
	focus: function( delay, fn ) {
		return typeof delay === "number" ?
			this.each(function() {
				var elem = this;
				setTimeout(function() {
					$( elem ).focus();
					if ( fn ) {
						fn.call( elem );
					}
				}, delay );
			}) :
			this._oldFocus.apply( this, arguments );
	},
	//获取设置滚动属性的 父元素
	scrollParent: function() {
		var scrollParent;
		if (($.browser.msie && (/(static|relative)/).test(this.css('position'))) || (/absolute/).test(this.css('position'))) {
			scrollParent = this.parents().filter(function() {
				return (/(relative|absolute|fixed)/).test($.curCSS(this,'position',1)) && (/(auto|scroll)/).test($.curCSS(this,'overflow',1)+$.curCSS(this,'overflow-y',1)+$.curCSS(this,'overflow-x',1));
			}).eq(0);
		} else {
			scrollParent = this.parents().filter(function() {
				return (/(auto|scroll)/).test($.curCSS(this,'overflow',1)+$.curCSS(this,'overflow-y',1)+$.curCSS(this,'overflow-x',1));
			}).eq(0);
		}
		return (/fixed/).test(this.css('position')) || !scrollParent.length ? $(document) : scrollParent;
	},
	//设置或获取元素的垂直坐标
	zIndex: function( zIndex ) {
		if ( zIndex !== undefined ) {
			return this.css( "zIndex", zIndex );
		}
		if ( this.length ) {
			var elem = $( this[ 0 ] ), position, value;
			while ( elem.length && elem[ 0 ] !== document ) {
				// Ignore z-index if position is set to a value where z-index is ignored by the browser
				// This makes behavior of this function consistent across browsers
				// WebKit always returns auto if the element is positioned
				position = elem.css( "position" );
				if ( position === "absolute" || position === "relative" || position === "fixed" ) {
					// IE returns 0 when zIndex is not specified
					// other browsers return a string
					// we ignore the case of nested elements with an explicit value of 0
					// <div style="z-index: -10;"><div style="z-index: 0;"></div></div>
					value = parseInt( elem.css( "zIndex" ), 10 );
					if ( !isNaN( value ) && value !== 0 ) {
						return value;
					}
				}
				elem = elem.parent();
			}
		}
		return 0;
	},
	//设置元素不支持被选择
	disableSelection: function() {
		return this.bind( ( $.support.selectstart ? "selectstart" : "mousedown" ) +
			".om-disableSelection", function( event ) {
				event.preventDefault();
			});
	},
	//设置元素支持被选择
	enableSelection: function() {
		return this.unbind( ".om-disableSelection" );
	}
});
// 扩展innerWidth、innerHeight、outerWidth和outerHeight方法，如果不传参则获取值，如果传参则设置计算后的宽高。
$.each( [ "Width", "Height" ], function( i, name ) {
	var side = name === "Width" ? [ "Left", "Right" ] : [ "Top", "Bottom" ],
		type = name.toLowerCase(),
		orig = {
			innerWidth: $.fn.innerWidth,
			innerHeight: $.fn.innerHeight,
			outerWidth: $.fn.outerWidth,
			outerHeight: $.fn.outerHeight
		};

	function reduce( elem, size, border, margin ) {
		$.each( side, function() {
			size -= parseFloat( $.curCSS( elem, "padding" + this, true) ) || 0;
			if ( border ) {
				size -= parseFloat( $.curCSS( elem, "border" + this + "Width", true) ) || 0;
			}
			if ( margin ) {
				size -= parseFloat( $.curCSS( elem, "margin" + this, true) ) || 0;
			}
		});
		return size;
	}

	$.fn[ "inner" + name ] = function( size ) {
		if ( size === undefined ) {
			// 返回innerWidth/innerHeight
			return orig[ "inner" + name ].call( this );
		}
		return this.each(function() {
			// 设置宽度/高度 = (size - padding)
			$( this ).css( type, reduce( this, size ) + "px" );
		});
	};

	$.fn[ "outer" + name] = function( size, margin ) {
		if ( typeof size !== "number" ) {
			// 返回outerWidth/outerHeight
			return orig[ "outer" + name ].call( this, size );
		}
		return this.each(function() {
			// 设置宽度/高度 = (size - padding - border - margin)
			$( this).css( type, reduce( this, size, true, margin ) + "px" );
		});
	};
});
// selectors
function focusable( element, isTabIndexNotNaN ) {
	var nodeName = element.nodeName.toLowerCase();
	if ( "area" === nodeName ) {
		var map = element.parentNode,
			mapName = map.name,
			img;
		if ( !element.href || !mapName || map.nodeName.toLowerCase() !== "map" ) {
			return false;
		}
		img = $( "img[usemap=#" + mapName + "]" )[0];
		return !!img && visible( img );
	}
	return ( /input|select|textarea|button|object/.test( nodeName )
		? !element.disabled
		: "a" == nodeName
			? element.href || isTabIndexNotNaN
			: isTabIndexNotNaN)
		// the element and all of its ancestors must be visible
		&& visible( element );
}
function visible( element ) {
	return !$( element ).parents().andSelf().filter(function() {
		return $.curCSS( this, "visibility" ) === "hidden" ||
			$.expr.filters.hidden( this );
	}).length;
}
$.extend( $.expr[ ":" ], {
	data: function( elem, i, match ) {
		return !!$.data( elem, match[ 3 ] );
	},
	focusable: function( element ) {
		return focusable( element, !isNaN( $.attr( element, "tabindex" ) ) );
	},
	tabbable: function( element ) {
		var tabIndex = $.attr( element, "tabindex" ),
			isTabIndexNaN = isNaN( tabIndex );
		return ( isTabIndexNaN || tabIndex >= 0 ) && focusable( element, !isTabIndexNaN );
	}
});
// support
$(function() {
	var body = document.body,
		div = body.appendChild( div = document.createElement( "div" ) );
	$.extend( div.style, {
		minHeight: "100px",
		height: "auto",
		padding: 0,
		borderWidth: 0
	});
	// 判断当前浏览器环境是否支持minHeight属性
	$.support.minHeight = div.offsetHeight === 100;
	$.support.selectstart = "onselectstart" in div;
	// set display to none to avoid a layout bug in IE
	// http://dev.jquery.com/ticket/4014
	body.removeChild( div ).style.display = "none";
});

// deprecated
$.extend( $.om, {
	// $.om.plugin is deprecated.  Use the proxy pattern instead.
	plugin: {
		add: function( module, option, set ) {
			var proto = $.om[module].prototype;
			for ( var i in set ) {
				proto.plugins[ i ] = proto.plugins[ i ] || [];
				proto.plugins[ i ].push( [ option, set[ i ] ] );
			}
		},
		call: function( instance, name, args ) {
			var set = instance.plugins[ name ];
			if ( !set || !instance.element[ 0 ].parentNode ) {
				return;
			}
			for ( var i = 0; i < set.length; i++ ) {
				if ( instance.options[ set[ i ][ 0 ] ] ) {
					set[ i ][ 1 ].apply( instance.element, args );
				}
			}
		}
	}
});

})( jQuery );


(function( $, undefined ) {
// jQuery 1.4+
if ( $.cleanData ) {
	var _cleanData = $.cleanData;
	$.cleanData = function( elems ) {
		for ( var i = 0, elem; (elem = elems[i]) != null; i++ ) { 
			$( elem ).triggerHandler( "om-remove" );
		}
		_cleanData( elems );
	};
}

$.omWidget = function( name, base, prototype ) {
	var namespace = name.split( "." )[ 0 ],
		fullName;
	name = name.split( "." )[ 1 ];
	fullName = namespace + "-" + name;
	// 例如参数name='om.tabs'，变成namespace='om',name='tabs',fullName='om-tabs' 
	// base默认为Widget类，组件默认会继承base类的所有方法  
	if ( !prototype ) {
		prototype = base;
		base = $.OMWidget;
	}
	// create selector for plugin
	$.expr[ ":" ][ fullName ] = function( elem ) {
		return !!$.data( elem, name );
	};
	// 创建命名空间$.om.tabs  
	$[ namespace ] = $[ namespace ] || {};
	// 组件的构造函数
	$[ namespace ][ name ] = function( options, element ) {
		// allow instantiation without initializing for simple inheritance
		if ( arguments.length ) {
			this._createWidget( options, element );
		}
	};
	// 初始化父类，一般调用了$.Widget  
	var basePrototype = new base();
	// we need to make the options hash a property directly on the new instance
	// otherwise we'll modify the options hash on the prototype that we're
	// inheriting from
//		$.each( basePrototype, function( key, val ) {
//			if ( $.isPlainObject(val) ) {
//				basePrototype[ key ] = $.extend( {}, val );
//			}
//		});
	basePrototype.options = $.extend( true, {}, basePrototype.options );
	// 给om.tabs继承父类的所有原型方法和参数  
	$[ namespace ][ name ].prototype = $.extend( true, basePrototype, {
		namespace: namespace,
		widgetName: name,
		// 组件的事件名前缀，调用_trigger的时候会默认给trigger的事件加上前缀  
        // 例如_trigger('create')实际会触发'tabscreate'事件  
		widgetEventPrefix: $[ namespace ][ name ].prototype.widgetEventPrefix || name,
		widgetBaseClass: fullName
	}, prototype );
	// 把tabs方法挂到jquery对象上，也就是$('#tab1').tabs();  
	$.omWidget.bridge( name, $[ namespace ][ name ] );
};

$.omWidget.bridge = function( name, object ) {
	$.fn[ name ] = function( options ) {
		// 如果tabs方法第一个参数是string类型，则认为是调用组件的方法，否则调用options方法  
		var isMethodCall = typeof options === "string",
			args = Array.prototype.slice.call( arguments, 1 ),
			returnValue = this;
		// allow multiple hashes to be passed on init
		options = !isMethodCall && args.length ?
			$.extend.apply( null, [ true, options ].concat(args) ) :
			options;
		// '_'开头的方法被认为是内部方法，不会被执行，如$('#tab1').tabs('_init')  
		if ( isMethodCall && options.charAt( 0 ) === "_" ) {
			return returnValue;
		}
		if ( isMethodCall ) {
			this.each(function() {
				// 执行组件方法  
				var instance = $.data( this, name );
				if (options == 'options') {
				    returnValue = instance && instance.options;
				    return false;
                } else {
    				var	methodValue = instance && $.isFunction( instance[options] ) ?
    						instance[ options ].apply( instance, args ) : instance;
    				if ( methodValue !== instance && methodValue !== undefined ) {
    					returnValue = methodValue;
    					return false;
    				}
                }
			});
		} else {
			// 调用组件的options方法  
			this.each(function() {
				var instance = $.data( this, name );
				if ( instance ) {
					// 设置options后再次调用_init方法，第一次调用是在_createWidget方法里面。这个方法需要开发者去实现。  
                    // 主要是当改变组件中某些参数后可能需要对组件进行重画  
                    instance._setOptions( options || {} );
				    $.extend(instance.options, options);
				    $(instance.beforeInitListeners).each(function(){
				        this.call(instance);
				    });
					instance._init();
					$(instance.initListeners).each(function(){
				        this.call(instance);
				    });
				} else {
					// 没有实例的话，在这里调用组件类的构造函数，并把构造后的示例保存在dom的data里面。注意这里的this是dom，object是模块类 
					$.data( this, name, new object( options, this ) );
				}
			});
		}

		return returnValue;
	};
};
$.omWidget.addCreateListener = function(name,fn){
    var temp=name.split( "." );
    $[ temp[0] ][ temp[1] ].prototype.createListeners.push(fn);
};
$.omWidget.addInitListener = function(name,fn){
    var temp=name.split( "." );
    $[ temp[0] ][ temp[1] ].prototype.initListeners.push(fn);
};
$.omWidget.addBeforeInitListener = function(name,fn){
    var temp=name.split( "." );
    $[ temp[0] ][ temp[1] ].prototype.beforeInitListeners.push(fn);
};
$.OMWidget = function( options, element ) {
    this.createListeners=[];
    this.initListeners=[];
    this.beforeInitListeners=[];
	// allow instantiation without initializing for simple inheritance
	if ( arguments.length ) {
		this._createWidget( options, element );
	}
};
$.OMWidget.prototype = {
	widgetName: "widget",
	widgetEventPrefix: "",
	options: {
		disabled: false
	},
	_createWidget: function( options, element ) {
		// $.widget.bridge stores the plugin instance, but we do it anyway
		// so that it's stored even before the _create function runs
		$.data( element, this.widgetName, this );
		this.element = $( element );
		this.options = $.extend( true, {},
			this.options,
			this._getCreateOptions(),
			options );
		var self = this;
		//注意，不要少了前边的 "om-"，不然会与jquery-ui冲突
		this.element.bind( "om-remove._" + this.widgetName, function() {
			self.destroy();
		});
		// 开发者实现  
		this._create();
		$(this.createListeners).each(function(){
	        this.call(self);
	    });
		// 如果绑定了初始化的回调函数，会在这里触发。注意绑定的事件名是需要加上前缀的，如$('#tab1').bind('tabscreate',function(){});  
		this._trigger( "create" );
		// 开发者实现 
		$(this.beforeInitListeners).each(function(){
	        this.call(self);
	    });
		this._init();
		$(this.initListeners).each(function(){
	        this.call(self);
	    });
	},
	_getCreateOptions: function() {
		return $.metadata && $.metadata.get( this.element[0] )[ this.widgetName ];
	},
	_create: function() {},
	_init: function() {},
	destroy: function() {
		this.element
			.unbind( "." + this.widgetName )
			.removeData( this.widgetName );
		this.widget()
			.unbind( "." + this.widgetName );
	},
	widget: function() {
		return this.element;
	},
	option: function( key, value ) {
        var options = key;
        if ( arguments.length === 0 ) {
            // don't return a reference to the internal hash
            return $.extend( {}, this.options );
        }
        if  (typeof key === "string" ) {
            if ( value === undefined ) {
                return this.options[ key ]; // 获取值
            }
            options = {};
            options[ key ] = value;
        }
        this._setOptions( options ); // 设置值
        return this;
    },
	_setOptions: function( options ) {
		var self = this;
		$.each( options, function( key, value ) {
			self._setOption( key, value );
		});
		return this;
	},
	_setOption: function( key, value ) {
		this.options[ key ] = value;
		return this;
	},
	
	// $.widget中优化过的trigger方法。type是回调事件的名称，如"onRowClick"，event是触发回调的事件（通常没有这个事件的时候传null）
	// 这个方法只声明了两个参数，如有其他参数可以直接写在event参数后面
	_trigger: function( type, event ) {
		// 获取初始化配置config中的回调方法
		var callback = this.options[ type ];
		// 封装js标准event对象为jquery的Event对象
		event = $.Event( event );
		event.type = type;
		// copy original event properties over to the new event
		// this would happen if we could call $.event.fix instead of $.Event
		// but we don't have a way to force an event to be fixed multiple times
		if ( event.originalEvent ) {
			for ( var i = $.event.props.length, prop; i; ) {
				prop = $.event.props[ --i ];
				event[ prop ] = event.originalEvent[ prop ];
			}
		}
		// 构造传给回调函数的参数，event放置在最后
		var newArgs = [],
			argLength = arguments.length;
		for(var i = 2; i < argLength; i++){
			newArgs[i-2] = arguments[i];
		}
		if( argLength > 1){
			newArgs[argLength-2] = arguments[1];
		}
		return !( $.isFunction(callback) &&
			callback.apply( this.element, newArgs ) === false ||
			event.isDefaultPrevented() );
	}
};
})( jQuery );/*
 * $Id: om-panel.js,v 1.47 2012/06/20 08:29:10 chentianzhen Exp $
 * operamasks-ui omPanel @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *   om-core.js
 */
(function($) {
	var innerToolId = ['collapse','min','max','close'],
		innerToolCls = ['om-panel-tool-collapse','om-panel-tool-expand','om-panel-tool-min','om-panel-tool-max','om-panel-tool-close'],
		effects = {anim:true , speed: 'fast'};
	/**
     * @name omPanel
     * @class 面版是一个布局组件，同时也是一个展示内容的容器。<br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>可以使用本地数据源，也可以使用远程数据源，同时提供友好的错误处理机制。</li>
     *      <li>支持动态修改标题内容和图标。</li>
     *      <li>工具条按钮内置与可扩展。</li>
     *      <li>提供丰富的事件。</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     * $(document).ready(function() {
     *     $('#panel').omPanel({
     *         width: '400px',
     *         height: '200px',
     *         title: 'panel标题',
     *         collapsed: false,//组件创建后为收起状态
     *         collapsible: true,//渲染收起与展开按钮
     *         closable: true, //渲染关闭按钮
     *         onBeforeOpen: function(event){if(window.count!==0)return false;}, 
     *         onOpen: function(event){alert('panel被打开了。');}
     *     });
     * });
     * &lt;/script>
     * 
     * &lt;input id="panel"/>
     * </pre>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
	$.omWidget("om.omPanel" , {
		options:/** @lends omPanel#*/{
			/**
			 * panel的标题，位于头部左边的位置。
			 * @type String 
			 * @default 无
			 * @example
             * $("#panel").omPanel({title:"&lt;span style='color:red'&gt;标题&lt;/span&gt;"});<br/>
             * 因为所给的标题会当成html文本，所以当出现特殊字符时必须进行转义，如"<"必须转义为"&amp;lt;"。
			 */
			title: '',
			/**
			 * panel的图标样式，位于头部左边的位置。
			 * @name omPanel#iconCls
			 * @type String
			 * @default 无
			 * @example
			 * $("#panel").omPanel({iconCls:'myCls'});(myCls为自定义的css样式类别)
			 */
			/**
			 * panel组件的宽度，可取值为'auto'（默认情况,由浏览器决定宽度），可以取值为'fit'，表示适应父容器的大小（width:100%）。任何其他的值（比如百分比、数字、em单位、px单位的值等等）将被直接赋给width属性。 
			 * @type Number,String
			 * @default 'auto'
			 * @example
			 * $("#panel").omPanel({width:'300px'});
			 */ 
			width: 'auto',
			/**
			 * panel组件的高度，可取值为'auto'（由内容决定高度），可以取值为'fit'，表示适应父容器的大小（height:100%）。任何其他的值（比如百分比、数字、em单位、px单位的值等等）将被直接赋给height属性。
			 * @type Number,String
			 * @default 'auto'
			 * @example
			 * $("#panel").omPanel({height:'200px'});
			 */
			height: 'auto',
			/**
			 * 在组件创建时是否要渲染其头部。
			 * @type Boolean
			 * @default true
			 * @example
			 * $("#panel").omPanel({header:false}); //不要渲染panel的头部
			 */
			header: true,
			/**
			 * 组件内容的数据来源。当设置了此值后，组件会从远程获取数据来填充主体部分。可以调用reload方法动态更新组件主体内容。
			 * @name omPanel#url
			 * @type String
			 * @default 无
			 * @example
			 * $("#panel").omPanel({url:'http://www.ui.operamasks.org/test'});
			 */
			/**
			 * 组件创建时是否显示收起工具按钮(位于头部右边)。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({collapsible:true});
			 */
			collapsible: false,
			/**
			 * 组件创建时是否显示关闭工具按钮(位于头部右边)。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({closable:true});
			 */
			closable: false,
			/**
			 * 组件创建后是否处于关闭状态，可调用open方法动态打开该组件。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({closed:false});
			 */
			closed: false,
			/**
			 * 组件创建后是否处于收起状态，可调用expand方法动态展开组件主体内容。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({collapsed:true});
			 */
			collapsed: false,
			/**
			 * 组件头部右上角的工具条。<br/>
			 * 当为Array时，数组中每个对象代表了一个工具按钮,每个对象格式如下:<br/>
			 * <pre>
			 * {
			 *     id:内置工具按钮，可选值为'min'，'max'，'close'，collapse'。
			 *     iconCls:工具按钮的样式，如果id属性存在，则忽略此属性，此属性可为String或者Array，
			 *             当为String时，表示按钮在所有状态下的样式，当为Array时，索引0表示按钮
			 * 	           常态下的样式，索引1表示按钮被鼠标hover时的样式。
			 *     handler:按钮图标被单击时触发的事件(如果没有提供此属性，则按钮按下后会没有反应)。
			 * }
			 * </pre>
			 * 补充:考虑到用户习惯，默认情况下，如果collapsible=true，则会显示收起按钮，它将永远排在第一个位置。<br/>
			 * 如果closable=true,则会显示关闭按钮，它将永远排在最后一个位置。 <br/>
			 * 所以可以认为tools产生的工具条会放在中间，如果用户不想受限于这样的排序，则不要设置collapsible和closable这两个属性，直接利用tools属性重新定义想要的工具条。 <br/><br/>
			 * 
			 * 当为Selector时，此Selector对应的dom结构将作为tool的一部分进行渲染，这时事件的注册，样式的变换将完全交由用户处理。
			 * @type Array,Selector
			 * @default []
			 * @example
			 * <pre>
			 * $("#panel").omPanel({tools:[
			 *         {id:'min',handler:function(panel , event){ alert("最小化操作还未实现."); }},
			 *         {id:'max',handler:function(panel , event){ alert("最大化操作还未实现."); }}
			 *     ]}
			 * );
			 * </pre>
			 */
			tools: [],
			/**
			 * 远程加载数据时的提示信息，只有设置了url或者调用reload方法时传入一个url才生效。
			 * 内置了一种默认的样式(显示一个正在加载的图标)，当传入字符串"default"时启用此默认样式。
			 * @type String
			 * @default 'default'
			 * @example
			 * $("#panel").omPanel({loadingMessage:"&lt;img src='load.gif'&gt;&lt;/img&gt;loading......"});
			 */
			loadingMessage: "default",
			/**
			 * 在远程取数时，拿到数据后，显示数据前的一个预处理函数，类似于一个过滤器的作用，该函数的返回值即为最终的数据。
			 * @name omPanel#preProcess
			 * @type Function
			 * @param data 服务端返回的数据 
			 * @param textStatus 服务端响应的状态
			 * @default null
			 * @example
			 * $("#panel").omPanel({url:'test.do',preProcess:function(data , textStatus){return 'test';}});
			 * //不管服务器返回什么数据，主体内容永远为'test'
			 */
			/**
			 * 远程取数发生错误时触发的函数。
			 * @event
			 * @param xmlHttpRequest XMLHttpRequest对象
			 * @param textStatus  错误类型
			 * @param errorThrown  捕获的异常对象
			 * @param event jQuery.Event对象
			 * @name omPanel#onError
			 * @type Function
			 * @default null
			 * @example
			 * <pre>
			 * $("#panel").omPanel({url:'test.do',
			 *     onError:function(xmlHttpRequest, textStatus, errorThrown, event){
			 *         alert('网络发生了错误，请稍后再试。');
			 *     }
			 * });
			 * </pre>
			 */
			/**
			 * 远程取数成功后触发的函数。
			 * @event
			 * @param data 从服务器返回的数据
			 * @param textStatus 服务端响应的状态
			 * @param xmlHttpRequest XMLHttpRequest对象
			 * @param event jQuery.Event对象
			 * @name omPanel#onSuccess
			 * @type Function
			 * @default null
			 * @example
			 * <pre>
			 * $("#panel").omPanel({url:'test.do',
			 *     onSuccess:function(data, textStatus, xmlHttpRequest, event){
			 *         alert("服务器返回的数据为:" + data);
			 *     }
			 * });
			 * </pre>
			 */
			/**
			 * 打开panel组件前触发的函数，返回false可以阻止打开。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeOpen
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeOpen:function(event){alert("永远打不开该组件.");return false;}});
			 */
			/**
			 * 打开panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onOpen
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onOpen:function(event){alert("panel已经被打开了。");}});
			 */
			/**
			 * 关闭panel组件前触发的函数，返回false可以阻止关闭。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeClose
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeClose:function(event){alert("该组件即将被关闭。");}});
			 */
			/**
			 * 关闭panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onClose
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onClose:function(event){alert("panel已经被关闭了。");}});
			 */
			/**
			 * 收起panel组件前触发的函数，返回false可以阻止收起。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeCollapse
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeCollapse:function(event){alert("该组件即将被收起。");}});
			 */
			/**
			 * 收起panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onCollapse
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onCollapse:function(event){alert("panel已经被收起了。");}});
			 */
			/**
			 * 展开panel组件前触发的函数，返回false可以阻止展开。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeExpand
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeExpand:function(event){alert("该组件即将被展开。");}});
			 */
			/**
			 * 展开panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onExpand
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onExpand:function(event){alert("panel已经被展开了。");}});
			 */
			 /**
			  *组件的关闭模式，当调用close方法时怎么处理组件的关闭，"hidden"表示直接display:none ,"visibility"表示缩小为1px的点
			  * 此属性暂时不暴露
			  */
			 _closeMode : "hidden",
			 _helpMsg : false
		},
		_create: function(){
		    this.element.addClass("om-panel-body om-widget-content")
		    	.wrap("<div class='om-widget om-panel'></div>");
		},
		_init: function(){
			var options = this.options,
				$body = this.element,
				$parent = $body.parent(),
				$header;
			this._renderHeader();
			$header = $body.prev();
			if(options.header === false){
		 		$body.addClass("om-panel-noheader");
		 	}
			this._bindEvent();
		 	this._resize($parent);
		 	var headerHeight = options.header !== false? $header.outerHeight() : 0;
		 	if(options.collapsed !== false){
		 		"auto"!==options.height && $parent.height(headerHeight);		 		
		 		$body.hide();
		 		if(options.header !== false){
		 			$header.find(">.om-panel-tool >.om-panel-tool-collapse").removeClass("om-panel-tool-collapse")
		 				.addClass("om-panel-tool-expand");
		 		}
		 	}else{
		 		$body.show();
		 		"auto"!==options.height && $parent.height(headerHeight + $body.outerHeight());
		 		if(options.header !== false){
		 			$header.find(">.om-panel-tool >.om-panel-tool-expand").removeClass("om-panel-tool-expand")
		 				.addClass("om-panel-tool-collapse");
		 		}	
		 	}
		 	options.closed !== false? this._hide($parent) : this._show($parent);
		 	this.reload();
		},
		_hide: function($target){
			if("hidden" === this.options._closeMode){
				$target.hide();
			}else if("visibility" === this.options._closeMode){
				$target.addClass("om-helper-hidden-accessible");
			}
		},
		_show: function($target){
			if("hidden" === this.options._closeMode){
				$target.show();
			}else if("visibility" === this.options._closeMode){
				$target.removeClass("om-helper-hidden-accessible");
			}
		},
		_bindEvent: function(){
			var self = this,
				$body = this.element,
				options = this.options,
				header = $body.prev();
			if(options.collapsible !== false){
				header.click(function(event){
					if($(event.target).is(".om-panel-icon,.om-panel-title,.om-panel-header")){
						options.collapsed !== false? self.expand() : self.collapse();
					}
				}).find(".om-panel-tool-collapse , .om-panel-tool-expand")
				.click(function(){
					options.collapsed !== false? self.expand() : self.collapse();
				});
			}
			if(options.closable !== false){
				header.find(".om-panel-tool-close")
					.click(function(e){
						self.close();
					});				
			}
		},
		_renderHeader: function(){
			this.header && this.header.remove();
			if(this.options.header === false){
				return ;
			}
			var that = this,
				options = this.options,
				tools = options.tools,
				$header = this.header = $("<div class='om-panel-header'></div>").insertBefore(this.element);
			if(options._helpMsg){
				$header.parent().addClass('helpMsg');
			}
			if(options.iconCls){
				$("<div class='om-icon om-panel-icon'></div>").addClass(options.iconCls).appendTo($header);
			}
			$("<div class='om-panel-title'></div>").html(options.title).appendTo($header);
			$tool = $("<div class='om-panel-tool'></div>");
			if(options.collapsible !== false){
				$("<div class='om-icon om-panel-tool-collapse'></div>").appendTo($tool);	
			}
			//处理自定义头部右边的工具条
			if($.isArray(tools)){
				for(var i=0,len=tools.length; i<len; i++){
					var tool = tools[i],
						iconCls;
					if(iconCls = this._getInnerToolCls(tool.id)){
						$("<div class='om-icon'></div>").addClass(iconCls)
							.click(	function(event){
								tool.handler.call(this,that,event);
							}).appendTo($tool);
					}else if(typeof tool.iconCls === 'string'){
						$("<div class='om-icon'></div>").addClass(tool.iconCls)
							.click(	function(event){
								tool.handler.call(this,that,event);
							}).appendTo($tool);
					}else if($.isArray(tool.iconCls)){
						//这里必须要用内部匿名函数，因为hover中用到了tool，否则tool的值很可能已经被改掉了
						(function(tool){
							$("<div class='om-icon'></div>").addClass(tool.iconCls[0])
								.click(function(event){
									tool.handler.call(this,that,event);
								})
								.hover(function(){
									if(tool.iconCls[1]){
										$(this).toggleClass(tool.iconCls[1]);
									}
								}).appendTo($tool);
						})(tool);
					}
				}
			}else{
				try{
					$(tools).appendTo($tool);
				}catch(error){
					throw "bad format of jquery selector.";
				}
			}
			
			if(options.closable !== false){
				$("<div class='om-icon om-panel-tool-close'></div>").appendTo($tool);	
			}
			//处理内置工具按钮hover时的样式变换
			$tool.find(">div.om-icon").hover(
				function(){
					var self = this;
					$.each(innerToolCls , function(){
						if($(self).hasClass(this)){
							$(self).toggleClass(this+"-hover");
						}
					});
				}
			);
			$tool.appendTo($header);
		},
		/**
		 * 初始化panel,header,body的宽和高
		 */
	 	_resize: function($panel){
	 		var $body = this.element,
	 			$header = $body.prev(),
	 			$panel = $body.parent(),
	 			options = this.options;
	 		if(options.width == 'fit'){
	 			options.width = '100%';
	 			$panel.width('100%');
	 			$header.css("width" , "");
	 			$body.css("width" , "");
	 		}else if(options.width !== 'auto'){
				$panel.width(options.width);
				$header.outerWidth($panel.width());
				$body.outerWidth($panel.width());
	 		}else{
	 			var style = $body.attr("style");
	 			if(style && style.indexOf("width") !== -1){
	 				$panel.width($body.outerWidth());
	 				$header.outerWidth($body.outerWidth());
	 			}else{
	 				$panel.css("width" , "");
		 			$header.css("width" , "");
		 			$body.css("width" , "");
	 			}
	 		}
	 		if(options.height == 'fit'){
	 			options.height = '100%';
	 			$panel.height('100%');
	 			$body.outerHeight($panel.height()- (this.options.header!==false?$header.outerHeight():0) );	 
	 		}else if(options.height !== 'auto'){
				$panel.height(options.height);
				$body.outerHeight($panel.height()- (this.options.header!==false?$header.outerHeight():0) );	 
	 		}else{
	 			var style = $body.attr("style");
	 			if(style && style.indexOf("height") !== -1){
	 				$panel.height($header.outerHeight() + $body.outerHeight());
	 			}else{
	 				$panel.css("height" , "");
		 			$body.css("height" , "");
	 			}
	 		}
	 	},
	 	_getInnerToolCls: function(id){
	 		return $.inArray(id , innerToolId)!=-1? 'om-panel-tool-'+id : null;
	 	},
		_showLoadingMessage: function(){
			var options = this.options,
				$body = this.element,
				$loadMsg = $body.next(".om-panel-loadingMessage"),
				position = {
					width:$body.innerWidth(), 
					height:$body.innerHeight(),
					left:$body.position().left + parseInt($body.css("border-left-width")),
					top:$body.position().top
				};
			if($loadMsg.length === 0){
				if("default" === options.loadingMessage){
					$("<div class='om-panel-loadingMessage'><div class='valignMiddle'><div class='loadingImg'>数据加载中</div></div></div>")
					.css(position).appendTo($body.parent());
				}else{
					$("<div class='om-panel-loadingMessage'></div>").appendTo($body.parent())
					.html(options.loadingMessage)
					.css(position);
				}
			}else{
				$loadMsg.css(position).show();
			}
		},
		_hideLoadingMessage: function(){
			this.element.parent().find(".om-panel-loadingMessage").hide();
		},
		/**
		 * 设置panel的标题
		 * @name omPanel#setTitle
		 * @function
		 * @param title 新的标题
		 */
		setTitle: function(title){
		 	this.element.prev().find(">.om-panel-title").html(title);
		},
		/**
		 * 设置panel的图标样式
		 * @name omPanel#setIconClass
		 * @function
		 * @param iconCls 新的图标样式
		 * @returns 当前jquery对象
		 */
		setIconClass: function(iconCls){
			var $header = this.element.prev();
			var $icon = $header.find(">.om-panel-icon");
		 	if(iconCls == null && $icon.length!==0){
		 		$icon.remove();
		 	}else{
		 		if($icon.length==0){
		 			$icon = $("<div class='om-icon om-panel-icon'></div>").insertBefore($header.find(">.om-panel-title"));
		 		}
		 		if(this.options.iconCls){
		 			$icon.removeClass(this.options.iconCls);
		 		}
		 		$icon.addClass(iconCls);
		 		this.options.iconCls = iconCls;
		 	}
		},
		/**
		 * 打开组件，使组件可见。
		 * @name omPanel#open
		 * @function
		 */
		open: function(){
			var $body = this.element,
				options = this.options;
			if(options.closed){
				if(options.onBeforeOpen && this._trigger("onBeforeOpen") === false){
					return ;
				}
				this._show($body.parent());
				options.closed = false;
				options.onOpen && this._trigger("onOpen");
			}
		},
		/**
		 * 关闭组件，使组件不可见。
		 * @name omPanel#close
		 * @function
		 */
		close: function(){
			var $body = this.element,
				options = this.options;
			if(!options.closed){
				if(options.onBeforeClose && this._trigger("onBeforeClose") === false){
					return ;
				}
				this._hide($body.parent());
				options.closed = true;
				options.onClose && this._trigger("onClose");
			}
		},
		/**
		 * 重新加载数据,为使该方法有效，创建组件时必须指定url属性或者调用此方法时传入一个合法的url。
		 * @name omPanel#reload
		 * @function
		 * @param url 一个有效的取数地址
		 */
		reload: function(url){
			var options = this.options,
				$body = this.element,
				self = this;
			if($body.data("loading")){
				return ;
			}else{
				$body.data("loading" , true);
			}
		 	url = url || options.url;
		 	if(!url){
		 		$body.data("loading" , false);
		 		return ;
		 	}
		 	options.url = url;
		 	this._showLoadingMessage();
		 	$.ajax(url , {
		 		cache: false,
		 		success: function(data, textStatus, jqXHR){
		 			$body.html(options.preProcess? options.preProcess.call($body[0] , data , textStatus) : data);
		 			$body.data("loading" , false);
		 			self._hideLoadingMessage();
		 			options.onSuccess && self._trigger("onSuccess", null, data, textStatus, jqXHR);
		 		},
		 		error: function(jqXHR, textStatus, errorThrown){
		 			$body.data("loading" , false);
		 			self._hideLoadingMessage();
		 			options.onError && self._trigger("onError", null, jqXHR, textStatus, errorThrown);
		 		}
		 	});
		},
		/**
		 * 改变组件的大小。
		 * @name omPanel#resize
		 * @function
		 * @param position (1)可以为Object,格式如{width:'100px',height:'100px'} <br/>
		 *                 (2)只有一个参数表示width,有两个参数时依次表示width,height
		 */
		resize: function(position){
		 	var options = this.options,
		 		width,
		 		height;
		 	if($.isPlainObject(position)){
		 		width = position.width || null;
		 		height = position.height || null;
		 	}else{
		 		width = arguments[0];
		 		height = arguments[1];
		 	}
		 	options.width = width || options.width;
		 	options.height = height || options.height;
		 	this._resize(this.element.parent());
		},
		/**
		 * 收起组件。
		 * @name omPanel#collapse
		 * @function
		 */
		collapse: function(/**anim , speed**/){
		 	var self = this,
		 		$body = this.element,
				$header = $body.prev(),
				$parent = $body.parent(),
				$loadMessage = $body.next(".om-panel-loadingMessage"),
				options = this.options,
				anim = effects.anim,
				speed = effects.speed;
				if(arguments[0] != undefined){//由于anim为boolean，所以不可以写成 anim = arguments[0] || effects.anim
					anim = arguments[0];//内部使用
				}
				speed = arguments[1] || speed;//内部使用
			if (options.onBeforeCollapse && self._trigger("onBeforeCollapse") === false) {
            	return ;
        	}
        	$parent.stop(true,true);
			if($header.length !== 0){
				var $expandTool = $header.find("> .om-panel-tool > div.om-panel-tool-collapse");
				if($expandTool.length !== 0){
					$expandTool.removeClass("om-panel-tool-collapse").addClass("om-panel-tool-expand");
					if($expandTool.hasClass("om-panel-tool-collapse-hover")){
						$expandTool.toggleClass("om-panel-tool-collapse-hover om-panel-tool-expand-hover");
					}
				}
			}

			$parent.animate({
					height: '-='+$body.outerHeight()
				} , 
				anim? (speed || 'normal') : 0 , 
				function(){
					$body.hide();
					$loadMessage.hide();
					"auto"===options.height && $parent.css("height" , "");//动画执行后parent会自动添加高度值，所以设置为"auto"时要手动去掉此高度
                	options.onCollapse && self._trigger("onCollapse");
				}
			);    
			options.collapsed = true;
		},
		/**
		 * 展开组件。
		 * @name omPanel#expand
		 * @function
		 */
		expand: function(/**anim , speed**/){
			var self = this,
				$body = this.element,
				$header = $body.prev(),
				$parent = $body.parent(),
				$loadMessage = $body.next(".om-panel-loadingMessage"),
				options = this.options,
				anim = effects.anim,
				speed = effects.speed;
				if(arguments[0] != undefined){//由于anim为boolean，所以不可以写成 anim = arguments[0] || effects.anim
					anim = arguments[0];//内部使用
				}
				speed = arguments[1] || speed;//内部使用
			if (options.onBeforeExpand && self._trigger("onBeforeExpand") === false) {
            	return ;
        	}
        	$parent.stop(true,true);
			if($header.length !== 0){
				var $expandTool = $header.find("> .om-panel-tool > div.om-panel-tool-expand");
				if($expandTool.length !== 0){
					$expandTool.removeClass("om-panel-tool-expand").addClass("om-panel-tool-collapse");
					if($expandTool.hasClass("om-panel-tool-expand-hover")){
						$expandTool.toggleClass("om-panel-tool-expand-hover om-panel-tool-collapse-hover");
					}
				}
			}
			//如果parent没有设置高度值，要设置一个，不然动画效果是出不来的
			"auto"===options.height && $parent.height($header.outerHeight());
			$body.show();
			if($body.data("loading")){
				$loadMessage.show();
			}
			$parent.animate({
					height: '+='+$body.outerHeight()
				} , 
				anim? (speed || 'normal') : 0 , 
				function(){
					"auto"===options.height && $parent.css("height" , "");//动画执行后parent会自动添加高度值，所以设置为"auto"时要手动去掉此高度
	                options.onExpand && self._trigger("onExpand");
				}
			);     
			options.collapsed = false;
		},
		/**
		 * 销毁组件
		 * @name omPanel#destroy
		 * @function
		 */
		destroy: function(){
			var $body = this.element;
			$body.parent().after($body).remove();
		}
	});
})(jQuery);/*
 * $Id: om-accordion.js,v 1.69 2012/06/20 08:30:31 chentianzhen Exp $
 * operamasks-ui omAccordion @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 *  om-panel.js
 */
(function( $, undefined ) {
    var panelIdPrefix = 'om-accordion-panel-' + (((1+Math.random())*0x10000)|0).toString(16).substring(1) + '-',
    id = 0;
	/**
     * @name omAccordion
     * @class 抽屉布局组件。以抽屉的形式展现信息，每个抽屉内容可为当前页面内容，也可以使用Ajax装载其他页面的内容，其原理是jQuery的load方法，没用到嵌入的iframe，不支持跨域装载.支持初始化过后再次更新某个抽屉的数据源(调用url方法)，值得注意的是:更新数据源不会触发抽屉的刷新操作，需要显示调用另一个api来完成(调用reload方法)。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>支持Ajax装载</li>
     *      <li>支持自定义每个抽屉图标</li>
     *      <li>支持多种抽屉切换方式</li>
     *      <li>支持动态更换数据源</li>
     *      <li>支持动态更换标题</li>
     *      <li>支持定时自动切换抽屉</li>
     *      <li>支持多种事件捕获</li>
     * </ol>
     * <b>使用方式：</b><br/><br/>
     * 页面上有如下html标记
     * <pre>
     * &lt;script type="text/javascript" >
     * $(document).ready(function() {
     *     $('#make-accordion').omAccordion();
     * });
     * &lt;/script>
     * 
     * &lt;div id="make-accordion"&gt;
     *    &lt;ul&gt;
     *        &lt;li&gt;
     *            &lt;a href="./remote.html" id="accordion1"&gt;Title1&lt;/a&gt;&lt;!--此抽屉的id为accordion1，如果没有显示指定，会自动生成--&gt;
     *        &lt;/li&gt;
     *        &lt;li&gt;
     *             &lt;a href="#accordion2"&gt;Title2&lt;/a&gt;&lt;!--此抽屉的id为accordion2--&gt;
     *         &lt;/li&gt;
     *         &lt;li&gt;
     *             &lt;a href="#accordion3"&gt;Title3&lt;/a&gt;&lt;!--此抽屉的id为accordion3--&gt;
     *         &lt;/li&gt;
     *    &lt;/ul&gt;
     *    &lt;div id="accordion2"&gt;
     *      this is accordion2 content
     *    &lt;/div&gt;
     *    &lt;div id="accordion3"&gt;
     *      this is accordion3 content
     *    &lt;/div&gt;
     * &lt;/div&gt;
	 * </pre>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{width:500, height:300}
     */
$.omWidget( "om.omAccordion", {
	
    options: /**@lends omAccordion#*/{
        /**
         * 抽屉布局首次展现时，默认展开的抽屉的索引，可以为整数,也可以为抽屉的id,获取当前处于激活状态的抽屉id可用getActivated()方法。<br/>
         * 如果创建组件时想所有抽屉都不展开，可以这样创建<br/>
         * $('#make-accordion').omAccordion({collapsible:true,active:-1});<br/>
         * 组件会优先按id进行处理，如果找不到对应抽屉，则以索引处理，在处理索引时，将会用parseInt及isNaN进行处理。<br/>
         * <ul>
         * <li>如果抽屉个数为0，则active=-1，</li>
         * <li>如果抽屉个数大于0，且active小于0(当为-1时并且collapsible!==false，则可以收起所有抽屉)，则active=0，</li>
         * <li>如果抽屉个数大于0，且active大于抽屉的个数，则active=抽屉的个数-1</li>
         * </ul>
         * @default 0
         * @type Number String
         * @example
         * //激活第一个抽屉
         * $('#make-accordion').omAccordion({active: 1});
         * //激活id为'contentId'的抽屉
         * $('#make-accordion').omAccordion({active: 'contentId'});
         * //收起所有的抽屉(这时必须有collapsible!==false)
         * $('#make-accordion').omAccordion({active:-1});
         */
        active:0,
        /**
         * 是否自动循环切换抽屉。跟interval配合使用，interval用来指定切换的时间间隔。
         * @default false
         * @type Boolean
         * @example
         * //自动循环切换抽屉
         * $('#make-accordion').omAccordion({autoPlay: true});
         */
        autoPlay : false,
        /**
         * 是否允许将所有抽屉收起。当该值为true时，点击已经展开的抽屉时该抽屉被收起，结果所有抽屉都处于收起状态。（默认情况下不可以收起该抽屉，任一时刻总有一个抽屉是处于激活状态的）。
         * @default false
         * @type Boolean
         * @example
         * //设置可以收起所有的抽屉
         * $('#make-accordion').omAccordion({collapsible :true});<br/>
         * //接着再执行下边代码就可以收起所有抽屉了
         * $('#make-accordion').omAccordion({active : -1});<br/>
         */
        collapsible : false,
        /**
         * 是否禁用组件。如果禁用，则不可以对抽屉进行任何操作。
         * @type Boolean
         * @default false
         * @example
         * $('#make-accordion').omAccordion({disabled:true});
         */
        disabled : false,
        /**
         * 抽屉布局的高度，可取值为'auto'（每个抽屉的高度分别由抽屉的内容决定），可以取值为'fit'，表示适应父容器的大小（height:100%）。任何其他的值（比如百分比、数字、em单位、px单位的值等等）将被直接赋给height属性。
         * @default 'auto'
         * @type Number,String
         * @example
         * $('#make-accordion').omAccordion({height: '50%'});
         */
        height:'auto',
        /**
         * 头部元素选择器。用来指明组件创建时如何获取各个抽屉的初始信息。
         * @default '> ul:first li'
         * @type String
         * @example
         * $('#make-accordion').omAccordion({header:'>h3'});
         */
        header:"> ul:first li",
        /**
         * 每个抽屉的header前面可以配置一个小图标，iconCls为该小图标的样式。该图标的配置与其他属性不同，不是配置在config对象中，而是作为DOM结构中 &lt;a&gt; 标签的属性而存在。
         * 在上面的demo"简单抽屉"中可以看到完整的示例。
         * @default 无
         * @type String
         * @example
         * //DOM树结构，注意a标签上的iconCls
         * &lt;div id="make-accordion"&gt;
         *  &lt;ul&gt;
         *      &lt;li&gt;
         *          &lt;a iconCls="file-save" href="#accordion-1"&gt;&lt;/a&gt;
         *      &lt;/li&gt;
         *  &lt;/ul&gt;
         *  &lt;div id="accordion-1"&gt;
         *      This is Accordion-1
         *  &lt;/div&gt;
         * &lt;/div&gt;
         */
        iconCls : null,
        /**
         * 当自动循环切换抽屉（将autoPlay设置true）时，两次切换动作之间的时间间隔，单位为毫秒。
         * @default 1000
         * @type Number
         * @example
         * //每隔2s自动切换抽屉
         * $('#make-accordion').omAccordion({autoPlay: true, interval : 2000});
         */
        interval : 1000,
        /**
         * 抽屉切换时是否需要动画效果，若启用动画效果，则使用jQuery的slideUp和slideDown，动画速度为fast， 动画效果不可定制。
         * @default false
         * @type Boolean
         * @example
         * //收起和展开抽屉时使用动画效果
         * $('#make-accordion').omAccordion({switchEffect: true});
         */
        switchEffect : false,
        /**
         * 抽屉切换的方式。取值为下面的2种之一: "click"、"mouseover"。click表示单击切换，mouseover表示鼠标滑过切换。
         * @default "click"
         * @type String
         * @example
         * //鼠标滑过切换抽屉
         * $('#make-accordion').omAccordion({switchMode: 'mouseover'});
         */
        switchMode:"click",
        /**
         * 抽屉布局的宽度，可取值为'auto'（默认情况,由浏览器决定宽度），可以取值为'fit'，表示适应父容器的大小（width:100%）。任何其他的值（比如百分比、数字、em单位、px单位的值等等）将被直接赋给width属性。 
         * @default 'auto'
         * @type Number,String
         * @example
         * $('#make-accordion').omAccordion({width: 500});
         */
        width:'auto',
        /**
         * 激活一个抽屉时执行的方法
         * @event
         * @param index 被激活的抽屉的索引，从0开始计数。
         * @param event jQuery.Event对象。
         * @default emptyFn 
         * @example
         *  $('#make-accordion').omAccordion({
         *      onActivate : function(index, event) {
         *          alert('accordion ' + index + ' has been activated!');
         *      }
         *  });
         */
        onActivate: function(index, event){
        },
        /**
         * 激活一个抽屉之前执行的方法。
         * 如果返回布尔值false,那么对应抽屉将不会激活。
         * @event
         * @param index 被选择的抽屉的索引，从0开始计数。
         * @param event jQuery.Event对象。
         * @default emptyFn 
         * @example
         *  $('#make-accordion').omAccordion({
         *      onBeforeActivate : function(index, event) {
         *          alert('accordion ' + index + ' will be activated!');
         *      }
         *  });
         */
        onBeforeActivate: function(index, event){
        },
        /**
         * 收起一个抽屉前执行的方法。
         * 如果返回布尔值false,那么对应抽屉将不会被收起。
         * @event
         * @param index 被收起的抽屉的索引，从0开始计数。
         * @param event jQuery.Event对象。
         * @default emptyFn 
         * @example
         *  $('#make-accordion').omAccordion({
         *      onBeforeCollapse : function(index, event) {
         *          alert('accordion ' + index + ' will been collapsed!');
         *      }
         *  });
         */
        onBeforeCollapse: function(index, event){
        },
        /**
         * 收起一个抽屉时执行的方法。
         * @event
         * @param index 被收起的抽屉的索引，从0开始计数。
         * @param event jQuery.Event对象。
         * @default emptyFn 
         * @example
         *  $('#make-accordion').omAccordion({
         *      onCollapse : function(index, event) {
         *          alert('accordion ' + index + ' has been collapsed!');
         *      }
         *  });
         */
        onCollapse : function(index, event) {
        }
    },
    /**
     * 激活指定的抽屉。index为整数或者抽屉的id。<br/>
     * 任何其它数据将会用parseInt及isNaN进行处理。
     * (注意，如果组件为禁用状态，执行此方法无任何效果)
     * <ul>
     * <li>如果抽屉个数为0，则不激活任何抽屉</li>
     * <li>如果抽屉个数大于0，且index<0，则激活第一个抽屉(索引为0的那个抽屉)</li>
     * <li>如果抽屉个数大于0，且index>=抽屉的个数，则激活最后一个抽屉</li>
     * </ul>
     * @name omAccordion#activate
     * @function
     * @param index 要激活的抽屉的索引(从0开始)或者抽屉的id
     * @example
     * $('#make-accordion').omAccordion('activate', '1');
     */
    activate: function(index){
    	var options = this.options;
    	clearInterval(options.autoInterId);
        this._activate(index);
        this._setAutoInterId(this);
    },
    /**
     * 禁用整个抽屉组件。
     * @name omAccordion#disable
     * @function
     * @example
     * $('#make-accordion').omAccordion('disable');
     */
    disable : function() {
        var $acc = this.element,
        	options = this.options,
        	$disableDiv;
        if (options.autoPlay) {
            clearInterval(options.autoInterId);
        }
        options.disabled = true;
        
        if( ($disableDiv = $acc.find(">.om-accordion-disable")).length === 0 ){
	        $("<div class='om-accordion-disable'></div>").css({position:"absolute",top:0,left:0})
	        	.width($acc.outerWidth()).height($acc.outerHeight()).appendTo($acc);
        }
        $disableDiv.show();
    },
    /**
     * 使整个抽屉处于可用状态(即非禁用状态)
     * @name omAccordion#enable
     * @function
     * @example
     * $('#make-accordion').omAccordion('enable');
     */
    enable : function() {
        this.options.disabled = false;
        this.element.find(">.om-accordion-disable").hide();
    },
    /**获取当前处于激活状态的抽屉的id,如果抽屉总数为0或者当前没有抽屉处于激活状态，那么返回null<br/>
     * 抽屉的id在创建时可以自行指定，如果没有指定，组件内部会动态产生一个。<br/>
     * //DOM树结构，注意，下面代码创建后的抽屉的id为accordion-1.<br/>
     * <pre>
     * &lt;div id="make-accordion"&gt;
     *  &lt;ul&gt;
     *      &lt;li&gt;
     *          &lt;a iconCls="file-save" href="#accordion-1"&gt;&lt;/a&gt;
     *      &lt;/li&gt;
     *  &lt;/ul&gt;
     *  &lt;div id="accordion-1"&gt;
     *      This is Accordion-1
     *  &lt;/div&gt;
     * &lt;/div&gt;
     * </pre>
     * @name omAccordion#getActivated
     * @function
     * @returns 当前处于激活状态的抽屉的id
     * @example
     * $('#make-accordion').omAccordion('getActivated');
     */
    getActivated: function(){
        var panels= $.data(this.element , "panels");
        for(var i=0, len = panels.length; i < len; i++){
        	if(!panels[i].omPanel("option" , "collapsed")){
        		return panels[i].prop("id");
        	}
        }
        return null;
    },
    /**
     * 获取抽屉的总数。
     * @name omAccordion#getLength
     * @function
     * @return len 抽屉的总数
     * @example
     * var len = $('#make-accordion').omAccordion('getLength');
     * alert('total lenght of accordoins is : ' + len);
     */
    getLength: function(){
        return $.data(this.element, "panels").length;
    },
    /**
     * 重新装载指定抽屉的内容.如果该抽屉的数据源是url，则根据该url去取内容，如果该抽屉的数据源是普通文本，则什么都不会做。
     * @name omAccordion#reload
     * @function
     * @param index 要重新装载内容的抽屉的索引（从0开始计数）或者是抽屉的id
     * @example
     * //重新装载索引为1的抽屉
     * $('#make-accordion').omAccordion('reload', 1);
     */
    reload: function(index) {
        var panels= $.data(this.element , "panels");
        if(this.options.disabled !== false || panels.length === 0){
            return this;
        }
        index = this._correctIndex(index);
        panels[index].omPanel('reload');
    },
    /**
     * 重新计算并动态改变整个布局组件的大小,重新设置了组件的宽和高后要调用此方法才可以生效。
     */
    resize: function() {
        var $acc = this.element,
        	options  = this.options,
            panels = $.data(this.element , "panels"),
            headerHeight = 0;
        this._initWidthOrHeight('width');
        this._initWidthOrHeight('height');
    	$.each(panels , function(index , panel){
    		headerHeight += panel.prev().outerHeight();
    	});
    	$.each(panels , function(index , panel){
    		if(options.height === 'auto'){
    			panel.css('height'  , "");
    		}else{
    			panel.outerHeight($acc.height() - headerHeight);
    		}
    	});
    }, 
    /**
     * 设置指定抽屉的标题，标题内容可以为html文本。
     * @name omAccordion#setTitle
     * @function
     * @param index 要改变标题的抽屉的索引（从0开始计数）或者是抽屉的id
     * @param title 新的标题，内容可以为html
     * @example
     * $('#make-accordion').omAccordion('setTitle',0,'apusic').omAccordion('setTitle',1,'AOM');
     */
    setTitle: function(index , title){
        var panels= $.data(this.element , "panels");
        if(this.options.disabled !== false || panels.length === 0){
            return this;
        }
        index = this._correctIndex(index);
        panels[index].omPanel("setTitle" , title);
    },
    
    /**
     * 重新设置指定抽屉的数据源。注意该方法只会重新设置数据源，而不会主动去装载。<br/>
     * 重新装载需要调用另一个api : $('make-accordion').omAccordion('reload',1);<br/>
     * 如果需要更换内容的抽屉并不是用url去装载的，可以用如下方法更换:<br/>
     * $("#accordionId").find("#accordion-2").html("我们是AOM，一个神奇的团队");<br/>
     * 其中accordion-2为抽屉的id.
     * @name omAccordion#url
     * @function
     * @param index 要重新设置数据源的抽屉的索引（从0开始计数）
     * @example
     * //重新设置第二个抽屉的数据源，然后重新装载该抽屉的内容
     * $('#make-accordion').omAccordion( 'url', 1, './ajax/content2.html' );
     * $('#make-accordion').omAccordion( 'reload',1 );
     */
    url : function(index, url) {
        var panels= $.data(this.element , "panels");
        if (!url || this.options.disabled !== false || panels.length === 0) {
            return ;
        }
        index = this._correctIndex(index);
        panels[index].omPanel('option' , "url" , url);
    },
    _create: function(){
        var $acc = this.element,
            options = this.options;
        $acc.addClass("om-widget om-accordion").css("position","relative");
        this._renderPanels();
        options.active = this._correctIndex(options.active);
        this.resize();
        this._buildEvent();
        if(options.disabled !== false){
        	this.disable();
        }
    },
    /**
     * 修正索引，返回值为-1,0,1,2,...,len-1
     */
    _correctIndex: function(index){
    	var $acc = this.element,
        	panels = $.data(this.element , "panels"),
        	panel = $acc.children().find(">div#"+index),
        	len = panels.length,
        	oldIndex = index;
        index = panel.length ? $acc.find(">.om-panel").index(panel.parent()) : index;
        index = index==-1 ? oldIndex : index;
        //如果id找不到，则认为是索引，进行修正
        var r = parseInt(index);
        r = (isNaN(r) && '0' || r)-0;
        return len==0 || (r === -1 && this.options.collapsible !== false) ? -1: (r<0? 0 : (r>=len?len-1 : r));
    },
    _panelCreateCollapse: function(len , index){
    	var $acc = this.element,
    		options = this.options,
    		panel,
    		num;
    	if(options.active === -1 && options.collapsible === true){
    		return true;
    	}else{
			panel = $acc.find(">div#"+options.active);
			num = $acc.children().index(panel);
			num = (num == -1? options.active : num); 		
    		var r = parseInt(num);
    		r = (isNaN(r) && '0' || r)-0;
    		r = r<0? 0 : (r>=len?len-1 : r);
    		return r !== index;  
    	}
    },
    _renderPanels: function () {
        var $acc = this.element,
        	self = this,
            panels = [],
            options = this.options,
        	$headers = $acc.find(options.header),
        	cfg = [],
        	first;
        $headers.find("a").each(function(n){
            var href  = this.getAttribute('href', 2);
            var hrefBase = href.split( "#" )[ 0 ],
                baseEl;
            if ( hrefBase && ( hrefBase === location.toString().split( "#" )[ 0 ] ||
                    ( baseEl = $( "base" )[ 0 ]) && hrefBase === baseEl.href ) ) {
                href = this.hash;
                this.href = href;
            }
            var $anchor = $(this);
            cfg[n] = {
                    title : $anchor.text(),
                    iconCls: $anchor.attr("iconCls"),
                    onExpand : function(event) {
                    	self._trigger("onActivate",event,n);
                    },
                    tools:[{id:"collapse" , handler: function(panel , event){
                    	clearInterval(options.autoInterId);
	                    self._activate(n , true);
	                    self._setAutoInterId(self);
	                    event.stopPropagation();
                    }}],
                    onCollapse : function(event) {
                    	self._trigger("onCollapse",event,n);
                    },
                    onSuccess : function() {
                    	self.resize();
                    },
                    onError : function() {
                    	self.resize();
                    }
            };
            var target = $('>' + href, $acc);
            var pId = target.prop("id"); 
            //target要么指向一个当前页面的div，要么是一个url
            if (!!(target[0])) {
                if(!pId){
                    target.prop("id" , pId=panelIdPrefix+(id++));
                }
                target.appendTo($acc);
            } else {
                cfg[n].url = $anchor.attr('href');
                $("<div></div>").prop('id', pId=($anchor.prop('id') || panelIdPrefix+(id++)) ).appendTo($acc);
            }
            first = first || pId;
            /**
            if(n === 0){
                var $h = panels[0].prev();
                $h.css('border-top-width' , $h.css('border-bottom-width'));
            }
            **/
        }); 
        first && $acc.find("#"+first).prevAll().remove();
        $headers.remove();
        $.each(cfg , function(index , config){
        	config.collapsed = self._panelCreateCollapse(cfg.length , index);
        });
		$.each($acc.children() , function(index , panel){
			panels.push(self._createPanel(panel , cfg[index]));
			if( index === 0){
				var $h = panels[0].prev();
                $h.css('border-top-width' , $h.css('border-bottom-width'));
			}
		});
        $.data($acc , "panels" , panels);  
    },
    _initWidthOrHeight: function(type){
    	var $acc = this.element,
        	options = this.options,
        	styles = this.element.attr("style"),
        	value = options[type];
        if(value == 'fit'){
            $acc.css(type, '100%');
        }else if(value !== 'auto'){
        	$acc.css(type , value);
        }else if(styles && styles.indexOf(type)!=-1){
        	options[type] = $acc.css(type);
        }else{//'auto'
        	$acc.css(type , '');
        }
    },
    _createPanel: function(target, config){
        return $(target).omPanel(config);
    },
    _buildEvent: function() {
        var options = this.options,
            self = this;
        this.element.children().find('>div.om-panel-header').each(function(n){
            var header = $(this);
            header.unbind();
            if ( options.switchMode == 'mouseover' ) {
                header.bind('mouseenter.omaccordions', function(event){
                	clearInterval(options.autoInterId);
                    var timer = $.data(self.element, 'expandTimer');
                    (typeof timer !=='undefined') && clearTimeout(timer);
                    timer = setTimeout(function(){
                    	self._activate(n , true);
                        self._setAutoInterId(self);
                    },200);
                    $.data(self.element, 'expandTimer', timer);
                });
            } else if ( options.switchMode == 'click' ) {
                header.bind('click.omaccordions', function(event) {
                    clearInterval(options.autoInterId);
                    self._activate(n , true);
                    self._setAutoInterId(self);
                });
            } 
            header.hover(function(){
                $(this).toggleClass("om-panel-header-hover");
            });
        });
        if (options.autoPlay) {
            clearInterval(options.autoInterId);
            self._setAutoInterId(self);
        }
        
    },
    _setAutoInterId: function(self){
    	var options = self.options;
    	if (options.autoPlay) {
    		options.autoInterId = setInterval(function(){
                self._activate('next');
            }, options.interval);
        }
    },
    _setOption: function( key, value ) {
    	$.OMWidget.prototype._setOption.apply( this, arguments );
		var options = this.options;
		switch(key){
			case "active":
				this.activate(value);
				break;
			case "disabled":
				value===false?this.enable():this.disable();
				break;
			case "width":
			    options.width = value;
			    this._initWidthOrHeight("width");
				break;
			case "height":
			    options.height = value;
			    this._initWidthOrHeight("height");
				break;
		}
    },
    _activate: function(index , isSwitchMode){
        var panels = $.data(this.element , "panels"),
        len = panels.length,
        options = this.options,
        self = this,
        isExpand = false,
        expandIndex=-1,
        anim = false,
        speed;
    	if(options.disabled !== false && len === 0){
    		return ;
    	}
    	index = index==='next' ? (options.active + 1) % len : self._correctIndex(index);
        if (options.switchEffect) {
            anim = true;
            speed = 'fast';
        }
        for(var i=0; i<len; i++){
            $(panels[i]).stop(true , true);
        }
        //找出当前展开的panel
        var active = self.getActivated();
        isExpand = !!active;
        if(isExpand){
            expandIndex = self._correctIndex(active);
            if(expandIndex == index){
                if( isSwitchMode === true && options.collapsible !== false && self._trigger("onBeforeCollapse",null,expandIndex)!==false){
                    $(panels[expandIndex]).omPanel('collapse', anim, speed);
                    options.active = -1;
                }else{
                	options.active = expandIndex;
                }
            }else{//当前想要激活的抽屉并不是处于激活状态
                if(index === -1){
                	if(self._trigger("onBeforeCollapse",null,expandIndex)!==false){
	                	//收起抽屉，然后整个组件的抽屉都将处于收起状态
	                    $(panels[expandIndex]).omPanel('collapse', anim, speed);
	                    options.active = -1;
					}else{
						options.active = expandIndex;
					}
                }else if( self._trigger("onBeforeCollapse",null,expandIndex)!==false 
                        && (canAct=self._trigger("onBeforeActivate",null,index)!==false)  ){
                    $(panels[expandIndex]).omPanel('collapse', anim, speed);
                    $(panels[index]).omPanel('expand', anim, speed);
                    options.active = index;
                }else{
                	options.active = expandIndex;
                }
            }
        }else{//当前并没有任何已经激活的抽屉
        	if(index === "-1"){
        		options.active = -1;
        	}else if(self._trigger("onBeforeActivate",null,index)!==false){
        		$(panels[index]).omPanel('expand', anim, speed);
                options.active = index;
        	}
        }
    }
});
})( jQuery );
