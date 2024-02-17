package ru.sample.duckapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.data.DucksApi
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api


class MainActivity : AppCompatActivity() {

    private val ducksApi: DucksApi = Api.ducksApi
    lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)

        loadImage("https://random-d.uk/api/135")

        val btn = findViewById<Button>(R.id.button)

        btn.setOnClickListener {
            onClick()
        }

    }

    private fun loadImage(imageUrl: String?) {
        imageUrl?.let {
            Picasso.get().load(it).into(imageView)
        }
    }

    private fun onClick() {
        val call: Call<Duck> = ducksApi.getRandomDuck()

        call.enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.url
                    loadImage(imageUrl)
                } else {
                    error("Oops.. Some error occurred:(")
                }
            }

            override fun onFailure(call: Call<Duck>, e: Throwable) {
                e.printStackTrace()
            }
        })
    }
}
