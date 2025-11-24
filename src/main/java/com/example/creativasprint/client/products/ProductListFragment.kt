package com.example.creativasprint.client.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.creativasprint.R
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.databinding.FragmentProductListBinding
import com.example.creativasprint.databinding.ItemProductBinding
import com.example.creativasprint.model.Product
import com.example.creativasprint.network.ApiClient
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartManager: CartManager
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartManager = CartManager(requireContext())

        setupRecyclerView()
        setupSearch()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList, { product ->
            // onAddToCart
            cartManager.addToCart(product)
            Toast.makeText(requireContext(), "${product.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
        }, { product ->
            // onProductClick (podrías navegar a detalle del producto)
            // findNavController().navigate(R.id.productDetailFragment)
        })

        binding.productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter para el RecyclerView
    class ProductAdapter(
        private var products: List<Product>,
        private val onAddToCart: (Product) -> Unit,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        fun updateList(newList: List<Product>) {
            products = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ProductViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int = products.size

        inner class ProductViewHolder(
            private val binding: ItemProductBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(product: Product) {
                binding.productName.text = product.nombre
                binding.productPrice.text = "$${String.format("%.0f", product.precio)}"
                binding.productCategory.text = product.categoria

                // Cargar imagen con Coil
                com.example.creativasprint.utils.ImageLoader.loadImage(
                    binding.productImage,
                    product.getImageUrl()
                )

                binding.addToCartButton.setOnClickListener {
                    onAddToCart(product)
                }

                binding.root.setOnClickListener {
                    onProductClick(product)
                }
            }
        }
    }
}