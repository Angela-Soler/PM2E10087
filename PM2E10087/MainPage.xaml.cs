using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Essentials;
using Plugin.Media;
using System.IO;
using PM2E10087.Views;
using PM2E10087.Models;
using PM2E10087.Controllers;

namespace PM2E10087
{
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            InitializeComponent();
            obtenerLatitudLongitud();
        }
        //Obtener la posicion gps
        public async void obtenerLatitudLongitud()
        {
            try
            {
                var georequest = new GeolocationRequest(GeolocationAccuracy.Best, TimeSpan.FromSeconds(10));

                var tokendecancelacion = new System.Threading.CancellationTokenSource();

                var localizacion = await Geolocation.GetLocationAsync(georequest, tokendecancelacion.Token);


                if (localizacion != null)
                {
                    txtlatitud.Text = localizacion.Latitude.ToString();
                    txtlongitud.Text = localizacion.Longitude.ToString();
                }
            }
            //Si el dispositivo no cuenta con gps se muestra el mensaje 
            catch (FeatureNotSupportedException fnsEx)
            {
                await DisplayAlert("Advertencia", "Este dispositivo no soporta GPS" + fnsEx, "Ok");
            }
            //Si el gps esta desactivado se comunica y se cierra la app
            catch (FeatureNotEnabledException)
            {
                await DisplayAlert("Advertencia", "Su GPS se encuentra desactivado, favor volver a ingresar con el GPS activado", "Ok");
                System.Diagnostics.Process.GetCurrentProcess().Kill();

            }
            //Mostrar un mensaje si no tiene permisos de ubicacion
            catch (PermissionException pEx)
            {
                await DisplayAlert("Advertencia", "Sin Permisos de Geolocalizacion" + pEx, "Ok");
            }
            //Mostrar mensaje si no es posible obtener la ubicacion
            catch (Exception ex)
            {
                await DisplayAlert("Advertencia", "Sin Ubicacion " + ex, "Ok");
            }
        }

        byte[] imageToSave;
        //void para tomar la foto
        private async void AddImg(object sender, EventArgs e)
        {
            try
            {

                var takepic = await CrossMedia.Current.TakePhotoAsync(new Plugin.Media.Abstractions.StoreCameraMediaOptions
                {
                    Directory = "PhotoApp",
                    Name = DateTime.Now.ToString() + "_foto.jpg",
                    SaveToAlbum = true
                });

                //se muestra la ubicacion donde se guardo la foto
                await DisplayAlert("Ubicacion de la foto: ", takepic.Path, "Aceptar");

                if (takepic != null)
                {
                    imageToSave = null;
                    MemoryStream memoryStream = new MemoryStream();

                    takepic.GetStream().CopyTo(memoryStream);
                    imageToSave = memoryStream.ToArray();

                    img.Source = ImageSource.FromStream(() => { return takepic.GetStream(); });
                }
                obtenerLatitudLongitud();
                txtdescripcion.Focus();
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", "Se ha generado el siguiente error al agregar la imagen " + ex, "Aceptar");
            }
        }

        private async void btnlistar_Clicked(object sender, EventArgs e)
        {
            //Sintaxis para dirigirnos a otra pantalla
            await Navigation.PushAsync(new listarsitios());
        }

        private void btnsalir_Clicked(object sender, EventArgs e)
        {
            // Mata el proceso para salir
            System.Diagnostics.Process.GetCurrentProcess().Kill();
        }

        private async void btnagregars_Clicked(object sender, EventArgs e)
        {
            //Validaciones en los campos para evitar campos vacios
            if (imageToSave == null)
            {
                await DisplayAlert("Aviso!", "Ingrese una imagen del sitio", "Aceptar");
            }
            else if (txtdescripcion.Text == null)
            {
                await DisplayAlert("Aviso!", "Ingrese una descripcion del sitio", "Aceptar");
            }
            else
            {
                var sitio = new Sitios { imagen = imageToSave, longitud = txtlatitud.Text, latitud = txtlongitud.Text, descripcion = txtdescripcion.Text };
                var resultado = await App.Instancia.sitioSave(sitio);

                if (resultado != 0)
                {
                    await DisplayAlert("Aviso", "Sitio ingresado con exito!", "Aceptar");
                    txtdescripcion.Text = "";
                    img.Source = "sitio_.jpg";
                    imageToSave = null;

                }
                else
                {
                    await DisplayAlert("Aviso", "Ha Ocurrido un Error", "Aceptar");
                }


                await Navigation.PopAsync();
            }
        }
    }
}
