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
import com.example.receptikstlv.data.models.*
import com.example.receptikstlv.ui.adapters.RecipesAdapter

class FavoritesFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recipesAdapter: RecipesAdapter
    private lateinit var favoritesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        initViews()
        setupRecyclerView()
        loadFavorites()

        return rootView
    }

    private fun initViews() {
        favoritesRecyclerView = rootView.findViewById(R.id.favoritesRecyclerView)
    }

    private fun setupRecyclerView() {
        recipesAdapter = RecipesAdapter { recipe ->
            val fragment = RecipeDetailFragment.newInstance(recipe.id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipesAdapter
        }
    }

    private fun loadFavorites() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RECIPES,
            null,
            "${DatabaseHelper.COLUMN_FAVORITE} = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        recipesAdapter.submitList(cursorToRecipeList(cursor))
    }

    private fun cursorToRecipeList(cursor: Cursor): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        while (cursor.moveToNext()) {
            recipes.add(Recipe(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                category = Category.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))),
                timeMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                complexity = Complexity.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPLEXITY))),
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO)),
                isFavorite = true
            ))
        }
        cursor.close()
        return recipes
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }
}