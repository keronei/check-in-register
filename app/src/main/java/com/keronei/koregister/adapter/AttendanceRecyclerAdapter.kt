package com.keronei.koregister.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keronei.kiregister.databinding.LayoutAttendeeItemBinding
import com.keronei.koregister.models.AttendeePresentation

class AttendanceRecyclerAdapter :
    ListAdapter<AttendeePresentation, AttendanceRecyclerAdapter.FilmViewHolder>(FilmDiffUtil()) {

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

    class FilmViewHolder(private val binding: LayoutAttendeeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendeePresentation: AttendeePresentation) {
            binding.attendee = attendeePresentation
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FilmViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemFilmBinding = LayoutAttendeeItemBinding.inflate(inflater, parent, false)

                return FilmViewHolder(itemFilmBinding)
            }
        }


    }

    class FilmDiffUtil : DiffUtil.ItemCallback<AttendeePresentation>() {
        override fun areItemsTheSame(oldItem: AttendeePresentation, newItem: AttendeePresentation): Boolean {
            return oldItem.memberId == newItem.memberId
        }

        override fun areContentsTheSame(oldItem: AttendeePresentation, newItem: AttendeePresentation): Boolean {
            return oldItem == newItem
        }

    }
}