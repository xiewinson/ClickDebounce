package io.github.xiewinson.clickdebounce.plugin.asm

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

internal class MyMethodVisitor(private val className: String?, mv: MethodVisitor, access: Int, name: String?, desc: String?) : AdviceAdapter(Opcodes.ASM7, mv, access, name, desc), Opcodes {

    private var newLocal: Int = 0

    override fun onMethodEnter() {
        super.onMethodEnter()
        newLocal = newLocal(Type.LONG_TYPE)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitVarInsn(Opcodes.LSTORE, newLocal)
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

        mv.visitLdcInsn(className)
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        mv.visitLdcInsn("方法执行时间 $name -> ")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitVarInsn(Opcodes.LLOAD, newLocal)
        mv.visitInsn(Opcodes.LSUB)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false)
        mv.visitInsn(Opcodes.POP)

    }
}