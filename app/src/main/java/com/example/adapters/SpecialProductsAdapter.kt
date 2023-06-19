package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.aroom.databinding.SpecialRvItemBinding
import com.example.data.Product

class SpecialProductsAdapter : RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder> (){

     inner class SpecialProductsViewHolder( val binding:SpecialRvItemBinding):RecyclerView.ViewHolder(binding.root) {

         fun bind(product:Product){
             binding.apply {
                 Glide.with(itemView).load(product.images[0]).into(imgSpecialRvItem)
                 tvSpecialProductName.text = product.name
                 tvSpecialProductPrice.text = "LE ${product.price}"

                 product.offerPercentage?.let {
                     val remainingPricePercentage = 1f - it
                     val priceAfterOffer = remainingPricePercentage * product.price
                     tvSpecialProductPrice.text = "LE ${String.format("%.2f",priceAfterOffer)}"
                 }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.count()
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
        holder.binding.btnAddToCart.setOnClickListener {
            onBtnClick?.invoke(product)
        }
    }

    var onClick:( (Product) -> Unit )?  = null
    var onBtnClick:( (Product) -> Unit )?  = null
}