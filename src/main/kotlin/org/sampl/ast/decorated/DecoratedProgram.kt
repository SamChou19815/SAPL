package org.sampl.ast.decorated

import org.sampl.ast.protocol.PrettyPrintable
import org.sampl.ast.protocol.Transpilable
import org.sampl.codegen.IndentationQueue
import org.sampl.codegen.TranspilerVisitor

/**
 * [DecoratedProgram] node contains a single top-level class [clazz].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(val clazz: DecoratedClass) : PrettyPrintable, Transpilable {

    override fun prettyPrint(q: IndentationQueue): Unit = clazz.prettyPrint(q = q)

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, program = this)

}