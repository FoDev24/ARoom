package com.example.firebase

import com.example.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

class FirebaseCommon(
    private val firestore : FirebaseFirestore ,
    private val auth : FirebaseAuth
) {

    private val cartCollection = firestore.collection("user").document(auth.uid!!).collection("cart")

    fun addProductToCart(cartProduct: CartProduct , onResult : (CartProduct?,Exception?) -> Unit){
        cartCollection.document().set(cartProduct).
        addOnSuccessListener {
            onResult(cartProduct,null)
        }
            .addOnFailureListener {
                onResult(null,it)
            }
    }

    fun increaseQuantity(documentId:String ,onResult : (String?,Exception?) -> Unit) {
        //Need to read first from firestore then we gonna update it
        firestore.runTransaction{ transition->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuan = cartProduct.quantity + 1
                val newProdObject = cartProduct.copy(quantity = newQuan)

                transition.set(documentRef,newProdObject)
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    fun decreaseQuantity(documentId:String ,onResult : (String?,Exception?) -> Unit) {
        //Need to read first from firestore then we gonna update it
        firestore.runTransaction{ transition->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuan = cartProduct.quantity - 1
                val newProdObject = cartProduct.copy(quantity = newQuan)

                transition.set(documentRef,newProdObject)
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    enum class QuantityChanging {
        INCREASE,DECREASE
    }
}