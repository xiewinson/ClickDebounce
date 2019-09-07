package io.github.xiewinson.clickdebounce.plugin.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by xiewinson
 */
class MyClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM7, cv), Opcodes {
    private var className: String? = null


    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return MyMethodVisitor(className, methodVisitor, access, name, descriptor)
    }
}