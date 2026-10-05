package com.ute.studentprofile_lab3

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ute.studentprofile_lab3.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TAG_LIFECYCLE"
    }

    private lateinit var binding: ActivityMainBinding

    // Thong tin ca nhan sinh vien
    private var student = Student(
        id = "2415053122202",
        name = "Huỳnh Ngọc Anh",
        className = "24T2",
        email = "anh.hn@ute.udn.vn",
        gpa = 3.8
    )

    // 1. Launcher chinh sua thong tin sinh vien (StartActivityForResult)
    private val editLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                @Suppress("DEPRECATION")
                val updatedStudent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getSerializableExtra("UPDATED", Student::class.java)
                } else {
                    result.data?.getSerializableExtra("UPDATED") as? Student
                }

                updatedStudent?.let {
                    student = it
                    bindData(student)
                    Toast.makeText(this, "Đã lưu thành công thông tin của ${it.name}!", Toast.LENGTH_SHORT).show()
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Đã hủy chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show()
            }
        }

    // 2. Launcher chon anh tu thu vien (GetContent)
    private val galleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imgAvatar.setImageURI(it)
                Toast.makeText(this, "Đã thay đổi ảnh đại diện thành công!", Toast.LENGTH_SHORT).show()
            }
        }

    // 3. Launcher xin quyen Camera tai runtime (RequestPermission)
    private val cameraLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Đã cấp quyền Camera! Có thể chụp ảnh ngay.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bạn đã từ chối quyền Camera. Tính năng này bị khóa!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity đang được khởi tạo và nạp layout")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hien thi du lieu sinh vien len giao dien
        bindData(student)

        // Nut 1: Chinh sua ho so (Explicit Intent & Activity Result API)
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("STUDENT", student)
            }
            editLauncher.launch(intent)
        }

        // Nut 2: Doi avatar tu Gallery (GetContent)
        binding.btnChangeAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Nut 3: Goi Co van hoc tap (Implicit Intent ACTION_DIAL)
        binding.btnCallHotline.setOnClickListener {
            val hotline = "0905123456"
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$hotline")
            }
            try {
                startActivity(dialIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Không tìm thấy ứng dụng cuộc gọi phù hợp!", Toast.LENGTH_SHORT).show()
            }
        }

        // Nut 4: Kiem tra quyen Camera (Runtime Permission)
        binding.btnRequestCamera.setOnClickListener {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }

        // Nut mo rong: Xem vi tri truong tren Google Maps (BT Mo rong 1)
        binding.btnOpenMap.setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:16.0768,108.2141?q=Đại+học+Sư+phạm+Kỹ+thuật+Đà+Nẵng")
            }
            try {
                startActivity(mapIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Không tìm thấy ứng dụng bản đồ phù hợp!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindData(s: Student) {
        binding.tvName.text = s.name
        binding.tvDetails.text = "MSSV: ${s.id} | Lớp: ${s.className}"
        val classification = when {
            s.gpa >= 3.6 -> "Xuất sắc"
            s.gpa >= 3.2 -> "Giỏi"
            s.gpa >= 2.5 -> "Khá"
            else -> "Trung bình"
        }
        binding.tvGpaBadge.text = String.format(Locale.US, "GPA: %.2f (%s)", s.gpa, classification)
    }

    // ── Ghi log 7 phuong thuc callback vong doi Activity ───────────
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity đã hiển thị trên màn hình (chưa nhận tương tác)")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity ở trạng thái đỉnh stack, sẵn sàng tương tác!")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity bị che khuất một phần (mở dialog / chuẩn bị rời đi)")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity bị ẩn hoàn toàn (bấm Home / mở Activity khác)")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: Người dùng mở lại Activity từ trạng thái Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity bị hủy hoàn toàn khỏi bộ nhớ RAM!")
    }
}