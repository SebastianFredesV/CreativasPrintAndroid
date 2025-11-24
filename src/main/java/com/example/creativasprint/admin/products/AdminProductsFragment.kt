package com.example.creativasprint.admin.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativasprint.R
import com.example.creativasprint.databinding.FragmentAdminProductsBinding
import com.example.creativasprint.databinding.ItemAdminProductBinding
import com.example.creativasprint.model.Product
import com.example.creativasprint.network.ApiClient
import kotlinx.coroutines.launch

class AdminProductsFragment : Fragment() {
    private var _binding: FragmentAdminProductsBinding? = null
    private val binding get() = _binding!!
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: AdminProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = AdminProductAdapter(productList, { product ->
            // onEdit - Usar la nueva action del nav_graph corregido
            val action = AdminProductsFragmentDirections.actionAdminProductsToProductForm(product.id.toString())
            findNavController().navigate(action)
        }, { product ->
            // onDelete
            deleteProduct(product.id)
        })

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return true
            }
        })
    }

    private fun setupClickListeners() {
        binding.addProductButton.setOnClickListener {
            // Usar la nueva action del nav_graph corregido
            val action = AdminProductsFragmentDirections.actionAdminProductsToProductForm("new")
            findNavController().navigate(action)
        }
    }

    private fun loadProducts() {
        showLoading(true)
        binding.errorLayout.visibility = View.GONE
        binding.emptyLayout.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getProducts()

                if (response.isSuccessful) {
                    val productsList = response.body() ?: emptyList()
                    val validProducts = productsList.filter {
                        it.isValid() && it.nombre.isNotEmpty() && it.precio > 0
                    }

                    productList.clear()
                    productList.addAll(validProducts)
                    adapter.notifyDataSetChanged()

                    if (validProducts.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContent()
                    }
                } else {
                    showError("Error ${response.code()} al cargar productos")
                }
            } catch (e: Exception) {
                showError("Error de conexión: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun filterProducts(query: String) {
        val filtered = if (query.isEmpty()) {
            productList
        } else {
            productList.filter { product ->
                product.nombre.contains(query, ignoreCase = true) ||
                        product.descripcion.contains(query, ignoreCase = true) ||
                        product.categoria.contains(query, ignoreCase = true)
            }
        }

        adapter.updateList(filtered)

        if (filtered.isEmpty() && productList.isNotEmpty()) {
            binding.emptySearchLayout.visibility = View.VISIBLE
            binding.productsRecyclerView.visibility = View.GONE
        } else {
            binding.emptySearchLayout.visibility = View.GONE
            binding.productsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun deleteProduct(productId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.deleteProduct(productId)

                if (response.isSuccessful) {
                    // Eliminar de la lista local
                    productList.removeAll { it.id == productId }
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()

                    if (productList.isEmpty()) {
                        showEmptyState()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.productsRecyclerView.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        binding.errorLayout.visibility = View.VISIBLE
        binding.errorText.text = message
        binding.productsRecyclerView.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.productsRecyclerView.visibility = View.GONE
    }

    private fun showContent() {
        binding.errorLayout.visibility = View.GONE
        binding.emptyLayout.visibility = View.GONE
        binding.productsRecyclerView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadProducts() // Recargar cuando el fragment se vuelve visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter para el RecyclerView
    class AdminProductAdapter(
        private var products: List<Product>,
        private val onEdit: (Product) -> Unit,
        private val onDelete: (Product) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

        fun updateList(newList: List<Product>) {
            products = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
            val binding = ItemAdminProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AdminProductViewHolder(binding)
        }

        override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int = products.size

        inner class AdminProductViewHolder(
            private val binding: ItemAdminProductBinding
        ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

            fun bind(product: Product) {
                binding.productName.text = product.nombre
                binding.productPrice.text = "$${String.format("%.0f", product.precio)}"
                binding.productCategory.text = "Categoría: ${product.categoria}"
                binding.productStock.text = "Stock: ${product.stock}"
                binding.productDescription.text = product.descripcion

                // Estado del producto
                binding.productStatus.text = "Estado: ${if (product.isActive) "Activo" else "Inactivo"}"
                val statusColor = if (product.isActive) R.color.green else R.color.red
                binding.productStatus.setTextColor(binding.root.context.getColor(statusColor))

                // URL de imagen (para debug)
                binding.productImageUrl.text = "Imagen: ${product.getImageUrl().take(50)}..."

                // Botones de acción
                binding.editButton.setOnClickListener {
                    onEdit(product)
                }

                binding.deleteButton.setOnClickListener {
                    onDelete(product)
                }
            }
        }
    }
}