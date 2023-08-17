package com.example.yemektarifibtk

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.yemektarifibtk.databinding.FragmentTarifBinding
import java.io.ByteArrayOutputStream
import kotlin.math.max


class TarifFragment : Fragment() {

    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.kaydetButton.setOnClickListener {
            kaydet(it)
        }
        binding.gorselSec.setOnClickListener {
            gorselSec(it)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun kaydet(view: View) {
        //SQLite kayit
        val yemekIsmi = binding.yemekIsmiText.text.toString()
        val yemekMalzemeleri = binding.yemekMalzemesiText.text.toString()

        if (secilenBitmap != null){
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler(id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")
                    val sqlString = "INSERT INTO yemekler (yemekismi, yemekmalzemesi, gorsel) VALUES (?,?,?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi)
                    statement.bindString(2,yemekMalzemeleri)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()
                }
                val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
                Navigation.findNavController(view).navigate(action)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

    }

    fun gorselSec(view: View) {

        //Galeri erisim izni alma
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //izin verilmedi,izin istememiz gerekiyor
                ActivityCompat.requestPermissions(it,arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    1)


            } else {
                //izin zaten verilmis,tekrar istemeden galeriye git
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izin verildi
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilenGorsel = data.data
        }
        try {
            context?.let {
                if (secilenGorsel != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                        binding.gorselSec.setImageBitmap(secilenBitmap)
                    } else {
                        secilenBitmap =
                            MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                        binding.gorselSec.setImageBitmap(secilenBitmap)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    fun kucukBitmapOlustur(kullanicininSectigiBitmap : Bitmap ,maximumBoyut : Int) : Bitmap{
        var width = kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height
        val bitmapOrani : Double = width.toDouble() / height.toDouble()
        if (bitmapOrani>1){
            //yatay gorsel
            width = maximumBoyut
            val kisaltilmisHeight = width/bitmapOrani
            height = kisaltilmisHeight.toInt()
        }else{
            //dikey gorsel
            height = maximumBoyut
            val kisaltilmisWidth = height*bitmapOrani
            width = kisaltilmisWidth.toInt()
        }
        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
    }




}