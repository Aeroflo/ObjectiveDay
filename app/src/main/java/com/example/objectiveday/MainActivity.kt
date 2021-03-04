package com.example.objectiveday

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.*
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.adapters.ObjectiveListAdapter
import com.example.objectiveday.controllers.ObjectiveHandlers
import com.example.objectiveday.controllers.TokenSingleton
import com.example.objectiveday.database.ObjectiveDBHelper
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIToken
import com.example.objectiveday.webservices.apimodels.APIUserDeviceModel
import com.example.objectiveday.webservices.retrofit.RestAPIService
import com.google.android.material.chip.Chip


class MainActivity : AppCompatActivity() {

    private var notificationManager : NotificationManager?=null

    internal var dbHelper = ObjectiveDBHelper(this)

    var objectiveList : List<ObjectiveModel> = ArrayList<ObjectiveModel>()
    var listAdapter : ObjectiveListAdapter? = null

    //Handler
    lateinit var mainHandler :Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main);

        val urlText : EditText = findViewById(R.id.ip)
        urlText.setText("http://192.168.1.73:8080")

        //API
        val apiService = RestAPIService(urlText.text.toString())

        //VIEWS
        val issues : TextView = findViewById(R.id.issues);
        val emailText = findViewById(R.id.edtUserEmail) as EditText
        val signInBtn : Button = findViewById(R.id.signin)
        val newUserChip : Chip = findViewById(R.id.newuser)


        newUserChip.setOnClickListener(){
            if(newUserChip.isChecked){
                newUserChip.isCheckedIconVisible = true
                emailText.visibility = View.VISIBLE
                signInBtn.text = "REGISTER: user / device"
                signInBtn.invalidate()
                emailText.invalidate()
            }else{
                emailText.visibility = View.GONE
                emailText.invalidate()
                signInBtn.text = "SIGN IN"
                signInBtn.invalidate()
            }
        }

        val registerDevice : Chip = findViewById(R.id.registerdevice)
        registerDevice.setOnClickListener(){
            if(registerDevice.isChecked){
                registerDevice.isCheckedIconVisible = true
                emailText.visibility = View.VISIBLE
                emailText.invalidate()
                signInBtn.text = "REGISTER: device"
                signInBtn.invalidate()
            }else{
                emailText.visibility = View.GONE
                emailText.invalidate()
                signInBtn.text = "SIGN IN"
                signInBtn.invalidate()
            }
        }



        signInBtn.setOnClickListener{
            var username : String = ""
            var password : String = ""
            var email : String = ""

            var  allIsSet : Boolean = true;

            val shape = ShapeDrawable(RectShape())
            shape.paint.color = Color.RED
            shape.paint.style = Paint.Style.STROKE
            shape.paint.strokeWidth = 3f

            val userNameText = findViewById(R.id.edtUserName) as EditText
            if(userNameText.text == null || userNameText.text.toString().isEmpty()){
                userNameText.setBackground(shape)
                userNameText.invalidate()
                allIsSet = false
            }else{
                username = userNameText.text.toString()
            }

            val passwordText = findViewById(R.id.edtUserPassword) as EditText
            if(passwordText.text == null || passwordText.text.toString().isEmpty()){
                passwordText.setBackground(shape)
                passwordText.invalidate()
                allIsSet = false
            }else{
                password = passwordText.text.toString()
            }

            if(newUserChip.isChecked || registerDevice.isChecked) {
                if (emailText.text == null || emailText.text.toString().isEmpty()) {
                    emailText.setBackground(shape)
                    emailText.invalidate()
                    allIsSet = false
                } else {
                    email = emailText.text.toString()
                }
            }


            if(allIsSet) {
                val androidSerialNumber: String = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
                );


                val url : String = urlText.text.toString();
                if(newUserChip.isChecked){
                    val userDeviceModel: APIUserDeviceModel=
                        APIUserDeviceModel(username, androidSerialNumber, password, email)
                    apiService.fullRegister(userDeviceModel){
                        if(it == null){
                            if(it == null){
                                issues.setText("Cannot create account / device!")
                                issues.visibility = View.VISIBLE
                                issues.invalidate()
                            }
                        }
                        else{
                            Toast.makeText(this.applicationContext, "User/Device created", Toast.LENGTH_LONG).show()
                            //reset
                            issues.visibility = View.GONE
                            newUserChip.isChecked = false
                            emailText.visibility = View.GONE
                            emailText.invalidate()
                            signInBtn.text = "SIGN IN"
                            signInBtn.invalidate()
                        }
                    }
                }else if(registerDevice.isChecked){
                    val userDeviceModel: APIUserDeviceModel=
                        APIUserDeviceModel(username, androidSerialNumber, password, null)
                }else {

                    try{
                        Thread {
                            val userDeviceModel: APIUserDeviceModel =
                                APIUserDeviceModel(username, androidSerialNumber, password, null)
                            TokenSingleton.instance.userDeviceModel = userDeviceModel
                            TokenSingleton.instance.url = urlText.text.toString()
                            var token: APIToken? = TokenSingleton.instance.getToken()
                            if (token == null) {
                                runOnUiThread {
                                    issues.setText("Invalid user name and password!")
                                    issues.visibility = View.VISIBLE
                                    issues.invalidate()
                                }
                                vibratePhoneWrong()
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this.applicationContext,
                                        "Authenticated ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                val myIntent = Intent(this, ObjectiveView::class.java)
                                this.startActivity(myIntent)

                            }
                        }.start()
                    }catch(e : java.lang.Exception){
                        issues.setText("Internal error :"+e.message)
                        issues.visibility = View.VISIBLE
                        issues.invalidate()
                    }
                }
            }
            else{
                vibratePhoneWrong()
            }


        }
    }

    fun vibratePhoneWrong(){
        val vibrator = this.applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            val mVibratePattern =
                longArrayOf(0, 100)
            val effect = VibrationEffect.createWaveform(mVibratePattern, -1)
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(200)
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
            R.id.action_settings -> {
                System.out.println("Settings")
                var settings :LinearLayout = findViewById(R.id.main_settings)
                settings.setVisibility(View.VISIBLE);
                settings.invalidate();
                return true
            }
            R.id.demo -> {
                    Toast.makeText(this.applicationContext, "Access demo ", Toast.LENGTH_LONG).show()
                    val myIntent = Intent(this, ObjectiveView::class.java)
                    myIntent.putExtra("isDemo", "true") //Optional parameters
                    this.startActivity(myIntent)
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
