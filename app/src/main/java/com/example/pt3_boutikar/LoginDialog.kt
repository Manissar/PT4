package com.example.pt3_boutikar

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.ClassCastException

class LoginDialog(private var textUsername: String,private var url: String, private var userId : String) : AppCompatDialogFragment() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextUrl: EditText
    private lateinit var editTextUser: EditText
    private lateinit var listener: LoginDialogListener


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var alertDialog = AlertDialog.Builder(activity)

        var inflater = activity?.layoutInflater
        var view = inflater?.inflate(R.layout.layout_dialog, null)

        alertDialog.setView(view)
            .setTitle("Login")
            .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })
            .setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    val username = editTextUsername.text.toString()
                    val password = editTextPassword.text.toString()
                    val url = editTextUrl.text.toString()
                    val user = editTextUser.text.toString()
                    listener.applyTexts(username, password, url, user)
                }

            })

        if (view != null) {
            editTextUsername = view.findViewById(R.id.edit_username)
            if (this.textUsername != "")
                editTextUsername.setText(textUsername)
        }
        if (view != null) {
            editTextPassword = view.findViewById(R.id.edit_password)
        }
        if (view != null) {
            editTextUrl = view.findViewById(R.id.edit_url)
            if (this.url != null)
                editTextUrl.setText(url)
        }
        if (view != null) {
            editTextUser = view.findViewById(R.id.edit_userID)
            if (this.userId != null)
                editTextUser.setText(userId)
        }
        return alertDialog.create()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        try {
            listener = context as LoginDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement LoginDialogListener")
        }
    }
}

    public interface LoginDialogListener {
        fun applyTexts(username: String, password: String, url: String, user: String)
    }
