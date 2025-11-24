package com.example.creativasprint.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.creativasprint.R
import com.example.creativasprint.databinding.FragmentLoginBinding
import com.example.creativasprint.model.User
import com.example.creativasprint.network.ApiClient
import com.example.creativasprint.network.requests.LoginRequest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            performLogin()
        }

        binding.registerButton.setOnClickListener {
            // Usar Safe Args
            findNavController().navigate(LoginFragmentDirections.actionLoginToRegister())
        }
    }

    private fun performLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Completa todos los campos")
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = ApiClient.authService.login(loginRequest)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        val isAdmin = email == "admin@creativasprint.com"
                        val user = User(
                            id = authResponse.userId,
                            email = email,
                            name = if (isAdmin) "Administrador" else "Cliente",
                            role = if (isAdmin) SessionManager.ROLE_ADMIN else SessionManager.ROLE_CLIENT,
                            isActive = true
                        )

                        sessionManager.saveUserSession(user, authResponse.authToken)

                        // Usar Safe Args
                        if (isAdmin) {
                            findNavController().navigate(LoginFragmentDirections.actionLoginToAdminMain())
                        } else {
                            findNavController().navigate(LoginFragmentDirections.actionLoginToClientMain())
                        }
                    } else {
                        showError("Error en la respuesta del servidor")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401, 403 -> "Credenciales incorrectas"
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor"
                        else -> "Error en el login: ${response.code()}"
                    }
                    showError(errorMsg)
                }
            } catch (e: Exception) {
                showError("Error de conexión: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.loginButton.visibility = if (loading) View.GONE else View.VISIBLE
        binding.loginButton.isEnabled = !loading
        binding.registerButton.isEnabled = !loading
    }

    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}