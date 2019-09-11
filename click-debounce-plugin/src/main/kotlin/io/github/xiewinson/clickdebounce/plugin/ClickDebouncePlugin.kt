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
        project.extensions.create("clickDebounce", ClickDebounceExtension::class.java)
        project.extensions.getByType(AppExtension::class.java).registerTransform(ClickDebounceTransform(project))
        project.configurations.getByName("implementation").dependencies.add(project.dependencies.create("io.github.xiewinson:click-debounce-runtime:1.0.1"))
    }

}

