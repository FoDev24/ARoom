package com.example.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.adapters.BestProductsAdapter
import com.example.aroom.R
import com.example.aroom.databinding.FragmentSearchBinding
import com.example.util.Resource
import com.example.viewmodel.shopping.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment :Fragment(R.layout.fragment_search) {
    private lateinit var binding : FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private  val searchAdapter: BestProductsAdapter by lazy{ BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()

        searchAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product" , it) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment,b)
        }

        binding.apply {
            searchIcon.setOnClickListener{
                var query = binding.edSearch.text.toString().trim()
                if(query.isNotEmpty()){
                    viewModel.searchProduct(query)
                } else{
                    Toast.makeText(requireContext(),"No inputs", Toast.LENGTH_SHORT).show()
                }
            }

            edSearch.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = edSearch.text.toString().trim()
                    viewModel.searchProduct(query)
                    true
                } else {
                    false
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.search.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                       searchAdapter.differ.submitList(it.data)
                        hideLoading()
                        hideEmptyView()
                        if(it.data.isNullOrEmpty()){
                            showEmptyView()
                        }

                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showEmptyView() {
        binding.tvEmptySearch.visibility = View.VISIBLE
    }
    private fun hideEmptyView() {
        binding.tvEmptySearch.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBarSearch.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressBarSearch.visibility = View.VISIBLE
    }

    private fun setupRv() {
        binding.searchRv.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false   )
            adapter = searchAdapter
        }
    }
}