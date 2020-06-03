package com.ahmed.homeservices.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.homeservices.R
import com.ahmed.homeservices.adapters.multi_select.SelectCategoriesAdapter
import com.ahmed.homeservices.adapters.multi_select.SelectCitiesAdapter
import com.ahmed.homeservices.constants.Constants
import com.ahmed.homeservices.interfaces.multi_select.category.OnCategorySelected
import com.ahmed.homeservices.interfaces.multi_select.category.OnCategorySelectedFinished
import com.ahmed.homeservices.interfaces.multi_select.city.OnCitySelected
import com.ahmed.homeservices.interfaces.multi_select.city.OnCitySelectedFinished
import com.ahmed.homeservices.models.Category
import com.ahmed.homeservices.models.City
import kotlinx.android.synthetic.main.layout_category_item.view.*
import kotlinx.android.synthetic.main.layout_dialog_categories.*
import kotlinx.android.synthetic.main.layout_dialog_cities.*
import kotlinx.android.synthetic.main.layout_okay_cancel.view.*


//class MultiSelectDialog() : DialogFragment() {
class MultiSelectDialog : Dialog {

    private var ctxt: Context? = null
    //    private lateinit var listCities: MutableList<String>
    private lateinit var listCities: MutableList<City>
    private lateinit var listCategories: MutableList<Category>
    var cityOrCategory: String = Constants.CITIES
    lateinit var onCitySelectedFinished: OnCitySelectedFinished
    lateinit var onCategorySelectedFinished: OnCategorySelectedFinished
    //empty list
//    private var selectedCities: MutableList<String> = mutableListOf()
    private var selectedCities: MutableList<City> = mutableListOf()
    private var selectedCategories: MutableList<Category> = mutableListOf()
    var selectedCitiesStr: String = ""
    var selectedCategoryStr: String = ""

    constructor(context: Context, cities: MutableList<City>, onCitySelectedFinished: OnCitySelectedFinished) : super(context) {
        ctxt = context
        listCities = cities
        this.onCitySelectedFinished = onCitySelectedFinished

    }

    constructor(context: Context, categories: MutableList<Category>, citiesOrCategories: String,
                onCategorySelectedFinished: OnCategorySelectedFinished) : super(context) {
        ctxt = context
        listCategories = categories
        cityOrCategory = citiesOrCategories
        this.onCategorySelectedFinished = onCategorySelectedFinished

    }

    companion object {
        //        fun newIntance(activity: Activity, listCities: MutableList<String>) = MultiSelectDialog()
        //        val TAG = MultiSelectDialog::javaClass.name
        val TAG = MultiSelectDialog::class.java.simpleName
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)


//        getWindow()?.setLayout(((getWidth(context) / 100) * 90), LinearLayout.LayoutParams.MATCH_PARENT)
//        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
//        window?.setGravity(Gravity.END)
//        window?.setGravity(Gravity.CENTER)

        var vRootCities: View? = null
        var vRootCategories: View? = null


        when (cityOrCategory) {
            Constants.CITIES -> {
                vRootCities = LayoutInflater.from(ctxt)
                        .inflate(R.layout.layout_dialog_cities, null, false)
//                setContentView(R.layout.layout_dialog_cities)
                setContentView(vRootCities)
                onViewCreatedCities(vRootCities)
            }
            else -> {
                vRootCategories = LayoutInflater.from(ctxt)
                        .inflate(R.layout.layout_dialog_categories, null, false)
//                setContentView(R.layout.layout_dialog_categories)
                setContentView(vRootCategories)
                onViewCreatedCategories(vRootCategories)

            }
        }
    }

    private fun onViewCreatedCities(vRootCities: View) {

//                for (x in 0 until listCities.size) {
//                    Log.e(TAG, "cities ${listCities[x]}")
//                }

        setRecyclerViewAdapterCities()

        vRootCities.btnOkay.setOnClickListener {
            //            for (c in 0 until selectedCities.size) {
////                if (c == selectedCities.size - 1) {
////                    selectedCitiesStr = selectedCitiesStr.plus(selectedCities[c])
////                } else {
////                    selectedCitiesStr = selectedCitiesStr.plus(selectedCities[c]).plus(", ")
////                }
//                val city = selectedCities.get(c);
//
//            }
//            Log.e(TAG, "selectedCitiesStr  ".plus(selectedCitiesStr))
//            txtMultiCityCompany.text = "selectedCitiesStr"
//            txtMultiCityCompany. visibility = View.VISIBLE
//            onCitySelectedFinished.onCitySelectedFinished(selectedCitiesStr)


            onCitySelectedFinished.onCitySelectedFinished(selectedCities)
            dismiss()
        }

        vRootCities.btnCancel.setOnClickListener {
            onCitySelectedFinished.onCitySelectedCancelled(null)
            dismiss()
        }

    }

    private fun onViewCreatedCategories(vRootCategories: View) {

//                for (x in 0 until listCities.size) {
//                    Log.e(TAG, "cities ${listCities[x]}")
//                }

        setRecyclerViewAdapterCategories()

        Log.e(TAG, "CLICK okay1 ${selectedCategories.size}")

        vRootCategories.btnOkay.setOnClickListener {
            //            Log.e(TAG, "CLICK okay2 ${selectedCategories.size}")
//            for (c in 0 until selectedCategories.size) {
//                if (c == selectedCategories.size - 1) {
//                    selectedCategoryStr = selectedCategoryStr.plus(selectedCategories[c].categoryName)
//                } else {
//                    selectedCategoryStr = selectedCategoryStr.plus(selectedCategories[c].categoryName).plus(", ")
//                }
//            }
//            Log.e(TAG, "selectedCategoryStr3  ".plus(selectedCategoryStr))
//            txtMultiCityCompany.text = "selectedCitiesStr"
//            txtMultiCityCompany. visibility = View.VISIBLE
//            Log.e(TAG, "CLICK okay4 ${selectedCategoryStr}")

//            onCategorySelectedFinished.onCategorySelectedFinished(selectedCategoryStr)


            onCategorySelectedFinished.onCategorySelectedFinished(selectedCategories)

            dismiss()
        }

        vRootCategories.btnCancel.setOnClickListener {
            onCategorySelectedFinished.onCategorySelectedCancelled(null)
            dismiss()
        }

    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        selectedCitiesStr = ""
        selectedCities.clear()

        selectedCategoryStr = ""
        selectedCategories.clear()


    }

    private fun setRecyclerViewAdapterCategories() {
        rvMultiSelectCategories.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rvMultiSelectCategories.adapter = SelectCategoriesAdapter(listCategories, OnCategorySelected { v, category, pos ->
            Log.e(TAG, "CLICK Contain or not Categories ${category.categoryName}")
            if (selectedCategories.contains(category)) {
                Log.e(TAG, "CLICK Contain or not Categories1 ${category.categoryName}  and ${selectedCategories.size}")
//                for (x in 0 until selectedCities.size) {
                val iterator: MutableIterator<*> = selectedCategories.iterator()
                while (iterator.hasNext()) {
                    val cat = iterator.next()
                    Log.e(TAG, "CLICK remove1 ${selectedCategories.size} and $cat. and ${category}")
                    if (cat == category) {
                        Log.e(TAG, "CLICK remove2 ${selectedCategories.size} and $cat and ${category}")
                        iterator.remove()
//                        v.llCategoryHolder.setBackgroundColor(getColor(context, R.color.white))
                        Log.e(TAG, "CLICK remove3 ${selectedCategories.size} and $cat and ${category}")

                        v.tvTitle.setBackgroundColor(getColor(context, R.color.white))
                    }
                }

//                for (string in  selectedCities) {
////                    Log.e(TAG, "CLICK Contain ${selectedCities.size} and x ${listCities[x]} and $x")
//                    if (string == city) {
//                        selectedCities.remove(city)
////                        selectedCities.removeAt(pos)
//                        v.findViewById<TextView>(R.id.tvCity).setBackgroundColor(getColor(context, R.color.white))
////                        Log.e(TAG, "CLICK $x")
//                    }
//                }
            } else {
                selectedCategories.add(category)
//                v.llCategoryHolder.setBackgroundColor(getColor(context, R.color.LightGrey))
                v.tvTitle.setBackgroundColor(getColor(context, R.color.LightGrey))
                Log.e(TAG, "CLICK add categories ${selectedCategories.size} and  ")
            }
        })
    }

    private fun setRecyclerViewAdapterCities() {
        rvMultiSelectCities.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rvMultiSelectCities.adapter = SelectCitiesAdapter(listCities, OnCitySelected { v, city, pos ->
            if (selectedCities.contains(city)) {
//                Log.e(TAG, "CLICK Contain or not ${selectedCities} and $city")
//                for (x in 0 until selectedCities.size) {
                val iterator: MutableIterator<*> = selectedCities.iterator()
                while (iterator.hasNext()) {
                    val c = iterator.next()
                    if (c == city) {
                        iterator.remove()
                        v.findViewById<TextView>(R.id.tvCity).setBackgroundColor(getColor(context, R.color.white))
                    }
                }

//                for (string in  selectedCities) {
////                    Log.e(TAG, "CLICK Contain ${selectedCities.size} and x ${listCities[x]} and $x")
//                    if (string == city) {
//                        selectedCities.remove(city)
////                        selectedCities.removeAt(pos)
//                        v.findViewById<TextView>(R.id.tvCity).setBackgroundColor(getColor(context, R.color.white))
////                        Log.e(TAG, "CLICK $x")
//                    }
//                }
            } else {
                selectedCities.add(city)
                v.findViewById<TextView>(R.id.tvCity).setBackgroundColor(getColor(context, R.color.LightGrey))
                Log.e(TAG, "CLICK 2 ${selectedCities.size} and $city  ")
            }
        })
    }


    fun getWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowmanager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowmanager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }


//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.layout_cities, container, false)
////        return super.onCreateView(inflater, container, savedInstanceState)
//        return view
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//    }


}