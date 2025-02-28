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
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.PlanerDao
import com.example.planer.R
import com.example.planer.database.PlanerDatabase
import com.example.planer.database.MealEntity
import com.example.planer.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddMeal.setOnClickListener {
            val name = binding.ptGericht.text.toString()
            val category = binding.ptKategorie.text.toString()
            val ingredients = binding.ptZutaten.text.toString()
            val dietaryTags = binding.ptInfos.text.toString()
            val newMeal = MealEntity(name = name, category = category, ingredients = ingredients, dietaryTags = dietaryTags, lastEaten = null)

            val planerDao: PlanerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

            lifecycleScope.launch {
                planerDao.insert(newMeal)
                Log.d("NACHRICHT", "inserted new meal: ${newMeal.name}")
                findNavController().navigate(R.id.action_SecondFragment_to_ThirdFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}