package moe.wisteria.android.kimoji.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import moe.wisteria.android.kimoji.databinding.ItemLabelBinding

class LabelAdapter(
    private val context: Context,
    labelList: List<String>
) : RecyclerView.Adapter<LabelAdapter.ViewHolder>() {
    private val _labelList: MutableList<String> = labelList.toMutableList()

    class ViewHolder(
        private val binding: ItemLabelBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            text: String
        ) {
            binding.itemLabelText.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemLabelBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = _labelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            text = _labelList[position]
        )
    }

    fun insertLabel(
        string: String
    ) {
        _labelList.add(string)
        notifyItemInserted(_labelList.size)
    }

    fun insertLabels(
        vararg strings: String
    ) {
        for (str in strings) {
            insertLabel(str)
        }
    }

    fun replaceLabels(
        vararg strings: String
    ) {
        notifyItemRangeRemoved(0, itemCount)
        _labelList.clear()

        insertLabels(*strings)
    }
}