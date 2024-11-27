package com.example.bodysculptor2

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bodysculptor2.databinding.ActivityDietSuggestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DietSuggestion : AppCompatActivity() {

    private lateinit var binding: ActivityDietSuggestionBinding
    private var weight: String? = null
    private var idealWeight: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietSuggestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Retrieve weight and ideal weight from Firebase
        retrieveWeightData()

        // Log the received data to confirm it's correct
        Log.d("DietSuggestion", "Received weight: $weight, ideal weight: $idealWeight")

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Button for selecting ingredients
        binding.getRecipeButton.setOnClickListener {
            val selectedIngredients = getSelectedIngredients()

            // Ensure two ingredients are selected
            if (selectedIngredients.size == 2) {
                suggestRecipes(selectedIngredients)
            } else {
                Toast.makeText(this, "Please select exactly two ingredients.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun retrieveWeightData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("weightData").get().addOnSuccessListener { dataSnapshot ->
                val weightData = dataSnapshot.getValue(UserWeight::class.java)
                weight = weightData?.weight
                idealWeight = weightData?.idealWeight

                // Log the data to verify it was retrieved
                Log.d("DietSuggestion", "Weight data retrieved: weight = $weight, idealWeight = $idealWeight")

                // Display weight and ideal weight for user reference
                weight?.let { binding.weightText.text = "Current Weight: $it kg" }
                idealWeight?.let { binding.idealWeightText.text = "Ideal Weight: $it kg" }
            }.addOnFailureListener {
                Log.e("DietSuggestion", "Failed to retrieve weight data", it)
                Toast.makeText(this, "Failed to load weight data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSelectedIngredients(): List<String> {
        val ingredients = mutableListOf<String>()
        if (binding.ingredient1.isChecked) ingredients.add("Chicken Breast")
        if (binding.ingredient2.isChecked) ingredients.add("Avocado")
        if (binding.ingredient3.isChecked) ingredients.add("Spinach")
        return ingredients
    }

    private fun suggestRecipes(selectedIngredients: List<String>) {
        val recipes = when {
            weight != null && idealWeight != null -> {
                val currentWeight = weight!!.toInt()
                val targetWeight = idealWeight!!.toInt()

                // Determine whether the user is gaining, losing, or maintaining weight
                when {
                    currentWeight < targetWeight -> suggestGainingWeightRecipes(selectedIngredients)
                    currentWeight > targetWeight -> suggestLosingWeightRecipes(selectedIngredients)
                    else -> suggestMaintainingWeightRecipes(selectedIngredients)
                }
            }
            else -> {
                Toast.makeText(this, "Error: weight or ideal weight is missing.", Toast.LENGTH_SHORT).show()
                emptyList<String>()
            }
        }

        // Display the recipes
        if (recipes.isNotEmpty()) {
            binding.recipeText.text = recipes.joinToString("\n")
        } else {
            binding.recipeText.text = "No recipes found."
        }
    }

    private fun suggestGainingWeightRecipes(ingredients: List<String>): List<String> {
        return when {
            ingredients.contains("Spinach") && ingredients.contains("Chicken Breast") ->
                listOf("Chicken Breast with Spinach and Quinoa\n" +
                        "\nIngredients\n\n" +
                        "-2 boneless, skinless chicken breasts\n" +
                        "-1 cup quinoa\n" +
                        "-2 cups fresh spinach leaves\n" +
                        "-2 garlic cloves, minced\n" +
                        "-1 tbsp olive oil\n" +
                        "-1/2 tsp paprika\n" +
                        "-1/2 tsp dried oregano\n" +
                        "-Salt and pepper to taste\n" +
                        "-2 cups water or chicken broth (for cooking quinoa)\n\n" +
                        "Instruction\n\n" +
                        "\n1. Cook the Chicken: Season the chicken breast with salt, pepper, garlic powder, and paprika. Heat olive oil in a pan over medium heat.\n" +
                            "-Cook the chicken for 6-7 minutes on each side until fully cooked.\n" +
                            "-Add butter towards the end for extra calories.\n\n" +
                            "2. Sauté the Spinach: In the same pan, add a bit more olive oil and sauté the spinach until wilted (about 2-3 minutes).\n\n" +
                            "3. Combine: Serve the chicken on a plate with quinoa and sautéed spinach. Top with grated cheese if desired for extra calories.\n\n" +
                            "4. Serve: Enjoy this high-protein, calorie-dense meal for muscle gain!")

            ingredients.contains("Spinach") && ingredients.contains("Avocado") ->
                listOf ("Spinach Avocado Rice Bowl\n" +
                       "\nIngredients\n\n" +
                        "-1 cup cooked white or brown rice\n" +
                        "-1 cup fresh spinach leaves\n" +
                        "-1 ripe avocado, sliced\n" +
                        "-1/2 cup cherry tomatoes, halved\n" +
                        "-1/4 cup shredded carrot\n" +
                        "-1 boiled egg (optional)\n" +
                        "-1 tbsp olive oil\n" +
                        "-1 tbsp soy sauce\n" +
                        "-Salt and pepper to taste\n\n" +
                        "Instructions\n\n" +
                        "1. Prepare the Base: Place the cooked rice in serving bowl.\n\n" +
                        "2. Add Greens: Layer fresh spinach leaves on top of the rice.\n\n" +
                        "3. Slice and Arrange: Add sliced avocado, halved cherry tomatoes, and shredded carrot around the bowl.\n\n" +
                        "4. Add Protein (Optional): Slice the boil egg and arrange it in the bowl.\n\n" +
                        "5. Season: Drizzle with olive oil and soy sauce. Sprinkle with Salt and pepper to taste.\n\n" +
                        "6. Serve: Enjoy immediately as a healthy, balanced meal.")

            ingredients.contains("Chicken Breast") && ingredients.contains("Avocado") ->
                listOf("Chicken Avocado Sandwich\n" +
                        "\nIngredients\n\n" +
                        "-2 slices of whole-grain or sourdough breads\n" +
                        "-1 cooked chicken breast (grilled, roasted, or pan-seared), sliced or shredded\n" +
                        "-1 ripe avocado\n" +
                        "-1/2 cup lettuce or baby spinach leaves\n" +
                        "-2 slices of tomato\n" +
                        "-Salt and pepper to taste\n\n" +
                        "Instructions\n\n" +
                        "1. Prepare the Avocado Spread: Mash the avocado in a small bowl.\n" +
                        "-Season with salt, pepper, and a squeeze of lemon juice, if desired.\n\n" +
                        "2. Assemble the Sandwich: Spread avocado mash on one slice of bread.\n" +
                        "-Spread mayonnaise on the other slice (optional).\n" +
                        "-Layer lettuce or spinach, sliced chicken, and tomato slices on top of the avocado spread.\n\n" +
                        "3. Close the Sandwich: Place the second slice of bread on top to close the sandwich.\n\n" +
                        "4. Serve: Cut in half for easier handling and enjoy immediately.")
            else -> emptyList()
        }
    }

    private fun suggestLosingWeightRecipes(ingredients: List<String>): List<String> {
        return when {

            ingredients.contains("Spinach") && ingredients.contains("Chicken Breast") ->
                listOf("Grilled Chicken Breast with Spinach\n" +
                        "\nIngredients\n\n" +
                        "-2 boneless, skinless chicken breasts\n" +
                        "-2 cups fresh spinach leaves\n" +
                        "-2 cloves garlic, minced\n" +
                        "-1 tbsp olive oil\n" +
                        "-1/2 tsp paprika\n" +
                        "-1/2 tsp dried oregano\n" +
                        "-Salt and pepper to taste\n" +
                        "-Lemon wedges (optional, for serving)\n\n" +
                        "Instructions\n\n" +
                        "1. Prepare the Chicken: Season chicken breasts with paprika, oregano, salt, and pepper.\n" +
                        "-Heat 1/2 tbsp olive oil in a skillet over medium heat.\n" +
                        "-Cook the chicken for 5-6 minutes on each side until golden brown and fully cooked (internal temperature of 75°C or 165°F).\n" +
                        "-Remove from skillet and let rest for 5 minutes before slicing.\n\n" +
                        "2. Sauté the Spinach: In the same skillet, heat the remaining 1/2 tbsp olive oil.\n" +
                        "-Add minced garlic and sauté for 1 minute.\n" +
                        "-Add spinach and cook until wilted (about 2-3 minutes). Season with salt and pepper.\n\n" +
                        "3. Serve: Plate the sautéed spinach and top with sliced grilled chicken breast.\n" +
                        "-Garnish with lemon wedges, if desired, and enjoy immediately.")


            ingredients.contains("Spinach") && ingredients.contains("Avocado") ->
                listOf("Avocado Spinach Smoothie\n" +
                        "\nIngredients\n\n" +
                        "-1 ripe avocado\n" +
                        "-1 cup fresh spinach leaves\n" +
                        "-1 banana\n" +
                        "-1 cup almond milk (or any milk of choice)\n" +
                        "-1 tbsp honey or maple syrup (optional, for sweetness)\n" +
                        "-1/2 cup ice cubes (optional, for a chilled smoothie)\n\n" +
                        "Instructions\n\n" +
                        "1. Prepare the Ingredients: Scoop the avocado flesh and place it in a blender.\n" +
                        "-Add the spinach, banana, almond milk, and honey or maple syrup, if desired.\n\n" +
                        "2. Blend: Blend all ingredients until smooth and creamy.\n" +
                        "-Add ice cubes if you prefer a chilled smoothie and blend again.\n\n" +
                        "3. Serve: Pour the smoothie into a glass and enjoy immediately.")


            ingredients.contains("Chicken Breast") && ingredients.contains("Avocado") ->
                listOf("Avocado Chicken Salad\n" +
                        "\nIngredients\n\n" +
                        "-2 cooked chicken breasts (grilled, roasted, or shredded)\n" +
                        "-2 ripe avocados, diced\n" +
                        "-1/4 cup red onion, finely chopped\n" +
                        "-1/2 cup cherry tomatoes, halved\n" +
                        "-1/4 cup fresh cilantro or parsley, chopped\n" +
                        "-2 tbsp olive oil\n" +
                        "-1 tbsp lemon or lime juice\n" +
                        "-Salt and pepper to taste\n\n" +
                        "Instructions\n\n" +
                        "1. Prepare the Ingredients: Dice the cooked chicken breasts and avocados.\n" +
                        "-Chop the red onion, cherry tomatoes, and fresh cilantro or parsley.\n\n" +
                        "2. Combine: In a large mixing bowl, combine the chicken, avocados, red onion, cherry tomatoes, and cilantro.\n\n" +
                        "3. Make the Dressing: In a small bowl, whisk together the olive oil, lemon or lime juice, salt, and pepper.\n\n" +
                        "4. Toss: Pour the dressing over the salad and gently toss to combine.\n\n" +
                        "5. Serve: Serve immediately as a light meal or as a filling for wraps or sandwiches.")
            else -> emptyList()
        }
    }

    private fun suggestMaintainingWeightRecipes(ingredients: List<String>): List<String> {
        return when {
            ingredients.contains("Spinach") && ingredients.contains("Chicken Breast") ->
                listOf("Spinach Stuffed Chicken Breast\n" +
                        "\nIngredients\n\n" +
                        "-2 boneless, skinless chicken breasts\n" +
                        "-1 cup fresh spinach leaves, chopped\n" +
                        "-1/4 cup low-fat cream cheese (optional)\n" +
                        "-1/4 tsp garlic powder\n" +
                        "-Salt and pepper to taste\n\n" +
                        "Instructions\n\n" +
                        "1. Preheat oven to 190°C (375°F).\n\n" +
                        "2. Cut a pocket into each chicken breast and season with salt and pepper.\n\n" +
                        "3. In a bowl, mix chopped spinach, cream cheese (if using), and garlic powder.\n\n" +
                        "4. Stuff the mixture into the chicken breasts and secure with toothpicks.\n\n" +
                        "5. Bake for 25-30 minutes until the chicken is fully cooked.\n\n" +
                        "6. Serve with a side salad or steamed vegetables.")


            ingredients.contains("Spinach") && ingredients.contains("Avocado") ->
                listOf("Avocado Spinach Wrap\n" +
                        "\nIngredients\n\n" +
                        "-1 whole-grain tortilla wrap\n" +
                        "-1/2 ripe avocado, sliced\n" +
                        "-1 cup fresh spinach leaves\n" +
                        "-1/4 cup shredded chicken breast (optional for protein boost)\n" +
                        "-1 tbsp hummus or light mayonnaise\n\n" +
                        "Instructions\n\n" +
                        "1. Spread hummus or mayonnaise over the tortilla.\n\n" +
                        "2. Layer spinach, avocado slices, and shredded chicken (if using).\n\n" +
                        "3. Roll the tortilla tightly into a wrap.\n\n" +
                        "4. Cut in half and serve as a light and portable meal.")


            ingredients.contains("Chicken Breast") && ingredients.contains("Avocado") ->
                listOf("Chicken and Avocado Stuffed Bell Peppers\n" +
                        "\nIngredients\n\n" +
                        "-2 boneless, skinless chicken breasts, cooked and diced\n" +
                        "-2 large bell peppers, halved and deseeded\n" +
                        "-1 ripe avocado, diced\n" +
                        "-1/4 cup diced red onion\n" +
                        "-1/4 cup fresh cilantro, chopped\n" +
                        "-1 tbsp olive oil\n" +
                        "-1 tbsp lime juice\n" +
                        "Salt and pepper to taste\n\n" +
                        "Instructions\n\n" +
                        "1. Preheat oven to 190°C (375°F).\n\n" +
                        "2. Slice the bell peppers in half lengthwise and remove the seeds and membranes.\n\n" +
                        "3. In a mixing bowl, combine the cooked and diced chicken, avocado, red onion, cilantro, olive oil, lime juice, salt, and pepper.\n\n" +
                        "4. Stuff each bell pepper half with the chicken and avocado mixture.\n\n" +
                        "5. Place the stuffed peppers on a baking sheet and bake for 15-20 minutes, or until the peppers are tender.\n\n" +
                        "6. Serve immediately as a low-carb, protein-packed meal that supports weight maintenance.")
            else -> emptyList()
        }
    }
}