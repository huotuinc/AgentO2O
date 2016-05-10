using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Micro.Base.Common;
using Micro.PageBaseClass;
using Micro.AdminConfig.BLL;
using System.Net;
using Micro.Mall.Core.BLL;
using Micro.Common.Core;

namespace Micro.AdminConfig.Web._3rdParty.ajax
{
    public partial class LoginAndLogoutAjax : PageBaseHelper
    {
        /// <summary>
        /// 返回数据集合
        /// </summary>
        public Dictionary<object, object> Data { get; set; }

        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                string act = this.GetFormValue("action", "");
                switch (act.ToLower())
                {
                    case "userlogin":   //登陆
                        UserLogin();
                        break;
                    case "userlogout":  //注销
                        UserLogOut();
                        break;
                }
            }
        }
        /// <summary>
        /// 用户登录
        /// </summary>
        private void UserLogin()
        {
            this.Data = new Dictionary<object, object>();

            string username = this.GetFormValue("username", "");
            string password = this.GetFormValue("password", "");
            string customerName = this.GetFormValue("customerName", "");
            int logingtype = this.GetFormValue("logingtype", 0);
            string verifycode = this.GetFormValue("verifycode", "");
            int flag = 0;
            if (Session["VerifyCode"] != null)
            {
                if (Session["VerifyCode"].ToString() == verifycode.ToLower())
                {
                    string ip = Request.UserHostAddress;

                    if (logingtype == 2)
                    {
                        int customerid = ICustomer.Instance.GetCustomerID(customerName);
                        if (customerid > 0)
                        {
                            int buddyFunc = ICustomerMallConfig.Instance.GetBuddyFunc(customerid);
                            if (buddyFunc == 0)
                            {
                                flag = 5; //未开启小伙伴功能
                            }
                            else
                            {
                                int superBuddyid = MallSuperBuddyBLL.Instance.CheckLogin(username, password, customerid);
                                if (superBuddyid > 0)
                                {
                                    flag = 1;
                                    this.Data["superBuddyid"] = superBuddyid;
                                    this.Data["customerid"] = customerid;
                                    //存入cookie
                                    CookieHelper.SetCookieVal("SB_CustomerID", 240, customerid.ToString());
                                    CookieHelper.SetCookieVal("SB_LoginName", 240, username);
                                    CookieHelper.SetCookieVal("SB_Password", 240, password);
                                    CookieHelper.SetCookieVal("SB_CurrentSuperBuddy", 240, superBuddyid.ToString());
                                    CookieHelper.SetCookieVal("MM_Authority", 240, "");
                                    CookieHelper.SetCookieVal(Enum.GetName(typeof(CookieKeyValue), CookieKeyValue.LoginType), 60 * 24 * 7, logingtype.ToString());

                                    //更新最后一次登陆时间
                                    MallSuperBuddyBLL.Instance.UpdateLastLoginTime(superBuddyid);
                                }
                                else
                                {
                                    flag = 0;
                                }
                            }
                        }
                        else
                            flag = 4;
                    }
                    else if (logingtype == 3)
                    {
                        int customerid = ICustomer.Instance.GetCustomerID(customerName);
                        string md5Pass = EncryptHelper.MD5(password);
                        if (customerid > 0)
                        {
                            string authority = string.Empty;
                            string roleName = string.Empty;
                            int mallManagerId = MallManagerBLL.Instance.Login(username, md5Pass, customerid, out authority, out roleName);

                            if (mallManagerId > 0)
                            {
                                flag = 1;
                                this.Data["customerid"] = customerid;
                                CookieHelper.SetCookieVal("MM_CustomerID", 240, customerid.ToString());
                                CookieHelper.SetCookieVal("MM_Authority", 240, authority);
                                CookieHelper.SetCookieVal("MM_ManagerID", 240, mallManagerId.ToString());
                                CookieHelper.SetCookieVal("MM_RoleName", 240, EncryptHelper.Encrypt(roleName, EncryptHelper.ENCRYPT_KEY));
                                CookieHelper.SetCookieVal("MM_LoginName", 240, EncryptHelper.Encrypt(username, EncryptHelper.ENCRYPT_KEY));
                                CookieHelper.SetCookieVal("MM_Password", 240, md5Pass);
                                CookieHelper.SetCookieVal(Enum.GetName(typeof(CookieKeyValue), CookieKeyValue.LoginType), 60 * 24 * 7, logingtype.ToString());
                            }
                        }
                        else
                        {
                            flag = 4;
                        }
                    }
                    else
                    {
                        flag = IOperation.Instance.Login(username, password, ip, logingtype);
                    }
                }
                else
                    flag = 3;
            }
            else
                flag = 2;
            this.Data["code"] = flag;
            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }
        /// <summary>
        /// 用户注销
        /// </summary>
        private void UserLogOut()
        {
            HttpCookie aCookie;
            string cookieName;
            int limit = Request.Cookies.Count;
            for (int i = 0; i < limit; i++)
            {
                cookieName = Request.Cookies[i].Name;
                aCookie = new HttpCookie(cookieName);
                aCookie.Expires = DateTime.Now.AddYears(-112);
                Response.Cookies.Add(aCookie);
            }
            Response.Write(1);
            Response.End();
        }
    }
}