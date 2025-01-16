package com.example.playlistmaker.mediateca.ui.fragment.createPlaylist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatplaylistBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.presenter.createPlaylist.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlayListFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private lateinit var binding: FragmentCreatplaylistBinding
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var imageUri: Uri? = null
    private var title: String? = null
    var playlist: Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatplaylistBinding.inflate(inflater, container, false)
        playlist = arguments?.getSerializable(KEY_PLAYLIST) as? Playlist
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    binding.imageView.invalidate()
                    binding.imageView.setImageURI(uri)
                    imageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireActivity(),R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setTitle(getString(R.string.end_create_pleylist))
            .setMessage(getString(R.string.data_has_been_delete))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
            }.setPositiveButton(getString(R.string.end)) { dialog, which ->
                findNavController().navigateUp()
            }

        binding.toolbarSettings.setNavigationOnClickListener {
            if (!title.isNullOrBlank()) {
                if (playlist == null) {
                    confirmDialog.show()
                } else {
                    findNavController().navigateUp()
                }
            } else {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!title.isNullOrBlank()) {
                if (playlist == null) {
                    confirmDialog.show()
                } else {
                    findNavController().navigateUp()
                }
            } else {
                findNavController().navigateUp()
            }
        }

        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val textViewEditText = binding.textView.editText

        textViewEditText?.addTextChangedListener { text ->
            title = text.toString()
            val bool = !text.isNullOrBlank()
            binding.button.isEnabled = bool
        }



        viewModel.getIsPlaylistCreatedLiveData.observe(viewLifecycleOwner) { message ->
            if (playlist == null) {
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.playlist_has_been_create)} ${message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            findNavController().navigateUp()
        }


        binding.button.setOnClickListener {


                viewModel.savePlaylist(
                    playlist,
                    binding.textView.editText?.text.toString(),
                    binding.textView2.editText?.text.toString(),
                    imageUri
                )

        }
        if (playlist != null) {
            bind(playlist!!)
            binding.button.isEnabled = true
        }
    }

    private fun bind(playlist: Playlist) = with(binding) {
        textView.editText?.setText(playlist.title)
        textView2.editText?.setText(playlist.description)
        Glide.with(imageView)
            .load(playlist.directory)
            .placeholder(R.drawable.placeholder2)
            .centerCrop()
            .into(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    companion object {
        private const val KEY_PLAYLIST = "KEY_PLAYLIST"
    }
}