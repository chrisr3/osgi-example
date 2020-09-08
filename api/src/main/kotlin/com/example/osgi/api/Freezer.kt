package com.example.osgi.api

interface Freezer {
    fun freeze(workers: List<String>)
}