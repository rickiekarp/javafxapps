package net.rickiekarp.sha1pass.libloader

import com.sun.jna.Library

interface IGoLibSha1PassTransformer : Library {
    fun EncodeToHex(text: String): String
    fun GetMD5Hash(text: String): String
}