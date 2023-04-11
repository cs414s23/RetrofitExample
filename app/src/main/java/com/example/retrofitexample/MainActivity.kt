package com.example.retrofitexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://randomuser.me/api/"

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define an array to store a list of users
        val userList = ArrayList<User>()

        // specify a viewAdapter for the dataset
        val adapter = UsersAdapter(userList)

        // Store the the recyclerView widget in a variable
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.adapter = adapter

        // use a linear layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val randomUserAPI = retrofit.create(RandomUserService::class.java)

        // Using enqueue method allows to make asynchronous call without blocking/freezing main thread
        // randomUserAPI.getUserInfo("us").enqueue  // this end point gets one user only
        // getMultipleUserInfoWithNationality end point gets multiple user info with nationality as parameters
        randomUserAPI.getMultipleUserInfoWithNationality(20, "us").enqueue(object :
            Callback<UserData> {

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.d(TAG, "onFailure : $t")
            }

            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                Log.d(TAG, "onResponse: $response")

                val body = response.body()
                if (body == null){
                    Log.w(TAG, "Valid response was not received")
                    return
                }

                // The following log messages are just for testing purpose
                Log.d(TAG, ": ${body.results.get(0).name.first}")
                Log.d(TAG, ": ${body.results.get(0).name.last}")
                Log.d(TAG, ": ${body.results.get(0).email}")
                Log.d(TAG, ": ${body.results.get(0).gender}")
                Log.d(TAG, ": ${body.results.get(0).imageUrl.medium}")

                // Update the adapter with the new data
                userList.addAll(body.results)
                adapter.notifyDataSetChanged()
            }

        })

    }
}
