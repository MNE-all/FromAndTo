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
import android.widget.ImageView
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
import com.mne4.fromandto.WelcomeActivity
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
    private lateinit var gender: String
    private var genderList = arrayOf<String>("","")
    private var phone: String = ""
    private var password: String = ""
    private lateinit var USER: User
    private lateinit var userLocalDB: com.mne4.fromandto.Data.Room.Entities.User
    private var GalleryPick: Int =1
    private lateinit var bottomSheetDialog: BottomSheetDialog
    var phoneField: TextInputEditText? = null
    var passwordField: TextInputEditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        InitUser(bottomSheetDialog)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetDialog = BottomSheetDialog(view.context)
        ObserveGetCurrentUser(bottomSheetDialog)
        SecurityСonfirmed(bottomSheetDialog)
        viewModel.ApiImgUrl.observe(requireActivity()) {
            USER.image_url = it
        }

        ObservePostPhoneUnque(bottomSheetDialog)
        ObservePutEditUserLocal(bottomSheetDialog)
        ObserveDeleteAcc(bottomSheetDialog)
        binding.buttonAccSettings.setOnClickListener {
            bottomSheetDialog.setContentView(R.layout.profile_bottom_sheet_dialog)
            bottomSheetDialog.findViewById<Button>(R.id.buttonChangeUserImage)!!.setOnClickListener {
                imgClick()
            }
            bottomSheetDialog.show()

            InitUser(bottomSheetDialog)
            butDeleteAcc(bottomSheetDialog)
            butSave(bottomSheetDialog)
            Change(bottomSheetDialog)
            ChipActive(bottomSheetDialog)
            SpinnerGender(bottomSheetDialog)
            DateDialog(bottomSheetDialog)
        }
        binding.txtChangeAcc.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getLocalDB(bottomSheetDialog.context).getDao().updateUserisAcc(
                    userLocalDB.id_user,
                    false
                )

            }
            val intent = Intent(bottomSheetDialog.context, IntroActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

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

                viewModel.makeImgUrl(filePart)

            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                var error:Exception = result.error
            }
        }
    }

    fun InitUser(view: BottomSheetDialog){
        activity?.let { activ ->
            viewModel.getLocalDB(view.context).getDao().getAllUser().asLiveData()
                .observe(activ) {
                    for (user in it) {
                        if (user.isInAcc) {
                            userLocalDB = user
                            viewModel.getCurrentUser(user.id_user)
                            return@observe
                        }
                    }
                }
        }
    }
    fun ObserveGetCurrentUser(view: BottomSheetDialog){
        viewModel.ApiGetCurrentUser.observe(requireActivity()){
            USER = it

            val fio = "${USER.surname} ${USER.name}"
            binding.textViewUserFIO.text = fio

            if(USER.gender == "Женский"){
                genderList[0] = "Женский"
                genderList[1] = "Мужской"
            }else{
                genderList[0] = "Мужской"
                genderList[1] = "Женский"
            }
            Picasso.get().load(USER.image_url)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(binding.imageUser)
            view.findViewById<Spinner>(R.id.spinnerGender)
            view.findViewById<TextInputEditText>(R.id.surnameField)?.setText(USER.surname)
            view.findViewById<TextInputEditText>(R.id.nameField)?.setText(USER.name)

            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val date: Date?
            if (!USER.birthday.isNullOrEmpty()) {
                date = inputFormat.parse(USER.birthday.toString()) as Date
                val outputText: String = outputFormat.format(date)
                view.findViewById<TextView>(R.id.txtCalendar)?.text = outputText
            }

            view.findViewById<TextInputEditText>(R.id.emailField)?.setText(USER.email)
            view.findViewById<TextInputEditText>(R.id.passportField)?.setText(USER.passport)
            view.findViewById<TextInputEditText>(R.id.licenseField)?.setText(USER.license)
        }
    }
    fun ObserveDeleteAcc(view: BottomSheetDialog){
        viewModel.ApiDeleteUser.observe(requireActivity()){
            if(it){
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getLocalDB(view.context).getDao().deleteUser(userLocalDB.id_user)
                }
                Toast.makeText(
                    view.context,
                    "Аккаунт успешно удален!",
                    Toast.LENGTH_SHORT
                ).show()
                view.dismiss()
                startActivity(Intent(view.context, IntroActivity::class.java))
                activity?.finish()

            }else{
                Toast.makeText(
                    view.context,
                    "Неверный пароль!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun butDeleteAcc(view: BottomSheetDialog){
        view.findViewById<ImageView>(R.id.txtDeleteAcc)?.setOnClickListener {
            var dialog: AlertDialog.Builder = AlertDialog.Builder(view.context)
            dialog.setTitle("Подтверждение на удаление!")
            dialog.setMessage("Вы подтверждаете удаление аккаунта!!!\nВведите пароль!")
            var entry_delete_user =
                LayoutInflater.from(view.context).inflate(R.layout.entry_delete_user, null)
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
                    if(password.text.toString().isEmpty()){
                        return@OnClickListener
                    }
                    viewModel.deleteUser(userLocalDB.id_user, password.text.toString())
                })
            dialog.show()
        }
    }
    fun butSave(view: BottomSheetDialog) {
        view.findViewById<Button>(R.id.butSave)?.setOnClickListener {

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
            if (surname.isEmpty() || name.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Фамилия и имя не должны быть пустыми!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            USER.surname = surname
            USER.name = name
            USER.email = email
            USER.gender = gender
            USER.passport = passport
            USER.license = license

            if (phone != "" && password != "") {
                var passwordFieldStill =
                    view.findViewById<TextInputEditText>(R.id.passwordFieldStill)?.text.toString()
                if (passwordField?.text.toString() != passwordFieldStill) {
                    Toast.makeText(
                        requireContext(),
                        "Пароли не совпадают!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if(phone != phoneField?.text.toString()){
                    viewModel.postIsPhoneUnique(phoneField?.text.toString())
                    return@setOnClickListener
                }else{
                    if(passwordField?.text.toString() != password) {
                        USER.password = passwordField?.text.toString()
                        viewModel.putEditUserSecure(
                            userLocalDB.id_user,
                            userLocalDB.password,
                            USER
                        )
                        return@setOnClickListener
                    }
                }
            }
            viewModel.putEditUser(userLocalDB.id_user, userLocalDB.password, USER)
            Toast.makeText(
                view.context.applicationContext,
                "Изменения успешно сохранены",
                Toast.LENGTH_SHORT
            ).show()
            isVisibleSecurity(false, view)
        }
    }
    fun ObservePostPhoneUnque(view: BottomSheetDialog){
        viewModel.ApiPostIsPhoneUnique.observe(requireActivity()) { it ->
            if (!it) {
                Toast.makeText(
                    requireContext(),
                    "Номер уже существует!",
                    Toast.LENGTH_SHORT
                ).show()
                return@observe
            }
            USER.phone = phoneField?.text.toString()
            USER.password = passwordField?.text.toString()
            viewModel.putEditUserSecure(
                userLocalDB.id_user,
                userLocalDB.password,
                USER
            )
        }
    }

    fun ObservePutEditUserLocal(view: BottomSheetDialog){
        viewModel.ApiPutEditUser.observe(requireActivity()) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getLocalDB(view.context).getDao()
                    .updateUser(it.id_user, it.password, true)
                return@launch
            }
            activity?.runOnUiThread {
                isVisibleSecurity(false, view)
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
                })
                dialog.show()

            }else{
                isVisibleSecurity(false, view)
            }
        }
    }

    fun SecurityСonfirmed(view: BottomSheetDialog){
        viewModel.ApiPostAuthentication.observe(requireActivity()) {
            if (it != null) {
                if (USER.password == it.password && USER.phone == it.phone) {
                    isVisibleSecurity(true, view)
                    phoneField = view.findViewById<TextInputEditText>(R.id.phoneField)
                    phoneField?.setText(phone)
                    passwordField = view.findViewById<TextInputEditText>(R.id.passwordField)
                    passwordField?.setText(password)
                    view.findViewById<TextInputEditText>(R.id.passwordFieldStill)?.setText(password)
                    return@observe
                }
            }
            Toast.makeText(requireContext(), "Неправильно введенные данные!",Toast.LENGTH_SHORT).show()
            isVisibleSecurity(false, view)
        }
    }
    private fun isVisibleSecurity(truth: Boolean, view: BottomSheetDialog){

        val chipSecurity = view.findViewById<Chip>(R.id.chipSecurity)
        view.findViewById<TextInputLayout>(R.id.phoneFieldLayout)?.isVisible = truth
        view.findViewById<TextInputLayout>(R.id.passwordFieldLayout)?.isVisible = truth
        view.findViewById<TextInputLayout>(R.id.passwordFieldLayoutStill)?.isVisible = truth

        view.findViewById<Switch>(R.id.switchChange)?.isChecked = truth
        Init(truth, view)

        chipSecurity?.isChecked = truth
        if(!truth) {
            chipSecurity?.text = "Пройти безопасность"
        }else{
            chipSecurity?.text = "Безопасность подтверждена"
        }
    }
    private fun SpinnerGender(view: BottomSheetDialog){
        val spinnerGender = view.findViewById<Spinner>(R.id.spinnerGender)
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
        isVisibleSecurity(true, view)
        isVisibleSecurity(false, view)
        Init(false, view)
        view.findViewById<Switch>(R.id.switchChange)?.setOnCheckedChangeListener{buttonView, isChecked->
            Init(isChecked, view)
            InitUser(view)
        }


    }

    private fun Init(truth:Boolean, view: BottomSheetDialog){

        var chipSecurity =  view.findViewById<Chip>(R.id.chipSecurity)
        chipSecurity?.setChipBackgroundColorResource(R.color.gray_netrual)

        var butSave = view.findViewById<Button>(R.id.butSave)
        var buttonChangeUserImage = view.findViewById<Button>(R.id.buttonChangeUserImage)
        var colorNetral = R.color.gray_netrual
        var colorTeal = R.color.teal_200
        buttonChangeUserImage?.isEnabled = truth
        butSave?.isEnabled = truth

        if (truth){
            buttonChangeUserImage?.setBackgroundColor(view.context.getColor(colorTeal))
            butSave?.setBackgroundColor(view.context.getColor(colorTeal))
            chipSecurity?.setChipBackgroundColorResource(colorTeal)
        }
        else {
            buttonChangeUserImage?.setBackgroundColor(view.context.getColor(colorNetral))
            butSave?.setBackgroundColor(view.context.getColor(colorNetral))
            chipSecurity?.setChipBackgroundColorResource(colorNetral)
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
        chipSecurity?.isEnabled = truth



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