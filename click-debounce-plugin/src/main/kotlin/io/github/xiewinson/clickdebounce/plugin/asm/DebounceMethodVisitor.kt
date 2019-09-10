package io.github.xiewinson.clickdebounce.plugin.asm

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

internal class DebounceMethodVisitor(mv: MethodVisitor, access: Int, name: String?, desc: String?, private val interval: Long) : AdviceAdapter(Opcodes.ASM5, mv, access, name, desc), Opcodes {

    private val label = Label()

    override fun onMethodEnter() {
        super.onMethodEnter()
        with(mv) {
            visitLdcInsn(interval)
            visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/xiewinson/clickdebounce/ClickHelper", "isClickAllowed", "(J)Z", false)
            visitJumpInsn(Opcodes.IFEQ, label)
        }

    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        with(mv) {
            visitLabel(label)
        }
    }
}