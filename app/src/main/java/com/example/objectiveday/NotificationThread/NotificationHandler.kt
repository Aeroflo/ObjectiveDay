package com.example.objectiveday.NotificationThread

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.objectiveday.NewObjectiveView
import com.example.objectiveday.ObjectiveView
import com.example.objectiveday.TodoActivity
import com.example.objectiveday.controllers.TokenSingleton
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.retrofit.RestAPIService
import java.lang.Exception
import java.lang.StringBuilder

class NotificationHandler {



    companion object {
        var notificationHandler: Handler? = null
        var notificationManager: NotificationManager? = null
        var context: Context? = null
        val CHANNEL_ID: String = "com.example.objectiveday"
        var GROUP_KEY_NOTIFY : String = "objectiveGroups"

        fun setUpHandler(context: Context) {

            this.context = context

            val pattern = longArrayOf(0, 200, 60, 200)
            //val chatSound = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://" + context.packageName + "/" + R.raw.chat_alert)

            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val mChanel =
                    NotificationChannel(CHANNEL_ID, "test", NotificationManager.IMPORTANCE_HIGH)
                //val audioAttributes = AudioAttributes.Builder()
                //   .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                //   .build()
                //mChanel.setSound(chatSound, audioAttributes)
                mChanel.enableVibration(true)
                mChanel.enableLights(true)
                mChanel.vibrationPattern = pattern
                mNotificationManager.createNotificationChannel(mChanel)
                this.notificationManager = mNotificationManager
            }

            //Create handler
            notificationHandler = Handler(Looper.getMainLooper())
            val notificationRunnable: NotificationRunable = NotificationRunable()
            notificationHandler!!.postDelayed(notificationRunnable, 10000)


        }


        class NotificationRunable : java.lang.Runnable {

            override fun run() {
                System.out.println("Call Run ")

                Thread {
                    val apiService = RestAPIService(TokenSingleton.instance.url)
                    var objectivesTodo: List<APIObjectives> =
                        apiService.getObjectives(TokenSingleton.instance.getToken()!!, true)

                    if (objectivesTodo != null && !objectivesTodo.isEmpty()) {
                        sendNotification(getObjectivesToNotify(objectivesTodo))
                    }
                }.start()
                    notificationHandler?.postDelayed(this, 60000)

            }

            fun sendNotification(objectiveModels: List<ObjectiveModel>) {


                if(!objectiveModels.isNullOrEmpty()){
                    var title : StringBuilder = StringBuilder("You have ").append(objectiveModels.size).append(" ")
                    if(objectiveModels.size == 1)
                        title = title.append("important objective to do today!")
                    else
                        title = title.append("important objectives to do today!")


                    val resultIntent = Intent(context!!, TodoActivity::class.java)
                    resultIntent.putExtra("todo", objectiveModels.toTypedArray())
                    val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context!!).run {
                        // Add the intent, which inflates the back stack
                        addNextIntentWithParentStack(resultIntent)
                        // Get the PendingIntent containing the entire back stack
                        getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)
                    }

                    val mBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(title.toString())
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY_NOTIFY)
                        .setContentIntent(resultPendingIntent)
                        .setContentText("")

                    val notification = mBuilder.build()
                    notificationManager?.notify(1, notification)
                }

            }
        }

        fun getObjectivesToNotify(listOfAPIObjectives: List<APIObjectives>?) : List<ObjectiveModel>{
            if(listOfAPIObjectives.isNullOrEmpty()) return mutableListOf<ObjectiveModel>()

            else{
                var todoObjectives = mutableListOf<ObjectiveModel>();
                if (listOfAPIObjectives != null && !listOfAPIObjectives.isEmpty()) {
                    listOfAPIObjectives.filter { o -> o.notify != null && o.notify == true }
                        .forEach{o -> todoObjectives.add(o.toModel())}

                }
                return todoObjectives
            }
        }
    }




}