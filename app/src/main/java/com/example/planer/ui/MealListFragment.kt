package com.example.planer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.planer.R
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.MealEntity
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.FragmentMealListBinding
import kotlinx.coroutines.launch
import kotlin.getValue

class MealListFragment : Fragment() {

    // todo:
    // Meal Liste löschen

    private var _binding: FragmentMealListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealListBinding.inflate(inflater, container, false)
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
                var i = 0
                for (meal in meals) {
                    var rowLinearLayout = LinearLayout(requireContext())
                    rowLinearLayout.orientation = LinearLayout.HORIZONTAL

                    // 1. Erstelle die TextView
                    val textView = TextView(requireContext())
                    textView.setPadding(50, 20, 20, 20)
                    textView.textSize = 20f
                    textView.text = meal.name

                    // 2. Füge die TextView zum Container hinzu
                    rowLinearLayout.addView(textView)

                    // 3. Erstelle Button
                    var btnEdit = Button(requireContext())
                    btnEdit.text = "Edit"
                    btnEdit.id = i++
                    btnEdit.tag = meal.id

                    var btnDel = Button(requireContext())
                    btnDel.text = "Delete"
                    btnDel.id = i++
                    btnDel.tag = meal.id

                    // 4. Füge den Button zum Container hinzu
                    rowLinearLayout.addView(btnEdit)
                    rowLinearLayout.addView(btnDel)
                    binding.container.addView(rowLinearLayout)

                    binding.container.findViewById<Button>(btnEdit.id).setOnClickListener { btn ->
                        viewModel.mealId = btn.tag as Long
                        findNavController().navigate(R.id.action_MealListFragment_to_EditMealFragment)
                    }

                    binding.container.findViewById<Button>(btnDel.id).setOnClickListener { btn ->
                        lifecycleScope.launch {
                            planerDao.deleteMealById(btn.tag as Long)
                            findNavController().navigate(R.id.action_MealListFragment_to_self)
                        }

                    }
                }

            }

        }


        binding.buttonThird.setOnClickListener {
            findNavController().navigate(R.id.action_MealListFragment_to_AddMealFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}