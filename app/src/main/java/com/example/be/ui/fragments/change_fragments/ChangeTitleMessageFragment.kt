package com.example.be.ui.fragments.change_fragments

import android.widget.Button
import android.widget.EditText
import com.example.be.R
import com.example.be.activity.APP_ACTIVITY
import com.example.be.ui.fragments.BaseFragment
import com.example.be.utilits.CHILD_FOLDERS
import com.example.be.utilits.CURRENT_UID
import com.example.be.utilits.FOLDER
import com.example.be.utilits.MESSAGE
import com.example.be.utilits.NODE_USERS
import com.example.be.utilits.REF_DATABASE_ROOT
import com.example.be.utilits.TITLE_MESSAGE
import com.example.be.utilits.showToast

class ChangeTitleMessageFragment : BaseFragment(R.layout.fragment_change_title_message) {
    lateinit var TitleChange: EditText
    lateinit var btnDone: Button

    override fun onStart() {
        super.onStart()
        initFields()
        registerEvent()
    }

    private fun initFields() {
        TitleChange = view?.findViewById(R.id.enter_edit_title_message)!!
        btnDone = view?.findViewById(R.id.done_edit_title_message)!!
    }

    private fun registerEvent() {
        btnDone.setOnClickListener {
            if ((TitleChange.text).isEmpty()) {
                showToast("Введите название")
            } else {
                MESSAGE.title = TitleChange.text.toString()
                REF_DATABASE_ROOT.child(NODE_USERS)
                    .child(CURRENT_UID)
                    .child(CHILD_FOLDERS)
                    .child(FOLDER.id)
                    .child(MESSAGE.id)
                    .child(TITLE_MESSAGE)
                    .setValue(MESSAGE.title)
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }

        }
    }

}