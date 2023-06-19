package com.example.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.BestProductsAdapter
import com.example.aroom.R
import com.example.aroom.databinding.FragmentBaseCategoryBinding
import com.example.util.showBottomNavigationView

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding : FragmentBaseCategoryBinding
    protected val  offerAdapter : BestProductsAdapter by lazy{ BestProductsAdapter() }
    protected  val bestProductAdapter: BestProductsAdapter by lazy{ BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProductsRv()

        bestProductAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product" , it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        offerAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product" , it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!recyclerView.canScrollHorizontally(1) && dx != 0){
                    onOfferProductPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY){
                onBestProductPagingRequest()
            }

        })
    }

    fun showOfferLoading (){
        binding.offerProductProgressBar.visibility  = View.VISIBLE
    }

    fun hideOfferLoading (){
        binding.offerProductProgressBar.visibility  = View.GONE
    }

    fun showBestProductLoading (){
        binding.bestProductProgressBar.visibility  = View.VISIBLE
    }

    fun hideBestProductLoading (){
        binding.bestProductProgressBar.visibility  = View.GONE
    }

    open fun onOfferProductPagingRequest(){

    }

    open fun onBestProductPagingRequest(){

    }

    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false   )
            adapter = bestProductAdapter
        }
    }

    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}