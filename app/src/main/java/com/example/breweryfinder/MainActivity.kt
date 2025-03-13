package com.example.breweryfinder

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.breweryfinder.databinding.ActivityMainBinding
import com.google.gson.Gson
import org.qtproject.example.brewery_finder_qtquickApp.Brewery_finder_qtquick.Main
import org.qtproject.qt.android.QtQuickView
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var qtQuickView: QtQuickView? = null
    //! [qmlContent]
    private var mainQmlContent: Main = Main()

    //! [onCreate]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //! [binding]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //! [m_qtQuickView]
        qtQuickView = QtQuickView(this)

        // Set status change listener for m_qmlView
        // listener implemented below in OnStatusChanged
        //! [setStatusChangeListener]
        //mainQmlContent.setStatusChangeListener(this)

        //! [layoutParams]
        val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.mainLinear.addView(qtQuickView, params)

        //! [layoutParams]
        //! [loadContent]
        qtQuickView!!.loadContent(mainQmlContent)
        //! [loadContent]

        // Load Northest pub
        fetchPub("northern", "https://api.openbrewerydb.org/v1/breweries?by_dist=55.380920,-7.373415&per_page=1").start()

        // Load Southest pub
        fetchPub("southern", "https://api.openbrewerydb.org/v1/breweries?by_dist=51.461818,-9.417598&per_page=1").start()

        // Find and Load Longest Named pub
        fetchPub("longestName", "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=1").start()
    }

/*
    //! [onStatusChanged]
    override fun onStatusChanged(status: QtQmlStatus?) {
        Log.v(TAG, "Status of QtQuickView: $status")


        val qmlStatus = (resources.getString(R.string.qml_view_status)
                + m_statusNames[status])

        // Show current QML View status in a textview
        binding.qmlStatusText.text = qmlStatus

        // Connect signal listener to "onClicked" signal from main.qml
        // addSignalListener returns int which can be used later to identify the listener
        //! [qml signal listener]
        if (status == QtQmlStatus.READY && !binding.disconnectQmlListenerSwitch.isChecked) {
            qmlButtonSignalListenerId =
                mainQmlContent.connectOnClickedListener { _: String, _: Void? ->
                    Log.i(TAG, "QML button clicked")
                    binding.kotlinLinear.setBackgroundColor(
                        Color.parseColor(
                            colors.getColor()
                        )
                    )
                }
        }
        //! [qml signal listener]
    }

    */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun findLongestName(request: List<Request>): Request {
        var longestNamedPub = request[0]
        Log.d("PUBS", request.size.toString())
        for (pub in request) {
            if(pub.name.length > longestNamedPub.name.length) {
                Log.d("PUB", pub.toString())
                longestNamedPub = pub
            }
        }
        Log.d("LONG", longestNamedPub.toString())
        return longestNamedPub
    }

    private fun fetchPub(pub: String, urlString: String): Thread {
        return Thread {
            val url = URL(urlString)
            var connection = url.openConnection() as HttpsURLConnection

            if(connection.responseCode == 200) {
                var inputSystem = connection.inputStream
                var inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                var request: MutableList<Request> =
                    Gson().fromJson(inputStreamReader, Array<Request>::class.java).toList().toMutableList()
                if(pub == "longestName") {
                    val url = URL("https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=2")
                    connection = url.openConnection() as HttpsURLConnection
                    inputSystem = connection.inputStream
                    inputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                    request.addAll(Gson().fromJson(inputStreamReader, Array<Request>::class.java).toList())

                    var longestNamedPub = findLongestName(request)
                    request[0] = longestNamedPub
                }

                runOnUiThread {
                    updateUI(request, pub)
                }
                inputStreamReader.close()
                inputSystem.close()
            } else {
                binding.lastUpdated.text = "Failed Connection!"
            }

        }
    }



    private fun updateUI(request: List<Request>, pub: String) {

        kotlin.run {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
            val current = LocalDateTime.now().format(formatter)

            if(pub == "northern") {
                binding.lastUpdated.text = "Last Updated: " + current.toString()
                binding.northernPubName.text = "Name: " + request[0].name
                binding.northernPubBrewType.text = "Brewery Type: " +request[0].brewery_type
                binding.northernPubAddress.text = "Address: " + request[0].address_1 + ", " + request[0].address_2 + ", " + request[0].address_3
                binding.northernPubLatitude.text = "Latitude: " + (request[0].latitude ?: "not available")
                binding.northernPubLongitude.text = "Longitude: " + (request[0].longitude ?: "not available")
                binding.northernPubPhone.text = "Phone: " + (request[0].phone ?: "not available")
                binding.northernPubWebsiteUrl.text = "Website Url: " + (request[0].website_url ?: "not available")
            } else if(pub == "southern") {
                binding.southernPubName.text = "Name: " + request[0].name
                binding.southernPubBrewType.text = "Brewery Type: " +request[0].brewery_type
                binding.southernPubAddress.text = "Address: " + request[0].address_1 + ", " + request[0].address_2 + ", " + request[0].address_3
                binding.southernPubLatitude.text = "Latitude: " + (request[0].latitude ?: "not available")
                binding.southernPubLongitude.text = "Longitude: " + (request[0].longitude ?: "not available")
                binding.southernPubPhone.text = "Phone: " + (request[0].phone ?: "not available")
                binding.southernPubWebsiteUrl.text = "Website Url: " + (request[0].website_url ?: "not available")
            } else if(pub == "longestName") {
                binding.longestNamePubName.text = "Name: " + request[0].name
                binding.longestNamePubBrewType.text = "Brewery Type: " +request[0].brewery_type
                binding.longestNamePubAddress.text = "Address: " + request[0].address_1 + ", " + request[0].address_2 + ", " + request[0].address_3
                binding.longestNamePubLatitude.text = "Latitude: " + (request[0].latitude ?: "not available")
                binding.longestNamePubLongitude.text = "Longitude: " + (request[0].longitude ?: "not available")
                binding.longestNamePubPhone.text = "Phone: " + (request[0].phone ?: "not available")
                binding.longestNamePubWebsiteUrl.text = "Website Url: " + (request[0].website_url ?: "not available")
            }
        }
    }
}