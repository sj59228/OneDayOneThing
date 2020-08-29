package com.hj.onedayonething

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Log.d
import android.util.Log.w
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.signup_activity.*


class SignUpActivity : AppCompatActivity() {
    //    private var mFunctions: FirebaseFunctions? = null
    val db = FirebaseFirestore.getInstance()
    var idCheck: Boolean = false    // false :
    val PASS: Boolean = true    // false :
    val NON_PASS: Boolean = false    // false :

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        init()
        addListener()
    }

    private fun init() {
//        mFunctions = FirebaseFunctions.getInstance();
//        val db = FirebaseFirestore.getInstance()
        constEdit2.transformationMethod = PasswordTransformationMethod.getInstance()    // 비밀번호 감추기
        constEdit3.transformationMethod = PasswordTransformationMethod.getInstance()    // 비밀번호 확인 감추기
    }

    private fun addListener() {
        constEdit1.setOnClickListener {
            if (idCheck == PASS) {
                // 다이얼로그
                val builder = AlertDialog.Builder(ContextThemeWrapper(this@SignUpActivity, R.style.Theme_AppCompat_Light_Dialog))
                builder.setTitle("알림")
                builder.setMessage("아이디를 변경하시겠습니까?")

                /*builder.setPositiveButton("확인") {dialog, id ->
                }
                builder.setNegativeButton("취소") {dialog, id ->
                }*/
                builder.setPositiveButton("확인") { _, _ ->
                    d("alert ok", "확인")
                    idCheck = NON_PASS
                    buttonIdCheck.isEnabled = true
                }
                builder.setNegativeButton("취소") { _, _ ->
                    d("alert cancel", "취소")
                }

                builder.show()
            }
        }

        // 아이디 중복 확인 버튼
        buttonIdCheck.setOnClickListener {
            if (constEdit1.length() == 0) {
                Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (constEdit1.length() < 8) {
                Toast.makeText(this, "8자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val inputID: String = constEdit1.text.toString()
            db.collection("users")
                    .whereEqualTo("ID", inputID)    // ID와 일치하는 document만 받아옴
                    .get()
                    .addOnSuccessListener { documents ->
                        d("hjtest", "일치하는 숫자 : ${documents.size()}")
                        if (documents.size() > 0) { // 겹치는 것이 있을 경우
                            Toast.makeText(this, "아이디가 종복됩니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        } else {    // 겹치는 것이 없을 경우
                            val builder = AlertDialog.Builder(ContextThemeWrapper(this@SignUpActivity, R.style.Theme_AppCompat_Light_Dialog))
                            builder.setTitle("사용가능한 아이디입니다")
                            builder.setMessage("사용하시겠습니까?")

                            /*builder.setPositiveButton("확인") {dialog, id ->
                            }
                            builder.setNegativeButton("취소") {dialog, id ->
                            }*/
                            builder.setPositiveButton("확인") { _, _ ->
                                d("hjtest", "확인")
                                idCheck = PASS
                                buttonIdCheck.isEnabled = false
                            }
                            builder.setNegativeButton("취소") { _, _ ->
                                d("hjtest", "취소")
                            }
                            builder.show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        w("hjtest", "Error getting documents: ", exception)
                    }
        }

        // 비밀번호 체크박스
        showPw.setOnClickListener {
            if (showPw.isChecked) {   // 눌러졌을 경우
                constEdit2.transformationMethod = HideReturnsTransformationMethod.getInstance()    // 비밀번호 표시
                constEdit3.transformationMethod = HideReturnsTransformationMethod.getInstance()    // 비밀번호 확인 표시
            } else {
                constEdit2.transformationMethod = PasswordTransformationMethod.getInstance()    // 비밀번호 감추기
                constEdit3.transformationMethod = PasswordTransformationMethod.getInstance()    // 비밀번호 확인 감추기
            }
        }

        // 비밀번호 표시 글자
        constText4.setOnClickListener {
            if (showPw.isChecked) {   // 눌러졌을 경우
                showPw!!.isChecked = false
                constEdit2.transformationMethod = PasswordTransformationMethod.getInstance()    // 비밀번호 표시
                constEdit3.transformationMethod = PasswordTransformationMethod.getInstance()    // 비밀번호 확인 표시
            } else {
                showPw!!.isChecked = true
                constEdit2.transformationMethod = HideReturnsTransformationMethod.getInstance()    // 비밀번호 감추기
                constEdit3.transformationMethod = HideReturnsTransformationMethod.getInstance()    // 비밀번호 확인 감추기
            }
        }

        // 회원가입 완료 버튼
        constButton1.setOnClickListener {
            if (idCheck != PASS) {
                Toast.makeText(this, "아이디 중복확인을 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (constEdit2.text.toString().length < 8) {   // 길이가 너무 짧을 경우
                Toast.makeText(this, "비밀번호를 8자이상으로 설정해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (constEdit2.text.toString().length >= 24) {   // 길이가 너무 길 경우
                Toast.makeText(this, "비밀번호 길이가 너무 깁니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (constEdit2.text.toString() != constEdit3.text.toString()) {   // 비밀번호를 똑같이 2번 입력한 경우
                Toast.makeText(this, "비밀번호를 똑같이 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inputID: String = constEdit1.text.toString()
            val inputPW: String = constEdit2.text.toString()

            // Create a new user with a first and last name
            val user = hashMapOf(
                    "ID" to inputID,
                    "PW" to inputPW
            )
            // Add a new document with a generated ID
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        d("hjtest", "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(this, "아이디 : ${constEdit1.text}\n비밀번호 : ${constEdit2.text}\n회원가입 완료", Toast.LENGTH_SHORT).show()    // 회원가입 완료
                        finish()
                    }
                    .addOnFailureListener { e ->
                        w("hjtest", "Error adding document", e)
                    }

        }
    }
}