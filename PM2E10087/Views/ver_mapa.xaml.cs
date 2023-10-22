using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin.Forms.Maps;
using Xamarin.Essentials;
using Plugin.Media;

namespace PM2E10087.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ver_mapa : ContentPage
    {
        String maplatitud, maplongitud, mapdescripcion;
        public ver_mapa(String latitud, String longitud, String descripcion)
        {
            InitializeComponent();
            maplatitud = latitud;
            maplongitud = longitud;
            mapdescripcion = descripcion;

            
        }

        private async void btnCompartir_Clicked(object sender, EventArgs e)
        {
            try
            {
                await Share.RequestAsync(
                   new ShareTextRequest
                   {
                       Title= "Ubicacion",
                       Text= "Esta es mi ubicación",
                       Uri = "https://maps.google.com/?q="+maplongitud+","+maplatitud
                   }
                    );
            }
            catch
            {

            }
        }

        private void toolmenu_Clicked(object sender, EventArgs e)
        {

        }

        protected override void OnAppearing()
        {
            base.OnAppearing();

                var localizacion = Geolocation.GetLocationAsync();
                if (localizacion == null)
                {
                    DisplayAlert("Advertencia", "Su GPS esta desactivado", "Ok");
                }

                Pin ubicacion = new Pin();
                ubicacion.Label = mapdescripcion.ToString();
                ubicacion.Type = PinType.Place;
                ubicacion.Position = new Position(Double.Parse(maplongitud), Double.Parse(maplatitud));
                mapa.Pins.Add(ubicacion);
                mapa.IsShowingUser = true;
                mapa.MoveToRegion(MapSpan.FromCenterAndRadius(new Position(Double.Parse(maplongitud),
                Double.Parse(maplatitud)), Distance.FromMeters(500.0)));


      


        }





    }
}