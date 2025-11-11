package com.example.shoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Nhóm xác minh
    TextInputEditText etUserName, etEmail;
    Button btnVerify;

    // Nhóm đổi mật khẩu
    TextInputLayout tilNewPass, tilConfirmPass;
    TextInputEditText etNewPassword, etConfirmPassword;
    Button btnReset;

    ShoppingDatabase db;
    int verifiedUserId = 0; // lưu userId sau khi verify thành công

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Map view
        etUserName = findViewById(R.id.et_user_name_fp);
        etEmail = findViewById(R.id.et_email_fp);
        btnVerify = findViewById(R.id.btn_verify_fp);

        tilNewPass = findViewById(R.id.til_new_password_fp);
        tilConfirmPass = findViewById(R.id.til_confirm_password_fp);
        etNewPassword = findViewById(R.id.et_new_password_fp);
        etConfirmPassword = findViewById(R.id.et_confirm_password_fp);
        btnReset = findViewById(R.id.btn_reset_password);

        db = new ShoppingDatabase(this);

        // Ẩn nhóm đổi mật khẩu lúc đầu
        setResetGroupVisible(false);

        btnVerify.setOnClickListener(v -> {
            String username = safe(etUserName);
            String email = safe(etEmail);

            if (username.isEmpty()) { etUserName.setError("Enter username"); return; }
            if (email.isEmpty()) { etEmail.setError("Enter email"); return; }

            int id = db.checkUserByEmail(username, email);
            if (id > 0) {
                verifiedUserId = id;
                Toast.makeText(this, "Verified. Please enter new password", Toast.LENGTH_SHORT).show();
                setVerifyGroupEnabled(false);
                setResetGroupVisible(true);
            } else {
                Toast.makeText(this, "Invalid username or email", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            if (verifiedUserId <= 0) {
                Toast.makeText(this, "Please verify first", Toast.LENGTH_SHORT).show();
                return;
            }
            String pass = safe(etNewPassword);
            String confirm = safe(etConfirmPassword);

            if (pass.isEmpty()) { etNewPassword.setError("Enter new password"); return; }
            if (confirm.isEmpty()) { etConfirmPassword.setError("Confirm password"); return; }
            if (!pass.equals(confirm)) { etConfirmPassword.setError("Passwords do not match"); return; }

            boolean ok = db.updateUserPassword(verifiedUserId, pass);
            if (ok) {
                Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });
        TextView tvLoginFromFp = findViewById(R.id.tv_login_from_fp);
        tvLoginFromFp.setOnClickListener(v ->
                startActivity(new android.content.Intent(ForgotPasswordActivity.this, LoginActivity.class))
        );
    }

    private void setVerifyGroupEnabled(boolean enabled) {
        etUserName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        btnVerify.setEnabled(enabled);
    }

    private void setResetGroupVisible(boolean visible) {
        int vis = visible ? View.VISIBLE : View.GONE;
        tilNewPass.setVisibility(vis);
        tilConfirmPass.setVisibility(vis);
        btnReset.setVisibility(vis);
    }

    private String safe(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

}
