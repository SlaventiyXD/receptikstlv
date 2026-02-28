package com.example.receptikstlv.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptikstlv.R
import com.example.receptikstlv.data.DatabaseHelper
import com.example.receptikstlv.data.models.*
import com.example.receptikstlv.ui.adapters.IngredientsAdapter
import com.example.receptikstlv.ui.adapters.StepsAdapter

class AddRecipeFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var databaseHelper: DatabaseHelper

    // Объявляем все view
    private lateinit var recipeTitleEditText: EditText
    private lateinit var recipeTimeEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var complexitySpinner: Spinner
    private lateinit var selectPhotoButton: Button
    private lateinit var recipePhoto: ImageView
    private lateinit var ingredientNameEditText: EditText
    private lateinit var ingredientAmountEditText: EditText
    private lateinit var addIngredientButton: Button
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepDescriptionEditText: EditText
    private lateinit var addStepButton: Button
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var saveRecipeButton: Button

    private val ingredients = mutableListOf<Ingredient>()
    private val steps = mutableListOf<Step>()
    private var selectedPhotoUri: Uri? = null

    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepsAdapter: StepsAdapter

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedPhotoUri = uri
                recipePhoto.setImageURI(uri)
                recipePhoto.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_add_recipe, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        initViews()
        setupSpinners()
        setupRecyclerViews()
        setupClickListeners()

        return rootView
    }

    private fun initViews() {
        // Инициализируем все view
        recipeTitleEditText = rootView.findViewById(R.id.recipeTitleEditText)
        recipeTimeEditText = rootView.findViewById(R.id.recipeTimeEditText)
        categorySpinner = rootView.findViewById(R.id.categorySpinner)
        complexitySpinner = rootView.findViewById(R.id.complexitySpinner)
        selectPhotoButton = rootView.findViewById(R.id.selectPhotoButton)
        recipePhoto = rootView.findViewById(R.id.recipePhoto)
        ingredientNameEditText = rootView.findViewById(R.id.ingredientNameEditText)
        ingredientAmountEditText = rootView.findViewById(R.id.ingredientAmountEditText)
        addIngredientButton = rootView.findViewById(R.id.addIngredientButton)
        ingredientsRecyclerView = rootView.findViewById(R.id.ingredientsRecyclerView)
        stepDescriptionEditText = rootView.findViewById(R.id.stepDescriptionEditText)
        addStepButton = rootView.findViewById(R.id.addStepButton)
        stepsRecyclerView = rootView.findViewById(R.id.stepsRecyclerView)
        saveRecipeButton = rootView.findViewById(R.id.saveRecipeButton)

        // Скрываем фото по умолчанию
        recipePhoto.visibility = View.GONE
    }

    private fun setupSpinners() {
        // Категории
        val categories = arrayOf("Завтрак", "Обед", "Ужин", "Десерт")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Сложность
        val complexities = arrayOf("★ Легкий", "★★ Средний", "★★★ Сложный")
        val complexityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, complexities)
        complexityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        complexitySpinner.adapter = complexityAdapter
    }

    private fun setupRecyclerViews() {
        // Ингредиенты
        ingredientsAdapter = IngredientsAdapter(ingredients)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ingredientsRecyclerView.adapter = ingredientsAdapter

        // Шаги
        stepsAdapter = StepsAdapter(steps)
        stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        stepsRecyclerView.adapter = stepsAdapter
    }

    private fun setupClickListeners() {
        addIngredientButton.setOnClickListener {
            addIngredient()
        }

        addStepButton.setOnClickListener {
            addStep()
        }

        selectPhotoButton.setOnClickListener {
            selectPhoto()
        }

        saveRecipeButton.setOnClickListener {
            saveRecipe()
        }
    }

    private fun addIngredient() {
        val name = ingredientNameEditText.text.toString().trim()
        val amount = ingredientAmountEditText.text.toString().trim()

        if (name.isNotEmpty() && amount.isNotEmpty()) {
            val ingredient = Ingredient(
                recipeId = 0,
                name = name,
                amount = amount
            )
            ingredients.add(ingredient)
            ingredientsAdapter.notifyItemInserted(ingredients.size - 1)

            // Очищаем поля
            ingredientNameEditText.text?.clear()
            ingredientAmountEditText.text?.clear()
        } else {
            Toast.makeText(requireContext(), "Заполните название и количество ингредиента", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addStep() {
        val description = stepDescriptionEditText.text.toString().trim()

        if (description.isNotEmpty()) {
            val step = Step(
                recipeId = 0,
                stepNumber = steps.size + 1,
                description = description
            )
            steps.add(step)
            stepsAdapter.notifyItemInserted(steps.size - 1)

            // Очищаем поле
            stepDescriptionEditText.text?.clear()
        } else {
            Toast.makeText(requireContext(), "Введите описание шага", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun saveRecipe() {
        val title = recipeTitleEditText.text.toString().trim()
        val timeString = recipeTimeEditText.text.toString().trim()

        // Валидация
        if (title.isEmpty()) {
            recipeTitleEditText.error = "Введите название"
            recipeTitleEditText.requestFocus()
            return
        }

        if (timeString.isEmpty()) {
            recipeTimeEditText.error = "Введите время"
            recipeTimeEditText.requestFocus()
            return
        }

        val time = try {
            timeString.toInt()
        } catch (e: NumberFormatException) {
            recipeTimeEditText.error = "Введите число"
            recipeTimeEditText.requestFocus()
            return
        }

        if (ingredients.isEmpty()) {
            Toast.makeText(requireContext(), "Добавьте хотя бы один ингредиент", Toast.LENGTH_SHORT).show()
            return
        }

        if (steps.isEmpty()) {
            Toast.makeText(requireContext(), "Добавьте хотя бы один шаг приготовления", Toast.LENGTH_SHORT).show()
            return
        }

        // Получаем выбранные значения
        val category = when (categorySpinner.selectedItemPosition) {
            0 -> Category.BREAKFAST
            1 -> Category.LUNCH
            2 -> Category.DINNER
            3 -> Category.DESSERT
            else -> Category.BREAKFAST
        }

        val complexity = when (complexitySpinner.selectedItemPosition) {
            0 -> Complexity.EASY
            1 -> Complexity.MEDIUM
            2 -> Complexity.HARD
            else -> Complexity.EASY
        }

        // Сохраняем в БД
        val db = databaseHelper.writableDatabase
        db.beginTransaction()

        try {
            // Сохраняем рецепт
            val recipeValues = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_TITLE, title)
                put(DatabaseHelper.COLUMN_CATEGORY, category.name)
                put(DatabaseHelper.COLUMN_TIME, time)
                put(DatabaseHelper.COLUMN_COMPLEXITY, complexity.name)
                put(DatabaseHelper.COLUMN_PHOTO, selectedPhotoUri?.toString())
                put(DatabaseHelper.COLUMN_FAVORITE, 0)
            }

            val recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues)

            // Сохраняем ингредиенты
            ingredients.forEach { ingredient ->
                val values = android.content.ContentValues().apply {
                    put(DatabaseHelper.COLUMN_RECIPE_ID, recipeId)
                    put(DatabaseHelper.COLUMN_NAME, ingredient.name)
                    put(DatabaseHelper.COLUMN_AMOUNT, ingredient.amount)
                }
                db.insert(DatabaseHelper.TABLE_INGREDIENTS, null, values)
            }

            // Сохраняем шаги
            steps.forEachIndexed { index, step ->
                val values = android.content.ContentValues().apply {
                    put(DatabaseHelper.COLUMN_RECIPE_ID, recipeId)
                    put(DatabaseHelper.COLUMN_STEP_NUMBER, index + 1)
                    put(DatabaseHelper.COLUMN_DESCRIPTION, step.description)
                }
                db.insert(DatabaseHelper.TABLE_STEPS, null, values)
            }

            db.setTransactionSuccessful()

            Toast.makeText(requireContext(), "Рецепт успешно добавлен!", Toast.LENGTH_SHORT).show()

            // Возвращаемся на главный экран
            parentFragmentManager.popBackStack()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка при сохранении: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}