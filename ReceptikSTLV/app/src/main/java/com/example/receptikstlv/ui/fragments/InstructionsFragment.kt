package com.example.receptikstlv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.database.Cursor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptikstlv.R
import com.example.receptikstlv.data.DatabaseHelper
import com.example.receptikstlv.data.models.Step
import com.example.receptikstlv.ui.adapters.StepsAdapter

class InstructionsFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var stepsRecyclerView: RecyclerView
    private var recipeId: Int = 0

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int): InstructionsFragment {
            val fragment = InstructionsFragment()
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt(ARG_RECIPE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_instructions, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        initViews()
        loadSteps()

        return rootView
    }

    private fun initViews() {
        stepsRecyclerView = rootView.findViewById(R.id.stepsRecyclerView)
        stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadSteps() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_STEPS,
            null,
            "${DatabaseHelper.COLUMN_RECIPE_ID} = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_STEP_NUMBER} ASC"
        )

        val steps = mutableListOf<Step>()
        while (cursor.moveToNext()) {
            steps.add(Step(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                recipeId = recipeId,
                stepNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_NUMBER)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
            ))
        }
        cursor.close()

        stepsRecyclerView.adapter = StepsAdapter(steps)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}