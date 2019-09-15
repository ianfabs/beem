package com.fabs.beem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Login.FragmentInteractionListeners] interface
 * to handle interaction events.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listeners: FragmentInteractionListeners? = null

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
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        val email    = view.findViewById<EditText>(R.id.inEmail)
        val password = view.findViewById<EditText>(R.id.inPassword)

        view.findViewById<Button>(R.id.btnLogin).setOnClickListener {
            if (inEmail.text.toString() == "") {
                inEmail.invalidate()
            } else if (inPassword.text.toString() == "") {
                inPassword.invalidate()
            } else {
                listeners?.onLoginButtonInteraction(
                    User(
                        email = email.text.toString(),
                        password = password.text.toString()
                    )
                )
            }

        }

        view.findViewById<TextView>(R.id.linkToCreateAccount).setOnClickListener {
            findNavController().navigate(Uri.parse("beem://signup"))
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractionListeners) {
            listeners = context
        } else {
            throw NotImplementedError("$context must implement FragmentInteractionListeners")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listeners = null
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
    interface FragmentInteractionListeners {
        // TODO: Update argument type and name
        fun onLoginButtonInteraction(user: User)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
