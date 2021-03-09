package com.example.objectiveday.controllers

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.objectiveday.R

class ButtonSignIn {

    var context:Context? = null
    var cardView : CardView? = null
    var constraintLayout : ConstraintLayout? = null
    var signInText : TextView? = null
    var progressBar : ProgressBar? = null

    var mode : SignInButtonMode = SignInButtonMode.SIGN_IN

    constructor(context : Context, view : View){
        this.context = context
        this.constraintLayout = view.findViewById(R.id.signinBTN_ContraintLayout)
        this.cardView = view.findViewById(R.id.signInBTN_CV)
        this.progressBar = view.findViewById(R.id.signinBTN_progressbar)
        this.signInText = view.findViewById(R.id.signinBTN_text)
    }

    fun buttonActivated(){
        //cardView!!.isClickable = false
        //cardView!!.invalidate()
        progressBar!!.visibility = View.VISIBLE
        constraintLayout!!.setBackgroundColor(Color.WHITE)
        signInText!!.text =mode.runningValue
    }

    fun buttonFinished(success: Boolean){
        //cardView!!.isClickable = true
        //cardView!!.invalidate()
        progressBar!!.visibility = View.GONE
        if(success){
            constraintLayout!!.setBackgroundColor(Color.WHITE)
            signInText!!.text = mode.success
        }
        else{
            constraintLayout!!.setBackgroundColor(Color.RED)
            signInText!!.text = mode.errorValue
        }
    }

    fun buttonSetText(text:String){
        signInText!!.text = text
    }

    fun setModeRegisterDevice(){
        this.mode = SignInButtonMode.REGISTER_DEVICE
        this.signInText!!.text = mode.pressValue
        constraintLayout!!.setBackgroundColor(Color.WHITE)

    }
    fun setModeNewUserDevice(){
        this.mode = SignInButtonMode.NEW_USER
        this.signInText!!.text = mode.pressValue
        constraintLayout!!.setBackgroundColor(Color.WHITE)
    }
    fun setModeSignIn(){
        this.mode = SignInButtonMode.SIGN_IN
        this.signInText!!.text = mode.pressValue
        constraintLayout!!.setBackgroundColor(Color.WHITE)
    }
}