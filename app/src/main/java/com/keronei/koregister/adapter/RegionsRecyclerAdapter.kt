package com.keronei.koregister.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keronei.kiregister.databinding.ItemRegionLayoutBinding
import com.keronei.koregister.models.RegionPresentation
import java.util.*

class RegionsRecyclerAdapter(private val itemSelected: (region: RegionPresentation) -> Unit) :
    ListAdapter<RegionPresentation, RegionsRecyclerAdapter.FilmViewHolder>(FilmDiffUtil()) {

    var untouchedList = listOf<RegionPresentation>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmViewHolder {
        return FilmViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)

        holder.binding.root.setOnClickListener {
            itemSelected(getItem(position))
        }
    }

    fun modifyList(list: List<RegionPresentation>) {
        untouchedList = list
        submitList(list)
    }

    fun filter(query: CharSequence?) {
        val list = mutableListOf<RegionPresentation>()

        if (!query.isNullOrEmpty()) {
            list.addAll(untouchedList.filter { item ->
                item.name.toLowerCase(Locale.getDefault())
                    .contains(query.toString().toLowerCase(Locale.getDefault()))

            })
        } else {
            list.addAll(untouchedList)
        }

        submitList(list)
    }


    class FilmViewHolder(val binding: ItemRegionLayoutBinding) :
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
        override fun areItemsTheSame(
            oldItem: RegionPresentation,
            newItem: RegionPresentation
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RegionPresentation,
            newItem: RegionPresentation
        ): Boolean {
            return oldItem == newItem
        }

    }
}