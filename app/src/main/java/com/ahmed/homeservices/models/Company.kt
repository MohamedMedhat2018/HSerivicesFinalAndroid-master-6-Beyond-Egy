package com.ahmed.homeservices.models

import com.vincent.filepicker.filter.entity.NormalFile
import java.io.Serializable

class Company : Serializable {


//    var os = "Android"

//    var companyPasswordConfirm = ""

    //    var companyLocationMain: Location? = null

    //    var locations: MutableList<LocationCompany>? = null
//    var location: LocationCompany? = null

    var categories: MutableList<Category>? = null


    //    var rate = ""
//    val city = ""

    var companyLocation: LocationCompany? = null

    var companyAttachment = ""

    var companyCategoryId: MutableList<String>? = mutableListOf()

    var companyEmail = ""

    var companyId = ""

    var companyLocationAddress = ""

    var companyNameInArabic = ""

    var companyNameInEnglish = ""

    var companyPassword = ""

    var companyPhone: String = ""

    var companyPhoto: String = ""

    var companyStatusActivation = false

    //companyType = ""  remove it

    var createDate = ""

    var login: Boolean = false

    var messageToken = ""

//    var normalPdfFile: NormalFile? = null

    constructor()
}