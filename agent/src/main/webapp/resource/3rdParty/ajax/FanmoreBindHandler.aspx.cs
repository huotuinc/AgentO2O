using Micro.Base.Common;
using Micro.Common.Core;
using Micro.Common.Core.Model;
using Micro.PageBaseClass;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace Micro.AdminConfig.Web._3rdParty.ajax
{
    public partial class FanmoreBindHandler : IBaseHandler
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            Response.ContentType = "application/json";
            this.Data = new Dictionary<object, object>();
            if (Request.HttpMethod.ToLower() == "post")
            {
                this.OperatePostMethod();
                this.Data["code"] = this.code;
                this.outputJson = PageBase.GetJson(this.Data);
                Response.Write(this.outputJson);
            }
            else
            {
                this.Data["code"] = this.code;
                this.outputJson = PageBase.GetJson(this.Data);
                if (this.CallbackFuncName.StrIsNull())
                    Response.Write(this.outputJson);
                else
                    Response.Write(string.Format("{0}({1})", this.CallbackFuncName, this.outputJson));

            }
        }

        private void OperatePostMethod()
        {
            switch (this.PostAction)
            {
                case "industry": //获取行业
                    GetIndustry();
                    break;
                case "simulatelogin": //模拟操作
                    SimulateLogin();
                    break;
                case "simulateloginplus"://模拟操作新版
                    SimulateLoginPlus();
                    break;
                case "saveuserbaseinfo":
                    SaveUserBaseInfo();
                    break;
                case "initpullicaccountdata": //初始化公众号数据
                    InitPullicAccountData();
                    break;
                case "installsolution":
                    InstallSolution();
                    break;
                case "checkdomainexist":
                    CheckDomainExist();
                    break;
                case "isredirect":
                    IsRedirect();
                    break;
                case "getmydomain":
                    GetMyDomain();
                    break;
                case "getappinfo":
                    GetAppInfo();
                    break;
                case "clearappinfo":
                    ClearAppInfo();
                    break;
            }
        }


        #region 业务逻辑
        /// <summary>
        /// 获取行业
        /// </summary>
        private void GetIndustry()
        {
            DataTable dt = IUserInfo.Instance.GetIndustryList();
            if (!dt.DtIsNull())
            {

                this.code = 1;
                List<object> listIndustry = new List<object>();
                foreach (DataRow row in dt.Rows)
                {
                    Dictionary<object, object> dict = new Dictionary<object, object>();
                    dict["ID"] = row["SI_ID"];
                    dict["Value"] = row["SI_IndustryName"];
                    listIndustry.Add(dict);
                }
                this.Data["opts"] = listIndustry;
            }
        }

        /// <summary>
        /// 模拟登录
        /// </summary>
        private void SimulateLogin()
        {
            string LoginName = this.GetFormValue("loginname", "");
            string LoginPwd = this.GetFormValue("loginpwd", "");
            string DeveloperUrl = Server.UrlDecode(this.GetFormValue("developerurl", ""));
            string DeveloperToken = this.GetFormValue("developertoken", "");
            int DeveloperType = this.GetFormValue("developertype", 0);//默认微信
            string domain = this.GetFormValue("domain", "");
            //账户类型
            int userType = int.Parse(this.GetFormValue("userType", ""));
            //认证类型
            int flagOauth = this.GetFormValue("flagOauth", 0);

            this.Data["UserName"] = LoginName;

            #region 设置动态url和token配置
            if (DeveloperType == 0)
            {
                if (SetWeiXinDeveloperPattern(LoginName, LoginPwd, DeveloperUrl, DeveloperToken, domain))
                {
                    this.code = 1;
                }
                //LogHelper.Write("SimulateLogin:" + this.code);
            }
            #endregion
        }

        /// <summary>
        /// 模拟登录新版
        /// </summary>
        private void SimulateLoginPlus()
        {
            string LoginName = this.GetFormValue("loginname", "");
            string LoginPwd = this.GetFormValue("loginpwd", "");
            string DeveloperUrl = Server.UrlDecode(this.GetFormValue("developerurl", ""));
            string DeveloperToken = this.GetFormValue("developertoken", "");
            int DeveloperType = this.GetFormValue("developertype", 0);//默认微信
            string domain = this.GetFormValue("domain", "");
            //账户类型
            int userType = int.Parse(this.GetFormValue("userType", ""));
            //认证类型
            int flagOauth = this.GetFormValue("flagOauth", 0);

            string imgcode = this.GetFormValue("imgcode", "");

            #region 设置动态url和token配置
            if (DeveloperType == 0)
            {
                MPSimulateOperationResultMDL result = SetWeiXinDeveloperPatternPlus(LoginName, LoginPwd, DeveloperUrl, DeveloperToken, domain, imgcode);

                this.code = result.code;
                this.Data["account"] = result.account;
                this.Data["AppId"] = result.AppId;
                this.Data["AppSecret"] = result.AppSecret;
                this.Data["codeMsg"] = result.codeMsg;
                this.Data["dev"] = result.dev;
                this.Data["devMsg"] = result.devMsg;
                this.Data["openid"] = result.openid;
                this.Data["qrcode"] = result.qrcode;
                this.Data["realname"] = result.realname;
                this.Data["Token"] = result.Token;
                this.Data["URL"] = result.URL;
            }
            #endregion
        }

        /// <summary>
        /// 手动绑定公众号，设置公众基本信息
        /// </summary>
        private void SaveUserBaseInfo()
        {
            UserInfoModel model = new UserInfoModel();
            model.UserID = this.CurrentCustomerID;
            model.AppId = this.GetFormValue("AppId", "").Trim();
            model.AppSecret = this.GetFormValue("AppSecret", "").Trim();
            model.UserMicroToUser = this.GetFormValue("openid", "").Trim();
            model.UserMicroUserName = this.GetFormValue("nickname", "").Trim();
            int appType = this.GetFormValue("appType", 1);
            model.UserDredgeChinaswt = 0;
            string method = Request.HttpMethod.ToLower();
            int _id = method == "post" ? this.GetFormValue("id", 0) : this.GetQueryString("id", 0);
            //账户类型
            int userType = int.Parse(this.GetFormValue("userType", ""));
            //认证类型
            int flagOauth = this.GetFormValue("flagOauth", 0);

            int b = IUserInfo.Instance.SetCustomerPullicAccount(appType, this.CurrentCustomerID, model.UserMicroUserName, model.UserMicroToUser, model.AppId, model.AppSecret, _id, userType, flagOauth);

            if (b > 0)
            {
                IUserInfo.Instance.UpdateUserInfo(model);
                this.code = 1;
                this.Data["AppID"] = model.AppId;
                this.Data["AppSecret"] = model.AppSecret;
                this.Data["OpenID"] = model.UserMicroToUser;
                this.Data["NickName"] = model.UserMicroUserName;
            }
        }

        private void InitPullicAccountData()
        {
            DataTable dt = IUserInfo.Instance.GetCustomerPullicAccount(this.CurrentCustomerID);
            if (!dt.DtIsNull())
            {
                this.code = 1;
                List<object> pullicAccountDatas = new List<object>();
                foreach (DataRow row in dt.Rows)
                {
                    Dictionary<object, object> dict = new Dictionary<object, object>();
                    dict["tousername"] = row["pullicName"];
                    dict["openid"] = row["ToOriginalID"];
                    dict["appid"] = row["appID"];
                    dict["appsecret"] = row["appSecret"];
                    dict["apptype"] = row["appType"];
                    pullicAccountDatas.Add(dict);
                }
                this.Data["items"] = pullicAccountDatas;
            }
        }

        /// <summary>
        /// 安装解决方案
        /// </summary>
        private void InstallSolution()
        {
            SiteTemplateGlobalSettingModel model = ISiteTemplate.Instance.GetSiteTempalteSetting(this.CurrentCustomerID);
            if (model == null) model = new SiteTemplateGlobalSettingModel();
            model.CustomerID = this.CurrentCustomerID;
            model.IndexTemplateID = this.GetFormValue("indextemplateid", 0);
            model.ListTemplateID = this.GetFormValue("listtemplateid", 0);
            model.ViewTemplateID = this.GetFormValue("viewtemplateid", 0);
            model.NavBarTemplateID = this.GetFormValue("navbartemplateid", 0);
            string myDomain = this.GetFormValue("mydomain", "");
            int orginalCustomerId = this.GetFormValue("orginalcustomerid", 0);
            model.SolutionID = orginalCustomerId;
            try
            {

                bool flag = ISiteTemplate.Instance.InstallSolutionTemplate(orginalCustomerId, model);
                if (flag)
                {
                    //IUserInfo.Instance.SetCustomerSuDomain(this.CurrentCustomerID, myDomain);
                    this.code = 1;
                }

            }
            catch
            {
                this.code = 0;
            }
        }

        /// <summary>
        /// 检查域名是否存在
        /// </summary>
        private void CheckDomainExist()
        {
            string myDomain = this.GetFormValue("mydomain", "");
            bool flag = IUserInfo.Instance.CheckDomainIsExist(myDomain, 1);
            this.code = flag == true ? 1 : 0;
        }

        private void IsRedirect()
        {
            //string url = Request.Url.Host;
            //if (url.IndexOf("fanmore.cn") > 0)
            //{
                SiteTemplateGlobalSettingModel st = ISiteTemplate.Instance.GetSiteTempalteSetting(this.CurrentCustomerID);
                if (st != null)
                {
                    if (st.SolutionID > 0)
                        this.code = 1;
                }
            //}
        }

        private void GetMyDomain()
        {
            //string myDomain = IUserInfo.Instance.GetMyDomain(this.CurrentCustomerID);
            string myDomain = IUserInfo.Instance.GetMsiteUrl(this.CurrentCustomerID) ;
            if (!string.IsNullOrEmpty(myDomain))
            {
                this.code = 1;
                this.Data["domain"] = myDomain;
            }
        }




        private void GetAppInfo()
        {
            string method = Request.HttpMethod.ToLower();
            int _customerid = method == "post" ? this.GetFormValue("customerid", 0) : this.GetQueryString("customerid", 0);
            int _appType = method == "post" ? this.GetFormValue("t", 0) : this.GetQueryString("t", 0);
            int _id = method == "post" ? this.GetFormValue("id", 0) : this.GetQueryString("id", 0);

            DataTable dt = IUserInfo.Instance.GetWebAppInfo(_customerid, _appType, _id);
            if (dt != null && dt.Rows.Count > 0)
            {
                this.code = 1;
                List<object> list = new List<object>();
                foreach (DataRow row in dt.Rows)
                {
                    Dictionary<object, object> dict = new Dictionary<object, object>();
                    dict["id"] = row["id"].ToString();
                    dict["apptype"] = row["appType"].ToString();
                    dict["customerid"] = row["customerid"].ToString();
                    dict["pullicname"] = row["pullicName"].ToString();
                    dict["openid"] = row["ToOriginalID"].ToString();
                    dict["appid"] = row["appID"].ToString();
                    dict["appsecret"] = row["appSecret"].ToString();
                    dict["userType"] = row["userType"] == null ? "0" : row["userType"].ToString();
                    dict["flgOAuth"] = row["flgOAuth"] == null ? "0" : row["flgOAuth"].ToString();

                    list.Add(dict);
                }
                this.Data["optLenght"] = list.Count();
                this.Data["opts"] = list;

            }

        }

        /// <summary>
        /// 清除app公众号绑定数据
        /// </summary>
        private void ClearAppInfo()
        {
            string method = Request.HttpMethod.ToLower();
            int _customerid = method == "post" ? this.GetFormValue("customerid", 0) : this.GetQueryString("customerid", 0);
            int _appType = method == "post" ? this.GetFormValue("t", 0) : this.GetQueryString("t", 0);
            int _id = method == "post" ? this.GetFormValue("id", 0) : this.GetQueryString("id", 0);
            this.code = IUserInfo.Instance.ClearWebAppInfo(_customerid, _appType, _id);
        }


        #endregion






        #region 模拟登录操作
        /// <summary>
        /// 设置微信开发者模式
        /// </summary>
        /// <param name="LoginName">用户名</param>
        /// <param name="LoginPwd">用户密码</param>
        /// <param name="DeveloperUrl">开发者URL</param>
        /// <param name="DeveloperToken">开发者Token</param>
        [Obsolete]
        private bool SetWeiXinDeveloperPattern(string LoginName, string LoginPwd, string DeveloperUrl, string DeveloperToken, string domain)
        {
            try
            {
                #region 微信模拟操作
                //微信模拟操作
                object obj = WeiXinSimulateOperation.ExecSimulateOperation(SimulateOperationTypeOptions.设置开发者资料, LoginName, LoginPwd, DeveloperUrl, DeveloperToken, domain);

                if (obj != null)
                {
                    return SetDeveloperPatternBase(DeveloperUrl, DeveloperToken, obj);
                }
                #endregion 微信模拟操作
                return false;
            }
            catch (Exception ex)
            {
                LogHelper.Write("SetWeiXinDeveloperPattern:" + ex.Message);
                return false;
            }
        }

        /// <summary>
        /// 微信模拟登录操作
        /// </summary>
        /// <param name="LoginName"></param>
        /// <param name="LoginPwd"></param>
        /// <param name="DeveloperUrl"></param>
        /// <param name="DeveloperToken"></param>
        /// <param name="domain"></param>
        /// <param name="imgcode"></param>
        /// <returns></returns>
        private MPSimulateOperationResultMDL SetWeiXinDeveloperPatternPlus(string LoginName, string LoginPwd, string DeveloperUrl, string DeveloperToken, string domain, string imgcode)
        {
            try
            {
                WeiXinSimulateOperationPlus wxso = new WeiXinSimulateOperationPlus();
                MPSimulateOperationResultMDL result = wxso.ExecSimulateOperation(SimulateOperationTypeOptions.设置开发者资料,
                    LoginName, LoginPwd, DeveloperUrl,
                    DeveloperToken, domain, imgcode);
                if (result.code == (int)MPSimulateRetCodeOptions.成功)
                {
                    this.SetDeveloperPatternBasePlus(DeveloperUrl, DeveloperToken, result);
                }
                return result;
            }
            catch (Exception ex)
            {
                LogHelper.Write("SetWeiXinDeveloperPatternPlus:" + ex.Message);
                return new MPSimulateOperationResultMDL()
                {
                    code = (int)MPSimulateRetCodeOptions.异常,
                    codeMsg = "SetWeiXinDeveloperPatternPlus异常：" + ex.Message
                };
            }
        }

        /// <summary>
        /// 设置开发者基本信息(开发者模式开启后更新用户基本信息)新版
        /// </summary>
        /// <param name="DeveloperUrl"></param>
        /// <param name="DeveloperToken"></param>
        /// <param name="obj"></param>
        /// <returns></returns>
        private void SetDeveloperPatternBasePlus(string DeveloperUrl, string DeveloperToken, MPSimulateOperationResultMDL result)
        {
            #region 修改客户信息
            UserInfoModel model = new UserInfoModel();
            model.UserID = this.CurrentCustomerID;
            model.AppId = result.AppId;
            model.AppSecret = result.AppSecret;
            model.UserMicroToUser = result.openid;
            model.UserMicroUserName = result.realname;

            model.UserDredgeChinaswt = 0;
            model.UserIsServiceNum = result.dev;
            model.UserDeveloperUrl = DeveloperUrl;
            model.UserdeveloperToken = DeveloperToken;
            #endregion

            int b = IUserInfo.Instance.SetCustomerPullicAccount(1, this.CurrentCustomerID, model.UserMicroUserName, model.UserMicroToUser, model.AppId, model.AppSecret, 0, model.UserIsServiceNum, 1);
            if (b > 0)
            {
                this.Data["id"] = b;
                IUserInfo.Instance.UpdateUserInfo(model);
                ICustomerCommonConfig.Intance.UpdateQrcodeConfig(this.CurrentCustomerID, result.qrcode);
            }
        }

        /// <summary>
        /// 设置开发者基本信息(开发者模式开启后更新用户基本信息)
        /// </summary>
        /// <param name="DeveloperUrl">开发者url</param>
        /// <param name="DeveloperToken">开发者token</param>
        /// <param name="obj">设置开发模式时返回的数据</param>
        [Obsolete]
        private bool SetDeveloperPatternBase(string DeveloperUrl, string DeveloperToken, object obj)
        {
            JContainer jc = (JContainer)JsonConvert.DeserializeObject(obj.ToString());
            int _code = Convert.ToInt32(jc["code"].ToString());
            if (_code > 0)
            {
                #region 修改客户信息
                UserInfoModel model = new UserInfoModel();
                model.UserID = this.CurrentCustomerID;
                model.AppId = jc["AppId"].ToString();
                model.AppSecret = jc["AppSecret"].ToString();
                model.UserMicroToUser = jc["openid"].ToString();
                model.UserMicroUserName = jc["realname"].ToString();


                this.Data["AppID"] = model.AppId;
                this.Data["AppSecret"] = model.AppSecret;
                this.Data["OpenID"] = model.UserMicroToUser;
                this.Data["NickName"] = model.UserMicroUserName;



                model.UserDredgeChinaswt = 0;
                model.UserIsServiceNum = Convert.ToInt32(jc["dev"].ToString());
                model.UserDeveloperUrl = DeveloperUrl;
                model.UserdeveloperToken = DeveloperToken;
                #endregion

                int b = IUserInfo.Instance.SetCustomerPullicAccount(1, this.CurrentCustomerID, model.UserMicroUserName, model.UserMicroToUser, model.AppId, model.AppSecret, 0, model.UserIsServiceNum, 1);
                if (b > 0)
                {
                    this.Data["id"] = b;
                    IUserInfo.Instance.UpdateUserInfo(model);
                    #region 设置客户二维码
                    ICustomerCommonConfig.Intance.UpdateQrcodeConfig(this.CurrentCustomerID, jc["qrcode"].ToString());
                    #endregion

                }

                return true;
            }
            return false;
        }
        #endregion

    }

























    public class IBaseHandler : PageBaseHelper
    {
        #region 初始变量
        private string _json = "";
        /// <summary>
        /// 
        /// </summary>
        public string outputJson { get { return _json; } set { _json = value; } }
        /// <summary>
        /// 返回数据集合
        /// </summary>
        public Dictionary<object, object> Data { get; set; }
        /// <summary>
        /// post操作类型 已经全部转换小写，前端不分大小写
        /// </summary>
        public string PostAction { get { return this.GetFormValue("action", "").ToLower(); } }
        /// <summary>
        /// get操作类型 已经全部转换小写，前端不分大小写
        /// </summary>
        public string GetAction { get { return this.GetQueryString("action", "").ToLower(); } }
        /// <summary>
        /// 当前用户ID
        /// </summary>
        public int CurrentCustomerID { get { return this.GetFormValue("customerid", 0); } }

        /// <summary>
        /// 当前用户所属父级ID
        /// </summary>
        public int CurrentParentID { get { return Convert.ToInt32(ConfigHelper.GetConfigString("defaultParentID", "3")); } }


        private int _code = 0;
        /// <summary>
        /// 
        /// </summary>
        public int code { get { return _code; } set { _code = value; } }

        /// <summary>
        /// JSONP 回调函数名
        /// </summary>
        public string CallbackFuncName
        {
            get
            {
                return GetQueryString("jsonpcallback", "");
            }
        }




        #endregion
    }
}