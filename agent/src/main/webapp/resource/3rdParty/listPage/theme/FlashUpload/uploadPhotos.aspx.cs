using Micro.PageBaseClass;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace Micro.AdminConfig.Web._3rdParty.FlashUpload
{
    public partial class uploadPhotos : PageBase
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            this.SetCurrentCustomerID();
        }

        /// <summary>
        /// 文件保存的文件夹名
        /// </summary>
        public string uploadFileName
        {
            get { return this.GetQueryString("path", "other"); }
        }
        /// <summary>
        /// 上传文件最大体积 单位M，默认2M
        /// </summary>
        public string uploadFileMaxSize
        {
            get { return this.GetQueryString("maxsize", "2"); }
        }
        /// <summary>
        /// 上传文件最大数量，默认15个
        /// </summary>
        public string uploadFileMaxNum
        {
            get { return this.GetQueryString("maxnum", "15"); }
        }
    }
}