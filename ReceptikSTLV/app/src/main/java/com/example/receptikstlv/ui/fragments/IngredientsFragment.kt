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
import com.example.receptikstlv.data.models.Ingredient
import com.example.receptikstlv.ui.adapters.IngredientsAdapter

class IngredientsFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var ingredientsRecyclerView: RecyclerView
    private var recipeId: Int = 0

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int): IngredientsFragment {
            val fragment = IngredientsFragment()
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
        rootView = inflater.inflate(R.layout.fragment_ingredients, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        initViews()
        loadIngredients()

        return rootView
    }

    private fun initViews() {
        ingredientsRecyclerView = rootView.findViewById(R.id.ingredientsRecyclerView)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadIngredients() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_INGREDIENTS,
            null,
            "${DatabaseHelper.COLUMN_RECIPE_ID} = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            null
        )

        val ingredients = mutableListOf<Ingredient>()
        while (cursor.moveToNext()) {
            ingredients.add(Ingredient(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                recipeId = recipeId,
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                amount = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
            ))
        }
        cursor.close()

        ingredientsRecyclerView.adapter = IngredientsAdapter(ingredients)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}