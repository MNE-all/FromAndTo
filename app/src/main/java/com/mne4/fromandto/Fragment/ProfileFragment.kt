package com.mne4.fromandto.Fragment

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.User
import com.mne4.fromandto.Data.Room.MainDB
import com.mne4.fromandto.IntroActivity
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.ActivityProfileBinding
import com.mne4.fromandto.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    val viewModel: DataModel by activityViewModels()
    private var replay = false
    private var gender: String = "Мужской"
    private var phone: String = ""
    private var password: String = ""
    private lateinit var USER: User
    private var GalleryPick: Int =1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InitUser()
        butDeleteAcc()
        butSave()
        ImgClick()

        Change()
        ChipActive()
        SpinnerGender()
        DateDialog()
    }

    fun ImgClick() {
        binding.imageUser.setOnClickListener {
            var galleryIntent = Intent()
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
            galleryIntent.setType("image/*")
            @Suppress("DEPRECATION")
            startActivityForResult(galleryIntent, GalleryPick)
        }
//        Picasso.get()
//            .load( "http://vsegda-pomnim.com/uploads/posts/2022-04/1649121458_29-vsegda-pomnim-com-p-samie-krasivie-mesta-prirodi-foto-29.jpg")
//            .into(binding.imageUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        var imageUri: Uri?
        if(requestCode==GalleryPick && resultCode== RESULT_OK && data!=null){
            imageUri = data.data

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(requireActivity());
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                var resultUri: Uri? = result.uri
                Log.d("img","${resultUri?.encodedPath}")
                Picasso.get().load(resultUri).into(binding.imageUser)

            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                var error:Exception = result.error
            }
        }


    }

    fun InitUser(){

        var db = MainDB.getDB(requireActivity())
        db.getDao().getAllUser().asLiveData().observe(requireActivity()) {
            for (user in it) {
                if (user.isInAcc) {
                    viewModel.getCurrentUser(user.id_user)
                    return@observe
                }
            }
        }

        viewModel.ApiGetCurrentUser.observe(requireActivity()){
            USER = it
            binding.surnameField.setText(it.surname)
            binding.nameField.setText(it.name)
            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)

            val date: Date?
            if (!it.birthday.isNullOrEmpty()) {
                date = inputFormat.parse(it.birthday.toString()) as Date
                val outputText: String = outputFormat.format(date)
                binding.txtCalendar.setText(outputText)
            }



            binding.emailField.setText(it.email)
            binding.passportField.setText(it.passport)
            binding.licenseField.setText(it.license)

        }
    }

    fun butDeleteAcc(){
        binding.txtDeleteAcc.setOnClickListener {
            var dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            dialog.setTitle("Подтверждение на удаление!")
            dialog.setMessage("Вы подтверждаете удаление аккаунта!!!\nВведите пароль!")
            var entry_delete_user =
                LayoutInflater.from(requireContext()).inflate(R.layout.entry_delete_user, null)
            var password =
                entry_delete_user.findViewById<TextInputEditText>(R.id.passwordFieldDelete)
            dialog.setView(entry_delete_user)
            dialog.setNegativeButton("Отменить",
                DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                    dialog?.dismiss()
                })
            dialog.setPositiveButton(
                "Подтверждаю",
                DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                    var db = MainDB.getDB(requireActivity());
                    db.getDao().getAllUser().asLiveData().observe(requireActivity()) {
                        for (user in it) {
                            if (user.isInAcc) {
                                viewModel.deleteUser(user.id_user, password.text.toString())
                                var usersDB = com.mne4.fromandto.Data.Room.Entities.User(
                                    null,
                                    user.id_user,
                                    user.password,
                                    false
                                )
                                db.getDao().deleteUser(usersDB)
                                Toast.makeText(
                                    requireContext(),
                                    "Аккаунт успешно удален!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(requireContext(), IntroActivity::class.java))
                                activity?.finish()
                            }
                        }
                    }

                })
            dialog.show()
        }
    }
    fun butSave(){
        binding.butSave.setOnClickListener {
            replay = false
            var db = MainDB.getDB(requireActivity())
            db.getDao().getAllUser().asLiveData().observe(requireActivity()) {
                for (user in it) {
                    if (user.isInAcc && !replay) {
                        var surname = binding.surnameField.text.toString()
                        var name = binding.nameField.text.toString()
                        var email = binding.emailField.text.toString()

                        val outputFormat: DateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                        val inputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                        val date: Date?
                        if (!binding.txtCalendar.text.isNullOrEmpty()) {
                            date = inputFormat.parse(binding.txtCalendar.text.toString()) as Date
                            val outputText: String = outputFormat.format(date)
                            binding.txtCalendar.text = outputText
                            var birthday: String? = outputText
                            USER.birthday = birthday
                        }

                        var passport = binding.passportField.text.toString()
                        var license = binding.licenseField.text.toString()
                        if (TextUtils.isEmpty(surname) || TextUtils.isEmpty(name)) {
                            Toast.makeText(
                                requireContext(),
                                "Фамилия и имя не должны быть пустыми!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@observe
                        }
                        USER.surname = surname
                        USER.name = name
                        USER.email = email
                        USER.gender = gender
                        USER.passport = passport
                        USER.license = license

                        if (phone != "" && password != "") {
                            if (binding.passwordField.text.toString() == binding.passwordFieldStill.text.toString()) {
                                viewModel.postIsPhoneUnique(binding.phoneField.text.toString())
                                viewModel.ApiPostIsPhoneUnique.observe(requireActivity()) { it ->
                                    if (it) {
                                        USER.phone = binding.phoneField.text.toString()
                                        USER.password = binding.passwordField.text.toString()
                                        viewModel.putEditUserSecure(
                                            user.id_user,
                                            user.password,
                                            USER
                                        )
                                        viewModel.ApiPutEditUser.observe(requireActivity()) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                db.getDao()
                                                    .updateUser(it.id_user, it.password, true)
                                                return@launch
                                            }
                                            activity?.runOnUiThread {
                                                binding.switchChange.isChecked = false
                                                isVisibleSecurity(false)
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Номер уже существует!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Пароли не совпадают!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            viewModel.putEditUser(user.id_user, user.password, USER)
                            binding.switchChange.isChecked = false
                            isVisibleSecurity(false)
                        }
                        replay = true
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
                var dialog:AlertDialog.Builder = AlertDialog.Builder(requireContext())

                dialog.setTitle("Подтвердите данные")
                var inflater: LayoutInflater = LayoutInflater.from(requireContext())
                var entry_data = inflater.inflate(R.layout.entry_data_user,null)
                var phones: TextInputEditText = entry_data.findViewById(R.id.phoneFieldData)
                var passwords: TextInputEditText = entry_data.findViewById(R.id.passwordFieldData)
                dialog.setView(entry_data)
                dialog.setPositiveButton("Подтвердить", DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                    phone = phones.text.toString()
                    password = passwords.text.toString()
                    viewModel.postAuthentication(phone,password)
                    viewModel.ApiPostAuthentication.observe(requireActivity()) {
                        if (it != null) {
                            if (USER.password == it.password && USER.phone == it.phone) {
                                isVisibleSecurity(true)
                                binding.phoneField.setText(phone)
                                binding.passwordField.setText(password)
                                binding.passwordFieldStill.setText(password)
                                return@observe
                            }
                        }
                        Toast.makeText(requireContext(), "Неправильно введенные данные!",Toast.LENGTH_SHORT).show()
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
        var arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderList)
        binding.spinnerGender.adapter = arrayAdapter
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                Snackbar.make(view!!,"Вы выбрали пол: ${genderList[position]}", Snackbar.LENGTH_SHORT).show()
                gender = genderList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    fun DateDialog(){
        binding.txtCalendar.setOnClickListener {

            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            var date = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Дата рождения")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()


            date.show(super.getParentFragmentManager(), "DATE_PICKER")
            date.addOnPositiveButtonClickListener {
                binding.txtCalendar.text = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT).format(it)
            }

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

        if(!truth){
            phone = ""
            password = ""
        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}