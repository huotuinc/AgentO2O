/*
**************图片预加载插件******************
参数设置：
scaling     是否等比例自动缩放
width       图片最大高
height      图片最大宽
loadpic     加载中的图片路径
调用方法，在页面引用下面代码
$(function () {
    $("img").LoadImage(false, 20, 20, "/Images/loading.gif");
});

*/
jQuery.fn.LoadImage=function(scaling,width,height,loadpic){
    if (loadpic == null) loadpic = "images/loading.gif";
	return this.each(function(){
		var t=$(this);
		var src=$(this).attr("src")
		var img=new Image();
		img.src=src;
		/**自动缩放图片**/
		var autoScaling=function(){
			if(scaling){
			
				if(img.width>0 && img.height>0){ 
			        if(img.width/img.height>=width/height){ 
			            if(img.width>width){ 
			                t.width(width); 
			                t.height((img.height*width)/img.width); 
			            }else{ 
			                t.width(img.width); 
			                t.height(img.height); 
			            } 
			        } 
			        else{ 
			            if(img.height>height){ 
			                t.height(height); 
			                t.width((img.width*height)/img.height); 
			            }else{ 
			                t.width(img.width); 
			                t.height(img.height); 
			            } 
			        } 
			    } 
			}	
		}
		/**处理ff下会自动读取缓存图片**/
		if(img.complete){
			autoScaling();
		    return;
		}
		$(this).attr("src", "");
		if (width > 0 && height > 0)
		    var loading = $("<img alt=\"加载中...\" title=\"图片加载中...\" src=\"" + loadpic + "\" width=\""+width+"\" height=\""+height+"\"  />");
		else
		    var loading = $("<img alt=\"加载中...\" title=\"图片加载中...\" src=\"" + loadpic + "\" />");
		
		t.hide();
		t.after(loading);
		$(img).load(function(){
			autoScaling();
			loading.remove();
			t.attr("src",this.src);
			t.show();
		});
	});
}