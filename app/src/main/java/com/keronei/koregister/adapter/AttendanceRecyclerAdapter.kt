package com.keronei.koregister.adapter

import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.keronei.kiregister.databinding.LayoutAttendeeItemBinding
import com.keronei.koregister.models.AttendeePresentation

import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.keronei.kiregister.R


class AttendanceRecyclerAdapter(private val selectedMember: (member: AttendeePresentation) -> Unit, private val context: Context) :
    ListAdapter<AttendeePresentation, AttendanceRecyclerAdapter.AttendanceViewHolder>(FilmDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceViewHolder {
        return AttendanceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film, context)

        holder.binding.root.setOnClickListener {
            selectedMember(getItem(position))
        }
    }

    class AttendanceViewHolder(val binding: LayoutAttendeeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendeePresentation: AttendeePresentation, context: Context) {
            binding.attendee = attendeePresentation

            if (attendeePresentation.lastCheckIn != "") {
                binding.attendeeArrivalTime.text = attendeePresentation.lastCheckIn
                binding.checkinStatus.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
            }

            val iv: ImageView = binding.avatar

            iv.setImageBitmap(drawUserNameOnCanvas(getUserInitials( attendeePresentation.name).uppercase(), context))

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AttendanceViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemFilmBinding = LayoutAttendeeItemBinding.inflate(inflater, parent, false)

                return AttendanceViewHolder(itemFilmBinding)
            }
        }

        private fun drawUserNameOnCanvas(
            deviceName: String,
            context: Context
        ): Bitmap {

            val b: Bitmap =
                Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888)

            val markerAsDrawable = b.toDrawable(context.resources)

            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.textSize = 14 * context.resources.displayMetrics.density
            textPaint.color = ContextCompat.getColor(context, R.color.primaryColor)
            textPaint.typeface = Typeface.DEFAULT_BOLD

            val width = textPaint.measureText(deviceName).toInt()

            val staticLayout = StaticLayout(
                deviceName, textPaint,
                width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0F, false
            )

            val resultingWidthFromDisplayName = staticLayout.width
            val resultingHeightFromDisplayName = staticLayout.height

            val patchedBitmap = Bitmap.createBitmap(
                (resultingWidthFromDisplayName*1.7).toInt(), (resultingHeightFromDisplayName*1.7).toInt(),
                        //markerAsDrawable.intrinsicHeight + (resultingHeightFromDisplayName * 2),
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(patchedBitmap)

            val paint = Paint()

            paint.color = Color.TRANSPARENT

            paint.style = Paint.Style.FILL

            canvas.drawPaint(paint)


            staticLayout.draw(canvas)

            markerAsDrawable.draw(canvas)

            return patchedBitmap
        }

        fun getUserInitials(username: String): String {
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