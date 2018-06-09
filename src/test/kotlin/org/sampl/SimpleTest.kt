package org.sampl

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.sampl.classes.ClassConstructor
import org.sampl.codegen.IndentationQueue
import org.sampl.codegen.IndentationStrategy
import org.sampl.codegen.KotlinTranspilerVisitor
import org.sampl.util.writeToFile

/**
 * [SimpleTest] contains some simple programs to demonstrate the working status of the system.
 */
class SimpleTest {

    /**
     * [propositionsAreTypesProofsAreProgram] is a program that illustrates the concept of
     * 'Propositions Are Types, Proofs Are Programs'.
     */
    private val propositionsAreTypesProofsAreProgram: String = """
    class TestingProgram {
        let trueVar = ()
        let implication = function (a: String) -> 5
        let <A, B> modusPonens (f: (A) -> B) (v: A): B = f(v)
        // Function Application
        let constant5Impl1 (v: String): Int = implication (v)
        let constant5Impl2 (v: String): Int = modusPonens<String, Int> (implication v)
        let applyWithString (): Int = constant5Impl2 ("hi")
        let add (a: Int) (b: Int): Int = a + b
        let add1 (b: Int): Int = add (1) (b)
        let main (): Unit = ()
        // Classes
        class And<A, B>(a: A, b: B)
        class Or<A, B>(
          First of A | Second of B
        )
        class Empty
    }
    """.trimIndent()

    /**
     * [runSimpleInSteps] simply runs some code in compiler's steps to show that the system kinds of
     * works in each step.
     */
    @Test
    fun runSimpleInSteps() {
        val firstCompile = ClassConstructor
                .fromSource(code = propositionsAreTypesProofsAreProgram)
                .typeCheck()
        // println(firstCompile.asIndentedSourceCode)
        val secondCompile = firstCompile.asIndentedSourceCode
                .let { ClassConstructor.fromSource(code = it).typeCheck() }
        // println(secondCompile.asIndentedSourceCode)
        assertEquals(firstCompile, secondCompile)
        val kotlinCode = IndentationQueue(strategy = IndentationStrategy.FOUR_SPACES)
                .apply { KotlinTranspilerVisitor.visit(q = this, program = secondCompile) }
                .toIndentedCode()
        writeToFile(filename = "./src/test/resources/Program.kt", content = kotlinCode)
    }

    /**
     * [compileSimple] tests the compiler pipe line as a whole on a simple program.
     */
    @Test
    fun compileSimple() {
        PLCompiler.compileFromSource(code = propositionsAreTypesProofsAreProgram)
    }

}