using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Drawing;
using KudyStudio;
using System.IO; // 引用空间

namespace Micro.AdminConfig.Web
{
    public partial class VerifyImage : System.Web.UI.Page
    {
        //protected void Page_Load(object sender, EventArgs e)
        //{
        //    // CaptchaImage image = CaptchaImage.Create(CaptchaText.NumberAndLetter, Color.FromArgb(234, 237, 244),CaptchaOptions.RotateAngle);
        //    //CaptchaImage image = CaptchaImage.Create(CaptchaText.NumberAndLetter, Color.FromArgb(234, 237, 244));
        //    //CaptchaImage image = CaptchaImage.Create(CaptchaText.NumberAndLetter, CaptchaOptions.RotateAngle);
        //    CaptchaImage image = CaptchaImage.Create(CaptchaText.Number, CaptchaOptions.RotateAngle);
        //    // 保存session
        //    Session["VerifyCode"] = image.Text.ToLower();

        //    // 输出图像
        //    Response.OutputStream.Write(image.Data, 0, image.Data.Length);
        //}

        protected void Page_Load(object sender, EventArgs e)
        {
            this.CreateCheckCodeImage(GenerateMathCheckCode());
        }
        public List<object> codes { get; set; }

        /// <summary>
        /// 创建随机生成的验证码，可以为数字或字母
        /// </summary>
        /// <returns></returns>
        private string GenerateCheckCode()
        {
            codes = new List<object>();
            int number = 0;
            char code;
            string checkcode = string.Empty;

            // 随机生成数字，并转换成5个数字或字母
            System.Random random = new Random();
            for (int i = 0; i < 4; i++)
            {
                number = random.Next();
                if (number % 2 == 0)
                    code = (char)('0' + (char)(number % 10)); // 数字
                else
                    code = (char)('A' + (char)(number % 26)); // 字母


                codes.Add(code);
                checkcode += code.ToString();
            }
            Session["VerifyCode"] = checkcode.ToLower();
            return checkcode;
        }
        /// <summary>
        /// 创建随机生成的算术验证码
        /// </summary>
        /// <returns></returns>
        private string GenerateMathCheckCode()
        {
            codes = new List<object>();
            string checkcode = string.Empty;
            // 产生随机生成码
            Random random = new Random();
            int number1 = random.Next(0, 20);
            int number2 = random.Next(0, 20);
            string[] str = { "+", "-", "*"};
            int idx=random.Next(0, 3);
            codes.Add(number1);
            codes.Add(str[idx]);
            codes.Add(number2);
            codes.Add("=");
            codes.Add("?");
            checkcode = string.Format("{0}{2}{1}=?", number1, number2, str[idx]);

            Session["VerifyCode"] = idx == 0 ? (number1 + number2).ToString() : idx == 1 ? (number1 - number2).ToString() : (number1 * number2).ToString();
            return checkcode;
        }

        /// <summary>
        /// 创建随机验证的图片，倾斜字符
        /// </summary>
        /// <param name="checkcode"></param>
        private void CreateCheckCodeImageItalic(string checkcode)
        {
            if (checkcode == null || checkcode.Trim() == string.Empty)
                return;

            Bitmap image = new Bitmap((int)Math.Ceiling((checkcode.Length * 20.8)), 48);
            int w = (int)Math.Ceiling((1 * 15.8));
            int h = 48;
            Bitmap bitmap1 = new Bitmap(w, h);
            Bitmap bitmap2 = new Bitmap(w, h);
            Bitmap bitmap3 = new Bitmap(w, h);
            Bitmap bitmap4 = new Bitmap(w, h);

            Graphics grp = Graphics.FromImage(image);
            Graphics graphics1 = Graphics.FromImage(bitmap1);
            Graphics graphics2 = Graphics.FromImage(bitmap2);
            Graphics graphics3 = Graphics.FromImage(bitmap3);
            Graphics graphics4 = Graphics.FromImage(bitmap4);
            try
            {
                // 产生随机生成码
                Random random = new Random();

                // 清空图片背景色

                graphics1.Clear(Color.Transparent);
                graphics2.Clear(Color.Transparent);
                graphics3.Clear(Color.Transparent);
                graphics4.Clear(Color.Transparent);
                Font font = new Font("宋体", 13, FontStyle.Bold);
                Color[] fontcolor = { Color.Black, Color.Red, Color.DarkBlue, Color.Green, Color.Red, Color.Brown, Color.DarkCyan, Color.Purple };  //定义 8 种颜色
                SolidBrush brush1 = new SolidBrush(fontcolor[random.Next(0, 8)]); //随机颜色
                SolidBrush brush2 = new SolidBrush(fontcolor[random.Next(0, 8)]); //随机颜色
                SolidBrush brush3 = new SolidBrush(fontcolor[random.Next(0, 8)]); //随机颜色
                SolidBrush brush4 = new SolidBrush(fontcolor[random.Next(0, 8)]); //随机颜色

                graphics1.DrawString(codes[0].ToString(), font, brush1, 0, 15);
                graphics2.DrawString(codes[1].ToString(), font, brush2, 0, 15);
                graphics3.DrawString(codes[2].ToString(), font, brush3, 0, 15);
                graphics4.DrawString(codes[3].ToString(), font, brush4, 0, 15);

                graphics1.Dispose();
                graphics2.Dispose();
                graphics3.Dispose();
                graphics4.Dispose();

                //
                int angle1 = random.Next(0, 22);
                int angle2 = random.Next(22, 44);
                int angle3 = random.Next(44, 66);
                int angle4 = random.Next(66, 88);
                bitmap1 = RotateImage(bitmap1, angle1);
                bitmap2 = RotateImage(bitmap2, angle2);
                bitmap3 = RotateImage(bitmap3, angle3);
                bitmap4 = RotateImage(bitmap4, angle4);

                grp.Clear(Color.Transparent);
                grp.DrawImageUnscaled(bitmap1, random.Next(0, 9), 8, w, h);
                grp.DrawImageUnscaled(bitmap2, random.Next(10, 20), 8, w, h);
                grp.DrawImageUnscaled(bitmap3, random.Next(20, 40), 8, w, h);
                grp.DrawImageUnscaled(bitmap4, random.Next(40, 60), 8, w, h);
                MemoryStream ms = new MemoryStream();
                image.Save(ms, System.Drawing.Imaging.ImageFormat.Png);
                Response.ClearContent();
                Response.ContentType = "image/Png";
                Response.BinaryWrite(ms.ToArray());
            }
            catch (System.Exception ex)
            {
                Response.Write(ex.Message);
            }
            finally
            {
                // 清除类对象(Graphics和Bitmap)
                grp.Dispose();
                image.Dispose();
                grp = null;
                image = null;
            }
        }


        private void CreateCheckCodeImage(string checkcode)
        {
            if (checkcode == null || checkcode.Trim() == string.Empty)
                return;

            Bitmap image = new Bitmap((int)Math.Ceiling((checkcode.Length * 12.8)), 48);
            Graphics grp = Graphics.FromImage(image);
            try
            {
                // 产生随机生成码
                Random random = new Random();
                grp.Clear(Color.FromArgb(245, 246, 247));
                // 清空图片背景色
                Font font = new Font("Arial", 12, (FontStyle.Bold | FontStyle.Italic));
                Color[] fontcolor = { Color.Black, Color.Red, Color.DarkBlue, Color.Green, Color.Red, Color.Brown, Color.DarkCyan, Color.Purple };  //定义 8 种颜色

                System.Drawing.Drawing2D.LinearGradientBrush brush = new System.Drawing.Drawing2D.LinearGradientBrush(new Rectangle(0, 0, image.Width, image.Height), fontcolor[random.Next(0, 8)], fontcolor[random.Next(0, 8)], 1.2f, false);

                grp.DrawString(checkcode, font, brush, 2, 15);
                MemoryStream ms = new MemoryStream();
                image.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
                Response.ClearContent();
                Response.ContentType = "image/Jpeg";
                Response.BinaryWrite(ms.ToArray());
            }
            catch (System.Exception ex)
            {
                Response.Write(ex.Message);
            }
            finally
            {
                // 清除类对象(Graphics和Bitmap)
                grp.Dispose();
                image.Dispose();
                grp = null;
                image = null;
            }
        }


        /// <summary>
        /// 已坐标中心点0，0点 旋转
        /// </summary>
        /// <param name="image"></param>
        /// <param name="angle"></param>
        /// <returns></returns>
        public Bitmap RotateImage(System.Drawing.Image image, int angle)
        {
            if (image == null)
                throw new ArgumentNullException("image");
            const double pi2 = Math.PI / 2.0;
            double oldWidth = (double)image.Width;
            double oldHeight = (double)image.Height;
            double theta = ((double)angle) * Math.PI / 180.0;
            double locked_theta = theta;
            while (locked_theta < 0.0)
                locked_theta += 2 * Math.PI;
            double newWidth, newHeight;
            int nWidth, nHeight;

            double adjacentTop, oppositeTop;
            double adjacentBottom, oppositeBottom;
            if ((locked_theta >= 0.0 && locked_theta < pi2) ||
                (locked_theta >= Math.PI && locked_theta < (Math.PI + pi2)))
            {
                adjacentTop = Math.Abs(Math.Cos(locked_theta)) * oldWidth;
                oppositeTop = Math.Abs(Math.Sin(locked_theta)) * oldWidth;
                adjacentBottom = Math.Abs(Math.Cos(locked_theta)) * oldHeight;
                oppositeBottom = Math.Abs(Math.Sin(locked_theta)) * oldHeight;
            }
            else
            {
                adjacentTop = Math.Abs(Math.Sin(locked_theta)) * oldHeight;
                oppositeTop = Math.Abs(Math.Cos(locked_theta)) * oldHeight;
                adjacentBottom = Math.Abs(Math.Sin(locked_theta)) * oldWidth;
                oppositeBottom = Math.Abs(Math.Cos(locked_theta)) * oldWidth;
            }
            newWidth = adjacentTop + oppositeBottom;
            newHeight = adjacentBottom + oppositeTop;
            nWidth = (int)Math.Ceiling(newWidth);
            nHeight = (int)Math.Ceiling(newHeight);
            Bitmap rotatedBmp = new Bitmap(nWidth, nHeight);
            using (Graphics g = Graphics.FromImage(rotatedBmp))
            {
                Point[] points;
                if (locked_theta >= 0.0 && locked_theta < pi2)
                {
                    points = new Point[] { 
                                             new Point( (int) oppositeBottom, 0 ), 
                                             new Point( nWidth, (int) oppositeTop ),
                                             new Point( 0, (int) adjacentBottom )
                                         };
                }
                else if (locked_theta >= pi2 && locked_theta < Math.PI)
                {
                    points = new Point[] { 
                                             new Point( nWidth, (int) oppositeTop ),
                                             new Point( (int) adjacentTop, nHeight ),
                                             new Point( (int) oppositeBottom, 0 ) 
                                         };
                }
                else if (locked_theta >= Math.PI && locked_theta < (Math.PI + pi2))
                {
                    points = new Point[] { 
                                             new Point( (int) adjacentTop, nHeight ), 
                                             new Point( 0, (int) adjacentBottom ),
                                             new Point( nWidth, (int) oppositeTop )
                                             };
                }
                else
                {
                    points = new Point[] { 
                                             new Point( 0, (int) adjacentBottom ), 
                                             new Point( (int) oppositeBottom, 0 ),
                                             new Point( (int) adjacentTop, nHeight )
                                             };
                }
                g.DrawImage(image, points);
            }
            return rotatedBmp;
        }


    }

}