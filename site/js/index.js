import {changeTheme} from "./loader.js";
import {byId} from "./utils.js";

console.log("Hello");

byId("main-title").onclick = () => {
    console.log("Change")
    changeTheme()
}

let code = byId("code")
let output = byId("stdout")
let error = byId("stderr")

code.value =
    `assert-eq(1, 1)
# is same as:
assert(eq(1, 1))

# here is a more complex example
assert-not-not(true)
# which is similar to
assert(not(not(true)))`

byId("eval").onclick = () => {
    console.log(code.value)
    try {
        let res = window.MorphLang.org.llesha.Pipeline.eval(`load("file")
            load("lib")
            load("natives")
            load("ops")
            load("string")
            ` + code.value)
        output.textContent = res
        error.textContent = ""
    } catch (e) {
        output.textContent = "Failed:"
        error.textContent = e
    }
}
