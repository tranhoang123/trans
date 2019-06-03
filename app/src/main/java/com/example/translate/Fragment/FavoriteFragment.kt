package com.example.translate.Fragment

import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.translate.*
import com.example.translate.ROOM.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import java.util.*



class FavoriteFragment: Fragment()  {

    var words: ArrayList<Word> = ArrayList()
    lateinit var wordAdapter: WordAdapter
    lateinit var dao: WordDAO

    var word = Word()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_favorite, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRoomDatabase()

        setupRecycleView()

        getWords()
    }


    private fun initRoomDatabase() {
        // Getting application context
        val context = activity

        val db = Room.databaseBuilder(
            getContext()!!
            ,AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries()
            .build()
        dao = db.wordDAO()
    }

    private fun setupRecycleView() {
        rvFavorite.layoutManager = LinearLayoutManager(getContext()) as RecyclerView.LayoutManager?

        wordAdapter = WordAdapter(words, getContext()!!)

        rvFavorite.adapter = wordAdapter

        wordAdapter.setListener(wordItemClickListener)
    }

    private fun getWords()
    {
        var words = dao.getAll()
        this.words.addAll(words)
        wordAdapter.notifyDataSetChanged()

    }

    private val wordItemClickListener = object : WordItemClickListener {

        override fun onTrashIconClicked(position: Int) {
            dao.delete(words[position])
            wordAdapter.removeItem(words[position], position)
            wordAdapter.notifyDataSetChanged()
        }
    }


}