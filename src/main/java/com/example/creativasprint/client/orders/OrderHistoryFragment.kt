package com.example.creativasprint.client.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativasprint.R
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.databinding.FragmentOrderHistoryBinding
import com.example.creativasprint.databinding.ItemOrderBinding
import com.example.creativasprint.model.Order

class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderManager: OrderManager
    private val orders = mutableListOf<Order>()
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderManager = OrderManager(requireContext())

        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(orders)
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ordersRecyclerView.adapter = adapter
    }

    private fun loadOrders() {
        showLoading(true)

        val orderList = orderManager.getOrders()
        orders.clear()
        orders.addAll(orderList)
        adapter.notifyDataSetChanged()

        if (orderList.isEmpty()) {
            showEmptyState()
        } else {
            showOrdersState()
        }

        showLoading(false)
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.ordersRecyclerView.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showEmptyState() {
        binding.emptyOrderLayout.visibility = View.VISIBLE
        binding.ordersRecyclerView.visibility = View.GONE
    }

    private fun showOrdersState() {
        binding.emptyOrderLayout.visibility = View.GONE
        binding.ordersRecyclerView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadOrders() // Recargar órdenes cuando el fragment se vuelve visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter para el RecyclerView
    class OrderAdapter(
        private var orders: List<Order>
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return OrderViewHolder(binding)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            holder.bind(orders[position])
        }

        override fun getItemCount(): Int = orders.size

        fun updateList(newList: List<Order>) {
            orders = newList
            notifyDataSetChanged()
        }

        inner class OrderViewHolder(
            private val binding: ItemOrderBinding
        ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

            fun bind(order: Order) {
                binding.orderNumber.text = "Pedido #${order.id.take(8)}"
                binding.orderDate.text = order.createdAt ?: "Fecha no disponible"
                binding.orderStatus.text = order.getStatusText()
                binding.orderTotal.text = "$${String.format("%.0f", order.total)}"

                // Configurar color del estado
                val statusColor = when (order.status) {
                    "accepted" -> R.color.green
                    "rejected" -> R.color.red
                    "shipped" -> R.color.blue
                    else -> R.color.gray
                }
                binding.orderStatus.setTextColor(requireContext().getColor(statusColor))

                // Mostrar items del pedido
                val itemsText = StringBuilder()
                order.items?.take(2)?.forEach { item ->
                    itemsText.append("${item.productName ?: "Producto"} x${item.quantity}\n")
                    itemsText.append("$${String.format("%.0f", item.price * item.quantity)}\n\n")
                } ?: run {
                    itemsText.append("No hay productos en este pedido\n")
                }

                val itemsCount = order.items?.size ?: 0
                if (itemsCount > 2) {
                    itemsText.append("... y ${itemsCount - 2} productos más")
                }

                binding.orderItems.text = itemsText.toString()
            }

            private fun requireContext() = binding.root.context
        }
    }
}