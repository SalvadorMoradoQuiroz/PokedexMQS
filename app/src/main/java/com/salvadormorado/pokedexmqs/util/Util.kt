package com.salvadormorado.pokedexmqs.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.salvadormorado.pokedexmqs.R
import java.net.InetSocketAddress
import java.net.Socket
import java.security.SecureRandom
import java.text.DecimalFormat

class Util {
    companion object{
        var FLAG = false

        fun generateRandomNumber(): Int {
            val secureRandom = SecureRandom()
            return secureRandom.nextInt(1010) + 1
        }

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
                networkCapabilities?.run {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                } ?: false
            } else {
                connectivityManager?.activeNetworkInfo?.run {
                    type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE
                } ?: false
            }
        }

        fun isOnlineNet(): Boolean {
            return try {
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)
                socket.connect(socketAddress, 1500)
                socket.close()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun roundToTwoDecimalPlaces(number: Float): String {
            val df = DecimalFormat("#.##")
            return df.format(number)
        }

        fun alertDialogCustom(title: String, message: String, context: Context) : Dialog {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val inflater : LayoutInflater = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var layout = inflater.inflate(R.layout.item_alert, null)
            builder.setView(layout)
            val dialog: Dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
            dialog.setCancelable(false)

            val textViewTitleAlert: TextView = layout.findViewById(R.id.titleTextView)
            val textViewMassageAlert: TextView = layout.findViewById(R.id.messageTextView)
            val buttonAcceptAlert: Button = layout.findViewById(R.id.closeButton)

            textViewTitleAlert.text = "${title}"
            textViewMassageAlert.text = "${message}"
            textViewMassageAlert.movementMethod = ScrollingMovementMethod()
            buttonAcceptAlert.setOnClickListener{

            }
            return dialog
        }

    }
}