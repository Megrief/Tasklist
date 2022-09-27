package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import kotlinx.datetime.*
import kotlin.system.exitProcess

internal class Tasklist {
    private val taskList = mutableListOf<Task>()
    private val functions: Funs = Funs()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val tListType = Types.newParameterizedType(List::class.java, Task::class.java)
    private val tListAdapter = moshi.adapter<List<Task>>(tListType)
    private val file = File("tasklist.json").apply {
        if (!this.exists()) this.createNewFile()
    }
    init {
        if (file.readText().isNotEmpty()) tListAdapter.fromJson(file.readText()).let {
            it?.forEach { task ->
                taskList += task
            }
        }
        menu()
        file.writeText(tListAdapter.toJson(taskList))
        exitProcess(0)
    }

    private fun menu() {
        println("Input an action (add, print, edit, delete, end):")
        when (readln()) {
            "add" -> {
                functions.add(taskList)
                menu()
            }
            "print" -> {
                functions.print(taskList)
                menu()
            }
            "edit" -> {
                functions.print(taskList)
                if (taskList.isNotEmpty()) functions.edit(taskList)
                menu()
            }
            "delete" -> {
                functions.print(taskList)
                if (taskList.isNotEmpty()) functions.delete(taskList)
                menu()
            }
            "end" -> println("Tasklist exiting!")
            else -> {
                println("The input action is invalid")
                menu()
            }
        }
    }
}

data class Task(
    val priority: Char,
    val date: String,
    val time: String,
    val task: List<String>
    ) {
    private val current = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0"))
    private val dTag = { currentDate: LocalDateTime ->
        val strToDate = (date + 'T' + time).toLocalDateTime()
        (currentDate.date).daysUntil(strToDate.date)
    }
    val dueTag: Int = dTag(current)
}