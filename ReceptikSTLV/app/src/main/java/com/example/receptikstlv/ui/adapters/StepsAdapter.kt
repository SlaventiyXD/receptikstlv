package com.example.receptikstlv.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.receptikstlv.data.models.Step
import com.example.receptikstlv.databinding.ItemStepBinding

class StepsAdapter(
    private val steps: List<Step>
) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount() = steps.size

    inner class StepViewHolder(
        private val binding: ItemStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: Step) {
            binding.stepNumber.text = "ШАГ ${step.stepNumber}"
            binding.stepDescription.text = step.description
        }
    }
}