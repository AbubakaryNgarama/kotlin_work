package com.thundercode.learning.ui.videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.thundercode.learning.data.api.RetrofitClient
import com.thundercode.learning.data.repository.VideoRepository
import com.thundercode.learning.databinding.FragmentVideosBinding
import kotlinx.coroutines.launch

class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: VideoRepository
    private lateinit var adapter: VideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = VideoRepository(RetrofitClient.apiService)

        setupRecyclerView()
        setupSearchView()
        loadVideos()

        binding.swipeRefresh.setOnRefreshListener {
            loadVideos()
        }
    }

    private fun setupRecyclerView() {
        adapter = VideoAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadVideos(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun loadVideos(search: String? = null) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            repository.getVideos(search)
                .onSuccess { videos ->
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false

                    if (videos.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        adapter.updateVideos(videos)
                    }
                }
                .onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}