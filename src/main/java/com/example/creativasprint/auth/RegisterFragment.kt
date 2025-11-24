package com.example.creativasprint.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.creativasprint.databinding.FragmentRegisterBinding
import com.example.creativasprint.model.User
import com.example.creativasprint.network.ApiClient
import com.example.creativasprint.network.requests.RegisterRequest
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            performRegistration()
        }

        binding.loginButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args
            findNavController().navigate(RegisterFragmentDirections.actionRegisterToLogin())
        }
    }

    private fun performRegistration() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Completa todos los campos")
            return
        }

        if (password != confirmPassword) {
            showError("Las contraseñas no coinciden")
            return
        }

        if (password.length < 6) {
            showError("La contraseña debe tener al menos 6 caracteres")
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val registerRequest = RegisterRequest(name, email, password)
                val response = ApiClient.authService.register(registerRequest)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        val user = User(
                            id = authResponse.userId,
                            email = email,
                            name = name,
                            role = SessionManager.ROLE_CLIENT,
                            isActive = true
                        )

                        sessionManager.saveUserSession(user, authResponse.authToken)

                        // CORREGIDO: Usar Safe Args
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterToClientMain())
                    } else {
                        showError("Error en la respuesta del servidor")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Datos inválidos"
                        409 -> "El usuario ya existe"
                        500 -> "Error del servidor"
                        else -> "Error en el registro: ${response.code()}"
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
        binding.registerButton.visibility = if (loading) View.GONE else View.VISIBLE
        binding.registerButton.isEnabled = !loading
        binding.loginButton.isEnabled = !loading
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