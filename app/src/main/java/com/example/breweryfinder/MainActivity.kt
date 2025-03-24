package com.example.breweryfinder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.breweryfinder.databinding.ActivityMainBinding
import com.google.gson.Gson
import org.qtproject.example.brewery_finder_qtquickApp.Brewery_finder_qtquick.Main
import org.qtproject.qt.android.QtQmlStatus
import org.qtproject.qt.android.QtQuickView
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity(), View.OnClickListener {

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

        //! [layoutParams]
        val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        // calls runFetchPubs() and starts fetchnumOfPubs()
        findViewById<Button>(R.id.updateButton).setOnClickListener(this)

        binding.qmlFrame.addView(qtQuickView, params)

        //! [layoutParams]
        //! [loadContent]
        qtQuickView!!.loadContent(mainQmlContent)
        //! [loadContent]

        // Once elements in QtQuickView are initialized, calls runFetchPubs() and starts fetchnumOfPubs()
        qtQuickView!!.setStatusChangeListener { status ->
            if (status == QtQmlStatus.READY) {
                fetchNumOfPubs().start()
                runFetchPubs()
            }
        }
    }

    var numOfBarsIreland = "Number of Pubs in Ireland: "

    // Takes list of Requests and finds the one with the longest name
    private fun findLongestName(request: List<Request>): Request {
        var longestNamedPub = request[0]
        for (pub in request) {
            if(pub.name.length > longestNamedPub.name.length) {
                longestNamedPub = pub
            }
        }
        return longestNamedPub
    }

    // Takes API call urlString and pub String for determening which pub type to update
    // if pub is "longestName", make a new API call to get the rest of the pubs and add them to request
    // next call updateUI in UiThread to update corresponding pub type elements with request[0]
    private fun fetchPub(pub: String, urlString: String): Thread {
        return Thread {
            val url = URL(urlString)
            var connection = url.openConnection() as HttpsURLConnection

            if(connection.responseCode == 200) {
                var inputSystem = connection.inputStream
                var inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                var request: MutableList<Request> =
                    Gson().fromJson(inputStreamReader, Array<Request>::class.java).toList().toMutableList()
                if(pub == "long") {
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

    // Creates API call to get metadata of pubs in Ireland
    // Takes value of first element (total) from received Object into numOfBars
    // Updates public var numOfBarsIreland
    private fun fetchNumOfPubs(): Thread {
        return Thread {
            val numUrl = URL("https://api.openbrewerydb.org/v1/breweries/meta?by_country=ireland")
            val numConnection = numUrl.openConnection() as HttpsURLConnection

            if (numConnection.responseCode == 200) {
                val numInputSystem = numConnection.inputStream
                val numInputStreamReader = InputStreamReader(numInputSystem, "UTF-8")
                val numOfBars =
                    Gson().fromJson(numInputStreamReader, Map::class.java).values.first()
                Log.d("NUM", numOfBars.toString())
                numOfBarsIreland = "Number of Pubs in Ireland: " + numOfBars

                numInputStreamReader.close()
                numInputSystem.close()
            } else {
                numOfBarsIreland = "not found"
            }
        }
    }

    // Calls fetchPub with the pub type and API call as parameters
    // starts thread executions
    private fun runFetchPubs() {
        fetchPub(
            "north",
            "https://api.openbrewerydb.org/v1/breweries?by_dist=55.380920,-7.373415&per_page=1"
        ).start()

        // Load Southest pub
        fetchPub(
            "south",
            "https://api.openbrewerydb.org/v1/breweries?by_dist=51.461818,-9.417598&per_page=1"
        ).start()

        // Find and Load Longest Named pub
        fetchPub(
            "long",
            "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=1"
        ).start()

        // Load Random Pub
        fetchPub(
            "random",
            "https://api.openbrewerydb.org/v1/breweries/random?by_state=ireland"
        ).start()
    }

    // Updates Kotlin elements lastUpdated and numOfBars
    // checks which pub type to add the data in List<Request> using pub String
    // updates corresponding QML properties by adding request data
    private fun updateUI(request: List<Request>, pub: String) {
        kotlin.run {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            val current = LocalDateTime.now().format(formatter)
            binding.lastUpdated.text = "Last Updated: " + current.toString()
            binding.numOfBars.text = numOfBarsIreland

            // Update Pub data
            qtQuickView?.setProperty(pub + "PubName", request[0].name)
            qtQuickView?.setProperty(pub + "BreweryType", request[0].brewery_type)
            qtQuickView?.setProperty(pub + "Address", listOfNotNull(
                request[0].address_1,
                request[0].address_2,
                request[0].address_3
            ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "not available")
            qtQuickView?.setProperty(pub + "Latitude", request[0].latitude ?: "not available")
            qtQuickView?.setProperty(pub + "Longitude", request[0].longitude ?: "not available")
            qtQuickView?.setProperty(pub + "Phone", request[0].phone ?: "not available")
            qtQuickView?.setProperty(pub + "Website", request[0].website_url ?: "not available")
        }
    }

    // Clicking the button in the Kotlin view:
    // - Starts fetchNumOfPubs() Thread to fetch number of pubs metadata with API call
    // - calls runFetchPubs() that fetches data for all pub types
    override fun onClick(v: View?) {
        fetchNumOfPubs().start()
        runFetchPubs()
    }
}