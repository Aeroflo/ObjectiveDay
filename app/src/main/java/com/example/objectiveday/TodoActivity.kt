package com.example.objectiveday

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.adapters.ObjectiveListAdapter
import com.example.objectiveday.adapters.TodoAdapter
import com.example.objectiveday.databinding.ObjectiveListLayoutBinding
import com.example.objectiveday.models.ObjectiveModel
import kotlinx.android.synthetic.main.objective_list_layout.view.*
import java.lang.Exception

class TodoActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent

        var list  : List<ObjectiveModel> = ArrayList<ObjectiveModel>()

        try{
            var objectiveTodo = intent!!.getSerializableExtra("todo") as Array<ObjectiveModel>
            list = objectiveTodo.asList()
        }catch(e : Exception){

        }

        val todoBinding: ObjectiveListLayoutBinding = DataBindingUtil.setContentView(this, R.layout.objective_list_layout)
        todoBinding.root.listview.adapter = TodoAdapter(applicationContext, list)

    }

    override fun onBackPressed() {
        val myIntent = Intent(this, ObjectiveView::class.java)
        this.startActivity(myIntent)
    }
}