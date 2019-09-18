package com.fabs.beem

import android.content.Context
import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.kotlinProperty

class MainActivity : AppCompatActivity(), CreateAccount.FragmentInteractionListeners, Login.FragmentInteractionListeners {

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var userRef: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        var currentUser: FirebaseUser? = mAuth.currentUser
    }

    override fun onCreateAccountButtonInteraction(user: User) {
        createUserWithEmailAndPassword(user)
    }

    override fun onLoginButtonInteraction(user: User) {
        signInWithEmailAndPassword(user)
    }

    private fun createUserWithEmailAndPassword(user: User) {
        val (_, _, email, password) = user
        mAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val db = FirebaseFirestore.getInstance()
                    val userMap = user.toHashMap()
                    userMap["id"] = it.result!!.user!!.uid
                    db.collection("users")
                        .add(userMap)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    Log.d(TAG, "${::createUserWithEmailAndPassword.name}:success")
                    toast(this, "createUserWithEmailAndPassword:success", Toast.LENGTH_SHORT, "top")
                }else {
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    toast(this, "Authentication Failed", Toast.LENGTH_SHORT, "top")

                }
            }
    }

    private fun signInWithEmailAndPassword(user: User) {
        val (_, _, email, password) = user
        mAuth.signInWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = mAuth.currentUser as FirebaseUser
                    toast(this, "signInWithEmail:success", Toast.LENGTH_SHORT, "top")
                    findNavController(R.id.host).navigate(R.id.main)
                    val db = FirebaseFirestore.getInstance()
                    db
                        .collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener {
                            userRef = User.fromHashMap(it.documents[0].data as HashMap<String, String>)
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", it.exception)
                    toast(this, "Authentication failed.", Toast.LENGTH_SHORT, "top")
                    // updateUI(null)
                }
            }
    }

    companion object {
        public val TAG = MainActivity::class.qualifiedName
    }
}

/**
 * @author Ian Fabs <ian@fabs.dev>
 * A more convenient toast method
 *
 * One toast to rule them all
 *
 * @see Toast
 * @see Gravity
 *
 * @param ctx the toast context
 * @param msg the toast message
 * @param dur the toast duration; Either [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG]
 * @param grav the toast gravity
 * @param x the toast x offset
 * @param y the toast y offset
 * @return void
 */
fun toast(ctx: Context, msg: String, dur: Int, grav: String?, x: Int? = 0, y: Int? = 0) {
    val t = Toast.makeText(ctx, msg, dur)
    grav!!.run {
        val fieldName: String = this.toUpperCase(Locale.getDefault())
        val field = Gravity::class.java.getDeclaredField(fieldName).get(Int)!! as Int
        t.setGravity(field, x as Int , y as Int)
    }
    t.show()
}