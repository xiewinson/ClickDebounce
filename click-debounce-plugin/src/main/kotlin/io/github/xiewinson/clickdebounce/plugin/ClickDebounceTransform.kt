package io.github.xiewinson.clickdebounce.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import io.github.xiewinson.clickdebounce.plugin.asm.MyClassVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import org.apache.commons.codec.digest.DigestUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


/**
 * Created by xiehao03
 */
class ClickDebounceTransform : Transform(), Opcodes {
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
        transformInvocation?.let {
            transformInvocation.inputs?.forEach {
                it.directoryInputs.forEach { input: DirectoryInput ->
                    injectDir(input)
                    FileUtils.copyDirectory(input.file, transformInvocation.outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY))
                }
                it.jarInputs.forEach { input: JarInput ->
                    val modifyJarPath = input.file.parentFile.absolutePath + File.separator + DigestUtils.md5Hex(input.file.absolutePath) + "_" + input.file.name
                    val modifyJarFile = injectJar(input.file.absolutePath, modifyJarPath)
                    val dest = transformInvocation.outputProvider.getContentLocation(modifyJarFile.name, input.contentTypes, input.scopes, Format.JAR)
                    FileUtils.copyFile(modifyJarFile, dest)
                    modifyJarFile.delete()
                }
            }
        }

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
                    jarOutputStream.write(handleClass(it.readBytes()))
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
                .filter { it.isFile && shouldModifyClass(it.name) }
                .forEach { file ->
                    val data = file.readBytes()
                    file.outputStream().use {
                        it.write(handleClass(data))
                    }
                }
    }

    private fun shouldModifyClass(name: String): Boolean {
        return name.endsWith(".class")
                && !name.contains("R$")
                && !name.contains("R.class")
                && !name.contains("BuildConfig.class")
    }

    private fun handleClass(data: ByteArray): ByteArray {
        val reader = ClassReader(data)
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        val cv = MyClassVisitor(writer)
        reader.accept(cv, ClassReader.EXPAND_FRAMES)
        return writer.toByteArray()
    }
}