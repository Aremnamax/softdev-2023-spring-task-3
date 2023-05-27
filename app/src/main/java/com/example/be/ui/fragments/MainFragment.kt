package com.example.be.ui.fragments

import android.app.Dialog
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.be.models.Folder
import com.example.be.ui.fragments.adapters.FolderAdapter
import com.example.be.R
import com.example.be.activity.APP_ACTIVITY
import com.example.be.activity.Registration
import com.example.be.ui.fragments.change_fragments.ChangeFolderNameFragment
import com.example.be.utilits.AUTH
import com.example.be.utilits.CHILD_FOLDERS
import com.example.be.utilits.CURRENT_UID
import com.example.be.utilits.FOLDER
import com.example.be.utilits.NODE_USERS
import com.example.be.utilits.REF_DATABASE_ROOT
import com.example.be.utilits.replaceActivity
import com.example.be.utilits.replaceFragment
import com.example.be.utilits.showToast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class MainFragment : Fragment(R.layout.fragment_main), FolderAdapter.OnItemClickListener {

    private lateinit var rcForFolderView: RecyclerView
    private lateinit var cardPlus: CardView
    private lateinit var cardChat: CardView
    private lateinit var cardFolder: CardView
    private lateinit var databaseReference: DatabaseReference

    private lateinit var adapter: FolderAdapter
    private lateinit var nameFolderList: ArrayList<Folder>
    private lateinit var data: ArrayList<Folder>



    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
        initFuns()
        /*initRecycleView()*/

    }

   /* override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
    }

    private fun initRecycleView() {

        rcForFolderView = view?.findViewById(R.id.rcForFolder)!!
        *//*rcForFolderView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        rcForFolderView.layoutManager = GridLayoutManager(APP_ACTIVITY, 2)
        rcForFolderView.setHasFixedSize(true)*//*

        mRefFolders = REF_DATABASE_ROOT.child(CURRENT_UID).child(CHILD_FOLDERS)
        Log.d("MyLog", "$rcForFolderView")

        val options = FirebaseRecyclerOptions.Builder<Folder>()
            .setQuery(mRefFolders, Folder::class.java)
            .build()

        Log.d("MyLog", "options")
        mAdapter = object : FirebaseRecyclerAdapter<Folder, FoldersHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.new_folder, parent, false)
                return FoldersHolder(view)
            }

            override fun onBindViewHolder(holder: FoldersHolder, position: Int, model: Folder) {
                holder.nameFolderAdapter.text = model.name
                Log.d("MyLog", model.name)
            }

        }

        rcForFolderView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        rcForFolderView.adapter = mAdapter
        mAdapter.startListening()
    }

    class FoldersHolder(view: View) : RecyclerView.ViewHolder(view.rootView) {
        val nameFolderAdapter: TextView = view.findViewById(R.id.nameFolder)
    }*/

    private fun initFuns() {
        fetchData()
        registerEvents()
    }

    private fun initFields() {
        data = ArrayList()
        nameFolderList = arrayListOf()
        adapter = FolderAdapter(data, this@MainFragment)

        databaseReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FOLDERS)


        cardPlus = view?.findViewById(R.id.cardViewPlus)!!
        cardChat = view?.findViewById(R.id.cardViewChat)!!
        cardFolder = view?.findViewById(R.id.cardViewFolder)!!


        rcForFolderView = view?.findViewById(R.id.rcForFolder)!!
        rcForFolderView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        rcForFolderView.layoutManager = GridLayoutManager(APP_ACTIVITY, 2)
        rcForFolderView.adapter = adapter
    }

    private fun registerEvents() {
        /*нажатие на +*/
        cardPlus.setOnClickListener {
            cardPlus.visibility = View.GONE
            cardChat.visibility = View.VISIBLE
            cardFolder.visibility = View.VISIBLE
        }

        /*добавление папки*/
        cardFolder.setOnClickListener {
            dialogCreateNewFolder()
        }


    }

    private fun fetchData() {
        Log.d("MyLog", "fetchData")
        var countOfFolders = 0
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nameFolderList.clear()
                if (snapshot.exists()) {
                    rcForFolderView.visibility = View.VISIBLE
                    view?.findViewById<TextView>(R.id.textView2)?.visibility = View.GONE
                    for (s in snapshot.children) {
                        countOfFolders++
                        val values = s.getValue(Folder::class.java)
                        nameFolderList.add(values!!)
                    }

                    val mAdapter = FolderAdapter(nameFolderList, this@MainFragment)
                    rcForFolderView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
                    rcForFolderView.layoutManager = GridLayoutManager(APP_ACTIVITY, 2)
                    rcForFolderView.adapter = mAdapter
                } else {
                    rcForFolderView.visibility = View.INVISIBLE
                    view?.findViewById<TextView>(R.id.textView2)?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun dialogCreateNewFolder() {
        val dialog = Dialog(APP_ACTIVITY)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fragment_add)
        val folderName = dialog.findViewById<EditText>(R.id.etFolderName)
        dialog.findViewById<Button>(R.id.btnСreateFolder).setOnClickListener {
            val name = folderName.text.toString()
            val keyFolder = databaseReference.push().key.toString()
            data.add(Folder(name, keyFolder))

            databaseReference.child(keyFolder).setValue(Folder(name, keyFolder)).addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Папка сохранена")
                }
            }
            view?.findViewById<TextView>(R.id.textView2)?.visibility = View.GONE
            dialog.dismiss()
        }

        dialog.findViewById<ImageView>(R.id.closeBtn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.create()
        dialog.show()
    }

    override fun onFolderClick(folder: Folder) {
        /*FOLDER = Folder(folder.name, folder.id)*/
        Log.d("MyLog", "${FOLDER.name}, ${FOLDER.id}")
        replaceFragment(InFolderFragment())
    }

    override fun onDeleteClick(folder: Folder) {

        databaseReference.child(folder.id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Удалено")
            } else {
                showToast("Не Удалено")
            }
        }
    }

    override fun onEditClick(folder: Folder) {
        replaceFragment(ChangeFolderNameFragment())
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean { /*функция запускается когда нажимаем на какой-нибудь элемент из меню*/
        when (item.itemId) {
            R.id.profile_menu_exit -> {
                AUTH.signOut()
                replaceActivity(Registration())
            }
            R.id.profile -> {
                replaceFragment(ProfileFragment())
            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }
}


