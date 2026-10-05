package com.ute.studentprofile_lab3

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ute.studentprofile_lab3.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var originalStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Nhan du lieu sinh vien cu tu MainActivity
        @Suppress("DEPRECATION")
        originalStudent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("STUDENT", Student::class.java)
        } else {
            intent.getSerializableExtra("STUDENT") as? Student
        }

        originalStudent?.let {
            binding.edtName.setText(it.name)
            binding.edtClass.setText(it.className)
            binding.edtGpa.setText(it.gpa.toString())
        }

        // 2. Xu ly su kien nut Luu va Phan Hoi
        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val className = binding.edtClass.text.toString().trim()
            val gpa = binding.edtGpa.text.toString().toDoubleOrNull()

            // Validate du lieu nhap vao
            if (name.isEmpty() || className.isEmpty() || gpa == null || gpa !in 0.0..4.0) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên, lớp và GPA hợp lệ (0.0 - 4.0)!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cap nhat doi tuong sinh vien
            val updated = originalStudent?.copy(name = name, className = className, gpa = gpa)
                ?: Student(
                    id = "2415053122202",
                    name = name,
                    className = className,
                    email = "anh.hn@ute.udn.vn",
                    gpa = gpa
                )

            // Tra ve ket qua RESULT_OK cho MainActivity
            val resIntent = Intent().apply {
                putExtra("UPDATED", updated)
            }
            setResult(Activity.RESULT_OK, resIntent)
            finish()
        }

        // 3. Xu ly su kien nut Huy Bo
        binding.btnCancel.setOnClickListener {
            // Khong goi setResult, he thong tu mac dinh la RESULT_CANCELED
            finish()
        }
    }
}
