package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.identity.trace.R
import models.ItemsModel

class ItemsAdapter(private val items: List<ItemsModel>) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    // ViewHolder class to hold item views
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.missingPersonNameTV)
        val locationTextView: TextView = itemView.findViewById(R.id.missingPersonLocationTV)
        val missingPersonimageView: ImageView = itemView.findViewById(R.id.imageView2)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_missing_person, parent, false)
        return ItemViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.locationTextView.text = item.location
        Glide.with(holder.itemView.context)
            .load(item.imageUrl).centerCrop()
            .into(holder.missingPersonimageView)

    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return items.size
    }
}
