package com.example.breweryfinder

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.breweryfinder.databinding.ActivityMainBinding
import org.qtproject.example.brewery_finder_qtquickApp.Brewery_finder_qtquick.Main
import org.qtproject.qt.android.QtQuickView
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.qtproject.qt.android.QtQmlStatus
import org.qtproject.qt.android.QtQmlStatusChangeListener


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
        // mainQmlContent.setStatusChangeListener(this)

        //! [layoutParams]
        val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.mainLinear.addView(qtQuickView, params)

        //! [layoutParams]
        //! [loadContent]
        qtQuickView!!.loadContent(mainQmlContent)
        //! [loadContent]

    }


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


}