import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planer.R

sealed class DynamicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class MealListViewHolder(itemView: View) : DynamicViewHolder(itemView) {
        val meal: TextView = itemView.findViewById(R.id.meal)
        val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    class MealPlanViewHolder(itemView: View) : DynamicViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val mealTextView: TextView = itemView.findViewById(R.id.mealTextView)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val container: LinearLayout = itemView.findViewById(R.id.container)
    }
}