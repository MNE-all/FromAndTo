package com.mne4.fromandto.Fragment

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.User
import com.mne4.fromandto.Data.Room.MainDB
import com.mne4.fromandto.IntroActivity
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    private var imgURL = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLocalDB(view.context).getDao().getAllUser().asLiveData().observe(requireActivity()) {
            for (user in it) {
                if (user.isInAcc) {
                    viewModel.getCurrentUser(user.id_user)
                    return@observe
                }
            }
        }



        // Получение данных о пользователе
        viewModel.ApiGetCurrentUser.observe(requireActivity()){
            Picasso.get().load(it.image_url)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.search_result)
                .into(binding.imageUser)
            val fio = "${it.surname} ${it.name}"
            binding.textViewUserFIO.text = fio
        }

        binding.buttonAccSettings.setOnClickListener {
            var bottomSheetDialog = BottomSheetDialog(view.context)
            bottomSheetDialog.setContentView(R.layout.profile_bottom_sheet_dialog)

            bottomSheetDialog.findViewById<Button>(R.id.buttonChangeUserImage)!!.setOnClickListener {
                imgClick()
            }

            bottomSheetDialog.show()

            InitUser(bottomSheetDialog)
            butDeleteAcc()
            butSave(bottomSheetDialog)

            Change(bottomSheetDialog)
            ChipActive(bottomSheetDialog)
            SpinnerGender(bottomSheetDialog)
            DateDialog(bottomSheetDialog)
        }
    }

    fun imgClick() {
        var galleryIntent = Intent()
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
        galleryIntent.setType("image/*")
        @Suppress("DEPRECATION")
        startActivityForResult(galleryIntent, GalleryPick)
    }

    @Deprecated("Deprecated in Kotlin")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==GalleryPick && resultCode== RESULT_OK && data!=null){
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(requireActivity(),this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                // result.uri - local URL картинки
                var resultUri: Uri? = result.uri
                var file = result.uri.toFile()
                val requestFile= RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)


                viewModel.ApiImgUrl.observe(this) {
                    Picasso.get().load(it).into(binding.imageUser)
                    imgURL = it
                }

                viewModel.makeImgUrl(filePart)

            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                var error:Exception = result.error
            }
        }
    }

    fun InitUser(view: BottomSheetDialog){

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

            Picasso.get().load(USER.image_url)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.search_result)
                .into(binding.imageUser)

            view.findViewById<TextInputEditText>(R.id.surnameField)?.setText(it.surname)
            view.findViewById<TextInputEditText>(R.id.nameField)?.setText(it.name)
            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)

            val date: Date?
            if (!it.birthday.isNullOrEmpty()) {
                date = inputFormat.parse(it.birthday.toString()) as Date
                val outputText: String = outputFormat.format(date)
                view.findViewById<TextView>(R.id.txtCalendar)?.text = outputText
            }



            view.findViewById<TextInputEditText>(R.id.emailField)?.setText(it.email)
            view.findViewById<TextInputEditText>(R.id.passportField)?.setText(it.passport)
            view.findViewById<TextInputEditText>(R.id.licenseField)?.setText(it.license)
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
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.getDao().deleteUser(usersDB)
                                }
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
    fun butSave(view: BottomSheetDialog){
        view.findViewById<Button>(R.id.butSave)?.setOnClickListener {
            replay = false
            var db = MainDB.getDB(requireActivity())
            db.getDao().getAllUser().asLiveData().observe(requireActivity()) {
                for (user in it) {
                    if (user.isInAcc && !replay) {

                        val switchChange = view.findViewById<Switch>(R.id.switchChange)


                        var surname = view.findViewById<TextInputEditText>(R.id.surnameField)?.text.toString()
                        var name = view.findViewById<TextInputEditText>(R.id.nameField)?.text.toString()
                        var email = view.findViewById<TextInputEditText>(R.id.emailField)?.text.toString()
                        var txtCalendar = view.findViewById<TextView>(R.id.txtCalendar)

                        val outputFormat: DateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                        val inputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                        val date: Date?
                        if (!view.findViewById<TextView>(R.id.txtCalendar)?.text.isNullOrEmpty()) {
                            date = inputFormat.parse(txtCalendar?.text.toString()) as Date
                            val outputText: String = outputFormat.format(date)
                            txtCalendar?.text = outputText
                            var birthday: String? = outputText
                            USER.birthday = birthday
                        }

                        var passportField = view.findViewById<TextInputEditText>(R.id.passportField)

                        view.findViewById<TextInputEditText>(R.id.passportField)?.text.toString()
                        var passport = passportField?.text.toString()
                        var license = view.findViewById<TextInputEditText>(R.id.licenseField)?.text.toString()
                        if (surname.isNullOrEmpty() || name.isNullOrEmpty()) {
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
                        if (!imgURL.isNullOrEmpty()){
                            USER.image_url = imgURL
                        }

                        if (phone != "" && password != "") {
                            var phoneField = view.findViewById<TextInputEditText>(R.id.phoneField)
                            if (passportField?.text.toString() == view.findViewById<TextInputEditText>(R.id.passwordFieldStill)?.text.toString()) {
                                viewModel.postIsPhoneUnique(phoneField?.text.toString())
                                viewModel.ApiPostIsPhoneUnique.observe(requireActivity()) { it ->
                                    if (it) {
                                        USER.phone = phoneField?.text.toString()
                                        USER.password = view.findViewById<TextInputEditText>(R.id.passwordField)?.text.toString()
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

                                                switchChange?.isChecked = false
                                                isVisibleSecurity(false, view)
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
                            Toast.makeText(view.context.applicationContext, "Изменения успешно сохранены", Toast.LENGTH_SHORT).show()
                            switchChange?.isChecked = false
                            isVisibleSecurity(false, view)
                        }
                        replay = true
                    }
                }

            }

        }
    }

    private fun ChipActive(view: BottomSheetDialog){
        val chipSecurity = view.findViewById<Chip>(R.id.chipSecurity)
        chipSecurity?.setOnClickListener {
            if (chipSecurity.isChecked) {
                chipSecurity.isChecked = false
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
                                isVisibleSecurity(true, view)
                                view.findViewById<TextInputEditText>(R.id.phoneField)?.setText(phone)
                                view.findViewById<TextInputEditText>(R.id.passwordField)?.setText(password)
                                view.findViewById<TextInputEditText>(R.id.passwordFieldStill)?.setText(password)
                                return@observe
                            }
                        }
                        Toast.makeText(requireContext(), "Неправильно введенные данные!",Toast.LENGTH_SHORT).show()
                        isVisibleSecurity(false, view)
                    }
                })
                dialog.show()

            }else{
                isVisibleSecurity(false, view)
            }
        }
    }

    private fun isVisibleSecurity(truth: Boolean, view: BottomSheetDialog){

        val chipSecurity = view.findViewById<Chip>(R.id.chipSecurity)

        view.findViewById<TextInputLayout>(R.id.phoneFieldLayout)?.isVisible = truth
        view.findViewById<TextInputLayout>(R.id.passwordFieldLayout)?.isVisible = truth
        view.findViewById<TextInputLayout>(R.id.passwordFieldLayoutStill)?.isVisible = truth

        chipSecurity?.isChecked = truth
        if(!truth) {
            chipSecurity?.text = "Пройти безопасность"
        }else{
            chipSecurity?.text = "Безопасность подтверждена"
        }
    }
    private fun SpinnerGender(view: BottomSheetDialog){
        val spinnerGender = view.findViewById<Spinner>(R.id.spinnerGender)
        var genderList = arrayOf("Мужской","Женский")
        var arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderList)
        spinnerGender?.adapter = arrayAdapter
        spinnerGender?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                gender = genderList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
    fun DateDialog(view: BottomSheetDialog){
        val txtCalendar = view.findViewById<TextView>(R.id.txtCalendar)
        txtCalendar?.setOnClickListener {

            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            var date = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Дата рождения")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()


            date.show(super.getParentFragmentManager(), "DATE_PICKER")
            date.addOnPositiveButtonClickListener {
                txtCalendar?.text = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT).format(it)
            }

        }
    }

    private fun Change(view: BottomSheetDialog){
        Init(false, view)
        view.findViewById<Switch>(R.id.switchChange)?.setOnCheckedChangeListener{buttonView, isChecked->


            Init(isChecked, view)
            InitUser(view)
        }


    }

    private fun Init(truth:Boolean, view: BottomSheetDialog){
        view.findViewById<Chip>(R.id.chipSecurity)?.setChipBackgroundColorResource(com.google.android.material.R.color.material_dynamic_neutral80)

        isVisibleSecurity(true, view)
        isVisibleSecurity(false, view)

        view.findViewById<Button>(R.id.buttonChangeUserImage)?.isEnabled = truth
        view.findViewById<Button>(R.id.butSave)?.isEnabled = truth

        if (truth){
            view.findViewById<Button>(R.id.buttonChangeUserImage)?.setBackgroundColor(view.context.getColor(R.color.teal_200))
            view.findViewById<Button>(R.id.butSave)?.setBackgroundColor(view.context.getColor(R.color.teal_200))
            view.findViewById<Chip>(R.id.chipSecurity)?.setChipBackgroundColorResource(R.color.teal_200)
        }
        else {
            view.findViewById<Button>(R.id.buttonChangeUserImage)?.setBackgroundColor(view.context.getColor(com.google.android.material.R.color.material_dynamic_neutral80))
            view.findViewById<Button>(R.id.butSave)?.setBackgroundColor(view.context.getColor(com.google.android.material.R.color.material_dynamic_neutral80))
            view.findViewById<Chip>(R.id.chipSecurity)?.setChipBackgroundColorResource(com.google.android.material.R.color.material_dynamic_neutral80)
        }


        binding.imageUser.isEnabled = truth


        view.findViewById<TextInputEditText>(R.id.surnameField)?.isEnabled = truth
        view.findViewById<TextInputEditText>(R.id.nameField)?.isEnabled = truth
        view.findViewById<TextInputEditText>(R.id.emailField)?.isEnabled = truth

        view.findViewById<TextInputEditText>(R.id.phoneField)?.isEnabled = truth
        view.findViewById<TextInputEditText>(R.id.passwordField)?.isEnabled = truth
        view.findViewById<TextInputEditText>(R.id.passwordFieldStill)?.isEnabled = truth

        view.findViewById<TextInputEditText>(R.id.passportField)?.isEnabled = truth
        view.findViewById<TextInputEditText>(R.id.licenseField)?.isEnabled = truth

        view.findViewById<Spinner>(R.id.spinnerGender)?.isEnabled = truth
        view.findViewById<TextView>(R.id.txtCalendar)?.isEnabled = truth
        view.findViewById<Chip>(R.id.chipSecurity)?.isEnabled = truth



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