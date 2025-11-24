package com.example.creativasprint.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.creativasprint.R
import com.example.creativasprint.auth.SessionManager
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.databinding.FragmentClientMainBinding

class ClientMainFragment : Fragment() {
    private var _binding: FragmentClientMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var cartManager: CartManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        cartManager = CartManager(requireContext())

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val currentUser = sessionManager.getCurrentUser()

        binding.welcomeText.text = "Bienvenido a CreativasPrint"

        currentUser?.let { user ->
            binding.userNameText.text = "Hola, ${user.name}"
            binding.userNameText.visibility = View.VISIBLE
        }

        // Mostrar cantidad de items en el carrito
        val cartItemsCount = cartManager.getCartItemsCount()
        if (cartItemsCount > 0) {
            binding.cartItemsText.text = "Tienes $cartItemsCount items en tu carrito"
            binding.cartItemsText.visibility = View.VISIBLE
        } else {
            binding.cartItemsText.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.catalogButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args
            findNavController().navigate(ClientMainFragmentDirections.actionClientMainToProductList())
        }

        binding.cartButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args
            findNavController().navigate(ClientMainFragmentDirections.actionClientMainToCart())
        }

        binding.ordersButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args
            findNavController().navigate(ClientMainFragmentDirections.actionClientMainToOrderHistory())
        }

        binding.profileButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args
            findNavController().navigate(ClientMainFragmentDirections.actionClientMainToProfile())
        }

        binding.logoutButton.setOnClickListener {
            sessionManager.logout()
            cartManager.clearCart() // Limpiar carrito al cerrar sesión

            // CORREGIDO: Usar Safe Args con NavOptions para popUpTo
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.clientMainFragment, true)
                .build()

            findNavController().navigate(
                ClientMainFragmentDirections.actionClientMainToLogin(),
                navOptions
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar contador del carrito cuando el fragment se vuelve visible
        val cartItemsCount = cartManager.getCartItemsCount()
        if (cartItemsCount > 0) {
            binding.cartItemsText.text = "Tienes $cartItemsCount items en tu carrito"
            binding.cartItemsText.visibility = View.VISIBLE
        } else {
            binding.cartItemsText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}