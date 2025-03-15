package com.example.planer.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.planer.R
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.MealEntity
import com.example.planer.database.PlanerDao
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.FragmentEditMealBinding
import kotlinx.coroutines.launch
import kotlin.getValue


/**
 * A simple [Fragment] subclass.
 * Use the [EditMealFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditMealFragment : Fragment() {

    private var _binding: FragmentEditMealBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditMealBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        val id = viewModel.mealId

        lifecycleScope.launch {
            var meal = planerDao.getMealById(id)
            Log.d("NACHRICHT", "got meal: ${meal.name}")

            binding.ptGericht.setText(meal.name)
            binding.seekBarPopularity.progress = meal.popularity
            binding.checkBoxMonday.isChecked = meal.monday
            binding.checkBoxTuesday.isChecked = meal.tuesday
            binding.checkBoxWednesday.isChecked = meal.wednesday
            binding.checkBoxThursday.isChecked = meal.thursday
            binding.checkBoxFriday.isChecked = meal.friday
            binding.checkBoxSaturday.isChecked = meal.saturday
            binding.checkBoxSunday.isChecked = meal.sunday
        }

        binding.btnUpdateMeal.setOnClickListener {
            val updatedMeal = MealEntity(
                id = id,
                name = binding.ptGericht.text.toString(),
                popularity = binding.seekBarPopularity.progress,
                monday = binding.checkBoxMonday.isChecked,
                tuesday = binding.checkBoxTuesday.isChecked,
                wednesday = binding.checkBoxWednesday.isChecked,
                thursday = binding.checkBoxThursday.isChecked,
                friday = binding.checkBoxFriday.isChecked,
                saturday = binding.checkBoxSaturday.isChecked,
                sunday = binding.checkBoxSunday.isChecked,
                lastEaten = null
            )

            lifecycleScope.launch {
                planerDao.updateMeal(updatedMeal)
                Log.d("NACHRICHT", "updated meal: ${updatedMeal.name}")
                findNavController().navigate(R.id.action_EditMealFragment_to_MealListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}