package com.ahmed.homeservices.activites.cutomer_or_worker.register

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ahmed.homeservices.R
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity
import com.ahmed.homeservices.activites.sms.EnterSmsCodeActivity
import com.ahmed.homeservices.constants.Constants
import com.ahmed.homeservices.dialogs.MultiSelectDialog
import com.ahmed.homeservices.easy_image.SeyanahEasyImage
import com.ahmed.homeservices.fire_utils.RefBase
import com.ahmed.homeservices.interfaces.multi_select.category.OnCategorySelectedFinished
import com.ahmed.homeservices.interfaces.multi_select.city.OnCitySelectedFinished
import com.ahmed.homeservices.models.*
import com.ahmed.homeservices.pdf_utils.PdfUtils
import com.ahmed.homeservices.phone_utils.PhoneUtils.startPhoneNumberVerificationCompany
import com.ahmed.homeservices.snekers.Snekers
import com.ahmed.homeservices.utils.FileUtils
import com.ahmed.homeservices.utils.PhoneUtils.checkIfPhoneExistInSpecificRef
import com.ahmed.homeservices.utils.Utils.*
import com.esafirm.imagepicker.features.ImagePicker
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.reflect.TypeToken
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vincent.filepicker.Constant
import com.vincent.filepicker.filter.entity.NormalFile
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_company_register.*
import kotlinx.android.synthetic.main.layout_select_cat2.*
import kotlinx.android.synthetic.main.layout_select_location_company.*
import kotlinx.android.synthetic.main.layout_upload_pdf.*
import kotlinx.android.synthetic.main.user_photo.*
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CompanyRegisterActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    //use it to create members inside it then u can call any member of that class without create an object of that class (mean that members become static)
    companion object {
        val TAG = CompanyRegisterActivity::class.java.simpleName

        //        fun newInstance() = CompanyRegisterActivity
        const val RC_CAMERA_AND_STORAGE = 100
        const val REQUEST_CODE_PICK_FILE = 293
        private val listCountries: MutableList<String> = ArrayList()
        private val listCountriesObj: MutableList<Country> = ArrayList()
        val PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var bsDialog: BottomSheetDialog? = null
    }

    var fileImage: Uri? = null
    private var flagCompanyLogo = false
    private val listKeyCities: MutableList<String> = java.util.ArrayList()

    //    private val listCities: MutableList<String> = java.util.ArrayList()
    private var listCities: MutableList<City> = java.util.ArrayList()
    private val listCategories: MutableList<Category> = java.util.ArrayList()
    private val listKeyCategories: MutableList<String> = java.util.ArrayList()

    private var spotsDialog: AlertDialog? = null
    private var country: Country? = null
    private var posSpinnerCountry: Int = 0
    private var posSpinnerCity: Int = 0
    private var countryId: String? = null
    private var filePdf: Uri? = null
    private val categories: MutableList<Category> = java.util.ArrayList()
    var autoRetrieval = false
    var firebaseAuth: FirebaseAuth? = null
    var gson = Gson()

    //    var uri: Uri? = null
    private var flagCompanyPdfFile = false

    val company: Company = Company()

    var dlgProgress: AlertDialog? = null

    var phoneMaterials = PhoneMaterials()
    private var mVerificationId: String? = null

    private var listSelectedCities: MutableList<City> = mutableListOf()
    private var listSelectedCategory: MutableList<Category> = mutableListOf()

    private var mResendToken: ForceResendingToken? = null

    private var mCallbacks: OnVerificationStateChangedCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen(this)
        setContentView(R.layout.activity_company_register)
        initAuth()
//        accessMultiImageView()


    }

//    private fun accessMultiImageView() {
//
//        multiImageView.shape = MultiImageView.Shape.CIRCLE
//        multiImageView.addImage(BitmapFactory.decodeResource(resources, R.drawable.pdf));
//        multiImageView.addImage(BitmapFactory.decodeResource(resources, R.drawable.from_gallery));
//        multiImageView.rectCorners = (50);
//
//    }

    private fun initAuth() {

//        progressDialog = ProgressDialog(this)
//        progressDialog!!.setMessage(getString(R.string.please_wait))
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.setCanceledOnTouchOutside(false)
//        progressDialog!!.show()

        firebaseAuth = FirebaseAuth.getInstance()


//        if (dlgProgress != null && !dlgProgress.isShowing()) {
//            dlgProgress.setMessage("Registering")
//            dlgProgress.show()
//        }

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sms number format is not valid.
                //Called when some error occurred such as failing of sending SMS or Number format exception
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sms number format is not valid.
                Log.w(TAG, "onVerificationFailed", p0)
                // btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                //                btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                if (p0 is FirebaseAuthInvalidCredentialsException) { // Invalid request
                    etCompanyPhone.error = Constants.INVALID_PHONE
                    Toasty.error(applicationContext, Constants.INVALID_PHONE).show()
                    //???
                    startWobble(applicationContext, etCompanyPhone)
                    scrollToView(scrollView, etCompanyPhone)
                    //dlgProgress!!.dismiss()
                    progressDialog?.dismiss()
                } else if (p0 is FirebaseTooManyRequestsException) { // Quota exceeded
                    // The SMS quota for the project has been exceeded (u send a lot of codes in short time )
                    // Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                    // Snackbar.LENGTH_SHORT).show();
                    Toasty.error(applicationContext,
                            "Quota exceeded. u send a lot of codes in short time").show()
                    // initCountDownTimerResendCode();
                    dlgProgress!!.dismiss()
                    //                    btnRegisterPhoneNumber.setEnabled(false);
                    // countDownTimer.start();
                }

                // Show a message and update the UI
                // updateUI(STATE_VERIFY_FAILED);
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) { // The SMS verification code has been sent to the provided sms number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onCodeSent:$verificationId")
                Log.e(TAG, "onCodeSent2:$token")
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
                updateUI(Constants.STATE_CODE_SENT)
            }

        }

    }

    private fun updateUI(uiState: Int) {
        updateUI(uiState, firebaseAuth!!.currentUser, null)
    }

    private fun updateUI(uiState: Int, user: FirebaseUser?, cred: PhoneAuthCredential?) {
        if (dlgProgress != null) {
            dlgProgress!!.dismiss()
        }
        when (uiState) {
            Constants.STATE_INITIALIZED -> Log.e(TAG, "STATE_INITIALIZED")
            Constants.STATE_CODE_SENT -> {
                Log.e(TAG, "STATE_CODE_SENT")
                //                Prefs.edit().remove(Constants.BOTTOM_SHEET_IS_SHOWN).apply();
                codeSentSuccess()
            }
            Constants.STATE_VERIFY_FAILED -> Log.e(TAG, "STATE_VERIFY_FAILED")
            Constants.STATE_VERIFY_SUCCESS -> Log.e(TAG, "")
            Constants.STATE_SIGNIN_FAILED -> Log.e(TAG, "STATE_SIGNING_FAILED")
            Constants.STATE_SIGNIN_SUCCESS -> Log.e(TAG, "STATE_SIGNING_SUCCESS")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.e(TAG, "signInWithPhoneAuthCredential: ")
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "signInWithPhoneAuthCredential:    isSuccessful ")
                        autoRetrieval = true
                        progressDialog!!.dismiss()
                        codeSentSuccess()
                    } else {
                        //                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        //                            progressBar.setVisibility(View.GONE);
                    }
                }
    }

    private fun codeSentSuccess() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val intent: Intent
        if (firebaseUser == null || !Prefs.contains(Constants.FIREBASE_UID)) { // Signed out
            Log.e(TAG, "codeSentSuccess: ")

            progressDialog?.dismiss()

            phoneMaterials.setmResendToken(mResendToken)
            phoneMaterials.verificationId = mVerificationId
            phoneMaterials.phoneNumber = etCompanyPhone.text.toString()
            Log.e(TAG, "codeSentSuccess: " + phoneMaterials.getmResendToken() + phoneMaterials.phoneNumber + phoneMaterials.verificationId)
            Prefs.putString(Constants.PHONE_MATERIALS, gson.toJson(phoneMaterials, object : TypeToken<PhoneMaterials?>() {}.type))
            Prefs.putString(Constants.COMPANY, gson.toJson(company, object : TypeToken<Company?>() {}.type))

            Log.e(TAG, "codeSentSuccess: " + Prefs.getString(Constants.COMPANY, ""))
            intent = Intent(this, EnterSmsCodeActivity::class.java)
            intent.putExtra(Constants.COMPANY_PHOTO_URI, fileImage.toString())
            intent.putExtra(Constants.COMPANY_PDF_FILE, filePdf.toString())
//            intent.putExtra(Constants.COMPANY_PDF_NORMAL_FILE, gson.toJson(normalFile, NormalFile::class.java))
            intent.putExtra(Constants.COMPANY_PDF_NORMAL_FILE, fileImage.toString())
            Log.e(TAG, "Image " + fileImage.toString() + " and pdf " + filePdf.toString() + " and normal ")
            if (autoRetrieval) {
                intent.putExtra(Constants.AUTO_SMS_RETRIEVAL, true)
            }

            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        } else { // Signed in
            intent = Intent(applicationContext, MainActivity::class.java)
            //          intent.putExtra(Constants.USER_TYPE, Constants.DRIVERS);
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setMultiSelectSpinnerAdapterLocation()
        setOnClicks()
        setOnItemClicks()
        addDefaultItemsToSpinnerCountries()
        loadAllCountries()
        loadAllCatsIntoRecyclerView()

    }

    private fun loadAllCatsIntoRecyclerView() {
        RefBase.refCategories()
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            listCategories.clear()
                            for (snap in dataSnapshot.children) {
                                val map = snap.value as HashMap<String, Any>?
                                val category = Category()
                                category.categoryName = map!!["categoryName"].toString()
                                category.categoryNameArabic = map["categoryNameArabic"].toString()
                                category.categoryId = snap.key
                                category.categoryIcon = map["categoryIcon"].toString()
                                listCategories.add(category)
                            }
//                            listKeyCategories
//                            showMultiSelectDialogCategories()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    var normalFile: NormalFile? = null

    var isImageOrPdf: String = ""
    var ProfileOrPdf: String = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //for Image
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { // Get a list of picked images
            val images = ImagePicker.getImages(data)
            // or get a single image only
            val image = ImagePicker.getFirstImageOrNull(data)
            fileImage = Uri.fromFile(File(image.path))
            if (ProfileOrPdf == "Pdf") {
                if (isImageOrPdf == Constants.TYPE_CAM) {
                    filePdf = fileImage
                    ivPdfOrImage.visibility = View.VISIBLE
                    pdfViewer.visibility = View.GONE
                    llPdfIgmHolder.visibility = View.VISIBLE
                    Picasso.get().load(fileImage).into(ivPdfOrImage)
                    Log.e(TAG, "photo from camera aa ${fileImage.toString()} ")

                }
            } else if (ProfileOrPdf == "Profile") {
                Picasso.get().load(fileImage)
                        .into(ivUserPhoto, object : Callback {
                            override fun onSuccess() {
                                flagCompanyLogo = true
//                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "photo from camera x ${fileImage.toString()} ")
                            }

                            override fun onError(e: Exception) {
                                Log.e(TAG, "photo from camera error ${e.message} ")

                            }
                        })
            }

        }

        Log.e(TAG, "photo from camera1 ")
        SeyanahEasyImage.easyImage(this).handleActivityResult(requestCode, resultCode, data, this,
                object : EasyImage.Callbacks {
                    override fun onCanceled(source: MediaSource) {

                    }

                    override fun onImagePickerError(error: Throwable, source: MediaSource) {

                    }

                    //for Gallary
                    override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                        Log.e(TAG, "photo from camera2 ")
                        fileImage = Uri.fromFile(imageFiles[0].file)
                        if (ProfileOrPdf == "Pdf") {
                            if (isImageOrPdf == Constants.TYPE_GALLERY) {
                                filePdf = fileImage
                                ivPdfOrImage.visibility = View.VISIBLE
                                pdfViewer.visibility = View.GONE
                                llPdfIgmHolder.visibility = View.VISIBLE
                                Picasso.get().load(fileImage).into(ivPdfOrImage)
                                Log.e(TAG, "photo from Galary ${fileImage.toString()} ")

                            }
                        } else if (ProfileOrPdf == "Profile") {
                            Picasso.get().load(fileImage)
                                    .into(ivUserPhoto, object : Callback {
                                        override fun onSuccess() {
                                            flagCompanyLogo = true
//                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "photo from camera ${fileImage.toString()} ")
                                        }

                                        override fun onError(e: Exception) {
                                            Log.e(TAG, "photo from camera error ${e.message} ")

                                        }
                                    })
                        }

                        //                uploadPhoto(uri);


                    }

                })
        //for pdf
        if (Constant.REQUEST_CODE_PICK_FILE == requestCode) {
            if (resultCode == RESULT_OK) {
                val list: MutableList<NormalFile> = data!!.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE)
                Log.e(TAG, "onActivityResult: rocky ")
                if (list != null && list.isNotEmpty()) {
                    normalFile = list[0]
                    val path: String = list[0].path
                    Log.e(TAG, "onActivityResult: " + normalFile!!.path)
                    Log.e(TAG, "onActivityResult: " + normalFile!!.size)
                    Log.e(TAG, "onActivityResult: " + normalFile!!.name)
                    Log.e(TAG, "onActivityResult: " + normalFile!!.date)
                    Log.e(TAG, "onActivityResult: " + normalFile!!.bucketName)
                    Log.e(TAG, "onActivityResult: " + normalFile!!.mimeType)
                    val uri = Uri.parse(path)
                    val uriConcat: Uri? = myUri(uri, path)
                    filePdf = uriConcat
//                    filePdf = uri
//                        PdfUtils.pdfUploadFile(this, uriConcat, new PdfUtils.OnPdfUploadingResponse() {
//                            @Override
//                            public void onUploadSuccess(String success) {
//                                Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//                            }

//                            @Override
//                            public void onUploadError(Exception e, String err) {
//                                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
//
//                            }
//                        });


                    pdfViewer.fromUri(uriConcat)
//                    pdfViewer.fromUri(uri)
                            .defaultPage(0)
                            .onPageChange(OnPageChangeListener { page, pageCount -> })
                            .enableAnnotationRendering(true)
                            .onLoad(OnLoadCompleteListener {
                                Log.e(TAG, "loadComplete: ")
                                llPdfIgmHolder.visibility = View.VISIBLE
                                ivPdfOrImage.visibility = View.VISIBLE
                                ivPdfOrImage.setImageResource(R.drawable.pdf_icon)
                                pdfViewer.visibility = View.GONE
                                flagCompanyPdfFile = true
                            })
                            .scrollHandle(DefaultScrollHandle(applicationContext))
                            .spacing(10) // in dp
                            .onPageError(OnPageErrorListener { page, t -> Log.e(TAG, "onPageError: ") })
                            .load()

                    Log.e(TAG, "filePdf is ${filePdf.toString()}")

                    // Get length of file in bytes
                    val fileSizeInBytes = normalFile!!.size
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    val fileSizeInKB = fileSizeInBytes / 1024
                    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    val fileSizeInMB = fileSizeInKB / 1024
                    Log.e(TAG, "onActivityResult: $fileSizeInBytes")
                    Log.e(TAG, "onActivityResult: $fileSizeInKB")
                    Log.e(TAG, "onActivityResult: $fileSizeInMB")

//                    if (fileSizeInMB > 5) {
//                        Toast.makeText(this, "This file is more than 5 MB", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "This file is less than 5 MB", Toast.LENGTH_SHORT).show()
//                    }


                }
            } else {
                Log.e(TAG, "Process cancelled")
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

    }

    private var ref: StorageReference? = null
    private val storageReference: StorageReference? = null
    private var progressDialog: ProgressDialog? = null

    @SuppressLint("CheckResult")
    private fun uploadPhoto(filePath: Uri?, pdfOrImage: Boolean) {

        if (filePath != null) {
            var file: File? = null
            try {
                file = FileUtils.from(applicationContext, filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val iStream = arrayOf<InputStream?>(null)
            val inputData = arrayOf<ByteArray?>(null)
            assert(file != null)
            Compressor(applicationContext)
                    .compressToFileAsFlowable(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file ->
                        try {
                            iStream[0] = applicationContext.contentResolver.openInputStream(Uri.fromFile(file))
                            assert(iStream[0] != null)
                            inputData[0] = getBytes(iStream[0])
                            if (inputData[0] != null) //ref.putFile(filePath)
                                if (pdfOrImage) {
                                    ref = storageReference?.child("images/" + UUID.randomUUID().toString())
                                } else {
                                    ref = storageReference?.child("company_pdf_files/" + UUID.randomUUID().toString())
                                }
                            ref?.putBytes(inputData[0]!!)
                                    ?.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                                        //                                                progressDialog.dismiss();
                                        ref?.downloadUrl!!.addOnSuccessListener { uri ->
                                            Log.e(TAG, "uploaded:2 ")
                                            val url = uri.toString()
                                            if (pdfOrImage) {
                                                //image
                                                //model.setImagePath
                                                uploadPhoto(filePdf, false)
                                            } else {
                                                //pdf
                                                ///model.setPdfFilePath
//                                                saveCompanyDataIntoFirebaseDataabse()
                                            }

                                        }.addOnFailureListener(OnFailureListener { e: java.lang.Exception? -> })
                                    }
                                    ?.addOnFailureListener(OnFailureListener { e: java.lang.Exception? ->
                                        progressDialog?.dismiss()
                                        Toasty.error(applicationContext, Constants.NETWORK_ERROR).show()
                                    })
                                    ?.addOnProgressListener(OnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                                .totalByteCount
                                    })
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }) { throwable ->
                        throwable.printStackTrace()
                        //                            showError(throwable.getBody());
                    }
        }
    }

//    private fun saveCompanyDataIntoFirebaseDataabse() {
//
//        RefBase.refCompanies()
//                .child(key)
//                .setValue(orderRequest)
//                .addOnSuccessListener { aVoid: Void? ->
//                    val orderPending = OrderPending()
//                    orderPending.userId = orderRequest.getCustomerId()
//                    orderPending.categoryId = orderRequest.getCategoryId()
//                    //                        orderPending.setCategoryId(category.getCategoryId());
//                    orderPending.orderId = key
//                    //                        RefBase.requestPending(customerId)
//                    ////                                .push()
//                    ////                                .child(orderRequest.getOrderId())
//                    //                                .child(key)
//                    //                                .setValue(orderPending)
//                    //                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    //                                    @Override
//                    //                                    public void onSuccess(Void aVoid) {
//                    spotsDialog!!.dismiss()
//                    addNotificationToControlPanel()
//                    Toasty.success(Objects.requireNonNull(getActivity()), context.resources
//                            .getString(R.string.request_sent_success)).show()
//                    Prefs.edit().remove(Constants.ORDER).apply()
//                    EventBus.getDefault().post(MsgEventReloadFragment(true))
//                }
//                .addOnFailureListener { e: java.lang.Exception? -> spotsDialog!!.dismiss() }
//
//    }


    //    public static class pdfFileViewer extends PDFView {
//
//        /**
//         * Construct the initial view
//         *
//         * @param context
//         * @param set
//         */
//        public pdfFileViewer(Context context, AttributeSet set, File file) {
//            super(context, set);
//            this.fromFile(file);
//        }
//
//        public pdfFileViewer(Context context, AttributeSet set, Uri uri) {
//            super(context, set);
//            this.fromUri(uri);
//        }
//
//    }
    fun myUri(originalUri: Uri, path: String?): Uri? {
        var returnedUri: Uri? = null
        returnedUri = if (originalUri.scheme == null) {
            Uri.fromFile(File(path))
            // or you can just do -->
// returnedUri = Uri.parse("file://"+camUri.getPath());
        } else {
            originalUri
        }
        return returnedUri
    }

    private fun addDefaultItemsToSpinnerCountries() {
        listCountries.clear()
        listCountriesObj.clear()
        if (lang()) {
            listCountries.add(applicationContext.getString(R.string.select_country2_arb))
        } else {
            listCountries.add(applicationContext.getString(R.string.select_country2))

        }
        listCountriesObj.add(Country()) //fake
//        spinnerCity.visibility = View.GONE

        spinnerCountryCompany.setItems(listCountries)
    }

    private fun setOnItemClicks() {
        spinnerCountryCompany.setOnItemSelectedListener { view, position, id, item ->
            posSpinnerCountry = position
            if (position != 0) {
                country = listCountriesObj[position]
                countryId = country!!.countryId

                spotsDialog = getInstance().pleaseWait(this)
                spotsDialog!!.show()
                Log.e(TAG, "Empty 2")

                loadAllCities()
            } else {
                if (position == 0) {
                    txtMultiCityCompany.visibility = View.GONE
                    txtMultiCityCompany.text = ""
                    Log.e(TAG, "Empty")
                }
            }
        }
    }

    private fun loadAllCities() {

        RefBase.CitySection.refLocCities(countryId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.e(TAG, "Country id $countryId and $dataSnapshot")
                        dataSnapshot.ref.removeEventListener(this)
                        if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                            listCities.clear()
                            listKeyCities.clear()
//                            listCities.add(applicationContext.getString(R.string.select_city))
//                            listKeyCities.add("fake")
                            for (dataSnapshot1 in dataSnapshot.children) { //                                        Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                                val map = dataSnapshot1.value as HashMap<String, Any>?
                                if (map != null) {
                                    Log.e(TAG, "onDataChange: " + map["cityName"].toString())
                                    var city: String? = ""
                                    //auto key
                                    val key = dataSnapshot1.key
                                    city = if (lang()) {
                                        map["cityNameArabic"] as String?
                                    } else {
                                        map["cityName"] as String?
                                    }
                                    if (city != null) {
                                        listCities.add(City(city, key))
                                    }
                                    listKeyCities.add(key!!)
                                }
                            }
                            spotsDialog!!.dismiss()
                        }
//                         spinnerMultiCityCompany.setItems(listCities);
//                        if (isThereEditOrder() || Prefs.contains(Constants.ORDER)) {
//                            setSelectedCity();
//                        }

                        showMultiSelectDialogCities()

                    }

                })
    }

    var selectedCitiesStr: String = ""

    private fun showMultiSelectDialogCities() {
        //                        val multiSelectDialog = MultiSelectDialog.newIntance(this@CompanyRegisterActivity, listCities)
        //                        multiSelectDialog.show(supportFragmentManager, "7/8/1995")
        val multiSelectDialog = MultiSelectDialog(this@CompanyRegisterActivity, listCities, object : OnCitySelectedFinished {
            override fun onCitySelectedCancelled(error: String?) {
                txtMultiCityCompany.visibility = View.GONE
                txtMultiCityCompany.text = ""
                spinnerCountryCompany.selectedIndex = 0
            }

            override fun onCitySelectedFinished(result: MutableList<City>?) {
//                Log.e(TAG, result)
                listSelectedCities = result!!.toMutableList()
                for (c in 0 until result.size) {
                    if (c == result.size - 1) {
                        selectedCitiesStr = selectedCitiesStr.plus(result[c].cityName)
                    } else {
                        selectedCitiesStr = selectedCitiesStr.plus(result[c].cityName).plus(", ")
                    }

                }

                txtMultiCityCompany.text = selectedCitiesStr
                txtMultiCityCompany.visibility = View.VISIBLE
                selectedCitiesStr = ""
//                listSelectedCities.clear()
            }
        })
        multiSelectDialog.show()
    }

    var selectedCategoriesStr: String = ""

    private fun showMultiSelectDialogCategories() {
        //                        val multiSelectDialog = MultiSelectDialog.newIntance(this@CompanyRegisterActivity, listCities)
        //                        multiSelectDialog.show(supportFragmentManager, "7/8/1995")
        val multiSelectDialog = MultiSelectDialog(this@CompanyRegisterActivity,
                listCategories, Constants.CATEGORIES,
                object : OnCategorySelectedFinished {
                    override fun onCategorySelectedCancelled(error: String?) {
//                        tvSelectedCats.text = ""
                        tvSelectedCats.text = getString(R.string.select_cat_dialog)

                    }

                    override fun onCategorySelectedFinished(result: MutableList<Category>?) {

                        listSelectedCategory = result!!.toMutableList()

                        for (c in 0 until result.size) {
                            if (lang()) {
                                if (c == result.size - 1) {
                                    selectedCategoriesStr = selectedCategoriesStr.plus(result[c].categoryNameArabic)
                                } else {
                                    selectedCategoriesStr = selectedCategoriesStr.plus(result[c].categoryNameArabic).plus(", ")
                                }
                            } else {
                                if (c == result.size - 1) {
                                    selectedCategoriesStr = selectedCategoriesStr.plus(result[c].categoryName)
                                } else {
                                    selectedCategoriesStr = selectedCategoriesStr.plus(result[c].categoryName).plus(", ")
                                }
                            }

                        }
//                        Log.e(TAG, result)
                        tvSelectedCats.text = selectedCategoriesStr
                        selectedCategoriesStr = ""
                    }
                })
        multiSelectDialog.show()
    }

    private fun setMultiSelectSpinnerAdapterLocation() {
//        addItems()
        //Setting Multi Selection Spinner without image.
        spinnerCountryCompany.setItems(listCountries)
    }

    private fun loadAllCountries() {
        RefBase.CountrySection.refLocCountries().addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.ref.removeEventListener(this)
                if (dataSnapshot.exists()) {

                    for (dataSnapshot1 in dataSnapshot.children) {
                        val hashMap = dataSnapshot1.value as HashMap<String, Any?>?
                        if (hashMap != null) {
                            val country = Country()
                            country.countryId = dataSnapshot1.key
                            if (hashMap["countryName"] != null) {
                                country.countryName = hashMap["countryName"].toString()
                            }
                            if (hashMap["countryNameArabic"] != null) {
                                country.countryNameArabic = hashMap["countryNameArabic"].toString()
                            }
                            if (lang()) {
                                listCountries.add(country.countryNameArabic)
                            } else {
                                listCountries.add(country.countryName)
                            }
                            listCountriesObj.add(country)
                        }
                        spinnerCountryCompany.setItems(listCountries)
                        dataSnapshot.ref.removeEventListener(this)
                    }
                }
            }
        })
    }

    private fun setOnClicks() {
        ivUserPhoto.setOnClickListener {
            Log.e(TAG, "ivUserPhoto: ")
            requestCamAndStoragePerms()
            ProfileOrPdf = "Profile"

        }
        btn_Company_register.setOnClickListener {
            if (checkData()) {
                registerCompany()
            }
//            registerCompany()

        }
        txtMultiCityCompany.setOnClickListener {
            //            it.visibility =  View.GONE
//            txtMultiCityCompany.text =  "";
            showMultiSelectDialogCities()
        }
        tvSelectedCats.setOnClickListener {
            //            it.visibility =  View.GONE
//            txtMultiCityCompany.text =  "";
            showMultiSelectDialogCategories()
        }
//        btnFetchPdfFile.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
////            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//        }

//        btnUploadPdfFile.setOnClickListener {
//
//        }


        btnFetchPdfFile.setOnClickListener {
//            PdfUtils.pdfFetchFile(this)
            Log.e(TAG, "btnFetchPdfFile: ")
            showMenuPdfOrImage()
//            ProfileOrPdf = "Pdf"


        }

//        btnEditExistPdfFile.setOnClickListener {
////            PdfUtils.pdfFetchFile(this)
//            Log.e(TAG, "hoho")
////            PdfUtils.pdfUploadFile(this, filePdf, object : OnPdfUploadingResponse {
////                override fun onUploadSuccess(success: String?) {
////                    Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
////                }
////
////                override fun onUploadError(e: java.lang.Exception?, err: String?) {
////                    Toast.makeText(applicationContext, err, Toast.LENGTH_SHORT).show()
////                }
////            })
//            showMenuPdfOrImage()
//
//
//        }


        tvCompanyLogin.setOnClickListener {
            finish()
            val i = Intent()
            i.setClass(this, CustomerOrWorkerActivity::class.java)
            startActivity(i)

        }

    }

    private fun showMenuPdfOrImage() {
        BottomSheetBuilder(this, null)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .addDividerItem()
                .expandOnStart(true)
                .setDividerBackground(R.color.grey_400)
                .setBackground(R.drawable.ripple_grey)
                .setMenu(R.menu.menu_image_pdf_picker)
                .setItemClickListener { item: MenuItem? ->
                    when (item?.itemId) {
                        R.id.chooseFromCamera -> {
                            //EasyImage.openCamera(this@CompanyRegisterActivity, 0)
                            //SeyanahEasyImage.openCamera(this)
                            ImagePicker.cameraOnly().start(this)
                            isImageOrPdf = Constants.TYPE_CAM
                            ProfileOrPdf = "Pdf"

                        }
                        R.id.chooseFromGellery -> {
                            //                            EasyImage.openGallery(this@CompanyRegisterActivity, 0)
                            SeyanahEasyImage.openGallery(this)
                            isImageOrPdf = Constants.TYPE_GALLERY
                            ProfileOrPdf = "Pdf"

                        }
                        R.id.choosePdf -> {
                            PdfUtils.pdfFetchFile(this)
                            isImageOrPdf = Constants.TYPE_PDF
                            ProfileOrPdf = "Pdf"
                        }
                    }
                }
                .createDialog().show()
    }

    private fun registerCompany() {

//        if (dlgProgress != null && !dlgProgress!!.isShowing) {
//            dlgProgress!!.setMessage("Registering")
//            dlgProgress!!.show()
//        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(getString(R.string.reg_company))
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

        checkIfPhoneExistInSpecificRef(Constants.PHONE_FROM_USER_MODEL, etCompanyPhone.text.toString()) {
            if (it) {
                progressDialog!!.dismiss()
                startWobble(applicationContext, etCompanyPhone)
                Snekers.getInstance().error(getString(R.string.phone_already_exist), this)
                scrollToView(scrollView, etCompanyPhone)
            } else {
                Log.e(TAG, "phone not exist")
//                progressDialog!!.dismiss()
                startVirificationOperation()
                //call verification auth
            }
        }

//        uploadPhoto(fileImage, true)//true for image

    }

    fun startVirificationOperation() {
        //photo
        company.companyPhone = etCompanyPhone.text.toString()
        company.companyNameInArabic = etCompanyNameInArabic.text.toString()
        company.companyNameInEnglish = etCompanyNameInEnglish.text.toString()
        company.companyPassword = etCompanyPass.text.toString()
//        company.companyPasswordConfirm = etCompanyConfirmPass.text.toString()
        company.companyEmail = etCompanyEmail.text.toString()
        company.companyLocationAddress = etEnterLocation.text.toString()
        company.login = true

        //category
        //attached pdf
        //city
        //country
        //get country name and id
        val loc = LocationCompany()
//        val cObj = listCountries[posSpinnerCountry]
        val cObj = listCountriesObj[posSpinnerCountry]
//        country.countryName = cObj.countryName
//        country.countryId = cObj.countryId


//        loc.country.countryName = cObj.countryName
//        loc.country.countryId = cObj.countryId


        for (x in 0 until listSelectedCategory.size) {
            company.companyCategoryId?.add(listSelectedCategory[x].categoryId)
        }
        company.categories = null

        loc.countryId = cObj.countryId

        //for clearing all country names
        for (x in 0 until listSelectedCities.size) {
            loc.cityId.add(listSelectedCities[x].cityId)
        }

        loc.listOfCities = null
        loc.country = null

//        loc.country.countryId = cObj.countryId
        //get cities
//        val citiesWithoutCommas = txtMultiCityCompany.text.split(",").toTypedArray()
//        for (b in citiesWithoutCommas.indices) {
//            if (citiesWithoutCommas[b] == listCities[b]) {
//            }
//        }

//        loc.listOfCities = listSelectedCities
        company.companyLocation = loc

//        if (lang()) {
//            company.locations = country!!.countryNameArabic
//        } else {
//            location.country = country!!.countryName
//        }


        val cdate = Calendar.getInstance().time
        val cdateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val ctoday = cdateFormat.format(cdate)

        company.createDate = ctoday

        Log.e(TAG, "REGISTER AS COMPANY  ${company.companyNameInEnglish} and ${listSelectedCategory.size} ")


        //        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    cmWorker.setWorkerPassword(Utils.encrypt(etWorkerPass.getText().toString()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        //        cmWorker.setWorkerConfirmPassword(etWorkerConfirmPass.getText().toString());
//        val location = Location()
//        //        location.setCountry(spinnerCountry.getItems().get(posSpinnerCountry).toString());
//        if (lang()) {
//            location.country = country!!.countryNameArabic
//        } else {
//            location.country = country!!.countryName
//        }

        //        String fullPhone = tvCountryCode.getText().toString() + etEnterPhone.getText().toString();
//        String fullPhone = getString(R.string.country_code) + etEnterPhone.getText().toString();
        val fullPhone = Constants.COUNTRY_CODE + etCompanyPhone.text.toString()


//        PhoneUtils(this).startPhoneNumberVerifica(fullPhone, mCallbacks)
        startPhoneNumberVerificationCompany(this, fullPhone, mCallbacks)
    }

    private fun checkData(): Boolean {

        if (!flagCompanyLogo) {
            scrollView.fullScroll(View.FOCUS_UP)
            Snekers.getInstance().error(getString(R.string.upload_company_photo), this)
            startWobble(applicationContext, ivUserPhoto)
            Log.e(TAG, "test upload photo")
            updateProfilePhoto()
//            bsDialog?.show()
            return false
        }

        if (TextUtils.isEmpty(etCompanyPhone.text.trim())) {
            startWobble(applicationContext, etCompanyPhone)
            Snekers.getInstance().error(getString(R.string.error_enter_phone), this)
            scrollToView(scrollView, etCompanyPhone)
            return false
        }

        if (TextUtils.isEmpty(etCompanyNameInArabic.text.trim())) {
            startWobble(applicationContext, etCompanyNameInArabic)
            Snekers.getInstance().error(getString(R.string.error_enter_name), this)
            scrollToView(scrollView, etCompanyNameInArabic)
            return false
        }

        if (TextUtils.isEmpty(etCompanyNameInEnglish.text.trim())) {
            startWobble(applicationContext, etCompanyNameInEnglish)
            Snekers.getInstance().error(getString(R.string.error_enter_english_name), this)
            scrollToView(scrollView, etCompanyNameInEnglish)
            return false
        }

        if (TextUtils.isEmpty(etCompanyPass.text.trim())) {
            startWobble(applicationContext, etCompanyPass)
            Snekers.getInstance().error(getString(R.string.enter_pass), this)
            scrollToView(scrollView, etCompanyPass)
            return false
        }

        if (!isValidPassword(etCompanyPass.text.toString().trim())) {
            startWobble(applicationContext, etCompanyPass)
            Snekers.getInstance().error(getString(R.string.enter_6_chars), this)
            scrollToView(scrollView, etCompanyPass)
            return false
        }

        if (!TextUtils.equals(etCompanyPass.text.trim(), etCompanyConfirmPass.text.trim())) {
            startWobble(applicationContext, etCompanyConfirmPass)
            Snekers.getInstance().error(getString(R.string.error_pass_donot_match), this)
            scrollToView(scrollView, etCompanyConfirmPass)
            return false
        }

        if (TextUtils.isEmpty(etCompanyEmail.text.trim())) {
            startWobble(applicationContext, etCompanyEmail)
            Snekers.getInstance().error(getString(R.string.enter_email), this)
            scrollToView(scrollView, etCompanyEmail)
            return false
        }

        if (!isEmailValid(etCompanyEmail.text.trim().toString())) {
            startWobble(applicationContext, etCompanyEmail)
            Snekers.getInstance().error(getString(R.string.enter_valid_email), this)
            scrollToView(scrollView, etCompanyEmail)
            return false
        }

        if (posSpinnerCountry == 0) {
            Snekers.getInstance().error(getString(R.string.select_country), this)
            scrollToView(scrollView, spinnerCountryCompany)
            return false
        }

        if (txtMultiCityCompany.text.trim().isNullOrEmpty()) {
            Snekers.getInstance().error(getString(R.string.select_city), this)
            scrollToView(scrollView, txtMultiCityCompany)
            return false
        }

        if (TextUtils.isEmpty(etEnterLocation.text?.trim())) {
            startWobble(applicationContext, etEnterLocation)
            Snekers.getInstance().error(getString(R.string.enter_loc_address), this)
            scrollToView(scrollView, etEnterLocation)
            return false
        }

        if (TextUtils.equals(tvSelectedCats.text.trim(), getString(R.string.select_cat_dialog))) {
            startWobble(applicationContext, tvSelectedCats)
            Snekers.getInstance().error(getString(R.string.select_category), this)
            scrollToView(scrollView, tvSelectedCats)
            return false
        }

//        if (!flagCompanyPdfFile) {
//            startWobble(applicationContext, upload_pdf)
//            Snekers.getInstance().error(getString(R.string.select_pdf), this)
//            scrollToView(scrollView, upload_pdf)
//            Log.e(TAG, "test upload pdf" )
//            return false
//        }
        if (filePdf == null) {

            startWobble(applicationContext, upload_pdf)
            Snekers.getInstance().error(getString(R.string.select_pdf), this)
            scrollToView(scrollView, upload_pdf)
            return false
        }

        return true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private fun requestCamAndStoragePerms() {

        if (EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            updateProfilePhoto()
            bsDialog?.show()
        } else {
            EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, RC_CAMERA_AND_STORAGE, *PERMISSIONS)
                            .setRationale(R.string.cam_and_storage_rationale)
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
//                            .setTheme(R.style.AppTheme)
                            .build()
            )
        }

        // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, getString(R.string.contacts_and_storage_rationale),
//                    RC_CONTACT_AND_STORAGE, perms);
//        EasyPermissions.requestPermissions(
//                PermissionRequest.Builder(this, WorkerRegisterActivity.RC_CAMERA_AND_STORAGE, *WorkerRegisterActivity.PERMISSIONS)
//                        .setRationale(R.string.cam_and_storage_rationale)
//                        .setPositiveButtonText(R.string.rationale_ask_ok)
//                        .setNegativeButtonText(R.string.rationale_ask_cancel) //                            .setTheme(R.style.AppTheme)
//                        .build())
    }

    private fun updateProfilePhoto() {
        bsDialog = BottomSheetBuilder(this, null)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .addDividerItem()
                .expandOnStart(true)
                .setDividerBackground(R.color.grey_400)
                .setBackground(R.drawable.ripple_grey)
                .setMenu(R.menu.menu_image_picker)
                .setItemClickListener { item: MenuItem? ->
                    when (item?.itemId) {
                        R.id.chooseFromCamera -> {
                            //EasyImage.openCamera(this@CompanyRegisterActivity, 0)
                            //SeyanahEasyImage.openCamera(this)
                            ImagePicker.cameraOnly().start(this)
                        }

                        R.id.chooseFromGellery -> {
//                            EasyImage.openGallery(this@CompanyRegisterActivity, 0)
                            SeyanahEasyImage.openGallery(this)
                        }
                    }
                }
                .createDialog()
    }
}