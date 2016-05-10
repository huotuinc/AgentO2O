using Micro.AdminConfig.BLL;
using Micro.Base.Common;
using Micro.Common.Core;
using Micro.Common.Core.Model;
using Micro.PageBaseClass;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace Micro.AdminConfig.Web._3rdParty.ajax
{
    public partial class FunctionMudeleHandler : PageBaseHelper
    {
        protected void Page_Load(object sender, EventArgs e)
        {

            this.Data = new Dictionary<object, object>();
            this.OperateMethod();

            this.Data["code"] = this.code;
            _json = PageBase.GetJson(this.Data);
            Response.ContentType = "application/json";
            Response.Write(_json);
        }



        #region 业务操作入口
        /// <summary>
        /// 操作
        /// </summary>
        private void OperateMethod()
        {
            switch (this.Action)
            {
                case "setcustomerfunctionmudoleconfig":   //给用户设置功能模块
                    SetCustomerFunctionMudoleConfig();
                    break;
                case "deletefunctionmodule":  //删除功能模块，
                    DeleteFunctionModule();
                    break;
                case "editfunctionmodule":  //编辑功能模块
                    EditFunctionModule();
                    break;
                case "setsystemfunctionmudoleconfig":
                    SetSystemFunctionMudoleConfig();
                    break;
                case "isjudgeolduser":  //判断是否是老用户
                    IsJudgeOldUser();
                    break;

            }
        }
        #endregion



        #region 业务处理

        /// <summary>
        /// 设置用户功能模块开启/关闭状态
        /// </summary>
        private void SetCustomerFunctionMudoleConfig()
        {
            //if (this.CurrentCustomerID > 0 && this.CurrentFunctionID > 0)
            //{
            //    CustomerFunctionConfigModel model = new CustomerFunctionConfigModel();
            //    model.FMC_Customerid = this.CurrentCustomerID;
            //    model.FMC_FM_ID = this.CurrentFunctionID;
            //    model.IndustryID = ICustomer.Instance.GetCustomerIndustryType(this.CurrentCustomerID);
            //    int flag = 0;
            //    if (this.CurrentFunctionModuleSwitchStatus)
            //        flag = IFunctionModule.Instance.AddCustomerFunctionConfig(model);
            //    else
            //        flag = IFunctionModule.Instance.DeleteCustomerFunctionConfig(this.CurrentCustomerID, this.CurrentFunctionID) ? 1 : 0;
            //    if (flag > 0)
            //        code = 1;
            //}

            if (this.CurrentCustomerID > 0 && this.CurrentFunctionID > 0)
            {
                CustomerFunctionConfigModel model = new CustomerFunctionConfigModel();
                model.FMC_Customerid = this.CurrentCustomerID;
                model.FMC_FM_ID = this.CurrentFunctionID;
                model.IndustryID = ICustomer.Instance.GetCustomerIndustryType(this.CurrentCustomerID);
                model.FMC_Staus = this.CurrentFunctionModuleSwitchStatus ? 1 : 0;
                
                //检查是否存在商户自定义的功能项
                if (IFunctionModule.Instance.ExsitCustomerFunctionConfig(this.CurrentCustomerID, this.CurrentFunctionID))
                {
                    IFunctionModule.Instance.UpdateCustomerFunctionStatus(this.CurrentCustomerID, this.CurrentFunctionID, model.FMC_Staus);
                }
                else
                {
                    IFunctionModule.Instance.AddCustomerFunctionConfigPlus(model);
                }
                code = 1;
            }
        }

        /// <summary>
        /// 删除功能模块
        /// </summary>
        private void DeleteFunctionModule()
        {
            if (IFunctionModule.Instance.DeleteFunctionModele(this.CurrentFunctionID))
                code = 1;
        }
        /// <summary>
        /// 编辑/添加 功能模块
        /// </summary>
        private void EditFunctionModule()
        {
            FunctionModuleModel fmm = new FunctionModuleModel();
            fmm.FM_Type = this.GetFormValue("functype", (int)FunctionModuleClassifyOptions.行业通用);
            fmm.FM_Title = Server.UrlDecode(this.GetFormValue("functitle", ""));
            fmm.FM_Code = this.GetFormValue("funccode", "");
            fmm.FM_ColorCode = this.GetFormValue("funccolorcode", "2255a4");
            fmm.FM_FromtEndLink = this.GetFormValue("funcfrontendlink", "");
            fmm.FM_FrontEndShowTitle = Server.UrlDecode(this.GetFormValue("funcfrontendshowtitle", ""));
            if (fmm.FM_FrontEndShowTitle.StrIsNull())
            {
                fmm.FM_FrontEndShowTitle = fmm.FM_Title;
            }
            fmm.FM_FunctionName = HttpUtility.UrlDecode(this.GetFormValue("funcfunctionname", ""));
            fmm.FM_IcoUrl = this.GetFormValue("funciconurl", "");
            //fmm.FM_IsMarket = fmm.FM_Type;
            fmm.FM_ShowPlace = this.GetFormValue("funcshowplace", "frontEnd,backEnd");
            fmm.FM_TitleDescription = Server.UrlDecode(this.GetFormValue("funcdescription", ""));
            if (fmm.FM_TitleDescription.StrIsNull())
            {
                fmm.FM_TitleDescription = fmm.FM_Title;
            }
            fmm.FM_Sort = this.GetFormValue("funcsort", 10000);
            int flag = 0;
            if (this.CurrentFunctionID > 0)
            {
                fmm.FM_ID = this.CurrentFunctionID;
                if (IFunctionModule.Instance.UpdateFunctionModule(fmm))
                {
                    code = 1;
                    flag = this.CurrentFunctionID;
                }
            }
            else
            {
                flag = IFunctionModule.Instance.AddFunctionModule(fmm);
                if (flag > 0)
                    code = 1;
            }
            this.Data["funcid"] = flag;
        }
        /// <summary>
        /// 设置系统功能模块开启/关闭状态
        /// </summary>
        private void SetSystemFunctionMudoleConfig()
        {
            if (this.CurrentFunctionID > 0)
            {
                SystemFunctionConfigModel model = new SystemFunctionConfigModel();
                model.FMS_IndustryID = this.GetFormValue("industryid", 0);
                model.FMS_FM_ID = this.CurrentFunctionID;
                int flag = 0;
                if (this.CurrentFunctionModuleSwitchStatus)
                    flag = IFunctionModule.Instance.UpdateSysFunctionConfig(model);
                else
                    flag = IFunctionModule.Instance.DeleteSysFunctionConfig(model.FMS_IndustryID, this.CurrentFunctionID) ? 1 : 0;
                if (flag > 0)
                    code = 1;
            }
        }

        /// <summary>
        /// 判断是否是老用户
        /// </summary>
        private void IsJudgeOldUser()
        {
            if (ICustomer.Instance.GetCurrentCustomerIsOldUser(this.CurrentCustomerID))
                code = 1;
        }

        #endregion





        #region 数据初始设置
        private string _json = "";
        /// <summary>
        /// 返回数据集合
        /// </summary>
        public Dictionary<object, object> Data { get; set; }
        /// <summary>
        /// 操作类型 已经全部转换小写，前端不分大小写
        /// </summary>
        public string Action { get { return this.GetFormValue("action", "").ToLower(); } }
        /// <summary>
        /// 当前客户ID
        /// </summary>
        private int CurrentCustomerID
        {
            get { return this.GetFormValue("customerid", 0); }
        }
        /// <summary>
        /// 当前功能模块ID
        /// </summary>
        private int CurrentFunctionID
        {
            get { return this.GetFormValue("funcid", 0); }
        }
        /// <summary>
        /// 当前功能模块开关状态，开返回true 关返回false
        /// </summary>
        private bool CurrentFunctionModuleSwitchStatus
        {
            get { return this.GetFormValue("checked", 0) > 0; }
        }



        private int _code = 0;
        public int code { get { return _code; } set { _code = value; } }
        #endregion
    }
}