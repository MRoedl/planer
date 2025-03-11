import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planer.R

sealed class DynamicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class TextViewHolder(itemView: View) : DynamicViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    class ImageViewHolder(itemView: View) : DynamicViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    class MealPlanViewHolder(itemView: View) : DynamicViewHolder(itemView) {
        val mealIdTextView: TextView = itemView.findViewById(R.id.mealIdTextView)
        val maxDateTextView: TextView = itemView.findViewById(R.id.maxDateTextView)
    }

    class MealListViewHolder(itemView: View) : DynamicViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val mealTextView: TextView = itemView.findViewById(R.id.mealTextView)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val container: LinearLayout = itemView.findViewById(R.id.container)
    }
}