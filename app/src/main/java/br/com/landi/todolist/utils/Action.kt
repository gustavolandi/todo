package br.com.landi.todolist.utils

interface Action {
    fun execute() {}
    fun execute(a: Int, b: Int) {}
}