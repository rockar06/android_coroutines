package com.rockar.coroutines.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.rockar.coroutines.databinding.ActivityMainBinding
import com.rockar.coroutines.domain.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.contentMain.rootLayout.setOnClickListener {
            viewModel.onMainViewClicked()
        }
    }

    private fun setupObservers() {
        setupTitleObserver()
        setupTapsObserver()
        setupSpinnerObserver()
        setupSnackBarObserver()
    }

    private fun setupTitleObserver() {
        viewModel.title.observe(this) { value ->
            value?.let {
                binding.contentMain.title.text = it
            }
        }
    }

    private fun setupTapsObserver() {
        viewModel.taps.observe(this) { value ->
            value?.let {
                binding.contentMain.taps.text = it
            }
        }
    }

    private fun setupSpinnerObserver() {
        viewModel.spinner.observe(this) { value ->
            binding.contentMain.spinner.visibility = if (value) View.VISIBLE else View.GONE
        }
    }

    private fun setupSnackBarObserver() {
        viewModel.snackbar.observe(this) { value ->
            value?.let {
                showSnackBar(it)
                viewModel.onSnackBarShown()
            }
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.contentMain.rootLayout, text, Snackbar.LENGTH_SHORT).show()
    }
}
