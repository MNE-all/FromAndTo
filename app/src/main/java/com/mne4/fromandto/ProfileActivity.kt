package com.mne4.fromandto

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.databinding.ActivityProfileBinding
import com.mne4.fromandto.db.MainDB
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var viewModel = ViewModel()
    private var gender:String = ""
    private var phone:String = ""
    private var password:String = ""
    private lateinit var USER: User
    private lateinit var imageUser:ImageView
    private var GalleryPick:Int =1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InitUser()

        Change()
        ChipActive()
        SpinnerGender()
    }
    fun ImgClick(view: View) {
        imageUser = findViewById(R.id.imageUser)

        var galleryIntent = Intent()
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
        galleryIntent.setType("image/*")
        @Suppress("DEPRECATION")
        startActivityForResult(galleryIntent,GalleryPick)


//        Picasso.get()
//            .load( "http://vsegda-pomnim.com/uploads/posts/2022-04/1649121458_29-vsegda-pomnim-com-p-samie-krasivie-mesta-prirodi-foto-29.jpg")
//            .into(imageUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        var imageUri:Uri?
        if(requestCode==GalleryPick && resultCode== RESULT_OK && data!=null){
            imageUri = data.data

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                var resultUri: Uri? = result.uri

                Log.d("img","")
                Picasso.get().load(resultUri).into(imageUser)
            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                var error:Exception = result.error
            }
        }


    }

    fun InitUser(){

        var db = MainDB.getDB(this)
        db.getDao().getAllUser().asLiveData().observe(this) {
            for (user in it) {
                if (user.isInAcc) {
                    viewModel.getCurrentUser(user.id_user)
                    return@observe
                }
            }
        }

        viewModel.dataModelUsers.ApiGetCurrentUser.observe(this){
            USER = it
            binding.surnameField.setText(it.surname)
            binding.nameField.setText(it.name)
            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)

            val date: Date = inputFormat.parse(it.birthday.toString()) as Date
            val outputText: String = outputFormat.format(date)
            binding.txtCalendar.setText(outputText)
            binding.emailField.setText(it.email)
            binding.passportField.setText(it.passport)
            binding.licenseField.setText(it.license)

        }
    }

    fun butDeleteAcc(view: View){
        var dialog:AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("Подтверждение на удаление!")
        dialog.setMessage("Вы подтверждаете удаление аккаунта!!!\nВведите пароль!")
        var entry_delete_user = LayoutInflater.from(this).inflate(R.layout.entry_delete_user,null)
        var password = entry_delete_user.findViewById<TextInputEditText>(R.id.passwordFieldDelete)
        dialog.setView(entry_delete_user)
        dialog.setNegativeButton("Отменить",DialogInterface.OnClickListener{dialog:DialogInterface?,i->
            dialog?.dismiss()
        })
        dialog.setPositiveButton("Подтверждаю",DialogInterface.OnClickListener{dialog:DialogInterface?,i->
            var db = MainDB.getDB(this);
            db.getDao().getAllUser().asLiveData().observe(this) {
                for (user in it) {
                    if (user.isInAcc) {
                        viewModel.deleteUser(user.id_user, password.text.toString())
                        var usersDB = com.mne4.fromandto.db.User(
                            null,
                            user.id_user,
                            user.password,
                            false
                        )
                        db.getDao().deleteUser(usersDB)
                        Toast.makeText(applicationContext,"Аккаунт успешно удален!",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, IntroActivity::class.java))
                        finish()
                    }
                }
            }

        })
        dialog.show()
    }
    fun butSave(view:View){
        var db = MainDB.getDB(this)
        db.getDao().getAllUser().asLiveData().observe(this){
            for(user in it){
                if(user.isInAcc){
                    var surname = binding.surnameField.text.toString()
                    var name = binding.nameField.text.toString()
                    var email = binding.emailField.text.toString()

                    val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                    val inputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    val date: Date = inputFormat.parse(binding.txtCalendar.text.toString()) as Date
                    val outputText: String = outputFormat.format(date)
                    var birthday:String? = outputText
                    if(binding.txtCalendar.text.toString() == "") {
                        birthday = null
                    }
                    var passport = binding.passportField.text.toString()
                    var license = binding.licenseField.text.toString()
                    if(TextUtils.isEmpty(surname) || TextUtils.isEmpty(name)){
                        Toast.makeText(applicationContext,"Фамилия и имя не должны быть пустыми!",Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                     USER.surname = surname
                     USER.name = name
                     USER.email = email
                     USER.gender = gender
                     USER.birthday = birthday
                     USER.passport = passport
                     USER.license = license

                     if(phone!="" && password != ""){
                         if(binding.passwordField.text.toString() == binding.passwordFieldStill.text.toString()) {
                             viewModel.postIsPhoneUnique(binding.phoneField.text.toString())
                             viewModel.dataModelUsers.ApiPostIsPhoneUnique.observe(this){
                                 if(it){
                                    USER.phone = binding.phoneField.text.toString()
                                    USER.password = binding.passwordField.text.toString()
                                     viewModel.putEditUserSecure(user.id_user, user.password, USER)
                                     viewModel.dataModelUsers.ApiPutEditUser.observe(this) {
                                         CoroutineScope(Dispatchers.IO).launch {
                                             db.getDao().updateUser(it.id_user, it.password, true)
                                             binding.switchChange.isChecked = false
                                             isVisibleSecurity(false)
                                             return@launch
                                         }
                                     }
                                 }else{
                                     Toast.makeText(this,"Номер уже существует!",Toast.LENGTH_SHORT).show()
                                 }
                             }
                         }else{
                             Toast.makeText(this,"Пароли не совпадают!",Toast.LENGTH_SHORT).show()
                         }
                     }else {
                         viewModel.putEditUser(user.id_user, user.password, USER)
                         binding.switchChange.isChecked = false
                         isVisibleSecurity(false)
                     }
                }
            }

        }


    }

    private fun updateText(calendar: Calendar){
        val dateFormat = "dd-MM-yyyy"
        var simple = SimpleDateFormat(dateFormat,Locale.UK)
        binding.txtCalendar.text = simple.format(calendar.time)
    }

    private fun ChipActive(){
        binding.chipSecurity.setOnClickListener {
            if (binding.chipSecurity.isChecked) {
                var dialog:AlertDialog.Builder = AlertDialog.Builder(this)

                dialog.setTitle("Подтвердите данные")
                var inflater: LayoutInflater = LayoutInflater.from(this)
                var entry_data = inflater.inflate(R.layout.entry_data_user,null)
                var phones: TextInputEditText = entry_data.findViewById(R.id.phoneFieldData)
                var passwords: TextInputEditText = entry_data.findViewById(R.id.passwordFieldData)
                dialog.setView(entry_data)
                dialog.setPositiveButton("Подтвердить", DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                    phone = phones.text.toString()
                    password = passwords.text.toString()
                    viewModel.postAuthentication(phone,password)
                    viewModel.dataModelUsers.ApiPostAuthentication.observe(this) {
                        if (it != null) {
                            if (USER.password == it.password && USER.phone == it.phone) {
                                isVisibleSecurity(true)
                                binding.phoneField.setText(phone)
                                binding.passwordField.setText(password)
                                binding.passwordFieldStill.setText(password)
                                return@observe
                            }
                        }
                        Toast.makeText(applicationContext, "Неправильно введенные данные!",Toast.LENGTH_SHORT).show()
                        isVisibleSecurity(false)
                    }
                })
                dialog.show()
            }else{
                isVisibleSecurity(false)
            }
        }
    }

    private fun isVisibleSecurity(truth: Boolean){
        binding.phoneFieldLayout.isVisible = truth
        binding.passwordFieldLayout.isVisible = truth
        binding.passwordFieldLayoutStill.isVisible = truth

        binding.chipSecurity.isChecked = truth
        if(!truth) {
            binding.chipSecurity.text = "Пройти безопасность"
        }else{
            binding.chipSecurity.text = "Безопасность подтверждена"
        }
    }
    private fun SpinnerGender(){

        var genderList = arrayOf("Мужской","Женский")
        var arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        binding.spinnerGender.adapter = arrayAdapter
        binding.spinnerGender.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                Snackbar.make(view!!,"Вы выбрали пол: ${genderList[position]}",Snackbar.LENGTH_SHORT).show()
                gender = genderList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    fun DateDialog(view: View){


        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
        var date = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Дата рождения")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()


        date.show(supportFragmentManager, "DATE_PICKER")
        date.addOnPositiveButtonClickListener {
            binding.txtCalendar.text = SimpleDateFormat("dd-MM-yyyy",Locale.ROOT).format(it)
        }

    }

    private fun Change(){
        Init(false)
        binding.switchChange.setOnCheckedChangeListener{buttonView, isChecked->
            Init(isChecked)
            InitUser()
        }

    }

    private fun Init(truth:Boolean){

        isVisibleSecurity(true)
        isVisibleSecurity(false)

        binding.surnameField.isEnabled = truth
        binding.nameField.isEnabled = truth
        binding.emailField.isEnabled = truth

        binding.phoneField.isEnabled = truth
        binding.passwordField.isEnabled = truth
        binding.passwordFieldStill.isEnabled = truth

        binding.passportField.isEnabled = truth
        binding.licenseField.isEnabled = truth

        binding.spinnerGender.isEnabled = truth
        binding.txtCalendar.isEnabled = truth
        binding.chipSecurity.isEnabled = truth

        binding.butSave.isEnabled = truth
    }
}

