package com.chase.covid19.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chase.covid19.R
import com.chase.covid19.adapters.CityListAdapter
import com.chase.covid19.ext.gson
import com.chase.covid19.ext.readAssetFileText
import com.chase.covid19.model.CityModel
import com.chase.covid19.model.ResultData
import com.chase.covid19.utils.*
import com.chase.covid19.views.ProgressBarDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_send_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val PERM_REQ_LOCATION = 234

class SendResultActivity : BaseActivity(), CityListAdapter.OnItemClickListener {
    var needSelectLocation = false

    private var TILCity: TextInputLayout? = null
    private var etCity: TextInputEditText? = null

    var recyclerViewCities: RecyclerView? = null
    var cityListAdapter: CityListAdapter? = null
    var cities: List<CityModel> = ArrayList()
    var sheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    var bottomSheet: LinearLayout? = null

    private var searchTimer: Timer? = null
    private var searchResult: List<CityModel>? = null
    private var cityCode: String = ""
    private var citylat: String = ""
    private var citylong: String = ""

    private var citylatUser: String = ""
    private var citylongUser: String = ""

    private var progressDialog: ProgressBarDialog? = null

    // This can be local variable
    private var locationManager: LocationManager? = null

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERM_REQ_LOCATION && grantResults.size >= 2) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            try {
                // Request location updates
                locationManager?.let {
                    if (it.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                        it.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                  0L,
                                                  0f,
                                                  locationListener)
                    else if (it.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        it.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                  0L,
                                                  0f,
                                                  locationListener)
                }
            } catch (ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_send_result)

        progressDialog = ProgressBarDialog(this, true)

        TILCity = findViewById(R.id.profile_city)
        etCity = findViewById(R.id.et_profile_city)

        setCityListData()

        if (Prefs.submitText.isNotEmpty()) {
            tv_message.text = Prefs.submitText
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERM_REQ_LOCATION
        )

        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val result1 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            try {
                // Request location updates
                locationManager?.let {
                    if (it.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                        it.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                  0L,
                                                  0f,
                                                  locationListener
                        )
                    else if (it.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        it.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                  0L,
                                                  0f,
                                                  locationListener
                        )
                }
            } catch (ex: Exception) {
                Log.d("myTag", "Security Exception, no location available")
            }
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }

        btn_send_result.setOnClickListener {
            //call api
            if (!NetworkUtils.isNetworkConnected(rootView, showError = true) || checkErrors())
                return@setOnClickListener

            finish()
            startActivity(
                Intent(this, QuestionsActivity::class.java).putExtra(
                    Extra.ResultData,
                    ResultData(
                        if (radio_men.isChecked) "m" else "f",
                        profile_birth_date.progress.toString(),
                        if (citylatUser.isNotEmpty()) "$citylatUser,$citylongUser" else "$citylat,$citylong",
                        "$citylat,$citylong",
                        Prefs.mobile,
                        etname.text.toString(),
                        tv_weight.progress.toDouble(),
                        tv_height.progress.toDouble(),
                        if (needSelectLocation) "list" else "gps",
                        hashMapOf()
                    )
                )
            )
        }
    }

    private fun checkErrors(): Boolean {
        var hasError = false

        if (etCity?.text?.isEmpty() == true) {
            hasError = true
            TILCity?.error = getString(R.string.city_error)
        } else {
            TILCity?.error = null
            TILCity?.isErrorEnabled = false
        }

        if (etname?.text?.isEmpty() == true) {
            hasError = true
            TILname?.error = getString(R.string.name)
        } else {
            TILname?.error = null
            TILname?.isErrorEnabled = false
        }

        return hasError
    }

    private fun setCityListData() {
        bottomSheet = findViewById(R.id.bottom_sheet)
        recyclerViewCities = findViewById(R.id.recyclerViewCities)
        val editTextSearch: EditText? = findViewById(R.id.editTextSearch)
        recyclerViewCities?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cityListAdapter = CityListAdapter()
        cityListAdapter?.onItemClickListener = this
        recyclerViewCities?.adapter = cityListAdapter
        bottomSheet?.let {
            sheetBehavior = BottomSheetBehavior.from(it)
        }
        etCity?.setOnClickListener {
            sheetBehavior?.apply {
                if (state != BottomSheetBehavior.STATE_EXPANDED)
                    state = BottomSheetBehavior.STATE_EXPANDED
                else
                    state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        sheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        editTextSearch?.clearFocus()
                        editTextSearch?.requestFocus()
                        view.showKeyboard()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        view.hideKeyboard()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(view: View, v: Float) {
            }
        })

        getCityList()

        editTextSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s.toString())
            }
        })
    }

    private fun getCityList() {
        val tmp = applicationContext.readAssetFileText("convertcsv.json")

        cities = gson.fromJson(tmp, Array<CityModel>::class.java).asList()
        cityListAdapter?.setData(cities)
    }

    fun search(query: String?) {
        if (query.isNullOrBlank()) {
            searchResult = null
            cityListAdapter?.setData(cities)
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer?.cancel()
                }
            } catch (e: Exception) {
                // Please handle exception or remove this catch block.
            }

            searchTimer = Timer()
            searchTimer?.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        searchTimer?.cancel()
                        searchTimer = null
                    } catch (e: Exception) {
                        // Please handle exception or remove this catch block.
                    }

                    processSearch(query)
                }
            }, 100, 300)
        }
    }

    private fun processSearch(query: String) {
        // I think using Dispatchers.IO is better for REST calls.
        CoroutineScope(Dispatchers.Main).launch {
            val q = query.trim { it <= ' ' }.toLowerCase()
            if (q.isEmpty()) {
                updateSearchResults(listOf())
                return@launch
            }
            val resultArray = cities.filter { it.City.toLowerCase().contains(query) }
            updateSearchResults(resultArray)
        }
    }

    private fun updateSearchResults(counties: List<CityModel>) {
        searchResult = counties
        cityListAdapter?.setData(counties)
    }

    override fun onItemClick(cityModel: CityModel, position: Int) {
        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        etCity?.setText(cityModel.City)
        cityCode = cityModel.id.toString()
        citylat = cityModel.latitude
        citylong = cityModel.longitude
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                bottomSheet?.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                    sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat = location.latitude.toString()
            val lon = location.longitude.toString()
            if (lat.isNotEmpty() && lon.isNotEmpty()) {
                citylatUser = lat
                citylongUser = lon
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onDestroy() {
        super.onDestroy()

        locationManager?.removeUpdates(locationListener)
    }

    override fun onPause() {
        super.onPause()

        locationManager?.removeUpdates(locationListener)
    }
}