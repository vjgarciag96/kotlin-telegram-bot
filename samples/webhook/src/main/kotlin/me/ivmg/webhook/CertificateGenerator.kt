package me.ivmg.webhook

import io.ktor.network.tls.certificates.generateCertificate
import java.io.File
import java.security.KeyStore

object CertificateFactory {

    private val internalFile by lazy {
        File("build/temporary.jks").apply {
            parentFile.mkdirs()
        }
    }

    fun keyStoreFile(): File = internalFile

    fun get(): KeyStore {
        return generateCertificate(internalFile) // Generates the certificate
    }
}
