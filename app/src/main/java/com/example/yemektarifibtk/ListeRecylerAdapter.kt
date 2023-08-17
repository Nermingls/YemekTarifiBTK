package com.example.yemektarifibtk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yemektarifibtk.databinding.RecyclerRowBinding

class ListeRecylerAdapter(val yemekListesi : ArrayList<String>,val idListesi: ArrayList<Int>): RecyclerView.Adapter<ListeRecylerAdapter.YemekHolder>(){

    class  YemekHolder(val binding : RecyclerRowBinding ) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        //rowu hangi tasarimla olusturucagimizi belirliyoruz
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return YemekHolder(RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        //kac row olusturulacagini belirler
       return yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        //rowlarin icine koyacaklarimizi soyluyoruz
        holder.binding.recyclerRowText.text = yemekListesi[position]
        holder.itemView.setOnClickListener {

        }

    }
}