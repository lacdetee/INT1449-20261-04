package com.example.th2

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val onTaskCheckedChanged: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)
        val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskDeadline: TextView = itemView.findViewById(R.id.tvTaskDeadline)
        val tvTaskNote: TextView = itemView.findViewById(R.id.tvTaskNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTaskTitle.text = task.title
        holder.tvTaskDeadline.text = task.deadline

        if (task.note.isNotEmpty()) {
            holder.tvTaskNote.text = task.note
            holder.tvTaskNote.visibility = View.VISIBLE
        } else {
            holder.tvTaskNote.visibility = View.GONE
        }

        // Remove listener before setting checked to avoid triggering callback
        holder.cbTask.setOnCheckedChangeListener(null)
        holder.cbTask.isChecked = task.isCompleted

        // Apply strikethrough style for completed tasks
        updateTextStyle(holder.tvTaskTitle, task.isCompleted)

        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            updateTextStyle(holder.tvTaskTitle, isChecked)
            onTaskCheckedChanged(task, isChecked)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    private fun updateTextStyle(textView: TextView, isCompleted: Boolean) {
        if (isCompleted) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(textView.context.getColor(R.color.completed_text))
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(textView.context.getColor(R.color.text_primary))
        }
    }
}
