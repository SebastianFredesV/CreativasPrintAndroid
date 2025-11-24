package com.example.creativasprint.admin.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.creativasprint.R
import com.example.creativasprint.databinding.FragmentAdminProductFormBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdminProductFormFragment : Fragment() {
    private var _binding: FragmentAdminProductFormBinding? = null
    private val binding get() = _binding!!
    private val args: AdminProductFormFragmentArgs by navArgs()

    private var isLoading = false
    private val isEditing by lazy { args.productId != null && args.productId != "new" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupUI()
        setupClickListeners()
        loadProductData()
    }

    private fun setupToolbar() {
        binding.toolbar.title = if (isEditing) "Editar Producto" else "Nuevo Producto"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        // Configurar campos numéricos
        binding.precioEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.stockEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            saveProduct()
        }
    }

    private fun loadProductData() {
        if (isEditing) {
            showLoading(true)

            // Simular carga de producto (en una app real, esto vendría de una API)
            lifecycleScope.launch {
                delay(1000) // Simular carga de API

                // Datos de ejemplo para edición
                binding.nombreEditText.setText("Agenda Alicia en el País de las Maravillas")
                binding.descripcionEditText.setText("Sumérgete en el mágico y oscuro mundo de Tim Burton con esta hermosa agenda...")
                binding.precioEditText.setText("15000")
                binding.colorEditText.setText("Azul")
                binding.categoriaEditText.setText("Agendas")
                binding.stockEditText.setText("10")
                binding.imagenEditText.setText("img/AgendaAliciaimg1.jpg")
                binding.activeSwitch.isChecked = true

                showLoading(false)
            }
        }
    }

    private fun saveProduct() {
        val nombre = binding.nombreEditText.text.toString().trim()
        val descripcion = binding.descripcionEditText.text.toString().trim()
        val precio = binding.precioEditText.text.toString().trim()
        val color = binding.colorEditText.text.toString().trim()
        val categoria = binding.categoriaEditText.text.toString().trim()
        val stock = binding.stockEditText.text.toString().trim()
        val imagen = binding.imagenEditText.text.toString().trim()
        val isActive = binding.activeSwitch.isChecked

        if (!validateProductForm(nombre, descripcion, precio, color, categoria, stock)) {
            showValidationErrors(nombre, descripcion, precio, color, categoria, stock)
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                delay(1500) // Simular guardado en API

                // Aquí iría la lógica real para guardar en Xano
                // if (isEditing) {
                //     ApiClient.apiService.updateProduct(productId, product)
                // } else {
                //     ApiClient.apiService.createProduct(product)
                // }

                showLoading(false)
                Toast.makeText(requireContext(),
                    if (isEditing) "Producto actualizado" else "Producto creado",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error al guardar producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateProductForm(
        nombre: String,
        descripcion: String,
        precio: String,
        color: String,
        categoria: String,
        stock: String
    ): Boolean {
        return nombre.isNotEmpty() &&
                descripcion.isNotEmpty() &&
                precio.isNotEmpty() &&
                color.isNotEmpty() &&
                categoria.isNotEmpty() &&
                stock.isNotEmpty() &&
                precio.toDoubleOrNull() != null &&
                stock.toIntOrNull() != null
    }

    private fun showValidationErrors(
        nombre: String,
        descripcion: String,
        precio: String,
        color: String,
        categoria: String,
        stock: String
    ) {
        binding.nombreInput.error = if (nombre.isEmpty()) "Campo requerido" else null
        binding.descripcionInput.error = if (descripcion.isEmpty()) "Campo requerido" else null
        binding.precioInput.error = when {
            precio.isEmpty() -> "Campo requerido"
            precio.toDoubleOrNull() == null -> "Precio inválido"
            else -> null
        }
        binding.colorInput.error = if (color.isEmpty()) "Campo requerido" else null
        binding.categoriaInput.error = if (categoria.isEmpty()) "Campo requerido" else null
        binding.stockInput.error = when {
            stock.isEmpty() -> "Campo requerido"
            stock.toIntOrNull() == null -> "Stock inválido"
            else -> null
        }
    }

    private fun showLoading(loading: Boolean) {
        isLoading = loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !loading

        if (loading) {
            binding.saveButton.text = "Guardando..."
        } else {
            binding.saveButton.text = if (isEditing) "Actualizar Producto" else "Crear Producto"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}