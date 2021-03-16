package com.example.objectiveday.progressbars

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.example.objectiveday.R


class ObjectiveProgressBar {

    private var DELAY : Long = 100

    public var mHandler = Handler()
    public var image1 : Bitmap? = null
    public var image2 : Bitmap? = null
    public var image3 : Bitmap? = null
    public var image4 : Bitmap? = null
    public var image5 : Bitmap? = null

    public var imageView : ImageView? = null

    private var isRunning : Boolean = false

    private var runnable : Runnable = Runnable {


        when(level){
            1 -> {
                this.imageView!!.setImageBitmap(image1!!)
                level = level +1
            }

            2 -> {
                this.imageView!!.setImageBitmap(image2)
                level = level +1
            }

            3 -> {
                this.imageView!!.setImageBitmap(image3)
                level = level +1
            }

            4 -> {
                this.imageView!!.setImageBitmap(image4)
                level = level +1
            }

            5 -> {
                this.imageView!!.setImageBitmap(image5)
                level = 1
            }

        }

        if(isRunning) {
            doTheAnim()
        }

    }

    private var level = 1


    constructor(view : View, context: Context){
        this.imageView = view.findViewById(R.id.objectiveProgressBarPicture)

        this.image1  = BitmapFactory.decodeResource(context.resources, R.drawable.pb_1)
        this.image2  = BitmapFactory.decodeResource(context.resources, R.drawable.pb_2)
        this.image3  = BitmapFactory.decodeResource(context.resources, R.drawable.pb_3)
        this.image4  = BitmapFactory.decodeResource(context.resources, R.drawable.pb_4)
        this.image5  = BitmapFactory.decodeResource(context.resources, R.drawable.pb_5)
        doTheAnim()
    }

    public fun startTheAnim(){
        this.isRunning = true
        mHandler.post(runnable);
    }
    public fun doTheAnim(){

        mHandler.postDelayed(runnable, DELAY);
    }

    public fun stopTheAnim(){
        this.isRunning = false
    }


}