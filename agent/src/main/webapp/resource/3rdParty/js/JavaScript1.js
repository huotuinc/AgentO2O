head = document.getElementsByTagName('head').item(0);
CreateLink("../../3rdParty/css/admin.global.css");
CreateLink("../../3rdParty/css/admin.content.css");
CreateLink("../../3rdParty/jBox/Skins/Green/jbox.css");


CreateScript("../../3rdParty/js/jquery-1.4.2.min.js");
CreateScript("../../3rdParty/js/jquery.utils.js");
CreateScript("../../3rdParty/js/admin.js");
CreateScript("../../3rdParty/jBox/jquery.jBox-2.3.min.js");
CreateScript("../../3rdParty/js/jquery.jUploader-1.01.min.js");
CreateScript("../../3rdParty/ueditor/ueditor.config.js");
CreateScript("../../3rdParty/ueditor/ueditor.all.js");

function CreateScript(file) {
    var new_element;
    new_element = document.createElement("script");
    new_element.setAttribute("type", "text/javascript");
    new_element.setAttribute("src", file);
    void (head.appendChild(new_element));
}

function CreateLink(file) {
    var new_element;
    new_element = document.createElement("link");
    new_element.setAttribute("type", "text/css");
    new_element.setAttribute("rel", "stylesheet");
    new_element.setAttribute("href", file);
    void (head.appendChild(new_element));
}