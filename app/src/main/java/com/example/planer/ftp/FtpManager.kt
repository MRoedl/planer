package com.example.planer.ftp

import android.util.Log
import com.example.planer.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FtpManager {
    private val ftpClient = FTPClient()
    private val server = BuildConfig.FTP_SERVER
    private val port = BuildConfig.FTP_PORT.toInt()
    private val user = BuildConfig.FTP_USER
    private val pass = BuildConfig.FTP_PASSWORD

    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            ftpClient.connect(server, port)
            ftpClient.login(user, pass)
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
            Log.d("NACHRICHT", "Connected to FTP server")
            true
        } catch (e: Exception) {
            Log.e("NACHRICHT", "Error connecting to FTP server", e)
            false
        }
    }

    suspend fun disconnect(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            ftpClient.logout()
            ftpClient.disconnect()
            Log.d("FTP", "Disconnected from FTP server")
            true
        } catch (e: Exception) {
            Log.e("FTP", "Error disconnecting from FTP server", e)
            false
        }
    }

    suspend fun uploadFile(localFile: File, remoteFile: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = FileInputStream(localFile)
            val success = ftpClient.storeFile(remoteFile, inputStream)
            inputStream.close()
            if (success) {
                Log.d("FTP", "File uploaded successfully: $remoteFile")
            } else {
                Log.e("FTP", "Error uploading file: $remoteFile")
            }
            success
        } catch (e: Exception) {
            Log.e("FTP", "Error uploading file: $remoteFile", e)
            false
        }
    }

    suspend fun downloadFile(remoteFile: String, localFile: File): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val outputStream = FileOutputStream(localFile)
            val success = ftpClient.retrieveFile(remoteFile, outputStream)
            outputStream.close()
            if (success) {
                Log.d("FTP", "File downloaded successfully: $remoteFile")
            } else {
                Log.e("FTP", "Error downloading file: $remoteFile")
            }
            success
        } catch (e: Exception) {
            Log.e("FTP", "Error downloading file: $remoteFile", e)
            false
        }
    }

}