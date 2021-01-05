package com.example.objectiveday

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.NotificationThread.NotificationHandler
import com.example.objectiveday.adapters.ObjectiveListAdapter
import com.example.objectiveday.controllers.ObjectiveHandlers
import com.example.objectiveday.database.ObjectiveDBHelper
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.databinding.ObjectiveListLayoutBinding
import com.example.objectiveday.models.ObjectiveModel
import kotlinx.android.synthetic.main.objective_list_layout.*
import kotlinx.android.synthetic.main.objective_list_layout.view.*

class MainActivity : AppCompatActivity() {

    private var notificationManager : NotificationManager?=null

    internal var dbHelper = ObjectiveDBHelper(this)

    var objectiveList : List<ObjectiveModel> = ArrayList<ObjectiveModel>()
    var listAdapter : ObjectiveListAdapter? = null

    //Handler
    lateinit var mainHandler :Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        //load objectives
        //this.objectiveList = dbHelper.getAllObjective()

        val mainBinding: ObjectiveListLayoutBinding  = DataBindingUtil.setContentView(this, R.layout.objective_list_layout)



        val list = arrayListOf<ObjectiveModel>()
        for (i in 0..100) {
            var objectiveModel : ObjectiveModel = ObjectiveModel.Builder()
                .withDescription("This is a new objective test "+i)
                .withId(0)
                .withDayChecker(i)
                .withNbTriggered(100)
                .withNbDone(i)
                .build()

            //objectiveModel.getNextDate(0)
            list.add(objectiveModel)
        }
        this.objectiveList = list
        listAdapter = ObjectiveListAdapter(applicationContext, this.objectiveList)


        mainBinding.root.listview.adapter = listAdapter
       /*mainBinding.root.listview.setOnItemClickListener(){
                adapterView, view, position: Int, id: Long ->

            callPopUpObjective(adapterView.getItemAtPosition(position))
        }*/
        mainBinding.root.listview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            System.out.println("Touch :"+id)
        })

        //mainBinding.root.listview.setOnTouch
        /*mainBinding.root.listview.setOnTouchListener( object : View.OnTouchListener {

            var downX: Float? = null
            var upX : Float? = null
            val deltaX : Float = 100.0F
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when (event?.action) {
                    MotionEvent.ACTION_MOVE  -> {}
                    MotionEvent.ACTION_DOWN -> {downX = event.getRawX()}
                    MotionEvent.ACTION_UP -> {
                        val x : Float = event.getRawX();
                        var foundDeltaX :Float? = downX?.minus(x)
                        if(foundDeltaX != null && this.deltaX < foundDeltaX){
                            System.out.println("Left")
                        }else if( foundDeltaX != null && this.deltaX > foundDeltaX){
                            System.out.println("Right")
                        }
                    }
                    //MotionEvent.ACTION_
                    //MotionEvent.ACTION_DOWN -> //Do Something
                }
                return v?.onTouchEvent(event) ?: true
            }


        })*/



        /*binding.setOnItemClick { adapterView, view, position, l ->
            Toast.makeText(this, listAdapter.kemonoFriends[position].kemonoName, Toast.LENGTH_SHORT).show()
        }*/

        //mainBinding.objectiveMainModel = objectiveModel


        /*var userModel = UserModel()
        userModel.uName = "Androidian"
        userModel.pwd = "123456"
        mainBinding.userModel = userModel*/

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


        /*val mOnNavigationItemSelectedListener =  .OnNavigationItemSelectedListener{ item ->
            when(item.itemId){
                R.id.action_new -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_settings -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }*/

        //toolbar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel("com.example.objectiveday", "Notif Test", "Example")
        //sendNotification()

        NotificationHandler.setUpHandler(notificationManager, this@MainActivity)
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

    fun sendNotification(){
        val notificationID = 101
        val channelID = "com.example.objectiveday"

        val notification = Notification.Builder(this@MainActivity, channelID)
            .setContentTitle("Example Notif")
            .setContentText("This is an example")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .build()

        notificationManager?.notify(notificationID, notification)
    }


    private fun callObjectiveView(objectiveModel: Any?) {
        if(objectiveModel is ObjectiveModel){
            val currentObjectiveModel : ObjectiveModel = objectiveModel
            var objectiveIntent = Intent(this, ObjectiveView::class.java)
            objectiveIntent.putExtra("objectiveModel", currentObjectiveModel)
            startActivity(objectiveIntent)
        }
    }

    private fun callPopUpObjective(objectiveModel: Any?){
        var binding: ObjectiveBinding =DataBindingUtil.setContentView(this, R.layout.objective)
        binding.objectiveMainModel = objectiveModel as ObjectiveModel?
        binding.objectiveHandlers = ObjectiveHandlers(this.applicationContext)
        val popupWindow = PopupWindow(
            binding.root, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        binding.mondayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.tuesdayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.wednesdayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.thursdayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.fridayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }

        binding.saturdayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekEndCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.sundayChk.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekEndCheckBoxAndValidate(binding, buttonView, isChecked) }

        binding.weekDaysSw.setOnClickListener{view -> verifySwitchAndValidate(binding, view)}
        binding.weekEndSw.setOnClickListener{view -> verifySwitchAndValidate(binding, view) }//{buttonView, isChecked -> verifySwitchAndValidate(binding, buttonView, isChecked)}
    }

    //Best way to do this.
    fun verifyWeekDayCheckBoxAndValidate(binding: ObjectiveBinding, v: View, isChecked : Boolean ){
        //GetModel
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        var viewName: String = this.applicationContext.resources.getResourceEntryName(v.id)

        if(objectiveModel != null) {
            try {
                v as CheckBox
                when {
                    viewName.equals("monday_chk") -> {
                        objectiveModel.isMonday = v.isChecked
                    }
                    viewName.equals("tuesday_chk") -> {
                        objectiveModel.isTuesday = v.isChecked
                    }
                    viewName.equals("wednesday_chk") -> {
                        objectiveModel.isWednesday = v.isChecked
                    }
                    viewName.equals("thursday_chk") -> {
                        objectiveModel.isThursday = v.isChecked
                    }
                    viewName.equals("friday_chk") -> {
                        objectiveModel.isFriday = v.isChecked
                    }
                }
                binding.weekDaysSw.isChecked = objectiveModel.isWeekDaySet()
                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun verifyWeekEndCheckBoxAndValidate(binding: ObjectiveBinding, v: View, isChecked : Boolean ){
        //GetModel
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        var viewName: String = this.applicationContext.resources.getResourceEntryName(v.id)

        if(objectiveModel != null) {
            try {
                v as CheckBox
                when {
                    viewName.equals("saturday_chk") -> {
                        objectiveModel.isSaturday = v.isChecked
                    }
                    viewName.equals("sunday_chk") -> {
                        objectiveModel.isSunday = v.isChecked
                    }
                }
                binding.weekEndSw.isChecked = objectiveModel.isWeekEndSet()
                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun verifySwitchAndValidate(binding: ObjectiveBinding, v: View){
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        var viewName: String = this.applicationContext.resources.getResourceEntryName(v.id)

        if(objectiveModel != null) {
            try {
                v as Switch
                when {
                    viewName.equals("week_days_sw") -> {
                        var isChecked = v.isChecked

                        objectiveModel.isMonday = isChecked
                        binding.mondayChk.isChecked = isChecked

                        objectiveModel.isTuesday = isChecked
                        binding.tuesdayChk.isChecked = isChecked

                        objectiveModel.isWednesday = isChecked
                        binding.wednesdayChk.isChecked = isChecked

                        objectiveModel.isThursday = isChecked
                        binding.thursdayChk.isChecked = isChecked

                        objectiveModel.isFriday = isChecked
                        binding.fridayChk.isChecked = isChecked
                    }
                    viewName.equals("week_end_sw") -> {
                        var isChecked = v.isChecked

                        objectiveModel.isSaturday = isChecked
                        binding.saturdayChk.isChecked = isChecked

                        objectiveModel.isSunday = isChecked
                        binding.sundayChk.isChecked = isChecked
                    }
                }
                binding.weekDaysSw.isChecked = objectiveModel.isWeekDaySet()
                binding.weekEndSw.isChecked = objectiveModel.isWeekEndSet()
                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId){
            R.id.action_new -> {
                var newObjectiveModel : ObjectiveModel = ObjectiveModel.Builder().build()
                var objectiveIntent = Intent(this, ObjectiveView::class.java)
                objectiveIntent.putExtra("objectiveModel", newObjectiveModel)
                startActivity(objectiveIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item);
        }
    }

    override fun onResume() {

        //Reload and validate?
        super.onResume()
        /*if(this.listAdapter != null){
            this.listAdapter!!.updateDateSource(dbHelper.getAllObjective())
            this.listAdapter!!.notifyDataSetChanged()
        }*/
    }
}
