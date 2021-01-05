package com.example.objectiveday.NotificationThread

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper

class NotificationHandler {

    companion object{
        var notificationHandler : Handler? = null
        var notificationManager : NotificationManager?=null
        var context: Context?=null

        fun setUpHandler(notificationManager:NotificationManager?, context : Context?){
            if(notificationManager == null || context == null) return
            if(notificationHandler == null){
                this.notificationManager = notificationManager
                this.context = context

                notificationHandler = Handler(Looper.getMainLooper())
                val notificationRunnable : NotificationRunable = NotificationRunable()
                createNotificationChannel("com.example.objectiveday", "Notif Test", "Example")
                notificationHandler!!.postDelayed(notificationRunnable, 5000)
            }
        }

        private fun createNotificationChannel(id:String, name:String, description:String){
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(id, name, importance)

            channel.description =description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 400, 500, 400, 300, 200, 400)
            notificationManager?.createNotificationChannel(channel)

        }


    }

    class NotificationRunable :java.lang.Runnable {
        override fun run() {
            System.out.println("Call Run ")
            sendNotification()
            notificationHandler?.postDelayed(this, 5000)
        }

        fun sendNotification(){
            val notificationID = 101
            val channelID = "com.example.objectiveday"

            val notification = Notification.Builder(NotificationHandler.context, channelID)
                .setContentTitle("Example Notif")
                .setContentText("This is an example")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setChannelId(channelID)
                //.setVibrate(longArrayOf(100, 200, 400, 500, 400, 300, 200, 400))
                .build()

            notificationManager?.notify(notificationID, notification)
        }
    }



}