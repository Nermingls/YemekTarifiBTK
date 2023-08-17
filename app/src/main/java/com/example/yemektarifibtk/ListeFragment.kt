package com.example.yemektarifibtk

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemektarifibtk.databinding.ActivityMainBinding
import com.example.yemektarifibtk.databinding.FragmentListeBinding

class ListeFragment : Fragment() {

    private  var _binding : FragmentListeBinding? =null
    private val binding get() =_binding!!
    var yemekIsmiListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : ListeRecylerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecylerAdapter(yemekIsmiListesi,yemekIdListesi)
        binding.RecyclerView.layoutManager = LinearLayoutManager(context)
        binding.RecyclerView.adapter = listeAdapter
        sqlVeriAlma()
    }
    fun sqlVeriAlma(){
        try {
            activity?.let {
                val database = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                val cursor = database.rawQuery("SELECT * FROM yemekler", null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val yemekIdIndex = cursor.getColumnIndex("id")

                yemekIsmiListesi.clear()
                yemekIdListesi.clear()
                while (cursor.moveToNext()){

                    yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))

                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()

            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }



}