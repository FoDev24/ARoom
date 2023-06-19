package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aroom.databinding.SpecialRvItemBinding
import com.example.aroom.databinding.ViewRenderableItemBinding
import com.example.data.Product

class ArModelViewAdapter :RecyclerView.Adapter<ArModelViewAdapter.ArModelViewHolder> (){

    inner class ArModelViewHolder(private val binding:ViewRenderableItemBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(arViewProductImg)
                arViewProductName.text = product.name
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArModelViewHolder {
        return ArModelViewHolder(
            ViewRenderableItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.count()
    }

    override fun onBindViewHolder(holder: ArModelViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

    }
}