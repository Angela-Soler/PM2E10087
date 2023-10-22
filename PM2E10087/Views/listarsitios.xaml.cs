using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using PM2E10087.Views;
using PM2E10087.Models;

namespace PM2E10087.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]

    
    public partial class listarsitios : ContentPage
    {
        private Sitios sitio;

        public listarsitios()
        {
            InitializeComponent();
        }

        private void toolmenu_Clicked(object sender, EventArgs e)
        {

        }
       
        private void liestasistios_ItemTapped(object sender, ItemTappedEventArgs e)
        {
           sitio = (Sitios)e.Item;
           
        }
      
        private async void btnvermapa_Clicked(object sender, EventArgs e)
        {
            try
            {
                await Navigation.PushAsync(new ver_mapa(sitio.latitud, sitio.longitud, sitio.descripcion));
            }
             catch
            {
                await DisplayAlert("Advertencia", "Favor seleccione el sitio donde desea ver en el mapa", "Ok");
            }
        }

        protected async override void OnAppearing()
        {
            base.OnAppearing();

            listasitios.ItemsSource = await App.Instancia.ObtenerlistadoSitio();
        }

        //Evento para el boton de eliminar 
        private async void btneliminarsitio_Clicked(object sender, EventArgs e)
        {
            try
            {
                var eliminar = await App.Instancia.eliminarsitio(sitio);


                if (eliminar != 0)
                {
                    await DisplayAlert("Advertencia", "Sitio eliminado con exito", "Aceptar");
                    listasitios.ItemsSource = await App.Instancia.ObtenerlistadoSitio();

                }
                else
                {
                    await DisplayAlert("Advertencia", "Ha ocurrido un error", "Aceptar");
                }
            }
            catch
            {
                await DisplayAlert("Advertencia", "Favor seleccione que sitio desea eliminar", "Aceptar");
            }
            

            
        }
    }
  }
