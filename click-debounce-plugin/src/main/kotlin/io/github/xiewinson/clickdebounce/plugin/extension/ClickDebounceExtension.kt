package io.github.xiewinson.clickdebounce.plugin.extension

/**
 * Created by xiewinson
 */

open class ClickDebounceExtension {

    var includeClasses = mutableSetOf<String>()

    var excludeClasses = mutableSetOf<String>()

    /**
     * 默认的点击间隔时间
     */
    var interval = 300L
}