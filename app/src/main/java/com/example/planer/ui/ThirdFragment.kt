package com.example.planer.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.planer.R
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.MealEntity
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.FragmentThirdBinding
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.util.Calendar
import kotlin.getValue

class ThirdFragment : Fragment() {

    // todo: Btn für Gerichte hinzufügen hier einfügen
    // Meal Liste bearbeiten / löschen

    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the MealPlanDao instance
        val planerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        // Example of using a coroutine
        lifecycleScope.launch {
            val meals: List<MealEntity>? = planerDao.getAllMeals()

            if (meals != null && meals.isNotEmpty()) {
                for (meal in meals) {
                    // 1. Erstelle die TextView
                    val textView = TextView(requireContext())
                    textView.setPadding(50, 20, 20, 20)
                    textView.textSize = 20f
                    textView.text = meal.name

                    // 2. Füge die TextView zum Container hinzu
                    binding.container.addView(textView)

                    // 3. Erstelle Button
                    val button = Button(requireContext())
                    button.text = "Edit"
                    button.id = meal.id
                    button.tag = "meals"

                    // 4. Füge den Button zum Container hinzu
                    binding.container.addView(button)
                }

                binding.container.findViewWithTag<Button>("meals").setOnClickListener { btn ->
                    viewModel.mealId = btn.id
                    findNavController().navigate(R.id.action_ThirdFragment_to_EditMealFragment)
                }
            }

        }


        binding.buttonThird.setOnClickListener {
            findNavController().navigate(R.id.action_ThirdFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}