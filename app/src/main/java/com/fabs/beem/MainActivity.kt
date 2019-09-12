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
import kotlin.reflect.KCallable

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        var currentUser: FirebaseUser? = mAuth.currentUser
        findNavController(R.id.host).navigate(R.id.to_login)
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d(TAG, "${::createUserWithEmailAndPassword.name}:success")
                }else {
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    toast(this, "Authentication Failed", Toast.LENGTH_SHORT, "top")

                }
            }
    }

    companion object {
        private val TAG = MainActivity::class.qualifiedName
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
        val capsG= this.map { it.toUpperCase() } as String
        t.setGravity(Gravity::class.members.toTypedArray().let {
            val i: Int = it.map{ c -> c.name }.indexOf(capsG)
            it[i].call() as Int

        }, x as Int, y as Int)
    }
    t.show()
}