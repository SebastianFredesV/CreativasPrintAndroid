package com.example.creativasprint.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.creativasprint.R
import com.example.creativasprint.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler.postDelayed({
            checkUserAndNavigate()
        }, 1000)
    }

    private fun checkUserAndNavigate() {
        val sessionManager = SessionManager(requireContext())

        if (sessionManager.isLoggedIn()) {
            val currentUser = sessionManager.getCurrentUser()
            if (currentUser != null) {
                val destination = if (sessionManager.isAdmin()) {
                    R.id.adminMainFragment
                } else {
                    R.id.clientMainFragment
                }
                findNavController().navigate(destination)
            } else {
                findNavController().navigate(R.id.loginFragment)
            }
        } else {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}