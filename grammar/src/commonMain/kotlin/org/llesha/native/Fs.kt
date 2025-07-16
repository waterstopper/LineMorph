package org.llesha.native

expect fun write(path: String, data: String)

expect fun exists(path: String): Boolean

expect fun delete(path: String): Boolean

expect fun read(path: String): String