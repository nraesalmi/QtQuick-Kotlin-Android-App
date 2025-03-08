package com.example.breweryfinder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.breweryfinder.databinding.FragmentFirstBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        fetchNorthernPub().start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchNorthernPub(): Thread {
        return Thread {
            val url = URL("https://api.openbrewerydb.org/v1/breweries?by_dist=55.380920,-7.373415&per_page=1")
            val connection = url.openConnection() as HttpsURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                Log.d("HERE", inputSystem.toString())
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val northRequest: List<Request> = Gson().fromJson(inputStreamReader, Array<Request>::class.java).toList()
                updateUI(northRequest)
                inputStreamReader.close()
                inputSystem.close()
            } else {
                binding.northernPub.text = "Failed Connection!"
            }

        }
    }

    private fun updateUI(request: List<Request>) {
        requireActivity().runOnUiThread {
            kotlin.run {
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
                val current = LocalDateTime.now().format(formatter)
                binding.lastUpdated.text = "Last Updated: " + current.toString()
                binding.northernPubName.text = "Name: " + request[0].name
                binding.northernPubBrewType.text = "Brewery Type: " +request[0].brewery_type
                binding.northernPubAddress.text = "Address: " + request[0].address_1 + ", " + request[0].address_2 + ", " + request[0].address_3
                binding.northernPubLatitude.text = "Latitude: " + request[0].latitude
                binding.northernPubLongitude.text = "Longitude: " + request[0].longitude
                binding.northernPubPhone.text = "Phone: " + request[0].phone
                binding.northernPubWebsiteUrl.text = "Website Url: " + request[0].website_url

            }

        }
    }

}