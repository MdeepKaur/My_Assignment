package com.android_examples.productlistingappkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class Screen3 : AppCompatActivity() {

    var lv1: ListView? = null
    private var myad: myadapter? = null
    var totalTV: TextView? = null
    var netPriceTV: TextView? = null
    lateinit var prefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen3)
        totalTV = findViewById<TextView>(R.id.totalTV)

        netPriceTV = findViewById<TextView>(R.id.netPriceTV)
        go()
    }

    public override fun onRestart() {
        super.onRestart()
        go()
    }

    public override fun onResume() {
        super.onResume()
        go()
    }

    public override fun onStart() {
        super.onStart()
        go()
    }

    fun go() {
        prefs = getSharedPreferences("mypreference", MODE_PRIVATE)
        val json = prefs.getString("cart", null)
        val type = object : TypeToken<ArrayList<ProductData?>?>() {}.type
        Global.al = gson.fromJson<Any>(json, type) as java.util.ArrayList<ProductData>
        if (Global.al == null) {
            Global.al = ArrayList<ProductData>()
        }
        myad = myadapter(this)
        lv1 = findViewById(R.id.lv1)
        if (Global.al == null) {
            Toast.makeText(this, "Please select products to continue ", Toast.LENGTH_SHORT).show()
        } else if (Global.al.size == 0) {
            this.finish()
        } else {
            lv1!!.adapter = myad

            myad!!.notifyDataSetChanged()
        }
        setTotalAndDiscount()
    }

    private fun setTotalAndDiscount() {
        var total: Long = 0
        var discount = 0.0
        var net = 0.0
        for (p in Global.al) {
            total += p.price
            discount += p.discountPercentage * p.price / 100.0
        }
        net = total - discount
        totalTV!!.text = "Total: ₹" + total
        netPriceTV!!.text = "Offer Price: ₹" + net
    }

    internal inner class myadapter(
        private val context: Context
    ) : BaseAdapter() {

        private var layoutInflater: LayoutInflater? = null

        override fun getCount(): Int {
            return Global.al.size
        }

        override fun getItem(i: Int): Any {
            return Global.al[i]
        }

        override fun getItemId(i: Int): Long {
            return (i * 100).toLong()
        }

        override fun getView(i: Int, customview: View?, viewGroup: ViewGroup): View? {
            //Load (Inflate) XML file
            var customview = customview

            if (layoutInflater == null) {
                layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }
            // if custom view is null.
            // If it is null we are initializing it.
            if (customview == null) {
                //  pass the layout file
                // which we have to inflate for each item of list view.
                customview = layoutInflater!!.inflate(R.layout.selected_list_design, null)
            }

            //pick ith object
            val pData = Global.al[i]

            //extract values and fit in Components of  design
            val title = customview!!.findViewById<TextView>(R.id.title)
            val description = customview!!.findViewById<TextView>(R.id.description)
            val price = customview!!.findViewById<TextView>(R.id.price)
            val discount = customview!!.findViewById<TextView>(R.id.discount)
            val stock = customview!!.findViewById<TextView>(R.id.stock)
            val img = customview!!.findViewById<ImageView>(R.id.img)
            val btn = customview!!.findViewById<Button>(R.id.deleteBtn)

            btn.setOnClickListener {
                removeFromAl(i)
                setTotalAndDiscount()
            }
            title!!.text = pData.title
            description!!.text = pData.description
            price!!.text = "₹" + pData.price.toString()
            discount!!.text = pData.discountPercentage.toString() + "% OFF"

            if (pData.stock > 0) {
                stock!!.text = pData.stock.toString() + " items left in stock"

            } else {
                stock!!.text = "Empty Stock"
            }

            Picasso.get().load(pData.thumbnail).into(img)
            return customview
        }
    }

    fun removeFromAl(i: Int) {
        Global.al.removeAt(i)
        val json = gson.toJson(Global.al)
        editor = prefs!!.edit()
        editor.putString("cart", json)
        editor.apply()
        myad!!.notifyDataSetChanged()
    }
}