package moe.wisteria.android.kimoji.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import moe.wisteria.android.kimoji.databinding.ItemColumnComicBinding
import moe.wisteria.android.kimoji.entity.BaseComic

class ColumnComicAdapter(
    private val context: Context,
    private val itemOnClickListener: ((BaseComic) -> Unit) = { },
    comicList: List<BaseComic>
) : RecyclerView.Adapter<ColumnComicAdapter.ViewHolder>() {
    private val _comicList: MutableList<BaseComic> = comicList.toMutableList()

    class ViewHolder(
        private val binding: ItemColumnComicBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            baseComic: BaseComic,
            itemOnClickListener: ((BaseComic) -> Unit) = { }
        ) {
            binding.itemColumnComicTitle.text = baseComic.title
            binding.itemColumnComicCard.setOnClickListener {
                itemOnClickListener(baseComic)
            }
            binding.itemColumnComicCategory.apply {
                adapter = LabelAdapter(
                    context = context,
                    labelList = baseComic.categories
                )
                layoutManager = FlexboxLayoutManager(context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemColumnComicBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = _comicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            baseComic = _comicList[position],
            itemOnClickListener = itemOnClickListener
        )
    }

    fun insertComic(
        baseComic: BaseComic
    ) {
        _comicList.add(baseComic)
        notifyItemInserted(_comicList.size)
    }

    fun insertComics(
        vararg baseComics: BaseComic
    ) {
        for (baseComic in baseComics) {
            insertComic(baseComic)
        }
    }
}