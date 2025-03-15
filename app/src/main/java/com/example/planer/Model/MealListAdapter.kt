package com.example.planer.Model

import androidx.recyclerview.widget.RecyclerView
import DynamicViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.planer.R

class MealListAdapter(private val items: List<DynamicItem>) :
    RecyclerView.Adapter<DynamicViewHolder>() {

    // 1. Interface definieren
    interface OnMealClickListener {
        fun onMealClick(btnTag: Long, isDeleteButton: Boolean?)
    }

    // 2. Listener-Variable
    var onMealClickListener: OnMealClickListener? = null

    companion object {
        private const val VIEW_TYPE_MEAL_LIST = 0
        private const val VIEW_TYPE_MEAL_PLAN = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DynamicItem.MealListItem -> VIEW_TYPE_MEAL_LIST
            is DynamicItem.MealPlanItem -> VIEW_TYPE_MEAL_PLAN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MEAL_LIST -> {
                val view = inflater.inflate(R.layout.item_meal_list, parent, false)
                DynamicViewHolder.MealListViewHolder(view)
            }
            VIEW_TYPE_MEAL_PLAN -> {
                val view = inflater.inflate(R.layout.item_meal_plan, parent, false)
                DynamicViewHolder.MealPlanViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        when (val item = items[position]) {
            is DynamicItem.MealListItem -> {
                val mealPlanHolder = holder as DynamicViewHolder.MealListViewHolder
                mealPlanHolder.meal.text = item.meal
                mealPlanHolder.editButton.tag = item.btnTag
                mealPlanHolder.deleteButton.tag = item.btnTag

                mealPlanHolder.editButton.setOnClickListener { btn ->
                    onMealClickListener?.onMealClick(btn.tag as Long, false)
                }

                mealPlanHolder.deleteButton.setOnClickListener { btn ->
                    onMealClickListener?.onMealClick(btn.tag as Long, true)
                }

//                binding.container.findViewById<ImageButton>(btnEdit.id).setOnClickListener { btn ->
//                    viewModel.mealId = btn.tag as Long
//                    findNavController().navigate(R.id.action_MealListFragment_to_EditMealFragment)
//                }
//
//                binding.container.findViewById<ImageButton>(btnDel.id).setOnClickListener { btn ->
//                    lifecycleScope.launch {
//                        planerDao.deleteMealById(btn.tag as Long)
//                        findNavController().navigate(R.id.action_MealListFragment_to_self)
//                    }
//                }

            }
            is DynamicItem.MealPlanItem -> {
                val mealPlanHolder = holder as DynamicViewHolder.MealPlanViewHolder
                mealPlanHolder.dateTextView.text = item.ptVText
                mealPlanHolder.mealTextView.text = item.ptvMealText

                mealPlanHolder.container.setBackgroundColor(item.pColor)

                mealPlanHolder.imageButton.tag = item.pbtnEditTag
                mealPlanHolder.imageButton.id = item.pbtnEditId

                //Button Listener
                mealPlanHolder.imageButton.setOnClickListener { btn ->
                    onMealClickListener?.onMealClick(btn.tag as Long, null)
                }
                
            }

        }

    }

    override fun getItemCount(): Int = items.size
}