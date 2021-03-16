package com.example.objectiveday.controllers

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.objectiveday.R
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.progressbars.ObjectiveProgressBar

class ButtonSignIn {

    var context:Context? = null
    var cardView : CardView? = null
    var constraintLayout : ConstraintLayout? = null
    var signInText : TextView? = null
    var progressBar : View? = null

    var runnable : ObjectiveProgressBar? = null

    var mode : SignInButtonMode = SignInButtonMode.SIGN_IN

    constructor(context : Context, view : View){
        this.context = context
        this.constraintLayout = view.findViewById(R.id.signinBTN_ContraintLayout)
        this.cardView = view.findViewById(R.id.signInBTN_CV)
        this.progressBar = view.findViewById(R.id.signinBTN_progressbar)
        this.runnable =  ObjectiveProgressBar(progressBar!!, this.context!!)
        this.signInText = view.findViewById(R.id.signinBTN_text)
    }

    fun buttonActivated(){
        //cardView!!.isClickable = false
        //cardView!!.invalidate()
        progressBar!!.visibility = View.VISIBLE
        runnable!!.startTheAnim()
        constraintLayout!!.setBackgroundColor(Color.WHITE)
        signInText!!.text =mode.runningValue
    }

    fun buttonFinished(success: Boolean){
        //cardView!!.isClickable = true
        //cardView!!.invalidate()
        progressBar!!.visibility = View.GONE
        runnable!!.stopTheAnim()
        if(success){
            when(mode){
                SignInButtonMode.SAVE -> constraintLayout!!.setBackgroundColor(Color.GREEN)
                SignInButtonMode.SIGN_IN -> constraintLayout!!.setBackgroundColor(Color.WHITE)
                SignInButtonMode.REGISTER_DEVICE -> constraintLayout!!.setBackgroundColor(Color.GREEN)
                SignInButtonMode.NEW_USER -> constraintLayout!!.setBackgroundColor(Color.GREEN)
            }
            //constraintLayout!!.setBackgroundColor(Color.WHITE)
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

    fun setModeSave(){
        this.mode = SignInButtonMode.SAVE
        this.signInText!!.text = mode.pressValue
        constraintLayout!!.setBackgroundColor((Color.WHITE))
    }

    fun setModeGenerateQRCode(){
        this.mode = SignInButtonMode.GENERATE_QR
        this.signInText!!.text = mode.pressValue
        constraintLayout!!.setBackgroundColor((Color.WHITE))
    }

    fun setModeTodo(objectiveModel: ObjectiveModel?){
        this.mode = SignInButtonMode.TODO

        if(objectiveModel == null || !objectiveModel.isObjectiveDoneToday()) {
            this.signInText!!.text = mode.pressValue
            constraintLayout!!.setBackgroundColor((Color.WHITE))
        }
        else{
            this.constraintLayout!!.isClickable = false
            this.signInText!!.text = mode.success
            constraintLayout!!.setBackgroundColor((Color.GREEN))
        }
        //if(objectiveModel.la)
    }
}