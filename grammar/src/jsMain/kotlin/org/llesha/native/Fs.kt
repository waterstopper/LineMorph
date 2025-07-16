package org.llesha.native

actual fun write(path: String, data: String) {
    return
}

actual fun exists(path: String): Boolean {
    return false
}

actual fun delete(path: String): Boolean {
    return true
}

actual fun read(path: String): String {
    return ""
}