package com.keronei.koregister.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.ItemRegionLayoutBinding
import com.keronei.koregister.models.RegionPresentation
import java.util.*

class RegionsRecyclerAdapter(
    private val itemSelected: (region: RegionPresentation) -> Unit,
    private val context: Context
) :
    ListAdapter<RegionPresentation, RegionsRecyclerAdapter.RegionsViewHolder>(FilmDiffUtil()) {

    var untouchedList = listOf<RegionPresentation>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RegionsViewHolder {
        return RegionsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RegionsViewHolder, position: Int) {
        val region = getItem(position)
        holder.bind(region, context)

        holder.binding.root.setOnClickListener {
            itemSelected(region)
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


    class RegionsViewHolder(val binding: ItemRegionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(attendeePresentation: RegionPresentation, context: Context) {
            binding.regionInfo = attendeePresentation
            val count = attendeePresentation.memberCount
            when {
                count.toInt() < 1 -> {
                    binding.regionMemberCount.text = context.getString(R.string.no_member_in_region)
                }
                count.toInt() > 1 -> {
                    binding.regionMemberCount.text = "$count members."
                }
                else -> {
                    binding.regionMemberCount.text = context.getString(R.string.one_member)
                }
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RegionsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemFilmBinding = ItemRegionLayoutBinding.inflate(inflater, parent, false)

                return RegionsViewHolder(itemFilmBinding)
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