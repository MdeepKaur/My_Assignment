package com.android_examples.productlistingappkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.newFixedThreadPoolContext
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    var myRvAdapter: RecyclerViewAdapter? = null
    private lateinit var productDataArray: Array<ProductData?>
    private val arr: ArrayList<ProductData>? = null
    var al: List<ProductData>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bt1 = findViewById<Button>(R.id.bt1)
        bt1.setOnClickListener() {
            val prefs = applicationContext.getSharedPreferences("mypreference", MODE_PRIVATE)
            val gson = Gson()
            val json = prefs.getString("cart", null)
            val type = object : TypeToken<ArrayList<ProductData?>?>() {}.type
            val al = gson.fromJson<ArrayList<ProductData>>(json, type)
            if (al == null) {
                Global.al = ArrayList<ProductData>()
                Toast.makeText(this, "Please select products to continue ", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val intent = Intent(this, Screen3::class.java)
                startActivity(intent)
            }
        }

        recyclerView = findViewById(R.id.productRV)
        productDataArray = arrayOfNulls(0)
        al = ArrayList()

        // added data to array list
        val url = "https://dummyjson.com/products"
        val request = StringRequest(Request.Method.GET, url, { response ->
            var response = response
            response = response.trim { it <= ' ' }
            val jsonObject: JSONObject
            try {
                jsonObject = JSONObject(response)
                val jarray = jsonObject.getJSONArray("products")
                val gson = Gson()
                productDataArray = gson.fromJson<Array<ProductData?>>(
                    jarray.toString(),
                    Array<ProductData>::class.java
                )
                al = ArrayList(Arrays.asList(*productDataArray))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            commonLogic()
        }
        ) { error -> Toast.makeText(this@MainActivity, "Error: $error", Toast.LENGTH_LONG).show() }
        Volley.newRequestQueue(this).add(request)
    }

    public override fun onRestart() {
        super.onRestart()
        commonLogic()

    }

    public override fun onResume() {
        super.onResume()
        commonLogic()
    }

    fun commonLogic() {
        // add data from arraylist to adapter class.
        myRvAdapter = RecyclerViewAdapter(al!!, this@MainActivity)

        val layoutManager = GridLayoutManager(this@MainActivity, 2)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = myRvAdapter
        myRvAdapter!!.notifyDataSetChanged()
    }

}
