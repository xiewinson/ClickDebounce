package io.github.xiewinson.clickdebounce.plugin

import com.android.build.gradle.AppExtension
import io.github.xiewinson.clickdebounce.plugin.extension.ClickDebounceExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by xiewinson
 */

class ClickDebouncePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val clickDebounceExtension = project.extensions.create("ClickDebounceParam", ClickDebounceExtension::class.java)
        project.extensions.getByType(AppExtension::class.java).registerTransform(ClickDebounceTransform())
        project.afterEvaluate {
            val param = it.project.extensions.getByType(ClickDebounceExtension::class.java)
            println("传入的参数 $param")
        }
    }
}