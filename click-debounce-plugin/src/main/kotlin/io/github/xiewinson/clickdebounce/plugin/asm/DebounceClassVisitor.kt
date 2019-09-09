package io.github.xiewinson.clickdebounce.plugin.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by xiewinson
 */
class DebounceClassVisitor(cv: ClassVisitor, private val interval: Long) : ClassVisitor(Opcodes.ASM7, cv), Opcodes {

    private var isImplementOnClickListener = false

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        interfaces?.forEach {
            if (it == "android/view/View\$OnClickListener") {
                isImplementOnClickListener = true
            }
        }
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return if (isImplementOnClickListener && name == "onClick" && descriptor == "(Landroid/view/View;)V") {
            DebounceMethodVisitor(methodVisitor, access, name, descriptor, interval)
        } else {
            methodVisitor
        }
    }
}