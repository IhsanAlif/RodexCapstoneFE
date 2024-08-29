import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rodexapp.R
import com.example.rodexapp.model.Inspection

class ReportAdapter(private val onItemClicked: (Inspection) -> Unit) :
    ListAdapter<Inspection, ReportAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = getItem(position)
        holder.bind(report)
    }

    class ReportViewHolder(itemView: View, private val onItemClicked: (Inspection) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.reportImageView)
        private val nameView: TextView = itemView.findViewById(R.id.reportNameTextView)
        private val lengthView: TextView = itemView.findViewById(R.id.reportLengthTextView)
        private val latLonView: TextView = itemView.findViewById(R.id.reportLatLonTextView)
        private val surfaceTypeView: TextView = itemView.findViewById(R.id.reportSurfaceTypeTextView)

        fun bind(report: Inspection) {
            // Load the image using Glide or another image loader
            Glide.with(itemView.context).load(report.imageUrl).into(imageView)
            nameView.text = report.name_of_road
            lengthView.text = "Road length: ${report.length_of_road}"
            latLonView.text = "Lat, Lon = ${report.location_start}"
            surfaceTypeView.text = "Surface type: ${report.type_of_road_surface}"

            itemView.setOnClickListener {
                onItemClicked(report)
            }
        }
    }
}

class ReportDiffCallback : DiffUtil.ItemCallback<Inspection>() {
    override fun areItemsTheSame(oldItem: Inspection, newItem: Inspection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Inspection, newItem: Inspection): Boolean {
        return oldItem == newItem
    }
}
