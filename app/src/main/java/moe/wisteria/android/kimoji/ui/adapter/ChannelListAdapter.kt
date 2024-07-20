package moe.wisteria.android.kimoji.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.ItemChannelBinding

class ChannelListAdapter(
    private val context: Context,
    private val itemOnClickListener: ((String) -> Unit) = { },
    channelList: List<String>
) : RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {
    private val _channelList: MutableList<String> = channelList.toMutableList()

    class ViewHolder(
        private val binding: ItemChannelBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            channel: String,
            itemOnClickListener: ((String) -> Unit) = { }
        ) {
            binding.itemChannelButton.apply {
                text = context.getString(R.string.item_channel_button, channel)

                setOnClickListener {
                    itemOnClickListener(channel)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemChannelBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = _channelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            channel = _channelList[position],
            itemOnClickListener = itemOnClickListener
        )
    }

    fun noticeChange(
        channelList: List<String>
    ) {
        channelList.let {
            _channelList.addAll(it)
            notifyItemRangeInserted(getItemCount() - it.size, it.size)
        }
    }
}