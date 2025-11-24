package com.example.creativasprint.client.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativasprint.R
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.databinding.FragmentCartBinding
import com.example.creativasprint.databinding.ItemCartBinding
import com.example.creativasprint.model.CartItem

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartManager: CartManager
    private lateinit var orderManager: OrderManager
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartManager = CartManager(requireContext())
        orderManager = OrderManager(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadCartItems()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(cartItems, { productId, newQuantity ->
            // onQuantityChange
            cartManager.updateQuantity(productId, newQuantity)
            loadCartItems()
        }, { productId ->
            // onRemoveItem
            cartManager.removeFromCart(productId)
            loadCartItems()
        })

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.checkoutButton.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                findNavController().navigate(R.id.checkoutFragment)
            }
        }

        binding.continueShoppingButton.setOnClickListener {
            findNavController().navigate(R.id.productListFragment)
        }
    }

    private fun loadCartItems() {
        val items = cartManager.getCartItems()
        val total = cartManager.getCartTotal()

        cartItems.clear()
        cartItems.addAll(items)
        adapter.notifyDataSetChanged()

        if (items.isEmpty()) {
            showEmptyState()
        } else {
            showCartState()
            binding.totalText.text = "$${String.format("%.0f", total)}"
        }
    }

    private fun showEmptyState() {
        binding.emptyCartLayout.visibility = View.VISIBLE
        binding.cartContentLayout.visibility = View.GONE
        binding.checkoutButton.visibility = View.GONE
    }

    private fun showCartState() {
        binding.emptyCartLayout.visibility = View.GONE
        binding.cartContentLayout.visibility = View.VISIBLE
        binding.checkoutButton.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter para el RecyclerView
    class CartAdapter(
        private var cartItems: List<CartItem>,
        private val onQuantityChange: (Int, Int) -> Unit,
        private val onRemoveItem: (Int) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
            val binding = ItemCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return CartViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            holder.bind(cartItems[position])
        }

        override fun getItemCount(): Int = cartItems.size

        fun updateList(newList: List<CartItem>) {
            cartItems = newList
            notifyDataSetChanged()
        }

        inner class CartViewHolder(
            private val binding: ItemCartBinding
        ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

            fun bind(item: CartItem) {
                binding.productName.text = item.productName
                binding.unitPrice.text = "$${String.format("%.0f", item.productPrice)} c/u"
                binding.subtotalText.text = "Subtotal: $${String.format("%.0f", item.productPrice * item.quantity)}"
                binding.quantityText.text = item.quantity.toString()

                // Cargar imagen del producto
                com.example.creativasprint.utils.ImageLoader.loadImage(
                    binding.productImage,
                    item.productImage
                )

                // Botones de cantidad
                binding.decreaseButton.setOnClickListener {
                    if (item.quantity > 1) {
                        onQuantityChange(item.productId, item.quantity - 1)
                    } else {
                        onRemoveItem(item.productId)
                    }
                }

                binding.increaseButton.setOnClickListener {
                    onQuantityChange(item.productId, item.quantity + 1)
                }

                // Botón eliminar
                binding.deleteButton.setOnClickListener {
                    onRemoveItem(item.productId)
                }
            }
        }
    }
}