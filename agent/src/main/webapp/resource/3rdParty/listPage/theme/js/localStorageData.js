localStorageData = {
    hname: location.hostname ? location.hostname : 'localStatus',
    isLocalStorage: window.localStorage ? true : false,
    dataDom: null,

    initDom: function () { //初始化userData
        if (!this.dataDom) {
            try {
                this.dataDom = document.createElement('input');//这里使用hidden的input元素
                this.dataDom.type = 'hidden';
                this.dataDom.style.display = "none";
                this.dataDom.addBehavior('#default#userData');//这是userData的语法
                document.body.appendChild(this.dataDom);
                var exDate = new Date();
                exDate = exDate.getDate() + 30;
                this.dataDom.expires = exDate.toUTCString();//设定过期时间
            } catch (ex) {
                return false;
            }
        }
        return true;
    },
    set: function (key, value) {
        if (this.isLocalStorage) {
            window.localStorage.setItem(key, value);
        } else {
            if (this.initDom()) {
                this.dataDom.load(this.hname);
                this.dataDom.setAttribute(key, value);
                this.dataDom.save(this.hname)
            }
        }
    },
    get: function (key) {
        if (this.isLocalStorage) {
            return window.localStorage.getItem(key);
        } else {
            if (this.initDom()) {
                this.dataDom.load(this.hname);
                return this.dataDom.getAttribute(key);
            }
        }
    },
    remove: function (key) {
        if (this.isLocalStorage) {
            localStorage.removeItem(key);
        } else {
            if (this.initDom()) {
                this.dataDom.load(this.hname);
                this.dataDom.removeAttribute(key);
                this.dataDom.save(this.hname)
            }
        }
    },

    /*8进制加密*/
    EncodeEight: function (key) {
        if (key != null) {
            var monyer = new Array(); var i, s;
            for (i = 0; i < key.length; i++)
                monyer += "\\" + key.charCodeAt(i).toString(8);
            return monyer;
        }
        return "";
    },
    /*8进制解密*/
    DecodeEight: function (key) {
        if (key != null) {
            var monyer = new Array(); var i;
            var s = key.split("\\");
            for (i = 1; i < s.length; i++)
                monyer += String.fromCharCode(parseInt(s[i], 8));
            return monyer;
        }
        return "";
    }
    ///*任意进制加密*/
    //EncodeEight: function (key) {
    //    if (key != null) {


    //        var monyer = new Array(); var i, s;
    //        for (i = 0; i < key.length; i++)
    //            monyer += key.value.charCodeAt(i).toString(16) + " ";
    //        return monyer;
    //    }
    //    return "";
    //},
    ///*任意进制解密*/
    //DecodeEight: function (key) {
    //    if (key != null) {


    //        var monyer = new Array(); var i;
    //        var s = key.split(" ");
    //        for (i = 0; i < s.length; i++)
    //            monyer += String.fromCharCode(parseInt(s[i], 16));
    //        return monyer;
    //    }
    //    return "";
    //}
}