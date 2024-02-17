package ru.sample.duckapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.data.DucksApi
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private val ducksApi: DucksApi = Api.ducksApi
    private lateinit var imageView: ImageView
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)

        editText = findViewById(R.id.editCode)

        randomRequest()

        findViewById<Button>(R.id.button).setOnClickListener {
            onClick()
        }

    }


    private fun onClick() {
        val txt: String = editText.text.toString()
        if(txt.isBlank()) {
            randomRequest()
            return
        }
        try {
            val code = txt.toInt()
            if(code < 100 || code >= 600)
                showValidationPopup("Введите целое число в интервале [100; 600)")
            else
                httpCodeRequest(code)
        } catch (e: NumberFormatException) {
            showValidationPopup("Ну для начала хоть число введите...")
        }
    }

    private fun randomRequest() {
        val call: Call<Duck> = ducksApi.getRandomDuck()

        call.enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.url
                    loadImage(imageUrl)
                } else {
                    showValidationPopup("Упс..")
                }
            }

            override fun onFailure(call: Call<Duck>, e: Throwable) {
                showValidationPopup("Что-то не так не с сетью...")
            }
        })
    }

    private fun httpCodeRequest(code: Int) {

        val call: Call<ResponseBody> = ducksApi.getHttpCode(code)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageStream = response.body()?.byteStream()
                    loadImage(imageStream)
                } else {
                    showValidationPopup("Кажется, такой утки нет(")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showValidationPopup("Что-то не так не с сетью...")
            }
        })
    }


    private fun loadImage(imageByteStream: InputStream?) {
        imageByteStream?.let {
            val bitmap = BitmapFactory.decodeStream(imageByteStream)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun loadImage(imageUrl: String?) {
        imageUrl?.let {
            Picasso.get().load(it).into(imageView)
        }
    }

    fun showValidationPopup(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setMessage(message)

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
