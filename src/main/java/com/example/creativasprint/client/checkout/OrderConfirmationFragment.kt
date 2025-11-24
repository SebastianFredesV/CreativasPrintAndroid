package com.example.creativasprint.client.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.creativasprint.R
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.databinding.FragmentOrderConfirmationBinding

class OrderConfirmationFragment : Fragment() {
    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderManager: OrderManager
    private val args: OrderConfirmationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderManager = OrderManager(requireContext())

        setupUI()
        setupClickListeners()
        loadOrderData()
    }

    private fun setupUI() {
        // Configurar icono de confirmación
        binding.confirmationIcon.setImageResource(R.drawable.ic_check_circle)
        binding.confirmationIcon.setColorFilter(
            requireContext().getColor(R.color.purple_500)
        )
    }

    private fun loadOrderData() {
        val order = args.orderId?.let { orderManager.getOrderById(it) }

        binding.confirmationTitle.text = "¡Pedido Confirmado!"
        binding.confirmationMessage.text = "Tu pedido ha sido procesado exitosamente"

        order?.let {
            binding.orderSummaryCard.visibility = View.VISIBLE
            binding.orderNumberText.text = "Número: #${it.id.take(8)}"
            binding.orderTotalText.text = "Total: $${String.format("%.0f", it.total)}"
            binding.orderStatusText.text = "Estado: ${it.getStatusText()}"
            binding.orderDateText.text = "Fecha: ${it.createdAt ?: "N/A"}"
        } ?: run {
            binding.orderSummaryCard.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.homeButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args con popUpTo
            findNavController().navigate(
                OrderConfirmationFragmentDirections.actionOrderConfirmationToClientMain()
            )
        }

        binding.ordersButton.setOnClickListener {
            // CORREGIDO: Usar Safe Args
            findNavController().navigate(
                OrderConfirmationFragmentDirections.actionOrderConfirmationToOrderHistory()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}