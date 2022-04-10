package com.example.pt3_boutikar


import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.toolbox.Volley.*
import com.example.pt3_boutikar.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.padding
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import com.nextcloud.android.sso.ui.UiExceptionManager

import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException

import com.nextcloud.android.sso.AccountImporter
import com.google.gson.GsonBuilder

import com.nextcloud.android.sso.api.NextcloudAPI

import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException

import com.nextcloud.android.sso.helper.SingleAccountHelper

import com.nextcloud.android.sso.model.SingleSignOnAccount

import android.content.Intent
import android.net.Uri
import android.service.autofill.Validators.not
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.findFragment
import com.example.pt3_boutikar.databinding.FragmentSleepBinding
import com.google.gson.Gson
import com.nextcloud.android.sso.AccountImporter.IAccountAccessGranted
import com.nextcloud.android.sso.aidl.NextcloudRequest
import org.jetbrains.anko.lines
import org.jetbrains.anko.support.v4.fragmentTabHost
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.lang.Error
import java.lang.NullPointerException
import java.time.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), LoginDialogListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var count: Int = 0
    var usrname : String = ""
    var token : String = ""
    var user : String = ""
    var url : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.fab.setOnClickListener { view -> remove(count) }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_weight, R.id.nav_feeling, R.id.nav_measurement, R.id.nav_sleep, R.id.nav_activities, R.id.nav_persons
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



    }

    override fun applyTexts(username: String, password: String, url: String, user: String) {
        this.usrname = username
        if (password != "")
            this.token = password
        println(password)
        this.user = user
        this.url = url

        remove(count)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.getItemId() === R.id.action_settings) {
            openDialog()
            return true
        } else super.onOptionsItemSelected(item)
    }

    private fun openDialog() {
        var dialog = LoginDialog(usrname, url, user)
        dialog.show(supportFragmentManager,"login dialog")
    }



     fun request(username : String, token : String, url : String, user : String) {
         val textWeight = findViewById<TextView>(R.id.text_weight)
        val client = OkHttpClient()
        val credential = Credentials.basic(username, token)
         val request = Request.Builder()
            .url("https://$url/apps/health/weight/dataset/person/$user")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

                this@MainActivity.runOnUiThread(java.lang.Runnable { textWeight.setText(e.toString()) })
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            textWeight.setText("Error when trying the request, please check your password\n" +
                                    " code : ${response.code}, message : ${response.message}, request url : $url")
                        })
                        throw IOException("Unexpected code $response")
                    }

                    this@MainActivity.runOnUiThread(java.lang.Runnable { textWeight.setText("Weight")})

                    try {
                        val jsonArray =
                            JSONTokener(response.body?.string()).nextValue() as JSONArray
                        count = 0
                        for (i in 0 until jsonArray.length()) {
                            val date = jsonArray.getJSONObject(i).getString("date")
                            val weight = jsonArray.getJSONObject(i).getInt("weight")
                            val bodyFat = jsonArray.getJSONObject(i).getInt("bodyfat")
                            val comment = jsonArray.getJSONObject(i).getString("comment")
                            createWeightRow(date, weight, bodyFat, comment)
                            count++
                        }
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread(java.lang.Runnable { textWeight.setText("Sorry, the userID give is incorrect")})
                    }
                }
            }
        })

    }
    fun requestSleep(username : String, token : String, url : String, user : String) {
        val textSleep = findViewById<TextView>(R.id.text_sleep)
        val client = OkHttpClient()
        val credential = Credentials.basic(username, token)
        val request = Request.Builder()
            .url("https://$url/apps/health/sleep/dataset/person/$user")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                this@MainActivity.runOnUiThread(java.lang.Runnable { textSleep.setText(e.toString()) })
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful){
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            textSleep.setText("Error when trying the request, please check your password\n" +
                                    " code : ${response.code}, message : ${response.message}, request url : $url")
                        })
                        throw IOException("Unexpected code $response")}
                    this@MainActivity.runOnUiThread(java.lang.Runnable { textSleep.setText("Sleep")})
                    try {
                        val jsonArray =
                            JSONTokener(response.body?.string()).nextValue() as JSONArray
                        count = 0
                        for (i in 0 until jsonArray.length()) {
                            val asleep = jsonArray.getJSONObject(i).getString("asleep")
                            val wakeup = jsonArray.getJSONObject(i).getString("wakeup")
                            val quality : String
                            try {
                                quality = jsonArray.getJSONObject(i).getString("quality")
                            }catch (e : JSONException){
                                e.printStackTrace()
                                continue
                            }
                            val comment = jsonArray.getJSONObject(i).getString("comment")
                            createSleepRow(asleep, wakeup, quality, comment)
                            count++
                        }
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread(java.lang.Runnable { textSleep.setText("Sorry, the userID give is incorrect")})

                    }
                }
            }
        })

    }

    fun requestMeasurement(username : String, token : String, url : String, user : String) {
        val textMeasurement = findViewById<TextView>(R.id.text_measurement)
        val client = OkHttpClient()
        val credential = Credentials.basic(username, token)
        val request = Request.Builder()
            .url("https://$url/apps/health/measurement/dataset/person/$user")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

                this@MainActivity.runOnUiThread(java.lang.Runnable { textMeasurement.setText(e.toString()) })
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            textMeasurement.setText("Error when trying the request, please check your password\n" +
                                    " code : ${response.code}, message : ${response.message}, request url : $url")
                        })
                        throw IOException("Unexpected code $response")
                    }

                    this@MainActivity.runOnUiThread(java.lang.Runnable { textMeasurement.setText("Measurement")})

                    try {
                        val jsonArray =
                            JSONTokener(response.body?.string()).nextValue() as JSONArray
                        count = 0
                        for (i in 0 until jsonArray.length()) {
                            val date = jsonArray.getJSONObject(i).getString("datetime")
                            val temperature = jsonArray.getJSONObject(i).getString("temperature")
                            val heartRate = jsonArray.getJSONObject(i).getString("heartRate")
                            val bloodPressureS = jsonArray.getJSONObject(i).getString("bloodPressureS")
                            val bloodPressureD = jsonArray.getJSONObject(i).getString("bloodPressureD")
                            val comment = jsonArray.getJSONObject(i).getString("comment")
                            createMeasurementRow(date, temperature,heartRate, bloodPressureS, bloodPressureD, comment)
                            count++
                        }
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread(java.lang.Runnable { textMeasurement.setText("Sorry, the userID give is incorrect")})
                    }
                }
            }
        })

    }
    fun requestActivities(username : String, token : String, url : String, user : String) {
        val textActivities = findViewById<TextView>(R.id.text_activities)
        val client = OkHttpClient()
        val credential = Credentials.basic(username, token)
        val request = Request.Builder()
            .url("https://$url/apps/health/activities/dataset/person/$user")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

                this@MainActivity.runOnUiThread(java.lang.Runnable { textActivities.setText(e.toString()) })
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            textActivities.setText("Error when trying the request, please check your password\n" +
                                    " code : ${response.code}, message : ${response.message}, request url : $url")
                        })
                        throw IOException("Unexpected code $response")
                    }

                    this@MainActivity.runOnUiThread(java.lang.Runnable { textActivities.setText("Activities")})

                    try {
                        val jsonArray =
                            JSONTokener(response.body?.string()).nextValue() as JSONArray
                        count = 0
                        for (i in 0 until jsonArray.length()) {
                            val date = jsonArray.getJSONObject(i).getString("datetime")
                            val calories = jsonArray.getJSONObject(i).getString("calories")
                            val duration = jsonArray.getJSONObject(i).getString("duration")
                            val comment = jsonArray.getJSONObject(i).getString("comment")
                            createActivitiesRow(date, calories,duration, comment)
                            count++
                        }
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread(java.lang.Runnable { textActivities.setText("Sorry, the userID give is incorrect")})
                    }
                }
            }
        })

    }
    fun requestFeeling(username : String, token : String, url : String, user : String) {
        val textFeeling = findViewById<TextView>(R.id.text_feeling)
        val client = OkHttpClient()
        val credential = Credentials.basic(username, token)
        val request = Request.Builder()
            .url("https://$url/apps/health/feeling/dataset/person/$user")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                this@MainActivity.runOnUiThread(java.lang.Runnable { textFeeling.setText(e.toString()) })
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful){
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            textFeeling.setText("Error when trying the request, please check your password\n" +
                                    " code : ${response.code}, message : ${response.message}, request url : $url")
                        })
                        throw IOException("Unexpected code $response")}
                    this@MainActivity.runOnUiThread(java.lang.Runnable { textFeeling.setText("Feeling")})
                    try {
                        val jsonArray =
                            JSONTokener(response.body?.string()).nextValue() as JSONArray
                        count = 0
                        for (i in 0 until jsonArray.length()) {
                            val date = jsonArray.getJSONObject(i).getString("datetime")
                            val mood = jsonArray.getJSONObject(i).getString("mood")
                            val symptoms = jsonArray.getJSONObject(i).getString("symptoms")
                            val energy = jsonArray.getJSONObject(i).getString("energy")
                            val comment = jsonArray.getJSONObject(i).getString("comment")
                            createFeelingRow(date, mood, symptoms, energy, comment)
                            count++
                        }
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread(java.lang.Runnable { textFeeling.setText("Sorry, the userID give is incorrect")})
                    }
                }
            }
        })

    }
    fun requestPersons(username : String, token : String, url : String, user : String) {
        val textPersons = findViewById<TextView>(R.id.text_persons)
        val client = OkHttpClient()
        val credential = Credentials.basic(username, token)
        val request = Request.Builder()
            .url("https://$url/apps/health/persons")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                this@MainActivity.runOnUiThread(java.lang.Runnable { textPersons.setText(e.toString()) })
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful){
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            textPersons.setText("Error when trying the request, please check your password\n" +
                                    " code : ${response.code}, message : ${response.message}, request url : $url")
                        })
                        throw IOException("Unexpected code $response")}
                    this@MainActivity.runOnUiThread(java.lang.Runnable { textPersons.setText("Persons")})
                    try {
                        val jsonArray =
                            JSONTokener(response.body?.string()).nextValue() as JSONArray
                        count = 0
                        for (i in 0 until jsonArray.length()) {
                            val date = jsonArray.getJSONObject(i).getString("insertTime")
                            val name = jsonArray.getJSONObject(i).getString("name")
                            val userId = jsonArray.getJSONObject(i).getString("userId")
                            val idOfUser = jsonArray.getJSONObject(i).getString("id")
                            createPersonsRow(date, name, userId, idOfUser)
                            count++
                        }
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread(java.lang.Runnable { textPersons.setText("Sorry, the userID give is incorrect")})
                    }
                }
            }
        })

    }
    fun createPersonsRow(date : String, name: String, userId : String, idOfUser:String) {
        val testTable = findViewById<TableLayout>(R.id.dataTable_persons)
        val row = TableRow(this);
        row.padding = 10
        row.backgroundColor = Color.parseColor("#35524A")
        val tableRowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,132)
        row.layoutParams = tableRowParams

        val textParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 1F)


        var rowText = TextView(this)
        rowText.setText(date.substring(0,10))
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText(name)
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText(userId)
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)


        rowText = TextView(this)
        rowText.setText(idOfUser)
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)
        this@MainActivity.runOnUiThread(java.lang.Runnable { testTable.addView(row) })
    }
    fun createWeightRow(date : String, Weight: Int, bodyFat : Int, comment:String) {
        val testTable = findViewById<TableLayout>(R.id.dataTable)
        val row = TableRow(this);
        row.padding = 10
        row.backgroundColor = Color.parseColor("#35524A")
        val tableRowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,132)
        row.layoutParams = tableRowParams

        val textParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 1F)

        var rowText = TextView(this)
        rowText.setText(date)
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText(Weight.toString())
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("$bodyFat")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)


        rowText = TextView(this)
        rowText.setText("$comment")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)
        this@MainActivity.runOnUiThread(java.lang.Runnable { testTable.addView(row) })
    }
    fun createActivitiesRow(date : String, calories: String, duration : String, comment:String) {
        val testTable = findViewById<TableLayout>(R.id.dataTable_activities)
        val row = TableRow(this);
        row.padding = 10
        row.backgroundColor = Color.parseColor("#35524A")
        val tableRowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,132)
        row.layoutParams = tableRowParams

        val textParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 1F)

        var rowText = TextView(this)
        rowText.setText(date)
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText(calories)
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText(duration + "min")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)


        rowText = TextView(this)
        rowText.setText("$comment")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)
        this@MainActivity.runOnUiThread(java.lang.Runnable { testTable.addView(row) })
    }

    fun createSleepRow(asleep : String, wakeup: String, quality : String?, comment:String) {
        var sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        var date1 = sdf.parse(asleep)
        var date2 = sdf.parse(wakeup)
        var duration = Math.abs(date2.time - date1.time)
        var totalDur = TimeUnit.HOURS.convert(duration, TimeUnit.MILLISECONDS)
        val testTable = findViewById<TableLayout>(R.id.dataTable_sleep)
        val row = TableRow(this);
        row.padding = 10
        row.backgroundColor = Color.parseColor("#35524A")
        val tableRowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,132)
        row.layoutParams = tableRowParams

        val textParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 1F)

        var rowText = TextView(this)
        rowText.setText(asleep.substring(0,10))
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("")
        row.addView(rowText)




        rowText = TextView(this)
        rowText.setText(totalDur.toString() + "h")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("$quality")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)


        rowText = TextView(this)
        rowText.setText("$comment")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)
        this@MainActivity.runOnUiThread(java.lang.Runnable { testTable.addView(row) })
    }
    fun createMeasurementRow(date : String, temperature: String, heartRate: String,
                             bloodPressureS : String,  bloodPressureD:String, comment:String) {
        val testTable = findViewById<TableLayout>(R.id.dataTable_measurement)
        val row = TableRow(this);
        row.padding = 10
        row.backgroundColor = Color.parseColor("#35524A")
        val tableRowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,132)
        row.layoutParams = tableRowParams

        val textParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 1F)

        var rowText = TextView(this)
        rowText.setText(date.substring(0,10))
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("$temperature")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams
        rowText.textSize = 14F
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("$heartRate")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams
        rowText.textSize = 14F
        row.addView(rowText)


        rowText = TextView(this)
        rowText.setText("$bloodPressureS")
        rowText.textSize = 14F
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams

        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("$bloodPressureD")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams

        rowText.textSize = 14F
        row.addView(rowText)



        rowText = TextView(this)
        rowText.setText("$comment")
        rowText.layoutParams = textParams
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        row.addView(rowText)

        this@MainActivity.runOnUiThread(java.lang.Runnable { testTable.addView(row) })
    }
    fun createFeelingRow(date : String, mood: String, symptoms : String,  energy:String, comment:String) {
        val testTable = findViewById<TableLayout>(R.id.dataTable_feeling)
        val row = TableRow(this);
        row.padding = 10
        row.backgroundColor = Color.parseColor("#35524A")
        val tableRowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,132)
        row.layoutParams = tableRowParams

        val textParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 1F)


        val regex = "[\\[\\\\\\]]".toRegex()
        val symptomes = symptoms.split(regex)

        val value = symptomes.toString().split(",")


        var rowText = TextView(this)
        rowText.setText(date.substring(0,10))
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        rowText.layoutParams = textParams
        row.addView(rowText)


        rowText = TextView(this)
        rowText.setText("$mood")
        rowText.textSize = 14F
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams

        row.addView(rowText)

        rowText = TextView(this)
        for (i in 0 until value.size) {
            val bool = value.get(i).contains("]") || value.get(i).contains("[")
            if (!bool)
                rowText.append("${value.get(i)}\n")
        }
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams

        rowText.textSize = 14F
        row.addView(rowText)



        rowText = TextView(this)
        rowText.setText("$energy")
        rowText.layoutParams = textParams
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.textSize = 14F
        row.addView(rowText)

        rowText = TextView(this)
        rowText.setText("$comment")
        rowText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        rowText.layoutParams = textParams
        rowText.textSize = 14F
        row.addView(rowText)
        this@MainActivity.runOnUiThread(java.lang.Runnable { testTable.addView(row) })
    }
    fun remove(count:Int) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        if (navController.currentDestination?.id == R.id.nav_sleep) {
           val testTable = findViewById<TableLayout>(R.id.dataTable_sleep)
            try {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    testTable.removeViews(
                        2,
                        count
                    )
                })
            } catch (e: IndexOutOfBoundsException) {
                println("$e : $count")
                println(testTable.childCount)
            }
            requestSleep(usrname, token, url, user)
        }
        if (navController.currentDestination?.id == R.id.nav_feeling) {
            val testTable = findViewById<TableLayout>(R.id.dataTable_feeling)
            try {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    testTable.removeViews(
                        2,
                        count
                    )
                })
            } catch (e: IndexOutOfBoundsException) {
                println("$e : $count")
                println(testTable.childCount)
            }
            requestFeeling(usrname, token, url, user)
        }
        if (navController.currentDestination?.id == R.id.nav_weight) {
            val testTable = findViewById<TableLayout>(R.id.dataTable)
            try {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    testTable.removeViews(
                        2,
                        count
                    )
                })
            } catch (e: IndexOutOfBoundsException) {
                println("$e : $count")
                println(testTable.childCount)
            }
            request(usrname, token, url, user)
        }
        if (navController.currentDestination?.id == R.id.nav_measurement) {
            val testTable = findViewById<TableLayout>(R.id.dataTable_measurement)
            try {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    testTable.removeViews(
                        2,
                        count
                    )
                })
            } catch (e: IndexOutOfBoundsException) {
                println("$e : $count")
                println(testTable.childCount)
            }
            requestMeasurement(usrname, token, url, user)
        }
        if (navController.currentDestination?.id == R.id.nav_activities) {
            val testTable = findViewById<TableLayout>(R.id.dataTable_activities)
            try {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    testTable.removeViews(
                        2,
                        count
                    )
                })
            } catch (e: IndexOutOfBoundsException) {
                println("$e : $count")
                println(testTable.childCount)
            }
            requestActivities(usrname, token, url, user)
        }
        if (navController.currentDestination?.id == R.id.nav_persons) {
            val testTable = findViewById<TableLayout>(R.id.dataTable_persons)
            try {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    testTable.removeViews(
                        2,
                        count
                    )
                })
            } catch (e: IndexOutOfBoundsException) {
                println("$e : $count")
                println(testTable.childCount)
            }
            requestPersons(usrname, token, url, user)
        }

    }
}