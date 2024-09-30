package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.identity.trace.R
import models.MissingPersonModel

class MissingPersonAdapter(private val items: List<MissingPersonModel>) : RecyclerView.Adapter<MissingPersonAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.missingPersonNameTV)
        val locationTextView: TextView = itemView.findViewById(R.id.missingPersonLocationTV)
        val missingPersonimageView: ImageView = itemView.findViewById(R.id.ivMissingPerson)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_missing_person, parent, false)
        return ItemViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.locationTextView.text = item.location
        Glide.with(holder.itemView.context)
            .load(item.imageUrl).centerCrop()
            .into(holder.missingPersonimageView)

    }

    override fun getItemCount(): Int {
        return items.size
    }
}
