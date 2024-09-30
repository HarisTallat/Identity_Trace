package adapters



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.identity.trace.R
import models.CategoryModel

class CategoryAdapter(
    private val categories: List<CategoryModel>,
    private val onCategoryClick: (CategoryModel) -> Unit // Click listener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textView5)
        val categoryImageView: ImageView = itemView.findViewById(R.id.titleCat)

        init {
            itemView.setOnClickListener {
                val category = categories[adapterPosition]
                onCategoryClick(category)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.titleTextView.text = category.title
        Glide.with(holder.itemView.context)
            .load(category.imageUrl)
            .centerCrop()
            .into(holder.categoryImageView)
    }


    override fun getItemCount(): Int {
        return categories.size
    }
}
