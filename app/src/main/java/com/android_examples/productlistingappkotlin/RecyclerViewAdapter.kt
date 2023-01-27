package com.android_examples.productlistingappkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(private val arr: List<ProductData>, var myContext: Context) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    var al: List<ProductData>?
    var prefs: SharedPreferences
    var editor: SharedPreferences.Editor
    var gson: Gson

    init {
        al = ArrayList()
        prefs = myContext.applicationContext.getSharedPreferences("mypreference", Context.MODE_PRIVATE)
        editor = prefs.edit()
        gson = Gson()
        val json = prefs.getString("cart", null)
        val type = object : TypeToken<ArrayList<ProductData?>?>() {}.type
        al = gson.fromJson(json, type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        val view = LayoutInflater.from(myContext).inflate(R.layout.card_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val productData = arr[position]
        if (al == null) {

            holder.title.text=productData.title
            holder.brand.text="Brand: "+productData.brand
            holder.price.text= "₹"+productData.price.toString()
            holder.discount.text=productData.discountPercentage.toString()+"% OFF"
            Picasso.get().load(productData.thumbnail).into(holder.thumbnail)
            holder.btnYes.setOnClickListener {
                var toast = Toast.makeText(myContext, "Product Added", Toast.LENGTH_SHORT)
                toast.show()
                holder.btnYes.visibility = View.GONE
                holder.btnNo.visibility = View.VISIBLE
                Global.al.add(productData)
                val json = gson.toJson(Global.al)
                editor.putString("cart", json)
                editor.apply()
                notifyDataSetChanged()
                notifyItemChanged(position)
            }
            holder.btnNo.setOnClickListener {
                var toast = Toast.makeText(myContext, "Product Removed", Toast.LENGTH_SHORT)
                toast.show()
                holder.btnYes.visibility = View.VISIBLE
                holder.btnNo.visibility = View.GONE
                Global.al.remove(productData)
                val json = gson.toJson(Global.al)
                editor.putString("cart", json)
                editor.apply()
                notifyDataSetChanged()
            }
        } else {
            if (checkIfDataExists(productData)) {
                holder.btnYes.visibility = View.GONE
                holder.btnNo.visibility = View.VISIBLE
            } else {
                holder.btnYes.visibility = View.VISIBLE
                holder.btnNo.visibility = View.GONE
            }

            holder.title.text=productData.title
            holder.brand.text="Brand: "+productData.brand
            holder.price.text= "₹"+productData.price.toString()
            holder.discount.text=productData.discountPercentage.toString()+"% OFF"
            Picasso.get().load(productData.thumbnail).into(holder.thumbnail)
            holder.btnYes.setOnClickListener {
                var toast = Toast.makeText(myContext, "Product Added", Toast.LENGTH_SHORT)
                toast.show()
                holder.btnYes.visibility = View.GONE
                holder.btnNo.visibility = View.VISIBLE
                Global.al.add(productData)
                val json = gson.toJson(Global.al)
                editor.putString("cart", json)
                editor.apply()
                notifyDataSetChanged()
            }
            holder.btnNo.setOnClickListener {

                var toast = Toast.makeText(myContext, "Product Removed", Toast.LENGTH_SHORT)
                toast.show()
                holder.btnYes.visibility = View.VISIBLE
                holder.btnNo.visibility = View.GONE
                removeFromAl(productData.id)

                notifyDataSetChanged()
            }
        }
    }

    private fun checkIfDataExists(pd: ProductData): Boolean {
        var f = false
        for (p in al!!) {
            if (p.id ==pd.id) {
                f = true
                break
            }
        }
        return if (f) {
            true
        } else {
            false
        }
    }

    fun removeFromAl(id: Int) {
        val tempAl = ArrayList<ProductData>()
        for (p in Global.al) {
            if (p.id  !== id) {
                tempAl.add(p)
            }
        }
        Global.al = tempAl
        val json = gson.toJson(Global.al)
        editor.putString("cart", json)
        editor.apply()
    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return arr.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView=itemView.findViewById(R.id.title)
        val brand: TextView=itemView.findViewById(R.id.brand)
        val price: TextView=itemView.findViewById(R.id.price)
        val discount: TextView=itemView.findViewById(R.id.discount)
        val thumbnail: ImageView=itemView.findViewById(R.id.thumbnail)
        val btnYes: Button=itemView.findViewById(R.id.btnYes)
        val btnNo: Button=itemView.findViewById(R.id.btnNo)

    }
}



