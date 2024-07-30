package moe.wisteria.android.kimoji.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import moe.wisteria.android.kimoji.databinding.ItemEpisodeBinding
import moe.wisteria.android.kimoji.entity.Episode

class EpisodeAdapter(
    private val context: Context,
    private val onClickListener: (Episode) -> Unit,
    episodeList: List<Episode>
) : RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {
    private val _episodeList: MutableList<Episode> = episodeList.toMutableList()

    class ViewHolder(
        private val binding: ItemEpisodeBinding,
        private val onClickListener: (Episode) -> Unit
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(
            episode: Episode
        ) {
            binding.itemEpisodeButton.apply {
                text = episode.title

                setOnClickListener {
                    onClickListener(episode)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemEpisodeBinding.inflate(LayoutInflater.from(context), parent, false),
            onClickListener = onClickListener
        )
    }

    override fun getItemCount(): Int = _episodeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_episodeList[position])
    }

    fun insertEpisode(
        episode: Episode
    ) {
        if (_episodeList.contains(episode))
            return

        _episodeList.add(episode)
        notifyItemInserted(_episodeList.size)
    }

    fun insertEpisodes(
        vararg episodes: Episode
    ) {
        for (episode in episodes) {
            insertEpisode(episode)
        }
    }

    fun replaceEpisodes(
        vararg episodes: Episode
    ) {
        notifyItemRangeRemoved(0, itemCount)
        _episodeList.clear()

        insertEpisodes(*episodes)
    }

    fun getFirstEpisode(): Episode? = _episodeList.firstOrNull()

    fun removeAll() {
        notifyItemRangeChanged(0, itemCount)

        _episodeList.clear()
    }
}