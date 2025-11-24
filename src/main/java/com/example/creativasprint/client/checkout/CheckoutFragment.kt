package com.example.creativasprint.client.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.creativasprint.R
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.databinding.FragmentCheckoutBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartManager: CartManager
    private lateinit var orderManager: OrderManager

    private var isLoading = false
    private var errorMessage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartManager = CartManager(requireContext())
        orderManager = OrderManager(requireContext())

        setupUI()
        setupClickListeners()
        loadCartData()
    }

    private fun setupUI() {
        // Datos de prueba para desarrollo (quitar en producción)
        binding.customerNameEditText.setText("Cliente Ejemplo")
        binding.customerEmailEditText.setText("cliente@ejemplo.com")
        binding.customerPhoneEditText.setText("123456789")
        binding.shippingAddressEditText.setText("Calle Falsa 123")
    }

    private fun setupClickListeners() {
        binding.confirmOrderButton.setOnClickListener {
            processOrder()
        }
    }

    private fun loadCartData() {
        val cartItems = cartManager.getCartItems()
        val total = cartManager.getCartTotal()

        Log.d("CheckoutDebug", "Cart items loaded: ${cartItems.size}, Total: $total")

        if (cartItems.isEmpty()) {
            showEmptyState()
        } else {
            showCheckoutState()
            updateOrderSummary(cartItems, total)
        }
    }

    private fun updateOrderSummary(cartItems: List<com.example.creativasprint.model.CartItem>, total: Double) {
        val summaryText = StringBuilder()
        summaryText.append("Resumen del Pedido\n\n")

        cartItems.forEach { item ->
            summaryText.append("${item.productName} x${item.quantity}\n")
            summaryText.append("$${String.format("%.0f", item.productPrice * item.quantity)}\n\n")
        }

        summaryText.append("Total: $${String.format("%.0f", total)}")

        binding.orderSummaryText.text = summaryText.toString()
        binding.totalText.text = "$${String.format("%.0f", total)}"
        binding.confirmOrderButton.text = "Confirmar Pedido - $${String.format("%.0f", total)}"
    }

    private fun processOrder() {
        Log.d("CheckoutDebug", "Confirm button clicked")

        val customerName = binding.customerNameEditText.text.toString().trim()
        val customerEmail = binding.customerEmailEditText.text.toString().trim()
        val customerPhone = binding.customerPhoneEditText.text.toString().trim()
        val shippingAddress = binding.shippingAddressEditText.text.toString().trim()
        val shippingNotes = binding.shippingNotesEditText.text.toString().trim()

        if (!validateForm(customerName, customerEmail, customerPhone, shippingAddress)) {
            showError("Por favor completa todos los campos correctamente")
            return
        }

        val cartItems = cartManager.getCartItems()
        if (cartItems.isEmpty()) {
            showError("El carrito está vacío")
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                Log.d("CheckoutDebug", "Starting order processing...")

                val total = cartManager.getCartTotal()
                delay(2000) // Simular proceso de pago

                Log.d("CheckoutDebug", "Creating order...")
                val order = orderManager.createOrderFromCart(
                    cartItems = cartItems,
                    total = total,
                    customerName = customerName,
                    customerEmail = customerEmail,
                    customerPhone = customerPhone,
                    shippingAddress = shippingAddress,
                    shippingNotes = shippingNotes
                )

                Log.d("CheckoutDebug", "Order created: ${order.id}")
                orderManager.saveOrder(order)
                Log.d("CheckoutDebug", "Order saved successfully")

                cartManager.clearCart()
                Log.d("CheckoutDebug", "Cart cleared")

                showLoading(false)

                // Navegar a confirmación
                Log.d("CheckoutDebug", "Navigating to confirmation...")
                val action = CheckoutFragmentDirections.actionCheckoutToOrderConfirmation(order.id)
                findNavController().navigate(action)

            } catch (e: Exception) {
                Log.e("CheckoutDebug", "Error processing order: ${e.message}", e)
                showError("Error al procesar el pedido: ${e.message}")
                showLoading(false)
            }
        }
    }

    private fun validateForm(
        name: String,
        email: String,
        phone: String,
        address: String
    ): Boolean {
        val isValid = name.isNotEmpty() &&
                email.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                phone.isNotEmpty() &&
                address.isNotEmpty()

        // Actualizar estados de error en los campos
        binding.customerNameInput.error = if (name.isEmpty()) "Campo requerido" else null
        binding.customerEmailInput.error = when {
            email.isEmpty() -> "Campo requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }
        binding.customerPhoneInput.error = if (phone.isEmpty()) "Campo requerido" else null
        binding.shippingAddressInput.error = if (address.isEmpty()) "Campo requerido" else null

        return isValid
    }

    private fun showLoading(loading: Boolean) {
        isLoading = loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.confirmOrderButton.visibility = if (loading) View.GONE else View.VISIBLE
        binding.confirmOrderButton.isEnabled = !loading
    }

    private fun showError(message: String) {
        errorMessage = message
        binding.errorText.text = message
        binding.errorLayout.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.emptyCartLayout.visibility = View.VISIBLE
        binding.checkoutContentLayout.visibility = View.GONE
    }

    private fun showCheckoutState() {
        binding.emptyCartLayout.visibility = View.GONE
        binding.checkoutContentLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}