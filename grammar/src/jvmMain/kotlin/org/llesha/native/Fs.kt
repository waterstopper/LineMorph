package org.llesha.native

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

actual fun write(path: String, data: String) {
    Files.writeString(Path.of(path), data)
}

actual fun delete(path: String): Boolean {
    return File(path).delete()
}

actual fun exists(path: String): Boolean {
    return File(path).exists()
}

actual fun read(path: String): String {
    return Files.readString(Path.of(path))
}