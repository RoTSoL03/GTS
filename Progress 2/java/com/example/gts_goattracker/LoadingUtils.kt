package com.example.gts_goattracker

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object LoadingUtils {

    fun showLoading(progressBar: ProgressBar, show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun showError(activity: AppCompatActivity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun showSuccess(activity: AppCompatActivity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}