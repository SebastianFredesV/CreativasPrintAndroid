package com.example.creativasprint.admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativasprint.R
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.databinding.FragmentAdminOrdersBinding
import com.example.creativasprint.databinding.ItemAdminOrderBinding
import com.example.creativasprint.model.Order

class AdminOrdersFragment : Fragment() {
    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderManager: OrderManager
    private val orders = mutableListOf<Order>()
    private lateinit var adapter: AdminOrderAdapter

    private var searchQuery = ""
    private var filterStatus = "all"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderManager = OrderManager(requireContext())

        setupRecyclerView()
        setupSearch()
        setupFilterButtons() // Cambiado de setupFilterChips
        loadOrders()
    }

    private fun setupRecyclerView() {
        adapter = AdminOrderAdapter(orders, { order, newStatus ->
            orderManager.updateOrderStatus(order.id, newStatus)
            loadOrders()
        })

        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ordersRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText ?: ""
                filterOrders()
                return true
            }
        })
    }

    private fun setupFilterButtons() {
        binding.allChip.setOnClickListener { setFilterStatus("all") }
        binding.pendingChip.setOnClickListener { setFilterStatus("pending") }
        binding.acceptedChip.setOnClickListener { setFilterStatus("accepted") }
        binding.rejectedChip.setOnClickListener { setFilterStatus("rejected") }
        binding.shippedChip.setOnClickListener { setFilterStatus("shipped") }
    }

    private fun setFilterStatus(status: String) {
        filterStatus = status
        updateFilterButtons() // Cambiado de updateFilterChips
        filterOrders()
    }

    private fun updateFilterButtons() {
        // Actualizar colores según el filtro seleccionado
        val selectedColor = R.color.purple_500
        val unselectedColor = R.color.gray_light

        binding.allChip.setBackgroundColor(if (filterStatus == "all") requireContext().getColor(selectedColor) else requireContext().getColor(unselectedColor))
        binding.allChip.setTextColor(if (filterStatus == "all") requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black))

        binding.pendingChip.setBackgroundColor(if (filterStatus == "pending") requireContext().getColor(selectedColor) else requireContext().getColor(unselectedColor))
        binding.pendingChip.setTextColor(if (filterStatus == "pending") requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black))

        binding.acceptedChip.setBackgroundColor(if (filterStatus == "accepted") requireContext().getColor(selectedColor) else requireContext().getColor(unselectedColor))
        binding.acceptedChip.setTextColor(if (filterStatus == "accepted") requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black))

        binding.rejectedChip.setBackgroundColor(if (filterStatus == "rejected") requireContext().getColor(selectedColor) else requireContext().getColor(unselectedColor))
        binding.rejectedChip.setTextColor(if (filterStatus == "rejected") requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black))

        binding.shippedChip.setBackgroundColor(if (filterStatus == "shipped") requireContext().getColor(selectedColor) else requireContext().getColor(unselectedColor))
        binding.shippedChip.setTextColor(if (filterStatus == "shipped") requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black))
    }

    private fun loadOrders() {
        val orderList = orderManager.getOrders()
        orders.clear()
        orders.addAll(orderList)
        filterOrders()
    }

    private fun filterOrders() {
        val filtered = orders.filter { order ->
            val matchesSearch = searchQuery.isEmpty() ||
                    (order.customerName?.contains(searchQuery, ignoreCase = true) == true) ||
                    order.id.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (filterStatus) {
                "all" -> true
                else -> order.status == filterStatus
            }

            matchesSearch && matchesStatus
        }

        adapter.updateList(filtered)

        if (filtered.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.ordersRecyclerView.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.ordersRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter para el RecyclerView
    class AdminOrderAdapter(
        private var orders: List<Order>,
        private val onUpdateStatus: (Order, String) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder>() {

        fun updateList(newList: List<Order>) {
            orders = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
            val binding = ItemAdminOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AdminOrderViewHolder(binding)
        }

        override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
            holder.bind(orders[position])
        }

        override fun getItemCount(): Int = orders.size

        inner class AdminOrderViewHolder(
            private val binding: ItemAdminOrderBinding
        ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

            fun bind(order: Order) {
                binding.orderNumber.text = "Pedido #${order.id.take(8)}"
                binding.orderDate.text = order.createdAt ?: "Fecha no disponible"
                binding.orderStatus.text = order.getStatusText()
                binding.customerName.text = "Cliente: ${order.customerName ?: "Cliente no disponible"}"
                binding.customerEmail.text = "Email: ${order.customerEmail ?: "Email no disponible"}"
                binding.customerPhone.text = "Teléfono: ${order.customerPhone ?: "Teléfono no disponible"}"
                binding.shippingAddress.text = "Dirección: ${order.shippingAddress ?: "Dirección no disponible"}"
                binding.orderTotal.text = "$${String.format("%.0f", order.total)}"

                // Configurar color del estado
                val statusColor = when (order.status) {
                    "accepted" -> R.color.green
                    "rejected" -> R.color.red
                    "shipped" -> R.color.blue
                    else -> R.color.gray
                }
                binding.orderStatus.setTextColor(binding.root.context.getColor(statusColor))

                // Mostrar notas de envío si existen
                if (!order.shippingNotes.isNullOrEmpty()) {
                    binding.shippingNotes.text = "Notas: ${order.shippingNotes}"
                    binding.shippingNotes.visibility = View.VISIBLE
                } else {
                    binding.shippingNotes.visibility = View.GONE
                }

                // Mostrar items del pedido
                val itemsText = StringBuilder()
                val items = order.items ?: emptyList()
                items.take(2).forEach { item ->
                    itemsText.append("${item.productName ?: "Producto"} x${item.quantity}\n")
                    itemsText.append("$${String.format("%.0f", item.price * item.quantity)}\n\n")
                }

                if (items.size > 2) {
                    itemsText.append("... y ${items.size - 2} productos más")
                }

                binding.orderItems.text = itemsText.toString()

                // Configurar botones de acción según el estado
                setupActionButtons(order)
            }

            private fun setupActionButtons(order: Order) {
                when (order.status) {
                    "pending" -> {
                        binding.actionButtonsLayout.visibility = View.VISIBLE
                        binding.shippedStatusLayout.visibility = View.GONE

                        binding.rejectButton.setOnClickListener {
                            onUpdateStatus(order, "rejected")
                        }

                        binding.acceptButton.setOnClickListener {
                            onUpdateStatus(order, "accepted")
                        }
                    }
                    "accepted" -> {
                        binding.actionButtonsLayout.visibility = View.GONE
                        binding.shippedStatusLayout.visibility = View.VISIBLE
                        binding.shippedButton.visibility = View.VISIBLE
                        binding.finalStatusText.visibility = View.GONE

                        binding.shippedButton.setOnClickListener {
                            onUpdateStatus(order, "shipped")
                        }
                    }
                    "rejected", "shipped" -> {
                        binding.actionButtonsLayout.visibility = View.GONE
                        binding.shippedStatusLayout.visibility = View.VISIBLE
                        binding.shippedButton.visibility = View.GONE
                        binding.finalStatusText.visibility = View.VISIBLE

                        binding.finalStatusText.text = "Pedido ${order.getStatusText().lowercase()}"

                        val statusColor = when (order.status) {
                            "shipped" -> R.color.blue
                            "rejected" -> R.color.red
                            else -> R.color.gray
                        }
                        binding.finalStatusText.setTextColor(binding.root.context.getColor(statusColor))
                    }
                }
            }
        }
    }
}