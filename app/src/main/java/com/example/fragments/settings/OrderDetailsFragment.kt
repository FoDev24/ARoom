package com.example.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.BillingProductAdapter
import com.example.adapters.OrdersAdapter
import com.example.aroom.databinding.FragmentOrderDetailBinding
import com.example.data.order.OrderStatus
import com.example.data.order.OrderStatus.Canceled.getOrderStatus
import com.example.util.VerticalItemDecoration
import com.google.android.filament.ToneMapper.Linear

class OrderDetailsFragment : Fragment() {
    lateinit var binding : FragmentOrderDetailBinding
    private val  args by navArgs<OrderDetailsFragmentArgs>()
    private val orderDetailsAdapter by lazy {BillingProductAdapter()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        setupRv()

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            val currentOrderStauts = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            stepView.go(currentOrderStauts,false)
            if(currentOrderStauts ==3){
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.state},${order.address.city}"
            tvPhoneNumber.text = order.address.phoneNumb

            tvTotalPrice.text = "LE ${order.totalPrice}"
        }

        orderDetailsAdapter.differ.submitList(order.products)
    }

    private fun setupRv() {
        binding.rvProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter = orderDetailsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}