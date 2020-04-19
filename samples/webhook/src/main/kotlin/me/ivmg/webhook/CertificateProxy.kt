package me.ivmg.webhook

import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

object CertificateFactory {

    const val PASSWORD = "your_pass"
    const val ALIAS = "YOUR_ALIAS"

    private val internalFile by lazy {
        File("/etc/letsencrypt/live/$ALIAS/keystore.jks").let { file ->
            if (file.exists() || file.isAbsolute)
                file
            else
                File(".", "/etc/letsencrypt/live/$ALIAS/keystore.jks").absoluteFile
        }
    }

    fun keyStoreFile(): File = internalFile

    fun get(): KeyStore {
        return KeyStore.getInstance("JKS").apply {
            FileInputStream(internalFile).use {
                load(it, PASSWORD.toCharArray())
            }

            requireNotNull(getKey(ALIAS, PASSWORD.toCharArray()) == null) {
                "The specified key keyalias $ALIAS doesn't exist in the key store ${keyStoreFile()}"
            }
        }
    }
}
