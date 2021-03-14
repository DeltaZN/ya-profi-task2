package com.example.demo.repository

import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Note(
    @Id
    @GeneratedValue
    val id: Long = 0,
    var title: String? = null,
    var content: String = "",
)

interface NoteRepository : CrudRepository<Note, Long> {
    fun findByTitleContainsOrContentContains(title: String, content: String): List<Note>
}