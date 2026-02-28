package com.example.receptikstlv.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.receptikstlv.data.models.Complexity
import com.example.receptikstlv.data.models.Recipe
import com.example.receptikstlv.databinding.ItemRecipeBinding  // Этот импорт должен работать

class RecipesAdapter(
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    private var recipes = listOf<Recipe>()

    fun submitList(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    inner class RecipeViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.title
            binding.recipeTime.text = "${recipe.timeMinutes} мин"

            val complexityStars = when (recipe.complexity) {
                Complexity.EASY -> "★"
                Complexity.MEDIUM -> "★★"
                Complexity.HARD -> "★★★"
            }
            binding.recipeComplexity.text = "Сложность $complexityStars"

            binding.root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }
}