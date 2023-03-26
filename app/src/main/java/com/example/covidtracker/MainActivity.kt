package com.example.covidtracker

import android.annotation.SuppressLint
import android.app.DownloadManager.Request
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.Response
import org.json.JSONException
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context


class MainActivity : AppCompatActivity() {
    lateinit var worldCasesTV: TextView
    lateinit var worldRecoveredTV: TextView
    lateinit var worldDeathsTV: TextView
    lateinit var countryCasesTV: TextView
    lateinit var countryRecoveredTV: TextView
    lateinit var countryDeathsTV: TextView
    lateinit var stateRV: RecyclerView
    lateinit var stateRVAdapter: StateRVAdapter
    lateinit var stateList: List<StateModal>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        worldCasesTV = findViewById(R.id.idTVWorldCases)
        worldRecoveredTV = findViewById(R.id.idTCWorldRecovered)
        worldDeathsTV = findViewById(R.id.idTVWorldDeaths)
        countryCasesTV = findViewById(R.id.idTVIndiaCases)
        countryDeathsTV = findViewById(R.id.idTVIndiaDeaths)
        countryRecoveredTV = findViewById(R.id.idTVIndiaRecovered)
        stateRV =findViewById(R.id.idRVStates)
        stateList = ArrayList<StateModal>()
        getstateInfo()
        getWorldInfo()
    }

    //Creating two methods for getting the information for the world and the other method for state and country.

    private  fun getstateInfo(){

        val url = "https://api.rootnet.in/covid19-in/stats/latest"   //API URL
        val queue  = Volley.newRequestQueue(this@MainActivity)
        val request = JsonObjectRequest(
            com.android.volley.Request.Method.GET, url, null,
            {response->
                try {
                    val dataObj = response.getJSONObject("data")
                    val summaryObj = dataObj.getJSONObject("summary")
                    val cases: Int = summaryObj.getInt("total")
                    val recovered: Int = summaryObj.getInt("discharged")
                    val deaths: Int = summaryObj.getInt("deaths")

                    countryCasesTV.text =cases.toString()
                    countryDeathsTV.text = deaths.toString()
                    countryRecoveredTV.text = recovered.toString()

                    val regionalArray = dataObj.getJSONArray("regional")
                    for(i in 0  until regionalArray.length()){
                        val regionalObj = regionalArray.getJSONObject(i)
                        val stateName: String= regionalObj.getString("loc")
                        val cases: Int= regionalObj.getInt("totalConfirmed")
                        val recovered: Int= regionalObj.getInt("discharged")
                        val deaths: Int= regionalObj.getInt("deaths")

                        //We need to pass this data into our modal class
                        val stateModal = StateModal(stateName, recovered, deaths, cases)

                        //Now we need to add this sateModel into our stateList
                        stateList = stateList + stateModal
                    }
                // To set this data into our recycler view we are passing this list to our adapter classs
                    stateRVAdapter = StateRVAdapter(stateList)
                    stateRV.layoutManager = LinearLayoutManager(this)
                    stateRV.adapter = stateRVAdapter


                }catch (e: JSONException){
                    e.printStackTrace()
            }

                // Handle the response here
            }
        ) {
            // Handle errors here
        }

        queue.add(request)
    }

  private fun getWorldInfo(){
        val url  = "https://corona.lmao.ninja/v3/covid-19/all"
        val queue = Volley.newRequestQueue(this@MainActivity)
      val request = JsonObjectRequest(
          com.android.volley.Request.Method.GET, // HTTP method
          url, // API endpoint URL
          null, // JSON request body (can be null)
           { response ->
              // Handle successful response here
              try {
                  val worldCases: Int = response.getInt("cases")
                  val worldRecovered: Int = response.getInt("recovered")
                  val worldDeaths: Int = response.getInt("deaths")

                  worldRecoveredTV.text = worldRecovered.toString()
                  worldCasesTV.text = worldCases.toString()
                  worldDeathsTV.text = worldDeaths.toString()
              }
              catch (e:JSONException){
                  e.printStackTrace()
              }
          },
          Response.ErrorListener { error ->
              // Handle error response here
          }
      )

// Add the request to the Volley request queue


      queue.add(request)
   }

}


