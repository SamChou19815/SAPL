package org.sampl.ast.raw

import org.sampl.ast.decorated.DecoratedClassMember
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.sampl.ast.type.TypeInfo
import org.sampl.environment.TypeCheckingEnv

/**
 * [ClassMember] defines a set of supported class members.
 */
internal sealed class ClassMember {

    /**
     * [typeCheck] tries to type check this class member under the given [TypeCheckingEnv] [env].
     * It returns a decorated class member and a new environment after type check.
     */
    abstract fun typeCheck(env: TypeCheckingEnv): Pair<DecoratedClassMember, TypeCheckingEnv>

    /**
     * [Constant] represents a constant declaration of the form:
     * `public/private`([isPublic]) `let` [identifier] `=` [expr],
     * with [identifier] at [identifierLineNo].
     *
     * @property isPublic whether the constant is public.
     * @property identifierLineNo identifier line number of the constant.
     * @property identifier identifier of the constant.
     * @property expr expression of the constant.
     */
    data class Constant(
            val isPublic: Boolean,
            val identifierLineNo: Int,
            val identifier: String,
            val expr: Expression
    ) : ClassMember() {

        override fun typeCheck(env: TypeCheckingEnv): Pair<DecoratedClassMember, TypeCheckingEnv> {
            val decoratedExpr = expr.typeCheck(environment = env)
            val decoratedConstant = DecoratedClassMember.Constant(
                    isPublic = isPublic, identifier = identifier, expr = decoratedExpr,
                    type = decoratedExpr.type
            )
            val e = env.copy(normalTypeEnv = env.normalTypeEnv.put(identifier, decoratedExpr.type))
            return decoratedConstant to e
        }

    }

    /**
     * [FunctionGroup] represents a group of functions.
     *
     * @property functions a group of functions.
     */
    data class FunctionGroup(val functions: List<ClassFunction>) : ClassMember() {

        override fun typeCheck(env: TypeCheckingEnv): Pair<DecoratedClassMember, TypeCheckingEnv> {
            val newE = env.copy(
                    classFunctionTypeEnv = functions.fold(env.classFunctionTypeEnv) { e, f ->
                        val functionTypeInfo = TypeInfo(f.functionType, f.genericsDeclaration)
                        e.put(key = f.identifier, value = functionTypeInfo)
                    }
            )
            val decoratedFunctions = functions.map { it.typeCheck(environment = newE) }
                    .let { DecoratedClassMember.FunctionGroup(functions = it) }
            return decoratedFunctions to newE
        }

    }

    /**
     * [Clazz] node has an type identifier with generics [identifier], a type [declaration] and a
     * set of ordered [members] with [identifier] at [identifierLineNo].
     * It means class.
     *
     * @property identifierLineNo the line number of the identifier.
     * @property identifier identifier of the class.
     * @property declaration the type declaration.
     * @property members a list of members of the class.
     */
    data class Clazz(
            val identifierLineNo: Int,
            val identifier: TypeIdentifier,
            val declaration: TypeDeclaration,
            val members: List<ClassMember>
    ) : ClassMember() {

        /**
         * [typeCheckTypeDeclaration] uses the given [e] to type check the type declaration.
         *
         * Requires: [e] must already put all the type members inside to allow recursive types.
         */
        private fun typeCheckTypeDeclaration(e: TypeCheckingEnv) {
            val newDeclaredTypes = identifier.genericsInfo
                    .fold(initial = e.declaredTypes) { acc, s ->
                        acc.put(key = s, value = emptyList())
                    }
            val newEnv = e.copy(declaredTypes = newDeclaredTypes)
            when (declaration) {
                is TypeDeclaration.Variant -> declaration.map.values
                        .forEach { it?.checkTypeValidity(environment = newEnv) }
                is TypeDeclaration.Struct -> declaration.map.values
                        .forEach { it.checkTypeValidity(environment = newEnv) }
            }
        }

        override fun typeCheck(env: TypeCheckingEnv): Pair<DecoratedClassMember, TypeCheckingEnv> {
            val eInit = env.enterClass(clazz = this)
            typeCheckTypeDeclaration(e = eInit)
            val (typeCheckedMembers, newE) = members.typeCheck(env = eInit)
            // Exit Current Module and Return
            val decoratedClass = DecoratedClassMember.Clazz(
                    identifier = identifier,
                    declaration = declaration,
                    members = typeCheckedMembers
            )
            val eFinal = newE.exitClass(clazz = this)
            return decoratedClass to eFinal
        }

    }

    companion object {

        /**
         * [typeCheck] type checks all the members with current [env].
         */
        fun List<ClassMember>.typeCheck(
                env: TypeCheckingEnv
        ): Pair<List<DecoratedClassMember>, TypeCheckingEnv> {
            val typeCheckedMembers = ArrayList<DecoratedClassMember>(size)
            var currentEnv = env
            for (member in this) {
                val (typeCheckedMember, newE) = member.typeCheck(env = currentEnv)
                typeCheckedMembers.add(element = typeCheckedMember)
                currentEnv = newE
            }
            return typeCheckedMembers to currentEnv
        }

    }

}

