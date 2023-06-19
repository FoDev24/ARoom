package com.example.viewmodel.shopping

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Address
import com.example.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,
    private val auth : FirebaseAuth
) :ViewModel() {

    private val _addresses = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val addresses = _addresses.asStateFlow()

    init {
        getUserAddresses()
    }
    fun getUserAddresses(){
        viewModelScope.launch { _addresses.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if(error != null){
                    viewModelScope.launch { _addresses.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch { _addresses.emit(Resource.Success(addresses!!)) }


            }

    }
}