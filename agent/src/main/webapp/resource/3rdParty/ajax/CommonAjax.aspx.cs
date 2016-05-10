using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Micro.Base.Common;
using Micro.PageBaseClass;
using Micro.AdminConfig.Model;
using Micro.AdminConfig.BLL;
using System.IO;
using System.Net;
using System.Text;
using System.Data;
using Micro.Common.Core;
using System.Xml;
using Micro.Common.Core.Model;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Text.RegularExpressions;
using Micro.Common.Core.Service.DataCenterServce;
using System.Configuration;
using Micro.Mall.Core.BLL;


namespace Micro.AdminConfig.Web._3rdParty.ajax
{
    public partial class CommonAjax : PageBase
    {
        /// <summary>
        /// 返回数据集合
        /// </summary>
        public Dictionary<object, object> Data { get; set; }
        private string managerSiteAddr = ConfigurationManager.AppSettings["ManageSiteAddress"] == null ? "http://manager.chinaswt.cn" : ConfigurationManager.AppSettings["ManageSiteAddress"];

        //微信自定义菜单接口
        private const string WEIXIN_CUSTOM_MENU_URL_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token={0}";
        private const string WEIXIN_CUSTOM_MENU_URL_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token={0}";
        private const string WEIXIN_CUSTOM_MENU_URL_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token={0}";

        //易信自定义菜单接口
        private const string YIXIN_CUSTOM_MENU_URL_CREATE = "https://api.yixin.im/cgi-bin/menu/create?access_token={0}";
        private const string YIXIN_CUSTOM_MENU_URL_DELETE = "https://api.yixin.im/cgi-bin/menu/delete?access_token={0}";
        private const string YIXIN_CUSTOM_MENU_URL_GET = "https://api.yixin.im/cgi-bin/menu/get?access_token={0}";

        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                string act = this.GetFormValue("action", "");
                switch (act.ToLower())
                {
                    case "skippage":   //skip
                        SkipPage();
                        break;
                    case "createmenu": //创建或删除自定义菜单
                        CreateMenu();
                        break;
                    case "gettreenodes":
                        GetTreeNodes();
                        break;
                    case "removemenu":
                        RemoveMenu();
                        break;
                    case "saveworkmobile":
                        SaveWorkMobile();
                        break;
                    case "editimagetext":
                        EditImageText();
                        break;
                    case "deleteimagetext":
                        DeleteImageText();
                        break;
                    case "savepanoramadata":
                        SavePanoramaData();
                        break;
                    case "votestats":
                        VoteStats();
                        break;
                    case "getmenu":
                        GetMenu();
                        break;
                    case "getproducttype":
                        GetProductType();
                        break;
                    case "saveoption":
                        SaveOptions();
                        break;
                    case "getoptionslist":
                        GetOptionsList();
                        break;
                    case "checkdomainisexist": //检查域名是否存在(重复)
                        CheckDomainIsExist();
                        break;
                    case "savecustomerbaseinfo":
                        SaveCustomerBaseInfo();
                        break;
                    case "savecustomerappsinfo":
                        SaveCustomerAppsInfo();
                        break;
                    case "checkloginnameisexist":
                        CheckLoginNameIsExist();
                        break;
                    case "saveimages":
                        SaveImages();
                        break;

                }
            }
        }

        #region 业务操作1.0
        /// <summary>
        /// 跳转页面地址
        /// </summary>
        private void SkipPage()
        {
            this.Data = new Dictionary<object, object>();
            int eventType = GetFormValue("eventtype", 0);
            int replyType = GetFormValue("replytype", 0);
            string url = string.Empty;
            string eventTypeName = EnumTypeValue.GetObjectEnumValue(typeof(RequestMsgType), eventType).ToString();
            string repleyTypeName = EnumTypeValue.GetObjectEnumValue(typeof(ResponseMsgType), replyType).ToString();
            int flag = 0;
            switch (eventTypeName.ToLower())
            {
                case "text":
                    url = "CustomerManage/KeyWordList.aspx?pageIndex={0}&parentpageIndex={1}&userid={2}&configid={3}";
                    flag = 1;
                    break;
                default:
                    url = "CustomerManage/EditReplyContent.aspx?pageIndex={0}&parentpageIndex={1}&userid={2}&configid={3}&msgtype={4}";
                    flag = 2;
                    break;
            }
            this.Data["code"] = flag;
            this.Data["url"] = url;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        private Dictionary<object, object> GetAccessToken(int userid, string appid, string appsecret, int appType)
        {
            #region
            Dictionary<object, object> dict;
            if (appType == 1)//微信  //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            {
                string errmsg;
                string wxtoken = WxAccessTokenProvider.Intance.GetToken(userid, appid, appsecret, out errmsg);
                dict = new Dictionary<object, object>();
                if (errmsg != "")
                {
                    dict.Add("errcode", "500");
                    dict.Add("errmsg", errmsg);
                }
                else
                {
                    dict.Add("access_token", wxtoken);
                }
            }
            else
            {
                string json = GetGrantTypeRequest(appid, appsecret, appType);
                dict = GetParseJsonData(json);
            }
            return dict;
            #endregion
        }

        /// <summary>
        /// 创建或删除自定义菜单
        /// </summary>
        private void CreateMenu()
        {
            //string appid = GetFormValue("appid", "");
            //string appsecret = GetFormValue("appsecret", "");
            string operationType = GetFormValue("operationtype", "");
            int userid = GetFormValue("userid", 0);
            //获取凭证
            string access_token = "";// CookieHelper.GetCookieVal(appid);
            //bool flag = ICustomer.Instance.IsCredulity(userid);
            int status = 0;
            DataTable dt = Micro.Common.Core.IUserInfo.Instance.GetCustomerPullicAccount(userid);
            foreach (DataRow row in dt.Rows)
            {
                int appType = Convert.ToInt32(row["appType"].ToString());
                string appid = row["appID"].ToString();
                string appsecret = row["appSecret"].ToString();
                if (appid.StrIsNull() || appsecret.StrIsNull())
                    status = -3; //您还没有授权
                else
                {
                    //0923

                    Dictionary<object, object> dict = this.GetAccessToken(userid, appid, appsecret, appType);

                    if (dict != null)
                    {
                        //判断access_token是否存在
                        if (dict.ContainsKey("access_token"))
                        {
                            //获取凭证返回成功
                            access_token = dict["access_token"].ToString();
                            if (!string.IsNullOrEmpty(operationType))
                            {
                                string postData = GetCustomMenuData(userid);
                                if (string.IsNullOrEmpty(postData))
                                    status = 44002;//Response.Write(44002);
                                else
                                {
                                    #region 创建或删除操作
                                    string path = string.Empty;
                                    string _json = string.Empty;
                                    if (appType == 2)  //易信
                                    {
                                        //if (operationType == "1") //等于1 创建菜单    
                                        //    path = ConfigHelper.GetConfigString("yixincreatemenu");
                                        //else
                                        //    path = ConfigHelper.GetConfigString("yixindeletemenu");
                                        
                                        if (operationType == "1") //等于1 创建菜单    
                                            path = YIXIN_CUSTOM_MENU_URL_CREATE;
                                        else
                                            path = YIXIN_CUSTOM_MENU_URL_DELETE;

                                    }
                                    else if (appType == 1) //微信
                                    {
                                        //if (operationType == "1") //等于1 创建菜单  
                                        //    path = ConfigHelper.GetConfigString("createmenu");
                                        //else
                                        //    path = ConfigHelper.GetConfigString("deletemenu");

                                        if (operationType == "1") //等于1 创建菜单  
                                            path = WEIXIN_CUSTOM_MENU_URL_CREATE;
                                        else
                                            path = WEIXIN_CUSTOM_MENU_URL_DELETE;
                                    }
                                    else if (appType == 3) //来往
                                    {
                                    }
                                    if (!string.IsNullOrEmpty(path))
                                    {
                                        string posturl = string.Format(path, access_token);
                                        if (operationType == "1")
                                            _json = CreateMenuRequest(posturl, postData, access_token);
                                        else
                                            _json = MenuRequest(posturl, access_token);
                                        Dictionary<object, object> dictcreate = GetParseJsonData(_json);
                                        int code = Convert.ToInt32(dictcreate["errcode"]);
                                        //Response.Write(code);
                                        status = code;
                                    }
                                    #endregion
                                }
                            }
                            else
                                status = -1;
                        }
                        else if (dict.ContainsKey("errcode"))
                        {
                            //获取凭证返回失败
                            //Response.Write(-1);
                            status = -1;
                        }
                    }
                    else
                    {
                        status = -2;//获取access_token失败
                    }
                }
            }
            Response.Write(status);
            Response.End();
        }

        /// <summary>
        /// 获取菜单，判断是否已发布自定义菜单
        /// </summary>
        private void GetMenu()
        {
            int userid = GetFormValue("userid", 0);

            DataTable dt = Micro.Common.Core.IUserInfo.Instance.GetCustomerPullicAccount(userid);
            if (!dt.DtIsNull())
            {
                foreach (DataRow row in dt.Rows)
                {
                    int appType = Convert.ToInt32(row["appType"].ToString());
                    string appid = row["appID"].ToString();
                    string appsecret = row["appSecret"].ToString();
                    if (appid.StrIsNull() || appsecret.StrIsNull())
                        continue;
                    //获取凭证
                    string access_token = "";
                    //bool flag = ICustomer.Instance.IsCredulity(userid);

                    //string json = GetGrantTypeRequest(appid, appsecret, 1);
                    //Dictionary<object, object> dict = GetParseJsonData(json);
                    Dictionary<object, object> dict = this.GetAccessToken(userid, appid, appsecret, appType);

                    if (dict != null)
                    {
                        //判断access_token是否存在
                        if (dict.ContainsKey("access_token"))
                        {
                            //获取凭证返回成功
                            access_token = dict["access_token"].ToString();
                            string path = string.Empty;
                            //if (appType == 2)
                            //    path = ConfigHelper.GetConfigString("yixingetmenu");
                            //else
                            //    path = ConfigHelper.GetConfigString("getmenu");

                            if (appType == 2)
                                path = YIXIN_CUSTOM_MENU_URL_GET;
                            else
                                path = WEIXIN_CUSTOM_MENU_URL_GET;

                            string geturl = string.Format(path, access_token);
                            string _json = MenuRequest(geturl, access_token);
                            Dictionary<object, object> dictget = GetParseJsonData(_json);
                            if (dictget != null)
                            {
                                if (dictget.ContainsKey("menu"))
                                {
                                    Response.Write(1);//有菜单
                                }
                                else
                                {
                                    Response.Write(0); //没有菜单
                                }
                            }
                            else
                                Response.Write(-2);//获取自定义菜单失败

                        }
                        else if (dict.ContainsKey("errcode"))
                        {
                            //获取凭证返回失败
                            Response.Write(-1);
                        }
                    }
                }
            }

            Response.End();
        }

        /// <summary>
        /// 获取access_token，获取授权,返回json数据
        /// </summary>
        private string GetGrantTypeRequest(string appid, string appsecret, int flag)
        {
            string url = string.Empty;
            if (flag == 2)
                url = string.Format("https://api.yixin.im/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", appid, appsecret);
            else if (flag == 1)
                url = string.Format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", appid, appsecret);
            else if (flag == 3)
            {
                return "";
            }
            IHttpForm _httpForm = new HttpForm(managerSiteAddr, 1500, true, 8);
            HttpFormResponse _response = _httpForm.Get(new HttpFormGetRequest()
            {
                Url = url
            });
            string json = _response.Response.ToString();
            return json;
        }
        /// <summary>
        /// 设置自定义菜单(包含创建和删除)
        /// </summary>
        /// <param name="postData">菜单json数据</param>
        /// <param name="access_token">凭证</param>
        /// <returns></returns>
        private string CreateMenuRequest(string url, string postData, string access_token)
        {
            IHttpForm _httpForm = new HttpForm(managerSiteAddr, 1500, true, 8);
            HttpFormResponse _response = _httpForm.Post(new HttpFormPostRawRequest()
            {
                Url = url,
                Data = postData
            });
            string json = _response.Response.ToString();
            return json;
        }
        /// <summary>
        /// 自定义菜单get请求
        /// </summary>
        /// <param name="url"></param>
        /// <param name="access_token"></param>
        /// <returns></returns>
        private string MenuRequest(string url, string access_token)
        {
            IHttpForm _httpForm = new HttpForm(managerSiteAddr, 1500, true, 8);
            HttpFormResponse _response = _httpForm.Get(new HttpFormGetRequest()
            {
                Url = url
            });
            string json = _response.Response.ToString();
            return json;
        }
        /// <summary>
        /// 获取自定义菜单并封装成微信支持的json格式
        /// </summary>
        /// <param name="UserID"></param>
        /// <returns></returns>
        private string GetCustomMenuData(int UserID)
        {
            DataTable dt = ICustomer.Instance.GetCustomMenu(UserID);
            this.Data = new Dictionary<object, object>();
            List<object> buttons = new List<object>();
            if (!dt.DtIsNull())
            {
                //获取所有根节点
                DataRow[] rootNodes = dt.Select(" SCM_menuparentid=0");
                foreach (DataRow row in rootNodes)
                {
                    Dictionary<object, object> nodes = new Dictionary<object, object>();
                    //获取当前节点下所有子节点
                    DataRow[] childNodes = dt.Select(string.Format(" SCM_menuparentid={0}", Convert.ToInt32(row["SCM_ID"])));
                    if (childNodes != null && childNodes.Count() > 0)
                    {
                        Dictionary<object, object> nodeChild = new Dictionary<object, object>();
                        nodeChild["name"] = row["SCM_menutitle"];
                        List<object> nodeChildrenButtonList = new List<object>();
                        foreach (DataRow r in childNodes)
                        {
                            Dictionary<object, object> nodeChildButtons = new Dictionary<object, object>();
                            nodeChildButtons["type"] = r["SCM_menutype"];
                            nodeChildButtons["name"] = r["SCM_menutitle"];
                            if (r["SCM_menutype"].ToString().ToLower() == "click")
                                nodeChildButtons["key"] = r["SCM_menukey"];
                            else
                                nodeChildButtons["url"] = r["SCM_menukey"];

                            nodeChildrenButtonList.Add(nodeChildButtons);
                        }
                        nodeChild["sub_button"] = nodeChildrenButtonList;

                        buttons.Add(nodeChild);
                    }
                    else
                    {
                        nodes["type"] = row["SCM_menutype"];
                        nodes["name"] = row["SCM_menutitle"];
                        if (row["SCM_menutype"].ToString().ToLower() == "click")
                            nodes["key"] = row["SCM_menukey"];
                        else
                            nodes["url"] = row["SCM_menukey"];
                        buttons.Add(nodes);
                    }
                }
            }


            this.Data["button"] = buttons;
            string json = GetJson(this.Data);

            return json;
        }

        /// <summary>
        /// 获取树节点
        /// </summary>
        private void GetTreeNodes()
        {
            int UserID = GetFormValue("userid", 0);
            DataTable dt = ICustomer.Instance.GetCustomMenu(UserID);
            this.Data = new Dictionary<object, object>();
            Dictionary<object, object> rootNode = new Dictionary<object, object>();
            rootNode["id"] = "0";
            rootNode["pid"] = "0";
            rootNode["expanded"] = true;
            rootNode["classes"] = "folder";
            rootNode["text"] = "自定义菜单列表";
            List<object> nodesList = new List<object>();
            if (!dt.DtIsNull())
            {
                //获取所有根节点
                DataRow[] rootNodes = dt.Select(" SCM_menuparentid=0");
                foreach (DataRow row in rootNodes)
                {
                    Dictionary<object, object> nodes = new Dictionary<object, object>();
                    nodes["id"] = row["SCM_ID"];
                    nodes["pid"] = row["SCM_menuparentid"];
                    nodes["expanded"] = true;
                    nodes["msgtype"] = row["SCM_ReplyType"];
                    nodes["text"] = row["SCM_menutitle"];
                    //获取当前节点下所有子节点
                    DataRow[] childNodes = dt.Select(string.Format(" SCM_menuparentid={0}", Convert.ToInt32(row["SCM_ID"])));
                    List<object> nodeChildrenList = new List<object>();
                    foreach (DataRow r in childNodes)
                    {
                        Dictionary<object, object> nodeChildren = new Dictionary<object, object>();
                        nodeChildren["id"] = r["SCM_ID"];
                        nodeChildren["pid"] = r["SCM_menuparentid"];
                        nodeChildren["text"] = r["SCM_menutitle"];
                        nodeChildren["msgtype"] = r["SCM_ReplyType"];
                        nodeChildrenList.Add(nodeChildren);
                    }
                    nodes["children"] = nodeChildrenList;
                    nodesList.Add(nodes);
                }
            }
            rootNode["children"] = nodesList;

            List<object> rootNodeList = new List<object>();
            rootNodeList.Add(rootNode);

            this.Data["treeNode"] = nodesList;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 删除树节点
        /// </summary>
        private void RemoveMenu()
        {
            int userid = GetFormValue("userid", 0);
            int itemid = this.GetFormValue("itemid", 0);
            bool flag = ICustomer.Instance.DeleteCustomMenuInfo(itemid);
            Response.Write(flag == true ? 1 : 0);
            Response.End();
        }

        /// <summary>
        /// 保存工作人员手机号码(用于接收服务短信)
        /// </summary>
        private void SaveWorkMobile()
        {
            int userid = GetFormValue("userid", 0);
            string mobile = GetFormValue("mobile", "");
            int type = GetFormValue("type", 0);
            bool flag = ISmsService.Instance.UpdateUserMobile(userid, mobile, type);
            string tempStr = "";
            string tempStr2 = "";
            int status = 0;
            if (flag)
                status = 1;
            if (!ISmsService.Instance.ExistSmsService(userid, out tempStr, out tempStr2, type))
                status = 2;
            Response.Write(status);
            Response.End();
        }
        /// <summary>
        /// 获取图文编辑数据
        /// </summary>
        private void EditImageText()
        {
            int edit = GetFormValue("edit", 0);
            DataTable dt = IKeyword.Instance.GetImageTextReplyContentByID(edit);
            this.Data = new Dictionary<object, object>();
            int status = 0;
            if (dt != null && dt.Rows.Count > 0)
            {
                this.Data["title"] = dt.Rows[0]["Title"].ToString();
                this.Data["Description"] = dt.Rows[0]["Description"].ToString();
                this.Data["Url1"] = dt.Rows[0]["Url1"].ToString();
                this.Data["Url2"] = dt.Rows[0]["Url2"].ToString();
                this.Data["IsBody"] = dt.Rows[0]["IsBody"].ToString();
                this.Data["MainBody"] = dt.Rows[0]["MainBody"].ToString();
                this.Data["id"] = edit;


                this.Data["mainFuncId"] = dt.Rows[0]["mainFuncId"];
                this.Data["subFuncId"] = dt.Rows[0]["subFuncId"];
                this.Data["linkData"] = dt.Rows[0]["linkData"];

                status = 1;
            }
            this.Data["errorcode"] = status;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }


        /// <summary>
        /// 删除图文信息
        /// </summary>
        private void DeleteImageText()
        {
            int edit = GetFormValue("edit", 0);
            if (edit > 0)
            {
                int i = IKeyword.Instance.DeleteReplyContent(edit);
                Response.Write(i);
            }
            else
                Response.Write(0);
            Response.End();
        }



        private void SavePanoramaData()
        {
            this.Data = new Dictionary<object, object>();
            int customerid = this.GetFormValue("customerid", 0);
            int id = this.GetFormValue("id", 0);
            string title = Server.UrlDecode(this.GetFormValue("title", ""));
            string zipPath = Server.UrlDecode(this.GetFormValue("zippath", ""));
            string err = "";
            string unZipDir = ConfigHelper.GetConfigString("uploadfile");
            bool isUpadatePath = false;
            if (!zipPath.StrIsNull())
            {
                zipPath = Server.MapPath(zipPath);
                isUpadatePath = true;
            }
            string xmlText = "";
            int code = 0;
            if (id <= 0)
                id = IPanorama.Instance.AddPanoramaInfo(customerid, title, xmlText, "");
            if (id > 0)
            {
                if (isUpadatePath)
                {
                    unZipDir = string.Format("{0}{1}/360/{2}/", unZipDir, customerid, id);
                    string unZipDirPath = Server.MapPath(unZipDir);
                    bool b = IPanorama.Instance.UnZipFile(zipPath, unZipDirPath, out err);
                    if (b)
                    {
                        XmlDocument doc = new XmlDocument();
                        doc.Load(unZipDirPath + "/panorama_content_out.xml");
                        xmlText = doc.InnerXml;
                        xmlText = xmlText.Replace("images", unZipDir + "/iamges/");
                        //CreateXmlFile(xmlpath, unZipDirPath);
                        bool flag = IPanorama.Instance.UpdatePanoramaInfo(customerid, id, title, xmlText, unZipDir);
                        if (flag)
                        {
                            code = 1;
                        }
                        else
                        {
                            IPanorama.Instance.DeletePanoramaInfo(customerid, id);
                        }
                    }
                    else
                    {
                        IPanorama.Instance.DeletePanoramaInfo(customerid, id);
                    }
                }
                else
                {
                    bool flag = IPanorama.Instance.UpdatePanoramaInfo(customerid, id, title);
                    if (flag)
                    {
                        code = 1;
                    }
                }
            }
            this.Data["name"] = IPanorama.Instance.GetPanaoramaName(id);
            this.Data["id"] = id;
            this.Data["code"] = code;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }
        /// <summary>
        /// 创建xml
        /// </summary>
        /// <param name="path"></param>
        /// <param name="outPath"></param>
        private void CreateXmlFile(string path, string outPath)
        {
            if (File.Exists(path))
            {
                using (FileStream s = File.OpenRead(path))
                {
                    using (FileStream streamWriter = File.Create(outPath + "/panorama_content_out.xml"))
                    {
                        int size = 2048;
                        byte[] data = new byte[2048];
                        while (true)
                        {
                            size = s.Read(data, 0, data.Length);
                            if (size > 0)
                            {
                                streamWriter.Write(data, 0, size);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }



        /// <summary>
        /// 获取投票统计
        /// </summary>
        private void VoteStats()
        {
            int id = this.GetFormValue("activityid", 0);
            this.Data = new Dictionary<object, object>();
            VoteModel vote = IVote.Instance.GetVoteStats(id);
            List<object> list = new List<object>();
            int code = 0;
            if (vote != null)
            {
                this.Data["title"] = vote.VT_VoteTitle;
                if (vote.VT_VoteOptions != null && vote.VT_VoteOptions.Count() > 0)
                {
                    foreach (VoteOptionModel op in vote.VT_VoteOptions)
                    {
                        Dictionary<string, string> dict = new Dictionary<string, string>();
                        dict["optionname"] = op.VTO_OptionTitle;
                        dict["nums"] = op.VTO_VoteCount.ToString();
                        list.Add(dict);
                    }
                }
                code = 1;
            }
            this.Data["code"] = code;
            this.Data["options"] = list;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }


        /// <summary>
        /// 递归得到树形结构
        /// </summary>
        /// <param name="parentDep"></param>
        /// <param name="dt"></param>
        private void BuildTreeNode(ProductTypeModel parentDep, List<ProductTypeModel> list)
        {
            List<ProductTypeModel> _list = (from p in list
                                            where p.parentid == parentDep.id
                                            select p).ToList<ProductTypeModel>();
            foreach (ProductTypeModel t in _list)
            {
                parentDep.Children.Add(t);
                BuildTreeNode(t, list);
            }
        }

        private void GetProductType()
        {
            int customerid = this.GetFormValue("customerid", 0);
            List<ProductTypeModel> list = IProduct.Instance.GetProductTypeList(customerid);
            string result = string.Empty;
            List<ProductTypeModel> lstDeps = new List<ProductTypeModel>();
            ProductTypeModel defaultType = new ProductTypeModel();
            defaultType.id = 0;
            defaultType.title = "默认(根级)";
            lstDeps.Add(defaultType);
            if (list != null && list.Count() > 0)
            {
                List<ProductTypeModel> _list = (from p in list
                                                where p.parentid == 0
                                                select p).ToList<ProductTypeModel>();
                foreach (ProductTypeModel t in _list)
                {

                    BuildTreeNode(t, list);
                    lstDeps.Add(t);
                }

            }
            result = JsonConvert.SerializeObject(lstDeps);
            Response.Write(result);
            Response.End();


        }
        #endregion

        #region 选项管理
        private void SaveOptions()
        {
            List<ActSurveyOptionsModel> models = new List<ActSurveyOptionsModel>();
            int questionID = string.IsNullOrEmpty(GetQueryString("questionID", "")) ? 0 : Convert.ToInt32(GetQueryString("questionID", ""));
            this.Data = new Dictionary<object, object>();
            if (questionID != 0)
            {
                string content1 = GetQueryString("content1", "");
                int isRight1 = string.IsNullOrEmpty(GetQueryString("isRight1", "")) ? 0 : Convert.ToInt32(GetQueryString("isRight1", ""));
                string content2 = GetQueryString("content2", "");
                int isRight2 = string.IsNullOrEmpty(GetQueryString("isRight2", "")) ? 0 : Convert.ToInt32(GetQueryString("isRight2", ""));
                ActSurveyOptionsModel model1 = new ActSurveyOptionsModel();
                model1.SurveyQuestionID = questionID;
                model1.SurveyOptionContent = content1;
                model1.ASO_IsRight = isRight1;
                ActSurveyOptionsModel model2 = new ActSurveyOptionsModel();
                model2.SurveyQuestionID = questionID;
                model2.SurveyOptionContent = content2;
                model2.ASO_IsRight = isRight2;

                models.Add(model1);
                models.Add(model2);

                JContainer jc = (JContainer)JsonConvert.DeserializeObject(GetFormValue("options", ""));
                JToken token = jc["options"];
                foreach (JToken tk in token)
                {
                    if (tk["_destroy"] == null ? true : false)
                    {
                        ActSurveyOptionsModel model = new ActSurveyOptionsModel();
                        model.SurveyQuestionID = questionID;
                        model.SurveyOptionContent = tk["content"].ToString();
                        model.ASO_IsRight = tk["isRight"] != null && Convert.ToBoolean(tk["isRight"].ToString()) ? 1 : 0;
                        models.Add(model);
                    }
                }

                int type = IActSurvey.Instance.GetQuestionModel(questionID).QuestionNum;
                int count = models.Count(p => p.ASO_IsRight == 1);

                if (type == 0 && count > 1)
                {
                    this.Data["result"] = "保存失败，此题为单选题";
                }
                else
                {
                    if (IActSurvey.Instance.AddOption(models))
                    {
                        this.Data["result"] = "保存成功";
                    }
                    else
                    {
                        this.Data["result"] = "保存失败";
                    }
                }
            }
            else
            {
                this.Data["result"] = "保存失败";
            }
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }
        private void GetOptionsList()
        {
            this.Data = new Dictionary<object, object>();
            int questionID = string.IsNullOrEmpty(GetQueryString("questionID", "")) ? 0 : Convert.ToInt32(GetQueryString("questionID", ""));
            List<ActSurveyOptionsModel> models = IActSurvey.Instance.GetOptionsWrapList(questionID);
            if (models.Count > 0)
            {
                this.Data["options"] = models;
            }
            else
            {
                this.Data["options"] = "none";
            }

            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }
        #endregion

        #region 2.0
        /// <summary>
        /// 检查域名是否存在(重复)
        /// </summary>
        private void CheckDomainIsExist()
        {
            this.Data = new Dictionary<object, object>();
            string domainName = this.GetFormValue("domainName", "");
            int code = 0;
            code = IUserInfo.Instance.CheckDomainIsExist(domainName.Trim(), 1) == true ? 1 : 0;
            this.Data["code"] = code;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 检查用户名是否存在
        /// </summary>
        private void CheckLoginNameIsExist()
        {
            this.Data = new Dictionary<object, object>();
            string loginname = this.GetFormValue("loginname", "");
            int code = 0;
            code = IUserInfo.Instance.CheckLoginNameIsExist(loginname.Trim()) == true ? 1 : 0;
            this.Data["code"] = code;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }



        private void SaveCustomerBaseInfo()
        {
            this.Data = new Dictionary<object, object>();
            int code = 0;

            int customerid = this.GetFormValue("customerid", 0);
            Micro.AdminConfig.Model.UserInfoModel userinfo = new Micro.AdminConfig.Model.UserInfoModel();
            userinfo.UserLoginName = Server.UrlDecode(this.GetFormValue("loginName", ""));
            userinfo.UserLoginPassword = this.GetFormValue("loginPwd", "");
            userinfo.UserNickName = Server.UrlDecode(this.GetFormValue("realName", ""));
            userinfo.UserIndustryType = this.GetFormValue("industryid", 0);
            userinfo.UserActivate = this.GetFormValue("activate", 1);
            userinfo.UserBelongManagerID = Convert.ToInt32(CookieHelper.GetCookieVal(Enum.GetName(typeof(CookieKeyValue), CookieKeyValue.UserID)));
            userinfo.UserLastLoginIP = "0.0.0.0";
            userinfo.UserID = this.GetFormValue("customerid", 0);
            userinfo.UserRoleID = -2;
            userinfo.UserCityID = this.GetFormValue("cityid", 0);

            userinfo.UserIsSmsService = this.GetFormValue("sms", 0);
            userinfo.UserMoney = this.GetFormValue("money", "0");
            userinfo.UserPrice = this.GetFormValue("price", "1");
            userinfo.SubDomain = this.GetFormValue("myDomain", "");
            string jsoncontent = Server.UrlDecode(this.GetFormValue("jsoncontent", ""));


            int flag = 0;
            if (customerid > 0)
            {
                flag = ICustomer.Instance.UpdateCustomerInfoV2(userinfo);
                ICustomerCommonConfig.Intance.AddDefaultConfig(customerid);
                code = 1;
            }
            else
            {
                userinfo.UserDeveloperUrl = Server.UrlDecode(this.GetFormValue("developerurl", ""));
                userinfo.UserDeveloperToken = this.GetFormValue("developertoken", "");

                userinfo.UserIsOld = this.GetFormValue("version", 1);
                flag = ICustomer.Instance.AddCustomerInfoV2(userinfo);
                if (flag > 0)
                {
                    ICustomerCommonConfig.Intance.AddDefaultConfig(flag);
                    UserBaseInfoService.Instance.AddDefaultBuddy(userinfo.UserLoginName, EncryptHelper.MD5(userinfo.UserLoginPassword), flag);
                    string strReg = "key=(?<key>.*?)&";
                    Match m = Regex.Match(userinfo.UserDeveloperUrl, strReg);
                    if (m.Success)
                    {
                        string key = m.Groups["key"].Value;
                        IUserInfo.Instance.SetDeveloperPatternRecord(key, userinfo.UserDeveloperToken, flag);
                    }

                    code = 1;
                    customerid = flag;
                }
            }

            if (code == 1)
            {
                if (!jsoncontent.StrIsNull())
                {
                    System.Text.StringBuilder strCustomHtml = new System.Text.StringBuilder();
                    JContainer jc = (JContainer)JsonConvert.DeserializeObject(jsoncontent);
                    JToken token = jc["apps"];
                    List<UserInfoMicroAppModel> listModel = new List<UserInfoMicroAppModel>();
                    for (int i = 0; i < token.Count(); i++)
                    {
                        UserInfoMicroAppModel _am = new UserInfoMicroAppModel();
                        _am.ID = Convert.ToInt32(token[i]["id"].ToString());
                        _am.AppID = token[i]["appid"].ToString();
                        _am.AppKey = token[i]["appkey"].ToString();
                        _am.AppSecret = token[i]["appsecret"].ToString();
                        _am.AppTitle = token[i]["apptitle"].ToString();
                        _am.AppType = Convert.ToInt32(token[i]["apptype"].ToString());
                        _am.AppCustomerID = customerid;
                        listModel.Add(_am);
                    }
                    if (listModel != null && listModel.Count() > 0)
                    {
                        IUserInfo.Instance.SetCustomerPullicAccount(listModel);
                    }
                }
            }

            this.Data["code"] = code;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }


        private void SaveCustomerAppsInfo()
        {
            this.Data = new Dictionary<object, object>();
            int code = 0;
            int customerid = this.GetFormValue("customerid", 0);
            if (customerid > 0)
            {
                string jsoncontent = Server.UrlDecode(this.GetFormValue("jsoncontent", ""));
                if (!jsoncontent.StrIsNull())
                {
                    System.Text.StringBuilder strCustomHtml = new System.Text.StringBuilder();
                    JContainer jc = (JContainer)JsonConvert.DeserializeObject(jsoncontent);
                    JToken token = jc["apps"];
                    List<UserInfoMicroAppModel> listModel = new List<UserInfoMicroAppModel>();
                    for (int i = 0; i < token.Count(); i++)
                    {
                        UserInfoMicroAppModel _am = new UserInfoMicroAppModel();
                        _am.ID = Convert.ToInt32(token[i]["id"].ToString());
                        _am.AppSecret = token[i]["appsecret"].ToString();
                        _am.AppID = token[i]["appid"].ToString();
                        _am.AppType = Convert.ToInt32(token[i]["apptype"].ToString());
                        _am.AppCustomerID = customerid;
                        listModel.Add(_am);
                    }
                    if (listModel != null && listModel.Count() > 0)
                    {
                        if (IUserInfo.Instance.SetCustomerPullicAccountAppsInfo(listModel))
                            code = 1;

                    }
                }
            }
            else
                code = 0;
            this.Data["code"] = code;
            string json = GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }





        #endregion



        #region 上传相册相片
        private void SaveImages()
        {
            int albumid = this.GetFormValue("albumid", 0);
            int customerid = this.GetFormValue("customerid", 0);
            string imageUrls = Server.UrlDecode(this.GetFormValue("imageUrls", ""));
            List<PhotosManageModel> list = new List<PhotosManageModel>();
            if (!imageUrls.StrIsNull())
            {
                imageUrls = "{images:" + imageUrls + "}";
                JContainer jc = (JContainer)JsonConvert.DeserializeObject(imageUrls);
                JToken token = jc["images"];
                for (int i = 0; i < token.Count(); i++)
                {
                    PhotosManageModel model = new PhotosManageModel();
                    model.MPM_MPA_ID = albumid;
                    model.MPM_PhotoTitle = token[i]["title"].ToString();
                    model.MPM_PhotoUrl = token[i]["url"].ToString();
                    model.MPM_CustomerID = customerid;
                    list.Add(model);
                }
            }
            int code = 0;
            int flag = IPhotoAlbum.Instance.AddPhoto(list);
            if (flag > 0)
                code = 1;

            Dictionary<object, object> Data = new Dictionary<object, object>();
            Data["code"] = code;
            string json = GetJson(Data);
            Response.Write(json);
            Response.End();
        }
        #endregion
    }
}