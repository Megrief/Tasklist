package tasklist

import kotlinx.datetime.*

class Funs {
    fun add(list: MutableList<Task>) {
        val priority = setPriority()
        val date = dateTime(setDate()).modToStr()
        val task = setTask()
        if (task.isEmpty()) {
            println("The task is blank")
            return
        }
        list.add(Task(priority, date.first(), date.last(), task))
    }
    private fun setTask(): List<String> {
        println("Input a new task (enter a blank line to end):")
        return buildList {
            while (true) {
                val line = readln().trim()
                if (line.isEmpty()) break
                this.add(line)
            }
        }
    }
    private fun setPriority(): Char {
        println("Input the task priority (C, H, N, L):")
        return readln().let {
            when {
                it.length != 1 -> setPriority()
                it.first().uppercaseChar() !in listOf('C', 'H', 'N', 'L') -> setPriority()
                else -> it.first().uppercaseChar()
            }
        }
    }
    private fun setDate(): LocalDate {
        println("Input the date (yyyy-mm-dd):")
        return try {
            readln().split('-').map { it.toInt() }.let { list ->
                LocalDate(list[0], list[1], list[2])
            }
        } catch (_: Exception) {
            println("The input date is invalid")
            setDate()
        }
    }
    private fun dateTime(date: LocalDate): LocalDateTime {
        println("Input the time (hh:mm):")
        return try {
            readln().split(':').map { it.toInt() }.let { list ->
                LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, list[0], list[1])
            }
        } catch (_: IllegalArgumentException) {
            println("The input time is invalid")
            dateTime(date)
        }
    }
    private fun LocalDateTime.modToStr(): List<String> {
        return listOf(
            formattingYear(this.year.toString()) +
                    "-${ formattingOther(this.monthNumber.toString()) }" +
                    "-${ formattingOther(this.dayOfMonth.toString()) }",
            "${ formattingOther(this.hour.toString()) }:${ formattingOther(this.minute.toString()) }"
        )
    }
    private fun formattingYear(year: String): String {
        return if (year.length != 4) formattingYear("0$year") else year
    }
    private fun formattingOther(other: String): String {
        return if (other.length != 2) formattingOther("0$other") else other
    }

    fun edit(list: MutableList<Task>) {
        println("Input the task number (1-${list.size}):")
        readln().toIntOrNull().let { num ->
            if (num in 1..list.size) {
                editing(list, num!! - 1)
            } else {
                println("Invalid task number")
                edit(list)
            }
        }
    }
    private fun editing(list: MutableList<Task>, ind: Int) {
        println("Input a field to edit (priority, date, time, task):")
        when (readln()) {
            "priority" -> {
                list[ind] = Task(setPriority(), list[ind].date, list[ind].time, list[ind].task)
                println("The task is changed")
            }
            "date" -> {
                list[ind] = changeDate(list[ind])
                println("The task is changed")
            }
            "time" -> {
                list[ind] = changeTime(list[ind])
                println("The task is changed")
            }
            "task" -> setTask().let {
                if (it.isEmpty()) {
                    println("The task is blank")
                    return
                } else {
                    list[ind] = Task(list[ind].priority, list[ind].date, list[ind].time, it)
                    println("The task is changed")
                }
            }
            else -> {
                println("Invalid field")
                editing(list, ind)
            }
        }
    }
    private fun changeDate(task: Task): Task {
        val date = setDate().toString()
        return Task(task.priority, date, task.time, task.task)
    }
    private fun changeTime(task: Task): Task {
        val lDate = task.date.toLocalDate()
        val time = dateTime(lDate).modToStr().last()
        return Task(task.priority, task.date, time, task.task)
    }

    fun delete(list: MutableList<Task>) {
        println("Input the task number (1-${list.size}):")
        readln().toIntOrNull().let { num ->
            if (num in 1..list.size) {
                list.removeAt(num!! - 1)
                println("The task is deleted")
            } else {
                println("Invalid task number")
                delete(list)
            }
        }
    }

    fun print(list: MutableList<Task>) {
        if (list.isEmpty()) {
            println("No tasks have been input")
            return
        }
        println(
            """
            +----+------------+-------+---+---+--------------------------------------------+
            | N  |    Date    | Time  | P | D |                   Task                     |
            +----+------------+-------+---+---+--------------------------------------------+
        """.trimIndent()
        )
        list.forEachIndexed { index, task ->
            val dTag = when {
                task.dueTag < 0 -> "\u001B[101m \u001B[0m"
                task.dueTag == 0 -> "\u001B[103m \u001B[0m"
                else -> "\u001B[102m \u001B[0m"
            }
            val pr = when (task.priority) {
                'C' -> "\u001B[101m \u001B[0m"
                'H' -> "\u001B[103m \u001B[0m"
                'N' -> "\u001B[102m \u001B[0m"
                'L' -> "\u001B[104m \u001B[0m"
                else -> " "
            }
            val tNum = if ("${index + 1}".length == 1) "${index + 1} " else "${index + 1}"
            task.task.forEachIndexed { ind1, str ->
                str.chunked(44).forEachIndexed { ind2, subStr ->
                    val res = if (subStr.length < 44)  form(subStr) else subStr
                    if (ind1 == 0 && ind2 == 0) {
                        println(
                            """
                            | $tNum | ${task.date} | ${task.time} | $pr | $dTag |$res|
                        """.trimIndent()
                        )
                    } else println(
                        """
                        |    |            |       |   |   |$res|
                    """.trimIndent()
                    )
                }
            }
            println("+----+------------+-------+---+---+--------------------------------------------+")
        }
    }
    private fun form(str: String): String {
        return buildString {
            this.append(str)
            while (this.length != 44) {
                this.append(' ')
            }
        }
    }
}