package com.example.breweryfinder

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

        findViewById<Button>(R.id.updateButton).setOnClickListener(this)

        binding.qmlFrame.addView(qtQuickView, params)

        //! [layoutParams]
        //! [loadContent]
        qtQuickView!!.loadContent(mainQmlContent)
        //! [loadContent]

        qtQuickView!!.setStatusChangeListener { status ->
            if (status == QtQmlStatus.READY) {
                fetchNumOfPubs().start()
                runFetchPubs()
            }
        }
    }
    var numOfBarsIreland = "Number of Bars in Ireland: "


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
                numOfBarsIreland = "Number of Bars in Ireland: " + numOfBars

                numInputStreamReader.close()
                numInputSystem.close()
            } else {
                numOfBarsIreland = "not found"
            }
        }
    }

    private fun runFetchPubs() {
        fetchPub(
            "northern",
            "https://api.openbrewerydb.org/v1/breweries?by_dist=55.380920,-7.373415&per_page=1"
        ).start()
        // Load Southest pub
        fetchPub(
            "southern",
            "https://api.openbrewerydb.org/v1/breweries?by_dist=51.461818,-9.417598&per_page=1"
        ).start()

        // Find and Load Longest Named pub
        fetchPub(
            "longestName",
            "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=1"
        ).start()

        fetchPub(
            "random",
            "https://api.openbrewerydb.org/v1/breweries/random?by_state=ireland"
        ).start()

    }

    private fun updateUI(request: List<Request>, pub: String) {
        kotlin.run {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            val current = LocalDateTime.now().format(formatter)
            binding.lastUpdated.text = "Last Updated: " + current.toString()
            binding.numOfBars.text = numOfBarsIreland

            if(pub == "northern") {
                qtQuickView?.setProperty("northPubName", request[0].name)
                qtQuickView?.setProperty("northBreweryType", request[0].brewery_type)
                qtQuickView?.setProperty("northAddress", listOfNotNull(
                    request[0].address_1,
                    request[0].address_2,
                    request[0].address_3
                ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "not available")
                qtQuickView?.setProperty("northLatitude", request[0].latitude ?: "not available")
                qtQuickView?.setProperty("northLongitude", request[0].longitude ?: "not available")
                qtQuickView?.setProperty("northPhone", request[0].phone ?: "not available")
                qtQuickView?.setProperty("northWebsite", request[0].website_url ?: "not available")
            } else if(pub == "southern") {
                qtQuickView?.setProperty("southPubName", request[0].name)
                qtQuickView?.setProperty("southBreweryType", request[0].brewery_type)
                qtQuickView?.setProperty("southAddress", listOfNotNull(
                    request[0].address_1,
                    request[0].address_2,
                    request[0].address_3
                ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "not available")
                qtQuickView?.setProperty("southLatitude", request[0].latitude ?: "not available")
                qtQuickView?.setProperty("southLongitude", request[0].longitude ?: "not available")
                qtQuickView?.setProperty("southPhone", request[0].phone ?: "not available")
                qtQuickView?.setProperty("southWebsite", request[0].website_url ?: "not available")
            } else if (pub == "longestName"){
                qtQuickView?.setProperty("longPubName", request[0].name)
                qtQuickView?.setProperty("longBreweryType", request[0].brewery_type)
                qtQuickView?.setProperty("longAddress", listOfNotNull(
                    request[0].address_1,
                    request[0].address_2,
                    request[0].address_3
                ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "not available")
                qtQuickView?.setProperty("longLatitude", request[0].latitude ?: "not available")
                qtQuickView?.setProperty("longLongitude", request[0].longitude ?: "not available")
                qtQuickView?.setProperty("longPhone", request[0].phone ?: "not available")
                qtQuickView?.setProperty("longWebsite", request[0].website_url ?: "not available")
            } else {
                qtQuickView?.setProperty("randomPubName", request[0].name)
                qtQuickView?.setProperty("randomBreweryType", request[0].brewery_type)
                qtQuickView?.setProperty("randomAddress", listOfNotNull(
                    request[0].address_1,
                    request[0].address_2,
                    request[0].address_3
                ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "not available")
                qtQuickView?.setProperty("randomLatitude", request[0].latitude ?: "not available")
                qtQuickView?.setProperty("randomLongitude", request[0].longitude ?: "not available")
                qtQuickView?.setProperty("randomPhone", request[0].phone ?: "not available")
                qtQuickView?.setProperty("randomWebsite", request[0].website_url ?: "not available")
            }
        }
    }

    override fun onClick(v: View?) {
        fetchNumOfPubs().start()
        runFetchPubs()
    }
}