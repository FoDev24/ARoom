package com.example.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.aroom.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit,
){

    val dialog = BottomSheetDialog(requireContext() , R.style.DialogStyle )
    val view = layoutInflater.inflate(R.layout.reset_pass_dialog,null)

    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.ed_reset_password)
    val btnSend = view.findViewById<Button>(R.id.btn_send_reset_password)
    val btnCancel = view.findViewById<Button>(R.id.btn_cancel_reset_password)

    btnSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}