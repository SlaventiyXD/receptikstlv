package com.example.receptikstlv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.receptikstlv.R
import com.example.receptikstlv.data.DatabaseHelper
import com.example.receptikstlv.data.models.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipeDetailFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recipeTitle: TextView
    private lateinit var recipeTime: TextView
    private lateinit var recipeComplexity: TextView
    private lateinit var favoriteButton: ImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var recipeId: Int = 0

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int): RecipeDetailFragment {
            val fragment = RecipeDetailFragment()
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
        rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        initViews()
        loadRecipeDetails()
        setupViewPager()
        setupFavoriteButton()

        return rootView
    }

    private fun initViews() {
        recipeTitle = rootView.findViewById(R.id.recipeTitle)
        recipeTime = rootView.findViewById(R.id.recipeTime)
        recipeComplexity = rootView.findViewById(R.id.recipeComplexity)
        favoriteButton = rootView.findViewById(R.id.favoriteButton)
        tabLayout = rootView.findViewById(R.id.tabLayout)
        viewPager = rootView.findViewById(R.id.viewPager)
    }

    private fun loadRecipeDetails() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RECIPES,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val recipe = Recipe(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                category = Category.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))),
                timeMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                complexity = Complexity.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPLEXITY))),
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO)),
                isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE)) == 1
            )

            recipeTitle.text = recipe.title
            recipeTime.text = "${recipe.timeMinutes} мин"

            val complexityStars = when (recipe.complexity) {
                Complexity.EASY -> "★"
                Complexity.MEDIUM -> "★★"
                Complexity.HARD -> "★★★"
            }
            recipeComplexity.text = "Сложность $complexityStars"

            updateFavoriteButton(recipe.isFavorite)
        }
        cursor.close()
    }

    private fun setupViewPager() {
        class PagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> IngredientsFragment.newInstance(recipeId)
                    1 -> InstructionsFragment.newInstance(recipeId)
                    else -> throw IllegalArgumentException()
                }
            }
        }

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Ингредиенты"
                1 -> "Приготовление"
                else -> ""
            }
        }.attach()
    }

    private fun setupFavoriteButton() {
        favoriteButton.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun toggleFavorite() {
        val db = databaseHelper.writableDatabase
        val currentFavorite = getRecipe()?.isFavorite ?: false

        val contentValues = android.content.ContentValues().apply {
            put(DatabaseHelper.COLUMN_FAVORITE, if (currentFavorite) 0 else 1)
        }

        db.update(
            DatabaseHelper.TABLE_RECIPES,
            contentValues,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(recipeId.toString())
        )

        updateFavoriteButton(!currentFavorite)
    }

    private fun getRecipe(): Recipe? {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RECIPES,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            Recipe(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                category = Category.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))),
                timeMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                complexity = Complexity.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPLEXITY))),
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO)),
                isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE)) == 1
            )
        } else null
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }
}