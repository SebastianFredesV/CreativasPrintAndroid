package com.example.creativasprint.admin.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativasprint.R
import com.example.creativasprint.databinding.FragmentAdminUsersBinding
import com.example.creativasprint.databinding.ItemAdminUserBinding
import com.example.creativasprint.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdminUsersFragment : Fragment() {
    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private val users = mutableListOf<User>()
    private lateinit var adapter: AdminUserAdapter

    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadUsers()
        updateStats()
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(users, { user, newRole ->
            // onUpdateRole
            updateUserRole(user.id, newRole)
        }, { user, isActive ->
            // onToggleStatus
            toggleUserStatus(user.id, isActive)
        })

        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.usersRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText ?: ""
                filterUsers()
                return true
            }
        })
    }

    private fun loadUsers() {

        val sampleUsers = listOf(
            User(
                id = 1,
                email = "cliente@creativasprint.com",
                name = "Cliente Demo",
                role = "client",
                isActive = true,
                phone = "+56 9 1234 5678",
                address = "Av. Principal 123, Santiago"
            ),
            User(
                id = 2,
                email = "admin@creativasprint.com",
                name = "Administrador",
                role = "admin",
                isActive = true,
                phone = "+56 9 8765 4321",
                address = "Oficina Central, CreativasPrint"
            ),
            User(
                id = 3,
                email = "maria.gonzalez@email.com",
                name = "María González",
                role = "client",
                isActive = true,
                phone = "+56 9 5555 1234",
                address = "Calle Secundaria 456, Providencia"
            ),
            User(
                id = 4,
                email = "usuario.bloqueado@email.com",
                name = "Usuario Bloqueado",
                role = "client",
                isActive = false, // ✅ Usuario bloqueado
                phone = "+56 9 9999 8888",
                address = "Sin dirección registrada"
            ),
            User(
                id = 5,
                email = "juan.perez@email.com",
                name = "Juan Pérez",
                role = "client",
                isActive = true,
                phone = "+56 9 7777 6666",
                address = "Plaza Central 789, Las Condes"
            )
        )

        users.clear()
        users.addAll(sampleUsers)
        filterUsers()
        updateStats()
    }

    private fun filterUsers() {
        val filtered = if (searchQuery.isEmpty()) {
            users
        } else {
            users.filter { user ->
                (user.name?.contains(searchQuery, ignoreCase = true) == true) ||
                        (user.email?.contains(searchQuery, ignoreCase = true) == true)
            }
        }

        adapter.updateList(filtered)

        if (filtered.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.usersRecyclerView.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.usersRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun updateStats() {
        binding.totalUsers.text = users.size.toString()
        binding.adminUsers.text = users.count { it.role == "admin" }.toString()
        binding.clientUsers.text = users.count { it.role == "client" }.toString()
    }

    private fun updateUserRole(userId: Int, newRole: String) {
        val userIndex = users.indexOfFirst { it.id == userId }
        if (userIndex != -1) {
            users[userIndex] = users[userIndex].copy(role = newRole)
            adapter.notifyItemChanged(userIndex)
            updateStats()
            Toast.makeText(requireContext(), "Rol cambiado a $newRole", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleUserStatus(userId: Int, isActive: Boolean) {
        val userIndex = users.indexOfFirst { it.id == userId }
        if (userIndex != -1) {
            users[userIndex] = users[userIndex].copy(isActive = isActive)
            adapter.notifyItemChanged(userIndex)
            val action = if (isActive) "activado" else "desactivado"
            Toast.makeText(requireContext(), "Usuario $action", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter para el RecyclerView
    class AdminUserAdapter(
        private var users: List<User>,
        private val onUpdateRole: (User, String) -> Unit,
        private val onToggleStatus: (User, Boolean) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder>() {

        fun updateList(newList: List<User>) {
            users = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {
            val binding = ItemAdminUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AdminUserViewHolder(binding)
        }

        override fun onBindViewHolder(holder: AdminUserViewHolder, position: Int) {
            holder.bind(users[position])
        }

        override fun getItemCount(): Int = users.size

        inner class AdminUserViewHolder(
            private val binding: ItemAdminUserBinding
        ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

            fun bind(user: User) {
                binding.userName.text = user.name ?: "Usuario sin nombre"
                binding.userEmail.text = user.email ?: "Email no disponible"
                binding.changeRoleButton.text = user.role ?: "client"
                binding.userStatus.text = if (user.isActive) "🟢 Activo" else "🔴 Inactivo"

                // Configurar color del estado
                val statusColor = if (user.isActive) R.color.green else R.color.red
                binding.userStatus.setTextColor(binding.root.context.getColor(statusColor))

                // Mostrar teléfono si existe
                if (!user.phone.isNullOrEmpty()) {
                    binding.userPhone.text = "📞 ${user.phone}"
                    binding.userPhone.visibility = View.VISIBLE
                } else {
                    binding.userPhone.visibility = View.GONE
                }

                // Mostrar dirección si existe
                if (!user.address.isNullOrEmpty()) {
                    binding.userAddress.text = "📍 ${user.address}"
                    binding.userAddress.visibility = View.VISIBLE
                } else {
                    binding.userAddress.visibility = View.GONE
                }

                // Configurar botones de acción
                binding.changeRoleButton.setOnClickListener {
                    showRoleDialog(user)
                }

                binding.toggleStatusButton.setOnClickListener {
                    onToggleStatus(user, !user.isActive)
                }

                // Configurar icono de estado
                if (user.isActive) {
                    binding.toggleStatusButton.setImageResource(R.drawable.ic_check_circle)
                    binding.toggleStatusButton.imageTintList = android.content.res.ColorStateList.valueOf(
                        binding.root.context.getColor(R.color.green)
                    )
                } else {
                    binding.toggleStatusButton.setImageResource(R.drawable.ic_block)
                    binding.toggleStatusButton.imageTintList = android.content.res.ColorStateList.valueOf(
                        binding.root.context.getColor(R.color.red)
                    )
                }
            }

            private fun showRoleDialog(user: User) {
                val context = binding.root.context
                android.app.AlertDialog.Builder(context)
                    .setTitle("Cambiar Rol del Usuario")
                    .setMessage("Selecciona el nuevo rol para:\n${user.name ?: "Usuario"} (${user.email})")
                    .setPositiveButton("👑 Administrador") { dialog, which ->
                        onUpdateRole(user, "admin")
                    }
                    .setNegativeButton("👤 Cliente") { dialog, which ->
                        onUpdateRole(user, "client")
                    }
                    .setNeutralButton("Cancelar", null)
                    .show()
            }
        }
    }
}