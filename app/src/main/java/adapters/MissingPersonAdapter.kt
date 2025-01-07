package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.identity.trace.R

class MissingPersonAdapter(private var items: List<models.MissingPersonModel>) : RecyclerView.Adapter<MissingPersonAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.missingPersonNameTV)
        val locationTextView: TextView = itemView.findViewById(R.id.missingPersonLocationTV)
        val missingPersonImageView: ImageView = itemView.findViewById(R.id.ivMissingPerson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_missing_person, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.name
        holder.locationTextView.text = item.location
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .centerCrop()
            .into(holder.missingPersonImageView)
    }

    override fun getItemCount(): Int = items.size

    // Function to update adapter's data dynamically
    fun updateData(newItems: List<models.MissingPersonModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
