package moe.wisteria.android.kimoji.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import moe.wisteria.android.kimoji.databinding.ItemComicImageBinding
import moe.wisteria.android.kimoji.entity.Order
import moe.wisteria.android.kimoji.util.loadImage

class ComicImageAdapter(
    private val context: Context,
    private val onClickListener: (Order) -> Unit,
    private val onLongClickListener: (Order) -> Unit,
    orderList: List<Order>
): RecyclerView.Adapter<ComicImageAdapter.ViewHolder>() {
    private val _orderList: MutableList<Order> = orderList.toMutableList()

    class ViewHolder(
        private val binding: ItemComicImageBinding,
        private val onClickListener: (Order) -> Unit,
        private val onLongClickListener: (Order) -> Unit
    ): RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(
            order: Order
        ) {
            order.media.let {
                binding.itemComicImageContent.apply {
                    loadImage(
                        data = "${ it.fileServer }/static/${ it.path }"
                    )

                    setOnClickListener {
                        onClickListener(order)
                    }

                    setOnLongClickListener {
                        onLongClickListener(order)
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemComicImageBinding.inflate(LayoutInflater.from(context), parent, false),
            onClickListener = onClickListener,
            onLongClickListener = onLongClickListener
        )
    }

    override fun getItemCount(): Int = _orderList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_orderList[position])
    }

    fun insertOrder(
        order: Order
    ) {
        _orderList.add(order)
        notifyItemInserted(_orderList.size)
    }

    fun insertOrders(
        vararg orders: Order
    ) {
        for (order in orders) {
            insertOrder(order)
        }
    }

    fun replaceOrders(
        vararg orders: Order
    ) {
        notifyItemRangeRemoved(0, itemCount)
        _orderList.clear()

        insertOrders(*orders)
    }
}