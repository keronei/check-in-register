package com.keronei.koregister.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keronei.kiregister.databinding.ItemRegionLayoutBinding
import com.keronei.koregister.models.RegionPresentation

class RegionsRecyclerAdapter :
    ListAdapter<RegionPresentation, RegionsRecyclerAdapter.FilmViewHolder>(FilmDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmViewHolder {
        return FilmViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)
    }

    class FilmViewHolder(private val binding: ItemRegionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendeePresentation: RegionPresentation) {
            binding.regionInfo = attendeePresentation
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FilmViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemFilmBinding = ItemRegionLayoutBinding.inflate(inflater, parent, false)

                return FilmViewHolder(itemFilmBinding)
            }
        }


    }

    class FilmDiffUtil : DiffUtil.ItemCallback<RegionPresentation>() {
        override fun areItemsTheSame(oldItem: RegionPresentation, newItem: RegionPresentation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RegionPresentation, newItem: RegionPresentation): Boolean {
            return oldItem == newItem
        }

    }
}