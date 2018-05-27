package com.developersam.pl.sapl.exceptions

/**
 * [CyclicDependencyError] reports cyclic dependency error in compile time.
 */
class CyclicDependencyError : CompileTimeError(message = "Cyclic Dependency Detected!")