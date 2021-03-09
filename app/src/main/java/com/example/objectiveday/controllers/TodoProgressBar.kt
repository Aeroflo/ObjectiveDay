package com.example.objectiveday.controllers

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.objectiveday.R

class TodoProgressBar {

    var context: Context? = null
    var markAsDoneCardView : CardView? =null
    var constraintLayoutMarkAsDone : ConstraintLayout? = null
    var markAsDoneText : TextView? = null
    var progressBar : ProgressBar? =null

    constructor(context: Context, view : View){
        this.context = context
        this.markAsDoneCardView = view.findViewById(R.id.todoBTN_CV)
        this.constraintLayoutMarkAsDone = view.findViewById(R.id.todoBTN_ContraintLayout)
        this.markAsDoneText = view.findViewById(R.id.todoBTN_text)
        this.progressBar = view.findViewById(R.id.todoBTN_progressbar)
    }

    fun buttonActivated(){
        progressBar!!.visibility = View.VISIBLE
        markAsDoneText!!.text = "Saving"
    }

    fun buttonFinished(success : Boolean){
        progressBar!!.visibility = View.GONE
        if(success){
            constraintLayoutMarkAsDone!!.setBackgroundColor(Color.GREEN)
            markAsDoneText!!.text = "DONE"
        }else{
            constraintLayoutMarkAsDone!!.setBackgroundColor(Color.RED)
            markAsDoneText!!.text = "ERROR"
        }
    }
}