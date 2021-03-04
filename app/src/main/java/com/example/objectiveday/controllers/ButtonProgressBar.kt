package com.example.objectiveday.controllers

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.objectiveday.R
import kotlinx.android.synthetic.main.buttonprogressbar.view.*

class ButtonProgressBar {

    var context:Context? = null

    var cardView : CardView?= null
    var constraintLayout : ConstraintLayout? = null
    var text : TextView? = null
    var progressBar : ProgressBar? = null

    constructor(context: Context,view : View){
        this.context = context
        this.constraintLayout = view.findViewById(R.id.saveContraintLayout)
        this.cardView = view.findViewById(R.id.saveCardView)
        this.progressBar = view.findViewById(R.id.saveProgressBar)
        this.text = view.findViewById(R.id.saveTextView)
    }

    fun buttonActivated(){
        progressBar!!.visibility = View.VISIBLE
        constraintLayout!!.setBackgroundColor(Color.WHITE)
        text!!.text = "Saving wait..."
    }

    fun buttonFinished(success: Boolean){
        if(success) {
            constraintLayout!!.setBackgroundColor(Color.GREEN)
            progressBar!!.visibility = View.GONE
            text!!.text = "Saved"
        }else{
            constraintLayout!!.setBackgroundColor(Color.RED)
            progressBar!!.visibility = View.GONE
            text!!.text = "Not saved"
        }
    }
}