package com.hub.toolbox.mtg.sanitalia.home

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import aqua.extensions.Do
import aqua.extensions.makeActivityFullScreen
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.google.android.material.tabs.TabLayout
import com.hub.toolbox.mtg.sanitalia.R
import kotlinx.android.synthetic.main.activity_gea_medica.*
import kotlinx.android.synthetic.main.content_gea_medica.*

class GeaMedicaActivity : AppCompatActivity() {

    private val imageList = mutableListOf(
            "http://www.geamedica.net/cache/widgetkit/gallery/3/1-geamedica-struttura-fronte-95ac2e8e30.jpg",
            "http://www.geamedica.net/cache/widgetkit/gallery/3/2-reception-928583d3ef.jpg",
            "http://www.geamedica.net/cache/widgetkit/gallery/3/91-7c69e017b8.jpg",
            "http://www.geamedica.net/cache/widgetkit/gallery/3/6-DSC01176-ce2657fdfd.jpg",
            "http://www.geamedica.net/cache/widgetkit/gallery/3/9-DSC01257-46cc851f23.jpg",
            "http://www.geamedica.net/cache/widgetkit/gallery/3/2-reception-928583d3ef.jpg",
            "http://www.geamedica.net/cache/widgetkit/gallery/3/8-DSC01268-1a55637e51.jpg"
    )
    var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gea_medica)
        setSupportActionBar(toolbar)
        makeActivityFullScreen()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Chiama Gea Medica", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        supportActionBar?.title = "Gea Medica"

        imageLooper()

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        (geaMedicaViewPager as ViewPager).adapter = fragmentAdapter
        (geaMedicaTabs as TabLayout).setupWithViewPager(geaMedicaViewPager as ViewPager)
    }

    private fun imageLooper() {
        val imageToLoad = imageList.get(currentImageIndex)
        if(currentImageIndex < imageList.size-1) currentImageIndex++
        else currentImageIndex = 0
        if(imageGallery != null && this != null)
            Glide.with(applicationContext).load(imageToLoad).transition(withCrossFade()).into(imageGallery)
        Do after 5 seconds {
            imageLooper()
        }
    }
}

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                GeaMedicaStrutturaFragm()
            }
            1 -> GeaMedicaRicoveroFrag()
            2 -> GeaMedicaAccoglienza()
            3 -> GeaMedicaOrariFragment()
            else -> {
                return GeaMedicaStrutturaFragm()
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Struttura"
            1 -> "Ricovero"
            2 -> "Accoglienza"
            3 -> "Orari"
            else -> {
                return "Struttura"
            }
        }
    }
}