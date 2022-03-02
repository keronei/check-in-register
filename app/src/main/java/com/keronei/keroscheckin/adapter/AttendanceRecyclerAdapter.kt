package com.keronei.keroscheckin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.LayoutAttendeeItemBinding
import com.keronei.keroscheckin.models.AttendeePresentation
import timber.log.Timber
import java.util.*


class AttendanceRecyclerAdapter(private val selectedMember: (member: AttendeePresentation) -> Unit) :
    ListAdapter<AttendeePresentation, AttendanceRecyclerAdapter.AttendanceViewHolder>(FilmDiffUtil()) {

    var untouchedList = listOf<AttendeePresentation>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceViewHolder {
        return AttendanceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendee = getItem(position)
        holder.bind(attendee)

        holder.binding.root.setOnClickListener {
            selectedMember(attendee)
        }
    }

    fun modifyList(list: List<AttendeePresentation>) {
        untouchedList = list
        submitList(list)
    }

    fun filter(query: CharSequence?) {
        val list = mutableListOf<AttendeePresentation>()

        if (!query.isNullOrEmpty()) {
            list.addAll(untouchedList.filter { item ->
                item.name.lowercase(Locale.getDefault())
                    .contains(query.toString().lowercase(Locale.getDefault()))

            })
        } else {
            list.addAll(untouchedList)
        }

        submitList(list)
    }

    class AttendanceViewHolder(val binding: LayoutAttendeeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendeePresentation: AttendeePresentation) {
            binding.attendee = attendeePresentation

            if (attendeePresentation.lastCheckIn != "") {
                binding.attendeeArrivalTime.text = attendeePresentation.lastCheckIn
                binding.checkinStatus.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
            } else {
                binding.checkinStatus.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                binding.attendeeArrivalTime.text = ""
            }

            binding.textUserInitials.text = getUserInitials( attendeePresentation.name).uppercase()

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AttendanceViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemFilmBinding = LayoutAttendeeItemBinding.inflate(inflater, parent, false)

                return AttendanceViewHolder(itemFilmBinding)
            }
        }


        private fun getUserInitials(username: String): String {
            var initials = ""
            username.split(" ").take(2).onEach { name ->
                val initial = name[0]
                initials += (initial).toString()
            }
            return initials
        }



    }

    class FilmDiffUtil : DiffUtil.ItemCallback<AttendeePresentation>() {
        override fun areItemsTheSame(
            oldItem: AttendeePresentation,
            newItem: AttendeePresentation
        ): Boolean {
            return oldItem.memberId == newItem.memberId
        }

        override fun areContentsTheSame(
            oldItem: AttendeePresentation,
            newItem: AttendeePresentation
        ): Boolean {
            return oldItem == newItem
        }

    }
}