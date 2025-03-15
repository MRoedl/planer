package com.example.planer.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.planer.database.PlanerDao
import com.example.planer.R
import com.example.planer.database.PlanerDatabase
import com.example.planer.database.MealEntity
import com.example.planer.databinding.FragmentAddMealBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddMealFragment : Fragment() {

    private var _binding: FragmentAddMealBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddMealBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddMeal.setOnClickListener {
            val newMeal = MealEntity(
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

            val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

            lifecycleScope.launch {
                planerDao.insert(newMeal)
                Log.d("NACHRICHT", "inserted new meal: ${newMeal.name}")
                findNavController().navigate(R.id.action_AddMealFragment_to_MealListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}