package com.mne4.fromandto

import android.R
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mne4.fromandto.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.textView2.setSelected(true)
//        binding.textView.setSelected(true)

        /*var db = MainDB.getDB(this)

        db.getDao().getAllUser().asLiveData().observe(this){
            binding.edMessage.text = ""
            it.forEach{
                val text = "Id:${it.id} id_user:${it.id_user}\n" +
                        "Surname: ${it.surname} Name: ${it.name}\n" +
                        "birthday: ${it.birthday} phone: ${it.phone}\n" +
                        "isInAcc: ${it.isInAcc} gender: ${it.gender}\n"
                binding.edMessage.append(text)
            }
        }
serfse
        binding.button.setOnClickListener{
            var user = User(
                null,
                "3453-34534-34534-535",
                "Николаев",
                "Вячеслав",
                "27:10:2003",
                "Мужской",
                "89645843532",
                true
            )
            CoroutineScope(Dispatchers.IO).launch {
                db.getDao().insertUser(user)
            }
        }
*/

    }
}