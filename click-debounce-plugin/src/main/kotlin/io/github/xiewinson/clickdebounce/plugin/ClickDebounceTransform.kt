package io.github.xiewinson.clickdebounce.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import io.github.xiewinson.clickdebounce.plugin.asm.DebounceClassVisitor
import io.github.xiewinson.clickdebounce.plugin.extension.ClickDebounceExtension
import jdk.internal.org.objectweb.asm.Opcodes
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


/**
 * Created by xiewinson
 */
class ClickDebounceTransform(val project: Project) : Transform(), Opcodes {

    private val TAG = "[click debounce]"

    private var interval = 300L
    private val clickDebounceIncludeClasses = mutableListOf<String>()
    private val clickDebounceExcludeClasses = mutableListOf<String>()

    override fun getName(): String {
        return "clickDebounce"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val params = project.extensions.getByType(ClickDebounceExtension::class.java)
        interval = params.interval
        params.includeClasses.forEach {
            clickDebounceIncludeClasses.add(it.replace(".", "/"))
        }
        params.excludeClasses.forEach {
            clickDebounceExcludeClasses.add(it.replace(".", "/"))
        }

        val startTime = System.currentTimeMillis()
        println("$TAG start--------------------------------")
        transformInvocation?.let {
            transformInvocation.inputs?.forEach {
                it.directoryInputs.forEach { input: DirectoryInput ->
                    injectDir(input)
                    FileUtils.copyDirectory(input.file, transformInvocation.outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY))
                }
                it.jarInputs.forEach { input: JarInput ->
                    val modifyJarPath = input.file.parentFile.absolutePath + File.separator + DigestUtils.md5(input.file.absolutePath.toByteArray()) + "_" + input.file.name
                    val modifyJarFile = injectJar(input.file.absolutePath, modifyJarPath)
                    val dest = transformInvocation.outputProvider.getContentLocation(modifyJarFile.name, input.contentTypes, input.scopes, Format.JAR)
                    FileUtils.copyFile(modifyJarFile, dest)
                    modifyJarFile.delete()
                }
            }
        }
        println("$TAG end--------------------------------")
        println("$TAG cost time: ${System.currentTimeMillis() - startTime}")
    }

    private fun injectJar(srcPath: String, destPath: String): File {
        val result = File(destPath)
        val jarOutputStream = JarOutputStream(FileOutputStream(result))
        val jarFile = JarFile(srcPath)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val inputStream = jarFile.getInputStream(jarEntry)
            inputStream.use {
                jarOutputStream.putNextEntry(ZipEntry(jarEntry.name))
                if (shouldModifyClass(jarEntry.name)) {
                    jarOutputStream.write(modifyClass(jarEntry.name, it.readBytes()))
                } else {
                    jarOutputStream.write(it.readBytes())
                }
                jarOutputStream.closeEntry()
            }
        }
        jarOutputStream.close()
        return result
    }


    private fun injectDir(input: DirectoryInput) {
        input.file.walkTopDown()
                .filter { it.isFile && shouldModifyClass(it.absolutePath) }
                .forEach { file ->
                    val data = file.readBytes()
                    file.outputStream().use {
                        it.write(modifyClass(file.absolutePath, data))
                    }
                }
    }

    private fun shouldModifyClass(name: String): Boolean {
        return name.endsWith(".class")
                && !name.contains("R$")
                && !name.contains("R.class")
                && !name.contains("BuildConfig.class")
                && isTargetClass(name)
    }

    private fun isTargetClass(name: String): Boolean {
        var result = false
        clickDebounceIncludeClasses.forEach {
            if (name.contains(Regex(it))) {
                result = true
            }
        }
        clickDebounceExcludeClasses.forEach {
            if (name.contains(Regex(it))) {
                result = false
            }
        }
        return result
    }


    private fun modifyClass(name: String, data: ByteArray): ByteArray {
        println("$TAG modifying -> $name")
        val reader = ClassReader(data)
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        val cv = DebounceClassVisitor(writer, interval)
        reader.accept(cv, ClassReader.EXPAND_FRAMES)
        return writer.toByteArray()
    }
}