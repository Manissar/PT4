package com.example.pt3_boutikar.ui.weight



import android.app.Activity
import android.graphics.Color
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pt3_boutikar.MainActivity
import com.example.pt3_boutikar.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request;
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.padding
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException

class WeightViewModel : ViewModel() {



    private val client = OkHttpClient()

    public fun run() {
        val credential = Credentials.basic("Manissar", "JiXugN5cgq*0PooH59X&pcs%r^31c5df")
        val request = Request.Builder()
            .url("https://cloud.floriancuny.fr/apps/health/weight/dataset/person/2")
            .addHeader("Authorization", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response : okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    //_text.postValue(response.body?.string())
                    //println(response.body?.string())
                    val jsonArray = JSONTokener(response.body?.string()).nextValue() as JSONArray
                    for (i in 0 until jsonArray.length()){
                        val id = jsonArray.getJSONObject(i).getString("id")

                    }
                }
            }
        })
    }


}