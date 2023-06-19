package com.example.viewmodel.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Category
import com.example.data.Product
import com.example.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore : FirebaseFirestore ,
    private val category: Category
):ViewModel() {

    private val _offerProducts  = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts  = _offerProducts.asStateFlow()

    private val _bestProducts  = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts  = _bestProducts.asStateFlow()

    init {
        fetchBestProducts()
        fetchOfferProducts()
    }

    fun fetchOfferProducts (){

        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        firestore.collection("Products").whereEqualTo("category",category.category).whereNotEqualTo("offerPercentage",null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts (){

        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        firestore.collection("Products").whereEqualTo("category",category.category).whereEqualTo("offerPercentage",null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }



}