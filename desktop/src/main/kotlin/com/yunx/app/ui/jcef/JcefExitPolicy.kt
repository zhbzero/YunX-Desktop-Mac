package com.yunx.app.ui.jcef

/** The pinned Mac JCEF engine cannot safely run its explicit native shutdown on macOS 27. */
internal object JcefExitPolicy {
    fun finish(isMac: Boolean, persist: () -> Boolean, disposeAndWait: () -> Boolean): Boolean {
        if (!persist()) return false
        // A normal process exit releases the Mac engine after cookies and application
        // data have been saved. Do not call the faulty native Context::Destroy path.
        return isMac || disposeAndWait()
    }
}
