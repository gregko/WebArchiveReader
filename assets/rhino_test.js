function hello(param) {
    thisActivity.makeToast("Hello, Android! " + param);

    if (typeof log != 'undefined') {
        log("JavaScript say hello to " + param);
        log("Also, I can access Java object: " + thisActivity);
        log(thisActivity.strFromJava("In hello()")); // can pass args both ways!
    }
    return { foo: "bar in JavaScript" };
}