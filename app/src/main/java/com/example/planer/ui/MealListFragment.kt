package com.example.planer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planer.Model.DynamicItem
import com.example.planer.Model.MealListAdapter
import com.example.planer.R
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.MealEntity
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.FragmentMealListBinding
import kotlinx.coroutines.launch
import kotlin.getValue

class MealListFragment : Fragment(), MealListAdapter.OnMealClickListener {

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

        createRows()

        binding.buttonThird.setOnClickListener {
            findNavController().navigate(R.id.action_MealListFragment_to_AddMealFragment)
        }
    }

    fun createRows() {
        // 1. Referenz auf die RecyclerView
        val recyclerView = binding.recyclerView

        // 2. LayoutManager setzen
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 3. Daten erstellen (Beispiel)
        var items = mutableListOf<DynamicItem>()
        val context = this

        val planerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        // Example of using a coroutine
        lifecycleScope.launch {
            val meals: List<MealEntity>? = planerDao.getAllMeals()

            if (meals != null && meals.isNotEmpty()) {
                for (meal in meals) {
                    items.add(DynamicItem.MealListItem(meal = meal.name, btnTag = meal.id))
                }
                val adapter = MealListAdapter(items)
                recyclerView.adapter = adapter
                adapter.onMealClickListener = context
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMealClick(btnTag: Long, isDeleteButton: Boolean?) {
        val planerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()

        if (isDeleteButton!!) {
            lifecycleScope.launch {
                val mealName = planerDao.getMealById(btnTag).name

                val builder = AlertDialog.Builder(requireContext())
                builder
                    .setTitle("$mealName löschen")
                    .setMessage("Sind Sie sicher, dass Sie $mealName löschen möchten?")
                    .setNegativeButton("Abbrechen") { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Löschen") { dialog, which ->
                        lifecycleScope.launch {
                            planerDao.deleteMealById(btnTag)
                            findNavController().navigate(R.id.action_MealListFragment_to_self)
                        }
                    }

                val dialog = builder.create()
                dialog.show()
            }

        } else {
            viewModel.mealId = btnTag
            findNavController().navigate(R.id.action_MealListFragment_to_EditMealFragment)
        }

    }


}