package com.example.objectiveday


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.NotificationThread.NotificationHandler
import com.example.objectiveday.adapters.ObjectiveListAdapter
import com.example.objectiveday.controllers.ObjectiveWeekController
import com.example.objectiveday.controllers.TokenSingleton
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.databinding.ObjectiveListLayoutBinding
import com.example.objectiveday.internalData.DataSingleton
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.apimodels.APIToken
import com.example.objectiveday.webservices.retrofit.RestAPIService
import kotlinx.android.synthetic.main.objective.*
import kotlinx.android.synthetic.main.objective_list_layout.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class ObjectiveView : AppCompatActivity() {

    var objectiveList : List<ObjectiveModel> = ArrayList<ObjectiveModel>()
    var listAdapter : ObjectiveListAdapter? = null

    var apiToken : APIToken? = null
    var url : String? = null
    var isDemo : String? =null

    init{
        System.out.println("INIT CALL")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        System.out.println("ON CREATE CALL")
         super.onCreate(savedInstanceState)

        val intent = intent
        isDemo = intent.getStringExtra("isDemo")
        try{
            apiToken = intent.getSerializableExtra("token") as APIToken
            url = intent.getStringExtra("url")
        }catch(e : Exception){

        }

        val mainBinding: ObjectiveListLayoutBinding  = DataBindingUtil.setContentView(this, R.layout.objective_list_layout)



        var list  = arrayListOf<ObjectiveModel>()
        this.objectiveList = list
        listAdapter = ObjectiveListAdapter(applicationContext, this.objectiveList)


        mainBinding.root.listview.adapter = listAdapter
        mainBinding.root.listview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            System.out.println("Touch :"+id)
        })


        if(isDemo != null){
            updateObjectiveUIFromAPI() //test
            /*for (i in 0..100) {
                var objectiveModel: ObjectiveModel = ObjectiveModel.Builder()
                    .withDescription("This is a new objective test " + i)
                    .withId(0)
                    .withDayChecker(i)
                    .withNbTriggered(100)
                    .withNbDone(i)
                    .build()

                //objectiveModel.getNextDate(0)
                list.add(objectiveModel)
            }*/
            updateListUI(list)
        }else{
                System.out.println("GET OVBJECTIVE FROM API")
                updateObjectiveUIFromAPI()
        }

        if(NotificationHandler.notificationHandler == null) {
            NotificationHandler.setUpHandler(this.applicationContext)
        }
        hideKeyboard()
    }

    fun updateObjectiveUIFromAPI(){
            Thread {
                val apiService = RestAPIService(TokenSingleton.instance.url)
                objectiveList = arrayListOf<ObjectiveModel>()
                var objectiveModels: List<APIObjectives> = DataSingleton.instance.getData()//apiService.getObjectives(TokenSingleton.instance.getToken()!!, null)
                objectiveModels.forEach { o ->
                    (objectiveList as ArrayList<ObjectiveModel>).add(o.toModel())
                }
                runOnUiThread {updateListUI(objectiveList)}
            }.start()

        return
    }

    fun updateListUI(objectivesModels : List<ObjectiveModel>){
        this.listAdapter?.updateDataSource(objectivesModels);
        //this.listAdapter?.notifyDataSetChanged()
    }

    fun popToast(msg : String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_objective, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_new -> {
                val myIntent = Intent(this, NewObjectiveView::class.java)
                myIntent.putExtra("token", apiToken) //Optional parameters
                myIntent.putExtra("url", url)
                this.startActivity(myIntent)
                return true;
            }
            R.id.scan_objective ->{
                val myIntent = Intent(this, QRScannerActivity::class.java)
                myIntent.putExtra("token", apiToken) //Optional parameters
                myIntent.putExtra("url", url)
                this.startActivity(myIntent)
                return true
            }
            R.id.todo -> {
                //Load objective to be done and place in intent --> List APIObjectives OR ObjectiveModel
                Thread{
                    val apiService = RestAPIService(TokenSingleton.instance.url)
                    var objectivesNotify: List<APIObjectives> = DataSingleton.instance.getObjectifTodoToday()
                        //apiService.getObjectives(TokenSingleton.instance.getToken()!!, true)

                    val todoObjectives = mutableListOf<ObjectiveModel>();
                    if (objectivesNotify != null && !objectivesNotify.isEmpty()) {
                       objectivesNotify.forEach{o -> todoObjectives.add(o.toModel())}
                    }else{

                    }

                    runOnUiThread{
                        var intent : Intent = Intent(this, TodoActivity::class.java)
                        intent.putExtra("todo", todoObjectives.toTypedArray())
                        this.startActivity(intent)
                    }
                }.start()
                return true;
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}