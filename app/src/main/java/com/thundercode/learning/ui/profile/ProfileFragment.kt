package com.thundercode.learning.ui.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.thundercode.learning.R
import com.thundercode.learning.data.local.PrefsManager
import com.thundercode.learning.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefsManager: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PrefsManager(requireContext())
        val user = prefsManager.getUser()

        user?.let {
            binding.tvName.text = it.name
            binding.tvEmail.text = it.email
            binding.tvRole.text = it.roleDisplayName
            binding.tvPhone.text = it.phone ?: "Not provided"

            // Set role badge color
            val roleColor = when {
                it.isAdmin -> R.color.admin_color
                it.isContentCreator -> R.color.creator_color
                else -> R.color.viewer_color
            }
            binding.chipRole.setChipBackgroundColorResource(roleColor)

            // Set initial letter
            binding.tvInitial.text = it.name.first().uppercase()

            // Display permissions
            displayPermissions(it.role)
        }
    }

    private fun displayPermissions(role: String) {
        val permissions = when (role) {
            "admin" -> listOf(
                "✓ View all videos",
                "✓ Manage all users",
                "✓ Promote/Demote users",
                "✓ Delete any video",
                "✓ View statistics",
                "✓ Full admin access"
            )
            "content_creator" -> listOf(
                "✓ View all videos",
                "✓ Create videos",
                "✓ Edit own videos",
                "✓ Delete own videos",
                "✓ View own statistics"
            )
            else -> listOf(
                "✓ View all videos",
                "✓ Comment on videos",
                "✓ Like videos"
            )
        }

        binding.tvPermissions.text = permissions.joinToString("\n")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
