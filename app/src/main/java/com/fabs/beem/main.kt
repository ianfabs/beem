package com.fabs.beem

import android.content.Context
import android.icu.text.AlphabeticIndex
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.fabs.beem.atv.model.TreeNode
import com.fabs.beem.atv.view.AndroidTreeView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.reflect.jvm.internal.impl.load.java.lazy.ContextKt.child




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [main.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [main.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class main : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // updateTree(view)

        view.findViewById<FloatingActionButton>(R.id.btnCreate).setOnClickListener {
            findNavController().navigate(Uri.parse("beem://new"))
        }

        val tView = AndroidTreeView(activity as Context, generateTree(activity as Context, data))
        view.findViewById<LinearLayout>(R.id.tree).addView(tView.view)

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement FragmentInteractionListeners")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun updateTree(view: View) {
        val db = FirebaseFirestore.getInstance()

        db.collection("resources")
            .whereEqualTo("owner", FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(main::class.qualifiedName, "Listen failed.", e)
                    return@addSnapshotListener
                }

                for (document in querySnapshot!!) {
                    Log.d(main::class.qualifiedName, "${document.id} => ${document.data}")
                    val data = ArrayList<Node>(querySnapshot.map {
                        Node(
                            label = it.getString("label")!!,
                            uri = it.getString("uri")!!,
                            parent = it.getString("parent")!!
                        )
                    })
                    val tView = AndroidTreeView(activity as Context, generateTree(activity as Context, data))
                    view.findViewById<LinearLayout>(R.id.tree).addView(tView.view)
                }
            }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment main.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            main().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var data = arrayListOf(
            Node("one", "", ""),
            Node("two", "https://google.com/", "one"),
            Node("three", "https://dev.to/", "one"),
            Node("four", "https://fabs.dev", "")
        )

        fun generateTree(ctx: Context, data: ArrayList<Node>): TreeNode{
            val root = TreeNode.root()
            val map: HashMap<String, TreeNode> = hashMapOf()
            for (n in data) {
                val isBranch = n.uri == ""
                //val icon = if (isBranch) R.drawable.ic_folder else R.drawable.ic_link
                val childItem = MyHolder.IconTreeItem(n.label, n.uri)
                val child = TreeNode(childItem).setViewHolder(
                    MyHolder(
                        ctx,
                        n.uri == "",
                        if (isBranch) R.layout.parent else R.layout.child
                    )
                )
                
                map.takeIf { isBranch }?.apply {
                    this[n.label] = child as TreeNode
                }
                if (n.parent == "") {
                    root.addChild(child)
                }else {
                    map[n.parent]!!.addChild(child)
                }
            }
            return root
        }
    }
}

data class Node(var label: String, var uri: String, var parent: String?)