/// <reference path="jquery-1.7.2.min.js" />
/// <reference path="jquery.json-2.3.min.js" />

var customTools = {};
(function ($) {
    $.extend(customTools, {
        /*自定义字段模版*/
        getCustomTemplatehtml: function (len) {
            var template = $("#tmplGoodsCustomField").html();
            template = template.replace(/{nums}/gm, len.toString());
            template = template.replace(/{selected}/gm, "");
            template = template.replace(/{checked}/gm, "");
            template = template.replace(/{value}/gm, "");
            return template;
        },
        /*检查文本框是否为空，并且长度不能大于10个字符*/
        checkCustomInput: function () {
            var success = true;
            $(".customfield .field input[type='text']").each(function () {
                if ($(this).val().trim().length <= 0) {
                    $(this).css("border", "1px solid #F41414");
                    Login.Tip('请输入字段名称！', 'error');
                    $(this).focus();
                    success = false;
                    return success;
                }
                else if ($(this).val().trim().length > 5) {
                    $(this).css("border", "1px solid #F41414");
                    Login.Tip('最多5字符！', 'error');
                    $(this).focus();
                    success = false;
                    return success;
                }
                else
                    $(this).css("border", "1px solid #ccc");
            });
            return success;
        },
        /*添加字段元素*/
        addCustomField: function (obj) {
            var currLen = $(obj).attr("data-code");
            if (parseInt(currLen)<10) {
                var template = customTools.getCustomTemplatehtml(currLen);
                $(obj).before(template);
                $(obj).parent().find(".field_" + +currLen).find(".remove").bind("click", function () {
                    $(this).parent().remove();
                });
                $(obj).attr("data-code", parseInt(currLen) + 1);
                $("#hidCustomFieldCount").val(parseInt(currLen) + 1);
            }
            else {
                alert("亲，不要太多哟！");
            }
        },
        getCustomValue: function () {
            var len = $(".customfield").children(".field").length;
            var result = {
                count: len,
                data: []
            }
            if (len > 0) {
                $(".customfield").children(".field").each(function () {
                    var text = $(this).find("input[type='text']").val();
                    var select = $(this).find("select").val();
                    var cbxVal = 0;
                    if ($(this).find("input[type='checkbox']").attr("checked"))
                        cbxVal = 1;
                    var item = {
                        title: text,
                        type: select,
                        check: cbxVal,
                        text: ""
                    }
                    result.data.push(item);
                });
            }
            return $.toJSON(result);
        },
        init: function () {
            $(".customfield .addfield").attr("data-code", $("#hidCustomFieldCount").val());

            var types = $("#hidCustomFieldSelected").val();
            var arr = types.split("|");
            $(".customfield .field").each(function (i, v) {
                $(this).children("select").val(arr[i]);
                //$(this).val(arr[i]);

                $(this).find(".remove").bind("click", function () {
                    $(this).parent().remove();
                });
            });

            


            /*添加字段*/
            $(".customfield .addfield").click(function () {
                customTools.addCustomField(this);
            });
        }
    });
    customTools.init();

})(jQuery);

