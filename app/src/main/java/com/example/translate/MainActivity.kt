package com.example.translate

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.translate.ROOM.AppDatabase
import com.example.translate.ROOM.Word
import com.example.translate.ROOM.WordDAO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var words: ArrayList<Word> = ArrayList()
    lateinit var dao: WordDAO
    var word = Word()
    lateinit var db: AppDatabase

    private lateinit var mHandler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Room
        initRoomDatabase()

        // Get all data from Room
        getWords()

        mHandler = Handler()
        translate.setOnClickListener{
            if(English.text == "English"){
                English.text = "Vietnamese"
                Vietnam.text = "English"
                tvEnglish.text = "VIETNAMESE"
                tvVietnamese.text = "ENGLISH"
            }else{
                English.text = "English"
                Vietnam.text = "Vietnamese"
                tvEnglish.text = "ENGLISH"
                tvVietnamese.text = "VIETNAMESE"
            }
        }
        Save.setOnClickListener {
            if(edEnglish.text.toString() != "" && edVietnam.text.toString() != "")
            {
                // Get translation result
                if(English.text == "English")
                {
                    word = Word(null, edEnglish.text.toString(),edVietnam.text.toString(), true)
                }
                else
                {
                    word = Word(null, edEnglish.text.toString(),edVietnam.text.toString(), false)
                }


                // Default value
                val temp = Word()

                // Word is not exist & different with default value
                if((word != temp) && (wordAvailable(word) == false))
                {
                    dao.insert(word)
                    Toast.makeText(this@MainActivity, "Saved!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@MainActivity, "This word was available!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        save.setOnClickListener{
            val intent = Intent(this@MainActivity, SaveActivity::class.java)

            if(edEnglish.text.toString() != "" && edVietnam.text.toString() != "")
            {
                // Detect language
                if(English.text == "English")
                {
                    val word = Word(null, edEnglish.text.toString(),edVietnam.text.toString(), true)
                    intent.putExtra(WORD_KEY, word)
                }
                else
                {
                    val word = Word(null, edEnglish.text.toString(),edVietnam.text.toString(), false)
                    intent.putExtra(WORD_KEY, word)
                }
            }
            else
            {
                val word= Word()
                intent.putExtra(WORD_KEY, word)
            }
            startActivity(intent)
        }

        btTranslate.setOnClickListener{
            if(tvEnglish.text == "ENGLISH"){
                Log.i("Test: ", "Hello")
                var language1 = "en-vi"
                translation(language1)
            }else{
                var language2 = "vi-en"
                translation(language2)
            }
        }
        camera.setOnClickListener{ goToCamera() }

    }
    private fun wordAvailable(word: Word): Boolean {
        getWords()
        val size = words.size

        // Check item available
        if (size == 0)
        {
            return false
        }

        for (i in 0 until size)
        {
            if(words[i].vietnamese == word.vietnamese && words[i].english == word.english)
            {
                return true
            }
        }

        // List does not contain item
        return false
    }

    private fun initRoomDatabase() {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DATABASE_NAME
        ).allowMainThreadQueries().build()

        dao = db.wordDAO()
    }

    private fun getWords() {
        this.words.clear()
        val words = dao.getAll()
        this.words.addAll(words)
    }
    private fun goToCamera(){
        val intent = Intent(this, CamActivity::class.java)
        startActivity(intent)
    }
    private fun translation(lang: String){
        Log.i("translate", "Haha")
        var text = edEnglish.text
        var link= "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20190523T053835Z.0b61113b08a5ff52.f4eccc205a7332836a2a4a2444997625d673ad3a&lang="+lang+"&text="+text
        //var link = "https://api.themoviedb.org/3/movie/top_rated?api_key=7519cb3f829ecd53bd9b7007076dbe23"
        Log.i("Link: ", link.toString())
        okhttp(link)
    }
    fun okhttp( a: String){
        Log.i("Okhttp: ", "Haha")
        val client = OkHttpClient()
        val request = Request.Builder()
            .header("Authorization", "token abcd")
            .url(a)
            .build()
        client.newCall(request)
            .enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Fail", "No")
                    print("Fail load data")
                }
                override fun onResponse(call: Call, response: Response) {
                    Log.i("onResponse: ", "Haha")
                    if (response.isSuccessful){
                        Log.i("response: ", "HiHi")
                        var json = response.body()!!.string()
                        Log.i("JSON", json.toString())
                        var jsObect = JSONObject(json)
                        var result = jsObect.getJSONArray("text").toString()
                        var collectionType = object : TypeToken<Collection<String>>() {}.type
                        var word: ArrayList<String> = Gson().fromJson(result, collectionType)
                        Log.i("Data: ", word.toString())
                        mHandler.post(Runnable {
                            edVietnam.text = word.get(0).toString()
                        })

                    }
                }
            })

    }
}
