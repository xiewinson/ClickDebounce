package io.github.xiewinson.clickdebounce.plugin.extension

/**
 * Created by xiewinson
 */

open class ClickDebounceExtension {

    var packages = mutableSetOf<String>()

    /**
     * 默认的点击间隔时间
     */
    var interval = 300L
}