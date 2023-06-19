package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aroom.databinding.BestDealsRvItemBinding
import com.example.aroom.databinding.SpecialRvItemBinding
import com.example.data.Product

class BestDealsAdapter: RecyclerView.Adapter<BestDealsAdapter.BesDealsViewHolder>() {

    inner class BesDealsViewHolder( val binding: BestDealsRvItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(product:Product){

            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)


                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvPrice.text = "LE ${String.format("%.2f",priceAfterOffer)}"
                }
                tvBestDealsOldPrice.text = "LE ${product.price}"
                tvBestDealProductName.text = product.name
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BesDealsViewHolder {
        return BesDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
      return  differ.currentList.count()
    }

    override fun onBindViewHolder(holder: BesDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
        holder.binding.btnSeeProduct.setOnClickListener {
            onBtnClick?.invoke(product)
        }

    }

    var onClick:( (Product) -> Unit )?  = null
    var onBtnClick:( (Product) -> Unit )?  = null

}