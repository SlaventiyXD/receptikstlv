package com.example.receptikstlv.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.database.Cursor
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptikstlv.R
import com.example.receptikstlv.data.DatabaseHelper
import com.example.receptikstlv.data.models.*
import com.example.receptikstlv.ui.adapters.RecipesAdapter
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recipesAdapter: RecipesAdapter
    private lateinit var searchEditText: EditText
    private lateinit var categoryTabs: TabLayout
    private lateinit var recipesRecyclerView: RecyclerView

    private var currentCategory: Category = Category.BREAKFAST
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        initViews()
        setupSearch()
        loadRecipes()

        return rootView
    }

    private fun initViews() {
        searchEditText = rootView.findViewById(R.id.searchEditText)
        categoryTabs = rootView.findViewById(R.id.categoryTabs)
        recipesRecyclerView = rootView.findViewById(R.id.recipesRecyclerView)

        recipesAdapter = RecipesAdapter { recipe ->
            val fragment = RecipeDetailFragment.newInstance(recipe.id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipesAdapter
        }

        categoryTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCategory = when (tab?.position) {
                    0 -> Category.BREAKFAST
                    1 -> Category.LUNCH
                    2 -> Category.DINNER
                    3 -> Category.DESSERT
                    else -> Category.BREAKFAST
                }
                loadRecipes()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString() ?: ""
                loadRecipes()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadRecipes() {
        val db = databaseHelper.readableDatabase
        val cursor = if (searchQuery.isEmpty()) {
            db.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                "${DatabaseHelper.COLUMN_CATEGORY} = ?",
                arrayOf(currentCategory.name),
                null,
                null,
                null
            )
        } else {
            db.rawQuery("""
                SELECT DISTINCT r.* FROM ${DatabaseHelper.TABLE_RECIPES} r
                LEFT JOIN ${DatabaseHelper.TABLE_INGREDIENTS} i 
                ON r.${DatabaseHelper.COLUMN_ID} = i.${DatabaseHelper.COLUMN_RECIPE_ID}
                WHERE r.${DatabaseHelper.COLUMN_TITLE} LIKE ? 
                OR i.${DatabaseHelper.COLUMN_NAME} LIKE ?
            """.trimIndent(), arrayOf("%$searchQuery%", "%$searchQuery%"))
        }

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
                isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE)) == 1
            ))
        }
        cursor.close()
        return recipes
    }
}