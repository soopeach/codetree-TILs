package hyunsoo.`57week`

import kotlin.math.absoluteValue

/**
 *
 * <문제>
 * [메이즈 러너](https://www.codetree.ai/training-field/frequent-problems/problems/maze-runner/description?page=1&pageSize=20)
 *
 * - 아이디어
 *
 *
 * - 트러블 슈팅
 *
 */
class `전현수_메이즈_러너` {

    private data class Position(val x: Int, val y: Int) {

        operator fun plus(other: Position): Position {
            return Position(x + other.x, y + other.y)
        }

        operator fun minus(other: Position): Position {
            return Position(x - other.x, y - other.y)
        }

        fun calculateDistance(other: Position): Int {
            return (x - other.x).absoluteValue + (y - other.y).absoluteValue
        }

        fun rotate(
            startPos: Position,
            endPos: Position
        ): Position {
            return if (this.x !in startPos.x..endPos.x ||
                this.y !in startPos.y..endPos.y
            ) {
                this
            } else {
                if (endPos.x - startPos.x == 1) {
                    if (this.x == startPos.x) {
                        if (this.y == startPos.y) {
                            Position(x, y + 1)
                        } else {
                            Position(x + 1, y)
                        }
                    } else {
                        if (this.y == startPos.y) {
                            Position(x - 1, y)
                        } else {
                            Position(x, y - 1)
                        }
                    }
                } else {
                    Position(y - startPos.y, endPos.x - startPos.x - this.x + startPos.y)
                }
            }
        }
    }

    private data class PositionParcel(val pos: Position, val cost: Int, val priority: Int)

    private val dirs = listOf(
        Position(-1, 0),
        Position(1, 0),
        Position(0, 1),
        Position(0, -1)
    )

    private var board = mutableListOf<MutableList<Int>>()

    fun solution() {

        val (n, m, k) = readLine()!!.split(" ").map { it.toInt() }

        val participantList = Array<Position?>(m) {
            Position(0, 0)
        }
        var exit = Position(0, 0)
        var movedDistance = 0

        repeat(n) {
            val row = readLine()!!.split(" ").map { it.toInt() } as MutableList
            board.add(row)
        }

        repeat(m) { index ->
            val participant = readLine()!!.split(" ").map { it.toInt() }
                .run {
                    Position(this[0] - 1, this[1] - 1)
                }
            participantList[index] = participant
        }

        readLine()!!.split(" ").map { it.toInt() }
            .run {
                exit = Position(this[0] - 1, this[1] - 1)
            }

        repeat(k) {

            if (participantList.all { it == null }) return@repeat

            participantList.forEachIndexed { participantIndex, curParticipant ->

                if (curParticipant == null) return@forEachIndexed

                val availableSpots = mutableListOf<PositionParcel>()

                dirs.forEach { dir ->

                    val newPos = curParticipant + dir

                    if (newPos.x !in 0 until n ||
                        newPos.y !in 0 until n ||
                        // 빈 위치가 아닐 경우
                        board[newPos.x][newPos.y] != EMTPY
                    ) return@forEach

                    val preDistance = curParticipant.calculateDistance(exit)
                    val newDistance = newPos.calculateDistance(exit)

                    if (newDistance < preDistance) {
                        availableSpots.add(PositionParcel(newPos, newDistance, dirs.indexOf(dir)))
                    }

                }

                if (availableSpots.isEmpty()) return@forEachIndexed

                val nextPosition = availableSpots.sortedWith(
                    compareBy<PositionParcel> {
                        it.cost
                    }.thenBy {
                        it.priority
                    }
                ).first().pos

                movedDistance++

                if (nextPosition == exit) {
                    participantList[participantIndex] = null
                } else {
                    participantList[participantIndex] = nextPosition
                }
            }

            var (x1, y1, x2, y2) = listOf(0, 0, 0, 0)
            run {
                for (length in 1 until n) {
                    for (r1 in 0 until n) {
                        for (c1 in 0 until n) {

                            val r2 = r1 + length
                            val c2 = c1 + length

                            if (n <= r2 || n <= c2) continue

                            // 출구가 범위 내에 없을 경우
                            if (exit.x !in r1..r2 ||
                                exit.y !in c1..c2
                            ) continue

                            participantList.forEach { participant ->
                                if (participant == null) return@forEach
                                if (participant.x in r1..r2 &&
                                    participant.y in c1..c2
                                ) {
                                    x1 = r1; x2 = r2; y1 = c1; y2 = c2
                                    return@run
                                }
                            }

                        }

                    }
                }
            }

            participantList.forEachIndexed { index, it ->
                participantList[index] = it?.rotate(Position(x1, y1), Position(x2, y2))
            }

            exit = exit.rotate(Position(x1, y1), Position(x2, y2))
            board = board.rotate(Position(x1, y1), Position(x2, y2))

        }

        println(movedDistance)
        println("${exit.x + 1} ${exit.y + 1}")

    }

    // 회전 및 체력 감소
    private fun MutableList<MutableList<Int>>.rotate(
        startPos: Position,
        endPos: Position
    ): MutableList<MutableList<Int>> {
        val new = this.map {
            it.toList() as MutableList
        } as MutableList

        // 회전하며 벽은 체력 감소

        if (endPos.x - startPos.x == 1) {
            new[startPos.x][startPos.y] =
                if (this[endPos.x][startPos.y] == 0) 0 else this[endPos.x][startPos.y] - 1
            new[startPos.x][endPos.y] =
                if (this[startPos.x][startPos.y] == 0) 0 else this[startPos.x][startPos.y] - 1
            new[endPos.x][startPos.y] =
                if (this[endPos.x][endPos.y] == 0) 0 else this[endPos.x][endPos.y] - 1
            new[endPos.x][endPos.y] =
                if (this[startPos.x][endPos.y] == 0) 0 else this[startPos.x][endPos.y] - 1
        } else {
            for (i in startPos.x..endPos.x) {
                for (j in startPos.y..endPos.y) {
                    new[j - startPos.y][endPos.x - startPos.x - i + startPos.y] =
                        if (this[i][j] == 0) 0 else this[i][j] - 1
                }
            }
        }

        return new.map { row ->
            row.toList() as MutableList
        }.toList() as MutableList

    }

    companion object {
        const val EMTPY = 0
    }
}

fun main() {
    전현수_메이즈_러너().solution()
}