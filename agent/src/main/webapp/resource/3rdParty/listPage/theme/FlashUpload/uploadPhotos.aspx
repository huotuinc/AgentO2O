<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="uploadPhotos.aspx.cs" Inherits="Micro.AdminConfig.Web._3rdParty.FlashUpload.uploadPhotos" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>多图片上传</title>
    <script type="text/javascript" src="../js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="../js/jquery.utils.js"></script>
    <link href="../jBox/Skins/Green/jbox.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="../jBox/jquery.jBox-2.3.min.js"></script>
    <script src="tangram.js"></script>
    <script src="callbacks.js"></script>
    <style type="text/css">
        .panel {
            position: absolute;
            width: 710px;
            background: #fff;
            float: left;
            margin: 5px;
        }

        #flashUploadContainer {
            width: 710px;
            height: 290px;
        }

        #upload {
            width: 100px;
            height: 30px;
            float: left;
            margin: 3px 6px 0 0;
            cursor: pointer;
        }

        #uploadTip {
            width: 350px;
            height: 30px;
            margin: 3px 6px 0 0;
            color: #f00;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="panel">
        <div id="flashUploadContainer"></div>
        <div id="uploadTip" style="float: left;">提示：每次可上传<%=this.uploadFileMaxNum%>张照片，每张最大不能超过<%=this.uploadFileMaxSize%>M！</div>
        <div style="float: right;">
            <div id="upload" style="display: none;">
                <img src="upload.png" alt="" />
            </div>
        </div>
    </div>
    <script type="text/javascript">
        /*-=-=-=-=-=-=-=全局变量模块-=-=-=-=-=-=-*/
        var imageUrls = [], //从服务器返回的上传成功图片数组
            imageCount = 0; //预览框中的图片数量，初始为0
        var postConfig = [
            { "id": 0, "data": { "dir": "1", "custom": "value" } }
        ];

        var imageUploadStatus="NONE";

        //flash初始化参数
        var flashOptions = {
            createOptions: {
                id: 'flashUpload', //flash容器id
                url: 'imageUploader.swf', //这个就是附件里面的FLASH
                width: '710', //容器宽度
                height: '290', //容器高度
                errorMessage: 'Flash插件初始化错误，请更新您的flashplayer版本！',
                wmode: 'window',
                ver: '10.0.0',
                // 初始化的参数就是这些，
                vars: {
                    width: 710, //width是flash的宽
                    height: 290, //height是flash的高
                    gridWidth: 115, // gridWidth是每一个预览图片所占的宽度，应该为width的整除
                    gridHeight: 120, // gridHeight是每一个预览图片所占的高度，应该为height的整除
                    picWidth: 100, // 单张预览图片的宽度
                    picHeight: 100, // 单张预览图片的高度
                    uploadDataFieldName: 'picdata', // POST请求中图片数据的key
                    picDescFieldName: 'pictitle', // POST请求中图片描述的key
                    maxSize: <%=this.uploadFileMaxSize%>, // 文件的最大体积,单位M
                    compressSize: 1, // 上传前如果图片体积超过该值，会先压缩,单位M
                    maxNum: <%=this.uploadFileMaxNum%>, // 最大上传多少个文件
                    backgroundUrl: '', //背景图片,留空默认
                    listBackgroundUrl: '', //预览图背景，留空默认
                    buttonUrl: '', //按钮背景，留空默认
                    compressSide: 1, //等比压缩的基准，0为按照最长边，1为按照宽度，2为按照高度
                    compressLength: 700, // 能接受的最大边长，超过该值Flash会自动等比压缩
                    url: '../../UploadFileEidt.aspx?imageuptype=1&userid=<%=this.currentCustomerID%>&p=<%=this.uploadFileName%>', // 上传处理页面的url地址
                    ext: '{"param1":"参数值1", "param2":"参数值2"}', //可向服务器提交的自定义参数列表
                    fileType: '{"description":"图片", "extension":"*.gif;*.jpeg;*.png;*.jpg"}' //上传文件格式限制

                },
                container: 'flashUploadContainer' //flash容器id，对应页面上面的DIV
            },
            selectFileCallback:'selectFileCallback', // 选择文件的回调selectFileCallback
            exceedFileCallback: 'exceedFileCallback', // 文件超出限制的最大体积时的回调
            deleteFileCallback: 'deleteFileCallback', // 删除文件的回调
            startUploadCallback: 'startUploadCallback', // 开始上传某个文件时的回调
            uploadCompleteCallback: 'uploadCompleteCallback', // 某个文件上传完成的回调
            uploadErrorCallback: function (data) {
                if (!data.info) {
                    $.jBox.tip("上传失败："+data.info,"error");
                }
            }, // 某个文件上传失败的回调

            allCompleteCallback: function(){
                imageUploadStatus="SUCCESS";
                $.jBox.tip("上传完成!","success");
            } // 全部上传完成时的回调
            //changeFlashHeight: 'changeFlashHeight'     // 改变Flash的高度，mode==1的时候才有用    

        }
        var flashObj = new baidu.flash.imageUploader(flashOptions);

        function submitMenu() {
            if(imageUploadStatus=="SUCCESS"){
                var _json = {
                    code: 1,
                    urls: "[" + imageUrls.join(",") + "]"
                };
                return _json;
            }
            else if(imageUploadStatus=="READY"){
                $.jBox.tip("您选择的图片还没有上传完成！","error");
                return -1;
            }
            return false;
        }
    </script>
</body>
</html>
