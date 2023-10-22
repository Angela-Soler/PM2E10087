using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using PM2E10087.Views;
using PM2E10087.Controllers;
using PM2E10087.Models;
using System.IO;

namespace PM2E10087
{
    public partial class App : Application
    {

        static Controllers.DBproc dBProc;

        public static Controllers.DBproc Instancia
        {
            get
            {
                if (dBProc == null)
                {
                    String dbname = "SitiosDB.db3";
                    String dbpath = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
                    String dbfulp = Path.Combine(dbpath, dbname);
                    dBProc = new Controllers.DBproc(dbfulp);

                }
                return dBProc;
            }
        }

        public App()
        {
            InitializeComponent();

            MainPage = new NavigationPage(new MainPage());
        }

        protected override void OnStart()
        {
        }

        protected override void OnSleep()
        {
        }

        protected override void OnResume()
        {
        }
    }
}
