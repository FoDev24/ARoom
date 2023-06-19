package com.example.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aroom.databinding.CartProductItemBinding
import com.example.data.CartProduct
import com.example.helper.getProductPrice

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder> (){

    inner class CartProductViewHolder( val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(cartProduct: CartProduct){
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imgCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProdQuantity.text = cartProduct.quantity.toString()

                val priceAfterOffer =  cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "LE ${String.format("%.2f",priceAfterOffer)}"

                imgCartProdColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
                tvCartProdSize.text = cartProduct.selectedSize?:"".also { imgCartProdSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }


            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.count()
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imgPlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imgMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }



    }

    var onProductClick:( (CartProduct) -> Unit )?  = null
    var onPlusClick:( (CartProduct) -> Unit )?  = null
    var onMinusClick:( (CartProduct) -> Unit )?  = null
}