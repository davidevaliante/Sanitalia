package com.hub.toolbox.mtg.sanitalia.access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import aqua.extensions.makeActivityFullScreen
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.access.providers.RegistrationActivity
import com.hub.toolbox.mtg.sanitalia.constants.UserType
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import kotlinx.android.synthetic.main.activity_user_type_choice.*

class UserTypeChoiceActivity : AppCompatActivity() {

    private val registrationType = "REGISTRATION_TYPE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeActivityFullScreen()
        setContentView(R.layout.activity_user_type_choice)

        operatorChoiceButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra(registrationType, UserType.OPERATOR as Parcelable)
            startActivity(intent)
        }

        userChoiceButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra(registrationType, UserType.USER as Parcelable)
            startActivity(intent)
        }

        skipChoiceButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(registrationType, UserType.ANONYMOUS as Parcelable)
            startActivity(intent)
        }
    }
}
