package net.rickiekarp.colorpuzzlefx.ai

import java.util.*
import java.util.stream.Collectors

class SolverManager {
    private val solverMap: MutableMap<String, Solver> = TreeMap()
    val solverNames: List<String>
        get() = solverMap.keys.stream().collect(Collectors.toList())

    fun getSolver(name: String): Optional<Solver> {
        return Optional.ofNullable(solverMap[name])
    }

    init {
        solverMap["Bogo Solver"] = BogoSolver()
        solverMap["Brute Force Solver"] = BruteForceSolver()
        solverMap["Solver 1"] = Solver1()
    }
}