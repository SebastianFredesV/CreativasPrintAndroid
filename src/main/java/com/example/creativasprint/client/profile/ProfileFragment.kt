package com.example.creativasprint.client.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.creativasprint.R
import com.example.creativasprint.auth.SessionManager
import com.example.creativasprint.databinding.FragmentProfileBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private var isEditing = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupToolbar()
        setupUI()
        setupClickListeners()
        loadUserData()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Mi Perfil"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        // Inicialmente ocultar botones de edición
        binding.editSaveContainer.visibility = View.GONE
    }

    private fun setupClickListeners() {
        binding.editButton.setOnClickListener {
            enableEditing()
        }

        binding.cancelButton.setOnClickListener {
            cancelEditing()
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadUserData() {
        val currentUser = sessionManager.getCurrentUser()
        currentUser?.let { user ->
            binding.nameEditText.setText(user.name)
            binding.emailEditText.setText(user.email)
            binding.phoneEditText.setText(user.phone ?: "")
            binding.addressEditText.setText(user.address ?: "")
        }
    }

    private fun enableEditing() {
        isEditing = true
        binding.editButton.visibility = View.GONE
        binding.editSaveContainer.visibility = View.VISIBLE

        // Habilitar edición de campos
        binding.nameEditText.isEnabled = true
        binding.emailEditText.isEnabled = true
        binding.phoneEditText.isEnabled = true
        binding.addressEditText.isEnabled = true
    }

    private fun cancelEditing() {
        isEditing = false
        binding.editButton.visibility = View.VISIBLE
        binding.editSaveContainer.visibility = View.GONE

        // Deshabilitar edición y restaurar datos
        binding.nameEditText.isEnabled = false
        binding.emailEditText.isEnabled = false
        binding.phoneEditText.isEnabled = false
        binding.addressEditText.isEnabled = false

        loadUserData() // Restaurar datos originales
    }

    private fun saveProfile() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val address = binding.addressEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            binding.nameInput.error = if (name.isEmpty()) "Campo requerido" else null
            binding.emailInput.error = if (email.isEmpty()) "Campo requerido" else null
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                delay(1000) // Simular guardado

                // Actualizar usuario en sesión
                val currentUser = sessionManager.getCurrentUser()
                val updatedUser = currentUser?.copy(
                    name = name,
                    email = email,
                    phone = phone.ifEmpty { null },
                    address = address.ifEmpty { null }
                )

                updatedUser?.let { user ->
                    sessionManager.saveUserSession(user)
                }

                showLoading(false)
                cancelEditing() // Volver al modo visualización

                // Mostrar mensaje de éxito
                // Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                showLoading(false)
                // Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        isLoading = loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !loading
        binding.cancelButton.isEnabled = !loading

        if (loading) {
            binding.saveButton.text = "Guardando..."
        } else {
            binding.saveButton.text = "Guardar"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}