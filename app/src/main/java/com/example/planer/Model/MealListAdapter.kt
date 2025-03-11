import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.planer.Model.DynamicItem
import com.example.planer.R
import com.example.planer.database.MealEntity
import com.example.planer.ui.HomeFragment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.text.clear

class MealListAdapter(private val items: List<DynamicItem>) :
    RecyclerView.Adapter<DynamicViewHolder>() {

    // 1. Interface definieren
    interface OnMealClickListener {
        fun onMealClick(btnTag: Long)
    }

    // 2. Listener-Variable
    var onMealClickListener: OnMealClickListener? = null

    companion object {
        private const val VIEW_TYPE_TEXT = 0
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_MEAL_PLAN = 2
        private const val VIEW_TYPE_MEAL_LIST = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DynamicItem.TextItem -> VIEW_TYPE_TEXT
            is DynamicItem.ImageItem -> VIEW_TYPE_IMAGE
            is DynamicItem.MealPlanItem -> VIEW_TYPE_MEAL_PLAN
            is DynamicItem.MealListItem -> VIEW_TYPE_MEAL_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.item_text, parent, false)
                DynamicViewHolder.TextViewHolder(view)
            }
            VIEW_TYPE_IMAGE -> {
                val view = inflater.inflate(R.layout.item_image, parent, false)
                DynamicViewHolder.ImageViewHolder(view)
            }
            VIEW_TYPE_MEAL_PLAN -> {
                val view = inflater.inflate(R.layout.item_meal_plan, parent, false)
                DynamicViewHolder.MealPlanViewHolder(view)
            }
            VIEW_TYPE_MEAL_LIST -> {
                val view = inflater.inflate(R.layout.item_meal_list, parent, false)
                DynamicViewHolder.MealListViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        when (val item = items[position]) {
            is DynamicItem.TextItem -> {
                val textHolder = holder as DynamicViewHolder.TextViewHolder
                textHolder.textView.text = item.text
            }
            is DynamicItem.ImageItem -> {
                val imageHolder = holder as DynamicViewHolder.ImageViewHolder
//                Glide.with(imageHolder.itemView.context)
//                    .load(item.imageUrl)
//                    .into(imageHolder.imageView)
            }
            is DynamicItem.MealPlanItem -> {
                val mealPlanHolder = holder as DynamicViewHolder.MealPlanViewHolder
                mealPlanHolder.mealIdTextView.text = "Meal ID: ${item.meal}"
                mealPlanHolder.maxDateTextView.text = "Max Date: ${item.maxDate}"
            }
            is DynamicItem.MealListItem -> {
                val mealPlanHolder = holder as DynamicViewHolder.MealListViewHolder
                mealPlanHolder.dateTextView.text = item.ptVText
                mealPlanHolder.mealTextView.text = item.ptvMealText

                mealPlanHolder.container.setBackgroundColor(item.pColor)

                mealPlanHolder.imageButton.tag = item.pbtnEditTag
                mealPlanHolder.imageButton.id = item.pbtnEditId

                //Button Listener
                mealPlanHolder.imageButton.setOnClickListener { btn ->
                    onMealClickListener?.onMealClick(btn.tag as Long)
                }
                
            }

        }

    }

    override fun getItemCount(): Int = items.size
}