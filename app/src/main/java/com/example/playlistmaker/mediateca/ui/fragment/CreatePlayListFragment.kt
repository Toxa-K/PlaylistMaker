package com.example.playlistmaker.mediateca.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatplaylistBinding
import com.example.playlistmaker.mediateca.presenter.createPlaylist.CreatePlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlayListFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private lateinit var binding: FragmentCreatplaylistBinding
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.imageView.setImageURI(uri)
                    imageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.end_create_pleylist))
            .setMessage(getString(R.string.data_has_been_delete))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
            }.setPositiveButton(getString(R.string.end)) { dialog, which ->
                findNavController().navigateUp()
            }

        binding.toolbarSettings.setNavigationOnClickListener {
            confirmDialog.show()
        }

        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val textViewEditText = binding.textView.editText

        textViewEditText?.addTextChangedListener { text ->
            binding.button.isEnabled = !text.isNullOrBlank()
        }

        viewModel.getIsPlaylistCreatedLiveData.observe(viewLifecycleOwner) { isCreated ->
            if (isCreated) {
                Toast.makeText(requireContext(), getString(R.string.playlist_has_been_create), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }


        binding.button.setOnClickListener {
            viewModel.savePlaylist(
                binding.textView.editText?.text.toString(),
                binding.textView2.editText?.text.toString(),
                imageUri
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Восстанавливаем видимость нижней панели навигации
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
            View.VISIBLE
        (requireActivity().findViewById<View>(R.id.Constraint) as? View)?.visibility =
            View.VISIBLE
    }

    companion object {
        fun newInstance(): CreatePlayListFragment {
            return CreatePlayListFragment()
        }
    }
}