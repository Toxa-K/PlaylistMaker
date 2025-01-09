package com.example.playlistmaker.mediateca.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatplaylistBinding
import com.example.playlistmaker.mediateca.presenter.CreatPlaylist.CreatPlaylistViewModel
import com.example.playlistmaker.mediateca.presenter.playList.PlaylistViewModel
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Manifest

class CreatePlayListFragment : Fragment() {

    private val viewModel: CreatPlaylistViewModel by viewModel()
    private lateinit var binding: FragmentCreatplaylistBinding

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
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().navigateUp()  // Возвращаемся назад
        }

        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val textViewEditText =  binding.textView.editText
        textViewEditText?.addTextChangedListener{ text ->
            binding.button.isEnabled = !text.isNullOrBlank()
        }


        binding.button.setOnClickListener{
            viewModel.creatPlaylist()
        }


    }


    private fun saveImageToPrivateStorage(uri: Uri) {
        Toast.makeText(requireContext(), "Image has ben saved", Toast.LENGTH_LONG).show()
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistmaker"
            )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Восстанавливаем видимость нижней панели навигации
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
            View.VISIBLE
    }

    /*companion object {
        fun newInstance(): CreatePlayListFragment {
            return CreatePlayListFragment()
        }
    }
*/
}