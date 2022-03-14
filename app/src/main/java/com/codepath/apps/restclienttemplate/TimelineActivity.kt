package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {
    lateinit var client: TwitterClient

    lateinit var rvTweets: RecyclerView

    lateinit var adapter: TweetsAdapter

    lateinit var swipeContainer: SwipeRefreshLayout

    val tweets = ArrayList<Tweet>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i("Timeline Activity", "Refreshing Timeline")
            populateHomeTimeline()
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter
        populateHomeTimeline()

    }

    fun populateHomeTimeline(){
        client.getHomeTimeline(object: JsonHttpResponseHandler(){

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val jsonArray = json.jsonArray
                Log.i("Timeline Activity", "On Success $jsonArray")
                try {
                    adapter.clear()
                    val listOfNewTweetsRetrieved = Tweet.fromJasonArray(jsonArray)
                    tweets.addAll(listOfNewTweetsRetrieved)
                    adapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
                }

                catch (e:JSONException){
                    Log.e("Timeline Activity", "JSON Exception $e")

                }


            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("Timeline Activity", "api call failed $statusCode")
            }
        })

    }
}