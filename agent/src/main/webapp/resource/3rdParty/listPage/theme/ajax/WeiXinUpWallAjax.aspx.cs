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
using System.Text;
using Micro.Common.Core;
using Micro.Common.Core.Model;
using System.Data;
using System.Collections;

namespace Micro.AdminConfig.Web._3rdParty.ajax
{
    public partial class WeiXinUpWallAjax : PageBaseHelper
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
                    case "getmessage":   //读取信息
                        GetMessage();
                        break;
                    case "getimage":   //读取信息
                        GetImage();
                        break;
                    case "getprizetype":   //读取信息
                        GetPrizeType();
                        break;
                    case "getprizeuser":  //抽奖用户信息
                        GetPrizeUser();
                        break;
                    case "updateprizeuser":
                        UpdatePrizeUser();
                        break;
                    case "getallvote" :
                        GetAllVote();
                        break;
                    case "sendmessage":
                        SentMessage();
                        break;

                }
            }
        }

        /// <summary>
        /// 读取消息
        /// </summary>
        private void GetMessage()
        {
            this.Data = new Dictionary<object, object>();

            string activityID = this.GetFormValue("activityID","");
            string direction = this.GetFormValue("direction", "");
            string ifcheck = this.GetFormValue("ifcheck", "");
            string loadedId = this.GetFormValue("loadedId", "");
            int loadDataCount = this.GetFormValue("loadDataCount",0);

            StringBuilder strWhere = new StringBuilder();
            strWhere.AppendFormat(" mes.ActivityID='{0}' and w_user.BlackUser=0 ", activityID);
            if (int.Parse(ifcheck) > 0)
            {
                strWhere.Append(" and mes.PassCheck=1");
            }

            strWhere.AppendFormat(" and mes.id not in ('{0}')", loadedId);
            
            this.Data["flag"] = 0;
            DataSet ds = IActWallUserMessage.Instance.GetUserAndMessageModel(strWhere.ToString());
            if (ds.Tables[0].Rows.Count > 0)
            {
                
                this.Data["flag"] = 1;
                if (loadDataCount == 1)
                {
                    this.Data["name"] = ds.Tables[0].Rows[0]["UserName"].ToString();
                    this.Data["content"] = ds.Tables[0].Rows[0]["SentContent"].ToString();
                    this.Data["image"] = ds.Tables[0].Rows[0]["Image"].ToString();
                    this.Data["id"] = ds.Tables[0].Rows[0]["id"].ToString();
                }
                else
                {
                    this.Data["data"] = ds.Tables[0];
                }
            }

            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 获取图片
        /// </summary>
        private void GetImage()
        {
            this.Data = new Dictionary<object, object>();
            string activityID = this.GetFormValue("activityID", "");
            DataSet dt = IActWallImage.Intance.GetList(string.Format(" activityId='{0}'",activityID));
            this.Data["flag"] = 0;
            if (dt.Tables[0].Rows.Count > 0)
            {
                this.Data["flag"] = 1;
                ArrayList alImage = new ArrayList();
                for (int i = 0; i < dt.Tables[0].Rows.Count; i++)
                {
                   alImage.Add(dt.Tables[0].Rows[i]["image_path"].ToString());
                }
                this.Data["data"] = alImage;
            }
            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 获取活动的奖品类型
        /// </summary>
        private void GetPrizeType()
        {
            this.Data = new Dictionary<object, object>();
            string activityID = this.GetFormValue("activityID", "");
            DataSet ds=  IActWallPrize.Intance.GetList(string.Format(" activityId='{0}'", activityID));
            this.Data["flag"] = 0;
            if (ds.Tables[0].Rows.Count > 0)
            {
                this.Data["flag"] = 1;
                ArrayList alPrize = new ArrayList();
                for (int i = 0; i < ds.Tables[0].Rows.Count; i++)
                {
                    var obj = new
                    {
                        value = ds.Tables[0].Rows[i]["id"].ToString() ,
                        text = ds.Tables[0].Rows[i]["PrizeType"].ToString()
                    
                    };
                    alPrize.Add(obj);
                }
                this.Data["data"] = alPrize;
            }
            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 获取抽奖人员信息
        /// </summary>
        private void GetPrizeUser()
        {
            this.Data = new Dictionary<object, object>();
            string activityID = this.GetFormValue("activityID", "");
            DataSet ds = IActWallUser.Instance.GetList(string.Format(" activityId='{0}' and BlackUser=0 and PrizeStatus=0 and PassAuthorize=1", activityID));
            this.Data["flag"] = 0;
            this.Data["count"] = 0;
            if (ds.Tables[0].Rows.Count>0)
            {
                this.Data["flag"] = 1;
                this.Data["count"] = ds.Tables[0].Rows.Count;
                ArrayList alPrizeUser = new ArrayList();
                for (int i = 0; i < ds.Tables[0].Rows.Count; i++)
                {
                    var obj = new
                    {
                        id = ds.Tables[0].Rows[i]["id"].ToString(),
                        name = ds.Tables[0].Rows[i]["UserName"].ToString(),
                        image = ds.Tables[0].Rows[i]["Image"].ToString()
                    };
                    alPrizeUser.Add(obj);
                }
                this.Data["data"] = alPrizeUser;
            }
            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 改变抽奖人员状态
        /// </summary>
        private void UpdatePrizeUser()
        {
            try
            {
                this.Data = new Dictionary<object, object>();
                string id = this.GetFormValue("id", "");
                string prizeId = this.GetFormValue("prizeId", "");
                this.Data["flag"] = 0;
                ActWallUser model = IActWallUser.Instance.GetModel(int.Parse(id));
                if (model != null)
                {
                    DateTime dtime= DateTime.Now;
                    model.PrizeStatus = 1;
                    model.PrizeTime =dtime;
                    model.PrizeId = prizeId;
                    Random rd = new Random();
                    model.SN = rd.Next(1,999999).ToString().PadLeft(6,'0');
                    IActWallUser.Instance.Update(model);
                }
                this.Data["flag"] = 1;
                string json = PageBase.GetJson(this.Data);
                Response.Write(json);
                Response.End();
            }
            catch (Exception ex)
            {
                throw ex;
            }

        }

        /// <summary>
        /// 获取活动所以投票类型
        /// </summary>
        private void GetAllVote()
        {    
            this.Data = new Dictionary<object, object>();
            string activityID = this.GetFormValue("activityID", "");
            DataSet ds= IActWallVote.Intance.GetList(string.Format(" activityId='{0}'", activityID));
            this.Data["flag"] = 0;
            if (ds.Tables[0].Rows.Count > 0)
            {
                int sum = 0;
                for (int i = 0; i < ds.Tables[0].Rows.Count; i++)
                {
                    sum += int.Parse(ds.Tables[0].Rows[i]["count"] == null ? "0" : ds.Tables[0].Rows[i]["count"].ToString());
                }
                this.Data["flag"] = 1;
                this.Data["data"] = ds.Tables[0];
                this.Data["sum"] = sum;
            }

            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }

        /// <summary>
        /// 发送消息
        /// </summary>
        private void SentMessage()
        {

            this.Data = new Dictionary<object, object>();
            //发送内容
            string sentContent = GetFormValue("sentContent", "");
            string activityID = GetFormValue("activityID", "");
            string userId = GetFormValue("userId", "");

            //活动进行中
            //保存消息
            ActWallUserMessage model = new ActWallUserMessage();
            model.ActivityID = int.Parse(activityID);
            model.SentContent = sentContent;
            model.SentTime = DateTime.Now;
            model.UserId = userId;
            model.PassCheck = 0;
            IActWallUserMessage.Instance.Add(model);

            this.Data["flag"] = 1;
            string json = PageBase.GetJson(this.Data);
            Response.Write(json);
            Response.End();
        }
    }
}