package com.example.creativasprint.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.creativasprint.R
import com.example.creativasprint.auth.SessionManager
import com.example.creativasprint.databinding.FragmentAdminMainBinding

class AdminMainFragment : Fragment() {
    private var _binding: FragmentAdminMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val currentUser = sessionManager.getCurrentUser()
        binding.welcomeText.text = "Panel de Administración"

        currentUser?.let { user ->
            binding.userNameText.text = "Bienvenido, ${user.name}"
            binding.userNameText.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        binding.productsCard.setOnClickListener {
            findNavController().navigate(R.id.adminProductsFragment)
        }

        binding.usersCard.setOnClickListener {
            findNavController().navigate(R.id.adminUsersFragment)
        }

        binding.ordersCard.setOnClickListener {
            findNavController().navigate(R.id.adminOrdersFragment)
        }

        binding.logoutButton.setOnClickListener {
            sessionManager.logout()
            // Corrected navigation call
            findNavController().navigate(R.id.loginFragment, null, navOptions {
                popUpTo(R.id.adminMainFragment) {
                    inclusive = true
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}