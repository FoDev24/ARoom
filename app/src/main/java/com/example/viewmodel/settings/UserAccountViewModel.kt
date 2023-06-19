package com.example.viewmodel.settings

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ARoomApplication
import com.example.data.User
import com.example.util.RegisterValidation
import com.example.util.Resource
import com.example.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore : FirebaseFirestore,
    private val auth : FirebaseAuth,
    private val storage: StorageReference,
    app:Application
): AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()


    init {
        getUser()
    }

    fun getUser (){
        viewModelScope.launch { _user.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {

                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch { _user.emit(Resource.Success(it)) }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _user.emit(Resource.Error(it.message.toString())) }
            }
    }




    fun updateUser(user:User, imageUri : Uri?){
        val areInputValidate = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()
        if(!areInputValidate){
            viewModelScope.launch { _updateInfo.emit(Resource.Error("Check your inputs!")) }
            return
        }
        viewModelScope.launch { _updateInfo.emit(Resource.Loading()) }

        if(imageUri == null){
            saveUserInformation(user,true)
        }else{
            saveUserInfoWithNewImg(user,imageUri)
        }


    }

    private fun saveUserInformation(user: User, shouldRetrieveOldImg: Boolean) {
        firestore.runTransaction { transition->
            val ref = firestore.collection("user").document(auth.uid!!)

            if(shouldRetrieveOldImg){
                val currentUser = transition.get(ref).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transition.set(ref,newUser)
            }else{
                transition.set(ref,user)
            }
        } .addOnSuccessListener {
            viewModelScope.launch { _updateInfo.emit(Resource.Success(user)) }
        }.addOnFailureListener {
            Log.e("Tes1",it.message.toString())
            viewModelScope.launch { _updateInfo.emit(Resource.Error(it.message.toString())) }
        }
    }

    private fun saveUserInfoWithNewImg(user: User, imageUri: Uri) {
        viewModelScope.launch {
            // TODO: to upload an image to firesbase storage , u have to get the bitMap then the ByteArray

            try{
                val imgBitMap = MediaStore.Images.Media
                    .getBitmap(getApplication<ARoomApplication>().contentResolver,imageUri)
                val byteArrayOutPutStream = ByteArrayOutputStream()
                imgBitMap.compress(Bitmap.CompressFormat.JPEG,96,byteArrayOutPutStream)
                val imgByteArray = byteArrayOutPutStream.toByteArray()

                val imgDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imgDirectory.putBytes(imgByteArray).await()
                val imgUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imgUrl),false)

            } catch (e:Exception){
                viewModelScope.launch { _updateInfo.emit(Resource.Error(e.message.toString())) }
            }
        }
    }
}