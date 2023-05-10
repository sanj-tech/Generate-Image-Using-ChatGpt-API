package com.example.imagegeneratorchatgpt

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.imagegeneratorchatgpt.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var chatGpt_api_Key = ""
    lateinit var binding: ActivityMainBinding
    var client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnGenerate.setOnClickListener {
            val inputText = binding.edtInput.text.toString()
            if (inputText.isEmpty()) {
                binding.edtInput.setError("Text cant be empty")
            } else {
                getApiCall(inputText)
            }
        }


    }

    private fun getApiCall(inputText: String) {
        setInProgressBar(true)
        val jsonBody = JSONObject()
        try {
            jsonBody.put("prompt", inputText)
            jsonBody.put("size", "256x256")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val requestBody: RequestBody = RequestBody.create(JSON, jsonBody.toString())
        val request: Request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .header("Authorization", "Bearer $chatGpt_api_Key")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(applicationContext, "clicked", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())
                    val imageUrl = jsonObject.getJSONArray("data")
                        .getJSONObject(0)
                        .getString("url")
                    loadImage(imageUrl)
                    setInProgressBar(false)


                } catch (e: java.lang.Exception) {

                }


            }
        })


    }

    private fun loadImage(imageUrl: String) {
        runOnUiThread {
            Picasso.get().load(imageUrl).into(binding.ivImage)
            binding.ivImage.visibility = View.VISIBLE
        }

    }

    companion object {
        val JSON: MediaType = "application/json;charset=utf-8".toMediaType()
    }

    private fun setInProgressBar(isProgressBar: Boolean) {
        if (isProgressBar) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnGenerate.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.visibility = View.VISIBLE
        }

    }
}