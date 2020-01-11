package me.ivmg.webhook

import java.io.File

fun getFileFromResources(resName: String): File =
    File(
        contextClassLoader().getResource(resName)?.file
                ?: error("unable to get file $resName from resources")
    )

private fun contextClassLoader(): ClassLoader = Thread.currentThread().contextClassLoader