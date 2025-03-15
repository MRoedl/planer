package com.example.planer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planer.ViewModel.AppDataStore
import com.example.planer.ViewModel.MealViewModel
import com.example.planer.database.JsonConverter
import com.example.planer.database.PlanerDatabase
import com.example.planer.databinding.FragmentSettingsBinding
import com.example.planer.ftp.FtpManager
import kotlinx.coroutines.launch
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appDataStore = AppDataStore.getInstance(requireContext())

        lifecycleScope.launch {
            appDataStore.getDaysToPlan().collect {
                binding.editTextDaysToPlan.setText(it.toString())
            }
        }

        binding.editTextDaysToPlan.doAfterTextChanged { editText ->
            val daysToPlan = editText.toString().toIntOrNull()
            if (daysToPlan != null) {
                lifecycleScope.launch {
                    appDataStore.setDaysToPlan(daysToPlan)
                }
            }
        }

        binding.btnExport.setOnClickListener {
            export()
        }

        binding.btnImport.setOnClickListener {
            import()
        }
    }

    fun import() {
        val ftpmanager = FtpManager()
        lifecycleScope.launch {
            ftpmanager.connect()

            val deviceFilePath = MealViewModel().filePath + "/db2.json"
            val file = File(deviceFilePath)
            val serverFilePath = "/Dokumente/db.json"
            val success = ftpmanager.downloadFile(serverFilePath, file)

            if (success) {
                val planerDao = PlanerDatabase.getDatabase(requireContext()).planerDao()
                planerDao.deleteAllMeals()
                planerDao.deleteAllMealPlans()

                JsonConverter().importDatabaseFromJson(PlanerDatabase.getDatabase(requireContext()), deviceFilePath)
            }

            ftpmanager.disconnect()
        }
    }

    fun export() {
        val ftpmanager = FtpManager()
        lifecycleScope.launch {
            //todo
            ftpmanager.connect()

            val deviceFilePath = MealViewModel().filePath + "/db.json"
            val file = JsonConverter().exportDatabaseToJson(PlanerDatabase.getDatabase(requireContext()), deviceFilePath)
            //todo abfrage
            val serverFilePath = "/Dokumente/db.json"
            if (file != null) ftpmanager.uploadFile(file, serverFilePath)

            ftpmanager.disconnect()
        }
    }

}