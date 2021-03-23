package com.example.objectiveday.progressbars

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.example.objectiveday.R
import com.example.objectiveday.models.ObjectiveStatus


class ObjectiveProgressBar {

    private var DELAY : Long = 100

    public var mHandler = Handler()
    public var image1 : Bitmap? = null
    public var image2 : Bitmap? = null
    public var image3 : Bitmap? = null
    public var image4 : Bitmap? = null
    public var image5 : Bitmap? = null

    public var todo : Bitmap? = null
    public var done : Bitmap? = null

    public var imageView : ImageView? = null

    private var isRunning : Boolean = false

    private var ObjectiveStatus : ObjectiveStatus? = null

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
        }else{
            if(this.ObjectiveStatus!= null){
                when(this.ObjectiveStatus!!){
                    com.example.objectiveday.models.ObjectiveStatus.TODO -> setAsTodo()
                    com.example.objectiveday.models.ObjectiveStatus.DONE -> setAsDone()
                }
            }
            else{
                //this.imageView!!.visibility = View.GONE
            }
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

        this.done = BitmapFactory.decodeResource(context.resources, R.drawable.ic_check)
        this.todo = BitmapFactory.decodeResource(context.resources, R.drawable.ic_app_todo)
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

    public fun setAsDone(){
        this.imageView!!.setImageBitmap(done!!)
        this.ObjectiveStatus = com.example.objectiveday.models.ObjectiveStatus.DONE
        stopTheAnim()
    }

    public fun setAsTodo(){
        this.imageView!!.setImageBitmap(todo!!)
        this.ObjectiveStatus = com.example.objectiveday.models.ObjectiveStatus.TODO
        stopTheAnim()
    }

    public fun getObjectiveStatus(): ObjectiveStatus?{
        return this.ObjectiveStatus
    }


}