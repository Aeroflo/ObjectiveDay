package com.example.objectiveday

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.controllers.ObjectiveBindingController
import com.example.objectiveday.databinding.ObjectiveMainObjectLayoutBinding
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIToken
import java.lang.Exception

class NewObjectiveView : AppCompatActivity() {

    var apiToken : APIToken? = null
    var url : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent

        try{
            apiToken = intent.getSerializableExtra("token") as APIToken
            url = intent.getStringExtra("url")
        }catch(e : Exception){

        }



        var controller :ObjectiveBindingController = ObjectiveBindingController(this.applicationContext, this@NewObjectiveView, this.supportFragmentManager)
        var objectiveBinding : ObjectiveMainObjectLayoutBinding = DataBindingUtil.setContentView(this, R.layout.objective_main_object_layout)

        //objectiveBinding.saveProgress!!.getViewById(R.id.signinBTN_text)

        var objectiveModel : ObjectiveModel? = intent.getSerializableExtra("objective") as ObjectiveModel?
        if(objectiveModel != null){
            objectiveBinding  = controller.prepareBinding(objectiveBinding, objectiveModel)!!
            objectiveBinding.moreDetailsLayout.visibility = View.VISIBLE
            objectiveBinding.details.isEnabled = false
            objectiveBinding.moreDetailsLayout.isEnabled = false
            objectiveBinding.descriptionTxt.isEnabled = false
            objectiveBinding.qrCode.visibility = View.GONE

            //var textviewSave : TextView = objectiveBinding.saveProgress.getViewById(R.id.saveTextView) as TextView
            //textviewSave.text = "Add this objective in  my list"
        }
        else{
            objectiveBinding  = controller.prepareBinding(objectiveBinding, ObjectiveModel.Builder().build())!!
            objectiveBinding.moreDetailsLayout.visibility = View.VISIBLE
            objectiveBinding.details.isEnabled = false
            objectiveBinding.descriptionTxt.isEnabled = true
        }


        objectiveBinding.invalidateAll()
    }

    override fun onBackPressed() {
        val myIntent = Intent(this, ObjectiveView::class.java)
        this.startActivity(myIntent)
    }


}