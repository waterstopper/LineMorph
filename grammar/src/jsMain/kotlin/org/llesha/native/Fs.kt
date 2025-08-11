package org.llesha.native

import kotlinx.browser.localStorage
import org.llesha.exception.EvalException

actual fun write(path: String, data: String) {
    return localStorage.setItem(path, data)
}

actual fun exists(path: String): Boolean {
    return localStorage.getItem(path) != null
}

actual fun delete(path: String): Boolean {
    val res = localStorage.getItem(path) != null
    localStorage.removeItem(path)
    return res
}

actual fun read(path: String): String {
    return localStorage.getItem(path) ?: throw EvalException("Cannot read non-existing file")
}